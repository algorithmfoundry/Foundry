
package gov.sandia.cognition.testutil;

import gov.sandia.cognition.math.matrix.Vector;
import java.util.Collection;

import static org.junit.Assert.*;

/**
 * Class contains helper methods for tests
 */
public class AssertUtil
{

    /**
     * Returns the position of the most significant digit of d.
     *
     * @param d The number to test for position of most significant digit
     * @return the position of the most significant digit
     */
    public static long mostSignificantDigitPosition(double d)
    {
        double nDigits = Math.log10(Math.abs(d));
        if ((nDigits == (int) nDigits) && (nDigits >= 0))
        {
            nDigits += 1;
        }
        return (d == 0) ? 0 : (nDigits > 0) ? (long) Math.ceil(nDigits)
            : (long) Math.floor(nDigits);
    }

    /**
     * Rounds the input double to n significant digits. This code started from
     * an example on
     * http://stackoverflow.com/questions/202302/rounding-to-an-arbitrary-number-of-significant-digits
     *
     * @param d The number to round
     * @param n The number of digits to round to
     * @return d rounded to n digits
     */
    public static double roundToNumDigits(double d,
        int n)
    {
        if (d == 0)
        {
            return 0;
        }

        final long numDs = mostSignificantDigitPosition(d);
        final long power = n - ((numDs > 0) ? numDs : (numDs + 1));

        final double mag = Math.pow(10, power);
        final long shifted = Math.round(d * mag);

        return shifted / mag;
    }

    /**
     * Tests that d1 and d2 are the same number to numDigits significant digits.
     * This is different from junit's assertEquals that includes the precision
     * required in that this requires a certain number of digits be the same
     * instead of specifying the exact precision required (1 digit of precision
     * between 12,000 and 11,000 is different from equal to the 1's position).
     *
     * @param d1 The first number to test
     * @param d2 The second number to test
     * @param numDigits The number of significant digits that d1 and d2 must be
     * equal for the assert to pass
     */
    public static void equalToNumDigits(double d1,
        double d2,
        int numDigits)
    {
        assertEquals(roundToNumDigits(d1, numDigits), roundToNumDigits(d2,
            numDigits), 1e-15);
    }

    /**
     * Tests that d1 and d2 are the same number to numDigits significant digits.
     * This is different from junit's assertEquals that includes the precision
     * required in that this requires a certain number of digits be the same
     * instead of specifying the exact precision required (1 digit of precision
     * between 12,000 and 11,000 is different from equal to the 1's position).
     *
     * @param errString The string to print on an error
     * @param d1 The first number to test
     * @param d2 The second number to test
     * @param numDigits The number of significant digits that d1 and d2 must be
     * equal for the assert to pass
     */
    public static void equalToNumDigits(String errString,
        double d1,
        double d2,
        int numDigits)
    {
        assertEquals(errString, roundToNumDigits(d1, numDigits),
            roundToNumDigits(d2, numDigits), 1e-15);
    }

    /**
     * Checks that all elements in c1 are in c2 and all elements in c2 are in
     * c1. Optionally tests that they are the same size. NOTE: If these are
     * lists with repeated elements, there is no guarantee that they have the
     * same number of each element. There are no order checks.
     *
     * @param c1 First collection
     * @param c2 Second collection
     * @param sameSize If true, tests that the two inputs are the same size
     */
    public static <Type> void equalContents(Collection<Type> c1,
        Collection<Type> c2,
        boolean sameSize)
    {
        if (sameSize)
        {
            assertEquals(c1.size(), c2.size());
        }
        for (Type t : c1)
        {
            assertTrue(c2.contains(t));
        }
        for (Type t : c2)
        {
            assertTrue(c1.contains(t));
        }
    }

}
