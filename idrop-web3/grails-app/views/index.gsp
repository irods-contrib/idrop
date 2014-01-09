<head>
<meta name="layout" content="main" />
</head>
<div ng-app="home" ng-controller="HomeCtrl">
	<div class="container">
		<g:render template="/common/browseContainer" />
	</div>

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
					<li><a href="#">Show Collections</a></li>
					<li><a href="#">Show Quick View</a></li>
				</ul>
			</div>
			<span>Hello {{name}}</span>
		</div>
	</div>
</div>
<g:render template="/common/sharedJs" />
<g:javascript src="src/app/home/home.js" />