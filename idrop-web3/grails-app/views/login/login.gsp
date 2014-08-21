<head>
<meta name="layout" content="main" />
<g:javascript src="controllers/login.js" />
</head>
<div class="container"  ng-controller="LoginCtrl" >
	<form class="form-horizontal" action='' method="POST">
		<fieldset>
			<div id="legend">
					<g:message code="text.login" />
			</div>
			<div class="control-group">
				<!-- Host -->
				<label class="control-label" for="host"><g:message
						code="text.host" /></label>
				<div class="controls">
					<input type="text" id="host" name="host" placeholder=""
						class="input-xlarge" ng-model="login.host">
				</div>
			</div>

			
			<div class="control-group">
		
<%-- 		XXX: Refer to Config.groovy variables from this view?
			<g:if test="${true == false}">
				<!-- Port -->
				<label class="control-label" for="port"><g:message
						code="text.port" /></label>
				<div class="controls">
					<input type="text" id="port" name="port" placeholder="1247"
						class="input-xlarge" ng-model="login.port">
				</div> 
			</g:if>
--%>			
			</div>

			<div class="control-group">
				<!-- Zone -->
				<label class="control-label" for="zone"><g:message
						code="text.zone" /></label>
				<div class="controls">
					<input type="text" id="zone" name="zone" placeholder=""
						class="input-xlarge" ng-model="login.zone">
				</div>
			</div>

			<div class="control-group">
				<!-- Username -->
				<label class="control-label" for="username"><g:message
						code="text.username" /></label>
				<div class="controls">
					<input type="text" id="username" name="username" placeholder=""
						class="input-xlarge" ng-model="login.username">
				</div>
			</div>

			<div class="control-group">
				<!-- Password-->
				<label class="control-label" for="password"><g:message
						code="text.password" /></label>
				<div class="controls">
					<input type="password" id="password" name="password" placeholder=""
						class="input-xlarge" ng-model="login.password">
				</div>
			</div>


			<div class="control-group">
<%--		
			<g:if test="${true == false}">
				<!-- authmethod-->
				<label class="control-label" for="authtype"><g:message
						code="text.auth.type" /></label>
				<div class="controls">
					<g:select name="authMethod" from="${['Standard', 'PAM']}" value="" ng-model="login.authmethod"  />
				</div>
			</g:if>
--%>		
			</div>

			<div class="control-group">
				<!-- Button -->
				<div class="controls">
				<button class="btn btn-success button-pad" ng-click="doReset()">
						<g:message code="text.reset" />
					</button>
					<button class="btn btn-success button-pad" ng-click="doLogin()">
						<g:message code="text.login" />
					</button>
				</div>
			</div>
		</fieldset>
	</form>
</div>