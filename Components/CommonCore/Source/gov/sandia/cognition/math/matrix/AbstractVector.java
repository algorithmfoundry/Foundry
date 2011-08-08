/*
 * File:                AbstractVector.java
 * Authors:             Kevin R. Dixon and Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright February 21, 2006, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.math.matrix;

import gov.sandia.cognition.annotation.CodeReview;
import gov.sandia.cognition.annotation.CodeReviews;
import gov.sandia.cognition.math.AbstractRing;
import java.text.NumberFormat;

/**
 * Abstract implementation of some of the Vector interface, in a storage-free
 * manner
 *
 * @author Kevin R. Dixon
 * @author Justin Basilico
 * @since  1.0
 *
 */
@CodeReviews(
    reviews={
        @CodeReview(
            reviewer="Kevin R. Dixon",
            date="2008-02-27",
            changesNeeded=false,
            comments={
                "Minor changes to formatting and documentation for equals().",
                "Otherwise, looks good."
            }
        ),        
        @CodeReview(
            reviewer="Jonathan McClain",
            date="2006-05-16",
            changesNeeded=false,
            comments="Added some documentation and other minor changes. Looks ok."
        )
    }
)
public abstract class AbstractVector
    extends AbstractRing<Vector>
    implements Vector
{
    /** The default delimiter for a vector. */
    public static final String DEFAULT_DELIMITER = " ";

    public double sum()
    {
        double sum = 0.0;
        for (VectorEntry entry : this)
        {
            sum += entry.getValue();
        }
        return sum;
    }
    
    public double norm1()
    {
        double sum = 0.0;
        for (VectorEntry e : this)
        {
            sum += Math.abs( e.getValue() );
        }

        return sum;
    }

    public double norm2()
    {
        return Math.sqrt( this.norm2Squared() );
    }

    public double normInfinity()
    {
        double max = 0.0;
        for( VectorEntry e : this )
        {
            double v = Math.abs(e.getValue());
            if( max < v )
            {
                max = v;
            }
        }
        return max;
    }

    public double euclideanDistanceSquared(
        final Vector other )
    {
        return this.minus( other ).norm2Squared();
    }

    public double euclideanDistance(
        final Vector other )
    {
        return Math.sqrt( this.euclideanDistanceSquared( other ) );
    }

    public double angle(
        final Vector other)
    {
        return Math.acos(this.cosine(other));
    }

    public double cosine(
        final Vector other )
    {

        /*
         * Computing cosine as:
         *      cosine = (x' * y) / (||x|| * ||y||)
         */
        double dotproduct = this.dotProduct( other );

        if (dotproduct == 0.0)
        {
            return 0.0;
        }
        else
        {
            double norm1 = this.norm2Squared();
            double norm2 = other.norm2Squared();
            return dotproduct / Math.sqrt( norm1 * norm2 );
        }

    }

    @Override
    public boolean equals(
        Object other )
    {
        if (other == null)
        {
            return false;
        }
        else if (this == other)
        {
            return true;
        }
        else if (other instanceof Vector)
        {
            return this.equals( (Vector) other, 0.0 );
        }
        else
        {
            return false;
        }
    }

    public boolean equals(
        final Vector other,
        double effectiveZero)
    {
        VectorUnionIterator iterator = new VectorUnionIterator(this, other);

        while ( iterator.hasNext() )
        {
            TwoVectorEntry entry = iterator.next();

            double difference = entry.getFirstValue() - entry.getSecondValue();

            if ( Math.abs( difference ) > effectiveZero )
            {
                return false;
            }
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        final int dimensionality = this.getDimensionality();
        int result = 7 + dimensionality;
        
        for (int i = 0; i < dimensionality; i++)
        {
            final double value = this.getElement(i);
            
            // We add a check for zero so that sparse vectors can create the
            // same hash-code without needing to handle the zeros.
            if (value != 0.0)
            {
                final long bits = Double.doubleToLongBits(value);
                
                // This is based on how NetBeans auto-generates hash codes
                // plus the documentation for Map.Entry.
                result = 47 * result + i ^ (int) (bits ^ (bits >>> 32));
            }
        }
        
        return result;
    }
    
    public boolean checkSameDimensionality(
        Vector other )
    {
        return (this.getDimensionality() == other.getDimensionality());
    }

    public void assertSameDimensionality(
        Vector other)
    {
        assertEqualDimensionality(this, other);
    }
    
    public void assertDimensionalityEquals(
        final int otherDimensionality)
    {
        if (this.getDimensionality() != otherDimensionality)
        {
            throw new DimensionalityMismatchException(
                this.getDimensionality(), otherDimensionality);
        }
    }

    /**
     * Throws a DimensionalityMismatchException if
     * first.getDimensionality() != second.getDimensionality(),
     * otherwise this function has no effect
     *
     * @param first 
     *          first vector to consider
     * @param second 
     *          second vector to consider
     */
    public static void assertEqualDimensionality(
        Vector first,
        Vector second )
    {
        first.assertDimensionalityEquals( second.getDimensionality() );
    }

    public Vector unitVector()
    {
        final Vector result = this.clone();
        result.unitVectorEquals();
        return result;
    }

    public void unitVectorEquals()
    {
        final double norm2 = this.norm2();

        if (norm2 != 0.0)
        {
            this.scaleEquals( 1.0 / norm2 );
        }
        // else - The only way the norm2 can be zero is if the vector is all
        //        zeros. In this case the vector is to remain all zeros.
    }

    /**
     * Converts a vector to a string that consists of each value in the vector 
     *
     * @return String containing the elements of this
     */
    @Override
    public String toString()
    {
        final int dimensionality = this.getDimensionality();
        final StringBuilder builder = new StringBuilder(20 * dimensionality);
        for (int i = 0; i < dimensionality; i++)
        {
            if (i > 0)
            {
                builder.append(DEFAULT_DELIMITER);
            }

            builder.append( this.getElement(i) );
        }

        return builder.toString();    }
    
    public String toString(
        final NumberFormat format)
    {
        return this.toString(format, DEFAULT_DELIMITER);
    }
    
    public String toString(
        final NumberFormat format,
        final String delimiter)
    {
        final int dimensionality = this.getDimensionality();
        final StringBuilder builder = new StringBuilder(20 * dimensionality);
        for (int i = 0; i < dimensionality; i++)
        {
            if (i > 0)
            {
                builder.append(delimiter);
            }
            
            builder.append( format.format(this.getElement(i)) );
        }

        return builder.toString();
    }

    /**
     * Returns this.
     *
     * @return This.
     */
    public Vector convertToVector()
    {
        return this;
    }

    /**
     * Assigns the values in the provided vector into this.
     *
     * @param parameters The vector to convert.
     */
    public void convertFromVector(
        Vector parameters )
    {
        this.assertSameDimensionality( parameters );

        for (int i = 0; i < this.getDimensionality(); i++)
        {
            this.setElement( i, parameters.getElement( i ) );
        }
    }

    public boolean isZero(
        final double effectiveZero)
    {
        for (VectorEntry e : this)
        {
            if (Math.abs(e.getValue()) > effectiveZero)
            {
                return false;
            }
        }

        return true;
    }

    public boolean isUnitVector()
    {
        return this.isUnitVector(0.0);
    }

    public boolean isUnitVector(
        final double tolerance)
    {
        return Math.abs(this.norm2() - 1.0) <= tolerance;
    }
}
