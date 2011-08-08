/*
 * File:                SimplePatternRecognizerState.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Framework Lite
 *
 * Copyright May 19, 2006, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 *
 */

package gov.sandia.cognition.framework.lite;

import gov.sandia.cognition.framework.CognitiveModuleState;
import gov.sandia.cognition.framework.SemanticLabel;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.util.AbstractCloneableSerializable;
import gov.sandia.cognition.util.ObjectUtil;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * The <code>SimplePatternRecognizerState</code> class implements a
 * <code>CognitiveModuleState</code> for the 
 * <code>SimplePatternRecognizer</code>. It stores a state vector along with
 * the labels for the dimensions of the vector. The labels are stored as a
 * copy of the labels on the module so that if the module has labels added or
 * removed the state's copy will not be changed. This allows the module to map
 * the state activations properly even when the module has changed.
 *
 * @author Justin Basilico
 * @since 1.0
 */
public class SimplePatternRecognizerState
    extends AbstractCloneableSerializable
    implements CognitiveModuleState
{
    /**
     * The array of semantic labels that the state vector has. This is stored
     * as a private copy in the state object because the module can have
     * labels added or removed. If that happens then the module will need to
     * update the state based on the new labels.
     */
    private ArrayList<SemanticLabel> labels = null;
    
    /** The state vector. */
    private Vector stateVector = null;
    
    /**
     * Creates a new instance of SimplePatternRecognizerState.
     *
     * @param  labels The labels of the elements of the state vector.
     */
    public SimplePatternRecognizerState(
        Collection<SemanticLabel> labels)
    {
        this(labels, VectorFactory.getDefault().createVector(labels.size()));
    }
    
    /**
     * Creats a new instance of SimplePatternRecognizerState.
     *
     * @param  labels The labels of the elements of the state vector.
     * @param  stateVector The state vector.
     */
    public SimplePatternRecognizerState(
        Collection<SemanticLabel> labels,
        Vector stateVector)
    {
        this(labels, stateVector, true);
    }

    /**
     * Creats a new instance of SimplePatternRecognizerState.
     *
     * @param  labels The labels of the elements of the state vector.
     * @param  stateVector The state vector.
     * @param  copyState True to make a copy of the state, false to just use
     *         the given object as a reference.
     */
    public SimplePatternRecognizerState(
        Collection<SemanticLabel> labels,
        Vector stateVector,
        boolean copyState)
    {
        super();
        
        this.setLabels(labels);
        
        if ( copyState )
        {
            this.setStateVector(stateVector.clone());
        }
        else
        {
            this.setStateVector(stateVector);
        }
    }

    /**
     * Creats a new instance of SimplePatternRecognizerState.
     *
     * @param  other The SimplePatternRecognizerState to copy.
     */
    public SimplePatternRecognizerState(
        SimplePatternRecognizerState other)
    {
        this(other.labels, other.stateVector, true);
    }
    
    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    @Override
    public SimplePatternRecognizerState clone()
    {
        final SimplePatternRecognizerState clone = 
            (SimplePatternRecognizerState) super.clone();
        clone.labels = new ArrayList<SemanticLabel>(this.labels);
        clone.stateVector = ObjectUtil.cloneSmart(this.stateVector);
        return clone;
    }

    /**
     * Gets the labels corresponding to the dimensions of the state.
     *
     * @return The list of labels used by the state.
     */
    public List<SemanticLabel> getLabels()
    {
        return this.labels;
    }
    
    /**
     * Gets the state vector stored in the object, which contains the main
     * state data of the recognizer.
     *
     * @return The state vector.
     */
    public Vector getStateVector()
    {
        return this.stateVector;
    }

    /**
     * Sets the labels for the state to keep as the labels of the dimensions
     * of the state. It makes its own copy of the labels so that the given
     * object can change but the state will remain the same.
     *
     * @param  labels The labels for the dimensions of the state vector.
     */
    public void setLabels(
        Collection<SemanticLabel> labels)
    {
        this.labels = new ArrayList<SemanticLabel>(labels);
    }

    /**
     * Sets the state vector stored in the state object. It is not copied.
     *
     * @param  stateVector The state vector to store in the state object.
     */
    public void setStateVector(
        Vector stateVector)
    {
        this.stateVector = stateVector;
    }
}

