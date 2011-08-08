/*
 * File:                ProcessLauncherListener.java
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
 * Interface for receiving ProcessLauncher events
 *
 * @author Kevin R. Dixon
 * @since  1.0
 *
 */
public interface ProcessLauncherListener
{

    /**
     * 
     * Method that gets called when the ProcessLauncher is updated (for example,
     * the underlying process writes to stdout or stderr, or terminates)
     * @param event 
     * Event that descibes the update
     */
    void processLauncherEvent(
        ProcessLauncherEvent event );

}
