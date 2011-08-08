/*
 * File:                CommonTermWeighterFactory.java
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

import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.text.term.vector.weighter.local.LogLocalTermWeighter;
import gov.sandia.cognition.text.term.vector.weighter.local.TermFrequencyLocalTermWeighter;
import gov.sandia.cognition.text.term.vector.weighter.global.DominanceGlobalTermWeighter;
import gov.sandia.cognition.text.term.vector.weighter.global.EntropyGlobalTermWeighter;
import gov.sandia.cognition.text.term.vector.weighter.global.InverseDocumentFrequencyGlobalTermWeighter;
import gov.sandia.cognition.text.term.vector.weighter.normalize.UnitTermWeightNormalizer;

/**
 * A factory for well-known weighting schemes.
 * 
 * @author  Justin Basilico
 * @since   3.0
 */
public class CommonTermWeighterFactory
    extends Object
{

    /**
     * Private constructor for utility class.
     */
    private CommonTermWeighterFactory()
    {
        super();
    }


    /**
     * Creates a term-frequency (TF) weighting scheme. No global weight or
     * normalizer is used.
     *
     * @return
     *      A new TF-IDF weighter.
     */
    public static CompositeLocalGlobalTermWeighter createTFWeighter()
    {
        return new CompositeLocalGlobalTermWeighter(
            new TermFrequencyLocalTermWeighter(),
            null,
            null);
    }

    /**
     * Creates a term-frequency inverse-document-frequency (TF-IDF) weighting
     * scheme but without any normalization.
     *
     * @return
     *      A new TF-IDF weighter.
     */
    @PublicationReference(
        author = "Wikipedia",
        title = "tf-idf",
        type = PublicationType.WebPage,
        url = "http://en.wikipedia.org/wiki/tf-idf",
        year = 2009
    )
    public static CompositeLocalGlobalTermWeighter createTFIDFWeighter()
    {
        return new CompositeLocalGlobalTermWeighter(
            new TermFrequencyLocalTermWeighter(),
            new InverseDocumentFrequencyGlobalTermWeighter(),
            null);
    }

    /**
     * Creates a term-frequency inverse-document-frequency (TF-IDF) weighting
     * scheme with unit vector normalization (2-norm).
     *
     * @return
     *      A new TF-IDF weighter.
     */
    public static CompositeLocalGlobalTermWeighter createTFIDFWeighterWithUnitNormalization()
    {
        return new CompositeLocalGlobalTermWeighter(
            new TermFrequencyLocalTermWeighter(),
            new InverseDocumentFrequencyGlobalTermWeighter(),
            new UnitTermWeightNormalizer());
    }

    /**
     * Creates a log-entropy weighting scheme.
     *
     * @return
     *      A new log-entropy weighter.
     */
    @PublicationReference(
        author={"Susan T. Dumais"},
        title="Improving the retrieval of information from external sources",
        year=1991,
        type=PublicationType.Journal,
        publication="Behavior Research Methods, Instruments, and Computers",
        pages={229, 236},
        url="http://www.google.com/url?sa=t&source=web&ct=res&cd=1&url=http%3A%2F%2Fwww.psychonomic.org%2Fsearch%2Fview.cgi%3Fid%3D5145&ei=o7joSdGEHY-itgPLre3tAQ&usg=AFQjCNEvm6PZEL6_Hk3XThI6DQ-gGx9EnQ&sig2=-gjFzNroJQirwGtwjaJvgQ"
    )
    public static CompositeLocalGlobalTermWeighter createLogEntropyWeighter()
    {
        return new CompositeLocalGlobalTermWeighter(
            new LogLocalTermWeighter(),
            new EntropyGlobalTermWeighter(),
            null);
    }

    /**
     * Creates a log-dominance weighting scheme.
     *
     * @return
     *      A new log-dominance weighter.
     */
    public static CompositeLocalGlobalTermWeighter createLogDominanceWeighter()
    {
        return new CompositeLocalGlobalTermWeighter(
            new LogLocalTermWeighter(),
            new DominanceGlobalTermWeighter(),
            null);
    }

}
