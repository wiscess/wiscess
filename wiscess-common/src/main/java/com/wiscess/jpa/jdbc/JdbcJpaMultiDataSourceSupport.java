package com.wiscess.jpa.jdbc;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;

import com.wiscess.jpa.util.DynamicSqlUtil;
import com.wiscess.jpa.util.ISqlElement;
import com.wiscess.query.config.Query;

@Slf4j
public class JdbcJpaMultiDataSourceSupport extends JdbcJpaSupport{
	
	@Autowired
	@Qualifier("jdbcTemplate1")
	private JdbcTemplate jdbcTemplate1;

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

	public <E> IPage<E> findPageByDs1(String querySqlName, String countSqlName,
			Map<String, Object> params, final int pageNo, final int pageSize,
			final RowMapper<E> rh) throws Exception {
			log.debug("findPageByDs1(String, String, Map<String,Object>, int, int, IRowHandler<E>) - start");

		final ISqlElement seQuery = processSql(params, querySqlName);
		final ISqlElement seCount = processSql(params, countSqlName);
		final int count = jdbcTemplate1.queryForObject(seCount.getSql(), seCount.getParams(),Integer.class);
		
		
		IPage<E> returnIPage = (IPage<E>) jdbcTemplate1.query(
				seQuery.getSql(), seQuery.getParams(),
				new ResultSetExtractor<IPage<E>>() {

					public IPage<E> extractData(final ResultSet rs)
							throws SQLException, DataAccessException {

						IPage<E> returnObject = new JdbcPage<E>(rs, count,
								pageNo, pageSize, rh);
						return returnObject;
					}

				});
			
		log.debug("findPageByDs1(String, String, Map<String,Object>, int, int, IRowHandler<E>) - end");
		return returnIPage;
	}

	public <E> IPage<E> findPageByDs1(String querySqlName, String countSqlName,
			final int pageNo, final int pageSize, final RowMapper<E> rh,
			String[] paramName, Object... paramValue) throws Exception {
		log.debug("findPageByDs1(String, String, int, int, IRowHandler<E>, String[], Object) - start");

		Map<String, Object> params = new HashMap<String, Object>();
		for (int i = 0; i < paramName.length; i++) {
			params.put(paramName[i], paramValue[i]);
		}

		IPage<E> returnIPage = findPageByDs1(querySqlName, countSqlName, params,
				pageNo, pageSize, rh);
		log.debug("findPageByDs1(String, String, int, int, IRowHandler<E>, String[], Object) - end");
		return returnIPage;
	}

	public <E> IPage<E> findPageByDs1(String querySqlName, String countSqlName,
			final int pageNo, final int pageSize, final RowMapper<E> rh,
			String paramName, Object paramValue) throws Exception {
		log.debug("findPageByDs1(String, String, int, int, IRowHandler<E>, String, Object) - start");

		IPage<E> returnIPage = findPageByDs1(querySqlName, countSqlName, pageNo,
				pageSize, rh, new String[] { paramName }, paramValue);
		log.debug("findPageByDs1(String, String, int, int, IRowHandler<E>, String, Object) - end");
		return returnIPage;
	}

	public <E> IPage<E> findPageByDs1(String querySqlName, String countSqlName, final int pageNo, final int pageSize, final RowMapper<E> rh)
					throws Exception {
		log.debug("findPageByDs1(String, String, int, int, IRowHandler<E>) - start");

		IPage<E> returnIPage = findPageByDs1(querySqlName, countSqlName, pageNo, pageSize, rh, new String[] {}, "");

		log.debug("findPageByDs1(String, String, int, int, IRowHandler<E>) - end");
		return returnIPage;
	}
	
	@SuppressWarnings("unchecked")
	public <E> List<E> findListByDs1(String sqlName, Map<String, Object> params, RowMapper<E> rm) throws Exception {
		log.debug("findListByDs1(String, Map<String,Object>, RowMapper) - start");

		final ISqlElement se = processSql(params, sqlName);
		if (rm == null) {
			List<E> returnList = (List<E>) jdbcTemplate1.queryForList(se.getSql(),
					se.getParams());
			log.debug("findListByDs1(String, Map<String,Object>, RowMapper) - end");
			return returnList;
		} else {
			List<E> returnList = jdbcTemplate1.query(se.getSql(),
					se.getParams(), rm);
			log.debug("findListByDs1(String, Map<String,Object>, RowMapper) - end");
			return returnList;
		}
	}
	public <E> List<E> findListByDs1(String sqlName, RowMapper<E> rm, String[] paramName, Object... paramValue) throws Exception {
		log.debug("findListByDs1(String, RowMapper, String[], Object) - start");

		Map<String, Object> params = new HashMap<String, Object>();
		for (int i = 0; i < paramName.length; i++) {
			params.put(paramName[i], paramValue[i]);
		}

		List<E> returnList = findListByDs1(sqlName, params, rm);

		log.debug("findListByDs1(String, RowMapper, String[], Object) - end");
		return returnList;
	}
	public <E> List<E> findListByDs1(String sqlName, RowMapper<E> rm, String paramName, Object paramValue) throws Exception {
		log.debug("findListByDs1(String, RowMapper, String, Object) - start");
		List<E> returnList = findListByDs1(sqlName, rm, new String[] { paramName },
				paramValue);
		log.debug("findListByDs1(String, RowMapper, String, Object) - end");
		return returnList;
	}
	public <E> List<E> findListByDs1(String sqlName, RowMapper<E> rm) throws Exception {
		log.debug("findListByDs1(String, RowMapper) - start");
		List<E> returnList = findListByDs1(sqlName, rm, new String[] {}, "");
		log.debug("findListByDs1(String, RowMapper) - end");
		return returnList;
	}	
	public <E> List<E> findListByDs1(String sqlName, String paramName, Object paramValue) throws Exception {
		log.debug("findListByDs1(String, RowMapper) - start");
		List<E> returnList = findListByDs1(sqlName, null, new String[] { paramName }, paramValue);
		log.debug("findList(String, RowMapper) - end");
		return returnList;
	}	
	
	public boolean updateByDs1(String sqlName) throws Exception{
		return updateByDs1(sqlName,new HashMap<String, Object>());
	}
	
	public boolean updateByDs1(String sqlName,Map<String, Object> params)throws Exception {
		ISqlElement se=processSql(params, sqlName);
		this.jdbcTemplate1.update(se.getSql(), se.getParams());
		return true;
	}
	
	public <E> Object queryForObjectByDs1(String sqlName,Map<String, Object> params,Class<E> requiredType)throws Exception {
		ISqlElement se=processSql(params, sqlName);
		return jdbcTemplate1.queryForObject(se.getSql(),se.getParams(), requiredType);
	}
}
