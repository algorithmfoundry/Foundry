/*
 * File:                BatchHierarchicalClusterer.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright October 02, 2008, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 * See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.learning.algorithm.clustering.hierarchy;

import gov.sandia.cognition.learning.algorithm.clustering.cluster.Cluster;
import java.util.Collection;

/**
 * The {@code BatchHierarchicalClusterer} interface defines the functionality
 * of a batch hierarchical clustering algorithm. It takes a collection of 
 * elements and returns the root node of the hierarchical cluster for those
 * elements.
 *
 * @param   <DataType>
 *      The type of data to cluster.
 * @param   <ClusterType>
 *      The type of cluster created by the clustering algorithm.
 * @author  Justin Basilico
 * @since   2.1
 */
public interface BatchHierarchicalClusterer<DataType, ClusterType extends Cluster<DataType>>
{
    
    /**
     * Performs hierarchical clustering on the given elements. It returns the
     * root node of the hierarchy of clusters for the given elements.
     * 
     * @param   elements The elements to cluster.
     * @return  The root node of the hierarchical cluster for the given 
     *      elements.
     */
    ClusterHierarchyNode<DataType, ClusterType> clusterHierarchically(
        Collection<? extends DataType> elements);
}
