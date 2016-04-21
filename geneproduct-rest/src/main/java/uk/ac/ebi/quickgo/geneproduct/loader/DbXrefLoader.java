package uk.ac.ebi.quickgo.geneproduct.loader;

import uk.ac.ebi.quickgo.common.loader.GZIPFiles;
import uk.ac.ebi.quickgo.geneproduct.model.GeneProductXrefEntity;

import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toMap;

/**
 *
 * This class is be created at spring context creation time
 * Read the properties from load.properties.
 * Pass these values to common package
 *    property key is the name of the entity each tuple of the file will be loaded into
 *    property value is the name of the file to be read
 *
 *
 * @author Tony Wardell
 *         Date: 18/04/2016
 *         Time: 14:06
 *         Created with IntelliJ IDEA.
 */
public class DbXrefLoader {

    private static final int COL_DATABASE = 0;
    private static final int COL_ENTITY_TYPE = 1;
    private static final int COL_ENTITY_TYPE_NAME = 2;
    private static final int COL_LOCAL_ID_SYNTAX = 3;
    private static final int COL_URL_SYNTAX = 4;

    private static final String FILE_NAME = "DB_XREFS_ENTITIES.dat.gz";
    public static final String COL_DELIMITER = "\t";
    private String directory;

    public DbXrefLoader(String dir) {
        this.directory = dir;
    }

    public Map<GeneProductXrefEntity.Key, List<GeneProductXrefEntity>> load() {

        try {

            Path path = FileSystems.getDefault().getPath(this.directory, FILE_NAME);

            //			return GZIPFiles.lines(path)
            //					.skip(0)
            //					.map(line -> line.split(COL_DELIMITER))
            //					.map(arr -> new GeneProductXrefEntity(arr[COL_DATABASE], arr[COL_ENTITY_TYPE],
            //							arr[COL_ENTITY_TYPE_NAME], arr[COL_LOCAL_ID_SYNTAX], arr[COL_URL_SYNTAX]))
            //					.collect(toMap(GeneProductXrefEntity::getDatabase, entity -> entity));

            //			return GZIPFiles.lines(path)
            //					.skip(0)
            //					.map(line -> line.split(COL_DELIMITER))
            //					.map(arr -> new GeneProductXrefEntity(arr[COL_DATABASE], arr[COL_ENTITY_TYPE],
            // arr[COL_ENTITY_TYPE_NAME], arr[COL_LOCAL_ID_SYNTAX], arr[COL_URL_SYNTAX]))
            //					.collect(toMap(GeneProductXrefEntity::getDbKey, Functions.identity()));

            //2 operations

            //			List<GeneProductXrefEntity> list = GZIPFiles.lines(path)
            //					.skip(0)
            //					.map(line -> line.split(COL_DELIMITER))
            //					.map(arr -> new GeneProductXrefEntity(arr[COL_DATABASE], arr[COL_ENTITY_TYPE],
			// arr[COL_ENTITY_TYPE_NAME], arr[COL_LOCAL_ID_SYNTAX], arr[COL_URL_SYNTAX]))
            //					.collect(toList());
            //			return list.stream().collect(groupingBy(GeneProductXrefEntity::getDbKey));

            // 1 operation
            return GZIPFiles.lines(path)
                    .skip(0)
                    .map(line -> line.split(COL_DELIMITER))
                    .map(arr -> new GeneProductXrefEntity(arr[COL_DATABASE], arr[COL_ENTITY_TYPE],
                            arr[COL_ENTITY_TYPE_NAME], arr[COL_LOCAL_ID_SYNTAX], arr[COL_URL_SYNTAX]))
                    .collect(groupingBy(GeneProductXrefEntity::getDbKey));

            //2 operations -- different

            //			List<GeneProductXrefEntity> list = Files.lines(path)
            //					.skip(0)
            //					.map(line -> line.split(COL_DELIMITER))
            //					.map(arr -> new GeneProductXrefEntity(arr[COL_DATABASE], arr[COL_ENTITY_TYPE], arr[COL_ENTITY_TYPE_NAME], arr[COL_LOCAL_ID_SYNTAX], arr[COL_URL_SYNTAX]))
            //					.collect(toList());
            //			return list.stream().collect(toMap(GeneProductXrefEntity::getDbKey, Functions.identity()));

        } catch (Exception e) {
            throw new IllegalStateException("Tried to load DB Xref Entities file but failed", e);
        }

    }

}
