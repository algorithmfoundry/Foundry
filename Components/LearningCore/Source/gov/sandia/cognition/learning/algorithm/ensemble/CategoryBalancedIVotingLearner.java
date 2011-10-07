/*
 * File:                CategoryBalancedIVotingLearner.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry Learning Core
 * 
 * Copyright February 08, 2011, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government.
 *
 */

package gov.sandia.cognition.learning.algorithm.ensemble;

import gov.sandia.cognition.evaluator.Evaluator;
import gov.sandia.cognition.factory.Factory;
import gov.sandia.cognition.learning.algorithm.BatchLearner;
import gov.sandia.cognition.learning.data.InputOutputPair;
import gov.sandia.cognition.statistics.DataDistribution;
import gov.sandia.cognition.statistics.distribution.DefaultDataDistribution;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Random;

/**
 * An extension of IVoting for dealing with skew problems that makes sure that
 * there are an equal number of examples from each category in each sample that
 * an ensemble member is trained on.
 *
 * @param   <InputType>
 *      The type of the input for the categorizer to learn. This is the type
 *      passed to the internal batch learner to learn each ensemble member.
 * @param   <CategoryType>
 *      The type of the category that is the output for the categorizer to
 *      learn. It is also passed to the internal batch learner to learn each
 *      ensemble member. It must have a valid equals and hashCode method.
 * @author  Justin Basilico
 * @since   3.3.0
 */
public class CategoryBalancedIVotingLearner<InputType, CategoryType>
    extends IVotingCategorizerLearner<InputType, CategoryType>
{

    /**
     * Creates a new {@code CategoryBalancedIVotingLearner}.
     */
    public CategoryBalancedIVotingLearner()
    {
        this(null, DEFAULT_MAX_ITERATIONS, DEFAULT_PERCENT_TO_SAMPLE,
            new Random());
    }

    /**
     * Creates a new {@code CategoryBalancedIVotingLearner}.
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
    public CategoryBalancedIVotingLearner(
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
     * Creates a new {@code CategoryBalancedIVotingLearner}.
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
    public CategoryBalancedIVotingLearner(
        final BatchLearner<? super Collection<? extends InputOutputPair<? extends InputType, CategoryType>>, ? extends Evaluator<? super InputType, ? extends CategoryType>> learner,
        final int maxIterations,
        final double percentToSample,
        final double proportionIncorrectInSample,
        final boolean voteOutOfBagOnly,
        final Factory<? extends DataDistribution<CategoryType>> counterFactory,
        final Random random)
    {
        super(learner, maxIterations, percentToSample, proportionIncorrectInSample,
            voteOutOfBagOnly, counterFactory, random);
    }

    @Override
    protected void createBag(
        final ArrayList<Integer> correctIndices,
        final ArrayList<Integer> incorrectIndices)
    {
        // First we need to figure out which items are currently correct and
        // incorrect in each category.

        // Initialize the data structures.
        final LinkedHashMap<CategoryType, ArrayList<Integer>> correctPerCategory =
            new LinkedHashMap<CategoryType, ArrayList<Integer>>();
        final LinkedHashMap<CategoryType, ArrayList<Integer>> incorrectPerCategory =
            new LinkedHashMap<CategoryType, ArrayList<Integer>>();
        for (CategoryType category : this.ensemble.getCategories())
        {
            correctPerCategory.put(category, new ArrayList<Integer>());
            incorrectPerCategory.put(category, new ArrayList<Integer>());
        }

        // Add the index to the appropriate list.
        for (Integer index : correctIndices)
        {
            final CategoryType category = this.dataList.get(index).getOutput();
            correctPerCategory.get(category).add(index);
        }

        for (Integer index : incorrectIndices)
        {
            final CategoryType category = this.dataList.get(index).getOutput();
            incorrectPerCategory.get(category).add(index);
        }

        // Figure out how many to sample per category.
        final int categoryCount = this.ensemble.getCategories().size();

        final int correctPerCategorySize =
            Math.max(1, this.numCorrectToSample / categoryCount);
        final int incorrectPerCategorySize =
            Math.max(1, this.numIncorrectToSample / categoryCount);

        // Now sample from each category.
        for (CategoryType category : this.ensemble.getCategories())
        {
            // Get the correct and incorrect indices for thie category.
            ArrayList<Integer> categoryCorrect =
                correctPerCategory.get(category);
            ArrayList<Integer> categoryIncorrect =
                incorrectPerCategory.get(category);

            if (categoryIncorrect.isEmpty())
            {
                // Nothing incorrect, so just sample more from correct.
                categoryIncorrect = categoryCorrect;
            }
            else if (correctIndices.isEmpty())
            {
                // Nothing correct, so just sample more from incorrect.
                categoryCorrect = categoryIncorrect;
            }

            // Sample with replacement.
            sampleIndicesWithReplacementInto(categoryCorrect, this.dataList,
                correctPerCategorySize, this.random,
                this.currentBag, this.dataInBag);
            sampleIndicesWithReplacementInto(categoryIncorrect, this.dataList,
                incorrectPerCategorySize, this.random,
                this.currentBag, this.dataInBag);
        }

    }

}
