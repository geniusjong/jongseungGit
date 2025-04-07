import org.apache.log4j.Logger;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
/*import java.io.IOException;
import java.sql.ResultSet;*/
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;


import com.sap.conn.jco.AbapException;
//import com.sap.conn.jco.JCo;
import com.sap.conn.jco.JCoContext;
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoDestinationManager;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoField;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoFunctionTemplate;
import com.sap.conn.jco.JCoStructure;
import com.sap.conn.jco.JCoTable;

/**
 * basic examples for Java to ABAP communication  
 */
public class StepByStepClient
{
	static Logger logger = Logger.getLogger(StepByStepClient.class);
    
    
    static String ABAP_AS = "ABAP_AS_WITHOUT_POOL";
    static String ABAP_AS_POOLED = "ABAP_AS_WITH_POOL";
    static String ABAP_MS = "ABAP_MS_WITHOUT_POOL";

    /**
     * This example demonstrates the destination concept introduced with JCO 3.
     * The application does not deal with single connections anymore. Instead
     * it works with logical destinations like ABAP_AS and ABAP_MS which separates
     * the application logic from technical configuration.     
     * @throws JCoException
     */
    
    
    public static void step1Connect() throws JCoException
    {
        JCoDestination destination = JCoDestinationManager.getDestination(ABAP_AS);
        System.out.println("Attributes:");
        System.out.println(destination.getAttributes());
        System.out.println();

        destination = JCoDestinationManager.getDestination(ABAP_MS);
        System.out.println("Attributes:");
        System.out.println(destination.getAttributes());
        System.out.println();
    }
    
    /**
     * This example uses a connection pool. However, the implementation of
     * the application logic is still the same. Creation of pools and pool management
     * are handled by the JCo runtime. 
     * 
     * @throws JCoException
     */
    public static void step2ConnectUsingPool() throws JCoException
    {
        JCoDestination destination = JCoDestinationManager.getDestination(ABAP_AS_POOLED);
        destination.ping();
        System.out.println("Attributes:");
        System.out.println(destination.getAttributes());
        System.out.println();
    }
    
    /**
     * The following example executes a simple RFC function STFC_CONNECTION.
     * In contrast to JCo 2 you do not need to take care of repository management. 
     * JCo 3 manages the repository caches internally and shares the available
     * function metadata as much as possible. 
     * @throws JCoException
     */
    public static void step3SimpleCall() throws JCoException
    {
        //JCoDestination is the logic address of an ABAP system and ...
        JCoDestination destination = JCoDestinationManager.getDestination(ABAP_AS_POOLED);
        // ... it always has a reference to a metadata repository
        JCoFunction function = destination.getRepository().getFunction("STFC_CONNECTION");
        if(function == null)
            throw new RuntimeException("BAPI_COMPANYCODE_GETLIST not found in SAP.");

        //JCoFunction is container for function values. Each function contains separate
        //containers for import, export, changing and table parameters.
        //To set or get the parameters use the APIS setValue() and getXXX(). 
        function.getImportParameterList().setValue("REQUTEXT", "Hello SAP");
        
        try
        {
            //execute, i.e. send the function to the ABAP system addressed 
            //by the specified destination, which then returns the function result.
            //All necessary conversions between Java and ABAP data types
            //are done automatically.
            function.execute(destination);
        }
        catch(AbapException e)
        {
            System.out.println(e.toString());
            return;
        }
        
        System.out.println("STFC_CONNECTION finished:");
        System.out.println(" Echo: " + function.getExportParameterList().getString("ECHOTEXT"));
        System.out.println(" Response: " + function.getExportParameterList().getString("RESPTEXT"));
        System.out.println();
    }
    
    public static void step3SimpleCall2() throws JCoException, SQLException
    {
    	
    	/*JCoListMetaData importList = JCo.createListMetaData("IMPORTS");
		importList.add("E_MESSAGE", JCoMetaData.TYPE_CHAR, 120, 120, 0, null, null, JCoListMetaData.IMPORT_PARAMETER, null, null);
		importList.lock();*/
		
    	/*JCoListMetaData exportList = JCo.createListMetaData("EXPORTS");
		exportList.add("E_MESSAGE", JCoMetaData.TYPE_CHAR, 6, 0, 0, null, null, JCoListMetaData.EXPORT_PARAMETER, null, null);
		*/
    	List<Map<String, Object>> shipmentList = new ArrayList<>();
    	List<Map<String, Object>> deliveryList = new ArrayList<>();
    	try {
    		shipmentList = ClientDB.MAEIL_SHIPMENT_RETURN();
    		deliveryList = ClientDB.MAEIL_DELIVERY_RETURN();
		} catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        //JCoDestination is the logic address of an ABAP system and ...
        JCoDestination destination = JCoDestinationManager.getDestination(ABAP_AS_POOLED);
        // ... it always has a reference to a metadata repository
        JCoFunction function = destination.getRepository().getFunction("Z_SHIP_DELI_RETURN");
        if(function == null){
            throw new RuntimeException("Z_SHIP_DELI_RETURN not found in SAP.");
        }else{
        	System.out.println(function.getFunctionTemplate());
        }

       
        
        //JCoFunction is container for function values. Each function contains separate
        //containers for import, export, changing and table parameters.
        //To set or get the parameters use the APIS setValue() and getXXX(). 
        
        try
        {
        	
        	JCoTable myTable_T_3012 = function.getTableParameterList().getTable("T_3012");
        	JCoTable myTable_T_3013 = function.getTableParameterList().getTable("T_3013");
        	
        	/*table1.appendRow();
        	table1.setValue("WERKS", "1201");
        	table1.setValue("WADAT", "aa");*/
        	
        	System.out.println("myTable1.getNumRows():" + myTable_T_3012.getNumRows());
			System.out.println("myTable1.getRecordMetaData():" + myTable_T_3012.getRecordMetaData());
			
        	System.out.println("myTable2.getNumRows():" + myTable_T_3013.getNumRows());
			System.out.println("myTable2.getRecordMetaData():" + myTable_T_3013.getRecordMetaData());
			
			/*//JCoTable fields = function.getTableParameterList().getChar("MATNR");
			JCoFieldIterator iterator = fields.getFieldIterator();
			while (iterator.hasNextField()) {
				JCoField field = iterator.nextField();
				System.out.println("FieldValue: "+field.getString() +field.getName());
			}*/
			
			/*for (int i = 0; i < myTable_T_3012.getNumRows(); i++, myTable_T_3012.nextRow())
			{ 
				System.out.println("------------------------------------");
				System.out.println("getRow:"+myTable_T_3012.getRow());
				myTable_T_3012.setRow(i);
				System.out.println(" SEQNO: " + myTable_T_3012.getString("SEQNO"));
	            System.out.println(" TKNUM: " + myTable_T_3012.getString("TKNUM"));
			}*/
			
			logger.info("shipmentList.size():"+shipmentList.size());
			for (int i=0; i<shipmentList.size(); i++) {
				logger.info("shipmentList "+(i+1)+"번 데이터 전송");
    			Map<String, Object> m = shipmentList.get(i);
    			logger.info("SEQNO:"+m.get("SEQNO"));
    			logger.info("TKNUM:"+m.get("TKNUM"));
    			logger.info("TPNUM:"+m.get("TPNUM"));
    			logger.info("VBELN:"+m.get("VBELN"));
    			
    			
    			myTable_T_3012.appendRow();
    			myTable_T_3012.setValue("SEQNO", m.get("SEQNO"));
    			myTable_T_3012.setValue("TKNUM", m.get("TKNUM"));
    			myTable_T_3012.setValue("TPNUM", m.get("TPNUM"));
    			myTable_T_3012.setValue("VBELN", m.get("VBELN"));
    			myTable_T_3012.setValue("WERKS", m.get("WERKS"));
    			myTable_T_3012.setValue("VSTEL", m.get("VSTEL"));
    			myTable_T_3012.setValue("WADAT", m.get("WADAT"));
    			myTable_T_3012.setValue("WADAT_IST", m.get("WADAT_IST"));
    			myTable_T_3012.setValue("SNDOK", "");
    			myTable_T_3012.setValue("MESSAGE", "");
    		}
			
			logger.info("------------------------------------");
			logger.info("deliveryList.size():"+deliveryList.size());
			for (int i=0; i<deliveryList.size(); i++) {
    			//logger.info("deliveryList "+(i+1)+"번 데이터 전송");
    			Map<String, Object> m = deliveryList.get(i);
    			myTable_T_3013.appendRow();
    			logger.info("SEQNO:"+ m.get("SEQNO")+"/TKNUM:"+ m.get("TKNUM")+"/TPNUM:"+ m.get("TPNUM")+"/VBELN:"+ m.get("VBELN")+"/POSNR:"+ m.get("POSNR"));
    			logger.info("MATNR:"+ m.get("MATNR")+"/WERKS:"+ m.get("WERKS")+"/LGORT:"+ m.get("LGORT")+"/LFIMG1:"+ m.get("LFIMG1")+"/MEINS1:"+ m.get("MEINS1"));
/*    			logger.info("TKNUM:"+ m.get("TKNUM"));
    			logger.info("TPNUM:"+ m.get("TPNUM"));
    			logger.info("VBELN:"+ m.get("VBELN"));
    			logger.info("POSNR:"+ m.get("POSNR"));
    			logger.info("MATNR:"+ m.get("MATNR"));
    			logger.info("WERKS:"+ m.get("WERKS"));
    			logger.info("LGORT:"+ m.get("LGORT"));
    			logger.info("LFIMG1:"+ m.get("LFIMG1"));
    			logger.info("MEINS1:"+ m.get("MEINS1"));*/
    			
    			
    			myTable_T_3013.setValue("SEQNO", m.get("SEQNO"));
    			myTable_T_3013.setValue("TKNUM", m.get("TKNUM"));
    			myTable_T_3013.setValue("TPNUM", m.get("TPNUM"));
    			myTable_T_3013.setValue("VBELN", m.get("VBELN"));
    			myTable_T_3013.setValue("POSNR", m.get("POSNR"));
    			myTable_T_3013.setValue("MATNR", m.get("MATNR"));
    			myTable_T_3013.setValue("WERKS", m.get("WERKS"));
    			myTable_T_3013.setValue("LGORT", m.get("LGORT"));
    			myTable_T_3013.setValue("LFIMG", m.get("LFIMG1"));
    			myTable_T_3013.setValue("MEINS", m.get("MEINS1"));
    			myTable_T_3013.setValue("SNDOK", "");
    			myTable_T_3013.setValue("MESSAGE", "");
    		}
			
			function.execute(destination);
			
			logger.info("myTable.getNumRows2():" + myTable_T_3012.getNumRows());
			
			List<Map<String, Object>> reshipmentList = new ArrayList<>();
			//for (int i=0; i<myTable_T_3012.getNumRows(); i++) {
			for (int i = 0; i < myTable_T_3012.getNumRows(); i++, myTable_T_3012.nextRow())
			{ 
				logger.info("------------------------------------");
				logger.info("getRow reshipmentList:"+myTable_T_3012.getRow());
				myTable_T_3012.setRow(i);
					
				HashMap<String, Object> re_map = new HashMap<String, Object>();
				
    			logger.info(i+"번 데이터");
    			String SNDOK = myTable_T_3012.getString("SNDOK");
    			String SEQNO = myTable_T_3012.getString("SEQNO");
    			String TKNUM = myTable_T_3012.getString("TKNUM");
    			String TPNUM = myTable_T_3012.getString("TPNUM");
    			String VBELN = myTable_T_3012.getString("VBELN");
    			String MESSAGE = myTable_T_3012.getString("MESSAGE");
    			logger.info("SEQNO return:"+myTable_T_3012.getString("SEQNO"));
    			logger.info("TKNUM return:"+myTable_T_3012.getString("TKNUM"));
    			logger.info("SNDOK return:"+myTable_T_3012.getString("SNDOK"));
    			logger.info("MESSAGE return:"+myTable_T_3012.getString("MESSAGE"));
    			
    			
    			re_map.put("SEQNO", SEQNO);
    			re_map.put("TKNUM", TKNUM);
    			re_map.put("TPNUM", TPNUM);
    			re_map.put("VBELN", VBELN);
				re_map.put("SNDOK", SNDOK);
				re_map.put("MESSAGE", MESSAGE);
    			
    			reshipmentList.add(re_map);
    		}
			
			try {
				ClientDB.UPDATE_MAEIL_SHIPMENT_RETURN(reshipmentList);
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			logger.info("myTable.getNumRows3():" + myTable_T_3013.getNumRows());
			List<Map<String, Object>> reDeliveryList = new ArrayList<>();
			//for (int i=0; i<myTable_T_3013.getNumRows(); i++) {
			logger.info("myTable_T_3013.getNumRows():"+myTable_T_3013.getNumRows());
			for (int i = 0; i < myTable_T_3013.getNumRows(); i++, myTable_T_3013.nextRow())
			{ 
				HashMap<String, Object> re_map = new HashMap<String, Object>();
    			logger.info(i+"번 데이터");
    			myTable_T_3013.setRow(i);
    			String SEQNO = myTable_T_3013.getString("SEQNO");
    			String TKNUM = myTable_T_3013.getString("TKNUM");
    			String TPNUM = myTable_T_3013.getString("TPNUM");
    			String VBELN = myTable_T_3013.getString("VBELN");
    			String POSNR = myTable_T_3013.getString("POSNR");
    			String SNDOK = myTable_T_3013.getString("SNDOK");
    			String MESSAGE = myTable_T_3013.getString("MESSAGE");
    			
    			logger.info("return-SEQNO:"+myTable_T_3013.getString("SEQNO")
    			+" / TKNUM:"+myTable_T_3013.getString("TKNUM")+" / TPNUM:"+myTable_T_3013.getString("TPNUM")+
    			" / VBELN:"+myTable_T_3013.getString("VBELN")+" / POSNR:"+myTable_T_3013.getString("POSNR"));
    			logger.info("SNDOK return:"+myTable_T_3013.getString("SNDOK"));
    			logger.info("MESSAGE return:"+myTable_T_3013.getString("MESSAGE"));
    			
    			re_map.put("SEQNO", SEQNO);
    			re_map.put("TKNUM", TKNUM);
    			re_map.put("TPNUM", TPNUM);
    			re_map.put("VBELN", VBELN);
    			re_map.put("POSNR", POSNR);
				re_map.put("SNDOK", SNDOK);
				re_map.put("MESSAGE", MESSAGE);
    			
    			reDeliveryList.add(re_map);
    		}
			
			try {
				ClientDB.UPDATE_MAEIL_DELIVERY_RETURN(reDeliveryList);
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			

			String resultMsg = function.getExportParameterList().getString("E_MESSAGE");
			logger.info("---------- RETURN MESSAGE -----------");
			logger.info(resultMsg);
			
            
            //execute, i.e. send the function to the ABAP system addressed 
            //by the specified destination, which then returns the function result.
            //All necessary conversions between Java and ABAP data types
            //are done automatically.
            
        }
        catch(AbapException e)
        {
            System.out.println(e.toString());
            return;
        }
        
        logger.info("Z_SHIP_DELI_RETURN finished");
        /*System.out.println(" Echo: " + function.getExportParameterList().getString("ECHOTEXT"));
        System.out.println(" Response: " + function.getExportParameterList().getString("RESPTEXT"));*/
    }
    
    /**
     * ABAP APIs often uses complex parameters. This example demonstrates
     * how to read the values from a structure.  
     * @throws JCoException
     */
    public static void step3WorkWithStructure() throws JCoException
    {
        JCoDestination destination = JCoDestinationManager.getDestination(ABAP_AS_POOLED);
        JCoFunction function = destination.getRepository().getFunction("RFC_SYSTEM_INFO");
        if(function == null)
            throw new RuntimeException("RFC_SYSTEM_INFO not found in SAP.");

        try
        {
            function.execute(destination);
        }
        catch(AbapException e)
        {
            System.out.println(e.toString());
            return;
        }
        
        JCoStructure exportStructure = function.getExportParameterList().getStructure("RFCSI_EXPORT");
        System.out.println("System info for " + destination.getAttributes().getSystemID() + ":\n");

        //The structure contains some fields. The loop just prints out each field with its name.
        for(int i = 0; i < exportStructure.getMetaData().getFieldCount(); i++) 
        {
            System.out.println(exportStructure.getMetaData().getName(i) + ":\t" + exportStructure.getString(i));
        }
        System.out.println();
        
        //JCo still supports the JCoFields, but direct access via getXXX is more efficient as field iterator
        System.out.println("The same using field iterator: \nSystem info for " + destination.getAttributes().getSystemID() + ":\n");
        for(JCoField field : exportStructure)
        {
            System.out.println(field.getName() + ":\t" + field.getString());
        }
        System.out.println();
    }

    /**
     * A slightly more complex example than before. Query the companies list
     * returned in a table and then obtain more details for each company. 
     * @throws JCoException
     */
    public static void step4WorkWithTable() throws JCoException
    {
        JCoDestination destination = JCoDestinationManager.getDestination(ABAP_AS_POOLED);
        JCoFunction function = destination.getRepository().getFunction("BAPI_COMPANYCODE_GETLIST");
        if(function == null)
            throw new RuntimeException("BAPI_COMPANYCODE_GETLIST not found in SAP.");

        try
        {
            function.execute(destination);
        }
        catch(AbapException e)
        {
            System.out.println(e.toString());
            return;
        }
        
        JCoStructure returnStructure = function.getExportParameterList().getStructure("RETURN");
        if (! (returnStructure.getString("TYPE").equals("")||returnStructure.getString("TYPE").equals("S"))  )   
        {
           throw new RuntimeException(returnStructure.getString("MESSAGE"));
        }
        
        JCoTable codes = function.getTableParameterList().getTable("COMPANYCODE_LIST");
        for (int i = 0; i < codes.getNumRows(); i++) 
        {
            codes.setRow(i);
            System.out.println(codes.getString("COMP_CODE") + '\t' + codes.getString("COMP_NAME"));
        }

        //move the table cursor to first row
        codes.firstRow();
        for (int i = 0; i < codes.getNumRows(); i++, codes.nextRow()) 
        {
            function = destination.getRepository().getFunction("BAPI_COMPANYCODE_GETDETAIL");
            if (function == null) 
                throw new RuntimeException("BAPI_COMPANYCODE_GETDETAIL not found in SAP.");

            function.getImportParameterList().setValue("COMPANYCODEID", codes.getString("COMP_CODE"));
            
            //We do not need the addresses, so set the corresponding parameter to inactive.
            //Inactive parameters will be  either not generated or at least converted.  
            function.getExportParameterList().setActive("COMPANYCODE_ADDRESS",false);
            
            try
            {
                function.execute(destination);
            }
            catch (AbapException e)
            {
                System.out.println(e.toString());
                return;
            }

            returnStructure = function.getExportParameterList().getStructure("RETURN");
            if (! (returnStructure.getString("TYPE").equals("") ||
                   returnStructure.getString("TYPE").equals("S") ||
                   returnStructure.getString("TYPE").equals("W")) ) 
            {
                throw new RuntimeException(returnStructure.getString("MESSAGE"));
            }
            
            JCoStructure detail = function.getExportParameterList().getStructure("COMPANYCODE_DETAIL");
            
            System.out.println(detail.getString("COMP_CODE") + '\t' +
                               detail.getString("COUNTRY") + '\t' +
                               detail.getString("CITY"));
        }//for
    }
    
    /**
     * this example shows the "simple" stateful call sequence. Since all calls belonging to one
     * session are executed within the same thread, the application does not need
     * to take into account the SessionReferenceProvider. MultithreadedExample.java 
     * illustrates the more complex scenario, where the calls belonging to one session are 
     * executed in different threads.
     * 
     * Note: this example uses Z_GET_COUNTER and Z_INCREMENT_COUNTER. Most ABAP systems
     * contain function modules GET_COUNTER and INCREMENT_COUNTER that are not remote-enabled.
     * Copy these functions to Z_GET_COUNTER and Z_INCREMENT_COUNTER (or implement as wrapper)
     * and declare them to be remote enabled.
     * @throws JCoException
     */
    public static void step4SimpleStatefulCalls() throws JCoException
    {
        final JCoFunctionTemplate incrementCounterTemplate, getCounterTemplate;

        JCoDestination destination = JCoDestinationManager.getDestination(ABAP_MS);
        incrementCounterTemplate = destination.getRepository().getFunctionTemplate("Z_INCREMENT_COUNTER");
        getCounterTemplate = destination.getRepository().getFunctionTemplate("Z_GET_COUNTER");
        if(incrementCounterTemplate == null || getCounterTemplate == null)
            throw new RuntimeException("This example cannot run without Z_INCREMENT_COUNTER and Z_GET_COUNTER functions");
        
        final int threadCount = 5;
        final int loops = 5;
        final CountDownLatch startSignal = new CountDownLatch(threadCount);
        final CountDownLatch doneSignal = new CountDownLatch(threadCount);
        
        Runnable worker = new Runnable()
        {
            public void run()
            {
                startSignal.countDown();
                try
                {
                    //wait for other threads
                    startSignal.await();

                    JCoDestination dest = JCoDestinationManager.getDestination(ABAP_MS);
                    JCoContext.begin(dest);
                    try
                    {
                        for(int i=0; i < loops; i++)
                        {
                            JCoFunction incrementCounter = incrementCounterTemplate.getFunction();
                            incrementCounter.execute(dest);
                        }
                        JCoFunction getCounter = getCounterTemplate.getFunction();
                        getCounter.execute(dest);
                        
                        int remoteCounter = getCounter.getExportParameterList().getInt("GET_VALUE");
                        System.out.println("Thread-" + Thread.currentThread().getId() + 
                                " finished. Remote counter has " + (loops==remoteCounter?"correct":"wrong") + 
                                " value [" + remoteCounter + "]");
                    }
                    finally
                    {
                        JCoContext.end(dest);                    
                    }
                }
                catch(Exception e)
                {
                    System.out.println("Thread-" + Thread.currentThread().getId() + " ends with exception " + e.toString());
                }
                
                doneSignal.countDown();
            }
        };
        
        for(int i = 0; i < threadCount; i++)
        {
            new Thread(worker).start();
        }
        
        try
        {
            doneSignal.await();
        }
        catch(Exception e)
        {
        }
        
    }
    
    public void GetProperties(){
		
		FileReader resources = null;
		try {
			resources = new FileReader("DB.properties");
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} 
		Properties properties = new Properties(); 
		try { 
			properties.load(resources); 
			String driver = properties.getProperty("driver");
			String url = properties.getProperty("url");
			String database = properties.getProperty("database");
			String username = properties.getProperty("username");
			String password = properties.getProperty("password");
			

			System.out.println(properties.getProperty("username")); 
			System.out.println(properties.getProperty("password")); 
			System.out.println(properties.getProperty("url")); 
			
			ClientDB.connectionUrl = driver + url + ";database="+database;
			ClientDB.dbUserId = username;
			ClientDB.dbPassword = password;
		} 
		catch (IOException e) { 
			e.printStackTrace();
		}
    }
    
    public static void main(String[] args) throws JCoException, SQLException
    {
    	
		/*LOG.setLevel(Level.INFO);
		        
        LOG.severe("severe Log");
        LOG.warning("warning Log");
        LOG.info("info Log");*/
    	StepByStepClient sb = new StepByStepClient();
		sb.GetProperties();
        step1Connect();
        step2ConnectUsingPool();
        step3SimpleCall2();
        /*step4WorkWithTable();
        step4SimpleStatefulCalls();*/
    }
}
