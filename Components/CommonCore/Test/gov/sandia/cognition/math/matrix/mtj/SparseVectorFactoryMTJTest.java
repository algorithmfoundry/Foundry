/*
 * File:                SparseVectorFactoryMTJTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright May 22, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */
package gov.sandia.cognition.math.matrix.mtj;

import gov.sandia.cognition.math.matrix.SparseVectorFactoryTest;

/**
 * Unit tests for SparseVectorFactoryMTJTest
 *
 * @author Kevin R. Dixon
 * @since  1.0
 */
public class SparseVectorFactoryMTJTest
    extends SparseVectorFactoryTest
{
    
    public SparseVectorFactoryMTJTest(
        String testName)
    {
        super(testName);
    }

    @Override
    public SparseVectorFactoryMTJ createFactory()
    {
        return SparseVectorFactoryMTJ.INSTANCE;
    }
    
}
