/*
 * File:                SupervisedCostFunctionTestHarness.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Jul 8, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.learning.function.cost;

import gov.sandia.cognition.evaluator.Evaluator;
import gov.sandia.cognition.learning.data.DatasetUtil;
import gov.sandia.cognition.learning.data.DefaultWeightedInputOutputPair;
import gov.sandia.cognition.learning.data.DefaultWeightedTargetEstimatePair;
import gov.sandia.cognition.learning.data.InputOutputPair;
import gov.sandia.cognition.learning.data.TargetEstimatePair;
import gov.sandia.cognition.learning.data.WeightedInputOutputPair;
import gov.sandia.cognition.learning.function.vector.MultivariateDiscriminant;
import gov.sandia.cognition.math.matrix.MatrixFactory;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorFunction;
import gov.sandia.cognition.math.matrix.mtj.Vector3;
import java.util.ArrayList;
import java.util.Collection;
import junit.framework.TestCase;
import java.util.Random;

/**
 * Unit tests for SupervisedCostFunctionTestHarness.
 *
 * @param <InputType> Input type
 * @param <TargetType> Target type
 * @author krdixon
 */
public abstract class SupervisedCostFunctionTestHarness<InputType,TargetType>
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
     * Number of samples to generate
     */
    public static final int NUM_SAMPLES = 10;

    /**
     * Tests for class SupervisedCostFunctionTestHarness.
     * @param testName Name of the test.
     */
    public SupervisedCostFunctionTestHarness(
        String testName)
    {
        super(testName);
    }

    /**
     * Creates cost function
     * @return cost function
     */
    public abstract AbstractSupervisedCostFunction<InputType,TargetType> createInstance();

    /**
     * creates cost parameters
     * @return cost parameters
     */
    public abstract Collection<? extends InputOutputPair<InputType,TargetType>> createRandomCostParameters();

    /**
     * create evaluator
     * @return evaluator
     */
    public abstract Evaluator<? super InputType, ? extends TargetType> createEvaluator();

    /**
     * Tests against known values.
     */
    public abstract void testKnownValues();

    /**
     * Tests the constructors of class SupervisedCostFunctionTestHarness.
     */
    public abstract void testConstructors();

    /**
     * Returns VectorFunction
     * @return VectorFunction
     */
    protected MultivariateDiscriminant createVectorFunction()
    {
        return new MultivariateDiscriminant(
            MatrixFactory.getDefault().createUniformRandom(3, 3, -1.0, 1.0, RANDOM) );
    }

    /**
     * Creates vector data
     * @return vector data
     */
    protected ArrayList<WeightedInputOutputPair<Vector,Vector>> createVectorCostParameters()
    {
        ArrayList<WeightedInputOutputPair<Vector,Vector>> dataset =
            new ArrayList<WeightedInputOutputPair<Vector,Vector>>( NUM_SAMPLES );
        VectorFunction f = this.createVectorFunction();
        for( int i = 0; i < NUM_SAMPLES; i++ )
        {
            Vector input = Vector3.createRandom(RANDOM);
            Vector output = f.evaluate(input);
            double weight = RANDOM.nextDouble();
            WeightedInputOutputPair<Vector,Vector> pair =
                new DefaultWeightedInputOutputPair<Vector,Vector>( input, output, weight );
            dataset.add( pair );
        }
        return dataset;
    }

    /**
     * Creates target-estimate pairs from the stuff
     * @param costFunction cost function
     * @param f evaluator
     * @return target estimate pairs
     */
    public ArrayList<TargetEstimatePair<TargetType, TargetType>> createTargetEstimatePairs(
        AbstractSupervisedCostFunction<InputType,TargetType> costFunction,
        Evaluator<? super InputType, ? extends TargetType> f )
    {

        Collection<? extends InputOutputPair<? extends InputType, TargetType>> data =
            costFunction.getCostParameters();
        ArrayList<TargetEstimatePair<TargetType, TargetType>> tedata =
            new ArrayList<TargetEstimatePair<TargetType, TargetType>>( data.size() );
        for( InputOutputPair<? extends InputType,TargetType> pair : data )
        {
            TargetType estimate = f.evaluate( pair.getInput() );
            tedata.add(DefaultWeightedTargetEstimatePair.create(
                pair.getOutput(), estimate, DatasetUtil.getWeight(pair) ) );
        }

        return tedata;

    }

    /**
     * Tests the abstract functions
     */
    public void testAbstractMethods()
    {
        System.out.println( "Abstract methods" );
        AbstractSupervisedCostFunction<InputType,TargetType> instance =
            this.createInstance();
        assertNotNull( instance );
        assertNotNull( instance.getCostParameters() );

        Collection<? extends InputOutputPair<InputType,TargetType>> data =
            this.createRandomCostParameters();
        assertNotNull( data );
        assertTrue( data.size() > 0 );

        assertNotNull( this.createEvaluator() );

    }

    /**
     * Test of clone method, of class AbstractSupervisedCostFunction.
     */
    public void testClone()
    {
        System.out.println("clone");
        AbstractSupervisedCostFunction<InputType,TargetType> instance =
            this.createInstance();
        instance.setCostParameters( this.createRandomCostParameters() );
        AbstractSupervisedCostFunction<InputType,TargetType> clone =
            instance.clone();
        assertNotNull( clone );
        assertNotSame( instance, clone );
        assertNotNull( clone.getCostParameters() );
        assertNotSame( instance.getCostParameters(), clone.getCostParameters() );
    }

    /**
     * Test of evaluatePerformance method, of class AbstractSupervisedCostFunction.
     */
    public void testEvaluatePerformance()
    {
        System.out.println("evaluatePerformance");
        Collection<? extends InputOutputPair<InputType,TargetType>> data =
            this.createRandomCostParameters();
        AbstractSupervisedCostFunction<InputType,TargetType> instance =
            this.createInstance();
        instance.setCostParameters(data);

        Evaluator<? super InputType, ? extends TargetType> f = this.createEvaluator();
        ArrayList<TargetEstimatePair<TargetType, TargetType>> tedata =
            createTargetEstimatePairs(instance, f);
        assertEquals( instance.evaluate(f), instance.evaluatePerformance(tedata), TOLERANCE );
        assertEquals( instance.summarize(tedata), instance.evaluatePerformance(tedata) );

        // No data should always be 0.0
        tedata.clear();
        assertEquals( 0.0, instance.evaluatePerformance(tedata) );

    }

    /**
     * Test of evaluate method, of class AbstractSupervisedCostFunction.
     */
    public void testEvaluate()
    {
        System.out.println("evaluate");
        Evaluator<? super InputType, ? extends TargetType> evaluator =
            this.createEvaluator();
        AbstractSupervisedCostFunction<InputType,TargetType> instance =
            this.createInstance();
        instance.setCostParameters( this.createRandomCostParameters() );
        ArrayList<TargetEstimatePair<TargetType, TargetType>> tedata =
            createTargetEstimatePairs(instance, evaluator);

        assertEquals( instance.evaluatePerformance(tedata), instance.evaluate(evaluator), TOLERANCE );
        assertEquals( instance.summarize(tedata), instance.evaluate(evaluator), TOLERANCE );

        // No data should always be 0.0
        instance.getCostParameters().clear();
        assertEquals( 0.0, instance.evaluate(evaluator) );
    }

    /**
     * Test of getCostParameters method, of class AbstractSupervisedCostFunction.
     */
    public void testGetCostParameters()
    {
        System.out.println("getCostParameters");
        AbstractSupervisedCostFunction<InputType,TargetType> instance = this.createInstance();
        instance.setCostParameters(this.createRandomCostParameters());
        assertNotNull( instance.getCostParameters() );
    }

    /**
     * Test of setCostParameters method, of class AbstractSupervisedCostFunction.
     */
    public void testSetCostParameters()
    {
        System.out.println("setCostParameters");
        Collection<? extends InputOutputPair<? extends InputType, TargetType>> costParameters =
            this.createRandomCostParameters();
        AbstractSupervisedCostFunction<InputType,TargetType> instance = this.createInstance();
        instance.setCostParameters(costParameters);
        assertSame( costParameters, instance.getCostParameters() );
    }

    /**
     * Test of summarize method, of class AbstractSupervisedCostFunction.
     */
    public void testSummarize()
    {
        System.out.println("summarize");
        Evaluator<? super InputType, ? extends TargetType> evaluator =
            this.createEvaluator();
        AbstractSupervisedCostFunction<InputType,TargetType> instance =
            this.createInstance();
        instance.setCostParameters( this.createRandomCostParameters() );
        ArrayList<TargetEstimatePair<TargetType, TargetType>> tedata =
            createTargetEstimatePairs(instance, evaluator);

        assertEquals( instance.summarize(tedata), instance.evaluatePerformance(tedata) );
        assertEquals( instance.summarize(tedata), instance.evaluate(evaluator), TOLERANCE );

        tedata.clear();
        assertEquals( 0.0, instance.summarize(tedata) );
    }

}
