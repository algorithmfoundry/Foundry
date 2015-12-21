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

    
}
