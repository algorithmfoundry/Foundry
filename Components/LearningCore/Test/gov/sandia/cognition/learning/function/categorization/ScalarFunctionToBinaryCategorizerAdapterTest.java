/*
 * File:                ScalarFunctionToBinaryCategorizerAdapterTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Jul 1, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.learning.function.categorization;

import gov.sandia.cognition.evaluator.Evaluator;
import gov.sandia.cognition.util.AbstractCloneableSerializable;

/**
 * Unit tests for ScalarFunctionToBinaryCategorizerAdapterTest.
 *
 * @author krdixon
 */
public class ScalarFunctionToBinaryCategorizerAdapterTest
    extends ThresholdBinaryCategorizerTestHarness<String>
{

    /**
     * Tests for class ScalarFunctionToBinaryCategorizerAdapterTest.
     * @param testName Name of the test.
     */
    public ScalarFunctionToBinaryCategorizerAdapterTest(
        String testName)
    {
        super(testName);
    }


    /**
     * Maps string length onto a double
     */
    public static class StringLengthEvaluator
        extends AbstractCloneableSerializable
        implements Evaluator<String,Double>
    {

        public Double evaluate(
            String input)
        {
            return new Double( input.length() );
        }
        
    }

    @Override
    public ScalarFunctionToBinaryCategorizerAdapter<String> createInstance()
    {
        return new ScalarFunctionToBinaryCategorizerAdapter<String>(
            new StringLengthEvaluator(), RANDOM.nextGaussian() );
    }

    @Override
    public String createRandomInput()
    {
        String retval = "a";
        while( RANDOM.nextDouble() > 0.1 )
        {
            retval += String.valueOf(RANDOM.nextInt(255));
        }
        return retval;
    }



    /**
     * Tests the constructors of class ScalarFunctionToBinaryCategorizerAdapterTest.
     */
    public void testConstructors()
    {
        System.out.println( "Constructors" );

        ScalarFunctionToBinaryCategorizerAdapter f = new ScalarFunctionToBinaryCategorizerAdapter();
        assertNotNull( f );
        assertNull( f.getEvaluator() );
        assertEquals( ScalarFunctionToBinaryCategorizerAdapter.DEFAULT_THRESHOLD, f.getThreshold() );

        StringLengthEvaluator e = new StringLengthEvaluator();
        f = new ScalarFunctionToBinaryCategorizerAdapter<String>( e );
        assertNotNull( f );
        assertSame( e, f.getEvaluator() );
        assertEquals( ScalarFunctionToBinaryCategorizerAdapter.DEFAULT_THRESHOLD, f.getThreshold() );

        double t = RANDOM.nextGaussian();
        f = new ScalarFunctionToBinaryCategorizerAdapter<String>( e, t );
        assertNotNull( f );
        assertSame( e, f.getEvaluator() );
        assertEquals( t, f.getThreshold() );
    }


    /**
     * Clone local
     */
    public void testCloneLocal()
    {
        System.out.println( "Clone local" );

        ScalarFunctionToBinaryCategorizerAdapter<String> instance = this.createInstance();
        ScalarFunctionToBinaryCategorizerAdapter<String> clone = instance.clone();
        assertNotNull( clone );
        assertNotSame( instance, clone );
        assertNotSame( instance.getEvaluator(), clone.getEvaluator() );

    }


    /**
     * Test of evaluateAsDouble method, of class ScalarFunctionToBinaryCategorizerAdapter.
     */
    public void testEvaluateAsDouble()
    {
        System.out.println("evaluateAsDouble");
        ScalarFunctionToBinaryCategorizerAdapter<String> instance = this.createInstance();
        assertEquals( 5.0, instance.evaluateAsDouble("Kevin") );
    }

    /**
     * Test of getEvaluator method, of class ScalarFunctionToBinaryCategorizerAdapter.
     */
    public void testGetEvaluator()
    {
        System.out.println("getEvaluator");
        ScalarFunctionToBinaryCategorizerAdapter instance = this.createInstance();
        assertNotNull( instance.getEvaluator() );
    }

    /**
     * Test of setEvaluator method, of class ScalarFunctionToBinaryCategorizerAdapter.
     */
    public void testSetEvaluator()
    {
        System.out.println("setEvaluator");
        ScalarFunctionToBinaryCategorizerAdapter<String> instance = this.createInstance();
        Evaluator<? super String,Double> f = instance.getEvaluator();
        assertNotNull( f );
        instance.setEvaluator(null);
        assertNull( instance.getEvaluator() );
        instance.setEvaluator(f);
        assertSame( f, instance.getEvaluator() );
    }

}
