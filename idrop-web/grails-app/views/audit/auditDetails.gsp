<g:javascript library="mydrop/audit" />
<h3><a ><g:message code="text.audit" /></a></h3>
<div id="detailsTopSection">

	<div id="detailsToolbar" >
			<button type="button" id="reloadAuditButton"
				value="reloadAudit"
				onclick="callReloadForAudit()")>
				<g:message code="default.button.reload.label" />
			</button>
			<span id="backwardAuditButton"><button type="button" id="backwardAuditButton"
				value="backwardAudit"
				onclick="backwardAudit()")>
				<g:message code="text.backwards" />
			</button></span>
			<span id="forwardAuditButton"><button type="button" id="forwardAuditButton"
				value="forwardAudit"
				onclick="forwardAudit()")>
				<g:message code="text.forward" />
			</button></span>
		</div>
	</div>
	<g:hiddenField name='auditDetailsAbsPath' id='auditDetailsAbsPath' value='${dataObject.absolutePath}'/>

	<div id="auditTableDiv">
		<!-- div for audit table -->
	</div>
</div>

<script type="text/javascript">

	var origData = "";
	var pageableForward = false;
	var pageableBackwards = false;
	var firstCount = 0;
	var lastCount = 0;
	var auditPageSize = 1000;

	var path = $("#auditDetailsAbsPath").val();
	if (path == null) {
		path = baseAbsPath;
	}
	
	$(function() {
		reloadAuditTable(path);
	});

	function callReloadForAudit() {
		var absPath = $("#auditDetailsAbsPath").val();
		reloadAuditTable(path);
	}

	function backwardAudit() {
		if (pageableBackwards == false) {
			return false
		}
		var newOffset = firstCount - auditPageSize;
		if (newOffset < 0) {
			newOffset = 0;
		}
		reloadAuditTable(path, newOffset, auditPageSize);
		
	}

	function forwardAudit() {
		if (pageableForward == false) {
			return false
		}
		
		var newOffset = lastCount;
		reloadAuditTable(path, newOffset, auditPageSize);
	}

	
	</script>