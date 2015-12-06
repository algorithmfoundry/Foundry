
package gov.sandia.cognition.math.matrix.optimized;

import gov.sandia.cognition.math.matrix.AbstractVector;
import gov.sandia.cognition.math.matrix.DimensionalityMismatchException;
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
     * Checks the length of v and ensures it can be added to or dotted with this
     *
     * @param v The vector that may be added to or dotted with this
     * @throws DimensionalityMismatchException if the input vector's length
     * doesn't match this.
     */
    private void checkLength(Vector v)
    {
        if (v.getDimensionality() != getDimensionality())
        {
            throw new DimensionalityMismatchException("Input vector's length ("
                + v.getDimensionality() + ") does not match mine ("
                + getDimensionality() + ")");
        }
    }

    /**
     * @see Vector#plusEquals(gov.sandia.cognition.math.Vector)
     * @throws TypeConstraintException if the input Vector is not of a type
     * defined in this package.
     */
    @Override
    final public void plusEquals(Vector other)
    {
        checkLength(other);

        if (other instanceof SparseVector)
        {
            _plusEquals((SparseVector) other);
        }
        else if (other instanceof DenseVector)
        {
            _plusEquals((DenseVector) other);
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
    abstract void _plusEquals(DenseVector other);

    /**
     * Type-specific version of plusEquals for combining whatever type this is
     * with the input sparse vector.
     *
     * @param other A sparse vector to add to this
     */
    abstract void _plusEquals(SparseVector other);

    /**
     * @see Vector#scaledPlusEquals(gov.sandia.cognition.math.Vector)
     * @throws TypeConstraintException if the input Vector is not of a type
     * defined in this package.
     */
    @Override
    final public void scaledPlusEquals(double scaleFactor,
        Vector other)
    {
        checkLength(other);

        if (other instanceof SparseVector)
        {
            _scaledPlusEquals((SparseVector) other, scaleFactor);
        }
        else if (other instanceof DenseVector)
        {
            _scaledPlusEquals((DenseVector) other, scaleFactor);
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
    abstract void _scaledPlusEquals(DenseVector other,
        double scaleFactor);

    /**
     * Type-specific version of scaledPlusEquals for combining whatever type
     * this is with the input sparse vector.
     *
     * @param other A sparse vector to add to this
     * @param scaleFactor The scalar to multiply other by
     */
    abstract void _scaledPlusEquals(SparseVector other,
        double scaleFactor);

    /**
     * @see Vector#minusEquals(gov.sandia.cognition.math.Vector)
     * @throws TypeConstraintException if the input Vector is not of a type
     * defined in this package.
     */
    @Override
    final public void minusEquals(Vector other)
    {
        checkLength(other);

        if (other instanceof SparseVector)
        {
            _minusEquals((SparseVector) other);
        }
        else if (other instanceof DenseVector)
        {
            _minusEquals((DenseVector) other);
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
    abstract void _minusEquals(DenseVector other);

    /**
     * Type-specific version of minusEquals for combining whatever type this is
     * with the input sparse vector.
     *
     * @param other A sparse vector to subtract from this
     */
    abstract void _minusEquals(SparseVector other);

    /**
     * @see Vector#dotTimesEquals(gov.sandia.cognition.math.Vector)
     * @throws TypeConstraintException if the input Vector is not of a type
     * defined in this package.
     */
    @Override
    final public void dotTimesEquals(Vector other)
    {
        checkLength(other);

        if (other instanceof SparseVector)
        {
            _dotTimesEquals((SparseVector) other);
        }
        else if (other instanceof DenseVector)
        {
            _dotTimesEquals((DenseVector) other);
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
    abstract void _dotTimesEquals(DenseVector other);

    /**
     * Type-specific version of dotTimesEquals for combining whatever type this
     * is with the input sparse vector.
     *
     * @param other A sparse vector to dot with this
     */
    abstract void _dotTimesEquals(SparseVector other);

    /**
     * @see Vector#euclideanDistance(gov.sandia.cognition.math.matrix.Vector)
     * @throws TypeConstraintException if the input Vector is not of a type
     * defined in this package.
     */
    @Override
    final public double euclideanDistanceSquared(Vector other)
    {
        checkLength(other);

        if (other instanceof SparseVector)
        {
            return _euclideanDistanceSquared((SparseVector) other);
        }
        else if (other instanceof DenseVector)
        {
            return _euclideanDistanceSquared((DenseVector) other);
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
    abstract double _euclideanDistanceSquared(DenseVector other);

    /**
     * Type-specific version of euclideanDistanceSquared for combining whatever
     * type this is with the input sparse vector.
     *
     * @param other A sparse vector to calculate the distance from this
     * @return the Euclidean distance (L2 norm) between the two vectors
     */
    abstract double _euclideanDistanceSquared(SparseVector other);

    /**
     * @see Vector#outerProduct(gov.sandia.cognition.math.matrix.Vector)
     * @throws TypeConstraintException if the input Vector is not of a type
     * defined in this package.
     */
    @Override
    final public Matrix outerProduct(Vector other)
    {
        if (other instanceof SparseVector)
        {
            return _outerProduct((SparseVector) other);
        }
        else if (other instanceof DenseVector)
        {
            return _outerProduct((DenseVector) other);
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
    abstract Matrix _outerProduct(DenseVector other);

    /**
     * Type-specific version of outerProduct for combining whatever type this is
     * with the input sparse vector.
     *
     * @param other A sparse vector to "outer product" with this
     * @return the outer product (this.transpose().times(other))
     */
    abstract Matrix _outerProduct(SparseVector other);

    /**
     * @see Vector#times(gov.sandia.cognition.math.matrix.Matrix)
     * @throws TypeConstraintException if the input Vector is not of a type
     * defined in this package.
     */
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

    /**
     * @see Vector#stack(gov.sandia.cognition.math.matrix.Vector)
     * @throws TypeConstraintException if the input Vector is not of a type
     * defined in this package.
     */
    @Override
    final public Vector stack(Vector other)
    {
        if (other instanceof SparseVector)
        {
            return _stack((SparseVector) other);
        }
        else if (other instanceof DenseVector)
        {
            return _stack((DenseVector) other);
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
    abstract Vector _stack(DenseVector other);

    /**
     * Type-specific version of stack for combining whatever type this is with
     * the input sparse vector.
     *
     * @param other A sparse vector to stack below this
     * @return the vector resulting from stacking this above other
     */
    abstract Vector _stack(SparseVector other);

    /**
     * @see Vector#dotProduct(gov.sandia.cognition.math.matrix.Vector)
     * @throws TypeConstraintException if the input Vector is not of a type
     * defined in this package.
     */
    @Override
    final public double dotProduct(Vector other)
    {
        checkLength(other);

        if (other instanceof SparseVector)
        {
            return _dotProduct((SparseVector) other);
        }
        else if (other instanceof DenseVector)
        {
            return _dotProduct((DenseVector) other);
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
    abstract double _dotProduct(SparseVector other);

    /**
     * Type-specific version dotProduct for combining whatever type this is with
     * the input dense vector.
     *
     * @param other A dense vector to dot with this
     * @return the dot product of this with other
     */
    abstract double _dotProduct(DenseVector other);

}
