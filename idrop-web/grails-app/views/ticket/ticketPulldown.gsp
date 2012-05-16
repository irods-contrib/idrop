<div id="ticketPulldownDiv" style="overflow:auto;">
	<g:hiddenField name='irodsAbsolutePath' id='ticketDetailsAbsPath' value='${ticket.irodsAbsolutePath}'/>
	
			<div id="container" style="height:100%;width:100%;">
					
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
						<div>${ticket.expireTime}</div>
					</div>
					<g:if test="${isDataObject}">
						<div>
							<div><label><g:message code="text.ticket.url" />:</label></div>
							<div><a href="${ticketDistribution.ticketURL}">${ticketDistribution.ticketURL}</a></div>
						</div>
					</g:if>
					
				</div>
				
				<div id="container" style="height:100%;width:100%;margin:10px;" >
				
					<div><!--  pie chart row --> 
					
						<div style="width:33%;height:auto;" class="roundedContainer">
							<!--  pie chart cell usage -->
								<div id="ticketUsesChart" style="height:auto;width:auto;"></div>
								<label><g:message code="text.ticket.uses.count" /></label><br/>${ticket.usesCount}<br/>
								<label><g:message code="text.ticket.uses.limit" /></label><br/><g:textField id="usesLimit" name="usesLimit" value="${ticket.usesLimit}" />
					
								
						</div>
						<div style="width:33%;height:auto;"  class="roundedContainer">
							<!--  pie chart cell write bytes -->
							<div id="ticketWriteBytesChart" style="height:auto;width:auto;"></div>
							<label><g:message code="text.ticket.byte.count" /></label><br/>${ticket.writeByteCount}<br/>
							<label><g:message code="text.ticket.byte.limit" /></label><br/><g:textField id="writeByteLimit" name="writeByteLimit" value="${ticket.writeByteLimit}" />
						</div>
						<div style="width:33%;height:auto;" class="roundedContainer">
							<!--  pie chart cell write files -->
							<div id="ticketWriteFilesChart" style="height:auto;width:auto%;" ></div>
							<label><g:message code="text.ticket.file.count" /></label><br/>${ticket.writeFileCount}<br/>
							<label><g:message code="text.ticket.file.limit" /></label><br/>
							<g:textField id="writeFileLimit" name="writeFileLimit" value="${ticket.writeFileLimit}" />
						</div>
					
					
					
					</div><!--  pie chart row end -->
					
				</div> <!--  pie chart table end -->
					
					
					
					
					
			
</div>
<script>
$(function() {

	
	
	//var data = [$.gchart.series('Usage', [${ticket.usesCount}, ${ticket.usesLimit}])];

	 //var data = [$.gchart.series( [1, 3])];
	  
	  
	var current = ${ticket.usesCount};
	var limit =  ${ticket.usesLimit}; 

	if (limit == 0) {
		limit = 100;
		current = 0;
	} else if (current < limit) {
		limit = limit - current;
	} else if (current >= limit) {
		current=1;
		limit=0;	
	}
	

	var data = [$.gchart.series('Usage', [current,limit])];
	    
	 $('#ticketUsesChart').gchart({type: 'pie3D', series: data, legend: 'bottom', 
	      width:100, height:80}); 

	 current = ${ticket.writeByteCount};
	 limit =  ${ticket.writeByteLimit}; 
	 
	 if (limit == 0) {
			limit = 100;
			current = 0;
		} else if (current < limit) {
			limit = limit - current;
		} else if (current >= limit) {
			current=1;
			limit=0;	
		}

	 data = [$.gchart.series('Bytes', [current,limit])];

	 $('#ticketWriteBytesChart').gchart({type: 'pie3D', series: data, legend: 'bottom', 
	      width:100, height:80}); 

	 current = ${ticket.writeFileCount};
	 limit =  ${ticket.writeFileLimit}; 
	 if (limit == 0) {
			limit = 100;
			current = 0;
		} else if (current < limit) {
			limit = limit - current;
		} else if (current >= limit) {
			current=1;
			limit=0;	
		}
		 data = [$.gchart.series('Files', [current,limit])];

	 $('#ticketWriteFilesChart').gchart({type: 'pie3D', series: data, legend: 'bottom', 
	      width:100, height:80}); 


	 
});


	
</script>
