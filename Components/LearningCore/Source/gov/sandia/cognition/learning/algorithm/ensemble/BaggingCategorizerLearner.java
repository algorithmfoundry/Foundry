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

import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.collection.CollectionUtil;
import gov.sandia.cognition.evaluator.Evaluator;
import gov.sandia.cognition.learning.algorithm.AbstractAnytimeSupervisedBatchLearner;
import gov.sandia.cognition.learning.algorithm.BatchLearner;
import gov.sandia.cognition.learning.algorithm.BatchLearnerContainer;
import gov.sandia.cognition.learning.data.DatasetUtil;
import gov.sandia.cognition.learning.data.InputOutputPair;
import gov.sandia.cognition.util.Randomized;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

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
    extends AbstractAnytimeSupervisedBatchLearner<InputType, CategoryType, WeightedVotingCategorizerEnsemble<InputType, CategoryType, Evaluator<? super InputType, ? extends CategoryType>>>
    implements Randomized,
        BatchLearnerContainer<BatchLearner<? super Collection<? extends InputOutputPair<? extends InputType, CategoryType>>, ? extends Evaluator<? super InputType, ? extends CategoryType>>>
{

    /** The default maximum number of iterations is {@value}. */
    public static final int DEFAULT_MAX_ITERATIONS = 100;

    /** The default percent to sample is 1.0 (which represents 100%). */
    public static final double DEFAULT_PERCENT_TO_SAMPLE = 1.0;

    /** The learner to use to create the categorizer for each iteration. */
    protected BatchLearner<? super Collection<? extends InputOutputPair<? extends InputType, CategoryType>>,
        ? extends Evaluator<? super InputType, ? extends CategoryType>> learner;

    /** The percentage of the data to sample with replacement on each iteration.
     *  Must be positive. Represented as a floating point number with 1.0
     *  meaning 100%.
     */
    protected double percentToSample;

    /** The random number generator to use. */
    protected Random random;

    /** The ensemble being created by the learner. */
    protected transient WeightedVotingCategorizerEnsemble<InputType, CategoryType, Evaluator<? super InputType, ? extends CategoryType>> ensemble;

    /** The data stored for efficient random access. */
    protected transient ArrayList<? extends InputOutputPair<? extends InputType, CategoryType>> dataList;

    /** An indicator of whether or not the data is in the current bag. */
    protected transient int[] dataInBag;

    /** The current bag of data. */
    protected transient ArrayList<InputOutputPair<? extends InputType, CategoryType>> bag;

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
        super(maxIterations);

        this.setLearner(learner);
        this.setPercentToSample(percentToSample);
        this.setRandom(random);

        this.setEnsemble(null);
        this.dataList = null;
        this.dataInBag = null;
        this.bag = null;
    }

    protected boolean initializeAlgorithm()
    {
        final int dataSize = CollectionUtil.size(this.getData());
        if (dataSize <= 0)
        {
            // This is an invalid dataset.
            return false;
        }

        if (this.random == null)
        {
            this.random = new Random();
        }

        // Create the ensemble where we will be storing the output.
        final WeightedVotingCategorizerEnsemble<InputType, CategoryType, Evaluator<? super InputType, ? extends CategoryType>>
            localEmsemble = new WeightedVotingCategorizerEnsemble<InputType, CategoryType, Evaluator<? super InputType, ? extends CategoryType>>(
                DatasetUtil.findUniqueOutputs(this.getData()));
        this.setEnsemble(localEmsemble);

        // Create a random-access version of the data.
        this.dataList = CollectionUtil.asArrayList(this.getData());
        this.dataInBag = new int[dataSize];
        this.bag = new ArrayList<InputOutputPair<? extends InputType, CategoryType>>();

        return true;
    }

    protected boolean step()
    {
        // Figure out how many to sample.
        final int dataSize = this.dataList.size();
        final int sampleCount = Math.max(1, (int) (this.percentToSample * dataSize));

        // Clear out the bag from the previous iteration.
        this.bag.clear();
        for (int i = 0; i < dataSize; i++)
        {
            this.dataInBag[i] = 0;
        }

        // Fill the bag.
        this.fillBag(sampleCount);

        // Learn the categorizer on the new bag of data.
        final Evaluator<? super InputType,? extends CategoryType> learned =
            this.learner.learn(this.bag);

        // Add the categorizer to the ensemble and give it equal weight.
        this.ensemble.add(learned, 1.0);

        // We keep going until we've created the requested number of members,
        // which is checked by the super-class.
        return true;
    }

    /**
     * Fills the internal bag field by sampling the given number of samples.
     *
     * @param   sampleCount
     *      The number to sample.
     */
    protected void fillBag(
        final int sampleCount)
    {
        final int dataSize = this.dataList.size();

        // Create the bag by sampling with replacement.
        for (int i = 0; i < sampleCount; i++)
        {
            final int index = this.getRandom().nextInt(dataSize);
            final InputOutputPair<? extends InputType, CategoryType> example =
                this.dataList.get(index);
            this.bag.add(example);
            this.dataInBag[index] += 1;
        }
    }

    protected void cleanupAlgorithm()
    {
        // To clean up we remove the reference to the copy of the data
        // collection that we made.
        this.dataList = null;
        this.dataInBag = null;
        this.bag = null;
    }

    /**
     * Gets the ensemble created by this learner.
     *
     * @return The ensemble created by this learner.
     */
    public WeightedVotingCategorizerEnsemble<InputType, CategoryType, Evaluator<? super InputType, ? extends CategoryType>> getResult()
    {
        // The result is the ensemble.
        return this.ensemble;
    }

    /**
     * Gets the learner used to learn each ensemble member.
     *
     * @return
     *      The learner used for each ensemble member.
     */
    public BatchLearner<? super Collection<? extends InputOutputPair<? extends InputType, CategoryType>>, ? extends Evaluator<? super InputType, ? extends CategoryType>> getLearner()
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
        return percentToSample;
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
     * Sets the ensemble created by this learner.
     *
     * @param  ensemble The ensemble created by this learner.
     */
    protected void setEnsemble(
        final WeightedVotingCategorizerEnsemble<InputType, CategoryType, Evaluator<? super InputType, ? extends CategoryType>> ensemble)
    {
        this.ensemble = ensemble;
    }

    /**
     * Gets the most recently created bag.
     *
     * @return
     *      The most recently created bag.
     */
    public ArrayList<InputOutputPair<? extends InputType, CategoryType>> getBag()
    {
        return this.bag;
    }

}

