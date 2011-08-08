/*
 * File:                FunctionMinimizer.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright November 5, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.learning.algorithm.minimization;

import gov.sandia.cognition.algorithm.AnytimeAlgorithm;
import gov.sandia.cognition.evaluator.Evaluator;
import gov.sandia.cognition.learning.algorithm.BatchLearner;
import gov.sandia.cognition.learning.algorithm.regression.ParameterCostMinimizer;
import gov.sandia.cognition.learning.data.InputOutputPair;

/**
 * Interface for unconstrained minimization of nonlinear functions. Implementing 
 * classes find the input that minimizes the output of a function.  To find the
 * parameter set that minimizes a cost function, use {@code ParameterCostMinimizer}.
 * <BR><BR>
 * Broadly speaking, this package is decomposed into multivariate
 * ({@code Vector}) and line (scalar or {@code Double}) minimization.  Furthermore,
 * each of these categories can rely exclusively on function evaluations
 * (derivative-free) or could additionally require first-order derivative
 * (gradient) information.  Generally speaking, derivative-based minimizers
 * require fewer function evaluations and gradient evaluations than their
 * derivative-free counterparts.
 * <BR><BR>
 * Here are my opinions:
 * <BR><BR>
 * <B>Multivariate minimization</B>, averaged function evaluations and gradient
 * evaluations for a random (though repeated) set of initial conditions for
 * minimizing the Rosenbrock function:
 * <BR><BR>
 * There is broad consensus that the BFGS Quasi-Newton algorithm
 * (FunctionMinimizerBFGS, 73.49/59.35) is the most efficient on real-world
 * problems. However, all Quasi-Newton algorithms require the storage of an
 * N-by-N matrix, where N is the size of the input space.  If storing that
 * matrix is impractical, then use Liu-Storey Conjugate Gradient
 * (FunctionMinimizerLiuStorey, 92.91/44.58).  In my experience, this CG 
 * variant performs almost as well as BFGS but doesn't require the N-by-N
 * matrix.
 * <BR><BR>
 * When gradient information isn't available, or gradients are expensive to
 * compute, then I would <B>strongly</B> suggest trying finite-difference
 * approximation to emulate first-order derivatives and then using one of the
 * algorithms mentioned above.  If you are still not satisfied, then we have
 * implemented minimization algorithms that do not require derivative
 * information.  The very clever direction-set minimization method of Smith, 
 * Powell, and Brent (FunctionMinimizerDirectionSet, 448.66/0.0) is my personal
 * method of choice for derivative-free minimization.  Another option is the 
 * brute-force downhill simplex algorithm of Nelder and Mead 
 * (FunctionMinimizerNelderMead, 187.32/0.0), which can be quite zoomy on some
 * problems.  Since they are both inherently heuristic and neither is uniformly
 * better than the other, try them both before settling on a particular
 * algorithm for a particular domain.
 * <BR><BR>
 * <B>Line minimization</B>, reported as minimizing a cosine and a nonlinear
 * polynomial:
 * <BR>
 * We have three line minimizers, Fletcher-type derivative-based, Brent-type
 * derivative-free, and Newton-type backtracking.  I have yet to find an
 * instance when backtracking is beneficial, so I won't discuss it further.
 * An <B>extremely</B> important distinction between line search and
 * multivariate minimization is that derivative information appears to be much
 * less important, so it is not necessarily a given that a derivative-based
 * line minimizer is inherently superior to one that is derivative-free.  With
 * that said, in my experience the Fletcher-type line minimizer has superior
 * performance, particularly because it is vastly superior in the manner by
 * which it brackets a minimum.  However, I would try both the Fletcher-type
 * (LineMinimizerDerivativeBased, 4.300/3.435, 7.982/4.987) and Brent-type
 * (LineMinimizerDerivativeFree, 7.705/0.0, 11.300/0.00)
 * algorithms before settling on one of them for a particular domain.
 * 
 * @see gov.sandia.cognition.learning.algorithm.regression.ParameterCostMinimizer
 *
 * @param <InputType> 
 * Input class of the {@code Evaluator} that we are trying to minimize, 
 * such as {@code Vector}
 * @param <OutputType> 
 * Output class of the {@code Evaluator} that we are trying to minimize,
 * such as {@code Double}
 * @param <EvaluatorType> 
 * {@code Evaluator} class that this minimization algorithm can handle, such as
 * {@code Evaluator} or {@code DifferentiableEvaluator}.
 * @author Kevin R. Dixon
 * @since  2.0
 *
 */
public interface FunctionMinimizer<InputType, OutputType, EvaluatorType extends Evaluator<? super InputType, ? extends OutputType>>
    extends BatchLearner<EvaluatorType, InputOutputPair<InputType, OutputType>>,
    AnytimeAlgorithm<InputOutputPair<InputType, OutputType>>
{

    /**
     * Finds the (local) minimum of the given function
     * @param function 
     * Evaluator to minimize
     * @return 
     * InputOutputPair that has the minimum input and its corresponding output,
     * that is, output=function.evaluate(input)
     */
    public InputOutputPair<InputType, OutputType> learn(
        EvaluatorType function );

    /**
     * Gets the tolerance of the minimization algorithm
     * @return
     * Tolerance of the minimization algorithm
     */
    public double getTolerance();

    /**
     * Sets the tolerance of the minimization algorithm
     * @param tolerance
     * Tolerance of the minimization algorithm
     */
    public void setTolerance(
        double tolerance );
    
    /**
     * Gets the initial guess of the minimization routine
     * @return 
     * Initial guess of the minimization routine
     */
    public InputType getInitialGuess();

    /**
     * Sets the initial guess of the minimization routine
     * @param initialGuess
     * Initial guess of the minimization routine
     */
    public void setInitialGuess(
        InputType initialGuess );

}
