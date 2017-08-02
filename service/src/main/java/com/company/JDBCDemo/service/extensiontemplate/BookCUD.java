package com.company.JDBCDemo.service.extensiontemplate;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Locale;

import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.server.api.ODataApplicationException;
import org.apache.olingo.server.api.deserializer.DeserializerResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sap.gateway.v4.rt.api.extensions.DataProviderExtensionContext;
import com.sap.gateway.v4.rt.api.extensions.ExtendDataProvider;
import com.sap.gateway.v4.rt.api.extensions.ExtensionContext;
import com.sap.gateway.v4.rt.api.extensions.ExtensionException;
import com.sap.gateway.v4.rt.api.extensions.RequestType;
import com.sap.gateway.v4.rt.cds.api.CDSDSParams;

public class BookCUD {
	
	final static Logger logr = LoggerFactory.getLogger("CustomerCUDExt");

	@ExtendDataProvider(entitySet = { "Book" }, requestTypes = RequestType.CREATE)
	public void createBook(ExtensionContext ecx) throws ODataApplicationException, ExtensionException {
		Connection conn = ((CDSDSParams) ecx.getDSParams()).getConnection();
		PreparedStatement ps = null;

		DataProviderExtensionContext extCtx = ecx.asDataProviderContext();

		DeserializerResult payload = extCtx.getDeserializerResult();
		// Get the entity
		Entity ent = payload.getEntity();

		// Get the value of other properties
		int price = (Integer) ent.getProperty("price").getValue();
		String authorName = (String) ent.getProperty("authorName").getValue();
		String bookName = (String) ent.getProperty("bookName").getValue();
		String isbn = (String) ent.getProperty("isbn").getValue();
		String priceCurrency = (String) ent.getProperty("priceCurrency").getValue();

		String psSQL = "INSERT INTO \"JDBCDemo.db::store.Book\" VALUES (?,?,?,?,?,?)";

		try {
			ps = conn.prepareStatement(psSQL);

			ps.setString(1, "001212");
			ps.setString(2, bookName);
			ps.setString(3, isbn);
			ps.setInt(4, price);
			ps.setString(5, priceCurrency);
			ps.setString(6, authorName);
			
			extCtx.setEntityToBeRead();

		} catch (SQLException e) {
			e.printStackTrace();

			// Check if the SQL Exception was due to duplicate entry, based on
			// the error message thrown by HANA DB for unique constraint
			// violation
			if (e.getLocalizedMessage().contains("unique constraint violated")) {
				throw new ODataApplicationException("Duplicate Resource", 400, Locale.US);
			} else {
				throw new ODataApplicationException("Some error occurred while creating customer", 400, Locale.US);
			}
		} catch (Exception e) {
			e.printStackTrace();
			// Handling other generic exceptions
			throw new ODataApplicationException(
					"Some unknown error occurred while creating Customer.Please contact admin", 500, Locale.US);
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					logr.error("!!Some problem occurred while closing the DB connection");
					e.printStackTrace();
				}
			}		
		}
	}
}
