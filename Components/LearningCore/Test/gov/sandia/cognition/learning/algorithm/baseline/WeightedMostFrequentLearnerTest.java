/*
 * File:                WeightedMostFrequentLearnerTest.java
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
import gov.sandia.cognition.learning.data.WeightedInputOutputPair;
import gov.sandia.cognition.learning.function.ConstantEvaluator;
import gov.sandia.cognition.math.matrix.mtj.Vector2;
import gov.sandia.cognition.util.CloneableSerializable;
import java.util.ArrayList;
import junit.framework.TestCase;

/**
 * Tests of WeightedMostFrequentLearner
 * @author  Justin Basilico
 * @since   2.1
 */
public class WeightedMostFrequentLearnerTest
    extends TestCase
{
    /**
     * Creates a new test.
     *
     * @param   testName The test name.
     */
    public WeightedMostFrequentLearnerTest(
        String testName)
    {
        super(testName);
    }
    
    /**
     * Tests the constructors of class WeightedMostFrequentLearner.
     */
    public void testConstructor()
    {
        WeightedMostFrequentLearner<Boolean> instance = 
            new WeightedMostFrequentLearner<Boolean>();
        assertNotNull(instance);
    }
    
    /**
     * Tests of clone
     */
    public void testClone()
    {
        System.out.println( "Clone" );
        WeightedMostFrequentLearner instance = new WeightedMostFrequentLearner();

        CloneableSerializable clone = instance.clone();
        assertNotNull( clone );
        assertNotSame( instance, clone );
    }

    /**
     * Test of learn method, of class WeightedMostFrequentLearner.
     */
    public void testLearn()
    {
        WeightedMostFrequentLearner<Boolean> instance = 
            new WeightedMostFrequentLearner<Boolean>();
        
        ArrayList<DefaultWeightedInputOutputPair<Vector2, Boolean>> examples =
            new ArrayList<DefaultWeightedInputOutputPair<Vector2, Boolean>>();
        
        ConstantEvaluator<Boolean> result = instance.learn(examples);
        assertNull(result.getValue());
        
        Vector2[] positives = new Vector2[] {
            new Vector2(1.00, 1.00),
            new Vector2(1.00, 3.00),
            new Vector2(0.25, 4.00),
            new Vector2(2.00, 1.00),
            new Vector2(5.00, -3.00)
        };

        Vector2[] negatives = new Vector2[] {
            new Vector2(2.00, 3.00),
            new Vector2(2.00, 4.00),
            new Vector2(3.00, 2.00),
            new Vector2(4.25, 3.75),
            new Vector2(4.00, 7.00),
            new Vector2(7.00, 4.00)
        };

        for (Vector2 example : positives)
        {
            examples.add(new DefaultWeightedInputOutputPair<Vector2, Boolean>(example, true, 1.0));
        }

        for (Vector2 example : negatives)
        {
            examples.add(new DefaultWeightedInputOutputPair<Vector2, Boolean>(example, false, 1.0));
        }
        
        result = instance.learn(examples);
        assertEquals(Boolean.FALSE, result.getValue());
        
        examples.remove(7);
        examples.remove(7);
        
        result = instance.learn(examples);
        assertEquals(Boolean.TRUE, result.getValue());
        
        examples.get(7).setWeight(3.0);
        result = instance.learn(examples);
        assertEquals(Boolean.FALSE, result.getValue());
        
        examples.get(0).setWeight(2.0);
        examples.get(1).setWeight(2.0);
        result = instance.learn(examples);
        assertEquals(Boolean.TRUE, result.getValue());
    }

}
