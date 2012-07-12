/*
 * File:                CompositeEvaluatorList.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright September 29, 2008, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 * See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.evaluator;

import gov.sandia.cognition.util.AbstractCloneableSerializable;
import gov.sandia.cognition.util.ObjectUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/**
 * Implements the composition of a list of evaluators. The input is passed to
 * the first evaluator, whose output is passed to the next, which continues 
 * down the list until the output of the last evaluator is returned. If the
 * evaluators in the list are f1, f2, ..., fN, and the input is x, the result
 * of the composition is fN(...(f2(f1(x)))...).
 * 
 * @param   <InputType> 
 *      The input type of the first evaluator of the list, which is also the
 *      input type of the composite evaluator.
 * @param   <OutputType>
 *      The output type of the last evaluator of the list, which is also the
 *      output type of the composite evaluator.
 * @author  Justin Basilico
 * @since   2.1
 */
public class CompositeEvaluatorList<InputType, OutputType>
    extends AbstractCloneableSerializable
    implements Evaluator<InputType, OutputType>
{
    /** The list of evaluators to compose together. */
    private ArrayList<Evaluator<?,?>> evaluators;
    
    /**
     * Creates a new {@code CompositeEvaluatorList} with an empty list of 
     * evaluators.
     */
    public CompositeEvaluatorList()
    {
        this(new ArrayList<Evaluator<?,?>>());
    }
    
    /**
     * Creates a new {@code CompositeEvaluatorList} from the given array of 
     * evaluators.
     * 
     * @param   evaluatorsArray
     *      The array of evaluators to compose.
     */
    public CompositeEvaluatorList(
        final Evaluator<?,?>... evaluatorsArray)
    {
        this(Arrays.asList(evaluatorsArray));
    }
    
    /**
     * Creates a new {@code CompositeEvaluatorList} from the given collection
     * of evaluators. It makes a copy of the given collection.
     * 
     * @param   evaluators
     *      The evaluators to compose.
     */
    public CompositeEvaluatorList(
        final Collection<? extends Evaluator<?,?>> evaluators)
    {
        super();
        
        this.setEvaluators(evaluators);
    }

    @Override
    public CompositeEvaluatorList<InputType, OutputType> clone()
    {
        @SuppressWarnings("unchecked")
        final CompositeEvaluatorList<InputType, OutputType> result =
            (CompositeEvaluatorList<InputType, OutputType>) super.clone();
        result.evaluators = ObjectUtil.cloneSmartElementsAsArrayList(
            this.evaluators);
        return result;
    }
    
    @SuppressWarnings(value={"unchecked", "rawtypes"})
    public OutputType evaluate(
        final InputType input)
    {
        Object value = input;
        
        for (Evaluator evaluator : this.evaluators)
        {
            value = evaluator.evaluate(value);
        }
        
        return (OutputType) value;
    }
    
    /**
     * Gets the list of evaluators that is being composed together.
     * 
     * @return  The list of evaluators.
     */
    public ArrayList<Evaluator<?,?>> getEvaluators()
    {
        return this.evaluators;
    }
    
    /**
     * Sets the list of evaluators to compose together. The given collection
     * is copied.
     * 
     * @param   evaluators
     *      The list of evaluators.
     */
    public void setEvaluators(
        final Collection<? extends Evaluator<?,?>> evaluators)
    {
        this.evaluators = new ArrayList<Evaluator<?,?>>(evaluators);
    }
    
    /**
     * Sets the array of evaluators to compose together.
     * 
     * @param   evaluatorsArray
     *      The array of evaluators.
     */
    public void setEvaluators(
        final Evaluator<?,?>... evaluatorsArray)
    {
        this.setEvaluators(Arrays.asList(evaluatorsArray));
    }
    
}
