/*
 * File:                AbstractDecisionTreeNode.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright November 29, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 *
 */

package gov.sandia.cognition.learning.algorithm.tree;

import gov.sandia.cognition.collection.CollectionUtil;
import gov.sandia.cognition.learning.function.categorization.Categorizer;
import gov.sandia.cognition.util.AbstractCloneableSerializable;
import gov.sandia.cognition.util.ObjectUtil;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * The {@code AbstractDecisionTreeNode} class implements common functionality
 * for a decision tree node. It keeps the internal decider plus the collection
 * of child nodes and the incoming label value for the node.
 *
 * @param  <InputType> The input type of the tree.
 * @param  <OutputType> The output type of the tree.
 * @param  <InteriorType> The output of the decision at this node.
 * @author Justin Basilico
 * @since  2.0
 */
public abstract class AbstractDecisionTreeNode<InputType, OutputType, InteriorType>
    extends AbstractCloneableSerializable
    implements DecisionTreeNode<InputType, OutputType>
{

    /** The parent node of this node. */
    protected DecisionTreeNode<InputType, OutputType> parent;
    
    /** The mapping of decider decision values to child nodes. For a leaf node,
     *  this can be null or empty. */
    protected Map
        <InteriorType, DecisionTreeNode<InputType, OutputType>> 
        childMap;

    /** The decider used to make a decision as to which child use. For a
     *  leaf node, the decider should be null. */
    protected Categorizer<? super InputType, ? extends InteriorType> decider;

    /** The incoming value for the node. Usually if this is null it means that
     *  the node is a root node. */
    protected Object incomingValue;
    
    /**
     * Creates a new instance of AbstractDecisionTreeNode
     */
    public AbstractDecisionTreeNode()
    {
        this(null, null, null);
    }
    
    /**
     * Creates a new instance of CategorizationTreeNode.
     *
     * @param   parent The parent node of this node.
     * @param   decider The decision function.
     * @param   incomingValue The incoming value.
     */
    public AbstractDecisionTreeNode(
        final DecisionTreeNode<InputType, OutputType> parent,
        final Categorizer<? super InputType, ? extends InteriorType> decider,
        final Object incomingValue)
    {
        super();

        this.setParent(parent);
        this.setDecider(decider);
        this.setChildMap(null);
        this.setIncomingValue(incomingValue);
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public AbstractDecisionTreeNode<InputType, OutputType, InteriorType> 
        clone()
    {
        final AbstractDecisionTreeNode<InputType, OutputType, InteriorType> result = (AbstractDecisionTreeNode<InputType, OutputType, InteriorType>)
            super.clone();
        
        if (this.childMap != null)
        {
            result.childMap = CollectionUtil.createLinkedHashMapWithSize(this.childMap.size());
            for (Map.Entry<InteriorType, DecisionTreeNode<InputType, OutputType>> entry
                : this.childMap.entrySet())
            {
                final DecisionTreeNode<InputType, OutputType> node = (DecisionTreeNode<InputType, OutputType>)
                    entry.getValue().clone();
                if (node instanceof AbstractDecisionTreeNode)
                {
                    // Fix the parent pointer.
                    ((AbstractDecisionTreeNode<InputType, OutputType, ?>) node).parent = result;
                }
                result.childMap.put(entry.getKey(), node);
            }
        }
        result.decider = ObjectUtil.cloneSmart(this.decider);
        result.incomingValue = ObjectUtil.cloneSmart(this.incomingValue);
        
        return result;
    }
    
    /**
     * Adds a child for a given interior type.
     *
     * @param  value The interior type value for the child.
     * @param  child The child node to add.
     */
    public void addChild(
        final InteriorType value,
        final DecisionTreeNode<InputType, OutputType> child)
    {
        if ( this.childMap == null )
        {
            this.childMap = new LinkedHashMap
                <InteriorType, DecisionTreeNode<InputType, OutputType>>();
        }
        
        this.childMap.put(value, child);
    }
    
    public Collection<? extends DecisionTreeNode<InputType, OutputType>> getChildren()
    {
        if ( this.isLeaf() )
        {
            return Collections.emptyList();
        }
        else
        {
            return this.childMap.values();
        }
    }

    public boolean isLeaf()
    {
        return this.childMap == null || this.childMap.size() <= 0;
    }

    public DecisionTreeNode<InputType, OutputType> chooseChild(
        final InputType input)
    {
        if ( this.isLeaf() || this.decider == null )
        {
            // Leaf nodes have no children.
            return null;
        }
        
        // Apply the decider.
        InteriorType decision = this.decider.evaluate(input);
        if ( decision == null )
        {
            // No decision was made so we can't get a child node.
            return null;
        }
        else
        {
            // Return the child associated with the decision, if one exists.
            return this.childMap.get(decision);
        }
    }

    public int getDepth()
    {
	// A node's depth is the length of the path from the node to
	// the root.  The root node has depth 0.
        return this.parent == null ? 0 : 1 + parent.getDepth();
    }

    public int getTreeSize()
    {
        int size = 1;
        for (DecisionTreeNode<?, ?> child : this.getChildren())
        {
            size += child.getTreeSize();
        }
        return size;
    }

    /**
     * Gets the parent node for this node. Null if it is the root node.
     *
     * @return
     *      The parent node for this node.
     */
    public DecisionTreeNode<InputType, OutputType> getParent()
    {
        return this.parent;
    }

    /**
     * Sets the parent node for this node. Null if it is the root node.
     *
     * @param   parent
     *      The parent node for this node.
     */
    public void setParent(
        final DecisionTreeNode<InputType, OutputType> parent)
    {
        this.parent = parent;
    }

    /**
     * Gets the decider used at this node.
     *
     * @return The decider used.
     */
    public Categorizer<? super InputType, ? extends InteriorType> getDecider()
    {
        return this.decider;
    }

    /**
     * Sets the decider used at this node.
     *
     * @param  decider The decider used.
     */
    public void setDecider(
        final Categorizer<? super InputType, ? extends InteriorType> decider)
    {
        this.decider = decider;
    }

    /**
     * Gets the mapping of decision values to child nodes.
     *
     * @return The child map.
     */
    public Map
        <InteriorType, DecisionTreeNode<InputType, OutputType>> 
        getChildMap()
    {
        return childMap;
    }

    /**
     * Sets the mapping of decision values to child nodes.
     *
     * @param  childMap The child map.
     */
    protected void setChildMap(
        final Map
            <InteriorType, DecisionTreeNode<InputType, OutputType>> 
            childMap)
    {
        this.childMap = childMap;
    }
    
    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    public Object getIncomingValue()
    {
        return this.incomingValue;
    }
    
    /**
     * Sets the incoming value for the node.
     *
     * @param  incomingValue The incoming value for the node.
     */   
    public void setIncomingValue(
        final Object incomingValue)
    {
        this.incomingValue = incomingValue;
    }
}
