/*
 * File:                SumSquaredErrorCostFunctionTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Jul 4, 2008, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government. 
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */
package gov.sandia.cognition.learning.function.cost;

import gov.sandia.cognition.evaluator.Evaluator;
import gov.sandia.cognition.learning.data.DatasetUtil;
import gov.sandia.cognition.learning.data.InputOutputPair;
import gov.sandia.cognition.learning.data.TargetEstimatePair;
import gov.sandia.cognition.learning.data.WeightedTargetEstimatePair;
import gov.sandia.cognition.learning.function.vector.MatrixMultiplyVectorFunction;
import gov.sandia.cognition.math.RingAccumulator;
import gov.sandia.cognition.math.matrix.Matrix;
import gov.sandia.cognition.math.matrix.Vector;
import java.util.Collection;
import java.util.LinkedList;

/**
 * JUnit tests for class SumSquaredErrorCostFunctionTest
 * @author Kevin R. Dixon
 */
public class SumSquaredErrorCostFunctionTest
    extends SupervisedCostFunctionTestHarness<Vector,Vector>
{

    /**
     * Entry point for JUnit tests for class SumSquaredErrorCostFunctionTest
     * @param testName name of this test
     */
    public SumSquaredErrorCostFunctionTest(
        String testName)
    {
        super(testName);
    }


    @Override
    public SumSquaredErrorCostFunction createInstance()
    {
        return new SumSquaredErrorCostFunction( this.createRandomCostParameters() );
    }

    @Override
    public Collection<? extends InputOutputPair<Vector, Vector>> createRandomCostParameters()
    {
        return this.createVectorCostParameters();
    }

    @Override
    public MatrixMultiplyVectorFunction createEvaluator()
    {
        return this.createVectorFunction();
    }

    /**
     * Test of clone method, of class SumSquaredErrorCostFunction.
     */
    public void testConstructors()
    {
        System.out.println( "constructors" );
        
        SumSquaredErrorCostFunction sse = new SumSquaredErrorCostFunction();
        assertNull( sse.getCostParameters() );
        Collection<? extends InputOutputPair<Vector,Vector>> data = this.createRandomCostParameters();
        sse = new SumSquaredErrorCostFunction(data);
        assertNotNull(sse.getCostParameters());
        assertSame(data, sse.getCostParameters());
        
    }

    /**
     * Test of evaluatePerformance method, of class SumSquaredErrorCostFunction.
     */
    @Override
    public void testKnownValues()
    {
        System.out.println( "evaluatePerformance" );
        
        SumSquaredErrorCostFunction instance = this.createInstance();
        Evaluator<? super Vector, ? extends Vector> f = this.createEvaluator();
        Collection<TargetEstimatePair<Vector,Vector>> ted = this.createTargetEstimatePairs(instance, f);
        double actual = 0.0;
        double weightSum = 0.0;
        for( InputOutputPair<? extends Vector,Vector> sample : instance.getCostParameters() )
        {
            Vector input = sample.getInput();
            Vector output = sample.getOutput();
            Vector estimate = f.evaluate( input );
            double weight = DatasetUtil.getWeight(sample);
            actual += weight * output.minus( estimate ).norm2Squared();
            weightSum += weight;
            ted.add( new WeightedTargetEstimatePair<Vector, Vector>(
                output, estimate, weight) );
        }
        
        actual /= (weightSum*2.0);
        
        assertEquals( actual, instance.evaluatePerformance( ted ), TOLERANCE );
        assertEquals( instance.evaluate( f ), instance.evaluatePerformance( ted ), TOLERANCE );

        SumSquaredErrorCostFunction dummy = new SumSquaredErrorCostFunction(
            new LinkedList<InputOutputPair<Vector,Vector>>());
        assertEquals( 0.0, dummy.evaluate(f) );

    }

    /**
     * Test of computeParameterGradient method, of class SumSquaredErrorCostFunction.
     */
    public void testComputeParameterGradient()
    {
        System.out.println( "computeParameterGradient" );
        
        SumSquaredErrorCostFunction instance = this.createInstance();
        
        RingAccumulator<Vector> actual = new RingAccumulator<Vector>();
        double weightSum = 0.0;
        MatrixMultiplyVectorFunction fhat = this.createEvaluator();
        for( InputOutputPair<? extends Vector,Vector> sample : instance.getCostParameters() )
        {
            Vector input = sample.getInput();
            Vector output = sample.getOutput();
            Vector estimate = fhat.evaluate( input );
            double weight = DatasetUtil.getWeight(sample);
            weightSum += weight;
            Vector deriv = output.minus( estimate ).scale( weight );
            Matrix grad = fhat.computeParameterGradient( input );
            actual.accumulate( grad.transpose().times( deriv ) );
        }
        weightSum *= 2.0;
        
        assertEquals( actual.getSum().scale( -1.0/weightSum ), instance.computeParameterGradient( fhat ) );

        SumSquaredErrorCostFunction dummy = new SumSquaredErrorCostFunction(
            new LinkedList<InputOutputPair<Vector,Vector>>());
        try
        {
            dummy.computeParameterGradient(fhat);
            fail( "Should fail without data!" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }

    }
    
    /**
     * Test of computeParameterGradient method, of class SumSquaredErrorCostFunction.
     */
    public void testCache()
    {
        System.out.println( "cache" );

        SumSquaredErrorCostFunction instance = this.createInstance();
        MatrixMultiplyVectorFunction fhat = this.createEvaluator();
        SumSquaredErrorCostFunction.Cache cache = 
            SumSquaredErrorCostFunction.Cache.compute( fhat, instance.getCostParameters() );
        
        assertEquals( instance.evaluate( fhat ), cache.parameterCost, TOLERANCE );
        assertEquals( instance.computeParameterGradient( fhat ), cache.Jte );
        
    }

}
