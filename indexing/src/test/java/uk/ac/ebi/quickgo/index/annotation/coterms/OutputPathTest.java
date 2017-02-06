package uk.ac.ebi.quickgo.index.annotation.coterms;

import org.junit.Test;

/**
 * @author Tony Wardell
 * Date: 11/01/2017
 * Time: 09:16
 * Created with IntelliJ IDEA.
 */
public class OutputPathTest {

    @Test
    public void okPath(){
        new OutputPath("foo");
    }

    @Test(expected = IllegalArgumentException.class)
    public void outputPathCannotBeNull(){
        new OutputPath(null);
    }
}
