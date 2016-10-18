package com.wiscess.jpa.jdbc;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
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
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.JdbcUtils;

import com.wiscess.common.utils.StringUtil;
import com.wiscess.jpa.util.DynamicSqlUtil;
import com.wiscess.jpa.util.ISqlElement;
import com.wiscess.query.config.Query;

@Slf4j
public class JdbcJpaSupport {
	@Autowired
	private JdbcTemplate jdbcTemplate;

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

	public <E> List<E> findList(String sqlName, Map<String, Object> params, RowMapper<E> rm) throws Exception {
		log.debug("findList(String, Map<String,Object>, RowMapper) - start");

		final ISqlElement se = processSql(params, sqlName);
		if (rm == null) {
			@SuppressWarnings("unchecked")
			List<E> returnList = (List<E>) jdbcTemplate.queryForList(se.getSql(),
					se.getParams());
			log.debug("findList(String, Map<String,Object>, RowMapper) - end");
			return returnList;
		} else {
			List<E> returnList = jdbcTemplate.query(se.getSql(),
					se.getParams(), rm);
			log.debug("findList(String, Map<String,Object>, RowMapper) - end");
			return returnList;
		}
	}
	public <E> List<E> findList(String sqlName, RowMapper<E> rm, String[] paramName, Object... paramValue) throws Exception {
		log.debug("findList(String, RowMapper, String[], Object) - start");

		Map<String, Object> params = new HashMap<String, Object>();
		for (int i = 0; i < paramName.length; i++) {
			params.put(paramName[i], paramValue[i]);
		}

		List<E> returnList = findList(sqlName, params, rm);

		log.debug("findList(String, RowMapper, String[], Object) - end");
		return returnList;
	}
	public <E> List<E> findList(String sqlName, RowMapper<E> rm, String paramName, Object paramValue) throws Exception {
		log.debug("findList(String, RowMapper, String, Object) - start");
		List<E> returnList = findList(sqlName, rm, new String[] { paramName },
				paramValue);
		log.debug("findList(String, RowMapper, String, Object) - end");
		return returnList;
	}
	public <E> List<E> findList(String sqlName, RowMapper<E> rm) throws Exception {
		log.debug("findList(String, RowMapper) - start");
		List<E> returnList = findList(sqlName, rm, new String[] {}, "");
		log.debug("findList(String, RowMapper) - end");
		return returnList;
	}	
	public <E> List<E> findList(String sqlName, String paramName, Object paramValue) throws Exception {
		log.debug("findList(String, RowMapper) - start");
		List<E> returnList = findList(sqlName, null, new String[] { paramName }, paramValue);
		log.debug("findList(String, RowMapper) - end");
		return returnList;
	}	
	
	/**
	 * 更新语句
	 * @param sqlName
	 * @return
	 * @throws Exception
	 */
	public boolean update(String sqlName) throws Exception{
		return update(sqlName,new HashMap<String, Object>());
	}
	public boolean update(String sqlName,Map<String, Object> params)throws Exception {
		ISqlElement se=processSql(params, sqlName);
		this.jdbcTemplate.update(se.getSql(), se.getParams());
		return true;
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
		ISqlElement se=processSql(params, sqlName);
		return jdbcTemplate.queryForObject(se.getSql(),se.getParams(), requiredType);
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
	@SuppressWarnings("unchecked")
	public <E> Page<E> findPage(String querySqlName, String countSqlName,
			Map<String, Object> params,Pageable pageable,RowMapper<E> rm) throws Exception{
		if(rm==null){
			rm=(RowMapper<E>) new ColumnMapRowMapper();
		}
		if(params==null){
			params=new HashMap<String, Object>();
		}
		if (null == pageable) {
			//没有分页，等同于findList
			return new PageImpl<E>((List<E>) findList(querySqlName,params,rm));
		}
		//获取总数
		ISqlElement seQuery = processSql(params, querySqlName);
		ISqlElement seCount = null;
		if(countSqlName==null){
			params.put("count", "true");
			seCount = processSql(params, querySqlName);
			params.remove("count");
		}else{
			seCount = processSql(params, countSqlName);
		}
		final int total = jdbcTemplate.queryForObject(seCount.getSql(), seCount.getParams(),Integer.class);
		//创建空集合
		List<E> content = Collections.<E> emptyList();
		if(total>pageable.getOffset()){
			//如果未超过总记录，则读取数据，返回List
			content = jdbcTemplate.query(
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
	protected void importBaseData(HttpSession session,String importStatus,final String[] titleRow, 
			List<String[]> rowList,List<String> preExcuteSqlList,IFillStataData fill){
		this.importBaseData(session, importStatus, titleRow, null, rowList, preExcuteSqlList, new IFillStataData[]{fill});
	}
	
	protected void importBaseData(HttpSession session,String importStatus,final String[] titleRow,
			final Map<String,Integer> fieldMap,
			List<String[]> rowList,List<String> preExcuteSqlList,IFillStataData[] fillDatas){
		//错误列表
		List<String> errorList=new ArrayList<String>();
		//当前行
		int rownum=0;
		String[] rows=rowList.get(rownum);
		session.setAttribute(importStatus, "正在校验字段名称");
		if(fieldMap==null){
			//按原始方法校验字段的顺序
			for(int i=0;i<titleRow.length;i++){
				if(StringUtil.isNotEmpty(rows[i]))
					if(!titleRow[i].trim().equals(rows[i].trim())){
						errorList.add("第"+(i+1)+"列列名应为["+titleRow[i]+"];");
						continue;
				}
			}
		}else{
			//按新方法校验字段是否存在
			for(int i=0;i<titleRow.length;i++){
				if(!fieldMap.containsKey(titleRow[i])){
					errorList.add("缺少列："+titleRow[i]+";");
					continue;
				}
			}
		}
		if(errorList.size()>0){
			checkResult(session, importStatus, "字段名称不正确，"+errorList.toString());
		}
		
		//批量处理数据
		Connection con = null;// 本地库连接
		PreparedStatement deleteState = null;
		try {
			con = jdbcTemplate.getDataSource().getConnection();// 获取数据库连接
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
			for(IFillStataData fill:fillDatas){
				//读取sql的模板
				fill.insertState=con.prepareStatement(processSql(new HashMap<String, Object>(), fill.sqlKey).getSql());
			}
			//总记录数
			int totalCount=rowList.size()-1;
			//已处理记录
			int dealCount=0;
			session.setAttribute(importStatus, "开始导入数据，共有数据"+totalCount+"条。");
			Thread.sleep(500);
			//开始处理数据
			for(String[] row: rowList) {
				rownum++;
				if(rownum==1 || row==null || row.length<titleRow.length ||( StringUtil.isNotEmpty(titleRow[0]) && StringUtil.isEmpty(row[0]))) {
					continue;
				}
				for(IFillStataData fill:fillDatas){
					//处理数据
					fill.fillData(row,rownum);
				}
				dealCount++;
				session.setAttribute(importStatus, "已处理"+Math.round(dealCount*100.0/totalCount)+"%，"+dealCount+"/"+totalCount+"条。");
				Thread.sleep(5);
			}
			//处理剩余的数据
			for(IFillStataData fill:fillDatas){
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
		if(null != sqlList && sqlList.size()>0){
			String[] sqls=new String[sqlList.size()];
			sqls=sqlList.toArray(sqls);	
			jdbcTemplate.batchUpdate(sqls);
		}
	}
	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}
}
