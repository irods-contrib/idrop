<head>
<meta name="layout" content="mainNoSidebar" />
</head>
<div id="delayExecQueueDiv">
		<g:render template="ruleDelayExecQueueDetails" />
</div>
<script>
$(document).ready(function() {

		$.ajaxSetup({
			cache : false
		});
		$("#topbarTools").addClass("active");
	});


function deleteRulesBulkAction() {

	var formData = $("#delayExecForm").serializeArray();
	showBlockingPanel();

	var jqxhr = $.post(context + "/rule/deleteDelayExecQueue", formData, "html")
			.success(function(returnedData, status, xhr) {
				var continueReq = checkForSessionTimeout(returnedData, xhr);
				if (!continueReq) {
					return false;
				}
				
				setMessage("Delete action successful");
				$("#delayExecQueueDiv").html(returnedData);
				unblockPanel();
			}).error(function(xhr, status, error) {
				setErrorMessage(xhr.responseText);
				unblockPanel();
			});

}

</script>