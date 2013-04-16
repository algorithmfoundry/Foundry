/*
 * File:            AbstractRegressor.java
 * Authors:         Justin Basilico
 * Project:         Cognitive Foundry Learning Core
 * 
 * Copyright 2011 Cognitive Foundry. All rights reserved.
 */

package gov.sandia.cognition.learning.function.regression;

import gov.sandia.cognition.math.AbstractScalarFunction;

/**
 * An abstract implementation of the {@code Regressor} interface.
 *
 * @param   <InputType>
 *      The type of the input to the regressor.
 * @author  Justin Basilico
 * @version 3.4.0
 */
public abstract class AbstractRegressor<InputType>
    extends AbstractScalarFunction<InputType>
{

    /**
     * Creates a new {@code AbstractRegressor}.
     */
    public AbstractRegressor()
    {
        super();
    }
    
}
