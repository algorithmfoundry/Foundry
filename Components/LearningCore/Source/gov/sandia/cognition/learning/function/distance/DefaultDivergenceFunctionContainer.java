/*
 * File:                DefaultDivergenceFunctionContainer.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright March 24, 2008, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 * See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.learning.function.distance;

import gov.sandia.cognition.math.DivergenceFunction;
import gov.sandia.cognition.util.AbstractCloneableSerializable;
import gov.sandia.cognition.util.ObjectUtil;

/**
 * The {@code DefaultDivergenceFunctionContainer} class implements an object
 * that holds a divergence function. It is extended by various other classes
 * that contain divergence functions.
 * 
 * @param   <FirstType> The type of the first parameter to the divergence
 *      function.
 * @param   <SecondType> The type of the second parameter to the divergence
 *      function.
 * @author  Justin Basilico
 * @since   2.1
 */
public class DefaultDivergenceFunctionContainer<FirstType, SecondType>
    extends AbstractCloneableSerializable
    implements DivergenceFunctionContainer<FirstType, SecondType>
{
    /**
     * The internal divergence function for the object to use.
     */
    protected DivergenceFunction<? super FirstType, ? super SecondType> 
        divergenceFunction;
    
    /**
     * Creates a new instance of {@code DefaultDivergenceFunctionContainer}.
     */
    public DefaultDivergenceFunctionContainer()
    {
        super();
        
        this.setDivergenceFunction(null);
    }
    
    /**
     * Creates a new instance of {@code DefaultDivergenceFunctionContainer}.
     * 
     * @param   divergenceFunction The divergence function.
     */
    public DefaultDivergenceFunctionContainer(
        final DivergenceFunction<? super FirstType, ? super SecondType>
            divergenceFunction)
    {
        super();
        
        this.setDivergenceFunction(divergenceFunction);
    }
    
    /**
     * Creates a new instance of {@code DefaultDivergenceFunctionContainer}.
     * 
     * @param   other The other object to copy.
     */
    public DefaultDivergenceFunctionContainer(
        final DefaultDivergenceFunctionContainer<? super FirstType, ? super SecondType> other)
    {
        this(ObjectUtil.cloneSafe(other.getDivergenceFunction()));
    }

    @Override
    public DefaultDivergenceFunctionContainer<FirstType, SecondType> clone()
    {
        @SuppressWarnings("unchecked")
        final DefaultDivergenceFunctionContainer<FirstType, SecondType> result =
            (DefaultDivergenceFunctionContainer<FirstType, SecondType>) 
                super.clone();
        result.divergenceFunction = ObjectUtil.cloneSafe(this.divergenceFunction);
        return result;
    }
    
    /**
     * Gets the divergence function used by this object.
     * 
     * @return  The divergence function.
     */
    public DivergenceFunction<? super FirstType, ? super SecondType> 
        getDivergenceFunction()
    {
        return divergenceFunction;
    }
    
    /**
     * Sets the divergence function used by this object.
     * 
     * @param   divergenceFunction The divergence function.
     */
    public void setDivergenceFunction(
        final DivergenceFunction<? super FirstType, ? super SecondType> 
        divergenceFunction)
    {
        this.divergenceFunction = divergenceFunction;
    }
}
