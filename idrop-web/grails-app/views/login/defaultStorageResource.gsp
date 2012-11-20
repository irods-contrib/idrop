<head>
<meta name="layout" content="mainNoSidebar" />
<g:javascript library="mydrop/home" />
</head>
<div class="row-fluid">
	<div class="span5 offset2">
		<h4><g:message code="heading.set.resource"/></h4>
	</div>
</div>
<div class="row-fluid">
	<div class="span5 offset2">
	<form class="form-horizontal">
  	 		<fieldset>
  	 			<div class="control-group">
	  	 			<label  class="control-label"><g:message code="text.user" /></label>
	  	 			<div class="controls">${irodsAccount.userName}</div>
  	 			</div>
  	 			<div class="control-group">
	  	 			<label  class="control-label"><g:message code="text.zone" /></label>
	  	 			<div class="controls">${irodsAccount.zone}</div>
	  	 		</div>
	  	 		<div class="control-group">
	  	 			<label  class="control-label"><g:message code="text.resource" /></label>
    				<div class="controls"><g:select name="defaultStorageResource" id="defaultStorageResource" from="${resources}" value="${irodsAccount.defaultStorageResource}" 
    				onchange="topBarDefaultResourceChanged()"/></div>
	  	 		</div>
  	 	
    		</fieldset>
    	</form>
    </div>
</div>
<script>


function topBarDefaultResourceChanged() {
	//showBlockingPanel();
	try {
	var resource = $("#defaultStorageResource").val();
		if (resource == null) {
			return false;
		}
		setDefaultStorageResource(resource);
		//setMessage(jQuery.i18n.prop('msg_resource_changed'));
	} finally {
		//unblockPanel();
	}

}

</script>