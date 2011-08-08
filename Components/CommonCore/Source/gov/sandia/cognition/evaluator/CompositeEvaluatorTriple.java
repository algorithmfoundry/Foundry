/*
 * File:                CompositeEvaluatorTriple.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright September 28, 2008, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 * See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.evaluator;

import gov.sandia.cognition.annotation.CodeReview;
import gov.sandia.cognition.util.DefaultTriple;
import java.io.Serializable;

/**
 * Implements a composition of three evaluators. The input is passed to the
 * first evaluator, then the output of the first is passed to the second, then
 * the output of the second is passed to the third, and the output of the third
 * is returned. If the first evaluator is f1, the second is f2, the third is
 * f3, and the input is x, the composition is f3(f2(f1(x)). It is useful for 
 * adapting both the input and the output of an evaluator to be turned into
 * different types. To follow that pattern, the first evaluator would convert
 * the input type, the second would do the actual evaluation, and the third
 * would convert the output type.
 * 
 * @param <InputType> 
 *      The input type for the first evaluator. This is also the input type for
 *      the composite evaluator.
 * @param <FirstIntermediateType>
 *      The output type of the first evaluator and the input type of the second
 *      evaluator.
 * @param <SecondIntermediateType> 
 *      The output type of the second evaluator and the input type of the third
 *      evaluator.
 * @param <OutputType> 
 *      The output type of the third evaluator. This is also the output type of
 *      the composite evaluator.
 * @author  Justin Basilico
 * @since   2.1
 */
@CodeReview(
    reviewer="Kevin R. Dixon",
    date="2008-12-02",
    changesNeeded=false,
    comments="Please see my diatribe in CompositeEvaluatorPair"
)
public class CompositeEvaluatorTriple<InputType, FirstIntermediateType, SecondIntermediateType, OutputType>
    extends DefaultTriple<
        Evaluator<? super InputType, ? extends FirstIntermediateType>,
        Evaluator<? super FirstIntermediateType, ? extends SecondIntermediateType>,
        Evaluator<? super SecondIntermediateType, ? extends OutputType>>
    implements Evaluator<InputType, OutputType>, Serializable
{
    
    /**
     * Creates a new {@code CompositeEvalutorTriple}. The three internal 
     * evaluators are initialized to null.
     */
    public CompositeEvaluatorTriple()
    {
        this(null, null, null);
    }
    
    /**
     * Creates a new {@code CompositeEvaluatorTriple} from the three given 
     * evaluators.
     * 
     * @param   first 
     *      The first evaluator.
     * @param   second 
     *      The second evaluator.
     * @param   third
     *      The third evaluator.
     */
    public CompositeEvaluatorTriple(
        final Evaluator<? super InputType, ? extends FirstIntermediateType> first,
        final Evaluator<? super FirstIntermediateType, ? extends SecondIntermediateType> second,
        final Evaluator<? super SecondIntermediateType, ? extends OutputType> third)
    {
        super(first, second, third);
    }
    
    public OutputType evaluate(
        final InputType input)
    {
        final FirstIntermediateType firstIntermediate = 
            this.getFirst().evaluate(input);
        final SecondIntermediateType secondIntermediate =
            this.getSecond().evaluate(firstIntermediate);
        final OutputType output = 
            this.getThird().evaluate(secondIntermediate);
        return output;
    }
    
    /**
     * A convenience method for creating composite evaluators.
     * 
     * @param <InputType> 
     *      The input type for the first evaluator. This is also the input type 
     *      for the composite evaluator.
     * @param <FirstIntermediateType>
     *      The output type of the first evaluator and the input type of the 
     *      second evaluator.
     * @param <SecondIntermediateType> 
     *      The output type of the second evaluator and the input type of the 
     *      third evaluator.
     * @param <OutputType> 
     *      The output type of the third evaluator. This is also the output 
     *      type of the composite evaluator.
     * @param   first 
     *      The first evaluator.
     * @param   second 
     *      The second evaluator.
     * @param   third
     *      The third evaluator.
     * @return
     *      A new {@code CompositeEvaluatorTriple} from the three given
     *      evaluators.
     */
    public static <InputType, FirstIntermediateType, SecondIntermediateType, OutputType> 
    CompositeEvaluatorTriple<InputType, FirstIntermediateType, SecondIntermediateType, OutputType> 
    create(
        final Evaluator<? super InputType, ? extends FirstIntermediateType> first,
        final Evaluator<? super FirstIntermediateType, ? extends SecondIntermediateType> second,
        final Evaluator<? super SecondIntermediateType, ? extends OutputType> third)
    {
        return new CompositeEvaluatorTriple<InputType, FirstIntermediateType, SecondIntermediateType, OutputType>(
            first, second, third);
    }
}