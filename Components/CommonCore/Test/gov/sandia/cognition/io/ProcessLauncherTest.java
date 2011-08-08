/*
 * File:                ProcessLauncherTest.java
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
 * JUnit tests for class ProcessLauncherTest
 * @author Kevin R. Dixon
 */
public class ProcessLauncherTest
    extends TestCase
{

    /**
     * ProcessLauncherListener used for listening
     */
    public static class Listener
        implements ProcessLauncherListener
    {

        public void processLauncherEvent(
            ProcessLauncherEvent event )
        {
            System.out.println( "Got an event: " + event );
        }

    }

    /**
     * Command used in creating the ProcessLauncher
     */
    private static final String command = "This is a test";

    /**
     * Creates a new ProcessLauncher
     * @return new ProcessLauncher
     */
    public ProcessLauncher createInstance()
    {
        return new ProcessLauncher( command );
    }

    /**
     * Entry point for JUnit tests for class ProcessLauncherTest
     * @param testName name of this test
     */
    public ProcessLauncherTest(
        String testName )
    {
        super( testName );
    }

    /**
     * Test of run method, of class ProcessLauncher.
     */
    public void testRun()
    {
        System.out.println( "run" );
        ProcessLauncher instance = this.createInstance();
        instance.setActualCommand( command );
        try
        {
            instance.run();
            fail( "Should have thrown \"NotFound\" Exception!" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }


    }

    /**
     * Test of addListener method, of class ProcessLauncher.
     */
    public void testAddListener()
    {
        System.out.println( "addListener" );
        ProcessLauncherListener listener = new Listener();
        ProcessLauncher instance = this.createInstance();

        instance.addListener( listener );
        instance.removeListener( listener );
    }

    /**
     * Test of removeListener method, of class ProcessLauncher.
     */
    public void testRemoveListener()
    {
        System.out.println( "removeListener" );
        ProcessLauncherListener listener = new Listener();
        ProcessLauncher instance = this.createInstance();
        instance.removeListener( listener );


        instance.addListener( listener );
        instance.removeListener( listener );
    }

    /**
     * Test of stopProcess method, of class ProcessLauncher.
     */
    public void testStopProcess()
    {
        System.out.println( "stopProcess" );
        ProcessLauncher instance = this.createInstance();

        assertNull( instance.getProcess() );
        instance.stopProcess();
        assertNull( instance.getProcess() );

    }

    /**
     * Test of getProcess method, of class ProcessLauncher.
     */
    public void testGetProcess()
    {
        System.out.println( "getProcess" );
        ProcessLauncher instance = this.createInstance();
        Process process = instance.getProcess();
        assertNull( process );
    }

    /**
     * Test of setProcess method, of class ProcessLauncher.
     */
    public void testSetProcess()
    {
        System.out.println( "setProcess" );
        ProcessLauncher instance = this.createInstance();
        Process process = instance.getProcess();
        assertNull( process );
        process = new Process()
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

        instance.setProcess( process );
        assertSame( process, instance.getProcess() );
    }

    /**
     * Test of getActualCommand method, of class ProcessLauncher.
     */
    public void testGetActualCommand()
    {
        System.out.println( "getActualCommand" );
        ProcessLauncher instance = this.createInstance();
        assertNotNull( instance.getActualCommand() );
        assertTrue( instance.getActualCommand().contains( ProcessLauncherTest.command ) );

    }

    /**
     * Test of setActualCommand method, of class ProcessLauncher.
     */
    public void testSetActualCommand()
    {
        System.out.println( "setActualCommand" );
        ProcessLauncher instance = this.createInstance();
        assertNotNull( instance.getActualCommand() );
        assertTrue( instance.getActualCommand().contains( ProcessLauncherTest.command ) );

        String c2 = "Oh, boy!  Sleep!  That's where I'm a viking!";
        instance.setActualCommand( c2 );
        assertSame( c2, instance.getActualCommand() );

    }

}
