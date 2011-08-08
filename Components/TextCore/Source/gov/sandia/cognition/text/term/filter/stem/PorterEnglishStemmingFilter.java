/*
 * File:                PorterEnglishStemmingFilter.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright January 27, 2010, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 * See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.text.term.filter.stem;

import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationReferences;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.text.term.DefaultTerm;
import gov.sandia.cognition.text.term.DefaultTermOccurrence;
import gov.sandia.cognition.text.term.TermOccurrence;
import gov.sandia.cognition.text.term.filter.AbstractSingleTermFilter;
import org.tartarus.martin.Stemmer;

/**
 * A term filter that uses the Porter Stemming algorithm. It is a rule-based
 * algorithm for stemming English words. This class just wraps the Java
 * implementation of the stemmer by Martin Porter himself and turns it into
 * a TermFilter.
 * 
 * @author  Justin Basilico
 * @since   3.0
 */
@PublicationReferences(
    references={
        @PublicationReference(
            author="Martin Porter",
            title="The Porter Stemming Algorithm",
            year=2006,
            type=PublicationType.WebPage,
            url="http://tartarus.org/~martin/PorterStemmer/"
        ),
        @PublicationReference(
            author="Martin F. Porter",
            title=" An algorithm for suffix stripping",
            year=1980,
            publication="Program 14(3)",
            pages={130, 137},
            type=PublicationType.Journal
        ),
        @PublicationReference(
            author="Wikipedia",
            title="Stemming",
            year=2010,
            type=PublicationType.WebPage,
            url="http://en.wikipedia.org/wiki/Stemming"
        )
    }
)
public class PorterEnglishStemmingFilter
    extends AbstractSingleTermFilter
{

    /**
     * Creates a new {@code PorterEnglishStemmingFilter}.
     */
    public PorterEnglishStemmingFilter()
    {
        super();
    }

    public TermOccurrence filterTerm(
        final TermOccurrence occurrence)
    {
        // Get the old text.
        final String oldText = occurrence.getTerm().getName();

        // Create the stemmer and apply it.
        final Stemmer stemmer = new Stemmer();
        stemmer.add(oldText.toCharArray(), oldText.length());
        stemmer.stem();
        final String newText = stemmer.toString();

        // Create the new term and add it to the result.
        final DefaultTerm newTerm = new DefaultTerm(newText);
        final DefaultTermOccurrence newOccurrence = new DefaultTermOccurrence(
            newTerm, occurrence.getStart(), occurrence.getLength());
        return newOccurrence;
    }

    /**
     * Stems the given String according to the Porter stemming algorithm for
     * English words.
     *
     * @param   word
     *      The word to stem.
     * @return
     *      The stemmed version of the given word.
     */
    public static String stem(
        final String word)
    {
        // Create the stemmer.
        final Stemmer stemmer = new Stemmer();

        // Add the word.
        stemmer.add(word.toCharArray(), word.length());

        // Stem the word.
        stemmer.stem();

        // Return the stem.
        return stemmer.toString();

    }

}
