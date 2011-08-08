/*
 * File:                DefaultKernelsContainer.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright September 18, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.learning.function.kernel;

import gov.sandia.cognition.annotation.CodeReview;
import gov.sandia.cognition.util.AbstractCloneableSerializable;
import gov.sandia.cognition.util.ObjectUtil;
import java.util.ArrayList;
import java.util.Collection;

/**
 * The {@code DefaultKernelsContainer} class implements a container of kernels.
 *
 * @author Justin Basilico
 * @since  2.0
 * @param <InputType> Input class to the Kernel 
 */
@CodeReview(
    reviewer="Kevin R. Dixon",
    date="2009-07-08",
    changesNeeded=false,
    comments={
        "Made clone call super.clone.",
        "Looks fine otherwise."
    }
)
public class DefaultKernelsContainer<InputType>
    extends AbstractCloneableSerializable
{

    /** The collection of kernels in the container. */
    protected Collection<? extends Kernel<? super InputType>> kernels;

    /**
     * Creates a new instance of DefaultKernelsContainer.
     */
    public DefaultKernelsContainer()
    {
        this( new ArrayList<Kernel<? super InputType>>() );
    }

    /**
     * Creates a new instance of DefaultKernelsContainer.
     *
     * @param  kernels A collection of kernels.
     */
    public DefaultKernelsContainer(
        final Collection<? extends Kernel<? super InputType>> kernels )
    {
        super();

        this.setKernels( kernels );
    }

    /**
     * Creates a new copy of the DefaultKernelsConainer.
     *
     * @param  other The DefaultKernelsContainer to copy.
     */
    public DefaultKernelsContainer(
        final DefaultKernelsContainer<InputType> other )
    {
        super();

        ArrayList<Kernel<? super InputType>> localKernels =
            new ArrayList<Kernel<? super InputType>>(
            other.getKernels().size() );

        for (Kernel<? super InputType> kernel : other.getKernels())
        {
            localKernels.add( kernel );
        }

        this.setKernels( localKernels );
    }

    @Override
    public DefaultKernelsContainer<InputType> clone()
    {
        @SuppressWarnings("unchecked")
        DefaultKernelsContainer<InputType> clone =
            (DefaultKernelsContainer<InputType>) super.clone();
        clone.setKernels(
            ObjectUtil.cloneSmartElementsAsArrayList( this.getKernels() ) );
        return clone;
    }

    /**
     * Gets the collection of kernels.
     *
     * @return The collection of kernels.
     */
    public Collection<? extends Kernel<? super InputType>> getKernels()
    {
        return this.kernels;
    }

    /**
     * Sets the collection of kernels.
     *
     * @param  kernels The collection of kernels.
     */
    public void setKernels(
        Collection<? extends Kernel<? super InputType>> kernels )
    {
        this.kernels = kernels;
    }

}
