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
     * login controller function here
     */

    .controller('loginController', function ($scope, $translate, $log) {

        $scope.login = {};

        $scope.changeLanguage = function (langKey) {
            $translate.use(langKey);
        };

        $scope.submitLogin = function() {
            alert($scope.login.userName);
            // how to validate?
            // where do errors go?

            var irodsAccount = irodsAccount(login.host, login.port, login.zone, loginu.serName, login.password, "STANDARD", "");
            log.info("irodsAccount for host:" + login.host);

            alert (login.host);




        }


    });




