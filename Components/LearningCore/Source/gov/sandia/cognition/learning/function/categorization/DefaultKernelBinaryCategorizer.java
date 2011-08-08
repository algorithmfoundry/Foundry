/*
 * File:                DefaultKernelBinaryCategorizer.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry Learning Core
 * 
 * Copyright April 06, 2011, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 */

package gov.sandia.cognition.learning.function.categorization;

import gov.sandia.cognition.collection.CollectionUtil;
import gov.sandia.cognition.learning.function.kernel.Kernel;
import gov.sandia.cognition.util.DefaultWeightedValue;
import java.util.ArrayList;
import java.util.Collection;

/**
 * A default implementation of the {@code KernelBinaryCategorizer} that uses
 * the standard way of representing the examples (supports) using a
 * {@code DefaultWeightedValue}.
 *
 * @param   <InputType>
 *      The input type for the categorizer.
 * @author  Justin Basilico
 * @since   3.2.0
 */
public class DefaultKernelBinaryCategorizer<InputType>
    extends KernelBinaryCategorizer<InputType, DefaultWeightedValue<InputType>>
{

    /**
     * Creates a new {@code DefaultKernelBinaryCategorizer} with a null kernel,
     * no examples, and a zero bias.
     */
    public DefaultKernelBinaryCategorizer()
    {
        this(null);
    }

    /**
     * Creates a new {@code DefaultKernelBinaryCategorizer} with the given
     * kernel, no examples, and a zero bias.
     *
     * @param   kernel
     *      The kernel to use.
     */
    public DefaultKernelBinaryCategorizer(
        final Kernel<? super InputType> kernel)
    {
        this(kernel, new ArrayList<DefaultWeightedValue<InputType>>(), 0.0);
    }

    /**
     * Creates a new {@code DefaultKernelBinaryCategorizer} with the given
     * parameters.
     *
     * @param   kernel
     *      The kernel to use.
     * @param   examples
     *      The collection of examples to use.
     * @param   bias
     *      The bias term.
     */
    public DefaultKernelBinaryCategorizer(
        final Kernel<? super InputType> kernel,
        final Collection<DefaultWeightedValue<InputType>> examples,
        final double bias)
    {
        super(kernel, examples, bias);
    }

    /**
     * Adds a new example of the given value with the given weight.
     *
     * @param   value
     *      The value to add.
     * @param   weight
     *      The weight for the value.
     */
    public void add(
        final InputType value,
        final double weight)
    {
        this.examples.add(DefaultWeightedValue.create(value, weight));
    }

    /**
     * Gets the i-th example.
     *
     * @param   i
     *      The 0-based index of the example to get.
     * @return
     *      The example at the i-th position
     */
    public DefaultWeightedValue<InputType> get(
        final int i)
    {
        return CollectionUtil.getElement(this.examples, i);
    }

    /**
     * Removes the i-th example. May not be supported by some collection types.
     *
     * @param   i
     *      The 0-based index of the example to remove.
     * @return
     *      The item that was removed.
     */
    public DefaultWeightedValue<InputType> remove(
        final int i)
    {
        return CollectionUtil.removeElement(this.examples, i);
    }
    
    /**
     * Returns the number of examples (supports) in the categorizer.
     * 
     * @return
     *      The number of examples (supports) in the categorizer.
     */
    public int getExampleCount()
    {
        return this.examples.size();
    }

}
