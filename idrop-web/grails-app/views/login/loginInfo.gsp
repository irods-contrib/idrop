<div id="defaultResourceDialog">

	 <div class="modal-header">
    		<h3><g:message code="heading.set.resource"/></h3>
  	</div>
  	
  	 <div class="modal-body">
    		<b><g:message code="text.user" />:</b>${irodsAccount.userName}&nbsp;&nbsp;<b><g:message code="text.zone" />:</b>${irodsAccount.zone}&nbsp;&nbsp;<b><g:message code="text.resource" />:</b><g:select name="defaultStorageResource" id="defaultStorageResource" from="${resources}" value="${irodsAccount.defaultStorageResource}" onchange="topBarDefaultResourceChanged()"/>
    		
  	</div>

	<div class="modal-footer">
		<button type="button" id="cancelSetDefaultResource"
			 value="cancelAdd"
			onclick="closeDefaultResourceDialog()")><g:message code="text.cancel" /></button>
  	</div>
	
</div>

<script>

var defaultResourceDialog;
function topBarDefaultResourceChanged() {
	var resource = $("#defaultStorageResource").val();
	if (resource == null) {
		return false;
	}
	setDefaultStorageResource(resource);
	closeDefaultResourceDialog();

}

	$(function() {
		defaultResourceDialog = $("#defaultResourceDialog").dialog(
				{
					"modal":true,
					"width":"500px"
				}
		);
	});

	function closeDefaultResourceDialog() {
		 $("#defaultResourceDialog").dialog("close");
		$("#defaultDialogDiv").html("");
		
	}

	

</script>