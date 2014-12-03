<div id="advanced-filters-popup" class="lightbox" style="display: none;">
<div id="advanced-filters-popup-content">
<div id="advancedFiltersMenu" class="container_12" style="height:80%">
	<div alt="Close the advanced filters menu" class="grid_12">
		<a style="float: right" id="closeAdvancedFiltersMenu" href="#" class="closeBox">x</a>	
	</div>
	<h3>Advanced filters <img id="advanced-loading-image" src=" <%= request.getContextPath() %>/images/ajax-loader.gif" alt="Loading..." height="42" width="42" /></h3>	
	<div id="advancedFiltersTabs" class="grid_12" style="overflow-y:auto;height:100%">	
		<div class="grid_2">		
			<ul>
				<li><a href="#geneProductTab">Gene Product ID</a></li>				
				<li><a href="#taxonTab">Taxon</a></li>
				<li><a href="#quialifierTab">Qualifier</a></li>				
				<li><a href="#goIdTab">GO Identifier</a></li>				
				<li><a href="#aspectTab">Aspect</a></li>
				<li><a href="#evidenceTab">Evidence</a></li>
				<li><a href="#withTab">With</a></li>				
				<li><a href="#assignedByTab">Assigned By</a></li>
			</ul>
		</div>
		<div id="advancedTabsSection" style="height:100%">
			 <script>
				$(function() {
					$("#advancedFiltersTabs").tabs().addClass( "ui-tabs-vertical ui-helper-clearfix" );
					$("#advancedFiltersTabs li").removeClass( "ui-corner-top" ).addClass( "ui-corner-left" );
				});
			</script>
			<div id="geneProductTab" class="grid_9" style="height:80%">
				<h4>Gene Product ID</h4>
				<h6>Find annotations for a defined set of gene or protein identifiers</h6>	
				<p>To restrict the annotations to a set of gene or protein identifiers, enter a list of IDs in the text box below, separated by commas, spaces or newlines. There is no limit on the number of identifers that can be entered into this box.</p>
<!--<p>You can use identifier types other than UniProtKB accessions (for the full range of acceptable ID types, please see the ID Mapping tab on the Annotation Tool Bar). However please note that QuickGO will automatically map the identifiers to UniProtKB accessions, and unless you have additionally selected a specific Identifier Type to be returned in the annotation set, the default behaviour of this tool will be to display the filtered set of annotations with their corresponding UniProtKB accession numbers.</p>-->
				<div id="geneProductArea">
					<textarea class="advanced-filter-dbObjectID" style="width:50%"></textarea>
					<h6>OR select a list of proteins</h6>
					<table>
					<c:set var="targetSetChecked" value="" />
					<c:if test="${fn:contains(sessionScope.appliedFilters.parameters['targetSet'], 'BHF-UCL')}">
						<c:set var="targetSetChecked" value="checked"/>
					</c:if>
					<tr><td><input type="checkbox" ${targetSetChecked} value="BHF-UCL" class="advanced-filter-targetSet" /></td><td>BHF-UCL</td><td>The set of Cardiovascular-associated proteins being prioritised for annotation by the Cardiovascular Gene Ontology Annotation Initiative located at University College London</td><td><a href="http://www.ucl.ac.uk/cardiovasculargeneontology" target="_blank"/>http://www.ucl.ac.uk/cardiovasculargeneontology</a></td></tr>
					<c:set var="targetSetChecked" value="" />
					<c:if test="${fn:contains(sessionScope.appliedFilters.parameters['targetSet'], 'KRUK')}">
						<c:set var="targetSetChecked" value="checked"/>
					</c:if>
					<tr><td><input type="checkbox" ${targetSetChecked} value="KRUK" class="advanced-filter-targetSet" /></td><td>KRUK</td><td>The set of proteins associated with renal processes being prioritised for annotation by the EBI's Renal Gene Ontology Annotation Initiative</td><td><a href="http://www.ebi.ac.uk/GOA/kidney" target="_blank">http://www.ebi.ac.uk/GOA/kidney</a></td></tr>
					<c:set var="targetSetChecked" value="" />
					<c:if test="${fn:contains(sessionScope.appliedFilters.parameters['targetSet'], 'ReferenceGenome')}">
						<c:set var="targetSetChecked" value="checked"/>
					</c:if>
					<tr><td><input type="checkbox" ${targetSetChecked} value="ReferenceGenome" class="advanced-filter-targetSet" /></td><td>ReferenceGenome</td><td>The set of human proteins being comprehensively curated by the UniProt-GOA project as part of the GO Consortium's Reference Genome initiative</td><td><a href="http://www.geneontology.org/GO.refgenome.shtml" target="_blank">http://www.geneontology.org/GO.refgenome.shtml</a></td></tr>
						 	 	
					</table>
				</div>
			</div>
			<div id="taxonTab" class="grid_9">
				<h4>Taxon</h4>			
				<p>To filter by species/taxonomic group, list taxon identifiers in the text box or select one or more of the pre-defined taxonomic groups below</p>
				<div id="taxonomiesArea">				
					<table id="taxonomyIdAdvancedFilter" style="width:70%">				
						<c:forEach var="taxon" items="${sessionScope.mostCommonTaxonomies}">
							<c:set var="taxChecked" value="" />
							<c:forEach var="taxSelected" items="${sessionScope.appliedFilters.parameters['taxonomyClosure']}">
								<c:if test="${taxSelected == taxon.id}">
									<c:set var="taxChecked" value="checked"/>
								</c:if>
							</c:forEach>
							<tr><td><input type="checkbox" value="${taxon.id}" ${taxChecked} class="advanced-filter-taxonomyId" /></td><td>${taxon.id}</td><td>${taxon.name}</td></tr>
						</c:forEach>
					</table>
				</div>
				<div class="clearfix"><p/></div>
				<textarea class="advanced-filter-taxonomyId" style="width:50%"></textarea>
				<div class="clearfix"><p/></div>
				Visit <a href="http://www.uniprot.org/taxonomy/" target="_blank">UniProt Taxonomy</a> to find identifiers for other taxonomic groups
			</div>
			<div id="quialifierTab" class="grid_9">
				<h4>Qualifier</h4>
				<p>The Qualifier column in a GO annotation is used for flags that modify the interpretation of an annotation. Allowable values are NOT, contributes_to, and colocalizes_with.</p>
				<p>Further details on the usage of the qualifier column can be found <a href="http://www.geneontology.org/page/go-annotation-conventions#qual" target="_blank">here</a>.</p>
				<div id="qualifierArea">				
				<table>
					<c:set var="enablesChecked" value="" />
					<c:if test="${sessionScope.appliedFilters.containsIgnoreCase(sessionScope.appliedFilters.parameters['qualifier'], 'enables')}">
						<c:set var="enablesChecked" value="checked"/>
					</c:if>
					<tr><td><input type="checkbox" ${enablesChecked} value="enables" class="advanced-filter-qualifier" /></td><td>enables</td><td></td></tr>
					<c:set var="notEnablesChecked" value="" />
					<c:if test="${sessionScope.appliedFilters.containsIgnoreCase(sessionScope.appliedFilters.parameters['qualifier'], '\"NOT\" AND enables')}">
						<c:set var="notEnablesChecked" value="checked"/>
					</c:if>					
					<tr><td><input type="checkbox" ${notEnablesChecked} value="NOT AND enables" class="advanced-filter-qualifier" /></td><td>NOT | enables</td><td></td></tr>
					<c:set var="involvedInChecked" value="" />
					<c:if test="${sessionScope.appliedFilters.containsIgnoreCase(sessionScope.appliedFilters.parameters['qualifier'], 'involved_in')}">
						<c:set var="involvedInChecked" value="checked"/>
					</c:if>					
					<tr><td><input type="checkbox" ${involvedInChecked} value="involved_in" class="advanced-filter-qualifier" /></td><td>involved_in</td><td></td></tr>
					<c:set var="notInvolvedInChecked" value="" />
					<c:if test="${sessionScope.appliedFilters.containsIgnoreCase(sessionScope.appliedFilters.parameters['qualifier'], '\"NOT\" AND involved_in')}">
						<c:set var="notInvolvedInChecked" value="checked"/>
					</c:if>	
					<tr><td><input type="checkbox" ${notInvolvedInChecked} value="NOT AND involved_in" class="advanced-filter-qualifier" /></td><td>NOT | involved_in</td><td></td></tr>					<c:set var="partOfChecked" value="" />
					<c:if test="${sessionScope.appliedFilters.containsIgnoreCase(sessionScope.appliedFilters.parameters['qualifier'], 'part_of')}">
						<c:set var="partOfChecked" value="checked"/>
					</c:if>	
					<tr><td><input type="checkbox" ${partOfChecked} value="part_of" class="advanced-filter-qualifier" /></td><td>part_of</td><td></td></tr>
					<c:set var="notPartOfChecked" value="" />
					<c:if test="${sessionScope.appliedFilters.containsIgnoreCase(sessionScope.appliedFilters.parameters['qualifier'], '\"NOT\" AND part_of')}">
						<c:set var="notPartOfChecked" value="checked"/>
					</c:if>						
					<tr><td><input type="checkbox" ${notPartOfChecked} value="NOT AND part_of" class="advanced-filter-qualifier" /></td><td>NOT | part_of</td><td></td></tr>
					<c:set var="contributesTo" value="" />
					<c:if test="${sessionScope.appliedFilters.containsIgnoreCase(sessionScope.appliedFilters.parameters['qualifier'], 'contributes_to')}">
						<c:set var="contributesTo" value="checked"/>
					</c:if>
					<tr><td><input type="checkbox" ${contributesTo} value="contributes_to" class="advanced-filter-qualifier" /></td><td>contributes_to</td><td></td></tr>
					<c:set var="notContributesTo" value="" />
					<c:if test="${sessionScope.appliedFilters.containsIgnoreCase(sessionScope.appliedFilters.parameters['qualifier'], '\"NOT\" AND contributes_to')}">
						<c:set var="notContributesTo" value="checked"/>
					</c:if>
					<c:set var="notContributesTo" value="" />
					<c:if test="${sessionScope.appliedFilters.containsIgnoreCase(sessionScope.appliedFilters.parameters['qualifier'], '\"NOT\" AND contributes_to')}">
						<c:set var="notContributesTo" value="checked"/>
					</c:if>	
					<tr><td><input type="checkbox" ${notContributesTo} value="NOT AND contributes_to" class="advanced-filter-qualifier" /></td><td>NOT | contributes_to</td><td></td></tr>					
					<c:set var="colocalizesWithChecked" value="" />
					<c:if test="${sessionScope.appliedFilters.containsIgnoreCase(sessionScope.appliedFilters.parameters['qualifier'], 'colocalizes_with')}">
						<c:set var="colocalizesWithChecked" value="checked"/>
					</c:if>
					<tr><td><input type="checkbox" ${colocalizesWithChecked} value="colocalizes_with" class="advanced-filter-qualifier" /></td><td>colocalizes_with</td><td></td></tr>
					<c:set var="notColocalizesWithChecked" value="" />
					<c:if test="${sessionScope.appliedFilters.containsIgnoreCase(sessionScope.appliedFilters.parameters['qualifier'], '\"NOT\" AND colocalizes_with')}">
						<c:set var="notColocalizesWithChecked" value="checked"/>
					</c:if>
					<tr><td><input type="checkbox" ${notColocalizesWithChecked} value="NOT AND colocalizes_with" class="advanced-filter-qualifier" /></td><td>NOT | colocalizes_with</td><td></td></tr>
					<c:set var="notChecked" value="" />
					<c:if test="${sessionScope.appliedFilters.containsIgnoreCase(sessionScope.appliedFilters.parameters['qualifier'], '\"NOT\"')}">
						<c:set var="notChecked" value="checked"/>
					</c:if>
					<tr><td><input type="checkbox" ${notChecked} value="NOT" class="advanced-filter-qualifier" /></td><td>NOT</td><td></td></tr>
				</table>
   			     </div>
			</div>
			<div id="goIdTab" class="grid_9">
				<h4>GO Identifier</h4>			
				<h6>Select the terms that you wish to use</h6>
				<div id="addedTermsToBasket">
					<ul>
						<c:forEach var="basketItem" items="${sessionScope.basketTerms}">
							<c:set var="termChecked" value="" />
							<c:if test="${sessionScope.appliedFilters.isApplied(basketItem.key)}">
								<c:set var="termChecked" value="checked"/>
							</c:if>
							
							<li><input class="advanced-filter-goID" type="checkbox" value="${basketItem.key}" ${termChecked}/> ${basketItem.key} ${basketItem.value}</li>
						</c:forEach>
					</ul>
				</div>
				<div class="clearfix"><p/></div>
				Predefined GO term sets:
				<br>
				<select class="advanced-filter-subSet">
					<option value="">None</option>
					<c:forEach var="count" items="${sessionScope.allSubsetsCounts}">
						  <option value="${count.subset}">${count.subset}(${count.subsetCount})</option>						  
					</c:forEach>	
				</select> 
				<div class="clearfix"><p/></div>
				<div id="goIdArea">				
					Enter a list of GO identifiers (GO:nnnnnnn), separated by space, comma or newline characters:
					<textarea class="advanced-filter-goID" style="width:50%;height:50px"></textarea>
					<div class="clearfix"><p/></div>					
					<h6>Select how you want to use the terms that you have chosen</h6>
					<table id="slimOptions">					
						<tr><td><input type="radio" name="slim" value="true" class="advanced-filter-slim" id="slim-filtering"/></td><td>Use these terms as a GO slim</td></tr>
						<tr><td><input type="radio" name="slim" value="false" class="advanced-filter-slim" id="descendants-filtering"/></td><td>Find annotations to descendants of these terms (annotations will display the original GO terms applied)
Select which relationship an annotated term should have to the terms above:</td></tr>
						<script>	
							// Slim filtering options
							$('#descendants-filtering').on('change', function() {
								if($("#goIDclosureTypes").is(":visible")){
									$("#goIDclosureTypes").hide();
								}else{
									$("#goIDclosureTypes").show();
								}
							});
		
							$('#slim-filtering').click(function(event) {			    
								$("#goIDclosureTypes").hide();
							});
						</script>	
					</table>
					<table id="goIDclosureTypes" style="width:50%;margin-left: 35px;">					
						<tr><td><input type="radio" name="closure" value="ancestorsI" class="advanced-filter-ancestorsI"/></td><td>is_a only</td></tr>
						<tr><td><input type="radio" name="closure" value="ancestorsIPO" class="advanced-filter-ancestorsIPO"/></td><td>is_a, part_of</td></tr>
						<tr><td><input type="radio" name="closure" value="ancestorsIPOR" class="advanced-filter-ancestorsIPOR"/></td><td>is_a, part_of, regulates</td></tr>
					</table>								
				</div>		
			</div>
			<div id="aspectTab" class="grid_9">
				<h4>Aspect</h4>
				<p>You can use the check boxes below to limit the scope of the returned annotation set to terms from one or more specific ontologies:</p>
				<div id="aspectsArea">								
					<table id="aspects" style="width:30%">
						<c:set var="aspectChecked" value="" />
						<c:if test="${fn:contains(sessionScope.appliedFilters.parameters['goAspect'], 'Function')}">
							<c:set var="aspectChecked" value="checked"/>
						</c:if>
						<tr><td><input type="checkbox" ${aspectChecked} value="Function" class="advanced-filter-goAspect" /></td><td>Molecular Function</td></tr>
						<c:set var="aspectChecked" value="" />
						<c:if test="${fn:contains(sessionScope.appliedFilters.parameters['goAspect'], 'Process')}">
							<c:set var="aspectChecked" value="checked"/>
						</c:if>
						<tr><td><input type="checkbox" ${aspectChecked} value="Process" class="advanced-filter-goAspect" /></td><td>Biological Process</td></tr>
						<c:set var="aspectChecked" value="" />
						<c:if test="${fn:contains(sessionScope.appliedFilters.parameters['goAspect'], 'Component')}">
							<c:set var="aspectChecked" value="checked"/>
						</c:if>				
						<tr><td><input type="checkbox" ${aspectChecked} value="Component" class="advanced-filter-goAspect" /></td><td>Cellular Component</td></tr>
					</table>
				</div>
			</div>
			<div id="evidenceTab" class="grid_9">
				<h4>Evidence</h4>
				A full description of evidence codes and their usage can be found <a href="http://www.geneontology.org/page/guide-go-evidence-codes" target="_blank">here</a>.
				<div class="clearfix"><p/></div>
				<div id="evidencesArea">
					<table id="evidenceTypes" style="width:98%">
						<tr><td><input type="checkbox" value="ECO:0000352" class="advanced-filter-ecoID" /></td><td colspan="2"><strong>ECO:0000352 (IMP,IGI,IPI,IDA,IEP,EXP,ISS,TAS,NAS,ND,IC,RCA,IBA,IBD,IKR,IRD,ISA,ISM,ISO,IGC) Manual All</strong></td></tr>
	                    <tr><td><input type="checkbox" value="ECO:0000269" class="advanced-filter-ecoID" /></td><td colspan="2"><strong>ECO:0000269 (IDA,IMP,IPI,IGI,IEP,EXP) Manual Experimental</strong></td></tr>						
						<c:forEach items="${sessionScope.evidenceTypes}" var="evidenceType">
							<c:set var="evidenceChecked" value="" />
							<c:forEach var="evidenceSelected" items="${sessionScope.appliedFilters.parameters['ecoID']}">
								<c:if test="${evidenceSelected == evidenceType.key}">
									<c:set var="evidenceChecked" value="checked"/>
								</c:if>
							</c:forEach>
							<tr><td><input type="checkbox" value="${evidenceType.key}" ${evidenceChecked} class="advanced-filter-ecoID" /></td><td colspan="2"><strong>${evidenceType.value}</strong></td></tr>				
						</c:forEach>
					</table>
				</div>
				<div class="clearfix"><p/></div>
				Enter a list of ECO <strong>names</strong> separated by comma:<br/>
				<textarea class="advanced-filter-ecoName" style="width:50%;height:50px"></textarea>
				<div class="clearfix"><p/></div>
				Enter a list of ECO <strong>identifiers</strong> (ECO:nnnnnnn), separated by space, comma or newline characters:
				<textarea class="advanced-filter-ecoID" style="width:50%;height:50px"></textarea>				
				<div class="clearfix"><p/></div>
				<table id="evidenceMatch" style="width:50%">					
					<tr><td><input type="radio" name="evidence-match" value="" class="advanced-filter-ecoAncestorsI" checked/></td><td>Include child terms</td></tr>
					<tr><td><input type="radio" name="evidence-match" value="" /></td><td>Exact match</td></tr>
				</table>
			</div>
			<div id="withTab" class="grid_9">
				<div class="clearfix"><p/></div>
				<div id="withArea">
					<table id="withDatabases" style="width:98%">			
						<c:forEach items="${sessionScope.withDBs}" var="withDB">
							<c:set var="assignedChecked" value="" />
								<c:forEach var="withSelected" items="${sessionScope.appliedFilters.parameters['with']}">
									<c:if test="${withSelected == withDB.key}">
										<c:set var="withChecked" value="checked"/>
									</c:if>
								</c:forEach>
								<tr><td><input type="checkbox" value="${withDB.key}*" ${withChecked} class="advanced-filter-with" /></td><td><strong>${withDB.key}:*</strong></td><td>${withDB.value}</td></tr>			
						</c:forEach>
					</table>
				</div>
			</div>
			<div id="assignedByTab" class="grid_9">
				<h4>Assigned by</h4>	
				Select annotation from:
				<div class="clearfix"><p/></div>
				<div id="assignedByArea">
					<table id="assignedByDatabases" style="width:98%">			
						<c:forEach items="${sessionScope.assignedByDBs}" var="assignedByDb">
							<c:set var="assignedChecked" value="" />
							<c:forEach var="assignedSelected" items="${sessionScope.appliedFilters.parameters['assignedBy']}">
								<c:if test="${assignedSelected == assignedByDb.key}">
									<c:set var="assignedChecked" value="checked"/>
								</c:if>
							</c:forEach>
							<tr><td><input type="checkbox" value="${assignedByDb.key}" ${assignedChecked} class="advanced-filter-assignedBy" /></td><td><strong>${assignedByDb.key}</strong></td><td>${assignedByDb.value}</td></tr>				
						</c:forEach>
					</table>
				</div>				
			</div>
		</div>
	</div>
</div>
<div id="advancedBottomButtons">
	<ul id="advancedFiltersButtons" class="action-buttons-grouped" style="float:right">				
		<li><a style="float: right" href="#" id="cancelAdvancedFiltersMenu" class="button" title="">Cancel</a></li>
		<li><a style="float: right" href="#" id="resetAdvancedFiltersOptions" class="button" title="">Reset</a></li>
		<li><a style="float: right" href="#" id="submit_advanced_filtering" title="" class="button">Go</a></li>				
	</ul>
</div>
<script>
	advancedFiltersButtons();
</script>	
</div>
</div>