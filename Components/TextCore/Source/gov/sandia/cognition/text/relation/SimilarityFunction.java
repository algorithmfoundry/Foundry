/*
 * File:                SimilarityFunction.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright January 19, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 * See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.text.relation;

import gov.sandia.cognition.math.DivergenceFunction;

/**
 * Defines the functionality of a similarity function that computes the
 * similarity between two objects.
 * 
 * @param   <FromType>
 *      The type that the similarity is from.
 * @param   <ToType>
 *      The type that the similarity is to.
 * @author  Justin Basilico
 * @since   3.0
 */
public interface SimilarityFunction<FromType, ToType>
{

    /**
     * Evaluates the similarity between the two given objects. Similarity
     * between two objects is high if those objects are similar and low if they
     * are not similar. Typically, this is a value that is greater than or
     * equal to zero that is higher the more similar two objects are. If the
     * two types are the same, then the rule f(x, y) = f(y, x) must apply.
     *
     * @param   from
     *      The object the similarity is from.
     * @param   to
     *      The object the similarity is to.
     * @return
     *      The similarity between the two objects.
     */
    public double evaluate(
        final FromType from,
        final ToType to);

    /**
     * Converts the similarity function into a divergence function. A diverge
     *
     * @return
     *      The divergence function version of this similarity function.
     */
    public DivergenceFunction<FromType, ToType> asDivergence();
}
