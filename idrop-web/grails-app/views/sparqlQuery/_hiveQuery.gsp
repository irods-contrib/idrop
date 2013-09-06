<div class="container-fluid">
	<div class="row-fluid">
		<g:form class="well" style="width:900px; position:relative; margin-left:100px; box-shadow:10px 10px 10px rgba(0, 0, 0, 0.05)">
			<fieldset>
				<legend><h3>Query by Term</h3></legend>
				<div class="row-fluid">
					<label style="margin-top:30px; margin-bottom:30px;"><span class="label label-info" style="font-size:14px">Term 1</span></label> 
					<div class="span4 offset2 input-append">
						<input type="text" name="hiveSearchTerm" id="hiveSearchTerm1" class="span8" placeholder="enter a term or choose from concept browser">
						<button class="btn btn-success dropdown-toggle" data-toggle="collapse" href="#conceptbrowserwindow">
							<g:message code="text.choose.term"/>
							<span class="caret"></span>
						</button>
						
					</div>
					<div class="span2 offset1">
						<label class="checkbox">
							<input type="checkbox" id="searchNarrower" name="searchOption" value="narrower"> Narrower terms
						</label>
						<label class="checkbox">
							<input type="checkbox" id="searchBroader" name="searchOption" value="broader"> Broader terms
						</label>
						<label class="checkbox">
							<input type="checkbox" id="searchRelated" name="searchOption" value="related"> Related terms
						</label>
					</div>
				</div>
				<div class="collapse in" id="conceptbrowserwindow">
					<div style="height:100px; width:500px; overflow:auto; position: relative; margin:30px 100px 30px 100px; background-color:#FFFFFF; font-size:12px;">
						<%--<g:render template="/sparqlQuery/conceptBrowser" />
					--%>
					<p class="text-error">Please select terms from the tables below.......
					Anim pariatur cliche reprehenderit, enim eiusmod high life accusamus terry richardson ad squid. 3 wolf moon officia aute, non cupidatat skateboard dolor brunch. Food truck quinoa nesciunt laborum eiusmod. Brunch 3 wolf moon tempor, sunt aliqua put a bird on it squid single-origin coffee nulla assumenda shoreditch et. Nihil anim keffiyeh helvetica, craft beer labore wes anderson cred nesciunt sapiente ea proident. Ad vegan excepteur butcher vice lomo. Leggings occaecat craft beer farm-to-table, raw denim aesthetic synth nesciunt you probably haven't heard of them accusamus labore sustainable VHS.
					</p>
					</div>
				</div>
				
				<div class="row-fluid" style="margin-top:30px;">
					<label style="margin-top:30px; margin-bottom:30px;"><span class="label label-info" style="font-size:14px">Term 2</span></label> 
					<div class="span1 offset0">
						<select style="width:100px;">
							<option>select AND/OR</option>
							<option><g:message code="text.and"/></option>
							<option><g:message code="text.or"/></option>
						</select>
					</div>
					<div class="span4 offset1 input-append">
						<input type="text" name="hiveSearchTerm" id="hiveSearchTerm1" class="span8" placeholder="enter a vocabulary term or choose from concept browser">
						<button class="btn btn-success dropdown-toggle" data-toggle="dropdown">
							<g:message code="text.choose.term"/>
							<span class="caret"></span>
						</button>
						<span class="help-block">e.g.: Animals</span>
					</div>
					<div class="span2 offset1">
						<label class="checkbox">
							<input type="checkbox" id="searchNarrower" name="searchOption" value="narrower"> Narrower terms
						</label>
						<label class="checkbox">
							<input type="checkbox" id="searchBroader" name="searchOption" value="broader"> Broader terms
						</label>
						<label class="checkbox">
							<input type="checkbox" id="searchRelated" name="searchOption" value="related"> Related terms
						</label>
					</div>
				</div>
				<div class="row-fluid" style="margin-top:30px;">
					<label style="margin-top:30px; margin-bottom:30px;"><span class="label label-info" style="font-size:14px;">Term 3</span></label> 
					<div class="span1 offset0">
						<select style="width:100px;">
							<option>select AND/OR</option>
							<option><g:message code="text.and"/></option>
							<option><g:message code="text.or"/></option>
						</select>
					</div>
					<div class="span4 offset1 input-append">
						<input type="text" name="hiveSearchTerm" id="hiveSearchTerm1" class="span8" placeholder="enter a vocabulary term or choose from concept browser">
						<button class="btn btn-success dropdown-toggle" data-toggle="dropdown">
							<g:message code="text.choose.term"/>
							<span class="caret"></span>
						</button>
						<span class="help-block">e.g.: Animals</span>
					</div>
					<div class="span2 offset1">
						<label class="checkbox">
							<input type="checkbox" id="searchNarrower" name="searchOption" value="narrower"> Narrower terms
						</label>
						<label class="checkbox">
							<input type="checkbox" id="searchBroader" name="searchOption" value="broader"> Broader terms
						</label>
						<label class="checkbox">
							<input type="checkbox" id="searchRelated" name="searchOption" value="related"> Related terms
						</label>
					</div>
				</div>
				<div class="row-fluid span3 offset8" style="margin-top:80px">
					<button type="submit" class="btn btn-primary"><g:message code="text.submit"/></button>
					<button class="btn" style="margin-left:20px"><g:message code="text.clear"/></button>
				</div>
			</fieldset>
		</g:form>
	</div>
</div>