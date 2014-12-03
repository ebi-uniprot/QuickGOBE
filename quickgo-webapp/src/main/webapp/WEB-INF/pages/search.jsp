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
<title>Search results &lt; QuickGO &lt; EMBL-EBI</title>
<!-- Imports -->
<%@include file="/templates/imports.jsp"%>
<script src="<%= request.getContextPath() %>/js/annotations.js"></script>
</head>

<body>
	<div id="content" class="container_12">
		<!--Header -->
		<%@include file="/templates/header.jsp"%>
		
		<div class="sidebar grid_2">
			<h2 class="page-title">Search Results</h2>
		</div>
		<div class="mainContent grid_8">
			<p></p>
		</div>
		
		<div class="clearfix"></div>
		<!-- Sidebar -->
		<%@include file="/templates/search-sidebar.jsp"%>
		<!-- Main Content -->
		<div id="searchResultsMainContent" class="mainContent grid_10">			
			<span class="status">				
				<div class="pagination light-theme simple-pagination" id="light-search-pagination"></div></span>
			<span class="status">				
				<div id="searchPageCount">Page <strong>${searchCurrentPage}</strong> of <strong><fmt:formatNumber value="${totalNumberResults}" type="number"/></strong> results</div>
			</span>
			<div id="searchResultsArea" style="display: block;">	
				<input type="hidden" name="search_current_page" value="${searchCurrentPage}" />
				<input type="hidden" name="total_number_results" value="${totalNumberResults}" />
				<c:choose>
					<c:when test="${fn:length(searchResults) == 0}">
						<h4>No matches found</h4>
					</c:when>	
					<c:otherwise>
						<table id="searchResults" class="grid">						
							<tr>
								<th>ID</th>
								<th>Name</th>
							</tr>
							<c:choose>
								<c:when test="${viewBy == 'entity'}">
									<c:forEach var="result" items="${searchResults}">
										<tr>
											<td><a href="http://www.uniprot.org/uniprot/${result.dbObjectId}" target="_blank">${result.dbObjectId}</a></td>
											<td class="result_text">${result.dbObjectName}</td>
										</tr>
									</c:forEach>
								</c:when>
								<c:otherwise>
									<c:forEach var="result" items="${searchResults}">
										<tr>
											<td><a href="<c:url value="/"/>term/${result.id}">${result.id}</a></td>
											<td class="result_text">${result.name}</td>	
										</tr>	
									</c:forEach>
								</c:otherwise>
							</c:choose>
						</table>
					</c:otherwise>
				</c:choose>
				<script>
					var searchedText = document.getElementById("searched_text").value;					
					var words = searchedText.split(/[ ,]+/);
					$('.result_text').each(function() {
						var text = $(this).text();
						for (var i = 0; i < words.length; i++) {						
							var textToReplace = new RegExp(words[i], 'gi');
							text = text.replace(textToReplace,"<span style='background-color:#FFFBCC;'>" + words[i] + "</span>");
						}
						$(this).html(text);
					});
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