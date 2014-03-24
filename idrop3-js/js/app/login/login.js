/**
 * Created by mikeconway on 3/8/14.
 */
/**
 * Main angular module for login/logout views
 *
 * @author Mike Conway (DICE)
 */


angular.module('login', [ 'httpInterceptorModule', 'angularTranslateApp'])


    .config(function () {
        /*
         * configuration block
         */

    })


    /*
     * login controller fçunction here
     */

    .controller('loginController', ['$scope', '$translate', '$log', '$http', '$location', 'identityService', function ($scope, $translate, $log, $http, $location, identityService) {

        $scope.login = {};

        $scope.loggedInIdentity = identityService.loggedInIdentity;

        $scope.changeLanguage = function (langKey) {
            $translate.use(langKey);
        };

        $scope.getLoggedInIdentity = function () {
            return identityService.loggedInIdentity;

        };


        $scope.submitLogin = function () {
            var actval = irodsAccount($scope.login.host, $scope.login.port, $scope.login.zone, $scope.login.userName, $scope.login.password, "STANDARD", "");
            $log.info("irodsAccount for host:" + actval);
            $http({
                method: 'POST',
                url: 'login',
                data: actval,
                headers: { 'Content-Type': 'application/json' }  // set the headers so angular passing info as request payload
            })
                .success(function (data) {
                    $log.info(data);


                    identityService.loggedInIdentity = data;
                    $location.path("/home");

                });
        };

    }]).service('identityService', ['$log', function ($log) {

        this.loggedInIdentity = null;


    }]);










