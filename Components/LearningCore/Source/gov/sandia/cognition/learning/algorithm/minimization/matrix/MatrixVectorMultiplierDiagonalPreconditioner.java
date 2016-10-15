
package gov.sandia.cognition.learning.algorithm.minimization.matrix;

import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.math.matrix.Matrix;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.custom.DiagonalMatrix;

/**
 * 
 * 
 * @author Jeremy D. Wendt
 */
@PublicationReference(author = "Jonathan Richard Shewchuk",
    title = "An Introduction to the Conjugate Gradient Method Without the Agonizing Pain",
    type = PublicationType.WebPage,
    year = 1994,
    url = "http://www.cs.cmu.edu/~quake-papers/painless-conjugate-gradient.pdf‎")
public class MatrixVectorMultiplierDiagonalPreconditioner
    extends MatrixVectorMultiplierWithPreconditioner
{

    DiagonalMatrix Minv;

    private MatrixVectorMultiplierDiagonalPreconditioner()
    {
        super(null);
        throw new UnsupportedOperationException("Can't call the null "
            + "constructor!");
    }

    public MatrixVectorMultiplierDiagonalPreconditioner(Matrix m)
    {
        super(m);
        if (!m.isSquare())
        {
            throw new IllegalArgumentException("This preconditioner only works "
                + "on square matrices");
        }
        int n = m.getNumRows();
        Minv = new DiagonalMatrix(n);
        for (int i = 0; i < n; ++i)
        {
            double ij = m.getElement(i, i);
            if (ij == 0)
            {
                throw new IllegalArgumentException("Diagonal preconditioner "
                    + "only serves for matrices with non-zero diagonal elements");
            }
            Minv.setElement(i, i, 1.0 / ij);
        }
    }

    @Override
    Vector precondition(Vector v)
    {
        return Minv.times(v);
    }

}
