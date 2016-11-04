/*
 * File:                NativeBlasHandler.java
 * Authors:             Jeremy D. Wendt
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright 2015, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 */

package gov.sandia.cognition.math.matrix.custom;

import com.github.fommil.netlib.BLAS;
import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationType;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * This class provides a uniform interface between working with a native-coded
 * BLAS package or the jBLAS package. This code was begun from an example found
 * at http://www.jeshua.me/content/code/NetlibTest.java but modified for our
 * purposes.
 *
 * @author Jeremy D. Wendt
 * @since   3.4.3
 */
@PublicationReference(author = "Jeshua",
    title = "Netlib and Java",
    type = PublicationType.WebPage,
    year = 2013,
    url = "http://www.jeshua.me/content/code/NetlibTest.java")
public class NativeBlasHandler
{

    /**
     * The current version of BLAS to use (a reference to javaBlas or
     * nativeBlas)
     */
    private Field blasCurrent;

    /**
     * The Java BLAS implementation. Guaranteed to exist.
     */
    private Field javaBlas;

    /**
     * The native-code BLAS implementation. May be null if the library is not
     * found.
     */
    private Field nativeBlas;

    /**
     * Initializes the BLAS instances, searching for the native BLAS
     * implementation.
     */
    public NativeBlasHandler()
    {
        try
        {
            // get java blas object and make it accessible
            Class<?> javaBlasClass = Class.forName("com.github.fommil.netlib.BLAS");
            javaBlas = javaBlasClass.getDeclaredField("INSTANCE");
            Field jInstance = javaBlas.getClass().getDeclaredField("modifiers");
            jInstance.setAccessible(true);
            jInstance.setInt(javaBlas, javaBlas.getModifiers() & ~Modifier.FINAL);
            javaBlas.setAccessible(true);
            blasCurrent = javaBlas;

            if (nativeBlasAvailable())
            {
                // get native blas object and make it accessible
                Class<?> nativeBlasClass = Class.forName(
                    "com.github.fommil.netlib.NativeBLAS");
                nativeBlas = nativeBlasClass.getDeclaredField("INSTANCE");
                Field nInstance = nativeBlas.getClass().getDeclaredField(
                    "modifiers");
                nInstance.setAccessible(true);
                nInstance.setInt(nativeBlas, nativeBlas.getModifiers()
                    & ~Modifier.FINAL);
                nativeBlas.setAccessible(true);
                blasCurrent = nativeBlas;
            }
            else
            {
                nativeBlas = null;
            }
        }
        catch (ClassNotFoundException cnfe)
        {
            throw new IllegalStateException("Your machine isn't set up right",
                cnfe);
        }
        catch (NoSuchFieldException nsfe)
        {
            throw new IllegalStateException("This code has errors.", nsfe);
        }
        catch (IllegalAccessException iae)
        {
            throw new IllegalStateException("This code has errors.", iae);
        }
    }

    /**
     * Returns true if BLAS is available in any form. This should always return
     * true because we've included JBLAS.
     *
     * @return true if BLAS is available in any form.
     */
    public static boolean blasAvailable()
    {
        return (BLAS.getInstance() != null);
    }

    /**
     * Returns true if a native version of BLAS was found.
     *
     * @return true if a native version of BLAS was found
     */
    public static boolean nativeBlasAvailable()
    {
        return (BLAS.getInstance() != null)
            && "com.github.fommil.netlib.NativeBLAS".equals(
            BLAS.getInstance().getClass().getName());
    }

    /**
     * If native BLAS is available, this sets the BLAS library to use the native
     * version.
     */
    final public void setToNativeBlas()
    {
        if (nativeBlas != null)
        {
            try
            {
                blasCurrent.set(null, nativeBlas.get(null));
            }
            catch (IllegalAccessException iae)
            {
                throw new IllegalStateException("This code has errors.", iae);
            }
        }
    }

    /**
     * Sets the BLAS library to use the Java version.
     */
    final public void setToJBlas()
    {
        try
        {
            blasCurrent.set(null, javaBlas.get(null));
        }
        catch (IllegalAccessException iae)
        {
            throw new IllegalStateException("This code has errors.", iae);
        }
    }

}