
package gov.sandia.cognition.learning.algorithm.minimization.matrix;

import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.math.matrix.Matrix;
import gov.sandia.cognition.math.matrix.Vector;

/**
 * As various preconditioner operations could be created, this class defines the
 * interface that must be followed by them.
 * 
 * @author Jeremy D. Wendt
 */
@PublicationReference(author = "Jonathan Richard Shewchuk",
    title = "An Introduction to the Conjugate Gradient Method Without the Agonizing Pain",
    type = PublicationType.WebPage,
    year = 1994,
    url = "http://www.cs.cmu.edu/~quake-papers/painless-conjugate-gradient.pdf‎")
abstract public class MatrixVectorMultiplierWithPreconditioner
    extends MatrixVectorMultiplier
{

    /**
     * Never call this.
     *
     * @throws UnsupportedOperationException Because it's not supported.
     */
    private MatrixVectorMultiplierWithPreconditioner()
    {
        super(null);
        throw new UnsupportedOperationException("Can't call the null "
            + "constructor!");
    }

    /**
     * Clones the input matrix to prevent any later edits to the input from
     * changing the results of iterative multiplications.
     *
     * @param m The matrix to multiply with
     */
    MatrixVectorMultiplierWithPreconditioner(Matrix m)
    {
        super(m);
    }

    /**
     * Preconditions the residual vector (applies M^(-1))
     *
     * @param v The vector to precondition
     * @return The vector having been preconditioned
     */
    abstract Vector precondition(Vector v);

}
