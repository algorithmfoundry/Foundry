/*
 * File:                PartitionedDataset.java
 * Authors:             Justin Basilico and Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Framework Lite
 *
 * Copyright February 23, 2006, Sandia Corporation.  Under the terms of Contract
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
 * Interface for a dataset partitioned into training and testing sets.
 *
 * @param  <DataType> The type of the data in the dataset.
 * @author Justin Basilico
 * @author Kevin R. Dixon
 * @since  1.0
 */
public interface PartitionedDataset<DataType>
{
    /**
     * Gets the training dataset.
     *
     * @return The training dataset.
     */
    public Collection<DataType> getTrainingSet();

    /**
     * Gets the testing dataset.
     *
     * @return The testing dataset.
     */
    public Collection<DataType> getTestingSet();
}
