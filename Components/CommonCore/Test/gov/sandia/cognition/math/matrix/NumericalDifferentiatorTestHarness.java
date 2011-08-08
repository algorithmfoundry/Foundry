/*
 * File:                NumericalDifferentiatorTestHarness.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Jul 1, 2008, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government. 
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */
package gov.sandia.cognition.math.matrix;

import gov.sandia.cognition.evaluator.Evaluator;
import gov.sandia.cognition.math.DifferentiableEvaluator;
import java.util.Random;
import junit.framework.TestCase;

/**
 * JUnit tests for class NumericalDifferentiatorTestHarness
 * @author Kevin R. Dixon
 */
public abstract class NumericalDifferentiatorTestHarness<InputType,OutputType,DerivativeType>
    extends TestCase
{

    public final Random random = new Random(0);

    public final double TOLERANCE = 1e-3;
    
    /**
     * Entry point for JUnit tests for class NumericalDifferentiatorTestHarness
     * @param testName name of this test
     */
    public NumericalDifferentiatorTestHarness(
        String testName)
    {
        super(testName);
    }

    public abstract NumericalDifferentiator<InputType,OutputType,DerivativeType> createInstance();

    public abstract void testConstructors();

    /**
     * Test of clone method, of class NumericalDifferentiator.
     */
    public void testClone()
    {
        System.out.println( "clone" );
        NumericalDifferentiator<InputType, OutputType, DerivativeType> instance = this.createInstance();
        NumericalDifferentiator<InputType, OutputType, DerivativeType> clone = instance.clone();
        assertNotNull( clone );
        assertNotSame( instance, clone );
        assertEquals( instance.getDelta(), clone.getDelta() );
        assertNotSame( instance.getInternalFunction(), clone.getInternalFunction() );
    }

    /**
     * Test of getInternalFunction method, of class NumericalDifferentiator.
     */
    public void testGetInternalFunction()
    {
        System.out.println( "getInternalFunction" );
        NumericalDifferentiator<InputType,OutputType,DerivativeType> instance = this.createInstance();
        assertNotNull( instance.getInternalFunction() );
    }

    /**
     * Test of setInternalFunction method, of class NumericalDifferentiator.
     */
    public void testSetInternalFunction()
    {
        System.out.println( "setInternalFunction" );
        NumericalDifferentiator<InputType,OutputType,DerivativeType> instance = this.createInstance();
        Evaluator<InputType, OutputType> internalFunction = instance.getInternalFunction();
        assertNotNull( internalFunction );
        instance.setInternalFunction( null );
        assertNull( instance.getInternalFunction() );
        instance.setInternalFunction( internalFunction );
        assertSame( internalFunction, instance.getInternalFunction() );
    }

    /**
     * Test of getDelta method, of class NumericalDifferentiator.
     */
    public void testGetDelta()
    {
        System.out.println( "getDelta" );
        NumericalDifferentiator<InputType,OutputType,DerivativeType> instance = this.createInstance();
        double delta = instance.getDelta();
        assertTrue( delta > 0.0 );        
    }

    /**
     * Test of setDelta method, of class NumericalDifferentiator.
     */
    public void testSetDelta()
    {
        System.out.println( "setDelta" );
        NumericalDifferentiator<InputType,OutputType,DerivativeType> instance = this.createInstance();
        double delta = instance.getDelta();
        assertTrue( delta > 0.0 );
        double d2 = 2.0*delta;
        instance.setDelta( d2 );
        assertEquals( d2, instance.getDelta() );
        
        try
        {
            instance.setDelta( 0.0 );
            fail( "Delta must be > 0.0" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }
    }
    
    public abstract InputType createRandomInput();

    public abstract void assertDerivativeEquals(
        DerivativeType target, DerivativeType estimate );
    
    public void testEvaluate()
    {
        System.out.println( "evaluate" );
        int num = 100;
        for( int n = 0; n < num; n++ )
        {        
            NumericalDifferentiator<InputType,OutputType,DerivativeType> instance = this.createInstance();
            InputType input = this.createRandomInput();
            OutputType output = instance.getInternalFunction().evaluate( input );
            OutputType estimate = instance.evaluate( input );
            assertEquals( output, estimate );
        }
        
    }
    
    public void testDifferentiate()
    {
        System.out.println( "differentiate" );
        int num = 100;
        for( int n = 0; n < num; n++ )
        {
            NumericalDifferentiator<InputType,OutputType,DerivativeType> instance = this.createInstance();
            InputType input = this.createRandomInput();
            @SuppressWarnings("unchecked")
            DerivativeType output = ((DifferentiableEvaluator<InputType,OutputType,DerivativeType>) instance.getInternalFunction()).differentiate( input );
            DerivativeType estimate = instance.differentiate( input );
            assertDerivativeEquals( output, estimate );
        }
    }
    
}
