/*
 * File:                ThreeLayerFeedforwardNeuralNetwork.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Apr 15, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.learning.function.vector;

import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.learning.algorithm.gradient.GradientDescendable;
import gov.sandia.cognition.learning.function.scalar.AtanFunction;
import gov.sandia.cognition.math.DifferentiableUnivariateScalarFunction;
import gov.sandia.cognition.math.matrix.Matrix;
import gov.sandia.cognition.math.matrix.MatrixFactory;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.math.matrix.VectorInputEvaluator;
import gov.sandia.cognition.math.matrix.VectorOutputEvaluator;
import gov.sandia.cognition.math.matrix.VectorizableVectorFunction;
import gov.sandia.cognition.util.AbstractRandomized;
import gov.sandia.cognition.util.ObjectUtil;
import java.util.Random;

/**
 * This is a "standard" feedforward neural network with a single hidden
 * layer.  There are bias terms on the input and hidden layers.  The hidden
 * units are each applied with the same smooth (differentiable) squashing
 * function and the outputs are a linear combination of the hidden units and
 * the hidden-layer bias term.
 * @author Kevin R. Dixon
 * @since 3.0
 */
@PublicationReference(
    author="Wikipedia",
    title="Multilayer perceptron",
    type=PublicationType.WebPage,
    year=2009,
    url="http://en.wikipedia.org/wiki/Multilayer_perceptron"
)
public class ThreeLayerFeedforwardNeuralNetwork
    extends AbstractRandomized
    implements VectorizableVectorFunction,
    VectorInputEvaluator<Vector,Vector>,
    VectorOutputEvaluator<Vector,Vector>,
    GradientDescendable
{

    /**
     * Default initialization range, {@value}.
     */
    public static final double DEFAULT_INITIALIZATION_RANGE = 1e-3;

    /**
     * Default squashing function, AtanFunction.
     */
    public static final DifferentiableUnivariateScalarFunction
        DEFAULT_SQUASHING_FUNCTION = new AtanFunction();

    /**
     * Default random seed, {@value}.
     */
    public static final int DEFAULT_RANDOM_SEED = 1;

    /**
     * Matrix of weights to pre-multiply the inputs by.
     */
    protected Matrix inputToHiddenWeights;

    /**
     * Bias weights to add to each of the hidden units.
     */
    protected Vector inputToHiddenBiasWeights;

    /**
     * Matrix of weights to pre-multiply the hidden-unit activations by.
     */
    protected Matrix hiddenToOutputWeights;

    /**
     * Bias weights to add to each of the output units.
     */
    protected Vector hiddenToOutputBiasWeights;

    /**
     * Squashing function to apply at the hidden layer.
     */
    private DifferentiableUnivariateScalarFunction squashingFunction;

    /**
     * Range of values to initialize the weights between, must be greater than
     * or equal to zero.
     */
    private double initializationRange;

    /** 
     * Creates a new instance of ThreeLayerFeedforwardNeuralNetwork
     */
    public ThreeLayerFeedforwardNeuralNetwork()
    {
        this( 1, 1, 1 );
    }

    /**
     * Creates a new instance of ThreeLayerFeedforwardNeuralNetwork
     * @param numInputs
     * Input dimensionality, not including the bias term,
     * must be greater than zero.
     * @param numHidden
     * Number of hidden units, not including the bias term,
     * must be greater than zero.
     * @param numOutputs
     * Output dimensionality, must be greater than zero.
     */
    public ThreeLayerFeedforwardNeuralNetwork(
        int numInputs,
        int numHidden,
        int numOutputs )
    {
        this( numInputs, numHidden, numOutputs, DEFAULT_SQUASHING_FUNCTION );
    }

    /**
     * Creates a new instance of ThreeLayerFeedforwardNeuralNetwork
     * @param numInputs
     * Input dimensionality, not including the bias term,
     * must be greater than zero.
     * @param numHidden
     * Number of hidden units, not including the bias term,
     * must be greater than zero.
     * @param numOutputs
     * Output dimensionality, must be greater than zero.
     * @param squashingFunction
     * Function to apply at the hidden layer.
     */
    public ThreeLayerFeedforwardNeuralNetwork(
        int numInputs,
        int numHidden,
        int numOutputs,
        DifferentiableUnivariateScalarFunction squashingFunction )
    {
        this( numInputs, numHidden, numOutputs, squashingFunction,
            DEFAULT_RANDOM_SEED, DEFAULT_INITIALIZATION_RANGE );
    }

    /**
     * Creates a new instance of ThreeLayerFeedforwardNeuralNetwork
     * @param numInputs
     * Input dimensionality, not including the bias term,
     * must be greater than zero.
     * @param numHidden
     * Number of hidden units, not including the bias term,
     * must be greater than zero.
     * @param numOutputs
     * Output dimensionality, must be greater than zero.
     * @param squashingFunction
     * Function to apply at the hidden layer.
     * @param randomSeed
     * Value to use as the random seed for the random-number generator.
     * @param initializationRange
     * Range of values to initialize the weights between, must be greater than
     * or equal to zero.
     */
    public ThreeLayerFeedforwardNeuralNetwork(
        int numInputs,
        int numHidden,
        int numOutputs,
        DifferentiableUnivariateScalarFunction squashingFunction,
        int randomSeed,
        double initializationRange )
    {
        super( new Random( randomSeed ) );

        this.setInitializationRange(initializationRange);
        this.setSquashingFunction(squashingFunction);
        this.initializeWeights(numInputs, numHidden, numOutputs);
    }

    @Override
    public ThreeLayerFeedforwardNeuralNetwork clone()
    {
        ThreeLayerFeedforwardNeuralNetwork clone =
            (ThreeLayerFeedforwardNeuralNetwork) super.clone();

        clone.inputToHiddenWeights = ObjectUtil.cloneSafe(this.inputToHiddenWeights);
        clone.inputToHiddenBiasWeights = ObjectUtil.cloneSafe(this.inputToHiddenBiasWeights);
        clone.hiddenToOutputWeights = ObjectUtil.cloneSafe(this.hiddenToOutputWeights);
        clone.hiddenToOutputBiasWeights = ObjectUtil.cloneSafe(this.hiddenToOutputBiasWeights);
        clone.squashingFunction = ObjectUtil.cloneSmart(this.squashingFunction);

        return clone;

    }
    
    public Matrix computeParameterGradient(
        Vector input)
    {
        int numInputs = this.getInputDimensionality();
        int numHidden = this.getHiddenDimensionality();
        int numOutput = this.getOutputDimensionality();

        int num1 = numInputs * numHidden;
        int num2 = numHidden;
        int num3 = numHidden * numOutput;
        int num4 = numOutput;
        int N = num1 + num2 + num3 + num4;
        int M = numOutput;
        int offset;

        Vector hiddenActivation = this.evaluateHiddenLayerActivation(input);
        Vector squashedHiddenLayerActivation =
            this.evaluateSquashedHiddenLayerActivation(hiddenActivation);

        double[] squashedDerivativeHiddenLayerActivation = new double[ numHidden ];
        for( int i = 0; i < squashedDerivativeHiddenLayerActivation.length; i++ )
        {
            squashedDerivativeHiddenLayerActivation[i] =
                this.squashingFunction.differentiate( hiddenActivation.getElement(i) );
        }

        Matrix gradient = MatrixFactory.getDefault().createMatrix(M,N);

        // Chain rule: y=f(g(h(x))) => h'(x)g'(h(x))*f'(g(x))
        // My case:= y = W2*f(W1*x+b1)+b2 = W2*f(W1*x)+W2*f(b1) = W2*f(h)+b2
        // Therefore:
        // dy_i/db2_j  = 1.0
        // dy_i/dW2_ij = f(h_j)
        // dy_i/db1_j  = W2_ij*f'(h_j)
        // dy_i/dW1_jk = W2_ij*f'(h_j) * x_k

        int columnIndex = N-num4-num3;
        for( int j = 0; j < numHidden; j++ )
        {
            // Derivation of this gradient assumes that the parameters are
            // column-stacked from the underlying matrices...
            // This makes a gradient which looks like:
            // [ h0 0   0   h1  0   0]
            // [ 0  h0  0   0   h1  0]
            // [ 0  0   h0  0   0   h1]
            // And so forth...
            double hj = squashedHiddenLayerActivation.getElement(j);
            for (int i = 0; i < numOutput; i++)
            {
                gradient.setElement( i, columnIndex, hj );
                columnIndex++;
            }
        }

        // Add the output bias, because it's the easiest
        offset = N-num4;
        for( int i = 0; i < numOutput; i++ )
        {
            gradient.setElement( i, i+offset, 1.0 );
        }

        offset = numHidden*numInputs;
        for( int i = 0; i < numOutput; i++ )
        {
            // Because we stack each column to vectorize the matrix parameters
            for( int j = 0; j < numHidden; j++ )
            {
                double W2ij = this.hiddenToOutputWeights.getElement(i,j);
                double dfdhj = squashedDerivativeHiddenLayerActivation[j];

                // dy_i/db1_j  = W2_ij*f'(h_j)
                double dyi_db1j = W2ij*dfdhj;
                gradient.setElement( i, j + offset, dyi_db1j );
                for( int k = 0; k < numInputs; k++ )
                {
                    // dy_i/dW1_jk = W2_ij*f'(h_j) * x_k
                    double dyi_dW1jk = W2ij*dfdhj* input.getElement(k);
                    gradient.setElement(i, k*numHidden + j, dyi_dW1jk );
                }
            }
        }

        return gradient;
    }

    public Vector convertToVector()
    {
        Vector p1 = this.inputToHiddenWeights.convertToVector();
        Vector p2 = this.inputToHiddenBiasWeights;
        Vector p3 = this.hiddenToOutputWeights.convertToVector();
        Vector p4 = this.hiddenToOutputBiasWeights;

        int num = p1.getDimensionality() + p2.getDimensionality() +
            p3.getDimensionality() + p4.getDimensionality();
        Vector parameters = VectorFactory.getDefault().createVector( num );
        int index = 0;
        for( int i = 0; i < p1.getDimensionality(); i++ )
        {
            parameters.setElement(index, p1.getElement(i) );
            index++;
        }
        for( int i = 0; i < p2.getDimensionality(); i++ )
        {
            parameters.setElement(index, p2.getElement(i) );
            index++;
        }
        for( int i = 0; i < p3.getDimensionality(); i++ )
        {
            parameters.setElement(index, p3.getElement(i));
            index++;
        }
        for( int i = 0; i < p4.getDimensionality(); i++ )
        {
            parameters.setElement(index, p4.getElement(i));
            index++;
        }

        return parameters;

    }

    /**
     * Gets the total number of parameters in the neural net, including
     * the bias weights.
     * @return
     * Total number of parameters in the neural net.
     */
    public int getNumParameters()
    {
        int numInputs = this.getInputDimensionality();
        int numHidden = this.getHiddenDimensionality();
        int numOutput = this.getOutputDimensionality();

        int num1 = numInputs * numHidden;
        int num2 = numHidden;
        int num3 = numHidden * numOutput;
        int num4 = numOutput;
        return num1 + num2 + num3 + num4;
    }

    public void convertFromVector(
        Vector parameters)
    {
        int numInputs = this.getInputDimensionality();
        int numHidden = this.getHiddenDimensionality();
        int numOutput = this.getOutputDimensionality();

        int num1 = numInputs * numHidden;
        int num2 = numHidden;
        int num3 = numHidden * numOutput;
        int num4 = numOutput;
        int num = num1 + num2 + num3 + num4;
        parameters.assertDimensionalityEquals(num);

        Vector p1 = parameters.subVector(0,num1-1);
        Vector p2 = parameters.subVector(num1,num1+num2-1);
        Vector p3 = parameters.subVector(num1+num2,num1+num2+num3-1);
        Vector p4 = parameters.subVector(num1+num2+num3,num-1);

        this.inputToHiddenWeights.convertFromVector(p1);
        this.inputToHiddenBiasWeights = p2;
        this.hiddenToOutputWeights.convertFromVector(p3);
        this.hiddenToOutputBiasWeights = p4;
    }

    public Vector evaluate(
        Vector input)
    {
        Vector hiddenActivation = this.evaluateHiddenLayerActivation(input);
        Vector squashedHiddenActivation = 
            this.evaluateSquashedHiddenLayerActivation(hiddenActivation);
        return this.evaluateOutputFromSquashedHiddenLayerActivation(
            squashedHiddenActivation);
    }

    /**
     * Computes the raw (unsquashed) activation at the hidden layer for the
     * given input.
     * @param input
     * Input to compute the raw hidden activation of.
     * @return
     * Raw (unsquashed) activation at the hidden layer.
     */
    protected Vector evaluateHiddenLayerActivation(
        Vector input )
    {
        Vector hiddenActivation = this.inputToHiddenWeights.times( input );
        hiddenActivation.plusEquals( this.inputToHiddenBiasWeights );
        return hiddenActivation;
    }

    /**
     * Evaluates the squashed hidden-layer activation from its raw activation
     * value.  This is equivalent to apply an element-wise squashing function
     * to the raw hidden activation values.
     * @param hiddenActivation
     * Raw (unsquashed) hidden activation values.
     * @return
     * Squashed hidden-layer activation.
     */
    protected Vector evaluateSquashedHiddenLayerActivation(
        Vector hiddenActivation )
    {
        return ElementWiseVectorFunction.evaluate(
            hiddenActivation,this.getSquashingFunction());
    }

    /**
     * Evaluates the output from the squashed hidden-layer activation.
     * @param squashedHiddenActivation
     * Squashed hidden-layer activation.
     * @return
     * Output of the neural net.
     */
    protected Vector evaluateOutputFromSquashedHiddenLayerActivation(
        Vector squashedHiddenActivation )
    {
        Vector outputActivation = this.hiddenToOutputWeights.times(
            squashedHiddenActivation );
        outputActivation.plusEquals( this.hiddenToOutputBiasWeights );
        return outputActivation;
    }

    /**
     * Reinitializes the neural network parameters based on its current setup.
     * It uses the object's internal random number generator to generate
     * weights uniformly in the range of
     * [-initializationRange, +initializationRange].
     */
    public void reinitializeWeights()
    {
        this.initializeWeights(
            this.getInputDimensionality(),
            this.getHiddenDimensionality(),
            this.getOutputDimensionality());
    }

    /**
     * Initializes the neural net parameters for the given dimensions, not
     * including the bias terms, using the object's random-number generator
     * uniformly between the initialization range (and its negative value).
     * @param inputDimensionality
     * Number of the inputs, not including the bias term,
     * must be greater than zero.
     * @param hiddenDimensionality
     * Number of the hidden units, not including the bias term,
     * must be greater than zero.
     * @param outputDimensionality
     * Number of the outputs, must be greater than zero.
     */
    public void initializeWeights(
        int inputDimensionality,
        int hiddenDimensionality,
        int outputDimensionality )
    {
        if( inputDimensionality < 1 )
        {
            throw new IllegalArgumentException(
                "inputDimensionality must be >= 1" );
        }
        if( hiddenDimensionality < 1 )
        {
            throw new IllegalArgumentException(
                "hiddenDimensionality must be >= 1" );
        }
        if( outputDimensionality < 1 )
        {
            throw new IllegalArgumentException(
                "outputDimensionality must be >= 1" );
        }

        this.inputToHiddenWeights = MatrixFactory.getDefault().createUniformRandom(
            hiddenDimensionality, inputDimensionality,
            -this.getInitializationRange(), this.getInitializationRange() ,this.getRandom());
        this.inputToHiddenBiasWeights = VectorFactory.getDefault().createUniformRandom(
            hiddenDimensionality, -this.getInitializationRange(),this.getInitializationRange(), this.random);

        this.hiddenToOutputWeights = MatrixFactory.getDefault().createUniformRandom(
            outputDimensionality, hiddenDimensionality,
            -this.getInitializationRange(), this.getInitializationRange() ,this.getRandom());
        this.hiddenToOutputBiasWeights = VectorFactory.getDefault().createUniformRandom(
            outputDimensionality, -this.getInitializationRange(),this.getInitializationRange(), this.random);

    }

    public int getOutputDimensionality()
    {
        return this.hiddenToOutputWeights.getNumRows();
    }

    /**
     * Sets the output dimensionality of the neural net by re-initializing the
     * weights.
     * @param outputDimensionality
     * Desired output dimensionality, must be greater than zero.
     */
    public void setOutputDimensionality(
        int outputDimensionality )
    {
        this.initializeWeights(
            this.getInputDimensionality(),
            this.getHiddenDimensionality(),
            outputDimensionality );
    }

    /**
     * Gets the number of hidden units, not including the bias term.
     * @return
     * Number of hidden units, must be greater than zero.
     */
    public int getHiddenDimensionality()
    {
        return this.hiddenToOutputWeights.getNumColumns();
    }

    /**
     * Sets the number of hidden units, not including the bias term, by
     * re-initializing the neural net's weights.
     * @param hiddenDimensionality
     * Number of hidden units, must be greater than zero.
     */
    public void setHiddenDimensionality(
        int hiddenDimensionality )
    {
        this.initializeWeights(
            this.getInputDimensionality(),
            hiddenDimensionality,
            this.getOutputDimensionality() );
    }

    public int getInputDimensionality()
    {
        return this.inputToHiddenWeights.getNumColumns();
    }

    /**
     * Sets the number of input units, not counting the bias term,
     * by re-initializing the neural net's parameters.
     * @param inputDimensionality
     * Desired input dimensionality, must be greater than zero.
     */
    public void setInputDimensionality(
        int inputDimensionality )
    {
        this.initializeWeights(
            inputDimensionality,
            this.getHiddenDimensionality(),
            this.getOutputDimensionality() );
    }

    /**
     * Getter for squashingFunction
     * @return
     * Squashing function to apply at the hidden layer.
     */
    public DifferentiableUnivariateScalarFunction getSquashingFunction()
    {
        return this.squashingFunction;
    }

    /**
     * Setter for squashingFunction
     * @param squashingFunction
     * Squashing function to apply at the hidden layer.
     */
    public void setSquashingFunction(
        DifferentiableUnivariateScalarFunction squashingFunction)
    {
        if( squashingFunction == null )
        {
            throw new IllegalArgumentException(
                "Squashing function cannot be null!" );
        }
        this.squashingFunction = squashingFunction;
    }

    /**
     * Getter for initializationRange
     * @return
     * Range of values to initialize the weights between, must be greater than
     * or equal to zero.
     */
    public double getInitializationRange()
    {
        return this.initializationRange;
    }

    /**
     * Setter for initializationRange
     * @param initializationRange
     * Range of values to initialize the weights between, must be greater than
     * or equal to zero.
     */
    public void setInitializationRange(
        double initializationRange)
    {
        if( initializationRange < 0.0 )
        {
            throw new IllegalArgumentException(
                "initializationRange must be >= 0.0" );
        }
        this.initializationRange = initializationRange;
    }
    
}
