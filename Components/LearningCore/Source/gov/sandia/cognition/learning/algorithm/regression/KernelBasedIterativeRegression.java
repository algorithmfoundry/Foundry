/*
 * File:                KernelBasedIterativeRegression.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright September 17, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.learning.algorithm.regression;

import gov.sandia.cognition.algorithm.MeasurablePerformanceAlgorithm;
import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.learning.algorithm.AbstractAnytimeSupervisedBatchLearner;
import gov.sandia.cognition.learning.function.kernel.Kernel;
import gov.sandia.cognition.learning.data.InputOutputPair;
import gov.sandia.cognition.learning.function.scalar.KernelScalarFunction;
import gov.sandia.cognition.util.DefaultNamedValue;
import gov.sandia.cognition.util.DefaultWeightedValue;
import gov.sandia.cognition.util.NamedValue;
import gov.sandia.cognition.util.ObjectUtil;
import gov.sandia.cognition.util.WeightedValue;
import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * The {@code KernelBasedIterativeRegression} class implements an online version of
 * the Support Vector Regression algorithm. It learns a scalar kernel function
 * that uses the given kernel to map inputs onto real numbers. The code is based
 * on the pseudocode in the book "Kernel Methods for Pattern Analysis" by
 * J. Shawe-Taylor and N. Cristianini. However, this the pseudo-code in the 
 * book is incorrect and seems to be missing an extra division by the kernel
 * of the example with itself. It also does not check to make sure that the
 * error is outside of the minimum sensitivity range. This implementation also
 * includes a bias term, which is also omitted from the pseudo-code in the book.
 * <BR><BR>
 * The update to the weight that is implemented is:
 * <BR>
 *    alpha_i = alpha_i +
 *        (y_i + epsilon * sign(alpha_i) + sum alpha_j k(x_j, x_i) + b)
 *         / k(x_i, x_i)
 * <BR><BR>
 * The loss function underlying the implementation is the epsilon-insensitive
 * loss. This parameter is named minSensitivity in this implementation. It 
 * means that errors less than or equal to minSensitivity are ignored.
 *
 * @param <InputType> Input parameter to the Kernels
 * @author Justin Basilico
 * @since  2.0
 */
@PublicationReference(
    author={
        "John Shawe-Taylor",
        "Nello Cristianini"
    },
    title="Kernel Methods for Pattern Analysis",
    type=PublicationType.Book,
    year=2004,
    url="http://www.kernel-methods.net/"
)
public class KernelBasedIterativeRegression<InputType>
    extends AbstractAnytimeSupervisedBatchLearner<InputType, Double, KernelScalarFunction<InputType>>
    implements MeasurablePerformanceAlgorithm
{

    /** The default maximum number of iterations, {@value}. */
    public static final int DEFAULT_MAX_ITERATIONS = 100;

    /** The default minimum sensitivity, {@value}. */
    public static final double DEFAULT_MIN_SENSITIVITY = 10.0;

    /** The kernel to use. */
    private Kernel<? super InputType> kernel;

    /** The bound on sensitivity. */
    private double minSensitivity;

    /** The result categorizer. */
    private KernelScalarFunction<InputType> result;

    /** The number of errors on the most recent iteration. */
    private int errorCount;

    /** The mapping of weight objects to non-zero weighted examples 
     *  (support vectors). */
    private transient LinkedHashMap<InputOutputPair<? extends InputType, Double>, DefaultWeightedValue<InputType>> supportsMap;

    /**
     * Creates a new instance of KernelBasedIterativeRegression.
     */
    public KernelBasedIterativeRegression()
    {
        this( null );
    }

    /**
     * Creates a new KernelBasedIterativeRegression with the given kernel.
     *
     * @param  kernel The kernel to use.
     */
    public KernelBasedIterativeRegression(
        final Kernel<? super InputType> kernel )
    {
        this( kernel, DEFAULT_MIN_SENSITIVITY );
    }

    /**
     * Creates a new KernelBasedIterativeRegression with the given kernel.
     *
     * @param  kernel The kernel to use.
     * @param  minSensitivity The minimum sensitivity to errors.
     */
    public KernelBasedIterativeRegression(
        final Kernel<? super InputType> kernel,
        final double minSensitivity )
    {
        this( kernel, minSensitivity, DEFAULT_MAX_ITERATIONS );
    }

    /**
     * Creates a new KernelBasedIterativeRegression with the given kernel and 
     * maximum number of iterations.
     *
     * @param  kernel The kernel to use.
     * @param  minSensitivity The minimum sensitivity to errors.
     * @param  maxIterations The maximum number of iterations.
     */
    public KernelBasedIterativeRegression(
        final Kernel<? super InputType> kernel,
        final double minSensitivity,
        final int maxIterations )
    {
        super( maxIterations );

        this.setKernel( kernel );
        this.setMinSensitivity( minSensitivity );

        this.setResult( null );
        this.setErrorCount( 0 );
        this.setSupportsMap( null );
    }

    @Override
    public KernelBasedIterativeRegression<InputType> clone()
    {
        KernelBasedIterativeRegression<InputType> clone =
            (KernelBasedIterativeRegression<InputType>) super.clone();
        clone.setKernel( ObjectUtil.cloneSmart( this.getKernel() ) );
        clone.setResult( ObjectUtil.cloneSafe( this.getResult() ) );
        clone.setSupportsMap( ObjectUtil.cloneSmart( this.getSupportsMap() ) );

        return clone;
    }

    protected boolean initializeAlgorithm()
    {
        if (this.getData() == null)
        {
            // Error: No data to learn on.
            return false;
        }

        // Count the number of valid examples.
        int validCount = 0;
        for (InputOutputPair<? extends InputType, Double> example : this.getData())
        {
            if (example != null)
            {
                validCount++;
            }
        }

        if (validCount <= 0)
        {
            // Nothing to perform learning on.
            return false;
        }

        // Set up the learning variables.
        this.setErrorCount( validCount );
        this.setSupportsMap( new LinkedHashMap<InputOutputPair<? extends InputType, Double>, DefaultWeightedValue<InputType>>() );
        this.setResult( new KernelScalarFunction<InputType>(
            this.getKernel(), this.getSupportsMap().values(), 0.0 ) );

        return true;
    }

    protected boolean step()
    {
        // Reset the number of errors for the new iteration.
        this.setErrorCount( 0 );

        if (this.getData().size() == 1)
        {
            // If there is only one data point, there is nothing to fit.
            InputOutputPair<? extends InputType, Double> first =
                this.getData().iterator().next();
            this.getResult().getExamples().clear();
            this.getResult().setBias( first.getOutput() );
            return false;
        }

        // Loop over all the training instances.
        for (InputOutputPair<? extends InputType, Double> example : this.getData())
        {
            if (example == null)
            {
                continue;
            }

            // Compute the predicted classification and get the actual
            // classification.
            final InputType input = example.getInput();
            final double actual = example.getOutput();
            final double prediction = this.result.evaluate( input );
            final double error = actual - prediction;

            // This is the update psuedo-code as listed in the book:
            // alphahat_i = alpha_i
            // alpha_i = alpha_i + y_i - epsilon * sign(alpha_i) 
            //           - sum alpha_j k(x_j, x_i)
            // if ( alphahat_i * alpha_i < 0 ) then alpha_i = 0
            // where when alpha_i is zero the value in [+1, -1] is used for
            // sign(alpha_i) that minimizes the size of the update.
            //
            // However, this code doesn't work as it is listed in the book.
            // Instead it adds an extra division by the value k(x_i, x_i)
            // to the update, making it:
            // alpha_i = alpha_i +
            //     (y_i - epsilon * sign(alpha_i) - sum alpha_j k(x_j, x_i) )
            //          / k(x_i, x_i)
            //
            // Also a check is made such that the weight value (alpha_i) is
            // not updated when the prediction error is less than the minimum
            // sensitivity.
            DefaultWeightedValue<InputType> support = this.supportsMap.get( example );
            final double oldWeight = support == null ? 0.0 : support.getWeight();

            double newWeight = oldWeight;
// TODO: Determine if this check to see if the error is outside the minimum
// sensitivity still preserves the support-vector nature of the algorithm or
// if it makes it a more greedy algorithm such as the Perceptron algorithm.
            if (Math.abs( error ) >= this.minSensitivity)
            {
                double weightUpdate = error;

                // This part computes the epsilon * sign(alpha_i) to deal with
                // the case where alpha_i is zero, in which case the sign must
                // be either interpreted as -1 or +1 based on which provides
                // a smaller update.
                if (oldWeight == 0.0)
                {
                    double positiveUpdate = weightUpdate - this.minSensitivity;
                    double negativeUpdate = weightUpdate + this.minSensitivity;

                    if (Math.abs( positiveUpdate ) <= Math.abs( negativeUpdate ))
                    {
                        weightUpdate -= this.minSensitivity;
                    }
                    else
                    {
                        weightUpdate += this.minSensitivity;
                    }
                }
                else if (oldWeight > 0.0)
                {
                    // This functions as -epsilon * sign(alpha_i) where
                    // sign(alpha_i) = +1.
                    weightUpdate -= this.minSensitivity;
                }
                else
                {
                    // This functions as -epsilon * sign(alpha_i) where
                    // sign(alpha_i) = -1.
                    weightUpdate += this.minSensitivity;
                }

                // Divide the update by the kernel applied to itself, while
                // avoiding a divide-by-zero error.
                final double selfKernel = this.kernel.evaluate( input, input );
                if (selfKernel != 0.0)
                {
                    weightUpdate /= selfKernel;
                }

                // Compute the new weight by adding the old weight and the
                // weight update.
                newWeight = oldWeight + weightUpdate;

                // This removes unneeded weights.
                if (oldWeight * newWeight < 0.0)
                {
                    newWeight = 0.0;
                }
            }

            // Compute the weight to see if this was considered an "error".
            final double difference = newWeight - oldWeight;

            if (difference != 0.0)
            {
                // We need to change the kernel scalar function..
                this.setErrorCount( this.getErrorCount() + 1 );

                // We are going to update the weight for this example and the
                // global bias.
                final double oldBias = this.result.getBias();
                final double newBias = oldBias + difference;

                if (support == null)
                {
                    // Add a support for this example.
                    support = new DefaultWeightedValue<InputType>( input, newWeight );
                    this.supportsMap.put( example, support );
                }
                else if (newWeight == 0.0)
                {
                    // This example is no longer a support.
                    this.supportsMap.remove( example );
                }
                else
                {
                    // Update the weight for the support.
                    support.setWeight( newWeight );
                }

                // Update the bias.
                this.result.setBias( newBias );
            }
        // else - The classification was correct, no need to update.
        }

        // Keep going while the error count is positive.
        return this.getErrorCount() > 0;
    }

    protected void cleanupAlgorithm()
    {
        if (this.getSupportsMap() != null)
        {
            // Make the result object have a more efficient backing collection
            // at the end.
            this.getResult().setExamples(
                new ArrayList<WeightedValue<InputType>>(
                this.getSupportsMap().values() ) );

            this.setSupportsMap( null );
        }
    }

    /**
     * Gets the kernel to use.
     *
     * @return The kernel to use.
     */
    public Kernel<? super InputType> getKernel()
    {
        return this.kernel;
    }

    /**
     * Sets the kernel to use.
     *
     * @param  kernel The kernel to use.
     */
    public void setKernel(
        final Kernel<? super InputType> kernel )
    {
        this.kernel = kernel;
    }

    public KernelScalarFunction<InputType> getResult()
    {
        return this.result;
    }

    /**
     * Sets the object currently being result.
     *
     * @param  result The object currently being result.
     */
    protected void setResult(
        final KernelScalarFunction<InputType> result )
    {
        this.result = result;
    }

    /**
     * Gets the error count of the most recent iteration.
     *
     * @return The current error count.
     */
    public int getErrorCount()
    {
        return this.errorCount;
    }

    /**
     * Sets the error count of the most recent iteration.
     *
     * @param  errorCount The current error count.
     */
    protected void setErrorCount(
        final int errorCount )
    {
        this.errorCount = errorCount;
    }

    /**
     * Gets the mapping of examples to weight objects (support vectors).
     *
     * @return The mapping of examples to weight objects.
     */
    protected LinkedHashMap<InputOutputPair<? extends InputType, Double>, DefaultWeightedValue<InputType>> getSupportsMap()
    {
        return supportsMap;
    }

    /**
     * Gets the mapping of examples to weight objects (support vectors).
     *
     * @param  supportsMap The mapping of examples to weight objects.
     */
    protected void setSupportsMap(
        final LinkedHashMap<InputOutputPair<? extends InputType, Double>, DefaultWeightedValue<InputType>> supportsMap )
    {
        this.supportsMap = supportsMap;
    }

    /**
     * Gets the minimum sensitivity that an example can have on the result
     * function.
     *
     * @return The minimum sensitivity.
     */
    public double getMinSensitivity()
    {
        return this.minSensitivity;
    }

    /**
     * Sets the minimum sensitivity that an example can have on the result
     * function.
     *
     * @param  minSensitivity The minimum sensitivity.
     */
    public void setMinSensitivity(
        final double minSensitivity )
    {
        if (minSensitivity < 0.0)
        {
            throw new IllegalArgumentException(
                "minSensitivity must be non-negative." );
        }

        this.minSensitivity = minSensitivity;
    }

    /**
     * Gets the performance, which is the error count on the last iteration.
     * 
     * @return The performance of the algorithm.
     */
    public NamedValue<Integer> getPerformance()
    {
        return new DefaultNamedValue<Integer>("error count", this.getErrorCount());
    }
}
