/*
 * File:                SequencePredictionLearner.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright June 09, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 * See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.learning.algorithm;

import gov.sandia.cognition.collection.DefaultMultiCollection;
import gov.sandia.cognition.collection.FiniteCapacityBuffer;
import gov.sandia.cognition.collection.MultiCollection;
import gov.sandia.cognition.learning.data.DatasetUtil;
import gov.sandia.cognition.learning.data.DefaultInputOutputPair;
import gov.sandia.cognition.learning.data.InputOutputPair;
import java.util.ArrayList;
import java.util.Collection;

/**
 * A wrapper learner that converts an unlabeled sequence of data into a sequence
 * of prediction data using a fixed prediction horizon. If the input data
 * contains several sequences then it should be represented as a
 * multi-collection.
 *
 * @param   <DataType>
 *      The data type to do sequence prediction learning over.
 * @param   <LearnedType>
 *      The type of object produced by this learner.
 * @author  Justin Basilico
 * @since   3.0
 */
public class SequencePredictionLearner<DataType, LearnedType>
    extends AbstractBatchLearnerContainer<BatchLearner<? super Collection<? extends InputOutputPair<? extends DataType, DataType>>, ? extends LearnedType>>
    implements BatchLearner<Collection<? extends DataType>, LearnedType>
{
    
    /** The default prediction horizon is {@value}. */
    public static final int DEFAULT_PREDICTION_HORIZION = 1;

    /** The prediction horizon, which is the number of samples in the future to
     *  try to learn to predict. It must be a positive number. */
    protected int predictionHorizon;

    /**
     * Creates a new {@code SequencePredictionLearner} with default parameters.
     */
    public SequencePredictionLearner()
    {
        this(null, DEFAULT_PREDICTION_HORIZION);
    }

    /**
     * Creates a new {@code SequencePredictionLearner} with the given learner
     * and prediction horizon.
     *
     * @param   learner
     *      The supervised learner to call on the prediction sequence.
     * @param   predictionHorizon
     *      The prediction horizon, which is the number of samples in the
     *      future to try to learn to predict. It must be a positive number.
     */
    public SequencePredictionLearner(
        final BatchLearner<? super Collection<? extends InputOutputPair<? extends DataType, DataType>>, ? extends LearnedType> learner,
        final int predictionHorizon)
    {
        super(learner);

        this.setPredictionHorizon(predictionHorizon);
    }

    public LearnedType learn(
        final Collection<? extends DataType> data)
    {
        // Convert the data to a multi-collection (if it is one).
        return this.learn(DatasetUtil.asMultiCollection(data));
    }

    /**
     * Converts the given multi-collection of data sequences to create sequences
     * of input-output pairs to learn over.
     *
     * @param   data
     *      The data to learn a prediction over.
     * @return
     *      The object learned over the input-output prediction pairs.
     */
    public LearnedType learn(
        final MultiCollection<? extends DataType> data)
    {
        // Convert the data to a multi-collection (if it is one).
        final MultiCollection<InputOutputPair<DataType, DataType>> supervisedData =
            createPredictionDataset(data, this.getPredictionHorizion());
        return this.getLearner().learn(supervisedData);
    }

    /**
     * Takes a collection and creates a multi-collection of sequences of
     * input-output pairs that are from the given sequence with the given
     * prediction horizon.
     *
     * @param   <DataType>
     *      The data type to create a prediction dataset for.
     * @param   data
     *      A collection (or multi-collection) to convert into a prediction
     *      collection.
     * @param   predictionHorizon
     *      The prediction horizon to create the prediction dataset over.
     *      Must be positive.
     * @return
     *      A multi-collection containing the input-output pairs that
     *      correspond to the prediction problem of prediction the output
     *      that is predictionHorizon elements after the input.
     */
    public static <DataType> MultiCollection<InputOutputPair<DataType, DataType>> createPredictionDataset(
        final Collection<? extends DataType> data,
        final int predictionHorizon)
    {
        return createPredictionDataset(DatasetUtil.asMultiCollection(data), predictionHorizon);
    }

    /**
     * Takes a multi-collection and creates a multi-collection of sequences of
     * input-output pairs that are from the given sequence with the given
     * prediction horizon. It treats each collection in the given
     * multi-collection as a separate sequence, so it does not create data
     * points that cross the the boundary between them.
     *
     * @param   <DataType>
     *      The data type to create a prediction dataset for.
     * @param   data
     *      A collection (or multi-collection) to convert into a prediction
     *      collection.
     * @param   predictionHorizon
     *      The prediction horizon to create the prediction dataset over.
     *      Must be positive.
     * @return
     *      A multi-collection containing the input-output pairs that
     *      correspond to the prediction problem of prediction the output
     *      that is predictionHorizon elements after the input.
     */
    public static <DataType> MultiCollection<InputOutputPair<DataType, DataType>> createPredictionDataset(
        final MultiCollection<? extends DataType> data,
        final int predictionHorizon)
    {
        // Create the resulting list of sequences.
        final ArrayList<Collection<InputOutputPair<DataType, DataType>>> sequences =
            new ArrayList<Collection<InputOutputPair<DataType, DataType>>>(
                data.subCollections().size());

        // Use a finite capacity buffer to buffer the inputs.
        final FiniteCapacityBuffer<DataType> buffer =
            new FiniteCapacityBuffer<DataType>(predictionHorizon);
        for (Collection<? extends DataType> subData : data.subCollections())
        {
            final int sequenceLength = subData.size() - predictionHorizon;
            if (sequenceLength <= 0)
            {
                // No data in this sub-sequence.
                continue;
            }

            // Create the sequence to store the result in.
            final ArrayList<InputOutputPair<DataType, DataType>> sequence =
                new ArrayList<InputOutputPair<DataType, DataType>>(
                    sequenceLength);

            // Clear out the buffer for the next loop.
            buffer.clear();
            for (DataType output : subData)
            {
                if (buffer.isFull())
                {
                    // The buffer is full, so the first element in the buffer
                    // should be the input to learn from.
                    final DataType input = buffer.getFirst();

                    // Add a new input-output pair to the sequence.
                    sequence.add(new DefaultInputOutputPair<DataType, DataType>(
                        input, output));
                }
                // else - Buffer is not yet full, so there is no new prediction
                // example to add.

                // Add the output value to the buffer.
                buffer.addLast(output);
            }

            // Add the created sequence to the list of sequences.
            sequences.add(sequence);
        }

        return new DefaultMultiCollection<InputOutputPair<DataType, DataType>>(
            sequences);
    }

    /**
     * Gets the prediction horizon, which is the number of samples ahead in time
     * that the learner is to predict.
     *
     * @return
     *      The prediction horizon.
     */
    public int getPredictionHorizion()
    {
        return this.predictionHorizon;
    }

    /**
     * Sets the prediction horizon, which is the number of samples ahead in time
     * that the learner is to predict.
     *
     * @param   predictionHorizon
     *      The prediction horizon. Must be positive.
     */
    public void setPredictionHorizon(
        final int predictionHorizon)
    {
        if (predictionHorizon <= 0)
        {
            throw new IllegalArgumentException(
                "predictionHorizon must be positive");
        }
        this.predictionHorizon = predictionHorizon;
    }

}
