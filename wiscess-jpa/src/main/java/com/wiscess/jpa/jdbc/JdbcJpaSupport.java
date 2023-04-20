package com.wiscess.jpa.jdbc;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.jdbc.core.ArgumentPreparedStatementSetter;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.jdbc.support.KeyHolder;

import com.google.common.cache.Cache;
import com.wiscess.jpa.util.DynamicSqlUtil;
import com.wiscess.jpa.util.GoogleCacheUtils;
import com.wiscess.jpa.util.ISqlElement;
import com.wiscess.query.config.Query;
import com.wiscess.utils.StringUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JdbcJpaSupport {
	@Autowired
	private JdbcTemplate jdbcTemplate;

	private static String databaseProductName="";
	/**
	 * 处理动态sql.
	 * @param params
	 *            参数.
	 * @param name
	 *            动态Sql的名称.
	 * @return
	 * @throws Exception
	 */
	public ISqlElement processSql(Map<String, Object> params, String name){
		log.debug("processSql(Map<String,Object>, String) - start, name="+name);
		//先根据name从sql缓存中查询出freemarker的模板，再进行模板解析
		String sqlTemplate=Query.getQuery(name);
		if(StringUtils.isNotEmpty(sqlTemplate)){
			sqlTemplate="<#setting number_format=\"0\">"+sqlTemplate;
			ISqlElement rs = DynamicSqlUtil.processSql(params, sqlTemplate);
			log.debug("processSql(Map<String,Object>, String) - end");
			return rs;
		}
		return null;
	}

	public JdbcTemplate getJdbcTemplate(){
		return this.jdbcTemplate;
	}
	/**
	 * 查询List方法
	 * @param sqlName
	 * @param params
	 * @param rm
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public <E> List<E> findListByTemplate(JdbcTemplate template,String sqlName, Map<String, Object> params, RowMapper rm)  {
		log.debug("findList(String, Map<String,Object>, RowMapper) - start");
		final ISqlElement se = processSql(params, sqlName);
		if (rm == null) {
			return (List<E>)template.query(se.getSql(),	se.getParams(),se.getArgTypes(),new ColumnMapRowMapper());
		} else {
			return template.query(se.getSql(), se.getParams(), se.getArgTypes(),rm);
		}
	}
	public <E> List<E> findList(String sqlName, Map<String, Object> params) {
		return findListByTemplate(jdbcTemplate, sqlName, params, new ColumnMapRowMapper());
	}
	public <E> List<E> findList(String sqlName, Map<String, Object> params, RowMapper<E> rm) {
		return findListByTemplate(jdbcTemplate, sqlName, params, rm);
	}
	public <E> List<E> findList(String sqlName, RowMapper<E> rm, String[] paramName, Object... paramValue) {
		Map<String, Object> params = new HashMap<String, Object>();
		for (int i = 0; i < paramName.length; i++) {
			params.put(paramName[i], paramValue[i]);
		}
		return findList(sqlName, params, rm);
	}
	public <E> List<E> findList(String sqlName, RowMapper<E> rm, String paramName, Object paramValue) {
		return findList(sqlName, rm, new String[] { paramName }, paramValue);
	}
	public <E> List<E> findList(String sqlName, RowMapper<E> rm) {
		return findList(sqlName, rm, new String[] {}, "");
	}	
	@SuppressWarnings("unchecked")
	public <E> List<E> findList(String sqlName, String paramName, Object paramValue) {
		return (List<E>)findList(sqlName, new ColumnMapRowMapper(), new String[] { paramName }, paramValue);
	}	

	/**
	 * 
	 * @param template
	 * @param sqlName
	 * @param params
	 * @param clazz
	 * @return
	 */
	public <E> List<E> findListByTemplate(JdbcTemplate template,String sqlName, Map<String, Object> params, Class<E> clazz)  {
		return findListByTemplate(template, sqlName, params, getRowMaperByClazz(clazz));
	}
	public <E> List<E> findList(String sqlName, Map<String, Object> params, Class<E> clazz) {
		return findList(sqlName, params,getRowMaperByClazz(clazz));
	}
	public <E> List<E> findList(String sqlName, Class<E> clazz, String[] paramName, Object... paramValue) {
		return findList(sqlName, getRowMaperByClazz(clazz),paramName,paramValue);
	}
	public <E> List<E> findList(String sqlName, Class<E> clazz, String paramName, Object paramValue) {
		return findList(sqlName, getRowMaperByClazz(clazz), paramName, paramValue);
	}
	public <E> List<E> findList(String sqlName, Class<E> clazz) {
		return findList(sqlName, getRowMaperByClazz(clazz));
	}	
	
	private <E> RowMapper<E> getRowMaperByClazz(Class<E> clazz){
		try {
			if(clazz==String.class
					|| clazz == Date.class
					|| ((Class<?>) clazz.getField("TYPE").get(null)).isPrimitive())
				return new SingleColumnRowMapper<E>(clazz);
		} catch (NoSuchFieldException | SecurityException e) {
		} catch (IllegalArgumentException e) {
		} catch (IllegalAccessException e) {
		}
		return new ObjectRowMapper<E>(clazz);
	}
	
	public Integer insert(String sqlName){
		return insert(sqlName,new HashMap<String, Object>());
	}
	public Integer insert(String sqlName,Map<String, Object> params){
		return insertByTemplate(this.jdbcTemplate, sqlName, params);
	}
	public Integer insertByTemplate(JdbcTemplate template,String sqlName,Map<String, Object> params){
		final ISqlElement se=processSql(params, sqlName);
		
		KeyHolder kg = new GeneratedKeyHolder();
		final PreparedStatementSetter pss=new ArgumentPreparedStatementSetter(se.getParams());
		template.update(
				new PreparedStatementCreator() {
					@Override
					public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
						PreparedStatement ps = con.prepareStatement(se.getSql(), Statement.RETURN_GENERATED_KEYS);
						if (pss != null) {
							pss.setValues(ps);
						}
						return ps;
					}
				},
				kg);
		
		return kg.getKey().intValue();
	}
	/**
	 * 更新语句
	 * @param sqlName
	 * @return
	 * @throws Exception
	 */
	public Integer update(String sqlName){
		return update(sqlName,new HashMap<String, Object>());
	}
	public Integer update(String sqlName,Map<String, Object> params){
		return updateByTemplate(this.jdbcTemplate, sqlName, params);
	}
	public Integer updateByTemplate(JdbcTemplate template,String sqlName,Map<String, Object> params){
		ISqlElement se=processSql(params, sqlName);
		return template.update(se.getSql(), se.getParams());
	}
	/**
	 * 批量更新语句
	 * @param sqlName
	 * @return
	 * @throws Exception
	 */
	public Integer[] updateBatch(String sqlName){
		return updateBatch(sqlName,new HashMap<String, Object>());
	}
	public Integer[] updateBatch(String sqlName,Map<String, Object> params){
		return updateBatchByTemplate(this.jdbcTemplate, sqlName, params);
	}
	public Integer[] updateBatchByTemplate(JdbcTemplate template,String sqlName,Map<String, Object> params){
		//先根据sqlName从配置中读取到所有的sql，再根据分号进行分隔，生成多个sql语句的template
		String sqlTemplate=Query.getQuery(sqlName);
		if(StringUtils.isNotEmpty(sqlTemplate)){
			String[] templates = sqlTemplate.split(";");
			//对每个template进行update
			List<Integer> result=new ArrayList<>();
			for(String temp:templates) {
				if(StringUtils.isNotEmpty(temp)) {
					temp="<#setting number_format=\"0\">"+temp;
					ISqlElement rs = DynamicSqlUtil.processSql(params, temp);
					Integer r=template.update(rs.getSql(), rs.getParams());
					result.add(r);
				}
			}
			return result.toArray(new Integer[]{});
		}
		return null;
	}
	
	/**
	 * 查询单数据类型结果
	 * @param sqlName
	 * @param params
	 * @param requiredType
	 * @return
	 * @throws Exception
	 */
	public <E> E queryForObject(String sqlName,Map<String, Object> params,Class<E> requiredType){
		return queryForObjectByTemplate(this.jdbcTemplate, sqlName, params, requiredType);
	}
	public <E> E queryForObjectByTemplate(JdbcTemplate template,String sqlName,Map<String, Object> params,Class<E> requiredType) {
		return queryForObjectByTemplate(template, sqlName, params, getRowMaperByClazz(requiredType));
	}
	public <E> E queryForObject(String sqlName,Map<String, Object> params,RowMapper<E> rowMapper) {
		return queryForObjectByTemplate(this.jdbcTemplate, sqlName, params, rowMapper);
	}
	public <E> E queryForObjectByTemplate(JdbcTemplate template,String sqlName,Map<String, Object> params,RowMapper<E> rowMapper) {
		ISqlElement se=processSql(params, sqlName);
		return template.queryForObject(se.getSql(),se.getParams(),se.getArgTypes(), rowMapper);
	}
	/**
	 * 新分页查询方法
	 * @throws Exception 
	 */
	public Page<Map<String, Object>> findPage(String querySqlName, 
			Map<String, Object> params,Pageable pageable){
		return findPage(querySqlName, null, params, pageable);
	}
	public <E> Page<E> findPage(String querySqlName, 
			Map<String, Object> params,Pageable pageable,Class<E> clazz){
		return findPage(querySqlName, null, params, pageable,clazz);
	}
	public <E> Page<E> findPage(String querySqlName, 
			Map<String, Object> params,Pageable pageable,RowMapper<E> rm){
		return findPage(querySqlName, null, params, pageable,rm);
	}
	
	public Page<Map<String, Object>> findPage(String querySqlName, String countSqlName,
			Map<String, Object> params,Pageable pageable){
		return findPage(querySqlName, countSqlName, params, pageable,new ColumnMapRowMapper());
	}
	public <E> Page<E> findPage(String querySqlName, String countSqlName,
			Map<String, Object> params,Pageable pageable,Class<E> clazz){
		return findPage(querySqlName, countSqlName, params, pageable, getRowMaperByClazz(clazz));
	}
	
	public <E> Page<E> findPage(String querySqlName, String countSqlName,
			Map<String, Object> params,Pageable pageable,RowMapper<E> rm){
		return findPageByTemplate(this.jdbcTemplate, querySqlName, countSqlName, params, pageable, rm);
	}
	
	@SuppressWarnings("unchecked")
	public <E> Page<E> findPageByTemplate(JdbcTemplate template, String querySqlName, String countSqlName,
			Map<String, Object> params,Pageable pageable,RowMapper<E> rm){
		if(rm==null){
			rm=(RowMapper<E>) new ColumnMapRowMapper();
		}
		if(params==null){
			params=new HashMap<String, Object>();
		}
		if (null == pageable) {
			//没有分页，等同于findList
			return new PageImpl<E>((List<E>) findListByTemplate(template, querySqlName,params,rm));
		}
		//获取总数
		ISqlElement seCount = null;
		if(countSqlName==null){
			params.put("count", "true");
			seCount = processSql(params, querySqlName);
			params.remove("count");
		}else{
			seCount = processSql(params, countSqlName);
		}
		//处理排序
		if(pageable.getSort()!=null && pageable.getSort()!=Sort.unsorted()){
			Order order=pageable.getSort().iterator().next();
			params.put("orderBy", order.getProperty()+" "+order.getDirection().toString().toLowerCase());
		}
		ISqlElement seQuery = processSql(params, querySqlName);

		final int total = template.queryForObject(seCount.getSql(), seCount.getParams(),seCount.getArgTypes(),Integer.class);
		//创建空集合
		List<E> content = Collections.<E> emptyList();
		while(total<=pageable.getOffset() && pageable.getOffset()>0){
			pageable=pageable.previousOrFirst();
		}
		if(total>pageable.getOffset()){
			//如果未超过总记录，则读取数据，返回List
			Connection con = null;// 本地库连接
			try {
				if(databaseProductName.equals("")) {
					con=template.getDataSource().getConnection();
					DatabaseMetaData md = con.getMetaData();
					databaseProductName=md.getDatabaseProductName();
					JdbcUtils.closeConnection(con);
				}
				if(databaseProductName.equalsIgnoreCase("mysql")) {
					//如果是mysql，换用limit的方式读取内容
					String sql=seQuery.getSql();
					//添加limit
					sql+=" limit "+pageable.getOffset() +","+pageable.getPageSize();
					content=template.query(sql, seQuery.getParams(),seQuery.getArgTypes(), rm);
				}else {
					//用原方式读取
					content = template.query(
							seQuery.getSql(),seQuery.getParams(),seQuery.getArgTypes(),
							new PageResultSetExtractor<E>(rm,pageable));
				}
			} catch (SQLException e) {
				//按原方式查询
				content = template.query(
						seQuery.getSql(),seQuery.getParams(),seQuery.getArgTypes(),
						new PageResultSetExtractor<E>(rm,pageable));
			}

		}

		return new PageImpl<E>(content, pageable, total);
	}

	/**
	 * 记录导入的进度，1分钟销毁
	 */
	private static Cache<String, String> progressCache = GoogleCacheUtils.createManualCache(1L);
	
	/**
	 * 公用的导入方法
	 * @param titleRow
	 * @param rowList
	 * @return
	 */
	public void importBaseData(String importStatus,final String[] titleRow, 
			List<String[]> rowList,List<String> preExcuteSqlList,IFillStataData<String[]> fill){
		this.importBaseData(importStatus, titleRow, null, rowList, preExcuteSqlList, fill);
	}
	@SuppressWarnings("unchecked")
	public List<String>  importBaseData(String importStatus,final String[] titleRow,	final Map<String,Integer> fieldMap,	
			List<String[]> rowList,List<String> preExcuteSqlList,IFillStataData<String[]> fill){
		return this.importBaseDataByTemplate(jdbcTemplate, importStatus, titleRow, fieldMap, rowList, preExcuteSqlList, new IFillStataData[]{fill});
	}
	public List<String>  importBaseData(String importStatus,final String[] titleRow,	final Map<String,Integer> fieldMap,	
			List<String[]> rowList,List<String> preExcuteSqlList,IFillStataData<String[]>[] fillDatas){
		return this.importBaseDataByTemplate(jdbcTemplate, importStatus, titleRow, fieldMap, rowList, preExcuteSqlList, fillDatas);
	}
	protected List<String> importBaseDataByTemplate(JdbcTemplate template,String importStatus,final String[] titleRow,	Map<String,Integer> fieldMap,	
			List<String[]> rowList,List<String> preExcuteSqlList,IFillStataData<String[]>[] fillDatas){
		//错误列表
		List<String> errorList=new ArrayList<String>();
		//当前行
		int rownum=0;
		String[] rows=rowList.get(rownum);
		progressCache.put(importStatus, "正在校验字段名称");
		if(fieldMap==null){
			fieldMap=fillFieldMap(fieldMap, rows);
		}
		//按新方法校验字段是否存在
		String missCol="";
		for(int i=0;i<titleRow.length;i++){
			if(!fieldMap.containsKey(titleRow[i])){
				missCol+=","+titleRow[i];
				continue;
			}
		}
		if(StringUtils.isNotEmpty(missCol)) {
			errorList.add("缺少列："+missCol.substring(1)+";");
		}
		if(errorList.size()>0){
			progressCache.put(importStatus, "字段名称不正确。");
			return errorList;
		}
		//总记录数
		int totalCount=rowList.size()-1;
		//校验每行数据是否合法
		progressCache.put(importStatus, "正在校验数据，共有数据"+totalCount+"条。");
		int dataCount=0;
		for(String[] row: rowList) {
			rownum++;
			if(fieldMap!=null){
				//新方法校验
				if(rownum==1 || row==null || ( StringUtils.isNotEmpty(titleRow[0]) && StringUtils.isEmpty(row[fieldMap.get(titleRow[0])]))) {
					continue;
				}
			}
			boolean flag=true;
			for(IFillStataData<String[]> fill:fillDatas){
				//校验row是否合法
				if(!fill.checkRow(row))
					continue;
				//校验row的数据内容
				fill.checkRowData(rownum,row,errorList);
				if(flag){
					flag=false;
					dataCount++;
				}
			}
		}
		progressCache.put(importStatus, "正在校验数据，共有数据"+totalCount+"条，有效数据"+dataCount+"条。");

		if(errorList.size()==0){
			rownum=0;
			//批量处理数据
			Connection con = null;// 本地库连接
			PreparedStatement deleteState = null;
			try {
				con = template.getDataSource().getConnection();// 获取数据库连接
				con.setAutoCommit(false);// 手动提交
				//预处理的sql
				if(preExcuteSqlList!=null){
					for(String sql:preExcuteSqlList){
						deleteState=con.prepareStatement(sql);
						deleteState.execute();
					}
					JdbcUtils.closeStatement(deleteState);
				}
			
				//输入项目
				for(IFillStataData<String[]> fill:fillDatas){
					//读取sql的模板
					fill.insertState=con.prepareStatement(processSql(new HashMap<String, Object>(), fill.sqlKey).getSql());
				}
				//已处理记录
				int dealCount=0;
				progressCache.put(importStatus, "开始导入数据，共有数据"+totalCount+"条，有效数据"+dataCount+"条。");
				Thread.sleep(1000);
				//开始处理数据
				for(String[] row: rowList) {
					rownum++;
					//新方法校验
					if(rownum==1 || row==null || ( StringUtils.isNotEmpty(titleRow[0]) && StringUtils.isEmpty(row[fieldMap.get(titleRow[0])]))) {
						continue;
					}

					for(IFillStataData<String[]> fill:fillDatas){
						//处理数据
						fill.fillData(row,rownum);
					}
					dealCount++;
					if(dealCount>=totalCount)
						dealCount=totalCount;
					progressCache.put(importStatus, "已处理"+Math.round(dealCount*100.0/totalCount)+"%，"+dealCount+"/"+totalCount+"条。");
					Thread.sleep(5);
				}
				//处理剩余的数据
				for(IFillStataData<String[]> fill:fillDatas){
					fill.closeStat();
				}
	
				progressCache.put(importStatus, "已处理"+Math.round(dealCount*100.0/totalCount)+"%，"+dealCount+"/"+totalCount+"条。");
				con.commit();
				progressCache.put(importStatus, "导入成功");
			} catch (Exception e) {
				if (con != null) {
					try {
						con.rollback();
					} catch (SQLException e1) {
					}
				}
				e.printStackTrace();
				progressCache.put(importStatus, "导入失败，请联系管理员");
			} finally {
				JdbcUtils.closeConnection(con);
			}
		}else{
			progressCache.put(importStatus, "数据校验失败，请检查后重新导入。");
			return errorList;
		}
		return null;
	}
	public Map<String,Integer> fillFieldMap(Map<String,Integer> fieldMap,String[] row){
		if(fieldMap==null){
			fieldMap=new HashMap<String, Integer>();
		}
		for(int i=0;i<row.length;i++){
			//存储标题行对应的列数
			row[i]=row[i].replaceAll("\n", "");
			fieldMap.put(row[i], i);
		}
		return fieldMap;
	}
	
	public String getProressMessage(String importStatus) {
		return progressCache.getIfPresent(importStatus);
	}

	/**
	 * 批量执行sql
	 * @param sqlList
	 */
	public void batchSql(List<String> sqlList){
		batchSqlByTemplate(this.jdbcTemplate, sqlList);
	}
	public void batchSqlByTemplate(JdbcTemplate template,List<String> sqlList){
		if(null != sqlList && sqlList.size()>0){
			String[] sqls=new String[sqlList.size()];
			sqls=sqlList.toArray(sqls);	
			template.batchUpdate(sqls);
		}
	}
	/**
	 * 批量执行数据操作
	 * @param list
	 * @throws Exception 
	 */
	public void batchSqlByList(String importStatus,List<Map<String, Object>> list,List<String> preExcuteSqlList,IFillStataData<Map<String, Object>>[] fillDatas) throws Exception{
		this.batchSqlByListByTemplate(jdbcTemplate,importStatus, list,preExcuteSqlList,fillDatas);
	}
	public void batchSqlByListByTemplate(JdbcTemplate template, String importStatus,List<Map<String, Object>> list,
			List<String> preExcuteSqlList,IFillStataData<Map<String, Object>>[] fillDatas) throws Exception{
		//批量处理数据
		Connection con = null;// 本地库连接
		PreparedStatement deleteState = null;
		try {
			con = template.getDataSource().getConnection();// 获取数据库连接
			con.setAutoCommit(false);// 手动提交
			//预处理的sql
			if(preExcuteSqlList!=null){
				for(String sql:preExcuteSqlList){
					deleteState=con.prepareStatement(processSql(new HashMap<String, Object>(), sql).getSql());
					deleteState.execute();
				}
				JdbcUtils.closeStatement(deleteState);
			}
			//读取sql的模板
			for(IFillStataData<Map<String, Object>> fill:fillDatas){
				//读取sql的模板
				fill.insertState=con.prepareStatement(processSql(new HashMap<String, Object>(), fill.sqlKey).getSql());
			}
			//总记录数
			int totalCount=list.size();
			//已处理记录
			int dealCount=0;
			progressCache.put(importStatus, "开始导入数据，共有数据"+totalCount+"条。");
			Thread.sleep(100);
			//开始处理数据
			int rownum=0;
			for(Map<String, Object> map:list){
				//处理数据
				for(IFillStataData<Map<String, Object>> fill:fillDatas){
					fill.fillData(map, rownum);
				}
				dealCount++;
				progressCache.put(importStatus, "已处理"+Math.round(dealCount*100.0/totalCount)+"%，"+dealCount+"/"+totalCount+"条。");
			}
			for(IFillStataData<Map<String, Object>> fill:fillDatas){
				fill.closeStat();
			}
			progressCache.put(importStatus, "已处理"+Math.round(dealCount*100.0/totalCount)+"%，"+dealCount+"/"+totalCount+"条。");
			con.commit();
		} catch (Exception e) {
			if (con != null) {
				try {
					con.rollback();
				} catch (SQLException e1) {
				}
			}
			throw e;
		} finally {
			JdbcUtils.closeConnection(con);
		}
	}
	private static SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	protected void setValue(PreparedStatement ps,int order , Object obj ,int type) throws SQLException
	{
		if(obj == null)
		{
			ps.setNull(order, type);
			return;
		}
		switch (type) {
			case Types.INTEGER:
				ps.setInt(order , Integer.parseInt(obj.toString()));
				break;
			case Types.NUMERIC:
				ps.setLong(order , Long.parseLong(obj.toString()));
				break;
			case Types.VARCHAR:
				ps.setString(order, obj.toString());
				break;
			case Types.DATE:
				try {
					ps.setDate(order,new java.sql.Date(sdf.parse(obj.toString()).getTime()));
				}catch (Exception e) {
					ps.setNull(order, type);
				}
				break;
			case Types.TIMESTAMP:
				try {
					ps.setTimestamp(order,new java.sql.Timestamp(sdf.parse(obj.toString()).getTime()));
				}catch (Exception e) {
					ps.setNull(order, type);
				}
				break;
			case Types.DOUBLE:
				ps.setDouble(order, Double.parseDouble(obj.toString()));
				break;
			case Types.FLOAT:
				ps.setFloat(order, Float.parseFloat(obj.toString()));
				break;
			case Types.SMALLINT:
				ps.setShort(order, Short.parseShort(obj.toString()));
				break;
			case Types.BOOLEAN:
				ps.setBoolean(order, Boolean.parseBoolean(obj.toString()));
				break;
			case Types.DECIMAL:
				ps.setBigDecimal(order, new BigDecimal(obj.toString()));
				break;
			default:
				break;
		}
	}

}
