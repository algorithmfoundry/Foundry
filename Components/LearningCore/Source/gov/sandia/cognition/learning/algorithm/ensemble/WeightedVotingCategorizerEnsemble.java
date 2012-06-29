/*
 * File:                WeightedVotingCategorizerEnsemble.java
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

import gov.sandia.cognition.evaluator.Evaluator;
import gov.sandia.cognition.learning.data.DefaultWeightedValueDiscriminant;
import gov.sandia.cognition.learning.function.categorization.AbstractCategorizer;
import gov.sandia.cognition.learning.function.categorization.DiscriminantCategorizer;
import gov.sandia.cognition.statistics.distribution.DefaultDataDistribution;
import gov.sandia.cognition.util.ArgumentChecker;
import gov.sandia.cognition.util.DefaultWeightedValue;
import gov.sandia.cognition.util.WeightedValue;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * An ensemble of categorizers where each ensemble member is evaluated with the
 * given input to find the category to which its weighted votes are assigned.
 * The output with the maximum number of votes is the output of the ensemble.
 * The default weight is 1.0, meaning that each ensemble member has equal votes.
 *
 * @param   <InputType>
 *      The type of the input to the ensemble. Passed on to each ensemble
 *      member categorizer to produce an output.
 * @param   <CategoryType>
 *      The type of the output of the ensemble. Also the output of ech
 *      ensemble member categorizer.
 * @param   <MemberType>
 *      The type of the members of the ensemble, which must be some extension
 *      of the Evaluator interface.
 * @author  Justin Basilico
 * @since   3.0
 */
public class WeightedVotingCategorizerEnsemble<InputType, CategoryType, MemberType extends Evaluator<? super InputType, ? extends CategoryType>>
    extends AbstractCategorizer<InputType, CategoryType>
    implements Ensemble<WeightedValue<MemberType>>,
        DiscriminantCategorizer<InputType, CategoryType, Double>
{

    /** The default weight when adding a member is {@value}. */
    public static final double DEFAULT_WEIGHT = 1.0;

    /** The members of the ensemble. */
    protected List<WeightedValue<MemberType>> members;

    /**
     * Creates a new instance of WeightedVotingCategorizerEnsemble.
     */
    public WeightedVotingCategorizerEnsemble()
    {
        this(new HashSet<CategoryType>());
    }

    /**
     * Creates a new instance of WeightedVotingCategorizerEnsemble.
     *
     * @param   categories
     *      The set of categories that the ensemble can output.
     */
    public WeightedVotingCategorizerEnsemble(
        final Set<CategoryType> categories)
    {
        this(categories, new ArrayList<WeightedValue<MemberType>>());
    }

    /**
     * Creates a new instance of WeightedVotingCategorizerEnsemble.
     *
     * @param   categories
     *      The set of categories that the ensemble can output.
     * @param   members
     *      The members of the ensemble.
     */
    public WeightedVotingCategorizerEnsemble(
        final Set<CategoryType> categories,
        final List<WeightedValue<MemberType>> members)
    {
        super(categories);

        this.setMembers(members);
    }

    /**
     * Adds the given categorizer with a default weight of 1.0.
     *
     * @param  categorizer
     *      The categorizer to add.
     */
    public void add(
        final MemberType categorizer)
    {
        this.add(categorizer, DEFAULT_WEIGHT);
    }

    /**
     * Adds the given categorizer with a given weight.
     *
     * @param   member
     *      The categorizer to add.
     * @param   weight
     *      The weight for the new member. Cannot be negative.
     */
    public void add(
        final MemberType member,
        final double weight)
    {
        ArgumentChecker.assertIsNotNull("member", member);
        ArgumentChecker.assertIsNonNegative("weight", weight);

        final WeightedValue<MemberType> weighted =
            new DefaultWeightedValue<MemberType>(member, weight);

        this.getMembers().add(weighted);
    }

    /**
     * Evaluates the ensemble. It determines the output by evaluating each
     * member and counting the weighted votes for each category output by the
     * member. It then returns the category with the most votes.
     *
     * @param  input
     *      The input to evaluate.
     * @return
     *      The ensemble evaluated on the given input.
     */
    @Override
    public CategoryType evaluate(
        final InputType input)
    {
        // Get the vote distribution.
        final DefaultDataDistribution<CategoryType> votes =
            this.evaluateAsVotes(input);

        // Get the maximum value of the votes.
        return votes.getMaxValueKey();
    }

    /**
     * Evaluates the ensemble on the given input and returns the category that
     * has the most weighted votes as a pair containing the category and the
     * percent of the weighted votes that it obtained.
     *
     * @param  input The input to evaluate.
     * @return The ensemble evaluated on the given input.
     */
    @Override
    public DefaultWeightedValueDiscriminant<CategoryType> evaluateWithDiscriminant(
        final InputType input)
    {
        // Get the votes for the input.
        final DefaultDataDistribution<CategoryType> votes =
            this.evaluateAsVotes(input);

        // Find the best votes.
        final CategoryType bestCategory = votes.getMaxValueKey();
        if (bestCategory == null)
        {
            // No category had any votes.
            return null;
        }
        else
        {
            // Compute the percentage of votes the best category got.
            final double bestVotePercentage = votes.getFraction(bestCategory);

            // Return the result.
            return DefaultWeightedValueDiscriminant.create(
                bestCategory, bestVotePercentage);
        }
    }

    /**
     * Evaluates the ensemble on the given input and returns the distribution
     * of votes over the output categories.
     *
     * @param  input
     *      The input to evaluate.
     * @return
     *      The ensemble's distribution of votes for the given input.
     */
    public DefaultDataDistribution<CategoryType> evaluateAsVotes(
        final InputType input)
    {
        // Compute the votes.
        final DefaultDataDistribution<CategoryType> votes =
            new DefaultDataDistribution<CategoryType>(
                this.getCategories().size());

        for (WeightedValue<MemberType> member : this.getMembers())
        {
            // Compute the estimate of the member.
            final CategoryType category = member.getValue().evaluate(input);
            final double weight = member.getWeight();

            if (category != null)
            {
                // Update the vote information for the voted category.
                votes.increment(category, weight);
            }
            // else - The member had no vote.
        }

        return votes;
    }

    /**
     * Gets the members of the ensemble.
     *
     * @return The members of the ensemble.
     */
    @Override
    public List<WeightedValue<MemberType>> getMembers()
    {
        return this.members;
    }

    /**
     * Sets the members of the ensemble.
     *
     * @param members The members of the ensemble.
     */
    public void setMembers(
        final List<WeightedValue<MemberType>> members)
    {
        this.members = members;
    }

}
