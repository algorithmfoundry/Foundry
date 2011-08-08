/*
 * File:                MatrixEntryIndexComparatorMTJ.java
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

package gov.sandia.cognition.math.matrix.mtj;

import gov.sandia.cognition.math.matrix.EntryIndexComparator;
import gov.sandia.cognition.math.matrix.MatrixEntry;

/**
 * An index comparator for MTJ matrices.
 *
 * @author Justin Basilico
 * @since 1.0
 */
public class MatrixEntryIndexComparatorMTJ
    extends Object
    implements EntryIndexComparator<MatrixEntry>
{
    /** An instance of this class since it has no internal fields. */
    public static final MatrixEntryIndexComparatorMTJ INSTANCE =
        new MatrixEntryIndexComparatorMTJ();
    
    /**
     * Creates a new instance of MatrixEntryComparatorMTJ
     */
    public MatrixEntryIndexComparatorMTJ()
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
        MatrixEntry firstEntry, 
        MatrixEntry secondEntry)
    {
        Compare retval = null;
        
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
        else if ( firstEntry.getRowIndex() < secondEntry.getRowIndex() )
        {
            retval = Compare.FIRST_LOWEST;
        }
        else if ( firstEntry.getRowIndex() > secondEntry.getRowIndex() )
        {
            retval = Compare.SECOND_LOWEST;
        }
        else
        {
            if ( firstEntry.getColumnIndex() < secondEntry.getColumnIndex() )
            {
                retval = Compare.FIRST_LOWEST;
            }
            else if ( firstEntry.getColumnIndex() > secondEntry.getColumnIndex() )
            {
                retval = Compare.SECOND_LOWEST;
            }
            else
            {
                retval = Compare.ENTRIES_EQUAL;
            }
        }
        
        return retval;
    }
}

