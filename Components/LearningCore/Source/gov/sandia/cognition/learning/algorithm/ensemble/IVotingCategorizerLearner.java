/*
 * File:                IVotingCategorizerLearner.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright November 25, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 * See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.learning.algorithm.ensemble;

import gov.sandia.cognition.algorithm.IterativeAlgorithm;
import gov.sandia.cognition.algorithm.IterativeAlgorithmListener;
import gov.sandia.cognition.collection.CollectionUtil;
import gov.sandia.cognition.collection.FiniteCapacityBuffer;
import gov.sandia.cognition.evaluator.Evaluator;
import gov.sandia.cognition.factory.Factory;
import gov.sandia.cognition.learning.algorithm.AbstractAnytimeSupervisedBatchLearner;
import gov.sandia.cognition.learning.algorithm.BatchLearner;
import gov.sandia.cognition.learning.data.DatasetUtil;
import gov.sandia.cognition.learning.data.InputOutputPair;
import gov.sandia.cognition.math.UnivariateStatisticsUtil;
import gov.sandia.cognition.statistics.DataHistogram;
import gov.sandia.cognition.statistics.distribution.MapBasedDataHistogram;
import gov.sandia.cognition.util.AbstractCloneableSerializable;
import gov.sandia.cognition.util.ObjectUtil;
import gov.sandia.cognition.util.Randomized;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Learns an ensemble in a method similar to bagging except that on each
 * iteration the bag is built from two parts, each sampled from elements from
 * disjoint sets. The two sets are the set of examples that the ensemble
 * currently gets correct and incorrect.
 *
 * In effect, ivoting has similar properties to boosting except that it does
 * not require that the learner for each ensemble member be able to use
 * weights on the examples.
 *
 * @param   <InputType>
 *      The type of the input for the categorizer to learn. This is the type
 *      passed to the internal batch learner to learn each ensemble member.
 * @param   <CategoryType>
 *      The type of the category that is the output for the categorizer to
 *      learn. It is also passed to the internal batch learner to learn each
 *      ensemble member. It must have a valid equals and hashCode method.
 * @author  Justin Basilico
 * @since   3.0
 */
// TODO: Find a reference for ivoting.
// --jdbasil (2009-11-29)
public class IVotingCategorizerLearner<InputType, CategoryType>
    extends AbstractAnytimeSupervisedBatchLearner<InputType, CategoryType, WeightedVotingCategorizerEnsemble<InputType, CategoryType, Evaluator<? super InputType, ? extends CategoryType>>>
    implements Randomized
{
// TODO: Decide on appropriate default values.
// --jdbasil (2009-11-29)

    /** The default maximum number of iterations is {@value}. */
    public static final int DEFAULT_MAX_ITERATIONS = 500;

    /** The default percent to sample {@value}. */
    public static final double DEFAULT_PERCENT_TO_SAMPLE = 0.10;

    /** By default use 50% incorrect (and 50%) correct in the percent to sample. */
    public static final double DEFAULT_PROPORTION_INCORRECT_IN_SAMPLE = 0.50;

    /** The default value to vote out-of-bag. */
    public static final boolean DEFAULT_VOTE_OUT_OF_BAG_ONLY = true;

    /** The learner used to produce each ensemble member. */
    protected BatchLearner<? super Collection<? extends InputOutputPair<? extends InputType, CategoryType>>,
        ? extends Evaluator<? super InputType, ? extends CategoryType>> learner;

    /** The percent to sample on each iteration. */
    protected double percentToSample;

    /** The proportion of incorrect examples in each sample. Must be between 0.0
     *  and 1.0 (inclusive). */
    protected double proportionIncorrectInSample;

    /** Controls whether or not an ensemble member can vote on items it was
     *  trained on during learning. By default, the ensemble member can only
     *  vote on out-of-bag values. */
    protected boolean voteOutOfBagOnly;

    /** Factory for counting votes. */
    protected Factory<? extends DataHistogram<CategoryType>> counterFactory;

    /** The random number generator to use. */
    protected Random random;

    /** The current ensemble. */
    protected transient WeightedVotingCategorizerEnsemble<InputType, CategoryType, Evaluator<? super InputType, ? extends CategoryType>> ensemble;

    /** The data represented as an array list. */
    protected transient ArrayList<? extends InputOutputPair<? extends InputType, CategoryType>> dataList;

    /** The running estimate of the ensemble for each example. Updated in each
     *  iteration with the ensemble member created for that iteration. This is
     *  used instead of evaluating the ensemble in each iteration to make it so
     *  that each ensemble member is only evaluated once on each training
     *  example.
     */
    protected transient ArrayList<DataHistogram<CategoryType>> dataFullEstimates;

    /** The running estimate of the ensemble for each example where an ensemble
     *  member can only vote on elements that were not in the bag used to train
     * it. */
    protected transient ArrayList<DataHistogram<CategoryType>> dataOutOfBagEstimates;

    /** A boolean for each example indicating if the ensemble currently gets the
     *  example correct or incorrect.
     */
    protected transient boolean[] currentEnsembleCorrect;

    /** The indices of examples that the ensemble currently gets correct. */
    protected transient ArrayList<Integer> currentCorrectIndices;

    /** The indices of examples that the ensemble currently gets incorrect. */
    protected transient ArrayList<Integer> currentIncorrectIndices;

    /** The size of sample to create on each iteration. */
    protected transient int sampleSize;

    /** The number of correct examples to sample on each iteration. */
    protected transient int numCorrectToSample;

    /** The number of incorrect examples to sample on each iteration. */
    protected transient int numIncorrectToSample;

    /** The current bag used to train the current ensemble member. */
    protected transient ArrayList<InputOutputPair<? extends InputType, CategoryType>> currentBag;

    /** A counter for each example indicating how many times it exists in the
     *  current bag.
     */
    protected transient int[] dataInBag;

    /** The currently learned member of the ensemble. */
    protected transient Evaluator<? super InputType, ? extends CategoryType> currentMember;

    /** The estimates of the current member for each example.
     */
    protected transient ArrayList<CategoryType> currentMemberEstimates;

    /**
     * Creates a new {@code IVotingCategorizerLearner}.
     */
    public IVotingCategorizerLearner()
    {
        this(null, DEFAULT_MAX_ITERATIONS, DEFAULT_PERCENT_TO_SAMPLE,
            new Random());
    }

    /**
     * Creates a new {@code IVotingCategorizerLearner}.
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
    public IVotingCategorizerLearner(
        final BatchLearner<? super Collection<? extends InputOutputPair<? extends InputType, CategoryType>>, ? extends Evaluator<? super InputType, ? extends CategoryType>> learner,
        final int maxIterations,
        final double percentToSample,
        final Random random)
    {
        this(learner, maxIterations, percentToSample,
            DEFAULT_PROPORTION_INCORRECT_IN_SAMPLE,
            DEFAULT_VOTE_OUT_OF_BAG_ONLY,
            new MapBasedDataHistogram.DefaultFactory<CategoryType>(2),
            random);
    }

    /**
     * Creates a new {@code IVotingCategorizerLearner}.
     *
     * @param  learner
     *      The learner to use to create the categorizer on each iteration.
     * @param  maxIterations
     *      The maximum number of iterations to run for, which is also the
     *      number of learners to create.
     * @param   percentToSample
     *      The percentage of the total size of the data to sample on each
     *      iteration. Must be positive.
     * @param   proportionIncorrectInSample
     *      The percentage of incorrect examples to put in each sample. Must
     *      be between 0.0 and 1.0 (inclusive).
     * @param  voteOutOfBagOnly
     *      Controls whether or not in-bag or out-of-bag votes are used to
     *      determine accuracy.
     * @param   counterFactory
     *      The factory for counting votes.
     * @param  random
     *      The random number generator to use.
     */
    public IVotingCategorizerLearner(
        final BatchLearner<? super Collection<? extends InputOutputPair<? extends InputType, CategoryType>>, ? extends Evaluator<? super InputType, ? extends CategoryType>> learner,
        final int maxIterations,
        final double percentToSample,
        final double proportionIncorrectInSample,
        final boolean voteOutOfBagOnly,
        final Factory<? extends DataHistogram<CategoryType>> counterFactory,
        final Random random)
    {
        super(maxIterations);

        this.setLearner(learner);
        this.setPercentToSample(percentToSample);
        this.setProportionIncorrectInSample(proportionIncorrectInSample);
        this.setVoteOutOfBagOnly(voteOutOfBagOnly);
        this.setCounterFactory(counterFactory);
        this.setRandom(random);
    }

    @Override
    protected boolean initializeAlgorithm()
    {
        final int dataSize = this.data.size();

        if (dataSize <= 0)
        {
            // Can't run on empty data.
            return false;
        }

        if (this.random == null)
        {
            this.random = new Random();
        }

        // Initialize the ensemble.
        this.ensemble = new WeightedVotingCategorizerEnsemble<InputType, CategoryType, Evaluator<? super InputType, ? extends CategoryType>>(
            DatasetUtil.findUniqueOutputs(this.data));

        // We need the data to be as a random-access list in order to
        // efficiently sample from it.
        this.dataList = CollectionUtil.asArrayList(this.data);

        // We keep a running estimate of the output of the ensemble by
        // evaluating each member once.
        this.dataFullEstimates =
            new ArrayList<DataHistogram<CategoryType>>(dataSize);
        this.dataOutOfBagEstimates =
            new ArrayList<DataHistogram<CategoryType>>(dataSize);

        // We keep track of whether or not the current ensemble gets each
        // example correct.
        this.currentEnsembleCorrect = new boolean[dataSize];

        // We keep track of the lists of the indices of the current correct
        // and incorrect examples.
        this.currentCorrectIndices = new ArrayList<Integer>(dataSize);
        this.currentIncorrectIndices = new ArrayList<Integer>(dataSize);

        // Initialize the data estimates and the incorrect indices. All
        // examples are incorrect to start.
        for (int i = 0; i < dataSize; i++)
        {
            this.dataFullEstimates.add(new MapBasedDataHistogram<CategoryType>(2));
            this.dataOutOfBagEstimates.add(new MapBasedDataHistogram<CategoryType>(2));
            this.dataOutOfBagEstimates.add(this.counterFactory.create());
            this.currentIncorrectIndices.add(i);
        }

        // Figure out how many total samples and then the numbers of correct
        // and incorrect samples..
        this.sampleSize =
            Math.max(1, (int) (this.percentToSample * dataSize));
        this.numIncorrectToSample =
            (int) (this.proportionIncorrectInSample * sampleSize);
        this.numCorrectToSample = sampleSize - this.numIncorrectToSample;

        // Initialize the current bag.
        this.currentBag = new ArrayList<InputOutputPair<? extends InputType, CategoryType>>(
            this.numCorrectToSample + this.numIncorrectToSample);
        this.dataInBag = new int[dataSize];

        // Initialize the information about the current member.
        this.currentMember = null;
        this.currentMemberEstimates = new ArrayList<CategoryType>(dataSize);

        // We initialize the array list.
        for (int i = 0; i < dataSize; i++)
        {
            this.currentMemberEstimates.add(null);
        }

        return true;
    }

    @Override
    protected boolean step()
    {
        final int dataSize = this.dataList.size();

        // Clear out the data from the previous iteration.
        this.currentBag.clear();

        for (int i = 0; i < dataSize; i++)
        {
            this.dataInBag[i] = 0;
        }

        // We use pointers here because we may have to change the pointers
        // after we make the lists in case one of them is empty.
        ArrayList<Integer> correctIndices = this.currentCorrectIndices;
        ArrayList<Integer> incorrectIndices = this.currentIncorrectIndices;

        // If one of the two lists is empty, we just point both pointers to the
        // same list, which means that the sampling steps are just going to be
        // sampling from the same distribution.
        if (incorrectIndices.isEmpty())
        {
            incorrectIndices = correctIndices;
        }
        else if (correctIndices.isEmpty())
        {
            correctIndices = incorrectIndices;
        }

        // Clear out the bag from the previous iteration.
        this.currentBag.clear();

        // Sample into the bag.
        sampleIndicesWithReplacementInto(correctIndices, this.dataList,
            this.numCorrectToSample, this.random,
            this.currentBag, this.dataInBag);
        sampleIndicesWithReplacementInto(incorrectIndices, this.dataList,
            this.numIncorrectToSample, this.random,
            this.currentBag, this.dataInBag);

        // Learn the ensemble member.
        this.currentMember = this.learner.learn(this.currentBag);

        // Add a new member to the ensemble. It gets a default weight of
        // 1.0.
        this.ensemble.add(this.currentMember, 1.0);


        // Clear out the correct and incorrect information.
        this.currentCorrectIndices.clear();
        this.currentIncorrectIndices.clear();

        // Go through all the data and update the estimates for it. We keep
        // track of both the estimates for the new member and for the ensemble
        // as a whole. Incrementally computing the estimates saves a lot of
        // execution time since each member only sees each data item once.
        for (int i = 0; i < dataSize; i++)
        {
            // Get the example.
            final InputOutputPair<? extends InputType, CategoryType> example =
                this.dataList.get(i);
            final CategoryType actual = example.getOutput();

            // Get the guess for the new member.
            final CategoryType memberGuess = 
                this.currentMember.evaluate(example.getInput());

            // Save the estimate for the current member.
            this.currentMemberEstimates.set(i, memberGuess);

            // Get the full ensemble estimate for the current item.
            final DataHistogram<CategoryType> fullEstimate =
                this.dataFullEstimates.get(i);

            // Get the out-of-bag estimate for the current item.
            final DataHistogram<CategoryType> outOfBagEstimate =
                this.dataOutOfBagEstimates.get(i);
            
            if (memberGuess != null)
            {
                // Update the full estimate.
                fullEstimate.add(memberGuess);

                if (this.dataInBag[i] <= 0)
                {
                    // Add to the out-of-bag estimate for the item.
                    outOfBagEstimate.add(memberGuess);
                }
            }
            // else - The member had nothing to contribute for the estimate.

            // Get the new ensemble guess.
            CategoryType ensembleGuess = null;

            // See if we're guessing based on out-of-bag only.
            if (this.voteOutOfBagOnly && outOfBagEstimate.getTotalCount() > 0)
            {
                ensembleGuess = outOfBagEstimate.getMaximumValue();
            }
            else
            {
                // Either we're not doing out-of-bag or there are no out-of-bag
                // votes for this item, so we use the full ensemble estimate.
                ensembleGuess = fullEstimate.getMaximumValue();
            }

            // We assume that the ensemble member is correct in the case that
            // out-of-bag-only counting is turned on and there are no votes for
            // an item. This case means that the item appears in the bag of
            // used to train every member so far. Thus, we assume that the
            // ensemble is getting it correct.
            // Otherwise, equality of the actual and the guess is used.
            final boolean ensembleCorrect = ensembleGuess == null
                || ObjectUtil.equalsSafe(actual, ensembleGuess);

            this.currentEnsembleCorrect[i] = ensembleCorrect;

            // Update the list of correct and incorrect indices.
            if (ensembleCorrect)
            {
                this.currentCorrectIndices.add(i);
            }
            else
            {
                this.currentIncorrectIndices.add(i);
            }
        }

        return true;
    }

    /**
     * Takes the given number of samples from the given list and places them in
     * the given output list. It samples with replacement, which means that a
     * given item may appear multiple times in the bag. It also keeps track of
     * how many times each item was sampled.
     *
     * @param   <DataType>
     *      The data type to sample.
     * @param   fromIndices
     *      The indices into the given base data to sample from.
     * @param   baseData
     *      The list to sample from using the given list of indices.
     * @param   numToSample
     *      The number to sample. Must be non-negative.
     * @param   random
     *      The random number generator to use.
     * @param   output
     *      The list to add the samples to.
     * @param   dataInBag
     *      The array of counters for the number of times each example is
     *      sampled.
     */
    protected static <DataType> void sampleIndicesWithReplacementInto(
        final ArrayList<Integer> fromIndices,
        final ArrayList<? extends DataType> baseData,
        final int numToSample,
        final Random random,
        final ArrayList<DataType> output,
        final int[] dataInBag)
    {
        // Do the sampling with replacement, which means duplicates are allowed.
        final int fromSize = fromIndices.size();
        for (int i = 0; i < numToSample; i++)
        {
            // Randomly pick an item.
            final int randomInt = random.nextInt(fromSize);

            // Get the index for the random item.
            final int index = fromIndices.get(randomInt);

            // Add the item to the bag.
            output.add(baseData.get(index));

            // Increse the bag counter for the chosen item.
            dataInBag[index] += 1;
        }
    }

    @Override
    protected void cleanupAlgorithm()
    {
        // Clear out the cached values.
        this.dataList = null;
        this.dataFullEstimates = null;
        this.dataOutOfBagEstimates = null;
        this.dataInBag = null;
        this.currentMember = null;
        this.currentCorrectIndices = null;
        this.currentIncorrectIndices = null;
        this.currentBag = null;
        this.currentEnsembleCorrect = null;
        this.currentMemberEstimates = null;
    }

    public WeightedVotingCategorizerEnsemble<InputType, CategoryType, Evaluator<? super InputType, ? extends CategoryType>>
        getResult()
    {
        return this.ensemble;
    }
    
    /**
     * Gets the learner used to learn each ensemble member.
     *
     * @return
     *      The learner used for each ensemble member.
     */
    public BatchLearner<? super Collection<? extends InputOutputPair<? extends InputType, CategoryType>>, ? extends Evaluator<? super InputType, ? extends CategoryType>>
        getLearner()
    {
        return this.learner;
    }

    /**
     * Sets the learner used to learn each ensemble member. Must be a supervised
     * learning algorithm that takes in a collection of input-output pairs of
     * the given data types and produces an evaluator for those data types.
     *
     * @param   learner
     *      The learner used for each ensemble member.
     */
    public void setLearner(
        final BatchLearner<? super Collection<? extends InputOutputPair<? extends InputType, CategoryType>>, ? extends Evaluator<? super InputType, ? extends CategoryType>> learner)
    {
        this.learner = learner;
    }

    /**
     * Gets the percentage of the total data to sample on each iteration.
     *
     * @return
     *      The percentage of the total data to sample on each iteration.
     */
    public double getPercentToSample()
    {
        return this.percentToSample;
    }

    /**
     * Sets the percentage of the data to sample (with replacement) on each
     * iteration. Must be greater than zero. The percent is represented as a
     * floating point number with 1.0 representing 100%.
     *
     * @param   percentToSample
     *      The percent of the data to sample on each iteration. Must be greater
     *      than zero. Defaults to 100%.
     */
    public void setPercentToSample(
        final double percentToSample)
    {
        if (percentToSample <= 0.0)
        {
            throw new IllegalArgumentException(
                "percentToSample must be greater than zero.");
        }

        this.percentToSample = percentToSample;
    }

    /**
     * Gets the proportion of incorrect examples to place in each sample.
     *
     * @return
     *      The proportion of incorrect examples in each sample.
     */
    public double getProportionIncorrectInSample()
    {
        return this.proportionIncorrectInSample;
    }

    /**
     * Sets the proportion of incorrect examples to place in each sample. Must
     * be between 0.0 and 1.0 (inclusive). The rest of the examples in the
     * sample will be filled from the correct examples.
     *
     * @param   proportionIncorrectInSample
     *      The proportion of incorrect examples in each sample. Must be between
     *      0.0 and 1.0 (inclusive).
     */
    public void setProportionIncorrectInSample(
        final double proportionIncorrectInSample)
    {
        if (    proportionIncorrectInSample < 0.0
             || proportionIncorrectInSample > 1.0)
        {
            throw new IllegalArgumentException(
                "proportionIncorrectInSample must be between 0.0 and 1.0 (inclusive).");
        }
        this.proportionIncorrectInSample = proportionIncorrectInSample;
    }

    /**
     * Gets whether during learning ensemble members can only vote on items
     * that they are not in their bag (training set).
     *
     * @return
     *      If out-of-bag-only voting is enabled.
     */
    public boolean isVoteOutOfBagOnly()
    {
        return voteOutOfBagOnly;
    }

    /**
     * Sets whether during learning ensemble members can only vote on items
     * that they are not in their bag (training set). In the vast majority
     * of cases, this should be enabled. It is enabled by default.
     *
     * @param   voteOutOfBagOnly
     *      If out-of-bag-only voting should be enabled.
     */
    public void setVoteOutOfBagOnly(
        final boolean voteOutOfBagOnly)
    {
        this.voteOutOfBagOnly = voteOutOfBagOnly;
    }

    /**
     * Gets the factory used for creating the object for counting the votes of
     * the learned ensemble members.
     *
     * @return
     *      The factory used to create the vote counting objects.
     */
    public Factory<? extends DataHistogram<CategoryType>> getCounterFactory()
    {
        return this.counterFactory;
    }
    
    /**
     * Sets the factory used for creating the object for counting the votes of
     * the learned ensemble members.
     *
     * @param   counterFactory
     *      The factory used to create the vote counting objects.
     */
    public void setCounterFactory(
        final Factory<? extends DataHistogram<CategoryType>> counterFactory)
    {
        this.counterFactory = counterFactory;
    }

    public Random getRandom()
    {
        return this.random;
    }

    public void setRandom(
        final Random random)
    {
        this.random = random;
    }

    /**
     * Gets the current estimates for each data point. Do not modify these
     * counts as they will change the algorithm.
     *
     * @return
     *      The current estimates for each data point.
     */
    public List<DataHistogram<CategoryType>> getDataFullEstimates()
    {
        return Collections.unmodifiableList(this.dataFullEstimates);
    }

    /**
     * Gets the current out-of-bag estimates for each data point. Do not modify
     * these counts as they will change the algorithm.
     *
     * @return
     *      The current out-of-bag estimates for each data point.
     */
    public List<DataHistogram<CategoryType>> getDataOutOfBagEstimates()
    {
        return Collections.unmodifiableList(this.dataOutOfBagEstimates);
    }

    /**
     * Gets whether or not the current ensemble gets each example correct.
     * Do not modify these values, as they will change the algorithm.
     *
     * @return
     *      The array of booleans regarding whether or not the ensemble gets
     *      an example correct.
     */
    public boolean[] getCurrentEnsembleCorrect()
    {
        return this.currentEnsembleCorrect;
    }

// TODO: Finish this out-of-bag stopping criteria.
// -- jdbasil (2009-12-23)
    public static class OutOfBagErrorStoppingCriteria<InputType, CategoryType>
        extends AbstractCloneableSerializable
        implements IterativeAlgorithmListener
    {
        /** The default smoothing window size is {@value}. */
        public static final int DEFAULT_SMOOTHING_WINDOW_SIZE = 25;

        /** The size of window of data to look at to determine if learning has
         *  hit a minimum. */
        protected int smoothingWindowSize;

        /** The learner the stopping criteria is for. */
        protected transient IVotingCategorizerLearner<InputType, CategoryType>
            learner;

        /** A boolean for each example indicating whether or not it is
         *  currently a correct or incorrect out-of-bag vote. This should be
         *  the same size as the collection of data. */
        protected transient boolean[] outOfBagCorrect;

        /** The total number of out-of-bag errors. This should equal the number
         *  of false values in the outOfBagCorrect array. */
        protected transient int outOfBagErrorCount;

        /** The raw out-of-bag error rate, per iteration. */
        protected transient ArrayList<Double> rawErrorRates;

        /** The smoothed out-of-bag error rates, per iteration. */
        protected transient ArrayList<Double> smoothedErrorRates;

        /** The buffer used for smoothing. */
        protected transient FiniteCapacityBuffer<Double> smoothingBuffer;

        /** The smoothed error rate of the previous iteration. */
        protected transient double previousSmoothedErrorRate;

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
            super();

            this.setSmoothingWindowSize(smoothingWindowSize);
        }

        @SuppressWarnings("unchecked")
        public void algorithmStarted(
            final IterativeAlgorithm algorithm)
        {
            this.learner = (IVotingCategorizerLearner<InputType, CategoryType>)
                algorithm;
            final int dataSize = this.learner.data.size();
            this.outOfBagCorrect = new boolean[dataSize];
            this.outOfBagErrorCount = dataSize;
            this.rawErrorRates = new ArrayList<Double>();
            this.smoothedErrorRates = new ArrayList<Double>();
            this.smoothingBuffer = new FiniteCapacityBuffer<Double>(
                this.smoothingWindowSize);
            this.previousSmoothedErrorRate = Double.MAX_VALUE;
        }

        public void algorithmEnded(
            final IterativeAlgorithm algorithm)
        {
            this.learner = null;
            this.outOfBagCorrect = null;
            this.rawErrorRates = null;
            this.smoothedErrorRates = null;
            this.smoothingBuffer = null;
        }

        public void stepStarted(
            final IterativeAlgorithm algorithm)
        {
            // Ignored.
        }

        public void stepEnded(
            final IterativeAlgorithm algorithm)
        {
            // Go through the data and update the values for the data that was
            // not in the bag.
            final int dataSize = learner.data.size();
            for (int i = 0; i < dataSize; i++)
            {
                if (learner.dataInBag[i] <= 0)
                {
                    // Get the actual category.
                    final CategoryType actual =
                        learner.dataList.get(i).getOutput();
                    
                    // Get the out-of-bag-votes to determine the ensemble's
                    // guess.
                    final DataHistogram<CategoryType> outOfBagVotes =
                        learner.dataOutOfBagEstimates.get(i);
                    final CategoryType ensembleGuess =
                        outOfBagVotes.getMaximumValue();

                    // Update whether or not the ensemble is getting this item
                    // correct.
                    final boolean oldEnsembleCorrect = this.outOfBagCorrect[i];
                    final boolean newEnsembleCorrect = 
                        ObjectUtil.equalsSafe(actual, ensembleGuess);

                    if (oldEnsembleCorrect != newEnsembleCorrect)
                    {
                        // Save the new correctness.
                        this.outOfBagCorrect[i] = newEnsembleCorrect;

                        // Update the error count.
                        if (newEnsembleCorrect)
                        {
                            this.outOfBagErrorCount--;
                        }
                        else
                        {
                            this.outOfBagErrorCount++;
                        }
                    }

                }
            }

            // Compute the out-of-bag error rate for the ensemble.
            final double outOfBagEnsembleErrorRate = 
                (double) this.outOfBagErrorCount / this.learner.data.size();

            // Store this and compute the smoothed error rate.
            this.rawErrorRates.add(outOfBagEnsembleErrorRate);
            this.smoothingBuffer.add(outOfBagEnsembleErrorRate);
            final double smoothedErrorRate =
                UnivariateStatisticsUtil.computeMean(this.smoothingBuffer);
            this.smoothedErrorRates.add(smoothedErrorRate);

//System.out.println("" + learner.getIteration() + "\t" + outOfBagEnsembleErrorRate + "\t" + this.outOfBagErrorCount + "\t" + smoothedErrorRate);

            // See if the algorithm is still making progress or not. Once the
            // smoothed error rate stops improving, its time to stop.
            if (smoothedErrorRate >= this.previousSmoothedErrorRate)
            {
                // Stop the learning since it is no longer improving.
                this.learner.stop();

                // Now we need to figure out where the ensemble had the best
                // performance from the smoothing buffer
                final int ensembleSize = this.rawErrorRates.size();
                int bestIndex = 0;
                double bestRawErrorRate = Double.MAX_VALUE;
                for (int i = 0; i < this.smoothingBuffer.size(); i++)
                {
                    // Walk the ensemble backwards.
                    final int index = ensembleSize - i - 1;
                    final double rawErrorRate = this.rawErrorRates.get(index);
                    if (rawErrorRate <= bestRawErrorRate)
                    {
                        bestIndex = index;
                        bestRawErrorRate = rawErrorRate;
                    }
                }

                // Now that we know which index was the best, we need to rewind
                // the ensemble to that point by removing all the members
                // added after it.
                for (int i = ensembleSize - 1; i > bestIndex; i--)
                {
                    learner.ensemble.members.remove(i);
                }
//System.out.println("Best index: " + bestIndex);
//System.out.println("Best raw error rate: " + bestRawErrorRate);
//System.out.println("Ensemble now has " + learner.ensemble.members.size() + " members.");
            }

            // Save the smoothed error rate.
            this.previousSmoothedErrorRate = smoothedErrorRate;

/*
            System.out.println("Step " + learner.getIteration());
            System.out.println("    Num incorrect: " + learner.currentIncorrectIndices.size());
//            System.out.println("    Out of bag count: " + outOfBagTotalCount);
            System.out.println("    Out of bag ensemble error count: " + this.outOfBagErrorCount);
            System.out.println("    Out of bag ensemble error rate: " + outOfBagEnsembleErrorRate);
            System.out.println("    Out of bag ensemble accuracy: " + (1.0 - outOfBagEnsembleErrorRate));
 */
        }

        /**
         * Gets the size of the smoothing window.
         *
         * @return
         *      The size of the smoothing window.
         */
        public int getSmoothingWindowSize()
        {
            return smoothingWindowSize;
        }

        /**
         * Sets the smoothing window size.
         *
         * @param   smoothingWindowSize
         *      The smoothing window size. Must be positive.
         */
        public void setSmoothingWindowSize(
            final int smoothingWindowSize)
        {
            if (smoothingWindowSize < 0)
            {
                throw new IllegalArgumentException(
                    "smoothingWindowSize must be positive.");
            }

            this.smoothingWindowSize = smoothingWindowSize;
        }


    }

}
