<div id="basket-contents" style="z-index: 3; top: 34px; display: none;">
	<div id="closeBasket">
		<span class="basketText">(max 400 entries)</span><a href="#" id="closeBasket" class="closeBox">x</a>
	</div>
	<div id="filledBasketContents" style="display: block;">
		<c:choose>		
			<c:when test="${empty sessionScope.basketTerms}">
				<h3>Your basket is currently empty.</h3>
				<p>
					You can add a GO term to the Term Basket by clicking on the <element class="icon icon-static" data-icon="b"></element> icon that appears next to its identifier in QuickGO.<br>
				</p>
			</c:when>
			<c:otherwise>
				<div id="basketTableDiv">
					<table id="basketTermsTable">
						<thead>
							<tr>
								<th><span id="idHeader"><span class="expanderHeading"><span
											title="Term ID"
											class="tooltipped">Term ID</span></span><span class="expander"></span></span></th>
								<th><span id="entry_nameHeader"><span
										class="expanderHeading"><span
											title="Term name"
											class="tooltipped">Term name</span></span><span class="expander"></span></span></th>				
								<th>Remove</th>
							</tr>
						</thead>
						<tbody id="basketTermsRows" class="basket-rows">
							<c:forEach var="basketItem" items="${sessionScope.basketTerms}">
								<tr><td><a href="<c:url value="/"/>term/${basketItem.key}">${basketItem.key}</a></td><td>${basketItem.value}</td><td><element id="remove_term_${basketItem.key}" class='remove-term enabled icon icon-functional' data-icon='d' title="Remove"></element></td></tr>
							</c:forEach>							
						</tbody>
					</table>
				</div>
			</c:otherwise>
		</c:choose>
		<div class="clearfix"><p/></div>
		Enter a list of terms to be added to your basket:
		<textarea id="termsListToAdd"style="width:80%;height:55px"></textarea>
		<a title="Add terms" class="button add-terms" href="#" id="add-list-terms-button">Add terms</a>	
		<script>
			removeTermFromBasketFunctionality();
			addListTermsToBasketFunctionality();
		</script>
	</div>	

	<div id="basket-actions" style="display: block;">
		<c:set var="display" value="" />	
		<c:if test="${empty sessionScope.basketTerms}">
			<c:set var="display" value="disabled" />
		</c:if>
		<ul class="action-buttons-grouped">			
			<!-- <li><a id="basket-download-button" href="#"
				class="${display} button icon icon-functional download"
				title="Download results" data-icon="=">Download</a></li>
			<li id="view-basket-annotations" style="float: right"><a href="" class="button ${display} fullView"
				title="View annotations">View annotations</a></li>-->
			<li id="clear-basket-button" style="float: right"><a href="#" class="button ${display} clear"
				title="Clear basket">Clear</a></li>
			<li id="display-terms-graph-basket" style="float: right"><a href="#" class="button ${display}"
				title="Display terms in Ancestor Chart">Display terms in Ancestor Chart</a></li>

		</ul>
	<script>
		setBasketButtonsFunctionality();
		generateTermsBasketOntologyGraphFunctionality();
	</script>
	</div>
</div>