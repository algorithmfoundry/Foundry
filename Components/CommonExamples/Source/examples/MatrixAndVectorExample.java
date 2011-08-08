/*
 * File:                MatrixAndVectorExample.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright May 6, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package examples;

import gov.sandia.cognition.math.matrix.Matrix;
import gov.sandia.cognition.math.matrix.MatrixFactory;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.math.matrix.decomposition.SingularValueDecomposition;
import gov.sandia.cognition.math.matrix.mtj.SparseMatrix;
import gov.sandia.cognition.math.matrix.mtj.SparseMatrixFactoryMTJ;
import gov.sandia.cognition.math.matrix.mtj.decomposition.SingularValueDecompositionMTJ;
import java.util.Arrays;

/**
 * This is an example of how to use the matrix package, which includes
 * matrix and vector computation, and matrix decompositions.
 * 
 * @author Kevin R. Dixon
 * @since 3.0
 */
public class MatrixAndVectorExample
{

    /**
     * Entry point for the example.
     * @param args
     * We don't use any arguments for this example.
     */
    public static void main(
        String ... args )
    {

        // The creation of all Matrix and Vector instances is done through
        // the "MatrixFactory" and "VectorFactory" subclasses.  We provide
        // default factories through the getDefault() method.
        //
        // We do this because the functionality of Matrix and Vector are
        // defined as interfaces and are independent of an implementation.
        // We do this so that anybody can wrap their favorite
        // matrix-computation package.  In our case, we like the "Matrix
        // Toolkits for Java" (MTJ) package for our computation.  However,
        // if you like using a specific implementation, then feel free
        // to point to your most-loved implemtation-specific Factory.
        //
        // Please note that our Vector interface is called "Vector", which
        // causes a name collision with Java's useless Vector class.
        // Since nobody would willfully use Java's Vector class, we figured
        // that this wouldn't be a huge problem.



        // Both vectors and matrices have fixed dimensions once you create them.
        // Here is a three-dimensional Vector with all-zero entries.
        Vector x0 = VectorFactory.getDefault().createVector(3);
        System.out.println( "x0 = " + x0 );

        // Now, let's set the values of the Vector using the setElement method.
        // We use C-style zero-based indices in our Vector and Matrix packages.
        // Note that Java does not allow operator overload like C++.  For
        // example, it's common for C++ matrix package to use some operator
        // overload so that a matrix-package user can simply type
        //      x0[1] = 2.0
        // This has historically lead to problems that people do not realize
        // that they've invoked the "operator[]" function... which has lead
        // to all sorts of headaches.  Instead, Java requires explicit
        // method calls for this type of thing.
        x0.setElement(0, 1.0);
        x0.setElement(1, 2.0);
        x0.setElement(2, 3.0);
        System.out.println( "x0: " + x0 );

        // If you attempt to set an element beyond the dimensionality of
        // the Vector (which, again, is fixed upon construction), then
        // you will receive a thrown exception.
        try
        {
            // Since we are using zero-based indices, the only valid indices
            // are from [0, 1, ... num-1].
            x0.setElement( x0.getDimensionality(),0.0);
        }
        catch (Exception e)
        {
            System.out.println( "Threw exception: " + e.getClass().getName() );
        }

        // We access elements of the Vector the getElement() method with
        // C-style zero-based indices.
        System.out.println( "x0(0): " + x0.getElement(0) );
        System.out.println( "x0(1): " + x0.getElement(1) );
        System.out.println( "x0(2): " + x0.getElement(2) );

        // Just to reiterate... attempting to access an out-of-bounds index
        // will throw an exception, just like in the setElement method above.
        try
        {
            // Since we use zero-based indices, negative indices are
            // always illegal.
            x0.getElement(-1);
        }
        catch (Exception e)
        {
            System.out.println( "Threw exception: " + e.getClass().getName() );
        }


        // This is an example of a three-dimensional Vector with entries
        // [1.0, 2.0, 3.0 ].
        Vector x1 = VectorFactory.getDefault().copyValues( 1.0, 2.0, 3.0 );
        System.out.println( "x1 = " + x1 );

        // Let's create a 3x3 identity matrix.  This is a matrix that has
        // ones along the diagonal, and zeros elsewhere.  The result of
        // premultiplying an identity matrix by a vector is the same vector
        // (so long as the dimensions of the vector equal the number of
        // columns in the matrix):
        Matrix identity = MatrixFactory.getDefault().createIdentity(3, 3);
        System.out.println( "Identity (3x3):\n" + identity );
        Vector y1 = identity.times( x1 );
        System.out.println( "y1 = I*x1 = " + y1 );

        // Again, an appropriate identify matrix times a vector should
        // equal the vector itself:
        System.out.println( "y1 == x1: " + y1.equals(x1) );

        // Many of the Matrix/Vector operations have inline versions of the
        // same operation.  This help minimize the amount of memory created.
        // This is true partially because both Vector and Matrix inherit from
        // the same interface, gov.sandia.cognition.math.Ring.
        // Let's scale the above equation.
        final double scaleFactor = 10.0;
        Vector y1scaled1 = identity.scale( scaleFactor ).times( x1 );
        System.out.println( "y1scaled1 = (10*I)*x1: " + y1scaled1 );

        // Of course, there are many ways to get the same result!
        Vector y1scaled2 = identity.times( x1 ).scale(scaleFactor);
        System.out.println( "y1scaled1 = I*(x1*10): " + y1scaled2 );

        // These should be equal, because scaling is associative.
        System.out.println( "y1scaled1 == y1scaled2: " + y1scaled1.equals(y1scaled2) );

        // Let's keep a copies of the original values of "identity" because
        // we're about to apply inline operators to it.
        Matrix identityClone = identity.clone();

        // This method modifies the Identity matrix inline and then go through
        // the times() method.  However, this scales 9 values on the
        // scaleEquals() method, so there may be a more efficient way to do it.
        identity.scaleEquals(scaleFactor);
        Vector y1scaled3 = identity.times(x1);
        System.out.println( "y1scaled1 == y1scaled3: " + y1scaled1.equals(y1scaled3) );

        // I believe this will be the most efficient way to get the result,
        // because we're only scaling three values and we're not creating
        // an additional Vector for the non-inlined scale() method.
        x1.scaleEquals(scaleFactor);
        Vector y1scaled4 = identityClone.times( x1 );
        System.out.println( "y1scaled1 == y1scaled4: " + y1scaled1.equals(y1scaled4) );

        // Inappropriately dimensioned matrix computation will throw an
        // exception.  Because the number of columns (2) doesn't equal the
        // dimensionality of x1 (3), the system will throw an exception.
        Matrix Ibad = MatrixFactory.getDefault().createIdentity(4,2);
        try
        {
            // This will barf and throw an exception.
            Ibad.times( x1 );
            // We will never get here.
        }
        catch (Exception e)
        {
            System.out.println( "Caught an exception of type: " + e.getClass().getName() );
        }
        
        // Here's a Matrix with the following columns:
        Vector c1 = VectorFactory.getDefault().copyValues( 1.0, 2.0, 3.0 );
        Vector c2 = VectorFactory.getDefault().copyValues( 0.0, 1.0, 0.0 );
        Vector c3 = c1;
        Vector c4 = c2;
        Matrix matrix = MatrixFactory.getDefault().copyColumnVectors(
            Arrays.asList(c1, c2, c3, c4 ) );
        System.out.println( "matrix =\n" + matrix );

        // The specific decompositions are located in an implementation-
        // specific package.  In this case, we use the MTJ package
        // implementation for dense and sparse matrix computation.
        // So, we'll use the MTJ SVD routine.
        SingularValueDecomposition svd =
            SingularValueDecompositionMTJ.create(matrix);
        System.out.println( "U =\n" + svd.getU() );
        System.out.println( "S =\n" + svd.getS() );
        System.out.println( "Vt =\n" + svd.getVtranspose() );

        // Note that we expect to have an effective rank of "matrix" of 2
        // because 2 of the 4 columns are copies of another column:
        final double tolerance = 1e-10;
        System.out.println( "Effective Rank of A = " + svd.effectiveRank(tolerance) );



        // Some applications require a sparse representation of matrices and
        // vectors.  This is true when the dimensions considered a large,
        // but the number of nonzero entries of the matrix a few.  Gererally
        // speaking sparse matrices and vectors only store the nonzero entries.
        // Let's look at how sparse matrices are stored using the MTJ package.
        SparseMatrix sparse1 = SparseMatrixFactoryMTJ.INSTANCE.createMatrix(10,5);

        // Initially, there will be no nonzero entries.
        System.out.println( "Sparse with no nonzero entries\n" + sparse1 );

        // Set a couple of values to nonzero entries... When we print out
        // the sparse matrix, note that we will see the coordinates of the
        // nonzero entries only.
        sparse1.setElement(1,2, 1.0);
        sparse1.setElement(5,3, 0.2);
        System.out.println( "Sparse with 2 nonzero entries \n" + sparse1 );

        // If we set a previously nonzero element back to zero, it doesn't
        // automatically compress the matrix back.  This must be done manually
        // since it can be pretty time consuming.
        sparse1.setElement(1,2, 0.0 );
        System.out.println( "Sparse with a nonzero entry returned to zero\n" + sparse1 );
        sparse1.compact();
        System.out.println( "Sparse that has been compacted\n" + sparse1 );

    }
    
}
