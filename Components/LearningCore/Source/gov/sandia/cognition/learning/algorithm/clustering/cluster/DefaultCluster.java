/*
 * File:                CentroidCluster.java
 * Authors:             Justin Basilico and Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright February 22, 2006, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.learning.algorithm.clustering.cluster;

import gov.sandia.cognition.annotation.CodeReview;
import java.util.ArrayList;
import java.util.Collection;

/**
 * The DefaultCluster class implements a default cluster which contains a
 * list of members in an ArrayList along with an index that identifies the
 * cluster.
 *
 * @param  <ClusterType> The type of data stored in the cluster.
 * @author Justin Basilico
 * @author Kevin R. Dixon
 * @since  1.0
 */
@CodeReview(
    reviewer="Kevin R. Dixon",
    date="2008-07-22",
    changesNeeded=false,
    comments="Interface looks fine."
)
public class DefaultCluster<ClusterType>
    extends java.lang.Object
    implements Cluster<ClusterType>
{
    /** The default index is {@value}. */
    public static final int DEFAULT_INDEX = -1;
    
    /** The index of the cluster in the collection of clusters. */
    private int index;
    
    /** The members of the cluster. */
    private ArrayList<ClusterType> members;
    
    /**
     * Creates a new instance of CentroidCluster.
     */
    public DefaultCluster()
    {
        this(null);
    }
    
    /**
     * Creates a new instance of CentroidCluster.
     *
     * @param index The index of the cluster.
     */
    public DefaultCluster(
        final int index)
    {
        this(index, null);
    }
    
    /**
     * Creates a new instance of CentroidCluster.
     *
     * @param members The members of the cluster.
     */
    public DefaultCluster(
        final Collection<ClusterType> members)
    {
        this(DEFAULT_INDEX, members);
    }
    
    /**
     * Creates a new instance of CentroidCluster.
     *
     * @param index The index of the cluster.
     * @param members The members of the cluster.
     */
    public DefaultCluster(
        final int index,
        final Collection<ClusterType> members)
    {
        super();
        
        this.setIndex(index);
        
        if ( members == null )
        {
            // No members so use an empty array.
            this.setMembers(new ArrayList<ClusterType>());
        }
        else
        {
            // Create a copy of the given members list.
            this.setMembers(new ArrayList<ClusterType>(members));
        }
    }
    
    /**
     * Gets the cluster index.
     *
     * @return The index of the cluster.
     */
    public int getIndex()
    {
        return this.index;
    }
    
    /**
     * Gets the members of the cluster.
     *
     * @return The members of the cluster.
     */
    public ArrayList<ClusterType> getMembers()
    {
        return this.members;
    }
    
    /**
     * Sets the index of the cluster.
     *
     * @param index The new index.
     */
    public void setIndex(
        final int index)
    {
        this.index = index;
    }
    
    /**
     * Sets the members of the cluster.
     *
     * @param members The new members of the cluster.
     */
    private void setMembers(
        final ArrayList<ClusterType> members)
    {
        this.members = members;
    }
}
