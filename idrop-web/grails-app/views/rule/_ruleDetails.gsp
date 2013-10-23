
<g:if test="${flash.error}">
                <script>
                $(function() { setErrorMessage("${flash.error}"); });
                </script>
              </g:if>

              <g:if test="${flash.message}">
                <script>
                $(function() { setMessage("${flash.message}");});
                </script>
              </g:if>
              
<g:form name="ruleDetailsForm" id="ruleDetailsForm">
  <fieldset>
    <label><g:message code="text.rule.body" /></label>
   <g:textArea id="ruleBody" name="ruleBody" value="${rule.ruleBody}" rows="40" cols="100"/>
  
  <g:hiddenField name="ruleAbsPath" value="${absPath}" id="ruleAbsPath"/>
   	
   	<table class="table">
   	 <caption><g:message code="text.input.parameters"/></caption>
   	
	<g:each in="${rule.inputParameters}">
	
		<tr>
			<g:hiddenField name="inputParamName" value="${it.uniqueName}" id="inputParamName"/>
			<td>${it.uniqueName}</td>
			<td><g:textField name="inputParamValue" id="inputParamValue" value="${it.getValueAsStringWithQuotesStripped()}" size="80"/></td>
			<td><i class='icon-remove' onclick='deleteInputParam(${it.uniqueName})'></i></td>	
		</tr>
	
	</g:each>   
   </table>
   
   <br/>
   	<table class="table">
   	 <caption><g:message code="text.output.parameters"/></caption>
   	
	<g:each in="${rule.outputParameters}">
	
		<tr>
			<g:hiddenField name="outputParamName" value="${it.uniqueName}" id="outputParamName"/>
			<td>${it.uniqueName}</td>
			<td><i class='icon-remove' onclick='deleteOutputParam(${it.uniqueName})'></i></td>	
		</tr>
	
	</g:each>   
   
   </table>
   
  </fieldset>
</g:form>
<script type="text/javascript">
$(function() {
    var myCodeMirror = CodeMirror.fromTextArea(document.getElementById('ruleBody'),{
        mode: 'clike',
        lineNumbers: true
      });
});
</script>
		