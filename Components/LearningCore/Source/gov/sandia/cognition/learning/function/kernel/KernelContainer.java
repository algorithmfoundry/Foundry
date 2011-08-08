/*
 * File:                KernelContainer.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright July 1, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.learning.function.kernel;

import gov.sandia.cognition.annotation.CodeReview;
import gov.sandia.cognition.util.CloneableSerializable;

/**
 * Defines an object that contains a Kernel.
 *
 * @param   <InputType> Input type of the Kernel.
 * @author  Kevin R. Dixon
 * @since   3.0
 */
@CodeReview(
    reviewer="Kevin R. Dixon",
    date="2009-07-08",
    changesNeeded=false,
    comments="Looks fine."
)
public interface KernelContainer<InputType>
    extends CloneableSerializable
{
    /**
     * Gets the kernel.
     *
     * @return
     *      Internal kernel encapsulated by the KernelContainer.
     */
    public Kernel<? super InputType> getKernel();
    
}
