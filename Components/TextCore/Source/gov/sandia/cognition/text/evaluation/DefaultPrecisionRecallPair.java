/*
 * File:                DefaultPrecisionRecallPair.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright February 19, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 * See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.text.evaluation;

import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.util.AbstractCloneableSerializable;

/**
 * A default implementation of the {@code PrecisionRecallPair} interface.
 * 
 * @author  Justin Basilico
 * @since   3.0
 */
@PublicationReference
(
    author="Wikipedia",
    title="Precision and Recall",
    type=PublicationType.WebPage,
    url="http://en.wikipedia.org/wiki/Precision_and_recall",
    year=2009
)
public class DefaultPrecisionRecallPair
     extends AbstractCloneableSerializable
     implements PrecisionRecallPair
{
    /** The default precision is {@value}. */
    public static final double DEFAULT_PRECISION = 0.0;

    /** The default recall is {@value}. */
    public static final double DEFAULT_RECALL = 0.0;

    /** The precision value. */
    private double precision;

    /** The recall value. */
    private double recall;

    /**
     * Creates a new {@code DefaultPrecisionRecallPair}.
     */
    public DefaultPrecisionRecallPair()
    {
        this(DEFAULT_PRECISION, DEFAULT_RECALL);
    }

    /**
     * Creates a new {@code DefaultPrecisionRecallPair}.
     *
     * @param   precision
     *      The precision. Must be between 0.0 and 1.0, inclusive.
     * @param   recall
     *      The recall. Must be between 0.0 and 1.0, inclusive.
     */
    public DefaultPrecisionRecallPair(
        final double precision,
        final double recall)
    {
        super();

        this.setPrecision(precision);
        this.setRecall(recall);
    }

    public double getPrecision()
    {
        return this.precision;
    }

    /**
     * Sets the precision. Must be between 0.0 and 1.0, inclusive.
     *
     * @param   precision
     *      The precision.
     */
    public void setPrecision(
        final double precision)
    {
        if (precision < 0.0 || precision > 1.0)
        {
            throw new IllegalArgumentException("precision must be between 0.0 and 1.0, inclusive.");
        }

        this.precision = precision;
    }

    public double getRecall()
    {
        return this.recall;
    }

    /**
     * Sets the recall. Must be between 0.0 and 1.0, inclusive.
     *
     * @param   recall
     *      The recall.
     */
    public void setRecall(
        final double recall)
    {
        if (recall < 0.0 || recall > 1.0)
        {
            throw new IllegalArgumentException("recall must be between 0.0 and 1.0, inclusive.");
        }

        this.recall = recall;
    }

    /**
     * Gets the first element, which is the precision.
     *
     * @return
     *      The precision.
     */
    public Double getFirst()
    {
        return this.getPrecision();
    }

    /**
     * Gets the second element, which is the recall.
     *
     * @return
     *      The recall.
     */
    public Double getSecond()
    {
        return this.getRecall();
    }

    @Override
    public String toString()
    {
        return "[precision = " + this.getPrecision() + ", recall = " + this.getRecall() + "]";
    }


}
