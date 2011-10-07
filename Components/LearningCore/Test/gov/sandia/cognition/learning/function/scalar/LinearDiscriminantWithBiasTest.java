/*
 * File:                LinearDiscriminantWithBiasTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Sep 30, 2011, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.learning.function.scalar;

import junit.framework.TestCase;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorFactory;
import java.util.Random;

/**
 * Tests for class LinearDiscriminantWithBiasTest.
 * @author krdixon
 */
public class LinearDiscriminantWithBiasTest
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
     * Default number of samples to test against, {@value}.
     */
    public final int NUM_SAMPLES = 1000;


    /**
     * Default Constructor
     */
    public LinearDiscriminantWithBiasTest(
        String testName )
    {
        super( testName );
    }

    /**
     * Tests the constructors of class LinearDiscriminantWithBiasTest.
     */
    public void testConstructors()
    {
        System.out.println( "Constructors" );
        LinearDiscriminantWithBias instance = new LinearDiscriminantWithBias();
        assertNull( instance.getWeightVector() );
        assertEquals( 0.0, instance.getBias() );

        int M = RANDOM.nextInt(5) + 1;
        Vector weight = VectorFactory.getDefault().createUniformRandom( M, -1, 1, RANDOM );
        instance = new LinearDiscriminantWithBias( weight );
        assertSame( weight, instance.getWeightVector() );
        assertEquals( 0.0, instance.getBias() );

        double bias = RANDOM.nextGaussian();
        instance = new LinearDiscriminantWithBias( weight, bias );
        assertSame( weight, instance.getWeightVector() );
        assertEquals( bias, instance.getBias() );

    }

    public LinearDiscriminantWithBias createInstance()
    {
        int M = RANDOM.nextInt(5) + 1;
        Vector weight = VectorFactory.getDefault().createUniformRandom( M, -1, 1, RANDOM );
        return new LinearDiscriminantWithBias( weight, RANDOM.nextGaussian() );
    }


    /**
     * Tests the clone method of class LinearDiscriminantWithBiasTest.
     */
    public void testClone()
    {
        System.out.println( "Clone" );

        LinearDiscriminantWithBias instance = this.createInstance();
        LinearDiscriminantWithBias clone = instance.clone();
        assertNotSame( instance, clone );
        assertNotSame( instance.getWeightVector(), clone.getWeightVector() );
        assertEquals( instance.convertToVector(), clone.convertToVector() );
    }

    /**
     * Test of getBias method, of class LinearDiscriminantWithBias.
     */
    public void testGetBias()
    {
        System.out.println("getBias");
        LinearDiscriminantWithBias instance = this.createInstance();
        double bias = RANDOM.nextGaussian();
        instance.setBias(bias);
        assertEquals( bias, instance.getBias() );
    }

    /**
     * Test of setBias method, of class LinearDiscriminantWithBias.
     */
    public void testSetBias()
    {
        System.out.println("setBias");
        LinearDiscriminantWithBias instance = this.createInstance();
        double bias = RANDOM.nextGaussian();
        instance.setBias(bias);
        assertEquals( bias, instance.getBias() );
    }

    /**
     * Test of evaluate method, of class LinearDiscriminantWithBias.
     */
    public void testEvaluate()
    {
        System.out.println("evaluate");
        LinearDiscriminantWithBias instance = this.createInstance();

        final int N = instance.getInputDimensionality();
        Vector x = VectorFactory.getDefault().createVector(N, 0.0);
        double result = instance.evaluate(x);
        assertEquals( instance.getBias(), result );

        x = VectorFactory.getDefault().createUniformRandom(N, -1.0, 1.0,RANDOM);
        result = instance.evaluate(x);
        double expected = x.dotProduct( instance.getWeightVector() ) + instance.getBias();
        assertEquals( expected, result );
    }

    /**
     * Test of convertToVector method, of class LinearDiscriminantWithBias.
     */
    public void testConvertToVector()
    {
        System.out.println("convertToVector");
        LinearDiscriminantWithBias instance = this.createInstance();
        Vector p = instance.convertToVector();
        assertEquals( instance.getInputDimensionality()+1, p.getDimensionality() );

        assertEquals( instance.getWeightVector(), p.subVector(0,instance.getInputDimensionality()-1) );
        assertEquals( instance.getBias(), p.getElement(instance.getInputDimensionality()) );
    }

    /**
     * Test of convertFromVector method, of class LinearDiscriminantWithBias.
     */
    public void testConvertFromVector()
    {
        System.out.println("convertFromVector");
        LinearDiscriminantWithBias instance = this.createInstance();
        Vector p = instance.convertToVector();
        double scale = RANDOM.nextGaussian();
        p.scaleEquals(scale);
        instance.convertFromVector(p);
        assertEquals( p, instance.convertToVector() );

        p = VectorFactory.getDefault().createVector(p.getDimensionality()+1);
        try
        {
            instance.convertFromVector(p);
            fail( "Wrong dim" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }

    }

}
