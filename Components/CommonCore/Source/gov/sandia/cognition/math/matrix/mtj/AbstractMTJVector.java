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
     * Definition for an Iterator over all MTJ vectors
     */
    class AbstractMTJVectorIterator
        extends AbstractCloneableSerializable
        implements Iterator<VectorEntry>
    {
        
        /**
         * Internal MTJ-based vector that does the heavy lifting for this package
         */
        private Iterator<no.uib.cipr.matrix.VectorEntry> internalIterator;
        /**
         * Current VectorEntry for this iterator
         */
        private VectorEntry entry;
        
        /**
         * Creates a new instance of AbstractMTJVectorIterator
         */
        public AbstractMTJVectorIterator()
        {
            setInternalIterator(
                AbstractMTJVector.this.internalVector.iterator() );
            setEntry( new AbstractMTJVectorEntry() );
        }   
        
        /**
         * Gets the internal MTJ iterator
         * @return
         * Internal MTJ-based vector that does the heavy lifting for this package
         */
        protected Iterator<no.uib.cipr.matrix.VectorEntry> getInternalIterator()
        {
            return this.internalIterator;
        }

        /**
         * Setter for internalIterator
         * @param internalIterator 
         * Internal MTJ-based vector that does the heavy lifting for this package
         */
        protected void setInternalIterator(
            Iterator<no.uib.cipr.matrix.VectorEntry> internalIterator)
        {
            this.internalIterator = internalIterator;
        }

        public boolean hasNext()
        {
            return this.getInternalIterator().hasNext();
        }

        public VectorEntry next()
        {
            no.uib.cipr.matrix.VectorEntry internalNext =
                getInternalIterator().next();

            getEntry().setIndex( internalNext.index() );
            return getEntry();
        }
        
        public void remove()
        {
            getInternalIterator().remove();
        }

        /**
         * Gets the underlying VectorEntry
         * @return VectorEntry specified by this iterator
         */
        public VectorEntry getEntry()
        {
            return this.entry;
        }

        /**
         * Sets the VectorEntry
         * @param entry VectorEntry specified by this iterator
         */
        public void setEntry(
            VectorEntry entry)
        {
            this.entry = entry;
        }
    }


    /**
     * VectorEntry for MTJ Vectors
     */
    class AbstractMTJVectorEntry
        extends AbstractCloneableSerializable
        implements VectorEntry
    {
        /**
         * Current index into the vector for the iterator
         */
        private int index;
        
        /**
         * Creates a new instance of AbstractMTJVectorEntry
         */
        public AbstractMTJVectorEntry()
        {
            this( 0 );
        }
        
        /**
         * Creates a new instances of AbstractMTJVectorEntry
         * @param index index to start in the Vector
         */
        public AbstractMTJVectorEntry(
            int index)
        {
            this.setIndex( index );
        }
        
        public double getValue()
        {
            return AbstractMTJVector.this.getElement( this.getIndex() );
        }

        public void setValue(
            double value)
        {
            AbstractMTJVector.this.setElement( this.getIndex(), value );
        }

        public int getIndex()
        {
            return this.index;
        }

        public void setIndex(
            int index)
        {
            this.index = index;
        }
    }
}
