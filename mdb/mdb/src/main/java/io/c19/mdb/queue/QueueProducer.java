package io.c19.mdb.queue;

import io.c19.mdb.DestinationProducer;

import javax.annotation.Resource;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.jms.ConnectionFactory;
import javax.jms.Queue;

@Singleton
public class QueueProducer
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
        DestinationProducer.sendMessage( connectionFactory, queue, message );
    }
}
