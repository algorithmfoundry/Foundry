/*
 * File:                ConstructorBasedFactory.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright April 18, 2008, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 * See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.factory;

import gov.sandia.cognition.annotation.CodeReview;
import gov.sandia.cognition.util.ObjectUtil;
import java.lang.reflect.Constructor;

/**
 * The {@code ConstructorBasedFactory} class implements a {@code Factory}
 * that takes a constructor and parameters to that constructor used to create
 * new objects.
 * 
 * @param   <CreatedType> The type that the factory creates.
 * @author  Justin Basilico
 * @since   2.1
 */
@CodeReview(
    reviewer="Kevin R. Dixon",
    date="2008-12-02",
    changesNeeded=false,
    comments={
        "I just don't think this class will be useful.",
        "However, the implementation is great... if anybody ever finds it useful."
    }
)
public class ConstructorBasedFactory<CreatedType>
    extends Object
    implements Factory<CreatedType>, Cloneable
{

    /** The constructor to use to create new objects. */
    protected Constructor<? extends CreatedType> constructor;

    /** The parameters to pass to the constructor. */
    protected Object[] parameters;

    /**
     * Creates a new {@code ConstructorBasedFactory} from the given constructor
     * and parameters.
     * 
     * @param   constructor
     *      The constructor to use to create new objects.
     * @param   parameters
     *      The parameters to the constructor.
     */
    public ConstructorBasedFactory(
        final Constructor<? extends CreatedType> constructor,
        final Object... parameters)
    {
        super();

        this.setConstructor(constructor);
        this.setParameters(parameters);
    }

    @Override
    public ConstructorBasedFactory<CreatedType> clone()
    {
        try
        {
            @SuppressWarnings("unchecked")
            final ConstructorBasedFactory<CreatedType> result =
                (ConstructorBasedFactory<CreatedType>) super.clone();
            // TODO: Should this really clone the list also?
            result.parameters = ObjectUtil.cloneSmartArrayAndElements(
                this.parameters);
            return result;
        }
        catch (CloneNotSupportedException e)
        {
            throw new RuntimeException(e);
        }
    }

    /**
     * Creates a new object by calling the constructor inside the factory with
     * the parameters it is configured to take.
     * 
     * @return  A new object.
     */
    public CreatedType create()
    {
        try
        {
            return this.getConstructor().newInstance(this.getParameters());
        }
        catch (Exception ex)
        {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Gets the constructor used to create new objects.
     * 
     * @return  The constructor used to create new objects.
     */
    public Constructor<? extends CreatedType> getConstructor()
    {
        return this.constructor;
    }

    /**
     * Sets the constructor to use to create new objects.
     * 
     * @param   constructor
     *      The constructor used to create new objects. It cannot be null. The
     *      parameters on the factory must be set to match the parameter types
     *      the constructor expects before create is called.
     */
    public void setConstructor(
        final Constructor<? extends CreatedType> constructor)
    {
        if (constructor == null)
        {
            throw new IllegalArgumentException("constructor cannot be null.");
        }

        this.constructor = constructor;
    }

    /**
     * Gets the parameters that are passed to the constructor to create new
     * objects.
     * 
     * @return  The parameters that are passed to the constructor.
     */
    public Object[] getParameters()
    {
        return this.parameters;
    }

    /**
     * Sets the parameters that are passed to the constructor to create new
     * objects.
     * 
     * @param   parameters
     *      The parameters to pass to the constructor.
     */
    public void setParameters(
        final Object... parameters)
    {
        this.parameters = parameters;
    }

}
