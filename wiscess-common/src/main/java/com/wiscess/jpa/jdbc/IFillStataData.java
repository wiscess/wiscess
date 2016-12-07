package com.wiscess.jpa.jdbc;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;

import org.springframework.jdbc.support.JdbcUtils;

/**
 * 填充数据
 * @author wanghai
 *
 */
public abstract class IFillStataData<T> {

	public int count=0;
	
	public int batchSize=100;
	
	public String sqlKey;
	
	public PreparedStatement insertState=null;
	
	/**
	 * 处理数据
	 * @param insertState
	 * @param row
	 */
	public abstract void handle(T row, int rownum) throws SQLException ;

	public void fillData(T row, int rownum) throws SQLException {
		//校验row是否合法
		if(!checkRow(row))
			return;
		handle(row, rownum);
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
	public boolean checkRow(T row){
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
			insertState.setInt(order , new Integer(obj.toString()));
			break;
		case Types.NUMERIC:
			insertState.setLong(order , new Long(obj.toString()));
			break;
		case Types.VARCHAR:
			insertState.setString(order, obj.toString());
		    break;
		case Types.TIMESTAMP:
			insertState.setDate(order,new java.sql.Date(((Timestamp)obj).getTime()));
			break;
		case Types.DOUBLE:
			insertState.setDouble(order, new Double(obj.toString()));
			break;
		case Types.FLOAT:
			insertState.setFloat(order, new Float(obj.toString()));
			break;
		case Types.SMALLINT:
			insertState.setShort(order, new Short(obj.toString()));
			break;
		case Types.BOOLEAN:
			insertState.setBoolean(order, new Boolean(obj.toString()));
			break;
		default:
			break;
		}
	}
}
