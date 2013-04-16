/*
 * File:            AbstractBaggingLearner.java
 * Authors:         Justin Basilico
 * Project:         Cognitive Foundry Learning Core
 * 
 * Copyright 2011 Cognitive Foundry. All rights reserved.
 */

package gov.sandia.cognition.learning.algorithm.ensemble;

import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.collection.CollectionUtil;
import gov.sandia.cognition.evaluator.Evaluator;
import gov.sandia.cognition.learning.algorithm.AbstractAnytimeSupervisedBatchLearner;
import gov.sandia.cognition.learning.algorithm.BatchLearner;
import gov.sandia.cognition.learning.algorithm.BatchLearnerContainer;
import gov.sandia.cognition.learning.data.InputOutputPair;
import gov.sandia.cognition.util.ArgumentChecker;
import gov.sandia.cognition.util.Randomized;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

/**
 * Learns an ensemble by randomly sampling with replacement
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
 * @param   <OutputType>
 *      The output type for supervised learning. Passed on to the internal
 *      learning algorithm. Also the output type of the learned ensemble.
 * @param   <MemberType>
 *      The type of ensemble member created by the inner learning algorithm.
 *      Usually an evaluator.
 * @param   <EnsembleType>
 *      The type of ensemble that the algorithm fills with ensemble members.
 * @author  Justin Basilico
 * @version 3.4.0
 */
@PublicationReference(
    title="Bagging Predictors",
    author="Leo Breiman",
    year=1996,
    type=PublicationType.Journal,
    publication="Machine Learning",
    pages={123, 140},
    url="http://www.springerlink.com/index/L4780124W2874025.pdf")
public abstract class AbstractBaggingLearner<InputType, OutputType, MemberType, EnsembleType extends Evaluator<? super InputType, ? extends OutputType>>
    extends AbstractAnytimeSupervisedBatchLearner<InputType, OutputType, EnsembleType>
    implements Randomized,
        BatchLearnerContainer<BatchLearner<? super Collection<? extends InputOutputPair<? extends InputType, OutputType>>, ? extends MemberType>>
{

    /** The default maximum number of iterations is {@value}. */
    public static final int DEFAULT_MAX_ITERATIONS = 100;

    /** The default percent to sample is 1.0 (which represents 100%). */
    public static final double DEFAULT_PERCENT_TO_SAMPLE = 1.0;

    /** The learner to use to create the categorizer for each iteration. */
    protected BatchLearner<? super Collection<? extends InputOutputPair<? extends InputType, OutputType>>,
        ? extends MemberType> learner;

    /** The percentage of the data to sample with replacement on each iteration.
     *  Must be positive. Represented as a floating point number with 1.0
     *  meaning 100%.
     */
    protected double percentToSample;

    /** The random number generator to use. */
    protected Random random;

    /** The ensemble being created by the learner. */
    protected transient EnsembleType ensemble;

    /** The data stored for efficient random access. */
    protected transient ArrayList<? extends InputOutputPair<? extends InputType, OutputType>> dataList;

    /** An indicator of whether or not the data is in the current bag. */
    protected transient int[] dataInBag;

    /** The current bag of data. */
    protected transient ArrayList<InputOutputPair<? extends InputType, OutputType>> bag;

    /**
     * Creates a new instance of AbstractBaggingLearner.
     */
    public AbstractBaggingLearner()
    {
        this(null);
    }

    /**
     * Creates a new instance of AbstractBaggingLearner.
     *
     * @param  learner
     *      The learner to use to create the ensemble member on each iteration.
     */
    public AbstractBaggingLearner(
        final BatchLearner<? super Collection<? extends InputOutputPair<? extends InputType, OutputType>>, ? extends MemberType> learner)
    {
        this(learner, DEFAULT_MAX_ITERATIONS, DEFAULT_PERCENT_TO_SAMPLE, new Random());
    }

    /**
     * Creates a new instance of AbstractBaggingLearner.
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
    public AbstractBaggingLearner(
        final BatchLearner<? super Collection<? extends InputOutputPair<? extends InputType, OutputType>>, ? extends MemberType> learner,
        final int maxIterations,
        final double percentToSample,
        final Random random)
    {
        super(maxIterations);

        this.setLearner(learner);
        this.setPercentToSample(percentToSample);
        this.setRandom(random);

        this.setEnsemble(null);
        this.setDataList(null);
        this.setDataInBag(null);
        this.setBag(null);
    }

    @Override
    protected boolean initializeAlgorithm()
    {
        final int dataSize = CollectionUtil.size(this.getData());
        if (dataSize <= 0)
        {
            // This is an invalid dataset.
            return false;
        }

        if (this.getRandom() == null)
        {
            this.setRandom(new Random());
        }

        // Create a random-access version of the data.
        this.setDataList(CollectionUtil.asArrayList(this.getData()));
        this.setDataInBag(new int[dataSize]);
        this.setBag(new ArrayList<InputOutputPair<? extends InputType, OutputType>>());

        // Create the ensemble where we will be storing the output.
        this.setEnsemble(this.createInitialEnsemble());

        return true;
    }

    @Override
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
        final MemberType learned = this.learner.learn(this.bag);

        // Add the categorizer to the ensemble and give it equal weight.
        this.addEnsembleMember(learned);

        // We keep going until we've created the requested number of members,
        // which is checked by the super-class.
        return true;
    }

    /**
     * Create the initial, empty ensemble for the algorithm to use.
     *
     * @return
     *      A new ensemble for the algorithm to use.
     */
    protected abstract EnsembleType createInitialEnsemble();

    /**
     * Adds a new member to the ensemble.
     *
     * @param   member
     *      The new member to add to the ensemble.
     */
    protected abstract void addEnsembleMember(
        final MemberType member);

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
            final InputOutputPair<? extends InputType, OutputType> example =
                this.dataList.get(index);
            this.bag.add(example);
            this.dataInBag[index] += 1;
        }
    }

    @Override
    protected void cleanupAlgorithm()
    {
        // To clean up we remove the reference to the copy of the data
        // collection that we made.
        this.setDataList(null);
        this.setDataInBag(null);
        this.setBag(null);
    }

    /**
     * Gets the ensemble created by this learner.
     *
     * @return The ensemble created by this learner.
     */
    @Override
    public EnsembleType getResult()
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
    @Override
    public BatchLearner<? super Collection<? extends InputOutputPair<? extends InputType, OutputType>>, ? extends MemberType> getLearner()
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
        final BatchLearner<? super Collection<? extends InputOutputPair<? extends InputType, OutputType>>, ? extends MemberType> learner)
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
        ArgumentChecker.assertIsPositive("percentToSample", percentToSample);
        this.percentToSample = percentToSample;
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
     * Gets the ensemble created by this learner.
     *
     * @return
     *      The ensemble created by this learner.
     */
    public EnsembleType getEnsemble()
    {
        return this.ensemble;
    }

    /**
     * Sets the ensemble created by this learner.
     *
     * @param  ensemble
     *      The ensemble created by this learner.
     */
    protected void setEnsemble(
        final EnsembleType ensemble)
    {
        this.ensemble = ensemble;
    }

    /**
     * Gets the data the learner is using as an array list.
     *
     * @return The data as an array list.
     */
    public ArrayList<? extends InputOutputPair<? extends InputType, OutputType>>
        getDataList()
    {
        return this.dataList;
    }

    /**
     * Sets the data the learner is using as an array list.
     *
     * @param  dataList The data as an array list.
     */
    protected void setDataList(
        final ArrayList<? extends InputOutputPair<? extends InputType, OutputType>> dataList)
    {
        this.dataList = dataList;
    }

    /**
     * Gets the array of counts of the number of samples of each example in
     * the current bag.
     * 
     * @return
     *      The bag counts.
     */
    public int[] getDataInBag()
    {
        return dataInBag;
    }

    /**
     * Sets the array of counts of the number of samples of each example in
     * the current bag.
     *
     * @param   dataInBag
     *      The bag counts.
     */
    protected void setDataInBag(
        final int[] dataInBag)
    {
        this.dataInBag = dataInBag;
    }

    /**
     * Gets the most recently created bag.
     *
     * @return
     *      The most recently created bag.
     */
    public ArrayList<InputOutputPair<? extends InputType, OutputType>> getBag()
    {
        return this.bag;
    }

    /**
     * Sets the most recently created bag.
     *
     * @param   bag
     *      The most recently created bag.
     */
    protected void setBag(
        final ArrayList<InputOutputPair<? extends InputType, OutputType>> bag)
    {
        this.bag = bag;
    }

}

