/*
 * File:            WeightedAdditiveEnsemble.java
 * Authors:         Justin Basilico
 * Project:         Community Foundry
 * 
 * Copyright 2011 Justin Basilico. All rights reserved.
 */

package gov.sandia.cognition.learning.algorithm.ensemble;

import gov.sandia.cognition.evaluator.Evaluator;
import gov.sandia.cognition.learning.function.regression.Regressor;
import gov.sandia.cognition.util.WeightedValue;
import java.util.ArrayList;
import java.util.List;

/**
 * An implementation of an ensemble that takes a weighted sum of the values
 * returned by its members.
 *
 * @param   <InputType>
 *      The type of input the ensemble can take. Passed to each ensemble
 *      member to produce an output.
 * @param   <MemberType>
 *      The type of members of this ensemble.
 * @author  Justin Basilico
 * @version 3.4.0
 */
public class WeightedAdditiveEnsemble<InputType, MemberType extends Evaluator<? super InputType, ? extends Number>>
    extends AbstractWeightedEnsemble<MemberType>
    implements Regressor<InputType>
{

    /**
     * Creates a new, empty of WeightedAdditiveEnsemble.
     */
    public WeightedAdditiveEnsemble()
    {
        this(new ArrayList<WeightedValue<MemberType>>());
    }

    /**
     * Creates a new instance of WeightedAdditiveEnsemble.
     *
     * @param   members
     *      The members of the ensemble.
     */
    public WeightedAdditiveEnsemble(
        final List<WeightedValue<MemberType>> members)
    {
        super(members);
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
        double sum = 0.0;
        for (WeightedValue<MemberType> member : this.getMembers())
        {
            // Compute the estimate of the member.
            final Number value = member.getValue().evaluate(input);

            if (value != null)
            {
                sum += member.getWeight() * value.doubleValue();
            }
            // else - The member had no value.
        }

        // Return the sum.
        return sum;
    }

}
