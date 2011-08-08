/*
 * File:                AbstractCloneableSerializable.java
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

package gov.sandia.cognition.util;

/**
 * The {@code AbstractCloneableserializable} abstract class implements a default
 * version of the clone method that calls the Object clone method, but traps
 * the exception that can be thrown.
 * 
 * @author  Justin Basilico
 * @since   2.1
 */
public abstract class AbstractCloneableSerializable
    extends Object
    implements CloneableSerializable
{
    /**
     * Creates a new instance of {@code AbstractCloneableSerializable}.
     */
    public AbstractCloneableSerializable()
    {
        super();
    }
    
    /**
     * This makes public the clone method on the {@code Object} class and 
     * removes the exception that it throws. Its default behavior is to 
     * automatically create a clone of the exact type of object that the
     * clone is called on and to copy all primitives but to keep all references, 
     * which means it is a shallow copy.
     * 
     * Extensions of this class may want to override this method (but call
     * {@code super.clone()} to implement a "smart copy". That is, to target
     * the most common use case for creating a copy of the object. Because of
     * the default behavior being a shallow copy, extending classes only need 
     * to handle fields that need to have a deeper copy (or those that need to
     * be reset). Some of the methods in {@code ObjectUtil} may be helpful in
     * implementing a custom clone method.
     * 
     * Note: The contract of this method is that you must use
     * {@code super.clone()} as the basis for your implementation.
     * 
     * @return A clone of this object.
     */
    @Override
    public CloneableSerializable clone()
    {
        try
        {
            return (CloneableSerializable) super.clone();
        }
        catch (CloneNotSupportedException ex)
        {
            throw new RuntimeException(ex);
        }
    }
    
}
