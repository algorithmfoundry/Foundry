/*
 * File:                AnytimeAlgorithmWrapperTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Mar 3, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.algorithm;

import gov.sandia.cognition.io.serialization.XStreamSerializationHandler;
import gov.sandia.cognition.math.LentzMethod;
import java.util.Random;
import junit.framework.TestCase;

/**
 * Unit tests for AnytimeAlgorithmWrapper
 *
 * @author krdixon
 */
public class AnytimeAlgorithmWrapperTest
extends TestCase
{
    
    /**
     * Test
     * @param testName name of test.
     */
    public AnytimeAlgorithmWrapperTest(
        String testName)
    {
        super(testName);
    }

    /**
     * random
     */
    public static Random random = new Random( 1111 );

    /**
     * wrapper
     */
    public static class AAWrapper
        extends AnytimeAlgorithmWrapper<Double,LentzMethod>
        implements Runnable
    {

        /**
         * algorithm start
         */
        public boolean algorithmStartedFlag = false;

        /**
         * algorithm end
         */
        public boolean algorithmEndedFlag = false;

        /**
         * step start
         */
        public boolean stepStartFlag = false;

        /**
         * step end
         */
        public boolean stepEndedFlag = false;

        /**
         * Default
         */
        public AAWrapper()
        {
            super( new LentzMethod() );
        }

        /**
         * Constructor
         * @param d d
         */
        public AAWrapper(
            boolean d )
        {
            super();
        }

        /**
         * Defaykt
         * @return
         */
        public static AAWrapper defaultConstructor()
        {
            return new AAWrapper(false);
        }

        public Double getResult()
        {
            return this.getAlgorithm().getResult();
        }

        public void run()
        {
            double a = random.nextDouble() + 1.0;
            double b = random.nextDouble();
            this.getAlgorithm().initializeAlgorithm(b);
            while( this.getAlgorithm().getKeepGoing() )
            {
                this.getAlgorithm().iterate(a,b);
            }
        }

        @Override
        public void algorithmStarted(
            IterativeAlgorithm algorithm)
        {
            super.algorithmStarted(algorithm);
            this.algorithmStartedFlag = true;
        }

        @Override
        public void algorithmEnded(IterativeAlgorithm algorithm)
        {
            super.algorithmEnded(algorithm);
            this.algorithmEndedFlag = true;
        }

        @Override
        public void stepEnded(IterativeAlgorithm algorithm)
        {
            super.stepEnded(algorithm);
            this.stepEndedFlag = true;
        }

        @Override
        public void stepStarted(IterativeAlgorithm algorithm)
        {
            super.stepStarted(algorithm);
            this.stepStartFlag = true;
        }

    }

    /**
     * Creates instance
     * @return instance
     */
    public AAWrapper createInstance()
    {
        return new AAWrapper();
    }

    /**
     * Tests constructors
     */
    public void testConstructors()
    {
        System.out.println( "Constructors" );
        AAWrapper instance = AAWrapper.defaultConstructor();
        assertNull( instance.getAlgorithm() );

        instance = new AAWrapper();
        assertNotNull( instance.getAlgorithm() );
    }

    /**
     * Tests clone
     */
    public void testClone()
    {
        System.out.println( "Clone" );

        AAWrapper instance = this.createInstance();
        AAWrapper clone = (AAWrapper) instance.clone();
        assertNotNull( clone );
        assertNotSame( instance, clone );
        assertNotNull( clone.getAlgorithm() );
        assertNotSame( instance.getAlgorithm(), clone.getAlgorithm() );
    }

    /**
     * Test of getMaxIterations method, of class AnytimeAlgorithmWrapper.
     */
    public void testGetMaxIterations()
    {
        System.out.println("getMaxIterations");
        AnytimeAlgorithmWrapper<?,?> instance = this.createInstance();
        assertTrue( instance.getMaxIterations() > 0 );
        assertEquals( instance.getAlgorithm().getMaxIterations(), instance.getMaxIterations() );
    }

    /**
     * Test of setMaxIterations method, of class AnytimeAlgorithmWrapper.
     */
    public void testSetMaxIterations()
    {
        System.out.println("setMaxIterations");
        AnytimeAlgorithmWrapper<?,?> instance = this.createInstance();
        int i = instance.getMaxIterations();

        int i2 = i += random.nextInt(100) + 1;
        instance.setMaxIterations(i2);
        assertEquals( i2, instance.getMaxIterations() );

        try
        {
            instance.setMaxIterations( 0 );
            fail( "Max iterations must be > 0" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }
    }

    /**
     * Test of getAlgorithm method, of class AnytimeAlgorithmWrapper.
     */
    public void testGetAlgorithm()
    {
        System.out.println("getAlgorithm");
        AnytimeAlgorithmWrapper<?,?> instance = this.createInstance();
        assertNotNull( instance.getAlgorithm() );
    }

    /**
     * Test of setAlgorithm method, of class AnytimeAlgorithmWrapper.
     */
    @SuppressWarnings("unchecked")
    public void testSetAlgorithm()
    {
        System.out.println("setAlgorithm");
        AnytimeAlgorithmWrapper<Double,LentzMethod> instance = this.createInstance();
        LentzMethod algorithm = instance.getAlgorithm();
        assertNotNull( algorithm );
        instance.setAlgorithm(null);
        assertNull( instance.getAlgorithm() );

        instance.setAlgorithm(algorithm);
        assertSame( algorithm, instance.getAlgorithm() );
    }

    /**
     * Test of stop method, of class AnytimeAlgorithmWrapper.
     */
    @SuppressWarnings("unchecked")
    public void testStop()
    {
        System.out.println("stop");
        AAWrapper instance = this.createInstance();

        instance.getAlgorithm().initializeAlgorithm(random.nextDouble());
        instance.getAlgorithm().iterate( random.nextDouble(), random.nextDouble() );
        instance.stop();

        try
        {
            instance.getAlgorithm().iterate( random.nextDouble(), random.nextDouble() );
            fail( "Cannot iterate after stopping!" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }

        instance.setAlgorithm(null);
        instance.stop();
    }

    /**
     * Test of getIteration method, of class AnytimeAlgorithmWrapper.
     */
    public void testGetIteration()
    {
        System.out.println("getIteration");
        AAWrapper instance = this.createInstance();
        assertEquals( 0, instance.getIteration() );

        instance.getAlgorithm().initializeAlgorithm(random.nextDouble());
        instance.getAlgorithm().iterate( random.nextDouble(), random.nextDouble() );

        assertEquals( 1, instance.getIteration() );

    }

    /**
     * Test of isResultValid method, of class AnytimeAlgorithmWrapper.
     */
    public void testIsResultValid()
    {
        System.out.println("isResultValid");
        AnytimeAlgorithmWrapper<?,?> instance = this.createInstance();
        assertFalse( instance.isResultValid() );
        assertNull( instance.getResult() );
    }

    /**
     * Algorithm Stuff
     */
    public void testAlgorithmStuff()
    {
        System.out.println( "Algorithm Stuff" );

        AAWrapper instance = this.createInstance();
        assertFalse( instance.isResultValid() );
        instance.run();

        // assertTrue( instance.isResultValid() );
        assertTrue( instance.algorithmStartedFlag );
        assertTrue( instance.algorithmEndedFlag );
        assertTrue( instance.stepStartFlag );
        assertTrue( instance.stepEndedFlag );

    }

    /**
     * Tests the serialization code regarding readResolve.
     *
     * @throws java.lang.Exception
     */
    public void testReadResolve()
        throws Exception
    {
        AAWrapper instance = this.createInstance();
        assertTrue(instance.getAlgorithm().getListeners().contains(instance));

        XStreamSerializationHandler handler = XStreamSerializationHandler.getDefault();
        String serialized = handler.convertToString(instance);
        System.out.println(serialized);
        AAWrapper deserialized = (AAWrapper) handler.convertFromString(
            serialized);

        // The main part of read-resolve is to make sure that the wrapper is
        // connected to the algorithm after deserialization.
        assertTrue(deserialized.getAlgorithm().getListeners().contains(
            deserialized));

        deserialized.run();

        assertTrue(deserialized.algorithmStartedFlag);
        assertTrue(deserialized.algorithmEndedFlag);
        assertTrue(deserialized.stepStartFlag);
        assertTrue(deserialized.stepEndedFlag);
    }

}
