/**
 * Home page controllers
 * Created by mikeconway on 3/9/14.
 */

/*
 * File metadata controller function here, representing collection and data object catalog info and operations
 */
angular.module('fileMetadata', ['globalsModule','httpInterceptorModule', 'angularTranslateApp', 'MessageCenterModule', 'ngRoute', 'fileModule',  'tagServiceModule'])

    /*
     * handle config of routes for home functions
     */
    .config(function ($routeProvider) {
        // route for the home page
        $routeProvider.when('/file/metadata', {
            templateUrl: 'assets/file/metadata/file-metadata-master-angularjs.html',
            controller: 'fileMetadataController',
            resolve: {
                // do a listing
                fileData: function ($route, fileService) {
                    var path = $route.current.params.path;
                    if (path == null) {
                        path = "/";
                    }
                    return fileService.retrieveFileBasics(path);
                }
            }
        });
    })

    .controller('fileMetadataController', ['$scope', 'fileService', 'fileMetadataService', '$translate', '$log', '$http', '$location', 'messageCenterService','fileData',function ($scope, fileService, fileMetadataService, $translate, $log, $http, $location, $messageCenterService, fileData) {

        $scope.file = fileData;
        $scope.metadataTab = true;


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
    .factory('fileMetadataService', ['$http', '$log', 'tagService', function ($http, $log, tagService) {

        var fileMetadataService = {


        };

        return fileMetadataService;

    }]);



