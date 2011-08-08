/*
 * File:                Binner.java
 * Authors:             Zachary Benz
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Nov 27, 2007, Sandia Corporation.  Under the terms of Contract 
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by 
 * or on behalf of the U.S. Government. Export of this program may require a 
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 * 
 */

package gov.sandia.cognition.statistics.method;

import gov.sandia.cognition.annotation.CodeReview;
import java.util.Set;

/**
 * Defines the functionality for a class that assigns values to some sort of
 * bin.
 *
 * @param   <ValueType>
 *      Value type to be binned.
 * @param   <BinnedType>
 *      Type that values are binned as.
 * @author  Zachary Benz
 * @since   2.0
 */
@CodeReview(
    reviewer = "Justin Basilico",
    date = "2009-05-29",
    changesNeeded = false,
    comments = "Changed the interface slightly. Cleaned up the javadoc."
)
public interface Binner<ValueType, BinnedType>
{

    /**
     * Finds the bin for the provided value, or null if a corresponding bin
     * for the value does not exist.
     * 
     * @param   value
     *      The value to get the bin for.
     * @return
     *      The bin for the provided value, or null if a corresponding bin
     *      for the value does not exist
     */
    public BinnedType findBin(
        final ValueType value);

    /**
     * Gets the total number of bins.
     * 
     * @return 
     *      The total number of bins.
     */
    public int getBinCount();

    /**
     * Gets the set of bins that the binner is using.
     *
     * @return
     *      The set of bins.
     */
    public Set<BinnedType> getBinSet();

}
