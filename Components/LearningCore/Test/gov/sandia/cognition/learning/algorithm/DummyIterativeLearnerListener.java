/*
 * File:                DummyIterativeLearnerListener.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright October 17, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 *
 */

package gov.sandia.cognition.learning.algorithm;

import gov.sandia.cognition.algorithm.IterativeAlgorithm;
import gov.sandia.cognition.algorithm.IterativeAlgorithmListener;

/**
 * The {@code DummyIterativeLearnerListener} is a class useful for testing 
 * {@code IterativeLearner} implementations to make sure that they fire off the
 * proper events.
 *
 * @author Justin Basilico
 * @since  2.0
 */
public class DummyIterativeLearnerListener
    extends Object
    implements IterativeAlgorithmListener
{

    public int learningStartedCount;

    public int learningEndedCount;

    public int stepStartedCount;

    public int stepEndedCount;

    /** Creates a new instance of DummyLearnerListener */
    public DummyIterativeLearnerListener()
    {
        super();

        this.resetCounts();
    }

    public void resetCounts()
    {
        this.learningStartedCount = 0;
        this.learningEndedCount = 0;
        this.stepStartedCount = 0;
        this.stepEndedCount = 0;
    }

    public void algorithmStarted(
        IterativeAlgorithm learner )
    {
        this.learningStartedCount++;
    }

    public void algorithmEnded(
        IterativeAlgorithm learner )
    {
        this.learningEndedCount++;
    }

    public void stepStarted(
        IterativeAlgorithm learner )
    {
        this.stepStartedCount++;
    }

    public void stepEnded(
        IterativeAlgorithm learner )
    {
        this.stepEndedCount++;
    }

}
