/*
 * File:                FeedforwardNeuralNetwork.java
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

import gov.sandia.cognition.math.matrix.Matrix;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.math.matrix.VectorizableVectorFunction;
import gov.sandia.cognition.math.UnivariateScalarFunction;
import gov.sandia.cognition.util.AbstractCloneableSerializable;
import gov.sandia.cognition.util.ObjectUtil;
import java.util.ArrayList;

/**
 * A feedforward neural network that can have an arbitrary number of layers,
 * and an arbitrary squashing (activation) function assigned to each layer.
 * The squashing functions need not be differentiable!  To use a neural net
 * with backprop (GradientDescent), then use
 * DifferentiableFeedforwardNeuralNetwork
 *
 * @author Kevin R. Dixon
 * @since  1.0
 *
 */
public class FeedforwardNeuralNetwork
    extends AbstractCloneableSerializable
    implements VectorizableVectorFunction
{

    /**
     * Layers that comprise this neural network
     */
    private ArrayList<? extends GeneralizedLinearModel> layers;

    /**
     * Creates a new instance of FeedforwardNeuralNetwork
     * @param nodesPerLayer
     * Number of nodes in each layer, must have no fewer than 2 layers
     * @param layerActivationFunctions 
     * Squashing function to assign to each layer, must have one fewer squashing
     * function than you do layers (that is, the input layer has no squashing)
     * 
     */
    public FeedforwardNeuralNetwork(
        ArrayList<Integer> nodesPerLayer,
        ArrayList<? extends UnivariateScalarFunction> layerActivationFunctions )
    {

        if (nodesPerLayer.size() != layerActivationFunctions.size() + 1)
        {
            throw new IllegalArgumentException(
                "Number of layers must equal layerActivationFunction + 1" );
        }
        ArrayList<GeneralizedLinearModel> localLayers =
            new ArrayList<GeneralizedLinearModel>(
            layerActivationFunctions.size() );

        for (int i = 0; i < nodesPerLayer.size() - 1; i++)
        {
            int currentNum = nodesPerLayer.get( i );
            int nextNum = nodesPerLayer.get( i + 1 );

            localLayers.add( new GeneralizedLinearModel(
                    currentNum, nextNum, layerActivationFunctions.get( i ) ) );
        }

        this.setLayers( localLayers );

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
     */
    public FeedforwardNeuralNetwork(
        int numInputs,
        int numHiddens,
        int numOutputs,
        UnivariateScalarFunction activationFunction )
    {
        ArrayList<GeneralizedLinearModel> localLayers =
            new ArrayList<GeneralizedLinearModel>( 2 );

        localLayers.add( new GeneralizedLinearModel(
            numInputs, numHiddens, activationFunction ) );

        localLayers.add( new GeneralizedLinearModel(
            numHiddens, numOutputs, activationFunction ) );

        this.setLayers( localLayers );
    }

    /**
     * Creates a new instance of FeedforwardNeuralNetwork
     * 
     * @param layers 
     * Layers that comprise this neural network
     */
    public FeedforwardNeuralNetwork(
        ArrayList<? extends GeneralizedLinearModel> layers )
    {
        this.setLayers( layers );
    }

    @Override
    public FeedforwardNeuralNetwork clone()
    {
        FeedforwardNeuralNetwork clone =
            (FeedforwardNeuralNetwork) super.clone();
        clone.setLayers( ObjectUtil.cloneSmartElementsAsArrayList(
            this.getLayers() ) );
        return clone;
    }

    public Vector convertToVector()
    {
        int numParams = 0;
        ArrayList<Vector> layerParameters =
            new ArrayList<Vector>( this.getLayers().size() );
        for (int i = 0; i < this.getLayers().size(); i++)
        {
            Vector p = this.getLayers().get( i ).convertToVector();
            layerParameters.add( p );
            numParams += p.getDimensionality();
        }
        
        Vector parameters = VectorFactory.getDefault().createVector( numParams );
        int index = 0;
        for( Vector p : layerParameters )
        {
            int dim = p.getDimensionality();
            for( int i = 0; i < dim; i++ )
            {
                parameters.setElement( index, p.getElement(i) );
                index++;
            }
        }
        
        return parameters;
    }

    public void convertFromVector(
        Vector parameters )
    {

        int minIndex = 0;
        int maxIndex = -1;

        for (int i = 0; i < this.getLayers().size(); i++)
        {
            GeneralizedLinearModel layer = this.getLayers().get( i );
            Matrix matrix = layer.getDiscriminant().getDiscriminant();
            int num = matrix.getNumRows() * matrix.getNumColumns();

            minIndex = maxIndex + 1;
            maxIndex = minIndex + num - 1;

            Vector layerParameters = parameters.subVector( minIndex, maxIndex );
            layer.convertFromVector( layerParameters );
        }

    }

    public Vector evaluate(
        Vector input )
    {
        ArrayList<Vector> layerActivations = this.evaluateAtEachLayer( input );
        return layerActivations.get( layerActivations.size() - 1 );
    }

    /**
     * Returns the activations that occured at each layer
     * @param input Input to evaluate
     * @return activations at each layer, where get(0) is the input layer
     * and get(n) is the output layer
     */
    protected ArrayList<Vector> evaluateAtEachLayer(
        Vector input )
    {
        int N = this.getLayers().size();
        ArrayList<Vector> layerActivations = new ArrayList<Vector>( N + 1 );
        layerActivations.add( input );
        Vector activation = input;
        for (GeneralizedLinearModel f : this.getLayers())
        {
            activation = f.evaluate( activation );
            layerActivations.add( activation );
        }

        return layerActivations;

    }

    /**
     * Getter for layers
     * @return 
     * Layers that comprise this neural network
     */
    public ArrayList<? extends GeneralizedLinearModel> getLayers()
    {
        return this.layers;
    }

    /**
     * Setter for layers
     * @param layers 
     * Layers that comprise this neural network
     */
    public void setLayers(
        ArrayList<? extends GeneralizedLinearModel> layers )
    {
        this.layers = layers;
    }

    @Override
    public String toString()
    {
        StringBuilder retval = new StringBuilder();
        retval.append( this.getClass() + " with " + this.getLayers().size() + " Layers." );
        retval.append( "\n" );
        for (int i = 0; i < this.getLayers().size(); i++)
        {
            retval.append( "Layer " + i + "->" + (i + 1) );
            retval.append( "\n" );
            retval.append( this.getLayers().get( i ).toString() );
        }

        return retval.toString();
    }

}
