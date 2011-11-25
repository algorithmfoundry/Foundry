/*
 * File:            IdentityDistanceMetric.java
 * Authors:         Justin Basilico
 * Project:         Community Foundry
 * 
 * Copyright 2011 Justin Basilico. All rights reserved.
 */

package gov.sandia.cognition.learning.function.distance;

import gov.sandia.cognition.math.Metric;
import gov.sandia.cognition.util.AbstractCloneableSerializable;
import gov.sandia.cognition.util.ObjectUtil;

/**
 * A distance metric that is 0 if two objects are equal and 1 if they are not.
 * Can be used with any object with a valid equals method.
 *
 * @author  Justin Basilico
 * @version 3.4.0
 */
public class IdentityDistanceMetric
    extends AbstractCloneableSerializable
    implements Metric<Object>
{
    
    /**
     * Creates a new {@code IdentityDistanceMetric}.
     */
    public IdentityDistanceMetric()
    {
        super();
    }

    @Override
    public double evaluate(
        final Object first,
        final Object second)
    {
        return (ObjectUtil.equalsSafe(first, second) ? 0.0 : 1.0);
    }

}
