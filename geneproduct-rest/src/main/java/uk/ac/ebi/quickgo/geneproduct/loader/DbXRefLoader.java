package uk.ac.ebi.quickgo.geneproduct.loader;

import uk.ac.ebi.quickgo.common.loader.GZIPFiles;
import uk.ac.ebi.quickgo.geneproduct.model.GeneProductDbXRefIDFormat;

import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.util.stream.Collectors.toList;

/**
 *
 * Read the file in the specified directory, and pass back the contents as a list.
 *
 *
 * @author Tony Wardell
 *         Date: 18/04/2016
 *         Time: 14:06
 *         Created with IntelliJ IDEA.
 *
 */
public class DbXRefLoader {

    private Logger logger = LoggerFactory.getLogger(DbXRefLoader.class);

    private static final int COL_DATABASE = 0;
    private static final int COL_ENTITY_TYPE = 1;
    private static final int COL_ENTITY_TYPE_NAME = 2;
    private static final int COL_LOCAL_ID_SYNTAX = 3;
    private static final int COL_URL_SYNTAX = 4;
    private static final String COL_DELIMITER = "\t";
    private String path;

    public DbXRefLoader(String path) {
        this.path = path;
    }

    /**
     * Load the file which contains information about database cross references and save it to a list of
     * GeneProductDbXRefIDFormat instances.
     *
     * We load this file to use the regexes it contains to validate gene product ids (each Db will have it's own
     * regex for the id it uses).
     *
     * If the file cannot be loaded supply the client with an empty list, as we will continue without
     * validation, rather than end the process.
     * @return
     */
    public List<GeneProductDbXRefIDFormat> load() {

        try {

            Path path = FileSystems.getDefault().getPath(this.path);

            return GZIPFiles.lines(path)
                    .skip(1)    //header
                    .map(line -> line.split(COL_DELIMITER))
                    .map(fields -> new GeneProductDbXRefIDFormat(fields[COL_DATABASE], fields[COL_ENTITY_TYPE],
                            fields[COL_ENTITY_TYPE_NAME], fields[COL_LOCAL_ID_SYNTAX], fields[COL_URL_SYNTAX]))
                    .collect(toList());

        } catch (Exception e) {
            logger.error("DbXRefLoader failed to load file " + this.path + ", the source of validation regexes to " +
                    "validate gene product IDs. Gene Product ID validation will not take place", e);

            return new ArrayList<>();
        }

    }

}
