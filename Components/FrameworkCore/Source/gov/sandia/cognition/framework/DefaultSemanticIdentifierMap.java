/*
 * File:                DefaultSemanticIdentifierMap.java
 * Authors:             Justin Basilico and Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Framework Lite
 *
 * Copyright March 1, 2006, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 *
 */

package gov.sandia.cognition.framework;

import gov.sandia.cognition.util.AbstractCloneableSerializable;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;

/**
 * The DefaultSemanticIdentifierMap is an implementation of 
 * SemanticIdentifierMap that is backed by a HashMap (a hashtable).
 * 
 * @author Justin Basilico
 * @author Kevin R. Dixon
 * @since 1.0
 */
public class DefaultSemanticIdentifierMap
    extends AbstractCloneableSerializable
    implements SemanticIdentifierMap
{
    /** The counter of identifiers, which ensures that each one is unique. */
    private int identifierCounter = 0;
    
    /** The mapping of semantic labels to semantic identifiers. */
    private LinkedHashMap<SemanticLabel, SemanticIdentifier> mapping = null;
    
    /** The listeners for this map. */
    private transient LinkedList<SemanticIdentifierMapListener> listeners 
        = null;
    
    /**
     * Creates a new instance of DefaultSemanticIdentifierMap.
     */
    public DefaultSemanticIdentifierMap()
    {
        super();
        
        this.resetIdentifierCounter();
        this.setMapping(new LinkedHashMap<SemanticLabel, SemanticIdentifier>());
        this.setListeners(null);
    }

    @Override
    public DefaultSemanticIdentifierMap clone()
    {
        final DefaultSemanticIdentifierMap clone =
            (DefaultSemanticIdentifierMap) super.clone();
        clone.mapping = new LinkedHashMap<SemanticLabel, SemanticIdentifier>(
            this.mapping);
        clone.listeners = null;
        return clone;
    }

    /**
     * {@inheritDoc}
     *
     * @param  label {@inheritDoc}
     * @return {@inheritDoc}
     */
    public SemanticIdentifier findIdentifier(
        SemanticLabel label)
    {
        if ( label == null )
        {
            return null;
        }
        else
        {
            return this.getMapping().get(label);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @param     label {@inheritDoc}
     * @return    {@inheritDoc}
     * @throws IllegalArgumentException {@inheritDoc}
     */
    public SemanticIdentifier addLabel(
        SemanticLabel label)
    {
        if ( label == null )
        {
            // Error: Bad label.
            throw new IllegalArgumentException("The label cannot be null.");
        }
        
        // Look to see if the identifier exists.
        SemanticIdentifier identifier = this.findIdentifier(label);
        
        if ( identifier == null )
        {
            // Identifier does not exist so create a new one.
            identifier = new DefaultSemanticIdentifier(
                    label, this.getNewIdentifier());
        
            // Add the identifier to the mapping.
            this.getMapping().put(label, identifier);
            
            // Fire the event for the mapping changed.
            this.fireSemanticIdentifierAddedEvent(identifier);
        }
        
        // Return the identifier.
        return identifier;
    }
    
    /**
     * {@inheritDoc}
     *
     * @param  labels {@inheritDoc}
     * @return {@inheritDoc}
     * @throws IllegalArgumentException {@inheritDoc}
     */
    public ArrayList<SemanticIdentifier> addLabels(
        Collection<SemanticLabel> labels)
    {
        // Loop over the labels and add them.
        ArrayList<SemanticIdentifier> identifiers = 
            new ArrayList<SemanticIdentifier>(labels.size());
        
        for ( SemanticLabel label : labels )
        {
            SemanticIdentifier identifier = this.addLabel(label);
            identifiers.add(identifier);
        }
        
        return identifiers;
    }
    
    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    public Collection<SemanticIdentifier> getIdentifiers()
    {
        return this.getMapping().values();
    }

    /**
     * Gets a new identifier for a new label.
     *
     * @return A new identifier.
     */
    protected int getNewIdentifier()
    {
        int result = this.getIdentifierCounter();
        this.setIdentifierCounter(this.getIdentifierCounter() + 1);
        return result;
    }

    /**
     * {@inheritDoc}
     *
     * @param  listener {@inheritDoc}
     */
    public void addSemanticIdentifierMapListener(
        SemanticIdentifierMapListener listener)
    {
        if ( listener == null )
        {
            return;
        }
        
        if ( this.getListeners() == null )
        {
            this.setListeners(new LinkedList<SemanticIdentifierMapListener>());
        }
        
        this.getListeners().add(listener);
    }
    
    /**
     * {@inheritDoc}
     *
     * @param  listener {@inheritDoc}
     */
    public void removeSemanticIdentifierMapListener(
        SemanticIdentifierMapListener listener)
    {
        if ( listener == null )
        {
            // Error: Bad parameter.
            return;
        }
        else if ( this.getListeners() == null )
        {
            // No listeners.
            return;
        }
        
        this.getListeners().remove(listener);
    }
    
    /**
     * Fires off a SemanticIdentifierMapEvent of type SemanticIdentifierAdded
     * for the given identifier.
     *
     * @param  identifier The identifier that was added.
     */
    protected void fireSemanticIdentifierAddedEvent(
        SemanticIdentifier identifier)
    {
        LinkedList<SemanticIdentifierMapListener> localListeners =
            this.getListeners();
        
        if ( localListeners != null && !localListeners.isEmpty() )
        {
            // Create the event.
            SemanticIdentifierMapEvent event = 
                new SemanticIdentifierMapEvent(this, 
                    SemanticIdentifierMapEventType.SemanticIdentifierAdded, 
                    identifier);
            
            // Fire the event on all the listeners.
            for ( SemanticIdentifierMapListener listener : localListeners )
            {
                listener.semanticIdentifierAdded(event);
            }
        }
    }
    
    /**
     * Resets the identifier counter. 
     * 
     * Should only be called once.
     */
    private void resetIdentifierCounter()
    {
        this.setIdentifierCounter(0);
    }
    
    /**
     * Sets the mapping used by the object.
     *
     * @param mapping The new mapping.
     */
    protected void setMapping(
        LinkedHashMap<SemanticLabel, SemanticIdentifier> mapping)
    {
        if ( mapping == null )
        {
            // Error: Bad mapping.
            throw new NullPointerException("The mapping cannot be null.");
        }
        
        this.mapping = mapping;
    }

    /***
     * Sets the list of listeners for this mapping.
     *
     * @param  listeners The new list of listeners for this mapping.
     */
    // Note: This setter is private because we do not want anyone else to mess
    // with the listeners. They can be added/removed using the proper functions.
    private void setListeners(
        LinkedList<SemanticIdentifierMapListener> listeners)
    {
        this.listeners = listeners;
    }

    /**
     * Getter for identifierCounter.
     *
     * @return The counter of identifiers, which ensures that each one is unique.
     */
    protected int getIdentifierCounter()
    {
        return this.identifierCounter;
    }

    /**
     * Setter for identifier Counter.
     *
     * @param identifierCounter
     * The counter of identifiers, which ensures that each one is unique.
     */
    protected void setIdentifierCounter(
        int identifierCounter)
    {
        this.identifierCounter = identifierCounter;
    }

    /**
     * Getter for mapping.
     *
     * @return The mapping of semantic labels to semantic identifiers.
     */
    // Note: This getter is protected because we do not want external objects
    // changing it.
    protected LinkedHashMap<SemanticLabel, SemanticIdentifier> getMapping()
    {
        return this.mapping;
    }

    /**
     * Getter for listeners.
     *
     * @return The listeners for this map.
     */
    protected LinkedList<SemanticIdentifierMapListener> getListeners()
    {
        return this.listeners;
    }
}

