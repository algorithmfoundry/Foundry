/*
 * File:                MatrixEntryIndexComparatorMTJTest.java
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

import junit.framework.*;
import gov.sandia.cognition.math.matrix.EntryIndexComparator;
import gov.sandia.cognition.math.matrix.Matrix;
import gov.sandia.cognition.math.matrix.MatrixEntry;
import gov.sandia.cognition.math.matrix.MatrixFactory;
import java.util.Iterator;
import java.util.Random;

/**
 * This class implements JUnit tests for the following classes:
 *
 *     MatrixEntryIndexComparatorMTJ
 *
 * @author Justin Basilico
 * @since 1.0
 */
public class MatrixEntryIndexComparatorMTJTest
    extends TestCase
{
    protected Random random = new Random();
    public MatrixEntryIndexComparatorMTJTest(
        String testName)
    {
        super(testName);
    }

    public static Test suite()
    {
        TestSuite suite = new TestSuite(MatrixEntryIndexComparatorMTJTest.class);
        
        return suite;
    }

    /**
     * Test of lowestIndex method, of class gov.sandia.isrc.math.matrix.mtj.MatrixEntryIndexComparatorMTJ.
     */
    public void testLowestIndex()
    {
        Matrix first = MatrixFactory.getDefault().createUniformRandom(4, 7, -1.0, 1.0, random);
        Matrix second = MatrixFactory.getDefault().createUniformRandom(4, 7, -1.0, 1.0, random);
        MatrixEntryIndexComparatorMTJ instance = 
            MatrixEntryIndexComparatorMTJ.INSTANCE;
        
        Iterator<MatrixEntry> it1 = first.iterator();
        it1.next();
        
        Iterator<MatrixEntry> it2 = first.iterator();
        it2.next();
        it2.next();
        it2.next();
        
        MatrixEntry entry1 = it1.next();
        MatrixEntry entry2 = it2.next();
        
        assertEquals(EntryIndexComparator.Compare.BOTH_ENTRIES_NULL, 
            instance.lowestIndex(null, null));
        
        assertEquals(EntryIndexComparator.Compare.ENTRIES_EQUAL, 
            instance.lowestIndex(entry1, entry1));
        assertEquals(EntryIndexComparator.Compare.ENTRIES_EQUAL, 
            instance.lowestIndex(entry2, entry2));
        
        assertEquals(EntryIndexComparator.Compare.FIRST_ENTRY_NULL, 
            instance.lowestIndex(null, entry2));
        assertEquals(EntryIndexComparator.Compare.FIRST_LOWEST, 
            instance.lowestIndex(entry1, entry2));
        
        assertEquals(EntryIndexComparator.Compare.SECOND_ENTRY_NULL, 
            instance.lowestIndex(entry1, null));
        assertEquals(EntryIndexComparator.Compare.SECOND_LOWEST, 
            instance.lowestIndex(entry2, entry1));
    }
}
