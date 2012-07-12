/*
 * File:                MatrixFactoryTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Jun 25, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.math.matrix;

import java.util.Random;

/**
 * Unit tests for MatrixFactoryTest.
 *
 * @author krdixon
 */
public class MatrixFactoryTest
    extends MatrixFactoryTestHarness
{

    /**
     * Random number generator to use for a fixed random seed.
     */
    public Random random = new Random( 1 );

    /**
     * Default tolerance of the regression tests, {@value}.
     */
    public double EPS = 1e-5;

    /**
     * Tests for class MatrixFactoryTest.
     * @param testName Name of the test.
     */
    public MatrixFactoryTest(
        String testName)
    {
        super(testName);
    }

    @Override
    public MatrixFactory<?> createFactory()
    {
        return MatrixFactory.getDefault();
    }

    @Override
    public Matrix createRandomMatrix()
    {
        int M = random.nextInt(10) + 1;
        int N = random.nextInt(10) + 1;
        return this.createFactory().createUniformRandom(M, N, -RANGE, RANGE, random);
    }

}
