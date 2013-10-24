
<div class="container-fluid" id="hiveQueryView">
	<div class="row-fluid">
		<g:form class="well"
			style="width:900px; position:relative; margin-left:100px; box-shadow:10px 10px 10px rgba(0, 0, 0, 0.05)">
			<fieldset>
				<legend>
					<h3>Query by Term</h3>
				</legend>
				<div>
					<button type="button" class="btn btn-info" id="addQueryRow"><g:message code="text.add" /></button>
				</div>
				<div id="queryInnerFull">
						<g:each status='i' in="${queryVocabList}" var="vocabItem">
							<div class="row-fluid" style="margin-top:30px;" id="${i}">
								<label style="margin-top:30px; margin-bottom:30px;"><span class="label label-info" style="font-size:14px">Term</span></label> 
								<div class="span1 offset0">
									<g:if test="${i==0}">
										<g:hiddenField name="hiddenField" value="${i}" />
									</g:if>
									<g:elseif test="${vocabItem.searchTypes==null ||vocabItem.searchTypes.size()==0}">
										<select id="connector" style="width:100px;">
											<option value="AND">select AND/OR</option>
											<option value="AND"><g:message code="text.and"/></option>
											<option value="OR"><g:message code="text.or"/></option>
										</select>
									</g:elseif>
									<g:else>
										<%--<div id="${vocabItem.searchTypes.size()}_size">--%>
										${vocabItem.connectorType}
										<%--</div>--%>
									</g:else>
								</div>
								<div class="span3 offset1">
									<button type="button" class="btn btn-warning disabled" disabled="disabled">${vocabItem.preferredLabel}</button>
								</div>
								<div id="${vocabItem.vocabularyTermURI}">
									<g:if test="${vocabItem.searchTypes==null ||vocabItem.searchTypes.size()==0}">
										<div class="span2 offset1" id="searchTypeForm">
											<label class="checkbox">
												<input type="checkbox" class="typecheck" name="searchOption" value="exact"> Exact term
											</label>
											<label class="checkbox">
												<input type="checkbox" class="typecheck" name="searchOption" value="narrower"> Narrower terms
											</label>
											<label class="checkbox">
												<input type="checkbox" class="typecheck" name="searchOption" value="broader"> Broader terms
											</label>
											<label class="checkbox">
												<input type="checkbox" class="typecheck" name="searchOption" value="related"> Related terms
											</label>
										</div>
										<div class="span1 offset1">
											<button type="button" class="btn btn-primary" id="addRow-btn" onclick="getSearchTypesAndConnector('${vocabItem.vocabularyTermURI}')">
												<g:message code="text.add" />
											</button>
										</div>
									</g:if>
									<g:else>
										<div class="span3 offset1">
											<g:each in="${vocabItem.searchTypes}" var="searchType">
												<ul>
													<li>${searchType}</li>
												</ul>
											</g:each>
										</div>
										<div class="span1 offset0">
											<button class="btn btn-mini" onclick="showEditablePanel('${i}')">
												<g:message code="text.edit" />
											</button>
											<button class="btn btn-danger btn-mini" style="margin-top: 10px;" onclick="deleteRow('${i}')">
												<g:message code="text.delete" />
											</button>
										</div>
									</g:else>
								</div>
							</div>
						</g:each>
						<div id="queryInner">
							<div id="term1"></div>
							<div id="term2"></div>
							<div id="term3"></div>
						</div>
				</div>
				<div class="row-fluid span3 offset8" style="margin-top: 80px">
					<button type="submit" class="btn btn-primary">
						<g:message code="text.submit" />
					</button>
					<button class="btn" style="margin-left: 20px">
						<g:message code="text.clear" />
					</button>
				</div>
			</fieldset>
		</g:form>
	</div>
</div>

<script type="text/javascript">

	var div_id = 1;
	
	$("#addQueryRow").on("click", function(){
		console.log("click");

		var term_id = "term" + div_id;
		addNewRow(term_id);
		
		
		div_id ++;
				
	});

	function getSearchTypesAndConnector(termURI){
			
		if(termURI == null || termURI == "") {
			setErrorMessage(jQuery.i18n.prop('msg_uri_missing'));
			return false;
		}

		var e = document.getElementById("connector");
		var connector;
		
		console.log(e);
		if(!e) {
			connector = "AND";
		} else {
		
			connector = e.options[e.selectedIndex].value;
		}	
			<%--var types = $(':input[name=searchOption]').serializeArray();--%>

		var types = [];
		$('.typecheck:checked').each(function(i) {
			types[i] = $(this).val();
			console.log("types: " + types[i]);
		});

		if(types.length == 0) {
			alert("Please specify the search types!");
			return;
		} 
		console.log(types);

		params = {
			types: types,
			uri: termURI,
			cnt: connector
		}

		console.log(types);
		console.log(connector);

		lcSendValueWithParamsAndPlugHtmlInDiv("/sparqlQuery/submitRow", params, "#hiveQueryForm", null);
			
	}

	function deleteRow(index) {
		console.log("deleteRow()");

		if(index == null || index == "") {
			setErrorMessage("index is missing"); // FIXME:i18n
			return false;
		}

		console.log("deleteRow(), ", index);

		var row = document.getElementById(index);
		row.parentNode.removeChild(row);
		//$(preLabel).remove();

		deleteSelectedSearchTerm(index);
		
	}

	function showEditablePanel(index) {
		console.log("editRow(), ", index);

		if(index == null || index == "") {
			setErrorMessage("index is missing"); // FIXME:i18n
			return false;
		}

		$('#'+index).empty();

		showEditPanel(index);

		
	}
		
		
</script>