<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
	"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
<title>iDrop-web - iRODS Personal Cloud"</title>

<style type="text/css">
body{
font-family:"Lucida Grande", "Lucida Sans Unicode", Verdana, Arial, Helvetica, sans-serif;
font-size:12px;
}
p, h1, form, button{border:0; margin:0; padding:0;}
.spacer{clear:both; height:1px;}
/* ----------- My Form ----------- */
.myform{
margin:0 auto;
width:400px;
height:400px;
padding:14px;
}

/* ----------- stylized ----------- */
#stylized{
border:solid 2px #b7ddf2;
background:#ebf4fb;
display:block;
}
#stylized h1 {
font-size:14px;
font-weight:bold;
margin-bottom:8px;
}
#stylized p{
font-size:11px;
color:#666666;
margin-bottom:20px;
border-bottom:solid 1px #b7ddf2;
padding-bottom:10px;
}
#stylized label{
display:block;
font-weight:bold;
text-align:right;
width:140px;
float:left;
}
#stylized .small{
color:#666666;
display:block;
font-size:11px;
font-weight:normal;
text-align:right;
width:140px;
}
#stylized input{
float:left;
font-size:12px;
padding:4px 2px;
border:solid 1px #aacfe4;
width:200px;
margin:2px 0 20px 10px;
}
#stylized button{
clear:both;
margin-left:150px;
width:125px;
height:31px;
background:#666666 url(img/button.png) no-repeat;
text-align:center;
line-height:31px;
color:#FFFFFF;
font-size:11px;
font-weight:bold;
}
</style>
<link rel="stylesheet" type="text/css" href="${grailsApplication.config.grails.serverURL}/css/main.css" />
<link rel="stylesheet" type="text/css" href="${grailsApplication.config.grails.serverURL}/css/base.css" />
<link rel="stylesheet" type="text/css" href="${grailsApplication.config.grails.serverURL}/css/style.css" />
</head>
<body style="height:100%;">

<div id="bannercontainer">
	<!--  image banner -->
</div>
<div id="stylized" style="width: 60%;height:80%;">
 <g:form class="myform" controller="j_spring_security_check"
	 method="post" >
	

    <g:if test="${request.login_error}">
		<div class="message" style="margin:10px;">
		${request.login_error}
		</div>
	</g:if>


		<g:if test="${presetHost}">
	
			<g:hiddenField
				name="host" id="host" value="${presetHost}"/>
				<g:hiddenField
				name="port" id="port" value="${presetPort}"/>
				<g:hiddenField
				name="zone" id="zone" value="${presetZone}"/>
		   <g:hiddenField
				name="resource" id="resource" value="${presetResource}"/>
	</g:if>
	<g:else>
		<label><g:message code="text.host" />:</label><input type="text" class="input-text" name="host" id="host" value="${host}"/><br/>
		<label><g:message code="text.port" />:</label><input type="text" class="input-text" name="port" id="port" value="${port}"/><br/>
		<label><g:message code="text.zone" />:</label><input type="text" class="input-text" name="zone" id="zone" value="${zone}"/><br/>
		<label><g:message code="text.resource" />:</label><input type="text" class="input-text" name="resource" id="resource" value="${resource}"/><br/>
	</g:else>
	<label><g:message code="text.user" />:</label><input type="text" class="input-text" name="user" id="user" value="${user}"/><br/>
	<label><g:message code="text.password" />:</label><input type="password" class="input-text" name="password" id="password" value="${password}"/></br>
	<button type="submit" id="login" name="login" style="float:right;margin:10px;"><g:message code="text.login"/></button>
	
</g:form> 
</div>
</body>
</html>

