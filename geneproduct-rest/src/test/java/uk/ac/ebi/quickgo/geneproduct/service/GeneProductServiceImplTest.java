package uk.ac.ebi.quickgo.geneproduct.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import uk.ac.ebi.quickgo.geneproduct.common.GeneProductRepository;
import uk.ac.ebi.quickgo.geneproduct.common.document.GeneProductDocument;
import uk.ac.ebi.quickgo.geneproduct.model.GeneProduct;
import uk.ac.ebi.quickgo.geneproduct.service.converter.GeneProductDocConverter;
import uk.ac.ebi.quickgo.rest.service.ServiceHelperImpl;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.mockito.Mockito.when;

/**
 * @author Tony Wardell
 * Date: 01/04/2016
 * Time: 13:01
 * Created with IntelliJ IDEA.
 */
@RunWith(MockitoJUnitRunner.class)
public class GeneProductServiceImplTest {

	private GeneProductService geneProductService;

	@Mock
	private ServiceHelperImpl serviceHelper;

	@Mock
	private GeneProductRepository geneProductRepository;

	@Mock
	private GeneProductDocConverter geneProductDocConverter;

	@Mock
	private GeneProductDocument geneProductDocument;

	@Mock
	private GeneProduct geneProduct;

	@Before
	public void setup(){
		geneProductService = new GeneProductServiceImpl(serviceHelper, geneProductRepository, geneProductDocConverter);

		//stub single
		List<GeneProductDocument> singleDocList = Arrays.asList(geneProductDocument);
		when(serviceHelper.buildIdList(id)).thenReturn(id);
		when(geneProductRepository.findById(id)).thenReturn(singleDocList);
		when(geneProductDocConverter.convert(geneProductDocument)).thenReturn(geneProduct);

	}

	final List<String> id = Arrays.asList("A0A000");

	@Test
	public void testWeFindASingleId(){

		List<GeneProduct> geneProducts = geneProductService.findById(id);
		assertThat(geneProducts, containsInAnyOrder(geneProduct));
	}


}
