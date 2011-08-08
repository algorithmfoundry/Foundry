/*
 * File:                GeneralConfidenceIntervalEvaluatorTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright October 30, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.statistics.method;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Random;
import junit.framework.TestCase;

/**
 *
 * @author Kevin R. Dixon
 */
public class FieldConfidenceIntervalTest
    extends TestCase
{

    Random random = new Random( 1 );

    public FieldConfidenceIntervalTest(
        String testName)
    {
        super(testName);
    }
    
    public static class TestClass
    {
        
        private int i1 = (int) (Math.random() * 1000);

        protected short s1 = (short) (Math.random() * 1000);
        
        public byte b1 = (byte) (Math.random() * 100);
        
        float f1 = (float) Math.random();
        
        private double d1 = Math.random();
        
        protected Integer I1 = new Integer( (int) (Math.random() * 1000) );

        public Double D1 = new Double( Math.random() );
        
        String string1 = "This shouldn't be included";
        
    }
    
    /**
     * Test of computeConfidenceInterval method, of class gov.sandia.cognition.learning.util.statistics.GeneralConfidenceIntervalEvaluator.
     */
    public void testComputeConfidenceInterval()
    {
        System.out.println("computeConfidenceInterval");
        
        Collection<TestClass> data = new LinkedList<TestClass>();
        int N = 3;
        for( int i = 0; i < N; i++ )
        {
            data.add( new TestClass() );
        }
        double confidence = random.nextDouble();

        ArrayList<FieldConfidenceInterval> fcis = 
            FieldConfidenceInterval.computeConfidenceInterval( 
                data, new StudentTConfidence(), confidence );
        
        for( FieldConfidenceInterval f : fcis )
        {
            System.out.println( f );
        }
        
        assertEquals( 7, fcis.size() );
        
    }
    
}
