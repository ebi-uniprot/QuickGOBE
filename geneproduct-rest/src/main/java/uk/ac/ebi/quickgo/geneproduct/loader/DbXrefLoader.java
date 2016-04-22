package uk.ac.ebi.quickgo.geneproduct.loader;

import uk.ac.ebi.quickgo.common.loader.GZIPFiles;
import uk.ac.ebi.quickgo.geneproduct.model.GeneProductDbXrefIDFormat;

import java.io.File;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

/**
 *
 * Read the file in the specified directory, and pass back the contents as a list.
 *
 *
 * @author Tony Wardell
 *         Date: 18/04/2016
 *         Time: 14:06
 *         Created with IntelliJ IDEA.
 */
public class DbXrefLoader {

    private Logger logger = LoggerFactory.getLogger(DbXrefLoader.class);

    private static final int COL_DATABASE = 0;
    private static final int COL_ENTITY_TYPE = 1;
    private static final int COL_ENTITY_TYPE_NAME = 2;
    private static final int COL_LOCAL_ID_SYNTAX = 3;
    private static final int COL_URL_SYNTAX = 4;
    private static final String COL_DELIMITER = "\t";
    private String path;

    public DbXrefLoader(String path) {
        this.path = path;
    }

    /**
     * Read the specified file and save it to a list of GeneProductDbXrefIDFormat instances.
     *
     * If the file cannot be loaded supply the client with an empty list, as we will continue without
     * validation, rather than end the process.
     * @return
     */
    public List<GeneProductDbXrefIDFormat> load() {

        try {

            Path path = FileSystems.getDefault().getPath(this.path);

            File here = new File("./");
            System.out.println("Path : " + here.getAbsolutePath());

            return GZIPFiles.lines(path)
                    .skip(1)    //header
                    .map(line -> line.split(COL_DELIMITER))
                    .map(fields -> new GeneProductDbXrefIDFormat(fields[COL_DATABASE], fields[COL_ENTITY_TYPE],
                            fields[COL_ENTITY_TYPE_NAME], fields[COL_LOCAL_ID_SYNTAX], fields[COL_URL_SYNTAX]))
                    .collect(toList());

        } catch (Exception e) {
            logger.error("Failed to load " + this.path, e);

            return new ArrayList<>();
        }

    }

}
