/*
 * File:                FieldConfidenceInterval.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright October 30, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.statistics.method;

import gov.sandia.cognition.util.ObjectUtil;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;

/**
 * This class has methods that automatically compute confidence intervals for
 * Double/double Fields in dataclasses.
 * For example, if you're in a hurry and want to compute the confidence
 * intervals for all given class (e.g., LinearRegression.Statistic), then
 * you can simply class the computeConfidenceInterval() with a Collection
 * of your desired class and it will return the confidence interval associated
 * with the internal Fields of the data.  That is, it will return a confidence
 * interval associated with each public/protected/private field within the class
 * automatically, with you having to specify them individually
 * (unless you want to).
 *
 * @author Kevin R. Dixon
 * @since  2.0
 *
 */
public class FieldConfidenceInterval
    extends ConfidenceInterval
{

    /**
     * Field associated with the confidence interval
     */
    private Field field;

    /**
     * Creates a new instance of FieldConfidenceInterval
     * @param field Field associated with the confidence interval
     * @param confidenceInterval ConfidenceInterval associated with the Field
     */
    public FieldConfidenceInterval(
        Field field,
        ConfidenceInterval confidenceInterval )
    {
        super( confidenceInterval );
        this.setField( field );
    }

    /**
     * Determines if the given field is a match to the internal Field
     * @param field
     * Field to compare against the internal Field
     * @return
     * True if a match, false otherwise
     */
    public boolean isMatch(
        Field field )
    {
        return this.isMatch( field.getName() );
    }

    /**
     * Determines if the given field is a match to the internal Field
     * @param fieldName
     * Field name to compare against the internal Field
     * @return
     * True if a match, false otherwise
     */
    public boolean isMatch(
        String fieldName )
    {
        return this.getField().getName().equals( fieldName );
    }

    /**
     * Determines whether the Field within the given Object is within the
     * specified interval
     * @param o
     * Object from which to pull the Field's value to determine if it's
     * within the specified interval
     * @return
     * True if o's Field is within the interval, false otherwise
     */
    public boolean withinInterval(
        Object o )
    {

        boolean retval;
        try
        {
            this.getField().setAccessible( true );
            double value = this.getField().getDouble( o );
            retval = this.withinInterval( value );
        }
        catch (Exception e)
        {
            retval = false;
        }

        return retval;
    }

    /**
     * Getter for field
     * @return
     * Field associated with the confidence interval
     */
    public Field getField()
    {
        return this.field;
    }

    /**
     * Setter for field
     * @param field
     * Field associated with the confidence interval
     */
    public void setField(
        Field field )
    {
        this.field = field;
    }

    @Override
    public String toString()
    {
        return "Field: " + this.getField().getName() +
            ", Interval: " + super.toString();
    }

    /**
     * Computes a FieldConfidenceInterval for each Double/double Field
     * in the given data.
     * 
     * @param   <DataType> The type of data to compute the confidence interval 
     *      over.
     * @param data
     * Collection of data from which to pull the values.  Must all be same type!
     * @param confidenceIntervalEvaluator
     * Statistical test that transforms a Collection of Doubles into a
     * ConfidenceInterval
     * @param confidence
     * Confidence (power) of the resulting ConfidenceInterval
     * @return
     * Collection of FieldConfidenceInterval, one for each Double/double Field
     * found in "DataType"
     */
    public static <DataType> ArrayList<FieldConfidenceInterval> computeConfidenceInterval(
        Collection<DataType> data,
        ConfidenceIntervalEvaluator<Collection<? extends Double>> confidenceIntervalEvaluator,
        double confidence )
    {

        // Get the fields from the first object in the dataset
        Class dataClass = data.iterator().next().getClass();

        // Now, get the list of fields
        LinkedList<Field> allFields = ObjectUtil.getAllFields( dataClass );

        // Copy over any "double" or "Double" Fields
        ArrayList<Field> interestedFields =
            new ArrayList<Field>( allFields.size() );
        for (Field f : allFields)
        {
            Class c = f.getType();

            if (c.isPrimitive() || Number.class.isAssignableFrom( c ))
            {
                interestedFields.add( f );
            }
        }

        return FieldConfidenceInterval.computeConfidenceInterval(
            data, interestedFields, confidenceIntervalEvaluator, confidence );

    }

    /**
     * Computes a FieldConfidenceInterval for the given Fields in the given data
     *
     * @param   <DataType> The type of data to compute the confidence interval 
     *      over.
     * @param interestedFields
     * Fields from which to pull data
     * @param data
     * Collection of data from which to pull the values.  Must all be same type!
     * @param confidenceIntervalEvaluator
     * Statistical test that transforms a Collection of Doubles into a
     * ConfidenceInterval
     * @param confidence
     * Confidence (power) of the resulting ConfidenceInterval
     * @return
     * Collection of FieldConfidenceInterval, one for each Double/double Field
     * found in "DataType"
     */
    public static <DataType> ArrayList<FieldConfidenceInterval> computeConfidenceInterval(
        Collection<DataType> data,
        ArrayList<Field> interestedFields,
        ConfidenceIntervalEvaluator<Collection<? extends Double>> confidenceIntervalEvaluator,
        double confidence )
    {

        // First of all, all data objects must be the EXACT same class
        Class dataClass = null;
        for (Object o : data)
        {
            if (dataClass == null)
            {
                dataClass = o.getClass();
            }

            if (!dataClass.isInstance( o ))
            {
                throw new IllegalArgumentException(
                    "All classes in data must be EXACT same type" );
            }

        }

        // Create a new ArrayList of data,
        // one for each field we're interested in
        int numFields = interestedFields.size();
        ArrayList<ArrayList<Double>> fieldValues =
            new ArrayList<ArrayList<Double>>( numFields );
        for (int i = 0; i < numFields; i++)
        {
            fieldValues.add( new ArrayList<Double>( data.size() ) );
        }

        // Add the field values to the list of data
        for (Object o : data)
        {
            ArrayList<Object> values =
                ObjectUtil.getFieldValues( o, interestedFields );
            for (int i = 0; i < numFields; i++)
            {
                fieldValues.get( i ).add( ((Number) values.get( i )).doubleValue() );
            }
        }

        // Compute confidence intervalues over the data
        ArrayList<FieldConfidenceInterval> confidenceIntervals =
            new ArrayList<FieldConfidenceInterval>( numFields );
        for (int i = 0; i < numFields; i++)
        {
            FieldConfidenceInterval fci = new FieldConfidenceInterval(
                interestedFields.get( i ),
                confidenceIntervalEvaluator.computeConfidenceInterval(
                fieldValues.get( i ), confidence ) );
            confidenceIntervals.add( fci );
        }

        return confidenceIntervals;

    }

}
