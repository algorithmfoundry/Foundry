/*
 * File:                ValueMapper.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright October 03, 2008, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 * See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.evaluator;

import gov.sandia.cognition.annotation.CodeReview;
import gov.sandia.cognition.util.AbstractCloneableSerializable;
import gov.sandia.cognition.util.ObjectUtil;
import java.util.HashMap;
import java.util.Map;

/**
 * An evaluator that uses a map to map input values to their appropriate output
 * values. All it does is perform a lookup in the map.
 * 
 * @param   <InputType>
 *      The input value for the evaluator and its map.
 * @param   <OutputType>
 *      The output type for the evaluator and its map.
 * @author  Justin Basilico
 * @since   3.0
 */
@CodeReview(
    reviewer="Kevin R. Dixon",
    date="2008-12-02",
    changesNeeded=false,
    comments={
        "Removed redundant claim to implement Serializable",
        "Looks good."
    }
)
public class ValueMapper<InputType, OutputType>
    extends AbstractCloneableSerializable
    implements Evaluator<InputType, OutputType>
{

    /** The map to use to map input values to output values. */
    protected Map<InputType, OutputType> valueMap;

    /**
     * Creates a new {@code ValueMapper}. It creates an empty {@code HashMap}
     * for the value map.
     */
    public ValueMapper()
    {
        this(new HashMap<InputType, OutputType>());
    }

    /**
     * Creates a new {@code ValueMapper} with the given map.
     * 
     * @param   valueMap
     *      The map to use. 
     */
    public ValueMapper(
        final Map<InputType, OutputType> valueMap)
    {
        super();

        this.setValueMap(valueMap);
    }
    
    @Override
    public ValueMapper<InputType, OutputType> clone()
    {
        @SuppressWarnings("unchecked")
        final ValueMapper<InputType, OutputType> result = 
            (ValueMapper<InputType, OutputType>) super.clone();
        result.valueMap = ObjectUtil.cloneSmart(this.valueMap);
        return result;
    }

    public OutputType evaluate(
        final InputType input)
    {
        // Just use the map.
        return this.valueMap.get(input);
    }

    /**
     * Gets the map of input values to output values.
     * 
     * @return
     *      The value map.
     */
    public Map<InputType, OutputType> getValueMap()
    {
        return this.valueMap;
    }

    /**
     * Sets the map of input values to output values.
     * 
     * @param   valueMap
     *      The value map.
     */
    public void setValueMap(
        final Map<InputType, OutputType> valueMap)
    {
        this.valueMap = valueMap;
    }

    /**
     * Creates an evaluator who is backed by the given map.
     *
     * @param   <InputType>
     *      The input value for the evaluator and its map.
     * @param   <OutputType>
     *      The output type for the evaluator and its map.
     * @param   valueMap
     *      The value map.
     * @return
     *      A new {@code ValueMapper} that uses the given map.
     */
    public static <InputType, OutputType> ValueMapper<InputType, OutputType> create(
        final Map<InputType, OutputType> valueMap)
    {
        return new ValueMapper<InputType, OutputType>(valueMap);
    }

}
