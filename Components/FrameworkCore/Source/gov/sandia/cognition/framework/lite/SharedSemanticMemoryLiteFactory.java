/*
 * File:                SharedSemanticMemoryLiteFactory.java
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
import gov.sandia.cognition.framework.CognitiveModuleFactory;
import gov.sandia.cognition.util.AbstractCloneableSerializable;
import gov.sandia.cognition.util.ObjectUtil;

/**
 * The SharedSemanticMemoryLiteFactory implements a CognitiveModuleFactory
 * for SharedSemanticMemoryLite modules.
 *
 * @author Justin Basilico
 * @author Kevin R. Dixon
 * @since  1.0
 */
public class SharedSemanticMemoryLiteFactory
    extends AbstractCloneableSerializable
    implements CognitiveModuleFactory
{
    /** The shared settings between the modules. */
    private SharedSemanticMemoryLiteSettings settings = null;
    
    /**
     * Creates a new instance of SharedSemanticMemoryLiteFactory.
     *
     * @param recognizer The pattern recognizer to use.
     */
    public SharedSemanticMemoryLiteFactory(
        PatternRecognizerLite recognizer)
    {
        super();
        
        this.setSettings(new SharedSemanticMemoryLiteSettings(recognizer));
    }
    
    /**
     * {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public SharedSemanticMemoryLiteFactory clone()
    {
        final SharedSemanticMemoryLiteFactory clone = 
            (SharedSemanticMemoryLiteFactory) super.clone();
        clone.settings = ObjectUtil.cloneSafe(this.settings);
        return clone;
    }
    
    /**
     * Creates a new SharedSemanticMemoryLite module for the given model.
     * 
     * @param model The model to create the module for.
     * @return The new SharedSemanticMemoryLite module.
     */
    public SharedSemanticMemoryLite createModule(
        CognitiveModel model)
    {
        if ( model == null )
        {
            throw new NullPointerException("The module cannot be null.");
        }
        
        return new SharedSemanticMemoryLite(
            model.getSemanticIdentifierMap(), this.getSettings());
    }

    /**
     * Gets the settings of the factory.
     *
     * @return The settings for the module.
     */
    public SharedSemanticMemoryLiteSettings getSettings()
    {
        return this.settings;
    }

    /**
     * Sets the settings used by the factory.
     *
     * @param settings The new settings for the factory to use.
     */
    // Note: This setter is private because the class needs to keep its own
    // copy of the settings which is created in the constructor. Thus, we 
    // cannot allow anyone else to set it.
    private void setSettings(
        SharedSemanticMemoryLiteSettings settings)
    {
        if ( settings == null )
        {
            // Error: Illegal settings.
            throw new NullPointerException("The settings cannot be null.");
        }
        
        this.settings = settings;
    }
}
