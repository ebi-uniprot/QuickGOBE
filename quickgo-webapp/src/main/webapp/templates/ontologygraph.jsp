<div id="ontology-graph-popup" class="lightbox" style="display: none;">	
	<div id="ontology-graph-content" style="overflow:auto">		
		<div id="ontology-graph-menu">
			<div alt="Close the graph menu">
				<a style="float: right" id="closeGraphPopUp" href="#" class="closeBox">x</a>	
			</div>
			<script>
				$('#closeGraphPopUp').click(function(event) {
					    // To avoid adding the "#" at the end of the URL
					    event.preventDefault();
			   		    $('#ontology-graph-popup').fadeOut('slow');
		  			    $('#ontology-graph-content').css("display","none");
				});
			</script>
			<div id="ancestors-graph-image-div">				
				<h5 style="text-align:center"><strong>${termGraphTitle}</strong></h5>
				<c:if test="${graphImageSrc != '' && graphImageSrc != null}">
					<input type="hidden" id="graphImageWidth" value="${graphImageWidth}" />
					<input type="hidden" id="graphImageHeight" value="${graphImageHeight}" />
					<img id="ontologyGraphImage" class="ancestorsGraphImage" alt="Graph image" usemap="#ontologygraphmap">
					<script>
						getGraphImage('<c:url value="/"/>${graphImageSrc}',false);
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
			</div>
		</div>
	</div>
</div>