package com.wiscess.jpa.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

public class JdbcPage<E> extends AbstractPage<E> {
	/**
	 * JDBC page.<br>
	 * 
	 * Use scoll to count total results.
	 * 
	 * @param rs Jdbc ResultSet.
	 * @param pageNumber current page number.
	 * @param pageSize page size.
	 * @param rh Row data handler
	 */
	public JdbcPage(ResultSet rs, int pageNumber, int pageSize, RowMapper<E> rm) {
		super(pageNumber, pageSize);
		try {
			rs.last();
			totalCount = rs.getRow();
			getElementsFromResultSet(rs, rm);
		} catch (SQLException e) {
			throw new RuntimeException("JDBC page exception.", e);
		}
	}

	/**
	 * JDBC page.<br>
	 * 
	 * Use scoll to find specified page data.
	 * 
	 * @param rs Jdbc ResultSet.
	 * @param totalCount total 
	 * @param pageNumber current page number.
	 * @param pageSize page size.
	 * @param rh Row data handler.
	 */
	public JdbcPage(ResultSet rs, int totalCount, int pageNumber, int pageSize, RowMapper<E> rm) {
		super(pageNumber, pageSize);
		try {
			this.totalCount = totalCount;
			getElementsFromResultSet(rs, rm);
		} catch (SQLException e) {
			throw new RuntimeException("JDBC page exception.", e);
		}
	}
	
	protected void getElementsFromResultSet(ResultSet rs, RowMapper<E> rm) throws SQLException {
		computePage();
		int offset = (currPageNumber - firstPageNumber) * pageSize;
		if (offset > 0) {
			rs.absolute(offset);
		} else {
			offset = 0;
		}
		int count = 0;
		while (rs.next() && count < pageSize) {
			pageElements.add(rm.mapRow(rs, count));
			count++;
		}
	}
}
