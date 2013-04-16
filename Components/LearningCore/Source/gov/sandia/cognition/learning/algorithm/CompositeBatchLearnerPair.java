/*
 * File:            CompositeBatchLearnerPair.java
 * Authors:         Justin Basilico
 * Project:         Cognitive Foundry
 * 
 * Copyright 2011. All rights reserved.
 */

package gov.sandia.cognition.learning.algorithm;

import gov.sandia.cognition.evaluator.CompositeEvaluatorPair;
import gov.sandia.cognition.evaluator.Evaluator;
import gov.sandia.cognition.learning.algorithm.baseline.ConstantLearner;
import gov.sandia.cognition.util.AbstractCloneableSerializable;
import gov.sandia.cognition.util.Pair;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Composes together a pair of batch (typically unsupervised) learners. It
 * takes the input data and passes it to the first learner to learn an evaluator.
 * It then takes that first evaluator, evaluates it on it to all of the input
 * data, and passes the resulting values as input to the second learner. It
 * takes the evaluator learned from the second learner and then composes the
 * two evaluators together to create a composite evaluator. Thus, this class
 * can be used to layer learning algorithms.
 *
 * @param   <InputType>
 *      The type of the input data. It is passed to the first learning
 *      algorithm to learn the evaluator that takes this type and returns the
 *      intermediate type.
 * @param   <IntermediateType>
 *      The type of the output of the first learned function that is used as
 *      input to the second learner. It is also the input type for the second
 *      learned evaluator that returns the output type.
 * @param   <OutputType>
 *      The type of output of the evaluator learned by the second learner. It
 *      is also the output type of the composite evaluator pair.
 * @author  Justin Basilico
 * @since   3.3.3
 * @see     InputOutputTransformedBatchLearner
 */
public class CompositeBatchLearnerPair<InputType, IntermediateType, OutputType>
    extends AbstractCloneableSerializable
    implements BatchLearner<Collection<? extends InputType>, CompositeEvaluatorPair<InputType, IntermediateType, OutputType>>,
        Pair<BatchLearner<? super Collection<? extends InputType>, ? extends Evaluator<? super InputType, ? extends IntermediateType>>, BatchLearner<? super Collection<? extends IntermediateType>, ? extends Evaluator<? super IntermediateType, ? extends OutputType>>>
{

    /** The first learner that is trained on the input data. */
    protected BatchLearner<? super Collection<? extends InputType>, ? extends Evaluator<? super InputType, ? extends IntermediateType>> firstLearner;

    /** The second learner that is trained on the output of the evaluator
     *  created by the first learner. */
    protected BatchLearner<? super Collection<? extends IntermediateType>, ? extends Evaluator<? super IntermediateType, ? extends OutputType>> secondLearner;

    /**
     * Creates a new, empty {@link CompositeBatchLearnerPair}.
     */
    public CompositeBatchLearnerPair()
    {
        this(null, null);
    }

    /**
     * Creates a new {@link CompositeBatchLearnerPair} with the given two
     * learner.
     *
     * @param   firstLearner
     *      The first learner that is trained on the input data.
     * @param   secondLearner
     *      The second learner that is trained based on the output of the first
     *      learner.
     */
    public CompositeBatchLearnerPair(
        final BatchLearner<? super Collection<? extends InputType>, ? extends Evaluator<? super InputType, ? extends IntermediateType>> firstLearner,
        final BatchLearner<? super Collection<? extends IntermediateType>, ? extends Evaluator<? super IntermediateType, ? extends OutputType>> secondLearner)
    {
        super();
        
        this.firstLearner = firstLearner;
        this.secondLearner = secondLearner;
    }

    /**
     * Learn by calling the first learner on all the input values. Then the
     * resulting evaluator is applied to all of the input data to create a
     * set of intermediate data. The second learner is then called on all the
     * intermediate data to create the second evaluator that maps to the
     * output type.
     *
     * @param   data
     *      The training data.
     * @return
     *      The composite evaluator pair that applies the two learned
     *      evaluators in sequence.
     */
    @Override
    public CompositeEvaluatorPair<InputType, IntermediateType, OutputType> learn(
        final Collection<? extends InputType> data)
    {
        // Take the input data and train the first learner on it.
        final Evaluator<? super InputType, ? extends IntermediateType>
            firstResult = this.getFirstLearner().learn(data);

        // Now transform the input data using the first evaluator.
        final ArrayList<IntermediateType> transformedData =
            new ArrayList<IntermediateType>(data.size());
        for (InputType input : data)
        {
            transformedData.add(firstResult.evaluate(input));
        }

        // Now take the transformed data and train the second learner on it.
        final Evaluator<? super IntermediateType, ? extends OutputType>
            secondResult = this.getSecondLearner().learn(transformedData);

        // Return the composed evaluator.
        return CompositeEvaluatorPair.create(firstResult, secondResult);

    }

    @Override
    public BatchLearner<? super Collection<? extends InputType>, ? extends Evaluator<? super InputType, ? extends IntermediateType>> getFirst()
    {
        return this.getFirstLearner();
    }

    @Override
    public BatchLearner<? super Collection<? extends IntermediateType>, ? extends Evaluator<? super IntermediateType, ? extends OutputType>> getSecond()
    {
        return this.getSecondLearner();
    }

    /**
     * Gets the first learner that is applied to the input data.
     *
     * @return
     *      The first learner.
     */
    public BatchLearner<? super Collection<? extends InputType>, ? extends Evaluator<? super InputType, ? extends IntermediateType>> getFirstLearner()
    {
        return this.firstLearner;
    }

    /**
     * Sets the first learner that is applied to the input data.
     *
     * @param   firstLearner
     *      The first learner.
     */
    public void setFirstLearner(
        final BatchLearner<? super Collection<? extends InputType>, ? extends Evaluator<? super InputType, ? extends IntermediateType>> firstLearner)
    {
        this.firstLearner = firstLearner;
    }

    /**
     * Gets the second learner that is applied to the output of the first
     * learner.
     *
     * @return
     *      The second learner.
     */
    public BatchLearner<? super Collection<? extends IntermediateType>, ? extends Evaluator<? super IntermediateType, ? extends OutputType>> getSecondLearner()
    {
        return this.secondLearner;
    }

    /**
     * Sets the second learner that is applied to the output of the first
     * learner.
     *
     * @param   secondLearner
     *      The second learner.
     */
    public void setSecondLearner(
        final BatchLearner<? super Collection<? extends IntermediateType>, ? extends Evaluator<? super IntermediateType, ? extends OutputType>> secondLearner)
    {
        this.secondLearner = secondLearner;
    }

    /**
     * Creates a new {@link CompositeBatchLearnerPair} from the given learners.
     *
     * @param   <InputType>
     *      The type of the input data. It is passed to the first learning
     *      algorithm to learn the evaluator that takes this type and returns
     *      the intermediate type.
     * @param   <IntermediateType>
     *      The type of the output of the first learned function that is used as
     *      input to the second learner. It is also the input type for the second
     *      learned evaluator that returns the output type.
     * @param   <OutputType>
     *      The type of output of the evaluator learned by the second learner.
     *      It is also the output type of the composite evaluator pair.
     * @param   firstLearner
     *      The first learner that is trained on the input data.
     * @param   secondLearner
     *      The second learner that is trained based on the output of the first
     *      learner.
     * @return
     *      A new composite evaluator pair of the given learners.
     */
    public static <InputType, IntermediateType, OutputType> CompositeBatchLearnerPair<InputType, IntermediateType, OutputType> create(
        final BatchLearner<? super Collection<? extends InputType>, ? extends Evaluator<? super InputType, ? extends IntermediateType>> firstLearner,
        final BatchLearner<? super Collection<? extends IntermediateType>, ? extends Evaluator<? super IntermediateType, ? extends OutputType>> secondLearner)
    {
        return new CompositeBatchLearnerPair<InputType, IntermediateType, OutputType>(
            firstLearner, secondLearner);
    }

    /**
     * Creates a new {@link CompositeBatchLearnerPair} from the given input
     * transform and learner.
     *
     * @param   <InputType>
     *      The type of the input data. It is passed to the input transform
     *      takes this type and returns the intermediate type.
     * @param   <IntermediateType>
     *      The type of the output of the input transform is used as input to
     *      the learner. It is also the input type for the learned evaluator
     *      that returns the output type.
     * @param   <OutputType>
     *      The type of output of the learned evaluator.  It is also the output
     *      type of the composite evaluator pair.
     * @param   inputTransform
     *      The input transform to apply to the input data.
     * @param   learner
     *      The learner that is trained based on the output of the transform.
     * @return
     *      A new composite evaluator pair of the given transform and learner.
     */
    public static <InputType, IntermediateType, OutputType> CompositeBatchLearnerPair<InputType, IntermediateType, OutputType> createInputTransformed(
        final Evaluator<? super InputType, ? extends IntermediateType> inputTransform,
        final BatchLearner<? super Collection<? extends IntermediateType>, ? extends Evaluator<? super IntermediateType, ? extends OutputType>> learner)
    {
        return new CompositeBatchLearnerPair<InputType, IntermediateType, OutputType>(
            ConstantLearner.create(inputTransform), learner);
    }

    /**
     * Creates a new {@link CompositeBatchLearnerPair} from the given learner
     * and output transform..
     *
     * @param   <InputType>
     *      The type of the input data. It is passed to the learning algorithm
     *      to learn the evaluator that takes this type and returns the
     *      intermediate type.
     * @param   <IntermediateType>
     *      The type of the output of the first learned function that is used as
     *      input to the output transform.
     * @param   <OutputType>
     *      The type of output of the output transform. It is also the output
     *      type of the composite evaluator pair.
     * @param   learner
     *      The learner that is trained on the input data.
     * @param   outputTransform
     *      The output transform that is composed with the learned function.
     * @return
     *      A new composite evaluator pair of the given learner and transform.
     */
    public static <InputType, IntermediateType, OutputType> CompositeBatchLearnerPair<InputType, IntermediateType, OutputType> createOutputTransformed(
        final BatchLearner<? super Collection<? extends InputType>, ? extends Evaluator<? super InputType, ? extends IntermediateType>> learner,
        final Evaluator<? super IntermediateType, ? extends OutputType> outputTransform)
    {
        return new CompositeBatchLearnerPair<InputType, IntermediateType, OutputType>(
            learner, ConstantLearner.create(outputTransform));
    }
    
}
