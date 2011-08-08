/*
 * File:                MultipleComparisonExperiment.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Jun 2, 2011, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.statistics.method;

import gov.sandia.cognition.util.AbstractCloneableSerializable;
import gov.sandia.cognition.util.ObjectUtil;
import java.util.Arrays;
import java.util.Collection;

/**
 * A multiple comparisons experiment that does a block comparison and then a
 * post-hoc test.
 * 
 * @author Kevin R. Dixon
 * @since 3.3.0
 */
public class MultipleComparisonExperiment
    extends AbstractCloneableSerializable
    implements BlockExperimentComparison<Number>,
    MultipleHypothesisComparison<Collection<? extends Number>>
{

    /**
     * Default block-experiment comparison, FriedmanConfidence.
     */
    public static final BlockExperimentComparison<Number> DEFAULT_BLOCK_EXPERIMENT_COMPARISON =
        FriedmanConfidence.INSTANCE;

    /**
     * Default post-host multiple hypothesis comparison test, NemenyiConfidence.
     */
    public static final MultipleHypothesisComparison<Collection<? extends Number>> DEFAULT_POST_HOC_TEST =
        NemenyiConfidence.INSTANCE;

    /**
     * Default alpha (p-value threshold), {@value}.
     */
    public static final double DEFAULT_ALPHA = 0.05;

    /**
     * Block-experiment comparison test that determines if there is a
     * statistically significant difference in one of the treatments
     */
    private BlockExperimentComparison<Number> blockExperimentComparison;

    /**
     * Post-hoc test that determines which treatments are statistically
     * significantly different
     */
    private MultipleHypothesisComparison<Collection<? extends Number>> postHocTest;

    /**
     * P-value threshold, such that if the null-hypothesis probability is below
     * alpha then we reject the null hypothesis.
     */
    private double alpha;

    /** 
     * Creates a new instance of MultipleComparisonExperiment 
     */
    public MultipleComparisonExperiment()
    {
        this( DEFAULT_BLOCK_EXPERIMENT_COMPARISON, DEFAULT_POST_HOC_TEST, DEFAULT_ALPHA );
    }

    /**
     * Creates a new instance of MultipleComparisonExperiment
     * @param blockExperimentComparison
     * Block-experiment comparison test that determines if there is a
     * statistically significant difference in one of the treatments
     * @param postHocTest
     * Post-hoc test that determines which treatments are statistically
     * significantly different
     * @param alpha
     * P-value threshold, such that if the null-hypothesis probability is below
     * alpha then we reject the null hypothesis.
     */
    public MultipleComparisonExperiment(
        final BlockExperimentComparison<Number> blockExperimentComparison,
        final MultipleHypothesisComparison<Collection<? extends Number>> postHocTest,
        final double alpha)
    {
        this.setBlockExperimentComparison(blockExperimentComparison);
        this.setPostHocTest(postHocTest);
        this.setAlpha(alpha);
    }



    @Override
    public MultipleComparisonExperiment clone()
    {
        MultipleComparisonExperiment clone =
            (MultipleComparisonExperiment) super.clone();
        clone.setBlockExperimentComparison( ObjectUtil.cloneSafe(
            this.getBlockExperimentComparison() ) );
        clone.setPostHocTest( ObjectUtil.cloneSafe(
            this.getPostHocTest() ) );
        return clone;
    }

    /**
     * Getter for blockExperimentComparison
     * @return
     * Block-experiment comparison test that determines if there is a
     * statistically significant difference in one of the treatments
     */
    public BlockExperimentComparison<Number> getBlockExperimentComparison()
    {
        return this.blockExperimentComparison;
    }

    /**
     * Setter for blockExperimentComparison
     * @param blockExperimentComparison 
     * Block-experiment comparison test that determines if there is a
     * statistically significant difference in one of the treatments
     */
    public void setBlockExperimentComparison(
        final BlockExperimentComparison<Number> blockExperimentComparison)
    {
        this.blockExperimentComparison = blockExperimentComparison;
    }

    /**
     * Getter for postHocTest
     * @return
     * Post-hoc test that determines which treatments are statistically
     * significantly different
     */
    public MultipleHypothesisComparison<Collection<? extends Number>> getPostHocTest()
    {
        return this.postHocTest;
    }

    /**
     * Setter for postHocTest
     * @param postHocTest
     * Post-hoc test that determines which treatments are statistically
     * significantly different
     */
    public void setPostHocTest(
        final MultipleHypothesisComparison<Collection<? extends Number>> postHocTest)
    {
        this.postHocTest = postHocTest;
    }

    /**
     * Getter for alpha
     * @return
     * P-value threshold, such that if the null-hypothesis probability is below
     * alpha then we reject the null hypothesis.
     */
    public double getAlpha()
    {
        return this.alpha;
    }

    /**
     * Setter for alpha
     * @param alpha
     * P-value threshold, such that if the null-hypothesis probability is below
     * alpha then we reject the null hypothesis.
     */
    public void setAlpha(
        final double alpha)
    {
        this.alpha = alpha;
    }

    @Override
    public MultipleComparisonExperiment.Statistic evaluateNullHypothesis(
        final Collection<? extends Collection<? extends Number>> treatments)
    {
        ConfidenceStatistic blockExperimentResult =
            this.getBlockExperimentComparison().evaluateNullHypothesis(treatments);

        MultipleHypothesisComparison.Statistic multipleComparisonResult = null;

        // if we reject the experiment null hypothesis, then run the
        // multiple-hypothesis comparison test
        if( blockExperimentResult.getNullHypothesisProbability() < this.getAlpha() )
        {
            multipleComparisonResult =
                this.getPostHocTest().evaluateNullHypotheses(treatments, this.getAlpha() );
        }

        return new MultipleComparisonExperiment.Statistic(
            blockExperimentResult, multipleComparisonResult);
        
    }

    @Override
    @SuppressWarnings("unchecked")
    public MultipleComparisonExperiment.Statistic evaluateNullHypothesis(
        final Collection<? extends Number> data1,
        final Collection<? extends Number> data2)
    {
        return evaluateNullHypothesis( Arrays.asList( data1, data2 ) );
    }

    @Override
    public MultipleComparisonExperiment.Statistic evaluateNullHypotheses(
        final Collection<? extends Collection<? extends Number>> data)
    {
        return this.evaluateNullHypothesis(data);
    }

    @Override
    public MultipleComparisonExperiment.Statistic evaluateNullHypotheses(
        final Collection<? extends Collection<? extends Number>> data,
        final double uncompensatedAlpha)
    {
        this.setAlpha(uncompensatedAlpha);
        return this.evaluateNullHypotheses(data);
    }

    /**
     * Result of running the MultipleHypothesisComparison hypothesis test
     */
    public static class Statistic
        extends AbstractCloneableSerializable
        implements ConfidenceStatistic,
        MultipleHypothesisComparison.Statistic
    {

        /**
         * Result from the block-experiment null-hypothesis test
         */
        protected ConfidenceStatistic blockExperimentResult;

        /**
         * Result from the multiple hypothesis comparison null-hypothesis test,
         * which will exist if the block-experiment null hypothesis is rejected
         */
        protected MultipleHypothesisComparison.Statistic multipleComparisonResult;

        /**
         * Creates a new instance of Statistic
         * @param blockExperimentResult
         * Result from the block-experiment null-hypothesis test
         * @param multipleComparisonResult
         * Result from the multiple hypothesis comparison null-hypothesis test,
         * which will exist if the block-experiment null hypothesis is rejected
         */
        public Statistic(
            final ConfidenceStatistic blockExperimentResult,
            final MultipleHypothesisComparison.Statistic multipleComparisonResult)
        {
            this.blockExperimentResult = blockExperimentResult;
            this.multipleComparisonResult = multipleComparisonResult;
        }

        @Override
        public double getTestStatistic()
        {
            return this.blockExperimentResult.getTestStatistic();
        }

        @Override
        public int getTreatmentCount()
        {
            if( this.multipleComparisonResult != null )
            {
                return this.multipleComparisonResult.getTreatmentCount();
            }
            else
            {
                return 0;
            }
        }

        @Override
        public double getUncompensatedAlpha()
        {
            if( this.multipleComparisonResult != null )
            {
                return this.multipleComparisonResult.getUncompensatedAlpha();
            }
            else
            {
                return 0.0;
            }
        }

        @Override
        public double getTestStatistic(
            int i,
            int j)
        {
            if( this.multipleComparisonResult != null )
            {
                return this.multipleComparisonResult.getTestStatistic(i, j);
            }
            else
            {
                return Double.NaN;
            }
        }

        @Override
        public double getNullHypothesisProbability(
            int i,
            int j)
        {
            if( this.multipleComparisonResult != null )
            {
                return this.multipleComparisonResult.getNullHypothesisProbability(i, j);
            }
            else
            {
                return 1.0;
            }
        }

        @Override
        public boolean acceptNullHypothesis(
            int i,
            int j)
        {
            if( this.multipleComparisonResult != null )
            {
                return this.multipleComparisonResult.acceptNullHypothesis(i, j);
            }
            else
            {
                return true;
            }
        }

        @Override
        public double getNullHypothesisProbability()
        {
            return this.blockExperimentResult.getNullHypothesisProbability();
        }

        /**
         * Getter for blockExperimentResult
         * @return
         * Result from the block-experiment null-hypothesis test
         */
        public ConfidenceStatistic getBlockExperimentResult()
        {
            return this.blockExperimentResult;
        }

        /**
         * Getter for multipleComparisonResult
         * @return
         * Result from the multiple hypothesis comparison null-hypothesis test,
         * which will exist if the block-experiment null hypothesis is rejected
         */
        public MultipleHypothesisComparison.Statistic getMultipleComparisonResult()
        {
            return this.multipleComparisonResult;
        }

        @Override
        public String toString()
        {
            return ObjectUtil.toString(this);
        }

    }
    
}
