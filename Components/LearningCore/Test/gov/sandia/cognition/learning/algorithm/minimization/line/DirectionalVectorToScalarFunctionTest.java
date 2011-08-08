/*
 * File:                DirectionalVectorToScalarFunctionTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright October 24, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.learning.algorithm.minimization.line;

import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.evaluator.Evaluator;
import gov.sandia.cognition.math.DifferentiableEvaluator;
import gov.sandia.cognition.math.matrix.NumericalDifferentiator;
import gov.sandia.cognition.math.matrix.mtj.Vector3;
import gov.sandia.cognition.util.AbstractCloneableSerializable;
import java.util.Random;
import junit.framework.TestCase;

/**
 *
 * @author Kevin R. Dixon
 */
public class DirectionalVectorToScalarFunctionTest extends TestCase
{
    /** The random number generator for the tests. */
    public final Random random = new Random(1);

    public final double TOLERANCE = 1e-5;
    
    public DirectionalVectorToScalarFunctionTest(String testName)
    {
        super(testName);
    }

    
    public Vector3 createRandomInput()
    {
        return Vector3.createRandom(random);
    }

    public class TestVectorScalarFunction
        extends AbstractCloneableSerializable
        implements DifferentiableEvaluator<Vector,Double,Vector>
    {
        
        private Vector other = createRandomInput();
        
        public Double evaluate(
            Vector input)
        {
            return input.dotProduct( this.other );
        }

        public Vector differentiate(
            Vector input)
        {
            return other;
        }
    }


    public DirectionalVectorToScalarFunction createInstance()
    {
        Vector point = this.createRandomInput();
        Vector direction = this.createRandomInput();
        return new DirectionalVectorToScalarFunction(
            new TestVectorScalarFunction(), point, direction );
    }

    public void testConstructors()
    {
        System.out.println( "Constructors" );

        DirectionalVectorToScalarFunction f = this.createInstance();
        assertNotNull( f );
        assertNotNull( f.getDirection() );
        assertNotNull( f.getVectorOffset() );
        assertNotNull( f.getVectorScalarFunction() );

        DirectionalVectorToScalarFunction f2 = new DirectionalVectorToScalarFunction( f );
        assertNotNull( f2 );
        assertNotSame( f, f2 );
        assertNotSame( f.getDirection(), f2.getDirection() );
        assertEquals( f.getDirection(), f2.getDirection() );
        assertNotSame( f.getVectorOffset(), f2.getVectorOffset() );
        assertEquals( f.getVectorOffset(), f2.getVectorOffset() );
        assertNotSame( f.getVectorScalarFunction(), f2.getVectorScalarFunction() );

    }

    public void testClone()
    {
        System.out.println( "Clone" );

        DirectionalVectorToScalarFunction f = this.createInstance();
        DirectionalVectorToScalarFunction f2 = f.clone();
        assertNotNull( f );
        assertNotSame( f, f2 );
        assertNotSame( f.getDirection(), f2.getDirection() );
        assertEquals( f.getDirection(), f2.getDirection() );
        assertNotSame( f.getVectorOffset(), f2.getVectorOffset() );
        assertEquals( f.getVectorOffset(), f2.getVectorOffset() );
        assertNotSame( f.getVectorScalarFunction(), f2.getVectorScalarFunction() );
    }

    /**
     * Test of getDirection method, of class gov.sandia.cognition.learning.linesearch.DirectionalVectorToScalarFunction.
     */
    public void testGetDirection()
    {
        System.out.println("getDirection");
        
        DirectionalVectorToScalarFunction instance = this.createInstance();
        assertNotNull( instance.getDirection() );
    }

    /**
     * Test of setDirection method, of class gov.sandia.cognition.learning.linesearch.DirectionalVectorToScalarFunction.
     */
    public void testSetDirection()
    {
        System.out.println("setDirection");
        
        DirectionalVectorToScalarFunction instance = this.createInstance();
        
        Vector d2 = instance.getDirection().scale( random.nextGaussian() );
        instance.setDirection( d2 );
        assertSame( d2, instance.getDirection() );

    }

    /**
     * Test of getVectorOffset method, of class gov.sandia.cognition.learning.linesearch.DirectionalVectorToScalarFunction.
     */
    public void testGetVectorOffset()
    {
        System.out.println("getVectorOffset");
        
        DirectionalVectorToScalarFunction instance = this.createInstance();
        assertNotNull( instance.getVectorOffset() );

    }

    /**
     * Test of setVectorOffset method, of class gov.sandia.cognition.learning.linesearch.DirectionalVectorToScalarFunction.
     */
    public void testSetVectorOffset()
    {
        System.out.println("setVectorOffset");
        
        DirectionalVectorToScalarFunction instance = this.createInstance();
        
        Vector d2 = instance.getVectorOffset().scale( random.nextGaussian() );
        instance.setVectorOffset( d2 );
        assertSame( d2, instance.getVectorOffset() );
    }

    /**
     * Test of computeVector method, of class gov.sandia.cognition.learning.linesearch.DirectionalVectorToScalarFunction.
     */
    public void testComputeVector()
    {
        System.out.println("computeVector");
        
        double scaleFactor = random.nextGaussian();
        DirectionalVectorToScalarFunction instance = this.createInstance();
        
        Vector delta = instance.getVectorOffset().plus( instance.getDirection().scale( scaleFactor ) );        
        Vector result = instance.computeVector( scaleFactor );
        if( !delta.equals( result, TOLERANCE ) )
        {
            assertEquals( delta, result );
        }

    }

    /**
     * Test of evaluate method, of class gov.sandia.cognition.learning.linesearch.DirectionalVectorToScalarFunction.
     */
    public void testEvaluate()
    {
        System.out.println("evaluate");
        
        Double scaleFactor = random.nextGaussian();
        DirectionalVectorToScalarFunction instance = this.createInstance();
        
        Double expResult = instance.getVectorScalarFunction().evaluate( instance.computeVector( scaleFactor ) );
        Double result = instance.evaluate(scaleFactor);
        assertEquals(expResult, result, TOLERANCE );

    }

    /**
     * Test of getVectorScalarFunction method, of class gov.sandia.cognition.learning.linesearch.DirectionalVectorToScalarFunction.
     */
    public void testGetVectorScalarFunction()
    {
        System.out.println("getVectorScalarFunction");
        
        DirectionalVectorToScalarFunction instance = this.createInstance();
        
        assertNotNull( instance.getVectorScalarFunction() );

    }

    /**
     * Test of setVectorScalarFunction method, of class gov.sandia.cognition.learning.linesearch.DirectionalVectorToScalarFunction.
     */
    public void testSetVectorScalarFunction()
    {
        System.out.println("setVectorScalarFunction");
        
        DirectionalVectorToScalarFunction instance = this.createInstance();
        Evaluator<? super Vector, ? extends Double> vectorScalarFunction = instance.getVectorScalarFunction();
        
        instance.setVectorScalarFunction( null );
        assertNull( instance.getVectorScalarFunction() );
        
        instance.setVectorScalarFunction( vectorScalarFunction );
        assertSame( vectorScalarFunction, instance.getVectorScalarFunction() );

    }

    public void testDifferentiate()
    {
        System.out.println( "Differentiate" );
        
        DirectionalVectorToScalarFunction instance = this.createInstance();
        double input = random.nextGaussian();
        double result = instance.differentiate( input );
        double estimate = NumericalDifferentiator.DoubleJacobian.differentiate(
            input, instance, DirectionalVectorToScalarFunction.FORWARD_DIFFERENCE );
        assertEquals( result, estimate, TOLERANCE );
        
    }

}
