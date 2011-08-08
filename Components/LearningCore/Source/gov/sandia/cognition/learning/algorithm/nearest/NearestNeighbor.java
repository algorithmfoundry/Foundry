/*
 * File:                NearestNeighbor.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Aug 10, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.learning.algorithm.nearest;

import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.evaluator.Evaluator;
import gov.sandia.cognition.learning.data.InputOutputPair;
import gov.sandia.cognition.math.DivergenceFunction;
import gov.sandia.cognition.util.CloneableSerializable;
import java.util.Collection;

/**
 * The {@code NearestNeighborExhaustive} class implements a simple evaluator
 * that looks up a given input object in a collection of input-output pair
 * examples and returns the output associated with the most similar input.
 * Similarity is judged using the given divergence function. This is an
 * implementation of the standard nearest-neighbor algorithm, which is a corner
 * case of the k-nearest neighbor algorithm, implemented in the
 * {@code KNearestNeighbor} class.
 * @param <InputType> Type of data upon which the KNearestNeighbor operates,
 * something like Vector, Double, or String
 * @param <OutputType> Output of the evaluator, like Matrix, Double, String
 * @author Kevin R. Dixon
 * @since 3.0
 */
@PublicationReference(
    author="Wikipedia",
    title="k-nearest neighbor algorithm",
    type=PublicationType.WebPage,
    year=2008,
    url="http://en.wikipedia.org/wiki/K-nearest_neighbor_algorithm"
)
public interface NearestNeighbor<InputType,OutputType>
    extends Evaluator<InputType, OutputType>,
    CloneableSerializable
{

    /**
     * Adds the Pair to the data.
     * @param value
     * Value to add to the data.
     */
    public void add(
        InputOutputPair<? extends InputType, OutputType> value);

    /**
     * Getter for divergenceFunction
     * @return
     * Divergence function that determines how "far" two objects are apart
     */
    public DivergenceFunction<? super InputType, ? super InputType> getDivergenceFunction();

    /**
     * Gets the data from which this computes the nearest neighbors.
     * @return
     * Collection of the data Pairs.
     */
    public Collection<InputOutputPair<? extends InputType, OutputType>> getData();
    
}
