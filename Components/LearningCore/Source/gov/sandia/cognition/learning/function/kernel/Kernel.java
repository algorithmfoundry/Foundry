/*
 * File:                Kernel.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright July 19, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */
package gov.sandia.cognition.learning.function.kernel;

import gov.sandia.cognition.annotation.CodeReview;
import gov.sandia.cognition.util.CloneableSerializable;

/**
 * The <code>Kernel</code> interface the functionality required from an object
 * that implements a kernel function. A kernel is a function that takes two 
 * arguments and returns a double that is equivalent to the inner-product 
 * between two vectors in a high-dimensional space. That is, a kernel must 
 * satisfy Mercer's conditions and produce a matrix that is positive 
 * semi-definite. Typically the inner-product is not actually computed by 
 * creating the high-dimensional representation, but instead is computed quickly 
 * such that the result would be equivalent to operating in that 
 * high-dimensional space.
 * 
 * It is recommended that a {@link Kernel} implement 
 * {@link CloneableSerializable}, though it is not required.
 *
 * @param  <InputType> The type of the input to the Kernel. For example, Vector.
 * @author Justin Basilico
 * @since  2.0
 */
@CodeReview(
    reviewer="Kevin R. Dixon",
    date="2009-07-08",
    changesNeeded=false,
    comments="Looks fine."
)
public interface Kernel<InputType>
{
    /**
     * The role of a kernel is to evaluate some function that is equivalent to 
     * an inner product in some vector space. The kernel must satisfy Mercer's 
     * conditions in that the kernel matrix must be positive semi-definite. 
     *
     * @param  x The first item.
     * @param  y The second item.
     * @return The kernel evaluated on the two given objects.
     */
    public double evaluate(
        InputType x,
        InputType y);
}
