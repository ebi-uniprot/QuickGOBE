package uk.ac.ebi.quickgo.index.annotation.coterms;

import com.google.common.base.Preconditions;
import java.util.Objects;
import org.springframework.core.io.FileSystemResource;

/**
 * @author Tony Wardell
 * Date: 06/02/2017
 * Time: 16:31
 * Created with IntelliJ IDEA.
 */
public class OutputPath {
    final FileSystemResource resource;
    OutputPath(String path) {
        Preconditions.checkArgument(Objects.nonNull(path), "The output path for the 'manual' coterms" +
                " file cannot be null");
        resource = new FileSystemResource(path);
    }
}

