/*
 * File:                VectorFunctionLinearDiscriminantTest.java
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

package gov.sandia.cognition.learning.function.scalar;

import gov.sandia.cognition.evaluator.Evaluator;
import gov.sandia.cognition.learning.function.vector.ScalarBasisSet;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.math.matrix.Vector;
import java.util.Random;
import junit.framework.TestCase;

/**
 * JUnit tests for class VectorFunctionLinearDiscriminantTest
 * @author Kevin R. Dixon
 */
public class VectorFunctionLinearDiscriminantTest
    extends TestCase
{

    /** The random number generator for the tests. */
    protected Random random = new Random(1);
    
    /**
     * Entry point for JUnit tests for class VectorFunctionLinearDiscriminantTest
     * @param testName name of this test
     */
    public VectorFunctionLinearDiscriminantTest(
        String testName )
    {
        super( testName );
    }

    public VectorFunctionLinearDiscriminant<Double> createInstance()
    {
        return new VectorFunctionLinearDiscriminant<Double>(
            new ScalarBasisSet<Double>( PolynomialFunction.createPolynomials( 0.0, 1.0, 2.0, 3.0 ) ),
            VectorFactory.getDefault().createUniformRandom( 4, -1.0, 1.0, random ) );
    }

    /**
     * Test of clone method, of class VectorFunctionLinearDiscriminant.
     */
    public void testClone()
    {
        System.out.println( "clone" );
        VectorFunctionLinearDiscriminant instance = this.createInstance();
        VectorFunctionLinearDiscriminant clone = instance.clone();
        assertNotSame( instance, clone );
        assertNotNull( clone.getVectorFunction() );
        assertNotSame( instance.getVectorFunction(), clone.getVectorFunction() );
        assertNotSame( instance.getWeightVector(), clone.getWeightVector() );
        assertEquals( instance.getWeightVector(), clone.getWeightVector() );
    }

    /**
     * Test of getWeightVector method, of class VectorFunctionLinearDiscriminant.
     */
    public void testGetWeightVector()
    {
        System.out.println( "getWeightVector" );
        VectorFunctionLinearDiscriminant instance = this.createInstance();
        Vector w = instance.getWeightVector();
        assertNotNull( w );

    }

    /**
     * Test of setWeightVector method, of class VectorFunctionLinearDiscriminant.
     */
    public void testSetWeightVector()
    {
        System.out.println( "setWeightVector" );
        VectorFunctionLinearDiscriminant instance = this.createInstance();
        Vector w = instance.getWeightVector();
        assertNotNull( w );

        instance.setWeightVector( null );
        assertNull( instance.getWeightVector() );

        instance.setWeightVector( w );
        assertSame( w, instance.getWeightVector() );
    }

    /**
     * Test of getVectorFunction method, of class VectorFunctionLinearDiscriminant.
     */
    public void testGetVectorFunction()
    {
        System.out.println( "getVectorFunction" );
        VectorFunctionLinearDiscriminant<Double> instance = this.createInstance();
        Evaluator<? super Double, Vector> f = instance.getVectorFunction();
        assertNotNull( f );

    }

    /**
     * Test of setVectorFunction method, of class VectorFunctionLinearDiscriminant.
     */
    public void testSetVectorFunction()
    {
        System.out.println( "setVectorFunction" );
        VectorFunctionLinearDiscriminant<Double> instance = this.createInstance();
        Evaluator<? super Double, Vector> f = instance.getVectorFunction();
        assertNotNull( f );

        instance.setVectorFunction( null );
        assertNull( instance.getVectorFunction() );

        instance.setVectorFunction( f );
        assertSame( f, instance.getVectorFunction() );
    }

    /**
     * Test of evaluate method, of class VectorFunctionLinearDiscriminant.
     */
    public void testEvaluate()
    {
        System.out.println( "evaluate" );
        VectorFunctionLinearDiscriminant<Double> instance = this.createInstance();
        double x = random.nextGaussian();
        Double yhat = instance.evaluate( x );

        Vector v = instance.getVectorFunction().evaluate( x );
        double y = instance.getWeightVector().dotProduct( v );
        assertEquals( y, yhat, 1e-5 );

    }

    /**
     * Test of convertToVector method, of class VectorFunctionLinearDiscriminant.
     */
    public void testConvertToVector()
    {
        System.out.println( "convertToVector" );
        VectorFunctionLinearDiscriminant instance = this.createInstance();
        Vector v = instance.convertToVector();
        assertNotNull( v );
        assertSame( v, instance.getWeightVector() );
        assertEquals( 4, v.getDimensionality() );

    }

    /**
     * Test of convertFromVector method, of class VectorFunctionLinearDiscriminant.
     */
    public void testConvertFromVector()
    {
        System.out.println( "convertFromVector" );
        VectorFunctionLinearDiscriminant instance = this.createInstance();
        Vector v = instance.convertToVector();
        assertNotNull( v );
        assertSame( v, instance.getWeightVector() );

        Vector v2 = v.scale( random.nextGaussian() );
        instance.convertFromVector( v2 );
        assertEquals( v2, instance.convertToVector() );

    }

}
