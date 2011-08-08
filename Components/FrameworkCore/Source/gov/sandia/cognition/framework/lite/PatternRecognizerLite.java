/*
 * File:                PatternRecognizerLite.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Framework Lite
 *
 * Copyright February 27, 2006, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 *
 */

package gov.sandia.cognition.framework.lite;

import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.framework.CognitiveModuleSettings;
import gov.sandia.cognition.framework.CognitiveModuleState;
import gov.sandia.cognition.framework.SemanticLabel;
import gov.sandia.cognition.framework.SemanticNetwork;
import java.util.Collection;

/**
 * The PatternRecognizerLite interface defines the functionality needed by a
 * pattern recognizer that is to be used by a SemanticMemoryLite.
 *
 * @author Justin Basilico
 * @author Kevin R. Dixon
 * @since  1.0
 */
public interface PatternRecognizerLite
    extends CognitiveModuleSettings
{
    /**
     * Creates a deep copy of the pattern recognizer.
     *
     * @return A deep copy of this PatternRecognizerLite.
     */
    public PatternRecognizerLite clone();
    
    /**
     * Creates a new initial state for the recognizer.
     *
     * @return A new initial state for the recognizer.
     */
    public CognitiveModuleState initialState();
    
    /**
     * Computes the recognition. It operates on the state by side-effect and
     * uses the given set of inputs and sets the outputs.
     *
     * @param state The previous state of the module.
     * @param inputs The vector of inputs to the recognizer.
     * @return The output vector
     */
    public Vector recognize(
        CognitiveModuleState state,
        Vector inputs);
    
    /**
     * Takes a SemanticLabel and returns true if the PatternRecognizer
     * uses it.
     *
     * @param  label The label to determine if the recognizer uses.
     * @return True if the label is used by the recognizer and false if
     *         not or if the label is not valid.
     */
    public boolean isLabel(
        SemanticLabel label);
    
    /**
     * Takes a SemanticLabel and returns true if it is a label used to
     * provide input to the PatternRecognizerLite.
     *
     * @param  label The label to determine if it is an input label.
     * @return True if the label is an input label and false otherwise.
     */
    public boolean isInputLabel(
        SemanticLabel label);
    
    /**
     * Takes a SemanticLabel and returns true if it is a label used as
     * output from the PatternRecognizerLite.
     *
     * @param  label The label to determine if it is an output label.
     * @return True if the label is an output label and false otherwise.
     */
    public boolean isOutputLabel(
        SemanticLabel label);
    
    /**
     * Creates an empty vector to use for input.
     *
     * @return An empty input vector.
     */
    public Vector createEmptyInputVector();
    
    /**
     * Gets the dimensionality of the input vector to the pattern recognizer.
     *
     * @return The dimensionality of the input vector of the pattern 
     *         recognizer.
     */
    public int getInputDimensionality();
    
    /**
     * Gets the dimensionality of the output vector to the pattern recognizer.
     *
     * @return The dimensionality of the output vector of the pattern 
     *         recognizer.
     */
    public int getOutputDimensionality();
    
    /**
     * Gets the collection of all the labels used in the recognizer.
     *
     * @return The collection of all labels used in the recognizer.
     */
    public Collection<SemanticLabel> getAllLabels();
    
    /**
     * Gets the labels for the dimensions of the input vector.
     *
     * @return The labels for the dimensions of the input vector.
     */
    public Collection<SemanticLabel> getInputLabels();
    
    
    /**
     * Gets the labels for the dimensions of the output vector.
     *
     * @return The labels for the dimensions of the output vector.
     */
    public Collection<SemanticLabel> getOutputLabels();
    
    /**
     * Gets the SemanticNetwork describing the structure of the pattern
     * recognizer.
     *
     * @return The recognizer's SemanticNetwork
     */
    public SemanticNetwork getNetwork();
}
