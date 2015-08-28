
package gov.sandia.cognition.learning.algorithm.minimization.matrix;

import gov.sandia.cognition.algorithm.IterativeAlgorithm;
import gov.sandia.cognition.algorithm.IterativeAlgorithmListener;
import gov.sandia.cognition.learning.data.InputOutputPair;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.optimized.DenseVector;
import gov.sandia.cognition.math.matrix.optimized.DiagonalMatrix;
import gov.sandia.cognition.math.matrix.optimized.SparseMatrix;
import gov.sandia.cognition.math.matrix.optimized.SparseVector;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * 
 * 
 * @author Jeremy D. Wendt
 */
public class IterativeMatrixSolversTest
{

    @Test
    public void basicCorrectnessTest()
    {
        // First, an easy diagonal matrix w/ all diagonals equal
        DiagonalMatrix mat = new DiagonalMatrix(5);
        mat.setElement(0, 0, 2);
        mat.setElement(1, 1, 2);
        mat.setElement(2, 2, 2);
        mat.setElement(3, 3, 2);
        mat.setElement(4, 4, 2);
        double[] vals =
        {
            1, 2, 3, 4, 5
        };
        DenseVector b = new DenseVector(vals);
        MatrixVectorMultiplier scale = new MatrixVectorMultiplier(mat);
        SteepestDescentMatrixSolver steepestDescent =
            new SteepestDescentMatrixSolver(
            new DenseVector(5), b);
        Vector x = steepestDescent.learn(scale).getOutput();
        assertTrue(x.scale(2).equals(b, 1e-6));
        // NOTE: As all eigenvalues are equal, it only takes one step
        assertEquals(1, steepestDescent.getIteration());
        ConjugateGradientMatrixSolver conjugateGradient =
            new ConjugateGradientMatrixSolver(new DenseVector(5), b);
        x = conjugateGradient.learn(scale).getOutput();
        assertTrue(x.scale(2).equals(b, 1e-6));
        // NOTE: As all eigenvalues are equal, it only takes one step
        assertEquals(1, conjugateGradient.getIteration());
        ConjugateGradientWithPreconditionerMatrixSolver precondCg =
            new ConjugateGradientWithPreconditionerMatrixSolver(
            new SparseVector(5), b);
        MatrixVectorMultiplierDiagonalPreconditioner precondMult =
            new MatrixVectorMultiplierDiagonalPreconditioner(mat);
        x = precondCg.learn(precondMult).getOutput();
        assertTrue(x.scale(2).equals(b, 1e-6));
        assertEquals(1, precondCg.getIteration());
        OverconstrainedConjugateGradientMatrixMinimizer cgMin =
            new OverconstrainedConjugateGradientMatrixMinimizer(
            new SparseVector(5), b);
        OverconstrainedMatrixVectorMultiplier overMult =
            new OverconstrainedMatrixVectorMultiplier(mat);
        x = cgMin.learn(overMult).getOutput();
        assertTrue(x.scale(2).equals(b, 1e-6));
        assertEquals(1, cgMin.getIteration());

        // Now it's still diagonal, but with 3 distinct values (out of 5)
        mat.setElement(0, 0, 1);
        mat.setElement(4, 4, 3);
        MatrixVectorMultiplier diag = new MatrixVectorMultiplier(mat);
        steepestDescent.setInitialGuess(new DenseVector(5));
        x = steepestDescent.learn(diag).getOutput();
        assertTrue(diag.evaluate(x).equals(b, 1e-4));
        conjugateGradient.setInitialGuess(new DenseVector(5));
        x = conjugateGradient.learn(diag).getOutput();
        assertTrue(diag.evaluate(x).equals(b, 1e-4));
        // NOTE: CG should only take the same number of iterations as there are
        // unique eigenvalues (barring numerical errors, which in this case
        // shouldn't happen).
        assertEquals(3, conjugateGradient.getIteration());
        assertTrue(conjugateGradient.getIteration()
            <= steepestDescent.getIteration());
        precondMult = new MatrixVectorMultiplierDiagonalPreconditioner(mat);
        precondCg.setInitialGuess(new SparseVector(5));
        x = precondCg.learn(precondMult).getOutput();
        assertTrue(diag.evaluate(x).equals(b, 1e-4));
        assertEquals(1, precondCg.getIteration());
        assertTrue(precondCg.getIteration() <= conjugateGradient.getIteration());
        overMult = new OverconstrainedMatrixVectorMultiplier(mat);
        x = cgMin.learn(overMult).getOutput();
        assertTrue(diag.evaluate(x).equals(b, 1e-4));
        // I don't care about the number of iterations for this one as much
        // as it should be stiffer than regular CG for square matrices

        // Now for something closer to "real" -- a small, 1D finite difference
        // matrix
        SparseMatrix finiteDiff = new SparseMatrix(5, 5);
        finiteDiff.setElement(0, 0, 2);
        finiteDiff.setElement(0, 1, -1);
        finiteDiff.setElement(1, 0, -1);
        finiteDiff.setElement(1, 1, 2);
        finiteDiff.setElement(1, 2, -1);
        finiteDiff.setElement(2, 1, -1);
        finiteDiff.setElement(2, 2, 2);
        finiteDiff.setElement(2, 3, -1);
        finiteDiff.setElement(3, 2, -1);
        finiteDiff.setElement(3, 3, 2);
        finiteDiff.setElement(3, 4, -1);
        finiteDiff.setElement(4, 3, -1);
        finiteDiff.setElement(4, 4, 2);
        MatrixVectorMultiplier fd = new MatrixVectorMultiplier(finiteDiff);
        SteepestDescentMatrixSolver fdSdSolve = new SteepestDescentMatrixSolver(
            new DenseVector(finiteDiff.getNumRows()), b, 1e-16, (int) 1e6);
        x = fdSdSolve.learn(fd).getOutput();
        assertTrue(finiteDiff.times(x).equals(b, 1e-6));
        System.err.println(fdSdSolve.getIteration());
        ConjugateGradientMatrixSolver fdCgSolve =
            new ConjugateGradientMatrixSolver(new DenseVector(
            finiteDiff.getNumRows()), b, 1e-16, (int) 1e6);
        x = fdCgSolve.learn(fd).getOutput();
        assertTrue(finiteDiff.times(x).equals(b, 1e-6));
        System.err.println(fdCgSolve.getIteration());
        assertTrue(fdCgSolve.getIteration() <= fdSdSolve.getIteration());
        // NOTE: CG should only take the same number of iterations as there are
        // unique eigenvalues (barring numerical errors, which in this case
        // shouldn't happen).
        assertEquals(5, fdCgSolve.getIteration());
        precondMult = new MatrixVectorMultiplierDiagonalPreconditioner(
            finiteDiff);
        precondCg.setInitialGuess(new SparseVector(5));
        x = precondCg.learn(precondMult).getOutput();
        assertTrue(finiteDiff.times(x).equals(b, 1e-4));
        assertTrue(precondCg.getIteration() <= fdCgSolve.getIteration());
        overMult = new OverconstrainedMatrixVectorMultiplier(finiteDiff);
        x = cgMin.learn(overMult).getOutput();
        assertTrue(finiteDiff.times(x).equals(b, 1e-4));
        // I don't care about the number of iterations for this one as much
        // as it should be stiffer than regular CG for square matrices

        // Now, I'll show that cgMin can solve for non-square matrices
        SparseMatrix m = new SparseMatrix(6, 5);
        m.setElement(0, 0, 1);
        m.setElement(1, 1, 1);
        m.setElement(2, 2, 1);
        m.setElement(3, 3, 1);
        m.setElement(4, 4, 1);
        m.setElement(5, 0, 1);
        m.setElement(5, 4, 1);
        double[] vals6 =
        {
            1, 2, 3, 4, 5, 6
        };
        DenseVector rhs = new DenseVector(vals6);
        cgMin = new OverconstrainedConjugateGradientMatrixMinimizer(
            new DenseVector(5), rhs);
        overMult = new OverconstrainedMatrixVectorMultiplier(m);
        x = cgMin.learn(overMult).getOutput();
        assertTrue(m.times(x).equals(rhs, 1e-4));
        // Now make it have no exact solution, just a minimum
        m.setElement(5, 1, .5);
        overMult = new OverconstrainedMatrixVectorMultiplier(m);
        x = cgMin.learn(overMult).getOutput();
        // Make sure that the two columns that are uneffected by the change are
        // still the same
        assertEquals(3, x.getElement(2), 1e-6);
        assertEquals(4, x.getElement(3), 1e-6);
        // The rest should be close, but only really w/in .5-ish
        assertEquals(1, x.getElement(0), .5);
        assertEquals(2, x.getElement(1), .5);
        assertEquals(5, x.getElement(4), .5);
    }

    @Test
    public void testGettersAndSetters()
    {
        // First, an easy diagonal matrix w/ all diagonals equal
        DiagonalMatrix mat = new DiagonalMatrix(5);
        mat.setElement(0, 0, 2);
        mat.setElement(1, 1, 2);
        mat.setElement(2, 2, 2);
        mat.setElement(3, 3, 3);
        mat.setElement(4, 4, 4);
        double[] vals =
        {
            1, 2, 3, 4, 5
        };
        MatrixVectorMultiplier mvm = new MatrixVectorMultiplier(mat);
        DenseVector b = new DenseVector(vals);

        SteepestDescentMatrixSolver steepDesc = new SteepestDescentMatrixSolver(
            new DenseVector(5), b, 1e-10);
        ConjugateGradientMatrixSolver conjGrad =
            new ConjugateGradientMatrixSolver(new DenseVector(5), b, 1e-10);

        // steepDesc.clone();
        // steepDesc.equals(b);
        // Make sure it clones to the same values, but not the same reference
        assertTrue(steepDesc.clone().equals(steepDesc));
        assertFalse(steepDesc.clone() == steepDesc);
        assertTrue(conjGrad.clone().equals(conjGrad));
        assertFalse(conjGrad.clone() == conjGrad);

        // steepDesc.getInitialGuess();
        // steepDesc.setInitialGuess(b);
        assertTrue(steepDesc.getInitialGuess().isZero());
        assertTrue(conjGrad.getInitialGuess().isZero());
        Vector x0;
        x0 = new SparseVector(5);
        x0.setElement(0, 4);
        x0.setElement(4, 3);
        steepDesc.setInitialGuess(x0);
        conjGrad.setInitialGuess(x0);
        assertEquals(x0, steepDesc.getInitialGuess());
        assertEquals(x0, conjGrad.getInitialGuess());
        x0.zero();
        steepDesc.setInitialGuess(x0);
        conjGrad.setInitialGuess(x0);
        assertTrue(steepDesc.getInitialGuess().isZero());
        assertTrue(conjGrad.getInitialGuess().isZero());

        // steepDesc.getTolerance();
        // steepDesc.setTolerance(tolerance);
        assertEquals(1e-10, steepDesc.getTolerance(), 1e-16);
        assertEquals(1e-10, conjGrad.getTolerance(), 1e-16);
        steepDesc.setTolerance(1e-14);
        conjGrad.setTolerance(1e-14);
        assertEquals(1e-14, steepDesc.getTolerance(), 1e-16);
        assertEquals(1e-14, conjGrad.getTolerance(), 1e-16);

        // steepDesc.getMaxIterations();
        // steepDesc.setMaxIterations(maxIterations);
        steepDesc.setMaxIterations((int) 1e6);
        conjGrad.setMaxIterations((int) 1e6);
        assertEquals((int) 1e6, steepDesc.getMaxIterations());
        assertEquals((int) 1e6, conjGrad.getMaxIterations());
        steepDesc.setMaxIterations(2);
        conjGrad.setMaxIterations(2);
        assertEquals(2, steepDesc.getMaxIterations());
        assertEquals(2, conjGrad.getMaxIterations());

        // steepDesc.getIteration();
        // steepDesc.getResult();
        // steepDesc.isResultValid();
        // steepDesc.learn(null);
        InputOutputPair<Vector, Vector> sd1 = steepDesc.learn(mvm);
        InputOutputPair<Vector, Vector> sd2 = steepDesc.getResult();
        InputOutputPair<Vector, Vector> cg1 = conjGrad.learn(mvm);
        InputOutputPair<Vector, Vector> cg2 = conjGrad.getResult();
        assertFalse(steepDesc.isResultValid());
        assertFalse(conjGrad.isResultValid());
        assertTrue(sd1.equals(sd2));
        assertTrue(cg1.equals(cg2));
        assertEquals(2, steepDesc.getIteration());
        assertEquals(2, conjGrad.getIteration());
        mat.setElement(3, 3, 2);
        mat.setElement(4, 4, 2);
        mvm = new MatrixVectorMultiplier(mat);
        sd1 = steepDesc.learn(mvm);
        sd2 = steepDesc.getResult();
        cg1 = conjGrad.learn(mvm);
        cg2 = conjGrad.getResult();
        assertTrue(steepDesc.isResultValid());
        assertTrue(conjGrad.isResultValid());
        assertTrue(sd1.equals(sd2));
        assertTrue(cg1.equals(cg2));
        assertEquals(1, steepDesc.getIteration());
        assertEquals(1, conjGrad.getIteration());

        // steeDesc.hashCode() -- just call it to make sure it doesn't fail
        steepDesc.hashCode();
    }

    @Test
    public void exceptionsThrown()
    {
        // First, an easy diagonal matrix w/ all diagonals equal
        DiagonalMatrix mat = new DiagonalMatrix(5);
        mat.setElement(0, 0, 2);
        mat.setElement(1, 1, 2);
        mat.setElement(2, 2, 2);
        mat.setElement(3, 3, 3);
        mat.setElement(4, 4, 4);
        double[] vals =
        {
            1, 2, 3, 4, 5
        };
        MatrixVectorMultiplier mvm = new MatrixVectorMultiplier(mat);
        DenseVector b = new DenseVector(vals);

        SteepestDescentMatrixSolver steepDesc = new SteepestDescentMatrixSolver(
            new DenseVector(3), b, 1e-10);
        ConjugateGradientMatrixSolver conjGrad =
            new ConjugateGradientMatrixSolver(new DenseVector(5), b.subVector(
            0, 3), 1e-10);

        // steepDesc.clone();
        // No exceptions possible
        // steepDesc.equals(b);
        // No exceptions possible
        // steepDesc.getInitialGuess();
        // No exceptions possible
        // steepDesc.getTolerance();
        // No exceptions possible
        // steepDesc.getMaxIterations();
        // No exceptions possible
        // steepDesc.getIteration();
        // No exceptions possible
        // steepDesc.getResult();
        // No exceptions possible
        // steepDesc.isResultValid();
        // No exceptions possible

        // steepDesc.setInitialGuess(b);
        try
        {
            steepDesc.setInitialGuess(null);
            assertFalse(true);
        }
        catch (NullPointerException e)
        {
            // correct path
        }
        try
        {
            conjGrad.setInitialGuess(null);
            assertFalse(true);
        }
        catch (NullPointerException e)
        {
            // correct path
        }

        // steepDesc.setTolerance(tolerance);
        try
        {
            steepDesc.setTolerance(-1e12);
            assertFalse(true);
        }
        catch (IllegalArgumentException e)
        {
            // correct path
        }
        try
        {
            conjGrad.setTolerance(-1e12);
            assertFalse(true);
        }
        catch (IllegalArgumentException e)
        {
            // correct path
        }
        // This is fine
        steepDesc.setTolerance(0);
        conjGrad.setTolerance(0);

        // steepDesc.setMaxIterations(maxIterations);
        try
        {
            steepDesc.setMaxIterations(-1);
            assertFalse(true);
        }
        catch (IllegalArgumentException e)
        {
            // correct path
        }
        try
        {
            conjGrad.setMaxIterations(-1);
            assertFalse(true);
        }
        catch (IllegalArgumentException e)
        {
            // correct path
        }
        try
        {
            steepDesc.setMaxIterations(0);
            assertFalse(true);
        }
        catch (IllegalArgumentException e)
        {
            // correct path
        }
        try
        {
            conjGrad.setMaxIterations(0);
            assertFalse(true);
        }
        catch (IllegalArgumentException e)
        {
            // correct path
        }

        // steepDesc.learn(null);
        try
        {
            steepDesc.learn(mvm);
            assertFalse(true);
        }
        catch (IllegalArgumentException e)
        {
            // Correct path
        }
        try
        {
            conjGrad.learn(mvm);
            assertFalse(true);
        }
        catch (IllegalArgumentException e)
        {
            // Correct path
        }
        try
        {
            steepDesc.learn(null);
            assertFalse(true);
        }
        catch (NullPointerException e)
        {
            // Correct path
        }
        try
        {
            conjGrad.learn(null);
            assertFalse(true);
        }
        catch (NullPointerException e)
        {
            // Correct path
        }
    }

    @Test
    public void listenersTest()
    {
        DiagonalMatrix mat = new DiagonalMatrix(5);
        mat.setElement(0, 0, 2);
        mat.setElement(1, 1, 3);
        mat.setElement(2, 2, 4);
        mat.setElement(3, 3, 5);
        mat.setElement(4, 4, 6);
        double[] vals =
        {
            1, 2, 3, 4, 5
        };
        DenseVector b = new DenseVector(vals);
        MatrixVectorMultiplier scale = new MatrixVectorMultiplier(mat);

        TestAlgListener l1 = new TestAlgListener();
        assertFalse(l1.started);
        assertFalse(l1.ended);
        assertEquals(0, l1.numStepStarted);
        assertEquals(0, l1.numStepEnded);
        assertEquals(-1, l1.stopAtStep);

        ConjugateGradientMatrixSolver cg = new ConjugateGradientMatrixSolver(
            new SparseVector(5), b);
        cg.addIterativeAlgorithmListener(l1);
        cg.learn(scale);
        assertTrue(l1.started);
        assertTrue(l1.ended);
        assertEquals(5, l1.numStepStarted);
        assertEquals(5, l1.numStepEnded);
        assertEquals(-1, l1.stopAtStep);
        l1.started = false;
        l1.ended = false;
        l1.numStepStarted = 0;
        l1.numStepEnded = 0;
        l1.stopAtStep = 3;
        cg.learn(scale);
        assertTrue(l1.started);
        assertTrue(l1.ended);
        assertEquals(3, l1.numStepStarted);
        assertEquals(3, l1.numStepEnded);
        assertEquals(3, l1.stopAtStep);
        l1.started = false;
        l1.ended = false;
        l1.numStepStarted = 0;
        l1.numStepEnded = 0;
        l1.stopAtStep = 3;
        cg.removeIterativeAlgorithmListener(l1);
        cg.learn(scale);
        assertFalse(l1.started);
        assertFalse(l1.ended);
        assertEquals(0, l1.numStepStarted);
        assertEquals(0, l1.numStepEnded);
        assertEquals(3, l1.stopAtStep);
    }

    private static class TestAlgListener
        implements IterativeAlgorithmListener
    {

        boolean started = false;

        boolean ended = false;

        int numStepStarted = 0;

        int numStepEnded = 0;

        int stopAtStep = -1;

        @Override
        public void algorithmStarted(IterativeAlgorithm algorithm)
        {
            started = true;
        }

        @Override
        public void algorithmEnded(IterativeAlgorithm algorithm)
        {
            ended = true;
        }

        @Override
        public void stepStarted(IterativeAlgorithm algorithm)
        {
            ++numStepStarted;
            if ((stopAtStep != -1) && (stopAtStep == algorithm.getIteration()))
            {
                ((IterativeMatrixSolver) algorithm).stop();
            }
        }

        @Override
        public void stepEnded(IterativeAlgorithm algorithm)
        {
            ++numStepEnded;
        }

    };

}
