<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
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
<title>Term &lt; QuickGO &lt; EMBL-EBI</title>
<!-- Imports -->
<%@include file="/templates/imports.jsp"%>
<script src="<%= request.getContextPath() %>/js/terms.js"></script>
</head>

<body>
	<div id="content" class="container_12">
		<!--Header -->
		<%@include file="/templates/header.jsp"%>
		
		<div class="sidebar grid_9">
			<h2 class="page-title">${term.id} ${term.name}</h2>
		</div>
		<div class="mainContent grid_1">
			<p></p>
		</div>
		<div class="grid_2">
				<div id="basket">
					<a id="basket-list"
						class="closed basket-empty icon icon-static caret_white button"
						data-icon="b" href="#">Basket <span id="basket-count" style="display: inline;">${fn:length(sessionScope.basketTerms)}</span></a>				
				</div>
				<!-- Basket -->
				<%@include file="/templates/basket.jsp"%>

		</div>
		<div class="clearfix"></div>
		<!-- Sidebar -->
		<%@include file="/templates/term-sidebar.jsp"%>
		
		<!-- Main Content -->				
		<div class="mainContent grid_10">
			<c:if test="${isGO}">
				<ul class="action-buttons-grouped decorated" id="resultActionButtons">
					<div id="add-to-basket-div">	
						<c:if test="${not term.obsolete}">
							<c:set var="clicked" value="enabled" />						
							<c:if test="${sessionScope.basketTerms[term.id] != null}">
								<c:set var="clicked" value="disabled" />				
							</c:if>						
							<li>
								<a id="term_${term.id}" class="add-basket-item icon button ${clicked}" data-icon="b" title="Add term to basket">Add to Basket</a>						</li>						
						</c:if>
						<script>
							refreshBasketTerms();							
							addTermsToBasketFunctionality();
							showTermAnnotationsButton();
						</script>
					</div>
				</ul>
			</c:if>		
			<div style="display: block;" id="resultsArea">
	
			<!-- Check if it's obsolete -->	
			<c:if test="${term.obsolete}">
				<div class="alert"><element id="warning"
					class="icon icon-generic"
					data-icon="l" href="#"></element> <strong>This term is obsolete.</strong><br/>
					${term.comment}</div><br/>
			</c:if>
	
			<!-- Term information -->
			<%@include file="termInformation.jsp"%>
			<div class="clearfix"></div>
			<!-- Ancestors graph -->
			<%@include file="ancestorsgraph.jsp"%>
			<div class="clearfix"></div>
			<!-- Child Terms -->
			<%@include file="childTerms.jsp"%>
			<div class="clearfix"></div>			
			<!-- Co-occurring terms -->
			<%@include file="coOccurring.jsp"%>
			<div class="clearfix"></div>
			<!-- Change log -->
			<%@include file="history.jsp"%>			
			<!-- Ontology graphs -->
			<%@include file="/templates/ontologygraph.jsp"%>
			
			<!-- Check if there is any section specified after # . In that case, focus the page on that section-->
			<script>
				var section = window.location.hash;
				if(section != ''){
					section = section.replace("#","");
					var checkbox = section + "-checkbox";
					$("#" + checkbox).prop("checked", true);
					section = section + "-section";
					$("#" + section).show();
				}
				$('html, body').animate({
						scrollTop : $("#" + section).offset().top
				}, 1000);
			</script>
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