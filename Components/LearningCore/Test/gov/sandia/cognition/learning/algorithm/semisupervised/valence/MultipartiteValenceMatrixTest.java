
package gov.sandia.cognition.learning.algorithm.semisupervised.valence;

import gov.sandia.cognition.learning.algorithm.minimization.matrix.ConjugateGradientMatrixSolver;
import gov.sandia.cognition.math.matrix.Vector;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Basic tests for the MultipartiteValenceMatrix class.
 *
 * @author jdwendt
 */
public class MultipartiteValenceMatrixTest
{

    /**
     * Creates a simple small graph w/ initial values on two nodes.
     *
     * @param mvm The datastructure to load the graph into
     */
    private static void fillMvm(MultipartiteValenceMatrix mvm)
    {
        mvm.addRelationship(0, 0, 1, 0, 1);
        mvm.addRelationship(0, 0, 1, 1, 1);
        mvm.addRelationship(0, 0, 1, 2, 1);
        mvm.addRelationship(0, 1, 1, 1, 1);
        mvm.addRelationship(0, 1, 1, 4, 1);
        mvm.addRelationship(0, 2, 1, 1, 1);
        mvm.addRelationship(0, 2, 1, 4, 1);
        mvm.addRelationship(0, 3, 1, 3, 1);
        mvm.addRelationship(0, 3, 1, 4, 1);
        mvm.addRelationship(0, 3, 1, 5, 1);

        mvm.setElementsScore(1, 0, 1, 1);
        mvm.setElementsScore(1, 5, 1, -1);
    }

    /**
     * Simple tests that makes sure the spreading works at all.
     */
    @Test
    public void simpleTest()
    {
        List<Integer> sizes = new ArrayList<Integer>(2);
        sizes.add(4);
        sizes.add(6);
        MultipartiteValenceMatrix mvm = new MultipartiteValenceMatrix(sizes, 10);
        fillMvm(mvm);

        Vector rhs = mvm.init();
        ConjugateGradientMatrixSolver s = new ConjugateGradientMatrixSolver(rhs,
            rhs, 1e-1);
        Vector result = s.learn(mvm).getOutput();
        // First make sure the group 0 nodes make sense
        assertTrue(result.getElement(0) > 0.5);
        assertTrue(Math.abs(result.getElement(1)) < 0.1);
        assertTrue(Math.abs(result.getElement(2)) < 0.1);
        assertTrue(result.getElement(3) < -0.5);
        // Now make sure the group 1 nodes make sense
        assertTrue(result.getElement(4) > 0.5);
        assertTrue(result.getElement(5) > 0.0);
        assertTrue(result.getElement(6) > 0.5);
        assertTrue(result.getElement(7) < -0.5);
        assertTrue(result.getElement(8) < -0.0);
        assertTrue(result.getElement(9) < -0.5);
    }

    /**
     * Tests that as you increase the power, the spread of the scores increases
     */
    @Test
    public void spreadTest()
    {
        List<Integer> sizes = new ArrayList<Integer>(2);
        sizes.add(4);
        sizes.add(6);
        MultipartiteValenceMatrix mvm = new MultipartiteValenceMatrix(sizes, 0);
        fillMvm(mvm);

        Vector rhs = mvm.init();
        ConjugateGradientMatrixSolver s = new ConjugateGradientMatrixSolver(
            rhs, rhs, 1e-1);
        Vector result = s.learn(mvm).getOutput();
        // In the zero-spread case, only the seeded-nodes should have value
        // First make sure the group 0 nodes make sense
        assertTrue(Math.abs(result.getElement(0)) < 0.01);
        assertTrue(Math.abs(result.getElement(1)) < 0.01);
        assertTrue(Math.abs(result.getElement(2)) < 0.01);
        assertTrue(Math.abs(result.getElement(3)) < 0.01);
        // Now make sure the group 1 nodes make sense
        assertTrue(result.getElement(4) >= 0.5);
        assertTrue(Math.abs(result.getElement(5)) < 0.01);
        assertTrue(Math.abs(result.getElement(6)) < 0.01);
        assertTrue(Math.abs(result.getElement(7)) < 0.01);
        assertTrue(Math.abs(result.getElement(8)) < 0.01);
        assertTrue(result.getElement(9) <= -0.5);

        mvm = new MultipartiteValenceMatrix(sizes, 1);
        fillMvm(mvm);
        rhs = mvm.init();
        s = new ConjugateGradientMatrixSolver(rhs, rhs, 1e-1);
        result = s.learn(mvm).getOutput();
        // In the one-spread case, only the nodes connected to the seeded-nodes
        // should have value
        // First make sure the group 0 nodes make sense
        assertTrue(result.getElement(0) >= 0.2);
        assertTrue(Math.abs(result.getElement(1)) < 0.01);
        assertTrue(Math.abs(result.getElement(2)) < 0.01);
        assertTrue(result.getElement(3) <= -0.2);
        // Now make sure the group 1 nodes make sense
        assertTrue(result.getElement(4) >= 0.5);
        assertTrue(Math.abs(result.getElement(5)) < 0.01);
        assertTrue(Math.abs(result.getElement(6)) < 0.01);
        assertTrue(Math.abs(result.getElement(7)) < 0.01);
        assertTrue(Math.abs(result.getElement(8)) < 0.01);
        assertTrue(result.getElement(9) <= -0.5);

        mvm = new MultipartiteValenceMatrix(sizes, 2);
        fillMvm(mvm);
        rhs = mvm.init();
        s = new ConjugateGradientMatrixSolver(rhs, rhs, 1e-1);
        result = s.learn(mvm).getOutput();
        // In the two-spread case, we're in the ballpark of final solution with
        // this graph
        // First make sure the group 0 nodes make sense
        assertTrue(result.getElement(0) > 0.5);
        assertTrue(Math.abs(result.getElement(1)) < 0.1);
        assertTrue(Math.abs(result.getElement(2)) < 0.1);
        assertTrue(result.getElement(3) < -0.5);
        // Now make sure the group 1 nodes make sense
        assertTrue(result.getElement(4) > 0.5);
        assertTrue(result.getElement(5) > 0.0);
        assertTrue(result.getElement(6) > 0.5);
        assertTrue(result.getElement(7) < -0.5);
        assertTrue(result.getElement(8) < -0.0);
        assertTrue(result.getElement(9) < -0.5);
    }

}
