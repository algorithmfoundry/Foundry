/*
 * File:                Ensemble.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright October 8, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.learning.algorithm.ensemble;

import gov.sandia.cognition.annotation.CodeReview;
import java.util.Collection;

/**
 * The {@code Ensemble} interface defines the functionality of an "ensemble"
 * that is typically created by combining together the result of multiple 
 * learning algorithms.
 *
 * @param   <MemberType> The type of the members of the ensemble.
 * @author  Justin Basilico
 * @since   2.0
 */
@CodeReview(
    reviewer="Kevin R. Dixon",
    date="2008-07-23",
    changesNeeded=false,
    comments="Looks fine."
)
public interface Ensemble<MemberType>
{
    /**
     * Gets the members of the ensemble.
     *
     * @return The members of the ensemble.
     */
    public Collection<MemberType> getMembers();
}
