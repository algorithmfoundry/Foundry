/*
 * File:                StudentizedRangeDistribution.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright May 10, 2011, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.statistics.distribution;

import gov.sandia.cognition.algorithm.ParallelUtil;
import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationReferences;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.math.UnivariateStatisticsUtil;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.statistics.AbstractClosedFormUnivariateDistribution;
import gov.sandia.cognition.statistics.ClosedFormCumulativeDistributionFunction;
import gov.sandia.cognition.statistics.InvertibleCumulativeDistributionFunction;
import gov.sandia.cognition.statistics.SmoothUnivariateDistribution;
import gov.sandia.cognition.util.ArgumentChecker;
import gov.sandia.cognition.util.ObjectUtil;
import gov.sandia.cognition.util.Pair;
import gov.sandia.cognition.util.Randomized;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.Callable;

/**
 * Implementation of the Studentized Range distribution, which defines the
 * population correction factor when performing multiple comparisons.  In other
 * words, if you compute the p-value of 20 objects, there's a 65% chance of
 * finding at least one with a p-value of less than 0.05.  The Studentized
 * Range compensates for this chance when conducting multiple comparisons.
 * @author Kevin R. Dixon
 * @since 3.1
 */
@PublicationReferences(
    references={
        @PublicationReference(
            author="David M. Lane",
            title="Studentized Range Distribution",
            type=PublicationType.WebPage,
            year=2011,
            url="http://davidmlane.com/hyperstat/A47912.html"
        )
        ,
        @PublicationReference(
            author="Wikipedia",
            title="Studentized range",
            type=PublicationType.WebPage,
            year=2011,
            url="http://en.wikipedia.org/wiki/Studentized_range"
        )
    }
)
public class StudentizedRangeDistribution
    extends AbstractClosedFormUnivariateDistribution<Double>
    implements Randomized
{

    /**
     * Default treatment count, {@value}.
     */
    public static final int DEFAULT_TREATMENT_COUNT = 2;

    /**
     * Default degrees of freedom, {@value}.
     */
    public static final double DEFAULT_DEGREES_OF_FREEDOM = Double.POSITIVE_INFINITY;

    /**
     * Number of samples to draw for Monte Carlo estimates, {@value}.
     */
    public static final int DEFAULT_NUM_SAMPLES = 1000;

    /**
     * Number of comparisons made
     */
    protected int treatmentCount;

    /**
     * Number of subjects in each treatment minus one.
     */
    protected double degreesOfFreedom;

    /**
     * Random number generator for Monte Carlo simulations
     */
    protected Random random;

    /**
     * Default constructor
     */
    public StudentizedRangeDistribution()
    {
        this( DEFAULT_TREATMENT_COUNT, DEFAULT_DEGREES_OF_FREEDOM );
    }

    /**
     * Creates a new instance of StudentizedRangeDistribution
     * @param treatmentCount
     * Number of comparisons made
     * @param degreesOfFreedom
     * Number of subjects in each treatment minus one.
     */
    public StudentizedRangeDistribution(
        final int treatmentCount,
        final double degreesOfFreedom)
    {
        this.setTreatmentCount(treatmentCount);
        this.setDegreesOfFreedom(degreesOfFreedom);
        this.setRandom( new Random(2) );
    }

    /**
     * Copy constructor
     * @param other
     * StudentizedRangeDistribution to copy
     */
    public StudentizedRangeDistribution(
        final StudentizedRangeDistribution other )
    {
        this( other.getTreatmentCount(), other.getDegreesOfFreedom() );
    }

    @Override
    public StudentizedRangeDistribution clone()
    {
        StudentizedRangeDistribution clone =
            (StudentizedRangeDistribution) super.clone();
        clone.setRandom( ObjectUtil.cloneSmart( this.getRandom() ) );
        return clone;
    }

    @Override
    public ArrayList<Double> sample(
        final Random random,
        final int numSamples)
    {

        ArrayList<SampleRange> tasks = new ArrayList<SampleRange>( numSamples );
        SmoothUnivariateDistribution t;
        if( this.getDegreesOfFreedom() < 30.0 )
        {
            t = new StudentTDistribution(this.getDegreesOfFreedom());
        }
        else
        {
            t = new UnivariateGaussian();
        }
        for( int n = 0; n < numSamples; n++ )
        {
            tasks.add( new SampleRange(random, this.getTreatmentCount(), t) );
        }

        try
        {
            return ParallelUtil.executeInSequence(tasks);
        }
        catch (Exception ex)
        {
            throw new RuntimeException( ex );
        }

    }

    @Override
    public StudentizedRangeDistribution.CDF getCDF()
    {
        return new StudentizedRangeDistribution.CDF( this );
    }

    @Override
    public Double getMean()
    {
        return UnivariateStatisticsUtil.computeMean(
            this.sample(this.getRandom(), DEFAULT_NUM_SAMPLES ) );
    }

    @Override
    public Vector convertToVector()
    {
        return VectorFactory.getDefault().copyValues(
            this.getTreatmentCount(), this.getDegreesOfFreedom() );
    }

    @Override
    public void convertFromVector(
        final Vector parameters)
    {
        parameters.assertDimensionalityEquals(2);
        this.setTreatmentCount( (int) parameters.getElement(0) );
        this.setDegreesOfFreedom( parameters.getElement(1) );
    }

    @Override
    public Double getMinSupport()
    {
        return 0.0;
    }

    @Override
    public Double getMaxSupport()
    {
        return Double.POSITIVE_INFINITY;
    }

    @Override
    public double getVariance()
    {
        return UnivariateStatisticsUtil.computeVariance(
            this.sample(this.getRandom(), 10*DEFAULT_NUM_SAMPLES ) );
    }

    /**
     * Getter for treatmentCount
     * @return
     * Number of comparisons made
     */
    public int getTreatmentCount()
    {
        return this.treatmentCount;
    }

    /**
     * Setter for treatmentCount
     * @param treatmentCount
     * Number of comparisons made
     */
    public void setTreatmentCount(
        final int treatmentCount)
    {
        ArgumentChecker.assertIsInRangeInclusive(
            "treatmentCount", treatmentCount, 2.0, Double.POSITIVE_INFINITY );
        this.treatmentCount = treatmentCount;
    }

    /**
     * Getter for degreesOfFreedom
     * @return
     * Number of subjects in each treatment minus one.
     */
    public double getDegreesOfFreedom()
    {
        return this.degreesOfFreedom;
    }

    /**
     * Setter for degreesOfFreedom
     * @param degreesOfFreedom 
     * Number of subjects in each treatment minus one.
     */
    public void setDegreesOfFreedom(
        final double degreesOfFreedom)
    {
        ArgumentChecker.assertIsPositive("degreesOfFreedom", degreesOfFreedom);
        this.degreesOfFreedom = degreesOfFreedom;
    }

    @Override
    public Random getRandom()
    {
        return this.random;
    }

    @Override
    public void setRandom(
        final Random random)
    {
        this.random = random;
    }

    /**
     * Computes the estimate of the Studentized Range for a single sample
     */
    protected static class SampleRange
        implements Callable<Double>
    {

        /**
         * Number of comparisons being made
         */
        int treatmentCount;

        /**
         * Random number generator
         */
        Random random;

        /**
         * Distribution from which we sample, Student-t or Gaussian
         */
        SmoothUnivariateDistribution t;

        /**
         * Creates a new instance of SampleRange
         * @param random
         * Random number generator
         * @param treatmentCount
         * Number of comparisons made
         * @param t
         * Distribution from which we sample, Student-t or Gaussian
         */
        public SampleRange(
            final Random random,
            final int treatmentCount,
            final SmoothUnivariateDistribution t )
        {
            this.random = random;
            this.treatmentCount = treatmentCount;
            this.t = t;
        }

        @Override
        public Double call()
            throws Exception
        {
            ArrayList<? extends Double> means =
                this.t.sample( this.random, this.treatmentCount );
            Pair<Double,Double> result =
                UnivariateStatisticsUtil.computeMinAndMax(means);
            double delta = result.getSecond() - result.getFirst();
            means = null;
            return delta;
        }

    }

    /**
     * CDF of the StudentizedRangeDistribution
     */
    public static class CDF
        extends StudentizedRangeDistribution
        implements ClosedFormCumulativeDistributionFunction<Double>,
        InvertibleCumulativeDistributionFunction<Double>
    {

        /**
         * Default constructor
         */
        public CDF()
        {
            super();
        }

        /**
         * Creates a new instance of StudentizedRangeDistribution
         * @param treatmentCount
         * Number of comparisons made
         * @param degreesOfFreedom
         * Number of subjects in each treatment minus one.
         */
        public CDF(
            final int treatmentCount,
            final double degreesOfFreedom)
        {
            super( treatmentCount, degreesOfFreedom );
        }

        /**
         * Copy constructor
         * @param other
         * StudentizedRangeDistribution to copy
         */
        public CDF(
            final StudentizedRangeDistribution other )
        {
            super( other );
        }


        @Override
        public Double evaluate(
            final Double input)
        {
            return APStat.prtrng(input, this.getDegreesOfFreedom(), this.getTreatmentCount() );
        }

        @Override
        public Double inverse(
            final double probability)
        {
            return APStat.qtrng(probability, this.getDegreesOfFreedom(), this.getTreatmentCount() );
        }

        @Override
        public StudentizedRangeDistribution.CDF getCDF()
        {
            return this;
        }

    }

    /**
     * This is a translation of Fortran code taken from
     * http://lib.stat.cmu.edu/apstat/, and the comments on the individual functions
     * in this class are taken directly from the original.
     */
    public static class APStat
    {
        /**
         * Algorithm AS66 Applied Statistics (1973) vol22 no.3.
         *
         * Evaluates the tail area of the standardised normal curve from x to
         * infinity if upper is .true. or from minus infinity to x if upper is
         * .false.
         *
         * @param x
         *            Location for which to compute tail area
         * @param upper
         *            True to find upper tail area, false to find lower tail area
         * @return Tail area for given x
         */
        public static double alnorm(double x, boolean upper) {
            if (x < 0) {
                x = -x;
                upper = !upper;
            }

            double y = 0.5 * x * x;
            double alnorm;

            if ((x > 7.0) && !(upper && x <= 18.66)) {
                alnorm = 0;
            } else if (x <= 1.28) {
                alnorm = 0.5
                        - x
                        * (0.398942280444 - 0.39990348504
                                * y
                                / (y + 5.75885480458 - 29.8213557807 / (y + 2.62433121679 + 48.6959930692 / (y + 5.92885724438))));
            } else {
                alnorm = 0.398942280385
                        * Math.exp(-y)
                        / (x - 3.8052e-8 + 1.00000615302 / (x + 3.98064794e-4 + 1.98615381364 / (x - 0.151679116635 + 5.29330324926 / (x + 4.8385912808 - 15.1508972451 / (x + 0.742380924027 + 30.789933034 / (x + 3.99019417011))))));
            }

            return upper ? alnorm : 1 - alnorm;
        }

        /**
         * ALGORITHM AS 111, APPL.STATIST., VOL.26, 118-121, 1977.
         *
         * PRODUCES NORMAL DEVIATE CORRESPONDING TO LOWER TAIL AREA = P.
         *
         * See also AS 241 which contains alternative routines accurate to about 7
         * and 16 decimal digits.
         *
         * @param p
         *            P-value for which to compute normal deviate
         * @return Normal deviate corresponding to lower tail area = p
         */
        public static double ppnd(double p) {
            double q = p - 0.5;

            // p < 0.08 or p > 0.92, set r = min(p,1-p)
            if (Math.abs(q) > 0.42) {
                double r = Math.min(p, 1 - p);

                if (r <= 0) {
                    throw new IllegalArgumentException("Invalid p value: " + p);
                }

                r = Math.sqrt(-Math.log(r));

                return (q < 0 ? -1 : 1)
                        * (((2.32121276858 * r + 4.85014127135) * r - 2.29796479134)
                                * r - 2.78718931138)
                        / ((1.63706781897 * r + 3.54388924762) * r + 1);
            }

            // 0.08 < p < 0.92
            else {
                double r = q * q;

                return q
                        * (((-25.44106049637 * r + 41.39119773534) * r - 18.61500062529)
                                * r + 2.50662823884)
                        / ((((3.13082909833 * r - 21.06224101826) * r + 23.08336743743)
                                * r - 8.47351093090)
                                * r + 1);
            }
        }

        /**
         * Algorithm AS 190 Appl Statist (1983) Vol.32, No.2. Incorporates
         * corrections from Appl. Statist. (1985) Vol.34 (1)
         *
         * Evaluates the probability from 0 to q for a studentized range having v
         * degrees of freedom and r samples.
         *
         * Uses subroutine ALNORM = algorithm AS66.
         *
         * Arrays vw and qw store transient values used in the quadrature summation.
         * Node spacing is controlled by step. pcutj and pcutk control truncation.
         * Minimum and maximum number of steps are controlled by jmin, jmax, kmin
         * and kmax. Accuracy can be increased by use of a finer grid - Increase
         * sizes of arrays vw and qw, and jmin, jmax, kmin, kmax and 1/step
         * proportionally.
         *
         * @param q
         *            Quantile for which to find p-value
         * @param v
         *            Degrees of freedom for distribution
         * @param r
         *            Number of samples for distribution
         * @return P-value for q for given distribution
         */
        public static double prtrng(double q, double v, double r) {
            final double pcutj = 0.00003;
            final double pcutk = 0.0001;
            final double step = 0.45;
            final double vmax = 120.0;
            final int jmin = 3;
            final int jmax = 15;
            final int kmin = 7;
            final int kmax = 15;

            // Check initial values

            double prtrng = 0;

            if (v < 1) {
                throw new IllegalArgumentException(
                        "Degrees of freedom must be >= 1.");
            }

            if (r < 2) {
                throw new IllegalArgumentException(
                        "Number of samples must be >= 2.");
            }

            if (q <= 0) {
                return prtrng;
            }

            // Computing constants, local midpoint, adjusting steps.

            double g = step * Math.pow(r, -0.2);
            double gmid = 0.5 * Math.log(r);
            double r1 = r - 1;
            double c = Math.log(r * g * 0.39894228);
            double h = 0;
            double v2 = 0;

            if (v <= vmax) {
                h = step * Math.pow(v, -0.5);
                v2 = v * 0.5;

                if (v == 1) {
                    c = 0.193064705;
                } else if (v == 2) {
                    c = 0.293525326;
                } else {
                    c = Math.sqrt(v2)
                            * 0.318309886
                            / (1 + ((-0.268132716e-2 / v2 + 0.347222222e-2) / v2 + 0.833333333e-1)
                                    / v2);
                }

                c = Math.log(c * r * g * h);
            }

            // Computing integral
            // Given a row k, the procedure starts at the midpoint and works
            // outward (index j) in calculating the probability at nodes
            // symmetric about the midpoint. The rows (index k) are also
            // processed outwards symmetrically about the midpoint. The
            // centre row is unpaired.

            double gstep = g;
            double[] qw = new double[30];
            double[] vw = new double[30];

            qw[0] = -1;
            qw[jmax] = -1;

            double pk1 = 1;
            double pk2 = 1;

            for (int k = 1; k <= kmax; ++k) {
                gstep -= g;

                do {
                    gstep = -gstep;

                    double gk = gmid + gstep;
                    double pk = 0;

                    if (pk2 > pcutk || k <= kmin) {
                        double w0 = c - gk * gk * 0.5;
                        double pz = alnorm(gk, true);
                        double x = alnorm(gk - q, true) - pz;

                        if (x > 0) {
                            pk = Math.exp(w0 + r1 * Math.log(x));
                        }

                        if (v <= vmax) {
                            int jump = -jmax;

                            do {
                                jump = jump + jmax;

                                for (int j = 1; j <= jmax; ++j) {
                                    int jj = j + jump;

                                    if (qw[jj - 1] <= 0) {
                                        double hj = h * j;

                                        if (j < jmax) {
                                            qw[jj] = -1;
                                        }

                                        double ehj = Math.exp(hj);

                                        qw[jj - 1] = q * ehj;
                                        vw[jj - 1] = v
                                                * (hj + 0.5 - ehj * ehj * 0.5);
                                    }

                                    double pj = 0;
                                    x = alnorm(gk - qw[jj - 1], true) - pz;

                                    if (x > 0) {
                                        pj = Math.exp(w0 + vw[jj - 1] + r1
                                                * Math.log(x));
                                    }

                                    pk += pj;

                                    if (pj > pcutj) {
                                        continue;
                                    }

                                    if (jj > jmin || k > kmin) {
                                        break;
                                    }
                                }

                                h = -h;
                            } while (h < 0);
                        }
                    }

                    prtrng = prtrng + pk;

                    if (k > kmin && pk <= pcutk && pk1 <= pcutk) {
                        return prtrng;
                    }

                    pk2 = pk1;
                    pk1 = pk;
                } while (gstep > 0);
            }

            return prtrng;
        }

        /**
         * Algorithm AS 190.1 Appl Statist (1983) Vol.32, No.2.
         *
         * Approximates the quantile p for a studentized range distribution having v
         * degrees of freedom and r samples for probability p, p.ge.0.90 .and.
         * p.le.0.99.
         *
         * Uses functions alnorm, ppnd, prtrng and qtrng0 - Algorithms AS 66, AS
         * 111, AS 190 and AS 190.2
         *
         * @param p
         *            P-value for which to find quantile
         * @param v
         *            Degrees of freedom for distribution
         * @param r
         *            Number of samples for distribution
         * @return Quantile at p for given distribution
         */
        public static double qtrng(double p, double v, double r) {
            final int jmax = 8;
            final double pcut = 0.001;
            final double eps = 1e-4;

            // Check input parameters

            if (v < 1) {
                throw new IllegalArgumentException(
                        "Degrees of freedom must be >= 1.");
            }

            if (r < 2) {
                throw new IllegalArgumentException(
                        "Number of samples must be >= 2.");
            }

            if (p < 0.9 || p > 0.99) {
                throw new IllegalArgumentException(
                        "P-value must be in range [0.9,0.99].");
            }

            // Obtain initial values

            double q1 = qtrng0(p, v, r);
            double p1 = prtrng(q1, v, r);
            double q2 = 0;
            double p2 = 0;
            double qtrng = q1;

            if (Math.abs(p1 - p) < pcut) {
                return qtrng;
            }

            if (p1 > p) {
                p1 = 1.75 * p - 0.75 * p1;
            }

            if (p1 < p) {
                p2 = p + (p - p1) * (1 - p) / (1 - p1) * 0.75;
            }

            if (p2 < 0.8) {
                p2 = 0.8;
            }

            if (p2 > 0.995) {
                p2 = 0.995;
            }

            q2 = qtrng0(p2, v, r);

            // Refine approximation

            double e1 = 0;
            double e2 = 0;
            double d = 0;

            for (int j = 2; j <= jmax; ++j) {
                p2 = prtrng(q2, v, r);
                e1 = p1 - p;
                e2 = p2 - p;
                qtrng = (q1 + q2) / 2;
                d = e2 - e1;

                if (Math.abs(d) > eps) {
                    qtrng = (e2 * q1 - e1 * q2) / d;
                }

                if (Math.abs(e1) >= Math.abs(e2)) {
                    q1 = q2;
                    p1 = p2;
                }

                if (Math.abs(p1 - p) < pcut * 5) {
                    return qtrng;
                }

                q2 = qtrng;
            }

            return qtrng;
        }

        /**
         * Algorithm AS 190.2 Appl Statist (1983) Vol.32, No.2.
         *
         * Calculates an initial quantile p for a studentized range distribution
         * having v degrees of freedom and r samples for probability p, p.gt.0.80
         * .and. p.lt.0.995.
         *
         * Uses function ppnd - Algorithm AS 111
         *
         * @param p
         *            P-value for which to find initial quantile
         * @param v
         *            Degrees of freedom for distribution
         * @param r
         *            Number of samples for distribution
         * @return Initial quantile at p for given distribution
         */
        public static double qtrng0(double p, double v, double r) {
            final double vmax = 120;

            double t = ppnd(0.5 + 0.5 * p);

            if (v < vmax) {
                t += (t * t * t + t) / v / 4;
            }

            double q = 0.8843 - 0.2368 * t;

            if (v < vmax) {
                q += -1.214 / v + 1.208 * t / v;
            }

            return t * (q * Math.log(r - 1) + 1.4142);
        }

    }

}
