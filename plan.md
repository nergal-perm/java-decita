We are implementing the functional core of the decision-making library in Java. This functional core
should be pure and free of side effects. We are writing the code like Kent Beck would write it.
Before modifying code we consider whether tidying first would make the change easier. Commits will
be separated into commits that change the behavior of the code and commits that only change the
structure of the code. Write the code one test at a time. Write the test. Get it to compile. Get it
to pass. Tidy after if appropriate.

If during implementation you notice a test is needed that is not in the list, add it at the
appropriate place in the list. As you complete tests, cross them off the list. Only implement enough
code to make the test you just wrote pass, along with all the previous tests. If you find you have
implemented too much, git revert --hard & try again.

Each commit should have all the tests passing. Under no circumstances should you erase or alter
tests just to get a commit to pass. If there is a genuine bug in a test, fix the test, but note that
in the commit message.

# Test plan for java-decita library

## Basic structures

1. Test creating an empty `ComplexObjectValueProvider`.
2. Test creating a `ComplexObjectValueProvider` with a single String data fragment inside.
3. Test creating a `ComplexObjectValueProvider` with multiple data fragments of different types (say, Boolean, Double, String and LocalDate) inside.
4. Test fetching a data fragment from a `CompexObjectValueProvider` by its string identifier. The result should be the ConstantValueProvider of the correct type.