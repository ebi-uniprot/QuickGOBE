<div id="slimming-popup" class="lightbox" style="display: none;">	
	<div id="slimming-popup-content" class="container_12">	
	<div alt="Close the slimming menu" class="grid_12">
		<a style="float: right" id="closeSlimmingMenu" href="#" class="closeBox">x</a>	
	</div>
		<div id="left_slimming_menu" class="grid_6">			
			<h2>GO SLIMS  <img id="loading-image" src="<%= request.getContextPath() %>/images/ajax-loader.gif" alt="Loading..." height="42" width="42" /></h2>
			<div id="subleftmenu">
				<h6>Step 1: Add terms to your list</h6>
				<h5>Add predefined slim sets</h5>
				<div id="slimming_set_terms">
					<div class="slimming_section">
						<c:set var="disabled" value=""/>
						<c:if test="${not empty selectedSet}">
							<c:set var="disabled" value="disabled"/>
						</c:if>
						<c:forEach var="count" items="${sessionScope.allSubsetsCounts}">
							 <c:set var="checked" value=""/>
							 <c:if test="${count.subset eq selectedSet}">
		  						 <c:set var="checked" value="checked"/>
							 </c:if>
							 <input ${checked} ${disabled} type="radio" name="set_values_slimming" value="${count.subset}"> ${count.subset} (${count.subsetCount})</input><br/>
						</c:forEach>
					</div>
				</div>
				<div class="slimming_buttons_panel">
						<a href="#" data-icon="+" class="closed icon icon-functional button" id="addSetTermsButton" onclick="addPredefinedSetsTerms()">Add to list >>></a>
				</div>
				<div class="clearfix"><p/></div>
				<h5>Add your own terms</h5>
				<div class="slimming_section">				
					<textarea id="slimming_own_terms" class="slimming_textarea"></textarea>
				</div>
				<div class="slimming_buttons_panel">
					<a href="#" data-icon="+" class="closed icon icon-functional button" id="addOwnTermsButton" onclick="addOwnTerms($('#slimming_own_terms').val())">Add to list >>></a>
				</div>
				<div class="clearfix"><p/></div>
				<h5>Add terms from your basket</h5>
				<div id="slimming_basket_terms">
					<div  class="slimming_section">
						<c:forEach var="basketItem" items="${sessionScope.basketTerms}">						
							 <c:set var="termchecked" value=""/>					
						  	 <c:if test="${fn:contains(sessionScope.slimmingTerms, basketItem)}">
						  	 	<c:set var="termchecked" value="checked"/>
							 </c:if>
					
							 <input ${termchecked} type="checkbox" name="basket_values_slimming" value="${basketItem.key}"> ${basketItem.key} ${basketItem.value}</input><br/>
						</c:forEach>
					</div>
				</div>
				<div class="slimming_buttons_panel">
					<a href="#" data-icon="+" class="closed icon icon-functional button" id="addBasketTermsButton" onclick="addBasketTerms()">Add to list >>></a>
				</div>	
			</div>
		</div>
		<div class="grid_6" id="right_slimming_menu">		
			<h5>Selected terms<span style="float:right;font-size:12px"><a href="#" onclick="clearSlimming()">Clear All</a></span></h5>
			<div id="selected_slimming_terms" style="height: 600px;overflow:auto;white-space: nowrap;">
				<h3>Biological Process Terms<span style="float:right;font-size:12px"><a onclick="inactivateTerm('P')" href="#">Deselect All</a></span><span style="float:right;font-size:12px"><a href="#" onclick="activateTerm('P')">Select All /</a></span></h3>
				<div id="bp_section">
					<ul class="no-li-symbol">
						<c:forEach var="slimmingTerm" items="${sessionScope.bp_slimmingTerms}">
							<c:set var="termchecked" value="checked"/>
							<c:set var="color" value=""/>
							<c:if test="${fn:contains(sessionScope.inactiveSlimmingTerms, slimmingTerm)}">
								<c:set var="termchecked" value=""/>
								<c:set var="color" value="#B5B5B5"/>					
							</c:if>
							<li><input ${termchecked} id="${slimmingTerm.key}" type="checkbox" name="final_terms_slimming" value="${slimmingTerm.key}"/><label for="${slimmingTerm.key}" style="color:${color}"> ${slimmingTerm.key} ${slimmingTerm.value}</label></li>

						</c:forEach>
					</ul>
					<script>
						activeInactiveEvent();
					</script>
				</div>
				<h3>Molecular Function Terms<span style="float:right;font-size:12px"><a onclick="inactivateTerm('F')" href="#">Deselect All</a></span><span style="float:right;font-size:12px"><a href="#" onclick="activateTerm('F')">Select All /</a></span></h3>
				<div id="mf_section">
					<ul class="no-li-symbol">
						<c:forEach var="slimmingTerm" items="${sessionScope.mf_slimmingTerms}">
							<c:set var="termchecked" value="checked"/>
							<c:set var="color" value=""/>
							<c:if test="${fn:contains(sessionScope.inactiveSlimmingTerms, slimmingTerm)}">
								<c:set var="termchecked" value=""/>
								<c:set var="color" value="#B5B5B5"/>					
							</c:if>
							<li><input ${termchecked} id="${slimmingTerm.key}" type="checkbox" name="final_terms_slimming" value="${slimmingTerm.key}"/><label for="${slimmingTerm.key}" style="color:${color}"> ${slimmingTerm.key} ${slimmingTerm.value}</label></li>

						</c:forEach>
					</ul>
					<script>
						activeInactiveEvent();
					</script>
				</div>
				<h3>Cellular component Terms<span style="float:right;font-size:12px"><a onclick="inactivateTerm('C')" href="#">Deselect All</a></span><span style="float:right;font-size:12px"><a href="#" onclick="activateTerm('C')">Select All /</a></span></h3>
				<div id="cc_section">
					<ul class="no-li-symbol">
						<c:forEach var="slimmingTerm" items="${sessionScope.cc_slimmingTerms}">
							<c:set var="termchecked" value="checked"/>
							<c:set var="color" value=""/>
							<c:if test="${fn:contains(sessionScope.inactiveSlimmingTerms, slimmingTerm)}">
								<c:set var="termchecked" value=""/>
								<c:set var="color" value="#B5B5B5"/>
							</c:if>
							<li><input ${termchecked} id="${slimmingTerm.key}" type="checkbox" name="final_terms_slimming" value="${slimmingTerm.key}"/><label for="${slimmingTerm.key}" style="color:${color}"> ${slimmingTerm.key} ${slimmingTerm.value}</label></li>
						</c:forEach>
					</ul>
					<script>
						activeInactiveEvent();
					</script>
				</div>				
			</div>			
		</div>
		<div class="grid_12" style="float:right;padding-bottom:1%" id="slimming_popup_buttons_menu">			
			<a  style="float:right" onclick="nextstep()" id="slimmingNextButton" class="closed icon icon-functional button" data-icon=">" href="#">Next</a>
			<a  style="float:right;display:none" onclick="backstep()" id="slimmingBackButton" class="closed icon icon-functional button" data-icon="<" href="#">Back</a>
			<a  style="float:right" onclick="slim()" id="slimmingProcess" class="closed icon icon-functional button" data-icon="i" href="#">View Annotations</a>
			<a  style="float:right" onclick="showSlimmingGraph()" id="showSlimmingTermsGraph" class="closed icon icon-conceptual button" data-icon="o" href="#">Show Graph</a>
		</div>

	</div>
		<script>
			var termsleftmenu = '';
			var proteinsmenu = '';
			function nextstep(){
				termsleftmenu = $("#subleftmenu").clone();
				$("#filterbyproteinidmenu").css("display", "block");
				if(proteinsmenu == ''){//first time
					proteinsmenu = $("#filterbyproteinidmenu").clone();
				}
				$("#subleftmenu").html(proteinsmenu);
				// change left menu class
				$("#left_slimming_menu").removeClass("grid_6");
				$("#left_slimming_menu").addClass("grid_12");
				// elements to hide
				$("#right_slimming_menu").css("display", "none");
				$("#showSlimmingTermsGraph").css("display", "none");
				$("#slimmingNextButton").css("display", "none");
				// elements to show
				$("#slimmingBackButton").css("display", "block");
			}

			function backstep(){
				proteinsmenu = $("#subleftmenu").clone();
				$("#filterbyproteinidtmenu").css("display", "none");
				$("#subleftmenu").html(termsleftmenu);
				// change left menu class
				$("#left_slimming_menu").removeClass("grid_12");
				$("#left_slimming_menu").addClass("grid_6");
				// elements to hide
				$("#slimmingBackButton").css("display", "none");
				// elements to show
				$("#right_slimming_menu").css("display", "block");
				$("#slimmingNextButton").css("display", "block");
				$("#showSlimmingTermsGraph").css("display", "block");
			}
		</script>
</div>