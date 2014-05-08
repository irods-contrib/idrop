/**
 *
 */

function mockAVc() {

    var vcData = {"class": "org.irods.jargon.core.query.PagingAwareCollectionListing", "collectionAndDataObjectListingEntries": [
        {"class": "org.irods.jargon.core.query.CollectionAndDataObjectListingEntry", "collection": true, "count": 2, "createdAt": "2013-07-23T13:07:40Z", "dataObject": false, "dataSize": 0, "description": "", "displayDataSize": "0 bytes", "formattedAbsolutePath": "/fedZone1", "id": 10000, "lastResult": true, "modifiedAt": "2013-07-23T13:07:40Z", "nodeLabelDisplayValue": "fedZone1", "objectType": {"enumType": "org.irods.jargon.core.query.CollectionAndDataObjectListingEntry$ObjectType", "name": "COLLECTION"}, "ownerName": "rodsBoot", "ownerZone": "fedZone1", "parentPath": "/", "pathOrName": "/fedZone1", "specColType": {"enumType": "org.irods.jargon.core.pub.domain.ObjStat$SpecColType", "name": "NORMAL"}, "specialObjectPath": "", "totalRecords": 5, "userFilePermission": []},
        {"class": "org.irods.jargon.core.query.CollectionAndDataObjectListingEntry", "collection": true, "count": 3, "createdAt": "2013-07-23T13:32:07Z", "dataObject": false, "dataSize": 0, "description": "", "displayDataSize": "0 bytes", "formattedAbsolutePath": "/fedZone2", "id": 10029, "lastResult": true, "modifiedAt": "2013-07-23T13:32:07Z", "nodeLabelDisplayValue": "fedZone2", "objectType": {"enumType": "org.irods.jargon.core.query.CollectionAndDataObjectListingEntry$ObjectType", "name": "COLLECTION"}, "ownerName": "rods", "ownerZone": "fedZone1", "parentPath": "/", "pathOrName": "/fedZone2", "specColType": {"enumType": "org.irods.jargon.core.pub.domain.ObjStat$SpecColType", "name": "NORMAL"}, "specialObjectPath": "", "totalRecords": 5, "userFilePermission": []},
        {"class": "org.irods.jargon.core.query.CollectionAndDataObjectListingEntry", "collection": true, "count": 4, "createdAt": "2014-03-29T06:13:47Z", "dataObject": false, "dataSize": 0, "description": "", "displayDataSize": "0 bytes", "formattedAbsolutePath": "/testStreamToIRODSFileUsingStreamIOAsRodsUnderRoot", "id": 874007, "lastResult": true, "modifiedAt": "2014-03-29T06:13:47Z", "nodeLabelDisplayValue": "testStreamToIRODSFileUsingStreamIOAsRodsUnderRoot", "objectType": {"enumType": "org.irods.jargon.core.query.CollectionAndDataObjectListingEntry$ObjectType", "name": "COLLECTION"}, "ownerName": "rods", "ownerZone": "fedZone1", "parentPath": "/", "pathOrName": "/testStreamToIRODSFileUsingStreamIOAsRodsUnderRoot", "specColType": {"enumType": "org.irods.jargon.core.pub.domain.ObjStat$SpecColType", "name": "NORMAL"}, "specialObjectPath": "", "totalRecords": 5, "userFilePermission": []},
        {"class": "org.irods.jargon.core.query.CollectionAndDataObjectListingEntry", "collection": true, "count": 5, "createdAt": "2014-03-29T06:14:36Z", "dataObject": false, "dataSize": 0, "description": "", "displayDataSize": "0 bytes", "formattedAbsolutePath": "/testWriteToOutputStreamInSubdirUnderRoot", "id": 874362, "lastResult": true, "modifiedAt": "2014-03-29T06:14:36Z", "nodeLabelDisplayValue": "testWriteToOutputStreamInSubdirUnderRoot", "objectType": {"enumType": "org.irods.jargon.core.query.CollectionAndDataObjectListingEntry$ObjectType", "name": "COLLECTION"}, "ownerName": "rods", "ownerZone": "fedZone1", "parentPath": "/", "pathOrName": "/testWriteToOutputStreamInSubdirUnderRoot", "specColType": {"enumType": "org.irods.jargon.core.pub.domain.ObjStat$SpecColType", "name": "NORMAL"}, "specialObjectPath": "", "totalRecords": 5, "userFilePermission": []}
    ], "collectionsComplete": true, "count": 5, "dataObjectsComplete": true, "dataObjectsCount": 0, "dataObjectsOffset": 0, "dataObjectsTotalRecords": 0, "offset": 0, "pageSizeUtilized": 5000, "pagingStyle": {"enumType": "org.irods.jargon.core.query.PagingAwareCollectionListing$PagingStyle", "name": "MIXED"}, "totalRecords": 5}
    return vcData;

}

describe("Tests list contents of a vc", function () {

    var collectionsService, $http, $httpBackend, $log;
    beforeEach(module('CollectionsModule'));
    beforeEach(inject(function (_collectionsService_, _$http_, _$httpBackend_, _$log_) {
        collectionsService = _collectionsService_;
        $http = _$http_;
        $log = _$log_;
        $httpBackend = _$httpBackend_;
    }));

    it("list collections should return pageable collection listing", function () {
        var actual;

        var irodsAccountVal = irodsAccount("host", 1247, "zone", "user", "password", "", "resc");
        var vc = mockAVc();
        $httpBackend.whenGET('collection/root?offset=0&path=').respond(vc);
        collectionsService.listCollectionContents("root","",0).then(function (d) {
            actual = d;
        });

        $httpBackend.flush();
        console.log("actual is:" + actual);


        expect(actual.data).toEqual(vc);
        expect(actual.status).toEqual(200);
    });

});