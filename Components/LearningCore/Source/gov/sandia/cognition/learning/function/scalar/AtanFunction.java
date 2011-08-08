/*
 * File:                AtanFunction.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright May 15, 2006, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.learning.function.scalar;

import gov.sandia.cognition.annotation.CodeReview;
import gov.sandia.cognition.annotation.CodeReviews;
import gov.sandia.cognition.math.AbstractDifferentiableUnivariateScalarFunction;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.Vectorizable;

/**
 * Returns the element-wise arctangent of the input vector, compressed between
 * -maxMagnitude and maxMagnitude (instead of just -PI/2 and PI/2)
 *
 * @author Kevin R. Dixon
 * @since  1.0
 */
@CodeReviews(
    reviews={
        @CodeReview(
            reviewer="Kevin R. Dixon",
            date="2009-07-06",
            changesNeeded=false,
            comments={
                "Made clone() call super.clone().",
                "Minor clean up of javadoc.",
                "Otherwise, class looks fine."
            }
        )
        ,
        @CodeReview(
            reviewer="Justin Basilico",
            date="2006-10-05",
            changesNeeded=false,
            comments="Class looks fine."
        )
    }
)
public class AtanFunction
    extends AbstractDifferentiableUnivariateScalarFunction
    implements Vectorizable
{

    /** The default max magnitude, which is PI / 2. */
    protected static final double DEFAULT_MAX_MAGNITUDE = Math.PI / 2.0;

    /** A unit scale squashing range. */
    private static final double UNIT_SCALE = 2.0 / Math.PI;

    /**
     * Scales the Math.atan() value to ensure that it has the desired 
     * "maxMagnitude"
     */
    private double scaleFactor;

    /**
     * Creates a new instance of AtanFunction with the standard
     * unit magnitude of -PI / 2 to PI / 2.
     */
    public AtanFunction()
    {
        this( DEFAULT_MAX_MAGNITUDE );
    }

    /**
     * Creates a new instance of AtanFunction.
     *
     * @param maxMagnitude maximum magnitude value of the function at
     * infinity and -infinity
     */
    public AtanFunction(
        double maxMagnitude )
    {
        super();

        this.setMaxMagnitude( maxMagnitude );
    }

    public double evaluate(
        double input )
    {
        return Math.atan( input ) * this.scaleFactor;
    }

    /**
     * Getter for maxMagnitude.
     *
     * @return maximum magnitude value of the function at infinity and -infinity
     */
    public double getMaxMagnitude()
    {
        return this.scaleFactor / AtanFunction.UNIT_SCALE;
    }

    /**
     * Setter for maxMagnitude.
     *
     * @param maxMagnitude maximum magnitude value of the function at
     * infinity and -infinity
     */
    public void setMaxMagnitude(
        double maxMagnitude )
    {
        this.scaleFactor = maxMagnitude * AtanFunction.UNIT_SCALE;
    }

    public double differentiate(
        double input )
    {
        double x = input;
        // atan() derivative is given in many reference books and Web sites,
        // such as http://www.mathreference.com/ca-int,tan.html
        return this.scaleFactor / (1.0 + x * x);
    }

    @Override
    public AtanFunction clone()
    {
        return (AtanFunction) super.clone();
    }

    public Vector convertToVector()
    {
        return VectorFactory.getDefault().copyValues( this.getMaxMagnitude() );
    }

    public void convertFromVector(
        Vector parameters )
    {
        this.setMaxMagnitude( parameters.getElement( 0 ) );
    }

}
