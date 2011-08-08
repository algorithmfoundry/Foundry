/*
 * File:                DifferentiableFeedforwardNeuralNetwork.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright February 28, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.learning.function.vector;

import gov.sandia.cognition.learning.algorithm.gradient.GradientDescendable;
import gov.sandia.cognition.math.DifferentiableUnivariateScalarFunction;
import gov.sandia.cognition.math.matrix.DifferentiableVectorFunction;
import gov.sandia.cognition.math.matrix.Matrix;
import gov.sandia.cognition.math.matrix.MatrixFactory;
import gov.sandia.cognition.math.matrix.Vector;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

/**
 * A feedforward neural network that can have an arbitrary number of layers,
 * and an arbitrary differentiable squashing (activation) function assigned to 
 * each layer. The squashing functions must be differentiable.
 *
 * @author Kevin R. Dixon
 * @since  1.0
 *
 */
public class DifferentiableFeedforwardNeuralNetwork
    extends FeedforwardNeuralNetwork
    implements GradientDescendable
{

    /**
     * Creates a new instance of DifferentiableFeedforwardNeuralNetwork
     * @param nodesPerLayer
     * Number of nodes in each layer, must have no fewer than 2 layers
     * @param layerActivationFunctions 
     * Squashing function to assign to each layer, must have one fewer squashing
     * function than you do layers (that is, the input layer has no squashing)
     * @param random
     * The random number generator for initial weights.
     */
    public DifferentiableFeedforwardNeuralNetwork(
        ArrayList<Integer> nodesPerLayer,
        ArrayList<DifferentiableUnivariateScalarFunction> layerActivationFunctions,
        Random random)
    {
        super(new ArrayList<DifferentiableSquashedMatrixMultiplyVectorFunction>());

        ArrayList<DifferentiableSquashedMatrixMultiplyVectorFunction> layers =
            new ArrayList<DifferentiableSquashedMatrixMultiplyVectorFunction>(
            layerActivationFunctions.size());

        final double range = 0.1;
        for (int i = 0; i < nodesPerLayer.size() - 1; i++)
        {
            int currentNum = nodesPerLayer.get(i);
            int nextNum = nodesPerLayer.get(i + 1);

            Matrix w = MatrixFactory.getDefault().createUniformRandom(nextNum, currentNum, -range, range, random);
            DifferentiableSquashedMatrixMultiplyVectorFunction layer =
                new DifferentiableSquashedMatrixMultiplyVectorFunction(
                new MatrixMultiplyVectorFunction(w),
                layerActivationFunctions.get(i));
            layers.add(layer);
        }

        this.setLayers(layers);

    }

    /**
     * Creates a new instance of FeedforwardNeuralNetwork
     * 
     * @param numInputs 
     * Number of nodes in the input layer
     * @param numHiddens 
     * Number of nodes in the hidden (middle) layer
     * @param numOutputs 
     * Number of nodes in the output layer
     * @param activationFunction 
     * Squashing function to assign to all layers
     * @param random
     * The random number generator for the initial weights.
     */
    public DifferentiableFeedforwardNeuralNetwork(
        int numInputs,
        int numHiddens,
        int numOutputs,
        DifferentiableVectorFunction activationFunction,
        Random random)
    {
        super(new ArrayList<DifferentiableSquashedMatrixMultiplyVectorFunction>());

        ArrayList<DifferentiableSquashedMatrixMultiplyVectorFunction> layers =
            new ArrayList<DifferentiableSquashedMatrixMultiplyVectorFunction>(2);

        final double range = 1.0;

        Matrix w12 = MatrixFactory.getDefault().createUniformRandom(numHiddens, numInputs, -range, range, random);
        Matrix w23 = MatrixFactory.getDefault().createUniformRandom(numOutputs, numHiddens, -range, range, random);

        layers.add(new DifferentiableSquashedMatrixMultiplyVectorFunction(
            new MatrixMultiplyVectorFunction(w12), activationFunction));

        layers.add(new DifferentiableSquashedMatrixMultiplyVectorFunction(
            new MatrixMultiplyVectorFunction(w23), activationFunction));

        this.setLayers(layers);
    }

    /**
     * Creates a new instance of FeedforwardNeuralNetwork
     * 
     * @param numInputs 
     * Number of nodes in the input layer
     * @param numHiddens 
     * Number of nodes in the hidden (middle) layer
     * @param numOutputs 
     * Number of nodes in the output layer
     * @param scalarFunction 
     * Squashing function to assign to all layers
     * @param random
     * The random number generator for the initial weights.
     */
    public DifferentiableFeedforwardNeuralNetwork(
        int numInputs,
        int numHiddens,
        int numOutputs,
        DifferentiableUnivariateScalarFunction scalarFunction,
        Random random)
    {
        this(numInputs, numHiddens, numOutputs,
            new ElementWiseDifferentiableVectorFunction(scalarFunction), 
            random);
    }
    
    /**
     * Creates a new instance of FeedforwardNeuralNetwork
     * @param layers
     * Layers of the neural network
     */
    public DifferentiableFeedforwardNeuralNetwork(
        DifferentiableSquashedMatrixMultiplyVectorFunction ... layers )
    {
        super( new ArrayList<DifferentiableSquashedMatrixMultiplyVectorFunction>( 
            Arrays.asList( layers ) ) );
    }

    @Override
    public DifferentiableFeedforwardNeuralNetwork clone()
    {
        return (DifferentiableFeedforwardNeuralNetwork) super.clone();
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public ArrayList<DifferentiableSquashedMatrixMultiplyVectorFunction> getLayers()
    {
        return (ArrayList<DifferentiableSquashedMatrixMultiplyVectorFunction>) super.getLayers();
    }

    public Matrix computeParameterGradient(
        Vector input)
    {
        int numLayers = this.getLayers().size();
        ArrayList<Vector> layerActivations = this.evaluateAtEachLayer(input);
        ArrayList<Matrix> layerGradients = new ArrayList<Matrix>(numLayers);

        int M = layerActivations.get(numLayers).getDimensionality();
        int N = 0;
        Matrix layerDerivative = MatrixFactory.getDefault().createIdentity(M, M);
        for (int i = numLayers - 1; i >= 0; i--)
        {
            DifferentiableSquashedMatrixMultiplyVectorFunction layer =
                this.getLayers().get(i);

            Vector layerInput = layerActivations.get(i);

            Matrix layerGradient = layerDerivative.times(
                layer.computeParameterGradient(layerInput));

            N += layerGradient.getNumColumns();
            layerGradients.add(layerGradient);

            // Don't need to chain rule beyond the first layer
            if( i > 0 )
            {
                layerDerivative = layerDerivative.times(
                    layer.differentiate(layerInput));
            }
        }

        // Gradients were pushed on in reverse order, so push them into
        // the gradient Matrix in reverse order... this will be the
        // correct order for the parameter ordering
        Matrix gradient = MatrixFactory.getDefault().createMatrix(M, N);
        int columnIndex = 0;
        for (int n = numLayers - 1; n >= 0; n--)
        {
            Matrix layerGradient = layerGradients.get(n);

            // The last entry has a special form
            // [ x0 0  0  x3 0  0  x6 ...
            // [ 0  x1 0  0  x4 0  ...
            // [ 0  0  x2 0  0  x5 ...
            if( n == 0 )
            {
                int row = 0;
                int Mi = layerGradient.getNumRows();
                int Ni = layerGradient.getNumColumns();
                for( int column = 0; column < Ni; column++ )
                {
                    double value = layerGradient.getElement( row, column );

                    gradient.setElement( row, columnIndex+column, value );
                    row = (row + 1) % Mi;
                }
            }
            else
            {
                gradient.setSubMatrix(0, columnIndex, layerGradient);
            }
            columnIndex += layerGradient.getNumColumns();
        }
        
        return gradient;

    }

}
