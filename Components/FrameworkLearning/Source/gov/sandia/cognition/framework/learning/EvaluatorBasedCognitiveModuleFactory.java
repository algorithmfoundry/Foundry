/*
 * File:                EvaluatorBasedCognitiveModuleFactory.java
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
 *
 */

package gov.sandia.cognition.framework.learning;

import gov.sandia.cognition.framework.CognitiveModel;
import gov.sandia.cognition.framework.CognitiveModule;
import gov.sandia.cognition.framework.CognitiveModuleFactory;
import gov.sandia.cognition.util.CloneableSerializable;
import gov.sandia.cognition.evaluator.StatefulEvaluator;

/**
 * The EvaluatorBasedCognitiveModuleFactory class implements a factory for the
 * EvaluatorBasedCognitiveModule. When a module is created the factory checks
 * to see if the underlying evaluator is a StatefulEvaluator to determine if
 * a normal EvaluatorBasedCognitiveModule should be created (non-stateful) or
 * a StatefulEvaluatorBasedCognitiveModule should be created (stateful).
 *
 * @param <InputType> Input type of the embedded Evaluator
 * @param <OutputType> Output type of the embedded Evaluator
 * @author Justin Basilico
 * @author Kevin R. Dixon
 * @since  2.0
 */
public class EvaluatorBasedCognitiveModuleFactory<InputType, OutputType>
    extends Object
    implements CognitiveModuleFactory, CloneableSerializable
{
    /** The settings for the module. */
    private EvaluatorBasedCognitiveModuleSettings<InputType, OutputType> 
        settings;
        
    /**
     * Human-readable name of the module
     */
    private String name;
    
    /**
     * Creates a new instance of EvaluatorBasedCognitiveModuleFactory.
     */
    public EvaluatorBasedCognitiveModuleFactory()
    {
        this(
            new EvaluatorBasedCognitiveModuleSettings<InputType, OutputType>(),
             EvaluatorBasedCognitiveModule.DEFAULT_NAME );
    }
    
    /**
     * Creates a new instance of EvaluatorBasedCognitiveModuleFactory.
     * 
     * 
     * @param name 
     * Human-readable name of the module
     * @param settings The settings for the module.
     */
    public EvaluatorBasedCognitiveModuleFactory(
        EvaluatorBasedCognitiveModuleSettings<InputType, OutputType> settings,
        String name )
    {
        super();
        
        this.setSettings(settings);
        this.setName( name );
    }
    
    /**
     * Creates a new copy of a EvaluatorBasedCognitiveModuleFactory.
     *
     * @param  other The other EvaluatorBasedCognitiveModuleFactory to copy.
     */
    public EvaluatorBasedCognitiveModuleFactory(
        EvaluatorBasedCognitiveModuleFactory<InputType, OutputType> other)
    {
        this( other.getSettings().clone(), other.getName() );
    }
    
    /**
     * Creates a clone of this EvaluatorBasedCognitiveModuleFactory.
     *
     * @return A clone of this object.
     */
    @Override
    public EvaluatorBasedCognitiveModuleFactory<InputType, OutputType> clone()
    {
        return new EvaluatorBasedCognitiveModuleFactory<InputType, OutputType>(
            this);
    }
    
    /**
     * {@inheritDoc}
     *
     * @param  model {@inheritDoc}
     * @return {@inheritDoc}
     */
    public CognitiveModule createModule(
        CognitiveModel model)
    {
        // Validate the state of the factory.
        EvaluatorBasedCognitiveModuleSettings<InputType, OutputType> localSettings =
            this.getSettings();
        if ( localSettings == null )
        {
            throw new IllegalStateException("settings is null");
        }
        else if ( localSettings.getEvaluator() == null )
        {
            throw new IllegalStateException("settings evaluator is null");
        }
        
        if ( localSettings.getEvaluator() instanceof StatefulEvaluator )
        {
            // This is a stateful Evaluator so we have to put it in the proper
            // cognitive module class.
            return new StatefulEvaluatorBasedCognitiveModule<InputType, 
                    OutputType>(model, this.getSettings(), this.getName() );
        }
        else
        {
            // This a standard Evaluator with no state.
            return new EvaluatorBasedCognitiveModule<InputType, OutputType>(
                model, this.getSettings(), this.getName() );
        }
    }
    
    /**
     * Gets the settings of the module created by the factory.
     *
     * @return The settings of the module created by the factory.
     */
    public EvaluatorBasedCognitiveModuleSettings<InputType, OutputType> 
        getSettings()
    {
        return settings;
    }
    
    /**
     * Sets the settings of the module created by the factory.
     *
     * @param  settings The settings of the module created by the factory.
     */
    public void setSettings(
        EvaluatorBasedCognitiveModuleSettings<InputType, OutputType> settings)
    {
        this.settings = settings;
    }

    /**
     * Getter for name
     * @return 
     * Human-readable name of the module
     */
    public String getName()
    {
        return this.name;
    }

    /**
     * Setter for name
     * @param name 
     * Human-readable name of the module
     */
    public void setName(
        String name)
    {
        this.name = name;
    }
}
