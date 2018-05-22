package org.actflow.platform.engine.akkaspringfactory;

import akka.actor.AbstractExtensionId;
import akka.actor.ExtendedActorSystem;
import akka.actor.Extension;
import akka.actor.Props;
import akka.routing.FromConfig;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * An Akka Extension to provide access to Spring managed Actor Beans.
 * 
 * @author Davis Lau
 * 
 */
@Component("springExtension")
public class SpringExtension extends AbstractExtensionId<SpringExtension.SpringExt> {
	
	private SpringExtension() {
	}

	// thread safety is needed
	private static class Holder {
		static final SpringExtension INSTANCE = new SpringExtension();
	}

	/**
	 * The method used to access the SpringExtension.
	 */
	public static SpringExtension instance() {
		return Holder.INSTANCE;
	}

	/**
	 * Is used by Akka to instantiate the Extension identified by this
	 * ExtensionId, internal use only.
	 */
	@Override
	public SpringExt createExtension(ExtendedActorSystem system) {
		return new SpringExt();
	}

	/**
	 * The lookup method is required by ExtensionIdProvider, so we return
	 * ourselves here, this allows us to configure our extension to be loaded
	 * when the ActorSystem starts up
	 *
	 * @return extension itself
	 */
	public SpringExtension lookup() {
		return SpringExtension.instance();
	}

	/**
	 * The Extension implementation.
	 */
	public static class SpringExt implements Extension {
		
		private volatile ApplicationContext applicationContext;

		public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
			this.applicationContext = applicationContext;
		}

		/**
		 * Create a Props for the specified actorBeanName using the
		 * SpringActorProducer class.
		 *
		 * @param actorBeanName
		 *            The name of the actor bean to create Props for
		 * @return a Props that will create the named actor bean using Spring
		 */
		public Props create(String actorBeanName) {
			return Props.create(SpringActorProducer.class, applicationContext, actorBeanName);
		}
		
		/**
		 * 
		 * @param actorBeanName
		 * 				The name of the actor bean to create Props for
		 * @param args
		 * @return a Props that will create the actor bean using Spring
		 */
		public Props create(String actorBeanName, Object... args) {
			return Props.create(SpringActorProducer.class, applicationContext, actorBeanName, args);
		}
		
		/**
		 * support router
		 */
		public Props create(boolean withRouter, String actorBeanName) {
			if (withRouter) {
				return FromConfig.getInstance().props(create(actorBeanName));
			}
			return create(actorBeanName);
		}
		
		/**
		 * support router
		 */
		public Props create(boolean withRouter, String actorBeanName, Object... args) {
			if (withRouter) {
				return FromConfig.getInstance().props(create(actorBeanName, args));
			}
			return create(actorBeanName, args);
		} 
	}
}