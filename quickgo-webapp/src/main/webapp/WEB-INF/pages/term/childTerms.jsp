<div id="child-terms-section" style="display:none">
	<h3><strong>Child Terms</strong></h3>
	<c:choose>
		<c:when test="${fn:length(childTermsRelations) == 0}">
		Term ${term.id} has no children
		</c:when>
		<c:otherwise>
			<p>This table lists all terms that are direct descendants (child terms) of ${term.id}:</p>
			<table id="childTerms" class="two-colours">
			<tr><th>Relationship to ${term.id}</th><th>Child Term</th><th>Child Term Name</th></tr>
			<c:forEach var="childRelation" items="${childTermsRelations}">
				<tr><td>${childRelation.typeof.description}</td><td><a href="<c:url value="/"/>term/${childRelation.child.id}">${childRelation.child.id}</a></td><td>${childRelation.child.name}</td></tr>
			</c:forEach>
			</table>
		</c:otherwise>
	</c:choose>
</div>