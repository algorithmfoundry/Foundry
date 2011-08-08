/*
 * File:                GaussianClusterDivergenceFunction.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright March 22, 2006, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.learning.algorithm.clustering.divergence;

import gov.sandia.cognition.annotation.CodeReview;
import gov.sandia.cognition.learning.algorithm.clustering.cluster.GaussianCluster;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.util.AbstractCloneableSerializable;

/**
 * The GaussianClusterDivergenceFunction class implements a divergence
 * function between a Gaussian cluster and a vector, which is calculated
 * by finding the likelihood that the vector was generated from that Gaussian
 * and then returning the negative of the likelihood since it is a divergence
 * measure, not a similarity measure.
 *
 * @author Justin Basilico
 * @since 1.0
 */
@CodeReview(
    reviewer="Kevin R. Dixon",
    date="2008-07-23",
    changesNeeded=false,
    comments="Looks fine."
)
public class GaussianClusterDivergenceFunction
    extends AbstractCloneableSerializable
    implements ClusterDivergenceFunction<GaussianCluster,Vector>
{
    /** An instance of the class since it has no internal data. */
    public static final GaussianClusterDivergenceFunction INSTANCE =
        new GaussianClusterDivergenceFunction();
    
    /**
     * Creates a new instance of GaussianClusterDivergenceFunction.
     */
    public GaussianClusterDivergenceFunction()
    {
        super();
    }
    
    /**
     * Evaluates the divergence between the Gaussian cluster and the given
     * vector, which is the negative of likelihood that the cluster was 
     * generated.
     *
     * @param  first The Gaussian cluster.
     * @param  second The vector to calculate the divergence to
     * @return The divergence between the cluster and vector.
     */
    public double evaluate(
        GaussianCluster first, 
        Vector second)
    {
        // We return the negative of the likelihood since we want to minimize
        // the divergence.
        return -first.getGaussian().evaluate(second);
    }
}
