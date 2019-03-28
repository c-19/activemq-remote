/*
 * Copyright (c) 2018 - 2018, C19, all rights reserved.
 *
 * This software is licensed under under GPL-3.0-only or GPL-3.0-or-later, https://opensource.org/licenses/GPL-3.0
 *
 */

package io.c19.mdb.queue;


import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.inject.Inject;
import javax.jms.Message;
import javax.jms.MessageListener;

@MessageDriven( activationConfig = {
        @ActivationConfigProperty(propertyName = "destination",     propertyValue = "queue01"),
        @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
        @ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge")
})
public class QueueConsumer implements MessageListener
{
    @Inject
    private JobWrapper processor;

/*    public QueueConsumer()
    {
        //CDI no args
    }


    public QueueConsumer( JobProcessor processor )
    {
        this.processor = processor;
    }*/

    @Override
    public void onMessage(Message message)
    {
        System.out.println( "Recieved a message." + message.toString() );
        processor.getProcessor().doIt(  message.toString() );
        System.out.println( "Finished with message." + message.toString() );
    }
}