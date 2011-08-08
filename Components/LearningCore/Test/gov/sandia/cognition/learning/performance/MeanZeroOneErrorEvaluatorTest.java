/*
 * File:                MeanZeroOneErrorTest.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Framework Lite
 *
 * Copyright September 26, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 *
 */

package gov.sandia.cognition.learning.performance;

import gov.sandia.cognition.learning.data.DefaultTargetEstimatePair;
import gov.sandia.cognition.learning.data.TargetEstimatePair;
import java.util.ArrayList;
import java.util.Collection;
import junit.framework.TestCase;

/**
 * This class implements JUnit tests for the following classes:
 *
 *     MeanZeroOneError
 *
 * @author Justin Basilico
 * @since  2.0
 */
public class MeanZeroOneErrorEvaluatorTest
    extends TestCase
{
    public MeanZeroOneErrorEvaluatorTest(
        String testName)
    {
        super(testName);
    }
    
    public void testConstructors()
    {
        MeanZeroOneErrorEvaluator<Object, String> instance = 
            new MeanZeroOneErrorEvaluator<Object, String>();
    }

    /**
     * Test of summarize method, of class gov.sandia.cognition.learning.measure.MeanZeroOneError.
     */
    public void testSummarize()
    {
        MeanZeroOneErrorEvaluator<Object, String> instance = 
            new MeanZeroOneErrorEvaluator<Object, String>();
        
        Collection<TargetEstimatePair<String, String>> data = 
            new ArrayList<TargetEstimatePair<String, String>>();
        
        assertEquals(0.0, instance.summarize(data));
        
        data.add(new DefaultTargetEstimatePair<String, String>("yes", "no"));
        assertEquals(1.0 / 1.0, instance.summarize(data));
        
        data.add(new DefaultTargetEstimatePair<String, String>("yes", "yes"));
        assertEquals(1.0 / 2.0, instance.summarize(data));
        
        data.add(new DefaultTargetEstimatePair<String, String>("no", "no"));
        assertEquals(1.0 / 3.0, instance.summarize(data));
        
        data.add(new DefaultTargetEstimatePair<String, String>("something", "else"));
        assertEquals(2.0 / 4.0, instance.summarize(data));
        
        data.add(new DefaultTargetEstimatePair<String, String>("same", "same"));
        assertEquals(2.0 / 5.0, instance.summarize(data));
        
        data.add(new DefaultTargetEstimatePair<String, String>("oh", "no"));
        assertEquals(3.0 / 6.0, instance.summarize(data));
        
        data.add(new DefaultTargetEstimatePair<String, String>("this", "bad"));
        assertEquals(4.0 / 7.0, instance.summarize(data));
        
        data.add(new DefaultTargetEstimatePair<String, String>("not null", null));
        assertEquals(5.0 / 8.0, instance.summarize(data));
        
        data.add(new DefaultTargetEstimatePair<String, String>(null, "not null"));
        assertEquals(6.0 / 9.0, instance.summarize(data));        
        
        data.add(new DefaultTargetEstimatePair<String, String>(null, null));
        assertEquals(6.0 / 10.0, instance.summarize(data));
    }

    /**
     * Test of compute method, of class gov.sandia.cognition.learning.measure.MeanZeroOneError.
     */
    public void testCompute()
    {
        // Tested by testSummarize.
    }
}
