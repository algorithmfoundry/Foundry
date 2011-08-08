/*
 * File:                LinearFunctionTest.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright October 5, 2006, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.learning.function.vector;

import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.math.matrix.Matrix;
import gov.sandia.cognition.math.matrix.MatrixFactory;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.mtj.Vector3;
import java.util.Random;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 *
 * @author jdbasil
 */
public class LinearVectorFunctionTest
    extends TestCase
{
    /** The random number generator for the tests. */
    public Random random = new Random(1);

    public static final double EPS = 1e-5;

    /** Default Constructor instance. */
    protected LinearVectorFunction defaultInstance;
    
    public LinearVectorFunctionTest(String testName)
    {
        super(testName);
    }

    /**
     * {@inheritDoc}
     * @throws java.lang.Exception
     */
    @Override
    protected void setUp() throws Exception
    {
        
        this.defaultInstance = new LinearVectorFunction();
        
    }

    public static Test suite()
    {
        TestSuite suite = new TestSuite(LinearVectorFunctionTest.class);
        
        return suite;
    }

    /**
     * Test of evaluate method, of class gov.sandia.isrc.learning.util.function.LinearFunction.
     */
    public void testEvaluate()
    {
        System.out.println("evaluate");
        
        Vector input = new Vector3(1.0, 0.0, -3.0);
        Vector defaultResult = this.defaultInstance.evaluate(input);
        assertEquals(new Vector3(1.0, 0.0, -3.0), defaultResult);
        
        
        LinearVectorFunction instance = new LinearVectorFunction(2.0);
        Vector result = instance.evaluate(input);
        assertEquals(new Vector3(2.0, 0.0, -6.0), result);
        
        
        Vector x2 = VectorFactory.getDefault().createUniformRandom( 10, -10.0, 10.0, random );
        LinearVectorFunction f2 = new LinearVectorFunction( random.nextGaussian() );
        Vector y2 = f2.evaluate( x2 );
        assertTrue( y2.equals( x2.scale( f2.getScaleFactor() ), EPS ) );
        
    }

    /**
     * Test of getScaleFactor method, of class gov.sandia.isrc.learning.util.function.LinearFunction.
     */
    public void testGetScaleFactor()
    {
        System.out.println("getScaleFactor");
        
        assertEquals(LinearVectorFunction.DEFAULT_SCALE_FACTOR, this.defaultInstance.getScaleFactor());
        
        double scaleFactor = random.nextGaussian();
        LinearVectorFunction instance = new LinearVectorFunction(scaleFactor);
        assertEquals(scaleFactor, instance.getScaleFactor());
    }

    /**
     * Test of setScaleFactor method, of class gov.sandia.isrc.learning.util.function.LinearFunction.
     */
    public void testSetScaleFactor()
    {
        System.out.println("setScaleFactor");
        double scaleFactor = random.nextGaussian();
        LinearVectorFunction instance = new LinearVectorFunction(scaleFactor);
        assertEquals(scaleFactor, instance.getScaleFactor());
        
        double newScaleFactor = random.nextGaussian();
        instance.setScaleFactor(newScaleFactor);
        assertEquals(newScaleFactor, instance.getScaleFactor());
        
        instance.setScaleFactor(0.0);
        assertEquals(0.0, instance.getScaleFactor());
    }

    /**
     * Test of differentiate method, of class gov.sandia.isrc.learning.util.function.LinearFunction.
     */
    public void testDifferentiate()
    {
        System.out.println("differentiate (Scalar)");
        
        for( int i = 0; i < 10; i++ )
        {
            int N = random.nextInt( 10 ) + 1;
            Vector x = VectorFactory.getDefault().createUniformRandom( N, -10.0, 10.0, random );
            LinearVectorFunction f = new LinearVectorFunction( random.nextGaussian() );
            Matrix dfdx = f.differentiate( x );
            Matrix expected = MatrixFactory.getDefault().createIdentity( N, N ).scale( f.getScaleFactor() );
            assertTrue( expected.equals( dfdx, EPS ) );
        }

    }

    protected LinearVectorFunction createFunction()
    {
        return new LinearVectorFunction( random.nextGaussian() );
    }
    
}
