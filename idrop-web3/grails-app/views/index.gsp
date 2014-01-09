<head>
<meta name="layout" content="main" />
</head>
<div ng-app="home" ng-controller="HomeCtrl" class="container">
		<g:render template="/common/browseContainer" />

	<div id="footer">
		<div class="container">
			<div class="btn-group pull-left pad-vertical">
				<button type="button" class="btn btn-default dropdown-toggle"
					data-toggle="dropdown">
					<span class="glyphicon glyphicon-eye-open"></span> <span
						class="glyphicon-class"></span>
					<g:message code="text.view" />
					<span class="caret"></span>
				</button>
				<ul class="dropdown-menu" role="menu">
				<li ng-show="hideDrives" ng-click="showCollections()"><a href="#home"> <span
							class="glyphicon glyphicon-eye-open"></span> <span
							class="glyphicon-class"><g:message code="text.show.collections" /></span>
					</a></li>
					<li ng-hide="hideDrives" ng-click="hideCollections()"><a href="#home"> <span
							class="glyphicon glyphicon-eye-close"></span> <span
							class="glyphicon-class"><g:message code="text.hide.collections" /></span>
					</a></li>
					<li><a href="#home"> <span
							class="glyphicon glyphicon-eye-open"></span> <span
							class="glyphicon-class"><g:message code="text.show.quick.view" /></span>
					</a></li>
					
				</ul>
			</div>
		</div>
	</div>
</div>
<g:render template="/common/sharedJs" />
<g:javascript src="src/app/home/home.js" />