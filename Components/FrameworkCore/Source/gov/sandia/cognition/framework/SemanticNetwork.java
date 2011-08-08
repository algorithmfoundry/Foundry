/*
 * File:                SemanticNetwork.java
 * Authors:             Justin Basilico and Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Framework Lite
 *
 * Copyright February 10, 2006, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 *
 */

package gov.sandia.cognition.framework;

import java.io.Serializable;
import java.util.Collection;

/**
 * The SemanticNetwork interface defines the functionality required for a
 * network that is used as part of the paramterization to a SemanticMemory.
 *
 * @author Justin Basilico
 * @author Kevin R. Dixon
 * @since  1.0
 */
public interface SemanticNetwork
    extends Serializable
{
    /**
     * Gets the total number of nodes in the network.
     *
     * @return The total number of nodes in the network
     */
    public int getNumNodes();
    
    /**
     * Returns true if the given SemanticLabel is a node in the 
     * SemanticNetwork.
     *
     * @param label The label to evaluate.
     * @return True if the given label is a node in the network and false 
     *         otherwise.
     */
    public boolean isNode(
        SemanticLabel label);
    
    /**
     * Gets the nodes that are in the network.
     *
     * @return The collection of nodes in the network.
     */
    public Collection<SemanticLabel> getNodes();
    
    /**
     * Gets the outgoing links from a given node.
     *
     * @param  nodeLabel The node to get the outgoing links from
     * @return The collection of outgoing links from the given node. If the
     *         given node is not in the network, null is to be returned.
     */
    public Collection<SemanticLabel> getOutLinks(
        SemanticLabel nodeLabel);
    
    /**
     * Gets the association between two nodes in the network.
     *
     * @param from The node the association is from
     * @param to The node the association is to
     * @return The association between the given nodes
     */
    public double getAssociation(
        SemanticLabel from,
        SemanticLabel to);
}
