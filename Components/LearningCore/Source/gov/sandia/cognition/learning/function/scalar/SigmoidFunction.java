/*
 * File:                SigmoidFunction.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright May 18, 2006, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.learning.function.scalar;

import gov.sandia.cognition.math.AbstractDifferentiableUnivariateScalarFunction;
import gov.sandia.cognition.annotation.CodeReview;
import gov.sandia.cognition.annotation.CodeReviews;
import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationType;

/**
 * An implementation of a sigmoid squashing function.
 *
 * @author Kevin R. Dixon
 * @since  1.0
 */
@CodeReviews(
    reviews={
        @CodeReview(
            reviewer="Kevin R. Dixon",
            date="2009-07-06",
            changesNeeded=false,
            comments={
                "Made clone() call super.clone().",
                "Otherwise, class looks fine."
            }
        )
        ,
        @CodeReview(
            reviewer="Justin Basilico",
            date="2006-10-05",
            changesNeeded=false,
            comments="Class looks fine."
        )
    }
)
@PublicationReference(
    author="Wikipedia",
    title="Sigmoid function",
    type=PublicationType.WebPage,
    year=2008,
    url="http://en.wikipedia.org/wiki/Sigmoid_function"
)
public class SigmoidFunction
    extends AbstractDifferentiableUnivariateScalarFunction
{

    /**
     * Creates a new instance of SigmoidFunction
     */
    public SigmoidFunction()
    {
        super();
    }

    @Override
    public SigmoidFunction clone()
    {
        return (SigmoidFunction) super.clone();
    }

    /**
     * Evaluates the squashing function on the given input value.
     *
     * @param  input The input value to squash.
     * @return The output of the sigmoid.
     */
    public double evaluate(
        double input )
    {
        return 1.0 / (1.0 + Math.exp( -input ));
    }

    public double differentiate(
        double input )
    {
        double y = this.evaluate( input );
        return y * (1.0 - y);
    }

}
