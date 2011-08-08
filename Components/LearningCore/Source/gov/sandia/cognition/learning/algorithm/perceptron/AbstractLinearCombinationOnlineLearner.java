/*
 * File:                AbstractLinearCombinationOnlineLearner.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry Learning Core
 * 
 * Copyright March 28, 2011, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 */

package gov.sandia.cognition.learning.algorithm.perceptron;

import gov.sandia.cognition.learning.function.categorization.DefaultKernelBinaryCategorizer;
import gov.sandia.cognition.learning.function.categorization.LinearBinaryCategorizer;
import gov.sandia.cognition.learning.function.kernel.Kernel;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.util.DefaultWeightedValue;

/**
 * An abstract class for online learning of linear binary categorizers that
 * take the form of a weighted sum of inputs. It has utility methods for
 * splitting up the computation into three steps and provides both linear
 * and kernel methods for learning.
 * 
 * @author  Justin Basilico
 * @since   3.3.0
 */
public abstract class AbstractLinearCombinationOnlineLearner
    extends AbstractKernelizableBinaryCategorizerOnlineLearner
{
    /** An option controlling whether or not the bias is updated or not.
     *  Individual algorithm implementations choose the default value for this.
     */
    protected boolean updateBias;
    
    /**
     * Creates a new {@code AbstractLinearCombinationOnlineLearner} with
     * default parameters.
     *
     * @param   updateBias
     *      Whether or not the bias should be updated by the algorithm.
     */
    public AbstractLinearCombinationOnlineLearner(
        final boolean updateBias)
    {
        this(updateBias, VectorFactory.getDefault());
    }

    /**
     * Creates a new {@code AbstractLinearCombinationOnlineLearner} with
     * the given parameters.
     *
     * @param   updateBias
     *      Whether or not the bias should be updated by the algorithm.
     * @param   vectorFactory
     *      The vector factory to use.
     */
    public AbstractLinearCombinationOnlineLearner(
        final boolean updateBias,
        final VectorFactory<?> vectorFactory)
    {
        super(vectorFactory);

        this.setUpdateBias(updateBias);
    }

    @Override
    public void update(
        final LinearBinaryCategorizer target,
        final Vector input,
        final boolean label)
    {

        Vector weights = target.getWeights();
        if (weights == null)
        {
            // This is the first example, so initialize the weight vector.
            this.initialize(target, input, label);
            weights = target.getWeights();
        }
        // else - Use the existing weights.

        // Predict the output as a double (negative values are false, positive
        // are true).
        final double predicted = target.evaluateAsDouble(input);
        final double actual = label ? +1.0 : -1.0;

        // Compute the update.
        final double update = this.computeUpdate(target, input, label,
            predicted);

        // Now compute the decay before we've applied the update.
        final double decay = this.computeDecay(
            target, input, label, predicted, update);

        // Do the decaying of old data.
        if (decay != 1.0)
        {
            if (decay == 0.0)
            {
                weights.zero();
            }
            else
            {
                weights.scaleEquals(decay);
            }
        }

        // Add the new value.
        if (update != 0.0)
        {
            if (update == 1.0)
            {
                // Special case for updating by 1 to avoid copying memory.
                if (label)
                {
                    weights.plusEquals(input);
                }
                else
                {
                    weights.minusEquals(input);
                }
            }
            else
            {
                weights.plusEquals(input.scale(update * actual));
            }

        }
        // else - Not an error.

        // Update the target.
        target.setWeights(weights);
        if (this.updateBias)
        {
            final double bias = target.getBias() * decay + actual * update;
            target.setBias(bias);
        }

        // Compute the rescaling.
        final double rescaling = this.computeRescaling(target, input, label,
            predicted, update, decay);
        if (rescaling != 1.0)
        {
            weights.scaleEquals(rescaling);
            
            target.setWeights(weights);
            if (this.updateBias)
            {
                final double bias = target.getBias() * rescaling;
                target.setBias(bias);
            }
        }
        // else - No need to rescale.

    }

    @Override
    public <InputType> DefaultKernelBinaryCategorizer<InputType> createInitialLearnedObject(
        final Kernel<? super InputType> kernel)
    {
        return new DefaultKernelBinaryCategorizer<InputType>(
            kernel);
    }

    @Override
    public <InputType> void update(
        final DefaultKernelBinaryCategorizer<InputType> target,
        final InputType input,
        final boolean output)
    {
        // Get the information about the example.
        final boolean label = output;

        if (target.getExamples().isEmpty())
        {
            // Target is not initialize, so initialize it.
            this.initialize(target, input, output);
        }

        // Predict the output as a double (negative values are false, positive
        // are true).
        final double predicted = target.evaluateAsDouble(input);
        final double actual = label ? +1.0 : -1.0;

        // Compute the update.
        final double update = this.computeUpdate(target, input, label,
            predicted);

        // Now compute the decay before we've applied the update.
        final double decay = this.computeDecay(
            target, input, label, predicted, update);

        // Do the decaying of old data.
        if (decay != 1.0)
        {
            if (decay == 0.0)
            {
                target.getExamples().clear();
            }
            else
            {
                for (DefaultWeightedValue<InputType> weighted
                    : target.getExamples())
                {
                    weighted.setWeight(decay * weighted.getWeight());
                }
            }
        }

        // Add the new value.
        if (update != 0.0)
        {
            target.add(input, update * actual);
        }
        // else - Not an error.

        // Update the target.
        if (this.updateBias)
        {
            final double bias = target.getBias() * decay + actual * update;
            target.setBias(bias);
        }

        // Compute the rescaling.
        final double rescaling = this.computeRescaling(target, input, label,
            predicted, update, decay);
        if (rescaling != 1.0)
        {
            for (DefaultWeightedValue<InputType> weighted
                : target.getExamples())
            {
                weighted.setWeight(rescaling * weighted.getWeight());
            }

            if (this.updateBias)
            {
                final double bias = target.getBias() * rescaling;
                target.setBias(bias);
            }
        }
        // else - No need to rescale.

    }

    /**
     * Initializes the linear binary categorizer. Can be overridden.
     * The default implementation just sets the weights to the zero vector.
     *
     * @param   target
     *      The categorizer to initialize.
     * @param   input
     *      The first input seen.
     * @param   actualCategory
     *      The actual category of the first input.
     */
    protected void initialize(
        final LinearBinaryCategorizer target,
        final Vector input,
        final boolean actualCategory)
    {
        Vector weights = this.getVectorFactory().createVector(
                input.getDimensionality());
        target.setWeights(weights);
    }

    /**
     * Compute the update weight in the linear case. Must be implemented by
     * subclasses.
     *
     * @param   target
     *      Target to compute the update for.
     * @param   input
     *      Input to use in computing the update.
     * @param   actualCategory
     *      The actual category of the input.
     * @param   predicted
     *      The predicted category of the input.
     * @return
     *      The update weight for how much to add the input to the target.
     *      May be zero if no update is needed.
     */
    protected abstract double computeUpdate(
        final LinearBinaryCategorizer target,
        final Vector input,
        final boolean actualCategory,
        final double predicted);

    /**
     * Computes the decay scalar for the existing weight vector. Can be
     * overridden. The default implementation just returns 1.0, which means no
     * change. Typically this will be a value between 0.0 and 1.0.
     *
     * @param   target
     *      Target to compute the update for.
     * @param   input
     *      Input to use in computing the update.
     * @param   actualCategory
     *      The actual category of the input.
     * @param   predicted
     *      The predicted category of the input.
     * @param   update
     *      The value from the computeUpdate step.
     * @return
     *      The decay to apply to the weight vector. Usually between 0.0 and
     *      1.0.
     */
    protected double computeDecay(
        final LinearBinaryCategorizer target,
        final Vector input,
        final boolean actualCategory,
        final double predicted,
        final double update)
    {
        // Default is no decay.
        return 1.0;
    }

    /**
     * Computes the rescaling for the new weight vector. Can be overridden.
     * The default implementation just returns 1.0, which means no change.
     * Typically this will be a value between 0.0 and 1.0.
     *
     * @param   target
     *      Target to compute the update for.
     * @param   input
     *      Input to use in computing the update.
     * @param   actualCategory
     *      The actual category of the input.
     * @param   predicted
     *      The predicted category of the input.
     * @param   update
     *      The value from the computeUpdate step.
     * @param   decay
     *      The value from the computeDecay step.
     * @return
     *      The rescaling to apply to the weight vector. Usually between 0.0 and
     *      1.0.
     */
    protected double computeRescaling(
        final LinearBinaryCategorizer target,
        final Vector input,
        final boolean actualCategory,
        final double predicted,
        final double update,
        final double decay)
    {
        // Default is no rescaling.
        return 1.0;
    }

    /**
     * Initializes the kernel binary categorizer. Can be overridden.
     * The default implementation does nothing.
     *
     * @param   <InputType>
     *      The input value for learning.
     * @param   target
     *      The categorizer to initialize.
     * @param   input
     *      The first input seen.
     * @param   actualCategory
     *      The actual category of the first input.
     */
    protected <InputType> void initialize(
        final DefaultKernelBinaryCategorizer<InputType> target,
        final InputType input,
        final boolean actualCategory)
    {
        // Nothing to initialize.
    }
    /**
     * Compute the update weight in the linear case. Must be implemented by
     * subclasses.
     *
     * @param   <InputType>
     *      The input value for learning.
     * @param   target
     *      Target to compute the update for.
     * @param   input
     *      Input to use in computing the update.
     * @param   actualCategory
     *      The actual category of the input.
     * @param   predicted
     *      The predicted category of the input.
     * @return
     *      The update weight for how much to add the input to the target.
     *      May be zero if no update is needed.
     */
    protected abstract <InputType> double computeUpdate(
        final DefaultKernelBinaryCategorizer<InputType> target,
        final InputType input,
        final boolean actualCategory,
        final double predicted);

    /**
     * Computes the decay scalar for the existing weights. Can be overridden.
     * The default implementation just returns 1.0, which means no change.
     * Typically this will be a value between 0.0 and 1.0.
     *
     * @param   <InputType>
     *      The input value for learning.
     * @param   target
     *      Target to compute the update for.
     * @param   input
     *      Input to use in computing the update.
     * @param   actualCategory
     *      The actual category of the input.
     * @param   predicted
     *      The predicted category of the input.
     * @param   update
     *      The value from the computeUpdate step.
     * @return
     *      The decay to apply to the weights. Usually between 0.0 and
     *      1.0.
     */
    protected <InputType> double computeDecay(
        final DefaultKernelBinaryCategorizer<InputType> target,
        final InputType input,
        final boolean actualCategory,
        final double predicted,
        final double update)
    {
        // Default is no decay.
        return 1.0;
    }

    /**
     * Computes the rescaling for the new weights. Can be overridden.
     * The default implementation just returns 1.0, which means no change.
     * Typically this will be a value between 0.0 and 1.0.
     *
     * @param   <InputType>
     *      The input value for learning.
     * @param   target
     *      Target to compute the update for.
     * @param   input
     *      Input to use in computing the update.
     * @param   actualCategory
     *      The actual category of the input.
     * @param   predicted
     *      The predicted category of the input.
     * @param   update
     *      The value from the computeUpdate step.
     * @param   decay
     *      The value from the computeDecay step.
     * @return
     *      The rescaling to apply to the weights. Usually between 0.0 and
     *      1.0.
     */
    protected <InputType> double computeRescaling(
        final DefaultKernelBinaryCategorizer<InputType> target,
        final InputType input,
        final boolean actualCategory,
        final double predicted,
        final double update,
        final double decay)
    {
        // Default is no rescaling.
        return 1.0;
    }

    /**
     * Gets whether or not the algorithm is updating the bias.
     *
     * @return
     *      True if the algorithm is updating the bias. Otherwise, false.
     */
    public boolean isUpdateBias()
    {
        return this.updateBias;
    }

    /**
     * Sets whether or not the algorithm is updating the bias.
     *
     * @param   updateBias
     *      True if the algorithm is updating the bias. Otherwise, false.
     */
    protected void setUpdateBias(
        final boolean updateBias)
    {
        this.updateBias = updateBias;
    }
    
}
