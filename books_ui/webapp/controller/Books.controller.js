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
			// set placeholders model
			this._addBookDialog.setModel(models.createNewNookPlaceholdersModel(), 'PL');
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
			var oDialogModel = this._getDialogModel();

			// make sure price is an integer field 			
			oDialogModel.setProperty("/data/price", parseInt(oDialogModel.getProperty("/data/price")));
			var oContext = oBinding.create(oDialogModel.oData.data);

			oContext.created().then(function() {
				// refresh binding in order to allow the creation of additional entities
				self._addBookDialog.close();
				utils.showInfoMessage(utils.getDefaultResourceBundle().getText("newBookCreatedInfoMessage"));
			});

			function resetBusy() {
				self._addBookDialog.setBusy(false);
			}
			// lock UI 
			this._addBookDialog.setBusy(true);
			oView.getModel().submitBatch("myAppUpdateGroup").then(resetBusy, resetBusy);
		},

		onDialogInputChange: function() {
			var oDialogModel = this._getDialogModel();
			var canCreate = oDialogModel.getProperty("/data/bookName").length > 0 &&
				oDialogModel.getProperty("/data/authorName").length > 0 &&
				oDialogModel.getProperty("/data/isbn").length > 0 &&
				oDialogModel.getProperty("/data/price").length > 0;
			oDialogModel.setProperty("/canCreate", canCreate);
		},

		_getDialogModel: function() {
			return this._addBookDialog.getModel();
		},
		/**
		 *@memberOf com.sap.sample.books_ui.controller.Books
		 */
		onDeleteBook: function(oEvent) {
			var oItemContext = oEvent.oSource.getBindingContext();
			oItemContext.delete("$auto").then(function() {
				utils.showInfoMessage(utils.getDefaultResourceBundle().getText("bookDeletedMessage"));
			});
		}
	});
});