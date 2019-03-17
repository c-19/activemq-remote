FROM cap-template/wildfly:15.0
 
RUN /opt/java/wildfly/bin/add-user.sh admin Admin#70365 --silent 

EXPOSE 9990 8080

CMD ["/opt/java/wildfly/bin/standalone.sh", "-b", "0.0.0.0", "-bmanagement", "0.0.0.0"]
    
COPY files/activemq-rar-5.15.8.rar /opt/java/wildfly/standalone/deployments

COPY files/standalone.xml /opt/java/wildfly/standalone/configuration/
