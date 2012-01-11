/*
 * File:                MultiCategoryAdaBoost.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry Learning Core
 * 
 * Copyright March 24, 2011, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government.
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
import gov.sandia.cognition.learning.data.DefaultWeightedInputOutputPair;
import gov.sandia.cognition.learning.data.InputOutputPair;
import gov.sandia.cognition.util.ObjectUtil;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

/**
 * An implementation of a multi-class version of the Adaptive Boosting
 * (AdaBoost) algorithm, known as AdaBoost.M1. Note that the "weak learner" in
 * this version of AdaBoost requires a weighted error rate that is greater than
 * 0.5 to be accepted into the ensemble and for learning to continue.
 *
 * @param   <InputType>
 *      The type of input that the weak learner can learn over.
 * @param   <CategoryType>
 *      The type of categories to learn over.
 * @author  Justin Basilico
 * @since   3.2.0
 */
@PublicationReference(
    author={"Yoav Freund", "Robert E.Schapire"},
    title="A decision-theoretic generalization of on-line learning and an application to boosting",
    publication="Journal of Computer and System Sciences",
    notes="Volume 55, Number 1",
    year=1997,
    pages={119,139},
    type=PublicationType.Journal,
    url="http://www.cse.ucsd.edu/~yfreund/papers/adaboost.pdf")
public class MultiCategoryAdaBoost<InputType, CategoryType>
    extends AbstractAnytimeSupervisedBatchLearner<InputType, CategoryType, WeightedVotingCategorizerEnsemble<InputType, CategoryType, Evaluator<? super InputType, ? extends CategoryType>>>
    implements BatchLearnerContainer<BatchLearner<? super Collection<? extends InputOutputPair<? extends InputType, CategoryType>>, ? extends Evaluator<? super InputType, ? extends CategoryType>>>
{

    /** The default maximum number of iterations is {@value}. */
    public static final int DEFAULT_MAX_ITERATIONS = 100;

    /**
     * The "weak learner" that must learn from the weighted input-output pairs
     * on each iteration.
     */
    protected BatchLearner<? super Collection<? extends InputOutputPair<? extends InputType, CategoryType>>, ? extends Evaluator<? super InputType, ? extends CategoryType>>
        weakLearner;

    /** The ensemble learned by the algorithm. */
    protected transient WeightedVotingCategorizerEnsemble<InputType, CategoryType, Evaluator<? super InputType, ? extends CategoryType>>
        ensemble;

    /** An array list containing the weighted version of the data. */
    protected transient ArrayList<DefaultWeightedInputOutputPair<InputType, CategoryType>> weightedData;

    /**
     * Creates a new {@code MultiCategoryAdaBoost} with default parameters and a null
     * weak learner.
     */
    public MultiCategoryAdaBoost()
    {
        this(null, DEFAULT_MAX_ITERATIONS);
    }

    /**
     * Creates a new {@code MultiCategoryAdaBoost} with the given parameters.
     *
     * @param   weakLearner
     *      The weak learner to use.
     * @param   maxIterations
     *      The maximum number of iterations. Must be positive.
     */
    public MultiCategoryAdaBoost(
        final BatchLearner<? super Collection<? extends InputOutputPair<? extends InputType, CategoryType>>, ? extends Evaluator<? super InputType, ? extends CategoryType>> weakLearner,
        final int maxIterations)
    {
        super(maxIterations);

        this.setWeakLearner(weakLearner);
    }

    @Override
    protected boolean initializeAlgorithm()
    {
        if (CollectionUtil.isEmpty(this.getData()))
        {
            return false;
        }
        
        // We initialize the weighted training examples and count them up
        // as we go so that we can initialize the weights in the next step.
        int numExamples = 0;
        this.weightedData =
            new ArrayList<DefaultWeightedInputOutputPair<InputType, CategoryType>>(
            this.getData().size());

        for (InputOutputPair<? extends InputType, CategoryType> example
            : this.getData())
        {
            if (example != null && example.getOutput() != null)
            {
                this.weightedData.add(
                    DefaultWeightedInputOutputPair.create(example.getInput(),
                        example.getOutput(), DatasetUtil.getWeight(example)));
                numExamples++;
            }
        }
        
        if (numExamples <= 0)
        {
            this.weightedData = null;
            return false;
        }

        // Figure out the set of categories.
        final Set<CategoryType> categories = DatasetUtil.findUniqueOutputs(
            this.weightedData);
        this.ensemble = new WeightedVotingCategorizerEnsemble<InputType, CategoryType, Evaluator<? super InputType, ? extends CategoryType>>(
            categories);
        
        return true;
    }

    @Override
    protected boolean step()
    {
        // Normalize the weights to sum to 1.0.
        double weightSum = 0.0;
        for (DefaultWeightedInputOutputPair<?, ?> example : this.weightedData)
        {
            weightSum += example.getWeight();
        }

        for (DefaultWeightedInputOutputPair<?, ?> example : this.weightedData)
        {
            example.setWeight(example.getWeight() / weightSum);
        }

        // Call the weak learner.
        final Evaluator<? super InputType, ? extends CategoryType> member =
            this.getWeakLearner().learn(weightedData);

        // Next compute the weighted error rate (epsilon) for the newly trained
        // member. Note that the weights sum to one so the error rate can be
        // at most 1.0.
        double weightedErrorRate = 0.0;
        final int dataSize = this.weightedData.size();
        final boolean[] correctness = new boolean[dataSize];
        for (int i = 0; i < dataSize; i++)
        {
            final DefaultWeightedInputOutputPair<InputType, CategoryType> example =
                this.weightedData.get(i);

            // Figure out if the categorizer is correct on this example.
            final CategoryType actual = example.getOutput();
            final CategoryType predicted = member.evaluate(example.getInput());
            final boolean correct = ObjectUtil.equalsSafe(predicted, actual);

            // Keep track of the correctness in order to compute the weight
            // update loop 
            correctness[i] = correct;

            if (!correct)
            {
                // This was an error. Increase the error rate.
                weightedErrorRate += example.getWeight();
            }
        }
        
        if (weightedErrorRate > 0.5)
        {
            // The best weak learner had too many errors, so we stop.
            return false;
        }

        // Compute beta.
        final double beta = weightedErrorRate / (1.0 - weightedErrorRate);

        // Update all of the weights based on beta.
        for (int i = 0; i < dataSize; i++)
        {
            final DefaultWeightedInputOutputPair<InputType, CategoryType> example =
                this.weightedData.get(i);
            final boolean correct = correctness[i];

            // Compute the new weight.
            final double oldWeight = example.getWeight();
            double newWeight =
                oldWeight * Math.pow(beta, 1.0 - (correct ? 0.0 : 1.0));
            example.setWeight(newWeight);
        }

        // Add the member to the ensemble using the weight log(1/beta).
        final double memberWeight = 
            (beta == 0.0 ? Double.POSITIVE_INFINITY : Math.log(1.0 / beta));
        this.ensemble.add(member, memberWeight);

        return true;
    }

    @Override
    protected void cleanupAlgorithm()
    {
        this.weightedData = null;
    }

    @Override
    public WeightedVotingCategorizerEnsemble<InputType, CategoryType, Evaluator<? super InputType, ? extends CategoryType>> getResult()
    {
        return this.ensemble;
    }

    @Override
    public BatchLearner<? super Collection<? extends InputOutputPair<? extends InputType, CategoryType>>, ? extends Evaluator<? super InputType, ? extends CategoryType>> getLearner()
    {
        return this.weakLearner;
    }

    /**
     * Gets the weak learner that is passed the weighted training data on each
     * step of the algorithm.
     *
     * @return 
     *      The weak learner for the algorithm to use.
     */
    public BatchLearner<? super Collection<? extends InputOutputPair<? extends InputType, CategoryType>>, ? extends Evaluator<? super InputType, ? extends CategoryType>> getWeakLearner()
    {
        return this.weakLearner;
    }

    /**
     * Sets the weak learner that is passed the weighted training data on each
     * step of the algorithm.
     *
     * @param   weakLearner
     *      The weak learner for the algorithm to use.
     */
    public void setWeakLearner(
        final BatchLearner<? super Collection<? extends InputOutputPair<? extends InputType, CategoryType>>, ? extends Evaluator<? super InputType, ? extends CategoryType>> weakLearner)
    {
        this.weakLearner = weakLearner;
    }

}
