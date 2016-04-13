package uk.ac.ebi.quickgo.geneproduct.service;

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
		this.serviceHelper = serviceHelper;
		this.geneProductRepository = geneProductRepository;
		this.converter = converter;
	}

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
