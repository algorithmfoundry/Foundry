/*
 * File:            BaggingRegressionLearner.java
 * Authors:         Justin Basilico
 * Project:         Cognitive Foundry
 * 
 * Copyright 2011 Cognitive Foundry. All rights reserved.
 */

package gov.sandia.cognition.learning.algorithm.ensemble;

import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.evaluator.Evaluator;
import gov.sandia.cognition.learning.algorithm.BatchLearner;
import gov.sandia.cognition.learning.data.InputOutputPair;
import java.util.Collection;
import java.util.Random;

/**
 * Learns an ensemble for regression by randomly sampling with replacement
 * (duplicates allowed) some percentage of the size of the data (defaults to
 * 100%) on each iteration to train a new ensemble member. The random sample is
 * referred to as a bag. Each learned ensemble member is given equal weight.
 * The idea here is that randomly sampling from the data and learning an
 * ensemble member that has high variance (such as a decision tree) with
 * respect to the input data, one can improve the performance of that algorithm.
 *
 * By default, the algorithm runs the maxIterations number of steps to create
 * that number of ensemble members. However, one can also use out-of-bag (OOB)
 * error on each iteration to determine a stopping criteria. The OOB error is
 * determined by looking at the performance of the categorizer on the examples
 * that it has not seen.
 *
 * @param   <InputType>
 *      The input type for supervised learning. Passed on to the internal
 *      learning algorithm. Also the input type for the learned ensemble.
 * @author  Justin Basilico
 * @version 3.4.0
 * @see     BaggingCategorizerLearner
 */
@PublicationReference(
    title="Bagging Predictors",
    author="Leo Breiman",
    year=1996,
    type=PublicationType.Journal,
    publication="Machine Learning",
    pages={123, 140},
    url="http://www.springerlink.com/index/L4780124W2874025.pdf")
public class BaggingRegressionLearner<InputType>
    extends AbstractBaggingLearner<InputType, Double, Evaluator<? super InputType, ? extends Number>, AveragingEnsemble<InputType, Evaluator<? super InputType, ? extends Number>>>
{

    /**
     * Creates a new, empty {@code BaggingRegressionLearner}.
     */
    public BaggingRegressionLearner()
    {
        this(null);
    }

    /**
     * Creates a new instance of BaggingRegressionLearner.
     *
     * @param  learner
     *      The learner to use to create the categorizer on each iteration.
     */
    public BaggingRegressionLearner(
        final BatchLearner<? super Collection<? extends InputOutputPair<? extends InputType, Double>>, ? extends Evaluator<? super InputType, ? extends Number>> learner)
    {
        this(learner, DEFAULT_MAX_ITERATIONS, DEFAULT_PERCENT_TO_SAMPLE, new Random());
    }

    /**
     * Creates a new instance of BaggingRegressionLearner.
     *
     * @param  learner
     *      The learner to use to create the regression function on each iteration.
     * @param  maxIterations
     *      The maximum number of iterations to run for, which is also the
     *      number of learners to create.
     * @param   percentToSample
     *      The percentage of the total size of the data to sample on each
     *      iteration. Must be positive.
     * @param  random
     *      The random number generator to use.
     */
    public BaggingRegressionLearner(
        final BatchLearner<? super Collection<? extends InputOutputPair<? extends InputType, Double>>, ? extends Evaluator<? super InputType, ? extends Number>> learner,
        final int maxIterations,
        final double percentToSample,
        final Random random)
    {
        super(learner, maxIterations, percentToSample, random);
    }

    @Override
    protected AveragingEnsemble<InputType, Evaluator<? super InputType, ? extends Number>> createInitialEnsemble()
    {
        return new AveragingEnsemble<InputType, Evaluator<? super InputType, ? extends Number>>();
    }

    @Override
    protected void addEnsembleMember(
        final Evaluator<? super InputType, ? extends Number> member)
    {
        this.ensemble.add(member);
    }
    
}
