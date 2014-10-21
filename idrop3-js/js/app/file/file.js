/**
 * Home page controllers
 * Created by mikeconway on 3/9/14.
 */

/*
 * File controller function here, representing collection and data object catalog info and operations
 */
angular.module('fileModule', ['httpInterceptorModule', 'angularTranslateApp', 'MessageCenterModule', 'ngRoute', 'tagServiceModule'])

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
                }
            }
        });
    })

    .controller('fileController', ['$scope', 'fileService', '$translate', '$log', '$http', '$location', 'messageCenterService', 'fileService', 'tagService', function ($scope, fileService, $translate, $log, $http, $location, $messageCenterService, file, tagService) {

        $scope.file = file;
        $scope.infoTab = true;

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


    }])
    .factory('fileService', ['$http', '$log', 'tagService', function ($http, $log, tagService) {

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

                return $http({method: 'GET', url: 'file/', params: {path: path}}).success(function (data) {

                    // decorate data with tag string

                    data.tagString = tagService.tagListToTagString(data.irodsTagValues);
                    return data;

                }).error(function () {
                        return null;
                    });
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



