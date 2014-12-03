<div id="ancestor-chart-section" style="display:none">
	<h3><strong>Ancestors Chart</strong></h3>	
	<c:if test="${termGraphImageSrc != '' && termGraphImageSrc != null}">
		<input type="hidden" id="termGraphImageWidth" value="${graphImageWidth}" />
		<input type="hidden" id="termGraphImageHeight" value="${graphImageHeight}" />
		<img id="ancestorsGraphImage" class="ancestorsGraphImage" alt="Graph image" usemap="#ancestorsgraphmap" style="max-width:none">
		<script>
			getGraphImage('<c:url value="/"/>${termGraphImageSrc}',true);
		</script>
		
		<map name="ancestorsgraphmap">
			<c:forEach var="termTermNode" items="${termTermsNodes}">
				<area shape="termArea"
					coords="${termTermNode.left},${termTermNode.top},${termTermNode.right},${termTermNode.bottom}"
					href="<c:url value="/"/>term/${termTermNode.id}"
					alt="Term ${termTermNode.id}"
					title="${termTermNode.id} - Click for more information" target="_blank">
			</c:forEach>
			<c:forEach var="termLegendNode" items="${termLegendNodes}">
				<area shape="termArea"
					id="${termLegendNode.topic}"
					class="relationHelp"
					coords="${termLegendNode.left},${termLegendNode.top},${termLegendNode.right},${termLegendNode.bottom}"/>
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