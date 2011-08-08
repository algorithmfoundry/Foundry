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
import gov.sandia.cognition.math.UnivariateStatisticsUtil;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.math.matrix.Matrix;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.evaluator.Evaluator;
import gov.sandia.cognition.learning.algorithm.SupervisedBatchLearner;
import gov.sandia.cognition.learning.data.DatasetUtil;
import gov.sandia.cognition.learning.function.scalar.VectorFunctionLinearDiscriminant;
import gov.sandia.cognition.learning.function.vector.ScalarBasisSet;
import gov.sandia.cognition.math.matrix.MatrixFactory;
import gov.sandia.cognition.statistics.method.AbstractConfidenceStatistic;
import gov.sandia.cognition.statistics.distribution.ChiSquareDistribution;
import gov.sandia.cognition.util.AbstractCloneableSerializable;
import gov.sandia.cognition.util.ObjectUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

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
public class LinearRegression<InputType>
    extends AbstractCloneableSerializable
    implements SupervisedBatchLearner<InputType, Double, VectorFunctionLinearDiscriminant<InputType>>
{

    /**
     * Tolerance for the pseudo inverse in the learn method, {@value}.
     */
    public static final double DEFAULT_PSEUDO_INVERSE_TOLERANCE = 1e-10;

    /**
     * Learned object
     */
    private VectorFunctionLinearDiscriminant<InputType> learned;

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
    public LinearRegression(
        Evaluator<? super InputType, Double>... basisFunctions )
    {
        this( Arrays.asList( basisFunctions ) );
    }

    /**
     * Creates a new instance of LinearRegression
     * @param basisFunctions 
     * Basis functions to create the ScalarBasisSet from
     */
    public LinearRegression(
        Collection<? extends Evaluator<? super InputType, Double>> basisFunctions )
    {
        this( new ScalarBasisSet<InputType>( basisFunctions ) );
    }

    /**
     * Creates a new instance of LinearRegression
     * @param inputToVectorMap 
     * Function that maps the InputType to a Vector
     */
    public LinearRegression(
        ScalarBasisSet<InputType> inputToVectorMap )
    {
        this( (Evaluator<? super InputType, Vector>) inputToVectorMap );
    }

    /**
     * Creates a new instance of LinearRegression
     * @param inputToVectorMap
     * Function that maps the InputType to a Vector
     */
    public LinearRegression(
        Evaluator<? super InputType, Vector> inputToVectorMap )
    {
        this.setInputToVectorMap( inputToVectorMap );
        this.setUsePseudoInverse( true );
        this.setLearned( null );
    }

    @Override
    public LinearRegression<InputType> clone()
    {
        @SuppressWarnings("unchecked")
        LinearRegression<InputType> clone =
            (LinearRegression<InputType>) super.clone();
        clone.setInputToVectorMap(
            ObjectUtil.cloneSmart( this.getInputToVectorMap() ) );
        clone.setLearned( ObjectUtil.cloneSafe( this.getLearned() ) );
        return clone;
    }

    /**
     * Getter for learned
     * @return 
     * Weighted linear combination of (potentially) nonlinear basis functions
     */
    public VectorFunctionLinearDiscriminant<InputType> getLearned()
    {
        return this.learned;
    }

    /**
     * Setter for learned
     * @param learned 
     * Weighted linear combination of (potentially) nonlinear basis functions
     */
    protected void setLearned(
        VectorFunctionLinearDiscriminant<InputType> learned )
    {
        this.learned = learned;
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
    public VectorFunctionLinearDiscriminant<InputType> learn(
        Collection<? extends InputOutputPair<? extends InputType, Double>> data )
    {

        this.setLearned( null );

        // We need to cheat to figure out how many coefficients we need...
        // So we'll push the first sample through... wasteful, but general
        InputOutputPair<? extends InputType, Double> first = 
            data.iterator().next();
        Vector firstOutput = this.inputToVectorMap.evaluate( first.getInput() );
        int numCoefficients = firstOutput.getDimensionality();
        int numSamples = data.size();

        Matrix X = MatrixFactory.getDefault().createMatrix( numSamples, numCoefficients );
        Vector y = VectorFactory.getDefault().createVector( numSamples );

        // The matrix equation looks like:
        // y = C*[f0(x) f1(x) ... fn(x) ], fi() is the ith basis function
        int i = 0;
        for (InputOutputPair<? extends InputType, Double> pair : data)
        {
            double weight = DatasetUtil.getWeight(pair);

            y.setElement( i, pair.getOutput() * weight );

            InputType input = pair.getInput();
            Vector xrow = this.inputToVectorMap.evaluate( input );
            X.setRow( i, xrow.scale(weight) );
            i++;
        }

        // Solve for the coefficients
        Vector coefficients;
        if( this.getUsePseudoInverse() )
        {
            Matrix psuedoInverse = X.pseudoInverse(DEFAULT_PSEUDO_INVERSE_TOLERANCE);
            coefficients = psuedoInverse.times(y);
        }
        else
        {
            coefficients = X.solve( y );
        }
        this.setLearned( new VectorFunctionLinearDiscriminant<InputType>(
            this.inputToVectorMap, coefficients ) );

        return this.getLearned();

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

    /**
     * Computes regression statistics using a chi-square measure of the
     * statistical significance of the learned approximator
     */
    public static class Statistic
        extends AbstractConfidenceStatistic
    {

        /**
         * Gets the value of the chi-square variable,
         * Total weighted sum-squared error between the targets and estimates
         */
        private double chiSquare;

        /**
         * Root mean-squared error of the targets and estimates
         */
        private double rootMeanSquaredError;

        /**
         * Average L1-norm error (absolute value difference) between the 
         * targets and estimates
         */
        private double meanL1Error;

        /**
         * Pearson Correlation between the targets and estimates, [-1,1]
         */
        private double targetEstimateCorrelation;

        /**
         * Fraction of variance unaccounted for in the predictions, [0,1]
         */
        private double unpredictedErrorFraction;

        /**
         * Number of samples used to create the Regression
         */
        private int numSamples;

        /**
         * Number of parameters in the learned approximator
         */
        private int numParameters;

        /**
         * Degrees of freedom in the Regression = numSamples-numParameters
         */
        private double degreesOfFreedom;

        /**
         * Creates a new instance of Statistic
         * @param targets
         * Collection of ground-truth targets for the learned approximator
         * @param estimates
         * Collection of estimates from the learned approximator
         * @param numParameters
         * Number of parameters in the learned approximator
         */
        public Statistic(
            Collection<Double> targets,
            Collection<Double> estimates,
            int numParameters )
        {
            super( 0.0 );
            Collection<Double> weights =
                Collections.nCopies( targets.size(), new Double( 1.0 ) );
            this.computeStatistics( targets, estimates, weights, numParameters );

        }

        /**
         * Creates a new instance of Statistic
         * @param targets
         * Collection of ground-truth targets for the learned approximator
         * @param estimates
         * Collection of estimates from the learned approximator
         * @param weights
         * Collection of weights to apply to the corresponding target-estimate
         * pair
         * @param numParameters
         * Number of parameters in the learned approximator
         */
        public Statistic(
            Collection<Double> targets,
            Collection<Double> estimates,
            Collection<Double> weights,
            int numParameters )
        {
            super( 0.0 );
            this.computeStatistics( targets, estimates, weights, numParameters );
        }

        /**
         * Copy Constructor
         * @param other
         * Statistic to copy
         */
        private Statistic(
            Statistic other )
        {
            super( other.getNullHypothesisProbability() );
            this.setDegreesOfFreedom( other.getDegreesOfFreedom() );
            this.setMeanL1Error( other.getMeanL1Error() );
            this.setNumParameters( other.getNumParameters() );
            this.setNumSamples( other.getNumSamples() );
            this.setRootMeanSquaredError( other.getRootMeanSquaredError() );
            this.setTargetEstimateCorrelation( other.getTargetEstimateCorrelation() );
            this.setUnpredictedErrorFraction( other.getUnpredictedErrorFraction() );
        }

        @Override
        public Statistic clone()
        {
            return (Statistic) super.clone();
        }

        /**
         * Creates a new instance of Statistic
         * @param targets
         * Collection of ground-truth targets for the learned approximator
         * @param estimates
         * Collection of estimates from the learned approximator
         * @param weights
         * Collection of weights to apply to the corresponding target-estimate
         * pair
         * @param numParameters
         * Number of parameters in the learned approximator
         */
        private void computeStatistics(
            Collection<Double> targets,
            Collection<Double> estimates,
            Collection<Double> weights,
            int numParameters )
        {
            
            if ((targets.size() != estimates.size()) &&
                (targets.size() != weights.size()))
            {
                throw new IllegalArgumentException(
                    "Targets, Estimates, and Weights must be the same size!" );
            }

            // Compute the errors between the targets and the estimates
            int num = targets.size();
            ArrayList<Double> errors = new ArrayList<Double>( num );
            double averageL1Error = 0.0;
            double weightSum = 0.0;

            Iterator<Double> it = targets.iterator();
            Iterator<Double> ie = estimates.iterator();
            Iterator<Double> iw = weights.iterator();
            for (int n = 0; n < num; n++)
            {
                double estimate = ie.next();
                double target = it.next();
                double weight = iw.next();

                double error = weight * (target - estimate);
                errors.add( error );

                averageL1Error += Math.abs( error );
                weightSum += weight;
            }
            if (weightSum > 0)
            {
                averageL1Error /= weightSum;
            }
            else
            {
                averageL1Error = 0.0;
            }

            // Make sure that the DOFs stays above 0.0
            // (so let's just cap it to a minimum of 1.0)
            double dofs = num - numParameters;
            if( dofs < 1.0 )
            {
                dofs = 1.0;
            }
            double chi2 = UnivariateStatisticsUtil.computeSumSquaredDifference( errors, 0.0 );

            double pvalue = 1.0 - ChiSquareDistribution.CDF.evaluate(
                chi2, dofs );

            double rmsError =
                UnivariateStatisticsUtil.computeRootMeanSquaredError( errors, 0.0 );

            double correlation =
                UnivariateStatisticsUtil.computeCorrelation( targets, estimates );

            double unpredictedFraction = 1.0 - (correlation * correlation);

            this.setNullHypothesisProbability( pvalue );
            this.setChiSquare( chi2 );
            this.setDegreesOfFreedom( dofs );
            this.setMeanL1Error( averageL1Error );
            this.setNumSamples( num );
            this.setRootMeanSquaredError( rmsError );
            this.setTargetEstimateCorrelation( correlation );
            this.setUnpredictedErrorFraction( unpredictedFraction );
            this.setNumParameters( numParameters );

        }
        
        /**
         * Getter for rootMeanSquaredError
         * @return 
         * Root mean-squared error of the targets and estimates
         */
        public double getRootMeanSquaredError()
        {
            return this.rootMeanSquaredError;
        }

        /**
         * Setter fpr rootMeanSquaredError
         * @param rootMeanSquaredError 
         * Root mean-squared error of the targets and estimates
         */
        protected void setRootMeanSquaredError(
            double rootMeanSquaredError )
        {
            this.rootMeanSquaredError = rootMeanSquaredError;
        }

        /**
         * Getter for targetEstimateCorrelation
         * @return 
         * Pearson Correlation between the targets and estimates, [-1,1]
         */
        public double getTargetEstimateCorrelation()
        {
            return this.targetEstimateCorrelation;
        }

        /**
         * Setter for targetEstimateCorrelation
         * @param targetEstimateCorrelation 
         * Pearson Correlation between the targets and estimates, [-1,1]
         */
        protected void setTargetEstimateCorrelation(
            double targetEstimateCorrelation )
        {
            this.targetEstimateCorrelation = targetEstimateCorrelation;
        }

        /**
         * Getter for unpredictedErrorFraction
         * @return 
         * Fraction of variance unaccounted for in the predictions, [0,1]
         */
        public double getUnpredictedErrorFraction()
        {
            return this.unpredictedErrorFraction;
        }

        /**
         * Setter for unpredictedErrorFraction
         * @param unpredictedErrorFraction 
         * Fraction of variance unaccounted for in the predictions, [0,1]
         */
        protected void setUnpredictedErrorFraction(
            double unpredictedErrorFraction )
        {
            this.unpredictedErrorFraction = unpredictedErrorFraction;
        }

        /**
         * Getter for numSamples
         * @return 
         * Number of samples used to create the Regression
         */
        public int getNumSamples()
        {
            return this.numSamples;
        }

        /**
         * Setter for numSamples
         * @param numSamples 
         * Number of samples used to create the Regression
         */
        protected void setNumSamples(
            int numSamples )
        {
            this.numSamples = numSamples;
        }

        /**
         * Getter for degreesOfFreedom
         * @return 
         * Degrees of freedom in the Regression = numSamples-numParameters
         */
        public double getDegreesOfFreedom()
        {
            return this.degreesOfFreedom;
        }

        /**
         * Setter for degreesOfFreedom
         * @param degreesOfFreedom 
         * Degrees of freedom in the Regression = numSamples-numParameters
         */
        protected void setDegreesOfFreedom(
            double degreesOfFreedom )
        {
            this.degreesOfFreedom = degreesOfFreedom;
        }

        /**
         * Getter for meanL1Error
         * @return 
         * Average L1-norm error (absolute value difference) between the 
         * targets and estimates
         */
        public double getMeanL1Error()
        {
            return this.meanL1Error;
        }

        /**
         * Setter for meanL1Error
         * @param meanL1Error 
         * Average L1-norm error (absolute value difference) between the 
         * targets and estimates
         */
        protected void setMeanL1Error(
            double meanL1Error )
        {
            this.meanL1Error = meanL1Error;
        }

        /**
         * Getter for numParameters
         * @return
         * Number of parameters in the learned approximator
         */
        public int getNumParameters()
        {
            return this.numParameters;
        }

        /**
         * Setter for numParameters
         * @param numParameters
         * Number of parameters in the learned approximator
         */
        public void setNumParameters(
            int numParameters )
        {
            this.numParameters = numParameters;
        }

        /**
         * Getter for chiSquare
         * @return
         * Gets the value of the chi-square variable,
         * Total weighted sum-squared error between the targets and estimates
         */
        public double getChiSquare()
        {
            return this.chiSquare;
        }

        /**
         * Setter for chiSquare
         * @param chiSquare
         * Gets the value of the chi-square variable,
         * Total weighted sum-squared error between the targets and estimates
         */
        public void setChiSquare(
            double chiSquare )
        {
            this.chiSquare = chiSquare;
        }

    }

}
