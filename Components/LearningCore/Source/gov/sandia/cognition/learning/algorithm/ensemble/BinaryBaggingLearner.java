/*
 * File:                BinaryBaggingLearner.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright September 17, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.learning.algorithm.ensemble;

import gov.sandia.cognition.annotation.CodeReview;
import gov.sandia.cognition.evaluator.Evaluator;
import gov.sandia.cognition.learning.algorithm.BatchLearner;
import gov.sandia.cognition.learning.data.InputOutputPair;
import gov.sandia.cognition.util.WeightedValue;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

/**
 * The {@code BinaryBaggingLearner} implements the Bagging learning algorithm.
 * At each step, the algorithm creates a "bag" of data by sampling from the
 * given data with replacement. It then passes the bag of data to the given
 * learner to learn a new binary categorizer, which it then adds to the 
 * ensemble. All learners are given an equal weight of 1.0.
 *
 * @param <InputType> Input class of the {@code Collection<InputOutputPairs>}
 * for the dataset, for example, something like Vector or String
 * @author Justin Basilico
 * @since  2.0
 */
@CodeReview(
    reviewer="Kevin R. Dixon",
    date="2008-07-23",
    changesNeeded=false,
    comments={
        "Cleaned up javadoc a little bit with code annotations.",
        "Otherwise, looks fine."
    }
)
public class BinaryBaggingLearner<InputType>
    extends AbstractBaggingLearner<InputType, Boolean,
        Evaluator<? super InputType, ? extends Boolean>,
        WeightedBinaryEnsemble<InputType, Evaluator<? super InputType, ? extends Boolean>>>
{

    /**
     * Creates a new instance of BinaryBaggingLearner.
     */
    public BinaryBaggingLearner()
    {
        this(null);
    }

    /**
     * Creates a new instance of BinaryBaggingLearner.
     *
     * @param  learner 
     *      The learner to use to create the categorizer on each iteration.
     */
    public BinaryBaggingLearner(
        final BatchLearner<? super Collection<? extends InputOutputPair<? extends InputType, Boolean>>, ? extends Evaluator<? super InputType, ? extends Boolean>>
            learner)
    {
        this(learner, DEFAULT_MAX_ITERATIONS);
    }

    /**
     * Creates a new instance of BinaryBaggingLearner.
     *
     * @param  learner 
     *      The learner to use to create the categorizer on each iteration.
     * @param  maxIterations 
     *      The maximum number of iterations to run for, which is also the 
     *      number of learners to create.
     */
    public BinaryBaggingLearner(
        final BatchLearner<? super Collection<? extends InputOutputPair<? extends InputType, Boolean>>, ? extends Evaluator<? super InputType, ? extends Boolean>>
            learner,
        final int maxIterations)
    {
        this(learner, maxIterations, new Random());
    }

    /**
     * Creates a new instance of BinaryBaggingLearner.
     *
     * @param  learner 
     *      The learner to use to create the categorizer on each iteration.
     * @param  maxIterations 
     *      The maximum number of iterations to run for, which is also the 
     *      number of learners to create.
     * @param  random 
     *      The random number generator to use.
     */
    public BinaryBaggingLearner(
        final BatchLearner<? super Collection<? extends InputOutputPair<? extends InputType, Boolean>>, ? extends Evaluator<? super InputType, ? extends Boolean>>
            learner,
        final int maxIterations,
        final Random random)
    {
        this(learner, maxIterations, DEFAULT_PERCENT_TO_SAMPLE, random);
    }


    /**
     * Creates a new instance of BinaryBaggingLearner.
     *
     * @param  learner
     *      The learner to use to create the ensemble member on each iteration.
     * @param  maxIterations
     *      The maximum number of iterations to run for, which is also the
     *      number of learners to create.
     * @param   percentToSample
     *      The percentage of the total size of the data to sample on each
     *      iteration. Must be positive.
     * @param  random
     *      The random number generator to use.
     */
    public BinaryBaggingLearner(
        final BatchLearner<? super Collection<? extends InputOutputPair<? extends InputType, Boolean>>, ? extends Evaluator<? super InputType, ? extends Boolean>>
            learner,
        final int maxIterations,
        final double percentToSample,
        final Random random)
    {
        super(learner, maxIterations, percentToSample, random);
    }

    @Override
    protected WeightedBinaryEnsemble<InputType, Evaluator<? super InputType, ? extends Boolean>> createInitialEnsemble()
    {
        return new WeightedBinaryEnsemble<InputType, Evaluator<? super InputType, ? extends Boolean>>(
            new ArrayList<WeightedValue<Evaluator<? super InputType,? extends Boolean>>>(
                this.getMaxIterations()));
    }

    @Override
    protected void addEnsembleMember(
        final Evaluator<? super InputType, ? extends Boolean> member)
    {
        this.ensemble.add(member);
    }

}
