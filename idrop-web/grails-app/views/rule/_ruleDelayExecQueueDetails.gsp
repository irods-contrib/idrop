	<form id="delayExecForm" name="delayExecForm">

<table id="delayExecQueueTable" class="table table-striped table-hover"
		cellspacing="0" cellpadding="0" border="0">
		<thead>
			<tr>
				<th>
					<div class="btn-group">
						<a class="btn dropdown-toggle" data-toggle="dropdown" href="#">Action<span
							class="caret"></span></a>
						<ul class="dropdown-menu">
							<li id="menuDeleteDetails"><a href="#deleteAllDetails"
								onclick="deleteRulesBulkAction()"><g:message code="text.delete.all" /></a></li>
							<!-- dropdown menu links -->
						</ul>
					</div>

				</th>
				<th><g:message code="text.name" /></th>
				<th><g:message code="text.user" /></th>
				<th><g:message code="text.last.exec.time" /></th>
				<th><g:message code="text.frequency" /></th>

			</tr>
		</thead>
		<tbody>
			<g:each in="${rules}" var="rule">

				<tr id="rule-${rule.id}">

					<td><g:checkBox name="selectDetail"
							value="select-${rule.id}" checked="false" />
					</td>
					<td>
						${rule.name}
					</td>
					<td>
						${rule.userName}
					</td>
					<td>
						${rule.lastExecTime}
					</td>
					<td>
						${rule.frequency}
					</td>
				</tr>
			</g:each>

		</tbody>

		<tfoot>
			<tr>
				<td></td>
				<td></td>
				<td></td>
				<td></td>
				<td></td>
			</tr>
		</tfoot>
	</table>
	</form>