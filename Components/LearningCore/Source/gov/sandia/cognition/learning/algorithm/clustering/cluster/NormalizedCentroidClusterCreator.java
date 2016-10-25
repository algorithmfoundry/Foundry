/*
 * File:                NormalizedCentroidClusterCreator.java
 * Authors:             Natasha Singh-Miller
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright 2011, Sandia Corporation. Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 */

package gov.sandia.cognition.learning.algorithm.clustering.cluster;

import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.Vectorizable;
import gov.sandia.cognition.util.AbstractCloneableSerializable;
import java.util.Collection;
import java.util.ArrayList;

/**
 * A cluster creator for {@link NormalizedCentroidCluster}s which are clusters
 * that have a normalized centroid in addition to the usual centroid. The
 * normalized centroid is the centroid formed from normalizing each member of
 * the cluster to have unit length.
 *
 * @author nsingh
 */
public class NormalizedCentroidClusterCreator
    extends AbstractCloneableSerializable
    implements
    IncrementalClusterCreator<NormalizedCentroidCluster<Vectorizable>, Vectorizable>
{

    /**
     * Creates a new instance of VectorizableCentroidClusterCreator()
     */
    public NormalizedCentroidClusterCreator()
    {
        super();
    }

    @Override
    public NormalizedCentroidCluster<Vectorizable> createCluster()
    {
        return new NormalizedCentroidCluster<>(null, null, new ArrayList<>());
    }

    @Override
    public NormalizedCentroidCluster<Vectorizable> createCluster(
        final Collection<? extends Vectorizable> members)
    {
        if (members.isEmpty())
        {
            // No members to create the cluster from.
            return new NormalizedCentroidCluster<>(null, null, members);
        }

        // We are going to create the centroid of the cluster.
        Vectorizable centroid = null;
        Vector data = null;
        Vectorizable normalizedCentroid = null;
        Vector normalizedData = null;
        for (Vectorizable member : members)
        {
            Vector memberVector = member.convertToVector();
            if (data == null)
            {
                centroid = member.clone();
                data = memberVector.clone();
                normalizedCentroid = member.clone();
                normalizedData = memberVector.norm2() != 0.0
                    ? memberVector.scale(1.0 / memberVector.norm2())
                    : memberVector;
            }
            else
            {
                data.plusEquals(memberVector);
                if (memberVector.norm2() != 0.0)
                {
                    normalizedData.plusEquals(memberVector.scale(1.0
                        / memberVector.norm2()));
                }
            }
        }

        data.scaleEquals(1.0 / (double) members.size());
        normalizedData.scaleEquals(1.0 / (double) members.size());
        centroid.convertFromVector(data);
        normalizedCentroid.convertFromVector(normalizedData);
        return new NormalizedCentroidCluster<>(centroid,
            normalizedCentroid, members);
    }

    @Override
    public void addClusterMember(
        final NormalizedCentroidCluster<Vectorizable> cluster,
        final Vectorizable member)
    {
        Vectorizable centroid = cluster.getCentroid();

        //add first member of cluster
        if (centroid == null)
        {
            //set centroid
            centroid = member.clone();
            cluster.setCentroid(centroid);

            //set normalized centroid
            Vectorizable normalizedCentroid = member.clone();
            Vector normalizedData = normalizedCentroid.convertToVector();

            if (normalizedData.norm2() != 0.0)
            {
                normalizedData.scaleEquals(1.0 / normalizedData.norm2());
            }
            normalizedCentroid.convertFromVector(normalizedData);
            cluster.setNormalizedCentroid(normalizedCentroid);
        }

        else
        {
            final int oldSize = cluster.getMembers().size();
            final int newSize = oldSize + 1;

            //set centroid
            Vector data = centroid.convertToVector();
            data.scaleEquals(oldSize);
            data.plusEquals(member.convertToVector());
            data.scaleEquals(1.0 / newSize);
            centroid.convertFromVector(data);
            cluster.setCentroid(centroid);

            //set normalized centroid
            Vectorizable normalizedCentroid = cluster.getNormalizedCentroid();
            Vector normalizedData = normalizedCentroid.convertToVector();
            normalizedData.scaleEquals(oldSize);
            Vector normalizedMember = member.convertToVector().scale(1.0
                / member.convertToVector().norm2());
            if (member.convertToVector().norm2() != 0.0)
            {
                normalizedData.plusEquals(normalizedMember);
            }
            normalizedData.scaleEquals(1.0 / newSize);
            normalizedCentroid.convertFromVector(normalizedData);
            cluster.setNormalizedCentroid(normalizedCentroid);
        }
        cluster.getMembers().add(member);
    }

    @Override
    public boolean removeClusterMember(
        final NormalizedCentroidCluster<Vectorizable> cluster,
        final Vectorizable member)
    {
        if (cluster.getMembers().remove(member))
        {

            final int newSize = cluster.getMembers().size();
            final int oldSize = newSize + 1;

            if (newSize == 0)
            {
                cluster.setCentroid(null);
                cluster.setNormalizedCentroid(null);
                return true;
            }

            //reset centroid
            Vectorizable centroid = cluster.getCentroid();
            Vector data = centroid.convertToVector();
            data.scaleEquals(oldSize);
            data.minusEquals(member.convertToVector());
            data.scaleEquals(1.0 / newSize);
            centroid.convertFromVector(data);
            cluster.setCentroid(centroid);

            //reset normalized centroid
            Vectorizable normalizedCentroid = cluster.getNormalizedCentroid();
            Vector normalizedData = normalizedCentroid.convertToVector();
            normalizedData.scaleEquals(oldSize);
            if (member.convertToVector().norm2() != 0.0)
            {
                normalizedData.minusEquals(member.convertToVector().scale(1.0
                    / member.convertToVector().norm2()));
            }
            normalizedData.scaleEquals(1.0 / newSize);
            normalizedCentroid.convertFromVector(normalizedData);
            cluster.setNormalizedCentroid(normalizedCentroid);

            return true;
        }
        else
        {
            return false;
        }
    }

}
