/*
 * File:                AbstractMTJVector.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright March 14, 2006, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.math.matrix.mtj;

import gov.sandia.cognition.annotation.CodeReview;
import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.annotation.SoftwareLicenseType;
import gov.sandia.cognition.annotation.SoftwareReference;
import gov.sandia.cognition.math.matrix.AbstractVector;
import gov.sandia.cognition.math.matrix.Matrix;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorEntry;
import gov.sandia.cognition.util.AbstractCloneableSerializable;
import java.io.Serializable;
import java.util.Iterator;

/**
 * Implementation of the Vector interface that relies on MTJ Vectors, but does
 * not specify sparse or dense storage.
 *
 * @author Kevin R. Dixon
 * @since  1.0
 *
 */
@CodeReview(
    reviewer="Jonathan McClain",
    date="2006-05-19",
    changesNeeded=false,
    comments="Comments marked throughout the file with / / / on first column."
)
@PublicationReference(
    author="Bjorn-Ove Heimsund",
    title="Matrix Toolkits for Java (MTJ)",
    type=PublicationType.WebPage,
    year=2006,
    url="http://ressim.berlios.de/",
    notes="All subclasses essentially wrap one of MTJ's vector classes."
)
@SoftwareReference(
    name="Matrix Toolkits for Java (MTJ)",
    version="0.9.6",
    url="http://ressim.berlios.de/",
    license=SoftwareLicenseType.LGPL,
    licenseVersion="2.1",
    licenseURL="http://ressim.berlios.de/")
public abstract class AbstractMTJVector
    extends AbstractVector
    implements Serializable
{

    /**
     * Internal vector from the MTJ package that does the heavy lifting
     */
    private transient no.uib.cipr.matrix.Vector internalVector;

    /**
     * Creates a new instance of AbstractMTJVector
     * @param internalVector Internal MTJ-based vector that does the heavy lifting
     */
    protected AbstractMTJVector(
        no.uib.cipr.matrix.Vector internalVector )
    {
        this.internalVector = internalVector;
    }

    @Override
    public AbstractMTJVector clone()
    {
        AbstractMTJVector clone = (AbstractMTJVector) super.clone();
        clone.setInternalVector( this.getInternalVector().copy() );
        return clone;
    }

    public int getDimensionality()
    {
        return this.internalVector.size();
    }
    
    @Override
    public double get(
        final int index)
    {
        return this.internalVector.get(index);
    }

    @Override
    public void set(
        final int index,
        final double value)
    {
        this.internalVector.set(index, value);
    }
    
    public double getElement(
        int index)
    {
        return this.internalVector.get( index );
    }

    public void setElement(
        int index,
        double value)
    {
        this.internalVector.set( index, value );
    }
    
    @Override
    public void increment(
        final int index,
        final double value)
    {
        this.internalVector.add(index, value);
    }

    public Vector times(
        Matrix matrix )
    {
        return this.times( (AbstractMTJMatrix) matrix );
    }
    
    /**
     * Premultiplies the matrix by the vector "this"
     * @param matrix
     * Matrix to premultiply by "this", must have the same number of rows as
     * the dimensionality of "this"
     * @return
     * Vector of dimension equal to the number of columns of "matrix"
     */
    public Vector times(
        AbstractMTJMatrix matrix )
    {
        int N = matrix.getNumColumns();
        DenseVector retval = new DenseVector( N );
        
        matrix.getInternalMatrix().transMult(
            this.getInternalVector(), retval.getInternalVector() );
        
        return retval;
    }
    
    /**
     * Getter for internalVector
     * @return MTJ-based internal vector that does the heavy lifting
     */
    protected no.uib.cipr.matrix.Vector getInternalVector()
    {
        return this.internalVector;
    }

    /**
     * Setter for internalVector
     * @param internalVector internal MTJ-based vector that does the heavy lifting
     */
    protected void setInternalVector(
         no.uib.cipr.matrix.Vector internalVector)
    {
        this.internalVector = internalVector;
    }

    @Override
    public double norm2()
    {
        return this.internalVector.norm(
            no.uib.cipr.matrix.Vector.Norm.Two );
    }
    
    public double norm2Squared()
    {
        double norm = this.norm2();
        return norm*norm;
    }

    public void plusEquals(
        final Vector other)
    {
        this.plusEquals( (AbstractMTJVector) other );
    }
    
    /**
     * Inline addition of this and the other vector
     *
     * @param other
     *          Vector to which to add the elements of this, must be the
     *          same dimension as this
     */
    public void plusEquals(
        final AbstractMTJVector other )
    {
        this.internalVector.add( other.internalVector );
    }
    
    public void minusEquals(
        final Vector other)
    {
        this.minusEquals( (AbstractMTJVector) other );
    }
    
    /**
     * Inline subtraction of the elements of other from the elements of this 
     *
     * @param other
     *          Vector from which to subtract the elements of this, must be the
     *          same dimension as this
     */
    public void minusEquals(
        final AbstractMTJVector other)
    {
        this.internalVector.add( -1.0, other.internalVector );
    }

    public double dotProduct(
        final Vector other)
    {
        return this.dotProduct( (AbstractMTJVector) other );
    }

    /**
     * Inner Vector product between two Vectors
     *
     *
     * @param other
     *          the Vector with which to compute the dot product with this,
     *          must be the same dimension as this
     * @return dot product, (0,\infty)
     */
    public double dotProduct(
        final AbstractMTJVector other)
    {
        this.assertSameDimensionality(other);
        return this.internalVector.dot( other.internalVector );
    }
    
    public void dotTimesEquals(
        final Vector other)
    {
        for( VectorEntry entry : this )
        {
            entry.setValue( 
                entry.getValue() * other.getElement(entry.getIndex()) );
        }
    }

    public void scaleEquals(double scaleFactor)
    {
        this.internalVector.scale( scaleFactor );   
    }

    @Override
    public void scaledPlusEquals(
        final double scaleFactor,
        final Vector other)
    {
        this.scaledPlusEquals(scaleFactor, (AbstractMTJVector) other);
    }

    /**
     * Adds to this vector the scaled version of the other given vector.
     *
     * @param   scaleFactor
     *      The scale factor to use.
     * @param   other
     *      The other vector to scale and then add to this vector.
     */
    public void scaledPlusEquals(
        final double scaleFactor,
        final AbstractMTJVector other)
    {
        this.internalVector.add(scaleFactor, other.internalVector);
    }

    /**
     * Subtracts from this vector the scaled version of the other given vector.
     *
     * @param   scaleFactor
     *      The scale factor to use.
     * @param   other
     *      The other vector to scale and then subtract from this vector.
     */
    public void scaledMinusEquals(
        final double scaleFactor,
        final AbstractMTJVector other)
    {
        this.scaledPlusEquals(-scaleFactor, other);
    }
    
    public Iterator<VectorEntry> iterator()
    {
        return new AbstractMTJVectorIterator();
    }

    @Override
    public void zero()
    {
        this.internalVector.zero();
    }

    public AbstractMTJMatrix outerProduct(
        final Vector other)
    {
        return this.outerProduct( (AbstractMTJVector) other );
    }

    /**
     * Computes the outer matrix product between the two vectors 
     *
     * @param other
     *          post-multiplied Vector with which to compute the outer product
     * @return Outer matrix product, which will have dimensions
     * (this.getDimensionality x other.getDimensionality) and WILL BE singular
     */
    public abstract AbstractMTJMatrix outerProduct(
        final AbstractMTJVector other);
    
    /**
     * Implements an iterator over {@link VectorEntry} objects for MTJ
     * Vectors. It is implemented with an emphasis on efficiency. As such,
     * it reuses the vector entry for each element so as not to create a lot 
     * of new objects. It relies on the MTJ internal iterator to provide the 
     * actual iteration implementation.
     */
    private final class AbstractMTJVectorIterator
        extends AbstractCloneableSerializable
        implements Iterator<VectorEntry>
    {
        
        /** Internal MTJ-based vector that does the heavy lifting. */
        private final Iterator<no.uib.cipr.matrix.VectorEntry> internalIterator;
        
        /** The vector entry that is reused when cycling through the iterator.
         *  Each time next is called it is updated to the next element, but
         *  the same entry is returned with this new state as an optimization to
         *  avoid creating a lot of extra objects when iterating. */
        private final AbstractMTJVectorEntry entry;
        
        /**
         * Creates a new instance of AbstractMTJVectorIterator
         */
        public AbstractMTJVectorIterator()
        {
            this.internalIterator = 
                AbstractMTJVector.this.internalVector.iterator();
            this.entry = new AbstractMTJVectorEntry(null);
        }  

        @Override
        public boolean hasNext()
        {
            return this.internalIterator.hasNext();
        }

        @Override
        public VectorEntry next()
        {
            this.entry.internalEntry = this.internalIterator.next();
            return this.entry;
        }
        
        @Override
        public void remove()
        {
            this.internalIterator.remove();
        }
        
    }

    /**
     * Implements a VectorEntry for MTJ Vectors. It is just a wrapper for
     * the internal MTJ entry that has the actual data methods.
     */
    private static final class AbstractMTJVectorEntry
        extends AbstractCloneableSerializable
        implements VectorEntry
    {
        
        /** Internally wrapped MTJ entry. */
        private no.uib.cipr.matrix.VectorEntry internalEntry;
        
        /**
         * Creates a new instance of {@link AbstractMTJVectorEntry}.
         */
        public AbstractMTJVectorEntry()
        {
            this(null);
        }
        
        /**
         * Creates a new instance of {@link AbstractMTJVectorEntry}.
         * 
         * @param internalEntry
         *      The internal MTJ vector entry to wrap.
         */
        public AbstractMTJVectorEntry(
            final no.uib.cipr.matrix.VectorEntry internalEntry)
        {
            super();
            
            this.internalEntry = internalEntry;
        }
        
        @Override
        public double getValue()
        {
            return this.internalEntry.get();
        }

        @Override
        public void setValue(
            double value)
        {
            this.internalEntry.set(value);
        }

        @Override
        public int getIndex()
        {
            return this.internalEntry.index();
        }

        @Override
        public void setIndex(
            int index)
        {
            // Setting the index on this entry doesn't make sense.
            throw new UnsupportedOperationException(
                "setIndex not supported on iterator VectorEntry.");
        }
        
    }
}
