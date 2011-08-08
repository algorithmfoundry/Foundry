/*
 * File:                DefaultDistributionParameter.java
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

import gov.sandia.cognition.util.AbstractNamed;
import gov.sandia.cognition.util.ObjectUtil;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;

/**
 * Default implementation of DistributionParameter using introspection.
 * @param <ParameterType>
 * Type of parameter of the conditional distribution.
 * @param <ConditionalType>
 * Type of parameterized distribution that generates observations.
 * @author Kevin R. Dixon
 * @since 3.0
 */
public class DefaultDistributionParameter<ParameterType,ConditionalType extends ClosedFormDistribution<?>>
    extends AbstractNamed
    implements DistributionParameter<ParameterType,ConditionalType>
{

    /**
     * Distribution from which to pull the parameters.
     */
    protected ConditionalType conditionalDistribution;

    /**
     * Setter for the parameter, the read method.
     */
    transient protected Method parameterSetter;

    /**
     * Getter for the parameter, the write method.
     */
    transient protected Method parameterGetter;

    /**
     * Name of the mean property, {@value}.
     */
    public static final String MEAN_NAME = "mean";

    /**
     * Setter for the mean, {@value}.
     */
    public static final String MEAN_SETTER = "setMean";

    /**
     * Getter for the mean, {@value}.
     */
    public static final String MEAN_GETTER = "getMean";

    /** 
     * Creates a new instance of DefaultDistributionParameter 
     * @param conditionalDistribution
     * Distribution from which to pull the parameters.
     * @param parameterName
     * Name of the parameter
     */
    public DefaultDistributionParameter(
        ConditionalType conditionalDistribution,
        String parameterName )
    {
        super( parameterName );
        this.setConditionalDistribution(conditionalDistribution);
    }

    /**
     * Creates a new instance of DefaultDistributionParameter
     * @param conditionalDistribution
     * Distribution from which to pull the parameters.
     * @param descriptor
     * PropertyDescriptor from the Introspector that has a setter and a getter.
     */
    public DefaultDistributionParameter(
        ConditionalType conditionalDistribution,
        PropertyDescriptor descriptor )
    {
        this( conditionalDistribution, descriptor.getName() );
        this.parameterGetter = descriptor.getReadMethod();
        this.parameterSetter = descriptor.getWriteMethod();
    }

    @Override
    public DefaultDistributionParameter<ParameterType,ConditionalType> clone()
    {
        @SuppressWarnings("unchecked")
        DefaultDistributionParameter<ParameterType,ConditionalType> clone =
            (DefaultDistributionParameter<ParameterType,ConditionalType>) super.clone();
        clone.setConditionalDistribution(
            ObjectUtil.cloneSafe(this.getConditionalDistribution() ) );
        return clone;
    }

    /**
     * Assigns the getter and setter from the given conditionalDistribution and parameter
     * name.
     * @param conditionalDistribution
     * Distribution from which to pull the parameters.
     * @param parameterName
     * Name of the parameter
     */
    protected void assignParameterMethods(
        Distribution<?> conditionalDistribution,
        String parameterName )
    {

        this.parameterGetter = null;
        this.parameterSetter = null;

        // Mean is a special case...
        // Note: We can't use BeanInfo and Introspection on PropertyDescriptor
        // because the signature of the getters and setters could be
        // inconsistent.  For example, a class could have a method of
        // "Double getMean()", but the setter might be "void setMean(double)".
        // Because the getter returns Double, but the setter takes a double,
        // the Bean Introspection is too clever and doesn't consider them
        // as getters and setters for the same property.  Thus, we have to
        // include our little hack below.
        if( parameterName.equals( MEAN_NAME ) )
        {
            for( Method method : conditionalDistribution.getClass().getMethods() )
            {
                String methodString = method.getName();
                if( methodString.contains( MEAN_GETTER ) )
                {
                    this.parameterGetter = method;
                }
                else if( methodString.contains( MEAN_SETTER ) )
                {
                    this.parameterSetter = method;
                }
            }
        }
        else
        {
            try
            {
                BeanInfo beaninfo =
                    Introspector.getBeanInfo(conditionalDistribution.getClass());
                for (PropertyDescriptor descriptor : beaninfo.getPropertyDescriptors())
                {
                    String propertyName = descriptor.getName();
                    if (propertyName.equals(parameterName))
                    {
                        this.parameterGetter = descriptor.getReadMethod();
                        this.parameterSetter = descriptor.getWriteMethod();
                        break;
                    }
                }
            }
            catch (IntrospectionException ex)
            {
                throw new RuntimeException( ex );
            }

        }

        if( this.parameterGetter == null )
        {
            throw new IllegalArgumentException(
                "Could not find getter: " + parameterName + " in class " + this.getConditionalDistribution() );
        }
        if( this.parameterSetter == null )
        {
            throw new IllegalArgumentException(
                "Could not find setter: " + parameterName + " in class " + this.getConditionalDistribution() );
        }

    }


    @SuppressWarnings("unchecked")
    public ParameterType getValue()
    {
        ParameterType retval = null;
        try
        {
            if( this.parameterGetter == null )
            {
                this.assignParameterMethods(
                    this.getConditionalDistribution(), this.getName() );
            }
            retval = (ParameterType) this.parameterGetter.invoke(
                this.getConditionalDistribution());
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }

        return retval;
    }

    public void setValue(
        ParameterType value )
    {
        try
        {
            if( this.parameterGetter == null )
            {
                this.assignParameterMethods(
                    this.getConditionalDistribution(), this.getName() );
            }
            this.parameterSetter.invoke( this.getConditionalDistribution(), value );
        }
        catch (Exception e)
        {
            throw new RuntimeException( e );
        }
    }


    @Override
    public ConditionalType getConditionalDistribution()
    {
        return this.conditionalDistribution;
    }

    /**
     * Setter for conditionalDistribution
     * @param conditionalDistribution
     * Conditional conditionalDistribution associated with the parameter.
     */
    protected void setConditionalDistribution(
        ConditionalType conditionalDistribution)
    {
        this.conditionalDistribution = conditionalDistribution;
    }

    @Override
    public void setName(
        String name)
    {
        this.parameterGetter = null;
        this.parameterSetter = null;
        super.setName(name);
    }
    
}
