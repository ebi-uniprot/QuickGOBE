<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" 
    prefix="fn" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<meta charset="utf-8">

<!-- Use the .htaccess and remove these lines to avoid edge case issues.
       More info: h5bp.com/b/378 -->
<!-- <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1"> -->
<!-- Not yet implemented -->

<meta name="description" content="EMBL-EBI">
<!-- Describe what this page is about -->
<meta name="keywords"
	content="bioinformatics, europe, institute, quickgo">
<!-- A few keywords that relate to the content of THIS PAGE (not the whol project) -->
<meta name="author" content="EMBL-EBI">
<!-- Your [project-name] here -->

<!-- Mobile viewport optimized: j.mp/bplateviewport -->
<meta name="viewport" content="width=device-width,initial-scale=1">

<!-- Place favicon.ico and apple-touch-icon.png in the root directory: mathiasbynens.be/notes/touch-icons -->

<link rel="stylesheet" type="text/css"
	href="<%= request.getContextPath() %>/css/boilerplate-style.css" />

<!-- CSS: implied media=all -->
<!-- CSS concatenated and minified via ant build script-->
<link rel="stylesheet" type="text/css"
	href="<%= request.getContextPath() %>/css/ebi-global.css" />
<link rel="stylesheet" type="text/css"
	href="<%= request.getContextPath() %>/css/ebi-visual.css">

<!-- you can replace this with [projectname]-colours.css. See http://frontier.ebi.ac.uk/web/style/colour for details of how to do this -->
<!-- also inform ES so we can host your colour palette file -->

<link rel="stylesheet" type="text/css"
	href="<%= request.getContextPath() %>/css/quickgo-colours.css">
<link rel="stylesheet" type="text/css"
	href="<%= request.getContextPath() %>/css/jquery-ui-1.10.0.custom.css">
<link rel="stylesheet" type="text/css"
	href="<%= request.getContextPath() %>/css/quickgo.css">
<link rel="stylesheet" type="text/css"
	href="<%= request.getContextPath() %>/css/960_fluid_grid.css">
<link rel="stylesheet" type="text/css"
	href="<%= request.getContextPath() %>/css/print.css" media="print">
<link rel="stylesheet" type="text/css"
	href="<%= request.getContextPath() %>/css/simple-pagination.css">
<!-- for production the above can be replaced with -->
<!--
  <link rel="stylesheet" href="//www.ebi.ac.uk/web_guidelines/css/compliance/mini/ebi-fluid-embl.css">
  -->

<style type="text/css">
/* You have the option of setting a maximum width for your page, and making sure everything is centered */
/* body { max-width: 1600px; margin: 0 auto; } */
</style>

<!-- end CSS-->


<!-- All JavaScript at the bottom, except for Modernizr / Respond.
       Modernizr enables HTML5 elements & feature detects; Respond is a polyfill for min/max-width CSS3 Media Queries
       For optimal performance, use a custom Modernizr build: www.modernizr.com/download/ -->

<!-- Full build -->
<!-- <script src="//www.ebi.ac.uk/web_guidelines/js/libs/modernizr.minified.2.1.6.js"></script> -->

<!-- custom build (lacks most of the "advanced" HTML5 support -->
<script
	src="//www.ebi.ac.uk/web_guidelines/js/libs/modernizr.custom.49274.js"></script>
<script src="http://code.jquery.com/jquery-latest.min.js"
	type="text/javascript"></script>
<script src="<%= request.getContextPath() %>/js/jquery.simplePagination.js"
	type="text/javascript"></script>
<script src="http://code.jquery.com/ui/1.10.3/jquery-ui.js"></script>
<script src="<%= request.getContextPath() %>/js/tooltipsy.min.js"></script>
<script src="<%= request.getContextPath() %>/js/jquery.highlight-4.js"></script>
<script src="<%= request.getContextPath() %>/js/jquery.cookie.js"></script>
<script src="<%= request.getContextPath() %>/js/common.js"></script>

<!-- Basket terms hidden input -->
<input type="hidden" id="basket_terms_hidden_list" name="basket_terms" />