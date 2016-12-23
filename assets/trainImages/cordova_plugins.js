cordova.define('cordova/plugin_list', function(require, exports, module) {
module.exports = [
    {
        "file": "plugins/cordova-plugin-device/www/device.js",
        "id": "cordova-plugin-device.device",
        "clobbers": [
            "device"
        ]
    },
     {
            "file": "plugins/cordova-plugin-device/www/handwrite.js",
            "id": "cordova-plugin-handwrite.handwrite",
            "clobbers": [
                "handwrite"
            ]
     }
];
module.exports.metadata = 
// TOP OF METADATA
{
    "cordova-plugin-whitelist": "1.3.1",
    "cordova-plugin-device": "1.1.4",
    "cordova-plugin-handwrite": "1.0.0"
};
// BOTTOM OF METADATA
});