/**
 * Home page controllers
 * Created by mikeconway on 3/9/14.
 */

/*
 * Home controller function here
 */
angular.module('home', ['httpInterceptorModule','login'])

    .controller('homeController',function ($scope, identityService ,$log) {

        $scope.init = function() {
            $log.info("getting logged in identity");
            $scope.loggedInIdentity = identityService.loggedInIdentity;
            $log.info("logged in identity....");
            $log.info($scope.loggedInIdentity);

        };


        $scope.hideDrives = "false";
        // create a message to display in our view
        $scope.message = 'Everyone come and see how good I look!';
        $scope.loggedInIdentity = {};
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
    });



