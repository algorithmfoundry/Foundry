/*
 * File:                AbstractConfidenceStatistic.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright August 16, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.statistics.method;

import gov.sandia.cognition.math.ProbabilityUtil;
import gov.sandia.cognition.util.AbstractCloneableSerializable;
import gov.sandia.cognition.util.ObjectUtil;

/**
 * Abstract implementation of ConfidenceStatistic.
 *
 * @author Kevin R. Dixon
 * @since  2.0
 *
 */
public abstract class AbstractConfidenceStatistic
    extends AbstractCloneableSerializable
    implements ConfidenceStatistic
{
    
    /**
     * Probability of the null hypothesis, often called "p-value"
     */
    protected double nullHypothesisProbability;
    
    /**
     * Creates a new instance of AbstractConfidenceStatistic
     * @param nullHypothesisProbability 
     * Probability of the null hypothesis, often called "p-value"
     */
    public AbstractConfidenceStatistic(
        double nullHypothesisProbability )
    {
        this.setNullHypothesisProbability( nullHypothesisProbability );
    }
    
    /**
     * Getter for nullHypothesisProbability
     * @return 
     * Probability of the null hypothesis, often called "p-value"
     */
    @Override
    public double getNullHypothesisProbability()
    {
        return this.nullHypothesisProbability;
    }
    

    /**
     * Setter for nullHypothesisProbability
     * @param nullHypothesisProbability 
     * Probability of the null hypothesis, often called "p-value"
     */
    protected void setNullHypothesisProbability(
        double nullHypothesisProbability)
    {
        ProbabilityUtil.assertIsProbability(nullHypothesisProbability);
        this.nullHypothesisProbability = nullHypothesisProbability;
    }

    @Override
    public String toString()
    {
        return ObjectUtil.toString(this);
    }

}
