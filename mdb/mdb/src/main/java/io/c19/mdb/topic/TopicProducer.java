package io.c19.mdb.topic;

import javax.annotation.Resource;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;

@Singleton
public class TopicProducer
{
    @Resource(mappedName = "java:/ConnectionFactory")
    private ConnectionFactory connectionFactory;

    @Resource(mappedName="java:/topic/topic01")
    private Topic topic;

    private int counter = 0;

    @Schedule(hour = "*", minute = "*", second = "*/10", persistent = false)
    public void createMessage( )
    {
        String message = "Count: " + counter++;
        try( Connection connection = connectionFactory.createConnection() )
        {
            Session session = connection.createSession( true, Session.AUTO_ACKNOWLEDGE );
            javax.jms.MessageProducer messageProducer = session.createProducer(topic);

            TextMessage textMessage = session.createTextMessage();
            textMessage.setText(message);
            messageProducer.send(textMessage);
        }
        catch( JMSException e )
        {
            e.printStackTrace();
        }
    }
}
