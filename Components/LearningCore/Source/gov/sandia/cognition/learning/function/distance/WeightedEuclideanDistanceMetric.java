/*
 * File:                WeightedEuclideanDistanceMetric.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright July 20, 2010, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 * See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.learning.function.distance;

import gov.sandia.cognition.math.Semimetric;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorUtil;
import gov.sandia.cognition.math.matrix.Vectorizable;
import gov.sandia.cognition.util.AbstractCloneableSerializable;
import gov.sandia.cognition.util.ObjectUtil;

/**
 * A distance metric that weights each dimension of a vector differently before
 * computing Euclidean distance.
 * 
 * @author  Justin Basilico
 * @since   3.1
 */
public class WeightedEuclideanDistanceMetric
    extends AbstractCloneableSerializable
    implements Semimetric<Vectorizable>
{

// TODO: Add a mechanism for enforcing that the weights are not negative.
    /** The weights assigned to each dimension for the distance. The weights
     *  cannot be negative. */
    protected Vector weights;

    /**
     * Creates a new {@code WeightedEuclideanDistanceMetric} with no initial
     * weights.
     */
    public WeightedEuclideanDistanceMetric()
    {
        this(null);
    }

    /**
     * Creates a new {@code WeightedEuclideanDistanceMetric} with the given
     * weights.
     *
     * @param   weights
     *      The vector of weights for each dimension. The weights cannot be
     *      negative or else this will create an invalid metric.
     */
    public WeightedEuclideanDistanceMetric(
        final Vector weights)
    {
        super();

        this.setWeights(weights);
    }

    @Override
    public WeightedEuclideanDistanceMetric clone()
    {
        final WeightedEuclideanDistanceMetric clone = 
            (WeightedEuclideanDistanceMetric) super.clone();

        clone.weights = ObjectUtil.cloneSafe(this.weights);

        return clone;
    }

    /**
     * Evaluates the weighted Euclidean distance between two vectors.
     *
     * @param   first
     *      The first vector.
     * @param   second
     *      The second vector.
     * @return
     *      The weighted Euclidean distance between  the two vectors.
     */
    @Override
    public double evaluate(
        final Vectorizable first,
        final Vectorizable second)
    {
        // \sqrt(\sum_i w_i * (x_i - y_i)^2)

        // First compute the difference between the two vectors.
        final Vector difference =
            first.convertToVector().minus(second.convertToVector());

        // Now square it.
        difference.dotTimesEquals(difference);

        // Now compute the square root of the weights times the squared
        // difference.
        return Math.sqrt(this.weights.dotProduct(difference));

    }

    /**
     * Gets the expected dimensionality of the input vectors for this distance
     * metric.
     *
     * @return
     *      The expected input dimensionality, if it is known. If the weights
     *      have not been set, it will be -1.
     */
    public int getInputDimensionality()
    {
        return VectorUtil.safeGetDimensionality(this.getWeights());
    }

    /**
     * Gets the vector of weights for each dimension.
     *
     * @return
     *      The vector of weights for each dimension.
     */
    public Vector getWeights()
    {
        return weights;
    }

    /**
     * Sets the vector of weights for each dimension.
     *
     * @param   weights
     *      The vector of weights for each dimension. The weights cannot be
     *      negative or else this will create an invalid metric.
     */
    public void setWeights(
        final Vector weights)
    {
        this.weights = weights;
    }

}
