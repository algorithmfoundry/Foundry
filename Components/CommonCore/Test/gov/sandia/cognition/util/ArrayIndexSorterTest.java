/*
 * File:                ArrayIndexSorterTest.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright July 18, 2006, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.util;

import gov.sandia.cognition.annotation.CodeReview;
import java.util.Random;
import junit.framework.TestCase;


/**
 * This class implements JUnit tests for the following classes:
 *
 *      ArrayIndexSorter
 *
 * @author Justin Basilico
 * @since 1.0
 */
@CodeReview(
    reviewer="Justin Basilico",
    date="2006-07-18",
    changesNeeded=false,
    comments={
        "Added file header.",
        "Otherwise, looks fine."
    }
)
public class ArrayIndexSorterTest
    extends TestCase
{
    
    public ArrayIndexSorterTest(
        String testName)
    {
        super(testName);
    }

    /**
     * Random
     */
    public final Random RANDOM = new Random(1);

    public void testConstructors()
    {
        System.out.println( "Constructors" );

        ArrayIndexSorter a = new ArrayIndexSorter();
        assertNotNull( a );
    }


    /**
     * Test of sortArrayAscending method, of class gov.sandia.isrc.util.ArrayIndexSorter.
     */
    public void testSortArrayAscending()
    {
        
        System.out.println("sortArrayAscending");
        
        int M = 10;
        double[] valuesToSort = new double[ M ];
        for( int i = 0; i < M; i++ )
        {
            valuesToSort[i] = RANDOM.nextGaussian();
        }
        
        int[] ascendingIndices = ArrayIndexSorter.sortArrayAscending( valuesToSort );
        double previousValue = Double.NEGATIVE_INFINITY;
        for( int i = 0; i < M; i++ )
        {
            double currentValue = valuesToSort[ascendingIndices[i]];
            if( currentValue < previousValue )
            {
                fail( "Values are not in ascending order!" );
            }
            previousValue = currentValue;
        }

    }

    /**
     * Test of sortArrayDescending method, of class gov.sandia.isrc.util.ArrayIndexSorter.
     */
    public void testSortArrayDescending()
    {
        System.out.println("sortArrayDescending");
        
        int M = 13;
        double[] valuesToSort = new double[ M ];
        for( int i = 0; i < M; i++ )
        {
            valuesToSort[i] = RANDOM.nextGaussian();
        }
        
        int[] descendingIndices = ArrayIndexSorter.sortArrayDescending( valuesToSort );
        double previousValue = Double.POSITIVE_INFINITY;
        for( int i = 0; i < M; i++ )
        {
            double currentValue = valuesToSort[descendingIndices[i]];
            if( currentValue > previousValue )
            {
                fail( "Values are not in descending order!" );
            }
            previousValue = currentValue;
        }
    }
    
}
