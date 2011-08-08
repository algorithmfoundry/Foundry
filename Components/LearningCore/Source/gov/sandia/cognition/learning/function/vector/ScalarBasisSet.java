/*
 * File:                ScalarBasisSet.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Nov 30, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 * 
 */

package gov.sandia.cognition.learning.function.vector;

import gov.sandia.cognition.evaluator.Evaluator;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorOutputEvaluator;
import gov.sandia.cognition.util.AbstractCloneableSerializable;
import gov.sandia.cognition.util.CloneableSerializable;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Collection of scalar basis functions, where the ith function operates
 * on the ith element of the output Vector
 * 
 * @param <InputType> Input class that the basis function operate upon
 * @author Kevin R. Dixon
 */
public class ScalarBasisSet<InputType>
    extends AbstractCloneableSerializable
    implements Evaluator<InputType, Vector>,
    VectorOutputEvaluator<InputType,Vector>
{

    /**
     * Collection of scalar basis functions, where the ith function operates
     * on the ith element of the output Vector
     */
    private Collection<? extends Evaluator<? super InputType, Double>> basisFunctions;

    /** 
     * Creates a new instance of ScalarBasisSet 
     * @param basisFunctions 
     * Collection of scalar basis functions, where the ith function operates
     * on the ith element of the output Vector
     */
    public ScalarBasisSet(
        Collection<? extends Evaluator<? super InputType, Double>> basisFunctions )
    {
        this.setBasisFunctions( basisFunctions );
    }

    /**
     * Copy Constructor
     * @param other ScalarBasisSet to copy
     */
    public ScalarBasisSet(
        ScalarBasisSet<InputType> other )
    {
        this( new ArrayList<Evaluator<? super InputType, Double>>(
            other.getBasisFunctions() ) );
    }

    public int getOutputDimensionality()
    {
        return this.getBasisFunctions().size();
    }

    public Vector evaluate(
        InputType input )
    {

        Vector output = VectorFactory.getDefault().createVector(
            this.getOutputDimensionality() );

        int i = 0;
        for (Evaluator<? super InputType, Double> f : this.getBasisFunctions())
        {
            output.setElement( i, f.evaluate( input ) );
            i++;
        }

        return output;
    }

    /**
     * Getter for basisFunctions
     * @return
     * Collection of scalar basis functions, where the ith function operates
     * on the ith element of the output Vector
     */
    public Collection<? extends Evaluator<? super InputType, Double>> getBasisFunctions()
    {
        return this.basisFunctions;
    }

    /**
     * Setter for basisFunctions
     * @param basisFunctions
     * Collection of scalar basis functions, where the ith function operates
     * on the ith element of the output Vector
     */
    public void setBasisFunctions(
        Collection<? extends Evaluator<? super InputType, Double>> basisFunctions )
    {
        this.basisFunctions = basisFunctions;
    }

}
