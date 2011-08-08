/*
 * File:                SimplifiedSequentialMinimalOptimization.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry Learning Core
 *
 * Copyright July 19, 2010, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government. Export
 * of this program may require a license from the United States Government.
 */

package gov.sandia.cognition.learning.algorithm.svm;

import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.learning.algorithm.AbstractAnytimeSupervisedBatchLearner;
import gov.sandia.cognition.learning.data.InputOutputPair;
import gov.sandia.cognition.learning.function.categorization.KernelBinaryCategorizer;
import gov.sandia.cognition.learning.function.kernel.Kernel;
import gov.sandia.cognition.learning.function.kernel.KernelContainer;
import gov.sandia.cognition.util.DefaultWeightedValue;
import gov.sandia.cognition.util.Randomized;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Random;

/**
 * This is a simplified version of the Sequential Minimization Algorithm (SMO)
 * that was used as a stepping-stone in the full SMO implementation.
 *
 * @author  Justin Basilico
 * @since   3.1
 * @see     SequentialMinimalOptimization
 */
@PublicationReference(
    title="The Simplified SMO Algorithm",
    author="Andrew Ng",
    year=2009,
    type=PublicationType.WebPage,
    url="http://www.stanford.edu/class/cs229/materials/smo.pdf")
public class SimplifiedSequentialMinimalOptimization<InputType>
    extends AbstractAnytimeSupervisedBatchLearner<InputType, Boolean, KernelBinaryCategorizer<InputType>>
    implements KernelContainer<InputType>, Randomized
{
    public static final int DEFAULT_MAX_ITERATIONS = 1000;
    public static final int DEFAULT_MAX_STEPS_WITHOUT_CHANGE = 10;

    /** The default maximum penalty is infinite, which means that it is
     *  hard-assignment. */
    public static final double DEFAULT_MAX_PENALTY = Double.POSITIVE_INFINITY;

    /** The default error tolerance is 0.001, which is what was recommended in
     *  the original Sequential Minimal Optimization paper. */
    public static final double DEFAULT_ERROR_TOLERANCE = 0.001;

    /** The default effective value for zero is {@value}. */
    public static final double DEFAULT_EFFECTIVE_ZERO = 1.0e-10;

    /** The kernel to use. */
    private Kernel<? super InputType> kernel;

    private double maxPenalty;

    private double errorTolerance;

    private int maxStepsWithoutChange;

    private double effectiveZero;

    private Random random;

    /** The result categorizer. */
    private transient KernelBinaryCategorizer<InputType> result;

    private transient ArrayList<InputOutputPair<? extends InputType, Boolean>> dataList;

    private transient int dataSize;

    /** The number of items changed on the most recent iteration. */
    private transient int changeCount;

    private transient int stepsWithoutChange;

    /** The mapping of weight objects to non-zero weighted examples
     *  (support vectors). */
    private transient LinkedHashMap<Integer, DefaultWeightedValue<InputType>> supportsMap;

    public SimplifiedSequentialMinimalOptimization()
    {
        this(null, DEFAULT_MAX_PENALTY, DEFAULT_ERROR_TOLERANCE,
            DEFAULT_MAX_STEPS_WITHOUT_CHANGE, DEFAULT_EFFECTIVE_ZERO,
            DEFAULT_MAX_ITERATIONS, new Random());
    }

    public SimplifiedSequentialMinimalOptimization(
        Kernel<? super InputType> kernel,
        final double maxPenalty,
        double errorTolerance,
        int maxStepsWithoutChange,
        double effectiveZero,
        final int maxIterations,
        Random random)
    {
        super(maxIterations);

        this.setKernel(kernel);
        this.setMaxPenalty(maxPenalty);
        this.setErrorTolerance(errorTolerance);
        this.setMaxStepsWithoutChange(maxStepsWithoutChange);
        this.setEffectiveZero(effectiveZero);
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

        this.dataList = new ArrayList<InputOutputPair<? extends InputType, Boolean>>(
            this.getData().size());

        int positives = 0;
        for (InputOutputPair<? extends InputType, Boolean> example : this.getData())
        {
            if (example != null && example.getInput() != null && example.getOutput() != null)
            {
                this.dataList.add(example);

                if (example.getOutput())
                {
                    positives++;
                }
            }
        }
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

        this.changeCount = this.getData().size();
        this.stepsWithoutChange = 0;
        this.supportsMap = new LinkedHashMap<Integer, DefaultWeightedValue<InputType>>();

        // initialize alpha array to all zero
        // initialize threshold to zero
        this.result = new KernelBinaryCategorizer<InputType>(
            this.kernel, this.supportsMap.values(), 0.0);
        return true;
    }

    @Override
    protected boolean step()
    {
        System.out.println("Iteration: " + this.getIteration());
        final double tol = this.errorTolerance;
        final double C = this.maxPenalty;

        this.changeCount = 0;

        for (int i = 0; i < this.dataSize; i++)
        {
//System.out.println();
//System.out.println("    i: " + i);
            final double yI = this.getTarget(i);
            final double eI = this.getSVMOutput(i) - yI;
            double alphaI = this.getAlpha(i);
//System.out.println("    yi: " + yi);
//System.out.println("    Ei: " + Ei);
//System.out.println("    alphai: " + alphai);

            final double yITimesEI = yI * eI;

            if (    ((yITimesEI < -tol) && (alphaI < C))
                 || ((yITimesEI > +tol) && (alphaI > 0)))
            {
                // Select a random j != i
                int j = this.random.nextInt(this.dataSize - 1);
                if (j >= i)
                {
                    j += 1;
                }
//for (int j = 0; j < dataSize; j++)
//{
//    if (i == j) continue;

                if (this.takeStep(i, j))
                {
                    changeCount++;
                }
            }
//}
        }
/*
System.out.println("Change count: " + changeCount);

System.out.println("Result " + result);
for (WeightedValue<?> support : result.getExamples())
{
    System.out.println("    " + support.getWeight() + " " + support.getValue());
}
System.out.println("Bias: " + result.getBias());
 */
        if (this.changeCount <= 0)
        {
            this.stepsWithoutChange++;
        }
        else
        {
            this.stepsWithoutChange = 0;
        }

        return this.stepsWithoutChange < this.maxStepsWithoutChange;
    }

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

        final double C = this.maxPenalty;
        final double epsilon = this.effectiveZero;
        final double CMinusEpsilon = C - epsilon;

        final double yI = this.getTarget(i);
        final double eI = this.getSVMOutput(i) - yI;
        final double oldAlphaI = this.getAlpha(i);
//        double alphaI = this.getAlpha(i);

        final double yJ = this.getTarget(j);
        final double eJ = this.getSVMOutput(j) - yJ;
        final double oldAlphaJ = this.getAlpha(j);
//        double alphaJ = this.getAlpha(j);

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
            final double alphaJMinusAlphaI = oldAlphaJ - oldAlphaI;
            lowerBound = Math.max(0, alphaJMinusAlphaI);
            upperBound = Math.min(C, alphaJMinusAlphaI + C);
        }
        else
        {
            final double alphaIPlusAlphaJ = oldAlphaI + oldAlphaJ;
            lowerBound = Math.max(0, alphaIPlusAlphaJ - C);
            upperBound = Math.min(C, alphaIPlusAlphaJ);
        }
//System.out.println("    L: " + L);
//System.out.println("    H: " + H);
        if (lowerBound >= upperBound)
        {
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
        if (eta >= 0.0)
        {
            return false;
        }

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
        else if (newAlphaJ > CMinusEpsilon)
        {
            newAlphaJ = C;
        }

//System.out.println("    alphajnew: " + alphaj);
        if (Math.abs(newAlphaJ - oldAlphaJ) < epsilon)
        {
            return false;
        }

        double newAlphaI = oldAlphaI + yI * yJ * (oldAlphaJ - newAlphaJ);

        // If the new alpha is close enough to 0.0 or the maximum alpha, just
        // set it to that value.
        if (newAlphaI < epsilon)
        {
            newAlphaI = 0.0;
        }
        else if (newAlphaI > CMinusEpsilon)
        {
            newAlphaI = C;
        }

        final double oldBias = this.getBias();
        final double b1 = oldBias - eI
            - yI * (newAlphaI - oldAlphaI) * kII
            - yJ * (newAlphaJ - oldAlphaJ) * kIJ;
        final double b2 = oldBias - eJ
            - yI * (newAlphaI - oldAlphaI) * kJI
            - yJ * (newAlphaJ - oldAlphaJ) * kJJ;

        final double newBias;
        if (newAlphaI > epsilon && newAlphaI < CMinusEpsilon)
        {
            newBias = b1;
        }
        else if (newAlphaJ > epsilon && newAlphaJ < CMinusEpsilon)
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

        return true;
    }

    @Override
    protected void cleanupAlgorithm()
    {
        this.dataList = null;
        this.supportsMap = null;
    }

    private double evaluateKernel(
        final int i,
        final int j)
    {
        return this.kernel.evaluate(this.getPoint(i), this.getPoint(j));
    }

    private double getSVMOutput(
        final InputType input)
    {
        return this.result.evaluateAsDouble(input);
    }

    private double getSVMOutput(
        final int i)
    {
        return this.getSVMOutput(this.getPoint(i));
    }

    private InputType getPoint(
        final int i)
    {
        return this.dataList.get(i).getInput();
    }

    private double getTarget(
        final int i)
    {
        return this.dataList.get(i).getOutput() ? +1.0 : -1.0;
    }

    private double getAlpha(
        final int i)
    {
        final DefaultWeightedValue<InputType> support = this.supportsMap.get(i);
        if (support == null)
        {
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

    private void setAlpha(
        int i,
        double alpha)
    {
        if (alpha == 0.0)
        {
            this.supportsMap.remove(i);
        }
        else
        {
            // The weight is the label times alpha.
            final double weight = this.getTarget(i) * alpha;
            DefaultWeightedValue<InputType> support = this.supportsMap.get(i);
            if (support == null)
            {
                support = new DefaultWeightedValue<InputType>(
                    this.getPoint(i), weight);
                supportsMap.put(i, support);
            }
            else
            {
                support.setWeight(weight);
            }
        }
    }

    private double getBias()
    {
        return this.result.getBias();
    }

    private void setBias(
        final double b)
    {
        this.result.setBias(b);
    }

    public KernelBinaryCategorizer<InputType> getResult()
    {
        return this.result;
    }

    public Kernel<? super InputType> getKernel()
    {
        return kernel;
    }

    public void setKernel(
        final Kernel<? super InputType> kernel)
    {
        this.kernel = kernel;
    }

    public double getMaxPenalty()
    {
        return maxPenalty;
    }

    public void setMaxPenalty(
        final double maxPenalty)
    {
        if (maxPenalty <= 0.0)
        {
            throw new IllegalArgumentException("maxPenalty must be positive.");
        }

        this.maxPenalty = maxPenalty;
    }

    public double getErrorTolerance()
    {
        return errorTolerance;
    }

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

    public int getMaxStepsWithoutChange()
    {
        return maxStepsWithoutChange;
    }

    public void setMaxStepsWithoutChange(
        final int maxStepsWithoutChange)
    {
        if (maxStepsWithoutChange <= 0)
        {
            throw new IllegalArgumentException(
                "maxStepsWithoutChange must be positive");
        }
        this.maxStepsWithoutChange = maxStepsWithoutChange;
    }

    public double getEffectiveZero()
    {
        return this.effectiveZero;
    }

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

    public Random getRandom()
    {
        return this.random;
    }

    public void setRandom(
        final Random random)
    {
        this.random = random;
    }


}
