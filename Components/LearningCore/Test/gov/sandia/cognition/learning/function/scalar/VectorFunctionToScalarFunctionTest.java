/*
 * File:                VectorFunctionToScalarFunctionTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Jul 6, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.learning.function.scalar;

import gov.sandia.cognition.evaluator.Evaluator;
import gov.sandia.cognition.learning.algorithm.regression.ParameterDerivativeFreeCostMinimizer;
import gov.sandia.cognition.learning.data.DefaultInputOutputPair;
import gov.sandia.cognition.learning.data.InputOutputPair;
import gov.sandia.cognition.learning.function.vector.ThreeLayerFeedforwardNeuralNetwork;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.math.matrix.Vectorizable;
import gov.sandia.cognition.math.matrix.mtj.Vector3;
import java.util.ArrayList;
import junit.framework.TestCase;
import java.util.Random;

/**
 * Unit tests for VectorFunctionToScalarFunctionTest.
 *
 * @author krdixon
 */
public class VectorFunctionToScalarFunctionTest
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
     * Tests for class VectorFunctionToScalarFunctionTest.
     * @param testName Name of the test.
     */
    public VectorFunctionToScalarFunctionTest(
        String testName)
    {
        super(testName);
    }

    public VectorFunctionToScalarFunction<Vector> createInstance()
    {
        return new VectorFunctionToScalarFunction<Vector>(
            new ThreeLayerFeedforwardNeuralNetwork() );
    }

    /**
     * Tests the constructors of class VectorFunctionToScalarFunctionTest.
     */
    public void testConstructors()
    {
        System.out.println( "Constructors" );
        VectorFunctionToScalarFunction<Vector> instance =
            new VectorFunctionToScalarFunction<Vector>();
        assertNotNull( instance );
        assertNull( instance.getVectorFunction() );

        ThreeLayerFeedforwardNeuralNetwork ann = new ThreeLayerFeedforwardNeuralNetwork();
        instance = new VectorFunctionToScalarFunction<Vector>( ann );
        assertNotNull( instance );
        assertSame( ann, instance.getVectorFunction() );

    }

    /**
     * Test of clone method, of class VectorFunctionToScalarFunction.
     */
    public void testClone()
    {
        System.out.println("clone");
        VectorFunctionToScalarFunction<?> instance = this.createInstance();
        VectorFunctionToScalarFunction<?> clone = instance.clone();
        assertNotNull( clone );
        assertNotSame( instance, clone );
        assertNotNull( clone.getVectorFunction() );
        assertNotSame( instance.getVectorFunction(), clone.getVectorFunction() );

    }

    /**
     * Test of evaluate method, of class VectorFunctionToScalarFunction.
     */
    public void testEvaluate()
    {
        System.out.println("evaluate");
        VectorFunctionToScalarFunction<Vector> instance = this.createInstance();

        Vector v = VectorFactory.getDefault().copyValues(RANDOM.nextGaussian());
        double result = instance.evaluate(v);
        double expected = instance.getVectorFunction().evaluate(v).convertToVector().getElement(0);
        assertEquals( expected, result );
    }

    /**
     * Test of getVectorFunction method, of class VectorFunctionToScalarFunction.
     */
    public void testGetVectorFunction()
    {
        System.out.println("getVectorFunction");
        VectorFunctionToScalarFunction<?> instance = this.createInstance();
        assertNotNull( instance.getVectorFunction() );
    }

    /**
     * Test of setVectorFunction method, of class VectorFunctionToScalarFunction.
     */
    public void testSetVectorFunction()
    {
        System.out.println("setVectorFunction");

        Evaluator<? super Vector, ? extends Vectorizable> vectorFunction =
            new ThreeLayerFeedforwardNeuralNetwork();
        VectorFunctionToScalarFunction<Vector> instance = this.createInstance();
        assertNotNull( instance.getVectorFunction() );

        assertNotSame( vectorFunction, instance.getVectorFunction() );
        instance.setVectorFunction(vectorFunction);
        assertSame( vectorFunction, instance.getVectorFunction() );
    }

    public void testLearner()
    {
        System.out.println( "Learner" );

        final int num = 100;
        ArrayList<InputOutputPair<Vector,Double>> data =
            new ArrayList<InputOutputPair<Vector, Double>>( num );
        for( int n = 0; n < num; n++ )
        {
            data.add( new DefaultInputOutputPair<Vector, Double>(
                Vector3.createRandom(RANDOM), RANDOM.nextGaussian() ) );
        }

        ParameterDerivativeFreeCostMinimizer learner =
            new ParameterDerivativeFreeCostMinimizer();
        learner.setObjectToOptimize( new ThreeLayerFeedforwardNeuralNetwork(3,2,1) );
        VectorFunctionToScalarFunction.Learner<Vector> v2s =
            new VectorFunctionToScalarFunction.Learner<Vector>();
        v2s.vectorLearner = learner;

        VectorFunctionToScalarFunction<Vector> f = v2s.learn(data);
        assertNotNull( f );
        assertNotNull( f.getVectorFunction() );
        
    }

}
