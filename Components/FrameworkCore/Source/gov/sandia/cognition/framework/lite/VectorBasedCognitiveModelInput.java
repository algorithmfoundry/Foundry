/*
 * File:                VectorBasedCognitiveModelInput.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Framework Lite
 *
 * Copyright June 25, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 *
 */

package gov.sandia.cognition.framework.lite;

import gov.sandia.cognition.framework.CognitiveModelInput;
import gov.sandia.cognition.framework.SemanticIdentifier;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.util.AbstractCloneableSerializable;
import gov.sandia.cognition.util.ObjectUtil;
import java.util.Arrays;

/**
 * Vector-based cognitive model input used by VectorBasedPerceptionModule.
 * This class associates SemanticSdentifiers with elements in a Vector.
 *
 * @author Kevin R. Dixon
 * @since  1.0
 *
 */
public class VectorBasedCognitiveModelInput
    extends AbstractCloneableSerializable
    implements CognitiveModelInput
{
    
    /**
     * Identifiers to use associate with the inputs
     */
    private SemanticIdentifier[] identifiers;
    
    /**
     * Activation values of the inputs
     */
    private Vector values;
    
    
    /**
     * Creates a new instance of VectorBasedCognitiveModelInput
     * @param identifiers 
     * Identifiers to use associate with the inputs
     * @param values 
     * Activation values of the inputs, must have the same number of semantic
     * identifiers as elements in the vector
     */
    public VectorBasedCognitiveModelInput(
        SemanticIdentifier[] identifiers,
        Vector values )
    {
        super();
        
        this.setIdentifiers( identifiers );
        this.setValues( values );
        if( identifiers.length != values.getDimensionality() )
        {
            throw new IllegalArgumentException( 
                 "The number of identifiers (" + identifiers.length + ") "
                + "must equal the number of values ("
                + values.getDimensionality() + ")");
        }
    }    

    /**
     * Copy Constructor
     * @param other VectorBasedCognitiveModelInput to clone
     */
    public VectorBasedCognitiveModelInput(
        VectorBasedCognitiveModelInput other )
    {
        this( other.getIdentifiers(), other.getValues().clone() );
    }
    
    /**
     * {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public VectorBasedCognitiveModelInput clone()
    {
        final VectorBasedCognitiveModelInput clone =
            (VectorBasedCognitiveModelInput) super.clone();
        
        clone.identifiers = Arrays.copyOf(
            this.identifiers, this.identifiers.length);
        clone.values = ObjectUtil.cloneSafe(this.values);

        return clone;
    }
    
    /**
     * Getter for identifiers
     * @return 
     * Identifiers to use associate with the inputs
     */
    protected SemanticIdentifier[] getIdentifiers()
    {
        return this.identifiers;
    }

    /**
     * Setter for identifiers
     * @param identifiers 
     * Identifiers to use associate with the inputs
     */
    protected void setIdentifiers(
        SemanticIdentifier[] identifiers)
    {
        this.identifiers = identifiers;
    }

    /**
     * Getter for values
     * @return 
     * Activation values of the inputs
     */
    public Vector getValues()
    {
        return this.values;
    }

    /**
     * Setter for values
     * @param values 
     * Activation values of the inputs
     */
    protected void setValues(
        Vector values)
    {
        this.values = values;
    }
    
    /**
     * Gets the SemanticIdentifier of the given index in the array.
     *
     * @param  index The index of the SemanticIdentifier to get from the array.
     * @return The SemanticIdentifier at the given index.
     */
    public SemanticIdentifier getIdentifier(
        int index)
    {
        return this.identifiers[index];
    }    
    
    /**
     * Gets the number of inputs in the array.
     *
     * @return The number of inputs in the array.
     */
    public int getNumInputs()
    {
        return this.identifiers.length;
    }    
    
}
