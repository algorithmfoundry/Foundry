/*
 * File:                ArrayBasedCognitiveModelInput.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Framework Lite
 *
 * Copyright March 29, 2006, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 *
 *
 */

package gov.sandia.cognition.framework.lite;

import gov.sandia.cognition.framework.CognitiveModelInput;
import gov.sandia.cognition.framework.SemanticIdentifier;
import java.io.Serializable;

/**
 * The ArrayBasedCognitiveModelInput class implements a CognitiveModelInput
 * that is used by the ArrayBasedPerceptionModule. As its name implies, it
 * is implemented by having two arrays underneath. The two arrays must be
 * of the same length. The first array is the array of the SemanticIdentifiers
 * to use as inputs and the second is the array of activation values for those
 * identifiers.
 *
 * @author Justin Basilico
 * @since 1.0
 */
public class ArrayBasedCognitiveModelInput
    extends java.lang.Object
    implements CognitiveModelInput, Serializable
{
    // Note: This class does not make use of the getters pattern because it
    // is expected to be high-performance and instead uses direct access to
    // the member variables.
    
    /** The identifiers to use as inputs. */
    private SemanticIdentifier[] identifiers = null;
    
    /** The activation values of the inputs. */
    private double[] values = null;
    
    /**
     * Creates a new instance of ArrayBasedCognitiveModelInput using the two
     * given arrays underneath by copying them.
     *
     * @param  identifiers The array of identifiers.
     * @param  values The array of values.
     */
    public ArrayBasedCognitiveModelInput(
        SemanticIdentifier[] identifiers,
        double[] values)
    {
        this(identifiers, values, true);
    }
    
    /**
     * Creates a new instance of ArrayBasedCognitiveModelInput using the two
     * given arrays underneath.
     *
     * @param  identifiers The array of identifiers.
     * @param  values The array of values.
     * @param  copy True to copy the given arrays and false to just use 
     *         references to them.
     */
    public ArrayBasedCognitiveModelInput(
        SemanticIdentifier[] identifiers,
        double[] values,
        boolean copy)
    {
        super();
        
        if ( identifiers == null || values == null )
        {
            // Error: Bad inputs.
            throw new NullPointerException();
        }
        else if ( identifiers.length != values.length )
        {
            // Error: The two arrays must be the same size.
            throw new IllegalArgumentException(
                  "The size of the array of identifiers must match "
                + "the size of the array of values.");
        }
        
        // We need top copy the arrays.
        if ( copy )
        {
            this.setIdentifiers(new SemanticIdentifier[identifiers.length]);
            this.setValues(new double[values.length]);
            
            // Use the system array copy.
            System.arraycopy(
                identifiers, 0, this.getIdentifiers(), 0, identifiers.length);
            System.arraycopy(values, 0, this.getValues(), 0, values.length);
        }
        else
        {
            // Just store them as references.
            this.setIdentifiers(identifiers);
            this.setValues(values);
        }
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
     * Gets the activation value of the given index in the array.
     *
     * @param  index The index of the activation value to get from the array.
     * @return The activation value at the given index.
     */
    public double getValue(
        int index)
    {
        return this.values[index];
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

    /**
     * Gets the array of identifiers.
     *
     * @return The array of identifiers.
     */
    // Note: This getter is final because the code doesn't actually call this
    // internally. Its just used so that subclasses can access the identifiers.
    protected final SemanticIdentifier[] getIdentifiers()
    {
        return identifiers;
    }

    /**
     * Gets the array of activation values.
     *
     * @return The array of activation values.
     */
    // Note: This getter is final because the code doesn't actually call this
    // internally. Its just used so that subclasses can access the values.
    protected final double[] getValues()
    {
        return values;
    }
    
    /**
     * Sets the array of identifiers.
     *
     * @param identifiers The new array of identifiers.
     */
    // Note: This setter is private because is should only be called once,
    // which is from teh constructor.
    private void setIdentifiers(
        SemanticIdentifier[] identifiers)
    {
        if ( identifiers == null )
        {
            // Error: Bad identifiers.
            throw new NullPointerException("The identifiers cannot be null.");
        }
            
        this.identifiers = identifiers;
    }

    /**
     * Sets the array of activation values.
     *
     * @param values The new array of activation values.
     */
    // Note: This setter is private because is should only be called once,
    // which is from teh constructor.
    private void setValues(
        double[] values)
    {
        if ( values == null )
        {
            // Error: Bad values.
            throw new NullPointerException("The values cannot be null.");
        }
        
        this.values = values;
    }
}

