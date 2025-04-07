
import java.sql.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

public class ClientDB {
	//static String connectionUrl = "jdbc:sqlserver://10.227.1.47:1433;database=SINDOH;";
	static Logger logger = Logger.getLogger(ClientDB.class);
	static String connectionUrl;
	static String dbUserId;
	static String dbPassword;
	public static List<Map<String, Object>> MAEIL_SHIPMENT_RETURN() throws ClassNotFoundException {
		String query = null;
    	List<Map<String, Object>> list = new ArrayList<>();
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            Connection conn = DriverManager.getConnection(connectionUrl, dbUserId, dbPassword);
            Statement stmt = conn.createStatement();            
            
    //        실제 쿼리
            query = "SELECT SEQNO,TKNUM,TPNUM,VBELN,WERKS,"
            		+ "VSTEL,WADAT,WADAT_IST,SNDOK,MESSAGE,FAIL_COUNT, "
            		+ "CREATED_DATETIME,CREATED_BY,UPDATED_DATETIME,UPDATED_BY "
            		+ "FROM dbo.MAEIL_SHIPMENT_RETURN WITH(NOLOCK) "
            		+ "WHERE (SNDOK != 'S' OR SNDOK IS NULL ) AND FAIL_COUNT < 3";
            
            

            //테스트 쿼리
            /*String query = "SELECT SEQNO,TKNUM,TPNUM,VBELN,WERKS,"
            		+ "VSTEL,WADAT "
            		+ "FROM dbo.MAEIL_SEND_DELIVERY_INFO "
            		+ "where CREATED_DATETIME >= \'2020-02-21\'";*/
            
            /*String query = "SELECT SEQNO,TKNUM,"
            		+ "(select top 1  TPNUM from MAEIL_SEND_DELIVERY_INFO b where b.SEQNO = a.SEQNO) as TPNUM "
            		+ ",(select top 1 VBELN from MAEIL_SEND_DELIVERY_INFO b where b.SEQNO = a.SEQNO) as VBELN,"
            		+ "(select top 1 WERKS from MAEIL_SEND_DELIVERY_INFO b where b.SEQNO = a.SEQNO) as WERKS,"
            		+ "(select top 1 VSTEL from MAEIL_SEND_DELIVERY_INFO b where b.SEQNO = a.SEQNO) as VSTEL,"
            		+ "(select top 1 WADAT from MAEIL_SEND_DELIVERY_INFO b where b.SEQNO = a.SEQNO) as WADAT "
            		+ "FROM dbo.MAEIL_SEND_SHIPMENT_INFO a where CREATED_DATETIME >= \'2020-02-21\'";
            */
            
        
            logger.info("query : " + query);
            ResultSet rs = null;
            rs = stmt.executeQuery(query);
            while( rs.next() ) {
            	HashMap<String, Object> map = new HashMap<String, Object>();
                map.put("SEQNO", rs.getString("SEQNO"));
                map.put("TKNUM", rs.getString("TKNUM"));
                map.put("TPNUM", rs.getString("TPNUM"));
                map.put("VBELN", rs.getString("VBELN"));
                map.put("WERKS", rs.getString("WERKS"));
                map.put("VSTEL", rs.getString("VSTEL"));
                map.put("WADAT", rs.getString("WADAT"));
                //실제 사용
                map.put("WADAT_IST", rs.getString("WADAT_IST"));
                //map.put("WADAT_IST", "20200221");
                logger.info("map : " + map);
                list.add(map);
             }
            
            rs.close();

            stmt.close();   
            conn.close();
            return list;
        } catch (SQLException sqle) {
            System.out.println("SQLException : " + sqle);
            
            logger.info("SQLException : " + sqle);
            logger.info("query : " + query);
            return list;
        }
    }
    
    public static List<Map<String, Object>> MAEIL_DELIVERY_RETURN() throws ClassNotFoundException {
    	
    	List<Map<String, Object>> list = new ArrayList<>();
    	String query = null;
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            Connection conn = DriverManager.getConnection(connectionUrl, dbUserId, dbPassword);
            Statement stmt = conn.createStatement();
            
            
            //실제 쿼리 
             /*query = "SELECT SEQNO,TKNUM,TPNUM,VBELN,POSNR"
            		+ ", MATNR,WERKS,LGORT,LFIMG1,MEINS1,SNDOK"
            		+ ", MESSAGE,FAIL_COUNT, CREATED_DATETIME"
            		+ ", CREATED_BY,UPDATED_DATETIME,UPDATED_BY "
            		+ "FROM dbo.MAEIL_DELIVERY_RETURN WITH(NOLOCK) "
            		+ "WHERE (SNDOK != 'S' OR SNDOK IS NULL ) AND FAIL_COUNT < 3";*/
             
             
             query = "SELECT A.SEQNO,A.TKNUM,A.TPNUM,A.VBELN,A.POSNR "
             		+ ", A.MATNR,A.WERKS,A.LGORT,A.LFIMG1,A.MEINS1,A.SNDOK "
             		+ ", A.MESSAGE,A.FAIL_COUNT, A.CREATED_DATETIME "
             		+ ", A.CREATED_BY, A.UPDATED_DATETIME, A.UPDATED_BY "
             		+ "FROM dbo.MAEIL_DELIVERY_RETURN A, MAEIL_SHIPMENT_RETURN B WITH(NOLOCK) "
             		+ "WHERE A.SEQNO = B.SEQNO AND A.TKNUM = B.TKNUM AND A.VBELN = B.VBELN "
             		+ "AND (A.SNDOK != 'S' OR A.SNDOK IS NULL ) AND A.FAIL_COUNT < 3 "
             		+ "AND (B.SNDOK != 'S' OR B.SNDOK IS NULL ) AND B.FAIL_COUNT < 3"; 

             
             
             
            //테스트 쿼리
            /*String query = "SELECT SEQNO,TKNUM,TPNUM,VBELN,POSNR"
            		+ ",MATNR,WERKS,LGORT,LFIMG1,MEINS1 "
            		+ "FROM dbo.MAEIL_SEND_DELIVERY_INFO "
            		+ "where CREATED_DATETIME >= \'2020-02-21\'";*/

            System.out.println(query);
            ResultSet rs = null;
            rs = stmt.executeQuery(query);
            while( rs.next() ) {
        	HashMap<String, Object> map = new HashMap<String, Object>();
/*                String SEQNO = rs.getString("SEQNO");
                String TKNUM = rs.getString("TKNUM");
                String TPNUM = rs.getString("TPNUM");
                String VBELN = rs.getString("VBELN");
                System.out.print("SEQNO:"+SEQNO);
                System.out.print("TKNUM:"+TKNUM);
                System.out.print("TPNUM:"+TPNUM);
                System.out.print("VBELN:"+VBELN);*/
				map.put("SEQNO", rs.getString("SEQNO"));
				map.put("TKNUM", rs.getString("TKNUM"));
				map.put("TPNUM", rs.getString("TPNUM"));
				map.put("VBELN", rs.getString("VBELN"));
				map.put("POSNR", rs.getString("POSNR"));
				map.put("MATNR", rs.getString("MATNR"));
				map.put("WERKS", rs.getString("WERKS"));
				map.put("LGORT", rs.getString("LGORT"));
				map.put("LFIMG1", rs.getString("LFIMG1"));
				map.put("MEINS1", rs.getString("MEINS1"));
				list.add(map);
			}
            
            rs.close();

            stmt.close();   
            conn.close();
            return list;
        } catch (SQLException sqle) {
            System.out.println("SQLException : " + sqle);
            logger.info("SQLException : " + sqle);
            logger.info("query : " + query);
            return list;
        }
    }
    
    
    public static boolean UPDATE_MAEIL_SHIPMENT_RETURN(List<Map<String, Object>> returnList) throws ClassNotFoundException {
		String query = null;
		String query2 = null;
		String query3 = null;
    	
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            Connection conn = DriverManager.getConnection(connectionUrl, dbUserId, dbPassword);
            Statement stmt = conn.createStatement();            
            
            for (int i=0; i<returnList.size(); i++) {
    			Map<String, Object> m = returnList.get(i);
    			String SEQNO = m.get("SEQNO").toString();
    			String TKNUM = m.get("TKNUM").toString();
    			String TPNUM = m.get("TPNUM").toString();
    			String VBELN = m.get("VBELN").toString();
    			String SNDOK = m.get("SNDOK").toString();
    			String MESSAGE = m.get("MESSAGE").toString();
    			
    			query = "UPDATE dbo.MAEIL_SHIPMENT_RETURN "
    					+ "SET SNDOK = '"+SNDOK+"', MESSAGE = '"+MESSAGE+"', "
    					+ "updated_datetime = getdate(), updated_by = 'SAP_Client' ";
    			
    			if(SNDOK.equals("E")){
    				query += ", FAIL_COUNT = FAIL_COUNT+1 ";	
    			}
    			
    			query += "WHERE SEQNO = '"+SEQNO+"' AND TKNUM = '"+TKNUM+"' "
    					+ " AND TPNUM = '"+TPNUM+"' AND VBELN = '"+VBELN+"'";
    			
	            System.out.println(query);
	            
	            
	            //여기서 S로 넘어와야만 STATUS = '60'으로 업데이트 하기
	            
	            query2 = "UPDATE dbo.MAEIL_SEND_SHIPMENT_INFO "
    					+ "SET STATUS = '60', "
    					+ "updated_datetime = getdate(), updated_by = 'SAP_Client' "
    					+ "WHERE SEQNO = '"+SEQNO+"'  AND TKNUM = '"+TKNUM+"' AND STATUS = '50'";
	            
	            System.out.println(query2);
	           

	            query3 = "INSERT INTO dbo.MAEIL_SHIPMENT_HISTORY ("
    					+ "SEQNO,TKNUM,STATUS,CREATED_DATETIME,CREATED_BY)"
	            		+ "VALUES('"+SEQNO+"', '"+TKNUM+"','60', getdate(), 'SAP_Client')";
	            
	            System.out.println(query3);
	            logger.info("query : " + query);

	            stmt.execute(query);
	            if(SNDOK.equals("S")){
		            logger.info("query2 : " + query2);
	            	logger.info("query3 : " + query3);
		            stmt.execute(query2);
		            stmt.execute(query3);
	            }
            }
            stmt.close();
            conn.close();
            return true;
        } catch (SQLException sqle) {
            System.out.println("SQLException : " + sqle);
            
            logger.info("SQLException : " + sqle);
            logger.info("query : " + query);
            return false;
        }
    }
    
    public static boolean UPDATE_MAEIL_DELIVERY_RETURN(List<Map<String, Object>> returnList) throws ClassNotFoundException {
		String query = null;
    	
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            Connection conn = DriverManager.getConnection(connectionUrl, dbUserId, dbPassword);
            Statement stmt = conn.createStatement();            
            
            for (int i=0; i<returnList.size(); i++) {
    			Map<String, Object> m = returnList.get(i);
    			String SEQNO = m.get("SEQNO").toString();
    			String TKNUM = m.get("TKNUM").toString();
    			String TPNUM = m.get("TPNUM").toString();
    			String VBELN = m.get("VBELN").toString();
    			String POSNR = m.get("POSNR").toString();
    			String SNDOK = m.get("SNDOK").toString();
    			String MESSAGE = m.get("MESSAGE").toString();
    			
    			query = "UPDATE dbo.MAEIL_DELIVERY_RETURN "
    					+ "SET SNDOK = '"+SNDOK+"', MESSAGE = '"+MESSAGE+"', "
    					+ "updated_datetime = getdate(), updated_by = 'SAP_Client' ";
    			if(SNDOK.equals("E")){
    				query += ", FAIL_COUNT = FAIL_COUNT+1 ";	
    			}
    			query += "WHERE SEQNO = '"+SEQNO+"' AND TKNUM = '"+TKNUM+"' "
						+ "AND TPNUM = '"+TPNUM+"' AND VBELN = '"+VBELN+"' "
						+ "AND POSNR = '"+POSNR+"'";
    			
	            System.out.println(query);
	            logger.info("query : " + query);
	            stmt.execute(query);
	            
            }
            stmt.close();
            conn.close();
            return true;
        } catch (SQLException sqle) {
            System.out.println("SQLException : " + sqle);
            
            logger.info("SQLException : " + sqle);
            logger.info("query : " + query);
            return false;
        }
    }
}