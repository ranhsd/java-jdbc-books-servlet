sap.ui.define([
	"sap/ui/core/mvc/Controller"
], function(Controller) {
	"use strict";

	return Controller.extend("com.sap.sample.books_ui.controller.Books", {
		onInit: function() {
			
			// $.get("/odata/JDBCDemo.db._.store/");
			
			var oModel = new sap.ui.model.odata.v4.ODataModel({
				serviceUrl: "/odata/JDBCDemo.db._.store/",
				synchronizationMode: "None"
			});
			
			this.getView().setModel(oModel);
		}
	});
});