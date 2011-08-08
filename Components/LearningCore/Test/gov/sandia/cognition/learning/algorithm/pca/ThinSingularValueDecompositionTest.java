/*
 * File:                ThinSingularValueDecompositionTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright October 9, 2007, Sandia Corporation.  Under the terms of Contract
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
public class ThinSingularValueDecompositionTest
    extends PrincipalComponentsAnalysisTestHarness
{

    public ThinSingularValueDecompositionTest(
        String testName )
    {
        super( testName );
    }

    public ThinSingularValueDecomposition createPCAInstance()
    {
        return new ThinSingularValueDecomposition( OUTPUT_DIM );
    }

    /**
     * Test of clone method, of class gov.sandia.cognition.learning.pca.ThinSingularValueDecomposition.
     */
    public void testClone()
    {
        System.out.println( "clone" );

        ThinSingularValueDecomposition instance = this.createPCAInstance();
        ThinSingularValueDecomposition clone =
            (ThinSingularValueDecomposition) instance.clone();
        assertNotNull( clone );
        assertNotSame( instance, clone );
        assertEquals( instance.getNumComponents(), clone.getNumComponents() );

    }

}
