<div id="download-stats-menu" class="options-menu"
	style="z-index: 3; top: 34px; display: none;">
	<a style="float: right" href="#" class="closeBox" id="closeDownloadStatsMenu">x</a>	
	<h6>QuickGO Statistics Download</h6>
	<label class="bold-label">Categories:</label><br>
		<input type="checkbox" name="statsCategory" value="goID" checked="checked"/> GO ID<br>
		<input type="checkbox" name="statsCategory" value="goAspect" checked="checked"/> Aspect<br>
		<input type="checkbox" name="statsCategory" value="goEvidence" checked="checked"/> Evidence<br>
		<input type="checkbox" name="statsCategory" value="dbXref" checked="checked"/> Reference<br>
		<input type="checkbox" name="statsCategory" value="taxonomyId" checked="checked"/> Taxon<br>
		<input type="checkbox" name="statsCategory" value="assignedBy" checked="checked"/> Assigned by<br>
	<label class="bold-label">Grouped by:</label><br>
		<input type="radio" name="statsBy" value="annotation">Annotation count<br>
		<input type="radio" name="statsBy" value="protein">Protein count<br>
		<input type="radio" name="statsBy" value="both" checked="checked">Both<br><br>
	
	<a id="download-stats-menu-go" class="button">Go</a>
</div>