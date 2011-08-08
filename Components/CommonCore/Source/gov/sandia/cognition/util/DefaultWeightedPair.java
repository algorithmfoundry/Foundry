/*
 * File:                DefaultWeightedPair.java
 * Authors:             Justin Basilico and Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright July 19, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 *
 */

package gov.sandia.cognition.util;

/**
 * The {@code DefaultWeightedPair} class extends the {@code DefaultPair} class 
 * to add a weight to the pair.
 *
 * @param   <FirstType> Type of the first object in the pair.
 * @param   <SecondType> Type of the second object in the pair.
 * @author  Justin Basilico
 * @author  Kevin R. Dixon
 * @since   2.0
 */
public class DefaultWeightedPair<FirstType, SecondType>
    extends DefaultPair<FirstType, SecondType>
    implements WeightedPair<FirstType, SecondType>
{

    /** The default weight for the pair is {@value}. */
    public static final double DEFAULT_WEIGHT = 0.0;

    /** The weight for the pair. */
    protected double weight;

    /**
     * Creates a new instance of {@code DefaultWeightedPair}.
     */
    public DefaultWeightedPair()
    {
        this(null, null, DEFAULT_WEIGHT);
    }

    /**
     * Creates a new instance of {@code DefaultWeightedPair}.
     * 
     * @param first The first object in the pair.
     * @param second The second object in the pair.
     * @param weight The weight for the pair.
     */
    public DefaultWeightedPair(
        final FirstType first,
        final SecondType second,
        final double weight)
    {
        super(first, second);

        this.setWeight(weight);
    }

    /**
     * Sets the weight of the pair.
     * 
     * @return   The weight of the pair.
     */
    public double getWeight()
    {
        return this.weight;
    }

    /**
     * Gets the weight of the pair.
     * 
     * @param   weight The weight of the pair.
     */
    public void setWeight(
        final double weight)
    {
        this.weight = weight;
    }

}
