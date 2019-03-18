package io.c19.mdb;

import javax.annotation.Resource;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;

@Singleton
public class MessageProducer
{
    @Resource(mappedName = "java:/ConnectionFactory")
    private ConnectionFactory connectionFactory;

    @Resource(mappedName="java:/queue/queue01")
    private Queue queue;

    private int counter = 0;

    @Schedule(hour = "*", minute = "*", second = "*/10", persistent = false)
    public void createMessage( )
    {
        String message = "Count: " + counter++;
        try( Connection connection = connectionFactory.createConnection() )
        {
            Session session = connection.createSession( true, Session.AUTO_ACKNOWLEDGE );
            javax.jms.MessageProducer messageProducer = session.createProducer(queue);

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
