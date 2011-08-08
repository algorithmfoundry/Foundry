/*
 * File:                SupervisedLearnerValidationExperimentTest.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright December 18, 2007, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 * See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.learning.experiment;

import gov.sandia.cognition.learning.data.InputOutputPair;
import gov.sandia.cognition.learning.performance.MeanZeroOneErrorEvaluator;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.statistics.method.ConfidenceInterval;
import gov.sandia.cognition.statistics.method.StudentTConfidence;
import junit.framework.TestCase;

/**
 * Tests of SupervisedLearnerValidationExperiment
 * @author  Justin Basilico
 * @since   2.0
 */
public class SupervisedLearnerValidationExperimentTest
    extends TestCase
{

    /**
     * Creates a new test.
     *
     * @param   testName The test name.
     */
    public SupervisedLearnerValidationExperimentTest(
        String testName)
    {
        super(testName);
    }

    /**
     * Tests the constructors of SupervisedLearnerValidationExperiment.
     */
    public void testConstructors()
    {
        SupervisedLearnerValidationExperiment<Vector, Boolean, Double, ConfidenceInterval> instance = 
            new SupervisedLearnerValidationExperiment<Vector, Boolean, Double, ConfidenceInterval>();

        assertNull(instance.getFoldCreator());
        assertNull(instance.getPerformanceEvaluator());
        assertNull(instance.getSummarizer());

        LeaveOneOutFoldCreator<InputOutputPair<Vector, Boolean>> foldCreator =
            new LeaveOneOutFoldCreator<InputOutputPair<Vector, Boolean>>();
        MeanZeroOneErrorEvaluator<Vector, Boolean> measure =
            new MeanZeroOneErrorEvaluator<Vector, Boolean>();
        StudentTConfidence.Summary summarizer = new StudentTConfidence.Summary(0.95);
        
        instance.setPerformanceEvaluator(measure);
        
        instance = new SupervisedLearnerValidationExperiment<Vector, Boolean, Double, ConfidenceInterval>(
            foldCreator, measure, summarizer);
        assertSame(foldCreator, instance.getFoldCreator());
        assertSame(measure, instance.getPerformanceEvaluator());
        assertSame(summarizer, instance.getSummarizer());
    }

}
