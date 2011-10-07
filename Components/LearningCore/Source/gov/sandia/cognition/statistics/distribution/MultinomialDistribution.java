/*
 * File:                MultinomialDistribution.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Dec 16, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.statistics.distribution;

import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.math.MathUtil;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.math.matrix.VectorInputEvaluator;
import gov.sandia.cognition.statistics.AbstractDistribution;
import gov.sandia.cognition.statistics.ClosedFormComputableDiscreteDistribution;
import gov.sandia.cognition.statistics.ProbabilityMassFunction;
import gov.sandia.cognition.statistics.ProbabilityMassFunctionUtil;
import gov.sandia.cognition.util.AbstractCloneableSerializable;
import gov.sandia.cognition.util.ObjectUtil;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

/**
 * A multinomial distribution is the multivariate/multiclass generalization
 * of the Binomial distribution.  That is, the PMF returns the probability of
 * from "numTrials" trials of selecting from "numClasses" classes with
 * the probabilities p1,p2,...p_numClasses.  In our class, the probability
 * parameters must be positive but do not have to sum to one because we
 * normalize by the L1 norm.
 * @author Kevin R. Dixon
 * @since 3.0
 */
@PublicationReference(
    author="Wikipedia",
    title="Multinomial distribution",
    type=PublicationType.WebPage,
    year=2009,
    url="http://en.wikipedia.org/wiki/Multinomial_distribution"
)
public class MultinomialDistribution 
    extends AbstractDistribution<Vector>
    implements ClosedFormComputableDiscreteDistribution<Vector>
{

    /**
     * Default number of classes (labels or parameters), {@value}.
     */
    public static final int DEFAULT_NUM_CLASSES = 2;

    /**
     * Default number of trials, {@value}.
     */
    public static final int DEFAULT_NUM_TRIALS = 1;

    /**
     * Number of trials in the distribution, must be greater than 0.
     */
    private int numTrials;

    /**
     * Parameters of the multinomial distribution, must be at least
     * 2-dimensional and each element must be nonnegative.
     */
    private Vector parameters;

    /** 
     * Creates a new instance of MultinomialDistribution 
     */
    public MultinomialDistribution()
    {
        this( DEFAULT_NUM_CLASSES, DEFAULT_NUM_TRIALS );
    }

    /**
     * Creates a new instance of MultinomialDistribution
     * @param numClasses
     * Number of classes (labels or parameters) to use.
     * @param numTrials
     * Number of trials in the distribution, must be greater than 0.
     */
    public MultinomialDistribution(
        final int numClasses,
        final int numTrials )
    {
        this( VectorFactory.getDefault().createVector(numClasses, 1.0),
            numTrials );
    }

    /**
     * Creates a new instance of MultinomialDistribution
     * @param numTrials
     * Number of trials in the distribution, must be greater than 0.
     * @param parameters
     * Parameters of the multinomial distribution, must be at least
     * 2-dimensional and each element must be nonnegative.
     */
    public MultinomialDistribution(
        final Vector parameters,
        final int numTrials )
    {
        this.setNumTrials(numTrials);
        this.setParameters(parameters);
    }

    /**
     * Copy constructor
     * @param other
     * MultinomialDistribution to copy
     */
    public MultinomialDistribution(
        final MultinomialDistribution other )
    {
        this( ObjectUtil.cloneSafe(other.getParameters()), other.getNumTrials() );
    }

    @Override
    public MultinomialDistribution clone()
    {
        MultinomialDistribution clone = (MultinomialDistribution) super.clone();
        clone.setParameters( ObjectUtil.cloneSafe(this.getParameters()) );
        return clone;
    }

    /**
     * Getter for parameters
     * @return
     * Parameters of the multinomial distribution, must be at least
     * 2-dimensional and each element must be nonnegative.
     */
    public Vector getParameters()
    {
        return this.parameters;
    }

    /**
     * Setter for parameters
     * @param parameters
     * Parameters of the multinomial distribution, must be at least
     * 2-dimensional and each element must be nonnegative.
     */
    public void setParameters(
        final Vector parameters)
    {

        final int N = parameters.getDimensionality();

        if( N < 2 )
        {
            throw new IllegalArgumentException( "Dimensionality must be >= 2" );
        }

        for( int i = 0; i < N; i++ )
        {
            if( parameters.getElement(i) < 0.0 )
            {
                throw new IllegalArgumentException(
                    "All parameter elements must be >= 0.0" );
            }
        }

        this.parameters = parameters;
    }

    @Override
    public Vector convertToVector()
    {
        return ObjectUtil.cloneSafe(this.getParameters());
    }

    @Override
    public void convertFromVector(
        final Vector parameters)
    {
        parameters.assertSameDimensionality( this.getParameters() );
        this.setParameters( ObjectUtil.cloneSafe(parameters) );
    }

    /**
     * Getter for numTrials
     * @return
     * Number of trials in the distribution, must be greater than 0.
     */
    public int getNumTrials()
    {
        return this.numTrials;
    }

    /**
     * Setter for numTrials
     * @param numTrials
     * Number of trials in the distribution, must be greater than 0.
     */
    public void setNumTrials(
        final int numTrials)
    {
        if( numTrials <= 0 )
        {
            throw new IllegalArgumentException( "numTrials must be > 0" );
        }
        this.numTrials = numTrials;
    }

    @Override
    public Vector getMean()
    {
        return this.parameters.scale( this.numTrials/this.parameters.norm1() );
    }

    @Override
    public ArrayList<Vector> sample(
        final Random random,
        final int numSamples)
    {

        final int numClasses = this.parameters.getDimensionality();
        final double []probs = new double[ numClasses ];
        final double probsum = this.parameters.norm1();
        for( int j = 0; j < numClasses; j++ )
        {
            probs[j] = this.parameters.getElement(j) / probsum;
        }

        ArrayList<Vector> samples = new ArrayList<Vector>( numSamples );
        for( int n = 0; n < numSamples; n++ )
        {
            double[] successes = new double[ numClasses ];
            for( int i = 0; i < this.numTrials; i++ )
            {
                double p = random.nextDouble();
                for( int k = 0; k < numClasses; k++ )
                {
                    if( p <= probs[k] )
                    {
                        successes[k]++;
                        break;
                    }
                    p -= probs[k];
                }
            }
            samples.add( VectorFactory.getDefault().copyArray(successes) );
        }

        return samples;
    }

    @Override
    public MultinomialDistribution.Domain getDomain()
    {
        return new Domain(
            this.getParameters().getDimensionality(), this.getNumTrials() );
    }

    @Override
    public int getDomainSize()
    {
        return this.getDomain().size();
    }

    @Override
    public MultinomialDistribution.PMF getProbabilityFunction()
    {
        return new MultinomialDistribution.PMF( this );
    }

    /**
     * Probability Mass Function of the Multinomial Distribution.
     */
    public static class PMF
        extends MultinomialDistribution
        implements ProbabilityMassFunction<Vector>,
        VectorInputEvaluator<Vector,Double>
    {

        /**
         * Creates a new instance of MultinomialDistribution.PMF
         */
        public PMF()
        {
            super();
        }

        /**
         * Creates a new instance of MultinomialDistribution.PMF
         * @param numClasses
         * Number of classes (labels or parameters) to use.
         * @param numTrials
         * Number of trials in the distribution, must be greater than 0.
         */
        public PMF(
            final int numClasses,
            final int numTrials )
        {
            super( numClasses, numTrials );
        }

        /**
         * Creates a new instance of MultinomialDistribution.PMF
         * @param numTrials
         * Number of trials in the distribution, must be greater than 0.
         * @param parameters
         * Parameters of the multinomial distribution, must be at least
         * 2-dimensional and each element must be nonnegative.
         */
        public PMF(
            final Vector parameters,
            final int numTrials )
        {
            super( parameters, numTrials );
        }

        /**
         * Copy constructor
         * @param other
         * MultinomialDistribution to copy
         */
        public PMF(
            MultinomialDistribution other )
        {
            super( other );
        }


        @Override
        public int getInputDimensionality()
        {
            return this.getParameters().getDimensionality();
        }

        @Override
        public Double evaluate(
            final Vector input)
        {
            return Math.exp( this.logEvaluate(input) );
        }

        @Override
        public double logEvaluate(
            final Vector input)
        {

            final int N = this.getInputDimensionality();
            input.assertDimensionalityEquals( N );

            Vector p = this.getParameters();
            double psum = p.norm1();
            double logCoeff = MathUtil.logFactorial( this.getNumTrials() );
            double logProb = 0.0;
            int total = 0;
            for( int i = 0; i < N; i++ )
            {
                int xi = (int) input.getElement(i);
                total += xi;
                double pi = p.getElement(i) / psum;
                if( pi < 0.0 )
                {
                    throw new IllegalArgumentException( "pi < 0.0" + p );
                }
                else if( pi == 0.0 )
                {
                    // if we've got 0 probability and nonzero successes,
                    // then this is impossible: probability == 0.0
                    if( xi != 0 )
                    {
                        return Math.log(0.0);
                    }
                }
                else
                {
                    if( xi != 0 )
                    {
                        logCoeff -= MathUtil.logFactorial(xi);
                        logProb += xi * Math.log(pi);
                    }
                }
            }

            if( total != this.getNumTrials() )
            {
                throw new IllegalArgumentException(
                    "Integer input sum != num trials" );
            }

            return logCoeff + logProb;
        }

        @Override
        public double getEntropy()
        {
            return ProbabilityMassFunctionUtil.getEntropy(this);
        }

        @Override
        public MultinomialDistribution.PMF getProbabilityFunction()
        {
            return this;
        }

    }

    /**
     * Allows the iteration through the set of subsets.  That is, how many ways
     * can we label "numTrials" objects with "numClasses" labels.  BE CAREFUL,
     * before iterating through all members of a Domain, as this
     * grows as a factorial!!!  (See the size() method for the exact size.)
     */
    public static class Domain
        extends AbstractSet<Vector>
    {

        /**
         * Number of classes to sample from.
         */
        private int numClasses;

        /**
         * Number of times to sample from the classes.
         */
        private int numTrials;

        /**
         * Creates a new instance of Domain
         * @param numClasses
         * Number of classes to sample from.
         * @param numTrials
         * Number of times to sample from the classes.
         */
        public Domain(
            final int numClasses,
            final int numTrials )
        {
            this.numClasses = numClasses;
            this.numTrials = numTrials;
        }

        @Override
        public Iterator<Vector> iterator()
        {
            return new MultinomialIterator(this.numClasses,this.numTrials);
        }

        @Override
        public int size()
        {
            return MathUtil.binomialCoefficient(
                this.numClasses+this.numTrials-1,this.numClasses-1 );
        }

        /**
         * The log of the size.
         *
         * @return
         *      The log of the size.
         */
        public double logSize()
        {
            return MathUtil.logBinomialCoefficient(
                this.numClasses+this.numTrials-1,this.numClasses-1 );
        }

        /**
         * An Iterator over a Domain
         */
        protected static class MultinomialIterator
            extends AbstractCloneableSerializable
            implements Iterator<Vector>
        {

            /**
             * Value at the head of the subset.
             */
            private int value;

            /**
             * Number of classes to sample from.
             */
            private int numClasses;

            /**
             * Number of times to sample from the classes.
             */
            private int numTrials;

            /**
             * Child iterator for recursion.
             */
            private MultinomialIterator child;

            /**
             * Creates a new instance of Domain
             * @param numClasses
             * Number of classes to sample from.
             * @param numTrials
             * Number of times to sample from the classes.
             */
            public MultinomialIterator(
                final int numClasses,
                final int numTrials )
            {
                if( numClasses <= 0 )
                {
                    throw new IllegalArgumentException( "NumClasses <= 0" );
                }
                this.numClasses = numClasses;

                if( numTrials < 0 )
                {
                    throw new IllegalArgumentException( "numTrials < 0" );
                }
                this.numTrials = numTrials;

                if( this.numClasses <= 1 )
                {
                    this.value = this.numTrials;
                }
                else
                {
                    this.value = 0;
                }

                if( this.numClasses > 1 )
                {
                    this.child = new MultinomialIterator(this.numClasses-1, this.numTrials-this.value );
                }

            }

            @Override
            public boolean hasNext()
            {

                if( this.value < this.numTrials )
                {
                    return true;
                }
                else if( this.child == null )
                {
                    return this.value <= this.numTrials;
                }
                else
                {
                    return this.child.hasNext();
                }

            }

            @Override
            public Vector next()
            {

                if( this.child == null )
                {
                    Vector subset = VectorFactory.getDefault().createVector(1,this.value);
                    this.value++;
                    return subset;
                }
                else
                {

                    if( !this.child.hasNext() )
                    {
                        this.value++;
                        this.child = new MultinomialIterator(numClasses-1, this.numTrials-this.value);
                    }

                    return VectorFactory.getDefault().createVector(1,this.value).stack( this.child.next() );

                }

            }

            @Override
            public void remove()
            {
                throw new UnsupportedOperationException(
                    "Cannot remove from MultinomialDomain" );
            }

        }

    }
    
}
