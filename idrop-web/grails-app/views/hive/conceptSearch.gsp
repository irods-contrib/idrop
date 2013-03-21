
<div id="hivePanelInner" class="container-fluid">

	<div class="row-fluid">
		<div class="offset3 span6">
			<div class="row-fluid">
				<div class="span10 offset1 well">
					<div class="container-fluid">
						<div class="row-fluid">
							<div class="span3">
								<strong>Searched Term:</strong>
							</div>
							<div class="offset1 span8">
								<em> ${searchResult.preLabel}
								</em>
							</div>
						</div>
						<div class="row-fluid">
							<div class="span3">
								<strong>Alternate labels</strong>
							</div>
						</div>
						<g:each in="${searchResult.altLabel}" var="altLabel">
							<div class="row-fluid">
								<div class="offset1 span8">
									<em> ${altLabel}
									</em>
								</div>
							</div>

						</g:each>
						<div class="row-fluid">
							<div class="span3">
								<strong>URI:</strong>
							</div>
							<div class="offset1 span8">
								<em> ${searchResult.URI}
								</em>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>

</div>
