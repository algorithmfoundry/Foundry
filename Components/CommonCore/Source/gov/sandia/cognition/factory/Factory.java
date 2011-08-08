/*
 * File:                Factory.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright April 18, 2008, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 * See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.factory;

import gov.sandia.cognition.annotation.CodeReview;

/**
 * The {@code Factory} interface defines a very general interface for a factory
 * object that can be used to create some other type of object.
 * 
 * @param   <CreatedType> The type of object that the factory creates.
 * @author  Justin Basilico
 * @since   2.1
 */
@CodeReview(
    reviewer="Kevin R. Dixon",
    date="2008-12-02",
    changesNeeded=false,
    comments={
        "I personally don't believe this class will be useful.",
        "However, I will defer... the interface itself is fine."
    }
)
public interface Factory<CreatedType>
{

    /**
     * Creates a new instance of an object.
     * 
     * @return A newly created object.
     */
    CreatedType create();

}
