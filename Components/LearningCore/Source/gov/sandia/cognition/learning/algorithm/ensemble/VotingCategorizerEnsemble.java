/*
 * File:                VotingCategorizerEnsemble.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry Learning Core
 * 
 * Copyright March 21, 2011, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government.
 *
 */

package gov.sandia.cognition.learning.algorithm.ensemble;

import gov.sandia.cognition.evaluator.Evaluator;
import gov.sandia.cognition.learning.data.DefaultWeightedValueDiscriminant;
import gov.sandia.cognition.learning.function.categorization.AbstractDiscriminantCategorizer;
import gov.sandia.cognition.statistics.distribution.DefaultDataDistribution;
import gov.sandia.cognition.util.ArgumentChecker;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * An ensemble of categorizers that determine the result based on an
 * equal-weight vote. The category with the most votes wins.
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
 * @since   3.1.1
 * @see     WeightedVotingCategorizerEnsemble
 */
public class VotingCategorizerEnsemble<InputType, CategoryType, MemberType extends Evaluator<? super InputType, ? extends CategoryType>>
    extends AbstractDiscriminantCategorizer<InputType, CategoryType, Double>
    implements Ensemble<MemberType>
{

    /** The members of the ensemble. */
    protected List<MemberType> members;

    /**
     * Creates a new, empty {@code VotingCategorizerEnsemble}.
     */
    public VotingCategorizerEnsemble()
    {
        this(new LinkedHashSet<CategoryType>());
    }

    /**
     * Creates a new, empty {@code VotingCategorizerEnsemble} with the given
     * set of categories.
     *
     * @param   categories
     *      The set of output categories for the ensemble.
     */
    public VotingCategorizerEnsemble(
        final Set<CategoryType> categories)
    {
        this(categories, new ArrayList<MemberType>());
    }

    /**
     * Creates a new {@code VotingCategorizerEnsemble} with the given set of
     * categories and list of members.
     *
     * @param   categories
     *      The set of output categories for the ensemble.
     * @param   members
     *      The list of ensemble members.
     */
    public VotingCategorizerEnsemble(
        final Set<CategoryType> categories,
        final List<MemberType> members)
    {
        super(categories);

        this.setMembers(members);
    }

    /**
     * Adds a given member to the ensemble.
     *
     * @param   member
     *      The ensemble member to add.
     */
    public void add(
        final MemberType member)
    {
        ArgumentChecker.assertIsNotNull("member", member);
        this.getMembers().add(member);
    }

    @Override
    public CategoryType evaluate(
        final InputType input)
    {
        // Get the maximum value of the votes.
        return this.evaluateAsVotes(input).getMaxValueKey();
    }

    @Override
    public DefaultWeightedValueDiscriminant<CategoryType> evaluateWithDiscriminant(
        final InputType input)
    {
        // Get the vote distribution.
        final DefaultDataDistribution<CategoryType> votes =
            this.evaluateAsVotes(input);

        // Get the maximum value of the votes.
        final CategoryType bestCategory = votes.getMaxValueKey();
        final double bestFraction = votes.getFraction(bestCategory);
        return DefaultWeightedValueDiscriminant.create(
            bestCategory, bestFraction);
    }

    /**
     * Evaluates the ensemble as votes.
     *
     * @param   input
     *      The input to evaluate.
     * @return
     *      The counts of the votes of each ensemble member for each category.
     */
    public DefaultDataDistribution<CategoryType> evaluateAsVotes(
        final InputType input)
    {
        // Create the counters to store the votes.
        final DefaultDataDistribution<CategoryType> votes =
            new DefaultDataDistribution<CategoryType>(
                this.getCategories().size());

        // Compute the votes.
        for (MemberType member : this.getMembers())
        {
            // Compute the estimate of the member.
            final CategoryType category = member.evaluate(input);

            if (category != null)
            {
                // Update the vote information for the voted category.
                votes.increment(category);
            }
            // else - The member had no vote.
        }

        // Return the vote distribution.
        return votes;
    }

    @Override
    public List<MemberType> getMembers()
    {
        return this.members;
    }

    /**
     * Sets the list of ensemble members.
     *
     * @param   members
     *      The list of ensemble members.
     */
    public void setMembers(
        final List<MemberType> members)
    {
        this.members = members;
    }

}
