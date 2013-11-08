
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
              
              
<div id="addParamDialogDiv">
<!--  area for hanging dialogs -->
</div>
              
<g:form name="ruleDetailsForm" id="ruleDetailsForm">
  <fieldset>
    <label></label>
   <g:textArea id="ruleBody" name="ruleBody" value="${rule.ruleBody}" rows="80" cols="100"/>
  
  <g:hiddenField name="ruleAbsPath" value="${absPath}" id="ruleAbsPath"/>
   	
   	<table class="table alert alert-info">
   	 <caption><g:message code="text.input.parameters"/></caption>
   	
	<g:each in="${rule.inputParameters}">
	
		<tr>
			<g:hiddenField name="inputParamName" value="${it.uniqueName}" id="inputParamName"/>
			<td>${it.uniqueName}</td>
			<td><g:textField name="inputParamValue" id="inputParamValue" value="${it.getStringValue()}" size="80"/></td>
			<td><i class='icon-remove' onclick='deleteInputParam(${"\"" + it.uniqueName + "\""})'></i></td>	
		</tr>
	
	</g:each>   
   </table>
   <div id="inputParamsToolbar" >
			<button type="button" id="addInputParameterButton" 
				value="addInputParameter"
				onclick="callAddInputParameter()">
				<i class="icon-plus"></i><g:message code="text.add.input.parm" />
			</button>
	</div>
	
   
   <br/>
   	<table class="table alert alert-info">
   	 <caption><g:message code="text.output.parameters"/></caption>
   	
	<g:each in="${rule.outputParameters}">
		<tr>
			<g:hiddenField name="outputParamName" value="${it.uniqueName}" id="outputParamName"/>
			<td>${it.uniqueName}</td>
			<td><i class='icon-remove' onclick='deleteOutputParam(${"\"" + it.uniqueName + "\""})'></i></td>	
		</tr>
	
	</g:each>   
   
   </table>
    <div id="outputParamsToolbar" >
			<button type="button" id="addOutputParameterButton" 
				value="addOutputParameter"
				onclick="callAddOutputParameter()">
				<i class="icon-plus"></i><g:message code="text.add.output.parm" />
			</button>
	</div>
   
  </fieldset>
</g:form>
<script type="text/javascript">

var editor = null;
$(function() {
   /* var myCodeMirror = CodeMirror.fromTextArea(document.getElementById('ruleBody'),{
        mode: 'clike',
        lineNumbers: true,
        theme: "blackboard"
      }); */

	var uiOptions = { path : 'js/', searchMode: 'popup' }
	var codeMirrorOptions = {
    		    mode: 'text/x-rule',
    	        lineNumbers: true,
    	        theme: "eclipse"
	}

	//then create the editor
	editor = new CodeMirrorUI(document.getElementById('ruleBody'),uiOptions,codeMirrorOptions);

});
</script>
		