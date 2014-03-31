/**
 * Home page controllers
 * Created by mikeconway on 3/9/14.
 */

/*
 * Home controller function here
 */
angular.module('home', ['httpInterceptorModule', 'angularTranslateApp', 'virtualCollectionsModule'])

    .controller('homeController', ['$scope','virtualCollectionsService','$translate', '$log', '$http', '$location',function ($scope, virtualCollectionsService, $translate, $log, $http, $location) {

        $scope.listVirtualCollections = function () {

            $log.info("getting virtual colls");
            virtualCollectionsService.listUserVirtualCollections().then(function (virColls) {
                console.log(virColls.data);
                $scope.virtualCollections = virColls.data;
            });
        };

        $scope.hideDrives = "false";
        /*
        Init the virtual collections
         */

           $scope.listVirtualCollections();

        /*
         * Cause the collections panel on the left to display
         */
        $scope.showCollections = function () {
            alert("show collections");
            $scope.hideDrives = "false";
        };

        /*
         * Cause the collections panel on the left to be hidden
         */
        $scope.hideCollections = function () {
            $scope.hideDrives = "true";
        };
    }]);



