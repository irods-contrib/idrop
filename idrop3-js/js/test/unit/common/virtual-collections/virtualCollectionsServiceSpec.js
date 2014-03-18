/**
 *
 */

function mockAVc() {

    var vcData = [
        {"name": "vc1", description: "desc1", sourcePath: "source/path"}
    ];
    return vcData;


}

function mockAVc2() {

    var vcData = [
        {"name": "vc2", description: "desc1", sourcePath: "source/path"}
    ];
    return vcData;


}


describe("A suite", function () {

    var virtualCollectionsService, $http, $httpBackend, $log;
    beforeEach(module('virtualCollectionsModule'));
    beforeEach(inject(function (_virtualCollectionsService_, _$http_, _$httpBackend_, _$log_) {
        virtualCollectionsService = _virtualCollectionsService_;
        $http = _$http_;
        $log = _$log_;
        $httpBackend = _$httpBackend_;
    }));


    it("list virtual collections should have exception because no irods account", function () {
        expect(function () {
            virtualCollectionsService.listUserVirtualCollections()
        }).toThrow(new Error('no iRODS account'));
    });


    it("list virtual collections should return a list of colls", function () {
        var actual;

        var irodsAccountVal = irodsAccount("host", 1247, "zone", "user", "password", "", "resc");
        var vc = mockAVc();
        $httpBackend.whenGET('/virtualCollections').respond(vc);
        virtualCollectionsService.listUserVirtualCollections(irodsAccountVal).then(function (d) {
            actual = d;
        });

        $httpBackend.flush();
        console.log("actual is:" + actual);

        expect($log.info.logs).toContain(['doing get of virtual collections']);

        expect(actual.data).toEqual(vc);
        expect(actual.status).toEqual(200);
    });

    it("list virtual collections should return an exception fro http", function () {
        var actual;

        var irodsAccountVal = irodsAccount("host", 1247, "zone", "user", "password", "", "resc");
        $httpBackend.whenGET('/virtualCollections').respond('500',"error");
        virtualCollectionsService.listUserVirtualCollections(irodsAccountVal).then(function (d) {
            actual = d;
        });

        $httpBackend.flush();
        console.log("actual is:" + actual);

        expect($log.info.logs).toContain(['doing get of virtual collections']);

        expect(actual.status).toEqual(500);
    });


});