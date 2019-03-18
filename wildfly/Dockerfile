FROM jboss/wildfly:15.0.0.Final

RUN /opt/jboss/wildfly/bin/add-user.sh admin Admin#70365 --silent

EXPOSE 9990 8080

COPY files/scripts/* /opt/jboss/wildfly/scripts/

RUN /opt/jboss/wildfly/scripts/configure-wildfly.sh

CMD ["/opt/jboss/wildfly/bin/standalone.sh", "-b", "0.0.0.0", "-bmanagement", "0.0.0.0"]

COPY files/activemq-rar-5.15.8.rar /opt/jboss/wildfly/standalone/deployments/

COPY files/mdb.jar /opt/jboss/wildfly/standalone/deployments/
