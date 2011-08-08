/*
 * File:                WilcoxonSignedRankConfidence.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright August 20, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.statistics.method;

import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.statistics.distribution.UnivariateGaussian;
import gov.sandia.cognition.util.AbstractCloneableSerializable;
import gov.sandia.cognition.util.ArrayIndexSorter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * This is a Wilcoxon Signed-Rank Sum test, which performs a pair-wise test
 * to determine if two datasets are different.
 *
 * @author Kevin R. Dixon
 * @since  2.0
 *
 */
@ConfidenceTestAssumptions(
    name="Wilcoxon Signed-Rank Sum Test",
    alsoKnownAs="Wilcoxon signed-rank test",
    description={
        "A nonparameteric statistical hypothesis test for the case of two related samples or repeated measurements on a single sample.",
        "Wilcoxon Signed-Rank Sum Test does not assume that the data are generated according to a particular distribution.",
        "Used as an alternative to a paired Student-t test when the data are not assumed to be Gaussian."
    },
    assumptions={
        "The differences between paired samples are independent.",
        "Each difference comes from a continuous population, identical for all differences, and is symmetric about a common median.",
        "The data are oridinal variables such that comparisons such as greater than, less than, and equal to have meaning."
    },
    nullHypothesis="The median difference between the paired samples is 0.0",
    dataPaired=true,
    dataSameSize=true,
    distribution=UnivariateGaussian.CDF.class,
    reference=@PublicationReference(
        author="Wikipedia",
        title="Wilcoxon signed-rank test",
        type=PublicationType.WebPage,
        year=2009,
        url="http://en.wikipedia.org/wiki/Wilcoxon_signed-rank_test"
    )
)
public class WilcoxonSignedRankConfidence
    extends AbstractCloneableSerializable
    implements NullHypothesisEvaluator<Collection<Double>>
{
    
    /**
     * Default instance since the class has no state.
     */
    public static final WilcoxonSignedRankConfidence INSTANCE =
        new WilcoxonSignedRankConfidence();

    /** Creates a new instance of WilcoxonSignedRankConfidence */
    public WilcoxonSignedRankConfidence()
    {
    }
    
    public WilcoxonSignedRankConfidence.Statistic evaluateNullHypothesis(
        Collection<Double> data1,
        Collection<Double> data2)
    {
        
        if( data1.size() != data2.size() )
        {
            throw new IllegalArgumentException(
                "Data1 and data2 must be same size!" );
        }
        
        // Find the differences and then rank them
        int N = data1.size();
        int numNonzero = 0;
        ArrayList<Double> rawDifference = new ArrayList<Double>(N);
        ArrayList<Double> absDifference = new ArrayList<Double>(N);
        Iterator<Double> i1 = data1.iterator();
        Iterator<Double> i2 = data2.iterator();
        while( i1.hasNext() )
        {
            // Only add nonzero differences
            double difference = i1.next() - i2.next();
            if( difference != 0.0 )
            {
                numNonzero++;
                rawDifference.add( difference );
                absDifference.add( Math.abs(difference) );
            }
        }
        
        double[] ranks = WilcoxonSignedRankConfidence.ranks( absDifference );
        double positiveRankSum = 0.0;
        double negativeRankSum = 0.0;
        for( int i = 0; i < numNonzero; i++ )
        {
            if( rawDifference.get(i) > 0.0 )
            {
                positiveRankSum += ranks[i];
            }
            else
            {
                negativeRankSum += ranks[i];
            }
        }
        
        double T = Math.min( positiveRankSum, negativeRankSum );        
        return new WilcoxonSignedRankConfidence.Statistic( T, numNonzero );
        
    }
    

    /**
     * Returns the ranks of the values in ascending order
     * @param values Collection of values to rank
     * @return Ranks of the indices of the values
     */
    public static double[] ranks(
        Collection<Double> values )
    {
        
        // Sort them first
        int N = values.size();
        double[] array = new double[ N ];
        int index = 0;
        for( Double value : values )
        {
            array[index] = value;
            index++;
        }
        int[] sortedIndices = ArrayIndexSorter.sortArrayAscending( array );
        double[] ranks = new double[ N ];
        
        for( int rank = 1; rank <= N; rank++ )
        {
            // See if we have any ties
            if( (rank < N) &&
                (array[sortedIndices[rank-1]] == array[sortedIndices[rank]]) )
            {
                double sum = rank + (rank+1);
                int numEqual = 2;
                for( int j = rank+2; j <= N; j++ )
                {
                    if( array[sortedIndices[rank-1]] == array[sortedIndices[j-1]] )
                    {
                        sum += j;
                        numEqual++;
                    }
                    else
                    {
                        break;
                    }
                }

                // Spread the total rank across the tying ranks
                for( int i = 0; i < numEqual; i++ )
                {
                    ranks[sortedIndices[rank+i-1]] = sum/numEqual;
                }
                rank += numEqual-1;
                
            }
            else
            {
                ranks[sortedIndices[rank-1]] = rank;
            }
            
        }
        
        return ranks;
        
    }
    
    
    /**
     * ConfidenceStatistics associated with a Wilcoxon test
     */
    public static class Statistic
        extends AbstractConfidenceStatistic
    {
        
        /**
         * Wilcoxon T statistic, minimum sign-rank sum
         */
        private double T;
        
        /**
         * Number of nonzero differences in the data
         */
        private int numNonZero;
        
        
        /**
         * Z-statistic for the Gaussian CDF
         */
        private double z;
        
        /**
         * Creates a new instance of Statistic
         * @param T 
         * Wilcoxon T statistic, minimum sign-rank sum
         * @param numNonZero 
         * Number of nonzero differences in the data
         */
        public Statistic(
            double T,
            int numNonZero )
        {
            this( T, numNonZero, computeZ(T,numNonZero) );
        }        
        
        
        /**
         * Creates a new instance of Statistic
         * @param T 
         * Wilcoxon T statistic, minimum sign-rank sum
         * @param numNonZero 
         * Number of nonzero differences in the data
         * @param z 
         * Z-statistic for the Gaussian CDF
         */
        private Statistic(
            double T,
            int numNonZero,
            double z )
        {
            super( computeNullHypothesisProbability( z ) );            
            this.setT( T );
            this.setNumNonZero( numNonZero );
            this.setZ( z );
        }

        
        /**
         * Computes the z-value from the T-statistic and numNonZero value
         * @param T
         * Wilcoxon T statistic, minimum sign-rank sum
         * @param numNonZero 
         * Number of nonzero differences in the data
         * @return 
         * Z-statistic for the Gaussian CDF
         */
        protected static double computeZ(
            double T,
            int numNonZero )
        {
            double numerator = T - numNonZero*(numNonZero+1)/4.0;
            double denominator = Math.sqrt( numNonZero*(numNonZero+1)*(2*numNonZero+1)/24.0 );
            double z = numerator / denominator;
            return z;
        }
        
        
        /**
         * Computes the p-value given the z-value
         * @param z 
         * Z-statistic for the Gaussian CDF
         * @return 
         * p-value for this Wilcoxon Test
         */
        protected static double computeNullHypothesisProbability(
            double z )
        {
            return 2.0*UnivariateGaussian.CDF.evaluate( -Math.abs(z), 0, 1 );
        }
        
        
        /**
         * Getter for T
         * @return 
         * Wilcoxon T statistic, minimum sign-rank sum
         */
        public double getT()
        {
            return this.T;
        }

        /**
         * Getter for T
         * @param T 
         * Wilcoxon T statistic, minimum sign-rank sum
         */
        protected void setT(
            double T)
        {
            this.T = T;
        }

        /**
         * Getter fir numNonZero
         * @return 
         * Number of nonzero differences in the data
         */
        public int getNumNonZero()
        {
            return this.numNonZero;
        }

        /**
         * Setter for numNonZero
         * @param numNonZero 
         * Number of nonzero differences in the data
         */
        protected void setNumNonZero(
            int numNonZero)
        {
            this.numNonZero = numNonZero;
        }

        /**
         * Getter for z
         * @return 
         * Z-statistic for the Gaussian CDF
         */
        public double getZ()
        {
            return this.z;
        }

        /**
         * Setter for z
         * @param z 
         * Z-statistic for the Gaussian CDF
         */
        protected void setZ(
            double z)
        {
            this.z = z;
        }
        
    }
    
}
