/*
 * File:                SparseVectorFactoryOptimized.java
 * Authors:             Jeremy D. Wendt
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright 2015, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 */

package gov.sandia.cognition.math.matrix.custom;

import gov.sandia.cognition.math.matrix.MatrixFactory;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.Vector1D;
import gov.sandia.cognition.math.matrix.Vector2D;
import gov.sandia.cognition.math.matrix.Vector3D;
import gov.sandia.cognition.math.matrix.VectorFactory;

/**
 * Generates Sparse Vectors with all settings initialized properly
 * 
 * @author Jeremy D. Wendt
 * @since   3.4.3
 */
public class SparseVectorFactoryOptimized
    extends VectorFactory<SparseVector>
{
    /** An instance of this class. */
    public static SparseVectorFactoryOptimized INSTANCE = new SparseVectorFactoryOptimized();

    @Override
    final public SparseVector copyVector(Vector m)
    {
        if (m instanceof DenseVector)
        {
            return new SparseVector((DenseVector) m);
        }
        else if (m instanceof SparseVector)
        {
            return new SparseVector((SparseVector) m);
        }

        // I have to handle non-package vectors
        int n = m.getDimensionality();
        SparseVector ret = new SparseVector(n);
        for (int i = 0; i < n; ++i)
        {
            ret.setElement(i, m.getElement(i));
        }
        return ret;
    }

    @Override
    final public SparseVector createVector(int dim)
    {
        return new SparseVector(dim);
    }

    /**
     * {@inheritDoc}
     *
     * NOTE: This produces a dense vector because a sparse vector with one
     * element is less memory efficient.
     * 
     * @return {@inheritDoc}
     */
    @Override
    final public Vector1D createVector1D(double x)
    {
        return new DenseVectorFactoryOptimized.DenseVector1D(x);
    }

    /**
     * {@inheritDoc}
     *
     * NOTE: This produces a dense vector because a sparse vector with one
     * element is less memory efficient than a dense vector with two elements.
     * 
     * @return {@inheritDoc}
     */
    @Override
    final public Vector2D createVector2D(double x,
        double y)
    {
        return new DenseVectorFactoryOptimized.DenseVector2D(x, y);
    }

    /**
     * {@inheritDoc}
     *
     * NOTE: This produces a dense vector because a sparse vector with two
     * elements is less memory efficient than a dense vector with three
     * elements.
     * 
     * @return {@inheritDoc}
     */
    @Override
    final public Vector3D createVector3D(double x,
        double y,
        double z)
    {
        return new DenseVectorFactoryOptimized.DenseVector3D(x, y, z);
    }

    @Override
    public MatrixFactory<?> getAssociatedMatrixFactory()
    {
        return SparseMatrixFactoryOptimized.INSTANCE;
    }

    @Override
    public SparseVector createVectorCapacity(
        final int dimensionality,
        final int initialCapacity)
    {
        return new SparseVector(dimensionality);
    }

}
