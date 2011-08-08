/*
 * File:                MutablePatternRecognizerLite.java
 * Authors:             Justin Basilico and Kevin R. Dixon
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

import gov.sandia.cognition.framework.SemanticLabel;

/**
 * The MutablePatternRecognizerLite interface extends the PatternRecognizerLite
 * interface to add methods for changing the recognizer dynamically.
 *
 * @author Justin Basilico
 * @author Kevin R. Dixon
 * @since  1.0
 */
public interface MutablePatternRecognizerLite
    extends PatternRecognizerLite
{
    /**
     * Adds a node to the pattern recognizer.
     *
     * @param label The label for the node to add.
     */
    public void addNode(
        SemanticLabel label);
    
    /**
     * Removes a node and all links associated with that node from the pattern 
     * recognizer. The node's entry in the vector is assumed to be removed so
     * that the vector is now one dimension shorter.
     *
     * @param label The label of the node to remove.
     */
    public void removeNode(
        SemanticLabel label);
    
    /**
     * Sets the association between nodes in the recognizer.
     *
     * @param from The label of the node the assocation is from.
     * @param to The label of the node the assocation is to.
     * @param weight The weight of the association.
     */
    public void setAssociation(
        SemanticLabel from,
        SemanticLabel to,
        double weight);
    
    /**
     * Attempts to set whether or not the given label is an input label. It 
     * returns true if the set operation was successful and false otherwise.
     *
     * @param  label The label to set whether or not it is input label.
     * @param  inputLabel Whether or not the label is an input label.
     * @return True if the operation was successful and false if it was not.
     */
    public boolean trySetInputLabel(
        SemanticLabel label,
        boolean inputLabel);
    
    /**
     * Attempts to set whether or not the given label is an output label. It 
     * returns true if the set operation was successful and false otherwise.
     *
     * @param  label The label to set whether or not it is output label.
     * @param  outputLabel Whether or not the label is an output label.
     * @return True if the operation was successful and false if it was not.
     */
    public boolean trySetOutputLabel(
        SemanticLabel label,
        boolean outputLabel);
}
