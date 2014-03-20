/**
 * Created by mikeconway on 3/8/14.
 */
/**
 * Main angular module for login/logout views
 *
 * @author Mike Conway (DICE)
 */


angular.module('login')


    .config(function () {
        /*
         * configuration block
         */


    })

    /*
     * login controller fçunction here
     */

    .controller('loginController', function ($scope, $translate, $log, $http, $location) {

        $scope.login = {};

        $scope.changeLanguage = function (langKey) {
            $translate.use(langKey);
        };

        $scope.submitLogin = function() {
            var actval = irodsAccount($scope.login.host, $scope.login.port, $scope.login.zone, $scope.login.userName, $scope.login.password, "STANDARD", "");
            $log.info("irodsAccount for host:" + actval);
            $http({
                method  : 'POST',
                url     : 'login',
                data    : actval,
                headers : { 'Content-Type': 'application/json' }  // set the headers so angular passing info as request payload
            })
                .success(function(data) {
                    //$log.info(data);

                    if (!data.successful) {
                        $log.error(data);
                        // if not successful, bind errors to error variables
                        //$scope.errorName = data.errors.name;
                        //$scope.errorSuperhero = data.errors.superheroAlias;
                    } else {
                        // if successful, bind success message to message
                       $location.path("/home");
                    }
                });
        };

    });










