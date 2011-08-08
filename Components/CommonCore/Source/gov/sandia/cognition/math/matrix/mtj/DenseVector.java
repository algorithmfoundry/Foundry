/*
 * File:                DenseVector.java
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
import gov.sandia.cognition.annotation.CodeReviewResponse;
import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorEntry;
import gov.sandia.cognition.math.matrix.VectorReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * A generally useful vector representation that allocates a fixed-size
 * underlying vector, based on MTJ's DenseVector
 *
 * @author Kevin R. Dixon
 * @since  1.0
 *
 */
@CodeReview(
    reviewer="Jonathan McClain",
    date="2006-05-19",
    changesNeeded=true,
    comments="Comments marked with / / /",
    response=@CodeReviewResponse(
        respondent="Kevin R. Dixon",
        date="2006-05-22",
        moreChangesNeeded=false,
        comments="Fixed comments for writeObject() and readObject()"
    )
)
@PublicationReference(
    author="Bjorn-Ove Heimsund",
    title="MTJ DenseVector javadoc",
    type=PublicationType.WebPage,
    year=2006,
    url="http://ressim.berlios.de/doc/no/uib/cipr/matrix/DenseVector.html"
)
public class DenseVector
    extends AbstractMTJVector
    implements Serializable
{
    
    /**
     * Creates a new instance of DenseVector
     * @param numDimensions Number of dimensions in the Vector
     */
    protected DenseVector(
        int numDimensions)
    {
        super( new no.uib.cipr.matrix.DenseVector( numDimensions ) );
        if( numDimensions < 0 )
        {
            throw new IllegalArgumentException(
                "Vector must have non-negative dimensionality" );
        }
    }

    /**
     * Creates a new instance of DenseVector
     * @param vector Vector from which to populate the elements of this, will not be modified
     */
    protected DenseVector(
        DenseVector vector )
    {
        super( (no.uib.cipr.matrix.DenseVector)
            vector.getInternalVector().copy() );
    }
    
    /**
     * Copy constructor for DenseVector
     * @param vector Vector from which to populate the elements of this, will not be modified
     */
    protected DenseVector(
        Vector vector )
    {
        this( vector.getDimensionality() );
        
        for( VectorEntry e : vector )
        {
            this.setElement( e.getIndex(), e.getValue() );
        }
    }
    
    /**
     * Copy constructor
     * @param vector Vector to copy
     */
    protected DenseVector(
        AbstractMTJVector vector )
    {
        super( new no.uib.cipr.matrix.DenseVector( vector.getInternalVector() ) );
    }
    
    /**
     * Creates a new instance of DenseVector
     * @param values The array of values to give the vector
     */
    protected DenseVector(
        double[] values)
    {
        this( new no.uib.cipr.matrix.DenseVector( values ) );
    }

    /**
     * Creates a new instance of DenseVector
     * @param internalVector Internal MTJ-based vector that does the heavy lifting
     */
    protected DenseVector(
        no.uib.cipr.matrix.DenseVector internalVector)
    {
        super( internalVector );
    }

    /**
     * Creates a new instance of DenseVector
     * @param reader takes in information from a java stream
     * @throws java.io.IOException if the stream is invalid
     */
    protected DenseVector(
        VectorReader reader )
        throws IOException
    {
        this( reader.read() );
    }

    /**
     * Sets the internalVector using MTJ's DenseVector
     * @param internalVector internal MTJ-based DenseVector
     */
    protected void setInternalVector(
        no.uib.cipr.matrix.DenseVector internalVector)
    {
        super.setInternalVector( internalVector );
    }

    @Override
    public boolean equals(
        final Vector other,
        double effectiveZero)
    {
        // Determine if all entries are within effectiveZero of each other.
        // If we find a single entry larger than effectiveZero, we know that
        // the vectors aren't equal, so just return false.  However, if we loop
        // over all entries and still don't find a large difference, then
        // we consider the vectors "equal".
        //
        // Please note: this structure does not exploit ANY type of sparseness
        // in either vector.
        double[] values = this.getArray();
        for( int i = 0; i < this.getDimensionality(); i++ )
        {
            double difference = values[i] - other.getElement( i );
            if( Math.abs( difference ) > effectiveZero )
            {
                return false;
            }
        }

        return true;

    }

    @Override
    public double euclideanDistanceSquared(
        Vector other )
    {
        this.assertSameDimensionality( other );
        
        double sumSquared = 0.0;
        double[] values = this.getArray();
        int M = this.getDimensionality();
        for( int i = 0; i < M; i++ )
        {
            double delta = values[i] - other.getElement( i );
            sumSquared += delta*delta;
        }
        
        return sumSquared;
    }
    
    
    public DenseMatrix outerProduct(
        final AbstractMTJVector other)
    {
        
        int M = this.getDimensionality();
        int N = other.getDimensionality();
        DenseMatrix retval = new DenseMatrix( M, N );
        double[] values = this.getArray();
        for( int i = 0; i < M; i++ )
        {
            for( int j = 0; j < N; j++ )
            {
                retval.setElement( i, j, values[i] * other.getElement(j) );
            }
        }
        
        return retval;
        
    }
    
    public DenseVector stack(
        Vector other)
    {
        int M1 = this.getDimensionality();
        int M2 = other.getDimensionality();
        
        DenseVector stacked = new DenseVector( M1 + M2 );
        for( VectorEntry e : this )
        {
            stacked.setElement( e.getIndex(), e.getValue() );
        }
        for( VectorEntry e : other )
        {
            stacked.setElement( M1 + e.getIndex(), e.getValue() );
        }
        
        return stacked;
        
    }

    /**
     * Gets a subvector of "this", specified by the inclusive indices
     * @param minIndex minimum index to get (inclusive)
     * @param maxIndex maximum index to get (inclusive)
     * @return vector of dimension (maxIndex-minIndex+1)
     */
    public DenseVector subVector(
        int minIndex,
        int maxIndex )
    {
        int M = maxIndex - minIndex + 1;
        DenseVector retval = new DenseVector( M );
        double[] retvalValues = retval.getArray();
        double[] values = this.getArray();
        for( int i = 0; i < M; i++ )
        {
            retvalValues[i] = values[i + minIndex];
        }
        return retval;
    }
    
    /**
     * Returns the underlying double array for this DenseVector
     * @return internal double array for this DenseVector
     */
    public double[] getArray()
    {
        return ((no.uib.cipr.matrix.DenseVector) this.getInternalVector()).getData();
    }

    
    /**
     * Writes a DenseVector out to a serialized stream (usually file)
     * @param out output stream to which the DenseVector will be written
     * @throws java.io.IOException On bad write
     */
    private void writeObject(
        ObjectOutputStream out) 
        throws IOException 
    {
        out.defaultWriteObject();
        
        //manually serialize superclass
        out.writeObject( this.getArray() );

    }

    
    /**
     * Reads in a serialized class from the specified stream
     * @param in stream from which to read the DenseVector
     * @throws java.io.IOException On bad read
     * @throws java.lang.ClassNotFoundException if next object isn't DenseVector
     */
    private void readObject(
        ObjectInputStream in) 
        throws IOException, ClassNotFoundException 
    {
        in.defaultReadObject();
        double[] data = (double[]) in.readObject();
        boolean deepCopy = false;
        this.setInternalVector( 
            new no.uib.cipr.matrix.DenseVector( data, deepCopy ) );
    }    
}
