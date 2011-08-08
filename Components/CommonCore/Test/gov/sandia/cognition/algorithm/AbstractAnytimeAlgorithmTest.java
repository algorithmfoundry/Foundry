/*
 * File:                AbstractAnytimeAlgorithmTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Apr 17, 2008, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government. 
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */
package gov.sandia.cognition.algorithm;

import junit.framework.TestCase;

/**
 * JUnit tests for class AbstractAnytimeAlgorithmTest
 * @author Kevin R. Dixon
 */
public class AbstractAnytimeAlgorithmTest
    extends TestCase
{

    /**
     * Entry point for JUnit tests for class AbstractAnytimeAlgorithmTest
     * @param testName name of this test
     */
    public AbstractAnytimeAlgorithmTest(
        String testName)
    {
        super(testName);
    }

    MyAbstractAnytimeAlgorithm createInstance()
    {
        return new MyAbstractAnytimeAlgorithm();
    }
    
    /**
     * 
     */
    public static class MyAbstractAnytimeAlgorithm
        extends AbstractAnytimeAlgorithm<Double>
    {

        public Double result;
        public boolean stopped;
        
        public static final int DEFAULT_MAX_ITERATIONS = 100;
        
        public MyAbstractAnytimeAlgorithm()
        {
            super( DEFAULT_MAX_ITERATIONS );
            this.result = null;
            this.stopped = false;
        }
        
        public Double getResult()
        {
            return this.result;
        }

        public void stop()
        {
            this.stopped = true;
        }
        
    }
    
    /**
     * Test of isResultValid method, of class AbstractAnytimeAlgorithm.
     */
    public void testIsResultValid()
    {
        System.out.println( "isResultValid" );
        MyAbstractAnytimeAlgorithm instance = this.createInstance();
        assertFalse( instance.isResultValid() );
        instance.result = new Double(1.0);
        assertTrue( instance.isResultValid() );
    }

    /**
     * Test of getMaxIterations method, of class AbstractAnytimeAlgorithm.
     */
    public void testGetMaxIterations()
    {
        System.out.println( "getMaxIterations" );
        MyAbstractAnytimeAlgorithm instance = this.createInstance();
        assertEquals( instance.DEFAULT_MAX_ITERATIONS, instance.getMaxIterations() );
    }

    /**
     * Test of setMaxIterations method, of class AbstractAnytimeAlgorithm.
     */
    public void testSetMaxIterations()
    {
        System.out.println( "setMaxIterations" );
        MyAbstractAnytimeAlgorithm instance = this.createInstance();
        assertEquals( instance.DEFAULT_MAX_ITERATIONS, instance.getMaxIterations() );
        int maxIterations = instance.getMaxIterations() + 1;
        instance.setMaxIterations(maxIterations);
        assertEquals( maxIterations, instance.getMaxIterations() );
        
        try
        {
            instance.setMaxIterations(0);
            fail( "Maxiterations must be > 0" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }

    }
    
    public void testIteration()
    {
        System.out.println( "Iteration" );
        
        MyAbstractAnytimeAlgorithm instance = this.createInstance();
        assertEquals( 0, instance.getIteration() );
        instance.setIteration(1);
        assertEquals( 1, instance.getIteration() );
    }

}
