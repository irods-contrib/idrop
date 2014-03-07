/**
 * Main angular module for home views
 * 
 * @author Mike Conway (DICE)
 */

function genId(path) {
    return encodeURIComponent(encodeURIComponent(path));
}

angular.module('home', ['ngRoute','ngResource'], function($provide, $routeProvider, $locationProvider) {

    $routeProvider.when("/Collection/:collId", {
        templateUrl: "assets/home/collection-angularjs.html",
        controller: "CollectionCtrl",
        controllerAs: "CollectionCtrl"
    }).otherwise({redirectTo: "/Collection/%2FtempZone%2Fhome%2Frods"});
    $provide.factory("irods", function($resource){
        var fileIcon = "glyphicon-file";
        var folderIcon = "glyphicon-folder-close";
        var zoneIcon = "glyphicon-hdd";

        return {
            collection: function(path) {
                var pages;
                var pageData;
                if(path === "") {
                    pages = [1];
                    pageData = [[{
                        kind: "Zone",
                        icon: zoneIcon,
                        name: "tempZone",
                        id: "/Collection/"+genId("/tempZone"),
                        created: "",
                        selected: false
                    }]];
                } else if(path === "/tempZone") {
                    pages = [1];
                    pageData = [[{
                        kind: "Collection",
                        icon: folderIcon,
                        name: "home",
                        id: "/Collection/"+genId("/tempZone/home"),
                        created: "",
                        selected: false
                    }]];

                } else if(path === "/tempZone/home") {
                    pages = [1];
                    pageData = [[{
                        kind: "Collection",
                        icon: folderIcon,
                        name: "rods",
                        id: "/Collection/"+genId("/tempZone/home/rods"),
                        created: "",
                        selected: false
                    }]];

                } else {

                    pages = [1,2,3,4,5,6];
                    pageData = [];
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
                                var name = "Collection "+i;
                                page.push({
                                    kind: "Collection",
                                    icon: folderIcon,
                                    name: name,
                                    id: "/Collection/"+genId(path+"/"+name),
                                    created: "12/01/2013 12:15:00",
                                    selected: false
                                });
                            }
                        }
                        pageData.push(page);
                    };
                }
                return {
                    pages: pages,
                    page: function(inx) {return pageData[inx];}
                }
            }
        };
    })
})

.config(function(){
	/*
	 * configuration block
	 */


})

/*
	 * Home controller function here
	 */
.controller('HomeCtrl', function($scope) {

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
	
	
})
.controller("CollectionCtrl", function($scope, $routeParams, $location, irods) {
    var path = decodeURIComponent($routeParams.collId);
    var pathObjects = [];

    if(path === "/") {
        path = "";
    }
    pathObjects.push({path: "", id: "/"});
    var pathList = path.split("/");
    var prefix = "";
    for(var i = 1;i<pathList.length;i++) {
        prefix += "/"+pathList[i];
        pathObjects.push({path: pathList[i], id: prefix});
    }
    $scope.collection = {
        path: pathObjects,
        data: [],
        metadata: {},
        selection: [],
        actions: [],
        numberSelected: 0
    }
    var irodsCollection = irods.collection(path);
    $scope.update = function(d) {
        var selection = $scope.collection.selection;
        if(d) {
            if(d.selected) {
                selection.push(d);
                $scope.collection.numberSelected++;
            } else {
                selection.splice(selection.indexOf(d), 1);
                $scope.collection.numberSelected--;
            }
        }
/*        switch(selection.length) {
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
        }*/
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
        collection.data = irodsCollection.page(collection.pageInx);
    }
    $scope.pages = function () {
        return irodsCollection.pages;
    }
    $scope.goto = function(path) {
        $location.path("/Collection/"+genId(path));
    }
    $scope.page(0);
    $scope.update();
});