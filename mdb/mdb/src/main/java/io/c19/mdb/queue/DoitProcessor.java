package io.c19.mdb.queue;

import javax.inject.Inject;

public class DoitProcessor implements JobProcessor
{
    private ActuallyDoitProcessor processor;
    
    @Inject
    public DoitProcessor(ActuallyDoitProcessor processor)
    {
        this.processor = processor;
    }

    @Override
    public void doIt( String message )
    {
        processor.actuallyDoit( message );
    }

}
