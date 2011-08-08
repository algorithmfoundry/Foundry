/*
 * File:                PerformanceEvaluator.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Framework Lite
 *
 * Copyright September 27, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 *
 */

package gov.sandia.cognition.learning.performance;

import gov.sandia.cognition.util.CloneableSerializable;

/**
 * The {@code PerformanceEvaluator} class defines the functionality of some
 * object with regards to some data.
 *
 * @param   <ObjectType> The type of object the class can evaluate the 
 *          performance of.
 * @param   <DataType> The data to use to evaluate the object's performance.
 * @param   <ResultType> The type of the result of the performance evaluation.
 * @author  Justin Basilico
 * @since   2.0
 */
public interface PerformanceEvaluator<ObjectType, DataType, ResultType>
    extends CloneableSerializable
{
    /**
     * Evaluates the performance of an object with regards to given data.
     *
     * @param  object The object to evaluate the performance of.
     * @param  data The data to evaluate the object using.
     * @return The performance evaluation result.
     */
    ResultType evaluatePerformance(
        ObjectType object,
        DataType data);
}
