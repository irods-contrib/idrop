<head>
<meta name="layout" content="mainNoSidebar" />
<g:javascript library="mydrop/profile" />
</head>
<div class="row-fluid">
   <div class="span12">
   		<form class="form-horizontal">
   			 <div class="control-group">
		    	<label class="control-label"><g:message code="text.user"/></label>
		     	<div class="controls">
		    		${userProfile.userName}
		    	</div>
	    	</div>
	    	<div class="control-group">
		    	<label class="control-label"><g:message code="text.nickname"/></label>
		    	<div class="controls"><g:textField id="nickName" name="nickName" value="${userProfile.userProfilePublicFields.nickName}" /></div>
		    </div>
		    <div class="control-group">
		    	<label class="control-label"><g:message code="text.personal.blurb"/></label>
		    	<div class="controls">
		    		<g:textArea id="description" name="description" rows="3" cols="40" value="${userProfile.userProfilePublicFields.description}" />
		    	</div>
		    </div>
		    <div class="control-group">
		    	<label class="control-label"><g:message code="text.email"/></label>
		    	<div class="controls">
			    	<g:textField id="email" name="email"
								value="${userProfile.userProfileProtectedFields.mail}" />
		    	</div>
		    </div>
		    <div class="control-group">
		    	<div class="controls">
			    	<button type="button" id="updateProfile" value="updateProfile" onclick="updateUserProfile()"><g:message code="text.update"/></button>
					<button  type="button" id="reloadProfile" value="reloadProfile" onclick="loadProfileData()"><g:message code="text.cancel"/></button>
		    	</div>
		    </div>
		   	
			</div>
		</form>
   </div>
</div>				

<script>


</script>