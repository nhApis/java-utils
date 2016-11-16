package com.github.nhapis.util.db;

import java.sql.Connection;
import java.sql.ResultSet;

import com.github.nhapis.util.conf.Config;
import com.github.nhapis.util.StringUtil;
import com.github.nhapis.util.db.MysqlUtil.MysqlPool;

public class TestMysqlUtil {
	
	public static void main(String [] args) {
		Config config = new Config();
		if (config.parse("conf/mysql.properties")) {
			System.out.println(config.toString());
		} else {
			System.err.println("parse conf file failed");
			return;
		}
		
		MysqlPool pool = MysqlUtil.createMysqlPool(config);
		if (pool != null) {
			try {
				Connection conn = pool.getInnerPool().getConnection();
				System.out.println(StringUtil.toString(conn));
				
				MysqlSession session = new MysqlSession(conn);
				String sql = "select * from t_article_profile limit 1;";
				ResultSet rs = session.executeQuery(sql);
				while (rs.next()) {
					System.out.println(rs.getString(2));
				}
				
				MysqlUtil.closeResultSet(rs);
			} catch (Exception e) {
				e.printStackTrace();
			} 
			
			MysqlUtil.closeMysqlPool(pool);
		}
	}

}
