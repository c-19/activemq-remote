package io.c19.mdb.queue;

import io.c19.processor.JobProcessor;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.CDI;

public class DoitProducer
{
    @Produces
    @TheProcessor
    public JobProcessor getDoitProcessor()
    {
        System.out.println( "Hi im inside the producer." );

        CDI<Object> cdi = CDI.current();
        return cdi.select(JobProcessor.class).get();
    }

}
