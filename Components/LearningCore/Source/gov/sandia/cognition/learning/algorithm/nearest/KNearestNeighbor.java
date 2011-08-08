/*
 * File:                KNearestNeighbor.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Aug 5, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.learning.algorithm.nearest;

import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.util.Summarizer;

/**
 * A generic k-nearest-neighbor classifier.  This classifier simply looks at
 * the nearest "k" neighbors to a point and returns the average of them.  Thus,
 * the learner is trivial, but the lookups (evaluations) are expensive.
 * For example, with k=1, then k-nearest-neighbor simply returns the nearest
 * data point to a given input.  And so forth.
 *
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
public interface KNearestNeighbor<InputType, OutputType> 
    extends NearestNeighbor<InputType,OutputType>
{

    /**
     * The default value for k is {@value}.
     */
    public static final int DEFAULT_K = 1;

    /**
     * Getter for averager.
     * @return
     * Creates a single object from a collection of data
     */
    public Summarizer<? super OutputType, ? extends OutputType> getAverager();

    /**
     * Setter for averager.
     * @param averager
     * Creates a single object from a collection of data
     */
    public void setAverager(
        Summarizer<? super OutputType, ? extends OutputType> averager);

    /**
     * Getter for k
     * @return
     * Number of neighbors to consider, must be greater than zero
     */
    public int getK();

    /**
     * Setter for k
     * @param k
     * Number of neighbors to consider, must be greater than zero
     */
    public void setK(
        int k);
    
}
