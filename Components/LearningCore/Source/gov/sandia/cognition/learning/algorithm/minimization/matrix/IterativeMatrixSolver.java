/*
 * File:                IterativeMatrixSolver.java
 * Authors:             Jeremy D. Wendt
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright 2016, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government. 
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 */

package gov.sandia.cognition.learning.algorithm.minimization.matrix;

import gov.sandia.cognition.algorithm.IterativeAlgorithmListener;
import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.learning.algorithm.minimization.FunctionMinimizer;
import gov.sandia.cognition.learning.data.InputOutputPair;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.util.CloneableSerializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Base class for all iterative matrix solvers that takes care of most of the
 * basic iterative logic and the function minimizer interface.
 * 
 * @author Jeremy D. Wendt
 * @since 4.0.0
 * @param <Operator> The operator for the solver.
 */
@PublicationReference(author = "Jonathan Richard Shewchuk",
    title = "An Introduction to the Conjugate Gradient Method Without the Agonizing Pain",
    type = PublicationType.WebPage,
    year = 1994,
    url = "http://www.cs.cmu.edu/~quake-papers/painless-conjugate-gradient.pdf‎")
abstract public class IterativeMatrixSolver<Operator extends MatrixVectorMultiplier>
    implements FunctionMinimizer<Vector, Vector, Operator>
{

    /**
     * The tolerance of the error accepted before stopping iterations.
     */
    protected double tolerance;

    /**
     * The initial guess for the left-hand-side vector (x).
     */
    protected Vector x0;

    /**
     * The right-hand-side vector (b).
     */
    protected Vector rhs;

    /**
     * Execution will stop after this number of iterations even if it has not
     * converged.
     */
    protected int maxIterations;

    /**
     * Listeners to the algorithms progress have the opportunity to stop the
     * algorithm after a specified number of iterations.
     */
    protected Set<IterativeAlgorithmListener> listeners;

    /**
     * Counts the number of iterations executed thus far.
     */
    protected int iterationCounter;

    /**
     * If set to true, the algorithm will stop after the current iteration
     * completes.
     */
    protected boolean shouldStop;

    /**
     * Stores the input rhs vector and the resulting x vector from the most
     * recent "learn" call.
     */
    private InputOutputPair<Vector, Vector> result;

    /**
     * Unsupported null constructor.
     *
     * @throws UnsupportedOperationException
     */
    private IterativeMatrixSolver()
    {
        throw new UnsupportedOperationException("Do not call this method.");
    }

    /**
     * Initializes a solver with basic necessary values
     *
     * @param x0 The initial guess for x
     * @param rhs The "b" to solve
     */
    protected IterativeMatrixSolver(Vector x0,
        Vector rhs)
    {
        this(x0, rhs, 1e-10, x0.getDimensionality() * 10);
    }

    /**
     * Initializes a solver with a few more values
     *
     * @param x0 The initial guess for x
     * @param rhs The "b" to solve
     * @param tolerance The minimum acceptable error
     */
    protected IterativeMatrixSolver(Vector x0,
        Vector rhs,
        double tolerance)
    {
        this(x0, rhs, tolerance, x0.getDimensionality() * 10);
    }

    /**
     * Inititalizes a solver with all user-definable parameters
     *
     * @param x0 The initial guess for x
     * @param rhs The "b" to solve
     * @param tolerance The minimum acceptable error
     * @param maxIterations The maximum number of iterations
     */
    protected IterativeMatrixSolver(Vector x0,
        Vector rhs,
        double tolerance,
        int maxIterations)
    {
        this.x0 = x0.clone();
        this.rhs = rhs.clone();
        setTolerance(tolerance);
        setMaxIterations(maxIterations);
        listeners = new HashSet<IterativeAlgorithmListener>();
        iterationCounter = -1;
        shouldStop = false;
        result = null;
    }

    /**
     * Protected copy constructor
     *
     * @param copy The "self" to copy
     */
    @SuppressWarnings("unchecked")
    protected IterativeMatrixSolver(IterativeMatrixSolver<Operator> copy)
    {
        this.x0 = copy.x0;
        this.rhs = copy.rhs;
        this.setTolerance(copy.tolerance);
        this.setMaxIterations(copy.maxIterations);
        this.listeners = copy.listeners;
        this.iterationCounter = copy.iterationCounter;
        this.shouldStop = copy.shouldStop;
        this.result = copy.result;
    }

    /**
     * Shell that solves for Ax = b (x0 and rhs passed in on initialization, A
     * is contained in function).
     *
     * @param function Matrix wrapper
     * @return The input b and resulting x found.
     */
    @Override
    final public InputOutputPair<Vector, Vector> learn(
        Operator function)
    {
        if (!function.canEvaluateAgainst(x0, rhs))
        {
            throw new IllegalArgumentException("Input matrix solves for a "
                + "dimensionality than the input x0 and rhs");
        }
        iterationCounter = 0;
        shouldStop = false;
        result = null;
        for (IterativeAlgorithmListener listener : listeners)
        {
            listener.algorithmStarted(this);
        }
        initializeSolver(function);
        while ((!shouldStop) && (iterationCounter < maxIterations))
        {
            ++iterationCounter;
            for (IterativeAlgorithmListener listener : listeners)
            {
                listener.stepStarted(this);
            }
            double residual = iterate();
            for (IterativeAlgorithmListener listener : listeners)
            {
                listener.stepEnded(this);
            }
            if (residual < tolerance)
            {
                break;
            }
        }
        result = completeSolver();
        for (IterativeAlgorithmListener listener : listeners)
        {
            listener.algorithmEnded(this);
        }

        return result;
    }

    /**
     * Called before iterations begin in learn. Iterative solvers can solve for
     * initial state and should store function away.
     *
     * @param function The matrix wrapper to save for iterate.
     */
    abstract protected void initializeSolver(Operator function);

    /**
     * Called during each step of the iterative solver. Take one step forward in
     * the algorithm.
     *
     * @return the residual after this step.
     */
    abstract protected double iterate();

    /**
     * Called after the final iteration. The solver should clean up any
     * intermediate results and return the final results.
     *
     * @return the final results of the algorithm.
     */
    abstract protected InputOutputPair<Vector, Vector> completeSolver();

    /**
     * @see FunctionMinimizer#clone()
     */
    @Override
    abstract public CloneableSerializable clone();

    /**
     * @see FunctionMinimizer#getTolerance()
     */
    @Override
    final public double getTolerance()
    {
        return tolerance;
    }

    /**
     * Sets the minimum tolerance before iterations complete (must be
     * non-negative). If set to zero, you'll likely go all iterations (to
     * maxIterations) in most cases due to numerical precision issues.
     *
     * @param tolerance The minimum tolerance acceptable before returning the
     * result.
     */
    @Override
    final public void setTolerance(double tolerance)
    {
        if (tolerance < 0)
        {
            throw new IllegalArgumentException("Tolerance must be non-negative.");
        }
        this.tolerance = tolerance;
    }

    /**
     * Returns the initial guess at "x"
     *
     * @return the initial guess at "x"
     */
    @Override
    final public Vector getInitialGuess()
    {
        return x0.clone();
    }

    /**
     * Sets the initial guess ("x0")
     *
     * @param initialGuess the initial guess ("x0")
     */
    @Override
    final public void setInitialGuess(Vector initialGuess)
    {
        x0 = initialGuess.clone();
    }

    /**
     * @see FunctionMinimizer#getMaxIterations()
     */
    @Override
    final public int getMaxIterations()
    {
        return maxIterations;
    }

    /**
     * Sets the maximum number of iterations before this will stop iterating. It
     * will stop sooner if the residual is below the minimum residual. The
     * number of iterations must be positive (>0).
     *
     * @param maxIterations The maximum number of iterations
     */
    @Override
    final public void setMaxIterations(int maxIterations)
    {
        if (maxIterations <= 0)
        {
            throw new IllegalArgumentException("Max iterations must be positive");
        }
        this.maxIterations = maxIterations;
    }

    /**
     * @see FunctionMinimizer#getResult()
     */
    @Override
    public InputOutputPair<Vector, Vector> getResult()
    {
        return result;
    }

    /**
     * @see FunctionMinimizer#getIteration()
     */
    @Override
    public int getIteration()
    {
        return iterationCounter;
    }

    /**
     * @see
     * FunctionMinimizer#addIterativeAlgorithmListener(gov.sandia.cognition.algorithm.IterativeAlgorithmListener)
     */
    @Override
    final public void addIterativeAlgorithmListener(
        IterativeAlgorithmListener listener)
    {
        listeners.add(listener);
    }

    /**
     * @see
     * FunctionMinimizer#removeIterativeAlgorithmListener(gov.sandia.cognition.algorithm.IterativeAlgorithmListener)
     */
    @Override
    final public void removeIterativeAlgorithmListener(
        IterativeAlgorithmListener listener)
    {
        listeners.remove(listener);
    }

    /**
     * Execution will stop after the current iteration completes.
     */
    @Override
    public void stop()
    {
        shouldStop = true;
    }

    /**
     * Returns true if execution stopped because the residual was below the
     * acceptable tolerance (vs. due to stop being called or exceeding
     * maxIterations).
     *
     * @return true if execution stopped because the residual was below
     * acceptable tolerance.
     */
    @Override
    final public boolean isResultValid()
    {
        // If it wasn't stopped early, the result is below tolerance
        return (!shouldStop) && (iterationCounter < maxIterations);
    }

    @Override
    public boolean equals(Object o)
    {
        if (!(o instanceof IterativeMatrixSolver))
        {
            return false;
        }
        IterativeMatrixSolver<?> other = (IterativeMatrixSolver) o;
        if (tolerance != other.tolerance)
        {
            return false;
        }
        else if ((x0 == null) && (other.x0 != null))
        {
            return false;
        }
        else if ((x0 != null) && !x0.equals(other.x0))
        {
            return false;
        }
        else if ((rhs == null) && (other.rhs != null))
        {
            return false;
        }
        else if ((rhs != null) && !rhs.equals(other.rhs))
        {
            return false;
        }
        else if (maxIterations != other.maxIterations)
        {
            return false;
        }
        else if ((listeners == null) && (other.listeners != null))
        {
            return false;
        }
        else if ((listeners != null) && !listeners.equals(other.listeners))
        {
            return false;
        }
        else if (iterationCounter != other.iterationCounter)
        {
            return false;
        }
        else if (shouldStop != other.shouldStop)
        {
            return false;
        }
        else if ((result == null) && (other.result != null))
        {
            return false;
        }
        else if ((result != null) && !result.equals(other.result))
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
        hash = hash * 17
            + Long.valueOf(Double.doubleToLongBits(tolerance)).hashCode();
        hash = hash * 17 + ((x0 == null) ? 0 : x0.hashCode());
        hash = hash * 17 + ((rhs == null) ? 0 : rhs.hashCode());
        hash = hash * 17 + Long.valueOf(maxIterations).hashCode();
        hash = hash * 17 + ((listeners == null) ? 0 : listeners.hashCode());
        hash = hash * 17 + Long.valueOf(iterationCounter).hashCode();
        hash = hash * 17 + Boolean.valueOf(shouldStop).hashCode();
        hash = hash * 17 + ((result == null) ? 0 : result.hashCode());

        return hash;
    }

}
