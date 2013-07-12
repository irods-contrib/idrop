<div class="containerFluid" id="conceptBrowserPivotView">

	<div class="row-fluid">
		<div class="span6">
			<div class="container-fluid">
				<g:if test="${!conceptProxy.topLevel}">
					<div class="row-fluid">
						<div class="span10 offset2">
							<h5>
								<g:message code="text.current.term" />
							</h5>
						</div>
					</div>
					<div class="row-fluid">
						<div class="span10 offset1 well">
							<div class="container-fluid">
								<div class="row-fluid">
									<div class="span3">
										<h6 style="color:#0088CC">
											<g:message code="text.preferred.label" />
											:
										</h6>
									</div>
								</div>

								<div class="row-fluid">
									<div class="offset1 span8">
										<em> ${conceptProxy.preLabel}
										</em>
									</div>
								</div>
								<div class="row-fluid">
									<div class="span3">
										<h6 style="color:#0088CC">
											<g:message code="text.alternate.labels" />
											:
										</h6>
									</div>
								</div>
								<g:each in="${conceptProxy.altLabel}" var="altLabel">
									<div class="row-fluid">
										<div class="offset1 span8">
											<em> ${altLabel}
											</em>
										</div>
									</div>
								</g:each>
								<div class="row-fluid">
									<div class="span3">
										<h6 style="color:#0088CC">
											<g:message code="text.uri" />
											:
										</h6>
									</div>
								</div>
								<div class="row-fluid">
									<div class="offset1 span8">
										<small> ${conceptProxy.URI}</small>
									</div>
								</div>
								<div class="row-fluid">
									<div class="offset1 span11">
										<div class="btn-group pad-around">
											<div id="skosCodeModal" title="View SKOS Code">
												<p><textarea id="skosCodeArea" style="display:none">${conceptProxy.skosCode}</textarea></p>
											</div>
											<button role="button" class="btn" id="btnViewInSKOS" 
											onclick='processViewInSKOS("${conceptProxy.origin}","${conceptProxy.URI}")'>
												<g:message code="text.view.in.skos" />
											</button>
											<g:if test="${conceptProxy.selected}">
												<button type="button" class="btn" id="btnEditTerm"
													onclick="processEditHiveTerm('${conceptProxy.origin}','${conceptProxy.URI}')">
													<g:message code="text.edit" />
												</button>
												<button type="button" class="btn" id="btnRemoveTerm"
													onclick="processRemoveHiveTerm('${conceptProxy.origin}','${conceptProxy.URI}')">
													<g:message code="text.delete" />
												</button>
											</g:if>
											<g:else>
												<button type="button" class="btn" id="btnApplyTerm"
													onclick="processApplyHiveTerm('${conceptProxy.origin}','${conceptProxy.URI}')">
													<g:message code="text.apply.hive.term" />
												</button>
											</g:else>
										</div>
									</div>
								</div>
							</div>
						</div>
					</div>
				</g:if>
				<g:else>
					<div class="row-fluid">
						<div class="span10 offset1 well"><em>Select terms or search to navigate and find descriptions for your data...</em></div>
					</div> 
				</g:else>

				<g:if test="${conceptProxy.isTopLevel()}">
					<div class="row-fluid" id="conceptBrowserNarrowerLetters">
						<div class="span10 offset1 well">
							<g:render template="/hive/alphabetTable" />
						</div>
					</div>
				</g:if>
				<div class="row-fluid">
					<div class="span10 offset2">
						<h5>
							<g:message code="text.narrower.terms" />
						</h5>
					</div>
				</div>
				<div class="row-fluid" id="conceptBrowserNarrower">
					<g:render template="/hive/narrowerTable" />
				</div>

			</div>
		</div>
		<div class="span6">
			<div class="container-fluid">
				<div class="row-fluid">
					<div class="span10 offset2">
						<h5>
							<g:message code="text.broader.terms" />
						</h5>
					</div>
				</div>
				<div class="row-fluid" id="conceptBrowserBroader">
					<div class="span10 offset1 well">
						<table cellspacing="0" cellpadding="0" border="0" style="margin-top:0px"
							id="hiveVocabBroaderTable"
							class="table table-striped table-hover">
							<thead>
								<tr>
									<th></th>
								</tr>
							</thead>
							<tbody>

								<g:each in="${conceptProxy.broader.keySet()}" var="key">
									<tr id="${conceptProxy.broader.get(key)}"
										onclick="processSelectOfTermAsCurrent('${conceptProxy.broader.get(key)}')"
										onmouseover="changeTextColor('${conceptProxy.broader.get(key)}')"
										onmouseout="changeTextColorBack('${conceptProxy.broader.get(key)}')">
										<td>
											${key}
										</td>
									</tr>
								</g:each>
							</tbody>
						</table>
					</div>
				</div>
				<div class="row-fluid">
					<div class="span10 offset2">
						<h5>
							<g:message code="text.related.terms" />
						</h5>
					</div>
				</div>
				<div class="row-fluid" id="conceptBrowserRelated">
					<div class="span10 offset1 well">
						<table cellspacing="0" cellpadding="0" border="0" style="margin-top:0px"
							id="conceptBrowserRelatedTable"
							class="table table-striped table-hover">
							<thead>
								<tr>
									<th></th>
								</tr>
							</thead>
							<tbody>

								<g:each in="${conceptProxy.related.keySet()}" var="key">
									<tr id="${conceptProxy.related.get(key)}"
										onclick="processSelectOfTermAsCurrent('${conceptProxy.related.get(key)}')"
										onmouseover="changeTextColor('${conceptProxy.related.get(key)}')"
										onmouseout="changeTextColorBack('${conceptProxy.related.get(key)}')"
										>
										<td>
											${key}
										</td>
									</tr>
								</g:each>
							</tbody>
						</table>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>