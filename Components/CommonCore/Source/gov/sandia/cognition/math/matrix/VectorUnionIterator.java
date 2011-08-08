/*
 * File:                VectorUnionIterator.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Framework Lite
 *
 * Copyright March 20, 2006, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 *
 */

package gov.sandia.cognition.math.matrix;

import gov.sandia.cognition.annotation.CodeReview;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Iterator that returns all nonzero entries for either underlying Vector
 *
 * @author Kevin R. Dixon
 * @since  1.0
 */
@CodeReview(
    reviewer="Jonathan McClain",
    date="2006-05-17",
    changesNeeded=false,
    comments="Looks fine."
)
public class VectorUnionIterator
    implements Iterator<TwoVectorEntry>
{
    
    /**
     * Iterator from the first Vector
     */
    private Iterator<VectorEntry> firstIterator;
    /**
     * Iterator from the second Vector
     */
    private Iterator<VectorEntry> secondIterator;
    
    /**
     * TwoVectorEntry that represents entries from both Vectors
     */
    private TwoVectorEntry internalEntry;
    
    /**
     * VectorEntry from the first Vector
     */
    private VectorEntry firstInternalEntry;
    /**
     * VectorEntry from the second Vector
     */
    private VectorEntry secondInternalEntry;
    
    /**
     * Creates a new instance of VectorUnionIterator.
     *
     * @param  first The first Vector.
     * @param  second The second Vector.
     */
    public VectorUnionIterator(
        Vector first,
        Vector second)
    {
        this(first.iterator(), second.iterator(), 
            new DefaultTwoVectorEntry(first, second));
    }
    
    /**
     * Creates a new instance of VectorUnionIterator
     * @param firstIterator Iterator from the first Vector
     * @param secondIterator Iterator from the second Vector
     * @param internalEntry TwoVectorEntry that represents entries from both Vectors
     */
    public VectorUnionIterator(
        Iterator<VectorEntry> firstIterator,
        Iterator<VectorEntry> secondIterator,
        TwoVectorEntry internalEntry)
    {
        this.setFirstIterator( firstIterator );
        this.setSecondIterator( secondIterator );
        this.setInternalEntry( internalEntry );
        
        this.setFirstInternalEntry( null );
        this.setSecondInternalEntry( null );
    }

    /**
     * Internal method for advancing the internal Iterators
     */
    protected void advanceInternalIterators()
    {
        
        boolean advanceFirst = false;
        boolean advanceSecond = false;
        
        EntryIndexComparator.Compare compare =
            VectorEntryIndexComparator.INSTANCE.lowestIndex(
                this.getFirstInternalEntry(), this.getSecondInternalEntry() );
        
        if( compare == EntryIndexComparator.Compare.FIRST_LOWEST )
        {
            advanceFirst = true;
        }
        else if( compare == EntryIndexComparator.Compare.SECOND_LOWEST )
        {
            advanceSecond = true;
        }
        else if( compare == EntryIndexComparator.Compare.FIRST_ENTRY_NULL )
        {
            advanceSecond = true;
        }
        else if( compare == EntryIndexComparator.Compare.SECOND_ENTRY_NULL )
        {
            advanceFirst = true;
        }
        else if( compare == EntryIndexComparator.Compare.BOTH_ENTRIES_NULL )
        {
            advanceFirst = true;
            advanceSecond = true;
        }
        else if( compare == EntryIndexComparator.Compare.ENTRIES_EQUAL )
        {
            advanceFirst = true;
            advanceSecond = true;
        }        
        else
        {
            throw new NoSuchElementException(
                "Unknown Compare Enum: " + compare );
         }
            

        if( (advanceFirst == false) &&
            (advanceSecond == false) )
        {
            throw new NoSuchElementException(
                "Problem: Not advancing any iterators..." );
        }
        
        if( advanceFirst == true )
        {
            this.safeFirstNext();
        }
        
        if( advanceSecond == true )
        {
            this.safeSecondNext();
        }
                
    }
    
    /**               
     * Try to advance the first entry... if the iterator throws an
     * exception, then there are no more elements in the vector,
     * so just null out the entry
     *
     * @return true if next was valid, false otherwise
     */
    public boolean safeFirstNext()
    {
        boolean valid_next;
        
        try
        {
            this.setFirstInternalEntry( this.getFirstIterator().next() );
            valid_next = true;
        }
        catch (Exception e)
        {
            this.setFirstInternalEntry( null );
            valid_next = false;
        }
        
        return valid_next;
        
    }

    /**               
     * Try to advance the second entry... if the iterator throws an
     * exception, then there are no more elements in the vector,
     * so just null out the entry
     *
     * @return true if next was valid, false otherwise
     */
    public boolean safeSecondNext()
    {
        boolean valid_next;
        
        try
        {
            this.setSecondInternalEntry( this.getSecondIterator().next() );
            valid_next = true;
        }
        catch (Exception e)
        {
            this.setSecondInternalEntry( null );
            valid_next = false;
        }
        
        return valid_next;
        
    }    
    
    
    /**
     * getter for firstIterator
     * @return Iterator from the first Vector
     */
    public Iterator<VectorEntry> getFirstIterator()
    {
        return this.firstIterator;
    }

    /**
     * setter for firstIterator
     * @param firstIterator Iterator from the first Vector
     */
    public void setFirstIterator(
        Iterator<VectorEntry> firstIterator)
    {
        this.firstIterator = firstIterator;
    }

    /**
     * getter for secondIterator
     * @return Iterator from the second Vector
     */
    public Iterator<VectorEntry> getSecondIterator()
    {
        return this.secondIterator;
    }

    /**
     * setter for secondIterator
     * @param secondIterator Iterator from the second Vector
     */
    public void setSecondIterator(
        Iterator<VectorEntry> secondIterator)
    {
        this.secondIterator = secondIterator;
    }

    /**
     * getter for internalEntry
     * @return TwoVectorEntry that represents both Vectors
     */
    public TwoVectorEntry getInternalEntry()
    {
        return this.internalEntry;
    }

    /**
     * setter for internalEntry
     * @param internalEntry TwoVectorEntry that represents entries from both Vectors
     */
    public void setInternalEntry(
        TwoVectorEntry internalEntry)
    {
        this.internalEntry = internalEntry;
    }

    /**
     * getter for firstInternalEntry
     * @return VectorEntry from the first Vector
     */
    public VectorEntry getFirstInternalEntry()
    {
        return this.firstInternalEntry;
    }

    /**
     * setter for firstInternalEntry
     * @param firstInternalEntry VectorEntry from the first Vector
     */
    public void setFirstInternalEntry(
        VectorEntry firstInternalEntry)
    {
        this.firstInternalEntry = firstInternalEntry;
    }

    /**
     * getter for secondInternalEntry
     * @return VectorEntry from the second Vector
     */
    public VectorEntry getSecondInternalEntry()
    {
        return this.secondInternalEntry;
    }

    /**
     * setter for secondInternalEntry
     * @param secondInternalEntry VectorEntry from the second Vector
     */
    public void setSecondInternalEntry(
        VectorEntry secondInternalEntry)
    {
        this.secondInternalEntry = secondInternalEntry;
    }
    
    
    /**
     * {@inheritDoc}
     * @return {@inheritDoc}
     */
    public boolean hasNext()
    {
        return (this.getFirstIterator().hasNext() == true) ||
            (this.getSecondIterator().hasNext() == true);
    }

    /**
     * {@inheritDoc}
     * @return {@inheritDoc}
     */
    public TwoVectorEntry next()
    {
        
        this.advanceInternalIterators();
        
        EntryIndexComparator.Compare compare = 
            VectorEntryIndexComparator.INSTANCE.lowestIndex(
                this.getFirstInternalEntry(), this.getSecondInternalEntry() );
        
        if( compare == EntryIndexComparator.Compare.ENTRIES_EQUAL )
        {
            this.getInternalEntry().setIndex(
                this.getFirstInternalEntry().getIndex() );
        }
        else if( compare == EntryIndexComparator.Compare.FIRST_LOWEST )
        {
            this.getInternalEntry().setIndex(
                this.getFirstInternalEntry().getIndex() );
        }
        else if( compare == EntryIndexComparator.Compare.SECOND_LOWEST )
        {
            this.getInternalEntry().setIndex(
                this.getSecondInternalEntry().getIndex() );
        }
        else if( compare == EntryIndexComparator.Compare.FIRST_ENTRY_NULL )
        {
            this.getInternalEntry().setIndex(
                this.getSecondInternalEntry().getIndex() );
        }
        else if( compare == EntryIndexComparator.Compare.SECOND_ENTRY_NULL )
        {
            this.getInternalEntry().setIndex(
                this.getFirstInternalEntry().getIndex() );
        }
        else
        {
            throw new NoSuchElementException( "No elements remaining." );
        }

        return this.getInternalEntry();
        
    }

    /**
     * {@inheritDoc}
     */
    public void remove()
    {
        this.getInternalEntry().setFirstValue( 0.0 );
        this.getInternalEntry().setSecondValue( 0.0 );
    }
    
    
}
