<div class="sidebar grid_2">
	<div id="quickFiltersMenu" class="quickFilters sidebarsubsection">
		<h3>
			<strong>Results</strong>		
		</h3>	
		<p class="builder">
			<div class="searchFilters">
			<input type="hidden" id="selectedviewBy" value="${viewBy}"/>
				<h4 class="filter-title">View By</h4>
				<ul id="resultsCategories">
					<li class="tooltipped" title="Entities"><a
					style="color: #909090" id="entity" class="resultsView" href="">Entities
						<c:choose><c:when test="${sessionScope.gpNumberResults > 0}">(${sessionScope.gpNumberResults})<script>
							$("#entity").css("color", "#00709B")
						</script>
						</c:when><c:otherwise><script>$('#entity').bind('click', false);</script></c:otherwise></c:choose>
				</a></li>				
				<li class="tooltipped" title="GO Terms"><a
					style="color: #909090" id="goID" class="resultsView" href="">GO
						Terms <c:choose><c:when test="${sessionScope.goNumberResults > 0}">(${sessionScope.goNumberResults})<script>
							$("#goID").css("color", "#00709B")
						</script>
						</c:when><c:otherwise><script>$('#goID').bind('click', false);</script></c:otherwise></c:choose>
				</a></li>

				<li class="tooltipped" title="Biological Process GO Terms"><a
					style="padding-left: 2em; color: #909090" id="bp"
					class="resultsView" href="">Biological Process <c:choose><c:when
							test="${sessionScope.bpGoNumberResults > 0}">(${sessionScope.bpGoNumberResults})<script>
								$("#bp").css("color", "#00709B")
							</script>
						</c:when><c:otherwise><script>$('#bp').bind('click', false);</script></c:otherwise></c:choose></a></li>

				<li class="tooltipped" title="Molecular Function GO Terms"><a
					style="padding-left: 2em; color: #909090" id="mf"
					class="resultsView" href="">Molecular Function <c:choose><c:when
							test="${sessionScope.mfGoNumberResults > 0}">(${sessionScope.mfGoNumberResults})<script>
								$("#mf").css("color", "#00709B")
							</script>
						</c:when><c:otherwise><script>$('#mf').bind('click', false);</script></c:otherwise></c:choose></a></li>

				<li class="tooltipped" title="Celular Component GO Terms"><a
					style="padding-left: 2em; color: #909090" id="cc"
					class="resultsView" href="">Cellular Component <c:choose><c:when
							test="${sessionScope.ccGoNumberResults > 0}">(${sessionScope.ccGoNumberResults})<script>
								$("#cc").css("color", "#00709B")
							</script>
						</c:when><c:otherwise><script>$('#cc').bind('click', false);</script></c:otherwise></c:choose></a></li>

				<li class="tooltipped" title="ECO Terms"><a
					style="color: #909090" id="ecoID" class="resultsView" href="">ECO
						Terms <c:choose><c:when test="${sessionScope.ecoNumberResults > 0}">(${sessionScope.ecoNumberResults})<script>
							$("#ecoID").css("color", "#00709B")
						</script>
						</c:when><c:otherwise><script>$('#ecoID').bind('click', false);</script></c:otherwise></c:choose>
				</a></li>

				<li class="tooltipped" title="Manual ECO Terms"><a
					style="padding-left: 2em; color: #909090" id="ecoManual"
					class="resultsView" href="">Manual <c:choose><c:when
							test="${sessionScope.expEcoTotalResults > 0}">(${sessionScope.expEcoTotalResults})<script>
								$("#ecoManual").css("color", "#00709B")
							</script>
						</c:when><c:otherwise><script>$('#ecoManual').bind('click', false);</script></c:otherwise></c:choose></a></li>

				<li class="tooltipped" title="Automatic ECO Terms"><a
					style="padding-left: 2em; color: #909090" id="ecoAutomatic"
					class="resultsView" href="">Automatic <c:choose><c:when
							test="${sessionScope.automaticEcoTotalResults > 0}">(${sessionScope.automaticEcoTotalResults})<script>$("#ecoAutomatic").css("color","#00709B")</script>
						</c:when><c:otherwise><script>$('#ecoAutomatic').bind('click', false);</script></c:otherwise></c:choose></a></li>

				<li class="tooltipped" title="Evidence ECO Terms"><a style="padding-left:2em;color:#909090" id="evidenceEco" class="resultsView" href="">Evidence <c:choose><c:when test="${sessionScope.evidenceEcoTotalResults > 0}">(${sessionScope.evidenceEcoTotalResults})<script>$("#evidenceEco").css("color","#00709B")</script></c:when><c:otherwise><script>$('#evidenceEco').bind('click', false);</script></c:otherwise></c:choose></a></li>
						
						<script>
							$('.resultsView').each(function() {
								var id = $(this).attr('id');
								var url = window.location.href;
								var newUrl = '';
								if(url.indexOf('viewBy') != -1){
									newUrl = url.replace(/(viewBy=)(.*)?/,'$1' + id);
								}else{
									newUrl = url + "&viewBy=" + id;
								}
								$(this).attr('href', newUrl);
							});
							
							// Make active/inactive search filters
							$(".searchFilters li").click(function(){
								if($(this).attr("class").indexOf("active") == -1){// It's not checked yet	
									$(this).siblings().removeClass("active");
									$(this).addClass("active");									
								} else { // It's checked, so uncheck it
									$(this).removeClass("active");
								}
							});
							
							//Make selected filter active
							$(".searchFilters li a").each(function(){
								if($(this).attr("id") == $("#selectedviewBy").val()){
									$(this).parent().addClass("active");									
								}
							});
						</script>											
				</ul>
			</div>			
	</div>
</div>