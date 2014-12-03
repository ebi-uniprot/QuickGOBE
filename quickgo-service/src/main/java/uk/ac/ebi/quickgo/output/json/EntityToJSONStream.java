package uk.ac.ebi.quickgo.output.json;

import java.io.IOException;
import java.io.OutputStream;

import org.springframework.stereotype.Service;

import uk.ac.ebi.quickgo.render.JSONSerialise;

import com.google.gson.Gson;

/**
 * Convert Java objects into JSON streams
 * 
 * @author cbonill
 * 
 * @param <T>
 *            Object
 */
@Service("entityToJSONStream")
public class EntityToJSONStream<T extends JSONSerialise> {

	/**
	 * Converts an object into a JSON stream and writes the value in the
	 * specified output stream
	 * 
	 * @param object
	 *            Object to convert
	 * @param outputStream
	 *            Output stream with the JSON representation
	 * @throws IOException 
	 */
	public void convertToJSONStream(T object, OutputStream outputStream) throws IOException {		
		outputStream.write(new Gson().toJson(object.serialise()).getBytes());
	}
}