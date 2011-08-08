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
import java.util.ArrayList;
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
    implements IncrementalClusterCreator<CentroidCluster<Vector>, Vector>
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

    @Override
    public CentroidCluster<Vector> createCluster()
    {
        return new CentroidCluster<Vector>(null, new ArrayList<Vector>());
    }

    @Override
    public CentroidCluster<Vector> createCluster(
        final Collection<Vector> members)
    {
        if (members.size() <= 0)
        {
            // No members to create the cluster from.
            return new CentroidCluster<Vector>(null, members);
        }

        // We are going to create the mean centroid of the cluster.
        Vector centroid = null;
        for (Vector member : members)
        {
            if (centroid == null)
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

    @Override
    public void addClusterMember(
        final CentroidCluster<Vector> cluster,
        final Vector member)
    {
        Vector centroid = cluster.getCentroid();
        if (centroid == null)
        {
            centroid = member.clone();
            cluster.setCentroid(centroid);
        }
        else
        {
            final int oldSize = cluster.getMembers().size();
            final int newSize = oldSize + 1;
            final Vector delta = member.minus(centroid);
            delta.scaleEquals(1.0 / newSize);
            centroid.plusEquals(delta);
        }
        cluster.getMembers().add(member);
    }

    @Override
    public boolean removeClusterMember(
        final CentroidCluster<Vector> cluster,
        final Vector member)
    {
        if (cluster.getMembers().remove(member))
        {
            final int newSize = cluster.getMembers().size();
            Vector centroid = cluster.getCentroid();
            if (newSize <= 0)
            {
                centroid.zero();
            }
            else
            {
                final Vector delta = member.minus(centroid);
                delta.scaleEquals(1.0 / newSize);
                centroid.minusEquals(delta);
            }
            return true;
        }
        else
        {
            return false;
        }
    }
    
}
