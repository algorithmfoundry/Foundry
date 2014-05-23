/*
 * File:                AbstractDataDistribution.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Apr 25, 2011, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.statistics;

import gov.sandia.cognition.collection.AbstractMutableDoubleMap;
import gov.sandia.cognition.collection.ScalarMap;
import gov.sandia.cognition.math.matrix.DefaultInfiniteVector;
import gov.sandia.cognition.math.matrix.InfiniteVector;
import gov.sandia.cognition.math.MathUtil;
import gov.sandia.cognition.math.MutableDouble;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 * An abstract implementation of the {@code DataDistribution} interface.
 *
 * @param <KeyType>
 *       The type of data stored at the indices, the hash keys.
 * @author  Justin Basilico
 * @author  Kevin R. Dixon
 * @since   3.3.0
 */
public abstract class AbstractDataDistribution<KeyType>
    extends AbstractMutableDoubleMap<KeyType>
    implements DataDistribution<KeyType>
{

    /**
     * Creates a new {@code AbstractDataDistribution}.
     * @param map
     * Map that stores the data
     */
    public AbstractDataDistribution(
        final Map<KeyType, MutableDouble> map )
    {
        super( map );
    }

    @Override
    @SuppressWarnings("unchecked")
    public AbstractDataDistribution<KeyType> clone()
    {
        return (AbstractDataDistribution<KeyType>) super.clone();
    }

    @Override
    public int getDomainSize()
    {
        return this.map.size();
    }

    @Override
    public double getEntropy()
    {
        double entropy = 0.0;
        final double total = this.getTotal();
        final double denom = (total != 0.0) ? total : 1.0;
        for (ScalarMap.Entry<KeyType> entry : this.entrySet())
        {
            double p = entry.getValue() / denom;
            if (p != 0.0)
            {
                entropy -= p * MathUtil.log2(p);
            }
        }
        return entropy;
    }

    @Override
    public double getLogFraction(
        KeyType key)
    {
        final double total = this.getTotal();
        return (total != 0.0) ? (Math.log(this.get(key)) - Math.log(total))
            : Double.NEGATIVE_INFINITY;
    }

    @Override
    public double getFraction(
        KeyType key)
    {
        final double total = this.getTotal();
        return (total != 0.0) ? (this.get(key) / this.getTotal()) : 0.0;
    }

    @Override
    public KeyType sample(
        final Random random)
    {
        double w = random.nextDouble() * this.getTotal();
        for (ScalarMap.Entry<KeyType> entry : this.entrySet())
        {
            w -= entry.getValue();
            if (w <= 0.0)
            {
                return entry.getKey();
            }
        }
        return null;
    }

    @Override
    public ArrayList<KeyType> sample(
        final Random random,
        final int numSamples)
    {
        final ArrayList<KeyType> result = new ArrayList<KeyType>(numSamples);
        this.sampleInto(random, numSamples, result);
        return result;
    }

    @Override
    public void sampleInto(
        final Random random,
        final int sampleCount,
        final Collection<? super KeyType> output)
    {
        // Compute the cumulative weights
        final int size = this.getDomainSize();
        double[] cumulativeWeights = new double[size];
        double cumulativeSum = 0.0;
        ArrayList<KeyType> domain = new ArrayList<KeyType>(size);
        int index = 0;
        for (ScalarMap.Entry<KeyType> entry : this.entrySet())
        {
            domain.add(entry.getKey());
            final double value = entry.getValue();
            cumulativeSum += value;
            cumulativeWeights[index] = cumulativeSum;
            index++;
        }
        
        ProbabilityMassFunctionUtil.sampleMultipleInto(
            cumulativeWeights, domain, random, sampleCount, output);
    }

    @Override
    public InfiniteVector<KeyType> toInfiniteVector()
    {
        final DefaultInfiniteVector<KeyType> result =
            new DefaultInfiniteVector<KeyType>(this.size());

        for (ScalarMap.Entry<KeyType> entry : this.entrySet())
        {
            result.set(entry.getKey(), entry.getValue());
        }

        return result;
    }

    @Override
    public void fromInfiniteVector(
        final InfiniteVector<? extends KeyType> vector)
    {
        this.clear();

        for (ScalarMap.Entry<? extends KeyType> entry : vector.entrySet())
        {
            this.set(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public double getMaxValue()
    {
        double result = super.getMaxValue();
        if( result == Double.NEGATIVE_INFINITY )
        {
            return 0.0;
        }
        else
        {
            return result;
        }
    }

    @Override
    public double getMinValue()
    {
        double result = super.getMinValue();
        if( result == Double.POSITIVE_INFINITY )
        {
            return 0.0;
        }
        else
        {
            return result;
        }
    }

    @Override
    public Set<KeyType> getDomain()
    {
        return this.keySet();
    }

}
