/*
 * File:                DecoupledVectorLinearRegression.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright September 10, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.learning.algorithm.regression;

import gov.sandia.cognition.learning.data.DatasetUtil;
import gov.sandia.cognition.learning.data.InputOutputPair;
import gov.sandia.cognition.learning.function.vector.DecoupledVectorFunction;
import gov.sandia.cognition.statistics.distribution.MultivariateGaussian;
import gov.sandia.cognition.math.UnivariateScalarFunction;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.evaluator.Evaluator;
import gov.sandia.cognition.learning.algorithm.SupervisedBatchLearner;
import gov.sandia.cognition.learning.function.scalar.VectorFunctionLinearDiscriminant;
import gov.sandia.cognition.learning.function.vector.ScalarBasisSet;
import gov.sandia.cognition.util.AbstractCloneableSerializable;
import gov.sandia.cognition.util.ObjectUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

/**
 * Performs LinearRegression by splitting a set of coupled Vector equations 
 * into decoupled scalar equations and solving the LinearRegression on the
 * decoupled equations.  Thus, if you have a set of input-output pair Vectors,
 * all of the same dimension, this algorithm will create a VectorFunction that
 * computes the least-squares to each row of the Vectors using the standard
 * LinearRegression algorithm for each dimension.  The LinearRegression
 * solution behaves as if the elements of the Vector dataset can be predicted
 * independently of all others in the dataset.
 *
 * @author Kevin R. Dixon
 * @since  2.0
 *
 */
public class DecoupledVectorLinearRegression
    extends AbstractCloneableSerializable
    implements SupervisedBatchLearner<Vector, Vector, DecoupledVectorFunction>
{

    /**
     * Weighted linear combination of (potentially) nonlinear basis functions,
     * the coefficients of each LinearCombinationScalarFunction will be tuned
     * to solve the ensuing decoupled Vector equations
     */
    private Collection<ScalarBasisSet<Double>> elementFunctions;

    /**
     * Function created from the learn method
     */
    private DecoupledVectorFunction learned;

    /**
     * Number of parameters from the regression
     */
    private int numParameters;

    /**
     * Creates a new instance of LinearRegression
     * 
     * @param dimensionality 
     * Dimensionality of the Vector equation to solve
     * @param basisFunctions
     * Basis functions to create the LinearCombinationFunction from, these
     * ScalarFunctions are combined into a single
     * LinearCombinationScalarFunction, and a copy of this function is repeated
     * for each row of the Vector function
     */
    public DecoupledVectorLinearRegression(
        int dimensionality,
        UnivariateScalarFunction... basisFunctions )
    {
        this( dimensionality, Arrays.asList( basisFunctions ) );
    }

    /**
     * Creates a new instance of LinearRegression
     * 
     * @param dimensionality 
     * Dimensionality of the Vector equation to solve
     * @param basisFunctions
     * Basis functions to create the LinearCombinationFunction from, these
     * ScalarFunctions are combined into a single
     * LinearCombinationScalarFunction, and a copy of this function is repeated
     * for each row of the Vector function
     */
    public DecoupledVectorLinearRegression(
        int dimensionality,
        Collection<? extends Evaluator<? super Double, Double>> basisFunctions )
    {
        this( dimensionality, new ScalarBasisSet<Double>( basisFunctions ) );
    }

    /**
     * Creates a new instance of DecoupledVectorLinearRegression
     * @param dimensionality 
     * Dimensionality of the Vector equation to solve
     * @param elementFunction 
     * LinearCombinationScalarFunction that will be repeated for each
     * row in solving the LinearRegression equation
     */
    @SuppressWarnings("unchecked")
    public DecoupledVectorLinearRegression(
        int dimensionality,
        ScalarBasisSet<Double> elementFunction )
    {
        ArrayList<ScalarBasisSet<Double>> functions =
            new ArrayList<ScalarBasisSet<Double>>( dimensionality );
        for (int i = 0; i < dimensionality; i++)
        {
            functions.add( (ScalarBasisSet<Double>) elementFunction.clone() );
        }
        this.setElementFunctions( functions );
        this.setLearned( null );
        this.setNumParameters( elementFunction.getOutputDimensionality() );
    }

    /**
     * Creates a new instance of DecoupledVectorLinearRegression
     * @param elementFunctions 
     * Collection of LinearCombinationScalarFunctions, one for each element
     * of the ensuing Vector equation, that will be used to solve for each
     * element in the LinearRegression equation after the Vector equation
     * has been decoupled
     */
    public DecoupledVectorLinearRegression(
        Collection<ScalarBasisSet<Double>> elementFunctions )
    {
        this.setElementFunctions( elementFunctions );
        this.setLearned( null );
        this.setNumParameters( elementFunctions.iterator().next().getOutputDimensionality() );
    }

    @Override
    public DecoupledVectorLinearRegression clone()
    {
        DecoupledVectorLinearRegression clone =
            (DecoupledVectorLinearRegression) super.clone();
        clone.setElementFunctions( ObjectUtil.cloneSmartElementsAsArrayList(
            this.getElementFunctions() ) );
        return clone;
    }

    /**
     * Getter for elementFunctions
     * @return 
     * Weighted linear combination of (potentially) nonlinear basis functions,
     * the coefficients of each LinearCombinationScalarFunction will be tuned
     * to solve the ensuing decoupled Vector equations
     */
    public Collection<ScalarBasisSet<Double>> getElementFunctions()
    {
        return this.elementFunctions;
    }

    /**
     * Setter for elementFunctions
     * @param elementFunctions 
     * Weighted linear combination of (potentially) nonlinear basis functions,
     * the coefficients of each LinearCombinationScalarFunction will be tuned
     * to solve the ensuing decoupled Vector equations
     */
    public void setElementFunctions(
        Collection<ScalarBasisSet<Double>> elementFunctions )
    {
        if (elementFunctions.size() <= 0)
        {
            throw new IllegalArgumentException(
                "Must have at least one function in the function Collection" );
        }
        this.elementFunctions = elementFunctions;
    }

    /**
     * Gets the dimensionality of the Vector equation that is expected
     * @return 
     * Dimension of the Vector equation that this algorithm will solve
     */
    public int getDimensionality()
    {
        return this.getElementFunctions().size();
    }

    /**
     * Creates a DecoupledVectorFunction that solves for the least-squares
     * solution to the decoupled scalar-scalar pairs for each element in
     * the dataset
     * @param data
     * Vector-Vector dataset pairs, whose elements will be decoupled, will
     * be solved using standard scalar LinearRegression. 
     * @return 
     * DecoupledVectorFunction that optimally estimates, in the least-squares
     * sense, the elements of the Vector equation independent of all others.
     */
    public DecoupledVectorFunction learn(
        Collection<? extends InputOutputPair<? extends Vector, Vector>> data )
    {

        // First, decouple the dataset
        ArrayList<ArrayList<InputOutputPair<Double, Double>>> decoupledDataset =
            DatasetUtil.decoupleVectorPairDataset( data );

        ArrayList<VectorFunctionLinearDiscriminant<Double>> regressionFunctions =
            new ArrayList<VectorFunctionLinearDiscriminant<Double>>( this.getDimensionality() );
        int i = 0;
        int params = -1;
        for (ScalarBasisSet<Double> fi : this.getElementFunctions())
        {
            ArrayList<? extends InputOutputPair<Double, Double>> rowDataset =
                decoupledDataset.get( i );
            LinearRegression<Double> lri = new LinearRegression<Double>( fi );
            regressionFunctions.add( lri.learn( rowDataset ) );
            if (params < 0)
            {
                params = lri.getLearned().convertToVector().getDimensionality();
            }
            i++;
        }

        this.setLearned( new DecoupledVectorFunction( regressionFunctions ) );
        this.setNumParameters( params );
        return this.getLearned();

    }

    /**
     * Computes the LinearRegression.Statistics associated with this
     * LinearRegression and the given dataset.  The dataset can be different
     * than that used to create the LinearCombinationFunction.  (For example,
     * it can be the training set again, or it can be a previously unseen 
     * cross-validation set).
     * @param data 
     * Dataset against which to compute the LinearRegression.Statistic
     * @return 
     * LinearRegression.Statistic that describes the ability for the
     * LinearRegression to predict the given dataset, including a p-value
     */
    public DecoupledVectorLinearRegression.Statistic computeStatistics(
        Collection<? extends InputOutputPair<Vector, Vector>> data )
    {

        ArrayList<Vector> targets = new ArrayList<Vector>( data.size() );
        ArrayList<Vector> estimates = new ArrayList<Vector>( data.size() );
        ArrayList<Double> weights = new ArrayList<Double>( data.size() );
        for (InputOutputPair<Vector, Vector> pair : data)
        {
            double weight = DatasetUtil.getWeight(pair);

            targets.add( pair.getOutput() );
            estimates.add( this.getLearned().evaluate( pair.getInput() ) );
            weights.add( weight );
        }

        return new DecoupledVectorLinearRegression.Statistic(
            targets, estimates, weights, this.getNumParameters() );
    }

    /**
     * Getter for learned
     * @return
     * Function created from the learn method
     */
    public DecoupledVectorFunction getLearned()
    {
        return this.learned;
    }

    /**
     * Setter for learned
     * @param learned
     * Function created from the learn method
     */
    public void setLearned(
        DecoupledVectorFunction learned )
    {
        this.learned = learned;
    }

    /**
     * Getter for numParameters
     * @return
     * Number of parameters from the regression
     */
    public int getNumParameters()
    {
        return this.numParameters;
    }

    /**
     * Setter for numParameters
     * @param numParameters
     * Number of parameters from the regression
     */
    public void setNumParameters(
        int numParameters )
    {
        this.numParameters = numParameters;
    }

    /**
     * Contains the statistics from a DecoupledVectorLinearRegression
     */
    public static class Statistic
        extends AbstractCloneableSerializable
    {

        /**
         * Small covariance value in the joint statistics, {@value}.
         */
        public static final double SMALL_COVARIANCE = 1e-10;

        /**
         * Statistics regarding the element-by-element regression performance
         */
        private Collection<LinearRegression.Statistic> elementStatistics;

        /**
         * Captures how the regression errors covary
         */
        private MultivariateGaussian jointErrorStatistics;

        /**
         * Creates a new instance of Statistics
         * @param targets 
         * Target Vectors (ground truth, or labels)
         * @param estimates 
         * Estimates of the corresponding targets
         * @param weights
         * Collection of weights to apply to the corresponding target-estimate
         * pair
         * @param numParameters
         * Number of parameters in the function approximator
         */
        public Statistic(
            Collection<Vector> targets,
            Collection<Vector> estimates,
            Collection<Double> weights,
            int numParameters )
        {

            ArrayList<ArrayList<Double>> decoupledTargets =
                DatasetUtil.decoupleVectorDataset( targets );
            ArrayList<ArrayList<Double>> decoupledEstimates =
                DatasetUtil.decoupleVectorDataset( estimates );

            if ((targets.size() != estimates.size()) ||
                (targets.size() != weights.size()))
            {
                throw new IllegalArgumentException(
                    "Number of targets must equal the number of estimates" );
            }

            final int num = targets.size();

            if (decoupledTargets.size() != decoupledEstimates.size())
            {
                throw new IllegalArgumentException(
                    "Dimensionality of targets aren't estimates" );
            }

            final int M = decoupledTargets.size();
            ArrayList<LinearRegression.Statistic> statistics =
                new ArrayList<LinearRegression.Statistic>( M );
            for (int i = 0; i < M; i++)
            {
                statistics.add( new LinearRegression.Statistic(
                    decoupledTargets.get( i ), decoupledEstimates.get( i ),
                    weights, numParameters ) );
            }

            // Compute the covarying error statistics
            ArrayList<Vector> errors = new ArrayList<Vector>( num );
            Iterator<Vector> ti = targets.iterator();
            Iterator<Vector> ei = estimates.iterator();
            for (int n = 0; n < num; n++)
            {
                errors.add( ti.next().minus( ei.next() ) );
            }

            MultivariateGaussian errorCovarianceStatistics =
                MultivariateGaussian.MaximumLikelihoodEstimator.learn(
                    errors, SMALL_COVARIANCE );

            this.setJointErrorStatistics( errorCovarianceStatistics );
            this.setElementStatistics( statistics );

        }

        @Override
        public Statistic clone()
        {
            Statistic clone = (Statistic) super.clone();
            clone.setJointErrorStatistics(
                ObjectUtil.cloneSafe( this.getJointErrorStatistics() ) );
            clone.setElementStatistics(
                ObjectUtil.cloneSmartElementsAsArrayList(
                    this.getElementStatistics() ) );

            return clone;
        }

        /**
         * Getter for elementStatistics
         * @return 
         * Statistics regarding the element-by-element regression performance
         */
        public Collection<LinearRegression.Statistic> getElementStatistics()
        {
            return this.elementStatistics;
        }

        /**
         * Setter for elementStatistics
         * @param elementStatistics 
         * Statistics regarding the element-by-element regression performance
         */
        public void setElementStatistics(
            Collection<LinearRegression.Statistic> elementStatistics )
        {
            this.elementStatistics = elementStatistics;
        }

        /**
         * Getter for jointErrorStatistics
         * @return 
         * Captures how the regression errors covary
         */
        public MultivariateGaussian getJointErrorStatistics()
        {
            return this.jointErrorStatistics;
        }

        /**
         * Setter for jointErrorStatistics
         * @param jointErrorStatistics 
         * Captures how the regression errors covary
         */
        public void setJointErrorStatistics(
            MultivariateGaussian jointErrorStatistics )
        {
            this.jointErrorStatistics = jointErrorStatistics;
        }

    }

}
