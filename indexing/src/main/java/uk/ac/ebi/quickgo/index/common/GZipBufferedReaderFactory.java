package uk.ac.ebi.quickgo.index.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

import org.springframework.batch.item.file.BufferedReaderFactory;
import org.springframework.core.io.Resource;

public class GZipBufferedReaderFactory implements BufferedReaderFactory {
    /**
     * Default value for gzip suffixes.
     */
    private static final List<String> gzipSuffixes = new ArrayList<String>() {

        {
            add(".gz");
            add(".gzip");
        }
    };

    /**
     * Creates Bufferedreader for gzip Resource, handles normal resources too.
     *
     * @param resource
     * @param encoding
     * @return
     * @throws UnsupportedEncodingException
     * @throws IOException
     */
    @Override
    public BufferedReader create(Resource resource, String encoding) throws UnsupportedEncodingException, IOException {
        for (String suffix : gzipSuffixes) {
            // test for filename and description, description is used when handling itemStreamResources
            if (resource.getFilename().endsWith(suffix) || resource.getDescription().endsWith(suffix)) {
                return new BufferedReader(new InputStreamReader(new GZIPInputStream(resource.getInputStream()), encoding));
            }
        }
        return new BufferedReader(new InputStreamReader(resource.getInputStream(), encoding));
    }
}
