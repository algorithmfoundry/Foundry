/*
 * File:                GeneralizedHebbianAlgorithmTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright October 8, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.learning.algorithm.pca;

/**
 *
 * @author Kevin R. Dixon
 */
//public class GeneralizedHebbianAlgorithmTest extends TestCase
public class GeneralizedHebbianAlgorithmTest
    extends PrincipalComponentsAnalysisTestHarness
{

    public GeneralizedHebbianAlgorithmTest(
        String testName )
    {
        super( testName );
    }

    public GeneralizedHebbianAlgorithm createPCAInstance()
    {
        return new GeneralizedHebbianAlgorithm( OUTPUT_DIM, 1e-1, 1000, 1e-5 );
    }

    /**
     * Test of stop method, of class gov.sandia.cognition.learning.hebbian.GeneralizedHebbianAlgorithm.
     */
    public void testStop()
    {
        System.out.println( "stop" );

        GeneralizedHebbianAlgorithm instance = this.createPCAInstance();

        instance.stop();

        assertFalse( instance.getKeepGoing() );

    }

    /**
     * Test of isResultValid method, of class gov.sandia.cognition.learning.hebbian.GeneralizedHebbianAlgorithm.
     */
    public void testIsResultValid()
    {
        System.out.println( "isResultValid" );

        GeneralizedHebbianAlgorithm instance = this.createPCAInstance();
        assertEquals( instance.isResultValid(), (instance.getResult() != null) );
    }

    /**
     * Test of getLearningRate method, of class gov.sandia.cognition.learning.hebbian.GeneralizedHebbianAlgorithm.
     */
    public void testGetLearningRate()
    {
        System.out.println( "getLearningRate" );

        double a = random.nextDouble();
        GeneralizedHebbianAlgorithm instance =
            new GeneralizedHebbianAlgorithm( 10, a, 100, 1e-10 );
        assertEquals( a, instance.getLearningRate() );

    }

    /**
     * Test of setLearningRate method, of class gov.sandia.cognition.learning.hebbian.GeneralizedHebbianAlgorithm.
     */
    public void testSetLearningRate()
    {
        System.out.println( "setLearningRate" );

        double a = random.nextDouble();
        GeneralizedHebbianAlgorithm instance =
            new GeneralizedHebbianAlgorithm( 10, a, 100, 1e-10 );
        assertEquals( a, instance.getLearningRate() );

        double a2 = 0.5 * a;
        instance.setLearningRate( a2 );
        assertEquals( a2, instance.getLearningRate() );

        instance.setLearningRate( 1.0 );

        try
        {
            instance.setLearningRate( 0.0 );
            fail( "Learning rate must be (0,1]" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }

        try
        {
            instance.setLearningRate( 1.0 + random.nextDouble() );
            fail( "Learning rate must be (0,1]" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }

    }

    /**
     * Test of getNumComponents method, of class gov.sandia.cognition.learning.hebbian.GeneralizedHebbianAlgorithm.
     */
    public void testGetNumComponents()
    {
        System.out.println( "getNumComponents" );

        int M = random.nextInt( 100 ) + 1;
        GeneralizedHebbianAlgorithm instance =
            new GeneralizedHebbianAlgorithm( M, random.nextDouble(), 100, 1e-10 );
        assertEquals( M, instance.getNumComponents() );

    }

    /**
     * Test of setNumComponents method, of class gov.sandia.cognition.learning.hebbian.GeneralizedHebbianAlgorithm.
     */
    public void testSetNumComponents()
    {
        System.out.println( "setNumComponents" );

        int M = random.nextInt( 100 ) + 1;
        GeneralizedHebbianAlgorithm instance =
            new GeneralizedHebbianAlgorithm( M, random.nextDouble(), 100, 1e-10 );
        assertEquals( M, instance.getNumComponents() );

        int M2 = instance.getNumComponents() + 1;
        instance.setNumComponents( M2 );
        assertEquals( M2, instance.getNumComponents() );

        try
        {
            instance.setNumComponents( 0 );
            fail( "Num components must be > 0" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }
    }

    /**
     * Test of getMinChange method, of class gov.sandia.cognition.learning.hebbian.GeneralizedHebbianAlgorithm.
     */
    public void testGetMinChange()
    {
        System.out.println( "getMinChange" );

        double minChange = random.nextDouble();
        GeneralizedHebbianAlgorithm instance =
            new GeneralizedHebbianAlgorithm( OUTPUT_DIM, random.nextDouble(), 1, minChange );

        assertEquals( minChange, instance.getMinChange() );

    }

    /**
     * Test of setMinChange method, of class gov.sandia.cognition.learning.hebbian.GeneralizedHebbianAlgorithm.
     */
    public void testSetMinChange()
    {
        System.out.println( "setMinChange" );

        double minChange = random.nextDouble();
        GeneralizedHebbianAlgorithm instance =
            new GeneralizedHebbianAlgorithm( OUTPUT_DIM, random.nextDouble(), 1, minChange );

        assertEquals( minChange, instance.getMinChange() );

        double m2 = minChange * 2.0;

        instance.setMinChange( m2 );
        assertEquals( m2, instance.getMinChange() );

        instance.setMinChange( 0.0 );

        try
        {
            instance.setMinChange( -1 );
            fail( "minChange must be >= 0" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }
    }

}
