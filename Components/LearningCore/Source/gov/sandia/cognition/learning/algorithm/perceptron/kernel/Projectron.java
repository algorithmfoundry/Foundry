/*
 * File:                Projectron.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry Learning Core
 * 
 * Copyright May 04, 2011, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 */

package gov.sandia.cognition.learning.algorithm.perceptron.kernel;

import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.learning.function.categorization.DefaultKernelBinaryCategorizer;
import gov.sandia.cognition.learning.function.kernel.Kernel;
import gov.sandia.cognition.math.matrix.Matrix;
import gov.sandia.cognition.math.matrix.MatrixFactory;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.util.ArgumentChecker;
import gov.sandia.cognition.util.DefaultWeightedValue;

/**
 * An implementation of the Projectron algorithm, which is an online kernel
 * binary categorizer learner that has a budget parameter tuned by the eta
 * parameter. It is based on the Perceptron algorithm with the addition that
 * it attempts to project errors onto the existing set of supports and
 * adjusting their weights instead of always adding them.
 *
 * @param   <InputType>
 *      The type of input to learn over. Passed to the kernel function.
 * @author  Justin Basilico
 * @since   3.3.0
 */
@PublicationReference(
    author={"Francesco Orabona", "Joseph Keshet", "Barbara Caputo"},
    title="Bounded Kernel-Based Online Learning",
    year=2009,
    type=PublicationType.Journal,
    publication="Journal of Machine Learning Research",
    pages={2643, 2666},
    url="http://portal.acm.org/citation.cfm?id=1755875")
public class Projectron<InputType>
    extends AbstractOnlineKernelBinaryCategorizerLearner<InputType>
{
    
    /** The default value of eta is {@value}. */
    public static final double DEFAULT_ETA = 0.01;

    /** The eta parameter, which ends up controlling the number of supports
     *  created. Must be non-negative. */
    protected double eta;

    /**
     * Creates a new {@code Projectron} with a null kernel and default
     * parameters.
     */
    public Projectron()
    {
        this(null);
    }

    /**
     * Creates a new {@code Projectron} with the given kernel and default
     * parameters.
     *
     * @param   kernel
     *      The kernel to use.
     */
    public Projectron(
        final Kernel<? super InputType> kernel)
    {
        this(kernel, DEFAULT_ETA);
    }

    /**
     * Creates a new {@code Projectron} with the given parameters.
     *
     * @param   kernel
     *      The kernel to use.
     * @param eta
     *      The eta parameter, which controls the number of supports created.
     *      Must be non-negative.
     */
    public Projectron(
        final Kernel<? super InputType> kernel,
        final double eta)
    {
        super(kernel);

        this.setEta(eta);
    }

    @Override
    public void update(
        final DefaultKernelBinaryCategorizer<InputType> target,
        final InputType input,
        final boolean label)
    {
        // Predict the output as a double (negative values are false, positive
        // are true).
        final double prediction = target.evaluateAsDouble(input);
        final double actual = label ? +1.0 : -1.0;
        final double margin = prediction * actual;

        if (!this.shouldUpdate(margin))
        {
            // Not an error, so no need to update.
            return;
        }

        // Get the supports.
        final int size = target.getExampleCount();

        // Create the kernel matrix and kernel vector.
        final Kernel<? super InputType> kernel = target.getKernel();
        final Matrix K = MatrixFactory.getDenseDefault().createMatrix(size, size);
        final Vector k = VectorFactory.getDenseDefault().createVector(size);

        for (int i = 0; i < size; i++)
        {
            final InputType xI = target.get(i).getValue();
            k.setElement(i, kernel.evaluate(input, xI));

            K.setElement(i, i, kernel.evaluate(xI, xI));

            // Loop over the upper-diagonal.
            for (int j = i + 1; j < size; j++)
            {
                final InputType xJ = target.get(j).getValue();
                final double value = kernel.evaluate(xI, xJ);
                K.setElement(i, j, value);
                K.setElement(j, i, value);
            }
        }
// TODO: Rather than performing the direct inverse on the kernel matrix, it
// is possible to incrementally compute the inverse of the kernel matrix as
// each item is added. This may make the algorithm faster to avoid the extra
// computation, however it will be extra book-keeping attached to the learned
// object. It should be done if the Projectron appears to perform well.
// - jdbasil (2011-04-11)
        // Compute d by K^-1 * k.
        final Vector d = K.inverse().times(k);
        final double kernelInputInput = kernel.evaluate(input, input);

        // Compute delta squared and then delta.
        final double deltaSquared = kernelInputInput - k.dotProduct(d);
        final double delta = Math.sqrt(Math.max(0.0, deltaSquared));
        
        applyUpdate(target, input, actual, margin, kernelInputInput, delta, d);
    }

    /**
     * Determine if an update should be made. This is here so that the
     * Projectron++ implementation can override it to provide a margin update.
     *
     * @param   margin
     *      The margin.
     * @return
     *      A determination of whether or not an update should be made based on
     *      the margin.
     */
    protected boolean shouldUpdate(
        final double margin)
    {
        return margin <= 0.0;
    }

    /**
     * Apply the update for the Projectron. This function is put here so that
     * it can be overriden by the Projectron++ implementation, which also
     * enforces a margin.
     *
     * @param   target
     *      The target to update.
     * @param   input
     *      The input value.
     * @param   actual
     *      The actual label.
     * @param   margin
     *      The margin on the input.
     * @param   kernelInputInput
     *      The kernel function k(input, input).
     * @param   delta
     *      The value delta computed by the Projectron.
     * @param   d
     *      The vector d that represents the coefficients for the projection.
     */
    protected void applyUpdate(
        final DefaultKernelBinaryCategorizer<InputType> target,
        final InputType input,
        final double actual,
        final double margin,
        final double kernelInputInput,
        final double delta,
        final Vector d)
    {
        if (delta <= this.getEta())
        {
            // Get the supports.
            final int size = target.getExampleCount();
            
            // Use the projection of the example point onto the others.
            for (int i = 0; i < size; i++)
            {
                final DefaultWeightedValue<InputType> support = target.get(i);
                final double oldWeight = support.getWeight();
                final double newWeight = oldWeight + d.getElement(i) * actual;
                support.setWeight(newWeight);
            }
        }
        else
        {
            // Add a new support.
            target.add(input, actual);
        }
    }

    /**
     * Gets the eta parameter that controls how many supports are allowed.
     *
     * @return
     *      The eta parameter. Cannot be negative.
     */
    public double getEta()
    {
        return this.eta;
    }

    /**
     * Sets the eta parameter that controls how many supports are allowed.
     *
     * @param   eta
     *      The eta parameter. Cannot be negative.
     */
    public void setEta(
        final double eta)
    {
        ArgumentChecker.assertIsNonNegative("eta", eta);
        this.eta = eta;
    }
    /**
     * An implementation of the Projectron++ algorithm, which is an online
     * kernel binary categorizer learner that has a budget parameter tuned by
     * the eta parameter. It is an extension of the Projectron algorithm
     * inspired by the Passive Aggressive I algorithm (PA-I), which uses a
     * linear soft margin constraint. This is why the class is named
     * {@code LinearSoftMargin}, like the PA-I implementation.
     *
     * @param   <InputType>
     *      The type of input to learn over. Passed to the kernel function.
     * @author  Justin Basilico
     * @since   3.3.0
     */
    @PublicationReference(
        author={"Francesco Orabona", "Joseph Keshet", "Barbara Caputo"},
        title="Bounded Kernel-Based Online Learning",
        year=2009,
        type=PublicationType.Journal,
        publication="Journal of Machine Learning Research",
        pages={2643, 2666},
        url="http://portal.acm.org/citation.cfm?id=1755875",
        notes="This is the Projectron++.")
    public static class LinearSoftMargin<InputType>
        extends Projectron<InputType>
    {

        /**
         * Creates a new {@code Projectron.LinearSoftMargin} with a null kernel
         * and default parameters.
         */
        public LinearSoftMargin()
        {
            this(null);
        }

        /**
         * Creates a new {@code Projectron.LinearSoftMargin} with the given
         * kernel and default parameters.
         *
         * @param   kernel
         *      The kernel to use.
         */
        public LinearSoftMargin(
            final Kernel<? super InputType> kernel)
        {
            this(kernel, DEFAULT_ETA);
        }

        /**
         * Creates a new {@code Projectron.LinearSoftMargin} with the given
         * parameters.
         *
         * @param   kernel
         *      The kernel to use.
         * @param eta
         *      The eta parameter, which controls the number of supports created.
         *      Must be non-negative.
         */
        public LinearSoftMargin(
            final Kernel<? super InputType> kernel,
            final double eta)
        {
            super(kernel, eta);
        }

        @Override
        protected boolean shouldUpdate(
            final double margin)
        {
            return margin <= 1.0;
        }

        @Override
        protected void applyUpdate(
            final DefaultKernelBinaryCategorizer<InputType> target,
            final InputType input,
            final double actual,
            final double margin,
            final double kernelInputInput,
            final double delta,
            final Vector d)
        {
            if (margin <= 0.0)
            {
                // Prediction error, apply the update in the standard way for
                // the Projectron.
                super.applyUpdate(target, input, actual, margin,
                    kernelInputInput, delta, d);
            }
            else if (margin <= 1.0)
            {
                // Margin error, attempt to update by projecting onto the
                // existing supports.
                
                // Get the supports.
                final int size = target.getExampleCount();
            
                final double loss = 1.0 - margin;
                if (loss >= delta / eta)
                {
                    // Compute tau, which is the amount to update.
                    final double norm = Math.max(0.0, kernelInputInput - delta);
                    final double tau = Math.min(1.0,
                        Math.min(loss / norm,
                            2.0 * (loss - delta / eta) / norm));
                    
                    // Use the projection of the example point onto the others.
                    for (int i = 0; i < size; i++)
                    {
                        final DefaultWeightedValue<InputType> support =
                            target.get(i);
                        final double oldWeight = support.getWeight();
                        final double newWeight = oldWeight
                            + tau * d.getElement(i) * actual;
                        support.setWeight(newWeight);
                    }
                }
                // else - Cannot perform a projection so ignore this margin
                // violation.
            }
        }

    }

}
