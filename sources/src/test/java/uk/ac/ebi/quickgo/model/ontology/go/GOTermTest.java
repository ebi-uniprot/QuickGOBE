package uk.ac.ebi.quickgo.model.ontology.go;


import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class GOTermTest {

  @Test
  void eGOAspect_stringToUsageFindsSuccessfully() {
    GOTerm.EGOAspect aspect = GOTerm.EGOAspect.fromString("Process");
    assertNotNull(aspect);

    aspect = GOTerm.EGOAspect.fromString("Function");
    assertNotNull(aspect);

    aspect = GOTerm.EGOAspect.fromString("Component");
    assertNotNull(aspect);

    aspect = GOTerm.EGOAspect.fromString("Root");
    assertNotNull(aspect);
  }

  @Test
  void eGOAspect_stringToUsageProducesIllegalArgumentException() {
    assertThrows(IllegalArgumentException.class, () -> GOTerm.EGOAspect.fromString("unknown"));
  }

}