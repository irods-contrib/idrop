/**
 *
 */


describe("login controller suite", function () {

    var $http, $httpBackend, $log, $translate, scope, controller, identityService, rootScope;
    beforeEach(module('login'));
    beforeEach(inject(function (_$http_, _$httpBackend_, _$log_ , _$translate_, _$rootScope_, $controller, _identityService_) {
        $http = _$http_;
        $log = _$log_;
        $httpBackend = _$httpBackend_;
        $translate = _$translate_;
        ctrlScope = _$rootScope_.$new();
        controller = $controller('loginController', {$scope: ctrlScope});
        identityService = _identityService_;
        rootScope = _$rootScope_;
    }));


    it("login should seet identity in root scope", function () {
        var irodsAccountVal = irodsAccount("host", 1247, "zone", "user", "password", "", "resc");

        var responseFromAuth = {"defaultStorageResource":null,"serverVersion":"rods3.3","userName":"test1","zone":"test1"};

        $httpBackend.whenPOST('login').respond(responseFromAuth);
        ctrlScope.submitLogin();

        $httpBackend.flush();
        expect(responseFromAuth).toEqual(identityService.loggedInIdentity);

       // expect($log.info.logs).toContain(['doing get of virtual collections']);

      // expect($rootScope.id).toEqual(500);
    });


});