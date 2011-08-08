/*
 * File:                DefaultCogxel.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Framework Lite
 *
 * Copyright May 5, 2006, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 *
 */

package gov.sandia.cognition.framework;

import gov.sandia.cognition.util.AbstractCloneableSerializable;

/**
 * The <code>DefaultCogxel</code> provides a default implementation of the
 * <code>Cogxel</code> interface that just stores the necessary peices of
 * information: the SemanticIdentifier and its activation.
 *
 * @author Justin Basilico
 * @since 1.0
 */
public class DefaultCogxel
    extends AbstractCloneableSerializable
    implements Cogxel
{
    /** The SemanticIdentifier that identifies the Cogxel. */
    private SemanticIdentifier semanticIdentifier = null;
    
    /** The current activation of the Cogxel. */
    private double activation = 0.0;
    
    /** 
     * Creates a new instance of Cogxel. The default activation is 0.0.
     *
     * @param identifier The SemanticIdentifier for the Cogxel.
     * @throws IllegalArgumentException If an invalid identifier is given.
     */
    public DefaultCogxel(
        SemanticIdentifier identifier)
    {
        this(identifier, 0.0);
    }
    
    /** 
     * Creates a new instance of Cogxel.
     *
     * @param identifier The SemanticIdentifier for the Cogxel.
     * @param activation The initial activation for the Cogxel.
     * @throws IllegalArgumentException If an invalid identifier is given.
     */
    public DefaultCogxel(
        SemanticIdentifier identifier,
        double activation)
    {
        super();
        
        this.setSemanticIdentifier(identifier);
        this.setActivation(activation);
    }
    
    /**
     * Creates a copy of a given Cogxel.
     *
     * @param  other The Cogxel to copy.
     */
    public DefaultCogxel(
        DefaultCogxel other)
    {
        this(other.getSemanticIdentifier(), other.getActivation());
    }
    
    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    @Override
    public DefaultCogxel clone()
    {
        return (DefaultCogxel) super.clone();
    }
    
    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    public SemanticIdentifier getSemanticIdentifier()
    {
        return this.semanticIdentifier;
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    public SemanticLabel getSemanticLabel()
    {
        return this.getSemanticIdentifier().getLabel();
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    public double getActivation()
    {
        return this.activation;
    }
    
    /**
     * Sets the SemanticIdentifier for the Cogxel.
     *
     * @param semanticIdentifier The new SemanticIdentifier.
     * @throws NullPointerException If an invalid identifier is given.
     */
    // Note: This setter is private because it should only be called once
    // (from the constructor).
    private void setSemanticIdentifier(
        SemanticIdentifier semanticIdentifier)
    {
        if ( semanticIdentifier == null )
        {
            // Error: Bad identifier.
            throw new NullPointerException(
                "A SemanticIdentifier cannot be null.");
        }
        
        this.semanticIdentifier = semanticIdentifier;
    }
    
    /**
     * {@inheritDoc}
     *
     * @param activation {@inheritDoc}
     */
    public void setActivation(
        double activation)
    {
        this.activation = activation;
    }
}

