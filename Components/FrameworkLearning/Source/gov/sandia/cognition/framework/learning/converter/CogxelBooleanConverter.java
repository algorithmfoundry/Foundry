/*
 * File:                CogxelBooleanConverter.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright March 06, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 * See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.framework.learning.converter;

import gov.sandia.cognition.framework.Cogxel;
import gov.sandia.cognition.framework.CogxelFactory;
import gov.sandia.cognition.framework.CogxelState;
import gov.sandia.cognition.framework.DefaultCogxelFactory;
import gov.sandia.cognition.framework.SemanticIdentifier;
import gov.sandia.cognition.framework.SemanticIdentifierMap;
import gov.sandia.cognition.framework.SemanticLabel;
import gov.sandia.cognition.util.ObjectUtil;

/**
 * Implements a {@code CogxelConverter} that encodes booleans as positive and
 * negative values (+1/-1).
 * 
 * @author  Justin Basilico
 * @since   3.0
 */
public class CogxelBooleanConverter
    extends AbstractCogxelConverter<Boolean>
{

    /** The label of the Cogxel to convert. */
    private SemanticLabel label;

    /** The semantic identifier of the Cogxel to convert. */
    private SemanticIdentifier identifier;

    /** The CogxelFactory to use. */
    private CogxelFactory cogxelFactory;
    /**
     * Creates a new instance of CogxelBooleanConverter.
     */
    public CogxelBooleanConverter()
    {
        this(null);
    }

    /**
     * Creates a new instance of CogxelDoubleConverter.
     *
     * @param  label The label for the Cogxel to convert.
     */
    public CogxelBooleanConverter(
        final SemanticLabel label)
    {
        this(label, null);
    }

    /**
     * Creates a new instance of CogxelBooleanConverter.
     *
     * @param  label The label for the Cogxel to convert.
     * @param  semanticIdentifierMap The SemanticIdentifierMap for the
     *         converter.
     */
    public CogxelBooleanConverter(
        final SemanticLabel label,
        final SemanticIdentifierMap semanticIdentifierMap)
    {
        this(label, semanticIdentifierMap, DefaultCogxelFactory.INSTANCE);
    }

    /**
     * Creates a new instance of CogxelBooleanConverter.
     *
     * @param  label The label for the Cogxel to convert.
     * @param  semanticIdentifierMap The SemanticIdentifierMap for the
     *         converter.
     * @param  cogxelFactory The CogxelFactory to use.
     */
    public CogxelBooleanConverter(
        final SemanticLabel label,
        final SemanticIdentifierMap semanticIdentifierMap,
        final CogxelFactory cogxelFactory)
    {
        super(semanticIdentifierMap);

        this.setLabel(label);
        this.setCogxelFactory(cogxelFactory);
    }

    @Override
    public CogxelBooleanConverter clone()
    {
        return (CogxelBooleanConverter) super.clone();
    }

    @Override
    public boolean equals(
        final Object other)
    {
        return other != null && other instanceof CogxelBooleanConverter
            && this.equals((CogxelBooleanConverter) other);
    }

    @Override
    public int hashCode()
    {
        return ObjectUtil.hashCodeSafe(this.label);
    }

    /**
     * This converter equals another converter of the same type if their labels
     * are equal.
     *
     * @param   other
     *      The other converter.
     * @return
     *      True if the two objects are equal; otherwise, false.
     */
    public boolean equals(
        final CogxelBooleanConverter other)
    {
        return other != null
            && ObjectUtil.equalsSafe(this.getLabel(), other.getLabel());
    }

    @Override
    protected void buildIdentifierCache()
    {
        this.setIdentifier(null);

        // Adds the label to the cache.
        SemanticIdentifierMap identifierMap = this.getSemanticIdentifierMap();
        if (identifierMap != null && this.getLabel() != null)
        {
            // Get the identifier for the label.
            this.setIdentifier(identifierMap.addLabel(this.getLabel()));
        }
        else
        {
            // Clear out the semantic identifier.
            this.setIdentifier(null);
        }
    }

    public Boolean fromCogxels(
        final CogxelState cogxels)
    {
        // Validate the parameters.
        if ( cogxels == null )
        {
            throw new IllegalArgumentException("cogxels is null");
        }

        // Validate the state.
        if ( this.getSemanticIdentifierMap() == null )
        {
            throw new IllegalStateException(
                "The semanticIdentifierMap is not set.");
        }
        else if ( this.getIdentifier() == null )
        {
            throw new IllegalStateException(
                "The semantic identifier is null");
        }

        // Get the activation for the cogxel.
        return cogxels.getCogxelActivation(this.getIdentifier()) > 0.0;
    }

    public void toCogxels(
        final Boolean data,
        final CogxelState cogxels)
    {
        // Validate the parameters.
        if ( data == null )
        {
            throw new IllegalArgumentException("data is null");
        }
        else if ( cogxels == null )
        {
            throw new IllegalArgumentException("cogxels is null");
        }

        // Validate the state.
        if ( this.getSemanticIdentifierMap() == null )
        {
            throw new IllegalStateException(
                "The semanticIdentifierMap is not set.");
        }
        else if ( this.getIdentifier() == null )
        {
            throw new IllegalStateException(
                "The semantic identifier is null");
        }

        // Get the Cogxel for the identifier.
        Cogxel cogxel = cogxels.getOrCreateCogxel(
            this.getIdentifier(), this.getCogxelFactory());

        // Set its activation.
        cogxel.setActivation(data ? 1.0 : -1.0);
    }

    /**
     * Gets the label of the cogxel to convert.
     *
     * @return The label of the cogxel to convert.
     */
    public SemanticLabel getLabel()
    {
        return label;
    }

    /**
     * Sets the label of the cogxel to convert.
     *
     * @param  label The label of the cogxel to convert.
     */
    public void setLabel(
        SemanticLabel label)
    {
        this.label = label;
    }

    /**
     * Gets the semantic identifier of the cogxel to convert.
     *
     * @return The semantic identifier of the cogxel to convert.
     */
    protected SemanticIdentifier getIdentifier()
    {
        return identifier;
    }

    /**
     * Gets the semantic identifier of the cogxel to convert.
     *
     * @param  identifier The semantic identifier of the cogxel to convert.
     */
    protected void setIdentifier(
        SemanticIdentifier identifier)
    {
        this.identifier = identifier;
    }
    

    /**
     * Gets the CogxelFactory used to create the cogxels used by the converter.
     *
     * @return The CogxelFactory used to create the cogxels used by the
     *         converter.
     */
    public CogxelFactory getCogxelFactory()
    {
        return this.cogxelFactory;
    }

    /**
     * Gets the CogxelFactory used to create the cogxels used by the converter.
     *
     * @param  cogxelFactory The CogxelFactory used to create the cogxels used
     *         by the converter.
     */
    public void setCogxelFactory(
        CogxelFactory cogxelFactory)
    {
        this.cogxelFactory = cogxelFactory;
    }

}
