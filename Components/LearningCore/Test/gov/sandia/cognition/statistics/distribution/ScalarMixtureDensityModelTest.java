/*
 * File:                ScalarMixtureModel.java
 * Authors:             jdmorr
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright August 7, 2006, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.statistics.distribution;

import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.statistics.ClosedFormCumulativeDistributionFunction;
import gov.sandia.cognition.statistics.ClosedFormScalarDistribution;
import gov.sandia.cognition.statistics.ProbabilityDensityFunction;
import gov.sandia.cognition.statistics.SmoothScalarDistributionTestHarness;
import gov.sandia.cognition.statistics.SmoothScalarDistribution;
import gov.sandia.cognition.statistics.distribution.ScalarMixtureDensityModel.SoftLearner;
import gov.sandia.cognition.statistics.method.KolmogorovSmirnovConfidence;
import gov.sandia.cognition.util.DefaultWeightedValue;
import gov.sandia.cognition.util.ObjectUtil;
import gov.sandia.cognition.util.WeightedValue;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

/**
 * Test class for the ScalarMixtureDensityModel
 *
 * @author jdmorr
 */
public class ScalarMixtureDensityModelTest
    extends SmoothScalarDistributionTestHarness
{
    private ScalarMixtureDensityModel mixtureModel = null;
    private ArrayList<SmoothScalarDistribution> distributions;
    private Vector priorProbabilities;
    private final double mean1 = 0.0, mean2 = 5.0, var1 = 1.0, var2 = 2.0;

    public ScalarMixtureDensityModelTest(String testName)
    {
        super(testName);
    }


    /**
     * setUp allocates a mixtureModel composed of two univariate gaussians
     * for use in the tests the follow.
     *
     * @throws Exception
     */
    @Override
    protected void setUp()
        throws Exception
    {
        super.setUp();

        this.distributions = new ArrayList<SmoothScalarDistribution>(2);
        this.distributions.add(new UnivariateGaussian.PDF(this.mean1, this.var1));
        this.distributions.add(new UnivariateGaussian.PDF(this.mean2, this.var2));

        this.priorProbabilities = VectorFactory.getDefault().createVector(
            this.distributions.size(), 1.0 / this.distributions.size());

        this.priorProbabilities.setElement(0, 0.7);
        this.priorProbabilities.setElement(1, 0.3);

        this.mixtureModel = new ScalarMixtureDensityModel(
            this.distributions,
            this.priorProbabilities);

    }

    @Override
    protected void tearDown()
        throws Exception
    {
        super.tearDown();
    }

    /**
     * tests various versions of the constructor(s) with different errors
     * in the parameters.  Notably: an empty distribution list, a mismatch in
     * the size of the distribution list with the probability vector, and a
     * probabliity vector that does not add to 1.0.
     */
    @Override
    public void testDistributionConstructors()
    {
        System.out.println("test constructors");

        ScalarMixtureDensityModel smdm;

        Collection<? extends SmoothScalarDistribution> distList = new ArrayList<SmoothScalarDistribution>() ;

        try {
            smdm = new ScalarMixtureDensityModel(distList);
            fail("Did not catch empty distribution list in constructor");
        }
        catch ( IllegalArgumentException e ) {
            System.out.println("Good: " + e );
        }

        ArrayList<SmoothScalarDistribution> aList = new ArrayList<SmoothScalarDistribution>(3);
        aList.add(new UnivariateGaussian(0.0,1.0));
        aList.add(new UnivariateGaussian(1.0,2.0));
        aList.add(new UnivariateGaussian(10.0,0.5));

        Vector pp = VectorFactory.getDefault().createVector(2,0.5);

        try {
            smdm = new ScalarMixtureDensityModel(aList,pp);
            fail("did not catch dimensionality mismatch");
        } catch (Exception e ) {
            System.out.println("Good: " + e );
        }

        pp = VectorFactory.getDefault().createVector(3, 0.4);
        try {
            smdm = new ScalarMixtureDensityModel(aList,pp);
            fail("did not catch incorrect probabilities (not summing to 1.0)");
        } catch (Exception e ) {
            System.out.println("Good: " + e );
        }

        smdm = new ScalarMixtureDensityModel(aList,null);
        assertEquals( smdm.getPriorProbabilities().getElement(0), 1.0/3);


    }

    /**
     * Tests degenerate arguments to buildSMDMfromGaussians method.
     */
    public void testBuildSMDMfromGaussians()
    {
        double mean[] = {0.0, 1.0};
        double var[] = {1.0, 2.0, 3.0};
        double pp[] = {0.1, 0.2, 0.3, 0.4};

        System.out.println("buildSMDMfromGaussians");

        try {
        ScalarMixtureDensityModel smdm = ScalarMixtureDensityModel.buildSMDMfromGaussians
                (mean,
                var,
                pp);
        fail("allowed ScalarMixtureDensityModel to be built from incompatible arguments.");
        }
        catch ( Exception e )
        {
            System.out.println("Good: " + e );
        }

        /*
         * now verify that it catches an invalid variance in the array
         */
        double mean1[] = {0.0,1.0,2.0};
        double var1[] = {0.0, -1.0, 2.0};
        double pp1[] = {0.3,0.3,0.4};

        try {
        ScalarMixtureDensityModel smdm = ScalarMixtureDensityModel.buildSMDMfromGaussians
                (mean1,
                var1,
                pp1);
        fail("allowed ScalarMixtureDensityModel to be built from incompatible arguments.");
        }
        catch ( Exception e )
        {
            System.out.println("Good: " + e );
        }

    }

    /**
     * Test of computeRandomVariableLikelihoods method, of class ScalarMixtureDensityModel.
     */
    public void testComputeRandomVariableLikelihoods()
    {
        System.out.println("computeRandomVariableLikelihoods");
        Double input = 2.3;
        ScalarMixtureDensityModel instance = this.mixtureModel;
        Vector expResult = VectorFactory.getDefault().createVector(2);
        expResult.setElement(0,
            instance.getDistributions().get(0).getProbabilityFunction().evaluate(input));
        expResult.setElement(1,
            instance.getDistributions().get(1).getProbabilityFunction().evaluate(input));
        Vector result = instance.computeRandomVariableLikelihoods(input);
        assertEquals(expResult, result);
    }

    /**
     * Test of getMean method, of class ScalarMixtureDensityModel.
     */
    public void testGetMean()
    {
        System.out.println("getMean");
        ScalarMixtureDensityModel instance = this.mixtureModel;
        int N = instance.getNumDistributions();
        double mean = 0.0;
        for (int i = 0; i < N; i++)
        {
            mean += instance.getDistributions().get(i).getMean() *
                this.priorProbabilities.getElement(i);
        }
        Double result = instance.getMean();
        assertEquals(mean, result, this.TOLERANCE);
        System.out.println("mean = " + mean);
        System.out.println("pp = " + this.priorProbabilities);
    }

    /**
     * Test of getMeanVector method, of class ScalarMixtureDensityModel.
     */
    public void testGetMeanVector()
    {
        System.out.println("getMeanVector");
        ScalarMixtureDensityModel instance = this.mixtureModel;
        Vector expResult = VectorFactory.getDefault().createVector(2);
        expResult.setElement(0, this.mean1);
        expResult.setElement(1, this.mean2);
        Vector result = instance.getMeanVector();
        assertEquals(expResult, result);
        System.out.println("mean vector = " + result);
    }

    /**
     * Test of computeVariance method, of class ScalarMixtureDensityModel.
     */
    public void testComputeVariance()
    {
        System.out.println("computeVariance");
        ScalarMixtureDensityModel instance = this.mixtureModel;
        Double expResult = 12.325; // based on paramters in setup
        Double result = instance.computeVariance();
        assertEquals(expResult, result, this.TOLERANCE);
    }

    /**
     * test of computeWeightedZsquared(), of class ScalarMixtureDensityModel
     */
    public void testComputeWeightedZsquared()
    {
        System.out.println("computeWeightedZsquared");
        ScalarMixtureDensityModel instance = this.mixtureModel;
        Double input = 1.5;

        // based on paramters in setup

        Double z = this.priorProbabilities.getElement(0) *
                (input-this.mean1)/Math.sqrt(this.var1) ;
        z += this.priorProbabilities.getElement(1) *
                (input-this.mean2)/Math.sqrt(this.var2);
        Double expResult = z*z;
        Double result = instance.computeWeightedZsquared(input);

        assertEquals(expResult, result, this.TOLERANCE);
    }

    /**
     * test computeWeightedZ, of class ScalarMixtureDensityModel
     */
    public void testComputeWeightedZ()
    {
        System.out.println("computeWeightedZsquared");
        ScalarMixtureDensityModel instance = this.mixtureModel;
        Double input = -1.33;

        // based on paramters in setup

        Double z = this.priorProbabilities.getElement(0) *
                (input-this.mean1)/Math.sqrt(this.var1) ;
        z += this.priorProbabilities.getElement(1) *
                (input-this.mean2)/Math.sqrt(this.var2);
        Double expResult = z;
        Double result = instance.computeWeightedZ(input);

        assertEquals(expResult, result, this.TOLERANCE);
    }

    /**
     * Test of sample method, of class ScalarMixtureDensityModel.
     */
    public void testSample()
    {

        System.out.println("sample");
        Random random = new Random(1);
        int numDraws = 1;
        ScalarMixtureDensityModel instance = this.mixtureModel;
        ArrayList<Double> expResult = new ArrayList<Double>();
        expResult.add(4.416839163843252);
        ArrayList<Double> result = instance.sample(random, numDraws);
        assertEquals(expResult, result);

        /*
         * test trying to sample from a degenerate mixture model
         * with no distributions.
         *
         */
        instance = instance.clone();
        instance.setDistributions(null);

        try {
            instance.sample(random,numDraws);
            fail("Able to try to sample from degenerate mixture model");
        } catch ( Exception e )
        {
            System.out.println("Good: " + e );
        }

    }

    /**
     * Test of clone method, of class ScalarMixtureDensityModel.
     */
    public void testClone()
    {
        System.out.println("clone");
        ScalarMixtureDensityModel instance = this.mixtureModel;
        ScalarMixtureDensityModel result = instance.clone();

        // ensure that the top-level objects are not the same objects
        assertNotSame( result, instance );
        
        // make sure key parameters are equal in value between the original
        // and the clone
        assertEquals( instance.computeVariance(), result.computeVariance());
        assertEquals( instance.getMean(), result.getMean() );
        assertEquals( instance.getNumDistributions(), result.getNumDistributions());
        assertEquals( instance.getPriorProbabilities(), result.getPriorProbabilities());

        // make sure that the arrays and objects are not the same ones between
        // the original and the clone
        assertNotSame( instance.getCDF(), result.getCDF());
        assertNotSame( instance.getProbabilityFunction(), result.getProbabilityFunction());
        assertNotSame( instance.getPriorProbabilities(), result.getPriorProbabilities());
        assertNotSame( instance.getDistributions(), result.getDistributions());
    }


    /**
     * Test of getDistributions method, of class ScalarMixtureDensityModel.
     */
    public void testGetDistributions()
    {
        System.out.println("getDistributions");
        ScalarMixtureDensityModel instance = this.mixtureModel;
        ArrayList<SmoothScalarDistribution> expResult = this.distributions;
        ArrayList<? extends SmoothScalarDistribution> result =
            instance.getDistributions();
        assertEquals(expResult, result);
    }

    /**
     * Test of setDistributions method, of class ScalarMixtureDensityModel.
     */
    public void testSetDistributions()
    {
        System.out.println("setDistributions");
        ArrayList<SmoothScalarDistribution> dists = this.distributions;
        ScalarMixtureDensityModel instance = this.mixtureModel;
        instance.setDistributions(dists);
        assertEquals(dists, instance.getDistributions());
    }

    /**
     * Test of getPriorProbabilities method, of class ScalarMixtureDensityModel.
     */
    public void testGetPriorProbabilities()
    {
        System.out.println("getPriorProbabilities");
        ScalarMixtureDensityModel instance = this.mixtureModel;
        Vector expResult = this.priorProbabilities;
        Vector result = instance.getPriorProbabilities();
        assertEquals(expResult, result);
    }

    /**
     * Test of setPriorProbabilities method, of class ScalarMixtureDensityModel.
     */
    public void testSetPriorProbabilities()
    {
        System.out.println("setPriorProbabilities");
        Vector pp = this.priorProbabilities;
        ScalarMixtureDensityModel instance = this.mixtureModel;
        instance.setPriorProbabilities(pp);
        assertEquals(pp, instance.getPriorProbabilities());
    }

    /**
     * Test of getNumDistributions method, of class ScalarMixtureDensityModel.
     */
    public void testGetNumDistributions()
    {
        System.out.println("getNumDistributions");
        ScalarMixtureDensityModel instance = this.mixtureModel;
        int expResult = 2;  // based on setup parameters
        int result = instance.getNumDistributions();
        assertEquals(expResult, result);
    }

    /**
     * Test of getVariance method, of class ScalarMixtureDensityModel.
     */
    public void testGetVariance()
    {
        System.out.println("getVariance");
        ScalarMixtureDensityModel instance = this.mixtureModel;
        double expResult = 12.325;  // based on setup parameters
        double result = instance.getVariance();
        assertEquals(expResult, result, this.TOLERANCE);
    }

    /**
     * Test of convertToVector method, of class ScalarMixtureDensityModel.
     */
    public void testConvertToVector()
    {
        System.out.println("convertToVector");
        ScalarMixtureDensityModel instance = this.mixtureModel;
        Vector expResult = this.priorProbabilities;
        Vector result = instance.convertToVector();
        assertEquals(expResult, result);
    }

    /**
     * Test of convertFromVector method, of class ScalarMixtureDensityModel.
     */
    public void testConvertFromVector()
    {
        System.out.println("convertFromVector");
        Vector parameters = VectorFactory.getDefault().createVector(2);
        parameters.setElement(0, 0.1);
        parameters.setElement(1, 0.9);
        ScalarMixtureDensityModel instance = this.mixtureModel;
        instance.convertFromVector(parameters);
        Vector result = instance.convertToVector();
        assertEquals(parameters, result);
    }


    /*
     * this evaluates the learned model using three KS tests.
     * One looks at the data generated by this model against the original model.
     * One looks at the data generated by this model against the original data.
     * One looks at the data generated by this model against a Gaussian assumption.
     *
     * @param learnedModel  -- ScalarMixtureDensityModel learned from generated data
     * @param originalModel -- The original ScalarMixtureDensityModel built from parameters
     * @param originalData  -- ArrayList<Double> The generated data from the originalModel
     * @param expectedMatch -- boolean whether or not you expect a match between the
     *                         original data and the learnedModel
     */
    private void evaluateLearnedModel(
        ScalarMixtureDensityModel learnedModel,
        ScalarMixtureDensityModel originalModel,
        ArrayList<Double> originalData,
        boolean expectedMatch)
    {
        final double significance = 0.005;

        ArrayList<Double> dataFromLearnedModel = learnedModel.sample(
                this.RANDOM,
                this.NUM_SAMPLES);

//        double nullHypProbGaussian =
//            KolmogorovSmirnovConfidence.evaluateGaussianHypothesis(originalData).getNullHypothesisProbability();
        double nullHypProbData = KolmogorovSmirnovConfidence.INSTANCE.evaluateNullHypothesis(
            originalData,
            dataFromLearnedModel).getNullHypothesisProbability();
        double nullHypProbOrigDataLearnedCDF = KolmogorovSmirnovConfidence.evaluateNullHypothesis(
            originalData,
            learnedModel.getCDF()).getNullHypothesisProbability();
        double nullHypProbLearnedDataOrigCDF = KolmogorovSmirnovConfidence.evaluateNullHypothesis(
            dataFromLearnedModel,
            originalModel.getCDF()).getNullHypothesisProbability();

        System.out.println("Learned ScalarMixtureDensityModel\n" + learnedModel);


        // we do not expect this to look gaussian...
//        System.out.println("ks null hyp prob: " + nullHypProbGaussian);
//        System.out.println("ks data null hyp prob: " + nullHypProbData);
//        System.out.println("ks orig Data / learned CDF null hyp prob: " +
//            nullHypProbOrigDataLearnedCDF);
//        System.out.println("ks learned Data / original CDF null hyp prob: " +
//            nullHypProbLearnedDataOrigCDF);
//
//        System.out.println("KS orig data / learned CDF");
//        System.out.println(ObjectUtil.toString(KolmogorovSmirnovConfidence.evaluateNullHypothesis(
//            originalData, learnedModel.getCDF())));
//        System.out.println("KS learned data / original CDF");
//        System.out.println(ObjectUtil.toString(KolmogorovSmirnovConfidence.evaluateNullHypothesis(
//            dataFromLearnedModel, originalModel.getCDF())));


//        assertTrue(nullHypProbGaussian < significance);

        if (expectedMatch)
        {
            assertTrue(nullHypProbData > significance);
            assertTrue(nullHypProbOrigDataLearnedCDF > significance);
            if ( !(nullHypProbLearnedDataOrigCDF > significance))
            {
                System.out.println("*** Learned Data against Original CDF failed significance **** ");
                System.out.println(ObjectUtil.toString(KolmogorovSmirnovConfidence.evaluateNullHypothesis(
            dataFromLearnedModel, originalModel.getCDF())));
            }
//            assertTrue( nullHypProbDataOrigCDF > significance );

        }
        else
        {
            assertTrue(nullHypProbData <= significance);
            assertTrue(nullHypProbOrigDataLearnedCDF <= significance);
//            assertTrue( nullHypProbLlearnedDataOrigCDF <= significance );
        }
    }



    /**
     * tests the MixtureDensity model by using two gaussians to generate
     * and fit the data to.
     */
    public void testSoftLearnerWithGaussians()
    {
        System.out.println("===========================");
        System.out.println("SoftLearner with Gaussians");

        /**
         * define 2-gaussian model
         */
        double mean[] = { 0.0, 10.0 };
        double var[] = {1.0, 1.0};
        double pp[] = { 0.8, 0.2 };
        testWithGaussians( mean, var, pp, 500, 3 );

        mean[0] = 0;
        mean[1] = 5;
        var[0] = 1;
        var[1] = 4;
        pp[0] = 0.3;
        pp[1] = 0.7;
        testWithGaussians( mean,var,pp,500,3);

        double mean3[] = {0.0,3.0,6.0};
        double var3[] = {1.0, 2.0, 3.0};
        double pp3[] = {0.1, 0.3, 0.6};

        testWithGaussians( mean3, var3, pp3, 500, 3 );
        
    }

    /**
     * a utility function for this test class which:
     *
     *   1) builds a ScalarMixtureDensityModel from the incoming parameters
     *      mean, var, pp
     *   2) generates some data using said model
     *   3) learns an n-gaussian (n depends on # of distributions used to generate
     *       the model and subsequent data) ScalarMixtureDensityModel from
     *       the generated data.
     *   4) evaluates the learned model against the generated data and the
     *       parametric model
     *
     * steps 3 & 4 are repeated numIterations times.
     *
     * @param mean  -- a vector of means for the gaussian distributions
     * @param var   -- a vector of variances for the gaussian distributions
     * @param pp    -- a vector of priorProbabilities for the gaussian distributions
     *
     * @param numSamples  -- the integer number of samples to generate with the
     *                       parameterized ScalarMixtureDensityModel
     * @param numIterations -- the integer number of times to re-learn a model
     *                         from the SAME generated data and perform the
     *                         evaluation.
     */
    private void testWithGaussians(
            double mean[],
            double var[],
            double pp[],
            int numSamples,
            int numIterations )
    {
        this.NUM_SAMPLES = numSamples;

        ArrayList<Double> data2g;
        ScalarMixtureDensityModel smdm2g =
                ScalarMixtureDensityModel.buildSMDMfromGaussians(mean, var, pp);
        data2g = smdm2g.sample(this.RANDOM, this.NUM_SAMPLES);// generate some data

        int numDistributions = smdm2g.getNumDistributions();

        ScalarMixtureDensityModel.SoftLearner smdm2gLearner =
            SoftLearner.buildSMDMmultiGaussianLearner(numDistributions);

        ScalarMixtureDensityModel learnedSMDM2g;

        System.out.println("ScalarMixtureDensityModel Gaussian Test");
        System.out.println("Constructed model:\n" + smdm2g);
//        System.out.println("\tmean: " + mean );
//        System.out.println("\tvar: " + var );
//        System.out.println("\tpp: " + pp );

        for (int i = 0; i < numIterations; i++)
        {
            System.out.println("--------------------");
            learnedSMDM2g = smdm2gLearner.learn(data2g);
            evaluateLearnedModel(learnedSMDM2g, smdm2g, data2g, true);

        }
        System.out.println("--------------------");
    }

    /**
     * tests learning a scalar mixture density distribution
     * made up of 1 Gaussian and 1 Laplace distribution
     */
    public void testSoftLearnerWithGaussianAndLaplace()
    {
        double pp[] = { 0.2, 0.8 };
        int numSamples = 500;
        int numIterations = 3;

        testWithGaussianAndLaplace(
                new UnivariateGaussian(0.0,1.0),
                new LaplaceDistribution(3.0, 1.0),
                pp, 
                numSamples,
                numIterations);

        pp[0] = 0.85;
        pp[1] = 0.15;
        testWithGaussianAndLaplace(
                new UnivariateGaussian(0.0,1.0),
                new LaplaceDistribution(2.0, 1.0),
                pp,
                numSamples,
                numIterations);
    }

    /**
     * utility function to test a scalar mixture density model that is based
     * on a single gaussian and single Laplace distribution
     *
     * 1) A scalar mixture density model is created based on the passed in
     *    gaussian and laplace distributions
     * 2) data is generated from that specific model (numSamples worth)
     * 3) we learn a scalar mixture density model (based on gaussian/laplace
     *    softlearner) using the generated data
     * 4) evaluate the learned model against the actual model and the generated
     *    data
     *
     * repeat steps 3 and 4 numIterations times (using same generated data)
     *
     * @param gaussian -- univariate gaussian distribution
     * @param laplace  -- laplace distribution
     * @param pp       -- prior probabilities
     * @param numSamples -- # of data points to generate with smdm model
     * @param numIterations -- number of times to learn and evaluate an smdm model
     */
    public void testWithGaussianAndLaplace(
            UnivariateGaussian gaussian,
            LaplaceDistribution laplace,
            double pp[],
            int numSamples,
            int numIterations )
    {
        ScalarMixtureDensityModel smdm =
                ScalarMixtureDensityModel.
                buildSMDMfromGaussianAndLaplace(gaussian, laplace, pp);

        this.NUM_SAMPLES = numSamples ;

        ArrayList<Double> genData = smdm.sample(this.RANDOM, this.NUM_SAMPLES);

        ScalarMixtureDensityModel.SoftLearner smdmLearner =
                SoftLearner.buildSmdmGaussianLaplaceLearner();

        ScalarMixtureDensityModel learnedSmdm;

        System.out.println("ScalarMixtureDensityModel Gaussian and Laplace Test");
        System.out.println("Constructed model:\n" + smdm);


        for (int i = 0; i < numIterations; i++)
        {
            System.out.println("--------------------");
            learnedSmdm = smdmLearner.learn(genData);
            evaluateLearnedModel(learnedSmdm, smdm, genData, true);

        }
        System.out.println("--------------------");

    }


    /**
     * returns a ScalarMixtureDensityModel instance
     * @return
     */
    @Override
    public ScalarMixtureDensityModel createInstance()
    {
        return new ScalarMixtureDensityModel();
    }

    /**
     * tested against matlab results using pdfnorm
     */
    @Override
    public void testPDFKnownValues()
    {
        // ProbabilityDensityFunction<Double> pdf = createPDFInstance();
        ProbabilityDensityFunction<Double> pdf =
            new ScalarMixtureDensityModel.PDF(this.mixtureModel);

        assertEquals(0.279422967598279, pdf.evaluate(0.0), this.TOLERANCE);
        assertEquals(0.074871303008531, pdf.evaluate(5.7), this.TOLERANCE);
        assertEquals(0.119962166165605, pdf.evaluate(-1.3), this.TOLERANCE);

    }

    /**
     * test the CDF convertToVector routine.  It converts the priorProbabilites
     * to a vector.
     */
    @Override
    public void testKnownConvertToVector()
    {
        ScalarMixtureDensityModel.CDF cdf =
            new ScalarMixtureDensityModel.CDF(
            this.distributions,
            this.priorProbabilities);

        assertEquals(priorProbabilities, cdf.convertToVector());
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * these are tested against MATLAB results using cdfnorm
     */
    @Override
    public void testCDFKnownValues()
    {
        ScalarMixtureDensityModel.CDF cdf =
            new ScalarMixtureDensityModel.CDF(
            this.distributions,
            this.priorProbabilities);
        assertEquals(0.3500610428078071, cdf.evaluate(0.0), this.TOLERANCE);
        assertEquals(0.633573006281660, cdf.evaluate(1.3), this.TOLERANCE);
        assertEquals(0.215991370396509, cdf.evaluate(-0.5), this.TOLERANCE);
        assertEquals(0.906907303841087, cdf.evaluate(5.7), this.TOLERANCE);
        assertEquals(0.999996686425450, cdf.evaluate(11.0), this.TOLERANCE);


    }

    /**
     * Tests CDF.convertFromVector
     */
    @Override
    public void testCDFConvertFromVector()
    {
        System.out.println( "CDF.convertFromVector" );

        ClosedFormScalarDistribution<Double> instance = this.createInstance();
        ClosedFormCumulativeDistributionFunction<Double> cdf = instance.getCDF();

        Vector x1 = cdf.convertToVector();
        assertNotNull( x1 );
        int N = x1.getDimensionality();

        // Create a new parameterization of the CDF
        // (try using positive parameters as some distributions need
        // positive parameters)
        final double r = 2.0;
        Vector y1 = VectorFactory.getDefault().createUniformRandom( N, 0.0, r, this.RANDOM );

        double sum = y1.sum();
        y1.scaleEquals(1.0/sum);

        cdf.convertFromVector( y1 );
        Vector y2 = cdf.convertToVector();
        assertNotNull( y2 );
        assertNotSame( y1, y2 );
        assertEquals( y1, y2 );

        // Convert back to the original parameterization
        cdf.convertFromVector( x1 );
        Vector x2 = cdf.convertToVector();
        assertNotNull( x2 );
        assertNotSame( x1, x2 );
        assertEquals( x1, x2 );

        try
        {
            cdf.convertFromVector( null );
            fail( "Cannot convert from null Vector" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }

        try
        {
            Vector z1 = VectorFactory.getDefault().createUniformRandom( N-1, r, r, this.RANDOM );
            cdf.convertFromVector( z1 );
            fail( "Cannot convert from a N-1 dimension Vector!" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }

        try
        {
            Vector z1 = VectorFactory.getDefault().createUniformRandom( N+1, r, r, this.RANDOM );
            cdf.convertFromVector( z1 );
            fail( "Cannot convert from a N+1 dimension Vector!" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }


    }

/**
 *  SoftLearner tests.
 *
 *  Tests the default creation parameters.
 *  Tests the set/get of RANDOM.
 *
 */
    public void testSoftLearner()
    {
        Random rnd = new Random(1);

        System.out.println("ScalarMixtureDensityModel SoftLearner");

        ScalarMixtureDensityModel.SoftLearner smdmSoftLearner = new ScalarMixtureDensityModel.SoftLearner();

        assertEquals( smdmSoftLearner.getMaxIterations(), ScalarMixtureDensityModel.SoftLearner.DEFAULT_MAX_ITERATIONS);
        assertEquals( smdmSoftLearner.numDistributions, 2 );

        smdmSoftLearner.setRandom(rnd);
        assertEquals( smdmSoftLearner.getRandom(), rnd );
    }


    /**
     * test the getAnchorPoints method in the SoftLearner.
     */
    public void testGetAnchorPoints()
    {
        ScalarMixtureDensityModel.SoftLearner smdmSoftLearner = new ScalarMixtureDensityModel.SoftLearner();

        System.out.println("ScalarMixtureDensityModel SoftLearner getAnchorPoints");


        Collection<Double> dummyData = new ArrayList<Double>(5);
        dummyData.add(1.0);
        dummyData.add(2.0);
        dummyData.add(3.0);
        dummyData.add(4.0);
        dummyData.add(5.0);
        smdmSoftLearner.setData(dummyData);

        Double[] anchors;

        anchors = smdmSoftLearner.getAnchorPoints(2);
        for(int i=0; i<2; i++)
        {
            assertTrue( anchors[i]<=5.0 && anchors[i]>=1.0 );
        }
        assertTrue( anchors[0] != anchors[1]);

        /*
         *  try to force a re-sampling by asking for as many
         *  anchors as there are points.  For the last point, there
         *  will be only a 20% chance of not needing to re-sample.
         */
        ScalarMixtureDensityModel.SoftLearner.DEBUG = true;
        anchors = smdmSoftLearner.getAnchorPoints(5);
        ScalarMixtureDensityModel.SoftLearner.DEBUG = false;
        

        try {
                smdmSoftLearner.getAnchorPoints(6);
                fail("Did not catch numberLearners > numberDataPoints");
        } 
        catch ( Exception e )
        {
            System.out.println("Good: " + e );
        }       
    }

    /**
     *  test the convertToWeighedData method in the SoftLearner.
     */
    public void testConvertToWeightedData()
    {
        ScalarMixtureDensityModel.SoftLearner smdmSoftLearner =
                new ScalarMixtureDensityModel.SoftLearner();

        System.out.println("ScalarMixtureDensityModel SoftLearner convertToWeightedData");

        Vector weights = VectorFactory.getDefault().createVector(3);
        weights.setElement(0, 1.0);
        weights.setElement(1, 2.0);
        weights.setElement(2, 3.0);

        Collection<Double> data = new ArrayList<Double>(2);
        data.add(10.0);
        data.add(20.0);


        try {
            smdmSoftLearner.convertToWeightedData(weights, data);
            fail("Mismatch in weights and data dimensionality not caught");
        } catch ( Exception e ) {
            System.out.println("Good: " + e );
        }

        data = new ArrayList<Double>(3);
        data.add(10.0);
        data.add(20.0);
        data.add(30.0);

        ArrayList<DefaultWeightedValue<Double>> wdata =
                smdmSoftLearner.convertToWeightedData(weights, data);

        int i = 1;
        for( WeightedValue<Double> wv : wdata )
        {
            assertEquals( wv.getValue(), i*10.0);
            assertEquals( wv.getWeight(), i*1.0);
            i++;
        }
    }

    /**
     * test getKernel method.
     */
    public void testGetKernel()
    {
        ScalarMixtureDensityModel.SoftLearner smdmSoftLearner =
                new ScalarMixtureDensityModel.SoftLearner();

        System.out.println("ScalarMixtureDensityModel SoftLearner testSetKernel");

        assertEquals( smdmSoftLearner.getKernel(),
                      ScalarMixtureDensityModel.SoftLearner.DEFAULT_KERNEL );
    }

    /**
     * test various PDF constructors.
     */
    public void testPDFconstructors()
    {
        ScalarMixtureDensityModel smdm =
                new ScalarMixtureDensityModel.PDF();
        assertEquals( smdm.getNumDistributions(), 2 );

        Collection<? extends SmoothScalarDistribution> distList = new ArrayList<UnivariateGaussian>() ;

        try {
            smdm = new ScalarMixtureDensityModel.PDF(distList);
            fail("Did not catch empty distribution list in PDF constructor");
        }
        catch ( IllegalArgumentException e ) {
            System.out.println("Good: " + e );
        }

        ArrayList<SmoothScalarDistribution> arrayList = new ArrayList<SmoothScalarDistribution>();
        arrayList.add( new UnivariateGaussian(0.0,1.0) );
        arrayList.add( new UnivariateGaussian(1.0,2.0) );

        smdm = new ScalarMixtureDensityModel.PDF( arrayList, null) ;

        assertEquals( smdm.getNumDistributions(), 2);
        assertEquals( smdm.getPriorProbabilities().getElement(1), 0.5);
    }

    /**
     * test various CDF constructors.
     */
    public void testCDFconstructors()
    {
        ScalarMixtureDensityModel smdm =
                new ScalarMixtureDensityModel.CDF();
        assertEquals( smdm.getNumDistributions(), 2 );

        Collection<SmoothScalarDistribution> distList = new ArrayList<SmoothScalarDistribution>() ;

        try {
            smdm = new ScalarMixtureDensityModel.CDF(distList);
            fail("Did not catch empty distribution list in CDF constructor");
        }
        catch ( IllegalArgumentException e ) {
            System.out.println("Good: " + e );
        }

        ArrayList<SmoothScalarDistribution> arrayList = new ArrayList<SmoothScalarDistribution>();
        arrayList.add( new UnivariateGaussian(0.0,1.0) );
        arrayList.add( new UnivariateGaussian(1.0,2.0) );

        smdm = new ScalarMixtureDensityModel.CDF( arrayList, null) ;

        assertEquals( smdm.getNumDistributions(), 2);
        assertEquals( smdm.getPriorProbabilities().getElement(1), 0.5);
    }

    /**
     * test the PerformanceData class
     */
    public void testPerformanceData()
    {
        ArrayList<SmoothScalarDistribution> distArray1 =
                new ArrayList<SmoothScalarDistribution>();
        distArray1.add(new UnivariateGaussian(0.0,1.0));
        distArray1.add(new UnivariateGaussian(1.0,2.0));

        ArrayList<SmoothScalarDistribution> distArray2 =
                new ArrayList<SmoothScalarDistribution>();
        distArray2.add(new UnivariateGaussian(0.1,1.0));
        distArray2.add(new UnivariateGaussian(1.0,2.0));


        Vector pp1 = VectorFactory.getDefault().createVector(2, 0.5);

        ScalarMixtureDensityModel.PerformanceData pd1 =
                new ScalarMixtureDensityModel.PerformanceData(distArray1,pp1);
        ScalarMixtureDensityModel.PerformanceData pd2 =
                new ScalarMixtureDensityModel.PerformanceData();
        pd2.updateData(pd1);

        // verify for equal parameters, the metric is 0.0
        assertEquals( pd1.computePerformanceMetric(pd2), 0.0);

        // verify the metric computation for different means
        pd2 = new ScalarMixtureDensityModel.PerformanceData(distArray2, pp1);
        assertEquals( pd1.computePerformanceMetric(pd2),0.1);

        // verify the metric computations for different variances
        distArray2.clear();
        distArray2.add( new UnivariateGaussian(0.0, 1.0));
        distArray2.add( new UnivariateGaussian(1.0, 1.5));
        pd2 = new ScalarMixtureDensityModel.PerformanceData(distArray2, pp1);
        assertEquals( pd1.computePerformanceMetric(pd2), 0.5/Math.sqrt(5.0));

        // verify the metrix computation for different probabilities
        Vector pp2 = VectorFactory.getDefault().createVector(2);
        pp2.setElement(0, 0.45);
        pp2.setElement(1, 0.55);
        pd2.updateData(distArray1, pp2);
        assertEquals( pd1.computePerformanceMetric(pd2),
                pd1.getPriorProbabilities().euclideanDistance(pd2.getPriorProbabilities()) /
                pd1.getPriorProbabilities().norm2()) ;
        assertEquals( pd1.computePerformanceMetric(pd2),
                Math.sqrt(2*Math.pow(0.05,2))/Math.sqrt(2*Math.pow(0.5,2)));

        pd2 = new ScalarMixtureDensityModel.PerformanceData();
        try {
            pd2.computePerformanceMetric(pd1);
            fail("did not catch invalid performanceData object");
        } catch ( Exception e ) {
            System.out.println("Good: " + e );
        }

        // now verify that we catch if we try to updateData from an invalid
        // PerformanceData object
        try {
            // pd2 is not valid
            pd1.updateData(pd2);
            fail("did not catch invalid data use in updateData");
        } catch ( Exception e ) {
            System.out.println("Good: "+ e);
        }

    }

    @Override
    public void testPDFConstructors()
    {
    }

    @Override
    public void testCDFConstructors()
    {
    }
    
}
