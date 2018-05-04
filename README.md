ActorFlowEngine
=========

A light & fastest work flow with Actor pattern.

environment: JDK1.8 above

to build:

mvn clean package install

use from maven center repository:

<dependency>
  <groupId>io.github.daviswlau</groupId>
  <artifactId>actflow-engine</artifactId>
  <version>1.0.2</version>
</dependency>


the cluster configuration, pls view 'resources/akkaActor-cluster.conf'

the flow sample, pls view 'engines/flow-sample.xml'

support Spring 4.x, just use the annotation @Inject, the actor will be auto inject your spring bean or service.
