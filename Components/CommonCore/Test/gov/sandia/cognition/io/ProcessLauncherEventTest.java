/*
 * File:                ProcessLauncherEventTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Dec 11, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *  
 * 
 */

package gov.sandia.cognition.io;

import java.io.InputStream;
import java.io.OutputStream;
import junit.framework.TestCase;

/**
 * JUnit tests for class ProcessLauncherEventTest
 * @author Kevin R. Dixon
 */
public class ProcessLauncherEventTest
    extends TestCase
{

    /**
     * Current line of the event
     */
    public static final String CURRENT_LINE = "This snowflake tastes like fishsticks";

    /**
     * Event type
     */
    public static final ProcessLauncherEvent.EventType EVENT_TYPE = ProcessLauncherEvent.EventType.STDOUT;

    /**
     * Process
     */
    public static final Process PROCESS = new Process()
    {

        @Override
        public OutputStream getOutputStream()
        {
            throw new UnsupportedOperationException( "Not supported yet." );
        }

        @Override
        public InputStream getInputStream()
        {
            throw new UnsupportedOperationException( "Not supported yet." );
        }

        @Override
        public InputStream getErrorStream()
        {
            throw new UnsupportedOperationException( "Not supported yet." );
        }

        @Override
        public int waitFor() throws InterruptedException
        {
            throw new UnsupportedOperationException( "Not supported yet." );
        }

        @Override
        public int exitValue()
        {
            throw new UnsupportedOperationException( "Not supported yet." );
        }

        @Override
        public void destroy()
        {
            throw new UnsupportedOperationException( "Not supported yet." );
        }

    };

    /**
     * 
     * @return
     */
    public ProcessLauncherEvent createInstance()
    {
        return new ProcessLauncherEvent(
            EVENT_TYPE, CURRENT_LINE, PROCESS );
    }

    /**
     * Entry point for JUnit tests for class ProcessLauncherEventTest
     * @param testName name of this test
     */
    public ProcessLauncherEventTest(
        String testName )
    {
        super( testName );
    }

    /**
     * Test of getType method, of class ProcessLauncherEvent.
     */
    public void testGetType()
    {
        System.out.println( "getType" );
        ProcessLauncherEvent instance = this.createInstance();
        assertEquals( EVENT_TYPE, instance.getType() );
    }

    /**
     * Test of setType method, of class ProcessLauncherEvent.
     */
    public void testSetType()
    {
        System.out.println( "setType" );
        ProcessLauncherEvent instance = this.createInstance();
        assertEquals( EVENT_TYPE, instance.getType() );
        ProcessLauncherEvent.EventType t2 = ProcessLauncherEvent.EventType.FINISHED;
        instance.setType( t2 );
        assertEquals( t2, instance.getType() );
    }

    /**
     * Test of getCurrentLine method, of class ProcessLauncherEvent.
     */
    public void testGetCurrentLine()
    {
        System.out.println( "getCurrentLine" );
        ProcessLauncherEvent instance = this.createInstance();
        assertEquals( CURRENT_LINE, instance.getCurrentLine() );
    }

    /**
     * Test of setCurrentLine method, of class ProcessLauncherEvent.
     */
    public void testSetCurrentLine()
    {
        System.out.println( "setCurrentLine" );
        ProcessLauncherEvent instance = this.createInstance();
        assertEquals( CURRENT_LINE, instance.getCurrentLine() );

        String s2 = "Was President Lincoln OK?";
        instance.setCurrentLine( s2 );
        assertSame( s2, instance.getCurrentLine() );
    }

    /**
     * Test of getProcess method, of class ProcessLauncherEvent.
     */
    public void testGetProcess()
    {
        System.out.println( "getProcess" );
        ProcessLauncherEvent instance = this.createInstance();

        assertSame( PROCESS, instance.getProcess() );
    }

    /**
     * Test of setProcess method, of class ProcessLauncherEvent.
     */
    public void testSetProcess()
    {
        System.out.println( "setProcess" );
        ProcessLauncherEvent instance = this.createInstance();

        assertSame( PROCESS, instance.getProcess() );

        instance.setProcess( null );
        assertNull( instance.getProcess() );

        instance.setProcess( PROCESS );
        assertSame( PROCESS, instance.getProcess() );

    }

}
