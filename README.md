# Connecting Wildfly to a Remote ActiveMQ JMS

In order to allow communication via JMS api with a remote ActiveMQ JMS Server the ActiveMQ Resource Adapter (RA) needs to be deployed.

There are various methods available to perform this described here:

http://www.mastertheboss.com/jboss-server/jboss-jms/integrate-activemq-with-wildfly
or
https://developer.jboss.org/wiki/HowToUseOutOfProcessActiveMQWithWildFly

Unfortunately these did not work with version 13,14 or 15 of wildfly when I was testing:

```
Caused by: java.lang.IllegalStateException: WFLYEJB0383: No message listener of type javax.jms.MessageListener found in resource adapter activemq-ra
    at org.jboss.as.ejb3.component.EJBUtilities.createActivationSpecs(EJBUtilities.java:94)
    at org.jboss.as.ejb3.component.messagedriven.MessageDrivenComponentCreateService.createComponent(MessageDrivenComponentCreateService.java:97)
    at org.jboss.as.ee.component.BasicComponentCreateService.start(BasicComponentCreateService.java:90)
    at org.jboss.as.ejb3.component.messagedriven.MessageDrivenComponentCreateService.start(MessageDrivenComponentCreateService.java:84)
    at org.jboss.msc.service.ServiceControllerImpl$StartTask.startService(ServiceControllerImpl.java:1738)
    at org.jboss.msc.service.ServiceControllerImpl$StartTask.execute(ServiceControllerImpl.java:1700)
    ... 6 more
```

This issue turned out to be that the module `javax.jms.api` was missing. Adding this as a global module resolved the issue.

Having managed to get this working I then wanted to have a scriptable reproducible way in which to perform this setup.

## Automated Installation

An issue that I have had with wildfly cli is that you need a connection to perform the majority of tasks. With docker this does not really work so well.

- A script for startup that performs the configuration and deploy becomes a single changeable layer, which then makes build updates slow as the entire script must run.
- Running scripts that require a restart for the server also causes unnecessary errors in the logs
- Restarts also causes potential issues for liveness and readiness probes with OpenShift

Therefore I wanted to find a way to perform all the configuration before the server is brought up.

http://www.mastertheboss.com/jbossas/wildfly9/configuring-wildfly-9-from-the-cli-in-offline-mode

This describes the use of an embedded server in order to perform the configuration to a specific standalone.xml. So we can use this in order to "prepare" the server.

With this approach I have just used normal cli commands inside scripts to configure the RA, Queues and Topics that are required.

### Wildfly Config

The resource adapter is configured using the script `add-activemq-ra.cli` which has an external properties files `activemq-ra.properties`.

This script ensures that the resource adapter module subsystem is configured correctly, the default resource adapter is set and that javax.jms.api is added as a global module.

The queue is configured using the script `add-activemq-destination.cli` which has an external properties file `queue01.properties`.
The topic is configured using the script `add-activemq-destination.cli` which has an external properties file `topic01.properties`.

This script has a reference to the resource adapter id and adds the necessary admin-objects to the resource adapter configuration.

The script configure-wildfly.sh is an example script that could be run from a docker file to perform these tasks as a single layer.

The deletion of the standalone_xml_history is due to errors that I noticed in the log after the embedded server configuration. There is perhaps a less brutal solution to this, but I have not looked into it any further.

### Java Test Application
Simple mdb application in java which has a queue (queue01) and a topic (topic01).

The queue has a single producer and a single consumer.
The topic has a single producer and two consumers.

The producers are setup as timers so that a message is produced every 10 seconds.

The consumers listen and print the raw message to stdout.

## Docker

### ActiveMQ
The application requires a running ActiveMQ instance for example:
https://hub.docker.com/r/rmohr/activemq
```
docker run -dt -p8161:8161 -p61616:61616 rmohr/activemq
```

### Java Test Application
Ensure to update the settings for the RA with the ActiveMQ you are using: ServerUrl, UserName, Password.

To build the java application:
```
cd mdb
./gradlew clean build
 cp mdb/build/libs/mdb-0.0.1-SNAPSHOT.jar ../wildfly/files/mdb.jar
```

To build the docker application:
```
cd wildfly
docker build -t c19/wildfly-activemq-test .
docker run -dt -p 9990:9990 -p 8080:8080 c19/wildfly-activemq-test
```

Check the log files for consumers:
```
docker ps
docker logs -f <container_id>
```

You should see logs similar to this:
```
12:26:54,617 INFO  [org.jboss.as] (Controller Boot Thread) WFLYSRV0025: WildFly Full 15.0.0.Final (WildFly Core 7.0.0.Final) started in 10916ms - Started 608 of 792 services (331 services are lazy, passive or on-demand)
12:26:54,618 INFO  [org.apache.activemq.ra.ActiveMQEndpointWorker] (default-threads - 1) Establishing connection to broker [tcp://192.168.99.100:61616?jms.rmIdFromConnectionId=true]
12:26:54,620 INFO  [org.apache.activemq.ra.ActiveMQEndpointWorker] (default-threads - 3) Establishing connection to broker [tcp://192.168.99.100:61616?jms.rmIdFromConnectionId=true]
12:26:54,617 INFO  [org.apache.activemq.ra.ActiveMQEndpointWorker] (default-threads - 2) Establishing connection to broker [tcp://192.168.99.100:61616?jms.rmIdFromConnectionId=true]
12:26:54,866 INFO  [org.apache.activemq.ra.ActiveMQEndpointWorker] (default-threads - 3) Successfully established connection to broker [tcp://192.168.99.100:61616?jms.rmIdFromConnectionId=true]
12:26:54,865 INFO  [org.apache.activemq.ra.ActiveMQEndpointWorker] (default-threads - 1) Successfully established connection to broker [tcp://192.168.99.100:61616?jms.rmIdFromConnectionId=true]
12:26:54,867 INFO  [org.apache.activemq.ra.ActiveMQEndpointWorker] (default-threads - 2) Successfully established connection to broker [tcp://192.168.99.100:61616?jms.rmIdFromConnectionId=true]
12:27:00,182 INFO  [stdout] (default-threads - 4) Recieved a message 01.ActiveMQTextMessage {commandId = 6, responseRequired = false, messageId = ID:08ff0141c06d-42791-1552912014699-9:1:1:1:1, originalDestination = null, originalTransactionId = null, producerId = ID:08ff0141c06d-42791-1552912014699-9:1:1:1, destination = topic://topic01, transactionId = XID:[131077,globalId=0:ffffac110003:3cace777:5c8f8e89:16,branchId=0:ffffac110003:3cace777:5c8f8e89:28], expiration = 0, timestamp = 1552912020128, arrival = 0, brokerInTime = 1552912020130, brokerOutTime = 1552912020136, correlationId = null, replyTo = null, persistent = true, type = null, priority = 4, groupID = null, groupSequence = 0, targetConsumerId = null, compressed = false, userID = null, content = org.apache.activemq.util.ByteSequence@2e2f7d7c, marshalledProperties = null, dataStructure = null, redeliveryCounter = 0, size = 0, properties = null, readOnlyProperties = true, readOnlyBody = true, droppable = false, jmsXGroupFirstForConsumer = false, text = Count: 0}
12:27:00,192 INFO  [stdout] (default-threads - 6) Recieved a message.ActiveMQTextMessage {commandId = 6, responseRequired = false, messageId = ID:08ff0141c06d-42791-1552912014699-7:1:1:1:1, originalDestination = null, originalTransactionId = null, producerId = ID:08ff0141c06d-42791-1552912014699-7:1:1:1, destination = queue://queue01, transactionId = XID:[131077,globalId=0:ffffac110003:3cace777:5c8f8e89:17,branchId=0:ffffac110003:3cace777:5c8f8e89:27], expiration = 0, timestamp = 1552912020128, arrival = 0, brokerInTime = 1552912020130, brokerOutTime = 1552912020137, correlationId = null, replyTo = null, persistent = true, type = null, priority = 4, groupID = null, groupSequence = 0, targetConsumerId = null, compressed = false, userID = null, content = org.apache.activemq.util.ByteSequence@332c32, marshalledProperties = null, dataStructure = null, redeliveryCounter = 0, size = 0, properties = null, readOnlyProperties = true, readOnlyBody = true, droppable = false, jmsXGroupFirstForConsumer = false, text = Count: 0}
12:27:00,200 INFO  [stdout] (default-threads - 5) Recieved a message 02.ActiveMQTextMessage {commandId = 6, responseRequired = false, messageId = ID:08ff0141c06d-42791-1552912014699-9:1:1:1:1, originalDestination = null, originalTransactionId = null, producerId = ID:08ff0141c06d-42791-1552912014699-9:1:1:1, destination = topic://topic01, transactionId = XID:[131077,globalId=0:ffffac110003:3cace777:5c8f8e89:16,branchId=0:ffffac110003:3cace777:5c8f8e89:28], expiration = 0, timestamp = 1552912020128, arrival = 0, brokerInTime = 1552912020130, brokerOutTime = 1552912020137, correlationId = null, replyTo = null, persistent = true, type = null, priority = 4, groupID = null, groupSequence = 0, targetConsumerId = null, compressed = false, userID = null, content = org.apache.activemq.util.ByteSequence@21c51df2, marshalledProperties = null, dataStructure = null, redeliveryCounter = 0, size = 0, properties = null, readOnlyProperties = true, readOnlyBody = true, droppable = false, jmsXGroupFirstForConsumer = false, text = Count: 0}
```
