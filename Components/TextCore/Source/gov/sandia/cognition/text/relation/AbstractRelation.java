/*
 * File:                AbstractRelation.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright March 18, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 * See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.text.relation;

import gov.sandia.cognition.util.AbstractCloneableSerializable;

/**
 * An abstract implementation of a relation between two objects. It holds
 * pointers to the source and target objects.
 *
 * @param   <SourceType> The type of the source object of the relation.
 * @param   <TargetType> The type of the target object of the relation.
 * @author  Justin Basilico
 * @since   3.0
 */
public class AbstractRelation<SourceType, TargetType>
    extends AbstractCloneableSerializable
{

    /** The source object of the relation. */
    protected SourceType source;

    /** The target object of the relation. */
    protected TargetType target;

    /**
     * Creates a new {@code AbstractRelation} with null source and target.
     */
    public AbstractRelation()
    {
        this(null, null);
    }

    /**
     * Creates a new {@code AbstractRelation} with the given source and target.
     *
     * @param   source
     *      The source object of the relation.
     * @param   target
     *      The target object of the relation.
     */
    public AbstractRelation(
        final SourceType source,
        final TargetType target)
    {
        super();

        this.setSource(source);
        this.setTarget(target);

    }

    /**
     * Gets the source object of the relation.
     *
     * @return
     *      The source object of the relation.
     */
    public SourceType getSource()
    {
        return this.source;
    }

    /**
     * Sets the source object of the relation.
     *
     * @param   source
     *      The source object of the relation.
     */
    protected void setSource(
        final SourceType source)
    {
        this.source = source;
    }

    /**
     * Sets the target object of the relation.
     *
     * @return
     *      The target object of the relation.
     */
    public TargetType getTarget()
    {
        return this.target;
    }

    /**
     * Sets the target object of the relation.
     *
     * @param   target
     *      The target object of the relation.
     */
    protected void setTarget(
        final TargetType target)
    {
        this.target = target;
    }

}
