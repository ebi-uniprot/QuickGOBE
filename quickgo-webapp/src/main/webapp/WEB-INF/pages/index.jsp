<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!doctype html>
<!-- paulirish.com/2008/conditional-stylesheets-vs-css-hacks-answer-neither/ -->
<!--[if lt IE 7]> <html class="no-js ie6 oldie" lang="en"> <![endif]-->
<!--[if IE 7]>    <html class="no-js ie7 oldie" lang="en"> <![endif]-->
<!--[if IE 8]>    <html class="no-js ie8 oldie" lang="en"> <![endif]-->
<!-- Consider adding an manifest.appcache: h5bp.com/d/Offline -->
<!--[if gt IE 8]><!-->
<html class="no-js" lang="en">
<!--<![endif]-->

<head>
<title>QuickGO &lt; EMBL-EBI</title>
<!-- Imports -->
<%@include file="/templates/imports.jsp"%>
<script src="<%= request.getContextPath() %>/js/annotations.js"></script>
</head>

<body>
	<div id="content" class="container_12">
		<!--Header -->
		<%@include file="/templates/header.jsp"%>
		<div class="sidebar grid_2">
			<p></p>
		</div>
		<div class="mainContent grid_8">
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
		<!-- Main Content -->
		<div class="mainContent grid_12">		
			<!-- Bot-friendly -->
			<div style="display:none">
				<a href="terms/P">Biological Process</a>
				<a href="terms/F">Molecular Function</a>
				<a href="terms/C">Cellular Component</a>
			</div>
		
			<div class="grid_10"><p id="intro_text">QuickGO is provided by the UniProt-GOA project at the EBI and incorporates annotations from the GO consortium and other specialist annotation groups</p></div>
			<div class="clearfix"><p></p></div>
			<div class="grid_5">
				<p><a id="start" class="closed icon icon-generic button main_button" data-icon="D"
						href="${pageContext.request.contextPath}/annotation">						
						<span style="font-size:20px">Start here</span><br/><span style="font-size:13px">Use filters to retrieve sets of GO annotations</span></a>
				</p>								
				<p>
				<a id="slims" class="closed icon icon-functional button main_button" data-icon="h"
										href="#">						
										<span style="font-size:20px">Investigate GO slims</span><br/><span style="font-size:13px">Create a broad functional overview for a set of proteins</span></a>					
				</p>				
				<p>
				<a id="history" class="closed icon icon-functional button main_button" data-icon="b"
					href="#" onclick="alert('This feature is not yet implemented')">						
						<span style="font-size:20px">History</span><br/><span style="font-size:13px">View changes to the GO</span></a>
				</p>				
			</div>
			<div id="groups_image">
				<img src="<%= request.getContextPath() %>/images/homepageimage.png" width="547" height="313" border="0" usemap="#Map" />
				<c:set var="req" value="${pageContext.request}" />
				<c:set var="baseURL" value="${req.scheme}://${req.serverName}:${req.serverPort}${req.contextPath}" />
				<map name="Map" id="Map">
				  <area shape="rect" coords="137,11,205,40" href="${baseURL}/annotation?q=assignedBy:HGNC" title="Display annotations from 'HGNC'" />
				  <area shape="rect" coords="216,9,277,40" href="${baseURL}/annotation?q=assignedBy:IntAct" title="Display annotations from 'IntAct'" />
				  <area shape="rect" coords="284,10,327,44" href="${baseURL}/annotation?q=assignedBy:TAIR" title="Display annotations from 'TAIR'" />
				  <area shape="rect" coords="337,24,425,48" href="${baseURL}/annotation?q={assignedBy:EnsemblFungi,EnsemblPlants/Gramene}" title="Display annotations from 'Ensembl'" />
				  <area shape="rect" coords="77,44,158,71" href="${baseURL}/annotation?q=assignedBy:ZFIN" title="Display annotations from 'ZFIN'" />
				  <area shape="rect" coords="186,48,301,74" href="${baseURL}/annotation?q=assignedBy:InterPro" title="Display annotations from 'InterPro'" />
				  <area shape="rect" coords="331,52,395,90" href="${baseURL}/annotation?q=assignedBy:ParkinsonsUK-UCL" title="Display annotations from 'ParkinsonsUK-UCL'" />
				  <area shape="rect" coords="398,53,477,74" href="${baseURL}/annotation?q=assignedBy:FlyBase" title="Display annotations from 'FlyBase'" />
				  <area shape="rect" coords="38,75,110,102" href="${baseURL}/annotation?q=assignedBy:GR" title="Display annotations from 'GR'" />
				  <area shape="rect" coords="128,78,240,107" href="${baseURL}/annotation?q=assignedBy:RefGenome" title="Display annotations from 'RefGenome'" />
				  <area shape="rect" coords="244,82,328,114" href="${baseURL}/annotation?q=assignedBy:Roslin_Institute" title="Display annotations from 'Roslin_Institute'" />
				  <area shape="rect" coords="356,100,420,129" href="${baseURL}/annotation?q=assignedBy:RGD" title="Display annotations from 'RGD'" />
				  <area shape="rect" coords="423,84,503,115" href="${baseURL}/annotation?q=assignedBy:CGD" title="Display annotations from 'CGD'" />
				  <area shape="rect" coords="12,113,93,138" href="${baseURL}/annotation?q=assignedBy:Ensembl" title="Display annotations from 'Ensembl'" />
				  <area shape="rect" coords="100,113,171,146" href="${baseURL}/annotation?q=assignedBy:UniProt" title="Display annotations from 'UniProt'" />
				  <area shape="circle" coords="193,136,19" href="${baseURL}/annotation?q=assignedBy:PseudoCAP" title="Display annotations from 'PseudoCAP'" />
				  <area shape="rect" coords="225,128,271,157" href="${baseURL}/annotation?q=assignedBy:SGD" title="Display annotations from 'SGD'" />
				  <area shape="rect" coords="284,121,352,166" href="${baseURL}/annotation?q={assignedBy:PAMGO_GAT,PAMGO_MGG}" title="Display annotations from 'PAMGO'" />
				  <area shape="rect" coords="356,136,438,175" href="${baseURL}/annotation?q=assignedBy:MTBBASE" title="Display annotations from 'MTBBASE'" />
				  <area shape="rect" coords="449,119,520,147" href="${baseURL}/annotation?q={assignedBy:EcoCyc,EcoliWiki}" title="Display annotations from 'EcoCy and EcoliWiki'" />
				  <area shape="rect" coords="456,153,541,178" href="${baseURL}/annotation?q=assignedBy:PomBase" title="Display annotations from 'PomBase'" />
				  <area shape="rect" coords="9,149,97,175" href="${baseURL}/annotation?q=assignedBy:MENGO" title="Display annotations from 'MENGO'" />
				  <area shape="rect" coords="107,154,160,195" href="${baseURL}/annotation?q=assignedBy:CGD" title="Display annotations from 'CGD'" />
				  <area shape="rect" coords="188,167,297,193" href="${baseURL}/annotation?q=assignedBy:GO_Central" title="Display annotations from 'GO_Central'" />
				  <area shape="rect" coords="32,183,82,217" href="${baseURL}/annotation?q=assignedBy:BHF-UCL" title="Display annotations from 'BHF-UCL'" />
				  <area shape="rect" coords="127,203,181,235" href="${baseURL}/annotation?q=assignedBy:dictyBase" title="Display annotations from 'dictyBase'" />
				  <area shape="rect" coords="194,201,343,229" href="${baseURL}/annotation?q={assignedBy: GeneDB_Lmajor,GeneDB_Pfalciparum,GeneDB_Tbrucei}" title="Display annotations from 'GeneDB'" />
				  <area shape="rect" coords="313,176,430,197" href="${baseURL}/annotation?q=assignedBy:WormBase" title="Display annotations from 'WormBase'" />
				  <area shape="rect" coords="436,190,531,218" href="${baseURL}/annotation?q=assignedBy:MTBBASE" title="Display annotations from 'MTBBASE'" />
				  <area shape="rect" coords="56,223,116,257" href="${baseURL}/annotation?q=assignedBy:MGI" title="Display annotations from 'MGI'" />
				  <area shape="rect" coords="172,242,262,270" href="${baseURL}/annotation?q=assignedBy:DFLAT" title="Display annotations from 'DFLAT'" />
				  <area shape="rect" coords="265,232,353,259" href="${baseURL}/annotation?q=assignedBy:LIFEdb" title="Display annotations from 'LIFEdb'" />
				  <area shape="rect" coords="357,204,399,247" href="${baseURL}/annotation?q=assignedBy:HPA" title="Display annotations from 'HPA'" />
				  <area shape="rect" coords="414,221,493,247" href="${baseURL}/annotation?q=assignedBy:CACAO" title="Display annotations from 'CACAO'" />
				  <area shape="rect" coords="90,261,171,288" href="${baseURL}/annotation?q=assignedBy:JCVI" title="Display annotations from 'JCVI'" />
				  <area shape="rect" coords="175,279,264,304" href="${baseURL}/annotation?q=assignedBy:SYSCILIA_CCNET" title="Display annotations from 'SYSCILIA_CCNET'" />
				  <area shape="rect" coords="276,271,350,299" href="${baseURL}/annotation?q=assignedBy:AgBase" title="Display annotations from 'AgBase'" />
				  <area shape="rect" coords="356,249,444,285" href="${baseURL}/annotation?q=assignedBy:Reactome" title="Display annotations from 'Reactome'" />
				</map>
				
			</div> 
		</div>
		<div class="clearfix"></div>
	</div>
	<!-- Footer -->
	<%@include file="/templates/footer.jsp"%>

	<!-- Sliming popup -->
	<%@include file="/templates/slimming.jsp"%>

	<!-- Sliming filtering by protein id panel -->
	<%@include file="/templates/slimmingProtein.jsp"%>

	<!-- Sliming Graph popup -->
	<%@include file="/templates/slimmingGraph.jsp"%>		

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