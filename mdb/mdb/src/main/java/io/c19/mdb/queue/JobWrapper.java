package io.c19.mdb.queue;

public class JobWrapper
{
    private JobProcessor processor;

    public JobWrapper( JobProcessor processor )
    {
        this.processor = processor;
    }

    public JobProcessor getProcessor()
    {
        return this.processor;
    }

}
