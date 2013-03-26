
<div id="hivePanelInner" class="container-fluid">
	<g:if test="${searchResult}">
		<div class="row-fluid">
			<div class="span10 offset1">
				<button type="button" class="btn" data-toggle="collapse" data-target="#searchResultCollapseArea">Show/Hide Results</button>
			</div>
		</div>
		</g:if>
		<div class="row-fluid " id="searchResultCollapseArea">
			<div class="span10 offset1 well">
				<div class="well">
					
				
					<table class="table table-striped">
					<caption>Search Result</caption>
						<thead>
							<tr>
							<th>Found Terms</th>
							<th>Vocabulary</th>
							</tr>
						</thead>
							<g:each in="${searchResult}" var="result">
								<tr>
									<td>
										<span onclick="processSelectOfTermAsCurrent('${result.URI}')">${result.preLabel}</span>
									</td>
									<td>
										${result.origin}
									</td>
								</tr>
							</g:each>
						</tr>
						 </tbody>
					</table>
				</div>
			</div>

		</div>

</div>
