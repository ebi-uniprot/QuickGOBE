package uk.ac.ebi.quickgo.ontology.traversal;

import java.util.Optional;

/**
 * An alternative to recursion.
 * <p>
 * https://stackoverflow.com/questions/32685660/achieving-stackless-recursion-in-java-8
 * <p>
 * Created by Tony Wardell on 17-Jun-17.
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
