/*
 * File:                EvaluatorBasedCognitiveModuleSettings.java
 * Authors:             Justin Basilico and Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Framework Lite
 *
 * Copyright June 21, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 *
 */

package gov.sandia.cognition.framework.learning;

import gov.sandia.cognition.framework.learning.converter.CogxelConverter;
import gov.sandia.cognition.framework.CognitiveModuleSettings;
import gov.sandia.cognition.evaluator.Evaluator;
import java.io.Serializable;

/**
 * The EvaluatorBasedCognitiveModuleSettings class implements the settings for
 * the EvaluatorBasedCognitiveModule. It contains the evaluator to be wrapped
 * along with the converters to convert the input Cogxels to the InputType and
 * the OutputType back to Cogxels.
 *
 * @param <InputType> Input type of the embedded Evaluator
 * @param <OutputType> Output type of the embedded Evaluator
 * @author Justin Basilico
 * @author Kevin R. Dixon
 * @since  2.0
 */
public class EvaluatorBasedCognitiveModuleSettings<InputType, OutputType>
    extends Object
    implements CognitiveModuleSettings, Serializable
{
    /** The evaluator to be used by the module. */
    private Evaluator<? super InputType, ? extends OutputType> evaluator;
    
    /** The CogxelConverter used to convert from a CogxelState to InputType. */
    private CogxelConverter<InputType> inputConverter;
    
    /** The CogxelConverter used to convert OutputType to a CogxelState. */
    private CogxelConverter<OutputType> outputConverter;
    
    /**
     * Creates a new instance of EvaluatorBasedCognitiveModuleSettings.
     */
    public EvaluatorBasedCognitiveModuleSettings()
    {
        this(null, null, null);
    }
    
    /**
     * Creates a new instance of EvaluatorBasedCognitiveModuleSettings.
     *
     * @param  evaluator The evaluator to be used by the module.
     * @param  inputConverter The CogxelConverter used to convert from a 
     *         CogxelState to InputType.
     * @param  outputConverter The CogxelConverter used to convert OutputType to 
     *         a CogxelState.
     */
    public EvaluatorBasedCognitiveModuleSettings(
        Evaluator<? super InputType, ? extends OutputType> evaluator,
        CogxelConverter<InputType> inputConverter,
        CogxelConverter<OutputType> outputConverter)
    {
        super();
        
        this.setEvaluator(evaluator);
        this.setInputConverter(inputConverter);
        this.setOutputConverter(outputConverter);
    }
    
    /**
     * Creates a new instance of EvaluatorBasedCognitiveModuleSettings that is
     * a copy of the given EvaluatorBasedCognitiveModuleSettings. This involves
     * cloning the input and output converters and passing a reference to the
     * evaluator to be used.
     *
     * @param  other The other EvaluatorBasedCognitiveModuleSettings to copy.
     */
    public EvaluatorBasedCognitiveModuleSettings(
        EvaluatorBasedCognitiveModuleSettings<InputType, OutputType> other)
    {
        this(other.getEvaluator(),
            other.getInputConverter().clone(),
            other.getOutputConverter().clone());
    }
    
    /**
     * Creates a clone of this EvaluatorBasedCognitiveModuleSettings. For
     * this clone the input converter and output converter are cloned, but
     * the evaluator is passed by reference.
     *
     * @return A clone of this object.
     */
    @Override
    public EvaluatorBasedCognitiveModuleSettings<InputType, OutputType> clone()
    {
        return new EvaluatorBasedCognitiveModuleSettings<InputType, OutputType>(
            this);
    }
    
    /**
     * Gets the evaluator to be used by the module.
     *
     * @return The evaluator to be used by the module.
     */
    public Evaluator<? super InputType, ? extends OutputType> getEvaluator()
    {
        return this.evaluator;
    }

    /**
     * Sets the evaluator to be used by the module.
     *
     * @param  evaluator The evaluator to be used by the module.
     */
    public void setEvaluator(
        Evaluator<? super InputType, ? extends OutputType> evaluator)
    {
        this.evaluator = evaluator;
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
     * @param  outputConverter The CogxelConverter used to convert OutputType to
     *         a CogxelState.
     */
    public void setOutputConverter(
        CogxelConverter<OutputType> outputConverter)
    {
        this.outputConverter = outputConverter;
    }
}
