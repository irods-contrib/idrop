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
            data: [],
            metadata: {}

        }
        $scope.page = function page(n) {
            if(n==="first") {
                page(0);
            } else if(n==="last") {
                var pages = $scope.pages();
                page(pages.length-1);
            } else if(n==="next") {
                var pages = $scope.pages();
                if($scope.collection.pageInx < pages.length-1) {
                    page($scope.collection.pageInx+1);
                }
            } else if(n==="prev") {
                var pages = $scope.pages();
                if($scope.collection.pageInx > 0) {
                    page($scope.collection.pageInx-1);
                }
            } else {
                $scope.collection.pageInx = n;
                $scope.collection.data = [ {
                    kind: "Collection",
                    name: "Collection "+n,
                    created: "12/01/2013 12:15:00"
                }, {
                    kind: "DataObject",
                    name: "file"+n+".txt",
                    dataSize: "88K",
                    created: "12/31/2013 10:00:00"
                } ];
            }
        }
        $scope.pages = function () {
            return [1,2,3,4,5,6];
        }
        $scope.page(0);

    });