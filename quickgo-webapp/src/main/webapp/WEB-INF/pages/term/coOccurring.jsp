<div id="co-occurring-terms-section" style="display: none">
	<h3>
		<strong>Co-occurrence statistics</strong>
	</h3>

	<p>These tables show the number of times the term listed in the
		table has been co-annotated with ${term.id}. The terms are listed in
		descending order of number of times the term has been co-annotated.
		The table on the left is calculated using both electronic and
		manual-evidenced annotations, while the table on the right is
		calculated using only manual-evidenced annotations.</p>
	<div id="allCoOccurringStats" class="grid_6" style="overflow-x: auto">
		<c:choose>
			<c:when test="${fn:length(allCoOccurrenceStatsTerms) == 0}">
				<h5><element class='icon icon-generic' data-icon='l'> No co-occurring statistics for ${term.id} based on the entire annotation set</h5>
			</c:when>	
			<c:otherwise>
				<h5>Co-occurrence statistics for ${term.id} based on the entire annotation set</h5>
				<table id="allCoOccurringStatsTable" class="two-colours" style="font-size: 90%;">	
					<tr>
						<th>Compared term</th>
						<th>Aspect</th>
						<th>Name</th>
						<th>PR</th>
						<th>S%</th>
						<th>#Together</th>
						<th>#Compared</th>
					</tr>
					<c:set var="totalTogetherAllStats" value="0" />
					<c:set var="totalComparedAllStats" value="0" />
					<c:forEach var="allStat" items="${allCoOccurrenceStatsTerms}">
						<tr><td><a href="<c:url value="/"/>term/${allStat.comparedTerm}">${allStat.comparedTerm}</a></td><td>${allStat.aspect}</td><td>${allStat.name}</td><td>${allStat.probabilityRatio}</td><td>${allStat.probabilitySimilarityRatio}</td><td>${allStat.together}</td><td>${allStat.compared}</td></tr>
						<c:set var="totalTogetherAllStats" value="${totalTogetherAllStats + allStat.together}" />
						<c:set var="totalComparedAllStats" value="${totalComparedAllStats + allStat.compared}" />
					</c:forEach>
					<tr><td colspan="5"><strong>Totals</strong></td><td>${totalTogetherAllStats}</td><td>${totalComparedAllStats}</td></tr>
				</table>
			</c:otherwise>
		</c:choose>
	</div>
	<div id="nonIEACoOccurringStats" class="grid_6" style="overflow-x: auto">
		<c:choose>
			<c:when test="${fn:length(nonIEACOOccurrenceStatistics) == 0}">
				<h5><element class='icon icon-generic' data-icon='l'> No co-occurring statistics for ${term.id} based on non-IEA annotations</h5>
			</c:when>	
			<c:otherwise>
				<h5>Co-occurrence statistics for ${term.id} based on non-IEA annotations only</h5>
				<table id="nonIEACoOccurringStatsTable" class="two-colours" style="font-size: 90%;">	
					<tr>
						<th>Compared term</th>
						<th>Aspect</th>
						<th>Name</th>
						<th>PR</th>
						<th>S%</th>
						<th>#Together</th>
						<th>#Compared</th>
					</tr>
					<c:set var="totalTogetherNonIEAStats" value="0" />
					<c:set var="totalComparedNonIEAStats" value="0" />
					<c:forEach var="nonIEAStat" items="${nonIEACOOccurrenceStatistics}">
						<tr><td><a href="<c:url value="/"/>term/${nonIEAStat.comparedTerm}">${nonIEAStat.comparedTerm}</a></td><td>${nonIEAStat.aspect}</td><td>${nonIEAStat.name}</td><td>${nonIEAStat.probabilityRatio}</td><td>${nonIEAStat.probabilitySimilarityRatio}</td><td>${nonIEAStat.together}</td><td>${nonIEAStat.compared}</td></tr>
						<c:set var="totalTogetherNonIEAStats" value="${totalTogetherNonIEAStats + nonIEAStat.together}" />
						<c:set var="totalComparedNonIEAStats" value="${totalComparedNonIEAStats + nonIEAStat.compared}" />
					</c:forEach>
					<tr><td colspan="5"><strong>Totals</strong></td><td>${totalTogetherNonIEAStats}</td><td>${totalComparedNonIEAStats}</td></tr>
				</table>
			</c:otherwise>
		</c:choose>
	</div>
</div>