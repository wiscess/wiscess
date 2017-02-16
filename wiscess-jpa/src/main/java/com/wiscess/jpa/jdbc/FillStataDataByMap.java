package com.wiscess.jpa.jdbc;

import java.sql.SQLException;
import java.sql.Types;
import java.util.Map;

/**
 * 从Map对象中获取数据，并添加到stata中
 * 需要定义字段名称和字段类型
 * @author wh
 *
 */
public class FillStataDataByMap extends IFillStataData<Map<String, Object>> {
	
	public String[] fields=null;
	public String[] types=null;
	
	@Override
	public void handle(Map<String, Object> map, int rownum) throws SQLException {
		//用数组记录字段名和类型
		int col=0;
		for(String field:fields){
			if(types[col].equals("N")){
				setValue(col+1, map.get(field), Types.NUMERIC);
			}else if(types[col].equals("V")){
				setValue(col+1, map.get(field), Types.VARCHAR);
			}else if(types[col].equals("I")){
				setValue(col+1, map.get(field), Types.INTEGER);
			}else if(types[col].equals("F")){
				setValue(col+1, map.get(field), Types.FLOAT);
			}else if(types[col].equals("B")){
				setValue(col+1, map.get(field), Types.BOOLEAN);
			}else if(types[col].equals("T")){
				setValue(col+1, map.get(field), Types.TIMESTAMP);
			}else if(types[col].equals("D")){
				setValue(col+1, map.get(field), Types.DOUBLE);
			}else if(types[col].equals("S")){
				setValue(col+1, map.get(field), Types.SMALLINT);
			}
			col++;
		}
	}
}
