/*
 * File:                ParameterAdaptable.java
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

import java.util.Collection;

/**
 * Interface for an object that allows parameter adapters to be attached to it.
 * The interface is similar to a listener pattern in that the parameter adapters
 * should expect to be called at the appropriate point for the object.
 * 
 * @param   <ObjectType>
 *      The type of object whose parameters can be adapted.
 * @param   <DataType>
 *      The type of data that the object gets to adapt parameters.
 * @author  Justin Basilico
 * @since   3.0
 */
public interface ParameterAdaptable<ObjectType, DataType>
{
    /**
     * Adds the given parameter adapter to this object.
     * 
     * @param   parameterAdapter
     *      The parameter adapter to add.
     */
    public void addParameterAdapter(
        final ParameterAdapter<? super ObjectType, ? super DataType> parameterAdapter);
    
    /**
     * Removes the given parameter adapter from this object.
     * 
     * @param   parameterAdapter
     *      The parameter adapter to remove.
     */
    public void removeParameterAdapter(
        final ParameterAdapter<? super ObjectType, ? super DataType> parameterAdapter);
    
    /**
     * Gets the collection of parameter adapters attached to this object.
     * 
     * @return  The parameter adapters attached to this object.
     */
    public Collection<ParameterAdapter<? super ObjectType, ? super DataType>> 
        getParameterAdapters();
    
}
