/*
 * File:                LinearFunctionTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Mar 19, 2008, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government. 
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */
package gov.sandia.cognition.learning.function.scalar;

/**
 * JUnit tests for class LinearFunctionTest
 * @author Kevin R. Dixon
 */
public class LinearFunctionTest
    extends DifferentiableUnivariateScalarFunctionTestHarness
{
    
    /**
     * Entry point for JUnit tests for class LinearFunctionTest
     * @param testName name of this test
     */
    public LinearFunctionTest(
        String testName)
    {
        super(testName);
    }

    /**
     * Test of evaluate method, of class LinearFunction.
     */
    public void testEvaluate()
    {
        System.out.println( "evaluate" );
        
        for( int i = 0; i < NUM_SAMPLES; i++ )
        {
            LinearFunction instance = new LinearFunction();
            double input = RANDOM.nextDouble();
            assertEquals( input, instance.evaluate(input) );
        }
    }

    /**
     * Test of differentiate method, of class LinearFunction.
     */
    public void testDifferentiate()
    {
        System.out.println( "differentiate" );
        for( int i = 0; i < NUM_SAMPLES; i++ )
        {
            LinearFunction instance = new LinearFunction();
            double input = RANDOM.nextDouble();
            assertEquals( 1.0, instance.differentiate(input) );
        }
    }

    @Override
    public LinearFunction createInstance()
    {
        return new LinearFunction();
    }

    @Override
    public void testConstructors()
    {
        LinearFunction f = new LinearFunction();
        assertNotNull( f );
        LinearFunction f2 = new LinearFunction(f);
        assertNotNull( f );
        assertNotSame( f, f2 );
     }

}
