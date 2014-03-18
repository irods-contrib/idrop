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

    .controller('loginController', function ($scope, $translate, $log) {

        $scope.login = {};

        $scope.changeLanguage = function (langKey) {
            $translate.use(langKey);
        };

        $scope.submitLogin = function() {
            alert($scope.login.userName);
            // how to validate?
            // where do errors go?

            var actval = irodsAccount(login.host, login.port, login.zone, login.userName, login.password, "STANDARD", "");
            $log.info("irodsAccount for host:" + actval);

            alert (login.host);




        }


    });




