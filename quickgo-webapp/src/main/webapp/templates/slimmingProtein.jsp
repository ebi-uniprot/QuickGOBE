<!-- Filter by protein id menu -->

<div id="filterbyproteinidmenu" class="grid_12" style="display: none;height: 600px">
	<h6>Step 2: Add proteins</h6>
	<h5>Add protein IDs</h5>
	<div class="slimming_section" style="height: 200px">
		<textarea style="height: 98%; width: 98%;" id="slimming_protein_ids"></textarea>
	</div>
	<h5>OR select a list of proteins</h5>
	<table>
		<c:set var="targetSetChecked" value="" />
		<c:if
			test="${fn:contains(sessionScope.appliedFilters.parameters['targetSet'], 'BHF-UCL')}">
			<c:set var="targetSetChecked" value="checked" />
		</c:if>
		<tr>
			<td><input type="checkbox" ${targetSetChecked} value="BHF-UCL" name="sliming-proteinset-filter" /></td>
			<td>BHF-UCL</td>
			<td>The set of Cardiovascular-associated proteins being
				prioritised for annotation by the Cardiovascular Gene Ontology
				Annotation Initiative located at University College London</td>
			<td><a href="http://www.ucl.ac.uk/cardiovasculargeneontology"
				target="_blank" />http://www.ucl.ac.uk/cardiovasculargeneontology</a></td>
		</tr>
		<c:set var="targetSetChecked" value="" />
		<c:if
			test="${fn:contains(sessionScope.appliedFilters.parameters['targetSet'], 'KRUK')}">
			<c:set var="targetSetChecked" value="checked" />
		</c:if>
		<tr>
			<td><input type="checkbox" ${targetSetChecked} value="KRUK" name="sliming-proteinset-filter" /></td>
			<td>KRUK</td>
			<td>The set of proteins associated with renal processes being
				prioritised for annotation by the EBI's Renal Gene Ontology
				Annotation Initiative</td>
			<td><a href="http://www.ebi.ac.uk/GOA/kidney" target="_blank">http://www.ebi.ac.uk/GOA/kidney</a></td>
		</tr>
		<c:set var="targetSetChecked" value="" />
		<c:if
			test="${fn:contains(sessionScope.appliedFilters.parameters['targetSet'], 'ReferenceGenome')}">
			<c:set var="targetSetChecked" value="checked" />
		</c:if>
		<tr>
			<td><input type="checkbox" ${targetSetChecked} value="ReferenceGenome" name="sliming-proteinset-filter" /></td>
			<td>ReferenceGenome</td>
			<td>The set of human proteins being comprehensively curated by
				the UniProt-GOA project as part of the GO Consortium's Reference
				Genome initiative</td>
			<td><a href="http://www.geneontology.org/GO.refgenome.shtml"
				target="_blank">http://www.geneontology.org/GO.refgenome.shtml</a></td>
		</tr>
	</table>
</div>