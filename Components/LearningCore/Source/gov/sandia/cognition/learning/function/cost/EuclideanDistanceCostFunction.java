/*
 * File:                EuclideanDistanceCostFunction.java
 * Authors:             Jonathan McClain
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright May 3, 2006, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.learning.function.cost;

import gov.sandia.cognition.annotation.CodeReview;
import gov.sandia.cognition.learning.function.distance.EuclideanDistanceMetric;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.Vectorizable;
import gov.sandia.cognition.util.AbstractCloneableSerializable;
import gov.sandia.cognition.util.ObjectUtil;

/**
 * The EuclideanDistanceCostFunction class implements a CostFunction that
 * calculates the Euclidean distance the given Vectorizable and the goal 
 * vector.
 *
 * @author Jonathan McClain
 * @since 1.0
 */
@CodeReview(
    reviewer="Justin Basilico",
    date="2006-10-04",
    changesNeeded=false,
    comments="Switched usage for the goal from Vectorizable to just a Vector so it does not call convertToVector() over and over."
)
public class EuclideanDistanceCostFunction
    extends AbstractCloneableSerializable
    implements CostFunction<Vectorizable, Vectorizable>
{
    /** The goal of the cost function. */
    private Vector goal;
    
    /**
     * Creates a new EuclideanDistanceCostFunction with no initial goal.
     */
    public EuclideanDistanceCostFunction()
    {
        this( (Vector) null);
    }
    
    /**
     * Creates a new instance of EuclideanDistanceCostFunction.
     *
     * @param goal The goal of the search.
     */
    public EuclideanDistanceCostFunction(
        Vector goal)
    {
        super();
        
        this.setCostParameters(goal);
    }
    
    @Override
    public EuclideanDistanceCostFunction clone()
    {
        EuclideanDistanceCostFunction clone =
            (EuclideanDistanceCostFunction) super.clone();
        clone.goal = ObjectUtil.cloneSafe(this.goal);
        return clone;
    }

    /**
     * Evaluates the Euclidean distance between the provided target and the 
     * goal.
     *
     * @param target The target to evaluate.
     * @return The distance between the target and the goal.
     */
    public Double evaluate(
        Vectorizable target) 
    {
        return EuclideanDistanceMetric.INSTANCE.evaluate(
            this.goal, target.convertToVector());
    }
    
    public void setCostParameters(
        Vectorizable costParameters)
    {
        if ( costParameters == null )
        {
            this.goal = null;
        }
        else
        {
            this.goal = costParameters.convertToVector();
        }
    }
    
    public Vectorizable getCostParameters()
    {
        return this.goal;
    }
    
}
