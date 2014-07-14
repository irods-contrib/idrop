/**
 *
 */


describe("Tests of the login controller", function () {

    var $http, $httpBackend, $log, $translate, ctrlScope, controller, userService, rootScope;
    beforeEach(module('login'));
    beforeEach(inject(function (_$http_, _$httpBackend_, _$log_ , _$translate_, _$rootScope_, $controller, _userService_) {
        $http = _$http_;
        $log = _$log_;
        $httpBackend = _$httpBackend_;
        $translate = _$translate_;
        ctrlScope = _$rootScope_.$new();
        controller = $controller('loginController', {$scope: ctrlScope});
        userService = _userService_;
        rootScope = _$rootScope_;
    }));


    it("login should set identity in userService", function () {
        var irodsAccountVal = irodsAccount("host", 1247, "zone", "user", "password", "", "resc");

        var responseFromAuth = {"defaultStorageResource":null,"serverVersion":"rods3.3","userName":"test1","zone":"test1"};

        $httpBackend.whenPOST('login').respond(responseFromAuth);
        ctrlScope.submitLogin();

        $httpBackend.flush();

        userService.retrieveLoggedInIdentity().then(function (d) {
            expect(responseFromAuth).toEqual(d);
        });

    });


});