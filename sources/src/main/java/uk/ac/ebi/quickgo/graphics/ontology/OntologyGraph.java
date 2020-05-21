package uk.ac.ebi.quickgo.graphics.ontology;

import uk.ac.ebi.quickgo.ff.phaseout.ColourUtils;
import uk.ac.ebi.quickgo.model.ontology.generic.GenericTerm;
import uk.ac.ebi.quickgo.model.ontology.generic.GenericTermSet;
import uk.ac.ebi.quickgo.model.ontology.generic.RelationType;
import uk.ac.ebi.quickgo.model.ontology.generic.TermRelation;

import java.awt.*;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static uk.ac.ebi.quickgo.graphics.ontology.GraphPresentation.*;

public class OntologyGraph extends GenericGraph<TermNode, RelationEdge> {
    public GraphPresentation presentation;
    EnumSet<RelationType> relationTypes;
    int ancestorLimit = 0;
    int pixelLimit = 0;
    int overflow = 0;
    // map containing all terms that are part of (i.e., nodes in) the graph
    private Map<GenericTerm, TermNode> termMap = new HashMap<>();
    // map containing all relations that are part of (i.e., edges in) the graph
    private Map<TermRelation, RelationEdge> edgeMap = new HashMap<>();

    public OntologyGraph(EnumSet<RelationType> relationTypes, int ancestorLimit, int pixelLimit,
            GraphPresentation style) {
        this.relationTypes = relationTypes;
        this.ancestorLimit = ancestorLimit;
        this.pixelLimit = pixelLimit;
        this.presentation = style;
    }

    public OntologyGraph(int ancestorLimit, int pixelLimit, GraphPresentation style) {
        this(null, ancestorLimit, pixelLimit, style);
    }

    public TermNode add(GenericTerm term) {
        TermNode tgn = termMap.get(term);
        if (tgn != null) {
            return tgn;
        }

        List<GenericTerm> ancestors = term.getFilteredAncestors(relationTypes);
        for (GenericTerm ancestor : ancestors) {
            if (!termMap.containsKey(ancestor)) {
                TermNode node = new TermNode(ancestor, presentation);
                termMap.put(ancestor, node);
                nodes.add(node);
            }
        }

        if (presentation.showChildren) {
            for (TermRelation tr : term.children) {
                if (!termMap.containsKey(tr.child)) {
                    TermNode node = new TermNode(tr.child, presentation);
                    termMap.put(tr.child, node);
                    nodes.add(node);
                }
            }
        }

        if (ancestorLimit > 0 && nodes.size() > ancestorLimit) {
            overflow = nodes.size() - ancestorLimit;
            return null;
        }

        for (GenericTerm ancestor : ancestors) {
            for (TermRelation relation : ancestor.parents) {
                addRelation(relation);
            }
        }

        if (presentation.showChildren) {
            for (TermRelation tr : term.children) {
                addRelation(tr);
            }
        }

        return termMap.get(term);
    }

    public GraphImage layout(ImageArchive imageArchive) {
        GraphImage image = layout();
        ImageArchive.store(image);
        return image;
    }

    public GraphImage layout() {
        GraphImage image;
        if (overflow > 0) {
            image = new GraphImage(
                    "Chart too large - there are more than " + ancestorLimit + " ancestor terms (overflow = " +
                            overflow + ")", presentation);
        } else {
            GraphLayout<TermNode, RelationEdge> layout =
                    new GraphLayout<TermNode, RelationEdge>(this, GraphLayout.Orientation.TOP);
            layout.horizontalMargin = 2;
            layout.verticalMargin = 5;
            layout.edgeLengthHeightRatio = 5;
            layout.layout();
            long pixelCount = ((long) layout.getWidth()) * layout.getHeight();

            if (pixelLimit > 0 && pixelCount > pixelLimit) {
                image = new GraphImage(
                        "Chart too large: limit is " + pixelLimit + " pixels, actual size is " + pixelCount +
                                " pixels", presentation);
            } else {
                image = new GraphImage(layout.getWidth(), layout.getHeight(), getNodes(), getEdges(), presentation,
                        relationTypes);
            }
        }
        return image;
    }

    public static OntologyGraph makeGraph(GenericTermSet terms, EnumSet<RelationType> relationTypes, int ancestorLimit,
            int pixelLimit, GraphPresentation style) {
        OntologyGraph graph = new OntologyGraph(relationTypes, ancestorLimit, pixelLimit, style);

        for (GenericTerm term : terms.getTerms()) {
            TermNode node = graph.add(term);
            if (node != null && FILL) {
                String colour = terms.getTermColour(term);
                node.setFillColour(
                        new Color(colour.length() == 0 ? 0xffffcc : ColourUtils.intDecodeColour("#" + colour)));
            }
        }

        return graph;
    }

    public static OntologyGraph makeGraph(GenericTermSet terms, int ancestorLimit, int pixelLimit,
            GraphPresentation style) {
        return makeGraph(terms, null, ancestorLimit, pixelLimit, style);
    }

    void addRelation(TermRelation relation) {
        if (!edgeMap.containsKey(relation)) {
            TermNode pt = termMap.get(relation.parent);
            TermNode ct = termMap.get(relation.child);
            if (pt != null && ct != null) {
                RelationEdge graphEdge = new RelationEdge(pt, ct, relation.typeof, presentation);
                edgeMap.put(relation, graphEdge);
                edges.add(graphEdge);
            }
        }
    }
}
