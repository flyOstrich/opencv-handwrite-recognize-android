cordova.define("cordova-plugin-handwrite.handwrite", function(require, exports, module) {

var exec=require('cordova/exec');


function HandWrite(){
}
/**
 * Get device info
 *
 * @param {Function} successCallback The function to call when the heading data is available
 * @param {Function} errorCallback The function to call when there is an error getting the heading data. (OPTIONAL)
 */
HandWrite.prototype.recognize = function(imgBase64,successCallback, errorCallback) {
//    argscheck.checkArgs('fF', 'HandWrite.getInfo', arguments);
    exec(successCallback, errorCallback, "HandWrite", "getHandWriteInfo", [imgBase64]);
};

module.exports = new HandWrite();

});
