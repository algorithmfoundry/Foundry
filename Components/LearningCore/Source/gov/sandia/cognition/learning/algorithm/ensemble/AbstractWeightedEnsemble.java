/*
 * File:            AbstractWeightedEnsemble.java
 * Authors:         Justin Basilico
 * Project:         Cognitive Foundry Learning Core
 * 
 * Copyright 2011 Cognitive Foundry. All rights reserved.
 */

package gov.sandia.cognition.learning.algorithm.ensemble;

import gov.sandia.cognition.util.AbstractCloneableSerializable;
import gov.sandia.cognition.util.ArgumentChecker;
import gov.sandia.cognition.util.DefaultWeightedValue;
import gov.sandia.cognition.util.ObjectUtil;
import gov.sandia.cognition.util.WeightedValue;
import java.util.ArrayList;
import java.util.List;

/**
 * An abstract implementation of the {@code Ensemble} interface for ensembles
 * that have a weight associated with each member. Implements the ensemble as a
 * list of weighted values.
 *
 * @param   <MemberType>
 *      The type of the members of this weighted ensemble.
 * @author  Justin Basilico
 * @since   3.3.3
 */
public class AbstractWeightedEnsemble<MemberType>
    extends AbstractCloneableSerializable
    implements Ensemble<WeightedValue<MemberType>>
{

    /** The default weight when adding a member is {@value}. */
    public static final double DEFAULT_WEIGHT = 1.0;

    /** The members of the ensemble. */
    protected List<WeightedValue<MemberType>> members;

    /**
     * Creates a new, empty of AbstractWeightedEnsemble.
     */
    public AbstractWeightedEnsemble()
    {
        this(new ArrayList<WeightedValue<MemberType>>());
    }

    /**
     * Creates a new instance of AbstractWeightedEnsemble.
     *
     * @param   members
     *      The members of the ensemble.
     */
    public AbstractWeightedEnsemble(
        final List<WeightedValue<MemberType>> members)
    {
        super();

        this.setMembers(members);
    }

    @Override
    public AbstractWeightedEnsemble<MemberType> clone()
    {
        @SuppressWarnings("unchecked")
        final AbstractWeightedEnsemble<MemberType> clone = 
            (AbstractWeightedEnsemble<MemberType>) super.clone();

        clone.members = ObjectUtil.cloneSmartElementsAsArrayList(this.members);

        return clone;
    }

    /**
     * Adds the given regression function with a default weight of 1.0.
     *
     * @param  member
     *      The regression function to add.
     */
    public void add(
        final MemberType member)
    {
        this.add(member, DEFAULT_WEIGHT);
    }

    /**
     * Adds the given regression function with a given weight.
     *
     * @param   member
     *      The regression function to add.
     * @param   weight
     *      The weight for the new member.
     */
    public void add(
        final MemberType member,
        final double weight)
    {
        ArgumentChecker.assertIsNotNull("categorizer", member);

        final WeightedValue<MemberType> weighted =
            new DefaultWeightedValue<MemberType>(member, weight);

        this.getMembers().add(weighted);
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

    /**
     * Gets the sum of the weights of the ensemble members.
     *
     * @return
     *      The sum of the ensemble member weights.
     */
    public double getWeightSum()
    {
        double sum = 0.0;
        for (WeightedValue<?> weighted : this.getMembers())
        {
            sum += weighted.getWeight();
        }
        return sum;
    }

    /**
     * Gets the members of the ensemble.
     *
     * @return The members of the ensemble.
     */
    @Override
    public List<WeightedValue<MemberType>> getMembers()
    {
        return this.members;
    }

    /**
     * Sets the members of the ensemble.
     *
     * @param members The members of the ensemble.
     */
    public void setMembers(
        final List<WeightedValue<MemberType>> members)
    {
        this.members = members;
    }

}
