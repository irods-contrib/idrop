<div id="userInfoDialog" class="roundedContainer">
	<h2><g:message code="heading.user.information" /></h2>
	<div id="userDialogMessageArea" style="width:90%;">
		<!--  no empty divs -->
	</div>
	 <div id="userInfoMenu" class="fg-buttonset fg-buttonset-multi"
       style="float: left, clear:both; margin: 5px;">

    <button type="button" id="btnShowSharedWith"
            class="ui-state-default ui-corner-all" value="sharedWithUser"
            onclick="xxx()")>
            <g:message code="text.show.shared.with" />
    </button>   
    <button type="button" id="btnShowSharedBy"
            class="ui-state-default ui-corner-all" value="sharedByUser"
            onclick="xxx()")>
            <g:message code="text.show.shared.by" />
    </button>   
  </div>
	<fieldset id="verticalForm">
		<label><b><g:message code="text.user" />:</label>${user.name}
		<label><b><g:message code="text.type" />:</label>${user.userType}
		<label><b><g:message code="text.zone" />:</label>${user.zone}
</fieldset>
<script>

	$(function() {
		$("#userInfoDialog").dialog({width:500, modal:true});
	});


</script>
