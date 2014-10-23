/**
 * Created by Mike on 10/23/2014.
 */



describe("Tests of mime type service functions", function () {

    var $log;
    beforeEach(module('mimeTypeServiceModule'));
    beforeEach(inject(function (_mimeTypeService_, _$log_) {
        mimeTypeService = _mimeTypeService_;
        $log = _$log_;
    }));

    it("get icon class when no mime type provided should return default", function () {
        var actual = mimeTypeService.iconClassFromMimeTypeFullSize();
        expect(actual).toEqual("glyphext2x glyphext-file-2x");
    });




});
