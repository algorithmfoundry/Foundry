/*
 * File:                DenseVectorFactoryMTJ.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright May 18, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.math.matrix.mtj;

import gov.sandia.cognition.math.matrix.MatrixFactory;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.Vector1D;
import gov.sandia.cognition.math.matrix.Vector2D;
import gov.sandia.cognition.math.matrix.Vector3D;
import gov.sandia.cognition.math.matrix.VectorFactory;
import java.util.Arrays;

/**
 * VectorFactory for MTJ's DenseVector-based Vector
 *
 * @author Kevin R. Dixon
 * @since  1.0
 *
 */
public class DenseVectorFactoryMTJ
    extends VectorFactory<DenseVector>
{
    
    /**
     * Default instance of this
     */
    public static final DenseVectorFactoryMTJ INSTANCE = 
        new DenseVectorFactoryMTJ();
    
    /** Creates a new instance of DenseVectorFactoryMTJ */
    public DenseVectorFactoryMTJ()
    {
        super();
    }

    public DenseVector copyVector(
        Vector m)
    {
        return new DenseVector( m );
    }

    public DenseVector createVector(
        int dim)
    {
        return new DenseVector( dim );
    }

    /**
     * Creates a DenseVector based on the given array values.
     * Note that this method reuses the array and does not make a copy of the
     * elements.
     * @param values 
     * Array to create the Vector based upon. Reused in the Vector
     * @return 
     * DenseVector based on the double array
     */
    @Override
    public DenseVector copyArray(
        double[] values)
    {
        return new DenseVector( values ); 
    }

    @Override
    public DenseVector createVector(
        int dimensionality,
        double initialValue )
    {
        double[] values = new double[ dimensionality ];
        Arrays.fill(values, initialValue);
        return this.copyArray(values);
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

    @Override
    public DenseVector createVectorCapacity(
        final int dimensionality,
        final int initialCapacity)
    {
        // Capacity is ignored for dense vectors.
        return this.createVector(dimensionality);
    }

    /**
     * Creates a new wrapper for a dense MTJ vector.
     *
     * @param   internalVector
     *      The MTJ vector to wrap.
     * @return
     *      A wrapper of the given MTJ vector.
     */
    public DenseVector createWrapper(
        final no.uib.cipr.matrix.DenseVector internalVector)
    {
        return new DenseVector(internalVector);
    }

    @Override
    public MatrixFactory<?> getAssociatedMatrixFactory()
    {
        return DenseMatrixFactoryMTJ.INSTANCE;
    }

}
