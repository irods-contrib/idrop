/**
 * Main angular module for home views
 *
 * @author Mike Conway (DICE)
 */

function genId(path) {
    return encodeURIComponent(encodeURIComponent(path));
}

angular.module('home', ['ngRoute', 'ngResource', 'httpInterceptorModule'], function ($provide, $routeProvider, $locationProvider) {

    // route for the home page
    $routeProvider.when('/home', {
        templateUrl: 'assets/home/home-angularjs.html',
        controller: 'homeController'
    })

        // route for the login page
        .when('/login', {
            templateUrl: 'assets/home/login-angularjs.html',
            controller: 'loginController'
        })
        .otherwise({redirectTo: "/home"});
})

    .config(function () {
        /*
         * configuration block
         */


    })

    /*
     * Home controller function here
     */
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
