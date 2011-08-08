/*
 * File:                OnlineBaggingCategorizerLearner.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Incremental Learning Core
 * 
 * Copyright March 21, 2011, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 */

package gov.sandia.cognition.learning.algorithm.ensemble;

import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.evaluator.Evaluator;
import gov.sandia.cognition.learning.algorithm.AbstractBatchAndIncrementalLearner;
import gov.sandia.cognition.learning.algorithm.IncrementalLearner;
import gov.sandia.cognition.learning.data.InputOutputPair;
import gov.sandia.cognition.statistics.distribution.PoissonDistribution;
import gov.sandia.cognition.util.ArgumentChecker;
import gov.sandia.cognition.util.Randomized;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Random;

/**
 * An implementation of an online version of the Bagging algorithm for learning
 * an ensemble of categorizers.
 *
 * @param   <InputType>
 *      The input type for supervised learning. Passed on to the internal
 *      learning algorithm. Also the input type for the learned ensemble.
 * @param   <CategoryType>
 *      The output type for supervised learning. Passed on to the internal
 *      learning algorithm. Also the output type of the learned ensemble.
 * @param   <MemberType>
 *      The type of ensemble member created by the base algorithm.
 * @author  Justin Basilico
 * @since   3.1.1
 */
@PublicationReference(
    author={"Nikunj C. Oza", "Stuart Russell"},
    title="Online Bagging and Boosting",
    year=2001,
    type=PublicationType.Conference,
    publication="In Artificial Intelligence and Statistics",
    pages={105, 112},
    url="http://citeseerx.ist.psu.edu/viewdoc/summary?doi=10.1.1.32.8889")
public class OnlineBaggingCategorizerLearner<InputType, CategoryType, MemberType extends Evaluator<? super InputType, ? extends CategoryType>>
    extends AbstractBatchAndIncrementalLearner<InputOutputPair<? extends InputType, CategoryType>, VotingCategorizerEnsemble<InputType, CategoryType, MemberType>>
    implements Randomized
{

    /** The default ensemble size is {@value}. */
    public static final int DEFAULT_ENSEMBLE_SIZE = 100;

    /** The default percent to sample is 1.0 (which represents 100%). */
    public static final double DEFAULT_PERCENT_TO_SAMPLE = 1.0;

    /** The base learner used for each ensemble member. */
    protected IncrementalLearner<? super InputOutputPair<? extends InputType, CategoryType>, MemberType> learner;

    /** The size of the ensemble to create. Must be positive. */
    protected int ensembleSize;

    /** The percentage of the data to sample for each ensemble member. Must be
     *  positive. Used as a parameter to the Poisson distribution to determine
     *  the number of samples for each ensemble member. */
    protected double percentToSample;

    /** The random number generator to use. */
    protected Random random;

    /**
     * Creates a new {@code OnlineBaggingCategorizerLearner} with a null learner
     * and default parameters.
     */
    public OnlineBaggingCategorizerLearner()
    {
        this(null);
    }

    /**
     * Creates a new {@code OnlineBaggingCategorizerLearner} with the given
     * base learner and default parameters.
     *
     * @param   learner
     *      The base learner to use for each ensemble member.
     */
    public OnlineBaggingCategorizerLearner(
        final IncrementalLearner<? super InputOutputPair<? extends InputType, CategoryType>, MemberType> learner)
    {
        this(learner, DEFAULT_ENSEMBLE_SIZE, DEFAULT_PERCENT_TO_SAMPLE,
            new Random());
    }

    /**
     * Creates a new {@code OnlineBaggingCategorizerLearner} with the given
     * parameters.
     *
     * @param learner
     *      The base learner to use for each ensemble member.
     * @param   ensembleSize
     *      The size of the ensemble to create. Must be positive,
     * @param   percentToSample
     *      The percentage of the data to sample for learning each ensemble
     *      member. Must be positive.
     * @param   random
     *      The random number generator to use.
     */
    public OnlineBaggingCategorizerLearner(
        final IncrementalLearner<? super InputOutputPair<? extends InputType, CategoryType>, MemberType> learner,
        final int ensembleSize,
        final double percentToSample,
        final Random random)
    {
        super();

        this.setLearner(learner);
        this.setEnsembleSize(ensembleSize);
        this.setPercentToSample(percentToSample);
        this.setRandom(random);
    }

    @Override
    public VotingCategorizerEnsemble<InputType, CategoryType, MemberType> createInitialLearnedObject()
    {
        // Initialize all of the ensemble members.
        final int size = this.getEnsembleSize();
        final ArrayList<MemberType> members = new ArrayList<MemberType>(size);
        for (int i = 0; i < this.ensembleSize; i++)
        {
            members.add(this.getLearner().createInitialLearnedObject());
        }

        // Create the ensemble.
        return new VotingCategorizerEnsemble<InputType, CategoryType, MemberType>(
                new LinkedHashSet<CategoryType>(), members);
    }

    @Override
    public void update(
        final VotingCategorizerEnsemble<InputType, CategoryType, MemberType> target,
        final InputOutputPair<? extends InputType, CategoryType> data)
    {
        // Make sure the ensemble's category set contains this category.
        final CategoryType category = data.getOutput();
        if (!target.getCategories().contains(category))
        {
            target.getCategories().add(category);
        }

        // To figure out how many examples to add, we sample from a Poisson
        // distribution using the percent to sample as the rate. In this paper
        // this is 1.0, but we provide a parameter to control the bag fraction.
        final PoissonDistribution.PMF poisson = new PoissonDistribution.PMF(
            this.getPercentToSample());
        for (MemberType member : target.getMembers())
        {
            // Figure out the number of times to give this member the example.
            final int updateCount = poisson.sample(this.random).intValue();

            // Now update that many times.
            for (int i = 0; i < updateCount; i++)
            {
                this.learner.update(member, data);
            }
        }
    }

    /**
     * Gets the incremental (online) learning algorithm to use to learn all of
     * the ensemble members.
     *
     * @return
     *      The base learning algorithm.
     */
    public IncrementalLearner<? super InputOutputPair<? extends InputType, CategoryType>, MemberType> getLearner()
    {
        return this.learner;
    }

    /**
     * Sets the incremental (online) learning algorithm to use to learn all of
     * the ensemble members.
     *
     * @param   learner
     *      The base learning algorithm.
     */
    public void setLearner(
        final IncrementalLearner<? super InputOutputPair<? extends InputType, CategoryType>, MemberType> learner)
    {
        this.learner = learner;
    }

    /**
     * Gets the size of the ensemble to create. When the ensemble is initially
     * created, it is filled with this many members.
     *
     * @return
     *      The size of the ensemble to create. Must be positive.
     */
    public int getEnsembleSize()
    {
        return this.ensembleSize;
    }

    /**
     * Sets the size of the ensemble to create. When the ensemble is initially
     * created, it is filled with this many members.
     *
     * @param   ensembleSize
     *      The size of the ensemble to create. Must be positive.
     */
    public void setEnsembleSize(
        final int ensembleSize)
    {
        ArgumentChecker.assertIsPositive("ensembleSize", ensembleSize);
        this.ensembleSize = ensembleSize;
    }

    /**
     * Gets the percent of the data to attempt to sample for each ensemble
     * member. Since this is an online algorithm, the expected number of
     * examples that each member is trained on is this amount. However, it does
     * not guarantee that each ensemble member will see exactly this fraction
     * of the data. In the algorithm, this value is used as the parameter to
     * the Poisson distribution to determine how many times to give each
     * member each example.
     *
     * @return
     *      The percentage of the data to sample for each ensemble member.
     *      Must be positive.
     */
    public double getPercentToSample()
    {
        return this.percentToSample;
    }

    /**
     * Sets the percent of the data to attempt to sample for each ensemble
     * member. Since this is an online algorithm, the expected number of
     * examples that each member is trained on is this amount. However, it does
     * not guarantee that each ensemble member will see exactly this fraction
     * of the data. In the algorithm, this value is used as the parameter to
     * the Poisson distribution to determine how many times to give each
     * member each example.
     *
     * @param   percentToSample
     *      The percentage of the data to sample for each ensemble member.
     *      Must be positive.
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
     * Convenience method for creating an
     * {@code OnlineBaggingCategorizerLearner}.
     *
     *
     * @param   <InputType>
     *      The input type for supervised learning. Passed on to the internal
     *      learning algorithm. Also the input type for the learned ensemble.
     * @param   <CategoryType>
     *      The output type for supervised learning. Passed on to the internal
     *      learning algorithm. Also the output type of the learned ensemble.
     * @param   <MemberType>
     *      The type of ensemble member created by the base algorithm.
     * @param learner
     *      The base learner to use for each ensemble member.
     * @param   ensembleSize
     *      The size of the ensemble to create. Must be positive,
     * @param   percentToSample
     *      The percentage of the data to sample for learning each ensemble
     *      member. Must be positive.
     * @param   random
     *      The random number generator to use.
     * @return
     *      A new online bagging learner.
     */
    public static <InputType, CategoryType, MemberType extends Evaluator<? super InputType, ? extends CategoryType>>
    OnlineBaggingCategorizerLearner<InputType, CategoryType, MemberType>
    create(
        final IncrementalLearner<? super InputOutputPair<? extends InputType, CategoryType>, MemberType> learner,
        final int ensembleSize,
        final double percentToSample,
        final Random random)
    {
        return new OnlineBaggingCategorizerLearner<InputType, CategoryType, MemberType>(
            learner, ensembleSize, percentToSample, random);
    }

}
