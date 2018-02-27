package com.wiscess.jpa.jdbc;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.List;

import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.util.StringUtils;

/**
 * 用于向批量插入数据的state中填充数据
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
	/**
	 * 校验row的内容是否满足条件
	 * @param rownum
	 * @param row
	 */
	public void checkRowData(Integer rownum,T row,List<String> errorList){
	}
	
	public void setValue(int order , Object obj ,int type) throws SQLException
	{
		if(obj == null || StringUtils.isEmpty(obj))
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
			insertState.setTimestamp(order,((Timestamp)obj));
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
		case Types.DECIMAL:
			insertState.setBigDecimal(order, new BigDecimal(obj.toString()));
			break;
		default:
			break;
		}
	}
}
