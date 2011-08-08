/*
 * File:                DefaultFactory.java
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
import gov.sandia.cognition.util.AbstractCloneableSerializable;
import java.lang.reflect.Modifier;

/**
 * The {@code DefaultFactory} class is a default implementation of the
 * {@code Factory} interface that takes a class as its parameter and uses the
 * default constructor of the class, called through newInstance(), to create
 * new objects of that class.
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
public class DefaultFactory<CreatedType>
    extends AbstractCloneableSerializable
    implements Factory<CreatedType>
{
    /** The class whose default constructor is used to create new objects. */
    protected Class<? extends CreatedType> createdClass;
    
    /**
     * Creates a new {@code DefaultFactory} for the given class.
     * 
     * @param   createdClass 
     *      The class whose default constructor is used to create new objects.
     */
    public DefaultFactory(
        final Class<? extends CreatedType> createdClass)
    {
        super();
        
        this.setCreatedClass(createdClass);
    }
    
    /**
     * Creates a new object using the default constructor of the class that
     * the factory contains.
     * 
     * @return A new object of the created class.
     */
    public CreatedType create()
    {
        try
        {
            return this.getCreatedClass().newInstance();
        }
        catch (Exception ex)
        {
            throw new RuntimeException(ex);
        }
    }
    
    /**
     * Gets the class whose default constructor is used to create new objects.
     * 
     * @return The class whose default constructor is used to create new 
     *      objects.
     */
    public Class<? extends CreatedType> getCreatedClass()
    {
        return this.createdClass;
    }

    
    /**
     * Sets the class whose default constructor is used to create new objects.
     * The class cannot be null, an interface, or an abstract class and must 
     * have a default constructor.
     * 
     * @param  createdClass The class whose default constructor is used to 
     *      create new objects.
     */
    public void setCreatedClass(
        final Class<? extends CreatedType> createdClass)
    {
        if (createdClass == null)
        {
            throw new IllegalArgumentException("createdClass cannot be null");
        }
        else if (createdClass.isInterface())
        {
            throw new IllegalArgumentException(
                "createdClass cannot be an interface.");
        }
        else if (Modifier.isAbstract(createdClass.getModifiers()))
        {
            throw new IllegalArgumentException(
                "createdClass cannot be an an abstract class.");
        } 
           

        // See if there is a default constructor.
        boolean hasDefaultConstructor = false;
        try
        {
            hasDefaultConstructor = createdClass.getConstructor() != null;
        }
        catch (NoSuchMethodException e)
        {
            hasDefaultConstructor = false;
        }
        finally
        {
            if (!hasDefaultConstructor)
            {
                throw new IllegalArgumentException(
                    "createdClass must have a default constructor.");
            }
        }
        
        this.createdClass = createdClass;
    }
    
    /**
     * Gets a new {@code DefaultFactory} for the given class. 
     * The class cannot be null, an interface, or an abstract class and must 
     * have a default constructor.
     * 
     * @param <T> 
     *      The type of the class.
     * @param createdClass 
     *      The class whose default constructor will be used in the factory.
     * @return
     *      A new {@code DefaultFactory} created for the given class.
     */
    public static <T> DefaultFactory<T> get(
        final Class<T> createdClass)
    {
        return new DefaultFactory<T>(createdClass);
    }

    
}
