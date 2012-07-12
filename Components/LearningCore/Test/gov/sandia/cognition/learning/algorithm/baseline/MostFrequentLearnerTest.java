/*
 * File:                MostFrequentLearnerTest.java
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

import gov.sandia.cognition.learning.data.DefaultInputOutputPair;
import gov.sandia.cognition.learning.data.InputOutputPair;
import gov.sandia.cognition.learning.function.ConstantEvaluator;
import gov.sandia.cognition.math.matrix.mtj.Vector2;
import gov.sandia.cognition.util.CloneableSerializable;
import java.util.ArrayList;
import junit.framework.TestCase;

/**
 * Tests of MostFrequentLearner
 * @author  Justin Basilico
 * @since   2.1
 */
public class MostFrequentLearnerTest
    extends TestCase
{
    /**
     * Creates a new test.
     *
     * @param   testName The test name.
     */
    public MostFrequentLearnerTest(
        String testName)
    {
        super(testName);
    }
    
    /**
     * Tests the constructors of class MostFrequentLearner.
     */
    public void testConstructors()
    {
        MostFrequentLearner<Boolean> instance = 
            new MostFrequentLearner<Boolean>();
        assertNotNull(instance);
    }

    /**
     * Tests of clone
     */
    public void testClone()
    {
        System.out.println( "Clone" );
        MostFrequentLearner<?> instance = new MostFrequentLearner<String>();

        CloneableSerializable clone = instance.clone();
        assertNotNull( clone );
        assertNotSame( instance, clone );
    }

    /**
     * Test of learn method, of class MostFrequentLearner.
     */
    public void testLearn()
    {
        
        MostFrequentLearner<Boolean> instance = 
            new MostFrequentLearner<Boolean>();
        
        ArrayList<InputOutputPair<Vector2, Boolean>> examples =
            new ArrayList<InputOutputPair<Vector2, Boolean>>();
        
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
            examples.add(new DefaultInputOutputPair<Vector2, Boolean>(example, true));
        }

        for (Vector2 example : negatives)
        {
            examples.add(new DefaultInputOutputPair<Vector2, Boolean>(example, false));
        }
        
        result = instance.learn(examples);
        assertEquals(Boolean.FALSE, result.getValue());
        
        examples.remove(7);
        examples.remove(7);
        
        result = instance.learn(examples);
        assertEquals(Boolean.TRUE, result.getValue());
        
    }

}
