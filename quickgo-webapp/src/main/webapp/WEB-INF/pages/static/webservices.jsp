<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page session="true"%>
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
<title>QuickGO Web Services &lt; QuickGO &lt; EMBL-EBI</title>
<!-- Imports -->
<%@include file="/templates/imports.jsp"%>
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
		<div style="padding: 50px">
			<p>
			<h4>GO term/ECO term/Protein lookup</h4>
			http://www.ebi.ac.uk/QuickGO-Beta/ws/lookup?scope=[go|eco|protein]&id=[go_id]
			</p>
			<p>
			<h4>GO term/ECO term/Protein search</h4>
			http://www.ebi.ac.uk/QuickGO-Beta/ws/search?query=[search_term]&scope=[go,eco,protein]&limit=[max_results]
			<p>
			<h4>Get valid annotation extension relations</h4>
			http://www.ebi.ac.uk/QuickGO-Beta/ws/validate?type=ann_ext&action=get_relations&id=[go_id]
			</p>
			<p>
			<h4>Validate annotation extension</h4>
			http://www.ebi.ac.uk/QuickGO-Beta/ws/validate?type=ann_ext&action=validate_relation&id=[go_id]&candidate=[extension]
			</p>
			<p>
			<h4>Get co-occurring GO terms</h4>
			http://www.ebi.ac.uk/QuickGO-Beta/ws/statistics?id=[go_id]&threshold=[threshold]&limit=[limit]
			</p>
			<p>
			<h4>Get all annotation blacklist entries relating to a
				particular taxon</h4>
			http://www.ebi.ac.uk/QuickGO-Beta/ws/validate?type=taxon&taxon_id=[taxon_id]&action=get_blacklist
			</p>
			<p>
			<h4>Get a list of all taxon constraint rules</h4>
			http://www.ebi.ac.uk/QuickGO-Beta/ws/validate?type=taxon&action=get_constraints
			</p>
		</div>
		<!-- Footer -->
		<%@include file="/templates/footer.jsp"%>
</body>
</html>