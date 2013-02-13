<head>
<meta name="layout" content="mainNoSidebar" />
<g:javascript library="mydrop/profile" />
</head>

<g:if test="${flash.error}">
	<script>
   $(function() { setMessage("${flash.error}"); });
   </script>
</g:if>

<g:if test="${flash.message}">
	<script>
     $(function() { setMessage("${flash.message}");
		});
	</script>
</g:if>

<div id="profileFormDiv">


	<g:hasErrors bean="${userProfile}">
		<div class="alert">
			<ul>
				<g:eachError var="err" bean="${userProfile}">
					<li><g:message error="${err}" /></li>
				</g:eachError>
			</ul>
		</div>
	</g:hasErrors>



	<div class="row-fluid">
		<div class="span12"> 
			<g:form name="userProfileForm" class="form-horizontal" controller="profile"
				action="updateProfile">
				<div class="control-group">
					<label class="control-label"><g:message code="text.user" /></label>
					<div class="controls">
						${userProfile.userName}
						<g:hiddenField name="userName" value="${userProfile.userName}"/>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label"><g:message
							code="text.nickname" /></label>
					<div class="controls">
						<g:textField id="nickName" name="nickName"
							value="${userProfile.nickName}" />
					</div>
				</div>

				<div class="control-group">
					<label class="control-label"><g:message code="text.title" /></label>
					<div class="controls">
						<g:textField id="title" name="title" value="${userProfile.title}" />
					</div>
				</div>
				
				
				<div class="control-group">
					<label class="control-label"><g:message code="text.url" /></label>
					<div class="controls">
						<g:textField id="labeledURL" name="labeledURL" value="${userProfile.labeledURL}" />
					</div>
				</div>
				

				<div class="control-group">
					<label class="control-label"><g:message
							code="text.description" /></label>
					<div class="controls">
						<g:textField id="description" name="description"
							value="${userProfile.description}" />
					</div>
				</div>

				<div class="control-group">
					<label class="control-label"><g:message
							code="text.first.name" /></label>
					<div class="controls">
						<g:textField id="givenName" name="givenName"
							value="${userProfile.givenName}" />
					</div>
				</div>

				<div class="control-group">
					<label class="control-label"><g:message
							code="text.last.name" /></label>
					<div class="controls">
						<g:textField id="lastName" name="lastName"
							value="${userProfile.lastName}" />
					</div>
				</div>

				<div class="control-group">
					<label class="control-label"><g:message code="text.address" /></label>
					<div class="controls">
						<g:textField id="postalAddress" name="postalAddress"
							value="${userProfile.postalAddress}" />
					</div>
				</div>

				<div class="control-group">
					<label class="control-label"><g:message
							code="text.post.office.box" /></label>
					<div class="controls">
						<g:textField id="postOfficeBox" name="postOfficeBox"
							value="${userProfile.postOfficeBox}" />
					</div>
				</div>

				<div class="control-group">
					<label class="control-label"><g:message code="text.city" /></label>
					<div class="controls">
						<g:textField id="city" name="city" value="${userProfile.city}" />
					</div>
				</div>


				<div class="control-group">
					<label class="control-label"><g:message code="text.state" /></label>
					<div class="controls">
						<g:textField id="state" name="state" value="${userProfile.state}" />
					</div>
				</div>

				<div class="control-group">
					<label class="control-label"><g:message
							code="text.postal.code" /></label>
					<div class="controls">
						<g:textField id="postalCode" name="postalCode"
							value="${userProfile.postalCode}" />
					</div>
				</div>

				<div class="control-group">
					<label class="control-label"><g:message
							code="text.telephone.number" /></label>
					<div class="controls">
						<g:textField id="telephoneNumber" name="telephoneNumber"
							value="${userProfile.telephoneNumber}" />
					</div>
				</div>



				<div class="control-group">
					<label class="control-label"><g:message code="text.email" /></label>
					<div class="controls">
						<g:textField id="email" name="email" value="${userProfile.email}" />
					</div>
				</div>



				<div class="control-group">
					<div class="controls">
						<button type="button" id="updateProfile" value="updateProfile"
							onclick="updateUserProfile()">
							<g:message code="text.update" />
						</button>
						<button type="button" id="reloadProfile" value="reloadProfile"
							onclick="loadProfileData()">
							<g:message code="text.cancel" />
						</button>
					</div>
				</div>
		</div>
		</g:form>
	</div>
</div>
</dov>
<script>
$(document).ready(function() {

		$.ajaxSetup({
			cache : false
		});
		$("#topbarPreferences").addClass("active");
		quickViewShowStarredFiles();

	});
</script>