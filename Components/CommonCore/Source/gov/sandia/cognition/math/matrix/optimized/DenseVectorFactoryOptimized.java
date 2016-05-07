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

package gov.sandia.cognition.math.matrix.optimized;

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
 * @since   3.4.3
 */
public class DenseVectorFactoryOptimized
    extends VectorFactory<DenseVector>
{

    /** An instance of this class. */
    public static DenseVectorFactoryOptimized INSTANCE = new DenseVectorFactoryOptimized();
    
    @Override
    final public DenseVector copyVector(Vector m)
    {
        int n = m.getDimensionality();
        DenseVector ret = new DenseVector(n, true);
        for (int i = 0; i < n; ++i)
        {
            ret.elements()[i] = m.getElement(i);
        }

        return ret;
    }

    @Override
    final public DenseVector createVector(int dim)
    {
        return new DenseVector(dim);
    }

    @Override
    final public Vector1D createVector1D(double x)
    {
        return new DenseVector1D(x);
    }

    @Override
    final public Vector2D createVector2D(double x,
        double y)
    {
        return new DenseVector2D(x, y);
    }

    @Override
    final public Vector3D createVector3D(double x,
        double y,
        double z)
    {
        return new DenseVector3D(x, y, z);
    }

    @Override
    public MatrixFactory<?> getAssociatedMatrixFactory()
    {
        return DenseMatrixFactoryOptimized.INSTANCE;
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
        DenseVector1D(double x)
        {
            super(1, true);
            elements()[0] = x;
        }

        @Override
        final public double getX()
        {
            return elements()[0];
        }

        @Override
        final public void setX(double x)
        {
            elements()[0] = x;
        }

        @Override
        public double get(int index)
        {
            return elements()[index];
        }

        @Override
        public void set(int index,
            double value)
        {
            elements()[index] = value;
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
        DenseVector2D(double x,
            double y)
        {
            super(2, true);
            elements()[0] = x;
            elements()[1] = y;
        }

        @Override
        final public double getX()
        {
            return elements()[0];
        }

        @Override
        final public void setX(double x)
        {
            elements()[0] = x;
        }

        @Override
        final public double getY()
        {
            return elements()[1];
        }

        @Override
        final public void setY(double y)
        {
            elements()[1] = y;
        }

        @Override
        final public void setXY(double x,
            double y)
        {
            elements()[0] = x;
            elements()[1] = y;
        }

        @Override
        final public Double getFirst()
        {
            return elements()[0];
        }

        @Override
        final public Double getSecond()
        {
            return elements()[1];
        }

        @Override
        public double get(int index)
        {
            return elements()[index];
        }

        @Override
        public void set(int index,
            double value)
        {
            elements()[index] = value;
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
        DenseVector3D(double x,
            double y,
            double z)
        {
            super(3, true);
            elements()[0] = x;
            elements()[1] = y;
            elements()[2] = z;
        }

        @Override
        final public double getX()
        {
            return elements()[0];
        }

        @Override
        final public void setX(double x)
        {
            elements()[0] = x;
        }

        @Override
        final public double getY()
        {
            return elements()[1];
        }

        @Override
        final public void setY(double y)
        {
            elements()[1] = y;
        }

        @Override
        final public double getZ()
        {
            return elements()[2];
        }

        @Override
        final public void setZ(double z)
        {
            elements()[2] = z;
        }

        @Override
        public Double getFirst()
        {
            return elements()[0];
        }

        @Override
        final public Double getSecond()
        {
            return elements()[1];
        }

        @Override
        final public Double getThird()
        {
            return elements()[2];
        }

        @Override
        final public void setXYZ(double x,
            double y,
            double z)
        {
            elements()[0] = x;
            elements()[1] = y;
            elements()[2] = z;
        }

        @Override
        public double get(int index)
        {
            return elements()[index];
        }

        @Override
        public void set(int index,
            double value)
        {
            elements()[index] = value;
        }

        @Override
        public boolean isSparse()
        {
            return false;
        }

    }

}
