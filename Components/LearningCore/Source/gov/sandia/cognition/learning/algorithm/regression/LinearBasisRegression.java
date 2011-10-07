/*
 * File:                LinearRegression.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright September 5, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.learning.algorithm.regression;

import gov.sandia.cognition.annotation.CodeReview;
import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.learning.data.InputOutputPair;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.evaluator.Evaluator;
import gov.sandia.cognition.learning.algorithm.SupervisedBatchLearner;
import gov.sandia.cognition.learning.data.DatasetUtil;
import gov.sandia.cognition.learning.data.DefaultWeightedInputOutputPair;
import gov.sandia.cognition.learning.data.WeightedInputOutputPair;
import gov.sandia.cognition.learning.function.scalar.LinearDiscriminant;
import gov.sandia.cognition.learning.function.scalar.VectorFunctionLinearDiscriminant;
import gov.sandia.cognition.learning.function.vector.ScalarBasisSet;
import gov.sandia.cognition.util.AbstractCloneableSerializable;
import gov.sandia.cognition.util.ObjectUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/**
 * Computes the least-squares regression for a LinearCombinationFunction
 * given a dataset.  A LinearCombinationFunction is a weighted linear
 * combination of (potentially) nonlinear basis functions.  This looks like
 * y(x) = a0*f0(x) + a1*f1(x) + ... + an*fn(x) and so forth.
 * The internal class LinearRegression.Statistic returns the goodness-of-fit
 * statistics for a set of target-estimate pairs, include a p-value for the
 * null hypothesis significance.
 *
 * @param   <InputType> Input class for the basis functions, for example, Double,
 *          Vector, String.
 * @author  Kevin R. Dixon
 * @since   2.0
 */
@CodeReview(
    reviewer="Kevin R. Dixon",
    date="2008-09-02",
    changesNeeded=false,
    comments={
        "Made minor changes to javadoc",
        "Looks fine."
    }
)
@PublicationReference(
    author="Wikipedia",
    title="Linear regression",
    type=PublicationType.WebPage,
    year=2008,
    url="http://en.wikipedia.org/wiki/Linear_regression"
)
public class LinearBasisRegression<InputType>
    extends AbstractCloneableSerializable
    implements SupervisedBatchLearner<InputType, Double, VectorFunctionLinearDiscriminant<InputType>>
{

    /**
     * Tolerance for the pseudo inverse in the learn method, {@value}.
     */
    public static final double DEFAULT_PSEUDO_INVERSE_TOLERANCE = 1e-10;

    /**
     * Function that maps the InputType to a Vector
     */
    private Evaluator<? super InputType, Vector> inputToVectorMap;
    
    /**
     * Flag to use a pseudoinverse.  True to use the expensive, but more
     * accurate, pseudoinverse routine.  False uses a very fast, but
     * numerically less stable LU solver.  Default value is "true".
     */
    private boolean usePseudoInverse;

    /**
     * Creates a new instance of LinearRegression
     * @param basisFunctions 
     * Basis functions to create the ScalarBasisSet from
     */
    public LinearBasisRegression(
        Evaluator<? super InputType, Double>... basisFunctions )
    {
        this( Arrays.asList( basisFunctions ) );
    }

    /**
     * Creates a new instance of LinearRegression
     * @param basisFunctions 
     * Basis functions to create the ScalarBasisSet from
     */
    public LinearBasisRegression(
        Collection<? extends Evaluator<? super InputType, Double>> basisFunctions )
    {
        this( new ScalarBasisSet<InputType>( basisFunctions ) );
    }

    /**
     * Creates a new instance of LinearRegression
     * @param inputToVectorMap 
     * Function that maps the InputType to a Vector
     */
    public LinearBasisRegression(
        ScalarBasisSet<InputType> inputToVectorMap )
    {
        this( (Evaluator<? super InputType, Vector>) inputToVectorMap );
    }

    /**
     * Creates a new instance of LinearRegression
     * @param inputToVectorMap
     * Function that maps the InputType to a Vector
     */
    public LinearBasisRegression(
        Evaluator<? super InputType, Vector> inputToVectorMap )
    {
        this.setInputToVectorMap( inputToVectorMap );
        this.setUsePseudoInverse( true );
    }

    @Override
    public LinearBasisRegression<InputType> clone()
    {
        @SuppressWarnings("unchecked")
        LinearBasisRegression<InputType> clone =
            (LinearBasisRegression<InputType>) super.clone();
        clone.setInputToVectorMap(
            ObjectUtil.cloneSmart( this.getInputToVectorMap() ) );
        return clone;
    }

    /**
     * Computes the linear regression for the given Collection of
     * InputOutputPairs.  The inputs of the pairs is the independent variable,
     * and the pair output is the dependent variable (variable to predict).
     * The pairs can have an associated weight to bias the regression equation.
     * @param data 
     * Collection of InputOutputPairs for the variables.  Can be 
     * WeightedInputOutputPairs.
     * @return 
     * LinearCombinationFunction that minimizes the RMS error of the outputs.
     */
    @Override
    public VectorFunctionLinearDiscriminant<InputType> learn(
        Collection<? extends InputOutputPair<? extends InputType, Double>> data )
    {
        // Create the vector-based dataset first
        ArrayList<WeightedInputOutputPair<Vector,Double>> vectorData =
            new ArrayList<WeightedInputOutputPair<Vector, Double>>( data.size() );
        for (InputOutputPair<? extends InputType, Double> pair : data)
        {
            double weight = DatasetUtil.getWeight(pair);
            Vector xrow = this.inputToVectorMap.evaluate( pair.getInput() );
            Double output = pair.getOutput();
            vectorData.add( DefaultWeightedInputOutputPair.create( xrow, output, weight ) );
        }
        
        LinearRegression linear = new LinearRegression();
        linear.setUsePseudoInverse(this.getUsePseudoInverse());
        LinearDiscriminant weights = linear.learn(vectorData);
        return new VectorFunctionLinearDiscriminant<InputType>(
            this.inputToVectorMap, weights );
    }

    /**
     * Getter for inputToVectorMap
     * @return
     * Function that maps the InputType to a Vector
     */
    public Evaluator<? super InputType, Vector> getInputToVectorMap()
    {
        return this.inputToVectorMap;
    }

    /**
     * Setter for inputToVectorMap
     * @param inputToVectorMap
     * Function that maps the InputType to a Vector
     */
    public void setInputToVectorMap(
        Evaluator<? super InputType, Vector> inputToVectorMap )
    {
        this.inputToVectorMap = inputToVectorMap;
    }

    /**
     * Getter for usePseudoInverse
     * @return
     * Flag to use a pseudoinverse.  True to use the expensive, but more
     * accurate, pseudoinverse routine.  False uses a very fast, but
     * numerically less stable LU solver.  Default value is "true".
     */
    public boolean getUsePseudoInverse()
    {
        return this.usePseudoInverse;
    }

    /**
     * Setter for usePseudoInverse
     * @param usePseudoInverse
     * Flag to use a pseudoinverse.  True to use the expensive, but more
     * accurate, pseudoinverse routine.  False uses a very fast, but
     * numerically less stable LU solver.  Default value is "true".
     */
    public void setUsePseudoInverse(
        boolean usePseudoInverse )
    {
        this.usePseudoInverse = usePseudoInverse;
    }

}
