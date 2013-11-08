<div id="addParameterDialog">

	<div class="modal-header">
		<h3>
			<g:message code="text.add.parameter" />
		</h3>
	</div>

	<div class="modal-body">
		<label for="addParameterName"><g:message
				code="text.parameter.name" />:<g:textField name="addParameterName"
				id="addParameterName" /></label>
		<g:hiddenField name="newParameterAbsPath" id="newParameterAbsPath"
			value="${absPath }" />
		<g:if test="${isInputParameter}">
			<label for="addParameterValue"><g:message
					code="text.parameter.value" />:<g:textField
					name="addParameterValue" id="addParameterValue" /></label>
		</g:if>
		<g:hiddenField name="isInputParameter" id="isInputParameter"
			value="${isInputParameter}" />


	</div>

	<div class="modal-footer">
		<g:if test="${isInputParameter}">
			<button type="button" id="updateNewParameterButton" value="update"
				onclick="submitAddInputParameterDialog()")>
				<g:message code="default.button.update.label" />
			</button>
		</g:if>
		<g:else>
			<button type="button" id="updateNewParameterButton" value="update"
				onclick="submitAddOutputParameterDialog()")>
				<g:message code="default.button.update.label" />
			</button>
		</g:else>
		<button type="button" id="cancelAddButton" value="cancelAdd"
			onclick="closeAddParameterDialog()")>
			<g:message code="text.cancel" />
		</button>
	</div>

</div>

<script>
	$(function() {
		$("#addParameterDialog").dialog({
			"modal" : true,
			"width" : "500px"
		});
	});

	function submitAddInputParameterDialog() {

		var absPath = $("#newParameterAbsPath").val();
		if (absPath == null || absPath == "") {
			setErrorMessage("no absPath for rule");
			return false;
		}

		var inputParamKey = $("#addParameterName").val();
		if (inputParamKey == null || inputParamKey == "") {
			setErrorMessage("no input parameter key for rule");
			return false;
		}

		var inputParamValue = $("#addParameterValue").val();
		if (inputParamValue == null || inputParamValue == "") {
			setErrorMessage("no input parameter value for rule");
			return false;
		}

		var params = {
			ruleAbsPath : absPath,
			addParameterName : inputParamKey,
			addParameterValue : inputParamValue
		}
		var url = "/rule/submitAddInputParameterDialog";

		showBlockingPanel();

		var jqxhr = $.post(context + url, params, "html")
				.success(
						function(returnedData, status, xhr) {
							var continueReq = checkForSessionTimeout(
									returnedData, xhr);
							if (!continueReq) {
								unblockPanel();
								return false;
							}
							$("#ruleDetailDiv").html(returnedData);

							$("#addParameterDialog").dialog("close");
							$("#addParameterDialog").html("");
							unblockPanel();

						}).error(function(xhr, status, error) {
					unblockPanel();

					setErrorMessage(xhr.responseText);
				});
	}

	function closeAddParameterDialog() {
		$("#addParameterDialog").dialog("close");
		$("#addParameterDialog").html("");
	}

	function submitAddOutputParameterDialog() {

		var absPath = $("#newParameterAbsPath").val();
		if (absPath == null || absPath == "") {
			setErrorMessage("no absPath for rule");
			return false;
		}

		var inputParamKey = $("#addParameterName").val();
		if (inputParamKey == null || inputParamKey == "") {
			setErrorMessage("no input parameter key for rule");
			return false;
		}

		var params = {
			ruleAbsPath : absPath,
			addParameterName : inputParamKey,
		}
		var url = "/rule/submitAddOutputParameterDialog";

		showBlockingPanel();

		var jqxhr = $.post(context + url, params, "html")
				.success(
						function(returnedData, status, xhr) {
							var continueReq = checkForSessionTimeout(
									returnedData, xhr);
							if (!continueReq) {
								unblockPanel();
								return false;
							}
							$("#ruleDetailDiv").html(returnedData);

							$("#addParameterDialog").dialog("close");
							$("#addParameterDialog").html("");
							unblockPanel();

						}).error(function(xhr, status, error) {
					unblockPanel();

					setErrorMessage(xhr.responseText);
				});
	}
</script>
