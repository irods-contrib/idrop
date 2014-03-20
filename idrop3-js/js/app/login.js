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

    .controller('loginController', function ($scope, $translate, $log, $http) {

        $scope.login = {};

        $scope.changeLanguage = function (langKey) {
            $translate.use(langKey);
        };

        $scope.submitLogin = function() {
            // how to validate?
            // where do errors go?

            var actval = irodsAccount($scope.login.host, $scope.login.port, $scope.login.zone, $scope.login.userName, $scope.login.password, "STANDARD", "");
            $log.info("irodsAccount for host:" + actval);
            var responsePromise = $http.post('login',
                actval
            );
            responsePromise.then(function(response) {
                $log.info("response:");
                $log.info(response);
            }, function(response) {
                $log.error("error:" + response);
                alert("error!");
            });
        }
    });










