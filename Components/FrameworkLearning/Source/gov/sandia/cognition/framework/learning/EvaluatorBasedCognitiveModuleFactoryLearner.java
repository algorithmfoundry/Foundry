/*
 * File:                CognitiveModuleFactoryEvaluatorLearner.java
 * Authors:             Justin Basilico and Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright June 21, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.framework.learning;

import gov.sandia.cognition.framework.learning.converter.CogxelConverter;
import gov.sandia.cognition.framework.CognitiveModel;
import gov.sandia.cognition.framework.CognitiveModelInput;
import gov.sandia.cognition.framework.CognitiveModelState;
import gov.sandia.cognition.learning.algorithm.BatchLearner;
import gov.sandia.cognition.util.CloneableSerializable;
import gov.sandia.cognition.evaluator.Evaluator;
import gov.sandia.cognition.util.ObjectUtil;
import java.util.Collection;
import java.util.LinkedList;

/**
 * The EvaluatorBasedCognitiveModuleFactoryLearner class implements a
 * CognitiveModuleFactoryLearner for the EvaluatorBasedCognitiveModuleFactory.
 * It can be used to adapt learning algorithms that learn Evaluator objects
 * to the Cognitive Framework.
 *
 * @param <InputType> Input type of the embedded Evaluator.
 * @param <OutputType> Output type of the embedded Evaluator.
 * @param <LearningDataType> Data type used to create the training set,
 *  for example "Vector" or "InputOutputPair<Vector,Vector>", etc.
 * @author Justin Basilico
 * @author Kevin R. Dixon
 * @since  2.0
 */
public class EvaluatorBasedCognitiveModuleFactoryLearner<InputType, OutputType, 
        LearningDataType>
    extends Object
    implements CognitiveModuleFactoryLearner, CloneableSerializable
{
    /**
     * The learner to use to learn the evaluator
     */
    private BatchLearner
        <? super Collection<LearningDataType>,
         ? extends Evaluator<? super InputType, ? extends OutputType>>
        learner;
    
    /**
     * The CogxelConverter used to convert from a CogxelState to InputType
     */
    private CogxelConverter<InputType> inputConverter;
    
    /**
     * The CogxelConverter used to convert OutputType to a CogxelState.
     */
    private CogxelConverter<OutputType> outputConverter;
    
    /**
     * The CogxelConverter used to convert from a CogxelState to 
     *  LearningDataType.
     */
    private CogxelConverter<LearningDataType> learningDataConverter;
    
    /**
     * Human-readable name for this module
     */
    private String name;
    
    /**
     * Default name for this module
     */
    public static final String DEFAULT_NAME = 
        "Learned Evaluator-based Cognitive Module";
    
    /**
     * Creates a new instance of CognitiveModuleFactoryEvaluatorLearner.
     */
    public EvaluatorBasedCognitiveModuleFactoryLearner()
    {
        this( (BatchLearner
                <? super Collection<LearningDataType>,
                 ? extends Evaluator<InputType, OutputType>>) null, 
                 DEFAULT_NAME);
    }
    
    /**
     * Creates a new instance of CognitiveModuleFactoryEvaluatorLearner.
     *
     * @param  learner The learner to use to learn the evaluator.
     * @param name High-level name of the module
     */
    public EvaluatorBasedCognitiveModuleFactoryLearner(
        BatchLearner
        <? super Collection<LearningDataType>,
         ? extends Evaluator<InputType, OutputType>> learner,
        String name )
    {
        this(learner, name, null, null, null);
    }

    /**
     * Creates a new instance of CognitiveModuleFactoryEvaluatorLearner.
     *
     * @param  learner The learner to use to learn the evaluator.
     * @param name High-level name of the module
     * @param  inputConverter The CogxelConverter used to convert from a
     *         CogxelState to InputType.
     * @param  outputConverter The CogxelConverter used to convert OutputType 
     *         to a CogxelState.
     * @param  learningDataConverter The CogxelConverter used to convert from 
     *         a CogxelState to LearningDataType. 
     */
    public EvaluatorBasedCognitiveModuleFactoryLearner(
        BatchLearner
            <? super Collection<LearningDataType>,
             ? extends Evaluator<? super InputType, ? extends OutputType>> 
            learner,
        String name,
        CogxelConverter<InputType> inputConverter,
        CogxelConverter<OutputType> outputConverter,
        CogxelConverter<LearningDataType> learningDataConverter )
    {
        super();
        
        this.setLearner(learner);
        this.setName( name );
        this.setInputConverter(inputConverter);
        this.setOutputConverter(outputConverter);
        this.setLearningDataConverter(learningDataConverter);
    }
    
    /**
     * Creates a new copy of a of CognitiveModuleFactoryEvaluatorLearner.
     *
     * @param  other The other CognitiveModuleFactoryEvaluatorLearner to copy.
     */
    public EvaluatorBasedCognitiveModuleFactoryLearner(
        EvaluatorBasedCognitiveModuleFactoryLearner<InputType, OutputType, 
            LearningDataType> other)
    {
        this(
            ObjectUtil.cloneSafe(other.getLearner()),
            other.getName(),
            other.getInputConverter().clone(),
            other.getOutputConverter().clone(),
            other.getLearningDataConverter().clone());
    }
    
    /**
     * Creates a copy of this EvaluatorBasedCognitiveModuleFactoryLearner.
     *
     * @return A copy of this EvaluatorBasedCognitiveModuleFactoryLearner.
     */
    @Override
    public EvaluatorBasedCognitiveModuleFactoryLearner<InputType, OutputType, 
            LearningDataType> clone()
    {
        return new EvaluatorBasedCognitiveModuleFactoryLearner<InputType, 
                OutputType,LearningDataType>(this);
    }
    
    /**
     * Learns a new EvaluatorBasedCognitiveModuleFactory<InputType, OutputType>
     * based on the given existing factory plus the given collection of 
     * CognitiveModelInput objects.
     *
     * @param  model The model to learn the module factory for.
     * @param  datasets The datasets to use to learn the module factory.
     * @return The learned EvaluatorBasedCognitiveModuleFactory.
     */
    public EvaluatorBasedCognitiveModuleFactory<InputType, OutputType> learn(
        CognitiveModel model, 
        Collection<? extends Collection<? extends CognitiveModelInput>> datasets)
    {
        // Check the arguments to make sure they are valid.
        if ( model == null )
        {
            throw new IllegalArgumentException("model is null");
        }
        else if ( datasets == null || datasets.size() <= 0 )
        {
            throw new IllegalArgumentException("dataSets is null");
        }
        
        // Check the state to make sure it is valid.
        // Note we don't need an output converter at this point, so
        // we won't check to ensure it's not null (this means that outputs
        // won't be written to CogxelState with null output converter though)
        if ( this.getLearner() == null )
        {
            throw new IllegalStateException("Learner is null");
        }
        else if ( this.getInputConverter() == null )
        {
            throw new IllegalStateException("Input converter is null");
        }
        else if ( this.getLearningDataConverter() == null )
        {
            throw new IllegalStateException("Learning data converter is null");
        }
              
        // Create the CogxelState to LearningDataType converter.
        CogxelConverter<LearningDataType> converter = 
            this.getLearningDataConverter().clone();
        converter.setSemanticIdentifierMap(model.getSemanticIdentifierMap());
        
        // Create the Dataset to hold the learned data.
        Collection<LearningDataType> learningSet = 
            new LinkedList<LearningDataType>();
        
        // Go through the inputs data sequences.
        for ( Collection<? extends CognitiveModelInput> dataset : datasets )
        {
            // Reset the model for the new data sequence.
            model.resetCognitiveState();
            
            // Go through the sequence and give the model each of the inputs.
            for ( CognitiveModelInput input : dataset )
            {
                // Update the model based on the input
                model.update(input);
                
                // Get the model's current state.
                CognitiveModelState state = model.getCurrentState();
                
                // Get the learning example from the Cogxel converter.
                LearningDataType example = 
                    converter.fromCogxels(state.getCogxels());
                
                // Add the example to the learning set.
                learningSet.add(example);
            }
        }
        
        // Perform the learning algorithm.
        Evaluator<? super InputType, ? extends OutputType> learned = 
            this.getLearner().learn(learningSet);
        
        // Create the settings based on the learned Evaluator plus the
        // input and output CogxelConverters that this object was given.
        EvaluatorBasedCognitiveModuleSettings<InputType, OutputType> settings =
            new EvaluatorBasedCognitiveModuleSettings<InputType, OutputType>(
                learned, this.getInputConverter(), this.getOutputConverter());
        
        // 
        return new EvaluatorBasedCognitiveModuleFactory<InputType, OutputType>(
            settings, this.getName() );
    }

    /**
     * Gets the learner used to create the Evaluator for the module.
     *
     * @return The learner used to create the Evaluator for the module.
     */
    public BatchLearner
        <? super Collection<LearningDataType>,
         ? extends Evaluator<? super InputType, ? extends OutputType>> 
        getLearner()
    {
        return this.learner;
    }
    
    /**
     * Sets the learner used to create the Evaluator for the module.
     *
     * @param  learner The learner used to create the Evaluator for the module.
     */
    public void setLearner(
        BatchLearner
        <? super Collection<LearningDataType>,
         ? extends Evaluator<? super InputType, ? extends OutputType>> learner)
    {
        this.learner = learner;
    }

    /**
     * Gets the CogxelConverter used to convert from a CogxelState to InputType.
     *
     * @return The CogxelConverter used to convert from a CogxelState to 
     *         InputType.
     */
    public CogxelConverter<InputType> getInputConverter()
    {
        return inputConverter;
    }

    /**
     * Sets the CogxelConverter used to convert from a CogxelState to InputType.
     *
     * @param  inputConverter The CogxelConverter used to convert from a 
     *         CogxelState to InputType.
     */
    public void setInputConverter(
        CogxelConverter<InputType> inputConverter)
    {
        this.inputConverter = inputConverter;
    }

    /**
     * Gets the CogxelConverter used to convert OutputType to a CogxelState.
     *
     * @return The CogxelConverter used to convert OutputType to a CogxelState.
     */
    public CogxelConverter<OutputType> getOutputConverter()
    {
        return outputConverter;
    }

    /**
     * Sets the CogxelConverter used to convert OutputType to a CogxelState.
     *
     * @param  outputConverter The CogxelConverter used to convert OutputType 
     *         to a CogxelState.
     */
    public void setOutputConverter(
        CogxelConverter<OutputType> outputConverter)
    {
        this.outputConverter = outputConverter;
    }

    /**
     * Gets the CogxelConverter used to convert from a CogxelState to 
     * LearningDataType.
     *
     * @return The CogxelConverter used to convert from a CogxelState to 
     *         LearningDataType.
     */
    public CogxelConverter<LearningDataType> getLearningDataConverter()
    {
        return learningDataConverter;
    }

    /**
     * Sets the CogxelConverter used to convert from a CogxelState to 
     * LearningDataType.
     *
     * @param  learningDataConverter The CogxelConverter used to convert from 
     *         a CogxelState to LearningDataType.
     */
    public void setLearningDataConverter(
        CogxelConverter<LearningDataType> learningDataConverter)
    {
        this.learningDataConverter = learningDataConverter;
    }

    /**
     * Getter for name
     * @return 
     * Human-readable name for this module
     */
    public String getName()
    {
        return this.name;
    }

    /**
     * Setter for name
     * @param name 
     * Human-readable name for this module
     */
    public void setName(
        String name)
    {
        this.name = name;
    }
}
