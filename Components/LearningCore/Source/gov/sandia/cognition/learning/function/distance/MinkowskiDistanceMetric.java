/*
 * File:            MinkowskiDistance.java
 * Authors:         Justin Basilico
 * Project:         Cognitive Foundry
 * 
 * Copyright 2011 Cognitive Foundry. All rights reserved.
 */

package gov.sandia.cognition.learning.function.distance;

import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.math.Metric;
import gov.sandia.cognition.math.matrix.Vectorizable;
import gov.sandia.cognition.util.AbstractCloneableSerializable;
import gov.sandia.cognition.util.ArgumentChecker;

/**
 * An implementation of the Minkowski distance metric. The metric is a
 * generalization of the 2-norm (Euclidean) and 1-norm (Manhattan) distances
 * to an arbitrary p-norm (p > 0).
 *
 * It is defined as:
 *     d(x, y) = (||x - y||_p)^(1/p)
 *
 * To support a power of infinity, see {@code ChebyshevDistanceMetric}.
 * 
 * @author  Justin Basilico
 * @since   3.3.3
 */
@PublicationReference(
    title="Minkowski Distance",
    author="Wikipedia",
    year=2011,
    type=PublicationType.WebPage,
    url="http://en.wikipedia.org/wiki/Minkowski_distance")
public class MinkowskiDistanceMetric
    extends AbstractCloneableSerializable
    implements Metric<Vectorizable>
{
    /** The default power is {@value}. */
    public static final double DEFAULT_POWER = 2.0;

    /** The power that the distance is computed to. */
    protected double power;

    /**
     * Creates a new {@code MinkowskiDistanceMetric} with the default power of
     * 2.0.
     */
    public MinkowskiDistanceMetric()
    {
        this(DEFAULT_POWER);
    }

    /**
     * Creates a new {@code MinkowskiDistanceMetric} with the given power.
     *
     * @param   power
     *      The power for the distance metric. Must be positive.
     */
    public MinkowskiDistanceMetric(
        final double power)
    {
        super();

        this.setPower(power);
    }

    @Override
    public double evaluate(
        final Vectorizable first,
        final Vectorizable second)
    {
        return first.convertToVector().minus(
            second.convertToVector()).norm(this.power);
    }

    /**
     * Gets the power used for the distance.
     *
     * @return
     *      The power used for the distance. Must be positive.
     */
    public double getPower()
    {
        return this.power;
    }

    /**
     * Sets the power used for the distance.
     *
     * @param   power
     *      The power used for the distance. Must be positive.
     */
    public void setPower(
        final double power)
    {
        ArgumentChecker.assertIsPositive("power", power);
        this.power = power;
    }

}
