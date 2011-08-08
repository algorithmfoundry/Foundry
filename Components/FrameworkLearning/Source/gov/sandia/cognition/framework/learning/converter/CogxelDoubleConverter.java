/*
 * File:                CogxelDoubleConverter.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Framework Lite
 *
 * Copyright June 27, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
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
 * The CogxelDoubleConverter class converts a Cogxel to and from a double
 * value by using its activation.
 *
 * @author Justin Basilico
 * @since  2.0
 */
public class CogxelDoubleConverter
    extends Object
    implements CogxelConverter<Double>
{
    /** The label of the Cogxel to convert. */
    private SemanticLabel label;
    
    /** The semantic identifier of the Cogxel to convert. */
    private SemanticIdentifier identifier;
    
    /** The SemanticIdentifierMap for the converter. */
    private SemanticIdentifierMap semanticIdentifierMap;
    
    /** The CogxelFactory to use. */
    private CogxelFactory cogxelFactory;
    
    /**
     * Creates a new instance of CogxelDoubleConverter.
     */
    public CogxelDoubleConverter()
    {
        this(null, null);
    }
    
    /**
     * Creates a new instance of CogxelDoubleConverter.
     *
     * @param  label The label for the Cogxel to convert.
     */
    public CogxelDoubleConverter(
        SemanticLabel label)
    {
        this(label, null);
    }
    
    /**
     * Creates a new instance of CogxelDoubleConverter.
     *
     * @param  label The label for the Cogxel to convert.
     * @param  semanticIdentifierMap The SemanticIdentifierMap for the 
     *         converter.
     */
    public CogxelDoubleConverter(
        SemanticLabel label,
        SemanticIdentifierMap semanticIdentifierMap)
    {
        this(label, semanticIdentifierMap, DefaultCogxelFactory.INSTANCE);
    }
    
    /**
     * Creates a new instance of CogxelDoubleConverter.
     *
     * @param  label The label for the Cogxel to convert.
     * @param  semanticIdentifierMap The SemanticIdentifierMap for the 
     *         converter.
     * @param  cogxelFactory The CogxelFactory to use.
     */
    public CogxelDoubleConverter(
        SemanticLabel label,
        SemanticIdentifierMap semanticIdentifierMap,
        CogxelFactory cogxelFactory)
    {
        super();
        
        this.setLabel(label);
        this.setSemanticIdentifierMap(semanticIdentifierMap);
        this.setCogxelFactory(cogxelFactory);
    }

    /**
     * Creates a new instance of CogxelDoubleConverter.
     *
     * @param  other A CogxelDoubleConverter to copy.
     */
    public CogxelDoubleConverter(
        CogxelDoubleConverter other)
    {
        this(other.getLabel(), other.getSemanticIdentifierMap(),
            other.getCogxelFactory());
    }
    
    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    @Override
    public CogxelDoubleConverter clone()
    {
        return new CogxelDoubleConverter(this);
    }
    
    /**
     * {@inheritDoc}
     *
     * @param  other {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public boolean equals(
        Object other)
    {
        if ( other instanceof CogxelDoubleConverter )
        {
            return this.equals((CogxelDoubleConverter) other);
        }
        else
        {   
            return false;
        }
    }
    
    /**
     * Returns true if the two converters have the same label.
     *
     * @param  other Another CogxelVectorConverter.
     * @return True if the two converters have the same label.
     */
    public boolean equals(
        CogxelDoubleConverter other)
    {
        return other != null 
            && ObjectUtil.equalsSafe(this.getLabel(), other.getLabel());
    }

    @Override
    public int hashCode()
    {
        return ObjectUtil.hashCodeSafe(this.label);
    }
    
    /**
     * Rebuilds the cache of SemanticIdentifier objects. Should only need to
     * be called after the SemanticIdentifierMap is changed.
     */
    protected void buildIdentifierCache()
    {
        this.setIdentifier(null);
        
        // Adds the label to the cache.
        SemanticIdentifierMap identifierMap = this.getSemanticIdentifierMap();
        if ( identifierMap != null && this.getLabel() != null )
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
    
    /**
     * {@inheritDoc}
     *
     * @param  cogxels {@inheritDoc}
     * @return {@inheritDoc}
     */
    public Double fromCogxels(
        CogxelState cogxels)
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
        return cogxels.getCogxelActivation(this.getIdentifier());
    }

    /**
     * {@inheritDoc}
     *
     * @param  data {@inheritDoc}
     * @param  cogxels {@inheritDoc}
     */
    public void toCogxels(
        Double data, 
        CogxelState cogxels)
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
        cogxel.setActivation(data);
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    public SemanticIdentifierMap getSemanticIdentifierMap()
    {
        return semanticIdentifierMap;
    }
    
    /**
     * {@inheritDoc}
     *
     * @param  semanticIdentifierMap {@inheritDoc}
     */
    public void setSemanticIdentifierMap(
        SemanticIdentifierMap semanticIdentifierMap)
    {
        this.semanticIdentifierMap = semanticIdentifierMap;
        this.buildIdentifierCache();
    }

    /**
     * Gets the label of the Cogxel to convert.
     *
     * @return The label of the Cogxel to convert.
     */
    public SemanticLabel getLabel()
    {
        return label;
    }
    
    /**
     * Sets the label of the Cogxel to convert.
     *
     * @param  label The label of the Cogxel to convert.
     */
    public void setLabel(
        SemanticLabel label)
    {
        this.label = label;
    }

    /**
     * Gets the semantic identifier of the Cogxel to convert.
     *
     * @return The semantic identifier of the Cogxel to convert.
     */
    protected SemanticIdentifier getIdentifier()
    {
        return identifier;
    }
    
    /**
     * Gets the semantic identifier of the Cogxel to convert.
     *
     * @param  identifier The semantic identifier of the Cogxel to convert.
     */
    protected void setIdentifier(
        SemanticIdentifier identifier)
    {
        this.identifier = identifier;
    }
    
    /**
     * Gets the CogxelFactory used to create the Cogxels used by the converter.
     *
     * @return The CogxelFactory used to create the Cogxels used by the 
     *         converter.
     */
    public CogxelFactory getCogxelFactory()
    {
        return this.cogxelFactory;
    }
    
    /**
     * Gets the CogxelFactory used to create the Cogxels used by the converter.
     *
     * @param  cogxelFactory The CogxelFactory used to create the Cogxels used  
     *         by the converter.
     */
    public void setCogxelFactory(
        CogxelFactory cogxelFactory)
    {
        this.cogxelFactory = cogxelFactory;
    }
}
