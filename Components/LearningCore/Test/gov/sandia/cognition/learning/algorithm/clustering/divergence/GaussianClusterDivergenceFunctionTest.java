/*
 * File:                GaussianClusterDivergenceFunctionTest.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright August 10, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.learning.algorithm.clustering.divergence;

import gov.sandia.cognition.math.matrix.mtj.DenseVectorFactoryMTJ;
import gov.sandia.cognition.learning.algorithm.clustering.cluster.GaussianCluster;
import gov.sandia.cognition.math.matrix.Matrix;
import gov.sandia.cognition.math.matrix.MatrixFactory;
import gov.sandia.cognition.statistics.distribution.MultivariateGaussian;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorFactory;
import java.util.ArrayList;
import java.util.Random;
import junit.framework.TestCase;

/**
 * This class implements JUnit tests for the following classes:
 *
 *     GaussianClusterDivergenceFunction
 *
 * @author Justin Basilico
 * @since  2.0
 */
public class GaussianClusterDivergenceFunctionTest
    extends TestCase
{
    /** The random number generator for the tests. */
    private Random random = new Random(1);
    
    public GaussianClusterDivergenceFunctionTest(
        String testName)
    {
        super(testName);
    }

    public void testConstants()
    {
        assertNotNull(GaussianClusterDivergenceFunction.INSTANCE);
    }
    
    /**
     * Test of evaluate method, of class gov.sandia.cognition.learning.clustering.divergence.GaussianClusterDivergenceFunction.
     */
    public void testEvaluate()
    {
        GaussianClusterDivergenceFunction instance = 
            new GaussianClusterDivergenceFunction();

        Matrix R = MatrixFactory.getDefault().createUniformRandom(3, 3, -1.0, 1.0, random);
        Matrix C = R.times( R.transpose() );
        MultivariateGaussian.PDF gaussian = new MultivariateGaussian.PDF(
            VectorFactory.getDefault().createUniformRandom(3, -1.0, 1.0, random), C );;
        GaussianCluster cluster = new GaussianCluster(new ArrayList<Vector>(),
            gaussian);
        
        for (int i = 0; i < 10; i++)
        {
            Vector vector = 
                DenseVectorFactoryMTJ.INSTANCE.createUniformRandom(3, -5.0, 5.0, random);
            double expected = -gaussian.evaluate(vector);
            assertEquals(expected, instance.evaluate(cluster, vector));
        }
    }
}
