<div id="slimming-graph-popup" class="lightbox" style="display: none;">
	<div id="slimming-graph-popup-content" class="container_12">
	<div alt="Close slimming terms menu" class="grid_12">
		<a style="float: right" id="closeSlimmingGraphMenu" href="#" class="closeBox">x</a>	
	</div>
		<div id="left_slimming_menu" class="grid_6" style="height:90%">
			<h5>Selected terms<span style="float:right;font-size:12px"></h5>
			<div id="selected_graph_slimming_terms" style="height: 600px;overflow:auto;white-space: nowrap;">
				<h3>Biological Process Terms<span style="float:right;font-size:12px"></span></h3>
				<div id="bp_graph_section">
					<ul class="no-li-symbol">
						<c:forEach var="slimmingTerm" items="${sessionScope.bp_slimmingTerms}">
							<c:set var="termchecked" value=""/>
							<c:set var="color" value="#B5B5B5"/>
							<c:if test="${fn:contains(sessionScope.activeSlimmingGraphTerms, slimmingTerm)}">
								<c:set var="termchecked" value="checked"/>
								<c:set var="color" value=""/>					
							</c:if>
							<li><input ${termchecked} id="${slimmingTerm.key}" type="checkbox" name="final_terms_slimming" value="${slimmingTerm.key}"/><label for="${slimmingTerm.key}" style="color:${color}"> ${slimmingTerm.key} ${slimmingTerm.value}</label></li>

						</c:forEach>
					</ul>
					<script>
						activeInactiveGraphEvent();
					</script>
				</div>
				<h3>Molecular Function Terms<span style="float:right;font-size:12px"></h3>
				<div id="mf_graph_section">
					<ul class="no-li-symbol">
						<c:forEach var="slimmingTerm" items="${sessionScope.mf_slimmingTerms}">
							<c:set var="termchecked" value=""/>
							<c:set var="color" value="#B5B5B5"/>
							<c:if test="${fn:contains(sessionScope.activeSlimmingGraphTerms, slimmingTerm)}">
								<c:set var="termchecked" value="checked"/>
								<c:set var="color" value=""/>					
							</c:if>
							<li><input ${termchecked} id="${slimmingTerm.key}" type="checkbox" name="final_terms_slimming" value="${slimmingTerm.key}"/><label for="${slimmingTerm.key}" style="color:${color}"> ${slimmingTerm.key} ${slimmingTerm.value}</label></li>

						</c:forEach>
					</ul>
					<script>
						activeInactiveGraphEvent();
					</script>
				</div>
				<h3>Cellular component Terms<span style="float:right;font-size:12px"></h3>
				<div id="cc_graph_section">
					<ul class="no-li-symbol">
						<c:forEach var="slimmingTerm" items="${sessionScope.cc_slimmingTerms}">
							<c:set var="termchecked" value=""/>
							<c:set var="color" value="#B5B5B5"/>
							<c:if test="${fn:contains(sessionScope.activeSlimmingGraphTerms, slimmingTerm)}">
								<c:set var="termchecked" value="checked"/>
								<c:set var="color" value=""/>
							</c:if>
							<li><input ${termchecked} id="${slimmingTerm.key}" type="checkbox" name="final_terms_slimming" value="${slimmingTerm.key}"/><label for="${slimmingTerm.key}" style="color:${color}"> ${slimmingTerm.key} ${slimmingTerm.value}</label></li>
						</c:forEach>
					</ul>
					<script>
						activeInactiveGraphEvent();
					</script>
				</div>				
			</div>			
		</div>
		<div style="height: 90%;" class="grid_6" id="right_slimming_menu">		
			<div id="slimming-ancestors-graph-image-div">			
			<c:choose>
				<c:when test="${graphImageSrc == null}">
					<h4>No term selected</h4>
				</c:when>	
				<c:otherwise>							
					<h5 style="text-align:center"><strong>${termGraphTitle}</strong></h5>
					<c:if test="${graphImageSrc != '' && graphImageSrc != null}">
						<input type="hidden" id="graphImageWidth" value="${graphImageWidth}" />
						<input type="hidden" id="graphImageHeight" value="${graphImageHeight}" />
						<img id="slimmingOntologyGraphImage" class="ancestorsGraphImage" alt="Loading..." usemap="#ontologygraphmap">
						<script>
							getSlimmingGraphImage('<c:url value="/"/>${graphImageSrc}',false);
						</script>
						<map name="ontologygraphmap">
							<c:if test="${not fn:containsIgnoreCase(termGraphTitle, 'ECO:')}">
								<c:forEach var="termNode" items="${termsNodes}">
									<area shape="termArea"
										coords="${termNode.left},${termNode.top},${termNode.right},${termNode.bottom}"
										href="<c:url value="/"/>term/${termNode.id}"
										alt="Term ${termNode.id}"
										title="${termNode.id} - Click for more information"
										target="_blank"/>
								</c:forEach>
							</c:if>
							<c:forEach var="legendNode" items="${legendNodes}">
									<area shape="termArea"
										id="${legendNode.topic}"
										class="relationHelp"
										coords="${legendNode.left},${legendNode.top},${legendNode.right},${legendNode.bottom}"
										/>
							</c:forEach>
							<script>
								// Create help tooltip for each relation
								$('.relationHelp').each(function() {
									var id = $(this).attr('id');
									formattedTooltip(id);
								});
							</script>
						</map>
					</c:if>
				</c:otherwise>
			</c:choose>
			</div>	
		</div>
	</div>
</div>