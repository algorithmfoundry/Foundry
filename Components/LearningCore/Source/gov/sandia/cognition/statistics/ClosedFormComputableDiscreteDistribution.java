/*
 * File:                ClosedFormComputableDiscreteDistribution.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright May 19, 2010, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.statistics;

/**
 * A discrete, closed-form Distribution with a PMF.
 * @param <DataType> Type of data in the Distribution.
 * @author Kevin R. Dixon
 * @since 3.0
 */
public interface ClosedFormComputableDiscreteDistribution<DataType>
    extends ClosedFormComputableDistribution<DataType>,
    DiscreteDistribution<DataType>
{
}
