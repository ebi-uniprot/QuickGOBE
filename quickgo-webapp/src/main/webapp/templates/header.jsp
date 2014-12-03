<div class="header">
	<div id="global-masthead" class="masthead grid_12">
		<form id="global-search" name="global-search"
			action="/ebisearch/search.ebi" method="GET">
			<fieldset>
				<label> <input type="text" id="global-searchbox"
					name="query">
				</label> <input type="submit" class="submit" value="" name="submit">
				<input type="hidden" checked="checked" value="allebi" name="db">
				<input type="hidden" checked="checked" value="global-masthead"
					name="request_from">
			</fieldset>
		</form>

		<p id="logo">

			<a title="Go to the EMBL-EBI homepage" href="/"><img
				src="//www.ebi.ac.uk/web_guidelines/images/logos/EMBL-EBI/logo.png"
				alt="European Bioinformatics Institute"></a>
		</p>

		<div class="nav">
			<ul id="global-nav">
				<li id="services" class="first"><a title="" href="/services">Services</a>
				</li>
				<li id="research"><a title="" href="/research">Research</a></li>
				<li id="training"><a title="" href="/training">Training</a></li>
				<li id="industry"><a title="" href="/industry">Industry</a></li>
				<li id="about" class="last"><a title="" href="/about">About
						us</a></li>
			</ul>
		</div>
	</div>

	<div id="local-masthead" class="masthead grid_12">
		<div class="grid_2 alpha" id="local-title">
			<h1>
				<a href="<%= request.getContextPath() %>" accesskey="1" id="logo"><img title="QuickGO home"
					src="<%= request.getContextPath() %>/images/logo/quickgo-logo.png" alt=""></a>
			</h1>
		</div>
		<div class="grid_10 omega" id="local-header-background">			
			<h4 class="title">A fast browser for Gene Ontology terms and annotations</h4>
			<span style="background-color:#FFFBCC;"><element class="icon icon-generic" data-icon="l"></element>This BETA version of QuickGO is provided to enable you to test new functionality and user interface features only; it is running on a single test server, so its performance should not be taken as an indication of how the final release version will behave</span> <!-- WARNING message -->
		</div>
		<div id="namespace-background" class="namespace-uniprot">
			<div id="query-area">
				<input id="query" name="query" class="ui-autocomplete-input" autocomplete="off" value="${sessionScope.searchedText}">
			</div>
			<div id="button-area">
				<a class="icon icon-functional button" data-icon="1" title="Search" id="search-button"></a>
			</div>
			<input type="hidden" id="searched_text" value="${sessionScope.searchedText}" />
		</div>
		<div id="nav">
			<ul class="grid_12" id="local-nav">
				<li class="first"><a href="${pageContext.request.contextPath}/webservices" onclick="">Web
						Services</a></li>
				<li><a href="${pageContext.request.contextPath}/dataset" accesskey="7" onclick="">Dataset</a></li>
				<li class="functional"><a id="contact" href="/contact"
					accesskey="9">Contact</a></li>
				<li class="functional"><a id="help" href="${pageContext.request.contextPath}/help" >Help</a></li>
			</ul>
		</div>
	</div>
</div>