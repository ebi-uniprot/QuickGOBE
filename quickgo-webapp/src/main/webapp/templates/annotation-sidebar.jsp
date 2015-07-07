<div id="bookmarkable-link-menu" class="lightbox" style="display: none;">
	<div id="bookmarkable-link-content">
		<a style="float: right" href="#" class="closeBox" id="closeLinkMenu">x</a>
		<h4>Bookmarkable link</h4>
		<p id="link-value"></p>
	</div>
</div>
<div class="sidebar grid_2">
	<div id="appliedFilters" class="filterBy sidebarsubsection">
		<h3 class="first">
			Applied filters
			<div class="sidebar-h-links"><a href="#" id="clearAllFilters">Clear</a></div>
		</h3>
			<div id="appliedFiltersValuesDiv">
			<input name="appliedFiltersValues" type="hidden" value="${sessionScope.appliedFilters.parameters}" />
				<c:if test="${sessionScope.appliedFilters.numValues() == 0}">
					No filters applied
					<div class="clearfix"><p></p></div>
				</c:if>

				<c:forEach var="filter" items="${sessionScope.appliedFilters.parameters}">
						<c:forEach var="filterValue" items="${sessionScope.appliedFilters.parameters[filter.key]}">
							<c:if test="${filter.key != 'slim' && filterValue != 'ancestorsI' && filterValue != 'ancestorsIPO' && filterValue != 'ancestorsIPOR'}">
								<div class="appliedValue"><p style="float: left">${sessionScope.appliedFilters.toAnnotationColumn(filter.key)} : ${fn:replace(filterValue, "\"", "").toUpperCase()}</p>
									<a style="float: right" id="${filter.key}-${fn:replace(filterValue, "\"", "")}" class="closeBox removeFilter" href="#">x</a>
								</div>
							</c:if>
<div class="clearfix"> </div>
	  				        </c:forEach>
				    </c:forEach>
					<script>

						// TO DO: Move these functions to the annotations.js file

						/**
						* Remove a filter function
						*/
						$('.removeFilter').off();
						// Remove filter
						$('.removeFilter').on("click", function() {
							var idValue = $(this).attr('id');
							removeFilter(idValue);
						});


						// Remove filter
						function removeFilter(idValue) {
							var url = window.location;
								$.ajax({
									type : "POST",
									url : url,
									data : {removeFilter:idValue, page:1},
									success : function(response) {
										// We have the response
										$('#resultsArea').html($(response).find('#resultsArea'));
										// Update pagination component and number of annotations
										$("#light-pagination").pagination('updateItems', $('input[name=total_number_annotations]').val());
										$('#pageCount').html($(response).find('#pageCount'));
										// Left sidebar
										$('#appliedFiltersValuesDiv').html($(response).find('#appliedFiltersValuesDiv'));
										$('#quickFiltersMenu').html($(response).find('#quickFiltersMenu'));
										makeSelectedTaxonomyActive();
										$('#taxonomiesArea').html($(response).find('#taxonomiesArea'));
										$('#qualifierArea').html($(response).find('#qualifierArea'));
										$('#evidencesArea').html($(response).find('#evidencesArea'));
										$('#aspectsArea').html($(response).find('#aspectsArea'));
										$('#withArea').html($(response).find('#withArea'));
										$('#assignedByArea').html($(response).find('#assignedByArea'));
										$('#addedTermsToBasket').html($(response).find('#addedTermsToBasket'));

									},
									error: function(xhr, status, error) {
										  var err = eval("(" + xhr.responseText + ")");
										  alert(err.Message);
									}
								});
						}
						/*
						 * Clear All Filters function
						 */
						$("#clearAllFilters").on("click", function() {
									var url = window.location;
									$.ajax({
										type : "POST",
										url : url,
										data : {removeAllFilters:true},
										success : function(response) {
					 						// We have the response
											$('#resultsArea').html($(response).find('#resultsArea'));
											// Update pagination component and number of annotations
											$("#light-pagination").pagination('updateItems', $('input[name=total_number_annotations]').val());
											$('#pageCount').html($(response).find('#pageCount'));
											// Left sidebar
											$('#quickFiltersMenu').html($(response).find('#quickFiltersMenu'));
											$('#appliedFiltersValuesDiv').html($(response).find('#appliedFiltersValuesDiv'));
											makeSelectedTaxonomyActive();
											$('#taxonomiesArea').html($(response).find('#taxonomiesArea'));
											$('#qualifierArea').html($(response).find('#qualifierArea'));
											$('#evidencesArea').html($(response).find('#evidencesArea'));
											$('#aspectsArea').html($(response).find('#aspectsArea'));
											$('#withArea').html($(response).find('#withArea'));
											$('#assignedByArea').html($(response).find('#assignedByArea'));
											$('#addedTermsToBasket').html($(response).find('#addedTermsToBasket'));

										},
										error : function(e) {
											alert('Error: ' + e.responseText);
										}
									});
						});
					</script>
				<c:set var="req" value="${pageContext.request}" />
				<c:set var="baseURL" value="${req.scheme}://${req.serverName}:${req.serverPort}${req.contextPath}" />
				<c:set var="filters" value="${sessionScope.appliedFilters.URL}" />
				<input type="hidden" name="filtersURL" id="filtersURL" value="${baseURL}/annotation?q=${filters}" />
				<script>
					$("#link-value").html($("#filtersURL").val());
					$("#bookmarkable-link").attr("href", $("#filtersURL").val())
				</script>
			</div>
			<element class='icon icon-generic' data-icon='L'><a id="bookmarkable-link" style="font-size:10px;text-align:right" href="#">Bookmarkable link</a>
	<div id="quickFiltersMenu" class="quickFilters sidebarsubsection">
		<h3>
			<strong>Quick Filters</strong>
		</h3>
		<p class="builder">
			<div class="suggestionOrganism">
				<h4 class="filter-title">Taxon</h4>
				<ul id="taxonomyId" class="filter-value">
					<c:forEach var="taxon" items="${sessionScope.mostCommonTaxonomies}">
						<li id="${taxon.id}" class="tooltipped" title="${taxon.name}"><a href="#">[${taxon.id}] ${taxon.name}</a></li>
					</c:forEach>
					<div id="moreOrganisms" style="padding: 5px;">
						<a href="#" id="showMoreTaxonomies" style="color:#B9772E"><label for="showMoreTaxonomies">+Show more</label></a>
					</div>
				</ul>
			</div>

			<script>
				// Suggested taxonomies
				$(".suggestionOrganism li").click(function(){
					if($(this).attr("class").indexOf("active") == -1){// It's not checked yet
						$(this).addClass("active");
						ajaxTaxonomyFilteringRequest();
					} else { // It's checked, so uncheck it
						$(this).removeClass("active");
						$(this).siblings().show();
						removeFilter("taxonomyClosure-" + $(this).attr("id"));
					}
				});
				// Show/Hide more taxonomies
				$('.suggestionOrganism li:gt(3)').hide();
				$('#showMoreTaxonomies').click(function() {
				    if($("label[for='showMoreTaxonomies']").text() == "+Show more"){
					    $("label[for='showMoreTaxonomies']").text("-Show less");
					    $('.suggestionOrganism li:gt(3)').show();
				    }else{
				    	    $("label[for='showMoreTaxonomies']").text("+Show more");
				    	    $('.suggestionOrganism li:gt(3)').hide();
				    }
				});
			</script>
		</p>
		<p class="builder">
			<div>
				<h4 class="filter-title">GO ID</h4>
				<fieldset><textarea id="goID" class="filter-value"></textarea></fieldset>
			</div>
		</p>
		<p class="builder">
			<div>
				<h4 class="filter-title">Gene Product ID</h4>
				<fieldset><textarea id="dbObjectID" class="filter-value"></textarea></fieldset>
			</div>
		</p>
		<a style="float: right" href="#" id="submit_quick_filtering" title=""
								class="icon icon-functional button tooltipped" data-icon="1">Filter</a>
			<div class="clearfix"><p></p></div>
			<div><a style="float: right" id="advanced-filters" title=""
								class="icon icon-functional button tooltipped" data-icon="f">Advanced filters</a>
			</div>
		<script>
			quickFilteringButtons();
			advancedFiltersButtons();
		</script>
	</div>

	<div class="viewBy sidebarsubsection">
		<h3>
			<strong>View by</strong>
		</h3>
		<p class="builder">
			<div class="viewBy">
				<ul class="viewByList">
					<li class="viewByParent" id="resultsTableView"><a class="viewBy" href="#" rel="nofollow">Results table</a></li>
					<li class="viewByParent" id="statisticsView"><a class="viewBy" href="#" rel="nofollow">Statistics</a></li>
				</ul>
			</div>
		</p>
	</div>
		<script>
				// View by
				$(".viewBy li").click(function(){
					if($(this).attr("class").indexOf("active") == -1){// It's not checked yet
						$(this).addClass("active");
						$(this).siblings().removeClass("active");
					} else { // It's checked, so uncheck it
						$(this).removeClass("active");
					}
				});
		</script>
</div>
