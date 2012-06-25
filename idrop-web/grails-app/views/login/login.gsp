<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
	"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
<title>iDrop-web - iRODS Personal Cloud"</title>
<g:javascript library="jquery-1.7.1.min" />
<g:javascript library="jquery-ui-1.8.7.custom.min" />
<link rel="stylesheet" href="${resource(dir:'css',file:'main.css')}" />
<link rel="stylesheet" href="${resource(dir:'css',file:'base.css')}" />
<link rel="stylesheet" href="${resource(dir:'css',file:'style.css')}" />
<link rel="stylesheet"
	href="${resource(dir:'css',file:'reset-fonts-grids.css')}" />
<link rel="stylesheet" href="${resource(dir:'css',file:'start/jquery-ui-1.8.18.custom.css')}" />

</head>
<body style="height:100%;">
<div id="bannercontainer">
	<!--  image banner -->
</div>
		
		<div id="normalLoginWrapper">
		<!--begin-normalLogin-->
		<div id="normalLoginForm" class="roundedContainer" style="float:left;clear:both;">

			 <g:form class="normalLogin" id="normalLogin" method="POST" controller="login" action="authenticate">
				
			
			   	<g:hasErrors bean="${loginCommand}">
					<div class="errors">
				  <ul>
				   <g:eachError var="err" bean="${loginCommand}">
				       <li><g:message error="${err}" /></li>
				   </g:eachError>
				  </ul>
				  </div>
		</g:hasErrors>
			
					<g:if test="${loginCommand.usePresets}">
				
						<g:hiddenField
							name="host" id="host" value="${loginCommand.host}"/>
							<g:hiddenField
							name="port" id="port" value="${loginCommand.port}"/>
							<g:hiddenField
							name="zone" id="zone" value="${loginCommand.zone}"/>
					   <g:hiddenField
							name="resource" id="resource" value="${loginCommand.defautStorageResoruce}"/>
				</g:if>
				<g:else>
					<label><g:message code="text.host" />:</label><input type="text" class="input-text" name="host" id="host" value="${loginCommand.host}"/><br/>
					<label><g:message code="text.port" />:</label><input type="text" class="input-text" name="port" id="port" value="${loginCommand.port}"/><br/>
					<label><g:message code="text.zone" />:</label><input type="text" class="input-text" name="zone" id="zone" value="${loginCommand.zone}"/><br/>
					<label><g:message code="text.resource" />:</label><input type="text" class="input-text" name="resource" id="resource" value="${loginCommand.defaultStorageResource}"/><g:message code="text.optional" /><br/>
				</g:else>
				<label><g:message code="text.user" />:</label><input type="text" class="input-text" name="user" id="user" value="${loginCommand.user}"/><br/>
				<label><g:message code="text.password" />:</label><input type="password" class="input-text" name="password" id="password" value="${loginCommand.password}"/></br>
								<button id="login" name="login" style="float:right;margin:2px;" ><g:message code="text.login"/></button>
					
			</g:form> 
			
				<!--end-normalLogin-->
		</div>
		
		
		</div>
	</div>
	
</div>
</body>
</html>
<script>
	var loginUrl = "/login/authenticate"
	/*$(function() {
    	$("#tabs").tabs();
    	
    });*/


	function normalLogin() {
		// see if there is form data (users in a pick list) that are selected
		var formData = $("#normalLogin").serializeArray();
		context = "${request.contextPath}";
	
		if (formData == null) {
			setErrorMessage(jQuery.i18n.prop('msg_no_login'));
			return false;
		}
	
		var jqxhr = $.post(context + loginUrl, formData,
				function(data, status, xhr) {

					// if i have error data, redisplay the normal login part of the form (I know, it's kind of a hack)
					var begin = data.indexOf("<!--begin-normalLogin-->");
					var end = data.indexOf("<!--end-normalLogin-->") + 22;
					var parsedResponse = data.substring(begin, end);
					
					$("#normalLoginWrapper").html("yo...." + parsedResponse + "...oy");
					//$("#normalLoginWrapper").html("yo....");
					return false;
		});
		
	}

    
</script>



