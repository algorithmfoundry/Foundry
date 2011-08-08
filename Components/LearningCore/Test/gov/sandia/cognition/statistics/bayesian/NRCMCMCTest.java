/*
 * File:                NRCMCMCTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Feb 23, 2010, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.statistics.bayesian;

import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.collection.CollectionUtil;
import gov.sandia.cognition.statistics.AbstractDistribution;
import gov.sandia.cognition.statistics.DataHistogram;
import gov.sandia.cognition.statistics.Distribution;
import gov.sandia.cognition.statistics.distribution.GammaDistribution;
import gov.sandia.cognition.statistics.distribution.LogNormalDistribution;
import gov.sandia.cognition.statistics.distribution.UnivariateGaussian;
import gov.sandia.cognition.statistics.method.GaussianConfidence;
import gov.sandia.cognition.util.AbstractCloneableSerializable;
import gov.sandia.cognition.util.DefaultWeightedValue;
import gov.sandia.cognition.util.ObjectUtil;
import gov.sandia.cognition.util.WeightedValue;
import java.util.ArrayList;
import junit.framework.TestCase;
import java.util.Random;

/**
 * Unit tests for NRCMCMCTest.
 *
 * @author krdixon
 */
@PublicationReference(
    author={
        "William H. Press",
        "Saul A. Teukolsky",
        "William T. Vetterling",
        "Brian P. Flannery"
    },
    title="Numerical Recipes, Third Edition",
    type=PublicationType.Book,
    year=2007,
    pages={829,835}
)
public class NRCMCMCTest
    extends TestCase
{

    /**
     * Random number generator to use for a fixed random seed.
     */
    public final static Random RANDOM = new Random( 1 );

    /**
     * Default tolerance of the regression tests, {@value}.
     */
    public final double TOLERANCE = 1e-5;

    /**
     * Tests for class NRCMCMCTest.
     * @param testName Name of the test.
     */
    public NRCMCMCTest(
        String testName)
    {
        super(testName);
    }

    public void testMCMC()
    {
        System.out.println( "NRC MCMC Example" );

        // Target on page 833
        State target = new State();
        target.block1 = new GammaDistribution.PDF( 1, 1.0/3.0 );
        target.block2 = new GammaDistribution.PDF( 2, 1.0/2.0 );
        target.tc = 200.0;

        ArrayList<Double> data = target.sample(RANDOM, 1000);
        

        int numSamples = 1000;
        MetropolisHastingsAlgorithm<Double,State> mcmc =
            new MetropolisHastingsAlgorithm<Double,State>();
        mcmc.setBurnInIterations(1000);
        mcmc.setIterationsPerSample(10);
        mcmc.setMaxIterations( numSamples );
        mcmc.setRandom(RANDOM);
        mcmc.setUpdater( new StateProposer() );
        
        DataHistogram<State> result = mcmc.learn( data );
        ArrayList<Double> lam1 = new ArrayList<Double>( result.getValues().size() );
        ArrayList<Double> lam2 = new ArrayList<Double>( result.getValues().size() );
        ArrayList<Double> k1 = new ArrayList<Double>( result.getValues().size() );
        ArrayList<Double> k2 = new ArrayList<Double>( result.getValues().size() );
        ArrayList<Double> tc = new ArrayList<Double>( result.getValues().size() );
        for( State state : result.getValues() )
        {
            lam1.add( 1.0 / state.block1.getScale() );
            k1.add( state.block1.getShape() );

            lam2.add( 1.0 / state.block2.getScale() );
            k2.add( state.block2.getShape() );

            tc.add( state.tc );
        }


        UnivariateGaussian.MaximumLikelihoodEstimator mle =
            new UnivariateGaussian.MaximumLikelihoodEstimator();
        UnivariateGaussian l1 = mle.learn(lam1);
        UnivariateGaussian l2 = mle.learn(lam2);
        UnivariateGaussian sample1 = mle.learn(k1);
        UnivariateGaussian sample2 = mle.learn(k2);
        UnivariateGaussian switchTime = mle.learn(tc);

        System.out.println( "Num Samples: " + result.getValues().size() );
        System.out.println( "Lambda1: " + l1 + " (" + 1.0/target.block1.getScale() + ")" );
        System.out.println( "Lambda2: " + l2 + " (" + 1.0/target.block2.getScale() + ")" );
        System.out.println( "K1: " + sample1 + " (" + target.block1.getShape() + ")" );
        System.out.println( "K2: " + sample2 + " (" + target.block2.getShape() + ")" );
        System.out.println( "TC: " + switchTime + " (" + target.tc + ")" );
        System.out.println( "Proposals: " + ((StateProposer) mcmc.getUpdater()).numProposals );

        final double confidence = 0.95;
        assertTrue( GaussianConfidence.computeConfidenceInterval(switchTime, 1, confidence ).withinInterval( target.tc ) );
        assertTrue( GaussianConfidence.computeConfidenceInterval(l1, 1, confidence ).withinInterval( 1.0/target.block1.getScale() ) );
        assertTrue( GaussianConfidence.computeConfidenceInterval(l2, 1, confidence ).withinInterval( 1.0/target.block2.getScale() ) );
        assertTrue( GaussianConfidence.computeConfidenceInterval(sample1, 1, confidence ).withinInterval( target.block1.getShape() ) );
        assertTrue( GaussianConfidence.computeConfidenceInterval(sample2, 1, confidence ).withinInterval( target.block2.getShape() ) );

    }

    public static class State
        extends AbstractDistribution<Double>
    {

        // Distribution as Gamma(k1,1/lam1)
        GammaDistribution.PDF block1;
        GammaDistribution.PDF block2;

        double tc;

        State()
        {
        }

        @Override
        public State clone()
        {
            State clone = (State) super.clone();
            clone.block1 = ObjectUtil.cloneSafe(this.block1);
            clone.block2 = ObjectUtil.cloneSafe(this.block2);
            return clone;
        }

        @Override
        public String toString()
        {
            StringBuilder retval = new StringBuilder( 1000 );
            retval.append( "L1: " + (1.0/this.block1.getScale()) );
            retval.append( ", L2: " + (1.0/this.block2.getScale()) );
            retval.append( ", k1: " + this.block1.getShape() );
            retval.append( ", k2: " + this.block2.getShape() );
            retval.append( ", TC: " + this.tc );


            return retval.toString();
        }



        public Double getMean()
        {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public ArrayList<Double> sample(
            Random random,
            int numSamples)
        {

            ArrayList<Double> samples = new ArrayList<Double>( numSamples );
            double tn = this.block1.sample(random);
            double tnm1;
            int num = 0;
            while( tn < this.tc )
            {
                if( num >= numSamples )
                {
                    break;
                }
                
                tnm1 = tn;
                double delta = this.block1.sample(random);
                tn = tnm1 + delta;
                samples.add( tn );
                num++;

            }

            while( num < numSamples )
            {
                tnm1 = tn;
                double delta = this.block2.sample(random);
                tn = tnm1 + delta;
                samples.add( tn );
                num++;
            }

            return samples;

        }

    }

    public static class StateProposer
        extends AbstractCloneableSerializable
        implements MetropolisHastingsAlgorithm.Updater<Double,State>
    {

        int numProposals;
        Distribution<Double> rateDistribution;

        public StateProposer()
        {
       

// Variance = 1e-2
//NRC MCMC Example
//Num Samples: 1000
//Lambda1: Mean: 3.046970925969783 Variance: 0.01682961943657213
//Lambda2: Mean: 1.956034800811245 Variance: 0.006137517773549317
//K1: Mean: 1.0 Variance: 1.0E-5
//K2: Mean: 2.0 Variance: 1.0E-5
//TC: Mean: 204.05029025923912 Variance: 6.079847500181761
//Proposals: 189790

// Variance = 1e-3
//NRC MCMC Example
//Num Samples: 1000
//Lambda1: Mean: 3.0500469358025795 Variance: 0.015369884721472172
//Lambda2: Mean: 1.9544424909132538 Variance: 0.005578737285304441
//K1: Mean: 1.0 Variance: 1.0E-5
//K2: Mean: 2.0 Variance: 1.0E-5
//TC: Mean: 203.68103081793976 Variance: 6.182053680254045
//Proposals: 56832


// Variance = 1e-4
//NRC MCMC Example
//Num Samples: 1000
//Lambda1: Mean: 3.049272758347756 Variance: 0.014315273259578792
//Lambda2: Mean: 1.9582259130893622 Variance: 0.005027429314720611
//K1: Mean: 1.0 Variance: 1.0E-5
//K2: Mean: 2.0 Variance: 1.0E-5
//TC: Mean: 203.85986467803403 Variance: 5.229608836301206
//Proposals: 25666

// Variance = 1e-5
//NRC MCMC Example
//Num Samples: 1000
//Lambda1: Mean: 2.9129840668142233 Variance: 0.12450411895211685
//Lambda2: Mean: 1.4253602585644953 Variance: 0.08850209147018964
//K1: Mean: 1.0 Variance: 1.0E-5
//K2: Mean: 1.181 Variance: 0.1483973873873874
//TC: Mean: 142.983944933737 Variance: 1502.8535284551501
//Proposals: 13953

// Variance = 1e-6
//NRC MCMC Example
//Num Samples: 1000
//Lambda1: Mean: 1.4832103096822644 Variance: 0.03894186531760483
//Lambda2: Mean: 1.6441403574137285 Variance: 0.04866942131302686
//K1: Mean: 1.0 Variance: 1.0E-5
//K2: Mean: 1.0 Variance: 1.0E-5
//TC: Mean: 90.75214296664389 Variance: 4.61211278684217
//Proposals: 12499
            
            this.rateDistribution = new LogNormalDistribution( 0.0, 1e-4 );
            this.numProposals = 0;
        }

        public WeightedValue<State> makeProposal(
            State location)
        {

            this.numProposals++;
            State proposal = location.clone();

            double qratio;
            
            // 90% of the time, fiddle with the rates
            double p = RANDOM.nextDouble();
            if( p < 0.9 )
            {
                double lam1 = 1.0/location.block1.getScale();
                double ln1 = this.rateDistribution.sample(RANDOM);
                proposal.block1.setScale( 1.0 / (lam1*ln1) );

                double lam2 = 1.0/location.block2.getScale();
                double ln2 = this.rateDistribution.sample(RANDOM);
                proposal.block2.setScale( 1.0 / (lam2*ln2) );

                double tcstep = this.rateDistribution.sample(RANDOM);
                proposal.tc = location.tc * tcstep;

                qratio = ln1 * ln2 * tcstep;
            }
            else
            {
                p = RANDOM.nextDouble();
                int k1 = (int) location.block1.getShape();
                double k1hat = k1;
                if( k1 > 1 )
                {
                    if( p < 0.5 )
                    {
                        k1hat = k1;
                        // Do nothing 
                    }
                    else if( p < 0.75 )
                    {
                        k1hat = k1+1;
                    }
                    else
                    {
                        k1hat = k1-1;
                    }
                }
                else
                {
                    // If we're already at k=1, then increament 25% of the time
                    if( p < 0.75 )
                    {
                        k1hat = k1;
                    }
                    else
                    {
                        k1hat = k1+1;
                    }
                }
                double ln1 = k1hat / k1;
                proposal.block1.setScale( proposal.block1.getScale() / ln1 );
                proposal.block1.setShape(k1hat);

                p = RANDOM.nextDouble();
                int k2 = (int) location.block2.getShape();
                double k2hat = k2;
                if( k2 > 1 )
                {
                    if( p < 0.5 )
                    {
                        k2hat = k2;
                        // Do nothing
                    }
                    else if( p < 0.75 )
                    {
                        k2hat = k2+1;
                    }
                    else
                    {
                        k2hat = k2-1;
                    }
                }
                else
                {
                    // If we're already at k=1, then increament 25% of the time
                    if( p < 0.75 )
                    {
                        k2hat = k2;
                    }
                    else
                    {
                        k2hat = k2+1;
                    }
                }
                double ln2 = k2hat / k2;
                proposal.block2.setScale( proposal.block2.getScale() / ln2 );
                proposal.block2.setShape(k2hat);

                qratio = 1.0;
            }

            return new DefaultWeightedValue<NRCMCMCTest.State>( proposal, qratio );
        }

        public State createInitialParameter()
        {
            State initial = new State();
            initial.block1 = new GammaDistribution.PDF( 1.0, 1.0 );
            initial.block2 = new GammaDistribution.PDF( 1.0, 1.0/3.0 );
            initial.tc = 100.0;
            return initial;
        }

        public double computeLogLikelihood(
            State first,
            Iterable<? extends Double> second)
        {

            ArrayList<? extends Double> data = CollectionUtil.asArrayList(second);
            GammaDistribution.PDF gamma;
            final int num = data.size();
            double plog = 0.0;
            for( int n = 1; n < num; n++ )
            {
                final double tn = data.get(n);
                if( tn <= first.tc )
                {
                    gamma = first.block1;
                }
                else
                {
                    gamma = first.block2;
                }

                double delta = tn - data.get(n-1);
                plog += Math.log(gamma.evaluate(delta));
            }

            return plog;
        }

    }


}
