/*
 * File:                WeightedMeanLearnerTest.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright April 21, 2008, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 * See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.learning.algorithm.baseline;

import gov.sandia.cognition.learning.data.DefaultWeightedInputOutputPair;
import gov.sandia.cognition.learning.function.ConstantEvaluator;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.util.CloneableSerializable;
import java.util.ArrayList;
import junit.framework.TestCase;

/**
 * Tests of WeightedMeanLearner
 * @author  Justin Basilico
 * @since   2.1
 */
public class WeightedMeanLearnerTest
    extends TestCase
{
    /**
     * Creates a new test.
     *
     * @param   testName The test name.
     */
    public WeightedMeanLearnerTest(
        String testName)
    {
        super(testName);
    }

    /**
     * Tests the constructors of class WeightedMeanLearner.
     */
    public void testConstructor()
    {
        WeightedMeanLearner instance = new WeightedMeanLearner();
        assertNotNull(instance);
    }

    /**
     * Test of clone
     */
    public void testClone()
    {
        System.out.println( "Clone" );
        WeightedMeanLearner instance = new WeightedMeanLearner();

        CloneableSerializable clone = instance.clone();
        assertNotNull( clone );
        assertNotSame( instance, clone );
    }

    /**
     * Test of learn method, of class WeightedMeanLearner.
     */
    public void testLearn()
    {
        WeightedMeanLearner instance = new WeightedMeanLearner();
        
        ArrayList<DefaultWeightedInputOutputPair<Vector, Double>> data =
            new ArrayList<DefaultWeightedInputOutputPair<Vector, Double>>();
        
        ConstantEvaluator<Double> result = instance.learn(data);
        assertEquals(0.0, result.getValue());
        
        double[][] values = new double[][]{
            new double[]{0.00, -2.00},
            new double[]{2.00, 2.00},
            new double[]{3.00, 4.10},
            new double[]{3.50, 5.00},
            new double[]{4.00, 5.90},
            new double[]{6.00, 10.10},
            new double[]{8.00, 13.90},
            new double[]{9.00, 16.00}};

        VectorFactory<?> factory = VectorFactory.getDefault();

        for (int i = 0; i < values.length; i++)
        {
            double input = values[i][0];
            double output = values[i][1];
            data.add(new DefaultWeightedInputOutputPair<Vector, Double>(
                factory.copyValues(input), output, 1.0));
        }
        
        result = instance.learn(data);
        assertEquals(6.875, result.getValue());
        
        data.get(3).setWeight(100000.0);
        result = instance.learn(data);
        assertEquals(5.0, result.getValue(), 0.001);
    }

}
