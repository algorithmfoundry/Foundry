/*
 * File:                PrimalEstimatedSubGradient.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright January 18, 2010, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 * See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.learning.algorithm.svm;

import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.collection.CollectionUtil;
import gov.sandia.cognition.learning.algorithm.AbstractAnytimeSupervisedBatchLearner;
import gov.sandia.cognition.learning.data.DatasetUtil;
import gov.sandia.cognition.learning.data.InputOutputPair;
import gov.sandia.cognition.learning.function.categorization.LinearBinaryCategorizer;
import gov.sandia.cognition.statistics.DiscreteSamplingUtil;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.math.matrix.Vectorizable;
import gov.sandia.cognition.util.ArgumentChecker;
import gov.sandia.cognition.util.Randomized;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * An implementation of the Primal Estimated Sub-Gradient Solver (PEGASOS)
 * algorithm for learning a linear support vector machine (SVM).
 * 
 * @author  Justin Basilico
 * @since   3.1
 */
@PublicationReference(
    author={"Shai Shalev-Shwartz", "Yoram Singer", "Nathan Srebro"},
    title="Pegasos: Primal Estimated sub-GrAdient SOlver for SVM",
    year=2007,
    type=PublicationType.Conference,
    publication="Proceedings of the 24th International Conference on Machine Learning",
    url="http://citeseerx.ist.psu.edu/viewdoc/summary?doi=10.1.1.74.8513"
)
public class PrimalEstimatedSubGradient
    extends AbstractAnytimeSupervisedBatchLearner<Vectorizable, Boolean, LinearBinaryCategorizer>
    implements Randomized
{

    /** The default sample size is {@value}. */
    public static final int DEFAULT_SAMPLE_SIZE = 100;

    /** The default regularization weight is {@value}. */
    public static final double DEFAULT_REGULARIZATION_WEIGHT = 0.0001;

    /** The default maximum number of iterations is {@value}. */
    public static final int DEFAULT_MAX_ITERATIONS = 10000;

    /** The sample size requested by the user. The actual sample size may be
     *  less than this in the case that the sample size is larger than the
     *  amount of data given in the training set. */
    protected int sampleSize;

    /** The weight assigned to the regularization term in the algorithm, which
     *  is often represented as lambda. */
    protected double regularizationWeight;

    /** The random number generator to use. */
    protected Random random;

    /** The size of the data in the training set. */
    protected transient int dataSize;

    /** The data represented as a list. */
    protected transient ArrayList<? extends InputOutputPair<? extends Vectorizable, Boolean>> dataList;

    /** The dimensionality of the dataset. */
    protected transient int dimensionality;

    /** The minimum of the sample size and the data size. */
    protected transient int dataSampleSize;

    /** A vector used to compute the update for the weight vector. It acts as a
     *  workspace so that multiple vectors do not need to be created in the
     *  algorithm, thus reducing the overall number of objects created. */
    protected transient Vector update;

    /** The categorizer learned as a result of the algorithm. */
    protected transient LinearBinaryCategorizer result;

    /**
     * Creates a new {@code PrimalEstimatedSubGradient} with default parameters.
     */
    public PrimalEstimatedSubGradient()
    {
        this(DEFAULT_SAMPLE_SIZE, DEFAULT_REGULARIZATION_WEIGHT,
            DEFAULT_MAX_ITERATIONS, new Random());
    }

    /**
     * Creates a new {@code PrimalEstimatedSubGradient} with the given
     * parameters.
     *
     * @param   sampleSize
     *      The number of examples sampled from the dataset on each iteration.
     * @param   regularizationWeight
     *      The regularization weight (lambda). Must be positive.
     * @param   maxIterations
     *      The maximum number of iterations. Must be positive.
     * @param   random
     *      The random number generator to use.
     */
    public PrimalEstimatedSubGradient(
        final int sampleSize,
        final double regularizationWeight,
        final int maxIterations,
        final Random random)
    {
        super(maxIterations);

        this.setSampleSize(sampleSize);
        this.setRegularizationWeight(regularizationWeight);
        this.setRandom(random);
    }

    @Override
    protected boolean initializeAlgorithm()
    {
        // Figure out if there is enough data to run the algorithm.
        if (CollectionUtil.isEmpty(this.data))
        {
            // Can't run the algorithm on empty data.
            return false;
        }

        this.dataSize = this.data.size();
        this.dataList = CollectionUtil.asArrayList(this.data);
        this.dimensionality = DatasetUtil.getInputDimensionality(this.data);
        this.dataSampleSize = Math.min(dataSize, this.sampleSize);

        // Compute a vector to store the update that gets reused between steps.
        final VectorFactory<?> vectorFactory = VectorFactory.getDenseDefault();
        this.update = vectorFactory.createVector(this.dimensionality);

        // Create initial random weights.
        final double lambda = this.regularizationWeight;
        final double sqrtLambda = Math.sqrt(lambda);
        final double initializationRange =
            1.0 / (this.dimensionality * sqrtLambda);
        final Vector initialWeights =
            vectorFactory.createUniformRandom(this.dimensionality,
                -initializationRange, initializationRange, this.random);
        if (initialWeights.norm2() < (1.0 / sqrtLambda))
        {
            initialWeights.unitVectorEquals();
            initialWeights.scaleEquals(1.0 / sqrtLambda);
        }

        this.result = new LinearBinaryCategorizer(initialWeights, 0.0);

        // Compute a vector to store the update that gets reused between steps.
        this.update = vectorFactory.createVector(this.dimensionality);

        return true;
    }

    @Override
    protected boolean step()
    {
        // Sample a sub-set of the dataset.
        final List<? extends InputOutputPair<? extends Vectorizable, Boolean>>
            subSet = DiscreteSamplingUtil.sampleWithoutReplacement(
                random, dataList, dataSampleSize);

        // Compute the learning rate (eta).
        final double lambda = this.regularizationWeight;
        final double learningRate = 1.0 / (lambda * this.iteration);

        // Compute the update to the weight vector.
        this.update.zero();
        double biasUpdate = 0.0;
        int errorCount = 0;
        for (InputOutputPair<? extends Vectorizable, Boolean> example : subSet)
        {
            final boolean output = example.getOutput();
            final double actual = output ? +1.0 : -1.0;
            final double predicted = this.result.evaluateAsDouble(
                example.getInput());

            if (actual * predicted < 1.0)
            {
                // An error occurred.
                errorCount++;

                // Increment the update vector.
                final Vector input = example.getInput().convertToVector();
                if (output)
                {
                    this.update.plusEquals(input);
                }
                else
                {
                    this.update.minusEquals(input);
                }
                biasUpdate += actual;
            }
            // else - No update required.
        }

        // Update the weights.
        final Vector weights = this.result.getWeights();

        // Regularization shrinkage.
        weights.scaleEquals(1.0 - (learningRate * lambda));

        // Apply update.
        final double stepSize = learningRate / subSet.size();
        this.update.scaleEquals(stepSize);
        weights.plusEquals(this.update);

        // Bias doesn't get regularized or projected.
        biasUpdate *= stepSize;
        double bias = this.result.getBias() + biasUpdate;

        // w_t+1 = min{1, (1 / sqrt(lambda)) / ||w_t+1/2||)} w_t+1/2
        final double norm2Squared = weights.norm2Squared();
        final double projection = 1.0 / Math.sqrt(lambda * norm2Squared);
        if (projection < 1.0)
        {
            weights.scaleEquals(projection);
        }

        this.result.setWeights(weights);
        this.result.setBias(bias);

        return true;
    }

    @Override
    protected void cleanupAlgorithm()
    {
        this.dataList = null;
        this.update = null;
    }

    @Override
    public LinearBinaryCategorizer getResult()
    {
        return this.result;
    }

    /**
     * Gets the sample size, which is the number of examples sampled without
     * replacement on each iteration of the algorithm.
     *
     * @return
     *      The sample size. Must be positive.
     */
    public int getSampleSize()
    {
        return this.sampleSize;
    }

    /**
     * Sets the sample size, which is the number of examples sampled without
     * replacement on each iteration of the algorithm.
     *
     * @param   sampleSize
     *      The sample size. Must be positive.
     */
    public void setSampleSize(
        final int sampleSize)
    {
        ArgumentChecker.assertIsPositive("sampleSize", sampleSize);
        this.sampleSize = sampleSize;
    }

    /**
     * Gets the regularization weight (lambda) assigned to the regularization
     * term of the algorithm.
     *
     * @return
     *      The regularization weight. Must be positive.
     */
    public double getRegularizationWeight()
    {
        return this.regularizationWeight;
    }

    /**
     * Sets the regularization weight (lambda) assigned to the regularization
     * term of the algorithm.
     *
     * @param   regularizationWeight
     *      The regularization weight. Must be positive.
     */
    public void setRegularizationWeight(
        final double regularizationWeight)
    {
        ArgumentChecker.assertIsPositive("regularizationWeight",
            regularizationWeight);
        this.regularizationWeight = regularizationWeight;
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

}
