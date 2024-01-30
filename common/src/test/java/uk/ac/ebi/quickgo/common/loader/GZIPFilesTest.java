package uk.ac.ebi.quickgo.common.loader;

import java.io.File;
import java.io.UncheckedIOException;
import java.util.List;
import org.junit.jupiter.api.Test;

import static java.util.stream.Collectors.toList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Tony Wardell
 * Date: 17/05/2016
 * Time: 15:35
 * Created with IntelliJ IDEA.
 */
class GZIPFilesTest {

    @Test
    void successfullyReadFile(){

        File tmpGZIPFile = GZIPFileMocker.createTestFile();
        List<String> lines = GZIPFiles.lines(tmpGZIPFile.toPath())
        .collect(toList());

        assertThat(lines, hasSize(1));
        assertThat(lines, contains(GZIPFileMocker.TEXT));

        tmpGZIPFile.delete();
    }

    @Test
    void noFileThrowsException(){
        File tmpGZIPFile = new File("Timbucktoo");  //doesn't exist.
        assertThrows(UncheckedIOException.class, () -> GZIPFiles.lines(tmpGZIPFile.toPath()).collect(toList()));
    }
}
