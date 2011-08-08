/*
 * File:                VectorFactory.java
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

package gov.sandia.cognition.math.matrix;

import gov.sandia.cognition.math.matrix.mtj.DenseVectorFactoryMTJ;
import gov.sandia.cognition.math.matrix.mtj.SparseVectorFactoryMTJ;
import java.io.Serializable;
import java.util.Collection;
import java.util.Random;

/**
 * Abstract factory for creating {@code Vector} objects.
 *
 * @param <VectorType> Type of Vector created by this factory.
 * @author Kevin R. Dixon
 * @since  1.0
 */
public abstract class VectorFactory<VectorType extends Vector>
    implements Serializable
{
    /** The default {@code VectorFactory} instance. */
    protected final static VectorFactory<? extends Vector> DEFAULT_DENSE_INSTANCE =
        new DenseVectorFactoryMTJ();

    /**
     * The default SparseVectorFactory instance.
     */
    protected final static SparseVectorFactory<? extends Vector> DEFAULT_SPARSE_INSTANCE =
        new SparseVectorFactoryMTJ();

    /**
     * Creates a new {@code VectorFactory}.
     */
    protected VectorFactory()
    {
        super();
    }
    
    /**
     * Gets the default implementation of the {@code VectorFactory}.
     * 
     * @return  The default {@code VectorFactory} implementation.
     */
    public static VectorFactory<? extends Vector> getDefault()
    {
        // Dense is the default.
        return DEFAULT_DENSE_INSTANCE;
    }

    /**
     * Gets the default implementation of {@code VectorFactory} for dense
     * vectors.
     *
     * @return
     *      The default dense vector factory;
     */
    public static VectorFactory<? extends Vector> getDenseDefault()
    {
        return DEFAULT_DENSE_INSTANCE;
    }

    /**
     * Gets the default implementation of the {@code SparseVectorFactory}.
     *
     * @return  The default {@code SparseVectorFactory} implementation.
     */
    public static SparseVectorFactory<? extends Vector> getSparseDefault()
    {
        return DEFAULT_SPARSE_INSTANCE;
    }
    
    /**
     * Creates a deep copy new Vector given another, argument is unchanged
     * @param m Vector to copy
     * @return Deep copy of the given Vector
     */
    public abstract VectorType copyVector(
        Vector m );
    
    /**
     * Copies the values from the array into the Vector
     * @param values Values to copy
     * @return Vector with same dimension and values as "values"
     */
    public VectorType copyArray(
        double[] values )
    {
        VectorType v = this.createVector( values.length );
        for( int i = 0; i < v.getDimensionality(); i++ )
        {
            double value = values[i];
            if( value != 0.0 )
            {
                v.setElement( i, value );
            }
        }
        return v;        
    }
    
    
    /**
     * Copies the values from the given doubles into a Vector
     * @param values Values to copy
     * @return Vector with same dimension and values as "values"
     */
    public VectorType copyValues(
        double... values )
    {
        return this.copyArray( values );
    }

    /**
     * Copies the values from the given Collection
     * @param values Values to copy
     * @return Vector with the same dimension and values as "values" has size.
     */
    public VectorType copyValues(
        Collection<? extends Number> values )
    {

        VectorType v = this.createVector( values.size() );
        int index = 0;
        for( Number value : values )
        {
            v.setElement(index, value.doubleValue());
            index++;
        }

        return v;

    }

    /**
     * Creates an empty Vector of the specified dimension, all elements
     * must be all zeros!
     * @param dim number of elements in the Vector
     * @return A new Vector or the given dimensionality with all zero elements.
     */
    public abstract VectorType createVector(
        int dim );

    /**
     * Creates a one-dimensional zero vector: (0.0).
     *
     * @return
     *      A one-dimensional zero vector.
     */
    public Vector1D createVector1D()
    {
        return this.createVector1D(0.0);
    }

    /**
     * Creates a one-dimensional vector with the given x coordinate: (x). This
     * is useful mainly for creating a vector wrapper for the double.
     *
     * @param   x
     *      The x coordinate.
     * @return
     *      A new one-dimensional vector with the given coordinate.
     */
    public abstract Vector1D createVector1D(
        final double x);

    /**
     * Creates a two-dimensional zero vector: (0.0, 0.0).
     *
     * @return
     *      A two-dimensional zero vector.
     */
    public Vector2D createVector2D()
    {
        return this.createVector2D(0.0, 0.0);
    }

    /**
     * Creates a two-dimensional vector with the given x and y coordinates:
     * (x, y).
     *
     * @param   x
     *      The x coordinate.
     * @param   y
     *      The y coordinate.
     * @return
     *      A new two-dimensional vector with the given coordinates.
     */
    public abstract Vector2D createVector2D(
        final double x,
        final double y);

    /**
     * Creates a three-dimensional zero vector: (0.0, 0.0, 0.0).
     *
     * @return
     *      A three-dimensional zero vector.
     */
    public Vector3D createVector3D()
    {
        return this.createVector3D(0.0, 0.0, 0.0);
    }

    /**
     * Creates a three-dimensional vector with the given x, y, and z
     * coordinates: (x, y, z).
     *
     * @param   x
     *      The x coordinate.
     * @param   y
     *      The y coordinate.
     * @param   z
     *      The z coordinate.
     * @return
     *      A new three-dimensional vector with the given coordinates.
     */
    public abstract Vector3D createVector3D(
        final double x,
        final double y,
        final double z);

    /**
     * Creates a Vector with random values for the entries, uniformly
     * distributed between "min" and "max"
     *
     * @param dim number of elements in the Vector
     * @param min minimum range of the uniform distribution
     * @param max maximum range of the uniform distribution
     * @param random The random number generator.
     * @return Vector with random values for the entries, uniformly
     * distributed between "min" and "max"
     */    
    public VectorType createUniformRandom(
        int dim,
        double min,
        double max,
        Random random)
    {
        VectorType v = this.createVector( dim );
        for( int i = 0; i < v.getDimensionality(); i++ )
        {
            double uniform = random.nextDouble();
            double value = ((max-min) * uniform) + min;
            v.setElement( i, value );
        }
        
        return v;
    }
    
    /**
     * Creates a Vector with the given initial value for all elements.
     *
     * @param   dimensionality
     *      The number of elements for the vector.
     * @param initialValue Initial value to set all elements to
     * @return Vector of the given dimensions, with all value equal to 
     * initialValue
     * 
     */
    public VectorType createVector(
        int dimensionality,
        double initialValue)
    {
        final VectorType vector = this.createVector(dimensionality);
        if (initialValue != 0.0)
        {
            for (int i = 0; i < dimensionality; i++)
            {
                vector.setElement(i, initialValue);
            }
        }
        return vector;
    }


    /**
     * Creates a new vector of the given dimensionality by setting the values
     * at the given indices.
     *
     * @param   dimensionality
     *      The dimensionality of the vector to create.
     * @param   indices
     *      The array of indices of values to set. Must be the same length as
     *      the values array. The indices must be between 0 and the
     *      dimensionality - 1.
     * @param   values
     *      The array of values to set. Must be the same length as the indices
     *      array.
     * @return
     *      A new vector with the given indices set to their corresponding
     *      values.
     */
    public VectorType copyArray(
        final int dimensionality,
        final int[] indices,
        final double[] values)
    {
        final int length = indices.length;
        if (length != values.length)
        {
            throw new IllegalArgumentException(
                "indices and values must have the same length");
        }

        final VectorType result = this.createVector(dimensionality);

        for (int i = 0; i < length; i++)
        {
            final double value = values[i];

            if (value != 0.0)
            {
                result.setElement(indices[i], value);
            }
        }

        return result;
    }

}
