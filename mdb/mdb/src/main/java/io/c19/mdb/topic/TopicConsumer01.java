/*
 * Copyright (c) 2018 - 2018, C19, all rights reserved.
 *
 * This software is licensed under under GPL-3.0-only or GPL-3.0-or-later, https://opensource.org/licenses/GPL-3.0
 *
 */

package io.c19.mdb.topic;


import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.Message;
import javax.jms.MessageListener;

@MessageDriven( activationConfig = {
        @ActivationConfigProperty(propertyName = "destination",     propertyValue = "topic01"),
        @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Topic"),
        @ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge")
})
public class TopicConsumer01 implements MessageListener
{

    @Override
    public void onMessage(Message message)
    {
        System.out.println( "Recieved a message 01." + message.toString() );
    }
}