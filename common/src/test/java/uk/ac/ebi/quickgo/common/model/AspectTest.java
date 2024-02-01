package uk.ac.ebi.quickgo.common.model;

import org.junit.jupiter.api.Test;

import static java.util.Optional.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

/**
 * Test the methods on Aspect.
 *
 * @author Tony Wardell
 * Date: 10/08/2017
 * Time: 16:47
 * findd with IntelliJ IDEA.
 */
class AspectTest {

    @Test
    void findAspectFromExistingScientificName(){
        assertThat(Aspect.fromScientificName("biological_process"), equalTo(of(Aspect.BIOLOGICAL_PROCESS)));
        assertThat(Aspect.fromScientificName("molecular_function"), equalTo(of(Aspect.MOLECULAR_FUNCTION)));
        assertThat(Aspect.fromScientificName("cellular_component"), equalTo(of(Aspect.CELLULAR_COMPONENT)));
    }

    @Test
    void findAspectFromInvalidScientificNameReturnsOptionalEmpty(){
        assertThat(Aspect.fromScientificName("dish-washing"), equalTo(empty()));
    }

    @Test
    void findAspectFromNullScientificNameReturnsOptionalEmpty(){
        assertThat(Aspect.fromScientificName(null), equalTo(empty()));
    }

    @Test
    void findAspectFromEmptyScientificNameReturnsOptionalEmpty(){
        assertThat(Aspect.fromScientificName(""), equalTo(empty()));
    }

    @Test
    void findAspectFromExistingShortName(){
        assertThat(Aspect.fromShortName("Process"), equalTo(of(Aspect.BIOLOGICAL_PROCESS)));
        assertThat(Aspect.fromShortName("Function"), equalTo(of(Aspect.MOLECULAR_FUNCTION)));
        assertThat(Aspect.fromShortName("Component"), equalTo(of(Aspect.CELLULAR_COMPONENT)));
    }

    @Test
    void findAspectFromInvalidShortNameReturnsOptionalEmpty(){
        assertThat(Aspect.fromShortName("dish-washing"), equalTo(empty()));
    }

    @Test
    void findAspectFromNullShortNameReturnsOptionalEmpty(){
        assertThat(Aspect.fromShortName(null), equalTo(empty()));
    }

    @Test
    void findAspectFromEmptyShortNameReturnsOptionalEmpty(){
        assertThat(Aspect.fromShortName(""), equalTo(empty()));
    }

    @Test
    void findAspectFromExistingCharacter(){
        assertThat(Aspect.fromCharacter("P"), equalTo(of(Aspect.BIOLOGICAL_PROCESS)));
        assertThat(Aspect.fromCharacter("F"), equalTo(of(Aspect.MOLECULAR_FUNCTION)));
        assertThat(Aspect.fromCharacter("C"), equalTo(of(Aspect.CELLULAR_COMPONENT)));
    }

    @Test
    void findAspectFromInvalidCharacterReturnsOptionalEmpty(){
        assertThat(Aspect.fromCharacter("dish-washing"), equalTo(empty()));
    }

    @Test
    void findAspectFromNullCharacterReturnsOptionalEmpty(){
        assertThat(Aspect.fromCharacter(null), equalTo(empty()));
    }

    @Test
    void findAspectFromEmptyCharacterReturnsOptionalEmpty(){
        assertThat(Aspect.fromCharacter(""), equalTo(empty()));
    }

    @Test
    void valueOfWorks(){
        assertThat(Aspect.valueOf("BIOLOGICAL_PROCESS"),equalTo(Aspect.BIOLOGICAL_PROCESS));
    }
}
