# Foundry contribution guide

Thank you for considering contributing to the Foundry. This document is a guide for making contributions and covers some of the details such as how to submit them and describes guidelines for code style, design, implementation, testing, and documentation.

## Submitting contributions

Contributing to the Foundry is handled through pull requests on the [Foundry GitHub repository](https://github.com/algorithmfoundry/Foundry).

To make a contribution, please use the following steps:
 1. If there isn’t already an issue open for the change you wish to make, open one on the [issue tracker](https://github.com/algorithmfoundry/Foundry/issues). If it is a big change or addition, make sure to collect feedback on the idea and potential design early on.
 2. Fork the code on GitHub and create a branch prefixed with “feature” and a clear name. For example, for adding Factorization Machine support, the branch could be named “feature-factorization-machine”.
 3. Implement the code following the guidelines below around design, style, and implementation.
 4. Add documentation for the change with appropriate Javadoc and potential example code, per the guidelines below.
 5. Write tests for the code following the testing guidelines below.
 6. Run all the unit tests and make sure they pass.
 7. Commit your code to your branch with descriptive commit messages for your changes and reference the issue number in it. The goal is to keep the change history readable. Squashing changes to a single commit can help here. Also, try to keep commits and pull requests to one change and avoid things like reformatting other parts of the code you aren't modifying, as it makes reviewing a lot more difficult.
 8. Make sure your branch is in a conflict-free state to be merged to master.
 9. Submit your pull request. From there it will be reviewed. If you have followed the guidelines listed here, it will likely be merged promptly. If not and there is feedback on the pull request, please make the requested changes and update it.

## Style guidelines

Code in the Foundry is meant to be clean and readable, not just functional and efficient. Keep this in mind when writing your code. This means using good names and attractions with judicious comments explaining how the code works.

We mostly follow the standard [Java style](http://www.oracle.com/technetwork/java/codeconventions-150003.pdf) guidelines. However, there are a few modifications we use. Most of these are captured in the style settings for NetBeans, which are provided with the repository. Here is an overview of those differences and other style aspects to be aware of:

- Use `this` whenever possible to make clear when member variables or methods are being called.
- Put `{` brackets on a newline for code blocks. There should be a newline after a `}` as well.
```java
if (foo)
{
   this.bar();
}
else if (baz)
{
   this.awesome();
}
else
{
   this.notAwesome();
}
```
- Use 4 spaces for indentation. No tabs.
- Always use braces `{ }`, even when optional.
- Put each method argument definition on a new line, indented by 4 spaces, even the first.
- Classes should always have an `extends` declaration, even if it is `extends Object`.  However, most Foundry classes inherit from `AbstractClonableSerializable` (at some point), so extending `Object` itself is rare. The extends is typically on a new line from the `class` definition and indented 4 spaces.
- Try to give descriptive and concise names to everything in the code: classes, methods, fields, variables, generic types, etc.
- Generic types are typically named with a `Type` suffix. For example, `KeyType` not just `T` or `TKey`.
- Avoid using cryptic or greek names for fields. Try to come up with names. For example: While `lambda` may be a convenient variable name for the math, might be better to name that parameter `regularization`, which more clearly defines its function.
- Method parameters should almost always be final.
- Avoid using `/* */` to make comments in the code and use `//`.
- Avoid naming fields, variables, methods, or classes with underscores (_) or unnecessary prefixes or suffixes. The only exception to this is static constants, which are named in all caps with underscores.
- Follow Java Language Specification for modifier ordering.
- New additions of classes or methods should have an `@since` notation with the next release number in the Javadoc.
- Do not use `Impl` class names. Come up with better names than that. If you really run out of ideas, make it a `Default` implementation.
- Don’t put multiple variable declarations on the same line.
- Capitalize acronyms where it makes sense. For example: `getXML()` not `getXml()`.
- Organize imports alphabetically. Avoid wildcard imports.

Generally: Make the code readable and make it look like the other code around it.

## Design guidelines

Generally, the Foundry is designed to be a a-la-carte library of functionality for Machine Learning. The pieces are meant to integrate well together and look a lot like the JDK. As such, the Foundry follows a lot of typical Java practices for design, but it also has a few non-standard design approaches that should be kept in mind when adding new components.

Here are some key design guidelines to be aware of:
- Design for generality, reuse, and extension. Follow a principle of least constraint.
- Most classes should be Java beans. That means they should have a default constructor and a getter and setter for each configurable field.
- Constructors should use setters for configuring when possible.
- Most fields should be protected to allow users to extend and override aspects when used.
- Avoid using mutation, but don’t go overboard to prevent it.
- Occasionally you may need to expose details or special methods for performance. That is fine in some cases, just make sure they are very well documented.
- Before making a multithreaded implementation of an algorithm, make a single-threaded version. In some use-cases you may already have parallelized at a higher level and the extra overhead is not needed.
- Provide convenience methods where it makes sense.
- Separate IO-oriented code from math/algorithm/logic code.

## Implementation guidelines

The Foundry tries to follow a few patterns with respect to implementations:

- Do not use `Math.random()`. Explicitly pass `Random` objects to allow users to make code deterministic.
- Prefer `LinkedHashMap` and `LinkedHashSet` over `HashMap` and `HashSet` unless you expect very large ones where you are very sure no one will loop through them or need to debug them. This also can remove a source of non-determinism.
- Avoid doing math on boxed values. Prefer primitives or the mutable boxed classes in the Foundry like `MutableDouble`.
- Try to avoid repeating yourself in the code; call methods once and cache the output. This can help both readability and performance.
- Especially avoid calling methods in loops if the value is known to not change.
- Don’t to overboard on synchronization unless writing multithreaded code.
- Avoid adding dependencies on other libraries.

## Testing guidelines

The Foundry is a production-grade Java library for machine learning and associated algorithms. Because of this, we need high unit test coverage of its functionality to be confident the code works before people use it in an application. As such, all additions and changes need to be very well tested. We target having over 90% coverage of statements.

-  Each class should have a corresponding JUnit unit test class. For example, `DenseVector` has a `DenseVectorTest` class with its unit tests. In that class there should be at least one test for each public method, plus tests for constructors. So if there is a method `cosine` then there should be a corresponding test named `testCosine`.
- When possible, an interface should have a corresponding test harness that can test properties of instances of that class. For example, `Vector` has `VectorTestHarness`.
- When fixing an issue, make a test case for it to prevent regressions.
- Test not just the main code path but also corner-cases.
- Test that various validation of method arguments is performed.

Before submitting a pull request, make sure all unit tests pass.

## Documentation guidelines

Because the Foundry is a library, we try to provide comprehensive documentation in the code using Javadoc. This includes documentation for all classes, methods, fields, and parameters. When writing the javadoc, please use the following practices:

- Provide full Javadoc for both public and private methods and fields and method arguments.
- Use full sentences with proper grammar, capitalization and punctuation. This includes ending them with a “.”
- If the documentation for an implementation of a method from an interface is clear, don’t duplicate the javadoc as it will automatically be inherited.
- Add `package-info.java` files for packages to contain package-level descriptions.
- Make sure to document parameters and 
- If parameters have certain constraints, make sure those are described in the `@param` documentation.
- Include an `@since` annotation with new code to indicate the next release version where it will first be available.
- Include documentation for each generic type as well. You may want to provide an example of what type might be used there as well.

Beyond javadoc, also think if there is relevant example code that should be provided for a new piece of functionality. If so, add those examples to the appropriate example projects.
