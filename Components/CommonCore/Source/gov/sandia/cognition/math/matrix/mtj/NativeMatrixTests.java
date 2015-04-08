/*
 * File:                NativeLibraryTests.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Framework Lite
 *
 * Copyright December 1, 2006, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 *
 */

package gov.sandia.cognition.math.matrix.mtj;

import com.github.fommil.netlib.BLAS;
import com.github.fommil.netlib.LAPACK;

/**
 * Tests to see if native versions of LAPACK and BLAS are loaded.
 *
 * @author Kevin R. Dixon
 * @since  1.0
 */
public class NativeMatrixTests
{
    
    /**
     * Tests if Native LAPACK is loaded
     * @return
     * true if native LAPACK loaded, false if Java LAPACK is loaded
     */
    public static boolean isNativeLAPACK()
    {
        return LAPACK.getInstance().getClass().getName().contains("Native");
    }
    
    /**
     * Tests if Native BLAS is loaded
     * @return 
     * true if native BLAS loaded, false if Java BLAS is loaded
     */
    public static boolean isNativeBLAS()
    {
        return BLAS.getInstance().getClass().getName().contains("Native");
    }

}
