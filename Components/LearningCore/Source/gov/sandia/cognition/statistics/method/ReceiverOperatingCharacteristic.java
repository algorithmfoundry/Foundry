/*
 * File:                ReceiverOperatingCharacteristic.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright August 24, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.statistics.method;

import gov.sandia.cognition.learning.performance.categorization.DefaultBinaryConfusionMatrix;
import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.collection.CollectionUtil;
import gov.sandia.cognition.learning.data.InputOutputPair;
import gov.sandia.cognition.learning.function.categorization.ScalarThresholdBinaryCategorizer;
import gov.sandia.cognition.statistics.distribution.UnivariateGaussian;
import gov.sandia.cognition.evaluator.Evaluator;
import gov.sandia.cognition.learning.data.DefaultInputOutputPair;
import gov.sandia.cognition.util.AbstractCloneableSerializable;
import gov.sandia.cognition.util.ObjectUtil;
import gov.sandia.cognition.util.Pair;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;

/**
 * Class that describes a Receiver Operating Characteristic (usually called an
 * "ROC Curve").  This is a function that describes the performance of a
 * classification system where the x-axis is the FalsePositiveRate and the
 * y-axis is the TruePositiveRate.  Both axes are on the interval [0,1].  A
 * typical ROC curve has a logarithm-shaped plot, ideally it looks like a
 * capital Gamma letter.  An ROC curve also has an associated group of
 * statistics with it from a Mann-Whitney U-test, which gives the probability
 * that the classifier is essentially randomly "guessing."  We create ROC curves 
 * by calling the method: ReceiverOperatingCharacteristic.create(data)
 *
 * @author Kevin R. Dixon
 * @since  2.0
 *
 */
@PublicationReference(
    author="Wikipedia",
    title="Receiver operating characteristic",
    type=PublicationType.WebPage,
    year=2009,
    url="http://en.wikipedia.org/wiki/Receiver_operating_characteristic"
)
public class ReceiverOperatingCharacteristic
    extends AbstractCloneableSerializable
    implements Evaluator<Double,Double>
{
    
    /**
     * Sorted data containing a ConfusionMatrix at each point, sorted in an
     * ascending order along the abscissa (x-axis), which is FalsePositiveRate
     */
    private ArrayList<ReceiverOperatingCharacteristic.DataPoint> sortedROCData;
    
    /**
     * Results from conducting a U-test on the underlying classification data,
     * the null hypothesis determines if the classifier can reliably separate
     * the classes, not just chance
     */
    private MannWhitneyUConfidence.Statistic Utest;
    
    /**
     * Creates a new instance of ReceiverOperatingCharacteristic
     * @param rocData 
     * Sorted data containing a ConfusionMatrix at each point
     * @param Utest 
     * Results from conducting a U-test on the underlying classification data,
     * the null hypothesis determines if the classifier can reliably separate
     * the classes, not just chance
     */
    private ReceiverOperatingCharacteristic(
        Collection<ReceiverOperatingCharacteristic.DataPoint> rocData,
        MannWhitneyUConfidence.Statistic Utest )
    {
        ArrayList<ReceiverOperatingCharacteristic.DataPoint> sortedData =
            CollectionUtil.asArrayList(rocData);
        
        Collections.sort( sortedData, new DataPoint.Sorter() );
        
        this.setSortedROCData( sortedData );
        this.setUtest( Utest );
    }


    @Override
    public ReceiverOperatingCharacteristic clone()
    {
        ReceiverOperatingCharacteristic clone =
            (ReceiverOperatingCharacteristic) super.clone();

        clone.setSortedROCData( ObjectUtil.cloneSmartElementsAsArrayList(
            this.getSortedROCData() ) );
        clone.setUtest( ObjectUtil.cloneSmart( this.getUtest() ) );
        return clone;

    }

    /**
     * Evaluates the "pessimistic" value of the truePositiveRate for a given
     * falsePositiveRate.  This evaluation is pessimistic in that it holds
     * the truePositiveRate (y-value) until we receive a corresponding
     * falsePositiveRate (x-value) that is greater than the given value
     * @param input 
     * falsePositiveRate from which to estimate the truePositiveRate
     * @return 
     * Pessimistic TruePositiveRate for the given FalsePositiveRate
     */
    @Override
    public Double evaluate(
        Double input )
    {
        double falsePositiveRate = input;
        
        double truePositiveRate = 0.0;
        for( DataPoint rocData : this.getSortedROCData() )
        {
            if( rocData.getFalsePositiveRate() <= falsePositiveRate )
            {
                truePositiveRate = rocData.getTruePositiveRate();
            }
            else
            {
                break;
            }
        }
        
        return truePositiveRate;
        
    }
    
    /**
     * Getter for sortedROCData
     * @return 
     * Sorted data containing a ConfusionMatrix at each point, sorted in an
     * ascending order along the abscissa (x-axis), which is FalsePositiveRate
     */
    public ArrayList<ReceiverOperatingCharacteristic.DataPoint> getSortedROCData()
    {
        return this.sortedROCData;
    }
    
    /**
     * Setter for srtedROCData
     * @param sortedROCData 
     * Sorted data containing a ConfusionMatrix at each point, sorted in an
     * ascending order along the abscissa (x-axis), which is FalsePositiveRate
     */
    protected void setSortedROCData(
        ArrayList<ReceiverOperatingCharacteristic.DataPoint> sortedROCData)
    {
        this.sortedROCData = sortedROCData;
    }

    /**
     * Creates an ROC curve based on the scored data with target information.
     *
     * @param data
     *      Collection of target/estimate-score pairs.  The second element in 
     *      the pair is an estimated score, the first is a flag to determine 
     *      which group the score belongs to.  For example:
     *      {(true, 1.0), (false, 0.9)}
     *      means that data1=1.0 and data2=0.9 and so forth.  This is useful
     *      for computing that classified data partitions data better than
     *      chance.
     * @return
     *      ROC Curve describing the scoring system versus the targets.
     */
    public static ReceiverOperatingCharacteristic createFromTargetEstimatePairs(
        final Collection<? extends Pair<Boolean, ? extends Number>> data)
    {
        // Transform the data to input-output pairs.
        final ArrayList<InputOutputPair<Double, Boolean>> transformed =
            new ArrayList<InputOutputPair<Double, Boolean>>(data.size());

        for (Pair<Boolean, ? extends Number> entry : data)
        {
            transformed.add(DefaultInputOutputPair.create(
                entry.getSecond().doubleValue(), entry.getFirst()));
        }

        return create(transformed);
    }
    
    /**
     * Creates an ROC curve based on the scored data with target information
     * @param data 
     * Collection of estimate-score/target pairs.  The second element in the
     * Pair is an estimated score, the first is a flag to determine which
     * group the score belongs to.  For example {<1.0,true>, <0.9,false> means
     * that data1=1.0 and data2=0.9 and so forth.  This is useful for computing
     * that classified data partitions data better than chance.
     * @return 
     * ROC Curve describing the scoring system versus the targets
     */
    public static ReceiverOperatingCharacteristic create(
        Collection<? extends InputOutputPair<Double,Boolean>> data)
    {
        // First we need to sort the data in increasing order by value.
        ArrayList<InputOutputPair<Double,Boolean>> sortedData =
            new ArrayList<InputOutputPair<Double,Boolean>>(data);
        
        Collections.sort(sortedData, new ROCScoreSorter());
        
        // Next we need to count the total number of positive examples.
        int totalPositives = 0;
        for ( InputOutputPair<Double,Boolean> pair : sortedData )
        {
            if ( pair.getOutput() == true )
            {
                totalPositives++;
            }
        }
        
        // Now we compute the total and the number of negatives.
        int total = sortedData.size();
        int totalNegatives = total - totalPositives;
        
        // We will be computing the confusion matrix iteratively, which means
        // keeping track of how many examples we have counted so far and the
        // number of positive examples counted so far.
        int countSoFar = 0;
        int positivesSoFar = 0;
        LinkedList<DataPoint> rocData =
            new LinkedList<DataPoint>();
        double lastThreshold = Double.NEGATIVE_INFINITY;
        for ( InputOutputPair<Double,Boolean> pair : sortedData )
        {
            // Compute the confusion matrix based on the current counters.
            final double trueNegatives = countSoFar - positivesSoFar;
            final double falseNegatives = positivesSoFar; 
            final double truePositives = totalPositives - falseNegatives;
            final double falsePositives = totalNegatives - trueNegatives;
            
            final double threshold = pair.getInput();
            if ( threshold > lastThreshold )
            {
                // Only add a data point if we have evaluated a new
                // threshold.
                final DefaultBinaryConfusionMatrix confusion =
                    new DefaultBinaryConfusionMatrix();
                confusion.setFalsePositivesCount(falsePositives);
                confusion.setFalseNegativesCount(falseNegatives);
                confusion.setTruePositivesCount(truePositives);
                confusion.setTrueNegativesCount(trueNegatives);
                rocData.add(new DataPoint(
                    new ScalarThresholdBinaryCategorizer(threshold), confusion));
                lastThreshold = threshold;
            }
            
            // Update the count so far and the positives so far. This is done
            // after the computations of the counts because the threshold is
            // x >= threshold, so the threshold puts the current point on the
            // positive side.
            countSoFar++;
            
            final boolean target = pair.getOutput();
            if ( target == true )
            {
                positivesSoFar++;
            }
        }

        Collections.sort(rocData, new ReceiverOperatingCharacteristic.DataPoint.Sorter());

        // Compute a statistical test on the data.
        MannWhitneyUConfidence.Statistic uTest =
            new MannWhitneyUConfidence().evaluateNullHypothesis(data);
        
        // Return the ROC.
        return new ReceiverOperatingCharacteristic(rocData, uTest);
    }
    
    
    /**
     * Computes useful statistical information associated with the ROC curve
     * @return ROC Statistics describing the ROC curve
     */
    public ReceiverOperatingCharacteristic.Statistic computeStatistics()
    {
        return new ReceiverOperatingCharacteristic.Statistic( this );
    }
    
    /**
     * Getter for Utest
     * @return 
     * Results from conducting a U-test on the underlying classification data,
     * the null hypothesis determines if the classifier can reliably separate
     * the classes, not just chance
     */
    public MannWhitneyUConfidence.Statistic getUtest()
    {
        return this.Utest;
    }
    
    /**
     * Setter for Utest
     * @param Utest 
     * Results from conducting a U-test on the underlying classification data,
     * the null hypothesis determines if the classifier can reliably separate
     * the classes, not just chance
     */
    public void setUtest(
        MannWhitneyUConfidence.Statistic Utest)
    {
        this.Utest = Utest;
    }
    
    
    /**
     * Contains useful statistics derived from a ROC curve
     */
    public static class Statistic
        extends MannWhitneyUConfidence.Statistic
    {
        
        /**
         * Estimated distance between the two classes to be split.  Larger 
         * values of d' indicate that the classes are easier to split,
         * d'=0 means that the classes overlap, and negative values mean
         * that your classifier is doing worse than chance, chump.  This
         * appears to only be used by psychologists.
         */
        private double dPrime;
        
        /**
         * Area underneath the ROC curve, on the interval [0,1].  A value of
         * 0.5 means that the classifier is doing no better than chance and
         * bigger is better
         */
        private double areaUnderCurve;
        
        /**
         * DataPoint, with corresponding threshold, that maximizes the value
         * of Area=TruePositiveRate*(1-FalsePositiveRate), usually the 
         * upper-left "knee" on the ROC curve
         */
        private DataPoint optimalThreshold;
        
        /**
         * Creates a new instance of Statistic
         * @param roc 
         * ROC Curve from which to pull statistics
         */
        protected Statistic(
            ReceiverOperatingCharacteristic roc )
        {
            super( roc.getUtest() );
         
            this.setAreaUnderCurve( computeAreaUnderCurve( roc ) );
            this.setOptimalThreshold( computeOptimalThreshold( roc ) );
            this.setDPrime( computeDPrime( this.getOptimalThreshold() ) );
        }
        
        
        /**
         * Computes the "pessimistic" area under the ROC curve using the
         * top-left rectangle method for numerical integration.
         * @param roc 
         * ROC Curve to compute the area under
         * @return 
         * Area underneath the ROC curve, on the interval [0,1].  A value of
         * 0.5 means that the classifier is doing no better than chance and
         * bigger is better
         */
        public static double computeAreaUnderCurve(
            ReceiverOperatingCharacteristic roc )
        {
            return computeAreaUnderCurveTopLeft( roc.getSortedROCData() );
        }

        /**
         * Computes the Area Under Curve for an x-axis sorted Collection
         * of ROC points using the top-left rectangle method for numerical
         * integration.
         * @param points
         * x-axis sorted collection of x-axis points
         * @return
         * Area underneath the ROC curve, on the interval [0,1].  A value of
         * 0.5 means that the classifier is doing no better than chance and
         * bigger is better
         */
        @PublicationReference(
            author="Wikipedia",
            title="Rectangle method",
            type=PublicationType.WebPage,
            year=2011,
            url="http://en.wikipedia.org/wiki/Rectangle_method"
        )
        public static double computeAreaUnderCurveTopLeft(
            Collection<ReceiverOperatingCharacteristic.DataPoint> points )
        {
            ReceiverOperatingCharacteristic.DataPoint current =
                CollectionUtil.getFirst(points);
            double auc = 0.0;
            double xnm1 = 0.0;
            double ynm1 = 0.0;
            double xn = 0.0;
            for( ReceiverOperatingCharacteristic.DataPoint point : points )
            {
                // Technically, this wastes the computation of the first point,
                // but since the delta is 0.0, it doesn't effect the AUC.
                ReceiverOperatingCharacteristic.DataPoint previous = current;
                previous = current;
                current = point;

                xnm1 = previous.getFalsePositiveRate();
                ynm1 = previous.getTruePositiveRate();
                xn = current.getFalsePositiveRate();

                final double area = ynm1*(xn-xnm1);
                auc += area;

            }

            // Assume that the final point is at xn=1.0
            xnm1 = xn;
            xn = 1.0;
            final double area = ynm1*(xn-xnm1);
            auc += area;
            return auc;
        }

        /**
         * Computes the Area Under Curve for an x-axis sorted Collection
         * of ROC points using the top-left rectangle method for numerical
         * integration.
         * @param points
         * x-axis sorted collection of x-axis points
         * @return
         * Area underneath the ROC curve, on the interval [0,1].  A value of
         * 0.5 means that the classifier is doing no better than chance and
         * bigger is better
         */
        @PublicationReference(
            author="Wikipedia",
            title="Trapezoidal rule",
            type=PublicationType.WebPage,
            year=2011,
            url="http://en.wikipedia.org/wiki/Trapezoidal_rule"
        )
        public static double computeAreaUnderCurveTrapezoid(
            Collection<ReceiverOperatingCharacteristic.DataPoint> points )
        {
            ReceiverOperatingCharacteristic.DataPoint current =
                CollectionUtil.getFirst(points);
            double auc = 0.0;
            double xnm1 = 0.0;
            double ynm1 = 0.0;
            double yn = 0.0;
            double xn = 0.0;
            for( ReceiverOperatingCharacteristic.DataPoint point : points )
            {
                // Technically, this wastes the computation of the first point,
                // but since the delta is 0.0, it doesn't effect the AUC.
                ReceiverOperatingCharacteristic.DataPoint previous = current;
                previous = current;
                current = point;

                xnm1 = previous.getFalsePositiveRate();
                ynm1 = previous.getTruePositiveRate();
                xn = current.getFalsePositiveRate();
                yn = current.getTruePositiveRate();

                final double area = (xn-xnm1) * (yn+ynm1) / 2.0;
                auc += area;

            }

            // Assume that the final point is at xn=1.0
            xnm1 = xn;
            xn = 1.0;
            yn = 1.0;
            final double area = (xn-xnm1) * (yn+ynm1) / 2.0;
            auc += area;
            return auc;
        }

        /**
         * Determines the DataPoint, and associated threshold, that
         * simultaneously maximizes the value of
         * Area=TruePositiveRate+TrueNegativeRate, usually the 
         * upper-left "knee" on the ROC curve.
         * 
         * @param roc
         * ROC Curve to consider
         * @return DataPoint, with corresponding threshold, that maximizes the value
         * of Area=TruePositiveRate*(1-FalsePositiveRate), usually the 
         * upper-left "knee" on the ROC curve.
         */
        public static DataPoint computeOptimalThreshold(
            ReceiverOperatingCharacteristic roc )
        {
            return computeOptimalThreshold( roc, 1.0, 1.0 );
        }

        
        /**
         * Determines the DataPoint, and associated threshold, that
         * simultaneously maximizes the value of
         * Area=TruePositiveRate+TrueNegativeRate, usually the 
         * upper-left "knee" on the ROC curve.
         * 
         * 
         * @return DataPoint, with corresponding threshold, that maximizes the value
         * of Area=TruePositiveRate*(1-FalsePositiveRate), usually the 
         * upper-left "knee" on the ROC curve.
         * @param truePositiveWeight 
         * Amount to weight the TruePositiveRate
         * @param trueNegativeWeight 
         * Amount to weight the TrueNegativeRate
         * @param roc ROC Curve to consider
         */
        public static DataPoint computeOptimalThreshold(
            ReceiverOperatingCharacteristic roc,
            double truePositiveWeight,
            double trueNegativeWeight )
        {
            
            DataPoint bestData = null;
            double bestValue = Double.NEGATIVE_INFINITY;
            
            for( ReceiverOperatingCharacteristic.DataPoint data : roc.getSortedROCData() )
            {
                // Find the point that maximizes the perimeter "below and to the 
                // right" of the point
                DefaultBinaryConfusionMatrix cm = data.getConfusionMatrix();
                double y = truePositiveWeight * cm.getTruePositivesRate();
                double x = trueNegativeWeight * cm.getTrueNegativesRate();
                
                double value = x + y;
                if( bestValue < value )
                {
                    bestValue = value;
                    bestData = data;
                }
            }
            
            return bestData;
            
        }
        
        
        /**
         * Computes the value of d-prime given a datapoint
         * @param data 
         * Datapoint from which to estimate d'
         * @return 
         * Estimated distance between the two classes to be split.  Larger 
         * values of d' indicate that the classes are easier to split,
         * d'=0 means that the classes overlap, and negative values mean
         * that your classifier is doing worse than chance, chump.  This
         * appears to only be used by psychologists.
         */
        public static double computeDPrime(
            DataPoint data )
        {
            
            double hitRate = data.getConfusionMatrix().getTruePositivesRate();
            double faRate = data.getFalsePositiveRate();
            
            double zhr = UnivariateGaussian.CDF.Inverse.evaluate( hitRate, 0.0, 1.0 );
            double zfa = UnivariateGaussian.CDF.Inverse.evaluate( faRate, 0.0, 1.0 );
            
            return zhr - zfa;
            
        }
        

        /**
         * Getter for dPrime
         * @return 
         * Estimated distance between the two classes to be split.  Larger 
         * values of d' indicate that the classes are easier to split,
         * d'=0 means that the classes overlap, and negative values mean
         * that your classifier is doing worse than chance, chump.  This
         * appears to only be used by psychologists.
         */
        public double getDPrime()
        {
            return this.dPrime;
        }

        /**
         * Setter for dPrime
         * @param dPrime 
         * Estimated distance between the two classes to be split.  Larger 
         * values of d' indicate that the classes are easier to split,
         * d'=0 means that the classes overlap, and negative values mean
         * that your classifier is doing worse than chance, chump.  This
         * appears to only be used by psychologists.
         */
        protected void setDPrime(
            double dPrime)
        {
            this.dPrime = dPrime;
        }

        /**
         * Getter for areaUnderCurve
         * @return 
         * Area underneath the ROC curve, on the interval [0,1].  A value of
         * 0.5 means that the classifier is doing no better than chance and
         * bigger is better
         */
        public double getAreaUnderCurve()
        {
            return this.areaUnderCurve;
        }

        /**
         * Setter for areaUnderCurve
         * @param areaUnderCurve 
         * Area underneath the ROC curve, on the interval [0,1].  A value of
         * 0.5 means that the classifier is doing no better than chance and
         * bigger is better
         */
        protected void setAreaUnderCurve(
            double areaUnderCurve)
        {
            this.areaUnderCurve = areaUnderCurve;
        }

        /**
         * Getter for optimalThreshold
         * 
         * @return DataPoint, with corresponding threshold, that maximizes the value
         * of Area=TruePositiveRate*(1-FalsePositiveRate), usually the 
         * upper-left "knee" on the ROC curve.
         */
        public DataPoint getOptimalThreshold()
        {
            return this.optimalThreshold;
        }

        /**
         * Setter for optimalThreshold
         * 
         * @param optimalThreshold 
         * DataPoint, with corresponding threshold, that maximizes the value
         * of Area=TruePositiveRate*(1-FalsePositiveRate), usually the 
         * upper-left "knee" on the ROC curve.
         */
        protected void setOptimalThreshold(
            DataPoint optimalThreshold)
        {
            this.optimalThreshold = optimalThreshold;
        }
        
    }
    
    
    /**
     * Contains information about a datapoint on an ROC curve
     */
    public static class DataPoint
        extends AbstractCloneableSerializable
    {
        
        /**
         * Binary classifier used to create the corresponding ConfusionMatrix,
         * which is really a wrapper for the threshold
         */
        private ScalarThresholdBinaryCategorizer classifier;
        
        
        /**
         * Corresponding ConfusionMatrix with this datapoint
         */
        private DefaultBinaryConfusionMatrix confusionMatrix;
        
        
        /**
         * Creates a new instance of DataPoint
         * 
         * @param classifier
         * Binary classifier used to create the corresponding ConfusionMatrix,
         * which is really a wrapper for the threshold
         * @param confusionMatrix 
         * Corresponding ConfusionMatrix with this datapoint
         */
        public DataPoint(
            ScalarThresholdBinaryCategorizer classifier,
            DefaultBinaryConfusionMatrix confusionMatrix )
        {
            this.setClassifier( classifier );
            this.setConfusionMatrix( confusionMatrix );
        }
        

        /**
         * Getter for classifier
         * @return 
         * Binary classifier used to create the corresponding ConfusionMatrix,
         * which is really a wrapper for the threshold
         */
        public ScalarThresholdBinaryCategorizer getClassifier()
        {
            return this.classifier;
        }

        /**
         * Setter for classifier
         * @param classifier 
         * Binary classifier used to create the corresponding ConfusionMatrix,
         * which is really a wrapper for the threshold
         */
        public void setClassifier(
            ScalarThresholdBinaryCategorizer classifier)
        {
            this.classifier = classifier;
        }        
        
        
        /**
         * Getter for confusionMatrix
         * @return 
         * Corresponding ConfusionMatrix with this datapoint
         */
        public DefaultBinaryConfusionMatrix getConfusionMatrix()
        {
            return this.confusionMatrix;
        }
        
        /**
         * Setter for confusionMatrix
         * @param confusionMatrix 
         * Corresponding ConfusionMatrix with this datapoint
         */
        protected void setConfusionMatrix(
            DefaultBinaryConfusionMatrix confusionMatrix)
        {
            this.confusionMatrix = confusionMatrix;
        }
        
        /**
         * Gets the falsePositiveRate associated with this datapoint
         * @return 
         * falsePositiveRate associated with this datapoint
         */
        public double getFalsePositiveRate()
        {
            return this.getConfusionMatrix().getFalsePositivesRate();
        }
        
        /**
         * Gets the truePositiveRate associated with this datapoint
         * @return 
         * truePositiveRate associated with this datapoint
         */
        public double getTruePositiveRate()
        {
            return this.getConfusionMatrix().getTruePositivesRate();
        }
        
        
        /**
         * Sorts DataPoints in ascending order according to their
         * falsePositiveRate (x-axis)
         */
        public static class Sorter
            extends AbstractCloneableSerializable
            implements Comparator<DataPoint>
        {
            /**
             * Sorts ROCDataPoints in ascending order according to their
             * falsePositiveRate (x-axis), used in Arrays.sort() method
             * 
             * @param o1 First datapoint
             * @param o2 Second datapoint
             * @return
             * -1 if o1<o2, +1 if o1>o2, 0 if o1=o2
             */
            @Override
            public int compare(
                ReceiverOperatingCharacteristic.DataPoint o1,
                ReceiverOperatingCharacteristic.DataPoint o2)
            {
                double x1 = o1.getFalsePositiveRate();
                double x2 = o2.getFalsePositiveRate();
                if( x1 < x2 )
                {
                    return -1;
                }
                else if( x1 > x2 )
                {
                    return +1;
                }
                else
                {
                    double y1 = o1.getTruePositiveRate();
                    double y2 = o2.getTruePositiveRate();
                    if( y1 < y2 )
                    {
                        return -1;
                    }
                    else if( y1 > y2 )
                    {
                        return +1;
                    }
                    else
                    {
                        return 0;
                    }
                }
                
            }
        }
        
    }
    
    
    /**
     * Sorts score estimates for the ROC create() method
     */
    private static class ROCScoreSorter
        extends AbstractCloneableSerializable
        implements Comparator<InputOutputPair<Double,? extends Object>>
    {
        
        /**
         * Sorts score estimates for the ROC create() method
         * 
         * @param o1 First score
         * @param o2 Second score
         * @return -1 if o1<o2, +1 if o1>o2, 0 if o1=02
         */
        @Override
        public int compare(
            InputOutputPair<Double,? extends Object> o1,
            InputOutputPair<Double,? extends Object> o2)
        {
            if( o1.getInput() < o2.getInput() )
            {
                return -1;
            }
            else if( o1.getInput() > o2.getInput() )
            {
                return +1;
            }
            else
            {
                return 0;
            }
            
        }
    }
    
    
}
