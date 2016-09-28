/*
 * File:                Message.java
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

/**
 * Package-private class used for BP message passing. Stores the message passed
 * from a source node to the destination node. The destination node stores this
 * message, so there's no need to store its id internally here.
 *
 * @author tong
 *
 */
class Message
{

    /**
     * The minimum value a belief can take on for numerical precision reasons
     */
    private static final double MIN_BELIEF = 1e-6;

    /**
     * The id for the node that sends this message
     */
    private final int sourceNode;

    /**
     * The message's values from the last complete iteration
     */
    private final double[] values;

    /**
     * The message's values from the current iteration being computed
     */
    private final double[] tempValues;

    /**
     * The log of the values from the last complete iteration
     */
    private final double[] logValues;

    /**
     * Initialize this message with the source id and number of labels
     *
     * @param source The id for the source node for this message
     * @param numLabels The number of labels this message will contain
     */
    public Message(int source,
        int numLabels)
    {
        this.sourceNode = source;
        this.values = new double[numLabels];
        this.tempValues = new double[numLabels];
        this.logValues = new double[numLabels];
    }

    /**
     * Returns the id for the source node for this message
     *
     * @return
     */
    public int getSourceNode()
    {
        return sourceNode;
    }

    /**
     * Normalize the temporary values for this message following the sum-product
     * algorithm
     */
    public void normalizeTempValuesForSumProductAlgorithm()
    {
        // Normalize the messages so that we don't converge to zero.
        double total = 0;
        for (int label = 0; label < tempValues.length; label++)
        {
            total += tempValues[label];
        }
        for (int label = 0; label < tempValues.length; label++)
        {
            tempValues[label] /= total;
        }
    }

    /**
     * Update this message at the completion of an iteration
     *
     * @return The maximum change for any label on this message
     */
    public double update()
    {
        double delta = 0;
        for (int label = 0; label < values.length; label++)
        {
            delta = Math.max(delta, Math.abs(values[label] - tempValues[label]));
            // Numerical precision issues
            values[label] = Math.max(tempValues[label], MIN_BELIEF);
            logValues[label] = Math.log(values[label]);
        }

        return delta;
    }

    /**
     * Set the temporary value for this message for this iteration
     *
     * @param label The index whose value should be set
     * @param value The value to set it to
     */
    public void setTempValue(int label,
        double value)
    {
        tempValues[label] = value;
    }

    /**
     * Returns the real-value for this message
     *
     * @param label The index whose value should be returned
     * @return The value for the input label
     */
    public double getValue(int label)
    {
        return values[label];
    }

    /**
     * Returns the log of the value for this message
     *
     * @param label The index whose value should be returned
     * @return The log of the value for the input label
     */
    public double getLogValue(int label)
    {
        return logValues[label];
    }

    /**
     * Resets all values for this message to default (1.0)
     */
    public void resetToOne()
    {
        for (int label = 0; label < values.length; label++)
        {
            values[label] = 1;
        }
    }

    @Override
    public String toString()
    {
        StringBuilder buffer = new StringBuilder();
        buffer.append("Source ");
        buffer.append(sourceNode);
        buffer.append(" [");
        for (double v : values)
        {
            buffer.append(v);
            buffer.append(", ");
        }
        buffer.append(']');
        return buffer.toString();
    }

}
