/*
 * File:                CogxelConverter.java
 * Authors:             Justin Basilico and Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Framework Lite
 *
 * Copyright June 21, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 *
 */

package gov.sandia.cognition.framework.learning.converter;

import gov.sandia.cognition.framework.CogxelState;
import gov.sandia.cognition.framework.SemanticIdentifierMap;
import gov.sandia.cognition.util.CloneableSerializable;

/**
 * The CogxelConverter interface defines the functionality required for an
 * object to act as a converter from some DataType to and from a CogxelState 
 * object. This is used to adapt components that use other data types to the
 * Cognitive Framework.
 *
 * It is important that the CogxelConverter not keep dynamic state and that it
 * supports the clone method properly.
 *
 * @param  <DataType> Type of data to convert to/from Cogxels
 * @author Justin Basilico
 * @author Kevin R. Dixon
 * @since  2.0
 */
public interface CogxelConverter<DataType>
    extends CloneableSerializable
{
    /**
     * Converts from a CogxelState object to an object of type DataType.
     *
     * @param  cogxels The CogxelState to convert to DataType.
     * @return An object of DataType extracted from the given CogxelState.
     */
    public DataType fromCogxels(
        CogxelState cogxels);
    
    /**
     * Converts from an object of type DataType to an updated CogxelState.
     *
     * @param  data The object to convert into the CogxelState.
     * @param  cogxels The CogxelState to update with the converted data.
     */
    public void toCogxels(
        DataType data,
        CogxelState cogxels);
    
    /**
     * Gets the SemanticIdentifierMap used by this converter.
     *
     * @return The SemanticIdentifierMap used by this converter.
     */
    public SemanticIdentifierMap getSemanticIdentifierMap();
    
    /**
     * Sets the SemanticIdentifierMap that the converter is to use.
     *
     * @param  semanticIdentifierMap The SemanticIdentifierMap the converter is 
     *         to use.
     */
    public void setSemanticIdentifierMap(
        SemanticIdentifierMap semanticIdentifierMap);
    
    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    public CogxelConverter<DataType> clone();
}
