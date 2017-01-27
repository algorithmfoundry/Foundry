/*
 * File:            AbstractCategorizerOutOfBagStoppingCriteria.java
 * Authors:         Justin Basilico
 * Project:         Cognitive Foundry Learning Core
 * 
 * Copyright 2017 Cognitive Foundry. All rights reserved.
 */

package gov.sandia.cognition.learning.algorithm.ensemble;

import gov.sandia.cognition.algorithm.IterativeAlgorithm;
import gov.sandia.cognition.algorithm.event.AbstractIterativeAlgorithmListener;
import gov.sandia.cognition.collection.FiniteCapacityBuffer;
import gov.sandia.cognition.math.UnivariateStatisticsUtil;
import gov.sandia.cognition.statistics.DataDistribution;
import gov.sandia.cognition.util.ObjectUtil;
import java.util.ArrayList;

/**
 * Abstract class for implementing a out-of-bag stopping criteria for a
 * bagging-based ensemble.
 * 
 * @param   <InputType>
 *      The type of the input for the categorizer to learn.
 * @param   <CategoryType>
 *      The type of the category that is the output for the categorizer to
 *      learn.
 * 
 * @author  Justin Basilico
 * @since   3.4.4
 */
public abstract class AbstractCategorizerOutOfBagStoppingCriteria<InputType, CategoryType>
    extends AbstractIterativeAlgorithmListener
{
// TODO: Implement a look-ahead capability.
// -- jdbasil (2011-02-18)
    /** The default smoothing window size is {@value}. */
    public static final int DEFAULT_SMOOTHING_WINDOW_SIZE = 25;

    /** The size of window of data to look at to determine if learning has
     *  hit a minimum. */
    protected int smoothingWindowSize;

    /** The learner the stopping criteria is for. */
    protected transient BagBasedCategorizerEnsembleLearner<InputType, CategoryType>
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
    public AbstractCategorizerOutOfBagStoppingCriteria()
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
    public AbstractCategorizerOutOfBagStoppingCriteria(
        final int smoothingWindowSize)
    {
        super();

        this.setSmoothingWindowSize(smoothingWindowSize);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void algorithmStarted(
        final IterativeAlgorithm algorithm)
    {
        this.learner = (BagBasedCategorizerEnsembleLearner<InputType, CategoryType>)
            algorithm;
        final int dataSize = this.learner.getData().size();
        this.outOfBagCorrect = new boolean[dataSize];
        this.outOfBagErrorCount = dataSize;
        this.rawErrorRates = new ArrayList<>();
        this.smoothedErrorRates = new ArrayList<>();
        this.smoothingBuffer = new FiniteCapacityBuffer<>(
            this.smoothingWindowSize);
        this.previousSmoothedErrorRate = Double.MAX_VALUE;
    }

    @Override
    public void algorithmEnded(
        final IterativeAlgorithm algorithm)
    {
        this.learner = null;
        this.outOfBagCorrect = null;
        this.rawErrorRates = null;
        this.smoothedErrorRates = null;
        this.smoothingBuffer = null;
    }

    public abstract DataDistribution<CategoryType> getOutOfBagEstimate(
        final int index);
    
    @Override
    public void stepEnded(
        final IterativeAlgorithm algorithm)
    {
        // Go through the data and update the values for the data that was
        // not in the bag.
        final int dataSize = learner.getData().size();
        final int[] dataInBag = learner.getDataInBag();
        
        for (int i = 0; i < dataSize; i++)
        {
            if (dataInBag[i] <= 0)
            {
                // Get the actual category.
                final CategoryType actual =
                    learner.getExample(i).getOutput();

                // Get the out-of-bag-votes to determine the ensemble's
                // guess.
                final DataDistribution<CategoryType> outOfBagVotes =
                    this.getOutOfBagEstimate(i);
                final CategoryType ensembleGuess =
                    outOfBagVotes.getMaxValueKey();

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
            (double) this.outOfBagErrorCount / this.learner.getData().size();

        // Store this and compute the smoothed error rate.
        this.rawErrorRates.add(outOfBagEnsembleErrorRate);
        this.smoothingBuffer.add(outOfBagEnsembleErrorRate);
        final double smoothedErrorRate =
            UnivariateStatisticsUtil.computeMean(this.smoothingBuffer);
        this.smoothedErrorRates.add(smoothedErrorRate);

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
                learner.getResult().members.remove(i);
            }
        }

        // Save the smoothed error rate.
        this.previousSmoothedErrorRate = smoothedErrorRate;

    }

    /**
     * Gets the size of the smoothing window.
     *
     * @return
     *      The size of the smoothing window.
     */
    public int getSmoothingWindowSize()
    {
        return this.smoothingWindowSize;
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

