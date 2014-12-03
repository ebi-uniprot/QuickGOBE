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
			
			<h3>Dataset Statistics</h3>

			<h4>GO Term Information</h4>
			<p>QuickGO contains information for 41157 GO terms, as defined in the version of the GO definition file which was downloaded at: 2012-09-27 02:17.
			You can find a history of changes to the ontology <a href=<c:url value="/dataset"/>/goTermHistory>here</a>.</p>
			
			<h4>Annotation Updates</h4>			 
			
			<table id="assignedByCount" class="two-colours" style="font-size:12px;width:25%">
					<tr>
						<th>Assigned By</th>
						<th>Annotations</th>
					<tr>
					<c:forEach var="assignedByCount" items="${assignedByCount}">
						<tr>
							<td>${assignedByCount.name}</td>
							<td><a href="#" onclick="getAssociatedAnnotations('assignedBy','${assignedByCount.name}')">${assignedByCount.count}</a></td>
						</tr>
					</c:forEach>
			</table>		
			
			<h3>How the QuickGO Dataset is Generated</h3>

			<h4>Annotation Blacklist</h4>
			<p>This list aims to correct annotations to UniProtKB accessions from incorrect annotations inferred from electronic annotation (IEA) methods that are supplied by the UniProt-GOA project to the GO Consortium. Details of the annotation blacklist can be found <a href=<c:url value="/dataset"/>/annotationBlacklist>here</a>.</p>

			<h4>Annotation Post-processing</h4>
			<p>UniProt-GOA will be displaying some electronic annotations that have been subject to minor post-processing to ensure taxonomic correctness of annotated GO terms. Details of the annotation post-processing can be found  <a href=<c:url value="/dataset"/>/annotationPostProcessing>here</a>.</p>

			<h4>GO Consortium Annotations</h4>
			<p>GO annotations from member groups of the GO Consortium may also be viewed in the AmiGO browser. However, the display of annotations between QuickGO and AmiGO can vary, due to the different update frequencies of the two tools plus the fact that QuickGO provides electronic (IEA-coded) annotations for all species in UniProtKB with GO annotations, while AmiGO only supplies electronic annotations for 12 model organism species. Additionally, it should be noted that UniProt-GOA only integrates manual annotations from external groups, and external annotations can only be incorporated where external sequence identifiers can be mapped to corresponding UniProtKB accession numbers and the GO identifier associated has not been made secondary.</p>

			<h4>PDB Gene Associations</h4>
			<p>QuickGO does not contain the GO annotations to PDB entries from the PDB Gene Association File since the entity annotated in this file is a protein structure rather than a protein. The PDB Gene Association File can be downloaded here</p> 

			<h4>Taxon Constraint Rules</h4>									
			<p>Details of the taxon constraint rules can be found  <a href=<c:url value="/dataset"/>/taxonConstraints>here</a>.</p>
									
			<h3>How to cite UniProt-GOA</h3>
			
			<p><a ref="http://www.ebi.ac.uk/GOA/publications" target="_blank">Cite UniProt-GOA</a></p>			
		</div>
	</div>
	<!-- Footer -->
	<%@include file="/templates/footer.jsp"%>
</body>
</html>