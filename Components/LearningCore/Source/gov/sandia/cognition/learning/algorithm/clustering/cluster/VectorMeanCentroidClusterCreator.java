/*
 * File:                VectorMeanCentroidClusterCreator.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright March 20, 2006, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.learning.algorithm.clustering.cluster;

import gov.sandia.cognition.annotation.CodeReview;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.util.AbstractCloneableSerializable;
import java.util.Collection;

/**
 * The <code>VectorMeanCentroidClusterCreator</code> class implements
 * a cluster creator for centroid clusters where the centroid is the 
 * mean of the vectors that are members of the cluster.
 *
 * @author Justin Basilico
 * @since 1.0
 */
@CodeReview(
    reviewer="Kevin R. Dixon",
    date="2008-07-22",
    changesNeeded=false,
    comments="Code generally looks fine."
)
public class VectorMeanCentroidClusterCreator
    extends AbstractCloneableSerializable
    implements ClusterCreator<CentroidCluster<Vector>, Vector>
{
    /** An instance of this class since it has no internal data. */
    public static final VectorMeanCentroidClusterCreator INSTANCE = 
        new VectorMeanCentroidClusterCreator();
    
    /**
     * Creates a new instance of VectorMeanCentroidClusterCreator
     */
    public VectorMeanCentroidClusterCreator()
    {
        super();
    }

    public CentroidCluster<Vector> createCluster(
        Collection<Vector> members)
    {
        if ( members == null )
        {
            // Error: Members is null.
            throw new NullPointerException("The members cannot be null.");
        }
        else if ( members.size() <= 0 )
        {
            // No members to create the cluster from.
            return null;
        }
        
        // We are going to create the mean centroid of the cluster.
        Vector centroid = null;
        for ( Vector member : members )
        {
            if ( centroid == null )
            {
                centroid = member.clone();
            }
            else
            {
                centroid.plusEquals(member);
            }
        }
        
        centroid.scaleEquals(1.0 / (double) members.size());
        return new CentroidCluster<Vector>(centroid, members);
    }
}
