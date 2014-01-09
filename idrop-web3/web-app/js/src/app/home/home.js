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
	
	
}).controller("CollectionCtrl", function($scope) {
        $scope.collection = {
            path: ["tempZone", "home", "rods"],
            data: [ {
                kind: "Collection",
                name: "Collection 1",
                created: "12/01/2013 12:15:00"
            }, {
                kind: "DataObject",
                name: "file1.txt",
                dataSize: "88K",
                created: "12/31/2013 10:00:00"
            } ],
            metadata: {}

        }
    });