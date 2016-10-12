package com.wiscess.jpa.jdbc;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

import org.springframework.jdbc.support.JdbcUtils;

/**
 * 填充数据
 * @author wanghai
 *
 */
public abstract class IFillStataData {

	public int count=0;
	
	public int batchSize=100;
	
	public String sqlKey;
	
	public PreparedStatement insertState=null;
	
	/**
	 * 处理数据
	 * @param insertState
	 * @param row
	 */
	public abstract void handle(PreparedStatement insertState, String[] row, int rownum) throws SQLException ;

	public void fillData( String[] row, int rownum) throws SQLException {
		//校验row是否合法
		if(!checkRow(row))
			return;
		handle(insertState, row, rownum);
		insertState.addBatch();
		count++;
		if(count%batchSize==0){
			//每100行更新一次
			insertState.executeBatch();
			insertState.clearBatch();
			count=0;
		}
	}
	
	public void closeStat(){
		try {
			if(count>0){
				insertState.executeBatch();
				insertState.clearBatch();
			}
			JdbcUtils.closeStatement(insertState);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 校验row是否合法
	 * @param row
	 * @return
	 */
	public boolean checkRow(String[] row){
		return true;
	}
	
	public void setValue(int order , Object obj ,int type) throws SQLException
	{
		if(obj == null)
		{
			insertState.setNull(order, type);
			return;
		}
		switch (type) {
		case Types.INTEGER:
			insertState.setInt(order , (Integer)obj);
			break;
		case Types.NUMERIC:
			insertState.setLong(order , (Long)obj);
			break;
		case Types.VARCHAR:
			insertState.setString(order, (String)obj);
		    break;
		case Types.TIMESTAMP:
			insertState.setDate(order,new java.sql.Date(((Date)obj).getTime()));
			break;
		case Types.DOUBLE:
			insertState.setDouble(order, (Double)obj);
			break;
		case Types.FLOAT:
			insertState.setFloat(order, (Float)obj);
			break;
		case Types.SMALLINT:
			insertState.setShort(order, (Short)obj);
			break;
		default:
			break;
		}
	}
}
