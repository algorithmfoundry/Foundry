/*
 * File:                MostFrequentSummarizer.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright April 07, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 * See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.learning.function.summarizer;

import gov.sandia.cognition.util.AbstractCloneableSerializable;
import gov.sandia.cognition.util.Summarizer;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Summarizes a set of values by returning the most frequent value. If there
 * is a tie, the first encountered value is returned.
 * 
 * @param <DataType> Type of Data to summarize.
 * @author  Justin Basilico
 * @since   3.0
 */
public class MostFrequentSummarizer<DataType>
    extends AbstractCloneableSerializable
    implements Summarizer<DataType, DataType>
{

    /**
     * Creates a new {@code MostFrequentSummarizer}.
     */
    public MostFrequentSummarizer()
    {
        super();
    }

    /**
     * Summarizes the given data by returning the most frequent value. If there
     * are multiple values at the same (maximum) frequency, then the first one
     * is used.
     * 
     * @param   data
     *      The data to summarize.
     * @return
     *      The most frequent value.
     */
    public DataType summarize(
        final Collection<? extends DataType> data)
    {
// TODO: There is probably a more efficient data structure for doing this. Maybe
// a heap combined with a hash map.
        // Create the map of counts.
        final LinkedHashMap<DataType, Integer> counts =
            new LinkedHashMap<DataType, Integer>();
        for (DataType item : data)
        {
            Integer count = counts.get(item);
            if (count == null)
            {
                count = 0;
            }

            counts.put(item, count + 1);
        }

        // Find the one with the best count.
        int bestCount = 0;
        DataType best = null;
        for (Map.Entry<DataType, Integer> entry : counts.entrySet())
        {
            final int count = entry.getValue();
            if (count > bestCount)
            {
                bestCount = count;
                best = entry.getKey();
            }
        }

        // Return the best value found.
        return best;
    }
}
