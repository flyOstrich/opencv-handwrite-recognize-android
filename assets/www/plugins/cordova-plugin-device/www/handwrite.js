cordova.define("cordova-plugin-handwrite.handwrite", function (require, exports, module) {

    var exec = require('cordova/exec');


    function HandWrite() {
    }

    /**
     * Get device info
     *
     * @param {Function} successCallback The function to call when the heading data is available
     * @param {Function} errorCallback The function to call when there is an error getting the heading data. (OPTIONAL)
     */
    HandWrite.prototype.recognize = function (imgBase64, successCallback, errorCallback) {
//    argscheck.checkArgs('fF', 'HandWrite.getInfo', arguments);
        exec(successCallback, errorCallback, "HandWrite", "recognize", [imgBase64]);
    };
    HandWrite.prototype.showImageList = function (successCallback, errorCallback) {
//    argscheck.checkArgs('fF', 'HandWrite.getInfo', arguments);
        exec(successCallback, errorCallback, "HandWrite", "showImageList", []);
    };
    HandWrite.prototype.setTrainImage = function (trainVal, imgBase64, successCallback, errorCallback) {
//    argscheck.checkArgs('fF', 'HandWrite.getInfo', arguments);
        exec(successCallback, errorCallback, "HandWrite", "setTrainImage", [trainVal, imgBase64]);
    };
    HandWrite.prototype.deleteTrainImage = function (imgPath, successCallback, errorCallback) {
        //    argscheck.checkArgs('fF', 'HandWrite.getInfo', arguments);
        exec(successCallback, errorCallback, "HandWrite", "deleteTrainImage", [imgPath]);
    };
    HandWrite.prototype.getTrainImageList = function (successCallback, errorCallback) {
        //    argscheck.checkArgs('fF', 'HandWrite.getInfo', arguments);
        exec(successCallback, errorCallback, "HandWrite", "getTrainImageList",[]);
    };
    HandWrite.prototype.trainFromTrainImages = function (successCallback, errorCallback) {
        //    argscheck.checkArgs('fF', 'HandWrite.getInfo', arguments);
        exec(successCallback, errorCallback, "HandWrite", "trainFromTrainImages",[]);
    };

     HandWrite.prototype.getLabelCharacterMap = function (successCallback, errorCallback) {
            //    argscheck.checkArgs('fF', 'HandWrite.getInfo', arguments);
            exec(successCallback, errorCallback, "HandWrite", "getLabelCharacterMap",[]);
     };



    module.exports = new HandWrite();

});
