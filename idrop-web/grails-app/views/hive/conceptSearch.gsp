
<div id="hivePanelInner" class="container-fluid">
	<div class="row-fluid">
		<div class="offset1 span10">
			<div class="row-fluid">
				<div class="span10 offset1 well">
					<div class="container-fluid">
						Searched Term : ${searchedConcept}
						<table class="table table-striped">
							<tr>
								<th>Found Terms</th>
								<th>Vocabulary</th>
								<g:each in="${searchResult.preLabel}" var="preLabel">
									<tr>
									<th>${preLabel}</th>
									<th>${searchResult.origin}</th>
									</tr>
								</g:each>
							</tr>
						</table>
					</div>
				</div>
			</div>
		</div>
	</div>

</div>
