/*
 Central module registry for the entire application
 */


(function () {
    'use strict';
    // this function is strict...
}());

angular.module('app', ['ngRoute', 'ngResource', 'httpInterceptorModule', 'home', 'login', 'flash','virtualCollectionFilter','MessageCenterModule','urlEncodingModule']);

angular.module('flash', []);

angular.module('app')

    .config(function ($routeProvider) {
        // route for the home page
        $routeProvider
            /*
            .when('/home', {
            templateUrl: 'assets/home/home-angularjs.html',
            controller: 'homeController'
        })*/

            // route for the login page
            .when('/login', {
                templateUrl: 'assets/home/login-angularjs.html',
                controller: 'loginController'
            }).otherwise({redirectTo: "/home"});
    })

/**
 * Provides a directive to prevent form loading per http://stackoverflow.com/questions/12319758/angularjs-clicking-a-button-within-a-form-causes-page-refresh
 * use the prevent-default directive on form buttons
 */.directive('preventDefault', function () {
        return function (scope, element, attrs) {
            angular.element(element).bind('click', function (event) {
                event.preventDefault();
                event.stopPropagation();
            });
        }
    })

    .controller('appController', function () {


    });
