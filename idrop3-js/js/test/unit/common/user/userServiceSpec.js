/**
 *
 */

function mockUserIdentity() {

    var userIdentity = [
        {"name": "vc1", description: "desc1", sourcePath: "source/path"}
    ];
    return userIdentity;

}

describe("Tests of User Services", function () {

    var userService, $http, $httpBackend, $log;
    beforeEach(module('userServiceModule'));
    beforeEach(inject(function (_userService_, _$http_, _$httpBackend_, _$log_) {
        userService = _userService_;
        $http = _$http_;
        $log = _$log_;
        $httpBackend = _$httpBackend_;
    }));


    it("get of logged in identity from server should return data", function () {
        var actual;
        var uid = mockUserIdentity();
        $httpBackend.whenGET('user').respond(uid);
        userService.retrieveLoggedInIdentity().then(function (d) {
            actual = d;
        });

        $httpBackend.flush();
        console.log("actual is:" + actual);

        expect($log.info.logs).toContain(['doing get of userIdentity from server']);

        expect(actual.data).toEqual(uid);
        expect(actual.status).toEqual(200);
    });

});