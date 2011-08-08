/*
 * File:                Evaluator.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Framework Lite
 *
 * Copyright February 22, 2006, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.evaluator;

import gov.sandia.cognition.annotation.CodeReview;
import gov.sandia.cognition.annotation.CodeReviews;

/**
 * The {@code Evaluator} interface is a general interface to a function
 * that can take an input and produce an output. It can be treated as a either
 * a means of creating simple "delegate" type objects in Java or it can be
 * treated as a "black box" component to provide some functionality.
 * 
 * @param  <InputType> The type of the input the evaluator can use.
 * @param  <OutputType> The type of the output the evaluator will produce.
 * @author Justin Basilico
 * @since  1.0
 * @see    StatefulEvaluator
 */
@CodeReviews(
    reviews={
        @CodeReview(
            reviewer="Kevin R. Dixon",
            date="2008-02-08",
            changesNeeded=false,
            comments="Looks fine."
        ),
        @CodeReview(
            reviewer="Jonathan McClain",
            date="2006-05-16",
            changesNeeded=false,
            comments="Interface looks good."
        )
    }
)
public interface Evaluator<InputType, OutputType>
{

    /**
     * Evaluates the function on the given input and returns the output. 
     *
     * @param input The input to evaluate.
     * @return The output produced by evaluating the input.
     */
    OutputType evaluate(
        InputType input );

}
