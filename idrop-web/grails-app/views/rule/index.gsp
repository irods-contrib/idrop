<g:javascript library="mydrop/rule" />
<h3><a ><g:message code="text.rule" /></a></h3>
<div id="detailsTopSection">

	<div id="detailsToolbar" >
			<button type="button" id="reloadRuleButton"
				value="reloadRule"
				onclick="callReloadForRule()">
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
		
<g:form name="ruleDetailsForm" id="ruleDetailsForm">
  <fieldset>
    <label><g:message code="text.rule.body" /></label>
   <g:textArea name="ruleBody" value="${rule.ruleBody}" rows="40" cols="100"/>
  
   	
   	<table class="table">
   	
	<g:each in="${rule.inputParameters}">
	
		<tr>
			<g:hiddenField name="paramName" value="${it.uniqueName}" id="paramName"/>
			<td>${it.uniqueName}</td>
			<td><g:textField name="parmValue" id="parmValue" value="${it.getValueAsStringWithQuotesStripped()}" size="80"/></td>
			<td><i class='icon-remove' onclick='deleteParam(${it.stringValue})'></i></td>	
		</tr>
	
	</g:each>   
   
   </table>
   
  </fieldset>
</g:form>
		
	</div>
	<script type="text/javascript">
		

	function deleteParam(param) {
		alert("param:" + param);
	}

	
	</script>
