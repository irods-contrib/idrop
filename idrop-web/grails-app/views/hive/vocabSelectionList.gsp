
<div id="hivePanelInner" class="container-fluid">

	<div class="row-fluid" >
		<div class="offset3 span6">
		<form name="hiveVocabularyForm" id="hiveVocabularyForm">
			<table cellspacing="0" cellpadding="0" border="0" id="hiveVocabTable"
				class="table table-striped table-hover">
				<thead>
					<tr>
						<th></th>
						<th>Vocabulary</th>
					</tr>
				</thead>
				<tbody>

					<g:each in="${vocabs}" var="vocab">
						<tr id="${vocab.vocabularyName}">
							<td><g:checkBox name="selectedVocab" value="${vocab.vocabularyName}"
									checked="${vocab.selected}" /></td>
							<td>
								${vocab.vocabularyName}
							</td>
						</tr>
					</g:each>

				</tbody>
				<tfoot>
					<tr>
						<td></td>
						<td></td>
					</tr>
				</tfoot>
			</table>
		</form>
		</div>
	</div>
	<div id="detailsDialogMenu" class="row-fluid">
		<div class="offset5 span1">
				<button type="button" id="updateVocabularyButton"
					value="update" onclick="selectVocabularies()">
					<g:message code="text.update" />
				</button>
			
		</div>
	</div>
</div>
