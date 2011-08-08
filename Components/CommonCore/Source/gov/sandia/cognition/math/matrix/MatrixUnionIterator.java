/*
 * File:                MatrixUnionIterator.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Framework Lite
 *
 * Copyright March 23, 2006, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 *
 */

package gov.sandia.cognition.math.matrix;

import gov.sandia.cognition.annotation.CodeReview;
import gov.sandia.cognition.annotation.CodeReviewResponse;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Iterator that stops at all nonzero entries for EITHER underlying matrix
 *
 * @author Kevin R. Dixon
 * @since  1.0
 */
@CodeReview(
    reviewer="Jonathan McClain",
    date="2006-05-16",
    changesNeeded=false,
    comments="A few comments, marked with triple slashes",
    response=@CodeReviewResponse(
        respondent="Kevin R. Dixon",
        date="2006-05-18",
        moreChangesNeeded=false,
        comments="Fixes from J.T.'s code review"
    )
)
public class MatrixUnionIterator
    implements Iterator<TwoMatrixEntry>
{
    
    /**
     * Iterator from the first Matrix
     */
    private Iterator<MatrixEntry> firstIterator;
    /**
     * Iterator from the second Matrix
     */
    private Iterator<MatrixEntry> secondIterator;
    
    /**
     * TwoMatrixEntry that represents the entries in each Matrix
     */
    private TwoMatrixEntry internalEntry;
    
    /**
     * Entry from the first Matrix
     */
    private MatrixEntry firstInternalEntry;
    /**
     * Entry from the Second Matrix
     */
    private MatrixEntry secondInternalEntry;
    
    /**
     * The comparator for iterating over matrix indices.
     */
    private EntryIndexComparator<MatrixEntry> indexComparator = null;

    
    /**
     * Creates a new instance of MatrixUnionIterator
     * @param firstIterator Iterator from the first Matrix
     * @param secondIterator Iterator from the second Matrix
     * @param internalEntry TwoMatrixEntry for this
     * @param indexComparator The index comparator for the matrices.
     */
    public MatrixUnionIterator(
        Iterator<MatrixEntry> firstIterator,
        Iterator<MatrixEntry> secondIterator,
        TwoMatrixEntry internalEntry,
        EntryIndexComparator<MatrixEntry> indexComparator)        
    {
        this.setFirstIterator( firstIterator );
        this.setSecondIterator( secondIterator );
        
        this.setInternalEntry( internalEntry );
        
        this.setFirstInternalEntry( null );
        this.setSecondInternalEntry( null );
     
        this.setIndexComparator(indexComparator);
    }
    
    
    /**
     * getter for firstInterator
     * @return Iterator from the first Matrix
     */
    public Iterator<MatrixEntry> getFirstIterator()
    {
        return this.firstIterator;
    }

    /**
     * setter for firstInterator
     * @param firstIterator Iterator from the first Matrix
     */
    public void setFirstIterator(
        Iterator<MatrixEntry> firstIterator)
    {
        this.firstIterator = firstIterator;
    }

    /**
     * getter for secondIterator
     * @return Iterator from the second matrix
     */
    public Iterator<MatrixEntry> getSecondIterator()
    {
        return this.secondIterator;
    }

    /**
     * setter for secondIterator
     * @param secondIterator Iterator from the second Matrix
     */
    public void setSecondIterator(
        Iterator<MatrixEntry> secondIterator)
    {
        this.secondIterator = secondIterator;
    }

    /**
     * getter for firstInternalEntry
     * @return MatrixEntry into the first Matrix
     */
    public MatrixEntry getFirstInternalEntry()
    {
        return this.firstInternalEntry;
    }

    /**
     * setter for firstInternalEntry
     * @param firstInternalEntry MatrixEntry for the first Matrix
     */
    public void setFirstInternalEntry(
        MatrixEntry firstInternalEntry)
    {
        this.firstInternalEntry = firstInternalEntry;
    }

    /**
     * getter for secondInternalEntry
     * @return MatrixEntry from the second Matrix
     */
    public MatrixEntry getSecondInternalEntry()
    {
        return this.secondInternalEntry;
    }

    /**
     * setter for secondInternalEntry
     * @param secondInternalEntry MatrixEntry from the second Matrix
     */
    public void setSecondInternalEntry(
        MatrixEntry secondInternalEntry)
    {
        this.secondInternalEntry = secondInternalEntry;
    }


    
    /**
     * getter for internalEntry
     * @return TwoMatrixEntry that represents the entries of both matrices
     */
    public TwoMatrixEntry getInternalEntry()
    {
        return this.internalEntry;
    }

    /**
     * setter for internalEntry
     * @param internalEntry TwoMatrixEntry that contains the entries for both matrices
     */
    public void setInternalEntry(
        TwoMatrixEntry internalEntry)
    {
        this.internalEntry = internalEntry;
    }

    /**
     * Gets the index comparator.
     *
     * @return The index comparator.
     */
    public EntryIndexComparator<MatrixEntry> getIndexComparator()
    {
        return indexComparator;
    }
    
    /**
     * Sets the index comparator.
     *
     * @param  indexComparator The index comparator.
     */
    protected void setIndexComparator(
        EntryIndexComparator<MatrixEntry> indexComparator)
    {
        this.indexComparator = indexComparator;
    }
    
    /**               
     * Try to advance the first entry... if the iterator throws an
     * exception, then there are no more elements in the matrix,
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
     * Internal routine for advancing the internal iterators
     */
    protected void advanceInternalIterators()
    {
        
        boolean advance_first = false;
        boolean advance_second = false;
        
        EntryIndexComparator.Compare compare = 
            this.getIndexComparator().lowestIndex(
                this.getFirstInternalEntry(), this.getSecondInternalEntry() );
        
        if( compare == EntryIndexComparator.Compare.FIRST_LOWEST )
        {
            advance_first = true;
        }
        else if( compare == EntryIndexComparator.Compare.SECOND_LOWEST )
        {
            advance_second = true;
        }
        else if( compare == EntryIndexComparator.Compare.FIRST_ENTRY_NULL )
        {
            advance_second = true;
        }
        else if( compare == EntryIndexComparator.Compare.SECOND_ENTRY_NULL )
        {
            advance_first = true;
        }
        else if( compare == EntryIndexComparator.Compare.BOTH_ENTRIES_NULL )
        {
            advance_first = true;
            advance_second = true;
        }
        else if( compare == EntryIndexComparator.Compare.ENTRIES_EQUAL )
        {
            advance_first = true;
            advance_second = true;
        }
        else
        {
            throw new NoSuchElementException(
                "Unknown Compare Enum: " + compare );
         }
            

        if( (advance_first == false) &&
            (advance_second == false) )
        {
            throw new NoSuchElementException(
                "Problem: Not advancing any iterators..." );
        }
        
        if( advance_first == true )
        {
            this.safeFirstNext();
        }
        
        if( advance_second == true )
        {
            this.safeSecondNext();
        }

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
    public TwoMatrixEntry next()
    {
        if ( !this.hasNext() )
        {
            throw new NoSuchElementException( "No elements remaining." );
        }
        
        this.advanceInternalIterators();
        
        EntryIndexComparator.Compare compare =
            this.getIndexComparator().lowestIndex(
                this.getFirstInternalEntry(), this.getSecondInternalEntry() );
        
        if( compare == EntryIndexComparator.Compare.ENTRIES_EQUAL )
        {
            this.getInternalEntry().setRowIndex(
                this.getFirstInternalEntry().getRowIndex() );
            this.getInternalEntry().setColumnIndex(
                this.getFirstInternalEntry().getColumnIndex() );
        }        
        else if( compare == EntryIndexComparator.Compare.FIRST_LOWEST )
        {
            this.getInternalEntry().setRowIndex(
                this.getFirstInternalEntry().getRowIndex() );
            this.getInternalEntry().setColumnIndex(
                this.getFirstInternalEntry().getColumnIndex() );
        }
        else if( compare == EntryIndexComparator.Compare.SECOND_LOWEST )
        {
            this.getInternalEntry().setRowIndex(
                this.getSecondInternalEntry().getRowIndex() );
            this.getInternalEntry().setColumnIndex(
                this.getSecondInternalEntry().getColumnIndex() );
        }
        else if( compare == EntryIndexComparator.Compare.FIRST_ENTRY_NULL )
        {
            this.getInternalEntry().setRowIndex(
                this.getSecondInternalEntry().getRowIndex() );
            this.getInternalEntry().setColumnIndex(
                this.getSecondInternalEntry().getColumnIndex() );
        }
        else if( compare == EntryIndexComparator.Compare.SECOND_ENTRY_NULL )
        {
            this.getInternalEntry().setRowIndex(
                this.getFirstInternalEntry().getRowIndex() );
            this.getInternalEntry().setColumnIndex(
                this.getFirstInternalEntry().getColumnIndex() );
        }
        else
        {
            throw new NoSuchElementException( "No elements remaining." );
        }

        return this.getInternalEntry();
                
    }

    /**
     * {@inheritDoc}
     *
     * @throws UnsupportedOperationException Always.
     * @throws NoSuchElementException If there is no element to remove.
     */
    public void remove()
        throws UnsupportedOperationException, NoSuchElementException
    {
        throw new UnsupportedOperationException("Remove is not supported");
    }
    
}
