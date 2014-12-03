<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<div id="column-display-popup" class="lightbox" style="display: none;">
	<div id="customizeColumns" class="container_12">
		<div class="grid_12" alt="Close the customize column menu">
			<a id="closeCustomizeColumns" class="closeBox" href="#">x</a>	
		</div>
		<h3>Customize table</h3>
		<h4>1. Select Columns</h4>		
		<div id="columns-options"class="columns grid_12 alpha omega">			
			<div class="customizeColumnsOptions grid_3 alpha">
				<h5><strong>Default columns</strong></h5>			
				<ol id="displayOptions-default">					
					<c:forEach var="annotationColumn" items="${allAnnotationsColumns}">
						<c:if test="${annotationColumn.checkedByDefault}">							
							<c:set var="checked" value="" />							
							<c:forEach var="visibleColumn" items="${sessionScope.visibleAnnotationsColumns}">
								<c:if test="${visibleColumn eq annotationColumn}">								
									<c:set var="checked" value="checked" />							
								</c:if>	
							</c:forEach>	
							<li><input type="checkbox" name="checkbox_display"
								value="${annotationColumn.name}" id="${annotationColumn.name}" ${checked}><label for="${annotationColumn.name}"}">${annotationColumn.description}</label></input></li>
						</c:if>
					</c:forEach>
				</ol>
				<p></p>
				<a id="reset_options" class="button reset" title="Reset to defaults" value="Reset" >Reset to defaults</a>
			</div>
			<div class="customizeColumnsOptions grid_9 omega">
				<h5><strong>Additional columns</strong></h5>			
				<ol id="displayOptions-others">
					<c:forEach var="annotationColumn" items="${allAnnotationsColumns}">
						<c:if test="${!annotationColumn.checkedByDefault}">
							<c:set var="checked" value="" />							
							<c:forEach var="visibleColumn" items="${sessionScope.visibleAnnotationsColumns}">
								<c:if test="${visibleColumn eq annotationColumn}">								
									<c:set var="checked" value="checked" />							
								</c:if>	
							</c:forEach>
							<li><input type="checkbox" name="checkbox_display" id="${annotationColumn.name}" value="${annotationColumn.name}" ${checked}><label for="${annotationColumn.name}">${annotationColumn.description}</label></input></li>
						</c:if>
					</c:forEach>
				</ol>
			</div>
		</div>
		<div id="selectedColumnsArea" class="grid_12 alpha omega">
			<h4>2. Reorder columns</h4>
			<ul id="selectedColumns" class="ui-sortable">
				
			</ul>
		</div>
		<div id="columnsControls">
			<ul class="action-buttons-grouped">
				<li><a id="submit_options" class="button go" title="Save selected columns and use" value="Go" >Go</a></li>				
				<li><a id="cancel_options" class="button cancel" title="Cancel selection and close customization">Cancel</a></li>
			</ul>
		</div>		
	</div>
</div>