sap.ui.define([
	"sap/ui/core/mvc/Controller",
	"com/sap/sample/books_ui/model/models",
	"com/sap/sample/books_ui/utils/utils"
], function(Controller, models, utils) {
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
			// set empty JSON model 
			this._addBookDialog.setModel(models.createNewBookDialogEmptyModel());
			this.getView().addDependent(this._addBookDialog);
			return this._addBookDialog;
		},
		onDialogCancel: function() {
			this._addBookDialog.close();
		},
		onDialogCreate: function() {
			var oView = this.getView();
			var self = this;
			var oBinding = this.getView().byId("booksTable").getBinding("items");
			var iRandomBookId = parseInt(utils.generateRandomNumber(100, 9999));
			var oDialogModel = this._getDialogModel();
			var oContext = oBinding.create({
				"bookId": iRandomBookId,
				"bookName": oDialogModel.getProperty("/bookName"),
				"authorName": oDialogModel.getProperty("/authorName"),
				"isbn": oDialogModel.getProperty("/isbn"),
				"price": parseInt(oDialogModel.getProperty("/price")),
				"priceCurrency": utils.getDefaultCurrencyCode()
			});
			oContext.created().then(function() {
				// refresh binding in order to allow the creation of additional entities
				oBinding.refresh();
				self._addBookDialog.close();
				utils.showInfoMessage(utils.getDefaultResourceBundle().getText("newBookCreatedInfoMessage"));
			});

			function resetBusy() {
				self._addBookDialog.setBusy(false);
			}
			// lock UI 
			this._addBookDialog.setBusy(true);
			oView.getModel().submitBatch(oView.getModel().getUpdateGroupId()).then(resetBusy, resetBusy);
		},
		onDialogInputChange: function() {
			var oDialogModel = this._getDialogModel();
			var canCreate = oDialogModel.getProperty("/bookName").length > 0 && oDialogModel.getProperty("/authorName").length > 0 &&
				oDialogModel.getProperty("/isbn").length > 0 && oDialogModel.getProperty("/price").length > 0;
			oDialogModel.setProperty("/canCreate", canCreate);
		},
		_getDialogModel: function() {
			return this._addBookDialog.getModel();
		},
		/**
		 *@memberOf com.sap.sample.books_ui.controller.Books
		 */
		onDeleteBook: function(oEvent) {
			debugger;
			if (oEvent) {
				
			}
		}
	});
});