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
		<div id="normalLoginForm" class="roundedContainer" style="float:left;clear:both;background:grey">

			 <g:form class="normalLogin" id="normalLogin" method="post" controller="login" action="authenticate">
				
			
				 <g:hiddenField
							name="usePresets" id="usePresets" value="${loginCommand.usePresets}"/>
			   	<g:hasErrors bean="${loginCommand}">
					<div class="errors">
				  <ul>
				   <g:eachError var="err" bean="${loginCommand}">
				       <li><g:message error="${err}" /></li>
				   </g:eachError>
				  </ul>
				  </div>
				</g:hasErrors>
				
				<div id="container" style="height:100%;width:100%;">
			
					<g:if test="${loginCommand.usePresets}">
				
						<g:hiddenField
							name="host" id="host" value="${loginCommand.host}"/>
							<g:hiddenField
							name="port" id="port" value="${loginCommand.port}"/>
							<g:hiddenField
							name="zone" id="zone" value="${loginCommand.zone}"/>
					   <g:hiddenField
							name="defaultStorageResource" id="resource" value="${loginCommand.defaultStorageResource}"/>
				</g:if>
				<g:else>
						<div>
							<div style="width:30%;"><label><g:message code="text.host" />:</label></div>
							<div><input type="text" class="input-text" name="host" id="host" value="${loginCommand.host}"/></div>
						</div>
						<div>
							<div ><label><g:message code="text.port" />:</label></div>
							<div><input type="text" class="input-text" name="port" id="port" value="${loginCommand.port}"/></div>
						</div>
						<div>
							<div ><label><g:message code="text.zone" />:</label></div>
							<div><input type="text" class="input-text" name="zone" id="zone" value="${loginCommand.zone}"/></div>
						</div>
						
				</g:else>
				
				<div>
					<div ><label><g:message code="text.guest.login" />:</label></div>
					<div><g:checkBox name="useGuestLogin" id="useGuestLogin" value="${loginCommand.useGuestLogin}" onclick="toggleGuestLogin()"/></div>
				</div>
				
				<div class="userLoginData">
					<div ><label><g:message code="text.user" />:</label></div>
					<div><input type="text" class="input-text" name="user" id="user" value="${loginCommand.user}"/></div>
				</div>
				<div  class="userLoginData">
					<div><label><g:message code="text.password" />:</label></div>
					<div><input type="password" class="input-text" name="password" id="password" value="${loginCommand.password}"/></div>
				</div>
												
				</span>
				<div>
					<div></div>
					<div><button id="login" name="login" style="margin-left:150px;margin-top:20px;margin-bottom:20px;" ><g:message code="text.login"/></button></div>
				</div>
				<g:if test="${!loginCommand.usePresets}">
				<div>
					<div><label><g:message code="text.resource" />:</label></div>
					<div><input type="text" class="input-text" name="resource" id="resource" value="${loginCommand.defaultStorageResource}"/><stong><g:message code="text.optional" /></strong></div>
				</div>
				</g:if>
				</div> <!-- container div -->
				
			</g:form> 
			
				<!--end-normalLogin-->
		</div>
		</div>
	</div>
	
</div>
</body>
</html>
<script>

	$(function() {
		toggleGuestLogin();

	});


    function toggleGuestLogin() {
		var checkVal = $("#useGuestLogin").attr("checked");
		if (checkVal) {
			$(".userLoginData").hide("slow");
			
		} else {
			$(".userLoginData").show("slow");
		}
	}

</script>



