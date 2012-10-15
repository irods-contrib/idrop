<div class="container-fluid">
<g:ifAuthenticated>
  <div class="row-fluid">
    
	<div class="span8">
		<span id="topBarLoginInfo" ></span>
    </div>
    <div class="span2 offset1">
      <button id="logoutButton" name="logoutButton"
				onclick="logout()")><g:message code="text.logout" /></button>
	</div>
  </div>
    </g:ifAuthenticated>
   
    <div class="span12">
      <center>iDrop-web developed by the DICE Group, developers of the <a href="http://www.irods.org">iRODS Data Grid</a> -- iDrop Project Page available <a href="https://code.renci.org/jargon">here</a> - version: ${grailsApplication.metadata.'app.version'} jargon:${org.irods.jargon.core.utils.JargonVersion.VERSION }</center>
    </div>
  </div>
</div>

<script type="text/javascript">
$(function() {	
	var url = "/browse/showLoginBar";
	lcSendValueAndCallbackHtmlAfterErrorCheck(url, "#topBarLoginInfo",
			"#topBarLoginInfo", null);
});



</script>
