/*
 * File:            MatrixInteroperabilityTest.java
 * Authors:         Justin Basilico
 * Project:         Cognitive Foundry
 * 
 * Copyright 2015 Cognitive Foundry. All rights reserved.
 */

package gov.sandia.cognition.math.matrix;

import gov.sandia.cognition.collection.CollectionUtil;
import static gov.sandia.cognition.math.matrix.MatrixInteroperabilityTest.assertEquals;
import gov.sandia.cognition.math.matrix.mtj.DenseMatrixFactoryMTJ;
import gov.sandia.cognition.math.matrix.mtj.SparseMatrixFactoryMTJ;
import gov.sandia.cognition.math.matrix.custom.CustomDenseMatrixFactory;
import gov.sandia.cognition.math.matrix.custom.CustomSparseMatrixFactory;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import junit.framework.TestCase;
import static junit.framework.TestCase.assertTrue;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for the interoperability of different {@link Matrix} 
 * implementations.
 * 
 * @author  Justin Basilico
 * @since   3.4.3
 */
public class MatrixInteroperabilityTest
{
    protected List<List<List<Matrix>>> groups;
    protected List<MatrixFactory<?>> factories;
    protected Random random = new Random(22233);
    protected double epsilon = 1e-10;
    
    public MatrixInteroperabilityTest()
    {
        super();
    }
    
    
    public static List<MatrixFactory<?>> getFactories()
    {
        List<MatrixFactory<?>> factories = new LinkedList<>();
        factories.add(new DenseMatrixFactoryMTJ());
        factories.add(new SparseMatrixFactoryMTJ());
        factories.add(new CustomDenseMatrixFactory());
        factories.add(new CustomSparseMatrixFactory());
        return factories;
    }
    @Before
    public void setUp()
    {
        this.groups = new LinkedList<>();
        
        this.factories = getFactories();
        
        for (int i = 0; i < 10; i++)
        {
            // General-purpose ones.
            int m = 1 + random.nextInt(10);
            int n = 1 + random.nextInt(10);
            for (MatrixFactory<?> f : this.factories)
            {
                Matrix a = f.createUniformRandom(m, n, -10, 10, random);
                Matrix b = f.createUniformRandom(m, n, -10, 10, random);
                List<Matrix> as = new ArrayList<>();
                as.add(a);
                List<Matrix> bs = new ArrayList<>();
                bs.add(b);
                
                for (MatrixFactory<?> g : this.factories)
                {
                    as.add(g.copyMatrix(a));
                    bs.add(g.copyMatrix(b));
                }
                
                groups.add(CollectionUtil.createArrayList(as, bs));
            }
        }
    }
    
    @Test
    public void testEquals()
    {

        for (List<List<Matrix>> group : groups)
        {
            List<Matrix> as = group.get(0);
            List<Matrix> bs = group.get(1);
            
            boolean expected = as.get(0).equals(bs.get(0));
            
            for (Matrix a1 : as)
            {
                for (Matrix a2 : as)
                {
                    assertTrue("" + a1.getClass() + " -> " + a2.getClass(),
                        a1.clone().equals(a2.clone()));
                }
            }
            for (Matrix a : as)
            {
                for (Matrix b : bs)
                {
                    TestCase.assertEquals(expected, a.clone().equals(b.clone()));
                }
            }
            
            for (Matrix b1 : bs)
            {
                for (Matrix b2 : bs)
                {
                    assertTrue(b1.clone().equals(b2.clone()));
                }
            }
        }
    }
    
    @Test
    public void testEqualsWithEffectiveZero()
    {

        for (List<List<Matrix>> group : groups)
        {
            List<Matrix> as = group.get(0);
            List<Matrix> bs = group.get(1);
            
            boolean expected = as.get(0).equals(bs.get(0), epsilon);
            
            for (Matrix a1 : as)
            {
                for (Matrix a2 : as)
                {
                    assertTrue(a1.clone().equals(a2.clone(), epsilon));
                }
            }
            for (Matrix a : as)
            {
                for (Matrix b : bs)
                {
                    TestCase.assertEquals(expected, a.clone().equals(b.clone(), epsilon));
                }
            }
            
            for (Matrix b1 : bs)
            {
                for (Matrix b2 : bs)
                {
                    assertTrue(b1.clone().equals(b2.clone(), epsilon));
                }
            }
        }
    }
    
    @Test
    public void testPlus()
    {

        for (List<List<Matrix>> group : groups)
        {
            List<Matrix> as = group.get(0);
            List<Matrix> bs = group.get(1);
            
            Matrix expected = as.get(0).plus(bs.get(0));
            
            for (Matrix a : as)
            {
                for (Matrix b : bs)
                {
                    Matrix actual = a.clone().plus(b.clone());
                    assertEquals(expected, actual, epsilon);
                }
            }
        }
    }
    
    @Test
    public void testPlusEquals()
    {

        for (List<List<Matrix>> group : groups)
        {
            List<Matrix> as = group.get(0);
            List<Matrix> bs = group.get(1);
            
            Matrix expected = as.get(0).clone();
            expected.plusEquals(bs.get(0).clone());
            
            for (Matrix a : as)
            {
                for (Matrix b : bs)
                {
                    Matrix actual = a.clone();
                    actual.plusEquals(b.clone());
                    assertEquals(expected, actual, epsilon);
                }
            }
        }
    }
    
    @Test
    public void testMinus()
    {

        for (List<List<Matrix>> group : groups)
        {
            List<Matrix> as = group.get(0);
            List<Matrix> bs = group.get(1);
            
            Matrix expected = as.get(0).minus(bs.get(0));
            
            for (Matrix a : as)
            {
                for (Matrix b : bs)
                {
                    Matrix actual = a.clone().minus(b.clone());
                    assertEquals(expected, actual, epsilon);
                }
            }
        }
    }
    
    @Test
    public void testMinusEquals()
    {

        for (List<List<Matrix>> group : groups)
        {
            List<Matrix> as = group.get(0);
            List<Matrix> bs = group.get(1);
            
            Matrix expected = as.get(0).clone();
            expected.minusEquals(bs.get(0).clone());
            
            for (Matrix a : as)
            {
                for (Matrix b : bs)
                {
                    Matrix actual = a.clone();
                    actual.minusEquals(b.clone());
                    assertEquals(expected, actual, epsilon);
                }
            }
        }
    }
    
    @Test
    public void testDotTimes()
    {

        for (List<List<Matrix>> group : groups)
        {
            List<Matrix> as = group.get(0);
            List<Matrix> bs = group.get(1);
            
            Matrix expected = as.get(0).dotTimes(bs.get(0));
            
            for (Matrix a : as)
            {
                for (Matrix b : bs)
                {
                    Matrix actual = a.clone().dotTimes(b.clone());
                    assertEquals(expected, actual, epsilon);
                }
            }
        }
    }
    
    @Test
    public void testDotTimesEquals()
    {

        for (List<List<Matrix>> group : groups)
        {
            List<Matrix> as = group.get(0);
            List<Matrix> bs = group.get(1);
            
            Matrix expected = as.get(0).clone();
            expected.dotTimesEquals(bs.get(0).clone());
            
            for (Matrix a : as)
            {
                for (Matrix b : bs)
                {
                    Matrix actual = a.clone();
                    actual.dotTimesEquals(b.clone());
                    assertEquals(expected, actual, epsilon);
                }
            }
        }
    }
    
    
    @Test
    public void testDotDivide()
    {

        for (List<List<Matrix>> group : groups)
        {
            List<Matrix> as = group.get(0);
            List<Matrix> bs = group.get(1);
            
            Matrix expected = as.get(0).dotDivide(bs.get(0));
            
            for (Matrix a : as)
            {
                for (Matrix b : bs)
                {
                    Matrix actual = a.clone().dotDivide(b.clone());
                    assertEquals(expected, actual, epsilon);
                }
            }
        }
    }
    
    @Test
    public void testDotDivideEquals()
    {

        for (List<List<Matrix>> group : groups)
        {
            List<Matrix> as = group.get(0);
            List<Matrix> bs = group.get(1);
            
            Matrix expected = as.get(0).clone();
            expected.dotDivideEquals(bs.get(0).clone());
            
            for (Matrix a : as)
            {
                for (Matrix b : bs)
                {
                    Matrix actual = a.clone();
                    actual.dotDivideEquals(b.clone());
                    assertEquals(expected, actual, epsilon);
                }
            }
        }
    }
    
    @Test
    public void testScaledPlus()
    {

        for (List<List<Matrix>> group : groups)
        {
            List<Matrix> as = group.get(0);
            List<Matrix> bs = group.get(1);
            double scale = this.random.nextGaussian();
            
            Matrix expected = as.get(0).scaledPlus(scale, bs.get(0));
            
            for (Matrix a : as)
            {
                for (Matrix b : bs)
                {
                    Matrix actual = a.clone().scaledPlus(scale, b.clone());
                    assertEquals(expected, actual, epsilon);
                }
            }
        }
    }
    
    @Test
    public void testScaledPlusEquals()
    {

        for (List<List<Matrix>> group : groups)
        {
            List<Matrix> as = group.get(0);
            List<Matrix> bs = group.get(1);
            
            double scale = this.random.nextGaussian();
            
            Matrix expected = as.get(0).clone();
            expected.scaledPlusEquals(scale, bs.get(0).clone());
            
            for (Matrix a : as)
            {
                for (Matrix b : bs)
                {
                    Matrix actual = a.clone();
                    actual.scaledPlusEquals(scale, b.clone());
                    assertEquals(expected, actual, epsilon);
                }
            }
        }
    }
    
    @Test
    public void testScaledMinus()
    {

        for (List<List<Matrix>> group : groups)
        {
            List<Matrix> as = group.get(0);
            List<Matrix> bs = group.get(1);
            double scale = this.random.nextGaussian();
            
            Matrix expected = as.get(0).scaledMinus(scale, bs.get(0));
            
            for (Matrix a : as)
            {
                for (Matrix b : bs)
                {
                    Matrix actual = a.clone().scaledMinus(scale, b.clone());
                    assertEquals(expected, actual, epsilon);
                }
            }
        }
    }
    
    @Test
    public void testScaledMinusEquals()
    {

        for (List<List<Matrix>> group : groups)
        {
            List<Matrix> as = group.get(0);
            List<Matrix> bs = group.get(1);
            
            double scale = this.random.nextGaussian();
            
            Matrix expected = as.get(0).clone();
            expected.scaledMinusEquals(scale, bs.get(0).clone());
            
            for (Matrix a : as)
            {
                for (Matrix b : bs)
                {
                    Matrix actual = a.clone();
                    actual.scaledMinusEquals(scale, b.clone());
                    assertEquals(expected, actual, epsilon);
                }
            }
        }
    }
    
    
    @Test
    public void testTimes()
    {

        for (List<List<Matrix>> group : groups)
        {
            List<Matrix> as = group.get(0);
            List<Matrix> bs = group.get(1);

            Matrix expected = as.get(0).times(bs.get(0).transpose());
            
            for (Matrix a : as)
            {
                for (Matrix b : bs)
                {
                    Matrix actual = a.clone().times(b.clone().transpose());
                    assertEquals(expected, actual, epsilon);
                }
            }
        }
        
        // Non-square examples.
        for (int i = 0; i < 10; i++)
        {
            int m = 1 + random.nextInt(10);
            int n = 1 + random.nextInt(10);
            int o = 1 + random.nextInt(10);
            for (MatrixFactory<?> f : this.factories)
            {
                Matrix a = f.createUniformRandom(m, n, -10, 10, random);
                Matrix b = f.createUniformRandom(n, o, -10, 10, random);
                List<Matrix> as = new ArrayList<>();
                as.add(a);
                List<Matrix> bs = new ArrayList<>();
                bs.add(b);
                
                for (MatrixFactory<?> g : this.factories)
                {
                    as.add(g.copyMatrix(a));
                    bs.add(g.copyMatrix(b));
                }
                
                Matrix expected = a.times(b);
            
                for (Matrix a2 : as)
                {
                    for (Matrix b2 : bs)
                    {
                        Matrix actual = a2.clone().times(b2.clone());
                        assertEquals(expected, actual, epsilon);
                    }
                }
            }
        }
    }
    
    @Test
    public void testTimesVector()
    {
        for (List<List<Matrix>> group : groups)
        {
            Matrix a1 = group.get(0).get(0);
            
            for (VectorFactory<?> f : VectorInteroperabilityTest.getFactories())
            {
                Vector v1 = f.createUniformRandom(a1.getNumColumns(), -10, 10, random);
                Vector expected = a1.times(v1);
                
                for (VectorFactory<?> g : VectorInteroperabilityTest.getFactories())
                {
                    Vector v = g.copyVector(v1);
                    Vector actual = a1.clone().times(v);
                    VectorInteroperabilityTest.assertEquals(expected, actual, epsilon);
                }
            }
        }
    }
    
    public static void assertEquals(
        final Matrix expected,
        final Matrix actual,
        final double epsilon)
    {
        TestCase.assertEquals(0.0, expected.minus(actual).normFrobenius(), epsilon);
    }
}
