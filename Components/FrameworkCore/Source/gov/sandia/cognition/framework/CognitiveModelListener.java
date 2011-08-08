/*
 * File:                CognitiveModelListener.java
 * Authors:             Justin Basilico and Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Framework Lite
 *
 * Copyright February 10, 2006, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 *
 */

package gov.sandia.cognition.framework;

/**
 * The CognitiveModelListener interface is an event listener that listens for
 * events on a CognitiveModel.
 *
 * @author Justin Basilico
 * @author Kevin R. Dixon
 * @since  1.0
 */
public interface CognitiveModelListener
    extends java.util.EventListener
{
    /**
     * This method is called on the listener when the model's state has 
     * changed. The event object for the change is passed in.
     *
     * @param event The event data
     */
    public void modelStateChanged(
        CognitiveModelStateChangeEvent event);
}
