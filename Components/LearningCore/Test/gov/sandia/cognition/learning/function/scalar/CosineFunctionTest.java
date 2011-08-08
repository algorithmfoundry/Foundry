/*
 * File:                CosineFunctionTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Jun 15, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.learning.function.scalar;

import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorFactory;

/**
 * Unit tests for CosineFunctionTest.
 *
 * @author krdixon
 */
public class CosineFunctionTest
    extends DifferentiableUnivariateScalarFunctionTestHarness
{

    /**
     * Tests for class CosineFunctionTest.
     * @param testName Name of the test.
     */
    public CosineFunctionTest(
        String testName)
    {
        super(testName);
        TOLERANCE = 1e-4;
    }

    @Override
    public CosineFunction createInstance()
    {
        return new CosineFunction( RANDOM.nextGaussian(), RANDOM.nextGaussian(), RANDOM.nextGaussian() );
    }

    /**
     * Tests the constructors of class CosineFunctionTest.
     */
    public void testConstructors()
    {
        System.out.println( "Constructors" );
        CosineFunction f = new CosineFunction();
        assertEquals( 1.0, f.getAmplitude() );
        assertEquals( 1.0, f.getFrequency() );
        assertEquals( 0.0, f.getPhase() );

        double amplitude = RANDOM.nextGaussian();
        double frequency = RANDOM.nextGaussian();
        f = new CosineFunction( amplitude, frequency );
        assertEquals( amplitude, f.getAmplitude() );
        assertEquals( frequency, f.getFrequency() );
        assertEquals( 0.0, f.getPhase() );

        double phase = RANDOM.nextGaussian();
        f = new CosineFunction( amplitude, frequency, phase );
        assertEquals( amplitude, f.getAmplitude() );
        assertEquals( frequency, f.getFrequency() );
        assertEquals( phase, f.getPhase() );

    }

    /**
     * Test of clone method, of class CosineFunction.
     */
    public void testClone()
    {
        System.out.println("clone");
        CosineFunction instance = new CosineFunction( RANDOM.nextGaussian(), RANDOM.nextGaussian(), RANDOM.nextGaussian() );
        CosineFunction clone = instance.clone();
        assertNotNull( clone );
        assertNotSame( instance, clone );
        assertEquals( instance.convertToVector(), clone.convertToVector() );
        assertEquals( instance.getAmplitude(), clone.getAmplitude() );
        assertEquals( instance.getFrequency(), clone.getFrequency() );
        assertEquals( instance.getPhase(), clone.getPhase() );

    }

    /**
     * Test of evaluate method, of class CosineFunction.
     */
    public void testEvaluate()
    {
        System.out.println("evaluate");
        CosineFunction instance = new CosineFunction( 3.0, 2.0, 1.0 );
        assertEquals( 1.620907, instance.evaluate( 4.0 ), TOLERANCE );
    }

    /**
     * Test of differentiate method, of class CosineFunction.
     */
    public void testDifferentiate()
    {
        System.out.println("differentiate");
        CosineFunction instance = new CosineFunction( 3.0, 2.0, 1.0 );
        assertEquals( -31.722709, instance.differentiate( 4.0 ), TOLERANCE );
    }

    /**
     * Test of getAmplitude method, of class CosineFunction.
     */
    public void testGetAmplitude()
    {
        System.out.println("getAmplitude");
        double amplitude = RANDOM.nextGaussian();
        double frequency = RANDOM.nextGaussian();
        double phase = RANDOM.nextGaussian();
        CosineFunction f = new CosineFunction( amplitude, frequency, phase );
        assertEquals( amplitude, f.getAmplitude() );
    }

    /**
     * Test of setAmplitude method, of class CosineFunction.
     */
    public void testSetAmplitude()
    {
        System.out.println("setAmplitude");
        double amplitude = RANDOM.nextGaussian();
        double frequency = RANDOM.nextGaussian();
        double phase = RANDOM.nextGaussian();
        CosineFunction f = new CosineFunction( amplitude, frequency, phase );
        assertEquals( amplitude, f.getAmplitude() );
        double v = amplitude + RANDOM.nextDouble();
        f.setAmplitude(v);
        assertEquals( v, f.getAmplitude() );
    }

    /**
     * Test of getFrequency method, of class CosineFunction.
     */
    public void testGetFrequency()
    {
        System.out.println("getFrequency");
        double amplitude = RANDOM.nextGaussian();
        double frequency = RANDOM.nextGaussian();
        double phase = RANDOM.nextGaussian();
        CosineFunction f = new CosineFunction( amplitude, frequency, phase );
        assertEquals( frequency, f.getFrequency() );

    }

    /**
     * Test of setFrequency method, of class CosineFunction.
     */
    public void testSetFrequency()
    {
        System.out.println("setFrequency");
        double amplitude = RANDOM.nextGaussian();
        double frequency = RANDOM.nextGaussian();
        double phase = RANDOM.nextGaussian();
        CosineFunction f = new CosineFunction( amplitude, frequency, phase );
        assertEquals( frequency, f.getFrequency() );
        double v = frequency + RANDOM.nextDouble();
        f.setFrequency(v);
        assertEquals( v, f.getFrequency() );

    }

    /**
     * Test of getPhase method, of class CosineFunction.
     */
    public void testGetPhase()
    {
        System.out.println("getPhase");
        double amplitude = RANDOM.nextGaussian();
        double frequency = RANDOM.nextGaussian();
        double phase = RANDOM.nextGaussian();
        CosineFunction f = new CosineFunction( amplitude, frequency, phase );
        assertEquals( phase, f.getPhase() );
    }

    /**
     * Test of setPhase method, of class CosineFunction.
     */
    public void testSetPhase()
    {
        System.out.println("setPhase");
        double amplitude = RANDOM.nextGaussian();
        double frequency = RANDOM.nextGaussian();
        double phase = RANDOM.nextGaussian();
        CosineFunction f = new CosineFunction( amplitude, frequency, phase );
        assertEquals( phase, f.getPhase() );
        double v = phase + RANDOM.nextDouble();
        f.setFrequency(v);
        assertEquals( v, f.getFrequency() );

    }

    /**
     * Test of convertToVector method, of class CosineFunction.
     */
    public void testConvertToVector()
    {
        System.out.println("convertToVector");
        double amplitude = RANDOM.nextGaussian();
        double frequency = RANDOM.nextGaussian();
        double phase = RANDOM.nextGaussian();
        CosineFunction f = new CosineFunction( amplitude, frequency, phase );
        Vector v = f.convertToVector();
        assertEquals( 3, v.getDimensionality() );
        assertEquals( amplitude, v.getElement(0) );
        assertEquals( frequency, v.getElement(1) );
        assertEquals( phase, v.getElement(2) );
    }

    /**
     * Test of convertFromVector method, of class CosineFunction.
     */
    public void testConvertFromVector()
    {
        System.out.println("convertFromVector");
        double amplitude = RANDOM.nextGaussian();
        double frequency = RANDOM.nextGaussian();
        double phase = RANDOM.nextGaussian();
        CosineFunction f = new CosineFunction( amplitude, frequency, phase );

        Vector v = VectorFactory.getDefault().createUniformRandom(3, -10.0, 10.0, RANDOM);
        f.convertFromVector(v);
        assertEquals( v.getElement(0), f.getAmplitude() );
        assertEquals( v.getElement(1), f.getFrequency() );
        assertEquals( v.getElement(2), f.getPhase() );
        Vector v2 = f.convertToVector();
        assertEquals( v, v2 );

    }


    /**
     * toString test
     */
    public void testToString()
    {

        System.out.println( "toString" );
        CosineFunction instance = new CosineFunction( 3.0, 2.0, 1.0 );
        String result = instance.toString();
        System.out.println( "Instance: " + result );
        assertNotNull( result );
        
    }


}
