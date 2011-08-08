/*
 * File:                ThreeLayerFeedforwardNeuralNetworkTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Apr 16, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.learning.function.vector;

import gov.sandia.cognition.learning.algorithm.gradient.GradientDescendableApproximator;
import gov.sandia.cognition.learning.function.scalar.AtanFunction;
import gov.sandia.cognition.learning.function.scalar.SigmoidFunction;
import gov.sandia.cognition.math.DifferentiableUnivariateScalarFunction;
import gov.sandia.cognition.math.matrix.Matrix;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorFactory;
import java.util.Random;
import junit.framework.TestCase;

/**
 * Unit tests for {@name}.
 *
 * @author krdixon
 */
public class ThreeLayerFeedforwardNeuralNetworkTest
extends TestCase
{

    public Random random = new Random( 1 );

    public static final double EPS = 1e-5;

    public ThreeLayerFeedforwardNeuralNetworkTest(
        String testName)
    {
        super(testName);
    }

    public void testConstructors()
    {
        System.out.println( "constructors" );
        ThreeLayerFeedforwardNeuralNetwork ann = new ThreeLayerFeedforwardNeuralNetwork();
        assertEquals( ThreeLayerFeedforwardNeuralNetwork.DEFAULT_INITIALIZATION_RANGE, ann.getInitializationRange() );
        assertSame( ThreeLayerFeedforwardNeuralNetwork.DEFAULT_SQUASHING_FUNCTION, ann.getSquashingFunction() );
        assertEquals( 1, ann.getInputDimensionality() );
        assertEquals( 1, ann.getHiddenDimensionality() );
        assertEquals( 1, ann.getOutputDimensionality() );
        assertEquals( 4, ann.convertToVector().getDimensionality() );

        int numInputs = random.nextInt( 10 ) + 1;
        int numHidden = random.nextInt( 10 ) + 1;
        int numOutput = random.nextInt( 10 ) + 1;
        double range = random.nextDouble();
        int seed = random.nextInt();
        DifferentiableUnivariateScalarFunction f = new SigmoidFunction();
        ann = new ThreeLayerFeedforwardNeuralNetwork(
            numInputs, numHidden, numOutput, f, seed, range );
        assertEquals( numInputs, ann.getInputDimensionality() );
        assertEquals( numHidden, ann.getHiddenDimensionality() );
        assertEquals( numOutput, ann.getOutputDimensionality() );
        assertSame( f, ann.getSquashingFunction() );
        assertEquals( range, ann.getInitializationRange() );

    }

    /**
     * Test of clone method, of class ThreeLayerFeedforwardNeuralNetwork.
     */
    public void testClone()
    {
        System.out.println("clone");


        int numInputs = random.nextInt( 10 ) + 1;
        int numHidden = random.nextInt( 10 ) + 1;
        int numOutput = random.nextInt( 10 ) + 1;
        ThreeLayerFeedforwardNeuralNetwork instance = new ThreeLayerFeedforwardNeuralNetwork(
            numInputs, numHidden, numOutput );
        ThreeLayerFeedforwardNeuralNetwork clone = instance.clone();
        assertNotSame( instance, clone );
        assertNotSame( instance.getSquashingFunction(), clone.getSquashingFunction() );
        
        Vector p1 = instance.convertToVector();
        Vector p2 = clone.convertToVector();
        assertEquals( p1, p2 );

        Vector p3 = p2.scale(random.nextGaussian());
        clone.convertFromVector(p3);
        Vector p4 = clone.convertToVector();
        assertEquals( p3, p4 );

        // Make sure fiddling with the clone's parameters didn't duff up
        // the original instance's parameters.
        Vector p5 = instance.convertToVector();
        assertEquals( p1, p5 );

    }

    /**
     * Test of computeParameterGradient method, of class ThreeLayerFeedforwardNeuralNetwork.
     */
    public void testComputeParameterGradient()
    {
        System.out.println("computeParameterGradient");
        Vector input = VectorFactory.getDefault().copyValues(1.0,2.0);
        ThreeLayerFeedforwardNeuralNetwork instance = new ThreeLayerFeedforwardNeuralNetwork();
        instance.setRandom(random);
        instance.setInitializationRange(10.0);
        instance.initializeWeights(2, 2, 2);

        Matrix result = instance.computeParameterGradient(input);
        System.out.println( "Result:\n" + result );
        GradientDescendableApproximator approx = new GradientDescendableApproximator(instance);
        Matrix estimate = approx.computeParameterGradient(input);
        System.out.println( "Estimte:\n" + estimate );

        System.out.println( "Difference:\n" + estimate.minus(result) );

        if( !estimate.equals(result,EPS) )
        {
            assertEquals(estimate,result);
        }

    }

    /**
     * Test of convertToVector method, of class ThreeLayerFeedforwardNeuralNetwork.
     */
    public void testConvertToVector()
    {
        System.out.println("convertToVector");
        int numInputs = random.nextInt( 10 ) + 1;
        int numHidden = random.nextInt( 10 ) + 1;
        int numOutput = random.nextInt( 10 ) + 1;
        ThreeLayerFeedforwardNeuralNetwork ann =
            new ThreeLayerFeedforwardNeuralNetwork( numInputs, numHidden, numOutput );

        Vector p = ann.convertToVector();
        assertEquals( numInputs*numHidden + numHidden + numHidden*numOutput + numOutput,
            p.getDimensionality() );

        // Make sure the values aren't all zeros
        boolean nonzero = false;
        for( int i = 0; i < p.getDimensionality(); i++ )
        {
            if( p.getElement(i) != 0.0 )
            {
                nonzero = true;
                break;
            }
        }
        assertTrue( nonzero );

    }

    /**
     * Test of convertFromVector method, of class ThreeLayerFeedforwardNeuralNetwork.
     */
    public void testConvertFromVector()
    {
        System.out.println("convertFromVector");
        int numInputs = random.nextInt( 10 ) + 1;
        int numHidden = random.nextInt( 10 ) + 1;
        int numOutput = random.nextInt( 10 ) + 1;
        ThreeLayerFeedforwardNeuralNetwork ann =
            new ThreeLayerFeedforwardNeuralNetwork( numInputs, numHidden, numOutput );

        Vector p1 = ann.convertToVector();

        Vector p2 = p1.scale( random.nextGaussian() );
        ann.convertFromVector(p2);
        Vector p3 = ann.convertToVector();
        assertEquals( p2, p3 );
    }

    /**
     * Test of evaluate method, of class ThreeLayerFeedforwardNeuralNetwork.
     */
    public void testEvaluate()
    {
        System.out.println("evaluate");
        int numInputs = 1;
        int numHidden = 2;
        int numOutput = 1;
        ThreeLayerFeedforwardNeuralNetwork ann =
            new ThreeLayerFeedforwardNeuralNetwork( numInputs, numHidden, numOutput );

        Vector p = VectorFactory.getDefault().copyValues( 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0 );
        ann.convertFromVector(p);

        // I found this by manually computing the output in octave...
        Vector x1 = VectorFactory.getDefault().copyValues(2.0);
        Vector y1 = ann.evaluate(x1);
        System.out.println( "Y1: " + y1 );
        assertEquals( 1, y1.getDimensionality() );
        assertEquals( 22.545652, y1.getElement(0), EPS );

    }

    /**
     * Test of initializeWeights method, of class ThreeLayerFeedforwardNeuralNetwork.
     */
    public void testInitializeWeights()
    {
        System.out.println("initializeWeights");
        ThreeLayerFeedforwardNeuralNetwork instance = new ThreeLayerFeedforwardNeuralNetwork();
        int numInputs = random.nextInt( 10 ) + 1;
        int numHidden = random.nextInt( 10 ) + 1;
        int numOutput = random.nextInt( 10 ) + 1;
        instance.initializeWeights(numInputs, numHidden, numOutput);
        assertEquals( numInputs, instance.getInputDimensionality() );
        assertEquals( numHidden, instance.getHiddenDimensionality() );
        assertEquals( numOutput, instance.getOutputDimensionality() );

        try
        {
            instance.initializeWeights(0, numHidden, numOutput );
            fail( "numInputs must be >= 1" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }
        try
        {
            instance.initializeWeights(numInputs, 0, numOutput );
            fail( "numHidden must be >= 1" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }
        try
        {
            instance.initializeWeights(numInputs, numHidden, 0 );
            fail( "numOutput must be >= 1" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }

    }

    /**
     * Test of getOutputDimensionality method, of class ThreeLayerFeedforwardNeuralNetwork.
     */
    public void testGetOutputDimensionality()
    {
        System.out.println("getOutputDimensionality");
        int numInputs = random.nextInt( 10 ) + 1;
        int numHidden = random.nextInt( 10 ) + 1;
        int numOutput = random.nextInt( 10 ) + 1;
        ThreeLayerFeedforwardNeuralNetwork instance = new ThreeLayerFeedforwardNeuralNetwork(
            numInputs, numHidden, numOutput );

        assertEquals( numOutput, instance.getOutputDimensionality() );

    }

    /**
     * Test of setOutputDimensionality method, of class ThreeLayerFeedforwardNeuralNetwork.
     */
    public void testSetOutputDimensionality()
    {
        System.out.println("setOutputDimensionality");
        int numInputs = random.nextInt( 10 ) + 1;
        int numHidden = random.nextInt( 10 ) + 1;
        int numOutput = random.nextInt( 10 ) + 1;
        ThreeLayerFeedforwardNeuralNetwork instance = new ThreeLayerFeedforwardNeuralNetwork(
            numInputs, numHidden, numOutput );
        assertEquals( numOutput, instance.getOutputDimensionality() );
        Vector p1 = instance.convertToVector();

        // This should reset the parameters
        instance.setOutputDimensionality(numOutput);
        Vector p2 = instance.convertToVector();
        assertEquals( p1.getDimensionality(), p2.getDimensionality() );
        assertFalse( p1.equals( p2 ) );

        int n2 = numOutput+1;
        instance.setOutputDimensionality(n2);
        assertEquals( n2, instance.getOutputDimensionality() );
        Vector p3 = instance.convertToVector();
        // Adding another output means an addition connection from each
        // of the hidden units PLUS the bias term
        assertEquals( p2.getDimensionality() + numHidden + 1, p3.getDimensionality() );
        try
        {
            instance.setOutputDimensionality(0);
            fail( "Dimensionality must be > 0" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }

    }

    /**
     * Test of getHiddenDimensionality method, of class ThreeLayerFeedforwardNeuralNetwork.
     */
    public void testGetHiddenDimensionality()
    {
        System.out.println("getHiddenDimensionality");
        int numInputs = random.nextInt( 10 ) + 1;
        int numHidden = random.nextInt( 10 ) + 1;
        int numOutput = random.nextInt( 10 ) + 1;
        ThreeLayerFeedforwardNeuralNetwork instance = new ThreeLayerFeedforwardNeuralNetwork(
            numInputs, numHidden, numOutput );
        assertEquals( numHidden, instance.getHiddenDimensionality() );
    }

    /**
     * Test of setHiddenDimensionality method, of class ThreeLayerFeedforwardNeuralNetwork.
     */
    public void testSetHiddenDimensionality()
    {
        System.out.println("setHiddenDimensionality");
        int numInputs = random.nextInt( 10 ) + 1;
        int numHidden = random.nextInt( 10 ) + 1;
        int numOutput = random.nextInt( 10 ) + 1;
        ThreeLayerFeedforwardNeuralNetwork instance = new ThreeLayerFeedforwardNeuralNetwork(
            numInputs, numHidden, numOutput );
        assertEquals( numHidden, instance.getHiddenDimensionality() );
        Vector p1 = instance.convertToVector();

        // This should reset the parameters
        instance.setHiddenDimensionality(numHidden);
        Vector p2 = instance.convertToVector();
        assertEquals( p1.getDimensionality(), p2.getDimensionality() );
        assertFalse( p1.equals( p2 ) );

        int n2 = numHidden+1;
        instance.setHiddenDimensionality(n2);
        assertEquals( n2, instance.getHiddenDimensionality() );
        Vector p3 = instance.convertToVector();
        assertEquals( p2.getDimensionality() + numInputs + 1 + numOutput, p3.getDimensionality() );
        try
        {
            instance.setHiddenDimensionality(0);
            fail( "Dimensionality must be > 0" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }
    }

    /**
     * Test of getInputDimensionality method, of class ThreeLayerFeedforwardNeuralNetwork.
     */
    public void testGetInputDimensionality()
    {
        System.out.println("getInputDimensionality");
        int numInputs = random.nextInt( 10 ) + 1;
        int numHidden = random.nextInt( 10 ) + 1;
        int numOutput = random.nextInt( 10 ) + 1;
        ThreeLayerFeedforwardNeuralNetwork instance = new ThreeLayerFeedforwardNeuralNetwork(
            numInputs, numHidden, numOutput );
        assertEquals( numInputs, instance.getInputDimensionality() );
    }

    /**
     * Test of setInputDimensionality method, of class ThreeLayerFeedforwardNeuralNetwork.
     */
    public void testSetInputDimensionality()
    {
        System.out.println("setInputDimensionality");
        int numInputs = random.nextInt( 10 ) + 1;
        int numHidden = random.nextInt( 10 ) + 1;
        int numOutput = random.nextInt( 10 ) + 1;
        ThreeLayerFeedforwardNeuralNetwork instance = new ThreeLayerFeedforwardNeuralNetwork(
            numInputs, numHidden, numOutput );
        assertEquals( numInputs, instance.getInputDimensionality() );
        Vector p1 = instance.convertToVector();

        // This should reset the parameters
        instance.setInputDimensionality(numInputs);
        Vector p2 = instance.convertToVector();
        assertEquals( p1.getDimensionality(), p2.getDimensionality() );
        assertFalse( p1.equals( p2 ) );

        int n2 = numInputs+1;
        instance.setInputDimensionality(n2);
        assertEquals( n2, instance.getInputDimensionality() );
        Vector p3 = instance.convertToVector();
        assertEquals( p2.getDimensionality() + numHidden, p3.getDimensionality() );

        try
        {
            instance.setInputDimensionality(0);
            fail( "Dimensionality must be > 0" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }
    }

    /**
     * Test of getSquashingFunction method, of class ThreeLayerFeedforwardNeuralNetwork.
     */
    public void testGetSquashingFunction()
    {
        System.out.println("getSquashingFunction");
        ThreeLayerFeedforwardNeuralNetwork instance = new ThreeLayerFeedforwardNeuralNetwork();
        assertSame( ThreeLayerFeedforwardNeuralNetwork.DEFAULT_SQUASHING_FUNCTION, instance.getSquashingFunction() );
    }

    /**
     * Test of setSquashingFunction method, of class ThreeLayerFeedforwardNeuralNetwork.
     */
    public void testSetSquashingFunction()
    {
        System.out.println("setSquashingFunction");
        DifferentiableUnivariateScalarFunction squashingFunction =
            new AtanFunction( random.nextDouble() );
        ThreeLayerFeedforwardNeuralNetwork instance = new ThreeLayerFeedforwardNeuralNetwork();
        assertSame( ThreeLayerFeedforwardNeuralNetwork.DEFAULT_SQUASHING_FUNCTION, instance.getSquashingFunction() );
        instance.setSquashingFunction(squashingFunction);
        assertSame( squashingFunction, instance.getSquashingFunction() );
    }

    /**
     * Test of getInitializationRange method, of class ThreeLayerFeedforwardNeuralNetwork.
     */
    public void testGetInitializationRange()
    {
        System.out.println("getInitializationRange");
        ThreeLayerFeedforwardNeuralNetwork instance = new ThreeLayerFeedforwardNeuralNetwork();
        assertEquals( ThreeLayerFeedforwardNeuralNetwork.DEFAULT_INITIALIZATION_RANGE, instance.getInitializationRange() );
    }

    /**
     * Test of setInitializationRange method, of class ThreeLayerFeedforwardNeuralNetwork.
     */
    public void testSetInitializationRange()
    {
        System.out.println("setInitializationRange");
        double initializationRange = random.nextDouble();
        ThreeLayerFeedforwardNeuralNetwork instance = new ThreeLayerFeedforwardNeuralNetwork();
        assertEquals( ThreeLayerFeedforwardNeuralNetwork.DEFAULT_INITIALIZATION_RANGE, instance.getInitializationRange() );
        instance.setInitializationRange(initializationRange);
        assertEquals( initializationRange, instance.getInitializationRange() );

        instance.setInitializationRange(0.0);
        assertEquals( 0.0, instance.getInitializationRange() );

        try
        {
            instance.setInitializationRange(-1.0 );
            fail( "Range must be >= 0.0" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }
    }

}
