/**
 * Home page controllers
 * Created by mikeconway on 3/9/14.
 */

/*
 * Home controller function here
 */
angular.module('home', ['httpInterceptorModule','userServiceModule', 'angularTranslateApp','virtualCollectionsModule'])

    .controller('homeController', ['$scope', '$translate', '$log', '$http', '$location', 'userService','virtualCollectionsService',function ($scope, $translate, $log, $http, $location,userService, virtualCollectionsService) {

        $scope.init = function () {
            $log.info("getting logged in identity");
            $scope.loggedInIdentity = userService.getLoggedInIdentity();
            $log.info("logged in identity....");
            $log.info($scope.loggedInIdentity);
            $log.info("getting virtual colls");
            $scope.virtualCollections = virtualCollectionsService.listUserVirtualCollections()
        };

        $scope.hideDrives = "false";
        // create a message to display in our view
        $scope.loggedInIdentity = {};
        $scope.virtualCollections = {};
        $scope.init();

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



