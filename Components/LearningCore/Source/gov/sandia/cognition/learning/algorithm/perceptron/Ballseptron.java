/*
 * File:                BallseptronAlgorithm.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry Learning Core
 * 
 * Copyright February 21, 2011, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government.
 *
 */

package gov.sandia.cognition.learning.algorithm.perceptron;

import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.learning.function.categorization.DefaultKernelBinaryCategorizer;
import gov.sandia.cognition.learning.function.categorization.LinearBinaryCategorizer;
import gov.sandia.cognition.learning.function.kernel.KernelUtil;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.util.ArgumentChecker;

/**
 * An implementation of the Ballseptron algorithm. It is a Perceptron-style
 * online learning algorithm that involves a margin update. Thus, it has both
 * a linear and kernel form.
 * 
 * @author  Justin Basilico
 * @since   3.3.0
 */
@PublicationReference(
    author={"Shai Shalev-Shwartz", "Yoram Singer"},
    title="A New Perspective on an Old Perceptron Algorithm",
    year=2005,
    type=PublicationType.Conference,
    publication="Conference on Learning Theory",
    pages={815, 824},
    url="http://www.springerlink.com/index/hr4hrbyajy0y8a7l.pdf")
public class Ballseptron
    extends AbstractKernelizableBinaryCategorizerOnlineLearner
{
    
    /** The default radius is {@value}. */
    public static final double DEFAULT_RADIUS = 0.1;

    /** The radius enforced by the algorithm. */
    protected double radius;

    /**
     * Creates a new {@code Ballseptron} with default parameters.
     */
    public Ballseptron()
    {
        this(DEFAULT_RADIUS);
    }

    /**
     * Creates a new {@code Ballseptron} with the given radius.
     *
     * @param   radius
     *      The radius.
     */
    public Ballseptron(
        final double radius)
    {
        super();

        this.setRadius(radius);
    }

    @Override
    public void update(
        final LinearBinaryCategorizer target,
        final Vector input,
        final boolean label)
    {
        Vector weights = target.getWeights();
        if (weights == null)
        {
            // This is the first example, so initialize the weight vector.
            weights = this.getVectorFactory().createVector(
                input.getDimensionality());
            target.setWeights(weights);
        }
        // else - Use the existing weights.

        // Predict the output as a double (negative values are false, positive
        // are true).
        final double prediction = target.evaluateAsDouble(input);
        final double actual = label ? +1.0 : -1.0;
        final double margin = prediction * actual;


        boolean error = false;
        if (margin <= 0.0)
        {
            // An actual mistake: Use the standard perceptron update rule.
            error = true;
        }
        else
        {
            final double weightNorm = weights.norm2();
            if (margin / weightNorm <= this.getRadius())
            {
                // This is one way to implement this. However, it is not as
                // efficient as the following way with sparse vectors, which
                // is based on the derivation:
                // final Vector change = weights.scale(
                //     -actual * this.getRadius() / weightNorm);
                // change.plusEquals(input);
                // change.scaleEquals(actual);
                // weights.plusEquals(change);

                final double scale = 1.0 - this.getRadius() / weightNorm;
                weights.scaleEquals(scale);
                error = true;
            }
            // else - No margin mistake change.
        }

        if (error)
        {
            if (label)
            {
                weights.plusEquals(input);
            }
            else
            {
                weights.minusEquals(input);
            }
        }
    }


    @Override
    public <InputType> void update(
        final DefaultKernelBinaryCategorizer<InputType> target,
        final InputType input,
        final boolean label)
    {
        // Predict the output as a double (negative values are false, positive
        // are true).
        final double prediction = target.evaluateAsDouble(input);
        final double actual = label ? +1.0 : -1.0;
        final double margin = prediction * actual;

        if (margin <= 0.0)
        {
            target.add(input, actual);
        }
        else
        {
            final double weightNorm = KernelUtil.norm2(target);
            if (margin / weightNorm <= this.getRadius())
            {
                // This update is equivalent to what is described in the paper
                // using the following identity:
                // z = x - y r w / ||w||
                // w2 = w + y z
                //    = w + y (x - y r w / ||w||)
                //    = y x + w - y^2 r w / ||w||
                //    = y x + w (1 - r / ||w||)
                final double scale = 1.0 - this.getRadius() / weightNorm;
                KernelUtil.scaleEquals(target, scale);
                
                target.add(input, actual);
            }
            // else - No margin mistake change.
        }
    }

    /**
     * Gets the radius parameter.
     *
     * @return
     *      The radius parameter. Must be positive.
     */
    public double getRadius()
    {
        return this.radius;
    }
    
    /**
     * Sets the radius parameter.
     *
     * @param   radius
     *      The radius parameter. Must be positive.
     */
    public void setRadius(
        final double radius)
    {
        ArgumentChecker.assertIsPositive("radius", radius);
        this.radius = radius;
    }

}
