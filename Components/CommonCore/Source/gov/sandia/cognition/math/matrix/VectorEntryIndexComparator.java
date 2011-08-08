/*
 * File:                VectorEntryIndexComparator.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Framework Lite
 *
 * Copyright July 31, 2006, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 *
 */

package gov.sandia.cognition.math.matrix;

/**
 * An index comparator for VectorEntry objects.
 *
 * @author Justin Basilico
 * @since 1.0
 */
public class VectorEntryIndexComparator
    extends Object
    implements EntryIndexComparator<VectorEntry>
{
    /** A single instance of this class to use because it has no internal memory. */
    public static final VectorEntryIndexComparator INSTANCE = 
        new VectorEntryIndexComparator();
    
    /**
     * Creates a new instance of VectorEntryIndexComparator
     */
    public VectorEntryIndexComparator()
    {
        super();
    }

    /**
     * {@inheritDoc}
     *
     * @param firstEntry {@inheritDoc}
     * @param secondEntry {@inheritDoc}
     * @return {@inheritDoc}
     */
    public EntryIndexComparator.Compare lowestIndex(
        VectorEntry firstEntry, 
        VectorEntry secondEntry)
    {
        Compare retval;
        
        if ( (firstEntry == null) &&
            (secondEntry == null) )
        {
            retval = Compare.BOTH_ENTRIES_NULL;
        }
        else if ( firstEntry == null )
        {
            retval = Compare.FIRST_ENTRY_NULL;
        }
        else if ( secondEntry == null )
        {
            retval = Compare.SECOND_ENTRY_NULL;
        }
        else if ( firstEntry.getIndex() < secondEntry.getIndex() )
        {
            retval = Compare.FIRST_LOWEST;
        }
        else if ( firstEntry.getIndex() > secondEntry.getIndex() )
        {
            retval = Compare.SECOND_LOWEST;
        }
        else
        {
            retval = Compare.ENTRIES_EQUAL;
        }
        
        return retval;  
    }
}

