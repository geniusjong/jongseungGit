import java.sql.*;
import java.util.HashMap;

import org.apache.log4j.Logger;

public class DB {
	//static String connectionUrl = "jdbc:sqlserver://10.227.1.47:1433;database=SINDOH;";
	static String connectionUrl;
	static String dbUserId;
	static String dbPassword;
	static Logger logger = Logger.getLogger(DB.class);
	
	public void DB_SDH_SEQ_EXEC() throws ClassNotFoundException {
    	sdh_seq_exec();
    }
	
	public void DB_SDH_CRATE_PACK_MASTER_IF(HashMap<String, Object> map) throws ClassNotFoundException {
    	logger.info("DB_SDH_CRATE_PACK_MASTER_IF:"+map);
    	sdh_crate_pack_master_if(map);
    }
	
	public void DB_SDH_MATERIAL_MAINT_IF_ST(HashMap<String, Object> map) throws ClassNotFoundException {
    	logger.info("DB_SDH_MATERIAL_MAINT_IF_ST:"+map);
    	sdh_material_maint_if_st(map);
    }
    
    public boolean DB_SDH_EIS_MATERIAL_CODE(HashMap<String, Object> map) throws ClassNotFoundException {
    	logger.info("DB_SDH_EIS_MATERIAL_CODE:"+map);
    	
    	return sdh_eis_material_code(map);
    }
        
    public void DB_SDH_EIS_LEVEL_CODE(HashMap<String, Object> map) throws ClassNotFoundException {
    	logger.info("DB_SDH_EIS_LEVEL_CODE:"+map);
    	sdh_eis_level_code(map);
    }
    
    public void DB_SDH_SORT_MASTER_TABLE(HashMap<String, Object> map) throws ClassNotFoundException {
    	logger.info("DB_SDH_SORT_MASTER_TABLE:"+map);
    	sdh_sort_master_table(map);
    }
    
    public void DB_SDH_INTAKE_AMOUNT_PACK(HashMap<String, Object> map) throws ClassNotFoundException {
    	logger.info("DB_SDH_INTAKE_AMOUNT_PACK:"+map);
    	sdh_intake_amount_pack(map);
    }
    
    public void DB_SDH_SEND_SHIP_DELI_INF_SHIP(HashMap<String, Object> map) throws ClassNotFoundException {
    	logger.info("DB_SDH_SEND_SHIP_DELI_INF_SHIP!:"+map);
    	sdh_send_ship_deli_inf_ship(map);
    }
    
    public void DB_SDH_SEND_SHIP_DELI_INF_DELI(HashMap<String, Object> map) throws ClassNotFoundException {
    	logger.info("DB_SDH_SEND_SHIP_DELI_INF_DELI!:"+map);
    	sdh_send_ship_deli_inf_deli(map);
    }
    
    public void DB_SAP_SHIP_COUNT(HashMap<String, Object> map) throws ClassNotFoundException {
    	logger.info("DB_SAP_SHIP_COUNT!:"+map);
    	sdh_sap_ship_count(map);
    }
    
    public void DB_SP_DELIVERYINFO() throws ClassNotFoundException {
    	logger.info("DB_sp_deliveryInfo!:");
    	sdh_sp_deliveryInfo();
    }
    
    
    
    public static void sdh_sp_deliveryInfo() throws ClassNotFoundException {
        try {
        	logger.info("sdh_sp_deliveryInfo");
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            Connection conn = DriverManager.getConnection(connectionUrl, dbUserId, dbPassword);
            Statement stmt = conn.createStatement();
            //logger.info("MS-SQL 서버 접속에 성공하였습니다.!!");
            
            
            String query = "EXECUTE [dbo].[sp_deliveryInfo] ";
            logger.info(query);
			stmt.execute(query);
            
            stmt.close();   
            conn.close();
        } catch (SQLException sqle) {
            logger.info("SQLException : " + sqle);
        }
    }
    
    public static void sdh_sap_ship_count(HashMap<String, Object> map) throws ClassNotFoundException {
        try {
        	logger.info("sdh_sap_ship_count");
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            Connection conn = DriverManager.getConnection(connectionUrl, dbUserId, dbPassword);
            Statement stmt = conn.createStatement();
            //logger.info("MS-SQL 서버 접속에 성공하였습니다.!!");
            
            
            String query = "EXECUTE [dbo].[sp_maeil_sap_shipment_count] ";
            query+="\'"+map.get("DPABF")+"\',";
            query+="\'"+map.get("WERKS")+"\',";
            query+=map.get("TKNUM_CNT")+",";
            query+=map.get("VBELN_CNT")+",";
            query+=map.get("TKNUM_CNT_R")+",";
            query+=""+map.get("VBELN_CNT_R");
            
            /*String query = "INSERT INTO dbo.MAEIL_SAP_SHIPMENT_COUNT "
            		+ "(SAP_DATE,WERKS, TKNUM_CNT,VBELN_CNT,TKNUM_CNT_R,VBELN_CNT_R"
            		+ ",CREATED_DATETIME,CREATED_BY) "
            		+ "VALUES "
            		+ "(getdate(),\'"+map.get("WERKS")+"\',"+map.get("TKNUM_CNT")+","+map.get("VBELN_CNT")+","
            		+map.get("TKNUM_CNT_R")+","+map.get("VBELN_CNT_R")+""
            		+ ",getdate(),'SAP')";

*/            logger.info(query);
			stmt.execute(query);
            
            stmt.close();   
            conn.close();
        } catch (SQLException sqle) {
            logger.info("SQLException : " + sqle);
        }
    }
    
    public boolean sdh_crate_pack_master_if(HashMap<String, Object> map) throws ClassNotFoundException {
        try {
        	logger.info("sdh_crate_pack_master_if");
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            Connection conn = DriverManager.getConnection(connectionUrl, dbUserId, dbPassword);
            Statement stmt = conn.createStatement();
            //logger.info("MS-SQL 서버 접속에 성공하였습니다.!!");
            
            String query = "EXECUTE [dbo].[sp_maeil_3001] ";
            query+="\'"+map.get("VSTEL")+"\',";
            query+="\'"+map.get("MATNR")+"\',";
            query+="\'"+map.get("LFIMG")+"\',";
            query+="\'"+map.get("MEINS")+"\',";
            query+="\'"+map.get("VHILM")+"\',";
            query+="\'S\',";
            query+="\'\' ";
		  

            logger.info(query);
            stmt.execute(query);
            stmt.close();   
            conn.close();
            return true;
        } catch (SQLException sqle) {
        	//로그에 에러 메시지와 쿼리를 남기기
            logger.info("SQLException : " + sqle);
            return false;
        }
    }
    
    public static String sdh_seq_exec() throws ClassNotFoundException {
    	String SEQ_EXEC =null;
    	try {
        	logger.info("sdh_seq_exec");
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            Connection conn = DriverManager.getConnection(connectionUrl, dbUserId, dbPassword);
            Statement stmt = conn.createStatement();
            
            String query = "SELECT NEXT VALUE FOR SEQ_EXEC AS SEQ_EXEC ";
            logger.info(query);
            
            ResultSet rs = null;
            rs = stmt.executeQuery(query);
            while( rs.next() ) {
            	SEQ_EXEC = rs.getString("SEQ_EXEC");
                logger.info("SEQ_EXEC : " + SEQ_EXEC);
             }
            
            rs.close();
            stmt.close();   
            conn.close();
            return SEQ_EXEC;
        } catch (SQLException sqle) {
        	//로그에 에러 메시지와 쿼리를 남기기
            logger.info("SQLException : " + sqle);
            return SEQ_EXEC;
        }
    }
    
    
    
    public static boolean sdh_material_maint_if_st(HashMap<String, Object> map) throws ClassNotFoundException {
        try {
        	logger.info("DBinsert sdh_material_if_st!!:"+map);
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            Connection conn = DriverManager.getConnection(connectionUrl, dbUserId, dbPassword);
            Statement stmt = conn.createStatement();
            //logger.info("MS-SQL 서버 접속에 성공하였습니다.!!");
            String query = "EXECUTE [dbo].[sp_maeil_3002] ";
	            query+="\'"+map.get("MATNR")+"\',";
	            query+="\'"+map.get("WERKS")+"\',";
	            query+="\'"+map.get("MEINS")+"\',";
	            query+="\'"+map.get("ZBOX")+"\',";
	        	query+="\'"+map.get("ZPAL")+"\',";
	            query+="\'S\',";
	            query+="\'"+"\'";

            logger.info(query);
            stmt.execute(query);
            stmt.close();   
            conn.close();
            return true;
        } catch (SQLException sqle) {
            logger.info("SQLException : " + sqle);
            return false;
        }
    }
    
    
    
    public boolean sdh_eis_material_code(HashMap<String, Object> map) throws ClassNotFoundException {
        try {
        	logger.info("DBinsert sdh_eis_material_code!!:"+map);
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            
            Connection conn = DriverManager.getConnection(connectionUrl, dbUserId, dbPassword);
            Statement stmt = conn.createStatement();
            //logger.info("MS-SQL 서버 접속에 성공하였습니다.!!");
            String query = "EXECUTE [dbo].[sp_maeil_3003] ";
    		query+="\'"+map.get("ZLEV1")+"\',";
    		query+="\'"+map.get("ZLEV2")+"\',";
			query+="\'"+map.get("ZLEV3")+"\',";
			query+="\'"+map.get("MATNR")+"\',";
			query+="\'"+map.get("ZUSER")+"\',";
			query+="\'"+map.get("DATUM")+"\',";
			query+="\'"+map.get("UZEIT")+"\',";
			query+="\'"+map.get("LVORM")+"\',";
			query+="\'S',";
			query+="\'\'";

            logger.info(query);
            stmt.execute(query);
            stmt.close();   
            conn.close();
            return true;
        } catch (SQLException sqle) {
            logger.info("SQLException : " + sqle);
            return false;
        }
    }
    
    public static boolean sdh_eis_level_code(HashMap<String, Object> map) throws ClassNotFoundException {
        try {
        	logger.info("DBinsert sdh_eis_level_code!!:"+map);
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");

            Connection conn = DriverManager.getConnection(connectionUrl, dbUserId, dbPassword);
            Statement stmt = conn.createStatement();
            //logger.info("MS-SQL 서버 접속에 성공하였습니다.!!");
            String query = "EXECUTE [dbo].[sp_maeil_3004] ";
			query+="\'"+map.get("ZLEVEL")+"\',";
			query+="\'"+map.get("ZCODE")+"\',";
			query+="\'"+map.get("ZCODE_NAME")+"\',";
			query+="\'"+map.get("ZUSER")+"\',";
			query+="\'"+map.get("DATUM")+"\',";
			query+="\'"+map.get("UZEIT")+"\',";
			query+="\'"+map.get("LVORM")+"\',";
			query+="\'S\',";
			query+="\'\'";
            logger.info(query);
            stmt.execute(query);
            stmt.close();   
            conn.close();
            return true;
        } catch (SQLException sqle) {
            logger.info("SQLException : " + sqle);
            return false;
        }
    }
    
    public static boolean sdh_sort_master_table(HashMap<String, Object> map) throws ClassNotFoundException {
        try {
        	logger.info("DBinsert sdh_sort_master_table!!:"+map);
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");

            Connection conn = DriverManager.getConnection(connectionUrl, dbUserId, dbPassword);
            Statement stmt = conn.createStatement();
            //logger.info("MS-SQL 서버 접속에 성공하였습니다.!!");
            String query = "EXECUTE [dbo].[sp_maeil_3005] ";
			query+="\'"+map.get("WERKS")+"\',";
			query+="\'"+map.get("LGORT")+"\',";
			query+="\'"+map.get("MATNR")+"\',";
			query+="\'"+map.get("MAKTX").toString().replace("'", "''")+"\',";
			if(Integer.parseInt(map.get("ZSORT").toString())==0){
				query+="\'9999\',";
			}else{
				query+="\'"+Integer.parseInt(map.get("ZSORT").toString())+"\',";	
			}
			query+="\'"+map.get("LVORM")+"\',";
			query+="\'S',";
			query+="\'\'";
        			
            logger.info(query);
            stmt.execute(query);
            stmt.close();   
            conn.close();
            return true;
        } catch (SQLException sqle) {
            logger.info("SQLException : " + sqle);
            return false;
        }
    }
    
    public static boolean sdh_intake_amount_pack(HashMap<String, Object> map) throws ClassNotFoundException {
        try {
        	logger.info("DBinsert sdh_intake_amount_pack!!:"+map);
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
	
            Connection conn = DriverManager.getConnection(connectionUrl, dbUserId, dbPassword);
            Statement stmt = conn.createStatement();
            //logger.info("MS-SQL 서버 접속에 성공하였습니다.!!");
            String query = "EXECUTE [dbo].[sp_maeil_3006] ";
                            
            query+="\'"+map.get("MATNR")+"\',";
            query+="\'"+map.get("MEINH")+"\',";
            query+="\'"+map.get("UMREZ")+"\',";
            query+="\'"+map.get("UMREN")+"\',";
            query+="\'"+map.get("EAN11")+"\',";
            query+="\'"+map.get("LVORM")+"\',";
            query+="\'S\',";
			query+="\'\'";
			
            logger.info(query);
            stmt.execute(query);
            stmt.close();   
            conn.close();
            return true;
        } catch (SQLException sqle) {
            logger.info("SQLException : " + sqle);
            return false;
        }
    }
   
    public static boolean sdh_send_ship_deli_inf_ship(HashMap<String, Object> map) throws ClassNotFoundException {
        try {
        	logger.info("sdh_send_ship_deli_inf_ship!!");
        	logger.info("mmmap!!:"+map);
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            
            Connection conn = DriverManager.getConnection(connectionUrl, dbUserId, dbPassword);
            Statement stmt = conn.createStatement();
            //logger.info("MS-SQL 서버 접속에 성공하였습니다.!!");
            String query = "EXECUTE [dbo].[sp_maeil_3010] ";
            query+="\'"+map.get("SEQNO")+"\',";
            query+="\'"+map.get("TKNUM")+"\',";
            query+="\'"+map.get("SHTYP")+"\',";
            query+="\'"+map.get("TPLST")+"\',";
            query+="\'"+map.get("BEZEI1")+"\',";
            query+="\'"+map.get("DTDIS")+"\',";
            query+="\'"+map.get("STDIS")+"\',";
            query+="\'"+map.get("ROUTE")+"\',";
            query+="\'"+map.get("BEZEI2")+"\',";
            query+="\'"+map.get("TDLNR")+"\',";
            query+="\'"+map.get("NAME1")+"\',";
            query+="\'"+map.get("TELF1")+"\',";
            query+="\'"+map.get("TELF2")+"\',";
            query+="\'"+map.get("DPABF")+"\',";
            query+="\'"+map.get("UPDKZ")+"\',";
            //query+="\'"+map.get("SNDOK")+"\',";
            //query+="\'"+map.get("MESSAGE")+"\',";
            query+="\'\',";
            query+="\'\',";
            query+=map.get("SEQ_EXEC");
            
            logger.info(query);
            //ResultSet rs = stmt.executeQuery(query);
            stmt.execute(query);
            
            /*while( rs.next() ) {
                   String field1 = rs.getString("TITLE");
                   String field2 = rs.getString("UNIT");
                   System.out.print(field1 + "\t");
                   logger.info(field2);
                  }*/
            //rs.close();
            stmt.close();   
            conn.close();
            return true;
        } catch (SQLException sqle) {
            logger.info("SQLException : " + sqle);
            return false;
        }
    }
    
    public static boolean sdh_send_ship_deli_inf_deli(HashMap<String, Object> map) throws ClassNotFoundException {
        try {
        	logger.info("sdh_send_ship_deli_inf_deli");
        	logger.info("connectionUrl:"+connectionUrl);
        	
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            
            Connection conn = DriverManager.getConnection(connectionUrl, dbUserId, dbPassword);
            Statement stmt = conn.createStatement();
            //logger.info("MS-SQL 서버 접속에 성공하였습니다.!!");
            String query = "EXECUTE [dbo].[sp_maeil_3011] ";
            query+="\'"+map.get("SEQNO")+"\',";
            query+="\'"+map.get("TKNUM")+"\',";
            query+="\'"+map.get("TPNUM")+"\',";
            query+="\'"+map.get("VBELN")+"\',";
            query+="\'"+map.get("POSNR")+"\',";
            query+="\'"+map.get("MATNR")+"\',";
            query+="\'"+map.get("ARKTX")+"\',";
            query+="\'"+map.get("WERKS")+"\',";
            query+="\'"+map.get("LGORT")+"\',";
            query+="\'"+map.get("LFIMG1")+"\',";
            query+="\'"+map.get("MEINS1")+"\',";
            query+="\'"+map.get("NTGEW1")+"\',";
            query+="\'"+map.get("BRGEW1")+"\',";
            query+="\'"+map.get("GEWEI1")+"\',";
            query+="\'"+map.get("VOLUM1")+"\',";
            query+="\'"+map.get("VOLEH1")+"\',";
            query+="\'"+map.get("MEINH")+"\',";
            query+="\'"+map.get("UMREZ")+"\',";
            query+="\'"+map.get("UMREN")+"\',";
            query+="\'"+map.get("BOX_LFIMG")+"\',";
            query+="\'"+map.get("BOX_EA_LFIMG")+"\',";
            query+="\'"+map.get("LFART")+"\',";
            query+="\'"+map.get("VSTEL")+"\',";
            query+="\'"+map.get("VTEXT")+"\',";
            query+="\'"+map.get("VKORG")+"\',";
            query+="\'"+map.get("WADAT")+"\',";
            query+="\'"+map.get("ZTIME")+"\',";
            query+="\'"+map.get("LFDAT")+"\',";
            query+="\'"+map.get("ROUTE")+"\',";
            query+="\'"+map.get("BEZEI1")+"\',";
            query+="\'"+map.get("KUNNR")+"\',";
            query+="\'"+map.get("NAME1")+"\',";
            query+="\'"+map.get("VKBUR")+"\',";
            query+="\'"+map.get("BEZEI2")+"\',";
            query+="\'"+map.get("J_1KFREPRE")+"\',";
            query+="\'"+map.get("TELF1")+"\',";
            query+="\'"+map.get("TELF2")+"\',";
            query+="\'"+map.get("ORT01")+"\',";
            query+="\'"+map.get("STRAS")+"\',";
            query+="\'"+map.get("KUNAG")+"\',";
            query+="\'"+map.get("NAME2")+"\',";
            query+="\'"+map.get("BTGEW2")+"\',";
            query+="\'"+map.get("GEWEI2")+"\',";
            query+="\'"+map.get("NTGEW2")+"\',";
            query+="\'"+map.get("VOLUM2")+"\',";
            query+="\'"+map.get("VOLEH2")+"\',";
            query+="\'"+map.get("UPDKZ")+"\',";
            //query+="\'"+map.get("SNDOK")+"\',";
            //query+="\'"+map.get("MESSAGE")+"\',";
            query+="\'\',";
            query+="\'\',";
            query+=map.get("SEQ_EXEC");
            
            logger.info(query);
            stmt.execute(query);
            
            /*while( rs.next() ) {
                   String field1 = rs.getString("TITLE");
                   String field2 = rs.getString("UNIT");
                   System.out.print(field1 + "\t");
                   logger.info(field2);
                  }*/
            //rs.close();
            stmt.close();   
            conn.close();
            return true;
        } catch (SQLException sqle) {
            logger.info("SQLException : " + sqle);
            return false;
        }
    }
}