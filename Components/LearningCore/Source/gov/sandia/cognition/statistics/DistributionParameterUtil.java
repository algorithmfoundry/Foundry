/*
 * File:                DistributionParameterUtil.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Mar 2, 2010, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.statistics;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.MethodDescriptor;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Functions to assist in creating DistributionParameters.
 * @author Kevin R. Dixon
 * @since 3.0
 */
public class DistributionParameterUtil
{

    /**
     * Finds all the DistributionParameters from a given Distribution.
     * @param <ConditionalType>
     * Type of parameterized distribution that generates observations.
     * @param distribution
     * Distribution from which to pull the DistributionParameters.
     * @return
     * Collection of all DistributionParameters in the Distribution.
     * @throws IntrospectionException
     * If the system could not perform introspection on the Distribution.
     */
    static <ConditionalType extends ClosedFormDistribution<?>> Collection<DistributionParameter<?,ConditionalType>> findAll(
        final ConditionalType distribution )
        throws IntrospectionException
    {

        BeanInfo beaninfo = Introspector.getBeanInfo( distribution.getClass() );
        ArrayList<DistributionParameter<?,ConditionalType>> parameters =
            new ArrayList<DistributionParameter<?,ConditionalType>>(
                beaninfo.getPropertyDescriptors().length );
        for( PropertyDescriptor property : beaninfo.getPropertyDescriptors() )
        {
            if( (property.getReadMethod() != null) &&
                (property.getWriteMethod() != null) )
            {
                parameters.add( new DefaultDistributionParameter<Object,ConditionalType>(
                    distribution, property ) );
            }
            else if( property.getName().equals( DefaultDistributionParameter.MEAN_NAME ) &&
                (property.getReadMethod() != null) )
            {
                // Look for a setMean method...
                for( MethodDescriptor method : beaninfo.getMethodDescriptors() )
                {
                    if( method.getDisplayName().contains(
                        DefaultDistributionParameter.MEAN_SETTER ) )
                    {
                        parameters.add( new DefaultDistributionParameter<Object,ConditionalType>(
                            distribution, property.getName() ) );
                        break;
                    }
                }
            }
        }

        return parameters;

    }
    
}
