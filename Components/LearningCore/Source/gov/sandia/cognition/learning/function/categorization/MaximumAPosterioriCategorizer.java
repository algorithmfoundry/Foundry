/*
 * File:                MaximumAPosterioriCategorizer.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Mar 26, 2010, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.learning.function.categorization;

import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.learning.algorithm.BatchLearner;
import gov.sandia.cognition.learning.algorithm.SupervisedBatchLearner;
import gov.sandia.cognition.learning.data.DefaultWeightedValueDiscriminant;
import gov.sandia.cognition.learning.data.InputOutputPair;
import gov.sandia.cognition.math.Ring;
import gov.sandia.cognition.statistics.AbstractDistribution;
import gov.sandia.cognition.statistics.ComputableDistribution;
import gov.sandia.cognition.statistics.PointMassDistribution;
import gov.sandia.cognition.statistics.ProbabilityFunction;
import gov.sandia.cognition.statistics.distribution.MapBasedPointMassDistribution;
import gov.sandia.cognition.util.AbstractCloneableSerializable;
import gov.sandia.cognition.util.DefaultWeightedValue;
import gov.sandia.cognition.util.ObjectUtil;
import gov.sandia.cognition.util.WeightedValue;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 * Categorizer that returns the category with the highest posterior likelihood
 * for a given observation.  This is known as a MAP categorizer, where
 * the posterior is proportionate to the category's conditional likelihood
 * for a given observation times the prior probability of the category.
 * @param <ObservationType> Type of observations
 * @param <CategoryType> Type of categories
 * @author Kevin R. Dixon
 * @since 3.0
 */
@PublicationReference(
    author="Wikipedia",
    title="Maximum a posteriori estimation",
    type=PublicationType.WebPage,
    year=2010,
    url="http://en.wikipedia.org/wiki/Maximum_a_posteriori_estimation"
)
public class MaximumAPosterioriCategorizer<ObservationType,CategoryType>
    extends AbstractDistribution<ObservationType>
    implements DiscriminantCategorizer<ObservationType,CategoryType,Double>
{

    /**
     * PMF of the various categories
     */
    PointMassDistribution.PMF<CategoryType> categoryPriors;

    /**
     * Map that contains the probability functions for the observations
     * for the given categories.
     */
    Map<CategoryType,ProbabilityFunction<ObservationType>> categoryConditionals;

    /** 
     * Creates a new instance of MaximumAPosterioriCategorizer 
     */
    public MaximumAPosterioriCategorizer()
    {
        this.categoryPriors = new MapBasedPointMassDistribution.PMF<CategoryType>( 2 );
        this.categoryConditionals =
            new HashMap<CategoryType, ProbabilityFunction<ObservationType>>( 2 );
    }

    @Override
    @SuppressWarnings("unchecked")
    public MaximumAPosterioriCategorizer<ObservationType,CategoryType> clone()
    {
        return (MaximumAPosterioriCategorizer<ObservationType,CategoryType>) super.clone();
    }

    /**
     * Adds the given category with the given mass (which is divided by the
     * masses of all categories to determine the prior probability weight)
     * and the distribution function
     * @param category
     * Category to add
     * @param mass
     * Mass of the category
     * @param conditional
     * Conditional probability function of observations for the category
     */
    public void addCategory(
        CategoryType category,
        double mass,
        ProbabilityFunction<ObservationType> conditional )
    {
        this.categoryPriors.add(category, mass);
        this.categoryConditionals.put( category, conditional );
    }

    /**
     * Gets the prior probability weight and conditional distribution for
     * the given category.
     * @param category
     * Category to consider
     * @return
     * Prior probability weight and conditional distribution for
     * the given category.
     */
    public WeightedValue<ProbabilityFunction<ObservationType>> getCategory(
        CategoryType category )
    {
        ProbabilityFunction<ObservationType> conditional =
            this.categoryConditionals.get(category);
        double prior = this.categoryPriors.evaluate(category);
        return new DefaultWeightedValue<ProbabilityFunction<ObservationType>>(
            conditional, prior );
    }

    public Set<? extends CategoryType> getCategories()
    {
        return this.categoryConditionals.keySet();
    }

    public CategoryType evaluate(
        ObservationType input)
    {
        return this.evaluateWithDiscriminant(input).getValue();
    }
    
    @Override
    public DefaultWeightedValueDiscriminant<CategoryType> evaluateWithDiscriminant(
        ObservationType input)
    {
        CategoryType maxCategory = null;
        double maxPosterior = Double.NEGATIVE_INFINITY;
        for( CategoryType category : this.getCategories() )
        {
            double posterior = this.computePosterior(input, category);
            if( maxPosterior < posterior )
            {
                maxPosterior = posterior;
                maxCategory = category;
            }
        }

        return DefaultWeightedValueDiscriminant.create(maxCategory, maxPosterior);
    }

    /**
     * Computes the posterior of the observation given the category.
     * Actually, this is the conjunctive likelihood since we've not normalizing
     * by the likelihood of the observation over all categories.  Since we're
     * only interested in finding the MAP category, we're doing the standard
     * thing and not normalizing.
     * @param observation
     * Observation to consider
     * @param category
     * Category to consider
     * @return
     * Posterior likelihood of the observation given the category.
     */
    public double computePosterior(
        ObservationType observation,
        CategoryType category )
    {

        ProbabilityFunction<ObservationType> categoryConditional =
            this.categoryConditionals.get(category);
        double posterior;
        if( categoryConditional != null )
        {
            double prior = this.categoryPriors.evaluate(category);
            double conditional = categoryConditional.evaluate(observation);
            posterior = conditional*prior;
        }
        else
        {
            posterior = 0.0;
        }
        
        return posterior;
    }

    /**
     * Gets the mean category, if it is a number or ring.
     *
     * @return
     *      The mean.
     */
    @SuppressWarnings("unchecked")
    public ObservationType getMean()
    {

        ObservationType mean = null;

        for( CategoryType category : this.getCategories() )
        {
            ObservationType categoryMean = this.getMean();
            double prior = this.categoryPriors.evaluate(category);
            if( categoryMean instanceof Number )
            {
                if( mean == null )
                {
                    mean = (ObservationType) new Double( 0.0 );
                }
                double weightedCategoryMean = prior * ((Number) categoryMean).doubleValue();
                mean = (ObservationType)
                    new Double( ((Number) mean).doubleValue() + weightedCategoryMean );
            }
            else if( categoryMean instanceof Ring )
            {
                Ring weightedCategoryMean = ((Ring) categoryMean).scale(prior);
                if( mean == null )
                {
                    mean = (ObservationType) weightedCategoryMean;
                }
                else
                {
                    ((Ring) mean).plusEquals( weightedCategoryMean );
                }
            }
            else
            {
                throw new UnsupportedOperationException(
                    "Mean not supported for type " + categoryMean.getClass() );
            }

        }

        return mean;

    }

    public ArrayList<? extends ObservationType> sample(
        Random random,
        int numSamples)
    {
        ArrayList<? extends CategoryType> categories =
            this.categoryPriors.sample(random, numSamples);
        ArrayList<ObservationType> observations =
            new ArrayList<ObservationType>( numSamples );
        for( CategoryType category : categories )
        {
            ProbabilityFunction<ObservationType> pdf =
                this.categoryConditionals.get(category);
            observations.add( pdf.sample(random) );
        }
        return observations;
    }


    /**
     * Learner for the MAP categorizer
     * @param <ObservationType> Type of observations
     * @param <CategoryType> Type of categories
     */
    public static class Learner<ObservationType,CategoryType>
        extends AbstractCloneableSerializable
        implements SupervisedBatchLearner<ObservationType,CategoryType,MaximumAPosterioriCategorizer<ObservationType,CategoryType>>
    {

        /**
         * Learner that creates the conditional distributions for each
         * category.
         */
        private BatchLearner<Collection<? extends ObservationType>, ? extends ComputableDistribution<ObservationType>> conditionalLearner;

        /**
         * Default constructor
         */
        public Learner()
        {
            this( null );
        }

        /**
         * Creates a new instance of Learner
         * @param conditionalLearner
         * Learner that creates the conditional distributions for each
         * category.
         */
        public Learner(
            BatchLearner<Collection<? extends ObservationType>, ? extends ComputableDistribution<ObservationType>> conditionalLearner)
        {
            this.conditionalLearner = conditionalLearner;
        }

        @Override
        public MaximumAPosterioriCategorizer.Learner<ObservationType,CategoryType> clone()
        {
            @SuppressWarnings("unchecked")
            Learner<ObservationType,CategoryType> clone =
                (Learner<ObservationType,CategoryType>) super.clone();
            clone.setConditionalLearner(
                ObjectUtil.cloneSmart( this.getConditionalLearner() ) );
            return clone;
        }

        public MaximumAPosterioriCategorizer<ObservationType, CategoryType> learn(
            Collection<? extends InputOutputPair<? extends ObservationType, CategoryType>> data)
        {
            PointMassDistribution.PMF<CategoryType> categoryPrior =
                new MapBasedPointMassDistribution.PMF<CategoryType>();
            Map<CategoryType,LinkedList<ObservationType>> categoryData =
                new HashMap<CategoryType, LinkedList<ObservationType>>();
            for( InputOutputPair<? extends ObservationType,CategoryType> pair : data )
            {
                categoryPrior.add( pair.getOutput(), 1.0 );
                LinkedList<ObservationType> categoryValues = categoryData.get( pair.getOutput() );
                if( categoryValues == null )
                {
                    categoryValues = new LinkedList<ObservationType>();
                    categoryData.put( pair.getOutput(), categoryValues );
                }
                categoryValues.add( pair.getInput() );
            }


            MaximumAPosterioriCategorizer<ObservationType,CategoryType> categorizer =
                new MaximumAPosterioriCategorizer<ObservationType, CategoryType>();

            for( CategoryType category : categoryPrior.getDomain() )
            {
                LinkedList<ObservationType> categoryValues =
                    categoryData.get(category);
                ComputableDistribution<ObservationType> distribution =
                    this.conditionalLearner.learn(categoryValues);
                ProbabilityFunction<ObservationType> conditional =
                    distribution.getProbabilityFunction();

                categorizer.addCategory(
                    category, categoryPrior.getMass(category), conditional );
            }

            return categorizer;

        }

        /**
         * Getter for conditionalLearner
         * @return 
         * Learner that creates the conditional distributions for each
         * category.
         */
        public BatchLearner<Collection<? extends ObservationType>,? extends ComputableDistribution<ObservationType>> getConditionalLearner()
        {
            return this.conditionalLearner;
        }

        /**
         * Setter for conditionalLearner
         * @param conditionalLearner
         * Learner that creates the conditional distributions for each
         * category.
         */
        public void setConditionalLearner(
            BatchLearner<Collection<? extends ObservationType>, ? extends ComputableDistribution<ObservationType>> conditionalLearner)
        {
            this.conditionalLearner = conditionalLearner;
        }

    }

}
