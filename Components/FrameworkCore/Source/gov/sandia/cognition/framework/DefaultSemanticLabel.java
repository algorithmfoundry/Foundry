/*
 * File:                DefaultSemanticLabel.java
 * Authors:             Justin Basilico and Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Framework Lite
 *
 * Copyright February 10, 2006, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 *
 */

package gov.sandia.cognition.framework;

import java.io.Serializable;

/**
 * This class implements a semantic label using a string.
 *
 * @author Justin Basilico
 * @author Kevin R. Dixon
 * @since  1.0
 */
public class DefaultSemanticLabel
    extends java.lang.Object
    implements SemanticLabel, Comparable<DefaultSemanticLabel>, Serializable
{
    /** The String label. */
    private String label = null;
    
    /**
     * Creates a new DefaultSemanticLabel.
     *
     * @param label The String for the label.
     */
    public DefaultSemanticLabel(
        String label)
    {
        super();
        
        this.setLabel(label);
    }
    
    /**
     * The name of the label.
     *
     * @return The name of the label.
     */
    @Override
    public String toString()
    {
        return this.getName();
    }
    
    /**
     * Gets the name of the label, which is its String.
     *
     * @return The name of the label
     */
    public String getName()
    {
        return this.getLabel();
    }

    /**
     * Computes the hash-code for the label, which is just the hash-code for
     * its String.
     *
     * @return The hash-code for the label
     */
    @Override
    public int hashCode()
    {
        return this.getLabel().hashCode();
    }
    
    /**
     * {@inheritDoc}
     *
     * @param other {@inheritDoc}
     * @return {@inheritDoc}
     */
    public @Override boolean equals(
        Object other)
    {
        if ( other instanceof DefaultSemanticLabel )
        {
            // The object is of the same type so use the specialized function
            // to determine equality.
            return this.equals((DefaultSemanticLabel) other);
        }
        else
        {
            // The object is of another type so it cannot be equal.
            return false;
        }
    }
    
    /**
     * Determines is this DefaultSemanticLabel is equal to the given one.
     *
     * @param other The other DefaultSemanticLabel to compare to
     * @return True if the labels are equal and false otherwise
     */
    public boolean equals(
        DefaultSemanticLabel other)
    {
        if ( other == null )
        {
            // An object is never equal to false.
            return false;
        }
        else
        {
            // Compare the labels.
            return this.getLabel().equals(other.getLabel());
        }
    }

    /**
     * Takes a label and compares that label to this one.
     *
     * @param other The label to compare with.
     * @return The comparison.
     */
    public int compareTo(
        DefaultSemanticLabel other)
    {
        // Compare this label to the other label.
        return this.getLabel().compareTo(other.getLabel());
    }

    /**
     * Sets the value of the label.
     *
     * @param label The new label value.  Cannot be null.
     */
    // Note: This setter is private because it should only be called once,
    // from the concert.
    private void setLabel(
        String label)
    {
        if ( label == null )
        {
            // Error: Bad label.
            throw new NullPointerException("The label cannot be null");
        }
        
        this.label = label;
    }

    /**
     * Getter for label.
     *
     * @return The String label.
     */
    public String getLabel()
    {
        return this.label;
    }
}
