package com.wiscess.jpa.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.util.Assert;

public class PageResultSetExtractor<T> implements ResultSetExtractor<List<T>> {
	private final RowMapper<T> rowMapper;
	private final Pageable pageable;
	
	public PageResultSetExtractor(RowMapper<T> rm, Pageable pageable) {
		Assert.notNull(rm, "RowMapper is required");
		this.rowMapper=rm;
		this.pageable=pageable;
	}

	@Override
	public List<T> extractData(ResultSet rs) throws SQLException, DataAccessException {
		
		rs.setFetchSize(pageable.getPageSize());
		
		if(pageable.getOffset()>0){
			rs.absolute(pageable.getOffset());
		}
		List<T> results = (this.pageable.getPageSize() > 0 ? new ArrayList<T>(this.pageable.getPageSize()) : new ArrayList<T>());
		int rowNum = 0;
		while (rs.next() && rowNum < this.pageable.getPageSize()) {
			results.add(this.rowMapper.mapRow(rs, rowNum++));
		}
		return results;
	}

}
