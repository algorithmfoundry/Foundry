/*
 * File:                AbstractAnytimeBatchLearnerTest.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright October 17, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.learning.algorithm;

import gov.sandia.cognition.algorithm.IterativeAlgorithmListener;
import gov.sandia.cognition.algorithm.IterativeAlgorithm;
import junit.framework.TestCase;

/**
 * This class implements JUnit tests for the following classes:
 *
 *     AbstractAnytimeBatchLearner
 *
 * @author Justin Basilico
 * @since  2.0
 */
public class AbstractAnytimeBatchLearnerTest
    extends TestCase
{

    public AbstractAnytimeBatchLearnerTest(
        String testName )
    {
        super( testName );
    }

    /**
     * Test of learn method, of class gov.sandia.cognition.learning.AbstractAnytimeBatchLearner.
     */
    public void testLearn()
    {
        int maxIterations = 1 + (int) (100.0 * Math.random());
        DummyStandardBatchIterativeLearner instance =
            new DummyStandardBatchIterativeLearner( maxIterations );
        DummyIterativeLearnerListener listener =
            new DummyIterativeLearnerListener();

        instance.addIterativeAlgorithmListener( listener );
        Object data = new Object();
        String learned = instance.learn( data );

        assertEquals( 1, instance.initializeLearningCount );
        assertEquals( maxIterations, instance.stepCount );
        assertEquals( 1, instance.cleanupLearningCount );

        assertEquals( 1, listener.learningStartedCount );
        assertEquals( maxIterations, listener.stepStartedCount );
        assertEquals( maxIterations, listener.stepEndedCount );
        assertEquals( 1, listener.learningEndedCount );

        assertSame( learned, instance.getResult() );
    }

    /**
     * Test of getLearned method, of class gov.sandia.cognition.learning.AbstractAnytimeBatchLearner.
     */
    public void testGetLearned()
    {
    // Tested by testLearn.
    }

    /**
     * Test of stop method, of class gov.sandia.cognition.learning.AbstractAnytimeBatchLearner.
     */
    public void testStop()
    {
        final int maxIterations = 100;
        final int stopAt = 4;
        final DummyStandardBatchIterativeLearner instance =
            new DummyStandardBatchIterativeLearner( maxIterations );
        assertFalse( instance.getKeepGoing() );

        final Object data = new Object();
        instance.addIterativeAlgorithmListener( new IterativeAlgorithmListener()
        {

            int count;

            public void algorithmStarted(
                IterativeAlgorithm learner )
            {

                count = 0;

            }

            public void algorithmEnded(
                IterativeAlgorithm learner )
            {

                count = -1;

                assertFalse( instance.getKeepGoing() );

            }

            public void stepStarted(
                IterativeAlgorithm learner )
            {

                count++;

            }

            public void stepEnded(
                IterativeAlgorithm learner )
            {

                if (count >= stopAt)
                {

                    assertTrue( instance.getKeepGoing() );

                    instance.stop();

                    assertFalse( instance.getKeepGoing() );

                }

            }

        } );

        String result = instance.learn( data );
        assertEquals( stopAt, instance.getIteration() );
    }

    /**
     * Test of isResultValid method, of class gov.sandia.cognition.learning.AbstractAnytimeBatchLearner.
     */
    public void testIsResultValid()
    {
        final DummyStandardBatchIterativeLearner instance =
            new DummyStandardBatchIterativeLearner( 10 );
        assertFalse( instance.isResultValid() );

        instance.result = "Test";
        assertTrue( instance.isResultValid() );

        instance.result = null;
        assertFalse( instance.isResultValid() );
    }

    /**
     * Test of getKeepGoing method, of class gov.sandia.cognition.learning.AbstractAnytimeBatchLearner.
     */
    public void testGetKeepGoing()
    {
        this.testSetKeepGoing();
    }

    /**
     * Test of setKeepGoing method, of class gov.sandia.cognition.learning.AbstractAnytimeBatchLearner.
     */
    public void testSetKeepGoing()
    {
        final int maxIterations = 100;
        final int stopAt = 4;
        final DummyStandardBatchIterativeLearner instance =
            new DummyStandardBatchIterativeLearner( maxIterations );
        assertFalse( instance.getKeepGoing() );

        final Object data = new Object();
        instance.addIterativeAlgorithmListener( new IterativeAlgorithmListener()
        {

            int count;

            public void algorithmStarted(
                IterativeAlgorithm learner )
            {

                count = 0;

                assertTrue( instance.getKeepGoing() );

            }

            public void algorithmEnded(
                IterativeAlgorithm learner )
            {

                count = -1;

                assertFalse( instance.getKeepGoing() );

            }

            public void stepStarted(
                IterativeAlgorithm learner )
            {

                count++;

                assertTrue( instance.getKeepGoing() );

            }

            public void stepEnded(
                IterativeAlgorithm learner )
            {

                assertTrue( instance.getKeepGoing() );



                boolean keepGoing = count < stopAt;

                instance.setKeepGoing( keepGoing );

                assertEquals( keepGoing, instance.getKeepGoing() );

            }

        } );

        instance.learn( data );
        assertFalse( instance.getKeepGoing() );

        instance.setKeepGoing( true );
        assertTrue( instance.getKeepGoing() );

        instance.setKeepGoing( false );
        assertFalse( instance.getKeepGoing() );
    }

    /**
     * Test of getMaxIterations method, of class gov.sandia.cognition.learning.AbstractAnytimeBatchLearner.
     */
    public void testGetMaxIterations()
    {
        this.testSetMaxIterations();
    }

    /**
     * Test of setMaxIterations method, of class gov.sandia.cognition.learning.AbstractAnytimeBatchLearner.
     */
    public void testSetMaxIterations()
    {
        int maxIterations = 1 + (int) (100.0 * Math.random());
        DummyStandardBatchIterativeLearner instance =
            new DummyStandardBatchIterativeLearner( maxIterations );
        assertEquals( maxIterations, instance.getMaxIterations() );

        maxIterations = 1 + (int) (100.0 * Math.random());
        instance.setMaxIterations( maxIterations );
        assertEquals( maxIterations, instance.getMaxIterations() );
        maxIterations = 1;
        instance.setMaxIterations( maxIterations );
        assertEquals( maxIterations, instance.getMaxIterations() );

        maxIterations = -1;
        try
        {
            instance.setMaxIterations( maxIterations );
            fail( "Maxiterations must be > 0" );
        }
        catch (Exception exception)
        {
            System.out.println( "Good: " + exception );
        }

        maxIterations = 2;
        instance.setMaxIterations( maxIterations );
        assertEquals( maxIterations, instance.getMaxIterations() );
    }

    /**
     * Test of getData method, of class gov.sandia.cognition.learning.AbstractAnytimeBatchLearner.
     */
    public void testGetData()
    {
        int maxIterations = 1 + (int) (100.0 * Math.random());
        final DummyStandardBatchIterativeLearner instance =
            new DummyStandardBatchIterativeLearner( maxIterations );

        final Object data = new Object();
        instance.addIterativeAlgorithmListener( new IterativeAlgorithmListener()
        {

            public void assertSameData(
                IterativeAlgorithm learner )
            {

                assertSame( data, instance.getData() );

            }

            public void algorithmStarted(
                IterativeAlgorithm learner )
            {

                assertSameData( learner );

            }

            public void algorithmEnded(
                IterativeAlgorithm learner )
            {

                assertSameData( learner );

            }

            public void stepStarted(
                IterativeAlgorithm learner )
            {

                assertSameData( learner );

            }

            public void stepEnded(
                IterativeAlgorithm learner )
            {

                assertSameData( learner );

            }

        } );

        instance.learn( data );
        assertNull( instance.getData() );
    }

    /**
     * Test of getIteration method, of class gov.sandia.cognition.learning.AbstractAnytimeBatchLearner.
     */
    public void testGetIteration()
    {
        final int maxIterations = 1 + (int) (100.0 * Math.random());
        final DummyStandardBatchIterativeLearner instance =
            new DummyStandardBatchIterativeLearner( maxIterations );

        final Object data = new Object();
        instance.addIterativeAlgorithmListener( new IterativeAlgorithmListener()
        {

            int count;

            public void algorithmStarted(
                IterativeAlgorithm learner )
            {

                count = 0;

                assertEquals( 0, instance.getIteration() );

            }

            public void algorithmEnded(
                IterativeAlgorithm learner )
            {

                count = -1;

                assertEquals( maxIterations, instance.getIteration() );

            }

            public void stepStarted(
                IterativeAlgorithm learner )
            {

                count++;

                assertEquals( count, instance.getIteration() );

            }

            public void stepEnded(
                IterativeAlgorithm learner )
            {

                assertEquals( count, instance.getIteration() );

            }

        } );
        instance.learn( data );
        assertEquals( maxIterations, instance.getIteration() );
    }

    public static class DummyStandardBatchIterativeLearner
        extends AbstractAnytimeBatchLearner<Object,String>
    {

        public int initializeLearningCount;

        public int stepCount;

        public int cleanupLearningCount;

        public String result;

        public DummyStandardBatchIterativeLearner(
            int maxIterations )
        {
            super( maxIterations );
        }
        
        protected boolean initializeAlgorithm()
        {
            assertEquals( this.cleanupLearningCount, this.initializeLearningCount );
            this.initializeLearningCount++;
            this.result = "Learned";
            return true;
        }
        
        protected boolean step()
        {
            assertEquals( this.cleanupLearningCount + 1,
                this.initializeLearningCount );
            this.stepCount++;
            return true;
        }
        
        protected void cleanupAlgorithm()
        {
            assertEquals( this.cleanupLearningCount + 1,
                this.initializeLearningCount );
            cleanupLearningCount++;
        }
        
        public String getResult()
        {
            return this.result;
        }

    }

}
