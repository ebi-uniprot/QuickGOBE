package uk.ac.ebi.quickgo.rest.search.query;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.verify;

/**
 * Tests the behaviour of the {@link JoinQuery} class.
 */
@RunWith(MockitoJUnitRunner.class)
public class JoinQueryTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Mock
    private QueryVisitor visitor;

    private JoinQuery query;

    private String joinFromAttribute = "id";
    private String joinFromTable = "annotation";
    private String joinToAttribute = "id";
    private String joinToTable = "ontology";

    private QuickGOQuery fromFilter = QuickGOQuery.createAllQuery();

    @Test
    public void throwsExceptionWhenJoinFromTableIsNull() throws Exception {
        joinFromTable = null;

        assertNullOrEmpty("Join From Table cannot be null or empty");

        query = new JoinQuery(joinFromTable, joinFromAttribute, joinToTable, joinToAttribute);
    }

    @Test
    public void throwsExceptionWhenJoinFromTableIsEmpty() throws Exception {
        joinFromTable = "";

        assertNullOrEmpty("Join From Table cannot be null or empty");

        query = new JoinQuery(joinFromTable, joinFromAttribute, joinToTable, joinToAttribute);
    }

    @Test
    public void throwsExceptionWhenJoinFromAttributeIsNull() throws Exception {
        joinFromAttribute = null;

        assertNullOrEmpty("Join From Attribute cannot be null or empty");

        query = new JoinQuery(joinFromTable, joinFromAttribute, joinToTable, joinToAttribute);
    }

    @Test
    public void throwsExceptionWhenJoinFromAttributeIsEmpty() throws Exception {
        joinFromAttribute = "";

        assertNullOrEmpty("Join From Attribute cannot be null or empty");

        query = new JoinQuery(joinFromTable, joinFromAttribute, joinToTable, joinToAttribute);
    }

    @Test
    public void throwsExceptionWhenJoinToTableIsNull() throws Exception {
        joinToTable = null;

        assertNullOrEmpty("Join To Table cannot be null or empty");

        query = new JoinQuery(joinFromTable, joinFromAttribute, joinToTable, joinToAttribute);
    }

    @Test
    public void throwsExceptionWhenJoinToTableIsEmpty() throws Exception {
        joinToTable = "";

        assertNullOrEmpty("Join To Table cannot be null or empty");

        query = new JoinQuery(joinFromTable, joinFromAttribute, joinToTable, joinToAttribute);
    }

    @Test
    public void throwsExceptionWhenJoinToAttributeIsNull() throws Exception {
        joinToAttribute = null;

        assertNullOrEmpty("Join To Attribute cannot be null or empty");

        query = new JoinQuery(joinFromTable, joinFromAttribute, joinToTable, joinToAttribute);
    }

    @Test
    public void throwsExceptionWhenJoinToAttributeIsEmpty() throws Exception {
        joinToAttribute = "";

        assertNullOrEmpty("Join To Attribute cannot be null or empty");

        query = new JoinQuery(joinFromTable, joinFromAttribute, joinToTable, joinToAttribute);
    }

    @Test
    public void createsJoinQueryWithJoinParametersInitializedCorrectly() {
        query = new JoinQuery(joinFromTable, joinFromAttribute, joinToTable, joinToAttribute);

        assertThat(query.getJoinFromTable(), is(joinFromTable));
        assertThat(query.getJoinFromAttribute(), is(joinFromAttribute));
        assertThat(query.getJoinToTable(), is(joinToTable));
        assertThat(query.getJoinToAttribute(), is(joinToAttribute));
    }

    @Test
    public void throwsExceptionWhenQueryIsNull() throws Exception {
        fromFilter = null;

        assertNullOrEmpty("Filter cannot be null");

        query = new JoinQuery(joinFromTable, joinFromAttribute, joinToTable, joinToAttribute, fromFilter);
    }

    @Test
    public void createsJoinQueryWithJoinParametersAndFilterQueryInitializedCorrectly() {
        query = new JoinQuery(joinFromTable, joinFromAttribute, joinToTable, joinToAttribute, fromFilter);

        assertThat(query.getJoinFromTable(), is(joinFromTable));
        assertThat(query.getJoinFromAttribute(), is(joinFromAttribute));
        assertThat(query.getJoinToTable(), is(joinToTable));
        assertThat(query.getJoinToAttribute(), is(joinToAttribute));
        assertThat(query.getFromFilter(), is(fromFilter));
    }

    @Test
    public void visitorIsCalledCorrectly() throws Exception {
        FieldQuery query = new FieldQuery("field1", "value1");
        query.accept(visitor);
        verify(visitor).visit(query);
    }

    private void assertNullOrEmpty(String errorMsg) {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(errorMsg);
    }
}