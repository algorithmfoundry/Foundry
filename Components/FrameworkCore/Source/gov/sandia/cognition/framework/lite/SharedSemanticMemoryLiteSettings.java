/*
 * File:                SharedSemanticMemoryLiteSettings.java
 * Authors:             Kevin R. Dixon
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

import gov.sandia.cognition.framework.ShareableCognitiveModuleSettings;
import gov.sandia.cognition.util.AbstractCloneableSerializable;
import gov.sandia.cognition.util.ObjectUtil;

/**
 * The SharedSemanticMemoryLiteSettings class implements the settings for 
 * the SharedSemanticMemoryLite module. The settings work by keeping its own 
 * copy of a pattern recognizer so that the shared settings can be given to
 * multiple SharedSemanticMemoryLite modules that are in different models. The
 * copy is kept to ensure that the pattern recognizer behind the settings is 
 * not modified after the shared settings are created.
 *
 * @author Justin Basilico
 * @author Kevin R. Dixon
 * @since  1.0
 */
public class SharedSemanticMemoryLiteSettings
    extends AbstractCloneableSerializable
    implements ShareableCognitiveModuleSettings
{
    /** The recognizer to use in the memory. */
    private PatternRecognizerLite recognizer = null;
    
    /**
     * Creates a new instance of SharedSemanticMemoryLiteSettings. It creates
     * its own copy of the given PatternRecognizerLite.
     *
     * @param recognizer The PatternRecognizerLite for the memory to use. It
     *                   will be copied.
     */
    public SharedSemanticMemoryLiteSettings(
        final PatternRecognizerLite recognizer)
    {
        super();
        
        this.setRecognizer(recognizer.clone());
    }
    
    
    /**
     * {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public SharedSemanticMemoryLiteSettings clone()
    {
        final SharedSemanticMemoryLiteSettings clone =
            (SharedSemanticMemoryLiteSettings) super.clone();

        clone.recognizer = ObjectUtil.cloneSafe(this.recognizer);

        return clone;
    }
    
    
    /**
     * Gets the pattern recognizer to use.
     *
     * @return The PatternRecognizerLite to use.
     */
    public PatternRecognizerLite getRecognizer()
    {
        return this.recognizer;
    }

    /**
     * Sets the recognizer internal to the settings.
     *
     * @param recognizer The recognizer to use.
     */
    // Note: This setter is private because it should only be called once
    // (from the constructor) and because it is used to store a private copy
    // of the recognizer.
    private void setRecognizer(
        PatternRecognizerLite recognizer)
    {
        if ( recognizer == null )
        {
            // Error: The recognizer cannot be null.
            throw new NullPointerException("The recognizer cannot be null.");
        }
        
        this.recognizer = recognizer;
    }
}
