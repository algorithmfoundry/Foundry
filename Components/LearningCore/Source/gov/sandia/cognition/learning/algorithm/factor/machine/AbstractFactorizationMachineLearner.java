/*
 * File:            AbstractFactorizationMachineLearner.java
 * Authors:         Justin Basilico
 * Project:         Cognitive Foundry
 * 
 * Copyright 2013 Cognitive Foundry. All rights reserved.
 */

package gov.sandia.cognition.learning.algorithm.factor.machine;

import gov.sandia.cognition.algorithm.MeasurablePerformanceAlgorithm;
import gov.sandia.cognition.learning.algorithm.AbstractAnytimeSupervisedBatchLearner;
import gov.sandia.cognition.learning.data.DatasetUtil;
import gov.sandia.cognition.math.matrix.Matrix;
import gov.sandia.cognition.math.matrix.MatrixFactory;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.util.ArgumentChecker;
import gov.sandia.cognition.util.Randomized;
import java.util.Random;

/**
 * An abstract class for learning {@link FactorizationMachine}s. It defines
 * the common parameters for learning algorithms and how to initialize the
 * factorization machine for learning.
 *
 * @author  Justin Basilico
 * @since   3.4.0
 */
public abstract class AbstractFactorizationMachineLearner
    extends AbstractAnytimeSupervisedBatchLearner<Vector, Double, FactorizationMachine>
    implements Randomized, MeasurablePerformanceAlgorithm
{
    /** The default number of factors is {@value}. */    
    public static final int DEFAULT_FACTOR_COUNT = 10;
    
    /** The default for bias enabled is {@value}. */
    public static boolean DEFAULT_BIAS_ENABLED = true;
    
    /** The default for weights enabled is {@value}. */
    public static boolean DEFAULT_WEIGHTS_ENABLED = true;
    
    /** The default bias regularization parameter is {@value}. */
    public static final double DEFAULT_BIAS_REGULARIZATION = 0.0;
    
    /** The default weight regularization parameter is {@value}. */
    public static final double DEFAULT_WEIGHT_REGULARIZATION = 0.001;
    
    /** The default factor regularization parameter is {@value}. */
    public static final double DEFAULT_FACTOR_REGULARIZATION = 0.01;
    
    /** The default seed initialization scale is {@value}. */
    public static final double DEFAULT_SEED_SCALE = 0.01;
    
    /** The default maximum number of iterations is {@value}. */
    public static final int DEFAULT_MAX_ITERATIONS = 100;
    
    /** True if the bias term is enabled. */
    protected boolean biasEnabled;
    
    /** True if the linear weight term is enabled. */
    protected boolean weightsEnabled;
    
    /** The number of factors to use. Zero means no factors. */
    protected int factorCount;
    
    /** The regularization term for the bias. Cannot be negative. */
    protected double biasRegularization;
    
    /** The regularization term for the linear weights. Cannot be negative. */
    protected double weightRegularization;
    
    /** The regularization term for the factor matrix. Cannot be negative. */
    protected double factorRegularization;
    
    /** The standard deviation for initializing the factors. Cannot be negative.
     */
    protected double seedScale;
    
    /** The random number generator to use. */
    protected Random random;
    
    /** The current factorization machine output learned by the algorithm. */
    protected transient FactorizationMachine result;
    
    /** The dimensionality of the input to the factorization machine. */
    protected transient int dimensionality;
    
    /**
     * Creates a new {@link AbstractFactorizationMachineLearner}.
     */
    public AbstractFactorizationMachineLearner()
    {
        this(DEFAULT_FACTOR_COUNT, DEFAULT_BIAS_REGULARIZATION,
            DEFAULT_WEIGHT_REGULARIZATION, DEFAULT_FACTOR_REGULARIZATION,
            DEFAULT_SEED_SCALE, DEFAULT_MAX_ITERATIONS, new Random());
    }

    /**
     * Creates a new {@link AbstractFactorizationMachineLearner}.
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
     * @param   random 
     *      The random number generator.
     */
    public AbstractFactorizationMachineLearner(
        final int factorCount,
        final double biasRegularization,
        final double weightRegularization,
        final double factorRegularization,
        final double seedScale,
        final int maxIterations,
        final Random random)
    {
        super(maxIterations);
        
        this.setFactorCount(factorCount);
        this.setBiasEnabled(DEFAULT_BIAS_ENABLED);
        this.setWeightsEnabled(DEFAULT_WEIGHTS_ENABLED);
        this.setBiasRegularization(biasRegularization);
        this.setWeightRegularization(weightRegularization);
        this.setFactorRegularization(factorRegularization);
        this.setSeedScale(seedScale);
        this.setRandom(random);
    }
    
    @Override
    protected boolean initializeAlgorithm()
    {
        // Initialize the weight vectors.
        this.dimensionality = DatasetUtil.getInputDimensionality(this.data);
        final VectorFactory<?> vectorFactory = VectorFactory.getDenseDefault();
        final Vector weights = vectorFactory.createVector(this.dimensionality);
       
        // Initialize the factors.
        final Matrix factors;
        if (this.factorCount <= 0)
        {
            factors = null;
        }
        else
        {
            // Initialize the factors to small random gaussian values.
            factors = MatrixFactory.getDenseDefault().createMatrix(
                this.factorCount, this.dimensionality);
            for (int i = 0; i < this.dimensionality; i++)
            {
                for (int j = 0; j < this.factorCount; j++)
                {
                    factors.setElement(j, i, 
                        this.seedScale * this.random.nextGaussian());
                }
            }
        }
        
        // Initialize the factorization machine.
        this.result = new FactorizationMachine(0.0, weights, factors);
        
        return true;
    }

    @Override
    public FactorizationMachine getResult()
    {
        return this.result;
    }

    /**
     * Gets the number of factors. The factors are used to represent the
     * pairwise interaction between dimensions in the input vector.
     * 
     * @return
     *      The number of factors to use. Zero means no factors. Cannot be
     *      negative.
     */
    public int getFactorCount()
    {
        return this.factorCount;
    }

    /**
     * Sets the number of factors. The factors are used to represent the
     * pairwise interaction between dimensions in the input vector.
     * 
     * @param   factorCount
     *      The number of factors to use. Zero means no factors. Cannot be
     *      negative.
     */
    public void setFactorCount(
        final int factorCount)
    {
        ArgumentChecker.assertIsNonNegative("factorCount", factorCount);
        this.factorCount = factorCount;
    }
    
    /**
     * Gets whether or not the bias term is enabled. If it is not enabled,
     * it will default to zero and not be updated.
     * 
     * @return 
     *      True if the bias term is enabled, otherwise false.
     */
    public boolean isBiasEnabled()
    {
        return this.biasEnabled;
    }
    
    /**
     * Sets whether or not the bias term is enabled. If it is not enabled,
     * it will default to zero and not be updated.
     * 
     * @param   biasEnabled 
     *      True if the bias term is enabled, otherwise false.
     */
    public void setBiasEnabled(
        final boolean biasEnabled)
    {
        this.biasEnabled = biasEnabled;
    }

    /**
     * Gets whether or not the linear weight term is enabled. If it is not
     * enabled, it will default to a zero vector and not be updated.
     * 
     * @return 
     *      True if the linear term is enabled, otherwise false.
     */
    public boolean isWeightsEnabled()
    {
        return this.weightsEnabled;
    }

    /**
     * Sets whether or not the linear weight term is enabled. If it is not
     * enabled, it will default to a zero vector and not be updated.
     * 
     * @param   weightsEnabled 
     *      True if the linear term is enabled, otherwise false.
     */
    public void setWeightsEnabled(
        final boolean weightsEnabled)
    {
        this.weightsEnabled = weightsEnabled;
    }
    
    /**
     * Gets whether or not the factors are enabled. This is true when the
     * number of factors is greater than zero.
     * 
     * @return 
     *      True if the number of factors is greater than zero, otherwise
     *      false.
     */
    public boolean isFactorsEnabled()
    {
        return this.getFactorCount() > 0;
    }
    
    /**
     * Gets the value for the parameter controlling the bias regularization.
     * 
     * @return 
     *      The regularization term for the bias. Cannot be negative.
     */
    public double getBiasRegularization()
    {
        return this.biasRegularization;
    }

    /**
     * Sets the value for the parameter controlling the bias regularization.
     * 
     * @param   biasRegularization  
     *      The regularization term for the bias. Cannot be negative.
     */
    public void setBiasRegularization(
        final double biasRegularization)
    {
        ArgumentChecker.assertIsNonNegative("biasRegularization", biasRegularization);
        this.biasRegularization = biasRegularization;
    }

    /**
     * Gets the value for the parameter controlling the linear weight
     * regularization.
     * 
     * @return 
     *      The regularization term for the weights. Cannot be negative.
     */
    public double getWeightRegularization()
    {
        return this.weightRegularization;
    }

    /**
     * 
     * Sets the value for the parameter controlling the linear weight
     * regularization.
     * 
     * @param   weightRegularization 
     *      The regularization term for the weights. Cannot be negative.
     */
    public void setWeightRegularization(
        final double weightRegularization)
    {
        ArgumentChecker.assertIsNonNegative("weightRegularization", weightRegularization);
        this.weightRegularization = weightRegularization;
    }

    /**
     * Gets the value for the parameter controlling the factor matrix
     * regularization.
     * 
     * @return 
     *      The regularization term for the factors. Cannot be negative.
     */
    public double getFactorRegularization()
    {
        return this.factorRegularization;
    }

    /**
     * Sets the value for the parameter controlling the factor matrix
     * regularization.
     * 
     * @param   factorRegularization  
     *      The regularization term for the factors. Cannot be negative.
     */
    public void setFactorRegularization(
        final double factorRegularization)
    {
        ArgumentChecker.assertIsNonNegative("factorRegularization", factorRegularization);
        this.factorRegularization = factorRegularization;
    }

    /**
     * Gets the seed initialization scale. It is multiplied by a random 
     * Gaussian to initialize each factor value.
     * 
     * @return
     *      The random initialization scale for the factors. Cannot be negative.
     */
    public double getSeedScale()
    {
        return this.seedScale;
    }

    /**
     * Sets the seed initialization scale. It is multiplied by a random 
     * Gaussian to initialize each factor value.
     * 
     * @param   seedScale
     *      The random initialization scale for the factors. Cannot be negative.
     */
    public void setSeedScale(
        final double seedScale)
    {
        ArgumentChecker.assertIsNonNegative("seedScale", seedScale);
        this.seedScale = seedScale;
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
}
