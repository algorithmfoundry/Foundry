/*
 * File:                ProcessLauncher.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Framework Lite
 *
 * Copyright September 20, 2006, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 *
 */

package gov.sandia.cognition.io;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;

/**
 * Launches a process as a separate thread and monitors the stdout and stderr,
 * throwing events when they update and exit
 *
 * @author Kevin R. Dixon
 * @since  1.0
 *
 */
public class ProcessLauncher
    extends Thread
{

    /**
     * Actual command line passed to the Runtime.exec() command
     */
    private String actualCommand;

    /**
     * Internal process that is spawned from the command
     */
    private Process process;

    /**
     * Listeners that are interested in the events fired by this object
     */
    private LinkedList<ProcessLauncherListener> listeners;

    /**
     * Thread that consumes stdout
     */
    private StreamGobbler stdout;

    /**
     * Thread that consumes stderr
     */
    private StreamGobbler stderr;

    /**
     * Thread class that asynchronously process the outputs from the process,
     * and throws events when a new line is encountered
     */
    private class StreamGobbler extends Thread
    {

        /**
         * Stream to monitor
         */
        InputStream stream;

        /**
         * Type of events to fire
         */
        ProcessLauncherEvent.EventType type;

        /**
         * Creates a new instance of StreamGobbler
         * @param stream 
         * Stream to monitor
         * @param type 
         * Type of events to fire
         */
        StreamGobbler(
            InputStream stream,
            ProcessLauncherEvent.EventType type )
        {
            this.stream = stream;
            this.type = type;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void run()
        {

            BufferedReader stdInput = new BufferedReader(
                new InputStreamReader( this.stream ) );

            try
            {
                String line;
                while ((line = stdInput.readLine()) != null)
                {
                    ProcessLauncher.this.fireEvent(
                        new ProcessLauncherEvent( this.type, line, null ) );
                }
            }
            catch (Exception e)
            {
                throw new RuntimeException( e );
            }


        }

    }

    /**
     * Creates a new instance of ProcessLauncher
     * @param command
     * String command, and all its arguments, to launch
     */
    public ProcessLauncher(
        String command )
    {

        String osName = System.getProperty( "os.name" );
        if (osName.equals( "Windows XP" ))
        {
            this.setActualCommand( "cmd.exe /C \"" + command + "\"" );
        }
        else
        {
            this.setActualCommand( command );
        }

        this.setProcess( null );
        this.stdout = null;
        this.stderr = null;
        this.listeners = new LinkedList<ProcessLauncherListener>();

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void run()
    {

        try
        {
            System.out.println( "Executing: " + this.getActualCommand() );
            this.setProcess( Runtime.getRuntime().exec( this.getActualCommand() ) );

            this.stdout = new StreamGobbler(
                this.getProcess().getInputStream(), ProcessLauncherEvent.EventType.STDOUT );
            this.stdout.start();

            this.stderr = new StreamGobbler(
                this.getProcess().getErrorStream(), ProcessLauncherEvent.EventType.STDERR );
            this.stderr.start();

            this.getProcess().waitFor();

            this.fireEvent( new ProcessLauncherEvent(
                ProcessLauncherEvent.EventType.FINISHED, null, this.getProcess() ) );
        }
        catch (Exception e)
        {
            throw new RuntimeException( e );
        }

    }

    /**
     * Adds the listener to the event queue
     * @param listener 
     * Object that wishes to receive update events
     */
    public void addListener(
        ProcessLauncherListener listener )
    {
        this.listeners.add( listener );
    }

    /**
     * Removes the given object from the event queue
     * @param listener 
     * Object that no longer wants to receive update events
     */
    public void removeListener(
        ProcessLauncherListener listener )
    {
        this.listeners.remove( listener );
    }

    /**
     * Passes the event to all the subscribed listeners
     * @param event 
     */
    private void fireEvent(
        ProcessLauncherEvent event )
    {
        for (ProcessLauncherListener listener : this.listeners)
        {
            listener.processLauncherEvent( event );
        }
    }

    /**
     * Stops the process.  The does not work on Windows, as we have to
     * envelope the desired command using "cmd.exe /c".  So the stop process
     * call actually cancels cmd.exe, and happily lets the desired command
     * continue running
     */
    public void stopProcess()
    {
        if (this.getProcess() != null)
        {
            this.getProcess().destroy();
            this.setProcess( null );
        }

        if (this.stdout != null)
        {
            this.stdout.interrupt();
            this.stdout = null;
        }

        if (this.stderr != null)
        {
            this.stderr.interrupt();
            this.stdout = null;
        }

        this.interrupt();

    }

    /**
     * Getter for process
     * @return 
     * Internal process that is spawned from the command
     */
    public Process getProcess()
    {
        return this.process;
    }

    /**
     * Setter for process
     * @param process 
     * Internal process that is spawned from the command
     */
    protected void setProcess(
        Process process )
    {
        this.process = process;
    }

    /**
     * Getter for actualCommand
     * @return 
     * Actual command line passed to the Runtime.exec() command
     */
    public String getActualCommand()
    {
        return this.actualCommand;
    }

    /**
     * Setter for actualCommand
     * @param actualCommand 
     * Actual command line passed to the Runtime.exec() command
     */
    protected void setActualCommand(
        String actualCommand )
    {
        this.actualCommand = actualCommand;
    }

}
