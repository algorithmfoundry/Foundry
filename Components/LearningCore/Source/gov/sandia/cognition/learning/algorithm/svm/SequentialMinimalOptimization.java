/*
 * File:                SequentialMinimalOptimization.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry Learning Core
 *
 * Copyright July 15, 2010, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government. Export
 * of this program may require a license from the United States Government.
 *
 */

package gov.sandia.cognition.learning.algorithm.svm;

import gov.sandia.cognition.algorithm.MeasurablePerformanceAlgorithm;
import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.learning.algorithm.AbstractAnytimeSupervisedBatchLearner;
import gov.sandia.cognition.learning.data.InputOutputPair;
import gov.sandia.cognition.learning.function.categorization.KernelBinaryCategorizer;
import gov.sandia.cognition.learning.function.kernel.Kernel;
import gov.sandia.cognition.learning.function.kernel.KernelContainer;
import gov.sandia.cognition.math.MutableDouble;
import gov.sandia.cognition.util.DefaultNamedValue;
import gov.sandia.cognition.util.DefaultWeightedValue;
import gov.sandia.cognition.util.Randomized;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

/**
 * An implementation of the Sequential Minimal Optimization (SMO) algorithm for
 * training a Support Vector Machine (SVM), which is a kernel-based binary
 * categorizer.
 *
 * @param   <InputType>
 *      The type of the input data to learn the support vector machine.
 * @author  Justin Basilico
 * @since   3.1
 */
@PublicationReference(
    title="Fast training of support vector machines using sequential minimal optimization",
    author="John C. Platt",
    year=1999,
    type=PublicationType.BookChapter,
    pages={185, 208},
    publication="Advances in Kernel Methods",
    url="http://research.microsoft.com/pubs/68391/smo-book.pdf")
public class SequentialMinimalOptimization<InputType>
    extends AbstractAnytimeSupervisedBatchLearner<InputType, Boolean, KernelBinaryCategorizer<InputType, DefaultWeightedValue<InputType>>>
    implements KernelContainer<InputType>, Randomized, MeasurablePerformanceAlgorithm
{
    // TODO: Add a better explanation of the SMO algorithm in the class
    // description.
    // -- jdbasil (2010-10-05)

    /** The default maximum number of iterations is {@value}. */
    public static final int DEFAULT_MAX_ITERATIONS = 1000;

    /** The default maximum penalty is infinite, which means that it is
     *  hard-assignment. */
    public static final double DEFAULT_MAX_PENALTY = Double.POSITIVE_INFINITY;

    /** The default error tolerance is 0.001, which is what was recommended in
     *  the original Sequential Minimal Optimization paper. */
    public static final double DEFAULT_ERROR_TOLERANCE = 0.001;

    /** The default effective value for zero is {@value}. */
    public static final double DEFAULT_EFFECTIVE_ZERO = 1.0e-10;

    /** The default size of the kernel cache. */
    public static final int DEFAULT_KERNEL_CACHE_SIZE = 1000;

    /** The performance name is {@value}. */
    public static final String PERFORMANCE_NAME = "Change count";

    /** The maximum penalty parameter (C). Must be greater than 0.0. */
    private double maxPenalty;

    /** The error tolerance for the algorithm. Must be non-negative. Also
     *  known as "tol" or "tolerance". */
    private double errorTolerance;

    /** The effective value for zero to use in the computation to deal with
     *  numerical imprecision. Must be a non-negative number. Typically a very
     *  small value. Also sometimes known as epsilon. */
    private double effectiveZero;

    /** The size of the kernel cache, which is the number of kernel computations
     *  to keep cached. May be 0 to indicate that the kernel cache should not
     *  be used. */
    private int kernelCacheSize;

    /** The random number generator to use. */
    private Random random;

    /** The kernel to use. */
    private Kernel<? super InputType> kernel;

    /** The result categorizer. */
    private transient KernelBinaryCategorizer<InputType, DefaultWeightedValue<InputType>> result;

    /** The training data as an array list. */
    private transient ArrayList<InputOutputPair<? extends InputType, Boolean>>
        dataList;

    /** The number of examples in the training data. */
    private transient int dataSize;

    /** A flag indicating if the next pass should examine all of the data points
     *  or just the support vectors in order to pick the two points to update.
     */
    private transient boolean examineAll;

    /** The number of items changed on the most recent iteration. */
    private transient int changeCount;

    /** The mapping of weight objects to non-zero weighted examples
     *  (support vectors). */
    private transient LinkedHashMap<Integer, DefaultWeightedValue<InputType>>
        supportsMap;

    /** A list of the alpha indices that are not at the bound. */
    private transient LinkedHashSet<Integer> nonBoundAlphaIndices;

    /** A cache of the current error values for all points that have an error.
     */
    private transient LinkedHashMap<Integer, MutableDouble> errorCache;

    /** The kernel cache, indexed using a long that is the concatentation of
     *  the two indices of the values. */
    private transient LinkedHashMap<Long, Double> kernelCache;

    /**
     * Creates a new instance of Sequential Minimal Optimization. It initializes
     * the parameters to default values and the kernel to null.
     */
    public SequentialMinimalOptimization()
    {
        this(null);
    }

    /**
     * Creates a new instance of Sequential Minimal Optimization with the
     * given kernel. All other parameters are set to their default values.
     *
     * @param   kernel
     *      The kernel to use.
     */
    public SequentialMinimalOptimization(
        final Kernel<? super InputType> kernel)
    {
        this(kernel, new Random());
    }

    /**
     * Creates a new instance of Sequential Minimal Optimization with the
     * given kernel and random number generator. All other parameters are set
     * to their default values.
     *
     * @param   kernel
     *      The kernel to use.
     * @param   random
     *      The random number generator to use.
     */
    public SequentialMinimalOptimization(
        final Kernel<? super InputType> kernel,
        final Random random)
    {
        this(kernel, DEFAULT_MAX_PENALTY, DEFAULT_ERROR_TOLERANCE,
            DEFAULT_EFFECTIVE_ZERO, DEFAULT_KERNEL_CACHE_SIZE,
            DEFAULT_MAX_ITERATIONS, random);
    }

    /**
     *
     * Creates a new instance of Sequential Minimal Optimization with the
     * given kernel and random number generator. All other parameters are set
     * to their default values.
     *
     * @param   kernel
     *      The kernel to use.
     * @param   maxPenalty
     *      The maximum penalty parameter (C). Must be greater than 0.0.
     * @param   errorTolerance
     *      The error tolerance for the algorithm. Must be non-negative.
     * @param   effectiveZero
     *      The effective value for zero. Must be non-negative.
     * @param   kernelCacheSize
     *      The size of the kernel cache. Must be non-negative.
     * @param maxIterations
     *      The maximum number of iterations to run the algorithm.
     * @param   random
     *      The random number generator to use.
     */
    public SequentialMinimalOptimization(
        final Kernel<? super InputType> kernel,
        final double maxPenalty,
        final double errorTolerance,
        final double effectiveZero,
        final int kernelCacheSize,
        final int maxIterations,
        final Random random)
    {
        super(maxIterations);

        this.setKernel(kernel);
        this.setMaxPenalty(maxPenalty);
        this.setErrorTolerance(errorTolerance);
        this.setEffectiveZero(effectiveZero);
        this.setKernelCacheSize(kernelCacheSize);
        this.setRandom(random);
    }

    @Override
    protected boolean initializeAlgorithm()
    {
        this.result = null;

        if (this.getData() == null)
        {
            // Error: No data to learn on.
            return false;
        }

        // We need to be able to have random-access into the given training
        // data, so we create an array-list to store it.
        this.dataList =
            new ArrayList<InputOutputPair<? extends InputType, Boolean>>(
                this.getData().size());

        // Fill the data list with the valid data points. Also count the
        // number of examples in the positive class.
        int positives = 0;
        for (InputOutputPair<? extends InputType, Boolean> example
            : this.getData())
        {
            if (example != null && example.getInput() != null
                && example.getOutput() != null)
            {
                // This is a good example.
                this.dataList.add(example);

                if (example.getOutput())
                {
                    positives++;
                }
            }
        }

        // Store the data size.
        this.dataSize = this.dataList.size();

        if (this.dataSize <= 0)
        {
            // Error: No valid data to learn from.
            this.dataList = null;
            return false;
        }
        else if (positives <= 0 || positives >= this.dataSize)
        {
            throw new IllegalArgumentException("Data is all one category");
        }

        // Initialize the supporting data structures.
        this.changeCount = this.getData().size();
        this.supportsMap =
            new LinkedHashMap<Integer, DefaultWeightedValue<InputType>>();
        this.nonBoundAlphaIndices = new LinkedHashSet<Integer>();
        this.errorCache = new LinkedHashMap<Integer, MutableDouble>();

        // Create the kernel cache, if requested.
        if (this.kernelCacheSize > 1 && this.dataSize > 1)
        {
            final int cacheSize =
                Math.min(dataSize * dataSize, this.kernelCacheSize);

            // This creates a cache with a least-recently-used removal policy.
            this.kernelCache =
                new LinkedHashMap<Long, Double>(cacheSize, 0.75f, true)
                {
                    @Override
                    protected boolean removeEldestEntry(
                        final Entry<Long, Double> eldest)
                    {
                        return this.size() > cacheSize;
                    }
                };
        }

        // Platt:
        //   initialize alpha array to all zero
        //   initialize threshold to zero
        this.result = new KernelBinaryCategorizer<InputType, DefaultWeightedValue<InputType>>(
            this.kernel, this.supportsMap.values(), 0.0);
        return true;
    }

    @Override
    protected boolean step()
    {
        this.changeCount = 0;
        
        if (this.examineAll)
        {
            // Loop over all examples.
            for (int j = 0; j < this.dataSize; j++)
            {
                this.changeCount += this.examineExample(j);
            }
        }
        else
        {
            // Platt:
            //   Loop over all indices where alpha is not 0 or C.
            // We copy the list of indices to avoid concurrent modification
            // exceptions.
            for (Integer j : new ArrayList<Integer>(this.nonBoundAlphaIndices))
            {
                final double alphaJ = this.getAlpha(j);

                if (alphaJ > 0.0 && alphaJ < this.maxPenalty)
                {
                    this.changeCount += this.examineExample(j);
                }
            }
        }

        if (this.examineAll)
        {
            // We examined everything in this pass, so no need to do it on
            // the next pass.
            examineAll = false;
        }
        else if (this.changeCount <= 0)
        {
            // There were no changes in this pass and we did not do a full
            // examination, so we need to on the next pass.
            examineAll = true;
        }
//System.out.println("    Change count: " + this.changeCount);

        // Keep going if there were changes or if we still need to do a full
        // examination of the data.
        return this.changeCount > 0 || this.examineAll;
    }

    @Override
    protected void cleanupAlgorithm()
    {
        this.dataList = null;
        this.supportsMap = null;
        this.nonBoundAlphaIndices = null;
        this.errorCache = null;
        this.kernelCache = null;
    }

    /**
     * Examines one example in order to try and take a step of SMO using it.
     *
     * @param   j
     *      The index of the example to examine.
     * @return
     *      1 if the example created an update. Otherwise, 0.
     */
    protected int examineExample(
        final int j)
    {
        // Map our parameters to variables whose names are closer to the paper.
        final double c = this.maxPenalty;
        final double tolerance = this.effectiveZero;
        final double yJ = this.getTarget(j);
        final double alphaJ = this.getAlpha(j);
        final double eJ = this.getError(j);
        final double rJ = eJ * yJ;

        if (   (rJ < -tolerance && alphaJ < c)
            || (rJ >  tolerance && alphaJ > 0))
        {

            final int nonBoundAlphasCount = this.nonBoundAlphaIndices.size();

            // Platt: if ( number of non-zero & non-c alpha > 1)
            // Platt:   i1 = result of second choice heuristic
            // Platt:   if takeStep(i1, i2)
            // Platt:     return 1
            if (nonBoundAlphasCount > 1)
            {
                // The "second choice heuristic" is to get the point that either
                // has the highest or lowest error that is not at the bound
                // (0 or C).
                final int i;
                if (eJ > 0)
                {
                    i = this.getMinErrorIndex();
                }
                else
                {
                    i = this.getMaxErrorIndex();
                }

                if (this.takeStep(i, j))
                {
                    return 1;
                }
            }

            // Platt:
            // loop over all non-zero and non-c alpha, starting at random point
            //   i1 = identity of current alpha
            //   if takeStep(i1, i2)
            //     return 1
            if (nonBoundAlphasCount > 0)
            {
                final int offset = this.random.nextInt(nonBoundAlphasCount);
                final ArrayList<Integer> nonBoundAlphas =
                    new ArrayList<Integer>(this.nonBoundAlphaIndices);
                for (int n = 0; n < nonBoundAlphasCount; n++)
                {
                    final int alphaIndex = (offset + n) % nonBoundAlphasCount;
                    final int i = nonBoundAlphas.get(alphaIndex);
                    if (this.takeStep(i, j))
                    {
                        return 1;
                    }
                }
            }

            // Platt:
            // loop over all i, starting at a random point
            //   i = loop variable
            //   if takeStep(i1, i2)
            //     return 1
            final int offset = this.random.nextInt(this.dataSize);
            for (int n = 0; n < this.dataSize; n++)
            {
                final int i = (offset + n) % this.dataSize;
                if (this.takeStep(i, j))
                {
                    return 1;
                }
            }
        }

        // No changes were made for example j.
        return 0;
    }

    /**
     * Takes one step in the SMO algorithm by attemping to update using the
     * two data points.
     *
     * @param   i
     *      The index of the first data point.
     * @param   j
     *      The index of the second data point.
     * @return
     *      True if a step was taken, meaning a modification was made.
     *      Otherwise, false.
     */
    private boolean takeStep(
        final int i,
        final int j)
    {
        if (i == j)
        {
            // This is a sanity check. It cannot take a step if the two
            // examples are exactly the same.
            return false;
        }

        // Map our data into terms similar to what is in the paper.
        final double c = this.maxPenalty;
        final double epsilon = this.effectiveZero;
        final double cMinusEpsilon = c - epsilon;

        final double yI = this.getTarget(i);
        final double eI = this.getError(i);
        final double oldAlphaI = this.getAlpha(i);

        final double yJ = this.getTarget(j);
        final double eJ = this.getError(j);
        final double oldAlphaJ = this.getAlpha(j);

//System.out.println("    i: " + i);
//System.out.println("    yi: " + yi);
//System.out.println("    Ei: " + Ei);
//System.out.println("    alphai: " + alphai);
//System.out.println("    j: " + j);
//System.out.println("    Ej: " + Ej);
//System.out.println("    yj: " + yj);
//System.out.println("    alphaj: " + alphaj);

        // Compute the lower and upper bounds to solve for new values of
        // alphaI and alphaJ.
        final double lowerBound;
        final double upperBound;

        if (yI != yJ)
        {
            // Points i and j belong to different categories.
            final double alphaJMinusAlphaI = oldAlphaJ - oldAlphaI;
            lowerBound = Math.max(0, alphaJMinusAlphaI);
            upperBound = Math.min(c, alphaJMinusAlphaI + c);
        }
        else
        {
            // Points i and j belong to the same category.
            final double alphaIPlusAlphaJ = oldAlphaI + oldAlphaJ;
            lowerBound = Math.max(0, alphaIPlusAlphaJ - c);
            upperBound = Math.min(c, alphaIPlusAlphaJ);
        }

        if (lowerBound >= upperBound)
        {
            // No update can be made by evaluting these two points.
            return false;
        }

        // Evaluate the kernels between the values, using the property that by
        // kernel symmetry: k(i,j) == k(j,i)
        final double kII = this.evaluateKernel(i, i);
        final double kIJ = this.evaluateKernel(i, j);
        final double kJI = kIJ;
        final double kJJ = this.evaluateKernel(j, j);

        final double eta = kIJ + kJI - kII - kJJ;
//System.out.println("    eta: " + eta);

// TODO: Handle case where eta == 0.0.
        if (eta >= 0.0)
        {
            return false;
        }

        // Compute the new alpha value for j.
        double newAlphaJ = oldAlphaJ - (yJ * (eI - eJ)) / eta;
        if (newAlphaJ <= lowerBound)
        {
            newAlphaJ = lowerBound;
        }
        else if (newAlphaJ >= upperBound)
        {
            newAlphaJ = upperBound;
        }

        // If the new alpha is close enough to 0.0 or the maximum alpha, just
        // set it to that value.
        if (newAlphaJ < epsilon)
        {
            newAlphaJ = 0.0;
        }
        else if (newAlphaJ > cMinusEpsilon)
        {
            newAlphaJ = c;
        }

//System.out.println("    alphajnew: " + alphaj);
        if (Math.abs(newAlphaJ - oldAlphaJ) < epsilon)
        {
            // The change in alpha is below numerical precision, so don't change
            // it.
            return false;
        }

        // Now compute the new value alpha value for i based on the new alpha
        // value for j.
        double newAlphaI = oldAlphaI + yI * yJ * (oldAlphaJ - newAlphaJ);

        // If the new alpha is close enough to 0.0 or the maximum alpha, just
        // set it to that value.
        if (newAlphaI < epsilon)
        {
            newAlphaI = 0.0;
        }
        else if (newAlphaI > cMinusEpsilon)
        {
            newAlphaI = c;
        }

        // Next compute the new bias term.
        final double oldBias = this.getBias();
        final double b1 = oldBias - eI
            - yI * (newAlphaI - oldAlphaI) * kII
            - yJ * (newAlphaJ - oldAlphaJ) * kIJ;
        final double b2 = oldBias - eJ
            - yI * (newAlphaI - oldAlphaI) * kJI
            - yJ * (newAlphaJ - oldAlphaJ) * kJJ;

        final double newBias;
        if (newAlphaI > epsilon && newAlphaI < cMinusEpsilon)
        {
            newBias = b1;
        }
        else if (newAlphaJ > epsilon && newAlphaJ < cMinusEpsilon)
        {
            newBias = b2;
        }
        else
        {
            newBias = (b1 + b2) / 2.0;
        }
//System.out.println("    alphai: " + alphai);
//System.out.println("    alphaj: " + alphaj);
//System.out.println("    b: " + b);
        this.setAlpha(i, newAlphaI);
        this.setAlpha(j, newAlphaJ);
        this.setBias(newBias);

        // Update the error cache.
        this.updateErrorCache(
            i, yI, oldAlphaI, newAlphaI,
            j, yJ, oldAlphaJ, newAlphaJ,
            oldBias, newBias);

        return true;
    }


    /**
     * Updates the error cache
     * @param i
     * @param yI
     * @param oldAlphaI
     * @param newAlphaI
     * @param j
     * @param yJ
     * @param oldAlphaJ
     * @param newAlphaJ
     * @param oldBias
     * @param newBias
     */
    private void updateErrorCache(
        final int i,
        final double yI,
        final double oldAlphaI,
        final double newAlphaI,
        final int j,
        final double yJ,
        final double oldAlphaJ,
        final double newAlphaJ,
        final double oldBias,
        final double newBias)
    {
        if (newAlphaI <= 0.0 || newAlphaI >= this.maxPenalty)
        {
            // Point i is no longer a non-bound error.
//            this.errorCache.remove(i);
        }

        if (newAlphaJ <= 0.0 || newAlphaJ >= this.maxPenalty)
        {
            // Point j is no longer a non-bound error.
//            this.errorCache.remove(j);
        }

        // Compute how much each weight ended up changing, pus how much
        // the bias changed.
        final double weightIChange = yI * (newAlphaI - oldAlphaI);
        final double weightJChange = yJ * (newAlphaJ - oldAlphaJ);
        final double biasChange = newBias - oldBias;

        // Update the error value for all the non-bound indices.
        for (Integer k : this.nonBoundAlphaIndices)
        {
            // Get the old error.
            final MutableDouble oldError = this.errorCache.get(k);

            // Compute the new error.
            if (k == i || k == j)
            {
                // Points i and j are no longer errors.
                if( oldError != null )
                {
                    oldError.value = 0.0;
                }
                else
                {
                    this.errorCache.put(k, new MutableDouble( 0.0 ));
                }
            }
            else if (oldError == null)
            {
                // Point k was not in the cache, so compute its error.
                final double newError = this.getSVMOutput(k) - this.getTarget(k);
                this.errorCache.put(k, new MutableDouble(newError) );
            }
            else
            {
                // Update the cached error value for point k based on how much
                // the weight changed.
                oldError.value += weightIChange * this.evaluateKernel(i, k)
                    + weightJChange * this.evaluateKernel(j, k)
                    + biasChange;
            }

        }
    }

    /**
     * Evaluate the kernel between the two given data points: K(i, j).
     *
     * @param   i
     *      The training example index of the first data point.
     *      Must be between 0 and dataSize - 1.
     * @param   j
     *      The training example index of the first data point.
     *      Must be between 0 and dataSize - 1.
     * @return
     *      The result of evaluating the kernel between the data points i and j.
     */
    private double evaluateKernel(
        final int i,
        final int j)
    {
        if (this.kernelCache == null)
        {
            // No kernel cache, so just evaluate the kernel directly.
            return this.kernel.evaluate(this.getPoint(i), this.getPoint(j));
        }
        else
        {
            // First check the kernel cache.
            // The cache is accessed by putting the two 32-bit integer indices
            // into one 64-bit long. The smaller part is up-shifted first, since
            // by kernel symmetry K(i, j) = K(j, i). Thus, we only store the
            // kernel value for (i, j).
            final long kernelCacheKey;
            if (i <= j )
            {
                kernelCacheKey =  ((long) i << 32) | (long) j;
            }
            else
            {
                kernelCacheKey =  ((long) j << 32) | (long) i;
            }
            final Double cachedValue = this.kernelCache.get(kernelCacheKey);

            if (cachedValue != null)
            {
                // The value was in the cache, so return it.
                return cachedValue;
            }

            // The value was not in the cache, so compute it using the kernel.
            final double value =
                this.kernel.evaluate(this.getPoint(i), this.getPoint(j));

            // Add the value to the cache.
            this.kernelCache.put(kernelCacheKey, value);

            // Return the value.
            return value;
        }
    }

    /**
     * Computes the output of the SVM for the given example index.
     *
     * @param   i
     *      The training example index. Must be between 0 and dataSize - 1.
     * @return
     *      The output of the SVM for that example.
     */
    private double getSVMOutput(
        final int i)
    {
        double retval = this.result.getBias();
        for (Map.Entry<Integer, DefaultWeightedValue<InputType>> entry
            : this.supportsMap.entrySet())
        {
            retval += entry.getValue().getWeight()
                * this.evaluateKernel(i, entry.getKey());
        }
        return retval;
    }

    /**
     * Computes the error between the output of the SVM and the target value
     * for a given example index.
     *
     * @param   i
     *      The training example index. Must be between 0 and dataSize - 1.
     * @return
     *      The error of the SVM for that example.
     */
    private double getError(
        final int i)
    {
        // First check for the error in the error cache.
        final MutableDouble cachedError = this.errorCache.get(i);
        if (cachedError != null)
        {
            return cachedError.value;
        }
        else
        {
            // Compute the error between the output of the SVM and the target
            // for the example.
            final double error = this.getSVMOutput(i) - this.getTarget(i);

            // We don't update the error cache here, since it is updated by
            // another function.

            return error;
        }
    }

    /**
     * Finds the index of the non-bound example with the minimum error, if one
     * exists. Otherwise, -1 is returned.
     *
     * @return
     *      The (first) index of the non-bound example with the minimum error.
     */
    private int getMinErrorIndex()
    {
        // Loop through all the non-bound indices to find the one with minimum
        // error.
        double minError = Double.POSITIVE_INFINITY;
        int minIndex = -1;
        for (Integer index : this.nonBoundAlphaIndices)
        {
            final double error = this.getError(index);
            if (error < minError)
            {
                minError = error;
                minIndex = index;
            }
        }
        return minIndex;
    }

    /**
     * Finds the index of non-bound the example with the maximum error, if one
     * exists. Otherwise, -1 is returned.
     *
     * @return
     *      The (first) index of the non-bound example with the maximum error.
     */
    private int getMaxErrorIndex()
    {
        // Loop through all the non-bound indices to find the one with
        // maximum error
        double maxError = Double.NEGATIVE_INFINITY;
        int maxIndex = -1;
        for (Integer index : this.nonBoundAlphaIndices)
        {
            final double error = this.getError(index);
            if (error > maxError)
            {
                maxError = error;
                maxIndex = index;
            }
        }
        return maxIndex;
    }

    /**
     * Gets the input value of the given training example index.
     *
     * @param   i
     *      The training example index. Must be between 0 and dataSize - 1.
     * @return
     *      The input value for that index.
     */
    private InputType getPoint(
        final int i)
    {
        return this.dataList.get(i).getInput();
    }

    /**
     * Gets the target value of the given training example index, as a double.
     *
     * @param   i
     *      The training example index. Must be between 0 and dataSize - 1.
     * @return
     *      The target value for that index represented as a double, which is
     *      either +1.0 or -1.0.
     */
    private double getTarget(
        final int i)
    {
        return this.dataList.get(i).getOutput() ? +1.0 : -1.0;
    }

    /**
     * Gets the alpha (weight) value for the given training example index. Note
     * that alpha values are stored as weights that incorporate the label as the
     * sign of the weight. That is, weight = y * alpha where y is either +1 or
     * -1.
     *
     * @param   i
     *      The training example index. Must be between 0 and dataSize - 1.
     * @return
     *      The current alpha value for i.
     */
    private double getAlpha(
        final int i)
    {
        final DefaultWeightedValue<InputType> support = this.supportsMap.get(i);
        if (support == null)
        {
            // Not a support, so the alpha value is zero.
            return 0.0;
        }
        else
        {
            // The weight is the label (+1 or -1) times alpha. Alpha is always
            // greater than zero, so we just take the absolute value of the
            // weight to get it.
            return Math.abs(support.getWeight());
        }
    }

    /**
     * Sets the alpha (weight) value for the given training example index. Note
     * that alpha values are stored as weights that incorporate the label as the
     * sign of the weight. That is, weight = y * alpha where y is either +1 or
     * -1.
     *
     * @param   i
     *      The training example index. Must be between 0 and dataSize - 1.
     * @param   alpha
     *      The current alpha value for example i.
     */
    private void setAlpha(
        final int i,
        final double alpha)
    {
        if (alpha == 0.0)
        {
            // No longer a support, so remove it from the appropriate data
            // structures.
            this.supportsMap.remove(i);
            this.nonBoundAlphaIndices.remove(i);
        }
        else
        {
            // The weight is the label times alpha.
            final double weight = this.getTarget(i) * alpha;

            // Get the support data for the example.
            DefaultWeightedValue<InputType> support = this.supportsMap.get(i);
            if (support == null)
            {
                // No support data yet, so add one.
                support = new DefaultWeightedValue<InputType>(
                    this.getPoint(i), weight);
                supportsMap.put(i, support);
            }
            else
            {
                // Change the weight of the support.
                support.setWeight(weight);
            }

            // Update whether or not the example is at the bound or not.
            if (alpha == this.maxPenalty)
            {
                // It is at the bound, so remove it from the list of bound
                // indices.
                this.nonBoundAlphaIndices.remove(i);
            }
            else
            {
                // It is not at the bound, so make sure it is in the list of
                // bound indices.
                this.nonBoundAlphaIndices.add(i);
            }
        }
    }

    /**
     * Gets the bias term for the support vector machine. Note that our bias is
     * implemented as a positive term added, not a negative like in  original
     * paper. Typically represented as "b".
     *
     * @return
     *      The bias term.
     */
    private double getBias()
    {
        return this.result.getBias();
    }

    /**
     * Sets the bias term for the support vector machine. Note that our bias is
     * implemented as a positive term added, not a negative like in  original
     * paper. Typically represented as "b".
     *
     * @param   bias
     *      The bias term.
     */
    private void setBias(
        final double bias)
    {
        this.result.setBias(bias);
    }

    @Override
    public KernelBinaryCategorizer<InputType, DefaultWeightedValue<InputType>> getResult()
    {
        return this.result;
    }

    @Override
    public Kernel<? super InputType> getKernel()
    {
        return this.kernel;
    }

    /**
     * Sets the kernel to use in training the SVM.
     *
     * @param   kernel
     *      The kernel to use in training the SVM.
     */
    public void setKernel(
        final Kernel<? super InputType> kernel)
    {
        this.kernel = kernel;
    }

    /**
     * Gets the maximum penalty parameter for the algorithm, which is also known
     * as C in the paper and in other related literature.
     *
     * @return
     *      The maximum penalty parameter (C). Must be greater than 0.0.
     */
    public double getMaxPenalty()
    {
        return this.maxPenalty;
    }

    /**
     * Sets the maximum penalty parameter for the algorithm, which is also known
     * as C in the paper and in other related literature.
     *
     * @param   maxPenalty
     *      The maximum penalty parameter (C). Must be greater than 0.0.
     */
    public void setMaxPenalty(
        final double maxPenalty)
    {
        if (maxPenalty <= 0.0)
        {
            throw new IllegalArgumentException("maxPenalty must be positive.");
        }

        this.maxPenalty = maxPenalty;
    }

    /**
     * Gets the error tolerance for the algorithm. This is how close the output
     * of the SVM must be to the target values of +1.0 or -1.0. In the original
     * paper, this is the "tolerance" or "tol" term. Typically close to zero,
     * but larger than the effective zero.
     *
     * @return
     *      The error tolerance for the algorithm. Must be non-negative.
     */
    public double getErrorTolerance()
    {
        return errorTolerance;
    }

    /**
     * Sets the error tolerance for the algorithm. This is how close the output
     * of the SVM must be to the target values of +1.0 or -1.0. In the original
     * paper, this is the "tolerance" or "tol" term. Typically close to zero,
     * but larger than the effective zero.
     *
     * @param   errorTolerance
     *      The error tolerance for the algorithm. Must be non-negative.
     */
    public void setErrorTolerance(
        final double errorTolerance)
    {
        if (errorTolerance < 0.0)
        {
            throw new IllegalArgumentException(
                "errorTolerance cannot be negative.");
        }

        this.errorTolerance = errorTolerance;
    }

    /**
     * Gets the effective value for zero to use in the computation to deal with
     * numerical imprecision. It is also known as "epsilon" or "eps" in the
     * original paper. Must be a non-negative number. Typically a very small
     * value.
     *
     * @return
     *      The effective value for zero. Must be non-negative.
     */
    public double getEffectiveZero()
    {
        return this.effectiveZero;
    }

    /**
     * Sets the effective value for zero to use in the computation to deal with
     * numerical imprecision. It is also known as "epsilon" or "eps" in the
     * original paper. Must be a non-negative number. Typically a very small
     * value.
     *
     * @param   effectiveZero
     *      The effective value for zero. Must be non-negative.
     */
    public void setEffectiveZero(
        final double effectiveZero)
    {
        if (effectiveZero < 0.0)
        {
            throw new IllegalArgumentException(
                "effectiveZero cannot be negative.");
        }

        this.effectiveZero = effectiveZero;
    }

    /**
     * Gets the size of the kernel cache or 0 if no kernel cache is to be used.
     *
     * @return
     *      The size of the kernel cache. Must be non-negative.
     */
    public int getKernelCacheSize()
    {
        return kernelCacheSize;
    }

    /**
     * Sets the size of the kernel cache or 0 if no kernel cache is to be used.
     *
     * @param   kernelCacheSize
     *      The size of the kernel cache. Must be non-negative.
     */
    public void setKernelCacheSize(
        final int kernelCacheSize)
    {
        if (kernelCacheSize < 0)
        {
            throw new IllegalArgumentException(
                "kernelCacheSize cannot be negative");
        }

        this.kernelCacheSize = kernelCacheSize;
    }

    @Override
    public Random getRandom()
    {
        return this.random;
    }

    @Override
    public void setRandom(
        final Random random)
    {
        this.random = random;
    }

    /**
     * Gets the number of changes made on the last iteration of the algorithm.
     *
     * @return
     *      The number of changes made on the last iteration of the algorithm.
     */
    public int getChangeCount()
    {
        return this.changeCount;
    }

    @Override
    public DefaultNamedValue<Integer> getPerformance()
    {
        return DefaultNamedValue.create(PERFORMANCE_NAME, this.getChangeCount());
    }

}
