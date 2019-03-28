#!/bin/bash

echo "Configuring activemq resource adapter..."
$JBOSS_HOME/bin/jboss-cli.sh --file=$JBOSS_HOME/scripts/add-activemq-ra.cli --properties=$JBOSS_HOME/scripts/activemq-ra.properties
echo "Configuration of resource adapter complete."
echo "Configuring queue01..."
$JBOSS_HOME/bin/jboss-cli.sh --file=$JBOSS_HOME/scripts/add-activemq-destination.cli --properties=$JBOSS_HOME/scripts/queue01.properties
echo "Configuration of queue01 complete."
echo "Configuring topic01..."
$JBOSS_HOME/bin/jboss-cli.sh --file=$JBOSS_HOME/scripts/add-activemq-destination.cli --properties=$JBOSS_HOME/scripts/topic01.properties
echo "Configuration of topic01 complete."
echo "Configuring processors on global module path..."
$JBOSS_HOME/bin/jboss-cli.sh --file=$JBOSS_HOME/scripts/add-processors.cli
echo "Configuration of processors on global module path complete."
echo "Removing configuration history..."
rm -Rf /opt/jboss/wildfly/standalone/configuration/standalone_xml_history/
echo "Removed configuration history."
