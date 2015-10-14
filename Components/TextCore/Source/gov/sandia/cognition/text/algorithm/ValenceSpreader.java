
package gov.sandia.cognition.text.algorithm;

import gov.sandia.cognition.learning.algorithm.minimization.matrix.ConjugateGradientMatrixSolver;
import gov.sandia.cognition.learning.algorithm.semisupervised.valence.MultipartiteValenceMatrix;
import gov.sandia.cognition.learning.data.DefaultInputOutputPair;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.util.Pair;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This class serves as a wrapper for the MultipartiteValenceMatrix class to
 * simplify the interface for the most common valence task: Ranking a set of
 * documents based on a small set of scored documents and/or a set of scored
 * terms.
 *
 * This algorithm only works when there are some negative scores and some
 * positive scores. However, some datasets (such as ANEW) score from [0 ... 10]
 * or similar. If your labels are like ANEW (with non-balanced scores on a
 * positive/negative scale), you can call centerWeightsRange to make sure there
 * are some negative and some positive scores.
 *
 * Note that this class also serves as an example of how to call
 * MultipartiteValenceMatrix for if you have a different application and just
 * want to see how it's done.
 *
 * @author jdwendt
 */
public class ValenceSpreader<TermType extends Comparable<TermType>, DocIdType extends Comparable<DocIdType>>
{

    /**
     * The (possibly empty) set of terms and their scores passed in by the
     * calling system. Note that either this or weightedDocuments should be
     * non-empty before calling solve.
     */
    private Map<TermType, Pair<Double, Double>> weightedTerms;

    /**
     * The (possibly empty) set of documents and their scores passed in by the
     * calling system. Note that either this or weightedTerms should be
     * non-empty before calling solve.
     */
    private Map<DocIdType, Pair<Double, Double>> weightedDocuments;

    /**
     * The set of documents to rank. The key is the document id. The value is a
     * map with each term in the document is the key and the score is the value
     * (can be passed in as a binary 1/0, TF, TF-IDF, etc.).
     */
    private Map<DocIdType, Map<TermType, Double>> documents;

    /**
     * The accuracy required before declaring that the iterative solver has
     * found the solution.
     */
    private double tolerance;

    /**
     * The number of threads to do for the matrix/vector multiply in the
     * iterative solver. Note that more threads is not necessarily better. In
     * some small problems a single thread or two threads are far better than
     * four threads.
     */
    private int numThreads;

    /**
     * Creates an empty valence spreader. After initialization, documents and
     * some set of scores must be passed in.
     */
    public ValenceSpreader()
    {
        weightedTerms = new HashMap<TermType, Pair<Double, Double>>();
        weightedDocuments = new HashMap<DocIdType, Pair<Double, Double>>();
        documents = new HashMap<DocIdType, Map<TermType, Double>>();
        tolerance = 1e-5;
        numThreads = 2;
    }

    /**
     * Specifies how many threads to use in the matrix/vector multiplies in the
     * iterative solver. Note that more threads is not necessarily better. On
     * many small tests (&lt;100 documents) a single thread has been best. We've
     * run up to several million entries in the matrix (including documents and
     * terms) with only 10-ish threads.
     *
     * Note that you don't need to call this method before solving as it's
     * initialized to a reasonable number of threads (2).
     *
     * @param numThreads The number of threads to use
     */
    public void setNumThreads(int numThreads)
    {
        if (numThreads <= 0)
        {
            throw new IllegalArgumentException("Unable to set the number of "
                + "threads to less than 1");
        }
        this.numThreads = numThreads;
    }

    /**
     * The tolerance that between-iteration error must be below before
     * considering the iterative solver "done". This essentially maps to the L-2
     * error of the result and inversely correlates with how long it takes for
     * the solver to complete. We initialize this to 1e-5, but you can alter
     * that.
     *
     * @param tolerance The error must go below this before the solver completes
     */
    public void setIterativeSolverTolerance(double tolerance)
    {
        if (tolerance <= 0)
        {
            throw new IllegalArgumentException("Unable to set the tolerance "
                + "to a value less than or equal to zero.");
        }
        this.tolerance = tolerance;
    }

    /**
     * Adds the input term with its associated score. Note that this term/score
     * pair will only be used when solving for the system if some document uses
     * that term at least once.
     *
     * @param term The term with the associated score
     * @param score The score for the input term
     */
    public void addWeightedTerm(TermType term,
        double score)
    {
        // This just gives all terms a default trust of 1
        // Note that since trust only matters relative to other trusts (and 
        // that it be positive/non-zero), this just says "trust all scores the 
        // same".
        addWeightedTerm(term, score, 1);
    }

    /**
     * Adds the input term with its associated score and trust level. Note that
     * this term/score/trust tuple will only be used when solving for if some
     * document uses that term at least once.
     *
     * @param term The term with its associated values
     * @param score The score for the input term
     * @param trust The amount to trust the input score. Should be greater than
     * 0. The importance here is how this score ranks relative to the other
     * scores input.
     */
    public void addWeightedTerm(TermType term,
        double score,
        double trust)
    {
        if (trust <= 0)
        {
            throw new IllegalArgumentException("Trust must be greater than 0.  "
                + "Input: " + trust);
        }
        weightedTerms.put(term,
            new DefaultInputOutputPair<Double, Double>(score, trust));
    }

    /**
     * Adds the input documentId with its associated score. Note that this
     * documentId/score will only be used when solving if a document was added
     * with this ID.
     *
     * @param documentId The document id that refers to a document added via one
     * of the addDocumentTerm* methods.
     * @param score The score for the input document
     */
    public void addWeightedDocument(DocIdType documentId,
        double score)
    {
        // This just gives all documents a default trust of 1
        // Note that since trust only matters relative to other trusts (and 
        // that it be positive/non-zero), this just says "trust all scores the 
        // same".
        addWeightedDocument(documentId, score, 1);
    }

    /**
     * Adds the input documentId with its associated score/trust. Note that this
     * will only be used when solving if a document was added with the input ID.
     *
     * @param documentId The document id that refers to a document added via one
     * of the addDocumentTerm* methods.
     * @param score The score for the input document
     * @param trust The amount to trust the input score (should be greater than
     * 0). This only matters in relation to other trust scores -- higher scores
     * are trusted more.
     */
    public void addWeightedDocument(DocIdType documentId,
        double score,
        double trust)
    {
        if (trust <= 0)
        {
            throw new IllegalArgumentException("Trust must be greater than 0.  "
                + "Input: " + trust);
        }
        weightedDocuments.put(documentId,
            new DefaultInputOutputPair<Double, Double>(score, trust));
    }

    /**
     * Adds the input document with all of the input terms in the data. Note
     * that this method and addDocumentTermWeights should be mutually exclusive
     * methods: It doesn't make sense to add one document via this method and
     * another via the other.
     *
     * @param documentId The unique ID for this document. If the same id is used
     * more than once, the earlier data will be replaced with the new data.
     * @param terms The set of terms that occur in the document
     */
    public void addDocumentTermOccurrences(DocIdType documentId,
        Set<TermType> terms)
    {
        Map<TermType, Double> document = new HashMap<TermType, Double>(
            terms.size());
        for (TermType term : terms)
        {
            document.put(term, 1.0);
        }
        documents.put(documentId, document);
    }

    /**
     * Adds the input document with all of the input terms with their input
     * scores (should be greater than 0) to the data. Note that this method and
     * addDocumentTermOccurrences should be mutually exclusive methods: It
     * doesn't make sense to add one document via this method and another via
     * the other.
     *
     * @param documentId The unique ID for this document. If the same id is used
     * more than once, the earlier data will be replaced with the new data.
     * @param terms The set of terms and their associated scores from this
     * document (score can be TF, TF-IDF, etc.)
     */
    public void addDocumentTermWeights(DocIdType documentId,
        Map<TermType, Double> terms)
    {
        documents.put(documentId, new HashMap<TermType, Double>(terms));
    }

    /**
     * Simple helper method that takes an input map of scores and centers the
     * map's values' first elements around zero. The centering is done by
     * remapping current min to -1 and current max to +1 (versus, centering so
     * that the mean is 0).
     *
     * @param m The map to recenter
     */
    private static <Type> void centerMap(Map<Type, Pair<Double, Double>> m)
    {
        double min, max;
        min = Double.MAX_VALUE;
        max = Double.MIN_VALUE;
        for (Pair<Double, Double> p : m.values())
        {
            min = Math.min(p.getFirst(), min);
            max = Math.max(p.getFirst(), max);
        }
        double mult = 2.0 / (max - min);
        Set<Map.Entry<Type, Pair<Double, Double>>> entries = m.entrySet();
        for (Map.Entry<Type, Pair<Double, Double>> e : entries)
        {
            m.put(e.getKey(), new DefaultInputOutputPair<Double, Double>(
                (e.getValue().getFirst() - min) * mult - 1,
                e.getValue().getSecond()));
        }
    }

    /**
     * This algorithm only works when there are some negative scores and some
     * positive scores. However, some datasets (such as ANEW) score from [0 ...
     * 10] or similar. This recenters both the term scores and document scores
     * to go from -1 to 1. Note that the two sets of scores are centered
     * independently, so if you want to have only positive term scores and only
     * negative document scores, don't call this method.
     */
    public void centerWeightsRange()
    {
        centerMap(weightedTerms);
        centerMap(weightedDocuments);
    }

    /**
     * This method solves the system of equations to determine the valence for
     * all documents input and for all terms in those documents. Before callig
     * this method, you should call an addDocumentTerm* method multiple times
     * for all of the documents and call addWeighted* with some positive and
     * negative values passed in. Optionally (if your positive and negative
     * values are all numerically positive) you should call centerWeightsRange
     * also before calling this method.
     *
     * This version uses the default power of 10. This has generally worked well
     * in previous experiments.
     *
     * @return The results of spreading the valence -- The term weights can be
     * used in the future as a classifier; the document weights can be used
     * independently to identify which documents are most extreme on either end.
     */
    public Result<TermType, DocIdType> spreadValence()
    {
        // 10 has been shown to be a good power for most of the text/valence spreading we've done thus far
        return spreadValence(10);
    }

    /**
     * This method solves the system of equations to determine the valence for
     * all documents input and for all terms in those documents. Before callig
     * this method, you should call an addDocumentTerm* method multiple times
     * for all of the documents and call addWeighted* with some positive and
     * negative values passed in. Optionally (if your positive and negative
     * values are all numerically positive) you should call centerWeightsRange
     * also before calling this method.
     *
     * @param power This correlates with how far to spread the influence of the
     * scored values. A power of 0 (not permitted) won't spread at all. A power
     * of 1 will only spread scores from a document to their terms or from terms
     * to their documents. It correlates with the distance of the spread, but
     * does not match it perfectly. In our experience, 10 has been a rather good
     * number for this parameter.
     * @return The results of spreading the valence -- The term weights can be
     * used in the future as a classifier; the document weights can be used
     * independently to identify which documents are most extreme on either end.
     */
    public Result<TermType, DocIdType> spreadValence(int power)
    {
        if (power <= 0)
        {
            throw new IllegalArgumentException("Unable to work with "
                + "non-positive power: " + power);
        }

        int numDocs = documents.size();
        // First get all of the terms in all of the documents
        Set<TermType> allTerms = new HashSet<TermType>();
        for (Map<TermType, Double> document : documents.values())
        {
            allTerms.addAll(document.keySet());
        }
        int numTerms = allTerms.size();
        // Now, put them in some deterministic order
        List<TermType> orderedTerms = new ArrayList<TermType>(allTerms);
        // (I use alphabetical ordering because it's convenient)
        Collections.sort(orderedTerms);
        // The list serves as a forward map (position to term), but I need both possibilities
        Map<TermType, Integer> reverseLookupTerms = new HashMap<TermType, Integer>(
            numTerms);
        for (int i = 0; i < numTerms; ++i)
        {
            reverseLookupTerms.put(orderedTerms.get(i), i);
        }
        // Now, I need an ordered list for the document ids
        List<DocIdType> orderedDocumentIds = new ArrayList<DocIdType>(
            documents.keySet());
        Collections.sort(orderedDocumentIds);
        // And a reverse map
        Map<DocIdType, Integer> reverseLookupDocuments =
            new HashMap<DocIdType, Integer>(numDocs);
        for (int i = 0; i < numDocs; ++i)
        {
            reverseLookupDocuments.put(orderedDocumentIds.get(i), i);
        }

        // Now, I can start putting things in the valence spreading algorithm
        List<Integer> sizes = new ArrayList<Integer>(2);
        sizes.add(numTerms);
        sizes.add(numDocs);
        MultipartiteValenceMatrix mvm = new MultipartiteValenceMatrix(sizes,
            power, numThreads);
        // For all documents...
        for (int i = 0; i < numDocs; ++i)
        {
            // Add all terms that document uses (w/ their scores)
            for (Map.Entry<TermType, Double> term : documents.get(
                orderedDocumentIds.get(i)).entrySet())
            {
                mvm.addRelationship(0, reverseLookupTerms.get(term.getKey()), 1,
                    i, term.getValue());
            }
        }

        // Now, set the initial scores for all of the scores passed in
        for (Map.Entry<TermType, Pair<Double, Double>> e
            : weightedTerms.entrySet())
        {
            Integer idx = reverseLookupTerms.get(e.getKey());
            if (idx != null)
            {
                mvm.setElementsScore(0, idx.intValue(), e.getValue().getSecond(),
                    e.getValue().getFirst());
            }
        }
        for (Map.Entry<DocIdType, Pair<Double, Double>> e
            : weightedDocuments.entrySet())
        {
            Integer idx = reverseLookupDocuments.get(e.getKey());
            mvm.setElementsScore(1, idx.intValue(), e.getValue().getSecond(),
                e.getValue().getFirst());
        }

        // Now, solve the stupid thing!
        Vector rhs = mvm.init();
        ConjugateGradientMatrixSolver s = new ConjugateGradientMatrixSolver(rhs,
            rhs, tolerance);
        Vector result = s.learn(mvm).getOutput();

        // Now pull out all of the scores into my return type
        Result<TermType, DocIdType> r = new Result<TermType, DocIdType>();
        r.termWeights = new HashMap<TermType, Double>(numTerms);
        r.documentWeights = new HashMap<DocIdType, Double>(numDocs);
        for (int i = 0; i < numTerms; ++i)
        {
            r.termWeights.put(orderedTerms.get(i), result.getElement(i));
        }
        for (int i = 0; i < numDocs; ++i)
        {
            r.documentWeights.put(orderedDocumentIds.get(i), result.getElement(
                numTerms + i));
        }

        return r;
    }

    /**
     * The return type from running the spreadValence methods. This reports the
     * weights assigned to all of the input documents and all of the terms that
     * existed in all of the documents.
     */
    public static class Result<TermType, DocIdType>
    {

        /**
         * The weights assigned to all of the terms in all of the input
         * documents.
         */
        public Map<TermType, Double> termWeights;

        /**
         * The weights assigned to all of the input documents.
         */
        public Map<DocIdType, Double> documentWeights;

    }

}
