package uk.ac.ebi.quickgo.rest.search.query;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;

/**
 * Tests the behaviour of the {@link JoinQuery} class.
 */
@ExtendWith(MockitoExtension.class)
class JoinQueryTest {

    @Mock
    private QueryVisitor visitor;

    private JoinQuery query;

    private String joinFromAttribute = "id";
    private String joinFromTable = "annotation";
    private String joinToAttribute = "id";
    private String joinToTable = "ontology";

    private QuickGOQuery fromFilter = QuickGOQuery.createAllQuery();

    @Test
    void throwsExceptionWhenJoinFromTableIsNull()  {
        joinFromTable = null;

        assertNullOrEmpty("Join From Table cannot be null or empty",
          () -> new JoinQuery(joinFromTable, joinFromAttribute, joinToTable, joinToAttribute));
    }

    @Test
    void throwsExceptionWhenJoinFromTableIsEmpty()  {
        joinFromTable = "";

        assertNullOrEmpty("Join From Table cannot be null or empty",
          () -> new JoinQuery(joinFromTable, joinFromAttribute, joinToTable, joinToAttribute));
    }

    @Test
    void throwsExceptionWhenJoinFromAttributeIsNull()  {
        joinFromAttribute = null;

        assertNullOrEmpty("Join From Attribute cannot be null or empty",
          () -> new JoinQuery(joinFromTable, joinFromAttribute, joinToTable, joinToAttribute));
    }

    @Test
    void throwsExceptionWhenJoinFromAttributeIsEmpty()  {
        joinFromAttribute = "";

        assertNullOrEmpty("Join From Attribute cannot be null or empty",
          () -> new JoinQuery(joinFromTable, joinFromAttribute, joinToTable, joinToAttribute));
    }

    @Test
    void throwsExceptionWhenJoinToTableIsNull()  {
        joinToTable = null;

        assertNullOrEmpty("Join To Table cannot be null or empty",
          () -> new JoinQuery(joinFromTable, joinFromAttribute, joinToTable, joinToAttribute));
    }

    @Test
    void throwsExceptionWhenJoinToTableIsEmpty()  {
        joinToTable = "";

        assertNullOrEmpty("Join To Table cannot be null or empty",
          () -> new JoinQuery(joinFromTable, joinFromAttribute, joinToTable, joinToAttribute));
    }

    @Test
    void throwsExceptionWhenJoinToAttributeIsNull()  {
        joinToAttribute = null;

        assertNullOrEmpty("Join To Attribute cannot be null or empty",
          () -> new JoinQuery(joinFromTable, joinFromAttribute, joinToTable, joinToAttribute));
    }

    @Test
    void throwsExceptionWhenJoinToAttributeIsEmpty()  {
        joinToAttribute = "";

        assertNullOrEmpty("Join To Attribute cannot be null or empty",
          () -> new JoinQuery(joinFromTable, joinFromAttribute, joinToTable, joinToAttribute));
    }

    @Test
    void createsJoinQueryWithJoinParametersInitializedCorrectly() {
        query = new JoinQuery(joinFromTable, joinFromAttribute, joinToTable, joinToAttribute);

        assertThat(query.getJoinFromTable(), is(joinFromTable));
        assertThat(query.getJoinFromAttribute(), is(joinFromAttribute));
        assertThat(query.getJoinToTable(), is(joinToTable));
        assertThat(query.getJoinToAttribute(), is(joinToAttribute));
    }

    @Test
    void throwsExceptionWhenQueryIsNull()  {
        fromFilter = null;

        assertNullOrEmpty("Filter cannot be null",
          () -> new JoinQuery(joinFromTable, joinFromAttribute, joinToTable, joinToAttribute, fromFilter));
    }

    @Test
    void createsJoinQueryWithJoinParametersAndFilterQueryInitializedCorrectly() {
        query = new JoinQuery(joinFromTable, joinFromAttribute, joinToTable, joinToAttribute, fromFilter);

        assertThat(query.getJoinFromTable(), is(joinFromTable));
        assertThat(query.getJoinFromAttribute(), is(joinFromAttribute));
        assertThat(query.getJoinToTable(), is(joinToTable));
        assertThat(query.getJoinToAttribute(), is(joinToAttribute));
        assertThat(query.getFromFilter(), is(fromFilter));
    }

    @Test
    void visitorIsCalledCorrectly()  {
        FieldQuery query = new FieldQuery("field1", "value1");
        query.accept(visitor);
        verify(visitor).visit(query);
    }

    private void assertNullOrEmpty(String errorMsg, Executable executable) {
        Throwable exception = assertThrows(IllegalArgumentException.class, executable);
        assertTrue(exception.getMessage().contains(errorMsg));
    }
}