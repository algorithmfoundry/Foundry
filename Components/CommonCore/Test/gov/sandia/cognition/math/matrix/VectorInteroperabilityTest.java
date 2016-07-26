/*
 * File:            VectorInteroperabilityTest.java
 * Authors:         Justin Basilico
 * Project:         Cognitive Foundry
 * 
 * Copyright 2015 Cognitive Foundry. All rights reserved.
 */

package gov.sandia.cognition.math.matrix;

import gov.sandia.cognition.collection.CollectionUtil;
import gov.sandia.cognition.math.matrix.mtj.DenseVectorFactoryMTJ;
import gov.sandia.cognition.math.matrix.mtj.SparseVectorFactoryMTJ;
import gov.sandia.cognition.math.matrix.custom.DenseVectorFactoryOptimized;
import gov.sandia.cognition.math.matrix.custom.SparseVectorFactoryOptimized;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import junit.framework.TestCase;
import static junit.framework.TestCase.assertTrue;
import static junit.framework.TestCase.fail;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for the interoperability of different {@link Vector} 
 * implementations.
 * 
 * @author  Justin Basilico
 * @since   3.4.3
 */
public class VectorInteroperabilityTest
{
    protected List<List<List<Vector>>> groups;
    protected Random random = new Random(22223);
    protected double epsilon = 1e-10;
    
    public VectorInteroperabilityTest()
    {
        super();
    }
    
    public static List<VectorFactory<?>> getFactories()
    {
        List<VectorFactory<?>> factories = new LinkedList<>();
        factories.add(new DenseVectorFactoryMTJ());
        factories.add(new SparseVectorFactoryMTJ());
        factories.add(new DenseVectorFactoryOptimized());
        factories.add(new SparseVectorFactoryOptimized());
        // Add new factories above here.
        
        return factories;
    }
    
    @Before
    public void setUp()
    {
        this.groups = new LinkedList<>();
        List<VectorFactory<?>> factories = getFactories();
        
        for (int i = 0; i < 10; i++)
        {
            // General-purpose ones.
            int d = 1 + random.nextInt(10);
            for (VectorFactory<?> f : factories)
            {
                Vector a = f.createUniformRandom(d, -10, 10, random);
                Vector b = f.createUniformRandom(d, -10, 10, random);
                List<Vector> as = new ArrayList<>();
                as.add(a);
                List<Vector> bs = new ArrayList<>();
                bs.add(b);
                
                for (VectorFactory<?> g : factories)
                {
                    as.add(g.copyVector(a));
                    bs.add(g.copyVector(b));
                }
                
                groups.add(CollectionUtil.createArrayList(as, bs));
            }
        }
    }
    
    @Test
    public void testEquals()
    {

        for (List<List<Vector>> group : groups)
        {
            List<Vector> as = group.get(0);
            List<Vector> bs = group.get(1);
            
            boolean expected = as.get(0).equals(bs.get(0));
            
            for (Vector a1 : as)
            {
                for (Vector a2 : as)
                {
                    assertTrue("" + a1.getClass() + " -> " + a2.getClass(),
                        a1.clone().equals(a2.clone()));
                }
            }
            for (Vector a : as)
            {
                for (Vector b : bs)
                {
                    TestCase.assertEquals(expected, a.clone().equals(b.clone()));
                }
            }
            
            for (Vector b1 : bs)
            {
                for (Vector b2 : bs)
                {
                    assertTrue(b1.clone().equals(b2.clone()));
                }
            }
        }
    }
    
    @Test
    public void testEqualsWithEffectiveZero()
    {

        for (List<List<Vector>> group : groups)
        {
            List<Vector> as = group.get(0);
            List<Vector> bs = group.get(1);
            
            boolean expected = as.get(0).equals(bs.get(0), epsilon);
            
            for (Vector a1 : as)
            {
                for (Vector a2 : as)
                {
                    assertTrue(a1.clone().equals(a2.clone(), epsilon));
                }
            }
            for (Vector a : as)
            {
                for (Vector b : bs)
                {
                    TestCase.assertEquals(expected, a.clone().equals(b.clone(), epsilon));
                }
            }
            
            for (Vector b1 : bs)
            {
                for (Vector b2 : bs)
                {
                    assertTrue(b1.clone().equals(b2.clone(), epsilon));
                }
            }
        }
    }
    
    @Test
    public void testPlus()
    {

        for (List<List<Vector>> group : groups)
        {
            List<Vector> as = group.get(0);
            List<Vector> bs = group.get(1);
            
            Vector expected = as.get(0).plus(bs.get(0));
            
            for (Vector a : as)
            {
                for (Vector b : bs)
                {
                    Vector actual = a.clone().plus(b.clone());
                    assertEquals(expected, actual, epsilon);
                }
            }
        }
    }
    
    @Test
    public void testPlusEquals()
    {

        for (List<List<Vector>> group : groups)
        {
            List<Vector> as = group.get(0);
            List<Vector> bs = group.get(1);
            
            Vector expected = as.get(0).clone();
            expected.plusEquals(bs.get(0).clone());
            
            for (Vector a : as)
            {
                for (Vector b : bs)
                {
                    Vector actual = a.clone();
                    actual.plusEquals(b.clone());
                    assertEquals(expected, actual, epsilon);
                }
            }
        }
    }
    
    @Test
    public void testMinus()
    {

        for (List<List<Vector>> group : groups)
        {
            List<Vector> as = group.get(0);
            List<Vector> bs = group.get(1);
            
            Vector expected = as.get(0).minus(bs.get(0));
            
            for (Vector a : as)
            {
                for (Vector b : bs)
                {
                    Vector actual = a.clone().minus(b.clone());
                    assertEquals(expected, actual, epsilon);
                }
            }
        }
    }
    
    @Test
    public void testMinusEquals()
    {

        for (List<List<Vector>> group : groups)
        {
            List<Vector> as = group.get(0);
            List<Vector> bs = group.get(1);
            
            Vector expected = as.get(0).clone();
            expected.minusEquals(bs.get(0).clone());
            
            for (Vector a : as)
            {
                for (Vector b : bs)
                {
                    Vector actual = a.clone();
                    actual.minusEquals(b.clone());
                    assertEquals(expected, actual, epsilon);
                }
            }
        }
    }
    
    @Test
    public void testDotTimes()
    {

        for (List<List<Vector>> group : groups)
        {
            List<Vector> as = group.get(0);
            List<Vector> bs = group.get(1);
            
            Vector expected = as.get(0).dotTimes(bs.get(0));
            
            for (Vector a : as)
            {
                for (Vector b : bs)
                {
                    Vector actual = a.clone().dotTimes(b.clone());
                    assertEquals(expected, actual, epsilon);
                }
            }
        }
    }
    
    @Test
    public void testDotTimesEquals()
    {

        for (List<List<Vector>> group : groups)
        {
            List<Vector> as = group.get(0);
            List<Vector> bs = group.get(1);
            
            Vector expected = as.get(0).clone();
            expected.dotTimesEquals(bs.get(0).clone());
            
            for (Vector a : as)
            {
                for (Vector b : bs)
                {
                    Vector actual = a.clone();
                    actual.dotTimesEquals(b.clone());
                    assertEquals(expected, actual, epsilon);
                }
            }
        }
    }
    
    
    @Test
    public void testDotDivide()
    {

        for (List<List<Vector>> group : groups)
        {
            List<Vector> as = group.get(0);
            List<Vector> bs = group.get(1);
            
            Vector expected = as.get(0).dotDivide(bs.get(0));
            
            for (Vector a : as)
            {
                for (Vector b : bs)
                {
                    Vector actual = a.clone().dotDivide(b.clone());
                    assertEquals(expected, actual, epsilon);
                }
            }
        }
    }
    
    @Test
    public void testDotDivideEquals()
    {

        for (List<List<Vector>> group : groups)
        {
            List<Vector> as = group.get(0);
            List<Vector> bs = group.get(1);
            
            Vector expected = as.get(0).clone();
            expected.dotDivideEquals(bs.get(0).clone());
            
            for (Vector a : as)
            {
                for (Vector b : bs)
                {
                    Vector actual = a.clone();
                    actual.dotDivideEquals(b.clone());
                    assertEquals(expected, actual, epsilon);
                }
            }
        }
    }
    
    @Test
    public void testScaledPlus()
    {

        for (List<List<Vector>> group : groups)
        {
            List<Vector> as = group.get(0);
            List<Vector> bs = group.get(1);
            double scale = this.random.nextGaussian();
            
            Vector expected = as.get(0).scaledPlus(scale, bs.get(0));
            
            for (Vector a : as)
            {
                for (Vector b : bs)
                {
                    Vector actual = a.clone().scaledPlus(scale, b.clone());
                    assertEquals(expected, actual, epsilon);
                }
            }
        }
    }
    
    @Test
    public void testScaledPlusEquals()
    {

        for (List<List<Vector>> group : groups)
        {
            List<Vector> as = group.get(0);
            List<Vector> bs = group.get(1);
            
            double scale = this.random.nextGaussian();
            
            Vector expected = as.get(0).clone();
            expected.scaledPlusEquals(scale, bs.get(0).clone());
            
            for (Vector a : as)
            {
                for (Vector b : bs)
                {
                    Vector actual = a.clone();
                    actual.scaledPlusEquals(scale, b.clone());
                    assertEquals(expected, actual, epsilon);
                }
            }
        }
    }
    
    @Test
    public void testScaledMinus()
    {

        for (List<List<Vector>> group : groups)
        {
            List<Vector> as = group.get(0);
            List<Vector> bs = group.get(1);
            double scale = this.random.nextGaussian();
            
            Vector expected = as.get(0).scaledMinus(scale, bs.get(0));
            
            for (Vector a : as)
            {
                for (Vector b : bs)
                {
                    Vector actual = a.clone().scaledMinus(scale, b.clone());
                    assertEquals(expected, actual, epsilon);
                }
            }
        }
    }
    
    @Test
    public void testScaledMinusEquals()
    {

        for (List<List<Vector>> group : groups)
        {
            List<Vector> as = group.get(0);
            List<Vector> bs = group.get(1);
            
            double scale = this.random.nextGaussian();
            
            Vector expected = as.get(0).clone();
            expected.scaledMinusEquals(scale, bs.get(0).clone());
            
            for (Vector a : as)
            {
                for (Vector b : bs)
                {
                    Vector actual = a.clone();
                    actual.scaledMinusEquals(scale, b.clone());
                    assertEquals(expected, actual, epsilon);
                }
            }
        }
    }
    
    @Test
    public void testDot()
    {

        for (List<List<Vector>> group : groups)
        {
            List<Vector> as = group.get(0);
            List<Vector> bs = group.get(1);
            
            double expected = as.get(0).dot(bs.get(0));
            
            for (Vector a : as)
            {
                for (Vector b : bs)
                {
                    double actual = a.clone().dot(b.clone());
                    TestCase.assertEquals(expected, actual, epsilon);
                }
            }
        }
    }
    
    @Test
    public void testDotProduct()
    {

        for (List<List<Vector>> group : groups)
        {
            List<Vector> as = group.get(0);
            List<Vector> bs = group.get(1);
            
            double expected = as.get(0).dotProduct(bs.get(0));
            
            for (Vector a : as)
            {
                for (Vector b : bs)
                {
                    double actual = a.clone().dotProduct(b.clone());
                    TestCase.assertEquals(expected, actual, epsilon);
                }
            }
        }
    }
    
    @Test
    public void testAngle()
    {

        for (List<List<Vector>> group : groups)
        {
            List<Vector> as = group.get(0);
            List<Vector> bs = group.get(1);
            
            double expected = as.get(0).angle(bs.get(0));
            
            for (Vector a : as)
            {
                for (Vector b : bs)
                {
                    double actual = a.clone().angle(b.clone());
                    TestCase.assertEquals(expected, actual, epsilon);
                }
            }
        }
    }
    
    @Test
    public void testCosine()
    {

        for (List<List<Vector>> group : groups)
        {
            List<Vector> as = group.get(0);
            List<Vector> bs = group.get(1);
            
            double expected = as.get(0).cosine(bs.get(0));
            
            for (Vector a : as)
            {
                for (Vector b : bs)
                {
                    double actual = a.clone().cosine(b.clone());
                    TestCase.assertEquals(expected, actual, epsilon);
                }
            }
        }
    }
    
    @Test
    public void testEuclideanDistance()
    {

        for (List<List<Vector>> group : groups)
        {
            List<Vector> as = group.get(0);
            List<Vector> bs = group.get(1);
            
            double expected = as.get(0).euclideanDistance(bs.get(0));
            
            for (Vector a : as)
            {
                for (Vector b : bs)
                {
                    double actual = a.clone().euclideanDistance(b.clone());
                    TestCase.assertEquals(expected, actual, epsilon);
                }
            }
        }
    }
    
    @Test
    public void testEuclideanDistanceSquared()
    {

        for (List<List<Vector>> group : groups)
        {
            List<Vector> as = group.get(0);
            List<Vector> bs = group.get(1);
            
            double expected = as.get(0).euclideanDistanceSquared(bs.get(0));
            
            for (Vector a : as)
            {
                for (Vector b : bs)
                {
                    double actual = a.clone().euclideanDistanceSquared(b.clone());
                    TestCase.assertEquals(expected, actual, epsilon);
                }
            }
        }
    }
    
    @Test
    public void testOuterProduct()
    {

        for (List<List<Vector>> group : groups)
        {
            List<Vector> as = group.get(0);
            List<Vector> bs = group.get(1);
            
            Matrix expected = as.get(0).outerProduct(bs.get(0));
            
            for (Vector a : as)
            {
                for (Vector b : bs)
                {
                    Matrix actual = a.clone().outerProduct(b.clone());
                    MatrixInteroperabilityTest.assertEquals(
                        expected, actual, epsilon);
                }
            }
        }
    }
    
    @Test
    public void testStack()
    {
        for (List<List<Vector>> group : groups)
        {
            List<Vector> as = group.get(0);
            List<Vector> bs = group.get(1);
            
            Vector expected = as.get(0).stack(bs.get(0));
            
            for (Vector a : as)
            {
                for (Vector b : bs)
                {
                    Vector actual = a.clone().stack(b.clone());
                    assertEquals(expected, actual, epsilon);
                }
            }
        }
    }
    
    @Test
    public void testTimesMatrix()
    {
        for (List<List<Vector>> group : groups)
        {
            List<Vector> as = group.get(0);
            
            final Vector a1 = as.get(0);
            
            for (MatrixFactory<?> f : MatrixInteroperabilityTest.getFactories())
            {
                int n = 1 + random.nextInt(10);
                Matrix m1 = f.createUniformRandom(a1.getDimensionality(), n, -10, 10, random);
                Vector expected = a1.times(m1);
                
                for (MatrixFactory<?> g : MatrixInteroperabilityTest.getFactories())
                {
                    Matrix m = g.copyMatrix(m1);
                    Vector actual = a1.clone().times(m);
                    assertEquals(expected, actual, epsilon);
                }
            }
        }
    }
    
    public static void assertEquals(
        final Vector expected,
        final Vector actual,
        final double effectiveZero)
    {
        TestCase.assertEquals(0.0, expected.minus(actual).norm2(), effectiveZero);
    }
    
}
