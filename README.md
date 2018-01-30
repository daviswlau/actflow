ActorFlowEngine
=========

A light & fastest work flow with Actor pattern.

environment
JDK1.8

to build
mvn clean package install

to use
<dependency>
	<groupId>org.actflow</groupId>
	<artifactId>actflow-engine</artifactId>
	<version>1.0.0-SNAPSHOT</version>
</dependency>

the cluster configuration, pls view 'resources/akkaActor-cluster.conf'

the flow sample, pls view 'engines/flow-sample.xml'

support Spring 4.x, just use the annotation @Inject, the actor will be auto inject your spring bean or service.
