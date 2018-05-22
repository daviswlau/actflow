package org.actflow.platform.engine.actor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import org.actflow.platform.engine.dto.ProcessMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import akka.actor.ActorSelection;
import akka.actor.ActorSystem;
import akka.actor.Address;
import akka.actor.UntypedActor;
import akka.cluster.Cluster;
import akka.cluster.ClusterEvent.UnreachableMember;
import akka.cluster.ClusterEvent.ReachableMember;
import akka.cluster.ClusterEvent.CurrentClusterState;
import akka.cluster.ClusterEvent.MemberEvent;
import akka.cluster.ClusterEvent.MemberUp;
import akka.cluster.ClusterEvent.ReachabilityEvent;
import akka.cluster.Member;
import akka.cluster.MemberStatus;

@Component("mainActor")
@Scope("prototype")
public class MainActor extends UntypedActor {
	
	private final static Logger logger = LoggerFactory.getLogger(MainActor.class);
	
	@Autowired
	ActorSystem actorSystem;

	final String routerPath;
	
	final Set<Address> nodes = new HashSet<Address>();
	
	Cluster cluster = Cluster.get(getContext().system());
	
	public MainActor(String routerPath) {
		this.routerPath = routerPath;
	}
	
	//subscribe to cluster changes, MemberEvent
	@Override
	public void preStart() {
		cluster.subscribe(getSelf(), MemberEvent.class, ReachabilityEvent.class);
	}
	
	//re-subscribe when restart
	@Override
	public void postStop() {
		cluster.unsubscribe(getSelf());
	}
	
	@Override
	public void onReceive(Object message) {
		logger.debug("Client ----------- " + this.getSelf());
		if (message instanceof ProcessMessage && !nodes.isEmpty()) {
			List<Address> nodesList = new ArrayList<Address>(nodes);
			Address address = nodesList.get(ThreadLocalRandom.current().nextInt(nodesList.size()));
			
			ActorSelection router = getContext().actorSelection(address + routerPath);
			router.forward(message, getContext());
		} /*else if (message instanceof StatsResult) {
    		StatsResult result = (StatsResult) message;
    		
    		System.out.println("result.message --- " + result);
    	} else if (message instanceof JobFailed) {
    		JobFailed failed = (JobFailed) message;
    		
    		System.out.println("failed.message --- " + failed);
    	}*/ else if (message instanceof CurrentClusterState) {
    		logger.debug("state.message --- " + message);
    		
    		CurrentClusterState state = (CurrentClusterState) message;
    		nodes.clear();
    		for (Member member : state.getMembers()) {
    			if (member.hasRole("compute") && member.status().equals(MemberStatus.up())) {
    				nodes.add(member.address());
    			}
    		}
    	} else if (message instanceof MemberUp) {
    		logger.debug("mUp.message --- " + message);
    		
    		MemberUp mUp = (MemberUp) message;
    		if (mUp.member().hasRole("compute")){
    			nodes.add(mUp.member().address());
    		}
    	} else if (message instanceof MemberEvent) {
    		logger.debug("other.message --- " + message);
    		
    		MemberEvent other = (MemberEvent) message;
    		nodes.remove(other.member().address());
    	} else if (message instanceof UnreachableMember) {
    		logger.debug("unreachable.message --- " + message);
    		
    		UnreachableMember unreachable = (UnreachableMember) message;
    		nodes.remove(unreachable.member().address());
    	} else if (message instanceof ReachableMember) {
    		logger.debug("reachable.message --- " + message);
    		
    		ReachableMember reachable = (ReachableMember) message;
    		if (reachable.member().hasRole("compute")){
    			nodes.add(reachable.member().address());
    		}
    	} else {
    		logger.debug("unhandled.message --- " + message);
    		
    		unhandled(message);
    	}
	}
}
