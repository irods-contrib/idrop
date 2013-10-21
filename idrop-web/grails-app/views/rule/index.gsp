<g:javascript library="mydrop/rule" />
<h3><a ><g:message code="text.rule" /></a></h3>
<div id="detailsTopSection">

	<div id="detailsToolbar" >
			<button type="button" id="reloadRuleButton"
				value="reloadRule"
				onclick="callReloadForRule()")>
				<g:message code="default.button.reload.label" />
			</button>
			<span id="saveRuleButton"><button type="button" id="saveRuleButton"
				value="saveRule"
				onclick="callSaveRule()")>
				<g:message code="text.save" />
			</button></span>
		</div>
	</div>

	<div id="ruleDetailDiv">
		<!-- div for audit table -->
		
<g:form name="ruleDetailsForm" id="ruleDetailsForm">
  <fieldset>
    <label><g:message code="text.rule.body" /></label>
   <g:textArea name="ruleBody" value=" 	${rule.ruleBody}" rows="40" cols="200"/>
  
   	
   	<table class="table">
   	
	<g:each in="rule.inputPaameters">
	
	<td>
		<tr>
			<g:hidden name="paramName" value="it.uniqueName" id="paramName"/>
			<td>${it.uniqueName}</td>
					
		
		
		</tr>
	</td>
	
	
	
	</g:each>   
   
   </table>
   
  </fieldset>
</g:form>
		
	</div>
</div>
