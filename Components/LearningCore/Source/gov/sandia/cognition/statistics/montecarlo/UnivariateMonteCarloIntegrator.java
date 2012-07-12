/*
 * File:                UnivariateMonteCarloIntegrator.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Feb 12, 2010, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.statistics.montecarlo;

import gov.sandia.cognition.evaluator.Evaluator;
import gov.sandia.cognition.statistics.distribution.UnivariateGaussian;
import gov.sandia.cognition.util.AbstractCloneableSerializable;
import gov.sandia.cognition.util.DefaultWeightedValue;
import gov.sandia.cognition.util.WeightedValue;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * A Monte Carlo integrator for univariate (scalar) outputs.
 * @author Kevin R. Dixon
 * @since 3.0
 */
public class UnivariateMonteCarloIntegrator 
    extends AbstractCloneableSerializable
    implements MonteCarloIntegrator<Double>
{

    /**
     * Default variance to add to the Gaussian, {@value}.
     */
    public static final double DEFAULT_VARIANCE = 0.0;

    /**
     * Default instance because this class has no state.
     */
    public static final UnivariateMonteCarloIntegrator INSTANCE =
        new UnivariateMonteCarloIntegrator();

    /** 
     * Creates a new instance of UnivariateMonteCarloIntegrator 
     */
    public UnivariateMonteCarloIntegrator()
    {
    }

    public <SampleType> UnivariateGaussian.PDF integrate(
        Collection<? extends SampleType> samples,
        Evaluator<? super SampleType, ? extends Double> expectationFunction)
    {
        ArrayList<Double> outputs = new ArrayList<Double>( samples.size() );
        for( SampleType sample : samples )
        {
            outputs.add( expectationFunction.evaluate(sample).doubleValue() );
        }
        return this.getMean(outputs);
    }

    public <SampleType> UnivariateGaussian.PDF integrate(
        List<? extends WeightedValue<? extends SampleType>> samples,
        Evaluator<? super SampleType, ? extends Double> expectationFunction)
    {
        ArrayList<DefaultWeightedValue<Double>> outputs =
            new ArrayList<DefaultWeightedValue<Double>>( samples.size() );
        for( WeightedValue<? extends SampleType> sample : samples )
        {
            Double output = expectationFunction.evaluate(sample.getValue());
            outputs.add( new DefaultWeightedValue<Double>(output, sample.getWeight()) );
        }
        return this.getMean(outputs);
    }

    public UnivariateGaussian.PDF getMean(
        Collection<? extends Double> samples)
    {
        UnivariateGaussian.PDF pdf =
            UnivariateGaussian.MaximumLikelihoodEstimator.learn(
                samples,DEFAULT_VARIANCE);
        pdf.setVariance( pdf.getVariance() / samples.size() );
        return pdf;
    }

    public UnivariateGaussian.PDF getMean(
        List<? extends WeightedValue<? extends Double>> samples)
    {
        UnivariateGaussian.PDF pdf =
            UnivariateGaussian.WeightedMaximumLikelihoodEstimator.learn(
                samples,DEFAULT_VARIANCE);
        double weightSum = 0.0;
        double sumSquared = 0.0;
        for( WeightedValue<? extends Double> sample : samples )
        {
            final double w = sample.getWeight();
            weightSum += w;
            sumSquared += w*w;
        }

        double ws2 = weightSum*weightSum;

        pdf.setVariance( pdf.getVariance() * sumSquared / ws2 );
        return pdf;
    }
    
}
