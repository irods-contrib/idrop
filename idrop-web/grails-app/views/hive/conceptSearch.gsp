<div id="hivePanelInner" class="container-fluid">
	<g:if test="${searchResult}">
		<div class="row-fluid">
			<div class="span10 offset1">
				<button type="button" class="btn" data-toggle="collapse" data-target="#searchResultCollapseArea"><g:message code="text.show.hide.results" /></button>
			</div>
		</div>
		</g:if>
		<div class="row-fluid " id="searchResultCollapseArea" class="collapse">
			<div class="span10 offset1 well">
				<div class="well">
					
				
					<table class="table table-striped">
					<caption><g:message code="text.search.result" /></caption>
						<thead>
							<tr>
							<th><g:message code="text.term" /></th>
							<th><g:message code="text.vocabulary" /></th>
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
