/*
 * File:                ConjugatePriorBayesianEstimatorTestHarness.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Nov 17, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.statistics.bayesian.conjugate;

import gov.sandia.cognition.collection.CollectionUtil;
import gov.sandia.cognition.math.Ring;
import gov.sandia.cognition.math.UnivariateStatisticsUtil;
import gov.sandia.cognition.math.matrix.Matrix;
import gov.sandia.cognition.math.matrix.MatrixFactory;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.statistics.ClosedFormDistribution;
import gov.sandia.cognition.statistics.ComputableDistribution;
import gov.sandia.cognition.statistics.CumulativeDistributionFunction;
import gov.sandia.cognition.statistics.Distribution;
import gov.sandia.cognition.statistics.ProbabilityFunction;
import gov.sandia.cognition.statistics.UnivariateDistribution;
import gov.sandia.cognition.statistics.bayesian.BayesianCredibleInterval;
import gov.sandia.cognition.statistics.bayesian.BayesianEstimatorPredictor;
import gov.sandia.cognition.statistics.bayesian.BayesianParameter;
import gov.sandia.cognition.statistics.bayesian.BayesianUtil;
import gov.sandia.cognition.statistics.bayesian.RecursiveBayesianEstimatorTestHarness;
import gov.sandia.cognition.statistics.distribution.UnivariateGaussian;
import gov.sandia.cognition.statistics.method.KolmogorovSmirnovConfidence;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * Unit tests for ConjugatePriorBayesianEstimatorTestHarness.
 *
 * @param <ObservationType> Type of observations
 * @param <ParameterType> Type of parameter
 * @param <BeliefType> Belief type
 * @author krdixon
 */
public abstract class ConjugatePriorBayesianEstimatorTestHarness<ObservationType,ParameterType,BeliefType extends ClosedFormDistribution<ParameterType>>
    extends RecursiveBayesianEstimatorTestHarness<ObservationType,ParameterType,BeliefType>
{

    /**
     * Default confidence interval, {@value}.
     */
    public double CONFIDENCE = 0.95;

    /**
     * Tests for class ConjugatePriorBayesianEstimatorTestHarness.
     * @param testName Name of the test.
     */
    public ConjugatePriorBayesianEstimatorTestHarness(
        String testName)
    {
        super(testName);
    }

    /**
     * Creates a new instance
     * @return
     * new instance.
     */
    public abstract ConjugatePriorBayesianEstimator<ObservationType,ParameterType,?,BeliefType> createInstance();

    /**
     * Creates the estimated type
     * @return
     * estimate type distribution
     */
    public abstract ClosedFormDistribution<ObservationType> createConditionalDistribution();

    /**
     * Ensures that reversing the order of online estimation doesn't change
     * the result.
     */
    @SuppressWarnings("unchecked")
    public void testReverseOrder()
    {
        System.out.println( "Reverse Order" );

        ClosedFormDistribution<ObservationType> conditional =
            this.createConditionalDistribution();
        ArrayList<ObservationType> data = new ArrayList<ObservationType>(
            this.createData(conditional) );

        ConjugatePriorBayesianEstimator<ObservationType,?,?,?> estimator = this.createInstance();
        ConjugatePriorBayesianEstimator<ObservationType,ParameterType,?,BeliefType> c1 =
            (ConjugatePriorBayesianEstimator<ObservationType,ParameterType,?,BeliefType>) estimator.clone();
        
        ConjugatePriorBayesianEstimator<ObservationType,ParameterType,?,BeliefType> c2 =
            (ConjugatePriorBayesianEstimator<ObservationType,ParameterType,?,BeliefType>) estimator.clone();

        BeliefType p1 = (BeliefType) c1.createInitialLearnedObject().clone();
        BeliefType p2 = (BeliefType) c2.createInitialLearnedObject().clone();

        for( ObservationType value : data )
        {
            c1.update( p1, value );
        }

        // Result should be the same if we reverse the order of estimation.
        Collections.reverse(data);
        for( ObservationType value : data )
        {
            c2.update( p2, value );
        }
        
        // We'll just make sure the means are approximately the same
        System.out.println( "Conditional: " + conditional );
        System.out.println( "Online Mean:  " + p1.getMean() );
        System.out.println( "Reverse Mean: " + p2.getMean() );

        if( p1.getMean() instanceof Ring )
        {
            assertTrue( ((Ring) p1.getMean()).equals( (Ring) p2.getMean(), TOLERANCE ) );            
        }
        else if( p1.getMean() instanceof Number )
        {
            assertEquals( ((Number) p1.getMean()).doubleValue(), ((Number) p2.getMean()).doubleValue(), TOLERANCE );
        }
        else
        {
            fail( "Unknown data type: " + p1.getMean().getClass() );
        }

    }

    /**
     * Test of createConditionalDistribution method, of class ConjugatePriorBayesianEstimator.
     */
    @SuppressWarnings("unchecked")
    public void testCreateConditionalDistribution()
    {
        System.out.println("createConditionalDistribution");

        ClosedFormDistribution<ObservationType> conditional =
            this.createConditionalDistribution();
        ArrayList<ObservationType> data = new ArrayList<ObservationType>(
            this.createData(conditional) );
        ConjugatePriorBayesianEstimator<ObservationType,ParameterType,?,BeliefType> estimator = this.createInstance();

        BeliefType belief = estimator.learn(data);
        System.out.println( "Belief: " + belief );
        ParameterType meanObject = belief.getMean();
 
        Distribution<ObservationType> distribution =
            estimator.createConditionalDistribution(meanObject);
        assertNotNull( distribution );


        // Make sure the distribution could have produced the given data
        if( CollectionUtil.getFirst(data) instanceof Number )
        {
            Collection<? extends Number> d1 = (Collection<? extends Number>) data;
            
            Collection<? extends ObservationType> s2 =
                distribution.sample(RANDOM, NUM_SAMPLES);
            Collection<? extends Number> d2 = (Collection<? extends Number>) s2;
            
            KolmogorovSmirnovConfidence.Statistic kstest =
                KolmogorovSmirnovConfidence.INSTANCE.evaluateNullHypothesis(d1,d2);
            System.out.println( "Mean: " + meanObject + " Sample Mean: " + UnivariateStatisticsUtil.computeMean(d1) );
            assertEquals( 1.0, kstest.getNullHypothesisProbability(), CONFIDENCE );
        }


        // Should barf with a different number of parameters.
        if( meanObject instanceof Vector )
        {
            Vector parameters = (Vector) meanObject;
            int num = parameters.getDimensionality();
            Vector p2 = VectorFactory.getDefault().createVector(num+1);
            try
            {
                estimator.createConditionalDistribution( (ParameterType) p2 );
                fail( "Wrong number of parameters!");
            }
            catch (Exception e)
            {
                System.out.println( "Good: " + e );
            }
        }

    }

    /**
     * computeEquivalentSampleSize
     */
    @SuppressWarnings("unchecked")
    public void testComputeEquivalentSampleSize()
    {
        System.out.println( "computeEquivalentSampleSize" );

        ConjugatePriorBayesianEstimator<ObservationType,ParameterType,?,BeliefType> estimator = this.createInstance();

        ClosedFormDistribution<ObservationType> conditional =
            this.createConditionalDistribution();
        ArrayList<ObservationType> data = new ArrayList<ObservationType>(
            this.createData(conditional) );
        BeliefType prior = estimator.createInitialLearnedObject();
        double initialSamples = estimator.computeEquivalentSampleSize(prior);
        for( ObservationType value : data )
        {
            estimator.update( prior, value);
        }
        double finalSamples = estimator.computeEquivalentSampleSize(prior);
        double delta = finalSamples - initialSamples;
        assertEquals( (double) data.size(), delta, TOLERANCE );

    }
    
    /**
     * createPredictiveDistribution
     */
    @SuppressWarnings("unchecked")
    public void testCreatePredictiveDistribution()
    {
        System.out.println( "createPredictiveDistribution" );

        ConjugatePriorBayesianEstimator<ObservationType,ParameterType,ClosedFormDistribution<ObservationType>,BeliefType> estimator =
            (ConjugatePriorBayesianEstimator<ObservationType, ParameterType, ClosedFormDistribution<ObservationType>, BeliefType>) this.createInstance();
        if( estimator instanceof BayesianEstimatorPredictor )
        {
            BayesianEstimatorPredictor<ObservationType,ParameterType,BeliefType> instance =
                (BayesianEstimatorPredictor<ObservationType, ParameterType, BeliefType>) estimator;

            ClosedFormDistribution<ObservationType> target =
                this.createConditionalDistribution();
            Collection<? extends ObservationType> observations =
                this.createData(target);
            BeliefType posterior = estimator.learn(observations);
            ArrayList<? extends ParameterType> parameters =
                posterior.sample(RANDOM,NUM_SAMPLES);


            ProbabilityFunction<ObservationType> predictivePDF =
                instance.createPredictiveDistribution(posterior).getProbabilityFunction();

            ArrayList<Double> results = new ArrayList<Double>( observations.size() );
            ArrayList<Double> empiricals = new ArrayList<Double>( observations.size() );
            for( ObservationType observation : observations )
            {
                // This is computing the integral of the predictive distribution
                // p(sample | data) = integral{ p(sample|data,parameter) * p(parameter|data) dparameter }
                // We're using Monte Carlo integration by sampling from the
                // posterior and multiplying by the conditional distribution.
                double sum = 0.0;
                for( ParameterType parameter : parameters )
                {
                    ComputableDistribution<ObservationType> generator =
                        (ComputableDistribution<ObservationType>) estimator.createConditionalDistribution(parameter);
                    sum += generator.getProbabilityFunction().evaluate( observation );
                }
                double empirical = sum/parameters.size();
                empiricals.add( empirical );
                double estimate = predictivePDF.evaluate(observation);
                results.add( estimate );
            }

            KolmogorovSmirnovConfidence.Statistic kstest =
                KolmogorovSmirnovConfidence.INSTANCE.evaluateNullHypothesis(results,empiricals);
            System.out.println( "K-S Test: " + kstest );
            assertEquals( 1.0, kstest.getNullHypothesisProbability(), 0.95 );

            // Let's get all Monte Carlo on them
            if( predictivePDF instanceof UnivariateDistribution )
            {
                CumulativeDistributionFunction<Number> cdf =
                    ((UnivariateDistribution) predictivePDF).getCDF();

                BayesianParameter<ParameterType,ClosedFormDistribution<ObservationType>,BeliefType> parameter =
                    estimator.createParameter( estimator.getParameter().getConditionalDistribution(), posterior);

                ArrayList<? extends Number> estimates = (ArrayList<? extends Number>) BayesianUtil.sample(
                    parameter, RANDOM, NUM_SAMPLES);

                KolmogorovSmirnovConfidence.Statistic kstest2 =
                    KolmogorovSmirnovConfidence.evaluateNullHypothesis(estimates,cdf);
                System.out.println( "K-S test2: " + kstest2 );
                assertEquals( 1.0, kstest2.getNullHypothesisProbability(), CONFIDENCE );

            }

        }
        else
        {
            System.out.println( estimator.getClass().getName() + " is not a BayesianEstimatorPredictor... createPredictiveDistribution not tested" );
        }

    }

    /**
     * getParameter
     */
    @SuppressWarnings("unchecked")
    public void testGetParameter()
    {
        System.out.println( "getParameter" );

        ConjugatePriorBayesianEstimator<ObservationType,ParameterType,?,BeliefType> estimator = this.createInstance();
        BayesianParameter<ParameterType,?,BeliefType> parameter = estimator.getParameter();
        assertNotNull( parameter );
        assertNotNull( parameter.getConditionalDistribution() );
        ParameterType value = parameter.getValue();
        parameter.setValue(value);
        assertEquals( value, parameter.getValue() );
        ParameterType mean = parameter.getParameterPrior().getMean();
        parameter.setValue(mean);
        assertEquals( mean, parameter.getValue() );
        assertTrue( parameter.getName().length() > 0 );

        if( mean instanceof Vector )
        {
            Vector p2 = VectorFactory.getDefault().createVector(
                ((Vector) mean).getDimensionality() + 1 );
            try
            {
                parameter.setValue( (ParameterType) p2);
                fail( "Value wrong dimension" );
            }
            catch (Exception e)
            {
                System.out.println( "Good: " + e );
            }
        }
        else if( mean instanceof Matrix )
        {
            int M = ((Matrix) mean).getNumRows();
            int N = ((Matrix) mean).getNumColumns();
            Matrix p2 = MatrixFactory.getDefault().createMatrix(M+1, N+1);
            try
            {
                parameter.setValue( (ParameterType) p2);
                fail( "Value wrong dimension" );
            }
            catch (Exception e)
            {
                System.out.println( "Good: " + e );
            }

        }

    }

    /**
     * createParameter
     */
    @SuppressWarnings("unchecked")
    public void testCreateParameter()
    {
        System.out.println( "createParameter" );

        ClosedFormDistribution<ObservationType> conditional =
            this.createConditionalDistribution();
        ConjugatePriorBayesianEstimator<ObservationType,ParameterType,ClosedFormDistribution<ObservationType>,BeliefType> instance =
            (ConjugatePriorBayesianEstimator<ObservationType, ParameterType, ClosedFormDistribution<ObservationType>, BeliefType>) this.createInstance();

        BeliefType prior = instance.createInitialLearnedObject();

        BayesianParameter<ParameterType,ClosedFormDistribution<ObservationType>,BeliefType> parameter =
            instance.createParameter( conditional, prior);

        assertSame( conditional, parameter.getConditionalDistribution() );
        assertSame( prior, parameter.getParameterPrior() );
    }

}
