package uk.ac.ebi.quickgo.geneproduct.service;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import uk.ac.ebi.quickgo.geneproduct.common.GeneProductRepository;
import uk.ac.ebi.quickgo.geneproduct.common.document.GeneProductDocument;
import uk.ac.ebi.quickgo.geneproduct.model.GeneProduct;
import uk.ac.ebi.quickgo.geneproduct.service.converter.GeneProductDocConverter;
import uk.ac.ebi.quickgo.rest.service.ServiceHelper;
import uk.ac.ebi.quickgo.rest.service.ServiceHelperImpl;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author Tony Wardell
 * Date: 29/03/2016
 * Time: 10:56
 * Created with IntelliJ IDEA.
 */
public class GeneProductServiceImpl implements GeneProductService {

	private final ServiceHelper serviceHelper;
	private final GeneProductRepository geneProductRepository;
	private final GeneProductDocConverter converter;

	public GeneProductServiceImpl(ServiceHelper serviceHelper, GeneProductRepository geneProductRepository, GeneProductDocConverter converter) {

		java.util.Objects.requireNonNull(serviceHelper, "The ServiceHelper instance passed to the constructor of " +
				"GeneProductServiceImpl should not be null.");
		java.util.Objects.requireNonNull(geneProductRepository, "The GeneProductRepository instance passed to the constructor of " +
				"GeneProductServiceImpl should not be null.");
		java.util.Objects.requireNonNull(converter, "The GeneProductDocConverter instance passed to the constructor of " +
				"GeneProductServiceImpl should not be null.");
		this.serviceHelper = serviceHelper;
		this.geneProductRepository = geneProductRepository;
		this.converter = converter;
	}

	/**
	 * Find the core data set stored for a specified list of geneProduct IDs.
	 * @param ids the ontology IDs
	 * @return a {@link List} of {@link GeneProduct} instances corresponding to the gene product ids containing the
	 * chosen information
	 */
	@Override
	public List<GeneProduct> findById(String[] ids) {
		return convertDocs(geneProductRepository.findById(serviceHelper.buildIdList(ids)));
	}

	protected List<GeneProduct> convertDocs(List<GeneProductDocument> docs) {
		return docs
				.stream()
				.map(converter::convert)
				.collect(Collectors.toList());
	}
}
