FROM jboss/wildfly:15.0.0.Final

RUN /opt/jboss/wildfly/bin/add-user.sh admin Admin#70365 --silent

EXPOSE 9990 8080

COPY files/scripts/* /opt/jboss/wildfly/scripts/

RUN /opt/jboss/wildfly/scripts/configure-wildfly.sh

COPY files/api.jar /opt/jboss/wildfly/processors/
COPY files/impl.jar /opt/jboss/wildfly/processors/

RUN /opt/jboss/wildfly/bin/jboss-cli.sh --command="module add --name=io.c19.api \
                --resources=wildfly/processors/api.jar \
                --dependencies=javaee.api"

RUN /opt/jboss/wildfly/bin/jboss-cli.sh --command="module add --name=io.c19.processors \
                --resources=wildfly/processors/impl.jar \
                --dependencies=javaee.api,io.c19.api"


CMD ["/opt/jboss/wildfly/bin/standalone.sh", "-b", "0.0.0.0", "-bmanagement", "0.0.0.0"]

COPY files/activemq-rar-5.15.8.rar /opt/jboss/wildfly/standalone/deployments/

COPY files/mdb.jar /opt/jboss/wildfly/standalone/deployments/
