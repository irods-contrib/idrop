/**
 * Main angular module for home views
 * 
 * @author Mike Conway (DICE)
 */

angular.module('home', [])

.config(function(){
	/*
	 * configuration block
	 */
	

})

/*
	 * Home controller function here
	 */
.controller('HomeCtrl', function($scope) {

        $scope.name="mconway";
        $scope.hideDrives="false";

	
	/*
	 * Cause the collections panel on the left to display
	 */
	$scope.showCollections = function () {
		$scope.hideDrives="false";
	};
	
	/*
	 * Cause the collections panel on the left to be hidden
	 */
	$scope.hideCollections = function () {
		$scope.hideDrives="true";
	};
	
	
});