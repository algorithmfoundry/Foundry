/*
 * File:            ForwardReverseEvaluatorPair.java
 * Authors:         Justin Basilico
 * Project:         Cognitive Foundry Common Core
 * 
 * Copyright 2012, Cognitive Foundry. All rights reserved.
 */

package gov.sandia.cognition.evaluator;

import gov.sandia.cognition.util.AbstractCloneableSerializable;

/**
 * Represents a both a (normal) forward evaluator and its reverse as a pair.
 * This makes it easy to couple two such functions together, even if they were
 * not originally implemented as reversible. 
 *
 * @author  Justin Basilico
 * @version 3.3.3
 */
public class ForwardReverseEvaluatorPair<InputType, OutputType, ForwardType extends Evaluator<? super InputType, ? extends OutputType>, ReverseType extends Evaluator<? super OutputType, ? extends InputType>>
    extends AbstractCloneableSerializable
    implements ReversibleEvaluator<InputType, OutputType, ForwardReverseEvaluatorPair<OutputType, InputType, ReverseType, ForwardType>>
{

    /** The forward evaluator from input type to output type. */
    protected ForwardType forward;

    /** The reverse evaluator from output type to input type. */
    protected ReverseType reverse;

    /**
     * Creates a new, empty {@link ForwardReverseEvaluatorPair}.
     */
    public ForwardReverseEvaluatorPair()
    {
        this(null, null);
    }

    /**
     * Creates a new  {@link ForwardReverseEvaluatorPair}.
     *
     * @param   forward
     *      The forward evaluator.
     * @param   reverse
     *      The reverse evaluator.
     */
    public ForwardReverseEvaluatorPair(
        final ForwardType forward,
        final ReverseType reverse)
    {
        super();

        this.setForward(forward);
        this.setReverse(reverse);
    }

    @Override
    public ForwardReverseEvaluatorPair<OutputType, InputType, ReverseType, ForwardType> reverse()
    {
        return new ForwardReverseEvaluatorPair<OutputType, InputType, ReverseType, ForwardType>(
            this.reverse, this.forward);
    }

    @Override
    public OutputType evaluate(
        final InputType input)
    {
        return this.forward.evaluate(input);
    }

    /**
     * Evaluates the reverse evaluator on a given object of output type.
     *
     * @param   output
     *      The object of output type to pass to the reverse evaluator.
     * @return
     *      The object of input type returned by the reverse evaluator.
     */
    public InputType evaluateReverse(
        final OutputType output)
    {
        return this.reverse.evaluate(output);
    }

    /**
     * Gets the forward evaluator that maps input type to output type.
     *
     * @return
     *      The forward evaluator.
     */
    public ForwardType getForward()
    {
        return this.forward;
    }

    /**
     * Sets the forward evaluator that maps input type to output type.
     *
     * @param   forward
     *      The forward evaluator.
     */
    public void setForward(
        final ForwardType forward)
    {
        this.forward = forward;
    }

    /**
     * Gets the reverse evaluator that maps output type to input type.
     *
     * @return
     *      The reverse evaluator.
     */
    public ReverseType getReverse()
    {
        return this.reverse;
    }

    /**
     * Sets the reverse evaluator that maps output type to input type.
     *
     * @param   reverse
     *      The reverse evaluator.
     */
    public void setReverse(
        final ReverseType reverse)
    {
        this.reverse = reverse;
    }

    /**
     * Convenience method for creating a new forward-reverse evaluator pair.
     *
     * @param   <InputType>
     *      The input type for the forward evaluator. Also the output type of
     *      the reverse evaluator.
     * @param   <OutputType>
     *      The output type for the forward evaluator. Also the input type of
     *      the reverse evaluator.
     * @param   <ForwardType>
     *      The type of the forward evaluator.
     * @param   <ReverseType>
     *      The type of the reverse evaluator.
     * @param forward
     *      The forward evaluator.
     * @param reverse
     *      The reverse evaluator.
     * @return
     *      A new evaluator pair.
     */
    public static <InputType, OutputType, ForwardType extends Evaluator<? super InputType, ? extends OutputType>, ReverseType extends Evaluator<? super OutputType, ? extends InputType>> ForwardReverseEvaluatorPair<InputType, OutputType, ForwardType, ReverseType> create(
        final ForwardType forward,
        final ReverseType reverse)
    {
        return new ForwardReverseEvaluatorPair<InputType, OutputType, ForwardType, ReverseType>(
            forward, reverse);
    }
}
