/*
 * File:                TimeSeriesPredictionLearner.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Mar 6, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.learning.algorithm;

import gov.sandia.cognition.evaluator.Evaluator;
import gov.sandia.cognition.learning.data.DefaultInputOutputPair;
import gov.sandia.cognition.learning.data.InputOutputPair;
import gov.sandia.cognition.learning.data.feature.DelayFunction;
import gov.sandia.cognition.util.AbstractCloneableSerializable;
import java.util.ArrayList;
import java.util.Collection;

/**
 * A learner used to predict the future of a sequence of data by wrapping
 * another learner and created a future-aligned data set.
 * @param <InputType> Type of inputs to the prediction engine.
 * @param <OutputType> Type of outputs to predict from the prediction engine.
 * @param <EvaluatorType> Type of evaluator produced by the learning algorithm.
 * @author Kevin R. Dixon
 * @since 3.0
 */
public class TimeSeriesPredictionLearner<InputType,OutputType,EvaluatorType extends Evaluator<? super InputType,? extends OutputType>>
    extends AbstractCloneableSerializable
    implements SupervisedBatchLearner<InputType,OutputType,EvaluatorType>
{
// TODO: This class should be modified to more closely match the API exposed by
// SequencePredictionLearner, except with supervised data.
// -- jdbasil (2009-12-23)

    /**
     * Default prediction horizon, {@value}.
     */
    public static final int DEFAULT_PREDICTION_HORIZON = 1;

    /**
     * Number of samples into the future to predict.
     */
    private int predictionHorizon;

    /**
     * Learning algorithm that does the heavy lifting.
     */
    private SupervisedBatchLearner<InputType,OutputType,EvaluatorType> supervisedLearner;

    /**
     * Default constructor
     */
    public TimeSeriesPredictionLearner()
    {
        this( DEFAULT_PREDICTION_HORIZON );
    }

    /**
     * Creates a new instance of TimeSeriesPredictionLearner
     * @param predictionHorizon
     * Number of samples into the future to predict.
     */
    public TimeSeriesPredictionLearner(
        int predictionHorizon )
    {
        this( predictionHorizon, null );
    }

    /**
     * Creates a new instance of TimeSeriesPredictionLearner
     * @param predictionHorizon
     * Number of samples into the future to predict.
     * @param supervisedLearner
     * Learning algorithm that does the heavy lifting.
     */
    public TimeSeriesPredictionLearner(
        int predictionHorizon,
        SupervisedBatchLearner<InputType,OutputType,EvaluatorType> supervisedLearner )
    {
        this.setPredictionHorizon( predictionHorizon );
        this.setSupervisedLearner( supervisedLearner );
    }

    /**
     * Getter for predictionHorizon
     * @return
     * Number of samples into the future to predict.
     */
    public int getPredictionHorizon()
    {
        return predictionHorizon;
    }

    /**
     * Setter for predictionHorizon
     * @param predictionHorizon
     * Number of samples into the future to predict.
     */
    public void setPredictionHorizon(
        int predictionHorizon )
    {
        this.predictionHorizon = predictionHorizon;
    }

    /**
     * Getter for supervisedLearner
     * @return
     * Learning algorithm that does the heavy lifting.
     */
    public SupervisedBatchLearner<InputType, OutputType, EvaluatorType> getSupervisedLearner()
    {
        return this.supervisedLearner;
    }

    /**
     * Setter for supervisedLearner
     * @param supervisedLearner
     * Learning algorithm that does the heavy lifting.
     */
    public void setSupervisedLearner(
        SupervisedBatchLearner<InputType, OutputType, EvaluatorType> supervisedLearner )
    {
        this.supervisedLearner = supervisedLearner;
    }

    public EvaluatorType learn(
        Collection<? extends InputOutputPair<? extends InputType, OutputType>> data )
    {
        ArrayList<InputOutputPair<InputType,OutputType>> predictionData =
            createPredictionDataset( this.getPredictionHorizon(), data );
        return this.getSupervisedLearner().learn( predictionData );
    }

    /**
     * Creates a dataset that can be used to predict the future by
     * "predictionHorizon" samples.  This is done by lagging the inputs to
     * align with those into the future.  Thus, the resulting Collection will
     * have a size of "predictionHorizon" shorter than the given "data".
     * @param <InputType> Type of inputs to the prediction engine.
     * @param <OutputType> Type of outputs to predict from the prediction engine.
     * @param predictionHorizon
     * Number of samples into the future to predict.
     * @param data
     * Data to align for predicting into the future.
     * @return
     * Dataset, aligned to predict into the future, that is "predictionHorizon"
     * samples shorter than the given "data".
     */
    public static <InputType,OutputType> ArrayList<InputOutputPair<InputType,OutputType>> createPredictionDataset(
        final int predictionHorizon,
        final Collection<? extends InputOutputPair<? extends InputType, OutputType>> data )
    {

        int capacity = data.size() - predictionHorizon;
        if( capacity < 0 )
        {
            capacity = 0;
        }
        ArrayList<InputOutputPair<InputType,OutputType>> result =
            new ArrayList<InputOutputPair<InputType, OutputType>>( capacity );
        
        DelayFunction<InputType> delay =
            new DelayFunction<InputType>( predictionHorizon );
        int index = -predictionHorizon;
        for( InputOutputPair<? extends InputType,OutputType> value : data )
        {
            InputType laggedInput = delay.evaluate( value.getInput() );
            if( index >= 0 )
            {
                result.add( new DefaultInputOutputPair<InputType, OutputType>(
                    laggedInput, value.getOutput() ) );
            }
            index++;
        }

        return result;
        
    }


}
