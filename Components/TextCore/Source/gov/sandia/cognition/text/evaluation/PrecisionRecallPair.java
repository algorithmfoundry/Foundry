/*
 * File:                PrecisionRecallPair.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright January 26, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 * See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.text.evaluation;

import gov.sandia.cognition.util.Pair;

/**
 * A pair of precision and recall values. Typically used for evaluating the
 * performance of an information retrieval algorithm.
 * 
 * @author  Justin Basilico
 * @since   3.0
 */
public interface PrecisionRecallPair
    extends Pair<Double, Double>
{

    /**
     * Gets the precision. It is a value between 0.0 and 1.0, inclusive. It
     * is equal to: tp / (tp + fp).
     *
     * @return
     *      The precision.
     */
    public double getPrecision();

    /**
     * Gets the recall. It is a value between 0.0 and 1.0, inclusive. It is
     * equal to: tp / (tp + fn).
     *
     * @return
     *      The recall.
     */
    public double getRecall();

}
