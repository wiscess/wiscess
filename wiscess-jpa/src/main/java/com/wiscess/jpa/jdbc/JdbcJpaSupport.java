package com.wiscess.jpa.jdbc;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Order;
import org.springframework.jdbc.core.ArgumentPreparedStatementSetter;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.jdbc.support.KeyHolder;

import com.wiscess.common.utils.StringUtil;
import com.wiscess.jpa.util.DynamicSqlUtil;
import com.wiscess.jpa.util.ISqlElement;
import com.wiscess.query.config.Query;

@Slf4j
public class JdbcJpaSupport {
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@SuppressWarnings("unused")
	@Autowired
	private Query query;
	/**
	 * 处理动态sql.
	 * @param params
	 *            参数.
	 * @param name
	 *            动态Sql的名称.
	 * @return
	 * @throws Exception
	 */
	public ISqlElement processSql(Map<String, Object> params, String name)
			throws Exception{
		log.debug("processSql(Map<String,Object>, String) - start, name="+name);
		//先根据name从sql缓存中查询出freemarker的模板，再进行模板解析
		ISqlElement rs = DynamicSqlUtil.processSql(params, Query.getQuery(name));
		log.debug("processSql(Map<String,Object>, String) - end");
		return rs;
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
	@SuppressWarnings("unchecked")
	public <E> List<E> findListByTemplate(JdbcTemplate template,String sqlName, Map<String, Object> params, RowMapper<E> rm) throws Exception {
		log.debug("findList(String, Map<String,Object>, RowMapper) - start");
		final ISqlElement se = processSql(params, sqlName);
		if (rm == null) {
			return (List<E>)template.queryForList(se.getSql(),	se.getParams());
		} else {
			return template.query(se.getSql(), se.getParams(), rm);
		}
	}
	public <E> List<E> findList(String sqlName, Map<String, Object> params, RowMapper<E> rm) throws Exception {
		return findListByTemplate(jdbcTemplate, sqlName, params, rm);
	}
	public <E> List<E> findList(String sqlName, RowMapper<E> rm, String[] paramName, Object... paramValue) throws Exception {
		Map<String, Object> params = new HashMap<String, Object>();
		for (int i = 0; i < paramName.length; i++) {
			params.put(paramName[i], paramValue[i]);
		}
		return findList(sqlName, params, rm);
	}
	public <E> List<E> findList(String sqlName, RowMapper<E> rm, String paramName, Object paramValue) throws Exception {
		return findList(sqlName, rm, new String[] { paramName }, paramValue);
	}
	public <E> List<E> findList(String sqlName, RowMapper<E> rm) throws Exception {
		return findList(sqlName, rm, new String[] {}, "");
	}	
	public <E> List<E> findList(String sqlName, String paramName, Object paramValue) throws Exception {
		return findList(sqlName, null, new String[] { paramName }, paramValue);
	}	
	
	public Integer insert(String sqlName) throws Exception{
		return insert(sqlName,new HashMap<String, Object>());
	}
	public Integer insert(String sqlName,Map<String, Object> params)throws Exception {
		return insertByTemplate(this.jdbcTemplate, sqlName, params);
	}
	public Integer insertByTemplate(JdbcTemplate template,String sqlName,Map<String, Object> params)throws Exception {
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
	public Integer update(String sqlName) throws Exception{
		return update(sqlName,new HashMap<String, Object>());
	}
	public Integer update(String sqlName,Map<String, Object> params)throws Exception {
		return updateByTemplate(this.jdbcTemplate, sqlName, params);
	}
	public Integer updateByTemplate(JdbcTemplate template,String sqlName,Map<String, Object> params)throws Exception {
		ISqlElement se=processSql(params, sqlName);
		return template.update(se.getSql(), se.getParams());
	}
	
	/**
	 * 查询单数据类型结果
	 * @param sqlName
	 * @param params
	 * @param requiredType
	 * @return
	 * @throws Exception
	 */
	public <E> Object queryForObject(String sqlName,Map<String, Object> params,Class<E> requiredType)throws Exception {
		return queryForObjectByTemplate(this.jdbcTemplate, sqlName, params, requiredType);
	}
	public <E> Object queryForObjectByTemplate(JdbcTemplate template,String sqlName,Map<String, Object> params,Class<E> requiredType)throws Exception {
		ISqlElement se=processSql(params, sqlName);
		return template.queryForObject(se.getSql(),se.getParams(), requiredType);
	}
	public <E> Object queryForObject(String sqlName,Map<String, Object> params,RowMapper<E> rowMapper)throws Exception {
		return queryForObjectByTemplate(this.jdbcTemplate, sqlName, params, rowMapper);
	}
	public <E> Object queryForObjectByTemplate(JdbcTemplate template,String sqlName,Map<String, Object> params,RowMapper<E> rowMapper)throws Exception {
		ISqlElement se=processSql(params, sqlName);
		return template.queryForObject(se.getSql(),se.getParams(), rowMapper);
	}
	/**
	 * 新分页查询方法
	 * @throws Exception 
	 */
	public Page<Map<String, Object>> findPage(String querySqlName, Map<String, Object> params,Pageable pageable) throws Exception{
		return findPage(querySqlName, null, params, pageable,new ColumnMapRowMapper());
	}
	public Page<Map<String, Object>> findPage(String querySqlName, String countSqlName,Map<String, Object> params,Pageable pageable) throws Exception{
		return findPage(querySqlName, countSqlName, params, pageable,new ColumnMapRowMapper());
	}
	public <E> Page<E> findPage(String querySqlName, 
			Map<String, Object> params,Pageable pageable,RowMapper<E> rm) throws Exception{
		return findPage(querySqlName, null, params, pageable,rm);
	}
	public <E> Page<E> findPage(String querySqlName, String countSqlName,
			Map<String, Object> params,Pageable pageable,RowMapper<E> rm) throws Exception{
		return findPageByTemplate(this.jdbcTemplate, querySqlName, countSqlName, params, pageable, rm);
	}
	@SuppressWarnings("unchecked")
	public <E> Page<E> findPageByTemplate(JdbcTemplate template, String querySqlName, String countSqlName,
			Map<String, Object> params,Pageable pageable,RowMapper<E> rm) throws Exception{
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
		if(pageable.getSort()!=null){
			Order order=pageable.getSort().iterator().next();
			params.put("orderBy", order.getProperty()+" "+order.getDirection().toString().toLowerCase());
		}
		ISqlElement seQuery = processSql(params, querySqlName);

		final int total = template.queryForObject(seCount.getSql(), seCount.getParams(),Integer.class);
		//创建空集合
		List<E> content = Collections.<E> emptyList();
		if(total<=pageable.getOffset()){
			pageable=pageable.previousOrFirst();
		}
		if(total>pageable.getOffset()){
			//如果未超过总记录，则读取数据，返回List
			content = template.query(
					seQuery.getSql(),seQuery.getParams(),
					new PageResultSetExtractor<E>(rm,pageable));
		}

		return new PageImpl<E>(content, pageable, total);
	}

	/**
	 * 公用的导入方法
	 * @param titleRow
	 * @param rowList
	 * @return
	 */
	public void importBaseData(HttpSession session,String importStatus,final String[] titleRow, 
			List<String[]> rowList,List<String> preExcuteSqlList,IFillStataData<String[]> fill){
		this.importBaseData(session, importStatus, titleRow, null, rowList, preExcuteSqlList, fill);
	}
	@SuppressWarnings("unchecked")
	public List<String>  importBaseData(HttpSession session,String importStatus,final String[] titleRow,	final Map<String,Integer> fieldMap,	
			List<String[]> rowList,List<String> preExcuteSqlList,IFillStataData<String[]> fill){
		return this.importBaseDataByTemplate(jdbcTemplate, session, importStatus, titleRow, fieldMap, rowList, preExcuteSqlList, new IFillStataData[]{fill});
	}
	public List<String>  importBaseData(HttpSession session,String importStatus,final String[] titleRow,	final Map<String,Integer> fieldMap,	
			List<String[]> rowList,List<String> preExcuteSqlList,IFillStataData<String[]>[] fillDatas){
		return this.importBaseDataByTemplate(jdbcTemplate, session, importStatus, titleRow, fieldMap, rowList, preExcuteSqlList, fillDatas);
	}
	protected List<String> importBaseDataByTemplate(JdbcTemplate template,HttpSession session,String importStatus,final String[] titleRow,	Map<String,Integer> fieldMap,	
			List<String[]> rowList,List<String> preExcuteSqlList,IFillStataData<String[]>[] fillDatas){
		//错误列表
		List<String> errorList=new ArrayList<String>();
		//当前行
		int rownum=0;
		String[] rows=rowList.get(rownum);
		session.setAttribute(importStatus, "正在校验字段名称");
		if(fieldMap==null){
			fieldMap=fillFieldMap(fieldMap, rows);
		}
		//按新方法校验字段是否存在
		for(int i=0;i<titleRow.length;i++){
			if(!fieldMap.containsKey(titleRow[i])){
				errorList.add("缺少列："+titleRow[i]+";");
				continue;
			}
		}
		if(errorList.size()>0){
			checkResult(session, importStatus, "字段名称不正确。");
			return errorList;
		}
		//总记录数
		int totalCount=rowList.size()-1;
		//校验每行数据是否合法
		session.setAttribute(importStatus, "正在校验数据，共有数据"+totalCount+"条。");
		for(String[] row: rowList) {
			rownum++;
			if(fieldMap!=null){
				//新方法校验
				if(rownum==1 || row==null || ( StringUtil.isNotEmpty(titleRow[0]) && StringUtil.isEmpty(row[fieldMap.get(titleRow[0])]))) {
					continue;
				}
			}
			for(IFillStataData<String[]> fill:fillDatas){
				//校验row是否合法
				if(!fill.checkRow(row))
					continue;
				//校验row的数据内容
				fill.checkRowData(rownum,row,errorList);
			}
		}
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
				session.setAttribute(importStatus, "开始导入数据，共有数据"+totalCount+"条。");
				Thread.sleep(500);
				//开始处理数据
				for(String[] row: rowList) {
					rownum++;
					for(IFillStataData<String[]> fill:fillDatas){
						//处理数据
						fill.fillData(row,rownum);
					}
					dealCount++;
					session.setAttribute(importStatus, "已处理"+Math.round(dealCount*100.0/totalCount)+"%，"+dealCount+"/"+totalCount+"条。");
					Thread.sleep(5);
				}
				//处理剩余的数据
				for(IFillStataData<String[]> fill:fillDatas){
					fill.closeStat();
				}
	
				session.setAttribute(importStatus, "已处理"+Math.round(dealCount*100.0/totalCount)+"%，"+dealCount+"/"+totalCount+"条。");
				con.commit();
				session.setAttribute(importStatus, "导入成功");
				Thread.sleep(1000);
				session.removeAttribute(importStatus);
			} catch (Exception e) {
				if (con != null) {
					try {
						con.rollback();
					} catch (SQLException e1) {
					}
				}
				e.printStackTrace();
				checkResult(session,importStatus, "导入失败，请联系管理员");
			} finally {
				JdbcUtils.closeConnection(con);
			}
		}else{
			checkResult(session, importStatus, "数据校验失败，请检查后重新导入。");
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
	private void checkResult(HttpSession session,String importStatus,Object result){
		if(result!=null){
			log.debug("get:"+session.getId()+":"+(String)session.getAttribute(importStatus));
			session.setAttribute(importStatus, result.toString());
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		session.removeAttribute(importStatus);
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
	 * @param sqlKey
	 * @param list
	 * @throws Exception 
	 */
	public void batchSqlByList(HttpSession session,String importStatus,List<Map<String, Object>> list,List<String> preExcuteSqlList,IFillStataData<Map<String, Object>>[] fillDatas) throws Exception{
		this.batchSqlByListByTemplate(jdbcTemplate,session,importStatus, list,preExcuteSqlList,fillDatas);
	}
	public void batchSqlByListByTemplate(JdbcTemplate template,HttpSession session,String importStatus,List<Map<String, Object>> list,
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
			session.setAttribute(importStatus, "开始导入数据，共有数据"+totalCount+"条。");
			Thread.sleep(100);
			//开始处理数据
			int rownum=0;
			for(Map<String, Object> map:list){
				//处理数据
				for(IFillStataData<Map<String, Object>> fill:fillDatas){
					fill.fillData(map, rownum);
				}
				dealCount++;
				session.setAttribute(importStatus, "已处理"+Math.round(dealCount*100.0/totalCount)+"%，"+dealCount+"/"+totalCount+"条。");
			}
			for(IFillStataData<Map<String, Object>> fill:fillDatas){
				fill.closeStat();
			}
			session.setAttribute(importStatus, "已处理"+Math.round(dealCount*100.0/totalCount)+"%，"+dealCount+"/"+totalCount+"条。");
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
	
}
