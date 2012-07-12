/*
 * File:                LinearCombinationFunctionTestHarness.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Jul 7, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.learning.function;

import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorFactory;
import junit.framework.TestCase;
import java.util.Random;

/**
 * Unit tests for LinearCombinationFunctionTestHarness.
 *
 * @param <InputType> Input type
 * @param <OutputType> Output type
 * @author krdixon
 */
public abstract class LinearCombinationFunctionTestHarness<InputType,OutputType>
    extends TestCase
{

    /**
     * Random number generator to use for a fixed random seed.
     */
    public final Random RANDOM = new Random( 1 );

    /**
     * Default tolerance of the regression tests, {@value}.
     */
    public final double TOLERANCE = 1e-5;

    /**
     * Tests for class LinearCombinationFunctionTestHarness.
     * @param testName Name of the test.
     */
    public LinearCombinationFunctionTestHarness(
        String testName)
    {
        super(testName);
    }

    /**
     * Creates a random input
     * @return Random input
     */
    public abstract InputType createRandomInput();

    /**
     * Creates an instance
     * @return instance
     */
    public abstract LinearCombinationFunction<InputType,OutputType> createInstance();

    /**
     * Tests the constructors of class LinearCombinationFunctionTestHarness.
     */
    public abstract void testConstructors();
    
    /**
     * Test of clone method, of class LinearCombinationFunction.
     */
    public void testClone()
    {
        System.out.println("clone");
        LinearCombinationFunction<?,?> instance = this.createInstance();
        LinearCombinationFunction<?,?> clone = instance.clone();
        assertNotNull( clone );
        assertNotSame( instance, clone );
        assertNotNull( clone.getCoefficients() );
        assertEquals( instance.getCoefficients(), clone.getCoefficients() );
        assertNotSame( instance.getBasisFunctions(), clone.getBasisFunctions() );
        assertEquals( instance.getBasisFunctions().size(), clone.getBasisFunctions().size() );
        for( int i = 0; i < instance.getBasisFunctions().size(); i++ )
        {
            assertNotNull( clone.getBasisFunctions().get(i) );
            assertNotSame( instance.getBasisFunctions().get(i),
                clone.getBasisFunctions().get(i) );
        }

    }

    /**
     * Test of getCoefficients method, of class LinearCombinationFunction.
     */
    public void testGetCoefficients()
    {
        System.out.println("getCoefficients");
        LinearCombinationFunction<?,?> instance = this.createInstance();
        assertNotNull( instance.getCoefficients() );
    }

    /**
     * Test of setCoefficients method, of class LinearCombinationFunction.
     */
    public void testSetCoefficients()
    {
        System.out.println("setCoefficients");
        LinearCombinationFunction<?,?> instance = this.createInstance();
        Vector c = instance.getCoefficients();
        Vector cs = c.scale( RANDOM.nextGaussian() );
        assertNotNull( c );
        instance.setCoefficients( cs );
        assertSame( cs, instance.getCoefficients() );

        try
        {
            instance.setCoefficients( null );
            fail( "Cannot have null coefficients" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }

        Vector c2 = VectorFactory.getDefault().createUniformRandom(
            c.getDimensionality()+1, -1, 1, RANDOM );
        try
        {
            instance.setCoefficients(c2);
            fail( "Wrong number of coefficients" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }
        
    }

    /**
     * Test of getBasisFunctions method, of class LinearCombinationFunction.
     */
    public void testGetBasisFunctions()
    {
        System.out.println("getBasisFunctions");
        LinearCombinationFunction<?,?> instance = this.createInstance();
        assertNotNull( instance.getBasisFunctions() );
        assertTrue( instance.getBasisFunctions().size() > 0 );
    }

    /**
     * Test of setBasisFunctions method, of class LinearCombinationFunction.
     */
    public void testSetBasisFunctions()
    {
        System.out.println("setBasisFunctions");
        LinearCombinationFunction<InputType,OutputType> instance = this.createInstance();
        LinearCombinationFunction<InputType,OutputType> i2 = this.createInstance();
        assertNotSame( instance.getBasisFunctions(), i2.getBasisFunctions() );
        instance.setBasisFunctions(i2.getBasisFunctions());
        assertSame( instance.getBasisFunctions(), i2.getBasisFunctions() );
    }

    /**
     * Test of convertToVector method, of class LinearCombinationFunction.
     */
    public void testConvertToVector()
    {
        System.out.println("convertToVector");
        LinearCombinationFunction<?,?> instance = this.createInstance();
        assertSame( instance.getCoefficients(), instance.convertToVector() );
    }

    /**
     * Test of convertFromVector method, of class LinearCombinationFunction.
     */
    public void testConvertFromVector()
    {
        System.out.println("convertFromVector");
        LinearCombinationFunction<?,?> instance = this.createInstance();
        Vector parameters = instance.convertToVector();
        Vector p2 = parameters.scale(RANDOM.nextGaussian());
        instance.convertFromVector(p2);
        assertEquals( p2, instance.convertToVector() );

        Vector p3 = VectorFactory.getDefault().createUniformRandom(
            p2.getDimensionality()+1, -1, 1, RANDOM );
        try
        {
            instance.convertFromVector(p3);
            fail( "Wrong number of parameters" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }
    }

    /**
     * Test of toString method, of class LinearCombinationFunction.
     */
    public void testToString()
    {
        System.out.println("toString");
        LinearCombinationFunction<?,?> instance = this.createInstance();
        String result = instance.toString();
        System.out.println( "Mixture:\n" + result );
        assertNotNull( result );
        assertTrue( result.length() > 0 );
    }

    /**
     * Test of evaluate method, of class LinearCombinationFunction.
     */
    public abstract void testEvaluate();

}
