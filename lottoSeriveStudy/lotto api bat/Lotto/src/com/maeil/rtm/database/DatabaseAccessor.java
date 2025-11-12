package com.maeil.rtm.database;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.util.Properties;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import com.maeil.rtm.mapper.ServiceMapper;


public final class DatabaseAccessor {
	//private static final Logger logger = LoggerFactory.getLogger(DatabaseAccessor.class);
	//private static final Logger logger = LogManager.getLogger(DatabaseAccessor.class);

	private static SqlSessionFactory sqlMapper = null;

	private static DatabaseAccessor instance = null;

	private DatabaseAccessor() {
		
		String configPath = "properties" + File.separator + "db.properties";
		
        Properties props = new Properties();	
        
        FileInputStream fis = null;
		try {
			fis = new FileInputStream(configPath);
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			//logger.error(e1.toString());
			e1.printStackTrace();
		}
         
        // 프로퍼티 파일 로딩
        try {
			props.load(new java.io.BufferedInputStream(fis));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			//logger.error(e1.toString());
			e1.printStackTrace();
			
		}        
		
		String resource = "com/maeil/rtm/mapper/configuration.xml";
		
		Reader reader = null;
		try {
			reader = Resources.getResourceAsReader(resource);
			
			sqlMapper = new SqlSessionFactoryBuilder().build(reader, props);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static synchronized DatabaseAccessor getInstance() {
		if (instance == null)
			instance = new DatabaseAccessor();
		return instance;
	}

	public SqlSession getSession(boolean flag) throws Exception {
		return sqlMapper.openSession(flag);
	}

	
	// 선적정보 확인
	public String selectDELI() throws Exception {
		String resultStr = "";
		SqlSession session = sqlMapper.openSession(false);
		ServiceMapper mapper = (ServiceMapper) session.getMapper(ServiceMapper.class);
		try {
			
			resultStr = session.selectOne("selectDELI");
			
		} catch (Exception ex) {
			throw ex;
		} finally {
			session.close();
		}
		
		return resultStr;
	}
	
	
	
	// S1 실시간 여부 확인
	public String selectS1() throws Exception {
		String resultStr = "";
		SqlSession session = sqlMapper.openSession(false);
		ServiceMapper mapper = (ServiceMapper) session.getMapper(ServiceMapper.class);
		try {
			
			resultStr = session.selectOne("selectS1");
			
		} catch (Exception ex) {
			throw ex;
		} finally {
			session.close();
		}
		
		return resultStr;
	}
	
	// 대단 실시간 여부 확인
		public String selectDaedan() throws Exception {
			String resultStr = "";
			SqlSession session = sqlMapper.openSession(false);
			ServiceMapper mapper = (ServiceMapper) session.getMapper(ServiceMapper.class);
			try {
				
				resultStr = session.selectOne("selectDaedan");
				
			} catch (Exception ex) {
				throw ex;
			} finally {
				session.close();
			}
			
			return resultStr;
		}
	
}
