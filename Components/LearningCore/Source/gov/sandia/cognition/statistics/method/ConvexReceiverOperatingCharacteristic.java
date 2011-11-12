/*
 * File:                ConvexReceiverOperatingCharacteristic.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Sep 2, 2011, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.statistics.method;

import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationReferences;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.evaluator.Evaluator;
import gov.sandia.cognition.statistics.method.ReceiverOperatingCharacteristic.DataPoint;
import gov.sandia.cognition.util.AbstractCloneableSerializable;
import gov.sandia.cognition.util.ObjectUtil;
import java.util.ArrayList;


/**
 * Computes the convex hull of the Receiver Operating Characteristic (ROC),
 * which a mathematician might call a "concave down" function.
 * curve.
 * @author Kevin R. Dixon
 * @since 3.2.1
 */
@PublicationReferences(
    references={
        @PublicationReference(
            author="Wikipedia",
            title="Concave function",
            type=PublicationType.WebPage,
            year=2011,
            url="http://en.wikipedia.org/wiki/Concave_function"
        )
        ,
        @PublicationReference(
            author="Wikipedia",
            title="Convex hull",
            type=PublicationType.WebPage,
            year=2011,
            url="http://en.wikipedia.org/wiki/Convex_hull"
        )
    }
)
public class ConvexReceiverOperatingCharacteristic 
    extends AbstractCloneableSerializable
    implements Evaluator<Double,Double>
{

    /**
     * Convex hull of the ROC curve
     */
    private ArrayList<ReceiverOperatingCharacteristic.DataPoint> convexHull;

    /** 
     * Creates a new instance of HandHMeasure 
     * @param convexHull
     * Convex hull of the ROC curve
     */
    private ConvexReceiverOperatingCharacteristic(
        ArrayList<ReceiverOperatingCharacteristic.DataPoint> convexHull )
    {
        this.setConvexHull(convexHull);
    }

    @Override
    public ConvexReceiverOperatingCharacteristic clone()
    {
        ConvexReceiverOperatingCharacteristic clone =
            (ConvexReceiverOperatingCharacteristic) super.clone();
        clone.setConvexHull(
            ObjectUtil.cloneSmartElementsAsArrayList( this.getConvexHull() ) );
        return clone;
    }

    /**
     * Computes the convex hull values using a trapezoid interpolation.
     * @param input
     * falsePositiveRate from which to estimate the truePositiveRate
     * @return
     * TruePositiveRate for the given FalsePositiveRate
     */
    @Override
    public Double evaluate(
        Double input )
    {
        final double falsePositiveRate = input;

        double truePositiveRate = 0.0;
        ArrayList<ReceiverOperatingCharacteristic.DataPoint> data = this.getConvexHull();
        final int N = data.size();
        for( int i = N-1; i >= 0; i-- )
        {
            ReceiverOperatingCharacteristic.DataPoint value = data.get(i);
            final double x = value.getFalsePositiveRate();

            // No need to interpolate if we're exactly on a point
            if( x == falsePositiveRate )
            {
                truePositiveRate = value.getTruePositiveRate();
                break;
            }
            else if( x < falsePositiveRate )
            {
                if( i < N-1 )
                {
                    ReceiverOperatingCharacteristic.DataPoint right = data.get(i+1);
                    final double run = right.getFalsePositiveRate() - x;
                    final double y = value.getTruePositiveRate();
                    final double rise = right.getTruePositiveRate() - y;
                    final double slope = rise/run;
                    final double delta = falsePositiveRate - x;
                    truePositiveRate = y + delta*slope;
                }
                else
                {
                    truePositiveRate = 1.0;
                }
                break;
            }

        }

        return truePositiveRate;

    }

    /**
     * Computes the convex hull of a ROC curve
     * @param roc
     * ROC curve from which to extract the convex hull
     * @return
     * Convex hull over the ROC curve
     */
    public static ConvexReceiverOperatingCharacteristic computeConvexNull(
        ReceiverOperatingCharacteristic roc )
    {
        ArrayList<ReceiverOperatingCharacteristic.DataPoint> origRocData =
            roc.getSortedROCData();

        ArrayList<DataPoint> convexRoc =
            new ArrayList<DataPoint>( origRocData.size() );
        int leftIndex = 0;
        ReceiverOperatingCharacteristic.DataPoint left =
            origRocData.get(leftIndex);
        convexRoc.add( left );

        boolean done = false;
        while( !done )
        {
            // Walk the "right" point toward the "left" point until no points
            // between them are in between on the y-axis
            left = origRocData.get(leftIndex);
            int rightIndex = origRocData.size()-1;

            if( leftIndex >= rightIndex )
            {
                done = true;
                break;
            }

            boolean foundAbove = false;
            while( rightIndex > leftIndex )
            {
                foundAbove = false;
                ReceiverOperatingCharacteristic.DataPoint right =
                    origRocData.get(rightIndex);

                double rise = right.getTruePositiveRate() - left.getTruePositiveRate();
                double run = right.getFalsePositiveRate() - left.getFalsePositiveRate();
                if( run == 0.0 )
                {
                    // If we get here then we've seen nothing but points above
                    // us... that means we're done.
                    foundAbove = false;
                }
                else
                {
                    double slope = rise / run;

                    for( int i = rightIndex-1; i > leftIndex; i-- )
                    {
                        ReceiverOperatingCharacteristic.DataPoint point =
                            origRocData.get(i);

                        double xdiff = point.getFalsePositiveRate() - right.getFalsePositiveRate();

                        // This is y-value the estimated convex hull without this point
                        double yhat = right.getTruePositiveRate() + xdiff * slope;

                        // If "point" is above estimated convex hull, then keep
                        // walking the right point toward the left point
                        if( yhat < point.getTruePositiveRate() )
                        {
                            rightIndex = i;
                            foundAbove = true;
                            break;
                        }

                    }
                }

                // No points were above the convex hull with the (left,right)
                // combo... so we've found the next pair!!
                // The "right" point becomes the next "left" point
                if( !foundAbove )
                {
                    convexRoc.add( right );
                    leftIndex = rightIndex;
                    break;
                }
            }

            leftIndex = rightIndex;

        }

        ConvexReceiverOperatingCharacteristic roch =
            new ConvexReceiverOperatingCharacteristic(convexRoc);
        return roch;

    }

    /**
     * Computes the area under the convex hull
     * @return
     * Area under the convex hull
     */
    public double computeAreaUnderConvexHull()
    {
        return ReceiverOperatingCharacteristic.Statistic.computeAreaUnderCurveTrapezoid(
            this.getConvexHull() );
    }

    /**
     * Getter for convexHull
     * @return
     * Convex hull of the ROC curve
     */
    public ArrayList<ReceiverOperatingCharacteristic.DataPoint> getConvexHull()
    {
        return this.convexHull;
    }

    /**
     * Setter for convexHull
     * @param convexHull
     * Convex hull of the ROC curve
     */
    protected void setConvexHull(
        ArrayList<ReceiverOperatingCharacteristic.DataPoint> convexHull)
    {
        this.convexHull = convexHull;
    }
    
}
