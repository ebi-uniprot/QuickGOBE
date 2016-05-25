package uk.ac.ebi.quickgo.annotation.model;

import uk.ac.ebi.quickgo.annotation.service.search.SearchConfig;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;

import static java.util.stream.Collectors.toList;

/**
 * Modify filter values for references.
 *
 *
 * @author Tony Wardell
 * Date: 24/05/2016
 * Time: 17:25
 * Created with IntelliJ IDEA.
 */
public class ReferenceModifier implements FilterModifier{

    private static final Pattern ALL_NUMERIC = Pattern.compile("\\d*");
    private final SearchConfig searchConfig;

    @Autowired
    public ReferenceModifier(SearchConfig searchConfig) {
        this.searchConfig = searchConfig;
    }

    public String[] modify(String[] original){
        Set<String> modifiedArgs = new LinkedHashSet<>();
        for (int i = 0; i < original.length; i++) {

            //If given just a Database identifier, make it a wildcard search
            if( searchConfig.referenceDbs.contains(original[i]) ){
                modifiedArgs.add(original[i]+":*");
            } else {

                //If we have just an id, create a request for all known DBs
                Matcher m = ALL_NUMERIC.matcher(original[i]);
                if (m.matches()) {
                    for (String db : searchConfig.referenceDbs)
                        modifiedArgs.add(db + ":" + original[i]);
                } else {
                    modifiedArgs.add(original[i]);
                }
            }
        }
        return modifiedArgs.toArray(new String[modifiedArgs.size()]);
    }


    public String[] modify2(String[] original){
        List<String> modifiedArgs = new ArrayList<>();

        Arrays.stream(original)
                .distinct()
                .filter(arg -> searchConfig.referenceDbs.contains(arg))
                .map(arg -> arg+":*")
                .forEach(modifiedArgs::add);




        return modifiedArgs.toArray(new String[modifiedArgs.size()]);
    }



}
