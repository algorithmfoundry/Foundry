/*
 * File:                LinearClassifier.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright October 9, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.learning.function.scalar;

import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.Vectorizable;
import gov.sandia.cognition.evaluator.Evaluator;
import gov.sandia.cognition.math.matrix.VectorInputEvaluator;
import gov.sandia.cognition.util.AbstractCloneableSerializable;
import gov.sandia.cognition.util.ObjectUtil;

/**
 * LinearDiscriminant takes the dot product between the weight Vector and
 * the input Vector.  This is a mapping from the M-dimensional space to the
 * scalar (real) space.
 *
 * @author Kevin R. Dixon
 * @since  2.0
 *
 */
public class LinearDiscriminant
    extends AbstractCloneableSerializable
    implements Evaluator<Vector,Double>,
    Vectorizable,
    VectorInputEvaluator<Vector,Double>
{
    
    /**
     * Weight Vector to dot-product with the input
     */
    private Vector weightVector;
    
    /**
     * Creates a new instance of LinearClassifier
     */
    public LinearDiscriminant()
    {
        this( (Vector) null );
    }    
    
    /**
     * Creates a new instance of LinearClassifier
     * @param weightVector 
     * Weight Vector to dot-product with the input
     */
    public LinearDiscriminant(
        Vector weightVector )
    {
        this.setWeightVector( weightVector );
    }

    @Override
    public LinearDiscriminant clone()
    {
        LinearDiscriminant clone = (LinearDiscriminant) super.clone();
        clone.setWeightVector( ObjectUtil.cloneSafe( this.getWeightVector() ) );
        return clone;
    }
    
    public int getInputDimensionality()
    {
        return this.getWeightVector().getDimensionality();
    }
    
    /**
     * Getter for weightVector
     * @return 
     * Weight Vector to dot-product with the input
     */
    public Vector getWeightVector()
    {
        return this.weightVector;
    }

    /**
     * Setter for weightVector
     * @param weightVector 
     * Weight Vector to dot-product with the input
     */
    public void setWeightVector(
        Vector weightVector)
    {
        this.weightVector = weightVector;
    }

    public Double evaluate(
        Vector input)
    {
        return this.getWeightVector().dotProduct( input );
    }

    public Vector convertToVector()
    {
        return this.getWeightVector();
    }

    public void convertFromVector(
        Vector parameters)
    {
        this.setWeightVector( parameters );
    }
    
}
