/*
 * File:                DenseVectorFactoryOptimized.java
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
 * Dense vector factory. Note that the constructors for DenseVector are also
 * public so this need not be called.
 *
 * @author Jeremy D. Wendt
 * @since   3.4.4
 */
public class CustomDenseVectorFactory
    extends VectorFactory<DenseVector>
{

    /** An instance of this class. */
    public static CustomDenseVectorFactory INSTANCE = new CustomDenseVectorFactory();
    
    @Override
    final public DenseVector copyVector(
        final Vector m)
    {
        int n = m.getDimensionality();
        DenseVector result = new DenseVector(n);
        for (int i = 0; i < n; ++i)
        {
            result.values[i] = m.get(i);
        }

        return result;
    }

    @Override
    final public DenseVector createVector(
        final int dim)
    {
        return new DenseVector(dim);
    }

    @Override
    final public Vector1D createVector1D(
        final double x)
    {
        return new DenseVector1D(x);
    }

    @Override
    final public Vector2D createVector2D(
        final double x,
        final double y)
    {
        return new DenseVector2D(x, y);
    }

    @Override
    final public Vector3D createVector3D(
        final double x,
        final double y,
        final double z)
    {
        return new DenseVector3D(x, y, z);
    }

    @Override
    public MatrixFactory<?> getAssociatedMatrixFactory()
    {
        return CustomDenseMatrixFactory.INSTANCE;
    }

    @Override
    public DenseVector createVectorCapacity(
        final int dimensionality,
        final int initialCapacity)
    {
        return this.createVector(dimensionality);
    }
    
    /**
     * Package-private implementation for all of the createVector1D methods
     * required by the VectorFactory interface.
     */
    final static class DenseVector1D
        extends DenseVector
        implements Vector1D
    {

        /**
         * Package-private constructor for creating an instance
         *
         * @param x The value to store
         */
        DenseVector1D(
            final double x)
        {
            super(1);
            this.values[0] = x;
        }

        @Override
        final public double getX()
        {
            return this.values[0];
        }

        @Override
        final public void setX(
            final double x)
        {
            this.values[0] = x;
        }

        @Override
        public double get(
            final int index)
        {
            return this.values[index];
        }

        @Override
        public void set(
            final int index,
            final double value)
        {
            this.values[index] = value;
        }

        @Override
        public boolean isSparse()
        {
            return false;
        }

    }

    /**
     * Package-private implementation for all of the createVector2D methods
     * required by the VectorFactory interface.
     */
    final static class DenseVector2D
        extends DenseVector
        implements Vector2D
    {

        /**
         * Package-private constructor for creating an instance
         *
         * @param x The first value to store
         * @param y The second value to store
         */
        DenseVector2D(
            final double x,
            final double y)
        {
            super(2);
            values[0] = x;
            values[1] = y;
        }

        @Override
        final public double getX()
        {
            return values[0];
        }

        @Override
        final public void setX(
            final double x)
        {
            values[0] = x;
        }

        @Override
        final public double getY()
        {
            return values[1];
        }

        @Override
        final public void setY(
            final double y)
        {
            values[1] = y;
        }

        @Override
        final public void setXY(
            final double x,
            final double y)
        {
            values[0] = x;
            values[1] = y;
        }

        @Override
        final public Double getFirst()
        {
            return values[0];
        }

        @Override
        final public Double getSecond()
        {
            return values[1];
        }

        @Override
        public double get(
            final int index)
        {
            return values[index];
        }

        @Override
        public void set(
            final int index,
            final double value)
        {
            values[index] = value;
        }

        @Override
        public boolean isSparse()
        {
            return false;
        }

    }

    /**
     * Package-private implementation for all of the createVector2D methods
     * required by the VectorFactory interface.
     */
    final static class DenseVector3D
        extends DenseVector
        implements Vector3D
    {

        /**
         * Package-private constructor for creating an instance
         *
         * @param x The first value to store
         * @param y The second value to store
         * @param z The third value to store
         */
        DenseVector3D(
            final double x,
            final double y,
            final double z)
        {
            super(3);
            values[0] = x;
            values[1] = y;
            values[2] = z;
        }

        @Override
        final public double getX()
        {
            return values[0];
        }

        @Override
        final public void setX(
            final double x)
        {
            values[0] = x;
        }

        @Override
        final public double getY()
        {
            return values[1];
        }

        @Override
        final public void setY(
            final double y)
        {
            values[1] = y;
        }

        @Override
        final public double getZ()
        {
            return values[2];
        }

        @Override
        final public void setZ(
            final double z)
        {
            values[2] = z;
        }

        @Override
        public Double getFirst()
        {
            return values[0];
        }

        @Override
        final public Double getSecond()
        {
            return values[1];
        }

        @Override
        final public Double getThird()
        {
            return values[2];
        }

        @Override
        final public void setXYZ(
            final double x,
            final double y,
            final double z)
        {
            values[0] = x;
            values[1] = y;
            values[2] = z;
        }

        @Override
        public double get(
            final int index)
        {
            return values[index];
        }

        @Override
        public void set(
            final int index,
            final double value)
        {
            values[index] = value;
        }

        @Override
        public boolean isSparse()
        {
            return false;
        }

    }

}
