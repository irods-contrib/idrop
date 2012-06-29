<g:javascript library="mydrop/audit" />
<h2>
	<g:message code="heading.audit" />
</h2>
<div id="detailsTopSection" class="box">

	<div id="detailsToolbar" class="fg-toolbar ui-widget-header">
			<button type="button" id="reloadAuditButton"
				class="ui-state-default ui-corner-all" value="reloadAudit"
				onclick="callReloadForAudit()")>
				<g:message code="default.button.reload.label" />
			</button>
		</div>
	</div>
	<g:hiddenField name='auditDetailsAbsPath' id='auditDetailsAbsPath' value='${dataObject.absolutePath}'/>

	<div id="auditTableDiv">
		<!-- div for audit table -->
	</div>
</div>

<script type="text/javascript">

	var origData = "";

	var path = $("#auditDetailsAbsPath").val();
	if (path == null) {
		path = baseAbsPath;
	}
	reloadAuditTable(path);
	
	$(function() {
		reloadAuditTable(path);
	});

	function callReloadForAudit() {
		var absPath = $("#auditDetailsAbsPath").val();
		reloadAuditTable(path);
	}

	
	</script>