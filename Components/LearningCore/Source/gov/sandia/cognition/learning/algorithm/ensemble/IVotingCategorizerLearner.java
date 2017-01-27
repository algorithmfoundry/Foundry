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
import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.collection.CollectionUtil;
import gov.sandia.cognition.evaluator.Evaluator;
import gov.sandia.cognition.factory.Factory;
import gov.sandia.cognition.learning.algorithm.AbstractAnytimeSupervisedBatchLearner;
import gov.sandia.cognition.learning.algorithm.BatchLearner;
import gov.sandia.cognition.learning.algorithm.BatchLearnerContainer;
import gov.sandia.cognition.learning.data.DatasetUtil;
import gov.sandia.cognition.learning.data.InputOutputPair;
import gov.sandia.cognition.statistics.DataDistribution;
import gov.sandia.cognition.statistics.distribution.DefaultDataDistribution;
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
@PublicationReference(
  author="Leo Breiman",
  title="Pasting small votes for classification in large databases and on-line",
  year=1999,
  type=PublicationType.Journal,
  publication="Machine Learning",
  pages={85, 103},
  url="http://www.springerlink.com/content/mnu2r28218651707/fulltext.pdf"
)
public class IVotingCategorizerLearner<InputType, CategoryType>
    extends AbstractAnytimeSupervisedBatchLearner<InputType, CategoryType, WeightedVotingCategorizerEnsemble<InputType, CategoryType, Evaluator<? super InputType, ? extends CategoryType>>>
    implements Randomized,
        BatchLearnerContainer<BatchLearner<? super Collection<? extends InputOutputPair<? extends InputType, CategoryType>>, ? extends Evaluator<? super InputType, ? extends CategoryType>>>,
        BagBasedCategorizerEnsembleLearner<InputType, CategoryType>
{
    /** The default maximum number of iterations is {@value}. */
    public static final int DEFAULT_MAX_ITERATIONS = 100;

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
    protected Factory<? extends DataDistribution<CategoryType>> counterFactory;

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
    protected transient ArrayList<DataDistribution<CategoryType>> dataFullEstimates;

    /** The running estimate of the ensemble for each example where an ensemble
     *  member can only vote on elements that were not in the bag used to train
     * it. */
    protected transient ArrayList<DataDistribution<CategoryType>> dataOutOfBagEstimates;

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
            new DefaultDataDistribution.DefaultFactory<CategoryType>(2),
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
        final Factory<? extends DataDistribution<CategoryType>> counterFactory,
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
            new ArrayList<DataDistribution<CategoryType>>(dataSize);
        this.dataOutOfBagEstimates =
            new ArrayList<DataDistribution<CategoryType>>(dataSize);

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
            this.dataFullEstimates.add(new DefaultDataDistribution<CategoryType>(2));
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
        this.createBag(correctIndices, incorrectIndices);

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
            final DataDistribution<CategoryType> fullEstimate =
                this.dataFullEstimates.get(i);

            // Get the out-of-bag estimate for the current item.
            final DataDistribution<CategoryType> outOfBagEstimate =
                this.dataOutOfBagEstimates.get(i);
            
            if (memberGuess != null)
            {
                // Update the full estimate.
                fullEstimate.increment(memberGuess);

                if (this.dataInBag[i] <= 0)
                {
                    // Add to the out-of-bag estimate for the item.
                    outOfBagEstimate.increment(memberGuess);
                }
            }
            // else - The member had nothing to contribute for the estimate.

            // Get the new ensemble guess.
            CategoryType ensembleGuess = null;

            // See if we're guessing based on out-of-bag only.
            if (this.voteOutOfBagOnly && outOfBagEstimate.getTotal() > 0)
            {
                ensembleGuess = outOfBagEstimate.getMaxValueKey();
            }
            else
            {
                // Either we're not doing out-of-bag or there are no out-of-bag
                // votes for this item, so we use the full ensemble estimate.
                ensembleGuess = fullEstimate.getMaxValueKey();
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
     * Create the next sample (bag) of examples to learn the next ensemble
     * member from.
     *
     * @param   correctIndices
     *      The list of indices the ensemble is currently getting correct.
     * @param   incorrectIndices
     *      The list of indices the ensemble is currently getting incorrect.
     */
    protected void createBag(
        final ArrayList<Integer> correctIndices,
        final ArrayList<Integer> incorrectIndices)
    {
        sampleIndicesWithReplacementInto(correctIndices, this.dataList,
            this.numCorrectToSample, this.random,
            this.currentBag, this.dataInBag);
        sampleIndicesWithReplacementInto(incorrectIndices, this.dataList,
            this.numIncorrectToSample, this.random,
            this.currentBag, this.dataInBag);
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
    public Factory<? extends DataDistribution<CategoryType>> getCounterFactory()
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
        final Factory<? extends DataDistribution<CategoryType>> counterFactory)
    {
        this.counterFactory = counterFactory;
    }

    @Override
    public Random getRandom()
    {
        return this.random;
    }

    @Override
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
    public List<DataDistribution<CategoryType>> getDataFullEstimates()
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
    public List<DataDistribution<CategoryType>> getDataOutOfBagEstimates()
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
    
    /**
     * Implements a stopping criteria for IVoting that uses the out-of-bag
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

        /** The learner the stopping criteria is for. */
        protected transient IVotingCategorizerLearner<InputType, CategoryType>
            learner;
    
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
            this.learner = (IVotingCategorizerLearner<InputType, CategoryType>) algorithm;
            super.algorithmStarted(algorithm);
        }
        
        @Override
        public DataDistribution<CategoryType> getOutOfBagEstimate(
            final int index)
        {
            return this.learner.getDataOutOfBagEstimates().get(index);
        }
        
    }

}
