/*
 * File:            AdditiveEnsemble.java
 * Authors:         Justin Basilico
 * Project:         Community Foundry
 * 
 * Copyright 2011 Justin Basilico. All rights reserved.
 */

package gov.sandia.cognition.learning.algorithm.ensemble;

import gov.sandia.cognition.evaluator.Evaluator;
import gov.sandia.cognition.learning.function.regression.Regressor;
import java.util.ArrayList;
import java.util.List;

/**
 * An ensemble of regression functions that determine the result by adding
 * their outputs together.
 *
 * @param   <InputType>
 *      The type of input to the ensemble. Passed to each ensemble member
 *      regression function to produce an output.
 * @param   <MemberType>
 *      The type of members of the ensemble, which must be evaluators that
 *      return numbers.
 * @author  Justin Basilico
 * @version 3.4.0
 */
public class AdditiveEnsemble<InputType, MemberType extends Evaluator<? super InputType, ? extends Number>>
    extends AbstractUnweightedEnsemble<MemberType>
    implements Regressor<InputType>
{

    /**
     * Creates a new, empty {@code AdditiveEnsemble}.
     */
    public AdditiveEnsemble()
    {
        this(new ArrayList<MemberType>());
    }

    /**
     * Creates a new {@code AdditiveEnsemble} with the given members.
     *
     * @param   members
     *      The list of ensemble members.
     */
    public AdditiveEnsemble(
        final List<MemberType> members)
    {
        super(members);
    }

    @Override
    public Double evaluate(
        final InputType input)
    {
        return this.evaluateAsDouble(input);
    }

    @Override
    public double evaluateAsDouble(
        final InputType input)
    {
        // Sum up the result.
        double sum = 0.0;
        for (MemberType member : this.getMembers())
        {
            // Compute the estimate of the member.
            final Number value = member.evaluate(input);

            if (value != null)
            {
                sum += value.doubleValue();
            }
            // else - The member had no value.
        }

        // Return the sum.
        return sum;
    }
    
}
