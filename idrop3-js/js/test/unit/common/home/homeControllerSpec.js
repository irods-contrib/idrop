/**
 *
 */


describe("Tests of the home controller", function () {

    var $http, $httpBackend, $log, $translate, scope, controller, userService, rootScope;
    beforeEach(module('home'));

    /**
     * Mocking userService for controller, see http://stackoverflow.com/questions/15854043/mock-a-service-in-order-to-test-a-controller
     */
    beforeEach(function() {
        UserServiceMock = {

            getLoggedInIdentity: function() {
                return {};

            },
            setLoggedInIdentity: function(loggedInIdentity)
            {

            }
        };



    });


    beforeEach(inject(function (_$http_, _$httpBackend_, _$log_ , _$translate_, _$rootScope_, $controller, _userService_) {
        $http = _$http_;
        $log = _$log_;
        $httpBackend = _$httpBackend_;
        $translate = _$translate_;
        ctrlScope = _$rootScope_.$new();
       // controller = $controller('homeController', {$scope: ctrlScope});
        userService = _userService_;
        rootScope = _$rootScope_;

        controller = $controller('homeController', {
            $scope: ctrlScope, userService: UserServiceMock
        });
    }));

    it("home should init virtual colls and identity", function () {
        var irodsAccountVal = irodsAccount("host", 1247, "zone", "user", "password", "", "resc");

            var vcData =
                {"name": "vc1", description: "desc1", sourcePath: "source/path"};

        userService.setLoggedInIdentity({});
        $httpBackend.whenGET('/virtualCollections').respond(vcData);

        ctrlScope.init();
        $httpBackend.flush();

        var actual;
        ctrlScope.virtualCollections.then(function(d) {
            actual = d;
            expect(vcData).toEqual(actual);
        });


    });

});