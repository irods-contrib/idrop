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

.controller('HomeCtrl', function($scope) {
	/*
	 * Home controller function here
	 */
	
	$scope.name="mconway";
	$scope.hideDrives="false";
	
});