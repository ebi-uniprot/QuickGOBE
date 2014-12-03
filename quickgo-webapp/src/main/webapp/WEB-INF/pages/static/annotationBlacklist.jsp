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
<title>The GOA Annotation Blacklist &lt; QuickGO &lt; EMBL-EBI</title>
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
			<h3>Scope</h3>
			<p>This method aims to correct annotations to UniProtKB accessions, from incorrect annotations inferred from electronic annotation (IEA) methods that are supplied by the UniProt-GOA project to the GO Consortium.</p>

			<h3>The need for a blacklist</h3>
			<p>The process of changing incorrect electronically inferred annotations is not instantaneous and in some cases these annotations are displayed for a while before the prediction method can be corrected at source.
			In some cases it is not possible for the IEA source to remove the annotation (see note 1, below).
			Example: False positives
			In the case where proteins are grouped together by a sequence model and the model hits a false positive, the 'false positive' protein would receive incorrect annotation(s).</p>

			<h3>Contents</h3>
			<p>This list contains UniProtKB protein accessions associated with specific GO terms that curators feel incorrectly describe a protein's function, associated process or subcellular location. This list is split into three sections:</p>
			<h4>1. Negative associations curated from IEA pipeline review</h4>
			<p>A list of protein-GO term associations that have been observed as being incorrect from the review of annotations obtained from an IEA pipeline. These associations are often added when a curator has observed that a function/process term is being over-predicted to a group of proteins.
			These associations are not present in the UniProt-GOA annotation set, as there is no published sequence or experimental data to specifically negate these associations. However, curator knowledge has been used to assert these undesirable associations.
			Note:
			Some electronic annotation sources have a cut-off criterion such that if the annotation is true for 95% of the proteins in the set, it is considered relevant for GO mappings to be added to the FULL protein set.
			This blacklist endeavours to highlight those proteins that may have been caught out in the 5% to provide users with as correct a set of protein annotations as possible.</p>
			<h4>2. Negative associations obtained from NOT-qualified GO annotations (annotation set updated weekly)</h4>
			<p>These protein-GO term associations have been created when a protein has been expected to have a particular function, associated process or location, but specific experimental or sequence-based evidence has been curated to demonstrate the contrary. These NOT-qualified GO annotations are obtained from the current UniProt-GOA annotation set and are updated weekly. GO Consortium guidance on curator usage of the NOT-qualifier is available here: http://www.geneontology.org/GO.annotation.conventions.shtml#not</p>
			<h4>3. Negative associations obtained from UniProt COMMENT_CAUTION entries (annotation set updated weekly)</h4>
			<p>These protein-GO term associations have been created when a 'caution' comment in the UniProt entry indicates that a protein does not have the expected function, process or location. These comments are added manually by UniProt curators after reviewing the sequence or literature.</p>
			<h3>Custodian of the blacklist</h3>
			<p>The <a href="http://www.ebi.ac.uk/GOA" target="_blank">UniProt-GOA</a> project is responsible for maintaining the annotation blacklist for annotation sources originating from this group.</p>
			<div class="clearfix"><p></p></div>
			<h2>The currently active annotation blacklist</h2>

			<div id="blacklist-tabs">
				<ul>
					<li><a href="#iea">Curated from IEA pipeline review</a></li>
					<li><a href="#notqualified">Obtained from NOT-qualified GO annotations</a></li>
					<li><a href="#uniprotcc">Obtained from UniProt COMMENT_CAUTION entries</a></li>
				</ul>
				<div id="iea">
					<table id="ieaTable" class="two-colours" style="font-size:12px">
						<tr>
							<th>Gene Product ID</th>
							<th>GO Identifier</th>
							<th>Reason</th>
							<th>Rule/Method ID</th>
						<tr>
						<c:forEach var="blacklistentry" items="${ieaReview}">
							<tr>
								<td><a href="#" onclick="getAssociatedAnnotations('dbObjectID','${blacklistentry.proteinAc}')">${blacklistentry.proteinAc}</a></td>
								<td><a href="<c:url value="/"/>term/${blacklistentry.goId}">${blacklistentry.goId}</a></td>
								<td>${blacklistentry.reason}</td>
								<td>${blacklistentry.methodId}</td>
							</tr>
						</c:forEach>
					</table>	
				</div>
				<div id="notqualified">
					<table id="notQualifiedTable" class="two-colours" style="font-size:12px">
						<tr>
							<th>Gene Product ID</th>
							<th>GO Identifier</th>
							<th>Reason</th>
						<tr>
						<c:forEach var="blacklistentry" items="${notQualified}">
							<tr>
								<td>${blacklistentry.proteinAc}</td>
								<td><a href="<c:url value="/"/>term/${blacklistentry.goId}">${blacklistentry.goId}</a></td>
								<td>${blacklistentry.reason}</td>
							</tr>
						</c:forEach>
					</table>
				</div>
				<div id="uniprotcc">
					<table id="uniprotCCTable" class="two-colours" style="font-size:12px">
						<tr>
							<th>Gene Product ID</th>
							<th>GO Identifier</th>
							<th>Reason</th>
						<tr>		
						<c:forEach var="blacklistentry" items="${uniprotCaution}">
							<tr>
								<td>${blacklistentry.proteinAc}</td>
								<td><a href="<c:url value="/"/>term/${blacklistentry.goId}">${blacklistentry.goId}</a></td>
								<td>${blacklistentry.reason}</td>
							</tr>
						</c:forEach>
					</table>
				</div>
			</div>
			<script>
				$(function() {
				$( "#blacklist-tabs" ).tabs();
				});
			</script>
		</div>
	</div>
	<!-- Footer -->
	<%@include file="/templates/footer.jsp"%>
</body>
</html>