sap.ui.define(["sap/ui/core/mvc/Controller"], function(Controller) {
	"use strict";
	return Controller.extend("com.sap.sample.books_ui.controller.Books", {

		_addBookDialog: null,

		onInit: function() {},
		/**
		 *@memberOf com.sap.sample.books_ui.controller.Books
		 */
		onAddBook: function() {
			this._getNewBookDialog().open();
		},

		_getNewBookDialog: function() {
			this._addBookDialog = sap.ui.xmlfragment("com.sap.sample.books_ui.view.NewBook", this);
			this.getView().addDependent(this._addBookDialog);
			return this._addBookDialog;
		},

		onDialogCancel: function() {
			this._addBookDialog.close();
		},

		onDialogCreate: function(oEvent) {
			
			var oBinding = this.getView().byId("booksTable").getBinding("items");
			var oContext = oBinding.create({
				"bookName": "test book",
				"authorName": "Ran",
				"isbn": "12345",
				"price": 100,
				"priceCurrency": "EUR"
			});

			oContext.created().then(function() {
				// will be called when a new book will be created
			});

			var oView = this.getView();

			function resetBusy() {
				oView.setBusy(false);
			}

			// lock UI until submitBatch is resolved, to prevent errors caused by updates while submitBatch is pending
			oView.setBusy(true);
			oView.getModel().submitBatch(oView.getModel().getUpdateGroupId()).then(resetBusy, resetBusy);
		}
	});
});