/**
 * Home page controllers
 * Created by mikeconway on 3/9/14.
 */

/*
 * Home controller function here
 */
angular.module('home')

    .controller('homeController', function ($scope) {

        $scope.hideDrives = "false";
        // create a message to display in our view
        $scope.message = 'Everyone come and see how good I look!';
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



