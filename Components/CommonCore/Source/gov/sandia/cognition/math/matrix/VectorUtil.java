/*
 * File:                VectorUtil.java
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
 * Utility methods for dealing with vectors.
 *
 * @author  Justin Basilico
 * @since   3.0
 */
public class VectorUtil
    extends Object
{

    /**
     * Gets the dimensionality of the given vector, if it is not null.
     * Otherwise, -1 is returned.
     *
     * @param   vector
     *      The vector to get the dimensionality of.
     * @return
     *      The dimensionality of the given vector, if it is not null.
     *      Otherwise, -1 is returned.
     */
    public static int safeGetDimensionality(
        final Vectorizable vector)
    {
        if (vector == null)
        {
            return -1;
        }
        else
        {
            return safeGetDimensionality(vector.convertToVector());
        }
    }

    /**
     * Gets the dimensionality of the given vector, if it is not null.
     * Otherwise, -1 is returned.
     *
     * @param   vector
     *      The vector to get the dimensionality of.
     * @return
     *      The dimensionality of the given vector, if it is not null.
     *      Otherwise, -1 is returned.
     */
    public static int safeGetDimensionality(
        final Vector vector)
    {
        if (vector == null)
        {
            return -1;
        }
        else
        {
            return vector.getDimensionality();
        }
    }

    /**
     * Returns a new vector whose elements are the elements of the original
     * vector, divided by the 1-norm of the vector (the sum of the absolute
     * values of the elements). If the 1-norm is zero (which means all the
     * elements are zero), then the result is just a duplicate of the input
     * (zero) vector.
     *
     * @param   input
     *      The vector to divide by its 1-norm.
     * @return
     *      A new vector whose elements are the elements from the given vector,
     *      divided by its 1-norm.
     */
    public static Vector divideByNorm1(
        final Vector input)
    {
        final Vector clone = input.clone();
        divideByNorm1Equals(clone);
        return clone;
    }

    /**
     * Divides all of the given elements of the vector by the 1-norm (the sum
     * of the absolute values of the elements). If the 1-norm is zero (which
     * means all the elements are zero), then the vector is not modified.
     *
     * @param   vector
     *      The vector to divide the elements by the 1-norm. It is modified by
     *      this method.
     */
    public static void divideByNorm1Equals(
        final Vector vector)
    {
        final double norm1 = vector.norm1();
        if (norm1 != 0.0)
        {
            vector.scaleEquals(1.0 / norm1);
        }
    }

    /**
     * Returns a new vector whose elements are the elements of the original
     * vector, divided by the 2-norm of the vector (the square root of the sum
     * of the squared values of the elements). If the 2-norm is zero (which 
     * means all the elements are zero), then the result is just a duplicate 
     * of the input (zero) vector.
     *
     * @param   input
     *      The vector to divide by its 2-norm.
     * @return
     *      A new vector whose elements are the elements from the given vector,
     *      divided by its 2-norm.
     */
    public static Vector divideByNorm2(
        final Vector input)
    {
        final Vector clone = input.clone();
        divideByNorm2Equals(clone);
        return clone;
    }

    /**
     * Divides all of the given elements of the vector by the 2-norm (the square 
     * root of the sum of the squared values of the elements). If the 2-norm is 
     * zero (which means all the elements are zero), then the vector is not 
     * modified.
     *
     * @param   vector
     *      The vector to divide the elements by the 2-norm. It is modified by
     *      this method.
     */
    public static void divideByNorm2Equals(
        final Vector vector)
    {
        final double norm2 = vector.norm2();
        if (norm2 != 0.0)
        {
            vector.scaleEquals(1.0 / norm2);
        }
    }
    
    /**
     * Performs linear interpolation between two vectors.
     *
     * If percent is between 0.0 and 1.0, the result is: percent (y - x) + x.
     * If percent is less than 0.0, the result is x. If percent is
     * greater than 1.0, the result is y.
     *
     * @param   first
     *      The first vector. Sometimes this is referred to as x.
     * @param second
     *      The second vector. Sometimes this is referred to as y.
     * @param   percent
     *      A value between 0.0 and 1.0 to interpolate between the two vectors.
     *      Sometimes this is also referred to as alpha.
     * @return
     *      A new vector that is linearly interpolated between the two vectors
     *      vectors.
     */
    public static Vector interpolateLinear(
        final Vectorizable first,
        final Vectorizable second,
        final double percent)
    {
        return interpolateLinear(
            first.convertToVector(), second.convertToVector(), percent);
    }

    /**
     * Performs linear interpolation between two vectors.
     * 
     * If percent is between 0.0 and 1.0, the result is: percent * (y - x) + x.
     * If percent is less than 0.0, the result is x. If percent is greater than
     * 1.0, the result is y.
     *
     * @param   first
     *      The first vector. Sometimes this is referred to as x.
     * @param second
     *      The second vector. Sometimes this is referred to as y.
     * @param   percent
     *      A value between 0.0 and 1.0 to interpolate between the two vectors.
     *      Sometimes this is also referred to as alpha.
     * @return
     *      A new vector that is linearly interpolated between the two vectors
     *      vectors.
     */
    public static Vector interpolateLinear(
        final Vector first,
        final Vector second,
        final double percent)
    {
        if (percent <= 0.0)
        {
            // The result is just the first value.
            return first.clone();
        }
        else if (percent >= 1.0)
        {
            // The result is just the second value.
            return second.clone();
        }

        // Do the interpolation: alpha * (y - x) + x
        final Vector result = second.minus(first);
        result.scaleEquals(percent);
        result.plusEquals(first);
        return result;
    }

    /**
     * Asserts that all of the dimensionalities of the vectors in the
     * given set of data equal the given dimensionality.
     *
     * @param   data
     *      A collection of input-output pairs.
     * @param   dimensionality
     *      The dimensionality that all the inputs must have.
     * @throws  DimensionalityMismatchException
     *      If the dimensionalities are not all equal.
     */
    public static void assertDimensionalitiesAllEqual(
        final Iterable<? extends Vectorizable> data,
        final int dimensionality)
    {
        if (data != null)
        {
            for (Vectorizable example : data)
            {
                if (example == null)
                {
                    // Ignore null examples.
                    continue;
                }

                final Vector vector = example.convertToVector();
                if (vector == null)
                {
                    // Ignore null vectors.
                    continue;
                }

                // Make sure this dimensionality is correct.
                vector.assertDimensionalityEquals(dimensionality);
            }
        }
    }
    
}
