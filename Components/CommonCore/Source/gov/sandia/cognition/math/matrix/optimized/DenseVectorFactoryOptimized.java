
package gov.sandia.cognition.math.matrix.optimized;

import gov.sandia.cognition.math.matrix.*;

/**
 * Dense vector factory. Note that the constructors for DenseVector are also
 * public so this need not be called.
 *
 * @author Jeremy D. Wendt
 */
public class DenseVectorFactoryOptimized
    extends VectorFactory<DenseVector>
{

    /**
     * @see VectorFactory#copyVector(gov.sandia.cognition.math.matrix.Vector)
     */
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

    /**
     * @see VectorFactory#createVector(int)
     */
    @Override
    final public DenseVector createVector(int dim)
    {
        return new DenseVector(dim);
    }

    /**
     * @see VectorFactory#createVector1D(double)
     */
    @Override
    final public Vector1D createVector1D(double x)
    {
        return new DenseVector1D(x);
    }

    /**
     * @see VectorFactory#createVector2D(double, double)
     */
    @Override
    final public Vector2D createVector2D(double x,
        double y)
    {
        return new DenseVector2D(x, y);
    }

    /**
     * @see VectorFactory#createVector3D(double, double, double)
     */
    @Override
    final public Vector3D createVector3D(double x,
        double y,
        double z)
    {
        return new DenseVector3D(x, y, z);
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

        /**
         * @see Vector1D#getX()
         */
        @Override
        final public double getX()
        {
            return elements()[0];
        }

        /**
         * @see Vector1D#setX(double)
         */
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

        /**
         * @see Vector2D#getX()
         */
        @Override
        final public double getX()
        {
            return elements()[0];
        }

        /**
         * @see Vector2D#setX(double)
         */
        @Override
        final public void setX(double x)
        {
            elements()[0] = x;
        }

        /**
         * @see Vector2D#getY()
         */
        @Override
        final public double getY()
        {
            return elements()[1];
        }

        /**
         * @see Vector2D#setY(double)
         */
        @Override
        final public void setY(double y)
        {
            elements()[1] = y;
        }

        /**
         * @see Vector2D#setXY(double, double)
         */
        @Override
        final public void setXY(double x,
            double y)
        {
            elements()[0] = x;
            elements()[1] = y;
        }

        /**
         * @see Vector2D#getFirst()
         */
        @Override
        final public Double getFirst()
        {
            return elements()[0];
        }

        /**
         * @see Vector2D#getSecond()
         */
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

        /**
         * @see Vector3D#getX()
         */
        @Override
        final public double getX()
        {
            return elements()[0];
        }

        /**
         * @see Vector3D#setX(double)
         */
        @Override
        final public void setX(double x)
        {
            elements()[0] = x;
        }

        /**
         * @see Vector3D#getY()
         */
        @Override
        final public double getY()
        {
            return elements()[1];
        }

        /**
         * @see Vector3D#setY(double)
         */
        @Override
        final public void setY(double y)
        {
            elements()[1] = y;
        }

        /**
         * @see Vector3D#getY()
         */
        @Override
        final public double getZ()
        {
            return elements()[2];
        }

        /**
         * @see Vector3D#setZ(double)
         */
        @Override
        final public void setZ(double z)
        {
            elements()[2] = z;
        }

        /**
         * @see Vector3D#getFirst()
         */
        @Override
        public Double getFirst()
        {
            return elements()[0];
        }

        /**
         * @see Vector3D#getSecond()
         */
        @Override
        final public Double getSecond()
        {
            return elements()[1];
        }

        /**
         * @see Vector3D#getThird()
         */
        @Override
        final public Double getThird()
        {
            return elements()[2];
        }

        /**
         * @see Vector3D#setXYZ(double, double, double)
         */
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
