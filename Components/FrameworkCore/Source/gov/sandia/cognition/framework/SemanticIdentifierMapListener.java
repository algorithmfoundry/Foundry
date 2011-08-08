/*
 * File:                SemanticIdentifierMapListener.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Framework Lite
 *
 * Copyright April 24, 2006, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 *
 */

package gov.sandia.cognition.framework;

/**
 * The SemanticIdentifierMapListener defines an EventListener for the
 * SemanticIdentifierMap.
 *
 * @author Justin Basilico
 * @since 1.0
 */
public interface SemanticIdentifierMapListener
    extends java.util.EventListener
{
    /**
     * This event is triggered when a SemanticIdentifier has been added to the
     * map.
     *
     * @param  event The event data.
     */
    public void semanticIdentifierAdded(
        SemanticIdentifierMapEvent event);
}

