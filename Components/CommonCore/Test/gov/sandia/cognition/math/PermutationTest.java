/*
 * File:                PermutationTest.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright 2006, Sandia Corporation. Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 */

package gov.sandia.cognition.math;

import junit.framework.*;
import java.util.ArrayList;
import java.util.Random;

/**
 *
 * @author jdbasil
 */
public class PermutationTest extends TestCase
{
    
    public PermutationTest(String testName)
    {
        super(testName);
    }

    protected void setUp() throws Exception
    {
    }

    protected void tearDown() throws Exception
    {
    }

    public static Test suite()
    {
        TestSuite suite = new TestSuite(PermutationTest.class);
        
        return suite;
    }


    /**
     * Test of reorder method, of class Permutation.
     */
    public void testReorder()
    {
        ArrayList<Integer> inputs = new ArrayList<Integer>();
        inputs.add(1);
        inputs.add(2);
        inputs.add(3);
        inputs.add(4);

        long seed = (long) (Math.random() * Long.MAX_VALUE);
        Random random = new Random(seed);
        Random randomCopy = new Random(seed);

        ArrayList<Integer> result = new ArrayList<Integer>(inputs);
        Permutation.reorder(result, random);

        assertEquals(4, result.size());

        for ( Integer i : inputs )
        {
            assertTrue(result.contains(i));
        }

        for ( Integer i : result )
        {
            assertTrue(inputs.contains(i));
        }

        ArrayList<Integer> result2 = Permutation.createReordering(
            new ArrayList<Integer>(inputs), randomCopy);

        assertEquals(result, result2);
    }

    /**
     * Test of createPermutation method, of class gov.sandia.isrc.math.Permutation.
     */
    public void testCreateReordering()
    {
        ArrayList<Integer> inputs = new ArrayList<Integer>();
        inputs.add(1);
        inputs.add(2);
        inputs.add(3);
        inputs.add(4);
        
        long seed = (long) (Math.random() * Long.MAX_VALUE);
        Random random = new Random(seed);
        Random randomCopy = new Random(seed);
        
        ArrayList<Integer> result = Permutation.createReordering(
            new ArrayList<Integer>(inputs), random);
        
        assertEquals(4, result.size());
        
        for ( Integer i : inputs )
        {
            assertTrue(result.contains(i));
        }
        
        for ( Integer i : result )
        {
            assertTrue(inputs.contains(i));
        }
        
        ArrayList<Integer> result2 = Permutation.createReordering(
            new ArrayList<Integer>(inputs), randomCopy);
        
        assertEquals(result, result2);
    }

    /**
     * Test of createPermutation method, of class gov.sandia.isrc.math.Permutation.
     */
    public void testCreatePermutation()
    {
        int n = 100;
        long seed = (long) (Math.random() * Long.MAX_VALUE);
        Random random = new Random(seed);
        Random randomCopy = new Random(seed);
        
        int[] perm = Permutation.createPermutation(n, random);
        assertEquals(n, perm.length);
        
        for (int i = 0; i < n; i++)
        {
            int numMatch = 0;
            
            for (int j = 0; j < n; j++)
            {
                if ( i == perm[j] )
                {
                    numMatch++;
                }
            }
            
            assertEquals(1, numMatch);
        }
        
        int[] perm2 = Permutation.createPermutation(n, randomCopy);
        assertEquals(n, perm2.length);
        for (int i = 0; i < n; i++)
        {
            assertEquals(perm[i], perm2[i]);
        }
    }
    
    /**
     * Test of createPartialPermutation method, of class Permutation.
     */
    public void testCreatePartialPermutation()
    {
        int n = 100;
        int k = 90;
        long seed = (long) (Math.random() * Long.MAX_VALUE);
        Random random = new Random(seed);
        Random randomCopy = new Random(seed);
        
        int[] perm = Permutation.createPartialPermutation(n, k, random);
        assertEquals(k, perm.length);
        
        
        for (int value : perm)
        {
            assertTrue(value >= 0);
            assertTrue(value < n);
        }
        
        for (int i = 0; i < n; i++)
        {
            int numMatch = 0;
            
            for (int j = 0; j < k; j++)
            {
                if ( i == perm[j] )
                {
                    numMatch++;
                }
            }
            
            assertTrue(numMatch == 1 || numMatch == 0);
        }
        
        
        int[] perm2 = Permutation.createPartialPermutation(n, k, randomCopy);
        assertEquals(k, perm2.length);
        for (int i = 0; i < k; i++)
        {
            assertEquals(perm[i], perm2[i]);
        }
        
        boolean exceptionThrown = false;
        try
        {
            Permutation.createPartialPermutation(n, n + 1, random);
        }
        catch (IllegalArgumentException e)
        {
            exceptionThrown = true;
        }
        finally
        {
            assertTrue(exceptionThrown);
        }
    }
}
