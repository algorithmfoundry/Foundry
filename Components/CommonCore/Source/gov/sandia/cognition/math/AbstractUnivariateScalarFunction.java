/*
 * File:                AbstractScalarFunction.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright November 1, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.math;

import gov.sandia.cognition.annotation.CodeReview;
import gov.sandia.cognition.annotation.CodeReviews;

/**
 * Abstract implementation of ScalarFunction where the evaluate(Double) method
 * calls back into the evaluate(double) method.
 *
 * @author Kevin R. Dixon
 * @since  2.0
 */
@CodeReviews(
    reviews={
        @CodeReview(
            reviewer="Kevin R. Dixon",
            date="2008-12-02",
            changesNeeded=false,
            comments={
                "Now extends AbstractCloneableSerializeable.",
                "Removed extraneous javadoc.",
                "Otherwise, class looks fine."
            }
        )
        ,
        @CodeReview(
            reviewer="Kevin R. Dixon",
            date="2008-02-08",
            changesNeeded=false,
            comments="Looks fine."
        )
    }
)
public abstract class AbstractUnivariateScalarFunction
    extends AbstractScalarFunction<Double>
    implements UnivariateScalarFunction
{
    
    /**
     * Creates a new {@code AbstractUnivariateScalarFunction}.
     */
    public AbstractUnivariateScalarFunction()
    {
        super();
    }

    @Override
    public Double evaluate(
        final Double input)
    {
        return this.evaluate((double) input);
    }

    @Override
    public double evaluateAsDouble(
        final Double input)
    {
        return this.evaluate((double) input);
    }

}
