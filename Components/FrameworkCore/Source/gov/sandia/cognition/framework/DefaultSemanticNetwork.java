/*
 * File:                DefaultSemanticNetwork.java
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

import java.util.Collection;
import java.util.TreeMap;

/**
 * This class contains a default implementation of a SemanticNetwork. It uses
 * a sparse graph representation using TreeMaps to map from nodes to links. 
 * All of the links are doubly linked, being stored by both their from and to
 * nodes. This allows for fast addition, removal, and lookup.
 * 
 * @author Justin Basilico
 * @author Kevin R. Dixon
 * @since 1.0
 */
public class DefaultSemanticNetwork
    extends java.lang.Object
    implements SemanticNetwork
{
    /**
     * The mapping of labels onto nodes in the network.
     */
    private TreeMap<SemanticLabel, DefaultSemanticNetworkNode>
        labelNodeMap = null;
        
    /** 
     * Creates a new instance of DefaultSemanticNetwork 
     */
    public DefaultSemanticNetwork()
    {
        super();
        
        this.setLabelNodeMap(
            new TreeMap<SemanticLabel, DefaultSemanticNetworkNode>());
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    public int getNumNodes()
    {
        return this.getLabelNodeMap().size();
    }
    
    /**
     * {@inheritDoc}
     *
     * @param  label {@inheritDoc}
     * @return {@inheritDoc}
     */
    public boolean isNode(
        SemanticLabel label)
    {
        return this.getLabelNodeMap().containsKey(label);
    }
    
    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    public Collection<SemanticLabel> getNodes()
    {
        return this.getLabelNodeMap().keySet();
    }
    
    /**
     * {@inheritDoc}
     *
     * @param  nodeLabel {@inheritDoc}
     * @return {@inheritDoc}
     */
    public Collection<SemanticLabel> getOutLinks(
        SemanticLabel nodeLabel)
    {
        DefaultSemanticNetworkNode node = this.getLabelNodeMap().get(nodeLabel);
        
        if ( node == null )
        {
            // No such node.
            return null;
        }
        else
        {
            return node.getOutLinks().keySet();
        }
    }
    
    /**
     * {@inheritDoc}
     *
     * @param  from {@inheritDoc}
     * @param  to   {@inheritDoc}
     * @return {@inheritDoc}
     */
    public double getAssociation(
        SemanticLabel from, 
        SemanticLabel to)
    {
        // Get the link between the nodes.
        DefaultSemanticNetworkLink link = this.getLink(from, to);
        
        if ( link == null )
        {
            // No association.
            return 0.0;
        }
        else
        {
            // Return the weight.
            return link.getWeight();
        }
    }
    
    /**
     * Adds a node to the semantic network. If the node is already in the 
     * network then nothing changes.
     *
     * @param  nodeLabel The semantic label of the node.
     */
    public void addNode(
        SemanticLabel nodeLabel)
    {
        if ( this.isNode(nodeLabel) )
        {
            // This is already a node so do nothing.
            return;
        }
        
        // Add the node.
        this.getLabelNodeMap().put(
            nodeLabel, new DefaultSemanticNetworkNode(nodeLabel));
    }
    
    /**
     * Removes a node from the semantic network including all of the incoming
     * and outgoing links of the node. If the node is not in the network then
     * nothing changes.
     *
     * @param  nodeLabel The node to remove from the network.
     */
    public void removeNode(
        SemanticLabel nodeLabel)
    {
        // Remove the node from our node table.
        DefaultSemanticNetworkNode node =
            this.getLabelNodeMap().remove(nodeLabel);
        
        if ( node == null )
        {
            // DefaultSemanticNetworkNode was already removed so
            // that there is nothing to update.
            return;
        }
        
        // Remove the links that the node has outgoing links to.
        for ( DefaultSemanticNetworkLink link : node.getOutLinks().values() )
        {
            link.getToNode().getInLinks().remove(nodeLabel);
        }
        
        // Remove the links that the node has incoming links from.
        for ( DefaultSemanticNetworkLink link : node.getInLinks().values() )
        {
            link.getFromNode().getOutLinks().remove(nodeLabel);
        }
    }
    
    /**
     * Sets the association between two nodes in the network. 
     * 
     * @param from The node that the link is from.
     * @param to   The node that the link is to.
     * @param weight The weight of the association.
     * @throws IllegalArgumentException Thrown if either the from or to node 
     *         is not in the network.
     */
    public void setAssociation(
        SemanticLabel from, 
        SemanticLabel to,
        double weight)
    {
        // Get the from and to nodes.
        DefaultSemanticNetworkNode fromNode = this.getLabelNodeMap().get(from);
        DefaultSemanticNetworkNode toNode   = this.getLabelNodeMap().get(to);
        
        if ( fromNode == null )
        {
            // Error: Bad from node.
            throw new IllegalArgumentException("No such 'from node' exists.");
        }
        else if ( toNode == null )
        {
            // Error: Bad to node.
            throw new IllegalArgumentException("No such 'to node' exists.");
        }
        
        // Look for the link between the two nodes, if it exists.
        DefaultSemanticNetworkLink link = this.getLink(from, to);
        
        if ( link != null )
        {
            if ( weight == 0.0 )
            {
                // The association is now zero so remove the link from the
                // nodes.
                link.getFromNode().getOutLinks().remove(to);
                link.getToNode().getInLinks().remove(from);
            }
            else
            {
                // Set the association
                link.setWeight(weight);
            }
        }
        else if ( weight != 0.0 )
        {
            // Create the link between the nodes.
            link = new DefaultSemanticNetworkLink(fromNode, toNode, weight);
            fromNode.getOutLinks().put(to, link);
            toNode.getInLinks().put(from, link);
        }
        // else - The weight was zero so there is nothing to update.
    }
    
    /**
     * Gets the DefaultSemanticNetwork between two nodes if it exists,
     * otherwise it returns null.
     * 
     * @param from The from node.
     * @param to   The to node.
     * @return The DefaultSemanticNetwork between the two given nodes,
     * if one exists.
     */
    private DefaultSemanticNetworkLink getLink(
        SemanticLabel from, 
        SemanticLabel to)
    {
        if ( from == null || to == null )
        {
            // Error: Bad parameter.
            return null;
        }
        
        // Get the two nodes.
        DefaultSemanticNetworkNode fromNode = this.getLabelNodeMap().get(from);
        DefaultSemanticNetworkNode toNode   = this.getLabelNodeMap().get(to);
        
        if ( fromNode == null || toNode == null )
        {
            // One of the nodes was not good.
            return null;
        }
        
        // Attempt to get the link.
        return fromNode.getOutLinks().get(to);
    }

    /**
     * Getter for labelNodeMap
     *
     * @return The mapping of labels onto nodes in the network.
     */
    TreeMap<SemanticLabel, DefaultSemanticNetworkNode>
        getLabelNodeMap()
    {
        return this.labelNodeMap;
    }

    /**
     * Setter for labelNodeMap
     *
     * @param labelNodeMap The mapping of labels onto nodes in the network.
     */
    void setLabelNodeMap(
        TreeMap<SemanticLabel, DefaultSemanticNetworkNode> labelNodeMap)
    {
        if( labelNodeMap == null )
        {
            // Error: Bad nodes.
            throw new NullPointerException("The nodes cannot be null.");
        }
        this.labelNodeMap = labelNodeMap;
    }
    
    
    
    /**
     * The DefaultSemanticNetworkNode class represents a node in the
     * semantic network.
     * 
     * @author Justin Basilico
     * @since 1.0
     */
    private static class DefaultSemanticNetworkNode
        extends java.lang.Object
    {
        /** The SemanticLabel of the node. */
        private SemanticLabel label = null;
        
        /** The mapping of incoming links to the node. */
        private TreeMap<SemanticLabel, DefaultSemanticNetworkLink>
            inLinks = null;
        
        /** The mapping of outgoing links from the node. */
        private TreeMap<SemanticLabel, DefaultSemanticNetworkLink>
            outLinks = null;
        
        /**
         * Creates a new instance of DefaultSemanticNetworkNode.
         * 
         * @param label The label for the DefaultSemanticNetworkNode.
         */
        private DefaultSemanticNetworkNode(
            SemanticLabel label)
        {
            super();
            
            this.setLabel(label);
            this.setInLinks(
                new TreeMap<SemanticLabel, DefaultSemanticNetworkLink>());
            this.setOutLinks(
                new TreeMap<SemanticLabel, DefaultSemanticNetworkLink>());
        }
        
        /**
         * Gets the label of the node.
         *
         * @return The label of the node.
         */
        private SemanticLabel getLabel()
        {
            return this.label;
        }

        /**
         * Gets the links going into the node.
         *
         * @return The links going into the node.
         */
        private TreeMap<SemanticLabel, DefaultSemanticNetworkLink> getInLinks()
        {
            return this.inLinks;
        }

        /**
         * Gets the links coming out of a node.
         *
         * @return The links coming out of a node.
         */
        private TreeMap<SemanticLabel, DefaultSemanticNetworkLink> getOutLinks()
        {
            return this.outLinks;
        }

        /**
         * Sets the label of the node.
         *
         * @param  label The new label.
         */
        private void setLabel(
            SemanticLabel label)
        {
            this.label = label;
        }

        /**
         * Sets the incoming links for the node.
         *
         * @param  inLinks The incoming links.
         */
        private void setInLinks(
            TreeMap<SemanticLabel, DefaultSemanticNetworkLink> inLinks)
        {
            this.inLinks = inLinks;
        }

        /**
         * Sets the outgoing links of the node.
         *
         * @param  outLinks The outgoing links.
         */
        private void setOutLinks(
            TreeMap<SemanticLabel, DefaultSemanticNetworkLink> outLinks)
        {
            this.outLinks = outLinks;
        }
    }
    
    /**
     * The DefaultSemanticNetworkLink class represents a link between two
     * nodes in the semantic network.
     * 
     * @author Justin Basilico
     * @since 1.0
     */
    private static class DefaultSemanticNetworkLink
        extends java.lang.Object
    {
        /**
         * The DefaultSemanticNetworkNode the link comes from.
         */
        private DefaultSemanticNetworkNode fromNode = null;
        
        /** The node the link goes to. */
        private DefaultSemanticNetworkNode toNode   = null;
        
        /** The weight strength of the link. */
        private double weight = 0.0;
        
        /**
         * Creates a new instance of DefaultSemanticNetworkLink.
         * 
         * @param fromNode The node the link comes from.
         * @param toNode   The node the link goes to.
         * @param weight The assocation weight between the two nodes.
         */
        private DefaultSemanticNetworkLink(
            DefaultSemanticNetworkNode fromNode,
            DefaultSemanticNetworkNode toNode,
            double weight)
        {
            super();
            
            this.setFromNode(fromNode);
            this.setToNode(toNode);
            this.setWeight(weight);
        }

        /**
         * Gets the node the link comes from.
         * 
         * @return The DefaultSemanticNetworkNode that the link comes from.
         */
        private DefaultSemanticNetworkNode getFromNode()
        {
            return this.fromNode;
        }

        /**
         * Gets the node that the link goes to.
         * 
         * @return The DefaultSemanticNetworkNode that the link goes to.
         */
        private DefaultSemanticNetworkNode getToNode()
        {
            return this.toNode;
        }

        /**
         * Gets the association weight between the nodes.
         * 
         * @return The association weight between the nodes.
         */
        private double getWeight()
        {
            return this.weight;
        }

        /**
         * Sets the node the link comes from.
         * 
         * @param fromNode The new from DefaultSemanticNetworkNode.
         */
        private void setFromNode(
            DefaultSemanticNetworkNode fromNode)
        {
            this.fromNode = fromNode;
        }

        /**
         * Sets the node the link goes to.
         * 
         * @param toNode The new to DefaultSemanticNetworkNode.
         */
        private void setToNode(
            DefaultSemanticNetworkNode toNode)
        {
            this.toNode = toNode;
        }

        /**
         * Sets the association weight between the nodes.
         * 
         * @param weight The new weight.
         */
        private void setWeight(
            double weight)
        {
            this.weight = weight;
        }
    }
}
