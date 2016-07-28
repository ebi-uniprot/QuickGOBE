package uk.ac.ebi.quickgo.ontology.service.converter;

import uk.ac.ebi.quickgo.common.converter.FieldConverter;
import uk.ac.ebi.quickgo.common.converter.FlatField;
import uk.ac.ebi.quickgo.ontology.model.GOTerm;

import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static uk.ac.ebi.quickgo.common.converter.FlatFieldBuilder.newFlatField;

/**
 * Defines the conversion of a {@link String} representing GO discussion information, to a
 * corresponding {@link uk.ac.ebi.quickgo.ontology.model.GOTerm.GoDiscussion} instance.
 * <p>
 * A {@link String} representation is of the form:
 * <ul>
 *     <li>title|url</li>
 * </ul>
 * <p>
 * @author Ricardo Antunes
 */
public class GoDiscussionConverter implements FieldConverter<GOTerm.GoDiscussion> {
    private static final Logger LOGGER = LoggerFactory.getLogger(GoDiscussionConverter.class);
    private static final int FIELD_COUNT = 2;

    @Override public Optional<GOTerm.GoDiscussion> apply(String fieldStr) {
        List<FlatField> fields = newFlatField().parse(fieldStr).getFields();

        Optional<GOTerm.GoDiscussion> goDiscussionOpt;

        if (fields.size() == FIELD_COUNT) {
            GOTerm.GoDiscussion discussion = new GOTerm.GoDiscussion();
            discussion.title = cleanFieldValue(fields.get(0).buildString());
            discussion.url = cleanFieldValue(fields.get(1).buildString());

            goDiscussionOpt = Optional.of(discussion);
        } else {
            LOGGER.warn("Could not parse flattened goDiscussion: {}", fieldStr);
            goDiscussionOpt = Optional.empty();
        }

        return goDiscussionOpt;
    }
}