/*
 * File:                DefaultConfusionMatrix.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry Learning Core
 * 
 * Copyright January 11, 2011, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 */

package gov.sandia.cognition.learning.performance.categorization;

import gov.sandia.cognition.learning.data.TargetEstimatePair;
import gov.sandia.cognition.math.MutableDouble;
import gov.sandia.cognition.util.AbstractCloneableSerializable;
import gov.sandia.cognition.util.Pair;
import gov.sandia.cognition.util.Summarizer;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * A default implementation of the {@code ConfusionMatrix} interface. It is
 * backed by a two-level map storing the category object counts, making a
 * sparse representation.
 *
 * @param <CategoryType>
 *      The type of the category object over the confusion matrix.
 * @author  Justin Basilico
 * @since   3.1
 */
public class DefaultConfusionMatrix<CategoryType>
    extends AbstractConfusionMatrix<CategoryType>
{

    /** The backing map of confusion matrix entries. The first key is the
     *  actual category and the second is the predicted category. */
    protected Map<CategoryType, Map<CategoryType, MutableDouble>> confusions;

    /**
     * Creates a new, empty {@code DefaultConfusionMatrix}.
     */
    public DefaultConfusionMatrix()
    {
        super();

        this.confusions = new LinkedHashMap<CategoryType, Map<CategoryType, MutableDouble>>();
    }

    /**
     * Creates a copy of a given confusion matrix.
     *
     * @param   other
     *      The other confusion matrix to copy.
     */
    public DefaultConfusionMatrix(
        final ConfusionMatrix<? extends CategoryType> other)
    {
        this();

        this.addAll(other);
    }

    @Override
    public DefaultConfusionMatrix<CategoryType> clone()
    {
        @SuppressWarnings("unchecked")
        final DefaultConfusionMatrix<CategoryType> clone =
            (DefaultConfusionMatrix<CategoryType>) super.clone();

        if (this.confusions != null)
        {
            clone.confusions = new LinkedHashMap<CategoryType, Map<CategoryType, MutableDouble>>(
                this.confusions.size());
            for (Map.Entry<CategoryType, Map<CategoryType, MutableDouble>> outerEntry
                : this.confusions.entrySet())
            {
                final LinkedHashMap<CategoryType, MutableDouble> categoryMap =
                    new LinkedHashMap<CategoryType, MutableDouble>(
                        outerEntry.getValue().size());
                clone.confusions.put(outerEntry.getKey(), categoryMap);
                for (Map.Entry<CategoryType, MutableDouble> innerEntry
                    : outerEntry.getValue().entrySet())
                {
                    categoryMap.put(innerEntry.getKey(),
                        innerEntry.getValue().clone());
                }
            }
        }

        return clone;
    }

    @Override
    public void add(
        final CategoryType target,
        final CategoryType estimate,
        final double value)
    {
        Map<CategoryType, MutableDouble> subMap = confusions.get(target);

        if (subMap == null)
        {
            subMap = new HashMap<CategoryType, MutableDouble>();
            this.confusions.put(target, subMap);
        }

        MutableDouble entry = subMap.get(estimate);
        if (entry == null)
        {
            entry = new MutableDouble(value);
            subMap.put(estimate, entry);
        }
        else
        {
            entry.value += value;
        }
    }

    @Override
    public double getCount(
        final CategoryType target,
        final CategoryType estimate)
    {
        Map<CategoryType, MutableDouble> subMap = confusions.get(target);

        if (subMap == null)
        {
            return 0.0;
        }
        else
        {
            MutableDouble entry = subMap.get(estimate);

            if (entry == null)
            {
                return 0.0;
            }
            else
            {
                return entry.getValue();
            }
        }

    }

    @Override
    public double getActualCount(
        final CategoryType target)
    {
        Map<CategoryType, MutableDouble> subMap = confusions.get(target);

        if (subMap == null)
        {
            return 0.0;
        }

        double result = 0.0;
        for (MutableDouble value : subMap.values())
        {
            result += value.getValue();
        }

        return result;
    }

    @Override
    public void clear()
    {
        this.confusions.clear();
    }

    @Override
    public Set<CategoryType> getCategories()
    {
        final LinkedHashSet<CategoryType> result =
            new LinkedHashSet<CategoryType>();
        result.addAll(this.getActualCategories());
        result.addAll(this.getPredictedCategories());
        return result;
    }

    @Override
    public Set<CategoryType> getActualCategories()
    {
        return this.confusions.keySet();
    }

    @Override
    public Set<CategoryType> getPredictedCategories()
    {
        final LinkedHashSet<CategoryType> estimates = new LinkedHashSet<CategoryType>(
            this.confusions.size());

        for (Map<CategoryType, ?> estimateCounts
            : this.confusions.values())
        {
            estimates.addAll(estimateCounts.keySet());
        }

        return estimates;
    }

    @Override
    public Set<CategoryType> getPredictedCategories(
        final CategoryType target)
    {
        Map<CategoryType, MutableDouble> subMap = confusions.get(target);

        if (subMap == null)
        {
            return Collections.emptySet();
        }
        else
        {
            return subMap.keySet();
        }
    }

    @Override
    public String toString()
    {
        return this.confusions.toString();
    }

    /**
     * Creates a new {@code DefaultConfusionMatrix} from the given
     * actual-predicted pairs.
     *
     * @param   <CategoryType>
     *      The category type.
     * @param   pairs
     *      The actual-category pairs.
     * @return
     *      A new confusion matrix populated from the given actual-category
     *      pairs.
     */
    public static <CategoryType> DefaultConfusionMatrix<CategoryType> createUnweighted(
        final Collection<? extends TargetEstimatePair<? extends CategoryType, ? extends CategoryType>> pairs)
    {
        final DefaultConfusionMatrix<CategoryType> result =
            new DefaultConfusionMatrix<CategoryType>();
        for (TargetEstimatePair<? extends CategoryType, ? extends CategoryType> item
            : pairs)
        {
            result.add(item.getTarget(), item.getEstimate());
        }
        return result;
    }

    /**
     * Creates a new {@code DefaultConfusionMatrix} from the given
     * actual-predicted pairs.
     *
     * @param   <CategoryType>
     *      The category type.
     * @param   pairs
     *      The actual-category pairs.
     * @return
     *      A new confusion matrix populated from the given actual-category
     *      pairs.
     */
    public static <CategoryType> DefaultConfusionMatrix<CategoryType> createFromActualPredictedPairs(
        final Collection<? extends Pair<? extends CategoryType, ? extends CategoryType>> pairs)
    {
        final DefaultConfusionMatrix<CategoryType> result =
            new DefaultConfusionMatrix<CategoryType>();
        for (Pair<? extends CategoryType, ? extends CategoryType> pair
            : pairs)
        {
            result.add(pair.getFirst(), pair.getSecond());
        }
        return result;
    }

    /**
     * A confusion matrix summarizer that summarizes actual-predicted pairs.
     *
     * @param   <CategoryType>
     *      The type of category of the summarizer.
     */
    public static class ActualPredictedPairSummarizer<CategoryType>
        extends AbstractCloneableSerializable
        implements Summarizer<Pair<? extends CategoryType, ? extends CategoryType>, DefaultConfusionMatrix<CategoryType>>
    {

        /**
         * Creates a new {@code CombineSummarizer}.
         */
        public ActualPredictedPairSummarizer()
        {
            super();
        }

        @Override
        public DefaultConfusionMatrix<CategoryType> summarize(
            final Collection<? extends Pair<? extends CategoryType, ? extends CategoryType>> data)
        {
            return createFromActualPredictedPairs(data);
        }

    }

    /**
     * A confusion matrix summarizer that adds together confusion matrices.
     *
     * @param   <CategoryType>
     *      The type of category of the summarizer.
     */
    public static class CombineSummarizer<CategoryType>
        extends AbstractCloneableSerializable
        implements Summarizer<ConfusionMatrix<CategoryType>, DefaultConfusionMatrix<CategoryType>>
    {

        /**
         * Creates a new {@code CombineSummarizer}.
         */
        public CombineSummarizer()
        {
            super();
        }

        @Override
        public DefaultConfusionMatrix<CategoryType> summarize(
            final Collection<? extends ConfusionMatrix<CategoryType>> data)
        {
            final DefaultConfusionMatrix<CategoryType> result =
                new DefaultConfusionMatrix<CategoryType>();

            for (ConfusionMatrix<CategoryType> item : data)
            {
                result.addAll(item);
            }

            return result;
        }

    }

}
