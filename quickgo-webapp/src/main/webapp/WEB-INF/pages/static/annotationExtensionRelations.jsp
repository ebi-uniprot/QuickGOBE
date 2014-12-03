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
<title>Visualisation of Annotation Extension Relations &lt; QuickGO &lt; EMBL-EBI</title>
<!-- Imports -->
<%@include file="/templates/imports.jsp"%>
<script	src="<%=request.getContextPath()%>/js/cytoscape/min/AC_OETags.min.js"></script>
<script	src="<%=request.getContextPath()%>/js/cytoscape/min/cytoscapeweb.min.js"></script>
<script	src="<%=request.getContextPath()%>/js/cytoscape/min/json2.min.js"></script>
<script	src="<%=request.getContextPath()%>/js/annExtRelations.js"></script>

</head>

<body>
	<div id="content" class="container_12">
		<!--Header -->
		<%@include file="/templates/header.jsp"%>

		<div class="sidebar grid_8">
			<h2 class="page-title">Visualisation of Annotation Extension Relations</h2>
		</div>
		<div class="mainContent grid_8">
			<p></p>
		</div>
		<div class="grid_2"></div>

		<div class="clearfix"></div>
		<!-- Main Content -->
		<div id="annotationExtensionRelationsContent"
			class="mainContent grid_12">

				<div id="annExtRelGraphArea" style="display: block;">
					
					<script type="text/javascript">
					$(document).ready(
								function() {
									$("#cytoscapeweb").html('Retrieving graph data...');
									$('.window .close').click(function(e) {
										//Cancel the link behavior
										e.preventDefault();
										$('.window').hide();
									});

									drawOntology(${data});
					});			
					</script>

					<div id="cytoscapeweb">(Cytoscape placeholder)</div>
					<div id="boxes">
						<div id="dialog" class="window">
							<a style="float: right" id="closeAnnExtRel" href="#" class="closeBox" onclick="$('#dialog').fadeOut('slow')">x</a>
							<span id="nodetitle" style="font-weight:bold">(id)</span>
							<span id="nodenote"></span>
						</div>
					</div>
				</div>
		</div>
		<div class="clearfix"></div>
	</div>
	<!-- Footer -->
	<%@include file="/templates/footer.jsp"%>


	<!-- JavaScript at the bottom for fast page loading -->

	<!-- Grab Google CDN's jQuery, with a protocol relative URL; fall back to local if offline -->
	<!--
  <script src="//ajax.googleapis.com/ajax/libs/jquery/1.6.2/jquery.min.js"></script>
  <script>window.jQuery || document.write('<script src="../js/libs/jquery-1.6.2.min.js"><\/script>')</script>
  -->


	<!-- Your custom JavaScript file scan go here... change names accordingly -->
	<!--
  <script defer="defer" src="//www.ebi.ac.uk/web_guidelines/js/plugins.js"></script>
  <script defer="defer" src="//www.ebi.ac.uk/web_guidelines/js/script.js"></script>
  -->

	<!-- end scripts-->

	<!-- Google Analytics details... -->
	<!-- Change UA-XXXXX-X to be your site's ID -->
	<!--
  <script>
    window._gaq = [['_setAccount','UAXXXXXXXX1'],['_trackPageview'],['_trackPageLoadTime']];
    Modernizr.load({
      load: ('https:' == location.protocol ? '//ssl' : '//www') + '.google-analytics.com/ga.js'
    });
  </script>
  -->


	<!-- Prompt IE 6 users to install Chrome Frame. Remove this if you want to support IE 6.
       chromium.org/developers/how-tos/chrome-frame-getting-started -->
	<!--[if lt IE 7 ]>
    <script src="//ajax.googleapis.com/ajax/libs/chrome-frame/1.0.3/CFInstall.min.js"></script>
    <script>window.attachEvent('onload',function(){CFInstall.check({mode:'overlay'})})</script>
  <![endif]-->

</body>
</html>