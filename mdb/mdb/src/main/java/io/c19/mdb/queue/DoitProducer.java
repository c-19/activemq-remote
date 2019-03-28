package io.c19.mdb.queue;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.CDI;
import java.util.ServiceLoader;

public class DoitProducer
{
    @Produces
    public JobWrapper getDoitProcessor()
    {
        System.out.println( "Hi im inside the producer." );
        //ServiceLoader<JobProcessor> handler = ServiceLoader.load( JobProcessor.class );

        CDI<Object> cdi = CDI.current();
        JobProcessor service = cdi.select(JobProcessor.class).get();

        return new JobWrapper(service);
//        for( JobProcessor j: handler )
//        {
//            return new JobWrapper( j );
//        }

        //throw new RuntimeException( "Whoops" );
    }

}
