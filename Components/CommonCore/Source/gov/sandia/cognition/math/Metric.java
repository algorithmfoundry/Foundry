/*
 * File:                Metric.java
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
import gov.sandia.cognition.annotation.CodeReviewResponse;
import gov.sandia.cognition.annotation.CodeReviews;
import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationType;

/**
 * A metric is a non-negative function that satisfies the following properties
 *
 *     g(x, y) + g(y, z) >= g(x, z)
 *               g(x, y) == g(y, x)
 *               g(x, x) == 0.
 * In other words, a metric is a semimetric that obeys the triangle inequality.
 *
 * @param <EvaluatedType> Class of objects that this metric can evaluate
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
            date="2006-05-15",
            changesNeeded=true,
            comments={
                "Code is fine, but is it ok to write a new interface that inherits from DivergenceFunction and adds no new functionality?",
                "I can see the value of specifying that a Metric is a special type of DivergenceFunction, but it seems a little overboard to go about making a whole new interface to do it."
            },
            response=@CodeReviewResponse(
                respondent="Justin Basilico",
                date="2006-05-16",
                moreChangesNeeded=false,
                comments={
                    "The Metric interface exists for two reasons.",
                    "The first is that a Metric is a special type of divergence function that obeys special properties that are documented in the class.",
                    "The second is that a Metric is a divergence function between two elements of the same type, which is implied how it changes the DivergenceFunction interface to use the same generic parameter."
                }
            )
        )
    }
)
@PublicationReference(
    author="Wikipedia",
    title="Metric (mathematics)",
    type=PublicationType.WebPage,
    year=2009,
    url="http://en.wikipedia.org/wiki/Metric_(mathematics)"
)
public interface Metric<EvaluatedType>
    extends Semimetric<EvaluatedType>
{
}
