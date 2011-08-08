/*
 * File:                AbstractDifferentiableUnivariateScalarFunctionTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Jun 10, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.math;

/**
 * Unit tests for AbstractDifferentiableUnivariateScalarFunctionTest.
 *
 * @author krdixon
 */
public class AbstractDifferentiableUnivariateScalarFunctionTest
    extends AbstractUnivariateScalarFunctionTest
{


    /**
     * Tests for class AbstractDifferentiableUnivariateScalarFunctionTest.
     * @param testName Name of the test.
     */
    public AbstractDifferentiableUnivariateScalarFunctionTest(
        String testName)
    {
        super(testName);
    }


    /**
     * Tests the constructors of class AbstractDifferentiableUnivariateScalarFunctionTest.
     */
    public void testConstructors()
    {
        System.out.println( "Constructors" );
        AbstractDifferentiableUnivariateScalarFunction f = this.createInstance();
        assertNotNull( f );
    }

    /**
     * Test of differentiate method, of class AbstractDifferentiableUnivariateScalarFunction.
     */
    public void testDifferentiate()
    {
        System.out.println("differentiate");
        Double input = random.nextGaussian();
        ScaleFunction f = this.createInstance();
        assertEquals( f.scale, f.differentiate(input) );
        assertEquals( f.scale, f.differentiate(input.doubleValue()) );
    }

}
