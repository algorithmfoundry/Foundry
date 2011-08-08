/*
 * File:                Perturber.java
 * Authors:             Justin Basilico and Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright February 20, 2006, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.learning.algorithm.annealing;

import gov.sandia.cognition.annotation.CodeReview;
import gov.sandia.cognition.annotation.CodeReviews;
import gov.sandia.cognition.util.CloneableSerializable;

/**
 * The Perturber interface defines the functionality of an object that can
 * take an object and perturb it, returning the perturbed value.
 *
 * @param <PerturbedType> Class that is given to, and returned from, the
 *  {@code perturb()} method
 * @author Justin Basilico
 * @author Kevin R. Dixon
 * @since  1.0
 */
@CodeReviews(
    reviews={
        @CodeReview(
            reviewer="Kevin R. Dixon",
            date="2008-07-22",
            changesNeeded=false,
            comments={
                "Moved previous code review to annotation.",
                "Fixed minor typo in javadoc.",
                "Interface looks fine."
            }
        )
        ,
        @CodeReview(
            reviewer="Justin Basilico",
            date="2006-10-02",
            changesNeeded=false,
            comments="Interface looks fine."
        )
    }
)
public interface Perturber<PerturbedType>
    extends CloneableSerializable
{
    /**
     * Perturbs the given object and returns the perturbed version.
     *
     * @param input The object to perturb. It should not be changed.
     * @return The perturbed version of the object.
     */
    public PerturbedType perturb(
        final PerturbedType input);
}
