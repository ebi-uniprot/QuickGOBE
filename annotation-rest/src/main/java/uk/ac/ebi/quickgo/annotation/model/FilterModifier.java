package uk.ac.ebi.quickgo.annotation.model;

/**
 * @author Tony Wardell
 * Date: 24/05/2016
 * Time: 17:19
 * Created with IntelliJ IDEA.
 */
public interface FilterModifier {

    String[] modify(String[] original);
}
