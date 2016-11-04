
package gov.sandia.cognition.learning.algorithm.minimization.matrix;

import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.evaluator.Evaluator;
import gov.sandia.cognition.math.matrix.Matrix;
import gov.sandia.cognition.math.matrix.Vector;

/**
 * The necessary multiplication wrapper class to match the FunctionMinimizer
 * interface. Takes a matrix. Multiplies the input vector by the matrix,
 * returning the result.
 * 
 * @author Jeremy D. Wendt
 */
@PublicationReference(author = "Jonathan Richard Shewchuk",
    title = "An Introduction to the Conjugate Gradient Method Without the Agonizing Pain",
    type = PublicationType.WebPage,
    year = 1994,
    url = "http://www.cs.cmu.edu/~quake-papers/painless-conjugate-gradient.pdf‎")
public class MatrixVectorMultiplier
    implements Evaluator<Vector, Vector>
{

    /**
     * The matrix to multiply with. This is a deep-copy of the input to prevent
     * any weirdness from altering the matrix after the constructor is called.
     */
    protected Matrix m;

    /**
     * Never call this.
     *
     * @throws UnsupportedOperationException Because it's not supported.
     */
    private MatrixVectorMultiplier()
    {
        throw new UnsupportedOperationException("Can't call the null "
            + "constructor!");
    }

    /**
     * Clones the input matrix to prevent any later edits to the input from
     * changing the results of iterative multiplications.
     *
     * @param m The matrix to multiply with
     */
    public MatrixVectorMultiplier(Matrix m)
    {
        this.m = m.clone();
    }

    /**
     * Returns m times input.
     *
     * @param input The vector to multiply by m.
     * @return m times input.
     */
    @Override
    public Vector evaluate(Vector input)
    {
        return m.times(input);
    }

    /**
     * Ensures that the input x and rhs are of the correct dimensions to work
     * with m.
     *
     * @param xi The current estimate of the unknown final left-hand-side vector
     * x.
     * @param rhs The right-hand-side vector (b).
     * @return True if the dimensions match, else false.
     */
    boolean canEvaluateAgainst(Vector xi,
        Vector rhs)
    {
        if ((xi.getDimensionality() != m.getNumColumns())
            || (rhs.getDimensionality() != m.getNumRows()))
        {
            return false;
        }
        return true;
    }

    /**
     * @see Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object o)
    {
        if (!(o instanceof MatrixVectorMultiplier))
        {
            return false;
        }
        MatrixVectorMultiplier other = (MatrixVectorMultiplier) o;
        if ((m == null) && (other.m != null))
        {
            return false;
        }
        else if ((m != null) && !m.equals(other.m))
        {
            return false;
        }
        return true;
    }

    /**
     * @see Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        int hash = 1;
        return hash * 17 + ((m == null) ? 0 : m.hashCode());
    }

}
