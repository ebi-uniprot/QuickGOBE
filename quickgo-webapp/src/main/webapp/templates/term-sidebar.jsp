<div class="sidebar grid_2">
	<div id="entrySidebar">
		<div class="sidebarsubsection">
			<h3 class="first">
				<strong>Display</strong>
				<!-- <div class="sidebar-h-links">
					<a href="#" id="all">All</a>
				</div>-->
			</h3>
			<ul id="entry_sections">
				<li class="default active"><div class="heading"
						id="jp_term_information">
						<input type="checkbox" checked="true"
							id="term-information-checkbox"><span title=""
							id="term-information" class="entryview-name-nav">Term
							Information</span>
					</div></li>
				<li class="default"><div class="heading"
						id="jp_ancestors">
						<input type="checkbox" 
							id="ancestor-chart-checkbox"><span title=""
							id="ancestor-chart" class="entryview-name-nav">Ancestor
							Chart</span>

					</div></li>
				<c:if test="${isGO}">
					<li class="default"><div class="heading"
							id="jp_children">
							<input type="checkbox" id="child-terms-checkbox"
								class="toggle-section"><span title="" id="child-terms"
								class="entryview-name-nav">Child Terms</span>
						</div></li>
					<li class="default"><div class="heading"
							id="jp_co_occurring">
							<input type="checkbox" 
								id="co-occurring-terms-checkbox"><span title=""
								id="co-occurring-terms" class="entryview-name-nav">Co-occurring
								Terms</span>
						</div></li>
					<li class="default"><div class="heading" id="jp_logs">
							<input type="checkbox" id="term-history-checkbox"><span
								title="" id="term-history" class="entryview-name-nav">Change
								Log</span>
						</div></li>
				</c:if>	
			</ul>
		</div>
		<div>
			<c:if test="${isGO}">
				<element data-icon="i" id="show-term-annotations" class="closed icon icon-functional button" value="${term.id}" style="cursor:pointer">Show annotations</element>
			</c:if>
		</div>
	</div>
</div>