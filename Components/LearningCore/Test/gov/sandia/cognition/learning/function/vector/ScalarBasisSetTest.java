/*
 * File:                ScalarBasisSetTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Nov 30, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 * 
 */

package gov.sandia.cognition.learning.function.vector;

import gov.sandia.cognition.evaluator.Evaluator;
import gov.sandia.cognition.learning.function.scalar.PolynomialFunction;
import gov.sandia.cognition.math.matrix.Vector;
import java.util.Collection;
import junit.framework.TestCase;

/**
 * JUnit tests for class ScalarBasisSetTest
 * @author Kevin R. Dixon
 */
public class ScalarBasisSetTest
    extends TestCase
{

    /**
     * Entry point for JUnit tests for class ScalarBasisSetTest
     * @param testName name of this test
     */
    public ScalarBasisSetTest(
        String testName )
    {
        super( testName );
    }

    public ScalarBasisSet<Double> createInstance()
    {
        return new ScalarBasisSet<Double>(
            PolynomialFunction.createPolynomials( 0.0, 1.0, 2.0 ) );
    }

    /**
     * Test of clone method, of class ScalarBasisSet.
     */
    public void testClone()
    {
        System.out.println( "clone" );
        ScalarBasisSet<?> instance = this.createInstance();
        ScalarBasisSet<?> clone = (ScalarBasisSet<?>) instance.clone();
        assertNotSame( instance, clone );
    }

    /**
     * Test of getOutputDimensionality method, of class ScalarBasisSet.
     */
    public void testGetOutputDimensionality()
    {
        System.out.println( "getOutputDimensionality" );
        ScalarBasisSet<?> instance = this.createInstance();
        assertEquals( instance.getBasisFunctions().size(), instance.getOutputDimensionality() );

    }

    /**
     * Test of evaluate method, of class ScalarBasisSet.
     */
    public void testEvaluate()
    {
        System.out.println( "evaluate" );
        ScalarBasisSet<Double> instance = this.createInstance();
        double x = Math.random();
        Vector yhat = instance.evaluate( x );
        int i = 0;
        for (Evaluator<? super Double, Double> f : instance.getBasisFunctions())
        {
            assertEquals( f.evaluate( x ), yhat.getElement( i ), 1e-5 );
            i++;
        }

    }

    /**
     * Test of getBasisFunctions method, of class ScalarBasisSet.
     */
    public void testGetBasisFunctions()
    {
        System.out.println( "getBasisFunctions" );
        ScalarBasisSet<Double> instance = this.createInstance();
        Collection<? extends Evaluator<? super Double, Double>> result = instance.getBasisFunctions();
        assertNotNull( result );

    }

    /**
     * Test of setBasisFunctions method, of class ScalarBasisSet.
     */
    public void testSetBasisFunctions()
    {
        System.out.println( "setBasisFunctions" );
        ScalarBasisSet<Double> instance = this.createInstance();
        Collection<? extends Evaluator<? super Double, Double>> result = instance.getBasisFunctions();
        assertNotNull( result );

        instance.setBasisFunctions( null );
        assertNull( instance.getBasisFunctions() );
        instance.setBasisFunctions( result );

        assertSame( result, instance.getBasisFunctions() );
    }

}
