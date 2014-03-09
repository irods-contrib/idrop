/**
 * Main angular module for master index page and routes
 *
 * @author Mike Conway (DICE)
 */


angular.module('app', function ($provide, $routeProvider, $locationProvider) {

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
    });
