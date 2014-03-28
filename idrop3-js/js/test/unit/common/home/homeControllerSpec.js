/**
 *
 */


describe("Tests of the home controller", function () {

    var $http, $httpBackend, $log, $translate, scope, controller, userService, rootScope, _$q_;
    beforeEach(module('home'));

    /**
     * Mocking userService for controller, see http://stackoverflow.com/questions/15854043/mock-a-service-in-order-to-test-a-controller
     */



    beforeEach(inject(function (_$http_, _$httpBackend_, _$log_ , _$translate_, _$rootScope_, $controller, _userService_, _$q_) {
        $http = _$http_;
        $log = _$log_;
        $httpBackend = _$httpBackend_;
        $translate = _$translate_;
        ctrlScope = _$rootScope_.$new();
       // controller = $controller('homeController', {$scope: ctrlScope});
        userService = _userService_;
        rootScope = _$rootScope_;
        $q = _$q_;
        controller = $controller('homeController', {
            $scope: ctrlScope, userService: buildServiceMock($q)
        });
    }));

    /**
     * Fake out the promise I expect from the user service per
     * http://stackoverflow.com/questions/17825798/how-do-i-mock-the-result-in-a-http-get-promise-when-testing-my-angularjs-contro
     * @param q
     * @returns {{retrieveLoggedInIdentity: retrieveLoggedInIdentity, setLoggedInIdentity: setLoggedInIdentity}}
     */
    function buildServiceMock(q) {
      return {

            retrieveLoggedInIdentity: function() {

                var deferred = q.defer();
                // Place the fake return object here
                deferred.resolve({ "one": "three" });
                return deferred.promise;

            },
            setLoggedInIdentity: function(loggedInIdentity)
            {

            }
        };


    }


    it("home should init virtual colls and identity", function () {

            var vcData =
                {"name": "vc1", description: "desc1", sourcePath: "source/path"};

        userService.setLoggedInIdentity({});
        $httpBackend.whenGET('/virtualCollections').respond(vcData);

        ctrlScope.init().success(function(data) {
           
        });
        $httpBackend.flush();

        var actual;
        ctrlScope.virtualCollections.then(function(d) {
            actual = d;
            expect(vcData).toEqual(actual);
        });


    });

});