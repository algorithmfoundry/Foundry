/*
 * File:                EntryIndexComparator.java
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
 * The EntryIndexComparator interface defines the functionality of a comparator
 * for index entries. It is not a standard Comparator because it handles null
 * entries in a graceful manner by having the return type indicate which 
 * parameter is null.
 *
 * @param <EntryType> Entry that this Comparator function upon
 * @author Justin Basilico
 * @since 1.0
 */
public interface EntryIndexComparator<EntryType>
{

    /**
     * Indicates which of two iterators has the lowest index
     */
    public enum Compare
    {

        /**
         * first iterator has lowest index
         */
        FIRST_LOWEST,
        /**
         * second iterator has lowest index
         */
        SECOND_LOWEST,
        /**
         * both iterators are equally indexed
         */
        ENTRIES_EQUAL,
        /**
         * both entries are null
         */
        BOTH_ENTRIES_NULL,
        /**
         * first entry is null
         */
        FIRST_ENTRY_NULL,
        /**
         * second entry is null
         */
        SECOND_ENTRY_NULL

    }

    /**
     * Determines which iterator has the lowest index 
     *  
     * @param firstEntry
     *          first entry to consider
     * @param secondEntry 
     *          second entry to consider
     *
     * @return FIRST_LOWEST if firstIterator has lowest index,
     *          SECOND_LOWEST if secondIterator has lowest index,
     *          ENTRIES_EQUAL if both are equal
     *          BOTH_ENTRIES_NULL if both are null
     *          FIRST_ENTRY_NULL if first entry is null
     *          SECOND_ENTRY_NULL if second entryis null
     */
    public Compare lowestIndex(
        EntryType firstEntry,
        EntryType secondEntry );

}

