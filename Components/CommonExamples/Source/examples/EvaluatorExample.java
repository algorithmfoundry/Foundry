/*
 * File:                EvaluatorExample.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright May 6, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package examples;

import gov.sandia.cognition.evaluator.Evaluator;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.math.matrix.Vectorizable;
import gov.sandia.cognition.util.AbstractCloneableSerializable;

/**
 * This class shows how to create an Evaluator, one of the most basic
 * classes in the Cognitive Foundry, in addition to main points of
 * the important class hierarchies in the Foundry.
 * <BR><BR>
 * This is an example of one of the most fundamental types in the Foundry,
 * the Evaluator.  An Evaluator is a simple interface that is intended
 * to define the functionality of a class that maps inputs onto an output,
 * like a mathematical "function."  Much of the Foundry incorporates
 * Java's generics (similar to C++'s templates).  In this case, the
 * class-generic parameters of an Evaluator define the type of the
 * input and output expected.
 * <BR><BR>
 * The reason for having an Evaluator interface in Java is that Java does
 * not allow first-level function pointers like C/C++ do.  For example, in
 * C/C++, one can specify that a function ("foo") takes another function as
 * an argument ("bar" with a definable input/output definition).  This
 * allows the function "foo" to invoke "bar".  Unless we probe the dark
 * bowels of Java's Reflection API, Java does not have a analogous ability.
 * Enter the Evaluator interface.  We know that a parameter to another
 * method of type "Evaluator<InputType,OutputType>" has a method "evaluate"
 * that takes a class of type "InputType" and maps that onto a class of
 * type "OutputType".  Many mathematically oriented objects in the Foundry
 * implement the Evaluator interface.  A quick "Find All Subtypes" found
 * 196 subtypes of Evaluator in the Foundry, as of this writing.
 * <BR><BR>
 * <BR><BR>
 * Another note about another frequently implemented/extended interface.
 * When Java was first created, the designers at Sun decided that objects
 * would not be serializable (e.g., written to disk) by default.  That
 * objects that could be written to disk would have to implement the
 * Java interface "Serializable" interface explicitly.  Most Java
 * developers have found out through experience that virtually all Java
 * classes should implement Serializable by default.
 * <BR><BR>
 * Perhaps the most bizarre design decision when Java was first created
 * involves Java's Cloneable interface.  The "clone" method in Java is a
 * type of "smart" copying.  That is, make deep copies of objects that
 * need to be truly copied over and otherwise just keep pointers to objects
 * that won't change the behavior of the encapsulating class.  To support
 * this feature, Java has a "clone" method in the Java root class, "Object".
 * However, Object's clone method is protected by default and so this
 * automatic cloning can't be invoked by another class without the subclass
 * explicitly exposing the "clone" method as public.  So, the developers at
 * Sun created the Cloneable interface.  Strangely enough, they appear to
 * have forgotten to include the "clone" method in the Cloneable interface.
 * To quote Sun's Technical Review Committee:
 * "Unfortunately, for reasons conveniently lost in the mists of time,
 * the Cloneable interface does not define a clone method. This combination
 * results in a fair amount of confusion."
 * <BR>
 * http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4098033
 * <BR><BR>
 * As such, most Java code bases create an interface that implements
 * the Serializable interface and the Cloneable interface AND defines
 * a public "clone()" method.  In the Foundry, we have created our own
 * obviously named CloneableSerializable interface, and its direct
 * cousin, the abstract class AbstractCloneableSerializable.  Because
 * many classes want by default the ability to serialize itself and
 * make a smart copy of itself, we expect most classes in the
 * Foundry will extend AbstractCloneableSerializable as the top-level
 * class or implement the CloneableSerializable interface. A quick
 * "Find All Subtypes" found 647 subtypes of CloneableSerializable as of this
 * writing.
 * <BR><BR>
 * <BR><BR>
 * Some of the high-level tools in the Foundry make use of the Java Beans
 * programming style.  We do not enforce this in any way, but we provide tools
 * that expected that Foundry classes:
 * <UL>
 * <LI>Have a public default (no-argument) constructor:
 * <BR>public MyClass()
 * <BR>{
 * <BR>    super();
 * <BR>}
 * </LI>
 * <LI>Access to internal variables is provided by CamelCase getter/setter.
 * Depending on the needs of the application, the getter and setter may be
 * public, protected, or private.
 * <BR>
 * <BR>private int internalValue;
 * <BR>public int getInternalValue()
 * <BR>{
 * <BR>    return this.internalValue;
 * <BR>}
 * <BR>protected void setInternalValue(
 * <BR>    int internalValue )
 * <BR>{
 * <BR>    this.internalValue = internalValue;
 * <BR>}
 * </LI>
 * </UL>
 * <BR><BR>For more information on Java Beans, see the Wikipedia site:
 * http://en.wikipedia.org/wiki/JavaBeans
 * 
 * @author Kevin R. Dixon
 * @since 3.0
 */
public class EvaluatorExample
{

    /**
     * This function transforms the Math.cos function into a first-level
     * object that can be passed around.  This maps some type of Java Number
     * (Double, Integer, etc.) and turns it into a Double.
     * <BR>
     * Note that this class extends AbstractCloneableSerializable so that
     * it can be written to disk and cloned.
     * <BR>
     * Also note that the use of the class-parameter generic, InputType, is
     * pretty gratuitous: it doesn't really serve a useful purpose except
     * to demonstrate that we use generics.  I apologize for not thinking of
     * a quick and easy generics-based class for this example.
     * 
     * @param <InputType> Type of Java Number (Double, Integer, etc.) to expect
     * as input.
     */
    public static class CosineEvaluator<InputType extends Number>
        extends AbstractCloneableSerializable
        implements Evaluator<InputType, Double>
    {

        /**
         * Default constructor.
         */
        public CosineEvaluator()
        {
            super();
        }

        /**
         * Evaluate method that takes the cosine of the input and returns
         * it as a Double.
         * @param input
         * Type of Java Number (Double, Integer, etc.) to map through Math.cos.
         * @return
         * Cosine of the input.
         */
        public Double evaluate(
            InputType input)
        {
            return Math.cos( input.doubleValue() );
        }
        
    }

    /**
     * This function transforms the Math.sin function into a first-level
     * object that can be passed around.  This maps some type of Java Number
     * (Double, Integer, etc.) and turns it into a Double.
     * <BR>
     * Note that this class extends AbstractCloneableSerializable so that
     * it can be written to disk and cloned.
     *
     * @param <InputType> Type of Java Number (Double, Integer, etc.) to expect
     * as input.
     */
    public static class SineEvaluator<InputType extends Number>
        extends AbstractCloneableSerializable
        implements Evaluator<InputType, Double>
    {

        /**
         * Default constructor.
         */
        public SineEvaluator()
        {
            super();
        }

        /**
         * Evaluate method that takes the sine of the input and returns
         * it as a Double.
         * @param input
         * Type of Java Number (Double, Integer, etc.) to map through Math.sin.
         * @return
         * Sine of the input.
         */
        public Double evaluate(
            InputType input)
        {
            return Math.sin( input.doubleValue() );
        }

    }


    /**
     * This class has two internal Evaluators that it uses to pass an input
     * to each of them, square their results, and return the sum of squares.
     * @param <InputType>
     * Type of input that my Evaluators have
     */
    public static class FunctionSquarer<InputType extends Number>
        extends AbstractCloneableSerializable
        implements Evaluator<InputType,Double>
    {

        /**
         * The first function to square.
         */
        private Evaluator<InputType,Double> f1;

        /**
         * The second function to square.
         */
        private Evaluator<InputType,Double> f2;

        /**
         * Default constructor.
         */
        public FunctionSquarer()
        {
            this( null, null );
        }

        /**
         * Creates a new FunctionSquarer
         * @param f1 First function to square.
         * @param f2 Second function to square.
         */
        public FunctionSquarer(
            final Evaluator<InputType,Double> f1,
            final Evaluator<InputType,Double> f2 )
        {
            this.f1 = f1;
            this.f2 = f2;
        }

        /**
         * Returns sum of the squared outputs from each of my two Evaluators.
         * @param input
         * Input to throw through my two Evaluators, square the respective
         * results, then return the sum of those two squared values.
         * @return
         * Sum of the squared outputs from each of my two Evaluators.
         */
        public Double evaluate(
            InputType input)
        {
            double y1 = this.getF1().evaluate(input);
            double y2 = this.getF2().evaluate(input);
            return y1*y1 + y2*y2;
        }

        /**
         * Getter for f1.
         * @return f1
         */
        public Evaluator<InputType, Double> getF1()
        {
            return this.f1;
        }

        /**
         * Setter for f1.
         * @param f1 f1
         */
        public void setF1(
            Evaluator<InputType, Double> f1)
        {
            this.f1 = f1;
        }

        /**
         * Getter for f2.
         * @return f2
         */
        public Evaluator<InputType, Double> getF2()
        {
            return this.f2;
        }

        /**
         * Setter for f2
         * @param f2 f2
         */
        public void setF2(
            Evaluator<InputType, Double> f2)
        {
            this.f2 = f2;
        }

    }


    /**
     * Another common thing that we want to do in the Foundry is convert
     * an object into its parameters.  This will be useful for things like
     * machine-learning optimization and so forth. Let's say that we want a
     * cosine Evaluator, but have the
     * magnitude and phase be parameters.
     * <BR>
     * That is, y = magnitude * cos( x + phase ).
     * <BR><BR>
     * Please note that we mean a mathematical Vector (n-dimensional group
     * of scalar values), not Java's useless Vector class.  Please see
     * the example "MatrixAndVectorExample.java" for more information.
     */
    public static class CosineEvaluatorWithMagnitudeAndPhase
        extends AbstractCloneableSerializable
        implements Evaluator<Double,Double>,
        Vectorizable
    {

        /**
         * Magnitude of the cosine.
         */
        private double magnitude;

        /**
         * Phase of the cosine.
         */
        private double phase;

        /**
         * Default constructor.
         */
        public CosineEvaluatorWithMagnitudeAndPhase()
        {
            this( 1.0, 0.0 );
        }

        /**
         * Creates a new CosineEvaluatorWithMagnitudeAndPhase
         * @param magnitude
         * Magnitude of the cosine.
         * @param phase
         * Phase of the cosine.
         */
        public CosineEvaluatorWithMagnitudeAndPhase(
            final double magnitude,
            final double phase )
        {
            this.magnitude = magnitude;
            this.phase = phase;
        }

        /**
         * Returns the cosine of the input with parameterized magnitude and
         * phase.
         * @param input
         * Input to the cosine
         * @return
         * magnitude * Math.cos( input + phase )
         */
        public Double evaluate(
            Double input)
        {
            return this.getMagnitude() * Math.cos( input + this.getPhase() );
        }

        @Override
        public CosineEvaluatorWithMagnitudeAndPhase clone()
        {
            // If you extend AbstractCloneableSerializable, then you
            // should only have to copy over non-elemental objects from
            // the resulting super.clone() call.
            return (CosineEvaluatorWithMagnitudeAndPhase) super.clone();
        }

        /**
         * This method returns the parameters of the class as a Vector.
         * @return
         * 2-dimensional Vector containing (magnitude,phase)
         */
        public Vector convertToVector()
        {
            return VectorFactory.getDefault().copyValues(
                this.getMagnitude(), this.getPhase());
        }

        /**
         * This method converts the parameters from a Vector to
         * (magnitude,phase).
         * @param parameters
         * 2-dimensional Vector containing (magnitude,phase)
         */
        public void convertFromVector(
            Vector parameters)
        {
            if( parameters.getDimensionality() != 2 )
            {
                throw new IllegalArgumentException(
                    "Expected 2 parameters: [ magnitude phase ]" );
            }
            this.setMagnitude(parameters.getElement(0));
            this.setPhase(parameters.getElement(1));
        }

        /**
         * Getter for magnitude.
         * @return Magnitude of the cosine.
         */
        public double getMagnitude()
        {
            return this.magnitude;
        }

        /**
         * Setter for magnitude.
         * @param magnitude Magnitude of the cosine.
         */
        public void setMagnitude(
            double magnitude)
        {
            this.magnitude = magnitude;
        }

        /**
         * Setter for phase.
         * @return Phase of the cosine.
         */
        public double getPhase()
        {
            return this.phase;
        }

        /**
         * Setter for phase.
         * @param phase Phase of the cosine.
         */
        public void setPhase(
            double phase)
        {
            this.phase = phase;
        }

    }


    /**
     * Entry point to the example
     * @param args
     * We don't use any command-line arguments.
     */
    public static void main(
        String ... args )
    {

        // Let's create a squarer with a cosine and sine... Does anybody
        // remember was cos^2 + sin^2 equals?  Let's find out!
        CosineEvaluator<Double> cosine = new CosineEvaluator<Double>();
        SineEvaluator<Double> sine = new SineEvaluator<Double>();
        FunctionSquarer<Double> squarer1 =
            new FunctionSquarer<Double>( cosine, sine );
        
        // Oh, yeah!  cos^2 + sin^2 = 1 for any input value!  Doh!
        final double x1 = 0.5;
        double y1 = squarer1.evaluate(x1);
        System.out.println( "x1: " + x1 + ", y1: " + y1 );
        
        // What's the value of sin^2 + sin^2?  I don't know, but for
        // x = 0.5, it's 0.4596976941318603.
        FunctionSquarer<Double> squarer2 =
            new FunctionSquarer<Double>( sine, sine );
        double y2 = squarer2.evaluate(x1);
        System.out.println( "x1: " + x1 + ", y2: " + y2 );

        final double magnitude1 = 10.0;
        final double phase1 = 2.0;
        CosineEvaluatorWithMagnitudeAndPhase f =
            new CosineEvaluatorWithMagnitudeAndPhase( magnitude1, phase1 );

        // This should equal the magnitude:
        double y3 = f.evaluate(-phase1);
        System.out.println( "Magnitude: " + magnitude1 + ", y3: " + y3 );

        // The parameters of the function are the (magnitude,phase) Vector:
        Vector parameters1 = f.convertToVector();
        System.out.println( "Parameters: " + parameters1 );

        // Let's change the magnitude to 0.0
        parameters1.setElement(0, 0.0);

        // Upload the parameters back into the function...
        // note that the magnitude was changed to 0.0
        f.convertFromVector(parameters1);
        System.out.println( "New magnitude: " + f.getMagnitude() );

        // Get the new parameter set and fiddle with them again
        Vector parameters2 = f.convertToVector();
        parameters2.setElement(0, magnitude1);
        parameters2.setElement(1, phase1*2.0);
        f.convertFromVector(parameters2);
        System.out.println( "New magnitude: " + f.getMagnitude() + ", New phase: " + f.getPhase() );
        System.out.println( "Parameters: " + f.convertToVector() );

    }

    
    
}
