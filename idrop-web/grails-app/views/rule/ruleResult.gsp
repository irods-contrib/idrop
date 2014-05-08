
<div class="container">
	<div class="row-fluid content">
		<div class="span12">

			<table class="table">
				<g:each in="${ruleResult.outputParameterResults.keySet()}">

					<g:if test="${it == "ruleExecOut" || it == "ruleExecErrorOut" }">
					</g:if>
					<g:else>
						<tr>
							<td>
								${it}
							</td>
							<td>
								${ruleResult.outputParameterResults.get(it).resultObject}
							</td>
						</tr>
					</g:else>
				</g:each>
			</table>

		</div>
	</div>
	<div class="row-fluid content alert alert-success">
		<div class="span12">
			${execOut}
		</div>
	</div>
	<div class="row-fluid content alert alert-error">
		<div class="span12">
			${errorOut}
		</div>
	</div>
</div>





