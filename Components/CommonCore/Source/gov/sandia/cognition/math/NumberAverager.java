/*
 * File:                NumberAverager.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Dec 11, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 * 
 */

package gov.sandia.cognition.math;

import gov.sandia.cognition.annotation.CodeReview;
import gov.sandia.cognition.util.AbstractCloneableSerializable;
import gov.sandia.cognition.util.Summarizer;
import java.util.Collection;

/**
 * Returns an average (arithmetic mean) of a collection of Numbers
 * @author Kevin R. Dixon
 * @since 2.0
 */
@CodeReview(
    reviewer="Kevin R. Dixon",
    date="2008-02-26",
    changesNeeded=false,
    comments={
        "Made some of the javadoc more descriptive",
        "Otherwise, looks good."
    }
)
public class NumberAverager
    extends AbstractCloneableSerializable
    implements Summarizer<Number, Double>
{

    /**
     * Instance of NumberAverager, since it has no state.
     */
    public static final NumberAverager INSTANCE = new NumberAverager();

    /** 
     * Creates a new instance of NumberAverager 
     */
    public NumberAverager()
    {
    }

    /**
     * Returns the average (arithmetic mean) of a Collection of Numbers,
     * or null if the collection of Numbers are null
     * @param data
     * Collection of Number to compute the average of
     * @return
     * Average (arithmetic mean) of the data, null if no Numbers are given
     */
    public Double summarize(
        Collection<? extends Number> data )
    {
        double sum = 0.0;
        for (Number value : data)
        {
            sum += value.doubleValue();
        }

        Double mean;
        if (data.size() > 0)
        {
            mean = sum / data.size();
        }
        else
        {
            mean = null;
        }

        return mean;
    }

}
