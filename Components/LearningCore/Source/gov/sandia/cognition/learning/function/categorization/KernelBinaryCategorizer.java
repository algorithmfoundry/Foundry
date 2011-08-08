/*
 * File:                KernelBinaryCategorizer.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright July 18, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.learning.function.categorization;

import gov.sandia.cognition.learning.function.kernel.Kernel;
import gov.sandia.cognition.learning.function.kernel.KernelContainer;
import gov.sandia.cognition.util.ObjectUtil;
import gov.sandia.cognition.util.WeightedValue;
import java.util.ArrayList;
import java.util.Collection;

/**
 * The {@code KernelBinaryCategorizer} class implements a binary
 * categorizer that uses a kernel to do its categorization. It is parameterized 
 * by a kernel function, a list of examples and their weights, and a bias term. 
 * This type of classifier represents what is learned by a standard Support 
 * Vector Machine or the Kernel Perceptron.
 *
 * @param   <InputType>
 *      The input type for the categorizer.
 * @param   <EntryType>
 *      The type of weighted value entry in the categorizer's list of examples.
 * @author Justin Basilico
 * @since  2.0
 */
public class KernelBinaryCategorizer<InputType, EntryType extends WeightedValue<? extends InputType>>
    extends AbstractDiscriminantBinaryCategorizer<InputType>
    implements KernelContainer<InputType>,
        ThresholdBinaryCategorizer<InputType>
{
    /** The default value for the bias is {@value}. */
    public static final double DEFAULT_BIAS = 0.0;

    /** The internal kernel. */
    protected Kernel<? super InputType> kernel;

    /** The list of weighted examples that are used for categorization. */
    protected Collection<EntryType> examples;
    
    /** The bias term. */
    protected double bias;
    
    /**
     * Creates a new instance of KernelBinaryCategorizer.
     */
    public KernelBinaryCategorizer()
    {
        this((Kernel<? super InputType>) null);
    }
    
    /**
     * Creates a new instance of KernelBinaryCategorizer with the given kernel.
     *
     * @param  kernel The kernel to use.
     */
    public KernelBinaryCategorizer(
        final Kernel<? super InputType> kernel)
    {
        this(kernel, new ArrayList<EntryType>(), DEFAULT_BIAS);
    }
        
    /**
     * Creates a new instance of KernelBinaryCategorizer with the given kernel,
     * weighted examples, and bias.
     *
     * @param  kernel The kernel to use.
     * @param  examples The weighted examples.
     * @param  bias The bias.
     */
    public KernelBinaryCategorizer(
        final Kernel<? super InputType> kernel,
        final Collection<EntryType> examples,
        final double bias)
    {
        super();
        
        this.setExamples(examples);
        this.setBias(bias);
        this.setKernel(kernel);
    }
    
    /**
     * Creates a new copy of a KernelBinaryCategorizer.
     *
     * @param  other The KernelBinaryCategorizer to copy.
     */
    public KernelBinaryCategorizer(
        final KernelBinaryCategorizer<InputType, ? extends EntryType> other)
    {
        this(ObjectUtil.cloneSafe(other.getKernel()),
            (other.getExamples() == null) ? null : new ArrayList<EntryType>(other.getExamples()),
            other.getBias() );
    }
    
    /**
     * Categorizes the given input vector as a double by:
     * 
     *     sum w_i * k(input, x_i)
     *
     * @param  input The input to categorize.
     * @return The output categorization as a double where the sign is the
     *         categorization.
     */
    @Override
    public double evaluateAsDouble(
        final InputType input)
    {
        // The sum starts out with the bias term.
        double sum = this.bias;

        // Loop over all the examples.
        for (EntryType example : this.examples)
        {
            final double weight = example.getWeight();

            if (weight == 0.0)
            {
                continue;
            }

            // Evaluate the kernel between the example and the input.
            final double value = 
                this.kernel.evaluate(input, example.getValue());

            // Updatre the sum.
            sum += weight * value;
        }
        
        return sum;
    }

    /**
     * Gets the threshold, which is the negative of the bias.
     *
     * @return
     *      The threshold, which is the negative of the bias.
     */
    @Override
    public double getThreshold()
    {
        return -this.getBias();
    }

    /**
     * Sets the threshold, which is the negative of the bias.
     *
     * @param   threshold
     *      the threshold, which is the negative of the bias.
     */
    @Override
    public void setThreshold(
        final double threshold)
    {
        this.setBias(-threshold);
    }

    /**
     * Gets the list of weighted examples that categorizer is using.
     *
     * @return The list of weighted examples.
     */
    public Collection<EntryType> getExamples()
    {
        return this.examples;
    }
    
    /**
     * Sets the list of weighted examples that categorizer is using.
     *
     * @param  examples The list of weighted examples.
     */
    public void setExamples(
        final Collection<EntryType> examples)
    {
        this.examples = examples;
    }

    /**
     * Gets the bias term.
     *
     * @return bias The bias term.
     */
    public double getBias()
    {
        return this.bias;
    }

    /**
     * Sets the bias term.
     *
     * @param  bias The bias term.
     */
    public void setBias(
        final double bias)
    {
        this.bias = bias;
    }

    public Kernel<? super InputType> getKernel()
    {
        return this.kernel;
    }

    /**
     * Sets the internal kernel.
     *
     * @param  kernel The internal kernel.
     */
    public void setKernel(
        final Kernel<? super InputType> kernel)
    {
        this.kernel = kernel;
    }
    
}
