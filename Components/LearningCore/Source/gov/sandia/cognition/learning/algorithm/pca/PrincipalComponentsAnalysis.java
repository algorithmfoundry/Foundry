/*
 * File:                PrincipalComponentsAnalysis.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright October 9, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.learning.algorithm.pca;

import gov.sandia.cognition.annotation.CodeReview;
import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.learning.algorithm.BatchLearner;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.util.CloneableSerializable;
import java.util.Collection;

/**
 * Principal Components Analysis is a family of algorithms that map from a
 * high-dimensional input space to a low-dimensional output space.  This
 * mapping attempts to capture the orthogonal components of maximum variance
 * of the input space.
 *
 * @author Kevin R. Dixon
 * @since  2.0
 *
 */
@CodeReview(
    reviewer="Kevin R. Dixon",
    date="2008-07-23",
    changesNeeded=false,
    comments={
        "Added PublicationReference to Wikipedia's article on PCA.",
        "Minor change to javadoc.",
        "Looks fine."
    }
)
@PublicationReference(
    author="Wikipedia",
    title="Principal components analysis",
    type=PublicationType.WebPage,
    year=2008,
    url="http://en.wikipedia.org/wiki/Principal_components_analysis",
    notes="The Wikipedia page on PCA is a too opinionated, but the gist is there."
)
public interface PrincipalComponentsAnalysis
    extends BatchLearner<Collection<Vector>, PrincipalComponentsAnalysisFunction>,
    CloneableSerializable
{

    /**
     * Gets the number of components used in the PCA dimension reduction.
     * @return 
     * Number of components used in the PCA dimension reduction
     */
    public int getNumComponents();

    /**
     * Gets the VectorFunction that maps from the input space to the reduced
     * output space of "getNumComponents" dimensions.
     * @return 
     * PCA function that reduces the dimensionality of the input space to
     * a (hopefully) simpler and smaller output space
     */
    public PrincipalComponentsAnalysisFunction getResult();

}
