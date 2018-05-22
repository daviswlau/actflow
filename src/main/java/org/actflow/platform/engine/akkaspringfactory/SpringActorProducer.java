package org.actflow.platform.engine.akkaspringfactory;

import akka.actor.Actor;
import akka.actor.IndirectActorProducer;
import org.springframework.context.ApplicationContext;

/**
 * Spring indirect actor producer
 * 
 * @author Davis Lau
 * 
 */
public class SpringActorProducer implements IndirectActorProducer {
	
	final ApplicationContext applicationContext;
	
	final String actorBeanName;
	
	final private Object[] args;

	public SpringActorProducer(ApplicationContext applicationContext, String actorBeanName) {
    	this.applicationContext = applicationContext;
        this.actorBeanName = actorBeanName;
        this.args = null;
    }
	
	public SpringActorProducer(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
		this.actorBeanName = null;
		this.args = null;
	}
	
	public SpringActorProducer(ApplicationContext applicationContext, String actorBeanName, Object... args) {
		this.applicationContext = applicationContext;
		this.actorBeanName = actorBeanName;
		this.args = args;
	}

	@Override
	public Actor produce() {
		Actor result = null;
		if (actorBeanName != null && args != null) {
			result = (Actor) applicationContext.getBean(actorBeanName, args);
		} else if (actorBeanName != null) {
			result = (Actor) applicationContext.getBean(actorBeanName);
		}
		return result;
	}

	@Override
	@SuppressWarnings("unchecked")
	public Class<? extends Actor> actorClass() {
		return (Class<? extends Actor>) applicationContext.getType(actorBeanName);
	}

}
