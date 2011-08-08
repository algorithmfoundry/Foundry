/*
 * File:                DenseVectorFactoryMTJTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Framework Lite
 *
 * Copyright May 18, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 *
 */
package gov.sandia.cognition.math.matrix.mtj;

import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.math.matrix.VectorFactoryTestHarness;
import junit.framework.*;

/**
 * Unit tests for DenseVectorFactoryMTJTest
 *
 * @author Kevin R. Dixon
 * @since  1.0
 */
public class DenseVectorFactoryMTJTest extends VectorFactoryTestHarness
{
    
    public DenseVectorFactoryMTJTest(String testName)
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
        TestSuite suite = new TestSuite(DenseVectorFactoryMTJTest.class);
        
        return suite;
    }

    public VectorFactory createFactory()
    {
        return new DenseVectorFactoryMTJ();
    }

    
}
