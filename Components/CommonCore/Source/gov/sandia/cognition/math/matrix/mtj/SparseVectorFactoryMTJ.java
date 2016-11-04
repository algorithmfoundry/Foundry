/*
 * File:                SparseVectorFactoryMTJ.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright May 22, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.math.matrix.mtj;

import gov.sandia.cognition.math.matrix.MatrixFactory;
import gov.sandia.cognition.math.matrix.SparseVectorFactory;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.Vector1D;

/**
 * Factory for MTJ's SparseVector
 *
 * @author Kevin R. Dixon
 * @since  1.0
 *
 */
public class SparseVectorFactoryMTJ
    extends SparseVectorFactory<SparseVector>
{

    /**
     * Default instance of this
     */
    public final static SparseVectorFactoryMTJ INSTANCE = new SparseVectorFactoryMTJ();

    /** Creates a new instance of SparseVectorFactoryMTJ */
    public SparseVectorFactoryMTJ()
    {
        super();
    }

    public SparseVector copyVector(
        Vector m)
    {
        return new SparseVector(m);
    }

    public SparseVector createVector(
        int dim)
    {
        return new SparseVector(dim);
    }

    @Override
    public SparseVector createVectorCapacity(
        int dimensionality,
        int initialCapacity)
    {
        return new SparseVector(dimensionality, initialCapacity);
    }
    
    @Override
    public Vector1D createVector1D(
        final double x)
    {
        return new Vector1(x);
    }

    @Override
    public Vector2 createVector2D(
        final double x,
        final double y)
    {
        return new Vector2(x, y);
    }

    @Override
    public Vector3 createVector3D(
        final double x,
        final double y,
        final double z)
    {
        return new Vector3(x, y, z);
    }

    /**
     * Creates a new wrapper for a sparse MTJ vector.
     *
     * @param   internalVector
     *      The MTJ vector to wrap.
     * @return
     *      A wrapper of the given MTJ vector.
     */
    public SparseVector createWrapper(
        final no.uib.cipr.matrix.sparse.SparseVector internalVector)
    {
        return new SparseVector(internalVector);
    }

    @Override
    public MatrixFactory<?> getAssociatedMatrixFactory()
    {
        return SparseMatrixFactoryMTJ.INSTANCE;
    }
    
}

