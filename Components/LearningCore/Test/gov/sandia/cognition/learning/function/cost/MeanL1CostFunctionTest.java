/*
 * File:                EuclideanDistanceCostFunctionTest.java
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

package gov.sandia.cognition.learning.function.cost;

import gov.sandia.cognition.evaluator.Evaluator;
import gov.sandia.cognition.learning.function.vector.LinearVectorFunction;
import gov.sandia.cognition.learning.data.WeightedInputOutputPair;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorFunction;
import java.util.Collection;
import java.util.LinkedList;

/**
 *
 * @author jdbasil
 */
public class MeanL1CostFunctionTest
    extends SupervisedCostFunctionTestHarness<Vector,Vector>
{
    
    /**
     * Constructor
     * @param testName test
     */
    public MeanL1CostFunctionTest(
        String testName)
    {
        super(testName);
    }

    public Collection<WeightedInputOutputPair<Vector,Vector>> createRandomCostParameters()
    {
        return this.createVectorCostParameters();
    }


    @Override
    public AbstractSupervisedCostFunction<Vector, Vector> createInstance()
    {
        return new MeanL1CostFunction( this.createRandomCostParameters() );
    }

    @Override
    public Evaluator<? super Vector, ? extends Vector> createEvaluator()
    {
        return this.createVectorFunction();
    }

    @Override
    public void testConstructors()
    {
        MeanL1CostFunction instance = new MeanL1CostFunction();
        assertNull(instance.getCostParameters());
        
        Collection<WeightedInputOutputPair<Vector,Vector>> dataset = this.createRandomCostParameters();
        instance = new MeanL1CostFunction(dataset);
        assertSame(dataset, instance.getCostParameters());
    }
        
    /**
     * Test of evaluate method, of class gov.sandia.isrc.learning.util.function.cost.MeanL1CostFunction.
     */
    public void testKnownValues()
    {
        System.out.println("evaluate");

        Collection<WeightedInputOutputPair<Vector,Vector>> dataset =
            this.createRandomCostParameters();
        
        VectorFunction f1 = new LinearVectorFunction( RANDOM.nextGaussian() );
        
        // This assumes that the evaluate() method for samples works properly
        double expected = 0.0;
        double denominator = 0.0;
        for( WeightedInputOutputPair<Vector,Vector> pair : dataset )
        {
            Vector estimate = f1.evaluate( pair.getInput() );
            expected += pair.getOutput().minus( estimate ).norm1() * pair.getWeight();
            denominator += pair.getWeight();
        }
            
        expected /= denominator;
        
        MeanL1CostFunction costFunction =
            new MeanL1CostFunction( dataset );
        
        assertEquals( expected, costFunction.evaluate( f1 ), TOLERANCE );
        
        // empty dataset should always evaluate to 0.0
        dataset = new LinkedList<WeightedInputOutputPair<Vector,Vector>>();
        costFunction = new MeanL1CostFunction( dataset );
        assertEquals( 0.0, costFunction.evaluate( f1 ) );
        
    }

}
