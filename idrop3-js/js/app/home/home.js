/**
 * Home page controllers
 * Created by mikeconway on 3/9/14.
 */

/*
 * Home controller function here
 */
angular.module('home', ['httpInterceptorModule', 'angularTranslateApp', 'virtualCollectionsModule', 'MessageCenterModule', 'CollectionsModule', 'ngRoute','globalsModule','angularFileUpload'])

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

    .controller('homeController', ['$scope', 'virtualCollectionsService', '$translate', '$log', '$http', '$location', 'messageCenterService', 'collectionsService', 'fileService','selectedVc', 'pagingAwareCollectionListing','breadcrumbsService', '$filter','$upload',function ($scope, virtualCollectionsService, $translate, $log, $http, $location, $messageCenterService, collectionsService, fileService, selectedVc, pagingAwareCollectionListing,breadcrumbsService, $filter, $upload ) {

        $scope.selectedVc = selectedVc;
        $scope.pagingAwareCollectionListing = pagingAwareCollectionListing.data;
        $scope.numberSelected = 0;
        $scope.hideDrives = "false";
        $scope.selection = [];
        $scope.newFolderInfo = [];

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
            $location.search("vc", $scope.selectedVc.data.uniqueName);
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
            $scope.newFolderInfo = {};
            //collectionAndDataObjectListingEntries.collectionAndDataObjectListingEntries.unshift("{newFolderAction:true}");

        };

        /**
         * Cancel a new folder action
         */
        $scope.cancelAddDirectory = function() {
            $log.info("cancelAddDirectory()");
            $scope.newFolderAction=false;
            $scope.newFolderInfo = {};

        }

        /**
         * Following an action to initiateAddDirectory(), take the required new folder name and
         * do the actual folder create
         **/
        $scope.addDirectory = function() {
            $log.info("addDirectory()");

            if(!$scope.newFolderInfo.name) {
                // show an error
                var message = $filter('translate')('NO_DIRECTORY_PROVIDED');
                $messageCenterService.add('danger', message);
                return;
            }

            $log.info("subdirectory name is:" + $scope.newFolderInfo.name);


            var data = fileService.createNewFolder($scope.pagingAwareCollectionListing.parentAbsolutePath, $scope.newFolderInfo.name);

            $scope.newFolderAction=false;
            $scope.newFolderInfo = {};

            // do new folder action stuff

            $scope.pagingAwareCollectionListing.collectionAndDataObjectListingEntries.unshift(data);

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


        $scope.onFileSelect = function($files) {
            //$files: an array of files selected, each file has name, size, and type.
            for (var i = 0; i < $files.length; i++) {
                var file = $files[i];
                $log.info("selected for upload:" + file);
                $scope.upload = $upload.upload({
                    url: 'server/upload/url', //upload.php script, node.js route, or servlet url
                    //method: 'POST' or 'PUT',
                    //headers: {'header-key': 'header-value'},
                    //withCredentials: true,
                    data: {myObj: $scope.myModelObj},
                    file: file // or list of files ($files) for html5 only
                    //fileName: 'doc.jpg' or ['1.jpg', '2.jpg', ...] // to modify the name of the file(s)
                    // customize file formData name ('Content-Disposition'), server side file variable name.
                    //fileFormDataName: myFile, //or a list of names for multiple files (html5). Default is 'file'
                    // customize how data is added to formData. See #40#issuecomment-28612000 for sample code
                    //formDataAppender: function(formData, key, val){}
                }).progress(function(evt) {
                    console.log('percent: ' + parseInt(100.0 * evt.loaded / evt.total));
                }).success(function(data, status, headers, config) {
                    // file is uploaded successfully
                    console.log(data);
                });
                //.error(...)
                //.then(success, error, progress);
                // access or attach event listeners to the underlying XMLHttpRequest.
                //.xhr(function(xhr){xhr.upload.addEventListener(...)})
            }
            /* alternative way of uploading, send the file binary with the file's content-type.
             Could be used to upload files to CouchDB, imgur, etc... html5 FileReader is needed.
             It could also be used to monitor the progress of a normal http post/put request with large data*/
            // $scope.upload = $upload.http({...})  see 88#issuecomment-31366487 for sample code.
        };


    }]);



