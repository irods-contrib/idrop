/**
 * Home page controllers
 * Created by mikeconway on 3/9/14.
 */

/*
 * File controller function here, representing collection and data object catalog info and operations
 */
angular.module('fileModule', ['httpInterceptorModule', 'angularTranslateApp', 'MessageCenterModule', 'ngRoute', 'tagServiceModule', 'StarModule','globalsModule','mimeTypeServiceModule'])

    /*
     * handle config of routes for home functions
     */
    .config(function ($routeProvider) {
        // route for the home page
        $routeProvider.when('/file', {
            templateUrl: 'assets/file/file-master-angularjs.html',
            controller: 'fileController',
            resolve: {
                // do a listing
                file: function ($route, fileService) {
                    var path = $route.current.params.path;
                    if (path == null) {
                        path = "/";
                    }
                    return fileService.retrieveFileBasics(path);
                },
                vc: function ($route, fileService) {
                    var vc = $route.current.params.vc;
                    if (vc == null) {
                        vc = "root";
                    }
                    return vc;
                }

            }
        });
    })

    .controller('fileController', ['$location','$scope', 'fileService', '$translate', '$log', '$http', 'messageCenterService', 'file', 'tagService','starService','mimeTypeService','$window','vc',function ($location,$scope, fileService, $translate, $log, $http, $messageCenterService, file, tagService, starService, mimeTypeService, $window,vc) {

        $scope.file = file;
        $scope.infoTab = true;
        $scope.vc = vc;

        $scope.checksumVisible = false;
        $scope.tagVisible = false;

        $scope.showChecksum = function() {
          $scope.checksumVisible = true;
        };

        $scope.hideChecksum = function() {
            $scope.checksumVisible = false;
        };

        $scope.showTag = function() {
            $scope.tagVisible = true;
        };

        $scope.hideTag = function() {
            $scope.tagVisible = false;
        };

        /**
         * star or unstar the given file
         */
        $scope.toggleStar = function() {

            $log.info("toggleStar()");
            var currPath = $scope.file.domainObject.absolutePath;
            if (!currPath) {
                $log.error("no path found, cannot star");
                throw "no absolute path";
            }
            $log.info("currPath:" + currPath);

            if ($scope.file.starred) {
                $log.info("remove star");
                starService.removeStar(currPath);
                $scope.file.starred = false;
            } else {
                $log.info("adding a star");
                starService.addStar(currPath);
                $scope.file.starred = true;
            }
        };

        $scope.showFileMetadata = function (absPath) {
            alert("show file metadata");
            $log.info("showFileMetadata()");
            if (!absPath) {
                throw "no apsPath provided";
            }

            $log.info("absPath:" + absPath);
            $location.path("/file/metadata");
            $location.search("path", absPath);

        };

        /**
         * Return to the parent collection in the home view
         */
        $scope.selectParentCollection = function() {
            $location.path("/home/" + $scope.vc);
            $location.search("path",  $scope.file.parentPath);
        };

        /**
         * Get a css class string based on the mime type of the current file.  This handles null data by returning a blank class
         */
        $scope.getFileIconClass = function() {
            if (!$scope.file) {
                return "";
            }

            $log.info("getting mime type for file:" + $scope.file.mimeType);

            return mimeTypeService.iconClassFromMimeTypeFullSize($scope.file.mimeType);

        }

    }])
    .factory('fileService', ['$http', '$log', '$q','tagService', function ($http, $log, $q, tagService) {

        var fileService = {
            /**
             * List the contents of a collection, based on the type of virtual collection, and any subpath
             * @param reqVcName
             * @param reqParentPath
             * @param reqOffset
             * @returns {*|Error}
             */
            retrieveFileBasics: function (path) {

                $log.info("get basic info about the file");

                if (!path) {
                    $log.error("path is missing");
                    throw "path is missing";
                }

                var deferred = $q.defer();

                var promise =  $http({method: 'GET', url: 'file/', params: {path: path}}).success(function(data, status, headers, config) {

                    deferred.resolve(data);
                    // decorate data with tag string
                    $log.info("return from call toget fileBasics:" + data);
                    data.tagString = tagService.tagListToTagString(data.irodsTagValues);


                }).error(function () {
                        return null;
                    });

                return deferred.promise;
            },

            /**
             * Create a new child folder underneath the given parent collection
             * @param parentPath path of parent
             * @param newChildName name of new folder
             * @returns {*|Error}
             */
            createNewFolder: function (parentPath, newChildName) {

                $log.info("createNewFolder()");

                if (!parentPath) {
                    $log.error("parentPath is missing");
                    throw "parentPath is missing";
                }

                if (!newChildName) {
                    $log.error("newChildName is missing");
                    throw "newChildName is missing";
                }

                var path = parentPath + "/" + newChildName;


                return $http({method: 'PUT', url: 'collection/', params: {path: path}}).success(function (data) {
                    $log.info("successfully added:" + path);

                   // var newFolder = { "collection": true, "createdAt": "", "dataObject": false, "dataSize": 0, "description": "", "displayDataSize": "", "formattedAbsolutePath": path, "nodeLabelDisplayValue": newChildName, "objectType": {"enumType": "org.irods.jargon.core.query.CollectionAndDataObjectListingEntry$ObjectType", "name": "COLLECTION"},
                     //   "parentPath": parentPath, "pathOrName": "/" + newChildName, "specColType": {"enumType": "org.irods.jargon.core.pub.domain.ObjStat$SpecColType", "name": "NORMAL"}};
                    return data;

                }).error(function () {
                        return null;
                    });
            }

        };

        return fileService;

    }]);



