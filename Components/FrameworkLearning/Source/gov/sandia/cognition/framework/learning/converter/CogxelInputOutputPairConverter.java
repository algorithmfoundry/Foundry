/*
 * File:                InputOutputPairCogxelConverter.java
 * Authors:             Justin Basilico and Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright June 25, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.framework.learning.converter;

import gov.sandia.cognition.framework.SemanticIdentifierMap;
import gov.sandia.cognition.learning.data.DefaultInputOutputPair;
import gov.sandia.cognition.learning.data.InputOutputPair;

/**
 * The InputOutputPairCogxelConverter class implements a converter to and from 
 * Cogxels to InputOutputPair objects. This is done by specifying two sub
 * converters: one for the input type and one for the output type.
 *
 * @param <InputType> Type of input to convert into the inputs of the 
 * InputOutputPairs
 * @param <OutputType> Type of outputs to convert into the outputs of the
 * InputOutputPairs
 * @author Justin Basilico
 * @author Kevin R. Dixon
 * @since  2.0
 */
public class CogxelInputOutputPairConverter<InputType, OutputType>
    extends AbstractCogxelPairConverter<InputType,OutputType,InputOutputPair<InputType,OutputType>>
    implements CogxelConverter<InputOutputPair<InputType, OutputType>>
{
    
    /**
     * Default constructor
     */
    public CogxelInputOutputPairConverter()
    {
        this( null, null );
    }
    
    /**
     * Creates a new instance of InputOutputCogxelConverter.
     *
     * @param  inputConverter The CogxelConverter for the input element of the 
     *         pair.
     * @param  outputConverter The CogxelConverter for the output element of the
     *         pair.
     */
    public CogxelInputOutputPairConverter(
        CogxelConverter<InputType> inputConverter,
        CogxelConverter<OutputType> outputConverter )
    {
        this( inputConverter, outputConverter, null );
    }
    
    
    /**
     * Creates a new instance of InputOutputCogxelConverter.
     *
     * @param  inputConverter The CogxelConverter for the input element of the 
     *         pair.
     * @param  outputConverter The CogxelConverter for the output element of the
     *         pair.
     * @param  semanticIdentifierMap The SemanticIdentifierMap used by the
     *         converter.
     */
    public CogxelInputOutputPairConverter(
        CogxelConverter<InputType> inputConverter,
        CogxelConverter<OutputType> outputConverter,
        SemanticIdentifierMap semanticIdentifierMap)
    {
        super( inputConverter, outputConverter, semanticIdentifierMap );
    }
    
    @Override
    public boolean equals(
        final Object other)
    {
        return other instanceof CogxelInputOutputPairConverter
            && super.equals(other);
    }

    @Override
    public int hashCode()
    {
        return super.hashCode();
    }
    
    @Override
    public InputOutputPair<InputType, OutputType> createPair(
        InputType first,
        OutputType second )
    {
        return new DefaultInputOutputPair<InputType, OutputType>( first, second );
    }
    
    /**
     * Gets the input converter
     * @return Input converter
     */
    public CogxelConverter<InputType> getInputConverter()
    {
        return this.getFirstConverter();
    }
    
    /**
     * Gets the output converter
     * @return Output converter
     */
    public CogxelConverter<OutputType> getOutputConverter()
    {
        return this.getSecondConverter();
    }
            
    /**
     * Sets the input converter
     * @param inputConverter Input converter
     */
    public void setInputConverter(
        CogxelConverter<InputType> inputConverter )
    {
        this.setFirstConverter( inputConverter );
    }
    
    /**
     * Sets the output converter
     * @param outputConverter Output converter
     */
    public void setOutputConverter(
        CogxelConverter<OutputType> outputConverter )
    {
        this.setSecondConverter( outputConverter );
    }
    
}
