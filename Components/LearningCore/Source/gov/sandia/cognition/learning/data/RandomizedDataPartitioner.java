/*
 * File:                RandomizedDataPartitioner.java
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

import gov.sandia.cognition.util.Randomized;

/**
 * The {@code RandomizedDataPartitioner} extends a {@code DataPartitioner} to
 * indicate that is it is randomized, which means that its partitions are based
 * (at least in part) on an underlying random number generator.
 *
 * @param  <DataType> The type of data to partition.
 * @author Justin Basilico
 * @since  2.0
 */
public interface RandomizedDataPartitioner<DataType>
    extends DataPartitioner<DataType>, Randomized
{
}
