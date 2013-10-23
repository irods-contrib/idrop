	<div class="container">
		<div class="row-fluid content">
			<div class="span12">

				<table class="table">
					<g:each in="${ruleResult.outputParameterResults.keySet()}">
						<tr>
							<td>
								${it}
							</td>
							<td>
								${ruleResult.outputParameterResults.get(it).resultObject}
							</td>
						</tr>
					</g:each>
				</table>

			</div>
		</div>
	</div>





