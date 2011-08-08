/*
 * File:                CogxelStateLite.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Framework Lite
 *
 * Copyright March 15, 2006, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 *
 */

package gov.sandia.cognition.framework.lite;

import gov.sandia.cognition.collection.DynamicArrayMap;
import gov.sandia.cognition.framework.Cogxel;
import gov.sandia.cognition.framework.CogxelFactory;
import gov.sandia.cognition.framework.CogxelState;
import gov.sandia.cognition.framework.SemanticIdentifier;
import gov.sandia.cognition.util.AbstractCloneableSerializable;
import java.util.Collection;
import java.util.Iterator;

/**
 * The CogxelStateLite class implements a CogxelState to be used with the 
 * CognitiveModelLite.
 *
 * @author Justin Basilico
 * @since 1.0
 */
public class CogxelStateLite
    extends AbstractCloneableSerializable
    implements CogxelState
{
    // Note: This class does not make use of the getters pattern because it
    // is expected to be high-performance and instead uses direct access to
    // the member variables.
    
    /** The underlying mapping of identifiers to cogxels. */
    private DynamicArrayMap<Cogxel> cogxels = null;

    
    /**
     * Creates a new instance of CogxelStateLite
     */
    public CogxelStateLite()
    {
        super();
        
        this.setCogxels(new DynamicArrayMap<Cogxel>());
    }
    
    /**
     * Creates a new instance of CogxelStateLite with an expected initial 
     * capacity for the number of cogxels contained in it.
     *
     * @param  initialCapacity The initial maximum capacity, as defined as the
     *         maximum SemanticIdentifier expected.
     */
    public CogxelStateLite(
        int initialCapacity)
    {
        super();
        
        this.setCogxels(new DynamicArrayMap<Cogxel>(initialCapacity));
    }
    
    /**
     * Creates a copy of a CogxelStateLite
     *
     * @param other The CogxelState to copy.
     */
    public CogxelStateLite(
        CogxelState other)
    {
        super();
        
        DynamicArrayMap<Cogxel> localCogxels = new DynamicArrayMap<Cogxel>();
        
        for ( Cogxel cogxel : other.getCogxels() )
        {
            Cogxel copy = cogxel.clone();
            localCogxels.put(copy.getSemanticIdentifier().getIdentifier(), copy);
        }
        
        this.setCogxels(localCogxels);
    }



    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    @Override
    public CogxelStateLite clone()
    {
        final CogxelStateLite clone = (CogxelStateLite) super.clone();
        clone.cogxels = new DynamicArrayMap<Cogxel>();

        for (Cogxel cogxel : this.getCogxels())
        {
            Cogxel copy = cogxel.clone();
            clone.cogxels.put(
                copy.getSemanticIdentifier().getIdentifier(), copy);
        }
        return clone;
    }
    
    /**
     * Clears all of the Cogxels in this CogxelState.
     */
    public void clear()
    {
        this.cogxels.clear();
    }

    /**
     * {@inheritDoc}
     *
     * @param cogxel {@inheritDoc}
     * @throws IllegalArgumentException {@inheritDoc}
     */
    public void addCogxel(
        Cogxel cogxel)
    {
        if ( cogxel == null )
        {
            // Error: Bad cogxel.
            throw new IllegalArgumentException("Invalid cogxel given.");
        }
        
        SemanticIdentifier identifier = cogxel.getSemanticIdentifier();
        
        if ( identifier == null )
        {
            // Error: Bad identifier.
            throw new IllegalArgumentException("Invalid identifier given.");
        }
        
        this.cogxels.put(identifier.getIdentifier(), cogxel);
    }

    /**
     * {@inheritDoc}
     *
     * @param identifier {@inheritDoc}
     * @return {@inheritDoc}
     */
    public boolean hasCogxel(
        SemanticIdentifier identifier)
    {
        if ( identifier == null )
        {
            // Invalid identifier.
            return false;
        }
        else
        {
            // See if the tree contains the key.
            return this.cogxels.containsKey(identifier.getIdentifier());
        }
    }

    /**
     * {@inheritDoc}
     *
     * @param identifier {@inheritDoc}
     * @return {@inheritDoc}
     * @throws IllegalArgumentException {@inheritDoc}
     */
    public Cogxel getCogxel(
        SemanticIdentifier identifier)
    {
        if ( identifier == null )
        {
            // Error: Bad identifier.
            return null;
//            throw new IllegalArgumentException("Invalid identifier given.");
        }
        else
        {
            // Return the identifier.
            return this.cogxels.get(identifier.getIdentifier());
        }
    }

    /**
     * {@inheritDoc}
     * 
     * 
     * @return {@inheritDoc}
     * @param identifier {@inheritDoc}
     * @param factory {@inheritDoc}
     * @throws IllegalArgumentException {@inheritDoc}
     */
    public Cogxel getOrCreateCogxel(
        SemanticIdentifier identifier, 
        CogxelFactory factory)
    {   
        // See if the cogxel exists. This will throw the
        // IllegalArgumentException if the identifier is null.
        Cogxel cogxel = this.getCogxel(identifier);
        
        if ( cogxel == null )
        {
            // It does not exist. Add it to the state.
            cogxel = factory.createCogxel(identifier);
            this.addCogxel(cogxel);
        }
        // else - It exists so there is nothing to be done except return it.
        
        return cogxel;
    }

    /**
     * {@inheritDoc}
     *
     * @param identifier {@inheritDoc}
     * @return {@inheritDoc}
     * @throws IllegalArgumentException {@inheritDoc}
     */
    public boolean removeCogxel(
        SemanticIdentifier identifier)
    {
        if ( identifier == null )
        {
            // Error: Bad identifier.
            throw new IllegalArgumentException("Invalid identifier given.");
        }
        
        return (this.cogxels.remove(identifier.getIdentifier()) != null);
    }

    /**
     * {@inheritDoc}
     *
     * @param cogxel {@inheritDoc}
     * @return {@inheritDoc}
     * @throws IllegalArgumentException {@inheritDoc}
     */
    public boolean removeCogxel(
        Cogxel cogxel)
    {
        if ( cogxel == null )
        {
            // Error: Bad cogxel.
            throw new IllegalArgumentException("Invalid cogxel given.");
        }
        
        // Remove the Cogxel by removing the semantic identifier.
        return this.removeCogxel(cogxel.getSemanticIdentifier());
    }

    /**
     * {@inheritDoc}
     *
     * @param identifier {@inheritDoc}
     * @return {@inheritDoc}
     * @throws IllegalArgumentException {@inheritDoc}
     */
    public double getCogxelActivation(
        SemanticIdentifier identifier)
    {
        // Attempt to get the cogxel.
        Cogxel cogxel = this.getCogxel(identifier);
        
        if ( cogxel == null )
        {
            // No cogxel exists in the state.
            return 0.0;
        }
        else
        {
            // Get the cogxel activation.
            return cogxel.getActivation();
        }
    }
    
    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    public Collection<Cogxel> getCogxels()
    {
        return this.cogxels.values();
    }
    
    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    public int getNumCogxels()
    {
        return this.cogxels.size();
    }
    
    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    public Iterator<Cogxel> iterator()
    {
        return this.getCogxels().iterator();
    }

    /**
     * Sets the cogxels in the state.
     *
     * @param cogxels The new cogxels.
     */
    private void setCogxels(
        DynamicArrayMap<Cogxel> cogxels)
    {
        if ( cogxels == null )
        {
            // Error: Bad cogxels.
            throw new NullPointerException("The cogxels cannot be null.");
        }
        
        this.cogxels = cogxels;
    }

}

