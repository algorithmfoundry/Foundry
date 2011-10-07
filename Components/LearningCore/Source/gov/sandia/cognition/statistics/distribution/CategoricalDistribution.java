/*
 * File:                CategoricalDistribution.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright May 3, 2011, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.statistics.distribution;

import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.collection.CollectionUtil;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.math.matrix.VectorInputEvaluator;
import gov.sandia.cognition.statistics.AbstractDistribution;
import gov.sandia.cognition.statistics.ClosedFormComputableDiscreteDistribution;
import gov.sandia.cognition.statistics.ProbabilityMassFunction;
import gov.sandia.cognition.statistics.ProbabilityMassFunctionUtil;
import gov.sandia.cognition.util.ObjectUtil;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Random;
import java.util.Set;

/**
 * The Categorical Distribution is the multivariate generalization of the
 * Bernoulli distribution, where the outcome of an experiment is a one-of-N
 * output, where the output is a selector Vector.  This Vector will have all
 * zeros except one index will have a 1.0.
 * @author Kevin R. Dixon
 * @since 3.3.0
 */
@PublicationReference(
    author="Wikipedia",
    title="Categoical Distribution",
    type=PublicationType.WebPage,
    year=2011,
    url="http://en.wikipedia.org/wiki/Categorical_distribution"
)
public class CategoricalDistribution 
    extends AbstractDistribution<Vector>
    implements ClosedFormComputableDiscreteDistribution<Vector>
{

    /**
     * Default number of classes (labels or parameters), {@value}.
     */
    public static final int DEFAULT_NUM_CLASSES = 2;


    /**
     * Parameters of the multinomial distribution, must be at least
     * 2-dimensional and each element must be nonnegative.
     */
    protected Vector parameters;

    /**
     * Creates a new instance of CategoricalDistribution
     */
    public CategoricalDistribution()
    {
        this( DEFAULT_NUM_CLASSES );
    }

    /**
     * Creates a new instance of CategoricalDistribution
     * @param numClasses
     * Number of classes (labels or parameters) to use.
     */
    public CategoricalDistribution(
        final int numClasses )
    {
        this( VectorFactory.getDefault().createVector(numClasses, 1.0) );
    }

    /**
     * Creates a new instance of CategoricalDistribution
     * @param parameters
     * Parameters of the multinomial distribution, must be at least
     * 2-dimensional and each element must be nonnegative.
     */
    public CategoricalDistribution(
        final Vector parameters )
    {
        this.setParameters(parameters);
    }

    /**
     * Copy constructor
     * @param other
     * CategoricalDistribution to copy
     */
    public CategoricalDistribution(
        CategoricalDistribution other )
    {
        this( ObjectUtil.cloneSafe(other.getParameters()) );
    }

    @Override
    public CategoricalDistribution clone()
    {
        CategoricalDistribution clone = (CategoricalDistribution) super.clone();
        clone.setParameters( ObjectUtil.cloneSafe( this.getParameters() ) );
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
    public ArrayList<Vector> sample(
        final Random random,
        final int numSamples)
    {
        ArrayList<Vector> domain = CollectionUtil.asArrayList(this.getDomain());
        final int N = domain.size();

        double[] cumulativeWeights = new double[N];
        double sum = 0.0;
        for( int n = 0; n < N; n++ )
        {
            double weight = this.parameters.getElement(n);
            sum += weight;
            cumulativeWeights[n] = sum;
        }

        return ProbabilityMassFunctionUtil.sampleMultiple(
            cumulativeWeights, sum, domain, random, numSamples);
    }

    @Override
    public Vector getMean()
    {
        return this.parameters.scale( this.parameters.norm1() );
    }

    @Override
    public Vector convertToVector()
    {
        return this.parameters.clone();
    }

    @Override
    public void convertFromVector(
        final Vector parameters)
    {
        this.parameters.assertSameDimensionality(parameters);
        this.setParameters(parameters);
    }

    /**
     * Gets the dimensionality of the input vectors
     * @return
     * Dimensionality of the input vectors
     */
    public int getInputDimensionality()
    {
        return this.getParameters().getDimensionality();
    }

    @Override
    public Set<Vector> getDomain()
    {
        final int N = this.getInputDimensionality();
        LinkedHashSet<Vector> domain = new LinkedHashSet<Vector>( N );
        for( int n = 0; n < N; n++ )
        {
            Vector x = VectorFactory.getDefault().createVector(N);
            x.setElement(n, 1.0);
            domain.add( x );
        }
        return domain;
    }

    @Override
    public int getDomainSize()
    {
        return this.getInputDimensionality();
    }

    @Override
    public CategoricalDistribution.PMF getProbabilityFunction()
    {
        return new CategoricalDistribution.PMF( this );
    }

    /**
     * PMF of the Categorical Distribution
     */
    public static class PMF
        extends CategoricalDistribution
        implements ProbabilityMassFunction<Vector>,
        VectorInputEvaluator<Vector, Double>
    {

        /**
         * Creates a new instance of CategoricalDistribution
         */
        public PMF()
        {
            super();
        }

        /**
         * Creates a new instance of CategoricalDistribution
         * @param numClasses
         * Number of classes (labels or parameters) to use.
         */
        public PMF(
            final int numClasses )
        {
            super( numClasses );
        }

        /**
         * Creates a new instance of CategoricalDistribution
         * @param parameters
         * Parameters of the multinomial distribution, must be at least
         * 2-dimensional and each element must be nonnegative.
         */
        public PMF(
            final Vector parameters )
        {
            super( parameters );
        }

        /**
         * Copy constructor
         * @param other
         * CategoricalDistribution to copy
         */
        public PMF(
            CategoricalDistribution other )
        {
            super( other );
        }

        @Override
        public double getEntropy()
        {
            return ProbabilityMassFunctionUtil.getEntropy(this);
        }

        @Override
        public double logEvaluate(
            Vector input)
        {
            return Math.log( this.evaluate(input) );
        }

        @Override
        public Double evaluate(
            Vector input)
        {
            this.parameters.assertSameDimensionality(input);
            double pi = -1.0;
            final int N = this.getInputDimensionality();
            double sum = 0.0;
            for( int n = 0; n < N; n++ )
            {
                double p = this.parameters.getElement(n);
                sum += p;
                double x = input.getElement(n);
                if( x == 1.0 )
                {
                    if( pi < 0.0 )
                    {
                        pi = p;
                    }
                    else
                    {
                        throw new IllegalArgumentException(
                            "input must only have one entry equal to 1.0!");
                    }
                }
                else if( x != 0.0 )
                {
                    throw new IllegalArgumentException(
                        "input entries must be either 0.0 or 1.0" );
                }
            }

            if( pi < 0.0 )
            {
                throw new IllegalArgumentException(
                    "input must have one entry equal to 1.0!" );
            }

            return pi/sum;
        }

        @Override
        public CategoricalDistribution.PMF getProbabilityFunction()
        {
            return this;
        }

    }

}
