sap.ui.define([
	"sap/ui/model/json/JSONModel",
	"sap/ui/Device"
], function(JSONModel, Device) {
	"use strict";

	return {

		createDeviceModel: function() {
			var oModel = new JSONModel(Device);
			oModel.setDefaultBindingMode("OneWay");
			return oModel;
		},
		
		createNewBookDialogEmptyModel: function() {
			var oDialogModel = new JSONModel({
				"bookName" : ""	,
				"isbn" : "",
				"price" : "",
				"authorName" : "",
				"canCreate" : false
			});
			
			return oDialogModel;
		}

	};
});