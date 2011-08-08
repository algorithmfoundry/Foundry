/*
 * File:                KernelUtilTest.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry Learning Core
 * 
 * Copyright April 07, 2011, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 */

package gov.sandia.cognition.learning.function.kernel;

import java.util.Random;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.math.matrix.Vectorizable;
import gov.sandia.cognition.learning.function.categorization.DefaultKernelBinaryCategorizer;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for class KernelUtil.
 *
 * @author  Justin Basilico
 * @since   3.3.0
 */
public class KernelUtilTest
{
    protected Random random = new Random(211);
    
    /**
     * Creates a new test.
     */
    public KernelUtilTest()
    {
    }

    /**
     * Test of norm2 method, of class KernelUtil.
     */
    @Test
    public void testNorm2()
    {
        int d = this.random.nextInt(20) + 1;
        VectorFactory<?> vf = VectorFactory.getDenseDefault();
        Vector w = vf.createVector(d);

        DefaultKernelBinaryCategorizer<Vectorizable> f =
            new DefaultKernelBinaryCategorizer<Vectorizable>(new LinearKernel());
        f.setBias(this.random.nextGaussian());

        int count = this.random.nextInt(5) + 1;
        for (int i = 0; i < count; i++)
        {
            Vector x = vf.createUniformRandom(d, -10, +10, random);
            double a = random.nextGaussian();
            w.plusEquals(x.scale(a));
            f.add(x, a);

            assertEquals(x.norm2(), KernelUtil.norm2(x, new LinearKernel()), 1e-10);
        }

        assertEquals(w.norm2(), KernelUtil.norm2(f), 1e-10);
    }


    /**
     * Test of norm2Squared method, of class KernelUtil.
     */
    @Test
    public void testNorm2Squared()
    {
        int d = this.random.nextInt(20) + 1;
        VectorFactory<?> vf = VectorFactory.getDenseDefault();
        Vector w = vf.createVector(d);

        DefaultKernelBinaryCategorizer<Vectorizable> f =
            new DefaultKernelBinaryCategorizer<Vectorizable>(new LinearKernel());
        f.setBias(this.random.nextGaussian());

        int count = this.random.nextInt(5) + 1;
        for (int i = 0; i < count; i++)
        {
            Vector x = vf.createUniformRandom(d, -10, +10, random);
            double a = random.nextGaussian();
            w.plusEquals(x.scale(a));
            f.add(x, a);

            assertEquals(x.norm2Squared(), KernelUtil.norm2Squared(x, new LinearKernel()), 1e-10);
        }

        assertEquals(w.norm2Squared(), KernelUtil.norm2Squared(f), 1e-10);
    }

    /**
     * Test of scaleEquals method, of class KernelUtil.
     */
    @Test
    public void testScaleEquals()
    {
        int d = this.random.nextInt(20) + 1;
        VectorFactory<?> vf = VectorFactory.getDenseDefault();
        Vector w = vf.createVector(d);

        DefaultKernelBinaryCategorizer<Vectorizable> f =
            new DefaultKernelBinaryCategorizer<Vectorizable>(new LinearKernel());
        f.setBias(this.random.nextGaussian());

        int count = this.random.nextInt(5) + 1;
        for (int i = 0; i < count; i++)
        {
            Vector x = vf.createUniformRandom(d, -10, +10, random);
            double a = random.nextGaussian();
        }

        double scale = random.nextGaussian();

        assertEquals(w.norm2Squared(), KernelUtil.norm2Squared(f), 1e-10);
    }

}