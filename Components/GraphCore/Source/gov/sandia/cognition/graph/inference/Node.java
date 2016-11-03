/*
 * File:                Node.java
 * Authors:             Tu-Thach Quach
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright 2016, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government. 
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.graph.inference;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Package-private support class for BP. Specifically stores and computes intermediate
 * values for a node in the BP algorithm. Only made public as the
 * SumProductInferencingAlgorithm class does expose a protected member of this
 * type.
 *
 * @author tong
 * @param <LabelType> The type that labels can be for this node -- used only as
 * an enum
 */
class Node<LabelType>
{

    /**
     * The BP-internal id for this node
     */
    private final int id;

    /**
     * The labels this node can take on
     */
    private final Collection<LabelType> labels;

    /**
     * Stores the messages that are sent to this node
     */
    private final List<Message> messages;

    /**
     * The beliefs currently associated with each label on this node
     */
    private final double[] beliefs;

    /**
     * Initialize this node with the specified id and possible labels
     *
     * @param id The BP-internal id for this node
     * @param labels The labels this node can take on
     */
    public Node(int id,
        Collection<LabelType> labels)
    {
        this.id = id;
        this.labels = labels;
        beliefs = new double[labels.size()];
        messages = new ArrayList<>();
    }

    /**
     * Create a message from sourceNode to this
     *
     * @param sourceNode The source of a message to this node
     * @param ensureUniqueEdges Ensure that entered edges are unique
     */
    public void link(int sourceNode,
        boolean ensureUniqueEdges)
    {
        if (ensureUniqueEdges)
        {
            // Make sure we do not have source node in the message list.
            for (Message msg : messages)
            {
                if (msg.getSourceNode() == sourceNode)
                {
                    throw new IllegalArgumentException("Source node "
                        + sourceNode
                        + " to " + id + " appears more than once.");
                }
            }
        }

        Message msg = new Message(sourceNode, labels.size());
        messages.add(msg);
    }

    /**
     * Helper that normalizes the messages following the sum-product methodology
     */
    public void normalizeMessagesForSumProductAlgorithm()
    {
        for (Message msg : messages)
        {
            msg.normalizeTempValuesForSumProductAlgorithm();
        }
    }

    /**
     * Update all of the messages received by this node
     *
     * @return The maximum change for any messages received by this node
     */
    public double update()
    {
        double delta = 0;
        for (Message msg : messages)
        {
            delta = Math.max(delta, msg.update());
        }
        return delta;
    }

    /**
     * Resets all the incoming messages to one (for sum-product algorithm)
     */
    public void resetToOne()
    {
        for (Message msg : messages)
        {
            msg.resetToOne();
        }
    }

    /**
     * Return the internal id for this node
     *
     * @return the internal id for this node
     */
    public int getId()
    {
        return id;
    }

    /**
     * Returns the sum of the log of the messages excluding the message from
     * excludeNode
     *
     * @param nodeLabel The label id to compute the product for
     * @param excludeNode The node whose message should be excluded
     * @return The sum of the log of the messages
     */
    public double getLogMessageSum(int nodeLabel,
        int excludeNode)
    {
        double v = 0;
        for (Message msg : messages)
        {
            if (msg.getSourceNode() != excludeNode)
            {
                v += msg.getLogValue(nodeLabel);
            }
        }
        return v;
    }

    /**
     * Returns the sum of the log of the messages incoming to this node
     *
     * @param nodeLabel The label id to compute the product for
     * @return The sum of the log of the messages
     */
    public double getLogMessageSum(int nodeLabel)
    {
        double v = 0;
        for (Message msg : messages)
        {
            v += msg.getLogValue(nodeLabel);
        }
        return v;
    }

    /**
     * Returns the message to this node from the input source node
     *
     * @param sourceNode The node whose message to this is requested
     * @return the message to this node from the input source node
     */
    public Message getMessageFromSource(int sourceNode)
    {
        for (Message msg : messages)
        {
            if (msg.getSourceNode() == sourceNode)
            {
                return msg;
            }
        }

        throw new IllegalArgumentException("Node " + id
            + " does not contain a message from source node " + sourceNode);
    }

    /**
     * Computes the beliefs for this node following the sum-product algorithm
     *
     * @param f The function to use for values
     */
    public void computeBeliefsForSumProductAlgorithm(EnergyFunction<LabelType> f)
    {
        double max = -Double.MAX_VALUE;
        Iterator<LabelType> iter = labels.iterator();
        for (int i = 0; i < labels.size(); ++i)
        {
            LabelType label = iter.next();
            double belief = -f.getUnaryCost(id, label) + getLogMessageSum(i);
            beliefs[i] = belief;
            max = Math.max(belief, max);
        }

        double total = 0;
        for (int i = 0; i < labels.size(); ++i)
        {
            beliefs[i] = Math.exp(beliefs[i] - max);
            total += beliefs[i];
        }
        // Normalize.
        for (int label = 0; label < labels.size(); label++)
        {
            beliefs[label] /= total;
        }
    }

    /**
     * Returns the belief solved for this node and the input label
     *
     * @param label The label whose associated belief is desired
     * @return The belief associated for the input label
     */
    public double getBelief(int label)
    {
        return beliefs[label];
    }

    @Override
    public String toString()
    {
        StringBuilder buffer = new StringBuilder();
        buffer.append("Node ");
        buffer.append(id);
        buffer.append(":\r\n");
        for (Message msg : messages)
        {
            buffer.append(msg.toString());
            buffer.append("\r\n");
        }
        return buffer.toString();
    }

}
