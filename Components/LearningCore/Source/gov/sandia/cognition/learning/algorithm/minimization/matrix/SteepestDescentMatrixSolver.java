
package gov.sandia.cognition.learning.algorithm.minimization.matrix;

import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.learning.data.DefaultInputOutputPair;
import gov.sandia.cognition.learning.data.InputOutputPair;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.util.CloneableSerializable;

/**
 * Implements a basic Steepest Descent iterative solver for linear systems of
 * equations.
 * 
 * @author Jeremy D. Wendt
 */
@PublicationReference(author = "Jonathan Richard Shewchuk",
    title = "An Introduction to the Conjugate Gradient Method Without the Agonizing Pain",
    type = PublicationType.WebPage,
    year = 1994,
    url = "http://www.cs.cmu.edu/~quake-papers/painless-conjugate-gradient.pdf‎")
final public class SteepestDescentMatrixSolver
    extends IterativeMatrixSolver<MatrixVectorMultiplier>
{

    /**
     * The matrix vector multiplier
     */
    private MatrixVectorMultiplier A;

    /**
     * The per-iteration residual
     */
    private Vector residual;

    /**
     * The current best estimate for x
     */
    private Vector x;

    /**
     * A cross-iteration variable
     */
    private double delta;

    /**
     * Not supported null constructor
     *
     * @throws UnsupportedOperationException
     */
    private SteepestDescentMatrixSolver()
    {
        super(null, null);
        throw new UnsupportedOperationException("Do not call this method.");
    }

    /**
     * Initializes a steepest-descent solver with the minimum values
     *
     * @param x0 The initial guess for x
     * @param rhs The "b" to solve to
     */
    public SteepestDescentMatrixSolver(Vector x0,
        Vector rhs)
    {
        super(x0, rhs);
        A = null;
        residual = null;
        x = null;
        delta = 0;
    }

    /**
     * Initializes a steepest-descent solver with some additional parameters
     *
     * @param x0 The initial guess for x
     * @param rhs The "b" to solve to
     * @param tolerance The minimum tolerance that this must reach before
     * stopping (unless maxIterations is exceeded).
     */
    public SteepestDescentMatrixSolver(Vector x0,
        Vector rhs,
        double tolerance)
    {
        super(x0, rhs, tolerance);
        A = null;
        residual = null;
        x = null;
        delta = 0;
    }

    /**
     * Initializes a steepest-descent solver with all user-definable parameters
     *
     * @param x0 The initial guess for x
     * @param rhs The "b" to solve to
     * @param tolerance The minimum tolerance that this must reach before
     * stopping (unless maxIterations is exceeded).
     * @param maxIterations The maximum number of iterations to make
     */
    public SteepestDescentMatrixSolver(Vector x0,
        Vector rhs,
        double tolerance,
        int maxIterations)
    {
        super(x0, rhs, tolerance, maxIterations);
        A = null;
        residual = null;
        x = null;
        delta = 0;
    }

    /**
     * Private copy constructor (for clone). Performs a shallow copy of member
     * variables.
     *
     * @param copy The other to copy into this
     */
    private SteepestDescentMatrixSolver(SteepestDescentMatrixSolver copy)
    {
        super(copy);
        this.A = copy.A;
        this.residual = copy.residual;
        this.x = copy.x;
        this.delta = copy.delta;
    }

    /**
     * @see
     * IterativeMatrixSolver#initializeSolver(gov.sandia.cognition.learning.algorithm.minimization.matrix.MatrixVectorMultiplier)
     */
    @Override
    final protected void initializeSolver(MatrixVectorMultiplier function)
    {
        this.A = function;
        x = super.x0;
        residual = rhs.minus(function.evaluate(x));
        delta = residual.dotProduct(residual);
    }

    /**
     * @see IterativeMatrixSolver#iterate()
     */
    @Override
    final protected double iterate()
    {
        Vector q = A.evaluate(residual);
        double alpha = delta / (residual.dotProduct(q));
        x.plusEquals(residual.scale(alpha));
        if (((iterationCounter + 1) % 50) == 0)
        {
            residual = rhs.minus(A.evaluate(x));
        }
        else
        {
            residual = residual.minus(q.scale(alpha));
        }
        delta = residual.dotProduct(residual);

        return delta;
    }

    /**
     * @see IterativeMatrixSolver#completeSolver()
     */
    @Override
    final protected InputOutputPair<Vector, Vector> completeSolver()
    {
        InputOutputPair<Vector, Vector> result =
            new DefaultInputOutputPair<Vector, Vector>(x0, x);
        A = null;
        residual = null;
        x = null;
        delta = 0;

        return result;
    }

    /**
     * @see IterativeMatrixSolver#clone()
     */
    @Override
    final public CloneableSerializable clone()
    {
        return new SteepestDescentMatrixSolver(this);
    }

    /**
     * @see Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object o)
    {
        if (!(o instanceof SteepestDescentMatrixSolver))
        {
            return false;
        }
        SteepestDescentMatrixSolver other = (SteepestDescentMatrixSolver) o;
        if ((A == null) && (other.A != null))
        {
            return false;
        }
        else if ((A != null) && !A.equals(other.A))
        {
            return false;
        }
        else if ((residual == null) && (other.residual != null))
        {
            return false;
        }
        else if ((residual != null) && !residual.equals(other.residual))
        {
            return false;
        }
        else if ((x == null) && (other.x != null))
        {
            return false;
        }
        else if ((x != null) && !x.equals(other.x))
        {
            return false;
        }
        else if (delta != other.delta)
        {
            return false;
        }

        return super.equals(o);
    }

    /**
     * @see Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        int hash = 1;
        hash = hash * 17 + super.hashCode();
        hash = hash * 17 + ((A == null) ? 0 : A.hashCode());
        hash = hash * 17 + ((residual == null) ? 0 : residual.hashCode());
        hash = hash * 17 + ((x == null) ? 0 : x.hashCode());
        hash = hash * 17
            + Long.valueOf(Double.doubleToLongBits(delta)).hashCode();

        return hash;
    }

}
