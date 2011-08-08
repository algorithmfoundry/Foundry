/*
 * File:                WeightedBinaryEnsemble.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright September 17, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.learning.algorithm.ensemble;

import gov.sandia.cognition.annotation.CodeReview;
import gov.sandia.cognition.evaluator.Evaluator;
import gov.sandia.cognition.learning.function.categorization.AbstractBinaryCategorizer;
import gov.sandia.cognition.util.DefaultWeightedValue;
import gov.sandia.cognition.util.WeightedValue;
import java.util.ArrayList;
import java.util.List;

/**
 * The {@code WeightedBinaryEnsemble} class implements an {@code Ensemble} of
 * {@code BinaryCategorizer} objects where each categorizer is assigned a 
 * weight and the category is selected by choosing the one with the largest
 * sum of weights.
 *
 * @param  <InputType> The input type for the categorizer.
 * @param  <MemberType> The type of the members of the ensemble.
 * @author Justin Basilico
 * @since  2.0
 */
@CodeReview(reviewer = "Kevin R. Dixon",
    date = "2008-07-23",
    changesNeeded = false,
    comments = "Looks fine.")
public class WeightedBinaryEnsemble<InputType, MemberType extends Evaluator<? super InputType, ? extends Boolean>>
    extends AbstractBinaryCategorizer<InputType>
    implements Ensemble<WeightedValue<MemberType>>
{

    /** The default weight when adding a member is {@value}. */
    public static final double DEFAULT_WEIGHT = 1.0;

    /** The members of the ensemble. */
    protected List<WeightedValue<MemberType>> members;

    /**
     * Creates a new instance of WeightedBinaryEnsemble.
     */
    public WeightedBinaryEnsemble()
    {
        this(new ArrayList<WeightedValue<MemberType>>());
    }

    /**
     * Creates a new instance of WeightedBinaryEnsemble.
     *
     * @param  members The members of the ensemble.
     */
    public WeightedBinaryEnsemble(
        final List<WeightedValue<MemberType>> members)
    {
        super();

        this.setMembers(members);
    }

    /**
     * Adds the given categorizer with a default weight of 1.0.
     *
     * @param  categorizer The categorizer to add.
     */
    public void add(
        final MemberType categorizer)
    {
        this.add(categorizer, DEFAULT_WEIGHT);
    }

    /**
     * Adds the given categorizer with a given weight.
     *
     * @param  categorizer The categorizer to add.
     * @param weight The weight for the new member.
     */
    public void add(
        final MemberType categorizer,
        final double weight)
    {
        if (categorizer == null)
        {
            throw new NullPointerException("categorizer cannot be null");
        }

        final WeightedValue<MemberType> weighted =
            DefaultWeightedValue.create(categorizer, weight);

        this.getMembers().add(weighted);
    }

    /**
     * Evaluates the ensemble. It determines the output by combining the 
     * weighted output of each ensemble member.
     *
     * @param  input The input to evaluate.
     * @return The ensemble evaluated on the given input.
     */
    public Boolean evaluate(
        final InputType input)
    {
        return this.evaluateAsDouble(input) >= 0.0;
    }

    /**
     * Evaluates the ensemble on the given input and returns the result as a
     * double. If the double is greater than or equal to zero then the result is
     * true and otherwise it is false. The number is based on the weighted sum
     * of estimates.
     *
     * @param  input The input to evaluate.
     * @return The ensemble evaluated on the given input.
     */
    public double evaluateAsDouble(
        final InputType input)
    {
        // Compute the sum and the weight sum.
        double sum = 0.0;
        double weightSum = 0.0;
        for (WeightedValue<MemberType> member : this.getMembers())
        {
            // Compute the estimate of the member.
            final Boolean estimate = member.getValue().evaluate(input);
            final double weight = member.getWeight();

            if (estimate != null)
            {
                // Add the estimate to the sum.
                sum += estimate ? +weight : -weight;
            }

            // Add the weight to the weight sum.
            weightSum += Math.abs(weight);
        }

        // Return the result.
        return weightSum == 0.0 ? 0.0 : (sum / weightSum);
    }

    /**
     * Gets the members of the ensemble.
     *
     * @return The members of the ensemble.
     */
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
