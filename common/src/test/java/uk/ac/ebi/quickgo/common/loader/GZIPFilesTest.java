package uk.ac.ebi.quickgo.common.loader;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static java.util.stream.Collectors.toList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;

/**
 * @author Tony Wardell
 * Date: 17/05/2016
 * Time: 15:35
 * Created with IntelliJ IDEA.
 */
public class GZIPFilesTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void successfullyReadFile(){

        File tmpGZIPFile = GZIPFileMocker.createTestFile();
        List<String> lines = GZIPFiles.lines(tmpGZIPFile.toPath())
        .collect(toList());

        assertThat(lines, hasSize(1));
        assertThat(lines, contains(GZIPFileMocker.TEXT));

        tmpGZIPFile.delete();
    }

    @Test
    public void noFileThrowsException(){
        thrown.expect(UncheckedIOException.class);
        File tmpGZIPFile = new File("Timbucktoo");  //doesn't exist.
        GZIPFiles.lines(tmpGZIPFile.toPath())
                .collect(toList());

    }
}
