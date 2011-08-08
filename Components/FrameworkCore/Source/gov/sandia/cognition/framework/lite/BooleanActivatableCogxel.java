/*
 * File:                BooleanActivatableCogxel.java
 * Authors:             Jonathan McClain
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Framework Lite
 *
 * Copyright July 3, 2006, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 *
 */

package gov.sandia.cognition.framework.lite;

import gov.sandia.cognition.framework.ActivatableCogxel;
import gov.sandia.cognition.framework.DefaultCogxel;
import gov.sandia.cognition.framework.SemanticIdentifier;

/**
 * BooleanActivatableCogxel extends the DefaultCogxel class to add an "activated" flag.
 * 
 * 
 * @author Jonathan McClain
 * @since 1.0
 */
public class BooleanActivatableCogxel
        extends DefaultCogxel
        implements ActivatableCogxel
{
    /** A boolean indicating whether the cogxel is activated. */
    private boolean activated = false;
    
    /**
     * Creates copy of the given cogxel.
     *
     * @param other The cogxel to copy.
     */
    public BooleanActivatableCogxel(
        BooleanActivatableCogxel other)
    {
        this(
            other.getSemanticIdentifier(), 
            other.getActivation(),
            other.getActivated());
    }
    
    /**
     * Creates a new instance of BooleanActivatableCogxel.
     * Default activation is 0.0. 
     * Default flag is false.
     * 
     * 
     * @param identifier The SemanticIdentifier for the cogxel.
     */
    public BooleanActivatableCogxel(
        SemanticIdentifier identifier)
    {
        this( identifier, 0.0, false);
    }
    
    /**
     * Creates a new instance of BooleanActivatableCogxel.
     * Default flag is false.
     * 
     * 
     * @param identifier The SemanticIdentifier for the cogxel.
     * @param activation The initial activation for the cogxel.
     */
    public BooleanActivatableCogxel(
        SemanticIdentifier identifier,
        double activation)
    {
        this( identifier, activation, false);
    }
    
    /**
     * Creates a new instance of BooleanActivatableCogxel.
     * Default activation is 0.0.
     * 
     * 
     * @param identifier The SemanticIdentifier for the cogxel.
     * @param activated A boolean indicating whether the cogxel is activated or
     * not.
     */
    public BooleanActivatableCogxel(
        SemanticIdentifier identifier,
        boolean activated)
    {
        this( identifier, 0.0, activated);
    }
    
    /**
     * Creates a new instance of BooleanActivatableCogxel.
     * 
     * 
     * @param identifier The SemanticIdentifier for the cogxel.
     * @param activation The initial activation for the cogxel.
     * @param activated A boolean indicating whether the cogxel is activated or
     * not.
     */
    public BooleanActivatableCogxel(
        SemanticIdentifier identifier,
        double activation,
        boolean activated)
    {
        super(identifier, activation);
        this.setActivated(activated);
    }
    
    

    /**
     * {@inheritDoc}
     * @return {@inheritDoc}
     */
    public boolean isActivated()
    {
        return this.getActivated();
    }
    
    /**
     * Gets a boolean indicating whether the cogxel is activated.
     *
     * @return True if activated, false otherwise.
     */
    private boolean getActivated()
    {
        return this.activated;
    }
    
    /**
     * Sets a boolean indicating whether the cogxel is activated.
     *
     * @param activated True if activated, false otherwise.
     */
    public void setActivated(
        boolean activated)
    {
        this.activated = activated;
    }
}
