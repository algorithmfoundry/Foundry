/*
 * File:                DistributionParameter.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Mar 1, 2010, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.statistics;

import gov.sandia.cognition.util.CloneableSerializable;
import gov.sandia.cognition.util.NamedValue;

/**
 * Allows access to a parameter within a closed-form distribution, given by
 * the high-level String value.  For example, we can access the variance
 * of a UnivariateGaussian by attaching a DistributionParameter to "variance".
 * @param <ParameterType> Type of Parameter.
 * @param <ConditionalType>
 * Type of parameterized distribution that generates observations.
 * @author Kevin R. Dixon
 * @since 3.0
 */
public interface DistributionParameter<ParameterType,ConditionalType extends Distribution<?>>
    extends NamedValue<ParameterType>,
    CloneableSerializable
{

    /**
     * Gets the conditional distribution associated with the parameter.
     * @return
     * Conditional distribution associated with the parameter.
     */
    public ConditionalType getConditionalDistribution();

    /**
     * Sets the value of the parameter.
     * @param value
     * Parameter to set.
     */
    public void setValue(
        ParameterType value );
    
}
