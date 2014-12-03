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
<title>Ancestor chart &lt; QuickGO &lt; EMBL-EBI</title>
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
			<h2 class="page-title">Ancestor chart for ${termstodisplay}</h2>
		</div>
		<div class="mainContent grid_8">
			<p></p>
		</div>
		<div class="grid_2"></div>

		<div class="clearfix"></div>
		<!-- Main Content -->
		<div id="chartContent"
			class="mainContent grid_12">
				<div id="chartArea" style="display: block;text-align: center;">					
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