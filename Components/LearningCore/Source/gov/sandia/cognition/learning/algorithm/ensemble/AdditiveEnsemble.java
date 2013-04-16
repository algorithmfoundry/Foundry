/*
 * File:            AdditiveEnsemble.java
 * Authors:         Justin Basilico
 * Project:         Cognitive Foundry
 * 
 * Copyright 2011 Cognitive Foundry. All rights reserved.
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
 * @since   3.3.3
 */
public class AdditiveEnsemble<InputType, MemberType extends Evaluator<? super InputType, ? extends Number>>
    extends AbstractUnweightedEnsemble<MemberType>
    implements Regressor<InputType>
{

    /** The default bias is {@value}. */
    public static final double DEFAULT_BIAS = 0.0;

    /** The initial offset value that the ensemble outputs are added to. */
    protected double bias;

    /**
     * Creates a new, empty {@code AdditiveEnsemble}.
     */
    public AdditiveEnsemble()
    {
        this(new ArrayList<MemberType>());
    }

    /**
     * Creates a new {@code AdditiveEnsemble} with the given members and a bias
     * of 0.
     *
     * @param   members
     *      The list of ensemble members.
     */
    public AdditiveEnsemble(
        final List<MemberType> members)
    {
        this(members, DEFAULT_BIAS);
    }

    /**
     * Creates a new {@code AdditiveEnsemble} with the given members.
     *
     * @param   members
     *      The list of ensemble members.
     * @param   bias
     *      The initial offset value.
     */
    public AdditiveEnsemble(
        final List<MemberType> members,
        final double bias)
    {
        super(members);

        this.setBias(bias);
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
        double sum = this.bias;
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

    /**
     * Gets the initial offset value (bias) to which the output of the ensemble
     * members are added when computing a result.
     *
     * @return
     *      The bias.
     */
    public double getBias()
    {
        return this.bias;
    }

    /**
     * Sets the initial offset value (bias) to which the output of the ensemble
     * members are added when computing a result.
     *
     * @param   bias
     *      The bias.
     */
    public void setBias(
        final double bias)
    {
        this.bias = bias;
    }

}
