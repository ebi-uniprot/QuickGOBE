<div id="term-information-section">
	<h3>
		<strong>Term information</strong>
	</h3>
	<table>
		<tr>
			<td class="first">ID</td>
			<td>${term.id}</td>
		</tr>
		<tr>
			<td class="first">Name</td>
			<td>${term.name}</td>
		</tr>
		<c:if test="${isGO}">
			<tr>
				<td class="first">Ontology</td>
				<td>${term.aspectDescription}</td>
			</tr>
		</c:if>
		<tr>
			<td class="first">Definition</td>
			<td>
				${term.definition}
				<div class="clearfix"/>
				<c:forEach var="xrefDef" items="${term.definitionXrefs}">
					<a href="http://europepmc.org/abstract/MED/${xrefDef.id}" target="_blank">${xrefDef.db}:${xrefDef.id}</a>
				</c:forEach>
			</td>
		</tr>
		<c:if test="${term.usage.code != 'U' && isGO}">
			<tr>
				<td class="first">Restrictions</td>
				<td>
					<span style="background-color:#FFFBCC;">${term.usage.description}
					<c:if test="${not empty term.comment}">See comment below for further information.</c:if></span>
				</td>
			</tr>
		</c:if>
		<c:if test="${not empty term.comment}">
			<tr>
				<td class="first">Comment</td>
				<td>${term.comment}</td>
			</tr>		
		</c:if>
		<c:if test="${fn:length(term.altIds) > 0}">
			<tr>
				<td class="first">Secondary IDs</td>
				<td id="secondariesColumn">${term.altIdsString}</td>
			</tr>		
		</c:if>
		<c:if test="${isGO}">
			<tr>
				<td class="first">GONUTS</td>
				<td><a href="http://gowiki.tamu.edu/wiki/index.php/Category:${term.id}" target="_blank">${term.id} Wiki Page</a></td>
			</tr>	
		</c:if>
			<c:if test="${fn:length(term.credits) > 0}">
			<tr>
				<td class="first">Acknowledgements</td>
				<td>	
					<c:forEach var="credit" items="${term.credits}">
						This term was created by the GO Consortium with support from
						<a href="${credit.url}" target="_blank">
							<c:if test="${credit.code eq 'BHF'}">
								<img src="http://www.bhf.org.uk/images/logo.jpg" alt="BHF image" style="width:50px;height:45px;" />
							</c:if>		
							<c:if test="${credit.code eq 'KRUK'}">
								<img src="http://www.kidneyresearchuk.org/img/logos/kidneyresearchuk-logo_201x85.png" alt="KRUK image" style="width:90px;height:45px;" />
							</c:if>							
						</a>
					</c:forEach>
				</td>	
			</tr>		
		</c:if>
	</table>

	<!-- Tabs -->
	<div id="termInformationTabs">
	
	<c:set var="synonymsDisplay" value="display:none"/>
	<c:set var="guidelinesDisplay" value="display:none"/>
	<c:set var="ontologyRelationsDisplay" value="display:none"/>
	<c:set var="xrefsDisplay" value="display:none"/>
	<c:set var="replacesDisplay" value="display:none"/>
	<c:set var="replacementsDisplay" value="display:none"/>
	<c:set var="taxonConstraintsDisplay" value="display:none"/>
	<c:set var="subsetsDisplay" value="display:none"/>
		<ul>
			<c:if test="${fn:length(term.synonyms) > 0}"> 
				<li><a href="#synonyms">Synonyms</a></li>
				<c:set var="synonymsDisplay" value="display:block"/>
			</c:if>
			<c:if test="${fn:length(term.guidelines) > 0}"> 
				<c:set var="guidelinesDisplay" value="display:block"/>
				<li><a href="#guidance">Annotation Guidance</a></li>
			</c:if>
			<c:if test="${fn:length(term.crossOntologyRelations) > 0}"> 
				<c:set var="ontologyRelationsDisplay" value="display:block"/>
				<li><a href="#ontologyrelations">Cross-Ontology Relations</a></li>
			</c:if>
			<c:if test="${fn:length(term.taxonConstraints) > 0}"> 
				<li><a href="#taxonconstraints">Taxon constraints</a></li>
				<c:set var="taxonConstraintsDisplay" value="display:block"/>
			</c:if>
			<c:if test="${fn:length(term.subsets) > 0}"> 
				<li><a href="#subsets">GO Slims</a></li>
				<c:set var="subsetsDisplay" value="display:block"/>
			</c:if>
			<c:if test="${fn:length(term.xrefs) > 0}"> 
				<li><a href="#xref">Cross-references</a></li>
				<c:set var="xrefsDisplay" value="display:block"/>
			</c:if>
			<c:if test="${fn:length(term.replaces) > 0}"> 
				<li><a href="#replaces">Replaces</a></li>
				<c:set var="replacesDisplay" value="display:block"/>
			</c:if>
			<c:if test="${fn:length(term.replacements) > 0}"> 
				<li><a href="#replacements">Replaced by</a></li>
				<c:set var="replacementsDisplay" value="display:block"/>
			</c:if>
		</ul>


		<div id="synonyms" style="${synonymsDisplay}">
			<p>Synonyms are alternative words or phrases closely related in meaning to the term name, with indication of the relationship between the name and synonym given by the synonym scope.</p>
			<table id="synonymsTable" class="two-colours">
				<tr>
					<th>Type</th>
					<th>Synonym</th>
				</tr>
				<c:forEach var="synonym" items="${term.synonyms}">
					<tr>
						<td>${synonym.type}</td>
						<td>${synonym.name}</td>
					</tr>
				</c:forEach>
			</table>
		</div>
		<div id="taxonconstraints" style="${taxonConstraintsDisplay}">
			<p>The use of this term should conform to the following taxon constraints:</p>			
			<table id="taxonconstraintsTable" class="two-colours">
				<tr>
					<th>Taxon Rule</th>
					<th>Ancestor GO ID</th>
					<th>Ancestor GO Term Name</th>
					<th>Relationship</th>
					<th>Taxon ID</th>
					<th>Taxon</th>
					<th>Reference(s)</th>
				</tr>
				<c:forEach var="taxonconstraint" items="${term.taxonConstraints}">
					<tr>
						<td>${taxonconstraint.ruleId}</td>
						<td><a href="<c:url value="/"/>term/${taxonconstraint.goId}">${taxonconstraint.goId}</a></td>
						<td>${taxonconstraint.name}</td>
						<td>${taxonconstraint.relationship.text}</td>
						<td>${taxonconstraint.taxId}</td>
						<td>${taxonconstraint.taxonName}</td>
						<td>
							<c:forEach var="source" items="${taxonconstraint.sources}">
								<a href="${source.url}" target="_blank">${source.pmid} </a>
							</c:forEach>
						</td>
					</tr>
				</c:forEach>
			</table>
		</div>
		<div id="guidance" style="${guidelinesDisplay}">
			<p>Usage of this term is subject to the following annotation guidelines:</p>
			<table id="guidelineTable" class="two-colours">
				<tr>
					<th colspan="2">Annotation Guidelines</th>
				</tr>
				<c:forEach var="guideline" items="${term.guidelines}">
					<tr>
						<td>${guideline.title}</td>
						<td><a href="${guideline.url}" target="_blanket">Link</a></td>
					</tr>
				</c:forEach>
			</table>
		</div>
		<div id="ontologyrelations" style="${ontologyRelationsDisplay}">			
			<table id="ontologyrelationsTable" class="two-colours">
				<tr>
					<th>Relation</th><th>Other Ontology</th><th>ID</th><th>Term</th>
				</tr>
				<c:forEach var="crossontology" items="${term.crossOntologyRelations}">
					<tr>
						<td>${crossontology.relation}</td>						
						<td>${crossontology.otherNamespace}</td>
						<td><a href="${crossontology.url}" target="_blanket">${crossontology.foreignID}</a></td>
						<td>${crossontology.foreignTerm}</td>
					</tr>
				</c:forEach>
			</table>
		</div>
		
		<div id="subsets" style="${subsetsDisplay}">
			<p>This term is present in the following GO Consortium-maintained GO slims:</p>
			<table id="subsetsTable" class="two-colours">
				<tr>
					<th>GO slim name</th>
					<th>Count of all terms</th>
				</tr>
				<c:forEach var="subset" items="${subsetsCounts}">
					<tr>
						<td>${subset.subset}</td>
						<td>${subset.subsetCount}</td>
					</tr>
				</c:forEach>
			</table>
		</div>
		<div id="xref" style="${xrefsDisplay}">
			<table id="xrefTable" class="two-colours">
				<tr>
					<th>Database</th>
					<th>ID</th>
					<th>Description</th>
				</tr>
				<c:forEach var="xref" items="${termXrefs}">
					<tr>
						<td>${xref.xrefDB}</td>
						<td>${xref.xrefID}</td>
						<td><a href="${xref.xrefDescription.url}" target="_blank">${xref.xrefDescription.name}</a></td>
					</tr>
				</c:forEach>
			</table>
		</div>
		<div id="replaces" style="${replacesDisplay}">
			<p>This term can be used instead of these obsolete terms:</p>			
			<table id="replacesTable" class="two-colours">
				<tr>
					<th>GO Identifier</th>
					<th>GO Term Name</th>
					<th>Reason</th>
				</tr>
				<c:forEach var="replacement" items="${term.replaces}">
					<tr>
						<td><a href="<c:url value="/"/>term/${replacement.child.id}">${replacement.child.id}</a></td>
						<td>${replacement.child.name}</td>
						<td>${replacement.typeof.description} <a href="<c:url value="/"/>term/${replacement.parent.id}">${replacement.parent.id}</a></td>
					</tr>
				</c:forEach>
			</table>
		</div>
		<div id="replacements" style="${replacementsDisplay}">
			<p>This term is obsolete. Try one of these terms:</p>			
			<table id="replacementsTable" class="two-colours">
				<tr>
					<th>Advice</th>
					<th>GO Identifier</th>
					<th>Ontology</th>
					<th>GO Term Name</th>
				</tr>
				<c:forEach var="replacedby" items="${term.replacements}">
					<tr>
						<td>${replacedby.typeof.description}</td>
						<td><a href="<c:url value="/"/>term/${replacedby.parent.id}">${replacedby.parent.id}</a></td>
						<td>${replacedby.parent.aspectDescription}</td>
						<td>${replacedby.parent.name}</td>
					</tr>
				</c:forEach>
			</table>
		</div>		
	</div>
	<script>
		// Tabs
		$("#termInformationTabs").tabs();
	</script>
</div>