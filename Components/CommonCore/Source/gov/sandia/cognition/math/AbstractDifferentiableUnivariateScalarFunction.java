/*
 * File:                AbstractDifferentiableUnivariateScalarFunction.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Feb 10, 2008, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 * 
 */

package gov.sandia.cognition.math;

import gov.sandia.cognition.annotation.CodeReview;

/**
 * Partial implementation of DifferentiableUnivariateScalarFunction that 
 * implements the differentiate(Double) method with a callback to the
 * differentiate(double) method, so that a concrete class only to implement
 * the differentiate(double) method
 * 
 * @author Kevin R. Dixon
 * @since 2.1
 */
@CodeReview(
    reviewer="Kevin R. Dixon",
    date="2008-12-02",
    changesNeeded=false,
    comments={
        "Looks fine."
    }
)
public abstract class AbstractDifferentiableUnivariateScalarFunction
    extends AbstractUnivariateScalarFunction
    implements DifferentiableUnivariateScalarFunction
{

    /** 
     * Creates a new instance of AbstractDifferentiableUnivariateScalarFunction 
     */
    public AbstractDifferentiableUnivariateScalarFunction()
    {
    }

    public Double differentiate(
        Double input )
    {
        return this.differentiate( input.doubleValue() );
    }

}
