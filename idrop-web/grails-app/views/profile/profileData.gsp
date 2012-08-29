<div id="container" style="height:100%;width:100%;">
				<div>
					<div style="width:15%;"><label><g:message code="text.user"/></label></div>
					<div>${userProfile.userName}</div>
				</div>
				<div>
					<div style="width:15%;"><label><g:message code="text.nickname"/></label></div>					
					<div><g:textField id="nickName" name="nickName"
					value="${userProfile.userProfilePublicFields.nickName}" />
					</div>
				</div>
				<div>
					<div style="width:15%;"><label><g:message code="text.personal.blurb"/></label></div>
					<div><g:textArea id="description" name="description" rows="3" cols="40"
					value="${userProfile.userProfilePublicFields.description}" /></div>
				</div>
				<div>
					<div style="width:15%;"><label><g:message code="text.email"/></label></div>
					<div><g:textField id="email" name="email"
					value="${userProfile.userProfileProtectedFields.mail}" /></div>
				</div>	
				<div>
					<div></div>
					<div><button type="button" class="ui-state-default ui-corner-all" id="updateProfile" value="updateProfile" onclick="updateProfile()"><g:message code="text.update"/></button>
					<button type="button" class="ui-state-default ui-corner-all" id="reloadProfile" value="reloadProfile" onclick="loadProfileData()"><g:message code="text.cancel"/></button>
					</div>
				</div>
</div>