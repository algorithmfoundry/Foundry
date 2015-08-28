
package examples;

import gov.sandia.cognition.text.algorithm.ValenceSpreader;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * This example shows how to run the ValenceSpreader code on a set of documents.
 * The document set is the movie review set (available for download from
 * https://www.cs.cornell.edu/people/pabo/movie-review-data/) and the starting
 * term scores are the ANEW set (available from
 * http://csea.phhp.ufl.edu/media/anewmessage.html if you're the right kind of
 * researcher).
 *
 * This code is meant as a fairly simple example of how to call the valence
 * spreader and different ways to call into it. This example is not intended as
 * the best possible results for learning a classifier for movie reviews (in
 * fact, the ANEW term lists appear to be very bad starting places to learn such
 * a classifier from).
 *
 * @author jdwendt
 */
public class ValenceAnewExample
{

    /**
     * This is a simple DTO for the ANEW data (sentiment, arousal, and
     * dominance).
     */
    private static class AnewData
    {

        /**
         * The sentiment score
         */
        double sentiment;

        /**
         * The arousal score
         */
        double arousal;

        /**
         * The dominance score
         */
        double dominance;

        /**
         * Loads the input values into the correct data members.
         *
         * @param sentiment
         * @param arousal
         * @param dominance
         */
        AnewData(double sentiment,
            double arousal,
            double dominance)
        {
            this.sentiment = sentiment;
            this.arousal = arousal;
            this.dominance = dominance;
        }

    }

    /**
     * Parses the CSV data at the input location into a map from terms to ANEW
     * scores. Note that this parses the ANEW CSV we've downloaded. If it
     * doesn't match your version, you may need to update this method. It
     * returns the word and the mean score for each of the sentiment, arousal,
     * and dominance scores.
     *
     * @param filename The file to parse
     * @return The data stored in the input file (for mean scores, not stdev)
     */
    private static Map<String, AnewData> parseAnewFile(String filename)
    {
        Map<String, AnewData> ret = new HashMap<String, AnewData>();
        try
        {
            BufferedReader br = new BufferedReader(new FileReader(filename));
            String line = null;
            boolean firstline = true;
            while ((line = br.readLine()) != null)
            {
                // Skip the header
                if (firstline)
                {
                    firstline = false;
                    continue;
                }

                // It's a basic CSV (no tricks, escaped commas, etc.)
                String[] elements = line.split(",");
                if (elements.length != 9)
                {
                    System.err.println("Found a line with wrong number of "
                        + "columns: \"" + line + "\"");
                    continue;
                }

                // Add the mean scores for each the word
                ret.put(elements[0], new AnewData(
                    Double.parseDouble(elements[2]), Double.parseDouble(
                    elements[4]), Double.parseDouble(elements[6])));
            }
            br.close();

            return ret;
        }
        catch (IOException ioe)
        {
            return null;
        }
    }

    /**
     * Returns the term frequency for each term in the document stored in the
     * input filename location. Note that this assumes the input file is in the
     * movie-review format (already lower-cased, spaces between all words and
     * even surrounding all punctuation). This includes punctuation as "terms".
     *
     * @param filename The file location to parse
     * @return All terms found in the document (key) and the number of times
     * they exist (value).
     */
    private static Map<String, Double> getTermFrequency(String filename)
    {
        Map<String, Double> termFrequency = new HashMap<String, Double>();
        try
        {
            BufferedReader br = new BufferedReader(new FileReader(filename));
            String line = null;
            while ((line = br.readLine()) != null)
            {
                // these files are so pretty you can just split on spaces
                // and consider the punctuation their own "words" (that should 
                // filter out in TF-IDF the same way "the" and "of" do)
                String[] words = line.split(" ");
                for (String word : words)
                {
                    if (!termFrequency.containsKey(word))
                    {
                        termFrequency.put(word, 0.0);
                    }
                    termFrequency.put(word, termFrequency.get(word) + 1);
                }
            }

            return termFrequency;
        }
        catch (IOException ioe)
        {
            throw new RuntimeException("Error reading file \"" + filename + "\"",
                ioe);
        }
    }

    /**
     * Reads all files in the input directory and returns a map containing the
     * filename (key) and the term frequencies (value) for all terms in each
     * document. Note that all documents' maps only include terms (key) and
     * counts (value) that were found in that document (no zero scores).
     *
     * @param path The path to the parent directory containing the documents to
     * parse.
     * @return A map containing all files' names found in the input directory
     * and their term frequencies.
     */
    private static Map<String, Map<String, Double>> parseReviewFiles(
        String path)
    {
        File dir = new File(path);
        if (!dir.exists() || !dir.isDirectory())
        {
            throw new RuntimeException("Although it looks like the base "
                + "diretory is there, the subdirectory \"" + path
                + "\" doesn't seem to exist.");
        }
        Map<String, Map<String, Double>> ret =
            new HashMap<String, Map<String, Double>>();
        for (String filename : dir.list())
        {
            ret.put(filename, getTermFrequency(path + filename));
        }

        return ret;
    }

    /**
     * Loads all documents into the solver. This adds the documents with _no_
     * scores assigned.
     *
     * @param solver The solver to load the documents into
     * @param documents The documents to add to the solver
     * @param useTermFrequency If true, term frequency is added, else only
     * binary exists/doesn't is added.
     */
    private static void loadDocuments(ValenceSpreader<String, String> solver,
        Map<String, Map<String, Double>> documents,
        boolean useTermFrequency)
    {
        for (Map.Entry<String, Map<String, Double>> e : documents.entrySet())
        {
            if (useTermFrequency)
            {
                solver.addDocumentTermWeights(e.getKey(), e.getValue());
            }
            else
            {
                solver.addDocumentTermOccurrences(e.getKey(),
                    e.getValue().keySet());
            }
        }
    }

    /**
     * Simple enum for specifying which ANEW score to use as seed data
     */
    private static enum AnewType
    {

        /**
         * The sentiment (happy/sad) scores
         */
        SENTIMENT,
        /**
         * The arousal (excited/depressed) scores
         */
        AROUSAL,
        /**
         * The dominance (in-control/controlled) scores
         */
        DOMINANCE,
        /**
         * Don't add any scores for terms before solving (assumes some documents
         * were scored)
         */
        NONE;

    }

    /**
     * The meat of this example. This code begins at the top with a set of
     * parameters you may wish to change to test how well the algorithm runs
     * with different input values. This is followed by some code that reads in
     * and loads the data, the code that runs the valence spreader, and then
     * finally some code to print the resulting confusion matrix.
     *
     * From our quick tests of this code, we found that all three of the ANEW
     * initial classifiers are very poor classifiers for movie reviews, while
     * the score is considerably better (~75% accuracy) with only 5% of the
     * documents labeled.
     *
     * @param args Ignored
     */
    public static void main(String[] args)
    {
        // The path we will look for the ANEW data
        String anewFilename = "Data/anew-1999/all.csv";
        // The path we will look for the movie review datasets
        String movieReviewsPath = "Data/review_polarity/txt_sentoken/";
        // If true, Term Frequency will be loaded to the solver, else just 
        // binary occurence
        boolean useTermFrequency = false;
        // The number of document scores to seed it with (which are randomly
        // chosen from the data).  NOTE: All documents will be loaded to the
        // solver; this specifies how many labels to add to the data.
        int numSeedDocScores = 50;
        // Which feature to rank on
        AnewType whichScore = AnewType.NONE;
        // If zero, there's no middle group.  Middle group separates false 
        // postive from true positive by a middle group of "uncertain"
        double middleGroup = 0.0;
        // Offsets if the scores are not quite zero-centered
        // 0 if you think the center is properly set.
        double offset = -0.02;
        // The power maps more-or-less to the distance that a score spreads.
        // Too high, and the algorithm takes too long, and you won't like the
        // results
        int power = 8;

        // Get all of the ANEW-ranked terms
        // Our ANEW file is a very basic CSV file with the columns as follows:
        // 0 - term, 1 - Word No., 2 - Sentiment mean, 3 - Sentiment SD, 
        // 4 - Arousal mean, 5 - Arousal SD, 6 - Dominance mean, 7 - Dom SD,
        // 8 - Word frequency
        // We parse the term and all of the means
        File anewFile = new File(anewFilename);
        if (!anewFile.exists())
        {
            System.out.println("Can't run without the ANEW file.  The ANEW\n"
                + "sentiment file is expected in a subdirectory of the current\n"
                + "directory as follows: " + anewFilename + "\n");
            System.out.println("The ANEW dataset can be requested from \n"
                + "http://csea.phhp.ufl.edu/media/anewmessage.html.  If you \n"
                + "are the right kind of researcher, they'll share it with \n"
                + "you.");
            return;
        }
        Map<String, AnewData> anewScores = parseAnewFile(anewFilename);

        // Get all of the positive and negative reviews (keep separate for 
        // later scoring of the results)
        File movieReviewDir = new File(movieReviewsPath);
        if (!movieReviewDir.exists())
        {
            System.out.println("Can't run without movie review files.  These\n"
                + "are expected in a subdirectory of the current directory as\n"
                + "follows: " + movieReviewsPath + "\n");
            System.out.println(
                "The movie review dataset can be downloaded from\n"
                + "https://www.cs.cornell.edu/people/pabo/movie-review-data/ \n"
                + "(we used polarity_dataset_v2.0 for testing this code).");
            return;
        }
        Map<String, Map<String, Double>> positiveDocuments = parseReviewFiles(
            movieReviewsPath + "pos/");
        Map<String, Map<String, Double>> negativeDocuments = parseReviewFiles(
            movieReviewsPath + "neg/");

        //
        // BEGIN ACTUAL EXAMPLE CODE
        //
        ValenceSpreader<String, String> solver = new ValenceSpreader<String, String>();
        // Load the documents in the solver
        // NOTE: These documents are loaded w/o scores attached.  If you only
        // have one set of "unknown valence" documents, you would only call
        // loadDocuments once.
        loadDocuments(solver, negativeDocuments, useTermFrequency);
        loadDocuments(solver, positiveDocuments, useTermFrequency);
        // Load the correct term scores in the solver
        if (whichScore != AnewType.NONE)
        {
            for (Map.Entry<String, AnewData> e : anewScores.entrySet())
            {
                switch (whichScore)
                {
                    case SENTIMENT:
                        solver.addWeightedTerm(e.getKey(),
                            e.getValue().sentiment);
                        break;
                    case AROUSAL:
                        solver.addWeightedTerm(e.getKey(), e.getValue().arousal);
                        break;
                    case DOMINANCE:
                        solver.addWeightedTerm(e.getKey(),
                            e.getValue().dominance);
                        break;
                    default:
                        throw new RuntimeException("Unknown term type: "
                            + whichScore + ".  Expected 0 (sentiment), 1 "
                            + "(arousal), or 2 (dominance).");
                }
            }
        }
        // Load a random (as defined by HashMap) set of documents' scores (the
        // same number of postiive and negative)
        // These documents were already loaded above (all terms already in the
        // system).  Here we're just telling the system how to score them.
        int cnt = 0;
        for (String docId : negativeDocuments.keySet())
        {
            solver.addWeightedDocument(docId, -5);
            if ((++cnt > numSeedDocScores))
            {
                break;
            }
        }
        cnt = 0;
        for (String docId : positiveDocuments.keySet())
        {
            solver.addWeightedDocument(docId, 5);
            if ((++cnt > numSeedDocScores))
            {
                break;
            }
        }

        // We need to balance the scores (as ANEW is 0..10, and we want 
        // balanced around 0)
        solver.centerWeightsRange();

        // Now we solve and look at the results!
        ValenceSpreader.Result<String, String> r = solver.spreadValence(power);

        // Now collect the four quadrants of the confusion matrix for positive 
        // and negative with middleGroup as the wide splitting ground
        int[] confusionMatrix =
        {
            0, 0, 0, 0, 0, 0
        };
        final int TRUE_POS = 0;
        final int FALSE_POS = 1;
        final int TRUE_NEG = 2;
        final int FALSE_NEG = 3;
        final int MIDDLE_POS = 4;
        final int MIDDLE_NEG = 5;
        for (String docId : negativeDocuments.keySet())
        {
            if (r.documentWeights.get(docId) - offset < (-1 * middleGroup))
            {
                ++confusionMatrix[TRUE_NEG];
            }
            else if (r.documentWeights.get(docId) - offset > middleGroup)
            {
                ++confusionMatrix[FALSE_POS];
            }
            else
            {
                ++confusionMatrix[MIDDLE_NEG];
            }
        }
        for (String docId : positiveDocuments.keySet())
        {
            if (r.documentWeights.get(docId) - offset > middleGroup)
            {
                ++confusionMatrix[TRUE_POS];
            }
            else if (r.documentWeights.get(docId) - offset < (-1 * middleGroup))
            {
                ++confusionMatrix[FALSE_NEG];
            }
            else
            {
                ++confusionMatrix[MIDDLE_POS];
            }
        }

        // NOTE: I'm not using the resulting word scores herein, but could save
        // them to a CSV file for use as a future classifier.
        System.out.println("Resulting confusion matrix:");
        System.out.println("        | EXPECTED  |");
        System.out.println("        | POS | NEG |");
        System.out.println("RES POS | " + confusionMatrix[TRUE_POS] + " | "
            + confusionMatrix[FALSE_POS] + " |");
        System.out.println("RES MID | " + confusionMatrix[MIDDLE_POS] + " | "
            + confusionMatrix[MIDDLE_NEG] + " |");
        System.out.println("RES NEG | " + confusionMatrix[FALSE_NEG] + " | "
            + confusionMatrix[TRUE_NEG] + " |");
    }

}
