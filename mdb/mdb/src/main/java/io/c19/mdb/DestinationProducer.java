package io.c19.mdb;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

public class DestinationProducer
{
    public static void sendMessage(ConnectionFactory connectionFactory, Destination destination, String message)
    {
        try( Connection connection = connectionFactory.createConnection() )
        {
            Session session = connection.createSession( true, Session.AUTO_ACKNOWLEDGE );
            MessageProducer messageProducer = session.createProducer(destination);

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
