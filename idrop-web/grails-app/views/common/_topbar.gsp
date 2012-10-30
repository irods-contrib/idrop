<div class="navbar navbar-fixed-top navbar-inverse">
<div class="navbar-inner">
    <a class="brand" href="#">iDrop</a>
   
    <ul class="nav">
    	 <g:ifAuthenticated>
    	 
	    	 <!--  menu items shown if user has been authenticated -->
	    	 
		      <li id="topbarHome" class="topbarItem active"><g:link controller="home" action="index">Home</g:link></li>
		      <li id="topbarBrowser" class="topbarItem"><g:link controller="browse" action="index">Browse</g:link></li>
		      <g:if test="${grailsApplication.config.idrop.config.use.userprofile==true}">
				 <li id="topbarPreferences" class="topbarItem"><g:link controller="profile" action="index">Profile</g:link></li>
			  </g:if>
			 
			 
			 
						
		</g:ifAuthenticated>
		
			<li id="topbarAccount" class="dropdown">
				 <a href="#" class="dropdown-toggle" data-toggle="dropdown">
      				Account<b class="caret"></b></a>
      				 <ul class="dropdown-menu">
      
  
	      				<g:ifAuthenticated>
	      					 <li><a href="#" id="logoutButton" onclick="logout()")><g:message code="text.logout" /></a></li>
	      				</g:ifAuthenticated>
      				
      				  </ul>
      				
      				
      				
      				
			</li>
			
			 <g:ifAuthenticated>
			 	  <li id="topbarHome" class="topbarItem"><g:link class="pull-right" controller="shoppingCart" action="index">Shopping Cart</g:link><b class="caret"></b></li>
			 
			 </g:ifAuthenticated>
		
		</ul>
   
    
  </div>
</div>




