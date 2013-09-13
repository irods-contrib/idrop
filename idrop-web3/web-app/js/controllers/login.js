/**
 * login controller
 */


function LoginCtrl($scope) {
	
	$scope.login = {};
	$scope.master = {};
	//$scope.login.host="";
	
	
	$scope.doLogin = function() {
		$scope.master= angular.copy(login);
		
		
		
		
		
	  }
	
	$scope.doReset = function() {
		$scope.login	 = angular.copy($scope.master);
	}
	
	
}