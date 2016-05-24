package uk.ac.ebi.quickgo.common.loader;

import java.io.*;
import java.util.zip.GZIPOutputStream;

/**
 * To test the GZIPFiles class, create a gzip'd file with known test in a temporary location.
 * The client is expected to clean up the file after it's finished with it.
 *
 * @author Tony Wardell
 * Date: 17/05/2016
 * Time: 15:38
 * Created with IntelliJ IDEA.
 */
public class GZIPFileMocker{

    public static final String TEXT = "Mary had a little lamb, it's fleece was white as snow";

    public static final File createTestFile(){

        BufferedWriter bufferedWriter = null;
        File temp = null;
        try {

            temp = File.createTempFile("temp-file-name", "txt.gz");


            //Construct the BufferedWriter object
            bufferedWriter = new BufferedWriter(
                    new OutputStreamWriter(
                            new GZIPOutputStream(new FileOutputStream(temp))
                    ));

            // from the input file to the GZIP output file
            bufferedWriter.write(TEXT);
            bufferedWriter.newLine();


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            //Close the BufferedWrter
            if (bufferedWriter != null) {
                try {
                    bufferedWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return temp;
    }
}
