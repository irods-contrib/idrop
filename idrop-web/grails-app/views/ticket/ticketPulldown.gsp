<div id="ticketPulldownDiv" class="">
			<g:if test="${flash.error}">
                <script>
                $(function() { setErrorMessage("${flash.error}"); });
                </script>
              </g:if>

              <g:if test="${flash.message}">
                <script>
                $(function() { setMessage("${flash.message}");});
                </script>
              </g:if>


<g:form name="ticketPulldownDetailsForm" id="ticketPulldownDetailsForm">

	<g:hiddenField name='irodsAbsolutePath' id='ticketDetailsAbsPath' value='${ticket.irodsAbsolutePath}'/>
	<g:hiddenField name='create' id='create' value='${ticket.create}'/>
	<g:hiddenField name='isDialog' id='isDialog' value='${ticket.isDialog}'/>
	<g:hiddenField name='ownerName' id='ownerName' value='${ticket.ownerName}'/>
	<g:hiddenField name='ownerZone' id='ownerZone' value='${ticket.ownerZone}'/>
	<g:hiddenField name='usesCount' id='usersCount' value='${ticket.usesCount}'/>
	<g:hiddenField name='writeFileCount' id='writeFileCount' value='${ticket.writeFileCount}'/>
	<g:hiddenField name='writeByteCount' id='writeByteCount' value='${ticket.writeByteCount}'/>
	<g:hiddenField name='ticketURL' id='ticketURL' value='${ticket.ticketURL}'/>
	<g:hiddenField name='ticketURLWithLandingPage' id='ticketURLWithLandingPage' value='${ticket.ticketURLWithLandingPage}'/>
	<g:hiddenField name='isDataObject' id='isDataObject' value='${ticket.isDataObject}'/>
	
		<g:hasErrors bean="${ticket}">
					<div class="errors">
				  <ul>
				   <g:eachError var="err" bean="${ticket}">
				       <li><g:message error="${err}" /></li>
				   </g:eachError>
				  </ul>
				  </div>
		</g:hasErrors>
					
			<div id="container" style="height:100%;width:100%;">
			
				<g:if test="${ticket.create}">
					<div>
						<div style="width:15%;"><label><g:message code="text.ticket.string" />:</label></div>
						<div><g:textField id="ticketString" name="ticketString" width="20em"
						value="${ticket.ticketString}" placeholder="Enter a ticket key or let iRODS generate one"/></div>
					</div>
					<div>
						<div style="width:15%;"><label><g:message code="text.ticket.type" />:</label></div>
						<div><g:select id="type" name="type" from="${['READ', 'WRITE']}"
						value="${ticket.type}" /></div>
					</div>
				
				
				</g:if>
				<g:else>
				<g:hiddenField name='ticketString' id='ticketString' value='${ticket.ticketString}'/>
				<g:hiddenField name='type' id='type' value='${ticket.type}'/>
					<div>
					<div style="width:20%;"><label><g:message code="text.ticket.string" />:</label></div>
						<div>${ticket.ticketString}</div>
					</div>
					<div>
						<div style="width:15%;"><label><g:message code="text.ticket.type" />:</label></div>
						<div>${ticket.type}</div>
					</div>
				</g:else>
			
					<div>
						<div style="width:20%;"><label><g:message code="text.ticket.user" />:</label></div>
						<div>${ticket.ownerName}</div>
					</div>
					<div>
						<div><label><g:message code="text.ticket.owner.zone" />:</label></div>
						<div>${ticket.ownerZone}</div>
					</div>
					<div>
						<div><label><g:message code="text.ticket.expire.time" />:</label></div>
						<div><g:textField id="expireTime" name="expireTime" width="20em"
						value="${ticket.expireTime}" /></div>
					</div>
					<!-- Data objects may have an optional landing page, colls always show a landing page (at least for now, maybe we can do an auto bundle or something) -->
					<g:if test="${ticket.isDataObject}">
						<div >
							<div><label><g:message code="text.ticket.url" />:</label></div>
							<div><span id="ticketPulldownUrl"><a target="_blank" href="${ticket.ticketURL}">${ticket.ticketURL}</a></span><span id="ticketPulldownUrlLanding"><a target="_blank" href="${ticket.ticketURLWithLandingPage}">${ticket.ticketURLWithLandingPage}</a></span></div>
						</div>
						<g:if test="${!ticket.create}">
						<div>
							<div><label><g:message code="text.ticket.url.landing" />:</label></div>
							<div><g:checkBox id="showLandingPage" name="showLandingPage" onclick="toggleLandingUrl()"/></div>
						</div>
						</g:if>
					</g:if>
					<g:else>
						<div >
							<div><label><g:message code="text.ticket.url" />:</label></div>
							<div><span id="ticketPulldownUrlCollection"><a target="_blank" href="${ticket.ticketURLWithLandingPage}">${ticket.ticketURLWithLandingPage}</a></span></div>
						</div>
					</g:else>
				</div>
				
				<div id="container" style="height:100%;width:100%;margin:10px;" >
				
					<div><!--  pie chart row --> 
					
						<div style="width:33%;height:auto;" class="well">
							<!--  pie chart cell usage -->
								<div id="ticketUsesChart" style="height:auto;width:auto;"></div>
								<label><g:message code="text.ticket.uses.count" /></label><br/>${ticket.usesCount}<br/>
								<label><g:message code="text.ticket.uses.limit" /></label><br/><g:textField id="usesLimit" name="usesLimit" value="${ticket.usesLimit}" />
						</div>
						<div style="width:33%;height:auto;" class="well">
							<!--  pie chart cell write files -->
							<div id="ticketWriteFilesChart" style="height:auto;width:auto%;" ></div>
							<label><g:message code="text.ticket.file.count" /></label><br/>${ticket.writeFileCount}<br/>
							<label><g:message code="text.ticket.file.limit" /></label><br/>
							<g:textField id="writeFileLimit" name="writeFileLimit" value="${ticket.writeFileLimit}" />
						</div>
					
						<div style="width:33%;height:auto;"  class="well">
							<!--  pie chart cell write bytes -->
							<div id="ticketWriteBytesChart" style="height:auto;width:auto;"></div>
							<label><g:message code="text.ticket.byte.count" /></label><br/>${ticket.writeByteCount}<br/>
							<label><g:message code="text.ticket.byte.limit" /></label><br/><g:textField id="writeByteLimit" name="writeByteLimit" value="${ticket.writeByteLimit}" />
						</div>
						
					</div><!--  pie chart row end -->
					
				</div> <!--  pie chart table end -->
				
				<g:if test="${ticket.isDialog}">
					<div id="detailsDialogMenu" 
							class="pull-right">
							<button type="button" id="updateTicketDetailButton"
								value="update  Ticket"
								onclick="submitTicketDialog()")><g:message code="text.update" /></button>
							<button type="button" id="cancelAddTicketButton"
								value="cancelAdd"
								onclick="closeTicketDialog()")><g:message code="default.button.cancel.label" /></button>
				</div>
			</g:if>
			<g:else>
			
				<div id="detailsDialogMenu" class="well pull-right"
							>
							<button type="button" id="updateTicketPulldownButton"
								
								onclick="updateTicketFromPulldown()")><g:message code="text.update" /></button>
							<button type="button" id="cancelTicketPulldownButton"
								
								onclick="cancelTicketFromPulldown()")><g:message code="default.button.cancel.label" /></button>
				</div>
					
			</g:else>
			</g:form>
			
</div>
<script>
	$(function() {

		$.datepicker.setDefaults($.datepicker.regional[""]);
		var current =
${ticket.usesCount}
	;
		var limit =
${ticket.usesLimit}
	;

		if (limit == 0) {
			limit = 100;
			current = 0;
		} else if (current < limit) {
			limit = limit - current;
		} else if (current >= limit) {
			current = 1;
			limit = 0;
		}

		var data = [ $.gchart.series('Usage', [ current, limit ]) ];

		$('#ticketUsesChart').gchart({
			type : 'pie3D',
			series : data,
			legend : 'bottom',
			width : 100,
			height : 80
		});

		current =
${ticket.writeByteCount}
	;
		limit =
${ticket.writeByteLimit}
	;

		if (limit == 0) {
			limit = 100;
			current = 0;
		} else if (current < limit) {
			limit = limit - current;
		} else if (current >= limit) {
			current = 1;
			limit = 0;
		}

		data = [ $.gchart.series('Bytes', [ current, limit ]) ];

		$('#ticketWriteBytesChart').gchart({
			type : 'pie3D',
			series : data,
			legend : 'bottom',
			width : 100,
			height : 80
		});

		current =
${ticket.writeFileCount}
	;
		limit =
${ticket.writeFileLimit}
	;
		if (limit == 0) {
			limit = 100;
			current = 0;
		} else if (current < limit) {
			limit = limit - current;
		} else if (current >= limit) {
			current = 1;
			limit = 0;
		}
		data = [ $.gchart.series('Files', [ current, limit ]) ];

		$('#ticketWriteFilesChart').gchart({
			type : 'pie3D',
			series : data,
			legend : 'bottom',
			width : 100,
			height : 80
		});

		$("#ticketPulldownUrlLanding").hide();
		$("#ticketPulldownUrl").show();
		$("#expireTime").datepicker();

	});

	/*
	 * switches content of url displayed to show either the URL with the landing page parameter, or one without to go 'direct'
	 */
	function toggleLandingUrl() {
		var checkVal = $("#showLandingPage").attr("checked");
		if (checkVal) {
			$("#ticketPulldownUrl").hide("fast");
			$("#ticketPulldownUrlLanding").show("slow");

		} else {
			$("#ticketPulldownUrlLanding").hide("fast");
			$("#ticketPulldownUrl").show("slow");
		}
	}
</script>
