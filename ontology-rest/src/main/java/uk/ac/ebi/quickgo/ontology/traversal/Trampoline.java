package uk.ac.ebi.quickgo.ontology.traversal;

import java.util.Optional;

/**
 * An alternative to recursion.
 * A trampoline is a pattern for turning stack-based recursion into an equivalent loop. Since loops don't add stack
 * frames, this can be thought of as a form of stackless recursion.
 *
 * You can think of a trampoline as a process that takes a starting value; iterates on that value; and then exits with the final value.
 *
 * Consider this stack-based recursion:
 *
 * public static int factorial(final int n) {
 * if (n <= 1) {
 *  return 1;
 * }
 *  return n * factorial(n - 1);
 *  }
 *  For every recursive call this makes, a new frame is pushed. This is because the prior frame cannot evaluate without the result of the new frame. This will become a problem when the stack gets too deep and we run out of memory.
 *
 *  Luckily, we can express this function as a loop:
 *
 *  public static int factorial2(int n) {
 *  int i = 1;
 *  while (n > 1) {
 *     i = i * n;
 *     n--;
 *  }
 *  return i;
 *  }
 *  What's going on here? We've taken the recursive step and made it the iteration inside of a loop. We loop until we have completed all recursive steps, storing the result or each iteration in a variable.
 *
 *  This is more efficient since fewer frames will be created. Instead of storing a frame for each recursive call (n frames), we store the current value and the number of iterations remaining (2 values).
 *
 *  The generalization of this pattern is a trampoline i.e the code below.
 *
 *  The Trampoline requires two members:
 *
 *  the value of the current step;
 *  the next function to compute, or nothing if we have reached the last step
 *  Any computation that can be described in this way can be "trampolined".
 *
 *  What does this look like for factorial?
 *
 *  public final class Factorial {
 *    public static Trampoline<Integer> createTrampoline(final int n, final int sum){
 *      if (n == 1) {
 *         return new Trampoline<Integer>() {
 *            public Integer getValue() { return sum; }
 *         };
 *      }
 *
 *     return new Trampoline<Integer>() {
 *       public Optional<Trampoline<Integer>> nextTrampoline() {
 *          return Optional.of(createTrampoline(n - 1, sum * n));
 *       }
 *     };
 *   }
 * }
 *  And to call:
 *
 *  Factorial.createTrampoline(4, 1).compute()
 *
 *  See https://stackoverflow.com/questions/32685660/achieving-stackless-recursion-in-java-8
 *
 *  * Created by Tony Wardell on 17-Jun-17.
 */
public class Trampoline<T> {
    public T getValue() {
        throw new RuntimeException("Not implemented");
    }

    public Optional<Trampoline<T>> nextTrampoline() {
        return Optional.empty();
    }

    final T compute() {
        Trampoline<T> trampoline = this;

        while (trampoline.nextTrampoline().isPresent()) {
            trampoline = trampoline.nextTrampoline().get();
        }

        return trampoline.getValue();
    }
}
