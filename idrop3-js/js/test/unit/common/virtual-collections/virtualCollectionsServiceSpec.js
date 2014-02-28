/**
 *
 */
(function () {
    'use strict';
    // this function is strict...
}());
describe("A suite", function () {

    var virtualCollectionsService, $http, $httpBackend;
    beforeEach(module('virtualCollectionsModule'));
    beforeEach(inject(function (_virtualCollectionsService_,_$http_, _$httpBackend_) {
        virtualCollectionsService = _virtualCollectionsService_;
        $http = _$http_;
        $httpBackend = _$httpBackend_;
    }));


    it("list virtual collections should have exception because no irods account", function () {
        expect(function () {
            virtualCollectionsService.listUserVirtualCollections()
        }).toThrow(new Error('no iRODS account'));
    });

    it("list virtual collections should return a list of colls", function () {
        var irodsAccountVal = irodsAccount("host", 1247, "zone", "user", "password", "", "resc");
        $httpBackend.whenGET('/virtualCollections').respond("hello");
        var actual = virtualCollectionsService.listUserVirtualCollections(irodsAccountVal);
        console.log("actual is:" + actual);
        $httpBackend.flush();
        expect(actual).toEqual('hello');
    });
});