/*
 * File:                DatasetUtil.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright September 11, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.learning.data;

import gov.sandia.cognition.collection.DefaultMultiCollection;
import gov.sandia.cognition.collection.MultiCollection;
import gov.sandia.cognition.math.matrix.DimensionalityMismatchException;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.math.matrix.Matrix;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorUtil;
import gov.sandia.cognition.math.matrix.Vectorizable;
import gov.sandia.cognition.math.matrix.mtj.SparseMatrixFactoryMTJ;
import gov.sandia.cognition.statistics.DataHistogram;
import gov.sandia.cognition.statistics.distribution.MapBasedDataHistogram;
import gov.sandia.cognition.util.DefaultPair;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Static class containing utility methods for handling Collections of data
 * in the learning package.
 *
 * @author Kevin R. Dixon
 * @since  2.0
 *
 */
public class DatasetUtil
{

    /**
     * Appends a bias (constant 1.0) to the end of each Vector in the dataset,
     * the original dataset is unmodified.  The resulting Vectors will have one
     * greater dimension and look like: [ x1 x2 ] -> [ x1 x2 1.0 ]
     * @param dataset
     * Dataset to append a bias term to, Vectors can be of different
     * dimensionality
     * @return
     * Dataset with 1.0 appended to each Vector in the dataset
     */
    public static ArrayList<Vector> appendBias(
        Collection<? extends Vector> dataset)
    {
        return DatasetUtil.appendBias(dataset, 1.0);
    }

    /**
     * Appends "biasValue" to the end of each Vector in the dataset,
     * the original dataset is unmodified.  The resulting Vectors will have one
     * greater dimension and look like: [ x1 x2 ] -> [ x1 x2 1.0 ]
     * @param dataset
     * Dataset to append a bias term to, Vectors can be of different
     * dimensionality
     * @param biasValue
     * Bias value to append to the samples
     * @return
     * Dataset with "biasValue" appended to each Vector in the dataset
     */
    public static ArrayList<Vector> appendBias(
        Collection<? extends Vector> dataset,
        double biasValue)
    {
        ArrayList<Vector> biasDataset = new ArrayList<Vector>(dataset.size());
        Vector bias = VectorFactory.getDefault().copyValues(biasValue);
        for (Vector data : dataset)
        {
            biasDataset.add(data.stack(bias));
        }
        return biasDataset;
    }

    /**
     * Takes a set of equal-dimension Vector-Vector InputOutputPairs and
     * turns them into a collection of Double-Double InputOutputPairs.  This
     * is useful when one can treat each element of the Vector-Vector pairs
     * as independent of the other elements
     * @param dataset
     * Collection of Vector-Vector InputOutputPairs.  All Vectors (both inputs
     * and outputs) must have equal dimension!!
     * @return
     * ArrayList of ArrayList of Double-Double InputOutputPairs.  The outer
     * ArrayList contains a dataset for each element in the vector (and thus
     * there are as many ArrayList<InputOutputPair<Double,Double>> as there
     * are elements in the Vectors).  Each ArrayList<InputOutputPair<Double,Double>>
     * has as many elements as the original dataset
     */
    public static ArrayList<ArrayList<InputOutputPair<Double, Double>>> decoupleVectorPairDataset(
        Collection<? extends InputOutputPair<? extends Vector, ? extends Vector>> dataset)
    {
        int numSamples = dataset.size();
        int M = dataset.iterator().next().getInput().getDimensionality();
        ArrayList<ArrayList<InputOutputPair<Double, Double>>> retval =
            new ArrayList<ArrayList<InputOutputPair<Double, Double>>>(M);
        for (int i = 0; i < M; i++)
        {
            retval.add(new ArrayList<InputOutputPair<Double, Double>>(numSamples));
        }

        for (InputOutputPair<? extends Vector, ? extends Vector> pair : dataset)
        {
            if ((pair.getInput().getDimensionality() != M) ||
                (pair.getOutput().getDimensionality() != M))
            {
                throw new IllegalArgumentException(
                    "All input-output Vectors must have same dimension!");
            }

            for (int i = 0; i < M; i++)
            {
                double x = pair.getInput().getElement(i);
                double y = pair.getOutput().getElement(i);
                InputOutputPair<Double, Double> rowPair;
                if (pair instanceof WeightedInputOutputPair<?, ?>)
                {
                    double weight = ((WeightedInputOutputPair<?, ?>) pair).getWeight();
                    rowPair = new DefaultWeightedInputOutputPair<Double, Double>(
                        x, y, weight);
                }
                else
                {
                    rowPair = new DefaultInputOutputPair<Double, Double>(x, y);
                }
                retval.get(i).add(rowPair);
            }
        }

        return retval;

    }

    /**
     * Takes a dataset of M-dimensional Vectors and turns it into M
     * datasets of Doubles
     * @param dataset
     * M-dimensional Vectors, throws IllegalArgumentException if all Vectors
     * aren't the same dimensionality
     * @return
     * M datasets of dataset.size() Doubles
     */
    static public ArrayList<ArrayList<Double>> decoupleVectorDataset(
        Collection<? extends Vector> dataset)
    {

        int M = dataset.iterator().next().getDimensionality();
        int num = dataset.size();
        ArrayList<ArrayList<Double>> decoupledDatasets =
            new ArrayList<ArrayList<Double>>(M);
        for (int i = 0; i < M; i++)
        {
            decoupledDatasets.add(new ArrayList<Double>(num));
        }

        for (Vector data : dataset)
        {

            if (M != data.getDimensionality())
            {
                throw new IllegalArgumentException(
                    "All vectors in the dataset must be the same size");
            }

            for (int i = 0; i < M; i++)
            {
                decoupledDatasets.get(i).add(data.getElement(i));
            }

        }

        return decoupledDatasets;
    }

    /**
     * Splits a dataset of input-output pair into two datasets, one for the
     * inputs that have a "true" output and another for the inputs that have
     * a "false" output
     * 
     * @param   <DataType> The type of the data.
     * @param data
     * Collection of InputOutputPairs to split according to the output flag
     * @return
     * DefaultPair of LinkedLists where the first dataset corresponds to the inputs
     * where the output was "true" and the second dataset corresponds to the
     * inputs where the output was "false"
     */
    public static <DataType> DefaultPair<LinkedList<DataType>, LinkedList<DataType>> splitDatasets(
        Collection<? extends InputOutputPair<? extends DataType, Boolean>> data)
    {
        LinkedList<DataType> dtrue = new LinkedList<DataType>();
        LinkedList<DataType> dfalse = new LinkedList<DataType>();

        for (InputOutputPair<? extends DataType, Boolean> pair : data)
        {
            if (pair.getOutput())
            {
                dtrue.add(pair.getInput());
            }
            else
            {
                dfalse.add(pair.getInput());
            }
        }
        
        return DefaultPair.create( dtrue, dfalse );
    }


    /**
     * Splits a dataset according to its output value (usually a category) so
     * that all the inputs for that category are given in a list. It maps the
     * category value to its list.
     *
     * @param   <InputType>
     *      The the of the input values.
     * @param   <CategoryType>
     *      The type of the output values.
     * @param   data
     *      The input-output pairs to split.
     * @return
     *      A mapping of category to a list of all of the inputs for that
     *      category.
     */
    public static <InputType, CategoryType> Map<CategoryType, List<InputType>> splitOnOutput(
        final Iterable<? extends InputOutputPair<? extends InputType, ? extends CategoryType>> data)
    {
        final LinkedHashMap<CategoryType, List<InputType>> result =
            new LinkedHashMap<CategoryType, List<InputType>>();

        for (InputOutputPair<? extends InputType, ? extends CategoryType> example
            : data)
        {
            final CategoryType category = example.getOutput();
            List<InputType> examplesForCategory = result.get(category);
            if (examplesForCategory == null)
            {
                examplesForCategory = new ArrayList<InputType>();
                result.put(category, examplesForCategory);
            }
            examplesForCategory.add(example.getInput());
        }

        return result;
    }

    /**
     * Computes the outer-product Matrix of the given set of data:
     * XXt = [ x1 x2 ... xn ] * [ x1 x2 ... xn ]^T.
     * The outer-product data Matrix is useful in things like computing the
     * Principal Components Analysis of the dataset.  For exapmle, finding the 
     * eigenvectors of the outer-product data Matrix is equivalent to finding 
     * the left singular Vectors ("U" from the SingularValueDecomposition) of
     * the dataset.
     * Note that if the input dataset has a size of "N" and each Vector in the
     * dataset has "M" dimensions, then the return Matrix (XXt) is an (MxM)
     * Matrix.  This method computes the return Matrix without explicitly
     * forming the data matrix, potentially saving quite a lot of memory.
     * @param data 
     * Input dataset where each of "N" Vectors has dimension of "M"
     * @return 
     * Outer product Matrix of the input dataset, having dimensions
     * (MxM).  
     */
    public static Matrix computeOuterProductDataMatrix(
        ArrayList<? extends Vector> data)
    {

        // This is X*transpose(X), if X is a (MxN) resulting in an (MxM) matrix
        // [ x1 x2 ... xn ] * [ x1 x2 ... xn ]^T
        // We're avoiding storing the entire matrices
        int M = data.iterator().next().getDimensionality();
        Matrix XXt = SparseMatrixFactoryMTJ.INSTANCE.createMatrix(M, M);
        for (int j = 0; j < M; j++)
        {
            for (int i = 0; i < M; i++)
            {
                double sum = 0.0;
                for (int k = 0; k < data.size(); k++)
                {
                    Vector dk = data.get(k);
                    sum += dk.getElement(i) * dk.getElement(j);
                }

                if( sum != 0.0 )
                {
                    XXt.setElement(i, j, sum);
                }
            }
        }

        return XXt;
    }

    /**
     * Computes the mean of the output data.
     * 
     * @param  data The data to compute the mean of the output.
     * @return The mean of the output values of the given data.
     */
    public static double computeOutputMean(
        final Collection<? extends InputOutputPair<?, ? extends Number>> data)
    {
        if (data == null)
        {
            // No data, so zero mean.
            return 0.0;
        }

        // Compute the sum in order to find the mean.
        double sum = 0.0;
        int count = 0;
        for (InputOutputPair<?, ? extends Number> example : data)
        {
            sum += example.getOutput().doubleValue();
            count++;
        }

        if (count <= 0)
        {
            // No elements, so zero mean.
            return 0.0;
        }
        else
        {
            return sum / count;
        }
    }
    
    
    /**
     * Computes the mean of the output data.
     * 
     * @param  data The data to compute the mean of the output.
     * @return The mean of the output values of the given data.
     */
    public static double computeWeightedOutputMean(
        final Collection<? extends InputOutputPair<?, ? extends Number>> data)
    {
        if (data == null || data.size() <= 0.0)
        {
            // No data, so zero mean.
            return 0.0;
        }

        // Compute the sum in order to find the mean.
        double sum = 0.0;
        double weightSum = 0.0;
        for (InputOutputPair<?, ? extends Number> example : data)
        {
            final double weight = DatasetUtil.getWeight(example);
            sum += weight * example.getOutput().doubleValue();
            weightSum += weight;
        }

        if (weightSum == 0.0)
        {
            // No elements, so zero mean.
            return 0.0;
        }
        else
        {
            return sum / weightSum;
        }
    }

    /**
     * Computes the variance of the output of a given set of input-output pairs.
     *
     * @param  data The data.
     * @return The variance of the output of the data.
     */
    public static double computeOutputVariance(
        final Collection<? extends InputOutputPair<?, ? extends Number>> data)
    {
        if (data == null)
        {
            // No data, so zero variance.
            return 0.0;
        }

        final int count = data.size();
        if (count <= 0)
        {
            // No elements, so zero variance.
            return 0.0;
        }


        // Compute the mean.
        final double mean = computeOutputMean(data);

        // Now compute the variance.
        double sum = 0.0;
        for (InputOutputPair<?, ? extends Number> example : data)
        {
            final double difference = example.getOutput().doubleValue() - mean;
            sum += difference * difference;
        }

        // Compute the variance and return it.
        final double variance = sum / count;
        return variance;
    }

    /**
     * Creates a set containing the unique output values from the given data.
     *
     * @param   <OutputType>
     *      The type of the output values.
     * @param   data
     *      The data to collect the unique output values from.
     * @return
     *      The set of unique output values. Implemented as a linked hash set.
     */
    public static <OutputType> Set<OutputType> findUniqueOutputs(
        final Iterable<? extends InputOutputPair<?, ? extends OutputType>> data)
    {
        // Create the result set.
        final Set<OutputType> outputs = new LinkedHashSet<OutputType>();

        if (data != null)
        {
            // Go through and add each output.
            for (InputOutputPair<?, ? extends OutputType> example : data)
            {
                outputs.add(example.getOutput());
            }
        }
        
        return outputs;
    }

    /**
     * Creates a data histogram over the output values from the given data.
     *
     * @param   <OutputType>
     *      The type of the output values.
     * @param   data
     *      The data to collect the output values from.
     * @return
     *      The histogram of output values.
     */
    public static <OutputType> DataHistogram<OutputType> countOutputValues(
        final Iterable<? extends InputOutputPair<?, ? extends OutputType>> data)
    {
        // Create the result set.
        final DataHistogram<OutputType> outputs =
            new MapBasedDataHistogram<OutputType>();

        if (data != null)
        {
            // Go through and add each output.
            for (InputOutputPair<?, ? extends OutputType> example : data)
            {
                outputs.add(example.getOutput());
            }
        }

        return outputs;
    }

    /**
     * Creates a list containing all of the input values from the given data.
     *
     * @param   <InputType>
     *      The type of the input values.
     * @param   data
     *      The data to collect the input values from.
     * @return
     *      The set of unique output values. Implemented as a linked hash set.
     */
    public static <InputType> List<InputType> inputsList(
        final Iterable<? extends InputOutputPair<? extends InputType, ?>> data)
    {
// TODO: Make this a proxy object rather than copying the list.
        // Create the result set.
        final ArrayList<InputType> inputs = new ArrayList<InputType>();

        if (data != null)
        {
            // Go through and add each input.
            for (InputOutputPair<? extends InputType, ?> example : data)
            {
                inputs.add(example.getInput());
            }
        }

        return inputs;
    }

    /**
     * Takes a collection and returns a multi-collection version of that
     * collection. If the given collection is a multi-collection, it casts it
     * to that value and returns it. If it is not a multi-collection, it creates
     * a new, singleton multi-collection with the given collection and returns
     * it.
     *
     * @param   <EntryType>
     *      The entry type of the collection.
     * @param   collection
     *      A collection.
     * @return
     *      A multi-collection version of the given collection.
     */
    public static <EntryType> MultiCollection<EntryType> asMultiCollection(
        final Collection<EntryType> collection)
    {
        if (collection instanceof MultiCollection)
        {
            return (MultiCollection<EntryType>) collection;
        }
        else
        {
            return new DefaultMultiCollection<EntryType>(
                Collections.singletonList(collection));
        }
    }

    /**
     * Takes a collection of {@code Vectorizable} objects and returns a
     * collection of {@code Vector} objects of the same size.
     *
     * @param   collection
     *      The collection of {@code Vectorizable} objects to convert.
     * @return
     *      The corresponding collection of {@code Vector} objects.
     */
    public static Collection<Vector> asVectorCollection(
        final Collection<? extends Vectorizable> collection)
    {
// TODO: Have this class make a wrapper collection that doesn't actually
// recreate the whole collection of vectors, but instead calls convertToVector
// when needed.

        // Convert the values to vector form.
        final Collection<Vector> result = new ArrayList<Vector>(
            collection.size());
        for (Vectorizable value : collection)
        {
            result.add(value.convertToVector());
        }
        return result;
    }

    /**
     * Gets the dimensionality of the input vectors in given set of input-output
     * pairs. It finds the first non-null vector and returns its dimensionality.
     * If there are non-null vectors, then -1 is returned.
     *
     * @param   data
     *      The data to find the input dimensionality of.
     * @return
     *      The dimensionality of the first non-null in put in the given data.
     *      -1 if there are no non-null inputs.
     */
    public static int getInputDimensionality(
        final Iterable<? extends InputOutputPair<? extends Vectorizable, ?>> data)
    {
        if (data != null)
        {
            for (InputOutputPair<? extends Vectorizable, ?> example : data)
            {
                if (example == null)
                {
                    // Keep looking since this was null.
                    continue;
                }

                final Vectorizable input = example.getInput();
                if (input == null)
                {
                    // Keep looking since this was null.
                    continue;
                }

                final Vector vector = input.convertToVector();
                if (vector != null)
                {
                    // Found a non-null vector. Get its dimensionality.
                    return vector.getDimensionality();
                }
                // else - The vector was null. Keep looking.
            }
        }

        // No valid vectors.
        return -1;
    }

    /**
     * Asserts that all of the dimensionalities of the input vectors in the
     * given set of input-output pairs are the same.
     *
     * @param   data
     *      A collection of input-output pairs.
     * @throws  DimensionalityMismatchException
     *      If the dimensionalities are not all equal.
     */
    public static void assertInputDimensionalitiesAllEqual(
        final Iterable<? extends InputOutputPair<? extends Vectorizable, ?>> data)
    {
        assertInputDimensionalitiesAllEqual(data, getInputDimensionality(data));
    }

    /**
     * Asserts that all of the dimensionalities of the input vectors in the
     * given set of input-output pairs equal the given dimensionality.
     *
     * @param   data
     *      A collection of input-output pairs.
     * @param   dimensionality
     *      The dimensionality that all the inputs must have.
     * @throws  DimensionalityMismatchException
     *      If the dimensionalities are not all equal.
     */
    public static void assertInputDimensionalitiesAllEqual(
        final Iterable<? extends InputOutputPair<? extends Vectorizable, ?>> data,
        final int dimensionality)
    {
        if (data != null)
        {
            for (InputOutputPair<? extends Vectorizable, ?> example : data)
            {
                if (example == null)
                {
                    // Ignore null examples.
                    continue;
                }

                final Vectorizable input = example.getInput();
                if (input == null)
                {
                    // Ignre null inputs.
                    continue;
                }
                
                final Vector vector = input.convertToVector();
                if (vector == null)
                {
                    // Ignore null vectors.
                    continue;
                }

                // Make sure this dimensionality is correct.
                vector.assertDimensionalityEquals(dimensionality);
            }
        }
    }


    /**
     * Gets the dimensionality of the vectors in given set of data. It finds
     * the first non-null vector and returns its dimensionality. If there are
     * non-null vectors, then -1 is returned.
     *
     * @param   data
     *      The data to find the dimensionality of.
     * @return
     *      The dimensionality of the first non-null vector in the given data.
     *      -1 if there are no non-null vector.
     */
    public static int getDimensionality(
        final Iterable<? extends Vectorizable> data)
    {
        if (data != null)
        {
            for (Vectorizable example : data)
            {
                if (example == null)
                {
                    // Keep looking since this was null.
                    continue;
                }

                final Vector vector = example.convertToVector();
                if (vector != null)
                {
                    // Found a non-null vector. Get its dimensionality.
                    return vector.getDimensionality();
                }
                // else - The vector was null. Keep looking.
            }
        }

        // No valid vectors.
        return -1;
    }

    /**
     * Asserts that all of the dimensionalities of the vectors in the
     * given set of data are the same.
     *
     * @param   data
     *      A collection of data.
     * @throws  DimensionalityMismatchException
     *      If the dimensionalities are not all equal.
     */
    public static void assertDimensionalitiesAllEqual(
        final Iterable<? extends Vectorizable> data)
    {
        VectorUtil.assertDimensionalitiesAllEqual(data, getDimensionality(data));
    }

    /**
     * Gets the weight of a given input-output pair. If it is a weighted
     * input-output pair (implements the {@code WeightedInputOutputPair}
     * interface, then it casts it to retrieve its weight. Otherwise, it
     * returns 1.0.
     *
     * @param   pair
     *      The pair to get the weight of.
     * @return
     *      The weight of the given pair, if it exists, otherwise 1.0.
     */
    public static double getWeight(
        final InputOutputPair<?, ?> pair)
    {
        if (pair instanceof WeightedInputOutputPair<?, ?>)
        {
            return ((WeightedInputOutputPair<?, ?>) pair).getWeight();
        }
        else
        {
            return 1.0;
        }
    }

    /**
     * Gets the weight of a given target-estimate pair. If it is a weighted
     * target-estimate pair (implements the {@code WeightedTargetEstimatePair}
     * interface, then it casts it to retrieve its weight. Otherwise, it
     * returns 1.0.
     *
     * @param   pair
     *      The pair to get the weight of.
     * @return
     *      The weight of the given pair, if it exists, otherwise 1.0.
     */
    public static double getWeight(
        final TargetEstimatePair<?, ?> pair)
    {
        if (pair instanceof WeightedTargetEstimatePair<?, ?>)
        {
            return ((WeightedTargetEstimatePair<?, ?>) pair).getWeight();
        }
        else
        {
            return 1.0;
        }
    }

}
