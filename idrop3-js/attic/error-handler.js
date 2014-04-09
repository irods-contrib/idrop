
/**
 * Generic error handling
 * see http://chstrongjavablog.blogspot.com/2013/09/creating-global-http-error-handler-for.html
 * Created by Mike on 4/9/14.
 */
angular.module('xx-http-error-handling', [])
    .config(function($provide, $httpProvider, $compileProvider) {
        var elementsList = $();

        // this message will appear for a defined amount of time and then vanish again
        var showMessage = function(content, cl, time) {
            $('<div/>')
                .addClass(cl)
                .hide()
                .fadeIn('fast')
                .delay(time)
                .fadeOut('fast', function() { $(this).remove(); })
                .appendTo(elementsList)
                .text(content);
        };

        // push function to the responseInterceptors which will intercept
        // the http responses of the whole application
        $httpProvider.responseInterceptors.push(function($timeout, $q) {
            return function(promise) {
                return promise.then(function(successResponse) {
                        // if there is a successful response on POST, UPDATE or DELETE we display
                        // a success message with green background
                        if (successResponse.config.method.toUpperCase() != 'GET') {
                            showMessage('Success', 'xx-http-success-message', 5000);
                            return successResponse;
                        }
                    },
                    // if the message returns unsuccessful we display the error
                    function(errorResponse) {
                        switch (errorResponse.status) {
                            case 400: // if the status is 400 we return the error
                                showMessage(errorResponse.data.message, 'xx-http-error-message', 6000);
                                // if we have found validation error messages we will loop through
                                // and display them
                                if(errorResponse.data.errors.length > 0) {
                                    for(var i=0; i<errorResponse.data.errors.length; i++) {
                                        showMessage(errorResponse.data.errors[i],
                                            'xx-http-error-validation-message', 6000);
                                    }
                                }
                                break;
                            case 401: // if the status is 401 we return access denied
                                showMessage('Wrong email address or password!',
                                    'xx-http-error-message', 6000);
                                break;
                            case 403: // if the status is 403 we tell the user that authorization was denied
                                showMessage('You have insufficient privileges to do what you want to do!',
                                    'xx-http-error-message', 6000);
                                break;
                            case 500: // if the status is 500 we return an internal server error message
                                showMessage('Internal server error: ' + errorResponse.data.message,
                                    'xx-http-error-message', 6000);
                                break;
                            default: // for all other errors we display a default error message
                                showMessage('Error ' + errorResponse.status + ': ' + errorResponse.data.message,
                                    'xx-http-error-message', 6000);
                        }
                        return $q.reject(errorResponse);
                    });
            };
        });

        // this will display the message if there was a http return status
        $compileProvider.directive('httpErrorMessages', function() {
            return {
                link: function(scope, element, attrs) {
                    elementsList.push($(element));
                }
            };
        });
    });