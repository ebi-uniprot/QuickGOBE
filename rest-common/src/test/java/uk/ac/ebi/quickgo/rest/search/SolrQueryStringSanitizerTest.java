package uk.ac.ebi.quickgo.rest.search;

import java.util.Arrays;
import java.util.Collection;
import org.apache.solr.client.solrj.util.ClientUtils;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

/**
 * Check all characters that need escaping with Solr queries, are indeed escaped.
 *
 * Created 01/03/16
 * @author Edd
 */
class SolrQueryStringSanitizerTest {

    private String escapeString;
    private SolrQueryStringSanitizer solrQueryStringSanitizer;

    public void initSolrQueryStringSanitizerTest(String escapeString) {
        this.solrQueryStringSanitizer = new SolrQueryStringSanitizer();
        this.escapeString = escapeString;
    }

    public static Collection<String> escapeChars() {
        return Arrays.asList(
                "\\",
                "+",
                "-",
                "!",
                "(",
                ")",
                ":",
                "^",
                "[",
                "]",
                "\"",
                "{",
                "}",
                "~",
                "*",
                "?",
                "|",
                "&",
                ";",
                " ",
                "\t");
    }

    @MethodSource("escapeChars")
    @ParameterizedTest(name = "{index}: checking \"{0}\"")
    void checkString(String escapeString) {
        initSolrQueryStringSanitizerTest(escapeString);
        String query = "values "+escapeString+" values";
        assertThat(solrQueryStringSanitizer.sanitize(query), is(equalTo(ClientUtils.escapeQueryChars(query))));
    }

}