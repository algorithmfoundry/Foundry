/*
 * File:                ObjectUtil.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright June 28, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.util;

import gov.sandia.cognition.collection.CollectionUtil;
import gov.sandia.cognition.io.FileUtil;
import gov.sandia.cognition.io.ObjectSerializationHandler;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The ObjectUtil class implements static utility methods for dealing with
 * Objects.
 *
 * @author Justin Basilico
 * @since  2.0
 */
public class ObjectUtil
    extends Object
{
    
    /**
     * Determines if two objects are equals in a way that is safe for dealing
     * with null. Null is only equal to itself.
     *
     * @param  first The first object.
     * @param  second The second object.
     * @return True if the two objects are equal and false otherwise.
     */
    public static boolean equalsSafe(
        Object first,
        Object second)
    {
        if ( first == null )
        {
            // Only equal if both are null.
            return second == null;
        }
        else if ( second == null )
        {
            // First is not null so they are not equal.
            return false;
        }
        else
        {
            // Call the equals method.
            return first.equals(second);
        }
    }

    /**
     * Determines the hash code of the given value by calling the hashCode
     * method on the given object if it is not null. If it is null, 0 is
     * returned.
     *
     * @param   value
     *      The value to get the hash code for. May be null.
     * @return
     *      The hash code of the given value if it is not null; otherwise, 0.
     */
    public static int hashCodeSafe(
        final Object value)
    {
        if (value == null)
        {
            return 0;
        }
        else
        {
            return value.hashCode();
        }
    }
    
    /**
     * Calls the Clone method on the given object of some type that extends
     * CloneableSerializable.
     *
     * Note: It assumes that the given object's clone method will return the
     * same object of type T.
     *
     * @param   <T> The type of the object to clone.
     * @param   object The object to clone.
     * @return  The clone.
     */
    @SuppressWarnings("unchecked")
    public static <T extends CloneableSerializable> T cloneSafe(
        T object)
    {
        if ( object == null )
        {
            return null;
        }
        else
        {
            return (T) object.clone();
        }
    }
    
    /**
     * Attempts to clone a given object. If the passed object is null, then
     * null is returned. Otherwise, it checks to see if the object implements
     * the {@code CloneableSerializable} interface. If  it does, it calls the
     * {@code cloneSafe} method with it. If not, it attempts to see if there is
     * a clone method on the given object, and if so it attempts to call the
     * method. If this fails, then it just returns the given object.
     * 
     * Note: It assumes that the given object's clone method will return the
     * same object of type T.
     * 
     * @param   <T> The type of the object to clone.
     * @param   object The object to clone.
     * @return  
     *      A clone of the given object, if it possible to clone it. If it is
     *      not, then the original object is returned.
     */
    @SuppressWarnings("unchecked")
    public static <T> T cloneSmart(
        final T object)
    {
        if (object == null)
        {
            // Its null, so there is no clone.
            return null;
        }
        else if (object instanceof CloneableSerializable)
        {
            // Try to clone via the CloneableSerializable interface.
            return (T) cloneSafe((CloneableSerializable) object);
        }
        
        // Look for a clone method by reflection.
        final Class<?> clazz = object.getClass();
        Method cloneMethod = null;
        try
        {
            cloneMethod = clazz.getDeclaredMethod("clone");
        }
        catch (NoSuchMethodException ex)
        {
        }
        catch (SecurityException ex)
        {
        }
        
        if (cloneMethod != null)
        {
            // Try to invoke the clone method.
            try
            {
                final Object clone = cloneMethod.invoke(object);
                return (T) clone;
            }
            catch (IllegalAccessException ex)
            {
            }
            catch (IllegalArgumentException ex)
            {
            }
            catch (InvocationTargetException ex)
            {
            }
        }
        
        // Couldn't clone it, so just return the original.
        return object;
    }
    
    /**
     * Creates a new {@code ArrayList} and attempts to copy all of the elements
     * from the given collection into it by calling the cloneSmart method on
     * each of them.
     * 
     * @param   <T> The type of the elements in the list.
     * @param   collection The collection of elements to clone.
     * @return  A new {@code ArrayList} with a clone of each element in it.
     */
    public static <T> ArrayList<T> cloneSmartElementsAsArrayList(
        final Collection<T> collection)
    {

        if( collection == null )
        {
            return null;
        }
        
        final ArrayList<T> result = new ArrayList<T>(collection.size());
        
        for (T value : collection)
        {
            result.add(cloneSmart(value));
        }
        
        return result;
    }
    
    /**
     * Creates a new {@code LinkedList} and attempts to copy all of the elements
     * from the given collection into it by calling the cloneSmart method on
     * each of them.
     * 
     * @param   <T> The type of the elements in the list.
     * @param   collection The collection of elements to clone.
     * @return  A new {@code LinkedList} with a clone of each element in it.
     */
    public static <T> LinkedList<T> cloneSmartElementsAsLinkedList(
        final Iterable<T> collection)
    {

        if( collection == null )
        {
            return null;
        }

        final LinkedList<T> result = new LinkedList<T>();
        
        for (T value : collection)
        {
            result.add(cloneSmart(value));
        }
        
        return result;
    }
    
    /**
     * Clones an array and its elements.
     * 
     * @param   <T> The type of object in the array.
     * @param   array The array to copy, along with its elements.
     * @return  A new smart copy of the array and its elements.
     */
    public static <T> T[] cloneSmartArrayAndElements(
        final T[] array)
    {
        if (array == null)
        {
            return null;
        }
        
        @SuppressWarnings("unchecked")
        final T[] result = (T[]) Array.newInstance(
            array.getClass().getComponentType(), array.length);
        for (int i = 0; i < result.length; i++)
        {
            result[i] = cloneSmart(array[i]);
        }
        return result;
    }
    
    /**
     * Performs a deep copy of a given object. Works by using serialization to
     * write out an object and read it back in. It should only be used if you
     * are really sure you want a deep copy of an object.
     * 
     * @param   <T> The type of the object to copy.
     * @param   object The object to copy.
     * @return  A deep copy of the given object or null, if the copy fails.
     */
    @SuppressWarnings("unchecked")
    public static <T extends Serializable> T deepCopy(
        final T object)
    {
        if (object == null)
        {
            // Don't attempt to deep copy null.
            return null;
        }
        
        try
        {
            byte[] bytes = ObjectSerializationHandler.convertToBytes(object);
            return (T) ObjectSerializationHandler.convertFromBytes(bytes);
        }
        catch (Exception ex)
        {
            Logger.getLogger(ObjectUtil.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
    
    /**
     * Prints out the Methods and Fields associated with the argument.  If the
     * argument is a type of "Class", then the referenced Class is accessed.
     * If the argument is a String, then the String is examined to extract
     * the name of the Class: e.g., "gov.sandia.cognition.util.DefaultPair".
     * Otherwise, the argument is taken to be an instantiated Object, and
     * we inspect the methods and fields of the object itself.
     * @param o 
     * Object/Class/String to inspect from
     * @return 
     * String representing a list of the Methods and Fields of the given 
     * argument.  If a ClassNotFoundException gets thrown, this method
     * intercepts the Exception and simply returns the Exception String as
     * the return value (thus, no Exceptions are thrown).
     */
    public static String inspector(
        final Object o)
    {
        Object instantiatedObject = null;
        Class c = null;
        if ( o instanceof String )
        {
            try
            {
                String s = (String) o;
                c = Class.forName( s );
            }
            catch (Exception e)
            {
                return "Exception: " + e.toString();
            }
        }
        else if ( o instanceof Class )
        {
            c = (Class) o;
        }
        else
        {
            c = o.getClass();
            instantiatedObject = o;
        }
        
        StringBuilder retval = new StringBuilder();
        retval.append( "Class "+ c.getSimpleName() + " extends " + c.getSuperclass() + "\n" );
        if( c.getInterfaces().length > 0 )
        {
            retval.append( "Implements " + c.getInterfaces().length + " interfaces:" );
            for( Class i : c.getInterfaces() )
            {
                retval.append( " " + i.getSimpleName() );
            }
            retval.append( "\n" );
        }
        
        retval.append("------------- Methods ----------------\n");
        
        retval.append( ObjectUtil.inspectAPI( c )  );
        
        retval.append("------------- Fields ----------------\n");
        
        if( instantiatedObject == null )
        {
            retval.append( ObjectUtil.inspectFields( c )  );
        }
        else
        {
            retval.append( ObjectUtil.inspectFieldValues( instantiatedObject )  );
        }
        
        return retval.toString();
    }
    
        
    /**
     * Creates a String representing the Constructors and Methods from the
     * given Class, inspects public constructors and all 
     * public/private/protected methods (except those from the Object class).
     * @param c 
     * Class to determine the API of
     * @return 
     * String representing the Constructors and Methods
     */
    public static String inspectAPI(
        final Class c )
    {
        
        LinkedList<Method> methods = ObjectUtil.getMethods( c );
        
        StringBuilder retval = new StringBuilder();
        
        Constructor[] constructors = c.getConstructors();
        
        for( Constructor m : constructors )
        {
            retval.append( Modifier.toString( m.getModifiers() ) + " " );
            retval.append( m.getName() );
            retval.append( "(" );
            for( int j = 0; j < m.getParameterTypes().length; j++ )
            {
                retval.append( " " + m.getParameterTypes()[j].getSimpleName() );
                if( j < m.getParameterTypes().length-1 )
                {
                    retval.append( "," );
                }
                else
                {
                    retval.append( " " );
                }
            }
            retval.append( ")" );
            retval.append( "\n" );
        }
        
        for( Method m : methods )
        {
            retval.append( Modifier.toString( m.getModifiers() ) + " " );
            retval.append( m.getReturnType().getSimpleName() + " " );
            retval.append( m.getName() );
            retval.append( "(" );
            for( int j = 0; j < m.getParameterTypes().length; j++ )
            {
                retval.append( " " + m.getParameterTypes()[j].getSimpleName() );
                if( j < m.getParameterTypes().length-1 )
                {
                    retval.append( "," );
                }
                else
                {
                    retval.append( " " );
                }
            }
            retval.append( ")" );
            retval.append( "\n" );
        }
        
        return retval.toString();
        
    }
    
    
    /**
     * Gets the Collection of all public/private/protected Methods in the
     * given Class (except those only given in the Object class).  If the 
     * argument represents an interface, then the interface methods are listed.
     * Otherwise, only nonvolatile (i.e., not abstract) methods are returned.
     * @param c 
     * Class from which to pull the Methods
     * @return 
     * Collection of all public/private/protected Methods in the given Class
     */
    public static LinkedList<Method> getMethods(
        final Class c)
    {
        // Don't recurse if te class is null or if it is Object.
        if ( c == null || Object.class.equals( c ) )
        {
            return new LinkedList<Method>();
        }
        
        final LinkedList<Method> methods = 
            ObjectUtil.getMethods(c.getSuperclass());
        
        final boolean isInterface = c.isInterface();
        for ( Method m : c.getDeclaredMethods() )
        {
            // If we're an interface, then include interface methods
            // Otherwise, don't include "volatile" methods, as those
            // appear from interfaces only
            if ( isInterface || !Modifier.isVolatile( m.getModifiers() ) )
            {
                methods.add( m );
            }
        }
        
        return methods;
        
    }

    /**
     * Creates a String that contains the names and values of the members, or
     * the default Object toString if that fails.
     * @param o
     * Object to convert to a String
     * @return
     * String with member names and values of the members, or the
     * default Object toString if that fails.
     */
    public static String toString(
        Object o )
    {
        String retval;
        try
        {
            if( o.getClass().isArray() )
            {
                final int num = Array.getLength(o);
                StringBuilder buffer = new StringBuilder( num * 20 );
                buffer.append( o.getClass().getSimpleName() + " with " + num + " entries:\n" );
                for( int i = 0; i < num; i++ )
                {
                    buffer.append( "Index " + i + ":\n" + ObjectUtil.toString( Array.get(o,i) ) );
                }
                retval = buffer.toString();
            }
            else if( o instanceof Iterable )
            {
                Iterable iterable = (Iterable) o;
                final int num = CollectionUtil.size(iterable);
                StringBuilder buffer = new StringBuilder( num * 20 );
                buffer.append( o.getClass().getSimpleName() + " with " + num + " entries:\n" );
                int index = 0;
                for( Object i : iterable )
                {
                    buffer.append( "Index " + index + ":\n" + ObjectUtil.toString(i) );
                    index++;
                }
                retval = buffer.toString();
            }
            else
            {
                retval = o.getClass().getSimpleName() + " field values:\n"
                    + ObjectUtil.inspectFieldValues( o );
            }
        }
        catch (Exception e)
        {
            retval = o.toString();
        }

        return retval;

    }

    /**
     * Returns a String representing the values of all public/private/protected 
     * fields in the given instantiated Object, e.g., "double myField = 3.14"
     * @param o 
     * Instantiated Object to inspect
     * @return 
     * String representing the values of all public/private/protected fields
     */
    public static String inspectFieldValues(
        final Object o )
    {
        ArrayList<DefaultPair<Field,Object>> fieldValuePairs = 
            ObjectUtil.getAllFieldValues( o );
        StringBuilder retval = new StringBuilder( fieldValuePairs.size() * 50 );
        for( DefaultPair<Field,Object> fieldValuePair : fieldValuePairs )
        {
            // Skip static fields.
            Field f = fieldValuePair.getFirst();
            if( !Modifier.isStatic( f.getModifiers() ) )
            {
                retval.append( f.getType().getSimpleName() + " " );
                retval.append( f.getName() );

                Object v = fieldValuePair.getSecond();
                if( v != null )
                {
                    retval.append( " = " + v.toString() );
                }
                else
                {
                    retval.append( " = {null or value protected}" );
                }
                retval.append( "\n" );
            }
        }
        
        return retval.toString();
    }
    
    /**
     * Returns a String representing the values of all public/private/protected 
     * fields in the given Class, e.g., "protected double myField"
     * @param c 
     * Class from which to inspect the fields
     * @return 
     * The String containing the name, type, and modifier of all fields.
     */
    public static String inspectFields(
        final Class c )
    {
        StringBuilder retval = new StringBuilder();
        
        for( Field f : ObjectUtil.getAllFields( c ) )
        {
            retval.append( Modifier.toString( f.getModifiers() ) + " " );
            retval.append( f.getType().getSimpleName() + " " );
            retval.append( f.getName() );
            retval.append( "\n" );
        }
        
        return retval.toString();
    }
    
    /**
     * Returns a list of all of the fields on the given class and any 
     * super-class. It includes fields of all access modifiers.
     *
     * @param c 
     *      The class to get all the fields from. 
     * @return The 
     *      list of fields on the given class or any of its super classes.
     */
    public static LinkedList<Field> getAllFields(
        final Class c)
    {
        // A null class has no fields.
        // Also, don't get fields from Object.
        if ( c == null || Object.class.equals( c ) )
        {
            return new LinkedList<Field>();
        }
        
        // Get the fields from the super class.
        final LinkedList<Field> fields = 
            ObjectUtil.getAllFields(c.getSuperclass());
        
        // Declared fields include both public and private members
        fields.addAll( Arrays.asList( c.getDeclaredFields() ) );
        
        return fields; 
    }
    
    /**
     * Returns an ArrayList of field/value pairs
     * @param o 
     * Object to get the field/value pairs
     * @return 
     * ArrayList of field/value pairs.  The value component of the DefaultPair will
     * be null if the field is protected by the security manager
     */
    public static ArrayList<DefaultPair<Field,Object>> getAllFieldValues(
        final Object o )
    {
        
        LinkedList<Field> fields = ObjectUtil.getAllFields( o.getClass() );
        ArrayList<Object> values = ObjectUtil.getFieldValues( o, fields );
        return DefaultPair.mergeCollections( fields, values );
        
    }
    
    
    /**
     * Gets the values of the given fields for the object (can get public / 
     * protected / private field values)
     * @param o 
     * Object to get the field values of
     * @param fields 
     * Fields to get the values of
     * @return 
     * ArrayList for the field values, null if not found of protected
     */
    public static ArrayList<Object> getFieldValues(
        final Object o,
        Collection<Field> fields )
    {
        
        ArrayList<Object> retval = new ArrayList<Object>( fields.size() );
        for( Field f : fields )
        {
            Object value;
            try
            {
                f.setAccessible(true);                
                value = f.get( o );
            }
            catch (Exception e)
            {
                value = null;
            }
            
            retval.add( value );
        }    
        
        return retval;        
        
    }
    
    /**
     * Gets a short version of the Class name, so that
     * gov.sandia.cognition.util.ObjectUtil$InternalClass will be returned as
     * "ObjectUtil$InternalClass", for example
     * @param o Object from which to pull the short Class name
     * @return
     * Short String representing the class name
     */
    public static String getShortClassName(
        final Object o )
    {
        return ObjectUtil.getShortClassName(o.getClass());
    }
    
    /**
     * Gets a short version of the Class name, so that
     * gov.sandia.cognition.util.ObjectUtil$InternalClass will be returned as
     * "ObjectUtil$InternalClass", for example
     * @param c Object from which to pull the short Class name
     * @return
     * Short String representing the class name
     */
    public static String getShortClassName(
        final Class c )
    {
        return FileUtil.getExtension(c.toString());
    }

    /**
     * Attempts to convert the given Object into a byte array
     * @param o
     * Object to convert
     * @return
     * bytes of the object, null if none
     */
    public static byte[] getBytes(
        Object o )
    {
        try
        {
            return ObjectSerializationHandler.convertToBytes( (Serializable) o );
        }
        catch (Exception ex)
        {
            return null;
        }
        
    }

}
