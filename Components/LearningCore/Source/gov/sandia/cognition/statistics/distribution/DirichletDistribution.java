/*
 * File:                DirichletDistribution.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Dec 14, 2009, Sandia Corporation.
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
import gov.sandia.cognition.statistics.ClosedFormComputableDistribution;
import gov.sandia.cognition.statistics.ProbabilityDensityFunction;
import gov.sandia.cognition.util.ObjectUtil;
import java.util.ArrayList;
import java.util.Random;

/**
 * The Dirichlet distribution is the multivariate generalization of the beta
 * distribution.  It describes the belief that the probabilities of K
 * mutually exclusive events "x_i" have been observed "a_i -1" times.  The
 * Dirichlet distribution is the conjugate prior of the multinomial
 * distribution.
 * 
 * @author Kevin R. Dixon
 * @since 3.0
 */
@PublicationReference(
    author="Wikipedia",
    title="Dirichlet distribution",
    type=PublicationType.WebPage,
    year=2009,
    url="http://en.wikipedia.org/wiki/Dirichlet_distribution"
)
public class DirichletDistribution
    extends AbstractDistribution<Vector>
    implements ClosedFormComputableDistribution<Vector>
{

    /**
     * Parameters of the Dirichlet distribution, must be at least 2-dimensional
     * and each element must be positive.
     */
    protected Vector parameters;

    /** 
     * Creates a new instance of DirichletDistribution 
     */
    public DirichletDistribution()
    {
        this( 2 );
    }

    /**
     * Creates a new instance of DirichletDistribution
     * @param dimensionality
     * Dimensionality of the distribution
     */
    public DirichletDistribution(
        final int dimensionality )
    {
        this( VectorFactory.getDefault().createVector(dimensionality,1.0) );
    }

    /**
     * Creates a new instance of DirichletDistribution
     * @param parameters
     * Parameters of the Dirichlet distribution, must be at least 2-dimensional
     * and each element must be positive.
     *
     */
    public DirichletDistribution(
        final Vector parameters )
    {
        this.setParameters(parameters);
    }

    /**
     * Copy Constructor.
     * @param other
     * DirichletDistribution to copy.
     */
    public DirichletDistribution(
        final DirichletDistribution other )
    {
        this( ObjectUtil.cloneSafe( other.getParameters() ) );
    }

    @Override
    public DirichletDistribution clone()
    {
        DirichletDistribution clone = (DirichletDistribution) super.clone();
        clone.setParameters( ObjectUtil.cloneSafe(this.getParameters()) );
        return clone;
    }

    @Override
    public Vector getMean()
    {
        return this.parameters.scale( 1.0/this.parameters.norm1() );
    }

    @Override
    public ArrayList<Vector> sample(
        final Random random,
        final int numSamples)
    {

        GammaDistribution.CDF gammaRV = new GammaDistribution.CDF(1.0, 1.0);

        int K = this.getParameters().getDimensionality();
        ArrayList<ArrayList<Double>> gammaData =
            new ArrayList<ArrayList<Double>>(K);
        for( int i = 0; i < K; i++ )
        {
            double ai = this.parameters.getElement(i);
            gammaRV.setShape(ai);
            gammaData.add( gammaRV.sample(random, numSamples) );
        }

        ArrayList<Vector> data = new ArrayList<Vector>( numSamples );
        for( int n = 0; n < numSamples; n++ )
        {
            Vector y = VectorFactory.getDefault().createVector(K);
            double ysum = 0.0;
            for( int i = 0; i < K; i++ )
            {
                double yin = gammaData.get(i).get(n);
                ysum += yin;
                y.setElement(i, yin );
            }
            if( ysum != 0.0 )
            {
                y.scaleEquals(1.0/ysum);
            }
            data.add( y );
        }

        return data;
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
     * Getter for parameters
     * @return
     * Parameters of the Dirichlet distribution, must be at least 2-dimensional
     * and each element must be positive.
     */
    public Vector getParameters()
    {
        return this.parameters;
    }

    /**
     * Setter for parameters
     * @param parameters
     * Parameters of the Dirichlet distribution, must be at least 2-dimensional
     * and each element must be positive.
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
            if( parameters.getElement(i) <= 0.0 )
            {
                throw new IllegalArgumentException(
                    "All parameter elements must be > 0.0" );
            }
        }

        this.parameters = parameters;
    }

    @Override
    public DirichletDistribution.PDF getProbabilityFunction()
    {
        return new DirichletDistribution.PDF( this );
    }

    /**
     * PDF of the Dirichlet distribution.
     */
    public static class PDF
        extends DirichletDistribution
        implements ProbabilityDensityFunction<Vector>,
        VectorInputEvaluator<Vector,Double>
    {

        /**
         * Default constructor.
         */
        public PDF()
        {
            super();
        }

        /**
         * Creates a new instance of PDF
         * @param parameters
         * Parameters of the Dirichlet distribution, must be at least 2-dimensional
         * and each element must be positive.
         */
        public PDF(
            final Vector parameters )
        {
            super( parameters );
        }

        /**
         * Copy Constructor.
         * @param other
         * DirichletDistribution to copy.
         */
        public PDF(
            final DirichletDistribution other )
        {
            super( other );
        }

        /**
         * Evaluates the Dirichlet PDF about the given input.  Note that we
         * normalize the given input by its L1 norm to ensure that its entries
         * sum to 1.
         * @param input
         * Input to consider, automatically normalized by its L1 norm without
         * side-effect.
         * @return
         * Dirichlet PDF evaluated about the given (unnormalized) input.
         */
        @Override
        public Double evaluate(
            final Vector input)
        {
            Vector xn = input.scale( 1.0 / input.norm1() );

            Vector a = this.getParameters();
            input.assertSameDimensionality( a );

            double logsum = 0.0;
            final int K = a.getDimensionality();
            for( int i = 0; i < K; i++ )
            {
                double xi = xn.getElement(i);
                if( (xi <= 0.0) || (1.0 <= xi) )
                {
                    throw new IllegalArgumentException(
                        "Expected all inputs to be (0.0,infinity): " + input );
                }
                double ai = a.getElement(i);
                logsum += (ai-1.0) * Math.log( xi );
            }
            logsum -= MathUtil.logMultinomialBetaFunction( a );
            
            return Math.exp(logsum);
        }

        @Override
        public double logEvaluate(
            final Vector input)
        {
            Vector xn = input.scale( 1.0 / input.norm1() );

            Vector a = this.getParameters();
            input.assertSameDimensionality( a );

            double logsum = 0.0;
            final int K = a.getDimensionality();
            for( int i = 0; i < K; i++ )
            {
                double xi = xn.getElement(i);
                if( (xi <= 0.0) || (1.0 <= xi) )
                {
                    throw new IllegalArgumentException(
                        "Expected all inputs to be (0.0,infinity): " + input );
                }
                double ai = a.getElement(i);
                logsum += (ai-1.0) * Math.log( xi );
            }

            logsum -= MathUtil.logMultinomialBetaFunction( a );
            return logsum;
        }

        @Override
        public int getInputDimensionality()
        {
            return (this.parameters != null) ? this.parameters.getDimensionality() : 0;
        }

        @Override
        public DirichletDistribution.PDF getProbabilityFunction()
        {
            return this;
        }

    }

}
