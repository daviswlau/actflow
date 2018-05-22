package org.actflow.platform.engine.akkaspringfactory;

import akka.actor.ActorSystem;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Factory bean that creates actor system for given name and/or configuration.
 * Created actor system is singleton.
 *
 * @author Davis Lau
 */
public class ActorSystemFactoryBean implements FactoryBean<ActorSystem>, ApplicationContextAware, InitializingBean {

	private final static Logger logger = LoggerFactory.getLogger(ActorSystemFactoryBean.class);

	private ApplicationContext ctx;
	
	private String name;
	
	private String configName;
	
	private Config fallbackConfig;
	
	private ActorSystem actorSystem;
	

	@Override
	public ActorSystem getObject() throws Exception {
		return actorSystem;
	}

	@Override
	public Class<?> getObjectType() {
		return ActorSystem.class;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setConfigName(String configName) {
		this.configName = configName;
	}

	public void setFallbackConfig(Config fallbackConfig) {
		this.fallbackConfig = fallbackConfig;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		ctx = applicationContext;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		logger.debug("set akka system properties...");
		ActorSystem system = null;
		if (StringUtils.isNotBlank(name) && configName != null && fallbackConfig != null) {
			logger.debug("setting mode 1");
			system = ActorSystem.create(name, ConfigFactory.load(configName).withFallback(fallbackConfig));
		} else if (StringUtils.isNotBlank(name)) {
			logger.debug("setting mode 2");
			system = ActorSystem.create(name);
		} else {
			logger.debug("setting mode 3");
			system = ActorSystem.create();
		}
		// init extensions
		SpringExtension.instance().get(system).setApplicationContext(ctx);
		actorSystem = system;	
	}
}