/*
 * File:                AbstractWeighted.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Dec 19, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 * 
 */

package gov.sandia.cognition.util;

import gov.sandia.cognition.annotation.CodeReview;

/**
 * Container class for a Weighted object
 * @author Kevin R. Dixon
 * @since 2.0
 */
@CodeReview(
    reviewer="Kevin R. Dixon",
    date="2008-10-02",
    changesNeeded=false,
    comments="Looks fine."
)
public class AbstractWeighted
    extends AbstractCloneableSerializable
    implements Weighted
{
    
    /**
     * Default weight, {@value}
     */
    public final static double DEFAULT_WEIGHT = 1.0;

    /**
     * The weight
     */
    protected double weight;
    
    /** 
     * Creates a new instance of AbstractWeighted 
     */
    public AbstractWeighted()
    {
        this( DEFAULT_WEIGHT );
    }

    /**
     * Creates a new instance of AbstractWeighted 
     * @param weight
     * The weight
     */
    public AbstractWeighted(
        double weight )
    {
        this.setWeight(weight);
    }

    public double getWeight()
    {
        return this.weight;
    }

    /**
     * Setter for weight
     * @param weight
     * The weight
     */
    public void setWeight(
        double weight )
    {
        this.weight = weight;
    }

}
