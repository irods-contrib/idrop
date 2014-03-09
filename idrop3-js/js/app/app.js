/*
 Central module registry for the entire application
 */


(function () {
    'use strict';
    // this function is strict...
}());

angular.module('app', ['ngRoute', 'ngResource', 'httpInterceptorModule','home','login']);

angular.module('home', ['ngRoute', 'ngResource', 'httpInterceptorModule']);

angular.module('login', ['ngRoute', 'ngResource', 'httpInterceptorModule']);


angular.module('app')


    .config(function($routeProvider) {
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

    .controller('appController', function () {


    });
