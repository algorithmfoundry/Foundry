/*
 * File:                VectorFunctionToScalarFunction.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright December 20, 2007, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 * See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.learning.function.scalar;

import gov.sandia.cognition.annotation.CodeReview;
import gov.sandia.cognition.evaluator.Evaluator;
import gov.sandia.cognition.learning.algorithm.BatchLearner;
import gov.sandia.cognition.learning.algorithm.SupervisedBatchLearner;
import gov.sandia.cognition.learning.data.DatasetUtil;
import gov.sandia.cognition.learning.data.DefaultWeightedInputOutputPair;
import gov.sandia.cognition.learning.data.InputOutputPair;
import gov.sandia.cognition.learning.data.WeightedInputOutputPair;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.math.matrix.Vectorizable;
import gov.sandia.cognition.util.AbstractCloneableSerializable;
import gov.sandia.cognition.util.ObjectUtil;
import java.util.ArrayList;
import java.util.Collection;

/**
 * The {@code VectorFunctionToScalarFunction} class implements an adapter for
 * using a vector function that outputs a single-dimensional vector as a
 * scalar function.
 * 
 * @param   <InputType> The type of the input to the function.
 * @author  Justin Basilico
 * @since   2.0
 */
@CodeReview(
    reviewer="Kevin R. Dixon",
    date="2009-07-06",
    changesNeeded=false,
    comments={
        "Made clone() call super.clone().",
        "Made Learner inherit SupervisedBatchLearner",
        "Otherwise, class looks fine."
    }
)
public class VectorFunctionToScalarFunction<InputType>
    extends AbstractCloneableSerializable
    implements Evaluator<InputType, Double>
{

    /** The function that takes a given input and outputs a 1-dimensional 
     *  vector. */
    protected Evaluator<? super InputType, ? extends Vectorizable> 
        vectorFunction;
    
    /**
     * Creates a new instance of {@code VectorFunctionToScalarFunction}.
     */
    public VectorFunctionToScalarFunction()
    {
        this(null);
    }
    
    /**
     * Creates a new instance of {@code VectorFunctionToScalarFunction}.
     * 
     * @param   vectorFunction The function that outputs a 1-dimensional 
     *          vector.
     */
    public VectorFunctionToScalarFunction(
        final Evaluator<? super InputType, ? extends Vectorizable> 
            vectorFunction)
    {
        super();
        
        this.setVectorFunction(vectorFunction);
    }

    @Override
    public VectorFunctionToScalarFunction<InputType> clone()
    {
        @SuppressWarnings("unchecked")
        final VectorFunctionToScalarFunction<InputType> result = 
            (VectorFunctionToScalarFunction<InputType>) super.clone();
        result.vectorFunction = ObjectUtil.cloneSmart(this.vectorFunction);
        return result;
    }
    
    
    public Double evaluate(
        final InputType input)
    {
        Vectorizable output = this.vectorFunction.evaluate(input);
        return output.convertToVector().getElement(0);
    }

    /**
     * Gets the vector function with a one-dimensional output that is being 
     * converted to a scalar function.
     * 
     * @return  The vector function with a one-dimensional output.
     */
    public Evaluator<? super InputType, ? extends Vectorizable>
        getVectorFunction()
    {
        return this.vectorFunction;
    }

    /**
     * Sets the vector function with a one-dimensional output that is being 
     * converted to a scalar function.
     * 
     * @param   vectorFunction The vector function with a one-dimensional output.
     */
    public void setVectorFunction(
        final Evaluator<? super InputType, ? extends Vectorizable> 
            vectorFunction)
    {
        this.vectorFunction = vectorFunction;
    }

    /**
     * The {@code VectorFunctionToScalarFunction.Learner} class implements a
     * simple learner for a {@code VectorFunctionToScalarFunction} that allows
     * a learning algorithm that outputs a vector function to be adapted to 
     * learn on data whose output are doubles.
     * 
     * @param <InputType> The input type for supervised learning.
     */
    public static class Learner<InputType>
        extends AbstractCloneableSerializable
        implements SupervisedBatchLearner<InputType,Double,VectorFunctionToScalarFunction<InputType>>
    {
        /** The supervised learner that learns on vectors as outputs. */
        protected BatchLearner<Collection<? extends InputOutputPair<? extends InputType, Vector>>,
            ? extends Evaluator<? super InputType, ? extends Vectorizable>> vectorLearner;
        
        /**
         * Creates a new {@code VectorFunctionToScalarFunction.Learner}.
         */
        public Learner()
        {
            this(null);
        }
        
        /**
         * Creates a new {@code VectorFunctionToScalarFunction.Learner}.
         * 
         * @param   vectorLearner The supervised learner to use that learns on 
         *          vectors as outputs.
         */
        public Learner(
            final BatchLearner<Collection<? extends InputOutputPair<? extends InputType, Vector>>,
                ? extends Evaluator<? super InputType, ? extends Vectorizable>> vectorLearner)
        {
            super();
            
            this.vectorLearner = vectorLearner;
        }

        @Override
        @SuppressWarnings("unchecked")
        public Learner clone()
        {
            final Learner result = (Learner) super.clone();
            result.vectorLearner = ObjectUtil.cloneSafe(this.vectorLearner);
            return result;
        }
        
        public VectorFunctionToScalarFunction<InputType> learn(
            final Collection<? extends InputOutputPair<? extends InputType, Double>> data)
        {
            // Create a copy of the data where the output is a vector, not a
            // double.
            int count = data.size();
            ArrayList<WeightedInputOutputPair<InputType, Vector>> vectorData =
                new ArrayList<WeightedInputOutputPair<InputType, Vector>>(
                    count);
            
            // Convert all the data.
            for (InputOutputPair<? extends InputType, Double> example : data)
            {
                final InputType input = example.getInput();
                final double output = example.getOutput();
                Vector outputVector = 
                    VectorFactory.getDefault().copyValues(output);
                final double weight = DatasetUtil.getWeight(example);
                vectorData.add(new DefaultWeightedInputOutputPair<InputType, Vector>(
                    input, outputVector, weight));
            }
        
            // Learn the vector function.
            Evaluator<? super InputType, ? extends Vectorizable> 
                vectorFunction = this.vectorLearner.learn(vectorData);
            
            // Return the wrapper.
            return new VectorFunctionToScalarFunction<InputType>(
                vectorFunction);
        }
    }

}
