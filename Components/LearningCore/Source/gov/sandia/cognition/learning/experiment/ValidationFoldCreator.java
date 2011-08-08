/*
 * File:                ValidationFoldCreator.java
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

package gov.sandia.cognition.learning.experiment;

import gov.sandia.cognition.learning.data.PartitionedDataset;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * The {@code ValidationFoldCreator} interface defines the functionality for
 * an object that can create a collection of folds for a validation experiment
 * where a set of data is split into training and testing sets multiple times.
 *
 * @param  <InputDataType>
 *      The type of the data to create the folds from. It is typically the same
 *      as the FoldDataType, but that is not required.
 * @param  <FoldDataType> 
 *      The type of data that the folds can be created for.
 * @author Justin Basilico
 * @since  2.0
 */
public interface ValidationFoldCreator<InputDataType, FoldDataType>
    extends Serializable
{
    /**
     * Creates a list of partitioned (training and testing) datasets from the
     * given single dataset.
     *
     * @param  data The data to create multiple folds from.
     * @return The list of partitioned datasets.
     */
    public List<PartitionedDataset<FoldDataType>> createFolds(
        Collection<? extends InputDataType> data);
}
