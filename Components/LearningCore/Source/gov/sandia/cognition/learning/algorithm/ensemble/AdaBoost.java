/*
 * File:                AdaBoost.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Framework Lite
 *
 * Copyright September 19, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.learning.algorithm.ensemble;

import gov.sandia.cognition.annotation.CodeReview;
import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.evaluator.Evaluator;
import gov.sandia.cognition.learning.algorithm.AbstractAnytimeSupervisedBatchLearner;
import gov.sandia.cognition.learning.algorithm.BatchLearner;
import gov.sandia.cognition.learning.algorithm.SupervisedBatchLearner;
import gov.sandia.cognition.learning.data.DefaultWeightedInputOutputPair;
import gov.sandia.cognition.learning.function.categorization.BinaryCategorizer;
import gov.sandia.cognition.learning.data.InputOutputPair;
import gov.sandia.cognition.learning.data.WeightedInputOutputPair;
import gov.sandia.cognition.util.WeightedValue;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;

/**
 * The {@code AdaBoost} class implements the Adaptive Boosting (AdaBoost)
 * algorithm formulated by Yoav Freund and Robert Shapire. It creates a 
 * weighted binary ensemble as output from the algorithm.
 *
 * @param <InputType> The algorithm operates on a
 * {@code Collection<InputOutputPair<InputType,Boolean>>}.  The
 * {@code InputType} will be something like Vector or String.
 * @author Justin Basilico
 * @since  2.0
 */
@CodeReview(
    reviewer="Kevin R. Dixon",
    date="2008-07-23",
    changesNeeded=false,
    comments={
        "Added PublicationReference",
        "Cleaned up javadoc a little bit with code annotations.",
        "Otherwise, looks fine."
    }
)
@PublicationReference(
    author={
        "Yoav Freund",
        "Robert E.Schapire"
    },
    title="A decision-theoretic generalization of on-line learning and an application to boosting",
    publication="Journal of Computer and System Sciences",
    notes="Volume 55, Number 1",
    year=1997,
    pages={119,139},
    type=PublicationType.Journal,
    url="http://www.cse.ucsd.edu/~yfreund/papers/adaboost.pdf"
)
public class AdaBoost<InputType>
    extends AbstractAnytimeSupervisedBatchLearner<InputType, Boolean, WeightedBinaryEnsemble<InputType, Evaluator<? super InputType, ? extends Boolean>>>
{

    /** The default maximum number of iterations is {@value}. */
    public static final int DEFAULT_MAX_ITERATIONS = 100;

    /**
     * The "weak learner" that must learn from the weighted input-output pairs
     * on each iteration.
     */
    protected BatchLearner<? super Collection<? extends InputOutputPair<? extends InputType, Boolean>>, ? extends Evaluator<? super InputType, ? extends Boolean>>
        weakLearner;

    /** The ensemble learned by the algorithm. */
    protected transient WeightedBinaryEnsemble<InputType, Evaluator<? super InputType, ? extends Boolean>>
        ensemble;

    /** An array list containing the weighted version of the data. */
    protected transient ArrayList<DefaultWeightedInputOutputPair<InputType, Boolean>> weightedData;

    /**
     * Creates a new instance of AdaBoost.
     */
    public AdaBoost()
    {
        this(null);
    }

    /**
     * Creates a new instance of AdaBoost.
     *
     * @param  weakLearner The weak learner to apply learning to on each 
     *         iteration.
     */
    public AdaBoost(
        final BatchLearner<? super Collection<? extends InputOutputPair<? extends InputType, Boolean>>, ? extends Evaluator<? super InputType, ? extends Boolean>> weakLearner)
    {
        this(weakLearner, DEFAULT_MAX_ITERATIONS);
    }

    /**
     * Creates a new instance of AdaBoost.
     *
     * @param  weakLearner 
     *      The weak learner to apply learning to on each iteration.
     * @param  maxIterations 
     *      The maximum number of iterations to run for, which is also the 
     *      upper bound on the number of learners to create.
     */
    public AdaBoost(
        final BatchLearner<? super Collection<? extends InputOutputPair<? extends InputType, Boolean>>, ? extends Evaluator<? super InputType, ? extends Boolean>> weakLearner,
        final int maxIterations)
    {
        super(maxIterations);

        this.setWeakLearner(weakLearner);

        this.setEnsemble(null);
        this.setWeightedData(null);
    }

    protected boolean initializeAlgorithm()
    {
        if (this.getData() == null || this.getData().size() <= 0)
        {
            // This is an invalid dataset.
            return false;
        }

        // Create the ensemble.
        this.setEnsemble(new WeightedBinaryEnsemble<InputType, Evaluator<? super InputType, ? extends Boolean>>(
            new LinkedList<WeightedValue<Evaluator<? super InputType, ? extends Boolean>>>()));

        // We initialize the weighted training examples and count them up
        // as we go so that we can initialize the weights in the next step.
        int numExamples = 0;
        this.setWeightedData(
            new ArrayList<DefaultWeightedInputOutputPair<InputType, Boolean>>(
            this.getData().size()));

        for (InputOutputPair<? extends InputType, Boolean> example : this.getData())
        {
            if (example != null)
            {
                this.weightedData.add(
                    new DefaultWeightedInputOutputPair<InputType, Boolean>(example, 1.0));
                numExamples++;
            }
        }

        // The initial weight is the same for all examples. We use this
        // value to ensure that they all sum to one.
        double initialWeight = 1.0 / numExamples;
        for (DefaultWeightedInputOutputPair<InputType, Boolean> example : this.weightedData)
        {
            example.setWeight(initialWeight);
        }

        return true;
    }

    protected boolean step()
    {
        // First perform the weak learning algorithm.
        Evaluator<? super InputType, ? extends Boolean> learned =
            this.weakLearner.learn(this.weightedData);

        if (learned == null)
        {
            // Nothing was learned.
            return false;
        }

        // Now compute the weighted error for the weak learner.
        // Also, while we compute the error we will save the predictions
        // that are made by the learner, which we will need when we update
        // the weights.
        double error = 0.0;
        int numExamples = this.weightedData.size();
        double[] predictions = new double[numExamples];
        // Note: The predictions array could be set as a class member and
        // reused if it is thrashing memory.

        for (int i = 0; i < numExamples; i++)
        {
            WeightedInputOutputPair<InputType, Boolean> example =
                this.weightedData.get(i);

            // Compute the prediction of the learner for this instance.
            Boolean prediction = learned.evaluate(example.getInput());

            double predictionDouble = 0.0;
            if (prediction == null)
            {
                predictionDouble = 0.0;
            }
            else if (prediction)
            {
                predictionDouble = +1.0;
            }
            else // ( !prediction )
            {
                predictionDouble = -1.0;
            }

            predictions[i] = predictionDouble;

            // Get the actual output.
            double actual = example.getOutput() ? +1.0 : -1.0;

            if (actual * predictionDouble < 0.0)
            {
                // The prediction was incorrect so add it to the weighted
                // error.
                error += example.getWeight();
            }
        // else - The prediction is correct or it was abstained (0.0)
        }

        if (error >= 0.5)
        {
            // The error rate of this classifier is worse than random, so we
            // stop the algorithm.
            return false;
        }

        // Alpha is fixed at 1.0 for this implementation because the
        // output of the weak learner has no constraints beyond requiring
        // that it is a real number.
        // Note: we could revisit this value of alpha in the future.
        double alpha = 1.0;
        if (error > 0.0)
        {
            alpha = 0.5 * Math.log((1 - error) / error);
        }

        // Add this to the ensemble.
        this.ensemble.add(learned, alpha);

        // Now update the weight assigned to each training example.
        // Also, we need to make it so the weights sum to one. We do this
        // by summing them up in this loop and then dividing them in
        // second pass.
        double sum = 0.0;
        for (int i = 0; i < numExamples; i++)
        {
            DefaultWeightedInputOutputPair<InputType, Boolean> example =
                this.weightedData.get(i);
            double predicted = predictions[i];
            double actual = example.getOutput() ? +1.0 : -1.0;
            double oldWeight = example.getWeight();

            // Compute the new weight.
            double newWeight =
                oldWeight * Math.exp(-alpha * actual * predicted);

            // Set the weight and update the sum.
            example.setWeight(newWeight);
            sum += newWeight;
        }

        // Normalize the weights to be a distribution.
        for (DefaultWeightedInputOutputPair<InputType, Boolean> example : this.weightedData)
        {
            example.setWeight(example.getWeight() / sum);
        }

        return true;
    }

    protected void cleanupAlgorithm()
    {
        // We no longer need the weighted data.
        this.setWeightedData(null);
    }

    public WeightedBinaryEnsemble<InputType, Evaluator<? super InputType, ? extends Boolean>> getResult()
    {
        return this.getEnsemble();
    }

    /**
     * Gets the weak learner that is passed the weighted training data on each
     * step of the algorithm. 
     *
     * @return The weak learner for the algorithm to use.
     */
    public BatchLearner<? super Collection<? extends InputOutputPair<? extends InputType, Boolean>>, ? extends Evaluator<? super InputType, ? extends Boolean>> getWeakLearner()
    {
        return this.weakLearner;
    }

    /**
     * Sets the weak learner that is passed the weighted training data on each
     * step of the algorithm. 
     *
     * @param   weakLearner The weak learner for the algorithm to use.
     */
    public void setWeakLearner(
        final BatchLearner<? super Collection<? extends InputOutputPair<? extends InputType, Boolean>>, ? extends Evaluator<? super InputType, ? extends Boolean>> weakLearner)
    {
        this.weakLearner = weakLearner;
    }

    /**
     * Gets the ensemble created by this learner.
     *
     * @return The ensemble created by this learner.
     */
    public WeightedBinaryEnsemble<InputType, Evaluator<? super InputType, ? extends Boolean>> getEnsemble()
    {
        return this.ensemble;
    }

    /**
     * Sets the ensemble created by this learner.
     *
     * @param  ensemble The ensemble created by this learner.
     */
    protected void setEnsemble(
        final WeightedBinaryEnsemble<InputType, Evaluator<? super InputType, ? extends Boolean>> ensemble)
    {
        this.ensemble = ensemble;
    }

    /**
     * Gets the weighted version of the data.
     *
     * @return The weighted version of the data.
     */
    public ArrayList<DefaultWeightedInputOutputPair<InputType, Boolean>> getWeightedData()
    {
        return this.weightedData;
    }

    /**
     * Sets the weighted version of the data.
     *
     * @param  weightedData The weighted version of the data.
     */
    protected void setWeightedData(
        final ArrayList<DefaultWeightedInputOutputPair<InputType, Boolean>> weightedData)
    {
        this.weightedData = weightedData;
    }

}
