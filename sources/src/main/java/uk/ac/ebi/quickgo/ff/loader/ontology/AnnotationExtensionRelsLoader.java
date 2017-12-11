package uk.ac.ebi.quickgo.ff.loader.ontology;

import uk.ac.ebi.quickgo.ff.files.ontology.GOSourceFiles;
import uk.ac.ebi.quickgo.ff.loader.SourceInfoLoader;
import uk.ac.ebi.quickgo.model.ontology.go.AnnotationExtensionRelations;
import uk.ac.ebi.quickgo.model.ontology.go.GeneOntology;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static uk.ac.ebi.quickgo.ff.files.ontology.GOSourceFiles.*;

/**
 * Loads annotation extension relations for a GO ontology.
 *
 * Created by Edd on 11/12/2015.
 */
public class AnnotationExtensionRelsLoader extends SourceInfoLoader<GOSourceFiles, AnnotationExtensionRelations> {
    private final static Logger LOGGER = LoggerFactory.getLogger(AnnotationExtensionRelsLoader.class);

    private final GeneOntology geneOntology;

    public AnnotationExtensionRelsLoader(GOSourceFiles sourceFiles, GeneOntology geneOntology) {
        super(sourceFiles);
        this.geneOntology = geneOntology;
    }

    @Override protected AnnotationExtensionRelations newInstance() {
        return new AnnotationExtensionRelations(geneOntology);
    }

    @Override public AnnotationExtensionRelations load() throws Exception {
        AnnotationExtensionRelations aer = getInstance();

        for (String[] row : sourceFiles.annExtRelations.reader(EAnnExtRelation.RELATION,
                                                               EAnnExtRelation.USAGE,
                                                               EAnnExtRelation.DOMAIN)) {
            aer.annExtRelations.put(row[0],
                                    new AnnotationExtensionRelations.AnnotationExtensionRelation(row[0],
                                                                                                 row[1],
                                                                                                 row[2]));
        }

        for (String[] row : sourceFiles.aerRelations.reader(EAnnExtRelRelation.CHILD,
                                                            EAnnExtRelRelation.PARENT,
                                                            EAnnExtRelRelation.RELATION_TYPE)) {
            AnnotationExtensionRelations.AnnotationExtensionRelation child = aer.annExtRelations.get(row[0]);
            AnnotationExtensionRelations.AnnotationExtensionRelation parent = aer.annExtRelations.get(row[1]);
            if (child != null && parent != null) {
                aer.relations.add(new AnnotationExtensionRelations.Relation(child, parent, row[2]));
                child.addParent(parent);
            }
        }

        for (String[] row : sourceFiles.aerSecondaries.reader(EAnnExtRelSecondary.RELATION,
                                                              EAnnExtRelSecondary.SECONDARY_ID)) {
            AnnotationExtensionRelations.AnnotationExtensionRelation rel = aer.annExtRelations.get(row[0]);
            if (rel != null) {
                rel.addSecondary(row[1]);
            }
        }

        for (String[] row : sourceFiles.aerSubsets.reader(EAnnExtRelSubset.SUBSET, EAnnExtRelSubset.RELATION)) {
            AnnotationExtensionRelations.AnnotationExtensionRelation rel = aer.annExtRelations.get(row[1]);
            if (rel != null) {
                if ("_valid_relations_".equals(row[0])) {
                    rel.setValidInExtension(true);
                } else if ("_displayed_relations_".equals(row[0])) {
                    rel.setDisplayForCurators(true);
                } else {
                    rel.addSubset(row[0]);
                }
            }
        }

        for (String[] row : sourceFiles.aerDomains.reader(EAnnExtRelDomain.RELATION,
                                                          EAnnExtRelDomain.ENTITY,
                                                          EAnnExtRelDomain.ENTITY_TYPE)) {
            AnnotationExtensionRelations.AnnotationExtensionRelation relation = aer.annExtRelations.get(row[0]);
            if (relation != null && row[1] != null) {
                relation.addDomain(aer.getEntity(row[1], row[2]));
            }
        }

        for (String[] row : sourceFiles.aerEntitySyntax.reader(EAnnExtRelEntitySyntax.ENTITY,
                                                               EAnnExtRelEntitySyntax.ENTITY_TYPE,
                                                               EAnnExtRelEntitySyntax.NAMESPACE,
                                                               EAnnExtRelEntitySyntax.ID_SYNTAX)) {
            AnnotationExtensionRelations.Entity entity = aer.getEntity(row[0], row[1]);
            entity.addMatcher(aer.entityMatchers.getMatcher(row[2], row[3]));
        }

        for (String[] row : sourceFiles.aerRanges.reader(EAnnExtRelRange.RELATION,
                                                         EAnnExtRelRange.ENTITY,
                                                         EAnnExtRelRange.ENTITY_TYPE)) {
            AnnotationExtensionRelations.AnnotationExtensionRelation relation = aer.annExtRelations.get(row[0]);
            if (relation != null && row[1] != null) {
                relation.addRange(aer.getEntity(row[1], row[2]));
            }
        }

        for (String[] row : sourceFiles.aerRangeDefaults.reader(EAnnExtRelRangeDefault.NAMESPACE,
                                                                EAnnExtRelRangeDefault.ID_SYNTAX)) {
            aer.rangeDefaults.add(aer.entityMatchers.getMatcher(row[0], row[1]));
        }

        return aer;

    }
}
