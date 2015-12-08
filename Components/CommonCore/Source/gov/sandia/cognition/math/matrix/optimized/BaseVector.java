
package gov.sandia.cognition.math.matrix.optimized;

import gov.sandia.cognition.math.matrix.AbstractVector;
import gov.sandia.cognition.math.matrix.Matrix;
import gov.sandia.cognition.math.matrix.Vector;

/**
 * This package-private class implements the basic math methods, ensures size
 * constraints are met for the operation, and then calls the
 * vector-type-specific (dense, sparse) version of the basic math method.
 *
 * @author Jeremy D. Wendt
 */
abstract class BaseVector
    extends AbstractVector
{

    /**
     * Creates a new {@link BaseVector}.
     */
    protected BaseVector()
    {
        super();
    }
    
    @Override
    final public void plusEquals(Vector other)
    {
        this.assertSameDimensionality(other);

        if (other instanceof SparseVector)
        {
            plusEquals((SparseVector) other);
        }
        else if (other instanceof DenseVector)
        {
            this.plusEquals((DenseVector) other);
        }
        else
        {
            super.plusEquals(other);
        }
    }

    /**
     * Type-specific version of plusEquals for combining whatever type this is
     * with the input dense vector.
     *
     * @param other A dense vector to add to this
     */
    public abstract void plusEquals(DenseVector other);

    /**
     * Type-specific version of plusEquals for combining whatever type this is
     * with the input sparse vector.
     *
     * @param other A sparse vector to add to this
     */
    public abstract void plusEquals(SparseVector other);

    @Override
    final public void scaledPlusEquals(double scaleFactor,
        Vector other)
    {
        this.assertSameDimensionality(other);

        if (other instanceof SparseVector)
        {
            scaledPlusEquals((SparseVector) other, scaleFactor);
        }
        else if (other instanceof DenseVector)
        {
            this.scaledPlusEquals((DenseVector) other, scaleFactor);
        }
        else
        {
            super.scaledPlusEquals(scaleFactor, other);
        }
    }

    /**
     * Type-specific version of scaledPlusEquals for combining whatever type
     * this is with the input dense vector.
     *
     * @param other A dense vector to add to this
     * @param scaleFactor The scalar to multiply other by
     */
    public abstract void scaledPlusEquals(DenseVector other,
        double scaleFactor);

    /**
     * Type-specific version of scaledPlusEquals for combining whatever type
     * this is with the input sparse vector.
     *
     * @param other A sparse vector to add to this
     * @param scaleFactor The scalar to multiply other by
     */
    public abstract void scaledPlusEquals(SparseVector other,
        double scaleFactor);

    @Override
    final public void minusEquals(Vector other)
    {
        this.assertSameDimensionality(other);

        if (other instanceof SparseVector)
        {
            minusEquals((SparseVector) other);
        }
        else if (other instanceof DenseVector)
        {
            this.minusEquals((DenseVector) other);
        }
        else
        {
            super.minusEquals(other);
        }
    }

    /**
     * Type-specific version of minusEquals for combining whatever type this is
     * with the input dense vector.
     *
     * @param other A dense vector to subtract from this
     */
    public abstract void minusEquals(DenseVector other);

    /**
     * Type-specific version of minusEquals for combining whatever type this is
     * with the input sparse vector.
     *
     * @param other A sparse vector to subtract from this
     */
    public abstract void minusEquals(SparseVector other);

    @Override
    final public void dotTimesEquals(Vector other)
    {
        this.assertSameDimensionality(other);

        if (other instanceof SparseVector)
        {
            dotTimesEquals((SparseVector) other);
        }
        else if (other instanceof DenseVector)
        {
            this.dotTimesEquals((DenseVector) other);
        }
        else
        {
            super.dotTimesEquals(other);
        }
    }

    /**
     * Type-specific version of dotTimesEquals for combining whatever type this
     * is with the input dense vector.
     *
     * @param other A dense vector to dot with this
     */
    public abstract void dotTimesEquals(DenseVector other);

    /**
     * Type-specific version of dotTimesEquals for combining whatever type this
     * is with the input sparse vector.
     *
     * @param other A sparse vector to dot with this
     */
    public abstract void dotTimesEquals(SparseVector other);

    @Override
    final public double euclideanDistanceSquared(Vector other)
    {
        this.assertSameDimensionality(other);

        if (other instanceof SparseVector)
        {
            return euclideanDistanceSquared((SparseVector) other);
        }
        else if (other instanceof DenseVector)
        {
            return this.euclideanDistanceSquared((DenseVector) other);
        }
        else
        {
            return super.euclideanDistanceSquared(other);
        }
    }

    /**
     * Type-specific version of euclideanDistanceSquared for combining whatever
     * type this is with the input dense vector.
     *
     * @param other A dense vector to calculate the distance from this
     * @return the Euclidean distance (L2 norm) between the two vectors
     */
    public abstract double euclideanDistanceSquared(DenseVector other);

    /**
     * Type-specific version of euclideanDistanceSquared for combining whatever
     * type this is with the input sparse vector.
     *
     * @param other A sparse vector to calculate the distance from this
     * @return the Euclidean distance (L2 norm) between the two vectors
     */
    public abstract double euclideanDistanceSquared(SparseVector other);

    @Override
    final public Matrix outerProduct(Vector other)
    {
        if (other instanceof SparseVector)
        {
            return outerProduct((SparseVector) other);
        }
        else if (other instanceof DenseVector)
        {
            return this.outerProduct((DenseVector) other);
        }
        else
        {
            return super.outerProduct(other);
        }
    }

    /**
     * Type-specific version of outerProduct for combining whatever type this is
     * with the input dense vector.
     *
     * @param other A dense vector to "outer product" with this
     * @return the outer product (this.transpose().times(other))
     */
    public abstract Matrix outerProduct(DenseVector other);

    /**
     * Type-specific version of outerProduct for combining whatever type this is
     * with the input sparse vector.
     *
     * @param other A sparse vector to "outer product" with this
     * @return the outer product (this.transpose().times(other))
     */
    public abstract Matrix outerProduct(SparseVector other);

    @Override
    final public Vector times(Matrix matrix)
    {
        if (matrix instanceof BaseMatrix)
        {
            return ((BaseMatrix) matrix).preTimes(this);
        }
        else
        {
            return super.times(matrix);
        }
    }

    @Override
    final public Vector stack(Vector other)
    {
        if (other instanceof SparseVector)
        {
            return stack((SparseVector) other);
        }
        else if (other instanceof DenseVector)
        {
            return this.stack((DenseVector) other);
        }
        else
        {
            return super.stack(other);
        }
    }

    /**
     * Type-specific version of stack for combining whatever type this is with
     * the input dense vector.
     *
     * @param other A dense vector to stack below this
     * @return the vector resulting from stacking this above other
     */
    public abstract Vector stack(DenseVector other);

    /**
     * Type-specific version of stack for combining whatever type this is with
     * the input sparse vector.
     *
     * @param other A sparse vector to stack below this
     * @return the vector resulting from stacking this above other
     */
    public abstract Vector stack(SparseVector other);

    @Override
    final public double dotProduct(Vector other)
    {
        this.assertSameDimensionality(other);

        if (other instanceof SparseVector)
        {
            return this.dotProduct((SparseVector) other);
        }
        else if (other instanceof DenseVector)
        {
            return dotProduct((DenseVector) other);
        }
        else
        {
            return super.dotProduct(other);
        }
    }

    /**
     * Type-specific version dotProduct for combining whatever type this is with
     * the input sparse vector.
     *
     * @param other A sparse vector to dot with this
     * @return the dot product of this with other
     */
    public abstract double dotProduct(SparseVector other);

    /**
     * Type-specific version dotProduct for combining whatever type this is with
     * the input dense vector.
     *
     * @param other A dense vector to dot with this
     * @return the dot product of this with other
     */
    public abstract double dotProduct(DenseVector other);

}
