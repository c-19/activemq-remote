package io.c19.mdb.queue;

public interface JobProcessor
{
    void doIt( String message );
}
