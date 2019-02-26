  /*
   * File:                OptimizedAffinityPropagation.java
   * Authors:             Marco Pezzulla
   * Company:             PTV SISTeMA
   * Project:             Cognitive Foundry
   *
   * Copyright August 7, 2007, Sandia Corporation.  Under the terms of Contract
   * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
   * or on behalf of the U.S. Government. Export of this program may require a
   * license from the United States Government. See CopyrightHistory.txt for
   * complete details.
   *
   */


  package gov.sandia.cognition.learning.algorithm.clustering;

  import gov.sandia.cognition.learning.algorithm.clustering.AffinityPropagation;
  import gov.sandia.cognition.math.DivergenceFunction;
  import java.util.ArrayList;
  import java.util.Arrays;
  import java.util.HashMap;
  import java.util.stream.DoubleStream;

  /**
   * The <code>OptimizedAffinityPropagation</code> extends <code>AffinityPropagation</code>.
   * The <code>OptimizedAffinityPropagation</code> algorithm requires three parameters:
   * a divergence function, a value to use for self-divergence, and a damping
   * factor (called lambda in the paper; 0.5 is the default). It clusters by
   * passing messages between each point to determine the best exemplar for the
   * point.
   * <BR><BR>
   * This implementation takes a divergence function instead of a similarity
   * function and sets the similarity value to the negative of the divergence
   * value, as described in the paper for Euclidean distance.
   * <BR><BR>
   * The self-divergence value is what controls how many clusters are generated.
   * Typically this value is set to the mean or median of all the divergence
   * values or the maximum divergence. In general, a smaller value will mean more
   * clusters and a larger value will mean less clusters. In the paper this is
   * called self-similarity (s(k,k)) but since this implementation uses a
   * divergence metric, we use self-divergence instead.
   * The implementation of <code>OptimizedAffinityPropagation</code> upgrades the implementation
   * of <code>AffinityPropagation</code> in terms of computation efficiency.
   * The two core functions are reimplemented. Computational complexity reduced from n^3 to n^2
   * for both the functions.
   * Moreover it adds the computation of the median of similarities if the "selfDivergence"
   * parameter is not specified in input by the client.
   * @author  Marco Pezzulla
   * @since   2.0
   */
  public class OptimizedAffinityPropagation<T>
      extends AffinityPropagation<T>
  {

    private static final long serialVersionUID = 7170610951827891277L;

    OptimizedAffinityPropagation(){this(null, DEFAULT_SELF_DIVERGENCE);}

    OptimizedAffinityPropagation(
        DivergenceFunction<? super T, ? super T> divergence)
    {
      this(divergence, Double.NaN);
    }

    /**
     * Creates a new instance of OptimizedAffinityPropagation.
     *
     * @param  divergence The divergence function to use to determine the
     *         divergence between two examples.
     * @param  selfDivergence The maxValue for self-divergence to use, which
     *         controls the number of clusters created.
     */
    OptimizedAffinityPropagation(
        DivergenceFunction<? super T, ? super T> divergence,
        double selfDivergence)
    {
      this(divergence, selfDivergence, DEFAULT_DAMPING_FACTOR);
    }

    /**
     * Creates a new instance of OptimizedAffinityPropagation.
     *
     * @param  divergence The divergence function to use to determine the
     *         divergence between two examples.
     * @param  selfDivergence The maxValue for self-divergence to use, which
     *         controls the number of clusters created.
     * @param  dampingFactor The damping factor (lambda). Must be between 0.0
     *         and 1.0.
     */
    OptimizedAffinityPropagation(
        DivergenceFunction<? super T, ? super T> divergence,
        double selfDivergence,
        double dampingFactor)
    {
      this(divergence, selfDivergence, dampingFactor, DEFAULT_MAX_ITERATIONS);
    }

    /**
     * Creates a new instance of OptimizedAffinityPropagation.
     *
     * @param  divergence The divergence function to use to determine the
     *         divergence between two examples.
     * @param  selfDivergence The maxValue for self-divergence to use, which
     *         controls the number of clusters created.
     * @param  dampingFactor The damping factor (lambda). Must be between 0.0
     *         and 1.0.
     * @param  maxIterations The maximum number of iterations.
     */
    OptimizedAffinityPropagation(
        DivergenceFunction<? super T, ? super T> divergence,
        double selfDivergence,
        double dampingFactor,
        int maxIterations)
    {
      super(divergence, selfDivergence, dampingFactor, maxIterations);
    }

    @Override
    protected boolean initializeAlgorithm()
    {
      if (this.getData() == null || this.getData().isEmpty())
      {
        // Make sure that the data is valid.
        return false;
      }

      // Initialize the main data for the algorithm.
      setExamples(new ArrayList<>(getData()));
      setSimilarities(new double[exampleCount][exampleCount]);
      setResponsibilities(new double[exampleCount][exampleCount]);
      setAvailabilities(new double[exampleCount][exampleCount]);

      double[] divergenciesArray = new double[exampleCount * (exampleCount+1)/2];
      boolean computeSelfDivergence = Double.isNaN(getSelfDivergence());

      // Compute the similarity matrix.
      fillSimilarityStructures(divergenciesArray);

      if(computeSelfDivergence) {
        double median = computeMedian(divergenciesArray);
        setSelfDivergence(median);
      }

      fillSimilarityDiagonal();

      // Initialize the assignments to -1, the changed count, and the
      // clusters.
      this.setAssignments(new int[exampleCount]);
      this.setChangedCount(exampleCount);
      this.setClusters(new HashMap<>());
      for (int i = 0; i < exampleCount; i++)
      {
        this.assignments[i] = -1;
      }

      // Ready to learn.
      return true;
    }

    /**
     * Set the self similarity based on the self divergence.
     */
    private void fillSimilarityDiagonal()
    {
      for (int i = 0; i < exampleCount; i++)
      {
        similarities[i][i] = -getSelfDivergence();
      }
    }

    private void fillSimilarityStructures(double[] divergenciesArray)
    {
      for (int i = 0; i < exampleCount; i++)
      {
        final T exampleI = examples.get(i);

        for (int j = 0; j <= i; j++)
        {
          // We compute similarity, which is the negative of divergence,
          // since a lower divergence means a higher similarity.
          final T exampleJ = examples.get(j);
          final double similarity = -divergence.evaluate(
              exampleI, exampleJ);
          similarities[i][j] = similarity;
          similarities[j][i] = similarity;
          divergenciesArray[i * (i+1) / 2 + j] = -similarity;
        }
      }
    }

    private double computeMedian(double[] divergenciesArray)
    {

      double[] doubles = Arrays.stream(divergenciesArray)
          .flatMap(DoubleStream::of)
          .distinct()
          .sorted()
          .toArray();

      double median;
      int size = doubles.length;
      if (size == 0) {
        median = 0;
      } else {
        if (size % 2 == 0)
          median = (doubles[size / 2] + doubles[size / 2 - 1]) / 2;
        else
          median = doubles[size / 2];
      }
      return median;

    }

    /**
     * Updates the responsibilities matrix using the similarity values and the
     * current availability values.
     */
    @Override
    protected void updateResponsibilities()
    {

      for(int i = 0 ; i < exampleCount; i++) {
        double[] rowArray = createSumArray(availabilities[i], similarities[i]);
        MaxResult maxResult = computeMax(rowArray);
        for (int j = 0; j < exampleCount; j++) {
          double maxToSubtract = j != maxResult.maxIndex ? maxResult.maxValue : maxResult.secondMaxValue;
          responsibilities[i][j] = oneMinusDampingFactor * (similarities[i][j] - maxToSubtract) + dampingFactor * responsibilities[i][j];
        }
      }
    }

    private MaxResult computeMax(double[] rowArray)
    {
      MaxResult maxResult = new MaxResult();
      for (int i = 0; i < rowArray.length; i++) {
        if(rowArray[i] > maxResult.maxValue){
          maxResult.secondMaxValue = maxResult.maxValue;
          maxResult.maxValue = rowArray[i];
          maxResult.maxIndex = i;
        }else if(rowArray[i] > maxResult.secondMaxValue){
          maxResult.secondMaxValue = rowArray[i];
        }
      }
      return maxResult;
    }


    private double[] createSumArray(double[] availability, double[] similarity)
    {
      double[] resultArray = new double[availability.length];
      for (int i = 0; i < availability.length; i++) {
        resultArray[i] = availability[i] + similarity[i];
      }
      return resultArray;
    }

    /**
     * Updates the availabilities matrix based on the current responsibility
     * values.
     */
    @Override
    protected void updateAvailabilities()
    {
      ColumnArray rp = new ColumnArray(exampleCount);
      for (int j = 0; j < exampleCount; j++) {
        removeNegativeValues(rp, responsibilities, j);
        rp.array[j] = responsibilities[j][j];
        rp.sum += responsibilities[j][j];
        updateAvailabilitiesMatrix(j, rp, availabilities);
      }
    }

    private void updateAvailabilitiesMatrix(int j, ColumnArray rp, double[][] availabilities)
    {
      double old;
      for (int i = 0; i < availabilities[0].length; i++) {
        old = availabilities[i][j];
        availabilities[i][j] = rp.sum - rp.array[i];
        if(i != j){
          availabilities[i][j] = Math.min(availabilities[i][j], 0);
        }
        availabilities[i][j] = oneMinusDampingFactor * availabilities[i][j] + dampingFactor * old;
      }
    }

    private void removeNegativeValues(ColumnArray columnArray, double[][] matrix, int j)
    {
      columnArray.sum = 0;
      for (int i = 0; i < matrix.length; i++) {
        columnArray.array[i] = Math.max(matrix[i][j], 0);
        columnArray.sum += columnArray.array[i];
      }
    }

    class MaxResult {
      int maxIndex;
      double maxValue;
      double secondMaxValue;

      MaxResult(){
        maxValue = Double.NEGATIVE_INFINITY;
        secondMaxValue = Double.NEGATIVE_INFINITY;
        maxIndex = -1;
      }
    }

    class ColumnArray
    {
      double[] array;
      double sum;

      ColumnArray(int size){
        array = new double[size];
        sum = 0.;
      }
    }
  }
