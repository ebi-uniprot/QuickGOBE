package uk.ac.ebi.quickgo.common.store;

import com.github.dockerjava.api.command.InspectContainerResponse;
import org.json.JSONException;
import org.testcontainers.containers.SolrClientUtils;
import org.testcontainers.shaded.okhttp3.*;
import org.testcontainers.solr.SolrContainer;
import org.testcontainers.utility.MountableFile;
import uk.ac.ebi.quickgo.common.SolrCollectionName;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Stream;

public class SolarTestContainer extends SolrContainer {

    public SolarTestContainer() {
        super("solr:10.0.0");
        super.withZookeeper(false)
          .withReuse(true)
          .withEnv("SOLR_MODULES", "extraction,langid")
          .withEnv("SOLR_OPTS", "-Dsolr.jetty.request.header.size=65535")
          .withCopyFileToContainer(MountableFile.forHostPath("../solr-cores/src/main/cores/solr.xml", 644), "/var/solr/solr.xml")
          .withCopyFileToContainer(MountableFile.forHostPath("../solr-plugin/target/similarity_plugin.jar"), "/opt/solr/lib/similarity_plugin.jar");
    }

    @Override
    protected void containerIsStarted(InspectContainerResponse containerInfo){
        //super.containerIsStarted(containerInfo); // we don't want dummy collection
        var CONFIG_FILE_NAME = "solrconfig.xml";
        var SCHEMA_FILE_NAME = "schema.xml";
        Stream.of(SolrCollectionName.ONTOLOGY, SolrCollectionName.ANNOTATION, SolrCollectionName.GENE_PRODUCT).forEach(col -> {
            try {
                if(!isCollectionExists(getHost(), getSolrPort(), col)) {
                    var conf = "../solr-cores/src/main/cores/%s/conf/".formatted(col);
                    SolrClientUtils.uploadConfiguration(super.getHost(), super.getSolrPort(), col,
                      Path.of(conf + CONFIG_FILE_NAME).toUri().toURL(),
                      Path.of(conf + SCHEMA_FILE_NAME).toUri().toURL()
                    );
                    SolrClientUtils.createCollection(super.getHost(), super.getSolrPort(), col, col);
                }
            } catch (URISyntaxException | IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private static boolean isCollectionExists(String hostname, int port, String collectionName) {
        HttpUrl url = generateSolrURL(hostname, port, Arrays.asList("admin", "collections"), Map.of("action","LIST"));
        try {
            return getSolrCollections(url).contains(collectionName);
        } catch (IOException | JSONException e) {
            return false;
        }
    }

    private static HttpUrl generateSolrURL(String hostname, int port, List<String> pathSegments, Map<String, String> parameters) {
        HttpUrl.Builder builder = new HttpUrl.Builder();
        builder.scheme("http");
        builder.host(hostname);
        builder.port(port);
        builder.addPathSegment("solr");
        if (pathSegments != null) {
            Objects.requireNonNull(builder);
            pathSegments.forEach(builder::addPathSegment);
        }

        Objects.requireNonNull(builder);
        parameters.forEach(builder::addQueryParameter);
        return builder.build();
    }

    private static List<String> getSolrCollections(HttpUrl url) throws IOException, JSONException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).get().build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful())
                throw new RuntimeException("Solr returned " + response.code());

            JSONObject json = new JSONObject(response.body().string());
            JSONArray arr = json.getJSONArray("collections");
            List<String> result = new ArrayList<>();
            for (int i = 0; i < arr.length(); i++)
                result.add(arr.getString(i));
            return result;
        }
    }
}
