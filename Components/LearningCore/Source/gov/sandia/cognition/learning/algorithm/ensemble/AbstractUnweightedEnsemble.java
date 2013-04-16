/*
 * File:            AbstractUnweightedEnsemble.java
 * Authors:         Justin Basilico
 * Project:         Cognitive Foundry Learning Core
 * 
 * Copyright 2011 Cognitive Foundry. All rights reserved.
 */

package gov.sandia.cognition.learning.algorithm.ensemble;

import gov.sandia.cognition.util.AbstractCloneableSerializable;
import gov.sandia.cognition.util.ArgumentChecker;
import gov.sandia.cognition.util.ObjectUtil;
import java.util.ArrayList;
import java.util.List;

/**
 * An abstract implementation of the {@code Ensemble} interface for
 * unweighted ensembles. Implements the ensemble as a list.
 *
 * @param   <MemberType>
 *      The type of the ensemble members.
 * @author  Justin Basilico
 * @since   3.3.3
 */
public abstract class AbstractUnweightedEnsemble<MemberType>
    extends AbstractCloneableSerializable
    implements Ensemble<MemberType>
{

    /** The members of the ensemble. */
    protected List<MemberType> members;

    /**
     * Creates a new {@code AbstractUnweightedEnsemble}.
     */
    public AbstractUnweightedEnsemble()
    {
        this(new ArrayList<MemberType>());
    }

    /**
     * Creates a new {@code AbstractUnweightedEnsemble} with the given
     * list of members.
     *
     * @param   members
     *      The list of ensemble members.
     */
    public AbstractUnweightedEnsemble(
        final List<MemberType> members)
    {
        super();

        this.setMembers(members);
    }

 
    @Override
    public AbstractUnweightedEnsemble<MemberType> clone()
    {
        @SuppressWarnings("unchecked")
        final AbstractUnweightedEnsemble<MemberType> clone =
            (AbstractUnweightedEnsemble<MemberType>) super.clone();

        clone.members = ObjectUtil.cloneSmartElementsAsArrayList(this.members);

        return clone;
    }

    /**
     * Adds a given member to the ensemble.
     *
     * @param   member
     *      The ensemble member to add.
     */
    public void add(
        final MemberType member)
    {
        ArgumentChecker.assertIsNotNull("member", member);
        this.getMembers().add(member);
    }

    /**
     * Gets the number of members in the ensemble.
     *
     * @return
     *      The number of members in the ensemble.
     */
    public int getMemberCount()
    {
        return this.getMembers().size();
    }

    @Override
    public List<MemberType> getMembers()
    {
        return this.members;
    }

    /**
     * Sets the list of ensemble members.
     *
     * @param   members
     *      The list of ensemble members.
     */
    public void setMembers(
        final List<MemberType> members)
    {
        this.members = members;
    }

}
