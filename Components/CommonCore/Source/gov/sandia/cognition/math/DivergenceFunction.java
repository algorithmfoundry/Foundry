/*
 * File:                DivergenceFunction.java
 * Authors:             Justin Basilico and Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright February 21, 2006, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.math;

import gov.sandia.cognition.annotation.CodeReview;
import gov.sandia.cognition.annotation.CodeReviews;
import gov.sandia.cognition.util.CloneableSerializable;

/**
 * The DivergenceFunction class defines the functionality of something that
 * computes the divergence between two objects. A divergence function should
 * have the following properties:
 *
 *     g(x, y) >= 0
 *     g(x, x) == 0
 *
 * @param <FirstType> First class to consider
 * @param <SecondType> Second class to consider
 * @author Justin Basilico
 * @author Kevin R. Dixon
 * @since  1.0
 */
@CodeReviews(
    reviews={
        @CodeReview(
            reviewer="Kevin R. Dixon",
            date="2008-02-26",
            changesNeeded=false,
            comments="Looks good."
        ),
        @CodeReview(
            reviewer="Jonathan McClain",
            date="2006-05-16",
            changesNeeded=false,
            comments="Looks good."
        )
    }
)
public interface DivergenceFunction<FirstType, SecondType>
    extends CloneableSerializable
{

    /**
     * Evaluates the divergence between the two given objects.
     *
     * @param  first The first object.
     * @param  second The second object.
     * @return The divergence between the objects.
     */
    public double evaluate(
        FirstType first,
        SecondType second );

}
