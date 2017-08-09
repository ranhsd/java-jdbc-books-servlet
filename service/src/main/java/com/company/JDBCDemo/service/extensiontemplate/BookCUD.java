package com.company.JDBCDemo.service.extensiontemplate;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Locale;

import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.server.api.ODataApplicationException;
import org.apache.olingo.server.api.deserializer.DeserializerResult;
import org.apache.olingo.server.api.uri.UriInfo;
import org.apache.olingo.server.api.uri.UriParameter;
import org.apache.olingo.server.api.uri.UriResourceEntitySet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sap.gateway.v4.rt.api.extensions.DataProviderExtensionContext;
import com.sap.gateway.v4.rt.api.extensions.ExtendDataProvider;
import com.sap.gateway.v4.rt.api.extensions.ExtensionContext;
import com.sap.gateway.v4.rt.api.extensions.ExtensionException;
import com.sap.gateway.v4.rt.api.extensions.RequestType;
import com.sap.gateway.v4.rt.cds.api.CDSDSParams;

public class BookCUD {
	
	final static Logger logr = LoggerFactory.getLogger("BookCUD");

	@ExtendDataProvider(entitySet = { "Book" }, requestTypes = RequestType.CREATE)
	public void createBook(ExtensionContext ecx) throws ODataApplicationException, ExtensionException {
		
		// get database connection
		Connection conn = ((CDSDSParams) ecx.getDSParams()).getConnection();
		PreparedStatement ps = null;

		DataProviderExtensionContext extCtx = ecx.asDataProviderContext();

		// deserialize request payload
		DeserializerResult payload = extCtx.getDeserializerResult();
		
		// Extract the entity from the request payload
		Entity ent = payload.getEntity();

		// Get the properties values 
		int price = (Integer) ent.getProperty("price").getValue();
		String authorName = (String) ent.getProperty("authorName").getValue();
		String bookName = (String) ent.getProperty("bookName").getValue();
		String isbn = (String) ent.getProperty("isbn").getValue();
		String priceCurrency = (String) ent.getProperty("priceCurrency").getValue();
		int bookId = (Integer) ent.getProperty("bookId").getValue();
		
		// query SQL staetement 
		String psSQL = "INSERT INTO \"JDBCDemo.db::store.Book\" VALUES (?,?,?,?,?,?)";

		try {
			// prepare SQL statement and map entity values
			ps = conn.prepareStatement(psSQL);
			ps.setInt(1, bookId);
			ps.setString(2, bookName);
			ps.setString(3, isbn);
			ps.setInt(4, price);
			ps.setString(5, priceCurrency);
			ps.setString(6, authorName);
			
			// Database commit - will create a new entry 
			ps.executeUpdate();
			
			extCtx.setEntityToBeRead();			
			
		} catch (SQLException e) {
			e.printStackTrace();

			// Check if the SQL Exception was due to duplicate entry, based on
			// the error message thrown by HANA DB for unique constraint
			// violation
			if (e.getLocalizedMessage().contains("unique constraint violated")) {
				throw new ODataApplicationException("Duplicate Resource", 400, Locale.US);
			} else {
				throw new ODataApplicationException("Some error occurred while creating Book", 400, Locale.US);
			}
		} catch (Exception e) {
			e.printStackTrace();
			// Handling other generic exceptions
			throw new ODataApplicationException(
					"Some unknown error occurred while creating Book.Please contact admin", 500, Locale.US);
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
	
	@ExtendDataProvider(entitySet = { "Book" }, requestTypes = RequestType.DELETE)
	public void deleteCustomer(ExtensionContext ectx) throws ODataApplicationException {
		// Get DB connection
		Connection conn = ((CDSDSParams) ectx.getDSParams()).getConnection();
		int bookId = 0;
		PreparedStatement ps = null;
		DataProviderExtensionContext extCtx = ectx.asDataProviderContext();

		// Get URI info to obtain key predicates. No payload for delete
		UriInfo uri = extCtx.getUriInfo();
		UriResourceEntitySet entSet = (UriResourceEntitySet) uri.getUriResourceParts().get(0);
		List<UriParameter> keys = entSet.getKeyPredicates();
		
		for (UriParameter key : keys) {
			if (key.getName().equals("bookId")) {
				bookId = Integer.parseInt(key.getText());
			}
		}
		// Prepare the SQL statement for prepareStatement
		String sql = "DELETE FROM \"JDBCDemo.db::store.Book\" WHERE \"bookId\"=?";
		try {
			ps = conn.prepareStatement(sql);
			ps.setInt(1, bookId);
			
			int i = ps.executeUpdate();
			//  If i==0, no rows were affected. This means there was no entity.
			//  So throw 404 'Entity Not Found' exception
			if (i == 0) {
				throw new ODataApplicationException("Entity not found!", 404, Locale.US);
			}

		} catch (SQLException e) {
			// Handle other SQL Exceptions
			e.printStackTrace();
			throw new ODataApplicationException("An error occurred while deleting Book.", 400, Locale.US);
		} catch (Exception e) {
			//Handling for other generic exceptions
			e.printStackTrace();
			throw new ODataApplicationException(
					"Some unknown error occurred while deleting Book.Please contact admin.", 500, Locale.US);
		} finally {
			// release all resources
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					logr.error("Some problem occurred while closing the DB connection!");
					e.printStackTrace();
				}
			}
		}

	} // End of method 	
}
