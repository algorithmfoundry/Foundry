/*
 * File:                SufficientStatisticTestFramework.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Apr 21, 2011, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.statistics;

import java.util.Random;

/**
 * Test Framework for SufficientStatistic
 * @param <DataType> DataType
 * @param <DistributionType> DistributionType
 * @author Kevin R. Dixon
 * @since 3.1
 */
public class SufficientStatisticTestFramework<DataType,DistributionType>
{

    /**
     * Random-number generator to use
     */
    public Random RANDOM = new Random( 1 );

    /**
     * Default tolerance
     */
    protected static double TOLERANCE = 1e-5;

    /**
     * Number of samples
     */
    public static int NUM_SAMPLES = 100;


    /**
     * harness
     */
    SufficientStatisticTestFramework.Harness<DataType,DistributionType> harness;

    /** 
     * Creates a new instance of SufficientStatisticTestFramework
     * @param harness Harness
     */
    public SufficientStatisticTestFramework(
        SufficientStatisticTestFramework.Harness<DataType,DistributionType> harness )
    {
        this.harness = harness;
    }


    

    /**
     * Interface for testing
     * @param <DataType> DataType
     * @param <DistributionType> DistributionType
     */
    public static interface Harness<DataType, DistributionType>
    {

        /**
         * Creates a new SufficientStatistic
         * @return
         * SufficientStatistic
         */
        public SufficientStatistic<DataType, DistributionType> createSufficientStatistic();

    }
}
