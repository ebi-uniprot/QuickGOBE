package uk.ac.ebi.quickgo.geneproduct.service;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import uk.ac.ebi.quickgo.geneproduct.common.GeneProductDocument;
import uk.ac.ebi.quickgo.geneproduct.common.GeneProductRepository;
import uk.ac.ebi.quickgo.geneproduct.common.common.GeneProductDocMocker;
import uk.ac.ebi.quickgo.geneproduct.model.GeneProduct;
import uk.ac.ebi.quickgo.geneproduct.service.converter.GeneProductDocConverter;
import uk.ac.ebi.quickgo.rest.service.ServiceHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;

/**
 * @author Tony Wardell
 * Date: 01/04/2016
 * Time: 13:01
 * Created with IntelliJ IDEA.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class GeneProductServiceImplTest {

    private final static List<String> id = Collections.singletonList("A0A000");
    private final static List<String> ids = Arrays.asList("A0A001", "A0A002", "A0A003", "A0A004");
    private static final String targetSet = "KRUK";

    @Mock
    private ServiceHelper serviceHelper;

    @Mock
    private GeneProductRepository geneProductRepository;

    @Mock
    private GeneProductDocConverter geneProductDocConverter;

    private GeneProductDocument geneProductDocument;

    private GeneProduct geneProduct;
    private GeneProduct geneProduct0;
    private GeneProduct geneProduct1;
    private GeneProduct geneProduct2;
    private GeneProduct geneProduct3;

    private GeneProductService geneProductService;

    @BeforeEach
    void setup() {
        geneProductService = new GeneProductServiceImpl(serviceHelper, geneProductRepository, geneProductDocConverter);

        stubSingleGeneProduct();
        stubMultipleGeneProducts();
    }

    private void stubSingleGeneProduct() {
        geneProduct = new GeneProduct();
        geneProductDocument = GeneProductDocMocker.createDocWithId("A0A000");
        List<GeneProductDocument> singleDocList = Collections.singletonList(geneProductDocument);
        when(serviceHelper.buildIdList(id)).thenReturn(id);
        when(geneProductRepository.findById(id)).thenReturn(singleDocList);
        when(geneProductDocConverter.convert(geneProductDocument)).thenReturn(geneProduct);
    }

    private void stubMultipleGeneProducts() {
        geneProduct0 = new GeneProduct();
        geneProduct0.id = new String[]{"A0A001", "A0A002", "A0A003", "A0A004"}[0];
        geneProduct1 = new GeneProduct();
        geneProduct1.id = new String[]{"A0A001", "A0A002", "A0A003", "A0A004"}[1];
        geneProduct2 = new GeneProduct();
        geneProduct2.id = new String[]{"A0A001", "A0A002", "A0A003", "A0A004"}[2];
        geneProduct3 = new GeneProduct();
        geneProduct3.id = new String[]{"A0A001", "A0A002", "A0A003", "A0A004"}[3];

        List<GeneProductDocument> multiDocList = new ArrayList<>();
        multiDocList.add(GeneProductDocMocker.createDocWithId(new String[]{"A0A001", "A0A002", "A0A003", "A0A004"}[0]));
        multiDocList.add(GeneProductDocMocker.createDocWithId(new String[]{"A0A001", "A0A002", "A0A003", "A0A004"}[1]));
        multiDocList.add(GeneProductDocMocker.createDocWithId(new String[]{"A0A001", "A0A002", "A0A003", "A0A004"}[2]));
        multiDocList.add(GeneProductDocMocker.createDocWithId(new String[]{"A0A001", "A0A002", "A0A003", "A0A004"}[3]));
        when(serviceHelper.buildIdList(ids)).thenReturn(ids);
        when(geneProductRepository.findById(ids)).thenReturn(multiDocList);
        when(geneProductDocConverter.convert(multiDocList.get(0))).thenReturn(geneProduct0);
        when(geneProductDocConverter.convert(multiDocList.get(1))).thenReturn(geneProduct1);
        when(geneProductDocConverter.convert(multiDocList.get(2))).thenReturn(geneProduct2);
        when(geneProductDocConverter.convert(multiDocList.get(3))).thenReturn(geneProduct3);
        when(geneProductRepository.findByTargetSet(targetSet)).thenReturn(multiDocList);
    }

    @Test
    void findSingleId() {
        List<GeneProduct> geneProducts = geneProductService.findById(id);
        assertThat(geneProducts, contains(geneProduct));
        assertThat(geneProducts, hasSize(1));
    }

    @Test
    void findForMultipleIDs() {
        List<GeneProduct> geneProducts = geneProductService.findById(ids);
        assertThat(geneProducts, contains(geneProduct0, geneProduct1, geneProduct2, geneProduct3));
        assertThat(geneProducts, hasSize(4));
    }

    @Test
    void idDoesntExist() {
        List<GeneProduct> geneProducts = geneProductService.findById(Collections.singletonList("QWERTY"));
        assertThat(geneProducts, hasSize(0));
    }

    @Test
    void findTargetSet() {
        List<GeneProduct> geneProducts = geneProductService.findByTargetSet(targetSet);
        assertThat(geneProducts, contains(geneProduct0, geneProduct1, geneProduct2, geneProduct3));
        assertThat(geneProducts, hasSize(4));
    }
}
