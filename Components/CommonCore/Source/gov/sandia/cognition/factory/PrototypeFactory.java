/*
 * File:                PrototypeFactory.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright May 01, 2008, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 * See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.factory;

import gov.sandia.cognition.annotation.CodeReview;
import gov.sandia.cognition.util.AbstractCloneableSerializable;
import gov.sandia.cognition.util.CloneableSerializable;
import gov.sandia.cognition.util.ObjectUtil;

/**
 * The {@code PrototypeFactory} class implements a {@code Factory} that uses a
 * prototype object to create new objects from by cloning it.
 * 
 * @param   <CreatedType> The type created by the factory.
 * @author  Justin Basilico
 * @since   2.1
 */
@CodeReview(
    reviewer="Kevin R. Dixon",
    date="2008-12-02",
    changesNeeded=false,
    comments={
        "I just don't think this class will be useful.",
        "However, the implementation is great... if anybody ever finds it useful.",
        "This is definitely the hazard of Justin with time on his hands ;"
    }
)
public class PrototypeFactory<CreatedType extends CloneableSerializable>
    extends AbstractCloneableSerializable
    implements Factory<CreatedType>
{

    /** The prototype to create clones from. */
    protected CreatedType prototype;

    /**
     * Creates a new {@code PrototypeFactory} with no prototype.
     */
    public PrototypeFactory()
    {
        this((CreatedType) null);
    }

    /**
     * Creates a new {@code PrototypeFactory} with the given prototype.
     * 
     * @param   prototype The prototype to create clones from.
     */
    public PrototypeFactory(
        final CreatedType prototype)
    {
        super();

        this.setPrototype(prototype);
    }

    /**
     * Creates a new copy of a {@code PrototypeFactory}.
     * 
     * @param   other The other factory to copy.
     */
    public PrototypeFactory(
        final PrototypeFactory<? extends CreatedType> other)
    {
        this(other.getPrototype());
    }

    /**
     * Clones this {@code PrototypeFactory}.
     * 
     * @return  A new copy of this {@code PrototypeFactory}.
     */
    @Override
    public PrototypeFactory<CreatedType> clone()
    {
        @SuppressWarnings("unchecked")
        final PrototypeFactory<CreatedType> result = 
            (PrototypeFactory<CreatedType>) super.clone();
        result.prototype = ObjectUtil.cloneSafe(this.prototype);
        return result;
    }

    /**
     * Creates a new object by calling the clone method on the prototype in the
     * factory.
     * 
     * @return   A new clone of the prototype.
     */
    public CreatedType create()
    {
        return ObjectUtil.cloneSafe(this.getPrototype());
    }

    /**
     * Gets the prototype object that is cloned to create new objects.
     * 
     * @return  The prototype object.
     */
    public CreatedType getPrototype()
    {
        return this.prototype;
    }

    /**
     * Sets the prototype object that is cloned to create new objects. This 
     * object is cloned to store the prototype.
     * 
     * @param   prototype The prototype object.
     */
    public void setPrototype(
        final CreatedType prototype)
    {
        this.prototype = ObjectUtil.cloneSafe(prototype);
    }

    /**
     * A convenience method for creating prototype factories.
     * 
     * @param   <T>
     *      The type of the prototype object to create a factory for.
     * @param   prototype
     *      The prototype object.
     * @return
     *      A new prototype factory that uses the given object as the prototype.
     */
    public static <T extends CloneableSerializable> PrototypeFactory<T> createFactory(
        final T prototype)
    {
        return new PrototypeFactory<T>(prototype);
    }
}
