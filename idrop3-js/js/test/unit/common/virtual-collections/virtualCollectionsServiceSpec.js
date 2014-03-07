/**
 *
 */
(function () {
    'use strict';
    // this function is strict...
}());
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

    var actual;

   it("list virtual collections should return a list of colls", function () {
        var irodsAccountVal = irodsAccount("host", 1247, "zone", "user", "password", "", "resc");
        $httpBackend.whenGET('/virtualCollections').respond("hello");
        virtualCollectionsService.listUserVirtualCollections(irodsAccountVal).then(function(d) {
            actual = d;
        });

        //var actual = virtualCollectionsService.virtualCollectionsService(irodsAccountVal);
       $httpBackend.flush();
       console.log("actual is:" + actual);

       expect($log.info.logs).toContain(['doing get of virtual collections']);

        expect(actual.data).toEqual('hello');
    });
});