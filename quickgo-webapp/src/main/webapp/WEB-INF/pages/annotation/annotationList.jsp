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
<title>Annotations &lt; QuickGO &lt; EMBL-EBI</title>
<!-- Imports -->
<%@include file="/templates/imports.jsp"%>
<script src="<%= request.getContextPath() %>/js/annotations.js"></script>
</head>

<body>
	<div id="content" class="container_12">
		<!--Header -->
		<%@include file="/templates/header.jsp"%>
		
		<div class="sidebar grid_2">
			<h2 class="page-title">Results <img id="loading-image" src="<%= request.getContextPath() %>/images/ajax-loader.gif" alt="Loading..." height="42" width="42" /></h2>
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
		<!-- Sidebar -->
		<%@include file="/templates/annotation-sidebar.jsp"%>
		<!-- Statistics panel -->
		<%@include file="statistics.jsp"%>
		<!-- Main Content -->
		<div id="annotationsMainContent" class="mainContent grid_10">			
			<ul class="action-buttons-grouped decorated" id="resultActionButtons">
				<li><a id="customize-columns-button" title="Add or remove columns from the results table"
					class="icon icon-functional button tooltipped" data-icon="e">Columns</a></li>
				<li><a id="download-button" title="Download results"
						class="icon icon-functional button tooltipped" data-icon="=">Download</a>
				</li>				
			</ul>
			<!-- Download menu -->
			<%@include file="/templates/download.jsp"%>
			<span class="status">				
				<div class="pagination light-theme simple-pagination" id="light-pagination"></div></span>
			<span class="status">				
				<div id="pageCount">Page <strong>${currentPage}</strong> of <strong><fmt:formatNumber value="${totalNumberAnnotations}" type="number"/></strong> annotations</div>
			</span>
			<div id="resultsArea" style="display: block;">	
				<input type="hidden" name="current_page" value="${currentPage}" />
				<input type="hidden" name="total_number_annotations" value="${totalNumberAnnotations}" />
				<c:choose>
					<c:when test="${fn:length(sessionScope.annotationsList) == 0}">
						<h4>No matching annotations</h4>
					</c:when>	
					<c:otherwise>
						<table id="results" class="grid">
						<script>
							quickFilteringButtons();							
							refreshBasketTerms();							
							addTermsToBasketFunctionality();
							addListTermsToBasketFunctionality();
							generateOntologyGraphFunctionality();
						</script>
							<tr>
								<c:forEach var="annotationColumn" items="${sessionScope.annotationsColumns}">
									<c:set var="display" value="display:none" />
									<c:forEach var="visible" items="${sessionScope.visibleAnnotationsColumns}">
										<c:if test="${visible eq annotationColumn}">
											<c:set var="display" value="" />
										</c:if>
									</c:forEach>
									<th class="${annotationColumn.name}" style="${display}">${annotationColumn.description}</th>
								</c:forEach>
							</tr>							
							<c:forEach var="annotation" items="${sessionScope.annotationsList}">
								<tr>
									<c:forEach var="annotationColumn" items="${annotationsColumns}">
										<c:set var="display" value="display:none" />
										<c:forEach items="${sessionScope.visibleAnnotationsColumns}" var="visible">
											<c:if test="${visible eq annotationColumn}">
												<c:set var="display" value="" />
											</c:if>
										</c:forEach>
								
										<td class="${annotationColumn.name}" style="${display}">
										<c:if test="${annotationColumn.name == 'goId' || annotationColumn.name == 'originaltermid'}">										
											<c:set var="clicked" value="enabled" />
											<c:if test="${sessionScope.basketTerms[annotation[annotationColumn.id].name] != null}">
												<c:set var="clicked" value="disabled" />				
											</c:if>
											<element id="term_${annotation[annotationColumn.id].name}"
												class="add-basket-item icon icon-static ${clicked}"
													data-icon="b" title="Add term to basket"></element> 
										</c:if>
										<c:choose>
											<c:when test="${annotationColumn.showURL}">
												<c:choose>											
													<c:when test="${annotationColumn.name == 'with'}">
														<c:forEach items="${annotation[annotationColumn.id]}" var="withElement">	
															<a href="${withElement.url}" target="_blank">${withElement.name}</a>
														</c:forEach>																					
													</c:when>
													<c:when test="${annotationColumn.name == 'goId'}">
														<a class="ontologygraph" id="ontologygraph_${annotation[annotationColumn.id].name}">${annotation[annotationColumn.id].name}</a>
													</c:when>
													<c:when test="${annotationColumn.name == 'evidence'}">
														<a class="ontologygraph" id="ontologygraph_${annotation.ecoID}">${annotation.ecoID}</a> <a href="${annotation.goEvidence.url}" target="_blank">(${annotation.goEvidence.name})</a>
													</c:when>
													<c:otherwise>												
														<a href="${annotation[annotationColumn.id].url}" target="_blank">${annotation[annotationColumn.id].name}</a>
													</c:otherwise>
												</c:choose>
											</c:when>
											<c:otherwise>
												<c:choose>
													<c:when test="${annotationColumn.name == 'extension'}">
														<c:choose>
															<c:when test="${fn:length(annotation[annotationColumn.id]) != 0}">
																<element class='icon icon-functional enabled' data-icon='3' onclick="showExtension('${annotation[annotationColumn.id]}')"/> ${fn:substring(annotation[annotationColumn.id], 0, 30)}...
															</c:when>
														</c:choose>
													</c:when>
													<c:otherwise>
														${annotation[annotationColumn.id]}
													</c:otherwise>	
												</c:choose>
											</c:otherwise>		
										</c:choose>										
										</td>
									</c:forEach>
								</tr>
							</c:forEach>
						</table>
					</c:otherwise>
				</c:choose>
			</div>
		</div>
		<div class="clearfix"></div>
	</div>	
	<!-- Footer -->
	<%@include file="/templates/footer.jsp"%>

	<!-- Column display popup -->
	<%@include file="columnDisplay.jsp"%>	

	<!-- Advanced filters popup -->
	<%@include file="advancedFilters.jsp"%>	

	<!-- Ontology graphs -->
	<%@include file="/templates/ontologygraph.jsp"%>
	
	<!-- Column display popup -->
	<%@include file="extension.jsp"%>

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