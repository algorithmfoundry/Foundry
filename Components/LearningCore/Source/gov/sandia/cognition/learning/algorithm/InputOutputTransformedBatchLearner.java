/*
 * File:            InputOutputTransformedLearner.java
 * Authors:         Justin Basilico
 * Project:         Community Foundry
 * 
 * Copyright 2011 Cognitive Foundry. All rights reserved.
 */

package gov.sandia.cognition.learning.algorithm;

import gov.sandia.cognition.evaluator.CompositeEvaluatorTriple;
import gov.sandia.cognition.evaluator.Evaluator;
import gov.sandia.cognition.evaluator.IdentityEvaluator;
import gov.sandia.cognition.evaluator.ReversibleEvaluator;
import gov.sandia.cognition.learning.algorithm.baseline.ConstantLearner;
import gov.sandia.cognition.learning.data.DatasetUtil;
import gov.sandia.cognition.learning.data.DefaultInputOutputPair;
import gov.sandia.cognition.learning.data.DefaultWeightedInputOutputPair;
import gov.sandia.cognition.learning.data.InputOutputPair;
import gov.sandia.cognition.learning.data.WeightedInputOutputPair;
import gov.sandia.cognition.util.ObjectUtil;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * An adapter class for performing supervised learning from data where both
 * the input and output have to be transformed before they are passed to the
 * learning algorithm. The typical use-case for this class is to make use of
 * some supervised learning algorithm on data that does not directly fit its
 * input and/or output types. Thus, the data must be transformed before the
 * learner is run on the data. It must also be transformed when the resulting
 * evaluator is applied to new data. Since both the inputs and outputs need
 * to be converted, unsupervised learning algorithms are to be provided to
 * learn those transforms from the collection of inputs and outputs,
 * respectively. While the input learner just needs to be an evaluator to
 * forward-transform the data, the output learner needs to be reversible so
 * that the training labels can be reversed from values that the supervised
 * learner can be trained on. Thus, the forward evaluation is used during 
 * training reverse evaluation of the output, while the reverse is used when
 * applying it to new data. The result of this learning adapter is the triple
 * containing the learned input, supervised, and output evaluators, which can
 * be applied to the same data types that the adapter was given in training.
 *
 * Note that this class can also be used in cases where only one side needs
 * to be converted, either by using the static {@code create} methods, or by
 * passing in {@code ConstantLearner}s that contain
 * {@code IdentityEvaluator}s. Thus, this class can act as a very flexible
 * adapter for many types of supervised learning problems.
 *
 * @param   <InputType>
 *      The input type of the data to learn on. This is passed into the
 *      unsupervised learner for the input transform to create the evaluator
 *      that will then produce the {@code TransformedInputType}.
 * @param   <TransformedInputType>
 *      The output type of the input transformer, which will be used as the
 *      input values to train the (middle) supervised learner.
 * @param   <TransformedOutputType>
 *      The input type of the output transformer, which will be used as the
 *      output values to train the (middle) supervised learner. It will be
 *      the output type of the output transformer (and input type of its
 *      reverse).
 * @param   <OutputType>
 *      The output type of the data to learn on. This is passed into the
 *      unsupervised learner for the output transform to create the reversible
 *      data converter for mapping the {@code OutputType} to the
 *      {@code TransformedOutputType}, or vice-versa.
 * @author  Justin Basilico
 * @version 3.4.0
 */
public class InputOutputTransformedBatchLearner<InputType, TransformedInputType, TransformedOutputType, OutputType>
    extends AbstractBatchLearnerContainer<BatchLearner<? super Collection<? extends InputOutputPair<? extends TransformedInputType, TransformedOutputType>>, ? extends Evaluator<? super TransformedInputType, ? extends TransformedOutputType>>>
    implements SupervisedBatchLearner<InputType, OutputType, CompositeEvaluatorTriple<InputType, TransformedInputType, TransformedOutputType, OutputType>>
{

    /** The unsupervised learning algorithm for creating the input
     *  transformation. */
    protected BatchLearner<? super Collection<? extends InputType>, ? extends Evaluator<? super InputType, ? extends TransformedInputType>> inputLearner;

    /** The unsupervised learning algorithm for creating the output
     *  transformation, which must be reversible for evaluation. */
    protected BatchLearner<? super Collection<? extends OutputType>, ? extends ReversibleEvaluator<OutputType, TransformedOutputType, ?>> outputLearner;

    /**
     * Creates a new, empty {@code InputOutputTransformedBatchLearner}.
     */
    public InputOutputTransformedBatchLearner()
    {
        this(null, null, null);
    }

    /**
     * Creates a new {@code InputOutputTransformedBatchLearner} with the given
     * learners.
     *
     * @param   inputLearner
     *      The unsupervised learning algorithm for creating the input
     *      transformation.
     * @param   learner
     *      The supervised learner whose input and output are being
     *      adapted.
     * @param   outputLearner
     *      The unsupervised learning algorithm for creating the
     *      output transformation, which must be reversible for evaluation.
     */
    public InputOutputTransformedBatchLearner(
        final BatchLearner<? super Collection<? extends InputType>, ? extends Evaluator<? super InputType, ? extends TransformedInputType>> inputLearner,
        final BatchLearner<? super Collection<? extends InputOutputPair<? extends TransformedInputType, TransformedOutputType>>, ? extends Evaluator<? super TransformedInputType, ? extends TransformedOutputType>> learner,
        final BatchLearner<? super Collection<? extends OutputType>, ? extends ReversibleEvaluator<OutputType, TransformedOutputType, ?>> outputLearner)
    {
        super(learner);

        this.setInputLearner(inputLearner);
        this.setOutputLearner(outputLearner);
    }

    @Override
    public InputOutputTransformedBatchLearner<InputType, TransformedInputType, TransformedOutputType, OutputType> clone()
    {
        @SuppressWarnings("unchecked")
        final InputOutputTransformedBatchLearner<InputType, TransformedInputType, TransformedOutputType, OutputType> clone =
            (InputOutputTransformedBatchLearner<InputType, TransformedInputType, TransformedOutputType, OutputType>)
            super.clone();

        clone.inputLearner = ObjectUtil.cloneSafe(this.inputLearner);
        clone.outputLearner = ObjectUtil.cloneSafe(this.outputLearner);
        
        return clone;
    }

    /**
     * Learn by first calling the input transformation learner on all the
     * input values and the output transformation on the output values. After
     * these are created, the adapted supervised data is constructed by
     * applying the learned input transformation to each input and the learned 
     * output transformation to each output. The third (middle) learner is then
     * trained on the transformed supervised learning problem.
     *
     * @param   data
     *      The training data.
     * @return
     *      The composite evaluator triple that applies the input transform,
     *      the learned function, and then the output transform.
     */
    @Override
    public CompositeEvaluatorTriple<InputType, TransformedInputType, TransformedOutputType, OutputType> learn(
        final Collection<? extends InputOutputPair<? extends InputType, OutputType>> data)
    {
        // First learn the input transformer.
        final List<InputType> originalInputs = DatasetUtil.inputsList(data);
        final Evaluator<? super InputType, ? extends TransformedInputType> inputTransformer =
            this.inputLearner.learn(originalInputs);

        // Now learn the output transformer and get its reverse.
        final List<OutputType> originalOutputs = DatasetUtil.outputsList(data);
        final ReversibleEvaluator<OutputType, TransformedOutputType, ?> outputTransformer =
            this.outputLearner.learn(originalOutputs);
        final Evaluator<? super TransformedOutputType, ? extends OutputType> outputReverseTransfomer =
            outputTransformer.reverse();

        // Now transform all the data.
        final ArrayList<InputOutputPair<TransformedInputType, TransformedOutputType>> transformedData =
            new ArrayList<InputOutputPair<TransformedInputType, TransformedOutputType>>(data.size());
        for (InputOutputPair<? extends InputType, OutputType> originalExample : data)
        {
            final TransformedInputType transformedInput = inputTransformer.evaluate(
                originalExample.getInput());
            final TransformedOutputType transformedOutput = outputTransformer.evaluate(
                originalExample.getOutput());

            // Create the transformed input-output pair.
// TODO: We could put in a factory for input-output pairs to special-case this
// type of transform.
            if (originalExample instanceof WeightedInputOutputPair<?, ?>)
            {
                // Handle weighted examples.
                transformedData.add(DefaultWeightedInputOutputPair.create(
                    transformedInput, transformedOutput,
                    DatasetUtil.getWeight(originalExample)));
            }
            else
            {
                // Unweighted data.
                transformedData.add(DefaultInputOutputPair.create(
                    transformedInput, transformedOutput));
            }
        }

        // Finally, learn the real learner.
        final Evaluator<? super TransformedInputType, ? extends TransformedOutputType> learned =
            this.learner.learn(transformedData);

        return CompositeEvaluatorTriple.create(
            inputTransformer, learned, outputReverseTransfomer);
    }

    /**
     * Gets the unsupervised learning algorithm for creating the input
     * transformation.
     *
     * @return
     *      The input transformation learner.
     */
    public BatchLearner<? super Collection<? extends InputType>, ? extends Evaluator<? super InputType, ? extends TransformedInputType>> getInputLearner()
    {
        return this.inputLearner;
    }

    /**
     * Sets the unsupervised learning algorithm for creating the input
     * transformation.
     *
     * @param   inputLearner
     *      The input transformation learner.
     */
    public void setInputLearner(
        final BatchLearner<? super Collection<? extends InputType>, ? extends Evaluator<? super InputType, ? extends TransformedInputType>> inputLearner)
    {
        this.inputLearner = inputLearner;
    }

    /**
     * Gets the unsupervised learning algorithm for creating the
     * output transformation, which must be reversible for evaluation.
     *
     * @return
     *      The output transformation learner.
     */
    public BatchLearner<? super Collection<? extends OutputType>, ? extends ReversibleEvaluator<OutputType, TransformedOutputType, ?>> getOutputLearner()
    {
        return this.outputLearner;
    }

    /**
     * Gets the unsupervised learning algorithm for creating the
     * output transformation, which must be reversible for evaluation.
     *
     * @param   outputLearner
     *      The output transformation learner.
     */
    public void setOutputLearner(
        final BatchLearner<? super Collection<? extends OutputType>, ? extends ReversibleEvaluator<OutputType, TransformedOutputType, ?>> outputLearner)
    {
        this.outputLearner = outputLearner;
    }

    /**
     * Creates a new {@code InputOutputTransformedBatchLearner} from the
     * three learners.
     *
     * @param   <InputType>
     *      The input type of the data to learn on. This is passed into the
     *      unsupervised learner for the input transform to create the evaluator
     *      that will then produce the {@code TransformedInputType}.
     * @param   <TransformedInputType>
     *      The output type of the input transformer, which will be used as the
     *      input values to train the (middle) supervised learner.
     * @param   <TransformedOutputType>
     *      The input type of the output transformer, which will be used as the
     *      output values to train the (middle) supervised learner. It will be
     *      the output type of the output transformer.
     * @param   <OutputType>
     *      The output type of the data to learn on. This is passed into the
     *      unsupervised learner for the output transform to create the reversible
     *      data converter for mapping the {@code OutputType} to the
     *      {@code TransformedOutputType} to the, or vice-versa.
     * @param   inputLearner
     *      The unsupervised learning algorithm for creating the input
     *      transformation.
     * @param   learner
     *      The supervised learner whose input and output are being
     *      adapted.
     * @param   outputLearner
     *      The unsupervised learning algorithm for creating the output
     *      transformation, which must be reversible for evaluation.
     * @return
     *      A new input-output transformed batch learner.
     */
    public static <InputType, TransformedInputType, TransformedOutputType, OutputType> InputOutputTransformedBatchLearner<InputType, TransformedInputType, TransformedOutputType, OutputType> create(
        final BatchLearner<? super Collection<? extends InputType>, ? extends Evaluator<? super InputType, ? extends TransformedInputType>> inputLearner,
        final BatchLearner<? super Collection<? extends InputOutputPair<? extends TransformedInputType, TransformedOutputType>>, ? extends Evaluator<? super TransformedInputType, ? extends TransformedOutputType>> learner,
        final BatchLearner<? super Collection<? extends OutputType>, ? extends ReversibleEvaluator<OutputType, TransformedOutputType, ?>> outputLearner)
    {
        return new InputOutputTransformedBatchLearner<InputType, TransformedInputType, TransformedOutputType, OutputType>(
            inputLearner, learner, outputLearner);
    }

    /**
     * Creates a new {@code InputOutputTransformedBatchLearner} from the
     * input and supervised learners, performing no transformation on the
     * output type.
     *
     * @param   <InputType>
     *      The input type of the data to learn on. This is passed into the
     *      unsupervised learner for the input transform to create the evaluator
     *      that will then produce the {@code TransformedInputType}.
     * @param   <TransformedInputType>
     *      The output type of the input transformer, which will be used as the
     *      input values to train the (middle) supervised learner.
     * @param   <OutputType>
     *      The output type of the data to learn on. It will be used as the
     *      output values to train the (middle) supervised learner.
     * @param   inputLearner
     *      The unsupervised learning algorithm for creating the input
     *      transformation.
     * @param   learner
     *      The supervised learner whose input is being adapted.
     * @return
     *      A new input transformed batch learner.
     */
    public static <InputType, TransformedInputType, OutputType> InputOutputTransformedBatchLearner<InputType, TransformedInputType, OutputType, OutputType> createInputTransformed(
        final BatchLearner<? super Collection<? extends InputType>, ? extends Evaluator<? super InputType, ? extends TransformedInputType>> inputLearner,
        final BatchLearner<? super Collection<? extends InputOutputPair<? extends TransformedInputType, OutputType>>, ? extends Evaluator<? super TransformedInputType, ? extends OutputType>> learner)
    {
        return create(inputLearner, learner,
            new IdentityEvaluator<OutputType>());
    }

    /**
     * Creates a new {@code InputOutputTransformedBatchLearner} from the
     * supervised and output learners, performing no transformation on the
     * input type.
     *
     * @param   <InputType>
     *      The input type of the data to learn on. It will be used as the
     *      input values to train the (middle) supervised learner.
     * @param   <TransformedOutputType>
     *      The input type of the output transformer, which will be used as the
     *      output values to train the (middle) supervised learner. It will be
     *      the output type of of the output transformer.
     * @param   <OutputType>
     *      The output type of the data to learn on. This is passed into the
     *      unsupervised learner for the output transform to create the reversible
     *      data converter for mapping the {@code OutputType} to the
     *      {@code TransformedOutputType} to the, or vice-versa.
     * @param   learner
     *      The supervised learner whose output is being adapted.
     * @param   outputLearner
     *      The unsupervised learning algorithm for creating the output
     *      transformation, which must be reversible for evaluation.
     * @return
     *      A new output transformed batch learner.
     */
    public static <InputType, TransformedOutputType, OutputType> InputOutputTransformedBatchLearner<InputType, InputType, TransformedOutputType, OutputType> createOutputTransformed(
        final BatchLearner<? super Collection<? extends InputOutputPair<? extends InputType, TransformedOutputType>>, ? extends Evaluator<? super InputType, ? extends TransformedOutputType>> learner,
        final BatchLearner<? super Collection<? extends OutputType>, ? extends ReversibleEvaluator<OutputType, TransformedOutputType, ?>> outputLearner)
    {
        return create(new IdentityEvaluator<InputType>(),
            learner, outputLearner);
    }

    /**
     * Creates a new {@code InputOutputTransformedBatchLearner} from the
     * predefined input and output transforms and the supervised learner.
     *
     * @param   <InputType>
     *      The input type of the data to learn on. This is passed into the
     *      input transform to produce the {@code TransformedInputType}.
     * @param   <TransformedInputType>
     *      The output type of the input transformer, which will be used as the
     *      input values to train the (middle) supervised learner.
     * @param   <TransformedOutputType>
     *      The input type of the output transformer, which will be used as the
     *      output values to train the (middle) supervised learner. It will be
     *      the output type of the output transformer.
     * @param   <OutputType>
     *      The output type of the data to learn on. This is passed into the
     *      reversible output data converter for mapping the {@code OutputType} 
     *      to the {@code TransformedOutputType} to the, or vice-versa.
     * @param   inputTransform
     *      The predefined input transformation.
     * @param   learner
     *      The supervised learner whose input and output are being
     *      adapted.
     * @param   outputTransform
     *      The predefined output transformation.
     * @return
     *      A new input-output transformed batch learner.
     */
    public static <InputType, TransformedInputType, TransformedOutputType, OutputType> InputOutputTransformedBatchLearner<InputType, TransformedInputType, TransformedOutputType, OutputType> create(
        final Evaluator<? super InputType, ? extends TransformedInputType> inputTransform,
        final BatchLearner<? super Collection<? extends InputOutputPair<? extends TransformedInputType, TransformedOutputType>>, ? extends Evaluator<? super TransformedInputType, ? extends TransformedOutputType>> learner,
        final ReversibleEvaluator<OutputType, TransformedOutputType, ?> outputTransform)
    {
        return create(
            ConstantLearner.create(inputTransform), learner,
            ConstantLearner.create(outputTransform));
    }

    /**
     * Creates a new {@code InputOutputTransformedBatchLearner} from the
     * predefined input transform and the supervised learner. It uses no
     * output transformation.
     *
     * @param   <InputType>
     *      The input type of the data to learn on. This is passed into the
     *      input transform to produce the {@code TransformedInputType}.
     * @param   <TransformedInputType>
     *      The output type of the input transformer, which will be used as the
     *      input values to train the (middle) supervised learner.
     * @param   <OutputType>
     *      The output type of the data to learn on. It will be used as the
     *      output values to train the (middle) supervised learner.
     * @param   inputTransform
     *      The predefined input transformation.
     * @param   learner
     *      The supervised learner whose input and output are being
     *      adapted.
     * @return
     *      A new input transformed batch learner.
     */
    public static <InputType, TransformedInputType, OutputType> InputOutputTransformedBatchLearner<InputType, TransformedInputType, OutputType, OutputType> createInputTransformed(
        final Evaluator<? super InputType, ? extends TransformedInputType> inputTransform,
        final BatchLearner<? super Collection<? extends InputOutputPair<? extends TransformedInputType, OutputType>>, ? extends Evaluator<? super TransformedInputType, ? extends OutputType>> learner)
    {
        return create(inputTransform, learner,
            new IdentityEvaluator<OutputType>());
    }

    /**
     * Creates a new {@code InputOutputTransformedBatchLearner} from the
     * predefined output transforms and the supervised learner. It uses no
     * input transformation.
     *
     * @param   <InputType>
     *      The input type of the data to learn on. It will be used as the
     *      input values to train the (middle) supervised learner.
     * @param   <TransformedOutputType>
     *      The input type of the output transformer, which will be used as the
     *      output values to train the (middle) supervised learner. It will be
     *      the output type of of the output transformer.
     * @param   <OutputType>
     *      The output type of the data to learn on. This is passed into the
     *      reversible output data converter for mapping the {@code OutputType}
     *      to the {@code TransformedOutputType} to the, or vice-versa.
     * @param   learner
     *      The supervised learner whose input and output are being
     *      adapted.
     * @param   outputTransform
     *      The predefined output transformation.
     * @return
     *      A new output transformed batch learner.
     */
    public static <InputType, TransformedOutputType, OutputType> InputOutputTransformedBatchLearner<InputType, InputType, TransformedOutputType, OutputType> createOutputTransformed(
        final BatchLearner<? super Collection<? extends InputOutputPair<? extends InputType, TransformedOutputType>>, ? extends Evaluator<? super InputType, ? extends TransformedOutputType>> learner,
        final ReversibleEvaluator<OutputType, TransformedOutputType, ?> outputTransform)
    {
        return create(new IdentityEvaluator<InputType>(),
            learner, outputTransform);
    }

    /**
     * Creates a new {@code InputOutputTransformedBatchLearner} from the 
     * unsupervised input transform learner, supervised learners, and output
     * transform.
     *
     * @param   <InputType>
     *      The input type of the data to learn on. This is passed into the
     *      unsupervised learner for the input transform to create the evaluator
     *      that will then produce the {@code TransformedInputType}.
     * @param   <TransformedInputType>
     *      The output type of the input transformer, which will be used as the
     *      input values to train the (middle) supervised learner.
     * @param   <TransformedOutputType>
     *      The input type of the output transformer, which will be used as the
     *      output values to train the (middle) supervised learner. It will be
     *      the output type of the output transformer.
     * @param   <OutputType>
     *      The output type of the data to learn on. This is passed into the
     *      reversible output data converter for mapping the {@code OutputType}
     *      to the {@code TransformedOutputType} to the, or vice-versa.
     * @param   inputLearner
     *      The unsupervised learning algorithm for creating the input
     *      transformation.
     * @param   learner
     *      The supervised learner whose input and output are being
     *      adapted.
     * @param   outputTransform
     *      The predefined output transformation.
     * @return
     *      A new input-output transformed batch learner.
     */
    public static <InputType, TransformedInputType, TransformedOutputType, OutputType> InputOutputTransformedBatchLearner<InputType, TransformedInputType, TransformedOutputType, OutputType> create(
        final BatchLearner<? super Collection<? extends InputType>, ? extends Evaluator<? super InputType, ? extends TransformedInputType>> inputLearner,
        final BatchLearner<? super Collection<? extends InputOutputPair<? extends TransformedInputType, TransformedOutputType>>, ? extends Evaluator<? super TransformedInputType, ? extends TransformedOutputType>> learner,
        final ReversibleEvaluator<OutputType, TransformedOutputType, ?> outputTransform)
    {
        return create(inputLearner, learner,
            ConstantLearner.create(outputTransform));
    }

    /**
     * Creates a new {@code InputOutputTransformedBatchLearner} from the
     * input transform, supervised learner, and unsupervised output transform
     * learner.
     *
     * @param   <InputType>
     *      The input type of the data to learn on. This is passed into the
     *      input transform to produce the {@code TransformedInputType}.
     * @param   <TransformedInputType>
     *      The output type of the input transformer, which will be used as the
     *      input values to train the (middle) supervised learner.
     * @param   <TransformedOutputType>
     *      The input type of the output transformer, which will be used as the
     *      output values to train the (middle) supervised learner. It will be
     *      the output type of of the output transformer.
     * @param   <OutputType>
     *      The output type of the data to learn on. This is passed into the
     *      unsupervised learner for the output transform to create the reversible
     *      data converter for mapping the {@code OutputType} to the
     *      {@code TransformedOutputType} to the, or vice-versa.
     * @param   inputTransform
     *      The predefined input transformation.
     * @param   learner
     *      The supervised learner whose input and output are being
     *      adapted.
     * @param   outputLearner
     *      The unsupervised learning algorithm for creating the output
     *      transformation, which must be reversible for evaluation.
     * @return
     *      A new input-output transformed batch learner.
     */
    public static <InputType, TransformedInputType, TransformedOutputType, OutputType> InputOutputTransformedBatchLearner<InputType, TransformedInputType, TransformedOutputType, OutputType> create(
        final Evaluator<? super InputType, ? extends TransformedInputType> inputTransform,
        final BatchLearner<? super Collection<? extends InputOutputPair<? extends TransformedInputType, TransformedOutputType>>, ? extends Evaluator<? super TransformedInputType, ? extends TransformedOutputType>> learner,
        final BatchLearner<? super Collection<? extends OutputType>, ? extends ReversibleEvaluator<OutputType, TransformedOutputType, ?>> outputLearner)
    {
        return create(ConstantLearner.create(inputTransform),
            learner, outputLearner);
    }

}
