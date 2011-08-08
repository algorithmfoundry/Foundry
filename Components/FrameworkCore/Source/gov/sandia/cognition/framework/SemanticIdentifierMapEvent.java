/*
 * File:                SemanticIdentifierMapEvent.java
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
 * The <code>SemanticIdentifierMapEvent</code> class implements an event
 * object for the <code>SemanticIdentifierMapListener</code> interface to
 * make use of. The events are fired from the <code>SemanticIdentifierMap</code>
 * class.
 *
 * @author Justin Basilico
 * @since 1.0
 */
public class SemanticIdentifierMapEvent
    extends java.util.EventObject
{
    /** The map that the event happened in. */
    private SemanticIdentifierMap map = null;
    
    /** The type of event that this is. */
    private SemanticIdentifierMapEventType eventType = null;
    
    /** The SemanticIdentifier causing the event. */
    private SemanticIdentifier identifier = null;
    
    /** 
     * Creates a new instance of CognitiveModelStateChangeEvent.
     *
     * @param  map The map that the event happened in.
     * @param  eventType The type of event that this is.
     * @param  identifier The identifier that caused the event.
     */
    public SemanticIdentifierMapEvent(
        SemanticIdentifierMap map,
        SemanticIdentifierMapEventType eventType,
        SemanticIdentifier identifier)
    {
        super(map);
        
        this.setMap(map);
        this.setEventType(eventType);
        this.setIdentifier(identifier);
    }

    /**
     * Gets the SemanticIdentifierMap that the event happened in.
     *
     * @return The SemanticIdentifierMap the event happened in.
     */
    public SemanticIdentifierMap getMap()
    {
        return map;
    }

    /**
     * Gets the type of the event.
     *
     * @return The event type.
     */
    public SemanticIdentifierMapEventType getEventType()
    {
        return this.eventType;
    }
    
    /**
     * Gets the SemanticIdentifier involved in the event.
     *
     * @return The SemanticIdentifier involved in the event.
     */
    public SemanticIdentifier getIdentifier()
    {
        return this.identifier;
    }

    /**
     * Sets the SemanticIdentifierMap that the event happened in.
     *
     * @param  map SemanticIdentifierMap the event happened in.
     */
    public void setMap(
        SemanticIdentifierMap map)
    {
        this.map = map;
    }
    
    /**
     * Sets the type of the event.
     *
     * @param  eventType the event type.
     */
    public void setEventType(
        SemanticIdentifierMapEventType eventType)
    {
        this.eventType = eventType;
    }

    /**
     * Sets the SemanticIdentifier involved in the event.
     *
     * @param  identifier The SemanticIdentifier involved in the event.
     */
    public void setIdentifier(
        SemanticIdentifier identifier)
    {
        this.identifier = identifier;
    }
}

