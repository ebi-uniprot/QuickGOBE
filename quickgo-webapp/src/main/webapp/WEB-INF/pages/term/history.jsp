<div id="term-history-section" style="display:none">
	<h3>
		<strong>Change Log</strong>
	</h3>

	<div id="termHistoryTabs">
		
		<c:set var="termchangesDisplay" value="display:none" />
		<c:set var="definitionchangesDisplay" value="display:none" />
		<c:set var="relationshipschangesDisplay" value="display:none" />		
		<c:set var="otherchangesDisplay" value="display:none" />		
		<c:set var="xrefschangesDisplay" value="display:none" />
		<c:set var="obsoletionschangesDisplay" value="display:none" />

		<ul>
			<li><a href="#allchanges">All changes</a></li>
			<c:if test="${fn:length(term.history.historyTerms) > 0}"> 
				<c:set var="termchangesDisplay" value="display:block"/>
				<li><a href="#termchanges">Term</a></li>
			</c:if>
			<c:if test="${fn:length(term.history.historyDefinitions) > 0}"> 
				<c:set var="definitionchangesDisplay" value="display:block"/>
				<li><a href="#definitionchanges">Definition/Synonyms</a></li>
			</c:if>
			<c:if test="${fn:length(term.history.historyRelations) > 0}"> 
				<c:set var="relationshipschangesDisplay" value="display:block"/>
				<li><a href="#relationshipschanges">Relationships</a></li>
			</c:if>
			<c:if test="${fn:length(term.history.historyOther) > 0}"> 
				<c:set var="otherchangesDisplay" value="display:block"/>
				<li><a href="#otherchanges">Other</a></li>
			</c:if>			
			<c:if test="${fn:length(term.history.historyXRefs) > 0}"> 
				<c:set var="xrefschangesDisplay" value="display:block"/>
				<li><a href="#xrefschanges">Cross-references</a></li>
			</c:if>
			<c:if test="${fn:length(term.history.historyObsoletions) > 0}"> 
				<c:set var="obsoletionschangesDisplay" value="display:block"/>
				<li><a href="#obsoletionschanges">Obsoletions</a></li>
			</c:if>
		</ul>

		<div id="allchanges">
			<table id="allChangesTable" class="two-colours">
				<tr>
					<th>Timestamp</th>
					<th>Action</th>
					<th>Category</th>
					<th>Detail</th>
				</tr>
				<c:forEach var="history" items="${term.history.historyAll}">
					<tr>
						<td>${history.timestamp}</td>
						<td>${history.actionString}</td>
						<td>${history.category}</td>
						<td>${history.text}</td>
					</tr>
				</c:forEach>
			</table>
		</div>
		<div id="termchanges" style="${termchangesDisplay}">
			<table id="termchangesTable" class="two-colours">
				<tr>
					<th>Timestamp</th>
					<th>Action</th>
					<th>Category</th>
					<th>Detail</th>
				</tr>
				<c:forEach var="history" items="${term.history.historyTerms}">
					<tr>
						<td>${history.timestamp}</td>
						<td>${history.actionString}</td>
						<td>${history.category}</td>
						<td>${history.text}</td>
					</tr>
				</c:forEach>
			</table>
		</div>
		<div id="definitionchanges" style="${definitionchangesDisplay}">
			<table id="definitionchangesTable" class="two-colours">
				<tr>
					<th>Timestamp</th>
					<th>Action</th>
					<th>Category</th>
					<th>Detail</th>
				</tr>
				<c:forEach var="history" items="${term.history.historyDefinitions}">
					<tr>
						<td>${history.timestamp}</td>
						<td>${history.actionString}</td>
						<td>${history.category}</td>
						<td>${history.text}</td>
					</tr>
				</c:forEach>
			</table>
		</div>
		<div id="relationshipschanges" style="${relationshipschangesDisplay}">
			<table id="relationshipschangesTable" class="two-colours">
				<tr>
					<th>Timestamp</th>
					<th>Action</th>
					<th>Category</th>
					<th>Detail</th>
				</tr>
				<c:forEach var="history" items="${term.history.historyRelations}">
					<tr>
						<td>${history.timestamp}</td>
						<td>${history.actionString}</td>
						<td>${history.category}</td>
						<td>${history.text}</td>
					</tr>
				</c:forEach>
			</table>
		</div>
		<div id="otherchanges" style="${otherchangesDisplay}">
			<table id="otherchangesTable" class="two-colours">
				<tr>
					<th>Timestamp</th>
					<th>Action</th>
					<th>Category</th>
					<th>Detail</th>
				</tr>
				<c:forEach var="history" items="${term.history.historyOther}">
					<tr>
						<td>${history.timestamp}</td>
						<td>${history.actionString}</td>
						<td>${history.category}</td>
						<td>${history.text}</td>
					</tr>
				</c:forEach>
			</table>
		</div>
		<div id="xrefschanges" style="${xrefschangesDisplay}">
			<table id="xrefschangesTable" class="two-colours">
				<tr>
					<th>Timestamp</th>
					<th>Action</th>
					<th>Category</th>
					<th>Detail</th>
				</tr>
				<c:forEach var="history" items="${term.history.historyXRefs}">
					<tr>
						<td>${history.timestamp}</td>
						<td>${history.actionString}</td>
						<td>${history.category}</td>
						<td>${history.text}</td>
					</tr>
				</c:forEach>
			</table>
		</div>
		<div id="obsoletionschanges" style="${obsoletionschangesDisplay}">
			<table id="obsoletionschangesTable" class="two-colours">
				<tr>
					<th>Timestamp</th>
					<th>Action</th>
					<th>Category</th>
					<th>Detail</th>
				</tr>
				<c:forEach var="history" items="${term.history.historyObsoletions}">
					<tr>
						<td>${history.timestamp}</td>
						<td>${history.actionString}</td>
						<td>${history.category}</td>
						<td>${history.text}</td>
					</tr>
				</c:forEach>
			</table>
		</div>
	</div>
	<script>
		// Tabs
		$("#termHistoryTabs").tabs();
	</script>
</div>