/*
 * File:                DefaultSemanticIdentifier.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Framework Lite
 *
 * Copyright May 5, 2006, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 *
 */

package gov.sandia.cognition.framework;

/**
 * The <code>DefaultSemanticIdentifier</code> class implements a default 
 * version of the <code>SemanticIdentifier</code> interface that stores the
 * <code>SemanticLabel</code> the identifier is for and the unique identifier
 * integer.
 * 
 * Only implementations of {@code SemanticIdentifierMap} should instantiate a
 * {@code DefaultSemanticIdentifier} directly. All other uses should access 
 * a {@code SemanticIdentifier} through a {@code SemanticIdentifierMap}.
 *
 * @author Justin Basilico
 * @since 1.0
 */
public class DefaultSemanticIdentifier
    extends AbstractSemanticIdentifier
{
    /** The SemanticLabel that is identified. */
    private SemanticLabel label;
    
    /** The unique identifier for the semantic label. */
    private int identifier;
    
    /**
     * Creates a new instance of SemanticIdentifier.
     *
     * @param label The SemanticLabel that is identified
     * @param identifier The identifier for the label
     */
    public DefaultSemanticIdentifier(
        SemanticLabel label,
        int identifier)
    {
        super();
        
        this.setLabel(label);
        this.setIdentifier(identifier);
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    public SemanticLabel getLabel()
    {
        return this.label;
    }
    
    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    public int getIdentifier()
    {
        return this.identifier;
    }
    
    /**
     * Sets the SemanticLabel.
     *
     * @param label The new SemanticLabel
     */
    // Note: This setter is private because it should only be called once,
    // from the constructor, and the value never should change.
    private void setLabel(
        SemanticLabel label)
    {
        if ( label  == null )
        {
            throw new NullPointerException("The label cannot be null.");
        }
        
        this.label = label;
    }
    
    /**
     * Sets the integer identifier for the label
     *
     * @param identifier The new identifiers
     */
    // Note: This setter is private because it should only be called once,
    // from the constructor, and the value never should change.
    private void setIdentifier(
        int identifier)
    {
        this.identifier = identifier;
    }
}
