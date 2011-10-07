/*
 * File:                AbstractVectorSpace.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright Jun 23, 2011, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 *
 */
package gov.sandia.cognition.math.matrix;

import gov.sandia.cognition.math.AbstractRing;
import gov.sandia.cognition.math.matrix.VectorSpace.Entry;
import gov.sandia.cognition.util.ArgumentChecker;

/**
 * Partial implementation of VectorSpace
 * @param <VectorType>
 * Type of VectorSpace
 * @param <EntryType>
 * Type of Entry in the VectorSpace
 * @author Kevin R. Dixon
 * @since 3.3.1
 */
public abstract class AbstractVectorSpace<VectorType extends VectorSpace<VectorType,? extends EntryType>,EntryType extends VectorSpace.Entry>
    extends AbstractRing<VectorType>
    implements VectorSpace<VectorType,EntryType>
{

    /** 
     * Creates a new instance of AbstractVectorSpace 
     */
    public AbstractVectorSpace()
    {
        super();
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean equals(
        final Object other )
    {
        if (other == null)
        {
            return false;
        }
        else if (this == other)
        {
            return true;
        }
        else if (other instanceof VectorSpace)
        {
            return this.equals( (VectorType) other, 0.0 );
        }
        else
        {
            return false;
        }
    }

    @Override
    public double sum()
    {
        double sum = 0.0;
        for( VectorSpace.Entry entry : this )
        {
            sum += entry.getValue();
        }
        return sum;
    }

    @Override
    public double norm1()
    {
        double sum1 = 0.0;
        for( VectorSpace.Entry entry : this )
        {
            sum1 += Math.abs(entry.getValue());
        }
        return sum1;
    }

    @Override
    public double norm2()
    {
        return Math.sqrt( this.norm2Squared() );
    }

    @Override
    public double norm2Squared()
    {
        double sum2 = 0.0;
        for( Entry entry : this )
        {
            final double value = entry.getValue();
            sum2 += value*value;
        }
        return sum2;
    }

    @Override
    public double normInfinity()
    {
        double max = 0.0;
        for( VectorSpace.Entry entry : this )
        {
            final double value = Math.abs(entry.getValue());
            if( max < value )
            {
                max = value;
            }
        }
        return max;
    }

    @Override
    public double norm(
        final double power)
    {
        ArgumentChecker.assertIsPositive("power", power);
        double sum = 0.0;
        for( Entry entry : this )
        {
            final double value = Math.abs(entry.getValue());
            sum += Math.pow(value,power);
        }
        return Math.pow(sum,1.0/power);
    }

    @Override
    public double angle(
        final VectorType other)
    {
        return Math.acos(this.cosine(other));
    }

    @Override
    public double cosine(
        final VectorType other)
    {
        // Computing cosine as:
        //  cosine = (x' * y) / (||x|| * ||y||)
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
    public double euclideanDistance(
        final VectorType other)
    {
        return Math.sqrt( this.euclideanDistanceSquared(other) );
    }

    @Override
    public VectorType unitVector()
    {
        final VectorType result = this.clone();
        result.unitVectorEquals();
        return result;
    }

    @Override
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

    @Override
    public boolean isUnitVector()
    {
        return this.isUnitVector(0.0);
    }

    @Override
    public boolean isUnitVector(
        final double tolerance)
    {
        return Math.abs(this.norm2() - 1.0) <= tolerance;
    }

    @Override
    public boolean equals(
        final VectorType other,
        final double effectiveZero)
    {
        return this.euclideanDistance(other) <= effectiveZero;
    }

    @Override
    public void scaleEquals(
        final double scaleFactor)
    {
        for( Entry entry : this )
        {
            final double value = entry.getValue();
            entry.setValue(value * scaleFactor);
        }
    }

    @Override
    public boolean isZero(
        final double effectiveZero)
    {
        for( Entry e: this )
        {
            if( e.getValue() > effectiveZero )
            {
                return false;
            }
        }
        return true;
    }

}
