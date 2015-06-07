/*
 * File:                SparseVectorFactory.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright March 12, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government. Export
 * of this program may require a license from the United States Government.
 * See CopyrightHistory.txt for complete details.
 *
 */

package gov.sandia.cognition.math.matrix;

import gov.sandia.cognition.util.ArgumentChecker;
import java.util.Map;


/**
 * Abstract factory class for creating sparse vectors.
 *
 * @param <VectorType> Type of Vector created by the VectorFactory.
 * @author  Justin Basilico
 * @since   3.0
 */
public abstract class SparseVectorFactory<VectorType extends Vector>
    extends VectorFactory<VectorType>
{

    /**
     * Gets the default instance.
     * @return
     * Default instance of the SparseVectorFactory
     */
    public static SparseVectorFactory<? extends Vector> getDefault()
    {
        return DEFAULT_SPARSE_INSTANCE;
    }

    /**
     * Creates a new, empty vector with the given dimensionality and expected
     * number of nonzero elements.
     *
     * @param   dimensionality
     *      The dimensionality for the vector to create.
     * @param   initialCapacity
     *      The expected initial number of nonzero elements of the vector to
     *      create.
     * @return
     *      A new sparse vector with the given dimensionality and number of
     *      nonzeros.
     */
    public abstract VectorType createVectorCapacity(
        final int dimensionality,
        final int initialCapacity);
    
    @Override
    public VectorType copyMap(
        final int dimensionality,
        final Map<Integer, ? extends Number> map)
    {
        ArgumentChecker.assertIsNotNull("map", map);
        
        final VectorType result = this.createVectorCapacity(dimensionality,
            map.size());
        for (final Map.Entry<Integer, ? extends Number> entry : map.entrySet())
        {
            result.set(entry.getKey(), entry.getValue().doubleValue());
        }
        return result;
    }
    
}
