/*
 * File:                CogxelWeightedInputOutputPairConverter.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Framework Lite
 *
 * Copyright July 17, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 *
 */

package gov.sandia.cognition.framework.learning.converter;

import gov.sandia.cognition.framework.CogxelState;
import gov.sandia.cognition.framework.SemanticIdentifierMap;
import gov.sandia.cognition.learning.data.DefaultWeightedInputOutputPair;
import gov.sandia.cognition.learning.data.InputOutputPair;
import gov.sandia.cognition.learning.data.WeightedInputOutputPair;

/**
 * A CogxelConverter for creating WeightedInputOutputPairs
 *
 * @param <InputType> Type of input to convert into the inputs of the 
 * InputOutputPairs
 * @param <OutputType> Type of outputs to convert into the outputs of the
 * InputOutputPairs
 * @author Kevin R. Dixon
 * @since  2.0
 *
 */
public class CogxelWeightedInputOutputPairConverter<InputType,OutputType>
    implements CogxelConverter<WeightedInputOutputPair<InputType,OutputType>>
{
    
    /**
     * Creates the underlying InputOutputPair
     */
    private CogxelInputOutputPairConverter<InputType, OutputType> pairConverter;

    /**
     * Adds the weight to the InputOutputPair
     */
    private CogxelConverter<Double> weightConverter;
    
    /** Creates a new instance of CogxelWeightedInputOutputPairConverter */
    public CogxelWeightedInputOutputPairConverter()
    {
        this( null, null, null );
    }
    
    /**
     * Creates a new instance of CogxelWeightedInputOutputPairConverter
     * @param inputConverter 
     * Converts Cogxels for the input
     * @param outputConverter 
     * Converts Cogxels for the output
     * @param weightConverter 
     * Converts the Cogxel for the weight
     */
    public CogxelWeightedInputOutputPairConverter(
        CogxelConverter<InputType> inputConverter,
        CogxelConverter<OutputType> outputConverter,
        CogxelConverter<Double> weightConverter )
    {
        this.setPairConverter( 
            new CogxelInputOutputPairConverter<InputType,OutputType>(
                inputConverter, outputConverter ) );
        this.setWeightConverter( weightConverter );
    }

    /**
     * Copy constructor
     * @param other Object to clone
     */
    @SuppressWarnings("unchecked")
    public CogxelWeightedInputOutputPairConverter(
        CogxelWeightedInputOutputPairConverter other )
    {
        this( other.getPairConverter().getInputConverter().clone(),
            other.getPairConverter().getOutputConverter().clone(),
            other.getWeightConverter().clone() );
    }
    
    /**
     * {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public CogxelWeightedInputOutputPairConverter<InputType,OutputType> clone()
    {
        return new CogxelWeightedInputOutputPairConverter<InputType,OutputType>( this );
    }
    
    /**
     * Getter for weightConverter
     * @return 
     * Adds the weight to the InputOutputPair
     */
    public CogxelConverter<Double> getWeightConverter()
    {
        return this.weightConverter;
    }

    /**
     * Setter for weightConverter
     * @param weightConverter 
     * Adds the weight to the InputOutputPair
     */
    public void setWeightConverter(
        CogxelConverter<Double> weightConverter)
    {
        this.weightConverter = weightConverter;
    }

    /**
     * {@inheritDoc}
     * @param cogxels {@inheritDoc}
     * @return {@inheritDoc}
     */
    public WeightedInputOutputPair<InputType, OutputType> fromCogxels(
        CogxelState cogxels)
    {
        InputOutputPair<InputType,OutputType> pair = 
            this.getPairConverter().fromCogxels( cogxels );
        double weight = this.getWeightConverter().fromCogxels( cogxels );
        return new DefaultWeightedInputOutputPair<InputType,OutputType>( pair, weight );
    }

    /**
     * {@inheritDoc}
     * @param data {@inheritDoc} 
     * @param cogxels {@inheritDoc}
     */
    public void toCogxels(
        WeightedInputOutputPair<InputType, OutputType> data,
        CogxelState cogxels)
    {
        this.getPairConverter().toCogxels( data, cogxels );
        this.getWeightConverter().toCogxels( data.getWeight(), cogxels );
    }

    /**
     * Getter for pairConverter
     * @return 
     * Creates the underlying InputOutputPair
     */
    public CogxelInputOutputPairConverter<InputType, OutputType> getPairConverter()
    {
        return this.pairConverter;
    }

    /**
     * Setter for pairConverter
     * @param pairConverter 
     * Creates the underlying InputOutputPair
     */
    public void setPairConverter(
        CogxelInputOutputPairConverter<InputType, OutputType> pairConverter)
    {
        this.pairConverter = pairConverter;
    }

    /**
     * {@inheritDoc}
     * @return {@inheritDoc}
     */
    public SemanticIdentifierMap getSemanticIdentifierMap()
    {
        return this.getPairConverter().getSemanticIdentifierMap();
    }

    /**
     * {@inheritDoc}
     * @param semanticIdentifierMap {@inheritDoc}
     */
    public void setSemanticIdentifierMap(
        SemanticIdentifierMap semanticIdentifierMap)
    {
        this.getPairConverter().setSemanticIdentifierMap( semanticIdentifierMap );
        this.getWeightConverter().setSemanticIdentifierMap( semanticIdentifierMap );
    }
    
}
