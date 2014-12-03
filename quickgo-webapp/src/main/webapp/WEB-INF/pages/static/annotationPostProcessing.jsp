<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page session="true" %>
<!doctype html>
<!-- paulirish.com/2008/conditional-stylesheets-vs-css-hacks-answer-neither/ -->
<!--[if lt IE 7]> <html class="no-js ie6 oldie" lang="en"> <![endif]-->
<!--[if IE 7]>    <html class="no-js ie7 oldie" lang="en"> <![endif]-->
<!--[if IE 8]>    <html class="no-js ie8 oldie" lang="en"> <![endif]-->
<!-- Consider adding an manifest.appcache: h5bp.com/d/Offline -->
<!--[if gt IE 8]><!-->
<html xml:lang="en" xmlns="http://www.w3.org/1999/xhtml">
<!--<![endif]-->
<head>
<title>Automatic Annotation Pot-Processing by UniProt-GOA &lt; QuickGO &lt; EMBL-EBI</title>
<!-- Imports -->
<%@include file="/templates/imports.jsp"%>
<script src="<%= request.getContextPath() %>/js/annotations.js"></script>
</head>
<body>
	<div id="content" class="container_12">
		<!--Header -->
		<%@include file="/templates/header.jsp"%>
		
		<div class="sidebar grid_2">
			<h2 class="page-title"></h2>
		</div>
		<div class="mainContent grid_8">
			<p></p>
		</div>
		<div style="padding:50px">
			<h3>Introduction</h3>
			<p>The table below describes the actions that UniProt-GOA carries out on automatic annotations supplied by the Ensembl, InterPro and UniProt groups for the UniProtKB GO annotation set.
			Conservative changes to particular annotation subsets are made by UniProt-GOA when the annotation originally supplied by the automatic annotation pipeline is incorrect for a UniProtKB protein and cannot be easily fixed by the annotation-contributing group without an unnecessarily high loss of correct annotations.
			Affected annotations are either individually filtered out of the UniProt-GOA annotation set or are conservatively changed to use an equivalent, correct GO term.
			UniProt-GOA only carries out such changes to automatic annotations sets when it is the group primarily responsible for supplying the data to the GO Consortium, and where the change to the annotation set at source is considered inappropriate. In many cases UniProt-GOA is best placed to make these changes for the UniProt annotations as it is able to consider taxonomic correctness of the annotated GO terms (e.g., for InterPro2GO annotations) or specific GO ontology requirements.
			All automatic annotations that are transformed by the UniProt-GOA processing will use a GO_REF reference that indicates to the user that such changes have occurred and which points users to this page. All of the changes described in the table below have been agreed to by the automatic annotation groups.</p> 
			<div class="clearfix"><p></p></div>
			<h2>The currently active post-processing rules</h2>
			<table id="ieaTable" class="two-colours" style="font-size:12px">
					<tr>
						<th>Original GO ID</th>
						<th>Original GO Term Name</th>
						<th>Cleanup Action</th>
						<th>Affected Taxonomic Group</th>
						<th>Substituted GO ID</th>
						<th>Substituted GO Term Name</th>
						<th>Curator Notes</th>
						<th>Taxon Rule</th>
						<th>Ancestor GO ID</th>
						<th>Ancestor GO Term Name</th>
						<th>Relationship</th>
						<th>Taxon Name</th>
					<tr>
				<c:forEach var="postProcessing" items="${postProcessingRules}">
					<tr>
						<td><a href="<c:url value="/"/>term/${postProcessing.originalGoId}">${postProcessing.originalGoId}</a></td>
						<td>${postProcessing.originalTerm}</td>
						<td>${postProcessing.cleanupAction}</td>
						<td>${postProcessing.affectedTaxGroup}</td>
						<td><a href="<c:url value="/"/>term/${postProcessing.substitutedGoId}">${postProcessing.substitutedGoId}</a></td>
						<td>${postProcessing.substitutedTerm}</td>
						<td>${postProcessing.curatorNotes}</td>
						<td>${postProcessing.ruleId}</td>
						<td><a href="<c:url value="/"/>term/${postProcessing.ancestorGoId}">${postProcessing.ancestorGoId}</a></td>
						<td>${postProcessing.ancestorTerm}</td>
						<td>${postProcessing.relationship}</td>
						<td>${postProcessing.taxonName}</td>
					</tr>
				</c:forEach>
			</table>	
			
		</div>
	</div>
	<!-- Footer -->
	<%@include file="/templates/footer.jsp"%>
</body>
</html>