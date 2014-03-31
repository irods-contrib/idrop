/**
 * Home page controllers
 * Created by mikeconway on 3/9/14.
 */

/*
 * Home controller function here
 */
angular.module('home', ['httpInterceptorModule', 'userServiceModule', 'angularTranslateApp', 'virtualCollectionsModule'])

    .controller('homeController', ['$scope', '$translate', '$log', '$http', '$location', 'userService', 'virtualCollectionsService', function ($scope, $translate, $log, $http, $location, userService, virtualCollectionsService) {

        $scope.listVirtualCollections = function () {

            $log.info("getting virtual colls");
            virtualCollectionsService.listUserVirtualCollections().success(function (virColls) {
                $scope.virtualCollections = virColls.data;
            });
        };

        $scope.hideDrives = "false";

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



