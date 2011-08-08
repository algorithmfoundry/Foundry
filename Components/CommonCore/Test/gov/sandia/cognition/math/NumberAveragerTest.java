/*
 * File:                NumberAveragerTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Dec 11, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 * 
 */
package gov.sandia.cognition.math;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Random;
import junit.framework.TestCase;

/**
 * JUnit tests for class NumberAveragerTest
 * @author Kevin R. Dixon
 */
public class NumberAveragerTest
    extends TestCase
{

    /**
     * Entry point for JUnit tests for class NumberAveragerTest
     * @param testName name of this test
     */
    public NumberAveragerTest(
        String testName)
    {
        super(testName);
    }

    /**
     * Test of clone method, of class NumberAverager.
     */
    public void testClone()
    {
        System.out.println( "clone" );
        NumberAverager instance = new NumberAverager();
        NumberAverager clone = (NumberAverager) instance.clone();
        assertNotNull( clone );
        assertNotSame( clone, instance );
    }

    /**
     * Test of summarize method, of class NumberAverager.
     */
    public void testSummarize()
    {
        System.out.println( "summarize" );
        Collection<Integer> data = new LinkedList<Integer>();
        Random r = new Random( 1 );
        NumberAverager instance = new NumberAverager();
        double sum = 0.0;
        int num = 100;
        for( int i = 0; i < num; i++ )
        {
            int v = r.nextInt();
            sum += v;
            data.add( v );
        }
        assertEquals( sum/num, instance.summarize(data) );

    }
    
    /**
     * Test of data with no elements in it
     */
    public void testZeroData()
    {
        System.out.println( "Zero data" );
        NumberAverager instance = new NumberAverager();
        Collection<Integer> data = new LinkedList<Integer>();
        assertNull( instance.summarize(data) );
    }

}
