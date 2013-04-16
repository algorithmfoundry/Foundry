/*
 * File:            DivergenceFunctionContainer.java
 * Authors:         Justin Basilico
 * Project:         Cognitive Foundry Learning Core
 * 
 * Copyright 2011 Cognitive Foundry. All rights reserved.
 */

package gov.sandia.cognition.learning.function.distance;

import gov.sandia.cognition.math.DivergenceFunction;

/**
 * Interface for a class that holds a divergence function.
 *
 * @param   <FirstType>
 *      The type of the first parameter to the divergence function.
 * @param   <SecondType>
 *      The type of the second parameter to the divergence function.
 * @author  Justin Basilico
 * @since   3.3.3
 */
public interface DivergenceFunctionContainer<FirstType, SecondType>
{

    /**
     * Gets the divergence function used by this object.
     *
     * @return
     *      The divergence function.
     */
    public DivergenceFunction<? super FirstType, ? super SecondType>
        getDivergenceFunction();
    
}
