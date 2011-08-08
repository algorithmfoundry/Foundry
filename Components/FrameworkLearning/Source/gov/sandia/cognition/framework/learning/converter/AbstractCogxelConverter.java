/*
 * File:                AbstractCogxelConverter.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Nov 18, 2008, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government. 
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.framework.learning.converter;

import gov.sandia.cognition.framework.CogxelFactory;
import gov.sandia.cognition.framework.SemanticIdentifierMap;
import gov.sandia.cognition.util.AbstractCloneableSerializable;

/**
 * Partial implementation of CogxelConverter
 * @author Kevin R. Dixon
 * @since 3.0
 * @param  <DataType> Type of data to convert to/from Cogxels
 */
public abstract class AbstractCogxelConverter<DataType>
    extends AbstractCloneableSerializable
    implements CogxelConverter<DataType>
{

    /**
     * The SemanticIdentifierMap for the converter.
     */
    protected SemanticIdentifierMap semanticIdentifierMap;

    /**
     * Default constructor
     */
    public AbstractCogxelConverter()
    {
        this(null);
    }

    /** 
     * Creates a new instance of AbstractCogxelConverter 
     * @param semanticIdentifierMap 
     * The SemanticIdentifierMap for the converter.
     */
    public AbstractCogxelConverter(
        SemanticIdentifierMap semanticIdentifierMap)
    {
        this.setSemanticIdentifierMap(semanticIdentifierMap);
    }

    @SuppressWarnings("unchecked")
    @Override
    public CogxelConverter<DataType> clone()
    {
        return (CogxelConverter<DataType>) super.clone();
    }


    /**
     * Rebuilds the cache of SemanticIdentifier objects. Should only need to
     * be called after the SemanticIdentifierMap is changed.
     *
     * @since 3.0
     */
    protected void buildIdentifierCache()
    {
    }


    public SemanticIdentifierMap getSemanticIdentifierMap()
    {
        return semanticIdentifierMap;
    }

    public void setSemanticIdentifierMap(
        SemanticIdentifierMap semanticIdentifierMap)
    {
        this.semanticIdentifierMap = semanticIdentifierMap;
        this.buildIdentifierCache();
    }

}
