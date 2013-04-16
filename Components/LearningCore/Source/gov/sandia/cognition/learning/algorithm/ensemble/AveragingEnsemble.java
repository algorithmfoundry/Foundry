/*
 * File:            AveragingEnsemble.java
 * Authors:         Justin Basilico
 * Project:         Community Foundry
 * 
 * Copyright 2011 Cognitive Foundry. All rights reserved.
 */

package gov.sandia.cognition.learning.algorithm.ensemble;

import gov.sandia.cognition.evaluator.Evaluator;
import gov.sandia.cognition.learning.function.regression.Regressor;
import java.util.ArrayList;
import java.util.List;

/**
 * An ensemble for regression functions that averages together the output value
 * of each ensemble member to get the final output.
 *
 * @param   <InputType>
 *      The type of input the ensemble can take. Passed to each ensemble
 *      member to produce an output.
 * @param   <MemberType>
 *      The type of members of this ensemble.
 * @author  Justin Basilico
 * @version 3.4.0
 */
public class AveragingEnsemble<InputType, MemberType extends Evaluator<? super InputType, ? extends Number>>
    extends AbstractUnweightedEnsemble<MemberType>
    implements Regressor<InputType>
{

    /**
     * Creates a new, empty {@code AdditiveEnsemble}.
     */
    public AveragingEnsemble()
    {
        this(new ArrayList<MemberType>());
    }

    /**
     * Creates a new {@code AdditiveEnsemble} with the given
     *
     * @param   members
     *      The list of ensemble members.
     */
    public AveragingEnsemble(
        final List<MemberType> members)
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
        // Compute the sum and the count of non-zero vailest.
        double sum = 0.0;
        int count = 0;
        for (MemberType member : this.getMembers())
        {
            // Compute the estimate of the member.
            final Number value = member.evaluate(input);

            if (value != null)
            {
                sum += value.doubleValue();
                count++;
            }
            // else - The member had no value.
        }

        // Return the average.
        if (count <= 0)
        {
            return 0.0;
        }
        else
        {
            return sum / count;
        }
    }

}
