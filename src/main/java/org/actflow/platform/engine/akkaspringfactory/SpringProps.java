package org.actflow.platform.engine.akkaspringfactory;

import akka.actor.ActorSystem;
import akka.actor.Props;

/**
 * @author Davis Lau
 */
public class SpringProps {

	public static Props create(boolean withRouter, ActorSystem actorSystem, String actorBeanName) {
		return SpringExtension.instance().get(actorSystem).create(withRouter, actorBeanName);
	}
	
	public static Props create(boolean withRouter, ActorSystem actorSystem, String actorBeanName, Object... args) {
		return SpringExtension.instance().get(actorSystem).create(withRouter, actorBeanName, args);
	}
}
