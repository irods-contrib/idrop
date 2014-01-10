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
        metadata: {},
        selection: [],
        actions: []
    }
    var pages = [1,2,3,4,5,6];
    var pageData = [];
    var fileIcon = "glyphicon-file";
    var folderIcon = "glyphicon-folder-close";
    for(var n=0;n<pages.length;n++) {
        var page = [];
        if(n!==0) {
        for(var i=0;i<10;i++) {
            page.push({
                kind: "DataObject",
                icon: fileIcon,
                name: "file"+n+i+".txt",
                dataSize: "88K",
                created: "12/31/2013 10:00:00",
                selected: false
            });
        }
        } else {
        for(var i=0;i<10;i++) {
            page.push({
                kind: "Collection",
                icon: folderIcon,
                name: "Collection "+i,
                created: "12/01/2013 12:15:00",
                selected: false
            });
        }
        }
        pageData.push(page);
    }
    $scope.update = function(d) {
        var selection = $scope.collection.selection;
        if(d) {
            if(d.selected) {
                selection.push(d);
            } else {
                selection.splice(selection.indexOf(d), 1);
            }
        }
        switch(selection.length) {
            case 0:
                $scope.collection.actions = [{
                    name: "Tools",
                    icon: "glyphicon-wrench"
                }]
                break;
            case 1:
                $scope.collection.actions = [ {
                    name: "Rename",
                    icon: "glyphicon-pencil"
                },{
                    name: "Move/Copy",
                    icon: "glyphicon-transfer"
                },{
                    name: "Delete",
                    icon: "glyphicon-trash"
                },{
                    name: "Tools",
                    icon: "glyphicon-wrench"
                },{
                    name: "Add to Cart",
                    icon: "glyphicon-shopping-cart"
                }];
                break;
            default :
                $scope.collection.actions =[{
                    name: "Move/Copy",
                    icon: "glyphicon-transfer"
                },{
                    name: "Delete",
                    icon: "glyphicon-trash"
                },{
                    name: "Tools",
                    icon: "glyphicon-wrench"
                },{
                    name: "Add to Cart",
                    icon: "glyphicon-shopping-cart"
                }];
        }
    }
    $scope.page = function page(n) {
        var collection = $scope.collection;
        if(n==="first") {
            collection.pageInx = 0;
        } else if(n==="last") {
            var pages = $scope.pages();
            collection.pageInx = pages.length-1;
        } else if(n==="next") {
            var pages = $scope.pages();
            if(collection.pageInx < pages.length-1) {
                collection.pageInx++;
            }
        } else if(n==="prev") {
            var pages = $scope.pages();
            if(collection.pageInx > 0) {
                collection.pageInx--;
            }
        } else {
            collection.pageInx = n;
        }

        collection.data = pageData[collection.pageInx];
    }
    $scope.pages = function () {
        return pages;
    }
    $scope.page(0);
    $scope.update();
});