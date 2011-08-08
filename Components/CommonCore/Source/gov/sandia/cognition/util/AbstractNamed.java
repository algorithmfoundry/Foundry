/*
 * File:                AbstractNamed.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright July 11, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.util;

import gov.sandia.cognition.annotation.CodeReview;

/**
 * The <code>AbstractNamed</code> class implements the Named interface
 * in a standard way by having a name field inside the object. It is provided
 * for convenience for implementers not having to keep track of this field. It
 * also overrides the toString method to return the name.
 *
 * @author Justin Basilico
 * @since  2.0
 */
@CodeReview(
    reviewer="Kevin R. Dixon",
    date="2007-11-25",
    changesNeeded=false,
    comments="Looks fine"
)
public abstract class AbstractNamed
    extends AbstractCloneableSerializable
    implements Named
{

    /** The name of the object. */
    protected String name;

    /**
     * Creates a new instance of AbstractNamed. The default name is 
     * null.
     */
    protected AbstractNamed()
    {
        this(null);
    }

    /**
     * Creates a new instance of AbstractNamed with the given name.
     *
     * @param  name The name.
     */
    protected AbstractNamed(
        String name)
    {
        super();

        this.setName(name);
    }

    @Override
    public AbstractNamed clone()
    {
        return (AbstractNamed) super.clone();
    }
    
    /**
     * The standard implementation of toString returns the name of the Named
     * Object.
     *
     * @return The name of the Object.
     */
    @Override
    public String toString()
    {
        final String result = this.getName();
        return result == null ? "" : result;
    }

    public String getName()
    {
        return this.name;
    }

    /**
     * Sets the name of this Object.
     *
     * @param  name The new name for the Object.
     */
    public void setName(
        String name)
    {
        this.name = name;
    }

}
