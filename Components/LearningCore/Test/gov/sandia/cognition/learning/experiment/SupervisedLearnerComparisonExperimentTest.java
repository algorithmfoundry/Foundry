/*
 * File:                SupervisedLearnerComparisonExperimentTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Aug 10, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.learning.experiment;

import gov.sandia.cognition.learning.data.InputOutputPair;
import gov.sandia.cognition.learning.performance.RootMeanSquaredErrorEvaluator;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.statistics.method.ConfidenceInterval;
import gov.sandia.cognition.statistics.method.StudentTConfidence;
import junit.framework.TestCase;
import java.util.Random;

/**
 * Unit tests for SupervisedLearnerComparisonExperimentTest.
 *
 * @author krdixon
 */
public class SupervisedLearnerComparisonExperimentTest
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
     * Tests for class SupervisedLearnerComparisonExperimentTest.
     * @param testName Name of the test.
     */
    public SupervisedLearnerComparisonExperimentTest(
        String testName)
    {
        super(testName);
    }


    /**
     * Tests the constructors of class SupervisedLearnerComparisonExperimentTest.
     */
    public void testConstructors()
    {
        System.out.println( "Constructors" );

        SupervisedLearnerComparisonExperiment<Vector,Double,Double,ConfidenceInterval> comparison =
            new SupervisedLearnerComparisonExperiment<Vector, Double, Double, ConfidenceInterval>();

        assertNull( comparison.getFoldCreator() );
        assertNull( comparison.getSummarizer() );
        assertNull( comparison.getListeners() );
        assertNull( comparison.getLearners() );
        assertNull( comparison.getStatistics() );
        assertNull( comparison.getStatisticalTest() );

        StudentTConfidence ttest = new StudentTConfidence();
        CrossFoldCreator<InputOutputPair<Vector, Double>> foldCreator =
            new CrossFoldCreator<InputOutputPair<Vector, Double>>( 10, RANDOM );
        RootMeanSquaredErrorEvaluator<Vector> rms =
            new RootMeanSquaredErrorEvaluator<Vector>();
        final double confidence = 0.95;
        StudentTConfidence.Summary tdistribution =
            new StudentTConfidence.Summary( confidence );
        
        comparison = new SupervisedLearnerComparisonExperiment<Vector, Double, Double, ConfidenceInterval>(
                foldCreator, rms, ttest, tdistribution );

        assertSame( foldCreator, comparison.getFoldCreator() );
        assertSame( rms, comparison.getPerformanceEvaluator() );
        assertSame( ttest, comparison.getStatisticalTest() );
        assertSame( tdistribution, comparison.getSummarizer() );

    }
    
}