/*
 * File:            WeightedAdditiveEnsemble.java
 * Authors:         Justin Basilico
 * Project:         Cognitive Foundry
 * 
 * Copyright 2011 Cognitive Foundry. All rights reserved.
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
 * @since   3.3.3
 */
public class WeightedAdditiveEnsemble<InputType, MemberType extends Evaluator<? super InputType, ? extends Number>>
    extends AbstractWeightedEnsemble<MemberType>
    implements Regressor<InputType>
{

    /** The default bias is {@value}. */
    public static final double DEFAULT_BIAS = 0.0;

    /** The initial offset value that the ensemble outputs are added to. */
    protected double bias;
    
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
        this(members, DEFAULT_BIAS);
    }

    /**
     * Creates a new instance of WeightedAdditiveEnsemble.
     *
     * @param   members
     *      The members of the ensemble.
     * @param   bias
     *      The initial offset for the result.
     */
    public WeightedAdditiveEnsemble(
        final List<WeightedValue<MemberType>> members,
        final double bias)
    {
        super(members);
        
        this.setBias(bias);
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
        double sum = this.bias;
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
