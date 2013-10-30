		<label id="change_${index}" style="margin-top:30px; margin-bottom:30px;"><span class="label label-info" style="font-size:14px">Term</span></label> 
		<div class="span1 offset0">
			<select id="connector" style="width:100px;">
				<option value="AND">select AND/OR</option>
				<option value="AND"><g:message code="text.and"/></option>
				<option value="OR"><g:message code="text.or"/></option>
			</select>
		</div>
		<g:if test="${preLabel}">
			<div class="span4 offset1 input-append">
				<div>${preLabel}</div>
			</div>
		</g:if>
		<g:else>
			<div class="span4 offset1 input-append">
				<button type="button" class="btn btn-success" 
								onclick="showConceptBrowser('${index}')">
					<g:message code="text.choose.term" />
					<%--<g:hidden id="blah_1" value="http://agrovocblah/blah/blah_657"/>--%>
					<span class="caret"></span>
				</button>
			</div>
		</g:else>
		<div class="span2 offset0">
			<form>
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
			</form>
		</div>
		<div class="span1 offset1">
			<button class="btn btn-primary">
				<g:message code="text.add" />
			</button>
		</div>
	