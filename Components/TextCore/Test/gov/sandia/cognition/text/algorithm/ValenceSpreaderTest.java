
package gov.sandia.cognition.text.algorithm;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * A couple of basic tests. These are most useful for running and looking at the
 * results to try to understand what the algorithm does. They are very hard to
 * make "realistic" enough to truly test that the spread is done correctly. So,
 * don't be surprised when some of the results are different than you might
 * expect.
 *
 * NOTE: For a more robust test, look at the ValenceAnewExample code in the
 * Examples project.
 *
 * @author jdwendt
 */
public class ValenceSpreaderTest
{

    /**
     * Loads the input terms into a set and returns the set.
     *
     * @param terms The terms to add
     * @return A set composed of all of the input terms
     */
    private static Set<String> toSet(String... terms)
    {
        Set<String> ret = new HashSet<String>(terms.length);
        for (String s : terms)
        {
            ret.add(s);
        }

        return ret;
    }

    /**
     * Simple helper that creates four "nice" and four "mean" documents. As each
     * document is very short, there may be insufficient connectivity to
     * properly spread values.
     *
     * @param v The solver to put the data into
     */
    private static void fillData(ValenceSpreader<String, String> v)
    {
        // Note that the terms overlap a bit in the different nice and mean groups
        v.addDocumentTermOccurrences("nice1", toSet("awesome", "cool",
            "fantastic", "buddy", "the", "of"));
        v.addDocumentTermOccurrences("nice2", toSet("bff", "friends", "amigos",
            "compadres", "with", "the", "of"));
        v.addDocumentTermOccurrences("nice3", toSet("awesome", "friends",
            "compadre", "amigo", "the", "of"));
        v.addDocumentTermOccurrences("nice4", toSet("cool", "amigo",
            "compadre", "amigo", "the", "of"));
        v.addDocumentTermOccurrences("mean1", toSet("jerk", "bum", "freak",
            "meany", "the", "of"));
        v.addDocumentTermOccurrences("mean2", toSet("moron", "dummy", "loser",
            "dimwit", "the", "of"));
        v.addDocumentTermOccurrences("mean3", toSet("bum", "loser", "zombie",
            "braindead", "the", "of"));
        v.addDocumentTermOccurrences("mean4", toSet("freak", "dummy", "zombie",
            "braindead", "the", "of"));
    }

    /**
     * Prints the input map to standard out.
     *
     * @param scores The map to print
     */
    private void printMap(Map<String, Double> scores)
    {
        for (Map.Entry<String, Double> e : scores.entrySet())
        {
            System.out.println(e.getKey() + " --> " + e.getValue());
        }
    }

    /**
     * A simple test that creates a set of "documents", assigns scores to a few
     * terms, and then prints the resulting scores from the full algorithm.
     */
    @Test
    public void basicTermSpreadTest()
    {
        System.out.println("Term Test!!");
        ValenceSpreader<String, String> v = new ValenceSpreader<String, String>();
        fillData(v);
        v.addWeightedTerm("jerk", -1);
        v.addWeightedTerm("moron", -1);
        v.addWeightedTerm("meany", -1);
        v.addWeightedTerm("bff", 1);
        v.addWeightedTerm("awesome", 1);
        v.addWeightedTerm("fantastic", 1);

        ValenceSpreader.Result<String, String> r = v.spreadValence();
        System.out.println("Documents:");
        printMap(r.documentWeights);
        System.out.println("Terms:");
        printMap(r.termWeights);

        // Make sure the documents connected to the labeled terms get the same
        // valence range
        assertTrue(r.documentWeights.get("nice2") >= .9);
        assertTrue(r.documentWeights.get("nice1") >= .9);
        assertTrue(r.documentWeights.get("mean2") <= -.9);
        assertTrue(r.documentWeights.get("mean1") <= -.9);
        // Make sure companion words in the same documents get similar valences
        assertTrue(r.termWeights.get("bum") <= -.5);
        assertTrue(r.termWeights.get("freak") <= -.5);
        assertTrue(r.termWeights.get("dummy") <= -.5);
        assertTrue(r.termWeights.get("loser") <= -.5);
        assertTrue(r.termWeights.get("cool") >= .5);
        assertTrue(r.termWeights.get("buddy") >= .5);
        assertTrue(r.termWeights.get("friends") >= .5);
        assertTrue(r.termWeights.get("fantastic") >= .5);
    }

    /**
     * A simple test that creates a set of "documents", assigns scores to a few
     * documents, and then prints the resulting scores from the full algorithm.
     */
    @Test
    public void basicDocumentSpreadTest()
    {
        System.out.println("Document Test!!");
        ValenceSpreader<String, String> v = new ValenceSpreader<String, String>();
        fillData(v);
        v.addWeightedDocument("mean1", -1);
        v.addWeightedDocument("mean2", -1);
        v.addWeightedDocument("mean4", -1);
        v.addWeightedDocument("nice1", 1);
        v.addWeightedDocument("nice2", 1);
        v.addWeightedDocument("nice4", 1);

        ValenceSpreader.Result<String, String> r = v.spreadValence();
        System.out.println("Documents:");
        printMap(r.documentWeights);
        System.out.println("Terms:");
        printMap(r.termWeights);

        // Make sure the terms connected to the labeled documents get the same
        // valence range
        assertTrue(r.termWeights.get("bum") <= -.4);
        assertTrue(r.termWeights.get("freak") <= -.4);
        assertTrue(r.termWeights.get("jerk") <= -.4);
        assertTrue(r.termWeights.get("moron") <= -.4);
        assertTrue(r.termWeights.get("dummy") <= -.4);
        assertTrue(r.termWeights.get("loser") <= -.4);
        assertTrue(r.termWeights.get("cool") >= .4);
        assertTrue(r.termWeights.get("awesome") >= .4);
        assertTrue(r.termWeights.get("fantastic") >= .4);
        assertTrue(r.termWeights.get("bff") >= .4);
        assertTrue(r.termWeights.get("friends") >= .4);
        assertTrue(r.termWeights.get("amigos") >= .4);
        // Make sure companion documents get similar valences
        assertTrue(r.documentWeights.get("nice3") >= .4);
        assertTrue(r.documentWeights.get("mean3") <= -.4);
    }

}
