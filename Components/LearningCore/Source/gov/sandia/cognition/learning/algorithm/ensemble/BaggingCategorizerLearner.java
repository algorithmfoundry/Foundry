/*
 * File:                BaggingCategorizerLearner.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright November 26, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 * See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.learning.algorithm.ensemble;

import gov.sandia.cognition.algorithm.IterativeAlgorithm;
import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.collection.CollectionUtil;
import gov.sandia.cognition.evaluator.Evaluator;
import gov.sandia.cognition.learning.algorithm.BatchLearner;
import gov.sandia.cognition.learning.data.DatasetUtil;
import gov.sandia.cognition.learning.data.InputOutputPair;
import gov.sandia.cognition.statistics.DataDistribution;
import gov.sandia.cognition.statistics.distribution.DefaultDataDistribution;
import gov.sandia.cognition.util.WeightedValue;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;
import java.util.Set;

/**
 * Learns an categorization ensemble by randomly sampling with replacement
 * (duplicates allowed) some percentage of the size of the data (defaults to
 * 100%) on each iteration to train a new ensemble member. The random sample is
 * referred to as a bag. Each learned ensemble member is given equal weight.
 * The idea here is that randomly sampling from the data and learning a
 * categorizer that has high variance (such as a decision tree) with respect to
 * the input data, one can improve the performance of that
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
 * @param   <CategoryType>
 *      The output type for supervised learning. Passed on to the internal
 *      learning algorithm. Also the output type of the learned ensemble.
 * @author  Justin Basilico
 * @since   3.0
 */
@PublicationReference(
    title="Bagging Predictors",
    author="Leo Breiman",
    year=1996,
    type=PublicationType.Journal,
    publication="Machine Learning",
    pages={123, 140},
    url="http://www.springerlink.com/index/L4780124W2874025.pdf")
public class BaggingCategorizerLearner<InputType, CategoryType>
    extends AbstractBaggingLearner<InputType, CategoryType, Evaluator<? super InputType, ? extends CategoryType>, WeightedVotingCategorizerEnsemble<InputType, CategoryType, Evaluator<? super InputType, ? extends CategoryType>>>
    implements BagBasedCategorizerEnsembleLearner<InputType, CategoryType>
{
    /**
     * Creates a new instance of BaggingCategorizerLearner.
     */
    public BaggingCategorizerLearner()
    {
        this(null);
    }

    /**
     * Creates a new instance of BaggingCategorizerLearner.
     *
     * @param  learner
     *      The learner to use to create the categorizer on each iteration.
     */
    public BaggingCategorizerLearner(
        final BatchLearner<? super Collection<? extends InputOutputPair<? extends InputType, CategoryType>>, ? extends Evaluator<? super InputType, ? extends CategoryType>> learner)
    {
        this(learner, DEFAULT_MAX_ITERATIONS, DEFAULT_PERCENT_TO_SAMPLE, new Random());
    }

    /**
     * Creates a new instance of BaggingCategorizerLearner.
     *
     * @param  learner
     *      The learner to use to create the categorizer on each iteration.
     * @param  maxIterations
     *      The maximum number of iterations to run for, which is also the
     *      number of learners to create.
     * @param   percentToSample
     *      The percentage of the total size of the data to sample on each
     *      iteration. Must be positive.
     * @param  random
     *      The random number generator to use.
     */
    public BaggingCategorizerLearner(
        final BatchLearner<? super Collection<? extends InputOutputPair<? extends InputType, CategoryType>>, ? extends Evaluator<? super InputType, ? extends CategoryType>> learner,
        final int maxIterations,
        final double percentToSample,
        final Random random)
    {
        super(learner, maxIterations, percentToSample, random);
    }

    @Override
    protected WeightedVotingCategorizerEnsemble<InputType, CategoryType, Evaluator<? super InputType, ? extends CategoryType>> createInitialEnsemble()
    {
        final Set<CategoryType> categories =
            DatasetUtil.findUniqueOutputs(this.getData());
        return new WeightedVotingCategorizerEnsemble<InputType, CategoryType, Evaluator<? super InputType, ? extends CategoryType>>(
            categories);
    }

    @Override
    protected void addEnsembleMember(
        final Evaluator<? super InputType, ? extends CategoryType> member)
    {
        // Add the categorizer to the ensemble and give it equal weight.
        this.ensemble.add(member, 1.0);
    }

    @Override
    public int[] getDataInBag()
    {
        return this.dataInBag;
    }

    @Override
    public InputOutputPair<? extends InputType, CategoryType> getExample(
        final int index)
    {
        return this.dataList.get(index);
    }
    
    /**
     * Implements a stopping criteria for bagging that uses the out-of-bag
     * error to determine when to stop learning the ensemble. It tracks the
     * out-of-bag error rate of the ensemble and keeps it in a given smoothing
     * window. Once the smoothed error rate stops decreasing, it stops learning
     * and removes all of the ensemble members back to the one that had the
     * minimal error in that window.
     *
     * @param <InputType>
     *      The input type the algorithm is learning over.
     * @param <CategoryType>
     *      The category type the algorithm is learning over.
     */
    public static class OutOfBagErrorStoppingCriteria<InputType, CategoryType>
        extends AbstractCategorizerOutOfBagStoppingCriteria<InputType, CategoryType>
    {

        /** The running estimate of the ensemble for each example where an ensemble
         *  member can only vote on elements that were not in the bag used to train
         *  it. Same size as the training data. */
        protected transient ArrayList<DataDistribution<CategoryType>> outOfBagEstimates;

        /**
         * Creates a new {@code OutOfBagErrorStoppingCriteria}.
         */
        public OutOfBagErrorStoppingCriteria()
        {
            this(DEFAULT_SMOOTHING_WINDOW_SIZE);
        }

        /**
         * Creates a new {@code OutOfBagErrorStoppingCriteria} with the given
         * smoothing window size.
         *
         * @param   smoothingWindowSize
         *      The smoothing window size to use. Must be positive.
         */
        public OutOfBagErrorStoppingCriteria(
            final int smoothingWindowSize)
        {
            super(smoothingWindowSize);
        }

        @SuppressWarnings("unchecked")
        @Override
        public void algorithmStarted(
            final IterativeAlgorithm algorithm)
        {
            super.algorithmStarted(algorithm);
            
            final int dataSize = this.learner.getData().size();
            this.outOfBagEstimates = new ArrayList<>(dataSize);
            for (int i = 0; i < dataSize; i++)
            {
                this.outOfBagEstimates.add(new DefaultDataDistribution<>(2));
            }
        }

        @Override
        public void algorithmEnded(
            final IterativeAlgorithm algorithm)
        {
            super.algorithmEnded(algorithm);
            
            this.outOfBagEstimates = null;
        }

        @Override
        public DataDistribution<CategoryType> getOutOfBagEstimate(
            final int index)
        {
            return this.outOfBagEstimates.get(index);
        }

        /**
         * Updates the out-of-bag estimates that this ensemble keeps.
         */
        protected void updateOutOfBagEstimates()
        {
            final WeightedValue<? extends Evaluator<? super InputType, ? extends CategoryType>> weightedMember = 
                CollectionUtil.getLast(this.learner.getResult().getMembers());
            
            final double weight = weightedMember.getWeight();
            final Evaluator<? super InputType, ? extends CategoryType> member = 
                weightedMember.getValue();
            
            final int[] dataInBag = this.learner.getDataInBag();

            // Go through the data and update the values for the data that was
            // not in the bag.
            final int dataSize = dataInBag.length;
             for (int i = 0; i < dataSize; i++)
            {
                if (dataInBag[i] <= 0)
                {
                    final InputOutputPair<? extends InputType, CategoryType> example =
                        this.learner.getExample(i);
                    final CategoryType memberGuess = member.evaluate(
                        example.getInput());
                    this.outOfBagEstimates.get(i).increment(
                        memberGuess, weight);
                }
            }
        }
        
        @Override
        public void stepEnded(
            final IterativeAlgorithm algorithm)
        {
            // First update all the estimates since they're used by the super
            // class.
            this.updateOutOfBagEstimates();
            super.stepEnded(algorithm);
        }

    }
}

