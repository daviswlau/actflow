package org.actflow.platform.engine.akkaspringfactory;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import akka.actor.ActorSystem;

public class ActorBootUp implements ApplicationContextAware {
	
	private ApplicationContext ctx;
	
	private int routerNum;
	
	private ActorSystem actorSystem;
	
	private String configName;
	
	private String dispatcher;
	
	public void setRouterNum(int routerNum) {
		this.routerNum = routerNum;
	}
	
	public void setActorSystem(ActorSystem actorSystem) {
		this.actorSystem = actorSystem;
	}
	
	public void setConfigName(String configName) {
		this.configName = configName;
	}
	
	public void setDispatcher(String dispatcher) {
		this.dispatcher = dispatcher;
	}

	public void init() {
		actorSystem.actorOf(SpringProps.create(false, actorSystem, "mainActor", "/user/process").withDispatcher(dispatcher), "actEngine");

		final Integer[] ports = new Integer[routerNum];
		for (int i = 2551; i < 2551 + routerNum - 1; i++) {
			ports[i - 2551] = i;
		}
		ports[routerNum - 1] = 0;
		engineSetup(ports, ConfigFactory.load(configName));
	}
	
	private void engineSetup(Integer[] ports, Config configSrc) {
		for (Integer port : ports) {
			Config config = ConfigFactory.parseString("akka.remote.netty.tcp.port=" + port).withFallback(ConfigFactory.parseString("akka.cluster.roles = [compute]")).withFallback(configSrc);
			ActorSystem system = ActorSystem.create(actorSystem.name(), config);
			SpringExtension.instance().get(system).setApplicationContext(ctx);
			
			system.actorOf(SpringProps.create(false, system, "processActor").withDispatcher(dispatcher), "process");
			system.actorOf(SpringProps.create(false, system, "classActor").withDispatcher(dispatcher), "classAct");
			system.actorOf(SpringProps.create(false, system, "beanActor").withDispatcher(dispatcher), "beanAct");
			system.actorOf(SpringProps.create(false, system, "serviceActor").withDispatcher(dispatcher), "serviceAct");
		}
	}
	
	public void shutdown() {
//		actorSystem.stop(arg0);
		actorSystem.terminate();
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		ctx = applicationContext;
	}

}
