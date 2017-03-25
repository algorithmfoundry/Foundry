/*
 * File:                VectorMeanCentroidClusterCreator.java
 * Authors:             Jeff Piersol
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright April 28, 2016, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.learning.algorithm.clustering.cluster;

import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.util.AbstractCloneableSerializable;
import java.util.Collection;

/**
 * Implementation of {@link VectorMeanCentroidClusterCreator} for mini-batch
 * clustering.
 *
 * @author Jeff Piersol
 * @since 4.0.0
 */
@PublicationReference(
    author = "Jeff Piersol",
    title = "Parallel Mini-Batch k-means Clustering",
    type = PublicationType.Conference,
    year = 2016,
    publication
    = "to appear",
    url = "to appear"
)
public class VectorMeanMiniBatchCentroidClusterCreator
    extends AbstractCloneableSerializable
    implements ClusterCreator<MiniBatchCentroidCluster, Vector>
{

    private static final long serialVersionUID = 1512395430699067693L;

    public static final VectorMeanMiniBatchCentroidClusterCreator INSTANCE
        = new VectorMeanMiniBatchCentroidClusterCreator();

    public VectorMeanMiniBatchCentroidClusterCreator()
    {
        super();
    }

    @Override
    public MiniBatchCentroidCluster createCluster(
        Collection<? extends Vector> members)
    {
        return new MiniBatchCentroidCluster(members);
    }

}
