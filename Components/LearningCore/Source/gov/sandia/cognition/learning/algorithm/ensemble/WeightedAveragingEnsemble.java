/*
 * File:            WeightedAveragingEnsemble.java
 * Authors:         Justin Basilico
 * Project:         Community Foundry
 * 
 * Copyright 2011 Cognitive Foundry. All rights reserved.
 */

package gov.sandia.cognition.learning.algorithm.ensemble;

import gov.sandia.cognition.evaluator.Evaluator;
import gov.sandia.cognition.learning.function.regression.Regressor;
import gov.sandia.cognition.util.ArgumentChecker;
import gov.sandia.cognition.util.WeightedValue;
import java.util.ArrayList;
import java.util.List;

/**
 * An implementation of an ensemble that takes the weighted average of its
 * members. The weights must be non-negative.
 *
 * @param   <InputType>
 *      The type of input the ensemble can take. Passed to each ensemble
 *      member to produce an output.
 * @param   <MemberType>
 *      The type of members of this ensemble.
 * @author  Justin Basilico
 * @version 3.4.0
 */
public class WeightedAveragingEnsemble<InputType, MemberType extends Evaluator<? super InputType, ? extends Number>>
    extends AbstractWeightedEnsemble<MemberType>
    implements Regressor<InputType>
{

    /**
     * Creates a new, empty of {@code WeightedAveragingEnsemble}.
     */
    public WeightedAveragingEnsemble()
    {
        this(new ArrayList<WeightedValue<MemberType>>());
    }

    /**
     * Creates a new instance of {@code WeightedAveragingEnsemble}.
     *
     * @param   members
     *      The members of the ensemble.
     */
    public WeightedAveragingEnsemble(
        final List<WeightedValue<MemberType>> members)
    {
        super(members);
    }

    @Override
    public void add(
        final MemberType member,
        final double weight)
    {
        ArgumentChecker.assertIsNonNegative("weight", weight);
        super.add(member, weight);
    }

    @Override
    public Double evaluate(
        final InputType input)
    {
        // Return the input evaluated as a double.
        return this.evaluateAsDouble(input);
    }

    @Override
    public double evaluateAsDouble(
        final InputType input)
    {
        // Sum up the result.
        double valueSum = 0.0;
        double weightSum = 0.0;
        for (WeightedValue<MemberType> member : this.getMembers())
        {
            // Compute the estimate of the member.
            final Number value = member.getValue().evaluate(input);

            if (value != null)
            {
                final double weight = member.getWeight();
                valueSum += weight * value.doubleValue();
                weightSum += weight;
            }
            // else - The member had no value.
        }

        if (weightSum <= 0.0)
        {
            // Avoid divide-by-zero.
            return 0.0;
        }
        else
        {
            // Return the weighted average.
            return valueSum / weightSum;
        }
    }

}
