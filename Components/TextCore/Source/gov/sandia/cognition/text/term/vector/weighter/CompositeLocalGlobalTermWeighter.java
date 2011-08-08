/*
 * File:                CompositeTermWeighter.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright April 20, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 * See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.text.term.vector.weighter;

import gov.sandia.cognition.text.term.vector.weighter.normalize.TermWeightNormalizer;
import gov.sandia.cognition.text.term.vector.weighter.local.LocalTermWeighter;
import gov.sandia.cognition.text.term.vector.weighter.global.GlobalTermWeighter;
import gov.sandia.cognition.evaluator.Evaluator;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.util.AbstractCloneableSerializable;
import gov.sandia.cognition.util.ObjectUtil;

/**
 * Composes together local and global term weighters along with a normalizer.
 * Used for creating a full term weighting scheme, for example TF-IDF or
 * LogEntropy. It multiplies the local weighted terms by the global weighted
 * terms and then passes the information to the normalizer.
 * 
 * @author  Justin Basilico
 * @since   3.0
 */
public class CompositeLocalGlobalTermWeighter
    extends AbstractCloneableSerializable
    implements Evaluator<Vector, Vector>
{

    /** The weighting scheme for the local weights. Null means no local
     * weights. */
    protected LocalTermWeighter localWeighter;

    /** The weighting scheme for the global weights. Null means no global
     *  weights. */
    protected GlobalTermWeighter globalWeighter;

    /** The weight normalizer. */
    protected TermWeightNormalizer normalizer;

    /**
     * Creates a new {@code CompositeLocalGlobalTermWeighter}. All of the
     * weightings default to null.
     */
    public CompositeLocalGlobalTermWeighter()
    {
        this(null, null, null);
    }

    /**
     * Creates a new {@code CompositeLocalGlobalTermWeighter} with the given
     * weighting schemes.
     *
     * @param   localWeighter
     *      The weighting scheme for local weights. Null means that the input
     *      values are not changed.
     * @param   globalWeighter
     *      The weighting scheme for the global weights. Null means that no
     *      global weights are used.
     * @param   normalizer
     *      The normalization scheme for the weights. Null means that no
     *      normalization is used.
     */
    public CompositeLocalGlobalTermWeighter(
        final LocalTermWeighter localWeighter,
        final GlobalTermWeighter globalWeighter,
        final TermWeightNormalizer normalizer)
    {
        super();

        this.localWeighter = localWeighter;
        this.globalWeighter = globalWeighter;
        this.normalizer = normalizer;
    }

    @Override
    public CompositeLocalGlobalTermWeighter clone()
    {
        final CompositeLocalGlobalTermWeighter clone = (CompositeLocalGlobalTermWeighter) super.clone();
        clone.localWeighter = ObjectUtil.cloneSafe(this.localWeighter);
        clone.globalWeighter = ObjectUtil.cloneSmart(this.globalWeighter);
        clone.normalizer = ObjectUtil.cloneSafe(this.normalizer);
        return clone;
    }



    public Vector evaluate(
        final Vector document)
    {
        Vector weights = null;
        if (this.localWeighter == null)
        {
            // If there is no local weighting scheme, start out with a copy of
            // the vector.
            weights = document.clone();
        }
        else
        {
            weights = this.localWeighter.computeLocalWeights(document);
        }

        Vector globalWeights = null;
        if (this.globalWeighter != null)
        {
            globalWeights = this.globalWeighter.getGlobalWeights();

            if (globalWeights != null)
            {
                weights.dotTimesEquals(globalWeights);
            }
        }
        // else - Don't apply global weights.

        if (this.normalizer != null)
        {
            this.normalizer.normalizeWeights(weights, document, globalWeights);
        }
        // else - Don't apply normalization.

        return weights;
    }

    /**
     * Gets the weighting scheme for the local weights.
     *
     * @return 
     *      The weighting scheme for the local weights. Null means no local
     *      weights.
     */
    public LocalTermWeighter getLocalWeighter()
    {
        return this.localWeighter;
    }

    /**
     * Sets the weighting scheme for the local weights.
     *
     * @param   localWeighter
     *      The weighting scheme for the local weights. Null means no local
     *      weights.
     */
    public void setLocalWeighter(
        final LocalTermWeighter localWeighter)
    {
        this.localWeighter = localWeighter;
    }

    /**
     * Gets the weighting scheme for the global weights.
     *
     * @return
     *      The weighting scheme for the global weights. Null means no global
     *      weights.
     */
    public GlobalTermWeighter getGlobalWeighter()
    {
        return this.globalWeighter;
    }

    /**
     * Sets the weighting scheme for the global weights.
     *
     * @param   globalWeighter
     *      The weighting scheme for the global weights. Null means no global
     *      weights.
     */
    public void setGlobalWeighter(
        final GlobalTermWeighter globalWeighter)
    {
        this.globalWeighter = globalWeighter;
    }

    /**
     * Gets the weight normalizer.
     *
     * @return
     *      The weight normalizer. Null means no normalizer.
     */
    public TermWeightNormalizer getNormalizer()
    {
        return this.normalizer;
    }

    /**
     * Sets the weight normalizer.
     *
     * @param   normalizer
     *      The weight normalizer. Null means no normalizer.
     */
    public void setNormalizer(
        final TermWeightNormalizer normalizer)
    {
        this.normalizer = normalizer;
    }

}
