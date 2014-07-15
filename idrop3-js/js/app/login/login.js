/**
 * Created by mikeconway on 3/8/14.
 */
/**
 * Main angular module for login/logout views
 *
 * @author Mike Conway (DICE)
 */


angular.module('login', [ 'httpInterceptorModule', 'angularTranslateApp', 'userServiceModule', 'MessageCenterModule'])


    .config(function () {
        /*
         * configuration block
         */

    })

    /*
     * login controller function here
     */

    .controller('loginController', ['$scope', '$translate', '$log', '$http', '$location', 'userService','globals','$q', '$timeout' ,function ($scope, $translate, $log, $http, $location, userService, globals, $q, $timeout) {


        $scope.login = {
            authType: 'STANDARD'
        };


        $scope.changeLanguage = function (langKey) {
            $translate.use(langKey);
        };

        $scope.getLoggedInIdentity = function () {
            return userService.retrieveLoggedInIdentity.success(function (identity) {
                $scope.loggedInIdentity = identity
            });

        };


        $scope.submitLogin = function () {
            var actval = irodsAccount($scope.login.host, $scope.login.port, $scope.login.zone, $scope.login.userName, $scope.login.password, $scope.login.authType, "");
            $log.info("irodsAccount for host:" + actval);
            $http({
                method: 'POST',
                url: 'login',
                data: actval,
                headers: { 'Content-Type': 'application/json' }  // set the headers so angular passing info as request payload
            }).then(function (data) {
                    $log.info("login successful" + data);
                    // userService.setLoggedInIdentity(data);

                    var path = globals.getLastPath();
                    return $q.when(path);

                }).then(function(path) {

                    if (!path) {
                        $log.info("hard code to go home");
                       path="/home/home";
                    } else {
                        // setpath
                        $log.info("setting location to last path:" + path);
                    }

                    $timeout(function () {
                        $location.path(path);
                    });

                    $log.info("end login success processing");

                });
        };

    }]);









