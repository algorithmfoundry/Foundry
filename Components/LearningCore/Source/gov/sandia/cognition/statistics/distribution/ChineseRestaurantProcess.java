/*
 * File:                ChineseRestaurantProcess.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Feb 9, 2010, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.statistics.distribution;

import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationReferences;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.math.MathUtil;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.statistics.ClosedFormComputableDiscreteDistribution;
import gov.sandia.cognition.statistics.ProbabilityMassFunction;
import gov.sandia.cognition.statistics.ProbabilityMassFunctionUtil;
import gov.sandia.cognition.util.AbstractCloneableSerializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Random;
import java.util.Set;

/**
 * A Chinese Restaurant Process is a discrete stochastic processes that
 * partitions data points to clusters.  This is done by imagining a
 * restaurant with an infinite number of tables.  The first customer sits at
 * an empty table.  The next customer picks an existing table proportionate to
 * how many customers are already sitting at the various tables, and a new
 * table with some nonzero probability.  This results in a Dirichlet
 * distribution with a variable number of parameters, which grows approximately
 * as O(log(n)), where n is the number of customers to assign to tables.
 * @author Kevin R. Dixon
 * @since 3.0
 */
@PublicationReferences(
    references={
        @PublicationReference(
            author="Michael I. Jordan",
            title="Dirichlet Processes, Chinese Restaurant Processes and All That",
            year=2005,
            type=PublicationType.Conference,
            publication="NIPS Tutorial",
            url="http://www.cs.berkeley.edu/~jordan/nips-tutorial05.ps"
        )
        ,
        @PublicationReference(
            author="Wikipedia",
            title="http://en.wikipedia.org/wiki/Chinese_restaurant_process",
            year=2010,
            type=PublicationType.WebPage,
            url="http://en.wikipedia.org/wiki/Chinese_restaurant_process",
            notes="Very poor, unclear description."
        )
    }
)
public class ChineseRestaurantProcess 
    extends AbstractCloneableSerializable
    implements ClosedFormComputableDiscreteDistribution<Vector>
{

    /**
     * Default concentration parameter, {@value}.
     */
    public static final double DEFAULT_ALPHA = 1.0;

    /**
     * Default number of customers, {@value}.
     */
    public static final int DEFAULT_NUM_CUSTOMERS = 2;

    /**
     * CRP concentration parameter, must be greater than zero.
     */
    protected double alpha;

    /**
     * Total number of customers that we will arrange around tables,
     * must be greater than zero.
     */
    protected int numCustomers;

    /** 
     * Creates a new instance of ChineseRestaurantProcess 
     */
    public ChineseRestaurantProcess()
    {
        this( DEFAULT_ALPHA, DEFAULT_NUM_CUSTOMERS );
    }

    /**
     * Creates a new instance of ChineseRestaurantProcess
     * @param alpha
     * CRP concentration parameter, must be greater than zero.
     * @param numCustomers
     * Total number of customers that we will arrange around tables,
     * must be greater than zero.
     */
    public ChineseRestaurantProcess(
        final double alpha,
        final int numCustomers)
    {
        this.setAlpha(alpha);
        this.setNumCustomers(numCustomers);
    }

    /**
     * Default constructor
     * @param other
     * CRP to copy
     */
    public ChineseRestaurantProcess(
        final ChineseRestaurantProcess other )
    {
        this( other.getAlpha(), other.getNumCustomers() );
    }

    @Override
    public ChineseRestaurantProcess clone()
    {
        return (ChineseRestaurantProcess) super.clone();
    }

    @Override
    public Vector getMean()
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Vector sample(
        final Random random)
    {

        ArrayList<Integer> tables = new ArrayList<Integer>( this.numCustomers );
        for( int n = 0; n < this.getNumCustomers(); n++ )
        {
            final int tableIndex =
                sampleNextCustomer(tables, n, this.alpha, random);
            if( tableIndex >= tables.size() )
            {
                // Add an empty table to the Restaurant
                tables.add( 0 );
            }

            // Increment the number of customers at the indexed table
            int nc = tables.get(tableIndex) + 1;
            tables.set(tableIndex, nc);
        }

        Vector parameters = VectorFactory.getDefault().copyValues(tables);
        return parameters;

    }

    /**
     * Determines where the next customer sits, given the number of customers
     * already sitting at the various tables and the concentration parameter
     * alpha.
     * @param tables
     * Number of customers sitting at the various tables.
     * @param numCustomers
     * Number of customers already sitting, should equal the sum
     * of "tables".
     * @param alpha
     * Concentration parameter.
     * @param random
     * Random number generator.
     * @return
     * Index of the table where the next customer sits, according to the
     * Chinese Restaurant Process.
     */
    public static int sampleNextCustomer(
        final Collection<Integer> tables,
        final int numCustomers,
        final double alpha,
        final Random random )
    {
        double p = random.nextDouble();
        double pnew = alpha / (numCustomers + alpha);
        p -= pnew;
        if( p <= 0.0 )
        {
            return tables.size();
        }

        int tableIndex = 0;
        for( Integer customersAtTable : tables )
        {
            final double tableProb = customersAtTable / (numCustomers + alpha);
            p -= tableProb;
            if( p <= 0.0 )
            {
                return tableIndex;
            }
            tableIndex++;
        }

        throw new IllegalArgumentException(
            "Bad computation in sampleNextcustomer!!!" );
    }

    @Override
    public ArrayList<Vector> sample(
        final Random random,
        final int numSamples)
    {
        ArrayList<Vector> samples = new ArrayList<Vector>( numSamples );
        for( int n = 0; n < numSamples; n++ )
        {
            samples.add( this.sample(random) );
        }
        return samples;
    }

    /**
     * Getter for alpha.
     * @return
     * CRP concentration parameter, must be greater than zero.
     */
    public double getAlpha()
    {
        return this.alpha;
    }

    /**
     * Setter for alpha.
     * @param alpha
     * CRP concentration parameter, must be greater than zero.
     */
    public void setAlpha(
        final double alpha)
    {
        if( alpha <= 0.0 )
        {
            throw new IllegalArgumentException( "Alpha must be > 0.0" );
        }
        this.alpha = alpha;
    }

    /**
     * Getter for numCustomers
     * @return
     * Total number of customers that we will arrange around tables,
     * must be greater than zero.
     */
    public int getNumCustomers()
    {
        return this.numCustomers;
    }

    /**
     * Setter for numCustomers
     * @param numCustomers
     * Total number of customers that we will arrange around tables,
     * must be greater than zero.
     */
    public void setNumCustomers(
        final int numCustomers)
    {
        if( numCustomers <= 0 )
        {
            throw new IllegalArgumentException(
                "numCustomers must be > 0" );
        }
        this.numCustomers = numCustomers;
    }

    @Override
    public Set<Vector> getDomain()
    {
        LinkedHashSet<Vector> domain = new LinkedHashSet<Vector>(
            this.getNumCustomers()*this.getNumCustomers() );
        for( int i = 1; i <= this.getNumCustomers(); i++ )
        {
            domain.addAll( new MultinomialDistribution.Domain( i, this.getNumCustomers() ) );
        }
        return domain;
    }

    @Override
    public int getDomainSize()
    {
        return this.getDomain().size();
    }

    @Override
    public ChineseRestaurantProcess.PMF getProbabilityFunction()
    {
        return new ChineseRestaurantProcess.PMF( this );
    }

    @Override
    public Vector convertToVector()
    {
        return VectorFactory.getDefault().copyValues(
            this.getAlpha(), this.getNumCustomers() );
    }

    @Override
    public void convertFromVector(
        final Vector parameters)
    {
        parameters.assertDimensionalityEquals(2);
        this.setAlpha( parameters.getElement(0) );
        this.setNumCustomers( (int) Math.round( parameters.getElement(1) ) );
    }

    /**
     * PMF of the Chinese Restaurant Process
     */
    public static class PMF
        extends ChineseRestaurantProcess
        implements ProbabilityMassFunction<Vector>
    {

        /**
         * Creates a new instance of ChineseRestaurantProcess
         */
        public PMF()
        {
            super();
        }

        /**
         * Creates a new instance of ChineseRestaurantProcess
         * @param alpha
         * CRP concentration parameter, must be greater than zero.
         * @param numCustomers
         * Total number of customers that we will arrange around tables,
         * must be greater than zero.
         */
        public PMF(
            final double alpha,
            final int numCustomers)
        {
            super( alpha, numCustomers );
        }

        /**
         * Copy constructor
         * @param other
         * CRP to copy
         */
        public PMF(
            final ChineseRestaurantProcess other )
        {
            super( other );
        }

        @Override
        public ChineseRestaurantProcess.PMF getProbabilityFunction()
        {
            return this;
        }

        @Override
        public double getEntropy()
        {
            return ProbabilityMassFunctionUtil.getEntropy(this);
        }

        @Override
        public double logEvaluate(
            final Vector input)
        {
            final int numTables = input.getDimensionality();
            double logSum = numTables * Math.log( this.alpha );
            int totalCustomers = 0;
            for( int table = 0; table < numTables; table++ )
            {
                // We must have at least 1 customer at each table in the CRP.
                final double value = input.getElement(table);
                if( value < 1.0 ||
                    value > this.numCustomers )
                {
                    return Math.log(0.0);
                }

                double floor = Math.floor(value);
                double ceil = Math.ceil(value);
                if( floor != ceil )
                {
                    throw new IllegalArgumentException(
                        "Customers at each table must be an integer: " + input );
                }
                // Posterior time: 31.828
                // Posterior time: 39.563
                final int customersAtTable = (int) floor;
                logSum += MathUtil.logFactorial(customersAtTable-1);
                totalCustomers += customersAtTable;
            }

            logSum += MathUtil.logGammaFunction( this.alpha );
            logSum -= MathUtil.logGammaFunction( this.alpha + totalCustomers );

            return logSum;
        }

        @Override
        public Double evaluate(
            final Vector input)
        {
            return Math.exp( this.logEvaluate(input) );
        }
        
    }
    
}
