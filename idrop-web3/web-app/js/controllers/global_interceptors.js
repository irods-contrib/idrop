app.config(function($provide) {
// register the interceptor as a service
$provide.factory('myHttpInterceptor', function($q, dependency1, dependency2) {
  return {
    // optional method
    'request': function(config) {
      // do something on success
      return config || $q.when(config);
    },
 
    // optional method
   'requestError': function(rejection) {
      // do something on error
      if (canRecover(rejection)) {
    	  // FIXME: rejection
        return false;
      }
      return $q.reject(rejection);
    },

    // optional method
    'response': function(response) {
      // do something on success
      return response || $q.when(response);
    },
 
    // optional method
   'responseError': function(rejection) {
      // do something on error
      if (canRecover(rejection)) {
    	  // FIXME: rejection
        return false;
      }
      return $q.reject(rejection);
    }
  }
});
 
$httpProvider.interceptors.push('myHttpInterceptor');
 
// register the interceptor via an anonymous factory
$httpProvider.interceptors.push(function($q, dependency1, dependency2) {
  return {
   'request': function(config) {
       // same as above
    },
    'response': function(response) {
       // same as above
    }
  }
});
});