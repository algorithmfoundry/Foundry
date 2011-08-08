/*
 * File:                ParameterAdapter.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright September 29, 2008, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 * See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.learning.parameter;

import gov.sandia.cognition.util.CloneableSerializable;

/**
 * Interface for an object that can adapt the parameters of another object
 * based on some given data. This is most commonly used in conjunction with
 * learning algorithms to employ some heuristic for setting a parameter of
 * the algorithm based on the data. For learning algorithms, the adapter should
 * be called before learning has begun.
 * 
 * @param   <ObjectType> 
 *      The type of object whose parameters can be adapted by this object.
 * @param   <DataType>
 *      The type of data that can be used to adapt the parameters.
 * @author  Justin Basilico
 * @since   3.0
 */
public interface ParameterAdapter<ObjectType, DataType>
    extends CloneableSerializable
{
    
    /**
     * Adapt the parameter(s) of a given object based on the given data. It
     * works by side-effect on the object.
     * 
     * @param   object
     *      The object containing the parameters to adapt.
     * @param   data
     *      The data to use to adapt the parameters.
     */
    public void adapt(
        final ObjectType object,
        final DataType data);
    
}
