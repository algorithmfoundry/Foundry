/*
 * File:            FactorizationMachineAlternatingLeastSquares.java
 * Authors:         Justin Basilico
 * Project:         Cognitive Foundry
 * 
 * Copyright 2015 Cognitive Foundry. All rights reserved.
 */

package gov.sandia.cognition.learning.algorithm.factor.machine;

import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationReferences;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.collection.CollectionUtil;
import gov.sandia.cognition.learning.data.InputOutputPair;
import gov.sandia.cognition.math.matrix.Matrix;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorEntry;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.util.ArgumentChecker;
import gov.sandia.cognition.util.DefaultNamedValue;
import gov.sandia.cognition.util.NamedValue;
import java.util.ArrayList;
import java.util.Random;

/**
 * Implements an Alternating Least Squares (ALS) algorithm for learning a 
 * Factorization Machine.
 * 
 * @author  Justin Basilico
 * @since   3.4.1
 * @see     FactorizationMachine
 */
@PublicationReferences(references={
    @PublicationReference(
        title="Fast Context-aware Recommendations with Factorization Machines",
        author={"Steffen Rendle", "Zeno Gantner", "Christoph Freudenthaler", "Lars Schmidt-Thieme"},
        year=2011,
        type=PublicationType.Conference,
        publication="Proceeding of the 34th international ACM SIGIR conference on Research and development in Information Retrieval (SIGIR)",
        url="http://www.inf.uni-konstanz.de/~rendle/pdf/Rendle2011-CARS.pdf"),
    @PublicationReference(
        title="Factorization Machines with libFM",
        author="Steffen Rendle",
        year=2012,
        type=PublicationType.Journal,
        publication="ACM Transactions on Intelligent Systems Technology",
        url="http://www.csie.ntu.edu.tw/~b97053/paper/Factorization%20Machines%20with%20libFM.pdf",
        notes="Algorithm 2: Alternating Least Squares (ALS)")
})
public class FactorizationMachineAlternatingLeastSquares
    extends AbstractFactorizationMachineLearner
{

    /** The default minimum change is {@value}. */
    public static final double DEFAULT_MIN_CHANGE = 0.00001;
    
    /** The minimum change allowed in an iteration. The algorithm stops if the
     *  change is less than this value. Cannot be negative. */
    protected double minChange;
    
    /** The size of the data. */
    protected transient int dataSize;
    
    /** The data in the form that it can be accessed in O(1) as a list. */
    protected transient ArrayList<? extends InputOutputPair<? extends Vector, Double>> dataList;
    
    /** A list representing a transposed form of the matrix of inputs. It is a
     *  d by n sparse matrix stored as an array list of sparse vectors. This is
     *  used to speed up the computation of the per-coordinate updates. */
    protected transient ArrayList<Vector> inputsTransposed;
    
    /** The total change from the current iteration. */
    protected double totalChange;
    
    /** The total error from the current iteration. */
    protected double totalError;
    
    /**
     * Creates a new {@link FactorizationMachineAlternatingLeastSquares} with
     * default parameter values.
     */
    public FactorizationMachineAlternatingLeastSquares()
    {
        this(DEFAULT_FACTOR_COUNT, DEFAULT_BIAS_REGULARIZATION,
            DEFAULT_WEIGHT_REGULARIZATION, DEFAULT_FACTOR_REGULARIZATION,
            DEFAULT_SEED_SCALE, DEFAULT_MAX_ITERATIONS, 
            DEFAULT_MIN_CHANGE, new Random());
    }

    /**
     * Creates a new {@link FactorizationMachineAlternatingLeastSquares}.
     * 
     * @param   factorCount
     *      The number of factors to use. Zero means no factors. Cannot be
     *      negative.
     * @param   biasRegularization
     *      The regularization term for the bias. Cannot be negative.
     * @param   weightRegularization
     *      The regularization term for the linear weights. Cannot be negative.
     * @param   factorRegularization
     *      The regularization term for the factor matrix. Cannot be negative.
     * @param   seedScale
     *      The random initialization scale for the factors.
     *      Multiplied by a random Gaussian to initialize each factor value.
     *      Cannot be negative.
     * @param   maxIterations
     *      The maximum number of iterations for the algorithm to run. Cannot
     *      be negative.
     * @param   minChange
     *      The minimum change allowed in an iteration. The algorithm stops if 
     *      the change is less than this value. Cannot be negative.
     * @param   random 
     *      The random number generator.
     */
    public FactorizationMachineAlternatingLeastSquares(
        final int factorCount,
        final double biasRegularization,
        final double weightRegularization,
        final double factorRegularization,
        final double seedScale,
        final int maxIterations,
        final double minChange,
        final Random random)
    {
        super(factorCount, biasRegularization, weightRegularization,
            factorRegularization, seedScale, maxIterations, random);
        
        this.setMinChange(minChange);
    }
    
    @Override
    protected boolean initializeAlgorithm()
    {
        if (!super.initializeAlgorithm())
        {
            return false;
        }
        this.dataSize = this.data.size();
        if (this.dataSize <= 0)
        {
            return false;
        }
        
        // Convert the input data to an array list.
        this.dataList = CollectionUtil.asArrayList(this.data);
        
        // Create a way to store the transposed input data in sparse vectors.
        final VectorFactory<?> sparseFactory = VectorFactory.getSparseDefault();
        this.inputsTransposed = new ArrayList<>(this.dimensionality);
        for (int i = 0; i < this.dimensionality; i++)
        {
            this.inputsTransposed.add(sparseFactory.createVector(this.dataSize));
        }
        
        // Fill in the transposed data.
        for (int i = 0; i < this.dataSize; i++)
        {
            final InputOutputPair<? extends Vector, ?> example = 
                this.dataList.get(i);
            for (final VectorEntry entry : example.getInput())
            {
                if (entry.getValue() != 0.0)
                {
                    this.inputsTransposed.get(entry.getIndex()).set(i,
                        entry.getValue());
                }
            }
        }
        
        return true;
    }

    @Override
    protected boolean step()
    {
        this.totalChange = 0.0;
        
// TODO: Should errors just be computed once and then updated?
        final Vector errors = VectorFactory.getDenseDefault().createVector(
            this.dataSize);
        
        // Compute the initial prediction and error terms per input.
        for (int i = 0; i < this.dataSize; i++)
        {
            final InputOutputPair<? extends Vector, Double> example = 
                this.dataList.get(i);
            final double prediction = this.result.evaluateAsDouble(
                example.getInput());
            final double actual = example.getOutput();
            final double error = actual - prediction;
            errors.set(i, error);
        }

        // Update the bias.
        if (this.isBiasEnabled())
        {
            final double oldBias = this.result.getBias();
            final double newBias = (oldBias * this.dataSize + errors.sum()) 
                / (this.dataSize + this.biasRegularization);
            this.result.setBias(newBias);
            
            // Update the running errors.
            final double biasChange = oldBias - newBias;
            for (int i = 0; i < this.dataSize; i++)
            {
                errors.increment(i, biasChange);
            }
            this.totalChange += Math.abs(biasChange);
        }
        
        // Update the weights.
        if (this.isWeightsEnabled())
        {
            final Vector weights = this.result.getWeights();
            for (int j = 0; j < this.dimensionality; j++)
            {
                final double oldWeight = weights.getElement(j);
                final Vector inputs = this.inputsTransposed.get(j);
// TODO: This could be cached and computed once.
                final Vector derivative = inputs;
                final double sumOfSquares = derivative.norm2Squared();
                final double newWeight = sumOfSquares == 0.0 ? 0.0 :
                    (oldWeight * sumOfSquares + derivative.dot(errors))
                    / (sumOfSquares + this.weightRegularization);
                weights.set(j, newWeight);

                // Update the running errors.
                final double weightChange = oldWeight - newWeight;
                errors.scaledPlusEquals(weightChange, inputs);
                this.totalChange += Math.abs(weightChange);
            }
            this.result.setWeights(weights);
        }
        
        // Update the factors.
        if (this.isFactorsEnabled())
        {
            final Matrix factors = result.getFactors();
            for (int k = 0; k < this.factorCount; k++)
            {
                final Vector factorTimesInput = VectorFactory.getDefault().createVector(
                    this.dataSize);

                final Vector factorRow = factors.getRow(k);
                for (int i = 0; i < this.dataSize; i++)
                {
                    factorTimesInput.set(i,
                        this.dataList.get(i).getInput().dot(factorRow));
                }

                for (int j = 0; j < this.dimensionality; j++)
                {

                    final double oldFactor = factors.get(k, j);
                    final Vector inputs = this.inputsTransposed.get(j);
                    final Vector derivative = inputs.dotTimes(factorTimesInput);
// TODO: This inputs^2 could be cached and computed once.
                    derivative.scaledMinusEquals(oldFactor, inputs.dotTimes(inputs));
                    final double sumOfSquares = derivative.norm2Squared();

                    final double newFactor = sumOfSquares == 0.0 ? 0.0 :
                        (oldFactor * sumOfSquares + derivative.dotProduct(errors))
                        / (sumOfSquares + this.factorRegularization);
                    factors.set(k, j, newFactor);

                    // Update the running errors and factor times input.
                    final double factorChange = oldFactor - newFactor;
                    errors.scaledPlusEquals(factorChange, derivative);
                    factorTimesInput.scaledPlusEquals(-factorChange, inputs);

                    this.totalChange += Math.abs(factorChange);
                }
            }
            this.result.setFactors(factors);
        }
        
        this.totalError = errors.norm2Squared();
        return this.totalChange >= this.minChange;
    }

    @Override
    protected void cleanupAlgorithm()
    {
        this.dataList = null;
        this.inputsTransposed = null;
    }

    /** 
     * Gets the total change in the model parameters from the current iteration.
     * 
     * @return 
     *      The total change in model parameters.
     */
    public double getTotalChange()
    {
        return this.totalChange;
    }

    /**
     * Gets the total squared error from the current iteration.
     * 
     * @return 
     *      The total squared error.
     */
    public double getTotalError()
    {
        return this.totalError;
    }
    
    /**
     * Gets the regularization penalty term in the error for the objective.
     * 
     * @return 
     *      The regularization penalty term.
     */
    public double getRegularizationPenalty()
    {
        if (this.result == null)
        {
            return 0.0;
        }
        
        double penalty = this.biasRegularization * this.result.getBias();
        
        if (this.result.hasWeights())
        {
            penalty += this.weightRegularization * this.result.getWeights().norm2Squared();
        }
        
        if (this.result.hasFactors())
        {
            penalty += this.factorRegularization * this.result.getFactors().normFrobeniusSquared();
        }
        
        return penalty;
    }
    
    /**
     * Gets the total objective, which is the squared error plus the 
     * regularization terms.
     * 
     * @return 
     *      The value of the optimization objective.
     */
    public double getObjective()
    {
        return this.getTotalError() / Math.max(1, this.dataSize)
            + 0.5 * this.getRegularizationPenalty();
    }
    
    /**
     * Gets the total objective, which is the squared error plus the 
     * regularization terms.
     * 
     * @return 
     *      The value of the optimization objective.
     */
    public double computeObjective()
    {
        double error = 0.0;
        for (int i = 0; i < this.dataSize; i++)
        {
            final InputOutputPair<? extends Vector, Double> example = 
                this.dataList.get(i);
            final double prediction = this.result.evaluateAsDouble(
                example.getInput());
            final double actual = example.getOutput();
            double difference = actual - prediction;
            error += difference * difference;
        }
        return error / Math.max(1, this.dataSize)
            + 0.5 * this.getRegularizationPenalty();
    }

    @Override
    public NamedValue<? extends Number> getPerformance()
    {
        return DefaultNamedValue.create("objective", this.getObjective());
    }
    
    /**
     * Gets the minimum change allowed in an iteration. The algorithm stops if 
     * the change is less than this value. 
     *
     * @return
     *      The minimum change. Cannot be negative.
     */
    public double getMinChange()
    {
        return this.minChange;
    }

    /**
     * Sets the minimum change allowed in an iteration. The algorithm stops if 
     * the change is less than this value. 
     *
     * @param   minChange
     *      The minimum change. Cannot be negative.
     */
    public void setMinChange(
        final double minChange)
    {
        ArgumentChecker.assertIsNonNegative("minChange", minChange);
        this.minChange = minChange;
    }

}
