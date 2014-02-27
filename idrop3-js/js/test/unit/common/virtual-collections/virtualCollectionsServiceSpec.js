/**
 * 
 */

describe("A suite", function() {

   var virtualCollectionsService;
    beforeEach(module('virtualCollectionsModule'));
    beforeEach(inject(function(_virtualCollectionsService_) {
        virtualCollectionsService = _virtualCollectionsService_;
    }))

  it("list virtual collections should return a list of colls", function() {
      var actual = virtualCollectionsService.listUserVirtualCollections();
      expect(actual).toEqual('hello');
  });
});