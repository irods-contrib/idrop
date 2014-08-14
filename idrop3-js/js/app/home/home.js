/**
 * Home page controllers
 * Created by mikeconway on 3/9/14.
 */

/*
 * Home controller function here
 */
angular.module('home', ['httpInterceptorModule', 'angularTranslateApp', 'virtualCollectionsModule', 'MessageCenterModule', 'CollectionsModule', 'ngRoute','globalsModule'])

    /*
     * handle config of routes for home functions
     */.config(function ($routeProvider) {
        // route for the home page
        $routeProvider.when('/home/:vcName', {
            templateUrl: 'assets/home/home-angularjs.html',
            controller: 'homeController',
            resolve: {

                // set vc name as selected
                selectedVc: function ($route, virtualCollectionsService) {

                    var vcData = virtualCollectionsService.listUserVirtualCollectionData($route.current.params.vcName);
                    return vcData;
                },
                // do a listing
                pagingAwareCollectionListing: function ($route, collectionsService) {
                    var vcName = $route.current.params.vcName;

                    var path = $route.current.params.path;
                    if (path == null) {
                        path = "";
                    }

                    return collectionsService.listCollectionContents(vcName, path, 0);
                }

            }
        }).when('/home', {
                templateUrl: 'assets/home/home-angularjs.html',
                controller: 'homeController',
                resolve: {
                    // set vc name as selected
                    selectedVc: function ($route) {

                        return null;
                    },
                    // do a listing
                    pagingAwareCollectionListing: function ($route, collectionsService) {
                        return {};
                    }

                }
            })

    })

    .controller('homeController', ['$scope', 'virtualCollectionsService', '$translate', '$log', '$http', '$location', 'messageCenterService', 'collectionsService', 'selectedVc', 'pagingAwareCollectionListing','breadcrumbsService', '$filter',function ($scope, virtualCollectionsService, $translate, $log, $http, $location, $messageCenterService, collectionsService, selectedVc, pagingAwareCollectionListing,breadcrumbsService, $filter) {

        $scope.selectedVc = selectedVc;
        $scope.pagingAwareCollectionListing = pagingAwareCollectionListing.data;
        $scope.numberSelected = 0;
        $scope.hideDrives = "false";
        $scope.selection = [];

        /**
         * List all virtual collections for the user
         */
        $scope.listVirtualCollections = function () {

            $log.info("getting virtual colls");
            virtualCollectionsService.listUserVirtualCollections().then(function (virColls) {
                $scope.virtualCollections = virColls.data;
            });
        };

        /**
         * Handle the selection of a virtual collection from the virtual collection list, by causing a route change and updating the selected virtual collection
         * @param vcName
         */
        $scope.selectVirtualCollection = function (vcName) {
            if (!vcName) {
                $messageCenterService.add('danger', "missing vcName");
                return;
            }

            $log.info("initializing virtual collection for:" + vcName);
            $location.path("/home/" + vcName + "?path=");

        };

        /**
         * Handle the selection of a collection from the iRODS and make a new iRODS parent
         *
         * @param vcName
         *
         */
        $scope.selectVirtualCollection = function (vcName) {
            if (!vcName) {
                $messageCenterService.add('danger', "missing vcName");
                return;
            }

            $log.info("initializing virtual collection for:" + vcName);
            $location.path("/home/" + vcName)
            $location.search("path", "");

        };

        /**
         * Upon the selection of an element in a breadrumb link, set that as the location of the browser, triggering
         * a view of that collection
         * @param index
         */
        $scope.goToBreadcrumb = function (index) {

            if (!index) {
                $log.error("cannot go to breadcrumb, no index");
                return;
            }

            $location.path("/home/root");
            $location.search("path", breadcrumbsService.buildPathUpToIndex(index));

        };


        /**
         * View the details for the selected file, by evaluating the selections and finding the selected path
         */
        $scope.viewInfoDetails = function() {

            $log.info("viewInfoDetails...looking for selected path...");

            if ($scope.selection.length == 0) {
                $log.info("nothing to select");
                return;
            }

            // find the selected path and chain the call to show the details

            var path = $scope.selection[0];
            $scope.showFileDetails(path);

        };

        /**
         * Show the file details view
         * @param path
         */
        $scope.showFileDetails = function(path) {
            $location.path("/file");
            $location.search("path", path);

        };


        /**
         * Cause the collections panel on the left to display
         */
        $scope.showCollections = function () {
            $scope.hideDrives = "false";
        };

        $scope.initiateAddDirectory = function() {
            $log.info("initiateAddDirectory()");
            $scope.newFolderAction=true;
            //collectionAndDataObjectListingEntries.collectionAndDataObjectListingEntries.unshift("{newFolderAction:true}");

        };

        /**
         * Following an action to initiateAddDirectory(), take the required new folder name and
         * do the actual folder create
         *
         * @param subDirNameToAdd the name of the subdirectory under the current pat that will be added
         */
        $scope.addDirectory = function(subDirNameToAdd) {
            $log.info("addDirectory()");
            if(!subDirNameToAdd) {
                // show an error
                var message = $filter('translate')('NO_DIRECTORY_PROVIDED');
                $messageCenterService.add('danger', message);
                return;
            }

            $log.info("subdirectory name is:" + subDirNameToAdd);

            if (!$scope.newFolderAction) {
                throw "This is not a new folder action, method should not be called";
            }

            // do new folder action stuff

        };

        /**
         * Get the breadcrumbs from the pagingAwareCollectionListing in the scope.  This updates the path
         * in the global scope breadcrmubsService.  I don't know if that's the best way, but gotta get it somehow.
         * Someday when I'm better at angualar we can do this differently.
         */
        $scope.getBreadcrumbPaths = function () {

            if (!$scope.pagingAwareCollectionListing) {
                return [];
            }

            breadcrumbsService.setCurrentAbsolutePath($scope.pagingAwareCollectionListing.parentAbsolutePath);
            return breadcrumbsService.getWholePathComponents();

        };


        /**
         * Cause the collections panel on the left to be hidden
         */
        $scope.hideCollections = function () {
            $scope.hideDrives = "true";
        };

        /**
         * respond to selection of a check box in the listing
         */
        $scope.updateSelectedFromCollection = function (absolutePath) {

           $log.info("update selected:" + absolutePath);
           // alert("selected!");
            //var checkbox = action.target;
            //(checkbox.checked ? $scope.numberSelected++ : $scope.numberSelected--);

            var idx = $scope.selection.indexOf(absolutePath);

            // is currently selected
            if (idx > -1) {
                $scope.selection.splice(idx, 1);
                $scope.numberSelected--;
            }

            // is newly selected
            else {
                $scope.selection.push(absolutePath);
                $scope.numberSelected++;
            }

        }

        /**
         * Indicates whether a virtual collection has been selected
         * @returns {boolean}
         */
        $scope.noVcSelected = function () {
            var selected = true;

            if ($scope.selectedVc == null) {
                selected = true;
            } else {
                selected = false;
            }

            return selected;
        };

        /**
         * INIT
         */

        $scope.listVirtualCollections();

    }]);



