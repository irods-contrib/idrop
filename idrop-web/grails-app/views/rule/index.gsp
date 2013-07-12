<head>
<meta name="layout" content="mainNoSidebar" />


</head>


<script>
	$(document).ready(function() {

		$.ajaxSetup({
			cache : false
		});
		$("#topbarTools").addClass("active");
		$("#topbarRule").addClass("active");

		 $(window).bind( 'hashchange', function(e) {
           //  processTagSearchStateChange( $.bbq.getState());
		});

		 refreshTagCloud();

		  $(window).trigger( 'hashchange' );
	
	});

	
</script>