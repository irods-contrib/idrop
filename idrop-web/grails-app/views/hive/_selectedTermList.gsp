<div id="SelectedTermInner" class="container-fluid">
	<div class="row-fluid">
		<div class="span10 offset1">
			<button type="button" class="btn btn-link" style="display:block" data-toggle="collapse" data-target="#appliedTermsCollapseArea"><g:message code="text.show.hide.applied.terms" /></button>
		</div>
	</div>
	
	<div class="row-fluid " id="appliedTermsCollapseArea" class="collapse">
		<div class="span10 offset1 well">
			<div class="well">
				<table class="table table-striped">
					<h3><g:message code="text.applied.terms" /></h3>
						<thead>
							<tr style="color:#0088CC">
							<th><g:message code="text.term" /></th>
							<th><g:message code="text.vocabulary" /></th>
							<th><g:message code="text.comment" /></th>
							<th><g:message code="text.delete"/></th>
							</tr>
						</thead>
							<g:each in="${selectedTerms}" var="term">
								<tr id="${term.preferredLabel}" onmouseover="changeTextColor('${term.preferredLabel}')" onmouseout="changeTextColorBack('${term.preferredLabel}')">
									<td>
										<span onclick="processSelectOfTermAsCurrent('${term.termURI}')">${term.preferredLabel}</span>
									</td>
									<td>
										${term.vocabularyName}
									</td>
									<td>
										${term.comment}
									</td><%--
									<td>
										<a href="#" onclick="deleteSelectedTerms('${term.preferredLabel}')"><i class="icon-trash"></i></a>
									</td>
								--%>
									<td class="delete">
										<a href="#" rel="tooltip" data-toggle="modal" class="deleteTerm" id="${term.termURI}" onclick="deleteSelectedTerms('${term.preferredLabel}','${term.termURI}')"><i class="icon-trash"></i></a>
									</td>
								</tr>
							</g:each>
						 </tbody>
					</table>
				</div>
			</div>
		</div>
		</div>