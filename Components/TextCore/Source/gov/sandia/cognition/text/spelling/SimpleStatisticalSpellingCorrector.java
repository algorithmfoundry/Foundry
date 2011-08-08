/*
 * File:                SimpleStatisticalSpellingCorrector.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright January 14, 2010, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 * See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.text.spelling;

import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.evaluator.Evaluator;
import gov.sandia.cognition.learning.algorithm.AbstractBatchAndIncrementalLearner;
import gov.sandia.cognition.statistics.distribution.MapBasedDataHistogram;
import gov.sandia.cognition.util.AbstractCloneableSerializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * A simple statistical spelling corrector based on word counts that looks at
 * possible one and two-character edits.
 * 
 * @author  Justin Basilico
 * @since   3.0
 */
@PublicationReference(
    author="Peter Norvig",
    title="How to Write a Spelling Corrector",
    year=2009,
    type=PublicationType.WebPage,
    url="http://norvig.com/spell-correct.html"
)
public class SimpleStatisticalSpellingCorrector
    extends AbstractCloneableSerializable
    implements Evaluator<String, String>
{
    /**
     * Creates the default alphabet, which are the lower-case English letters.
     *
     * @return
     *      The default alphabet.
     */
    public static char[] createDefaultAlphabet()
    {
        return "abcdefghijklmnopqrstuvwxyz".toCharArray();
    }

    /** Maps known words to the number of times they've been seen. */
    protected MapBasedDataHistogram<String> wordCounts;

    /** The alphabet of lower case characters. */
    protected char[] alphabet;

    /**
     * Creates a new, default {@code SimpleStatisticalSpellingCorrector} with
     * a default alphabet.
     */
    public SimpleStatisticalSpellingCorrector()
    {
        this(createDefaultAlphabet());
    }

    /**
     * Creates a new {@code SimpleStatisticalSpellingCorrector} with a given
     * alphabet.
     *
     * @param   alphabet
     *      The alphabet to use.
     */
    public SimpleStatisticalSpellingCorrector(
        final char[] alphabet)
    {
        this(new MapBasedDataHistogram<String>(), alphabet);
    }

    /**
     * Creates a new {@code SimpleStatisticalSpellingCorrector}.
     *
     * @param   wordCounts
     *      The initial word counts.
     * @param   alphabet
     *      The alphabet to use.
     */
    public SimpleStatisticalSpellingCorrector(
        final MapBasedDataHistogram<String> wordCounts,
        final char[] alphabet)
    {
        super();

        this.setWordCounts(wordCounts);
        this.setAlphabet(alphabet);
    }

    /**
     * Adds a word to the dictionary of counts for the spelling corrector.
     *
     * @param   word
     *      The word to add an occurrence of.
     */
    public void add(
        final String word)
    {
        this.wordCounts.add(word.toLowerCase());
    }

    /**
     * Adds a given number of counts for a word to the dictionary of counts for
     * the spelling corrector.
     *
     * @param   word
     *      The word to add.
     * @param count
     *      The count of occurrences.
     */
    public void add(
        final String word,
        final int count)
    {
        this.wordCounts.add(word, count);
    }

    public String evaluate(
        final String word)
    {
        if (word == null)
        {
            // Bad word.
            return null;
        }

        final String input = word.toLowerCase();
        if (input.isEmpty() || this.wordCounts.getCount(input) > 0)
        {
            // This is a known word, so nothing to correct.
            return input;
        }

        // Compute the one-character edits.
        final HashSet<String> oneCharacterEdits = new HashSet<String>();
        this.possibleOneCharacterEdits(input, oneCharacterEdits);

        // Find the best one-character edits.
        String result = this.findBest(oneCharacterEdits, null);
        if (result != null)
        {
            // There was a good one-character edit, so return it.
            return result;
        }
        // else - No known one-character edits.

        // Now compute all the possible edits from the one character edits.
        final Set<String> twoCharacterEdits =
            this.knownTwoCharacterEdits(oneCharacterEdits);

        // Find the best known two character edits with a default of using the
        // input word.
        result = this.findBest(twoCharacterEdits, input);

        // Return the result.
        return result;
    }

    /**
     * Finds the best word from a given list of words by finding the one with
     * the highest count in the dictionary. If no words are in the dictionary,
     * the given default best word is returned.
     *
     * @param words
     *      The list of words.
     * @param defaultBestWord
     *      The default word to return if none are in the dictionary.
     * @return
     *      The word with the highest count.
     */
    public String findBest(
        final Iterable<String> words,
        final String defaultBestWord)
    {
        String bestWord = defaultBestWord;
        int bestCount = 0;

        // Go through the words.
        for (String word : words)
        {
            // Get the count.
            final int count = this.wordCounts.getCount(word);

            if (count > bestCount)
            {
                // Best found so far.
                bestWord = word;
                bestCount = count;
            }
        }

        // Return the best word found so far.
        return bestWord;
    }

    /**
     * Lists all possible one-character edits for a given word by looking at
     * character deletes, transposes, replaces, and inserts.
     *
     * @param   word
     *      The word to get the edits for.
     * @param result
     *      The collection to write the edits into.
     */
    protected void possibleOneCharacterEdits(
        final String word,
        final Collection<String> result)
    {
        // Cache information about the word we will reuse.
        final int wordLength = word.length();
        final char[] characters = word.toCharArray();
        final String[] prefixes = new String[wordLength + 1];
        final String[] suffixes = new String[wordLength + 1];
        for (int i = 0; i < wordLength; i++)
        {
            prefixes[i] = word.substring(0, i);
            suffixes[i] = word.substring(i);
        }
        prefixes[wordLength] = word;
        suffixes[wordLength] = "";

        // Deletes:
        for (int i = 0; i < wordLength; i++)
        {
            result.add(prefixes[i] + suffixes[i + 1]);
        }

        // Transposes:
        for (int i = 0; i < wordLength - 1; i++)
        {
            result.add(prefixes[i]
                + characters[i + 1] + characters[i]
                + suffixes[i + 2]);
        }

        // Replaces:
        for (int i = 0; i < wordLength; i++)
        {
            for (char c : this.alphabet)
            {
                result.add(prefixes[i] + c + suffixes[i + 1]);
            }
        }

        // Inserts:
        for (int i = 0; i <= wordLength; i++)
        {
            for (char c : this.alphabet)
            {
                result.add(prefixes[i] + c + suffixes[i]);
            }
        }
    }

    /**
     * Creates the set of known two character edits for a given list of one
     * character edits.
     *
     * @param   oneCharacterEdits
     *      The list of one character edits.
     * @return
     *      The set of known two-character edits, which are the two-character
     *      edits that are in the dictionary.
     */
    protected Set<String> knownTwoCharacterEdits(
        final Iterable<String> oneCharacterEdits)
    {
        // Create a hash set for the unique results.
        final HashSet<String> result = new HashSet<String>();

        // Create a hash set to locally keep track of the possible edits.
        final HashSet<String> possible = new HashSet<String>();
        for (String word : oneCharacterEdits)
        {
            // Clear out the set of possible edits.
            possible.clear();

            // Get the possible edits for the word.
            possibleOneCharacterEdits(word, possible);

            // Go through the possible edits.
            for (String editedWord : possible)
            {
                // See if this is a known word.
                if (this.wordCounts.getCount(editedWord) > 0)
                {
                    // This word is known.
                    result.add(editedWord);
                }
            }
        }

        // Return the result.
        return result;
    }

    /**
     * Gets the dictionary of word counts.
     *
     * @return
     *      The word counts.
     */
    public MapBasedDataHistogram<String> getWordCounts()
    {
        return this.wordCounts;
    }

    /**
     * Sets the dictionary of words counts.
     *
     * @param   wordCounts
     *      The dictionary of word counts.
     */
    public void setWordCounts(
        final MapBasedDataHistogram<String> wordCounts)
    {
        this.wordCounts = wordCounts;
    }

    /**
     * Gets the alphabet of lower-case characters that can be used for replaces
     * and inserts.
     *
     * @return
     *      The alphabet of lower-case characters.
     */
    public char[] getAlphabet()
    {
        return this.alphabet;
    }

    /**
     * Sets the alphabet of lower-case characters that can be used for replaces
     * and inserts.
     *
     * @param   alphabet
     *      The alphabet of lower-case characters.
     */
    public void setAlphabet(
        final char[] alphabet)
    {
        this.alphabet = alphabet;
    }

    /**
     * A learner for the {@code SimpleStatisticalSpellingCorrector}.
     */
    public static class Learner
        extends AbstractBatchAndIncrementalLearner<String, SimpleStatisticalSpellingCorrector>
    {

        /** The alphabet of lower case characters. */
        protected char[] alphabet;

        /**
         * Creates a new simple statistical spelling corrector learner with the
         * default alphabet.
         */
        public Learner()
        {
            this(createDefaultAlphabet());
        }

        /**
         * Creates a new simple statistical spelling corrector learner with the
         * default alphabet.
         *
         * @param   alphabet
         *      The alphabet of lower-case characters to use.
         */
        public Learner(
            final char[] alphabet)
        {
            super();
            
            this.setAlphabet(alphabet);
        }
        
        public SimpleStatisticalSpellingCorrector createInitialLearnedObject()
        {
            return new SimpleStatisticalSpellingCorrector(this.getAlphabet());
        }

        public void update(
            final SimpleStatisticalSpellingCorrector target,
            final String word)
        {
            // Add each word.
            target.add(word);
        }

        /**
         * Gets the alphabet of lower-case characters that can be used for
         * replaces and inserts.
         *
         * @return
         *      The alphabet of lower-case characters.
         */
        public char[] getAlphabet()
        {
            return this.alphabet;
        }

        /**
         * Sets the alphabet of lower-case characters that can be used for
         * replaces and inserts.
         *
         * @param   alphabet
         *      The alphabet of lower-case characters.
         */
        public void setAlphabet(
            final char[] alphabet)
        {
            this.alphabet = alphabet;
        }

    }

}
