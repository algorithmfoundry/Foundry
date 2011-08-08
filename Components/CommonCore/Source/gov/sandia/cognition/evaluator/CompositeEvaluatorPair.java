/*
 * File:                CompositeEvaluatorPair.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright September 25, 2008, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 * See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.evaluator;

import gov.sandia.cognition.annotation.CodeReview;
import gov.sandia.cognition.util.DefaultPair;
import java.io.Serializable;

/**
 * Implements a composition of two evaluators. The input is passed to the first
 * then the output of the first is passed to the second, and the output of the
 * second is returned. If the first evaluator is f1, the second is f2, and the
 * input is x, the composition is f2(f1(x)).
 * 
 * @param   <InputType> 
 *      The input type for the first evaluator. This is also the input type of
 *      the composite evaluator.
 * @param   <IntermediateType> 
 *      The output of the first evaluator and input to the second evaluator.
 * @param   <OutputType>
 *      The output type of the second evaluator. This is also the output type
 *      of the composite evaluator.
 * @author  Justin Basilico
 * @since   2.1
 */
@CodeReview(
    reviewer="Kevin R. Dixon",
    date="2008-12-02",
    changesNeeded=false,
    comments={
        "I *really* don't like the names used by this class.",
        "'first' and 'second' are ambiguous about which one gets fired first.",
        "This is address by the comments, but I would still prefer to see the member names changed.",
        "I'm not going to flunk this, but I'm just going on record here."
    }
)
public class CompositeEvaluatorPair<InputType, IntermediateType, OutputType>
    extends DefaultPair<Evaluator<? super InputType, ? extends IntermediateType>, Evaluator<? super IntermediateType, ? extends OutputType>>
    implements Evaluator<InputType, OutputType>, Serializable
{
    /**
     * Creates a new {@code CompositeEvalutor}. The two internal evaluators are
     * initialized to null.
     */
    public CompositeEvaluatorPair()
    {
        this(null, null);
    }
    
    /**
     * Creates a new {@code CompositeEvaluatorPair} from the two given evaluators.
     * 
     * @param   first 
     *      The first evaluator.
     * @param   second 
     *      The second evaluator.
     */
    public CompositeEvaluatorPair(
        final Evaluator<? super InputType, ? extends IntermediateType> first,
        final Evaluator<? super IntermediateType, ? extends OutputType> second)
    {
        super(first, second);
    }
    
    public OutputType evaluate(
        final InputType input)
    {
        // Perform the composition.
        final IntermediateType intermediate = this.getFirst().evaluate(input);
        final OutputType output = this.getSecond().evaluate(intermediate);
        return output;
    }
    
    /**
     * A convenience method for creating composite evaluators.
     *  
     * @param   <InputType> 
     *      The input type for the first evaluator.
     * @param   <IntermediateType> 
     *      The output of the first evaluator and input to the second evaluator.
     * @param   <OutputType>
     *      The output type of the second evaluator.
     * @param   first
     *      The first evaluator.
     * @param   second
     *      The second evaluator.
     * @return
     *      A new {@code CompositeEvaluatorPair} from the two given evaluators.
     */
    public static <InputType, IntermediateType, OutputType> 
    CompositeEvaluatorPair<InputType, IntermediateType, OutputType> create(
        final Evaluator<? super InputType, ? extends IntermediateType> first,
        final Evaluator<? super IntermediateType, ? extends OutputType> second)
    {
        return new CompositeEvaluatorPair<InputType, IntermediateType, OutputType>(
            first, second);
    }
}
