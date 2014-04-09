/**
 * includes error handling code see: http://chstrongjavablog.blogspot.com/2013/09/creating-global-http-error-handler-for.html
 * Defines interceptors for auth and error handling with the REST back end
 * Created by mikeconway on 3/7/14.
 *
 *
 */

angular.module('httpInterceptorModule', []).factory('myHttpResponseInterceptor', ['$q', '$location', '$log', function ($q, $location, $log, $compileProvider) {
        return {

            elementsList: $(),

            // this message will appear for a defined amount of time and then vanish again
            showMessage: function (content, cl, time) {
                $('<div/>').addClass(cl).hide().fadeIn('fast').delay(time).fadeOut('fast', function () {
                        $(this).remove();
                    }).appendTo(elementsList).text(content);
            },

            // On request success
            request: function (config) {
                // console.log(config); // Contains the data about the request before it is sent.

                // Return the config or wrap it in a promise if blank.
                return config || $q.when(config);
            },

            // On request failure
            requestError: function (rejection) {
                // console.log(rejection); // Contains the data about the error on the request.

                // Return the promise rejection.
                return $q.reject(rejection);
            },

            // On response success
            response: function (response) {
                // console.log(response); // Contains the data from the response.
                $log.info(response);
                this.showMessage('Success', 'xx-http-success-message', 5000);
                // Return the response or promise.
                return response || $q.when(response);
            },

            // On response failture
            responseError: function (rejection) {
                // console.log(rejection); // Contains the data about the error.
                $log.error(rejection);
                var status = rejection.status;

                if (status == 401) { // unauthorized - redirect to login again
                    this.showMessage('Not signed in',
                        'xx-http-error-message', 6000);
                    $location.path("/login");
                } else if (status == 400) { // validation error display errors
                    this.showMessage(rejection.data.message, 'xx-http-error-message', 6000);
                    // if we have found validation error messages we will loop through
                    // and display them
                    if(rejection.data.errors.length > 0) {
                        for(var i=0; i<rejection.data.errors.length; i++) {
                            this.showMessage(rejection.data.errors[i],
                                'xx-http-error-validation-message', 6000);
                        }
                    }
                    return $q.reject(rejection);
                } else {
                    // otherwise reject other status codes
                    log.error("500 error");
                    this.showMessage(rejection.data.message,
                        'xx-http-error-message', 6000);
                    return $q.reject(rejection);

                }

            }
        };


    }])//Http Intercpetor to check auth failures for xhr requests
    .config(['$httpProvider', function ($httpProvider) {
        $httpProvider.interceptors.push('myHttpResponseInterceptor');

        // this will display the message if there was a http return status
        $compileProvider.directive('httpErrorMessages', function () {
            return {
                link: function (scope, element, attrs) {
                    elementsList.push($(element));
                }
            };
        });

    }]);
