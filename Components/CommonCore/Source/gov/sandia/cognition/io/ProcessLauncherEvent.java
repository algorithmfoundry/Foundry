/*
 * File:                ProcessLauncherEvent.java
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

/**
 * Event that gets fired when the ProcessLauncher is updated (for example,
 * the underlying process writes to stdout or stderr, or terminates)
 *
 * @author Kevin R. Dixon
 * @since  1.0
 *
 */
public class ProcessLauncherEvent
{

    /**
     * Types of events that may be fired
     */
    public enum EventType
    {

        /**
         * The underlying process has updated its stdout
         */
        STDOUT,
        /**
         * The underlying process has updated its stderr
         */
        STDERR,
        /**
         * The underlying process has terminated itself
         */
        FINISHED

    }

    /**
     * Type of event
     */
    private ProcessLauncherEvent.EventType type;

    /**
     * If non-null, these tokens correspond to the current line on the
     * stdout/stderr stream
     */
    private String currentLine;

    /**
     * The underlying process that fired this event
     */
    private Process process;

    /**
     * Creates a new instance of ProcessLauncherEvent
     * @param type 
     * Type of event
     * @param currentLine 
     * If non-null, these tokens correspond to the current line on the
     * stdout/stderr stream
     * @param process 
     * The underlying process that fired this event
     */
    public ProcessLauncherEvent(
        ProcessLauncherEvent.EventType type,
        String currentLine,
        Process process )
    {
        this.setType( type );
        this.setCurrentLine( currentLine );
        this.setProcess( process );
    }

    /**
     * Getter for type
     * @return Type of event
     */
    public ProcessLauncherEvent.EventType getType()
    {
        return this.type;
    }

    /**
     * Setter for type
     * @param type Type of event
     */
    protected void setType(
        ProcessLauncherEvent.EventType type )
    {
        this.type = type;
    }

    /**
     * Getter for currentLine
     * @return 
     * If non-null, this String corresponds to the current line on the
     * stdout/stderr stream
     */
    public String getCurrentLine()
    {
        return this.currentLine;
    }

    /**
     * Setter for currentLine
     * @param currentLine 
     * If non-null, this String corresponds to the current line on the
     * stdout/stderr stream
     */
    protected void setCurrentLine(
        String currentLine )
    {
        this.currentLine = currentLine;
    }

    /**
     * Getter for process
     * @return 
     * The underlying process that fired this event
     */
    public Process getProcess()
    {
        return this.process;
    }

    /**
     * Setter for process
     * @param process 
     * The underlying process that fired this event
     */
    protected void setProcess(
        Process process )
    {
        this.process = process;
    }

}
