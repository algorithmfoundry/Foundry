/*
 * File:                MatrixVectorMultiplyFunctionTest.java
 * Authors:             Kevin R. Dixon
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

package gov.sandia.cognition.learning.function.cost;

import gov.sandia.cognition.annotation.CodeReview;
import gov.sandia.cognition.learning.data.DatasetUtil;
import gov.sandia.cognition.learning.function.vector.MatrixMultiplyVectorFunction;
import gov.sandia.cognition.math.RingAccumulator;
import gov.sandia.cognition.math.matrix.Matrix;
import java.util.ArrayList;
import gov.sandia.cognition.learning.data.InputOutputPair;
import gov.sandia.cognition.learning.data.TargetEstimatePair;
import gov.sandia.cognition.math.matrix.Vector;
import java.util.Collection;

/**
 * This class implements JUnit tests for the following classes:
 *
 *     MeanSquaredErrorCostFunction
 *
 * @author Kevin R. Dixon
 * @since 1.0
 */
@CodeReview(
    reviewer="Justin Basilico",
    date="2006-10-06",
    changesNeeded=false,
    comments="Test class looks fine."
)
public class MeanSquaredErrorCostFunctionTest 
    extends SupervisedCostFunctionTestHarness<Vector,Vector>
{

    /**
     * Creates a new instance of MeanSquaredErrorCostFunctionTest.
     *
     * @param  testName The test name.
     */
    public MeanSquaredErrorCostFunctionTest(
        String testName)
    {
        super(testName);
    }


    @Override
    public MeanSquaredErrorCostFunction createInstance()
    {
        return new MeanSquaredErrorCostFunction( this.createRandomCostParameters() );
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
     * Constructors
     */
    @Override
    public void testConstructors()
    {
        MeanSquaredErrorCostFunction instance = new MeanSquaredErrorCostFunction();
        assertNull(instance.getCostParameters());        
        instance = new MeanSquaredErrorCostFunction(this.createRandomCostParameters());
        assertNotNull(instance.getCostParameters());
    }
    

    /**
     * Test of evaluate method, of class MeanSquaredErrorCostFunction.
     */
    @Override
    public void testKnownValues()
    {
        System.out.println("evaluate");

        MeanSquaredErrorCostFunction costFunction = this.createInstance();
        MatrixMultiplyVectorFunction estimateFunction = this.createEvaluator();
        double totalSquaredError = 0.0;
        double weightSum = 0.0;

        Collection<TargetEstimatePair<Vector,Vector>> tepairs =
            this.createTargetEstimatePairs(costFunction, estimateFunction);
        for( TargetEstimatePair<Vector,Vector> pair : tepairs )
        {
            Vector error = pair.getTarget().minus( pair.getEstimate() );
            double weight = DatasetUtil.getWeight(pair);
            totalSquaredError += weight * error.norm2Squared();
            weightSum += weight;
        }

        double expected = totalSquaredError / weightSum;
        
        double result = costFunction.evaluate( estimateFunction );
        assertEquals( expected, result, TOLERANCE );
    }


    /**
     * Test of differentiate method, of class MeanSquaredErrorCostFunction.
     */
    public void testDifferentiate()
    {
        System.out.println("differentiate");
        
        MatrixMultiplyVectorFunction targetFunction = this.createEvaluator();
        MatrixMultiplyVectorFunction estimateFunction = this.createEvaluator();
        
        ArrayList<InputOutputPair<Vector,Vector>> dataset = 
            new ArrayList<InputOutputPair<Vector,Vector>>();
        MeanSquaredErrorCostFunction dummy = 
            new MeanSquaredErrorCostFunction( dataset );
        assertNull( dummy.computeParameterGradient(targetFunction) );


        MeanSquaredErrorCostFunction costFunction = this.createInstance();

        RingAccumulator<Vector> totaler = new RingAccumulator<Vector>();
        double weightSum = 0.0;
        for( InputOutputPair<? extends Vector,Vector> pair : costFunction.getCostParameters() )
        {
            Vector input = pair.getInput();
            Vector estimate = estimateFunction.evaluate(input);
            double weight = DatasetUtil.getWeight(pair);
            weightSum += weight;
            Vector error = pair.getOutput().minus( estimate ).scale( weight );

            Matrix gradient = estimateFunction.computeParameterGradient( input );
            totaler.accumulate( gradient.transpose().times( error ) );
        }
        
        Vector expected = totaler.getSum().scale( -1.0 ).scale( 1.0/weightSum );
        Vector result = costFunction.computeParameterGradient( estimateFunction );
        
        assertEquals( expected, result );
    }

}
