/*
 * File:                ProjectronLinearSoftMarginTest.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry Learning Core
 * 
 * Copyright May 04, 2011, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 */

package gov.sandia.cognition.learning.algorithm.perceptron.kernel;

import gov.sandia.cognition.learning.function.kernel.PolynomialKernel;
import gov.sandia.cognition.learning.function.kernel.Kernel;
import gov.sandia.cognition.math.matrix.Vector;
import org.junit.Test;
import static org.junit.Assert.*;


/**
 * Unit tests for class Projectron.LinearSoftMargin.
 *
 * @author  Justin Basilico
 * @since   3.3.0
 */
public class ProjectronLinearSoftMarginTest
    extends ProjectronTest
{
    /**
     * Creates a new test.
     */
    public ProjectronLinearSoftMarginTest()
    {
    }

    @Override
    protected Projectron.LinearSoftMargin<Vector> createInstance(
        final Kernel<? super Vector> kernel)
    {
        return new Projectron.LinearSoftMargin<Vector>(kernel);
    }

    /**
     * Test of constructors of class Projectron.LinearSoftMargin.
     */
    @Test
    @Override
    public void testConstructors()
    {
        Kernel<? super Vector> kernel = null;
        double eta = Projectron.DEFAULT_ETA;
        Projectron.LinearSoftMargin<Vector> instance = new Projectron.LinearSoftMargin<Vector>();
        assertSame(kernel, instance.getKernel());
        assertEquals(eta, instance.getEta(), 0.0);

        kernel = new PolynomialKernel(
            1 + this.random.nextInt(10), this.random.nextDouble());
        instance = new Projectron.LinearSoftMargin<Vector>(kernel);
        assertSame(kernel, instance.getKernel());
        assertEquals(eta, instance.getEta(), 0.0);

        eta = random.nextDouble();
        instance = new Projectron.LinearSoftMargin<Vector>(kernel, eta);
        assertSame(kernel, instance.getKernel());
        assertEquals(eta, instance.getEta(), 0.0);
    }

}