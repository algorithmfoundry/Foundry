/*
 * File:                MutableSemanticMemoryLiteFactory.java
 * Authors:             Justin Basilico and Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Framework Lite
 *
 * Copyright February 24, 2006, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 *
 */

package gov.sandia.cognition.framework.lite;

import gov.sandia.cognition.framework.CognitiveModel;
import gov.sandia.cognition.framework.CognitiveModule;
import gov.sandia.cognition.framework.CognitiveModuleFactory;

/**
 * The MutableSemanticMemoryLiteFactory implements a CognitiveModuleFactory
 * for MutableSemanticMemoryLite modules.
 *
 * @author Justin Basilico
 * @author Kevin R. Dixon
 * @since  1.0
 */
public class MutableSemanticMemoryLiteFactory
    extends java.lang.Object
    implements CognitiveModuleFactory
{
    /** The module settings. */
    private MutablePatternRecognizerLite recognizer = null;
    
    /**
     * Creates a new instance of MutableSemanticMemoryLiteFactory.
     *
     * @param recognizer The pattern recognizer to use.
     */
    public MutableSemanticMemoryLiteFactory(
        MutablePatternRecognizerLite recognizer)
    {
        super();
        
        this.setRecognizer(recognizer);
    }
    
    /**
     * Creates a new MutableSemanticMemoryLite module for the given model.
     * 
     * @param model The model to create the module for.
     * @return The new MutableSemanticMemoryLite module.
     */
    public MutableSemanticMemoryLite createModule(
        CognitiveModel model)
    {
        return new MutableSemanticMemoryLite(
            model.getSemanticIdentifierMap(), this.getRecognizer());
    }

    /**
     * Gets the settings of the factory.
     *
     * @return The settings for the module.
     */
    public MutablePatternRecognizerLite getSettings()
    {
        return this.recognizer;
    }
    
    /**
     * Gets the recognizer of the factory.
     *
     * @return The recognizer for the module.
     */
    public MutablePatternRecognizerLite getRecognizer()
    {
        return this.recognizer;
    }

    /**
     * Sets the settings used by the factory.
     *
     * @param recognizer The new settings for the factory to use.
     */
    public void setRecognizer(
        MutablePatternRecognizerLite recognizer)
    {
        if ( recognizer == null )
        {
            throw new NullPointerException("The recognizer cannot be null.");
        }
        
        this.recognizer = recognizer;
    }
}