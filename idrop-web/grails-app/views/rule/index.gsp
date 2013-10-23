<g:javascript library="mydrop/rule" />
<h3><a ><g:message code="text.rule" /></a></h3>
<div id="detailsTopSection">

	<div id="detailsToolbar" >
			<button type="button" id="reloadRuleButton"
				value="reloadRule"
				onclick="callReloadRule()">
				<g:message code="default.button.reload.label" />
			</button>
			<span id="saveRuleButton"><button type="button" id="saveRuleButton"
				value="saveRule"
				onclick="callSaveRule()">
				<g:message code="text.update" />
			</button></span>
		</div>
	</div>

	<div id="ruleDetailDiv">
		<!-- div for audit table -->
		<g:render template="/rule/ruleDetails" />
	</div>
	<script type="text/javascript">
		

	function deleteInputParam(param) {
		alert("input param:" + param);
	}


	function deleteOutputParam(param) {
		alert("output param:" + param);
	}

	function callSaveRule() {
		var formData = $("#ruleDetailsForm").serializeArray();
		var jqxhr = $.post(context + "/rule/updateRule", formData, "html").success(
				function(returnedData, status, xhr) {
					var continueReq = checkForSessionTimeout(returnedData, xhr);
					if (!continueReq) {
						return false;
					}
					setMessage("rule saved");
					$("#ruleDetailDiv").html(returnedData);
				}).error(function(xhr, status, error) {
			setErrorMessage(xhr.responseText);
		});

		
	}

	function callReloadRule(absPath) {
		var absPath = $("#ruleAbsPath").val();
		if (absPath == null || absPath == "") {
			showError("no absPath for rule");
			return false;
		}
		
		var params = {
				absPath : absPath
			}
		var jqxhr = $.get(context + "/rule/reloadRule", params, "html").success(
				function(returnedData, status, xhr) {
					var continueReq = checkForSessionTimeout(returnedData, xhr);
					if (!continueReq) {
						return false;
					}
					$("#ruleDetailDiv").html(returnedData);
				}).error(function(xhr, status, error) {
			setErrorMessage(xhr.responseText);
		});

		
	}
	

	
	</script>
