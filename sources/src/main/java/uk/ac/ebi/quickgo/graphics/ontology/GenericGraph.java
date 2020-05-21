package uk.ac.ebi.quickgo.graphics.ontology;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GenericGraph<N extends INode, E extends IEdge<N>> implements IGraph<N, E> {
    List<N> nodes = new ArrayList<>();
    public List<E> edges = new ArrayList<>();

    public List<N> getNodes() {
        return nodes;
    }

    public List<E> getEdges() {
        return edges;
    }

    public Set<N> parents(N a) {
        Set<N> results = new HashSet<>();
        for (E e : edges) {
            if (e.getChild() == a) {
                results.add(e.getParent());
            }
        }
        return results;
    }

    public Set<N> children(N a) {
        Set<N> results = new HashSet<>();
        for (E e : edges) {
            if (e.getParent() == a) {
                results.add(e.getChild());
            }
        }
        return results;
    }

    private E findEdge(N parent, N child) {
        for (E e : edges) {
            if ((e.getParent() == parent) && (e.getChild() == child)) {
                return e;
            }
        }
        return null;
    }

    boolean connected(N a, N b) {
        return (findEdge(a, b) != null) || (findEdge(b, a) != null);
    }
}
