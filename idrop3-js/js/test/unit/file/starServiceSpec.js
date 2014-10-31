/**
 *
 */
describe("star service tests", function () {

    var starService, $http, $httpBackend, $log;

    beforeEach(module('StarModule'));

    beforeEach(inject(function (_$httpBackend_, _$log_, _starService_, _$http_) {
        starService = _starService_;
        $http = _$http_;
        $log = _$log_;
        $httpBackend = _$httpBackend_;
    }));

    it("add star on a collection should call proper http put", function () {
        var actual;

        var irodsAccountVal = irodsAccount("host", 1247, "zone", "user", "password", "", "resc");
        var path = "star/a/path";

        $httpBackend.whenPUT(path).respond(204);

        starService.addStar("/a/path").then(function (d) {
            actual = d;
        });

        $httpBackend.flush();
        console.log("actual is:" + actual);

        expect(actual.status).toEqual(204);
    });

});