/*
 * File:                VectorMeanCentroidClusterCreator.java
 * Authors:             Justin Basilico
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
import gov.sandia.cognition.math.matrix.mtj.DenseVectorFactoryMTJ;
import gov.sandia.cognition.util.AbstractCloneableSerializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementation of {@link VectorMeanCentroidClusterCreator} for mini-batch
 * clustering.
 *
 * @author Jeff Piersol
 * @since 3.4.4
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
    implements MiniBatchClusterCreator<CentroidCluster<Vector>, Vector>
{

    private static final long serialVersionUID = 1512395430699067693L;

    /**
     * The number of times that this cluster creator has updated the centroid
     * for the given cluster.
     */
    protected final Map<CentroidCluster<Vector>, Integer> numCentroidUpdates;

    public VectorMeanMiniBatchCentroidClusterCreator()
    {
        super();
        numCentroidUpdates = new HashMap<>();
    }

    /**
     * {@inheritDoc}
     *
     * This implementation is not thread-safe.
     *
     * @param initialPoints
     * @return
     */
    @Override
    public CentroidCluster<Vector> createCluster(
        final Collection<? extends Vector> initialPoints)
    {
        if (initialPoints.size() <= 0)
        {
            throw new IllegalArgumentException(
                "You must provide at least one data point in order to create a cluster.");
        }

        // Create a centroid of the same type as input vectors
        Vector centroid = initialPoints.stream().findAny().get().clone();
        centroid.zero();
        // Sum the member vectors
        for (Vector member : initialPoints)
        {
            centroid.plusEquals(member);
        }
        centroid.scaleEquals(1.0 / (double) initialPoints.size());

        CentroidCluster<Vector> cluster = new CentroidCluster<>(centroid,
            new ArrayList<>()); // empty list, since mini-batch cluster creators don't assign membership
        numCentroidUpdates.put(cluster, initialPoints.size());

        return cluster;
    }

    @Override
    public void updatePrototype(CentroidCluster<Vector> cluster,
        Vector dataPoint)
    {
        updatePrototype(cluster, Collections.singletonList(dataPoint));
    }

    @Override
    public void updatePrototype(CentroidCluster<Vector> cluster,
        List<Vector> dataPoints)
    {
        Vector centroid = cluster.getCentroid();

        Integer initNumUpdates = numCentroidUpdates.get(cluster);
        if (initNumUpdates == null)
        {
            initNumUpdates = 0; // first time seeing this cluster, so clear it and begin afresh
        }
        int finalNumUpdates = initNumUpdates + dataPoints.size();
        numCentroidUpdates.put(cluster, finalNumUpdates);
        double finalEta = 1 / (double) finalNumUpdates;

        Vector shiftVector = DenseVectorFactoryMTJ.INSTANCE.createVector(
            centroid.getDimensionality());

        for (Vector sample : dataPoints)
        {
            shiftVector.plusEquals(sample);
        }

        // Move centroid towards data point
        centroid.scaleEquals(initNumUpdates * finalEta);
        centroid.scaledPlusEquals(finalEta, shiftVector);
    }

}
