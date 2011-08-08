/*
 * File:                DataPartitioner.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Framework Lite
 *
 * Copyright August 30, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 *
 */

package gov.sandia.cognition.learning.data;

import java.util.Collection;

/**
 * The {@code DataPartitioner} interface defines the functionality of an object
 * that can create a {@code PartitionedDataset} from a collection of data.
 *
 * @param  <DataType> The type of data to partition.
 * @author Justin Basilico
 * @since  2.0
 */
public interface DataPartitioner<DataType>
{
    /**
     * Partitions the given collection of data into a training set and a 
     * testing set.
     *
     * @param  data The data to partition.
     * @return The partitioned dataset.
     */
    PartitionedDataset<DataType> createPartition(
        Collection<? extends DataType> data);
}
