//package com.sindoh.mpslinkage.controller;

//import java.util.ArrayList;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
//import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.sap.conn.jco.JCo;
import com.sap.conn.jco.JCoCustomRepository;
import com.sap.conn.jco.JCoDestinationManager;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoFunctionTemplate;
import com.sap.conn.jco.JCoListMetaData;
import com.sap.conn.jco.JCoMetaData;
import com.sap.conn.jco.JCoRecordMetaData;
import com.sap.conn.jco.JCoTable;
import com.sap.conn.jco.server.DefaultServerHandlerFactory;
import com.sap.conn.jco.server.JCoServer;
import com.sap.conn.jco.server.JCoServerContext;
import com.sap.conn.jco.server.JCoServerContextInfo;
import com.sap.conn.jco.server.JCoServerErrorListener;
import com.sap.conn.jco.server.JCoServerExceptionListener;
import com.sap.conn.jco.server.JCoServerFactory;
import com.sap.conn.jco.server.JCoServerFunctionHandler;
import com.sap.conn.jco.server.JCoServerState;
import com.sap.conn.jco.server.JCoServerStateChangedListener;

/*import com.sindoh.mpslinkage.service.StepByStepServerService;
import com.sindoh.utils.ExceptionCode;
*/
public class StepByStepServer {
	
	/*@Autowired
	private static StepByStepServerService serverService;*/
//	/private static final Logger logger = LoggerFactory.getLogger("MPSLinkage");
	
	static String SERVER_NAME1 = "SERVER";
	static String DESTINATION_NAME1 = "ABAP_AS_WITHOUT_POOL";
	static String DESTINATION_NAME2 = "ABAP_AS_WITH_POOL";
	static String reportFilePath;
	//static MyTIDHandler myTIDHandler = null;
	static Logger logger = Logger.getLogger(StepByStepServer.class);

	/**
	 * This class provides the implementation for the function STFC_CONNECTION.
	 * You will find the RFC-enabled function STFC_CONNECTION in almost any ABAP
	 * system. The function is pretty simple - it has 1 input parameter and 2
	 * output parameter. The content of the input parameter REQUTEXT is copied
	 * to the output parameter ECHOTEXT. The output parameter RESPTEXT is set to
	 * "Hello World".
	 */
	/*static class StfcConnectionHandler implements JCoServerFunctionHandler {
		public void handleRequest(JCoServerContext serverCtx, JCoFunction function) {

			logger.info("----------------------------------------------------------------");
			logger.info("call              : " + function.getName());
			logger.info("ConnectionId      : " + serverCtx.getConnectionID());
			logger.info("SessionId         : " + serverCtx.getSessionID());
			logger.info("TID               : " + serverCtx.getTID());
			logger.info("repository name   : " + serverCtx.getRepository().getName());
			logger.info("is in transaction : " + serverCtx.isInTransaction());
			logger.info("is stateful       : " + serverCtx.isStatefulSession());
			logger.info("----------------------------------------------------------------");
			logger.info("gwhost: " + serverCtx.getServer().getGatewayHost());
			logger.info("gwserv: " + serverCtx.getServer().getGatewayService());
			logger.info("progid: " + serverCtx.getServer().getProgramID());
			logger.info("----------------------------------------------------------------");
			logger.info("attributes  : ");
			logger.info(serverCtx.getConnectionAttributes().toString());
			logger.info("----------------------------------------------------------------");
			logger.info("CPIC conversation ID: " + serverCtx.getConnectionAttributes().getCPICConversationID());
			logger.info("----------------------------------------------------------------");
			logger.info("req text: " + function.getImportParameterList().getString("REQUTEXT"));
			function.getExportParameterList().setValue("ECHOTEXT",
					function.getImportParameterList().getString("REQUTEXT"));
			function.getExportParameterList().setValue("RESPTEXT", "Hello World");

			// In sample 3 (tRFC Server) we also set the status to executed:
			if (myTIDHandler != null)
				myTIDHandler.execute(serverCtx);
		}
	}*/

	/**
	 * First server example. At first we get an instance of the JCoServer
	 * through JCoServerFactory. The requested instance will be created, or an
	 * existing one will be returned if the instance was created before. It is
	 * not possible to run more then one instance with a particular
	 * configuration. Then we register the implementation for the function
	 * STFC_CONNECTION provided by class StfcConnectionHandler through
	 * FunctionHandlerFactory provided by JCo. You are free to write your own
	 * implementation JCoServerFunctionHandlerFactory, if you need more than
	 * simple mapping between function name and java class implementing the
	 * function. Now we can start the server instance. After a while the JCo
	 * runtime opens the server connections. You may check the server
	 * connections via sm59 or invoke STFC_CONNECTION via se37.
	 */
	/*static void step1SimpleServer() {
		JCoServer server;
		try {
			server = JCoServerFactory.getServer(SERVER_NAME1);
		} catch (JCoException ex) {
			throw new RuntimeException("Unable to create the server " + SERVER_NAME1 + " because of " + ex.getMessage(),
					ex);
		}

		JCoServerFunctionHandler stfcConnectionHandler = new StfcConnectionHandler();
		DefaultServerHandlerFactory.FunctionHandlerFactory factory = new DefaultServerHandlerFactory.FunctionHandlerFactory();
		factory.registerHandler("STFC_CONNECTION", stfcConnectionHandler);
		server.setCallHandlerFactory(factory);

		server.start();
		logger.info("The program can be stoped using <ctrl>+<c>");
	}*/

	static class MyThrowableListener implements JCoServerErrorListener, JCoServerExceptionListener {

		public void serverErrorOccurred(JCoServer jcoServer, String connectionId, JCoServerContextInfo serverCtx,
				Error error) {
			logger.info(">>> Error occured on " + jcoServer.getProgramID() + " connection " + connectionId);
			error.printStackTrace();
		}

		public void serverExceptionOccurred(JCoServer jcoServer, String connectionId, JCoServerContextInfo serverCtx,
				Exception error) {
			logger.info(">>> Error occured on " + jcoServer.getProgramID() + " connection " + connectionId);
			error.printStackTrace();
		}
	}

	static class MyStateChangedListener implements JCoServerStateChangedListener {
		public void serverStateChangeOccurred(JCoServer server, JCoServerState oldState, JCoServerState newState) {

			// Defined states are: STARTED, DEAD, ALIVE, STOPPED;
			// see JCoServerState class for details.
			// Details for connections managed by a server instance
			// are available via JCoServerMonitor.
			logger.info("Server state changed from " + oldState.toString() + " to " + newState.toString()
					+ " on server with program id " + server.getProgramID());
		}
	}

	/*static void step2SimpleServer() {
		JCoServer server;
		try {
			server = JCoServerFactory.getServer(SERVER_NAME1);
		} catch (JCoException ex) {
			throw new RuntimeException("Unable to create the server " + SERVER_NAME1 + " because of " + ex.getMessage(),
					ex);
		}

		JCoServerFunctionHandler stfcConnectionHandler = new StfcConnectionHandler();
		DefaultServerHandlerFactory.FunctionHandlerFactory factory = new DefaultServerHandlerFactory.FunctionHandlerFactory();
		factory.registerHandler("STFC_CONNECTION", stfcConnectionHandler);
		server.setCallHandlerFactory(factory);

		// additionally to step 1
		MyThrowableListener eListener = new MyThrowableListener();
		server.addServerErrorListener(eListener);
		server.addServerExceptionListener(eListener);

		MyStateChangedListener slistener = new MyStateChangedListener();
		server.addServerStateChangedListener(slistener);

		server.start();
		logger.info("The program can be stoped using <ctrl>+<c>");
	}
*/
	/*static class MyTIDHandler implements JCoServerTIDHandler {

		Map<String, TIDState> availableTIDs = new Hashtable<String, TIDState>();

		public boolean checkTID(JCoServerContext serverCtx, String tid) {
			// This example uses a Hashtable to store status information.
			// Normally, however,
			// you would use a database. If the DB is down throw a
			// RuntimeException at
			// this point. JCo will then abort the tRFC and the R/3 backend will
			// try
			// again later.

			logger.info("TID Handler: checkTID for " + tid);
			TIDState state = availableTIDs.get(tid);
			if (state == null) {
				availableTIDs.put(tid, TIDState.CREATED);
				return true;
			}

			if (state == TIDState.CREATED || state == TIDState.ROLLED_BACK)
				return true;

			return false;
			// "true" means that JCo will now execute the transaction, "false"
			// means
			// that we have already executed this transaction previously, so JCo
			// will
			// skip the handleRequest() step and will immediately return an OK
			// code to R/3.
		}

		public void commit(JCoServerContext serverCtx, String tid) {
			logger.info("TID Handler: commit for " + tid);

			// react on commit, e.g. commit on the database;
			// if necessary throw a RuntimeException, if the commit was not
			// possible
			availableTIDs.put(tid, TIDState.COMMITTED);
		}

		public void rollback(JCoServerContext serverCtx, String tid) {
			logger.info("TID Handler: rollback for " + tid);
			availableTIDs.put(tid, TIDState.ROLLED_BACK);

			// react on rollback, e.g. rollback on the database
		}

		public void confirmTID(JCoServerContext serverCtx, String tid) {
			logger.info("TID Handler: confirmTID for " + tid);

			try {
				// clean up the resources
			}
			// catch(Throwable t) {} //partner won't react on an exception at
			// this point
			finally {
				availableTIDs.remove(tid);
			}
		}

		public void execute(JCoServerContext serverCtx) {
			String tid = serverCtx.getTID();
			if (tid != null) {
				logger.info("TID Handler: execute for " + tid);
				availableTIDs.put(tid, TIDState.EXECUTED);
			}
		}

		private enum TIDState {
			CREATED, EXECUTED, COMMITTED, ROLLED_BACK, CONFIRMED;
		}
	}*/

	/**
	 * Follow server example demonstrates how to implement the support for tRFC
	 * calls, calls executed BACKGROUND TASK. At first we write am
	 * implementation for JCoServerTIDHandler interface. This implementation is
	 * registered by the server instance and will be used for each call send in
	 * "background task". Without such implementation JCo runtime deny any tRFC
	 * calls. See javadoc for interface JCoServerTIDHandler for details.
	 */
	/*static void step3SimpleTRfcServer() {
		JCoServer server;
		try {
			server = JCoServerFactory.getServer(SERVER_NAME1);
		} catch (JCoException ex) {
			throw new RuntimeException("Unable to create the server " + SERVER_NAME1 + " because of " + ex.getMessage(),
					ex);
		}

		JCoServerFunctionHandler stfcConnectionHandler = new StfcConnectionHandler();
		DefaultServerHandlerFactory.FunctionHandlerFactory factory = new DefaultServerHandlerFactory.FunctionHandlerFactory();
		factory.registerHandler("STFC_CONNECTION", stfcConnectionHandler);
		server.setCallHandlerFactory(factory);

		// additionally to step 1
		myTIDHandler = new MyTIDHandler();
		server.setTIDHandler(myTIDHandler);

		server.start();
		logger.info("The program can be stopped using <ctrl>+<c>");
	}
*/
	/**
	 * The following server example demonstrates how to develop a function
	 * module available only on Java side. At first we create the respective
	 * function meta data, because the function is not available in ABAP DDIC.
	 * Then the function meta data is stored in a custom repository which is
	 * registered with the server instance. Naturally we also need the
	 * implementation of the function - see the class SetTraceHandler.
	 * 
	 * Last but not least, the following ABAP report invokes the function module
	 * SetTraceHandler.
	 * 
	 * REPORT ZTEST_JCO_SET_TRACE.
	 * 
	 * DATA trace_level TYPE N. DATA trace_path TYPE STRING. DATA msg(255) TYPE
	 * C.
	 * 
	 * trace_level = '5'. trace_path = '.'.
	 * 
	 * CALL FUNCTION 'JCO_SET_TRACE' destination 'JCO_SERVER' EXPORTING
	 * TRACE_LEVEL = trace_level TRACE_PATH = trace_path EXCEPTIONS
	 * COMMUNICATION_FAILURE = 1 SYSTEM_FAILURE = 2 MESSAGE msg RESOURCE_FAILURE
	 * = 3 OTHERS = 4 . IF SY-SUBRC <> 0. write: 'ERROR: ', SY-SUBRC, msg.
	 * ENDIF.
	 */
	static void step4StaticRepository() {
		JCoCustomRepository cR = JCo.createCustomRepository("MyCustomRepository");
		JCoListMetaData impList = JCo.createListMetaData("IMPORTS");
		impList.add("TRACE_LEVEL", JCoMetaData.TYPE_NUM, 1, 2, 0, null, null, JCoListMetaData.IMPORT_PARAMETER, null,
				null);
		impList.add("TRACE_PATH", JCoMetaData.TYPE_STRING, 8, 8, 0, null, null, JCoListMetaData.IMPORT_PARAMETER, null,
				null);
		impList.lock();
		JCoFunctionTemplate fT = JCo.createFunctionTemplate("JCO_SET_TRACE", impList, null, null, null, null);
		cR.addFunctionTemplateToCache(fT);

		JCoServer server;
		try {
			server = JCoServerFactory.getServer(SERVER_NAME1);
		} catch (JCoException ex) {
			throw new RuntimeException("Unable to create the server " + SERVER_NAME1 + " because of " + ex.getMessage(),
					ex);
		}

		String repDest = server.getRepositoryDestination();
		if (repDest != null) {
			try {
				cR.setDestination(JCoDestinationManager.getDestination(repDest));
			} catch (JCoException e) {
				e.printStackTrace();
				logger.info(">>> repository contains static function definition only");
			}
		}
		server.setRepository(cR);

		JCoServerFunctionHandler setTraceHandler = new SetTraceHandler();
		DefaultServerHandlerFactory.FunctionHandlerFactory factory = new DefaultServerHandlerFactory.FunctionHandlerFactory();
		factory.registerHandler(fT.getName(), setTraceHandler);
		server.setCallHandlerFactory(factory);

		server.start();
		logger.info("The program can be stoped using <ctrl>+<c>");
	}

	static void step1DataRepository() {

		JCoCustomRepository cR = JCo.createCustomRepository("MyCustomRepository");
		JCoListMetaData impList = JCo.createListMetaData("TABLES");
		JCoRecordMetaData ZLEC3001 = JCo.createRecordMetaData("ZSDT1008", 1); // ZLES3001,ZSDT1008,
		
		ZLEC3001.add("VSTEL", JCoMetaData.TYPE_CHAR, 4, 0, 0, 0);
		ZLEC3001.add("MATNR", JCoMetaData.TYPE_CHAR, 18, 4, 0, 0);
		ZLEC3001.add("LFIMG", JCoMetaData.TYPE_CHAR, 13, 22, 0, 0);
		ZLEC3001.add("MEINS", JCoMetaData.TYPE_CHAR, 3, 35, 0, 0);
		ZLEC3001.add("VHILM", JCoMetaData.TYPE_CHAR, 18, 38, 0, 0);
		ZLEC3001.add("SNDOK", JCoMetaData.TYPE_CHAR, 1, 56, 0, 0);
		ZLEC3001.add("MESSAGE", JCoMetaData.TYPE_CHAR, 200, 57, 0, 0);
		ZLEC3001.setRecordLength(257, 514);
		logger.info("-getRecordMetaData-");
		logger.info(ZLEC3001.getRecordMetaData(0));
		ZLEC3001.lock();
		
		impList.add("T_DATA", JCoMetaData.TYPE_TABLE, ZLEC3001, 0);
		impList.lock();
		JCoFunctionTemplate fT = JCo.createFunctionTemplate("SDH_CRATE_PACK_MASTER_IF", null, null, null, impList, null);
		cR.addFunctionTemplateToCache(fT);
		
		
		JCoServer server;
		try {
			server = JCoServerFactory.getServer(SERVER_NAME1);
		} catch (JCoException ex) {
			throw new RuntimeException("Unable to create the server " + SERVER_NAME1 + " because of " + ex.getMessage(),
					ex);
		}

		String repDest = server.getRepositoryDestination();
		if (repDest != null) {
			try {
				cR.setDestination(JCoDestinationManager.getDestination(repDest));
			} catch (JCoException e) {
				e.printStackTrace();
				logger.info(">>> repository contains static function definition only");
			}
		}
		server.setRepository(cR);
		

		JCoServerFunctionHandler sdhCrateHandler = new CLS_SDH_CRATE_PACK_MASTER_IF();
		DefaultServerHandlerFactory.FunctionHandlerFactory factory = new DefaultServerHandlerFactory.FunctionHandlerFactory();
		factory.registerHandler(fT.getName(), sdhCrateHandler);
		server.setCallHandlerFactory(factory);
		
		

		server.start();
		logger.info("The program can be stoped using <ctrl>+<c>");
	}
	

	
	
	

	static class CLS_SDH_CRATE_PACK_MASTER_IF implements JCoServerFunctionHandler {
		
		public void handleRequest(JCoServerContext serverCtx, JCoFunction function) {
			logger.info("function.name():" + function.getName());
			
			JCoTable myTable = function.getTableParameterList().getTable("T_DATA");
			logger.info("myTable.getNumRows():" + myTable.getNumRows());
			
			logger.info("myTable.getRecordMetaData():" + myTable.getRecordMetaData());
			
			/*//JCoTable fields = function.getTableParameterList().getChar("MATNR");
			JCoFieldIterator iterator = fields.getFieldIterator();
			while (iterator.hasNextField()) {
				JCoField field = iterator.nextField();
				logger.info("FieldValue: "+field.getString() +field.getName());
			}*/
			
			for (int i = 0; i < myTable.getNumRows(); i++, myTable.nextRow())
			{ 
				logger.info("------------------------------------");
				logger.info("getRow:"+myTable.getRow());
				myTable.setRow(i);
				logger.info("isEmpty:"+myTable.isEmpty());
				logger.info("getFieldCount:"+myTable.getFieldCount());
				
				/*logger.info("출하 지점/입고 지점(VSTEL):"+myTable.getString("VSTEL"));
				logger.info("자재 번호(MATNR):"+myTable.getString("MATNR"));
				logger.info("실제수량납품 (판매단위)(LFIMG):"+myTable.getString("LFIMG"));
				logger.info("기본 단위(MEINS):"+myTable.getString("MEINS"));
				logger.info("포장재(VHILM):"+myTable.getString("VHILM"));
				*/
				String VSTEL = myTable.getString("VSTEL");
				String MATNR = myTable.getString("MATNR");
				String LFIMG = myTable.getString("LFIMG");
				String MEINS = myTable.getString("MEINS");
				String VHILM = myTable.getString("VHILM");
				
				logger.info("출하 지점/입고 지점(VSTEL):"+VSTEL);
				logger.info("자재 번호(MATNR):"+MATNR);
				logger.info("실제수량납품 (판매단위)(LFIMG):"+LFIMG);
				logger.info("기본 단위(MEINS):"+MEINS);
				logger.info("포장재(VHILM):"+VHILM);
				
				//여기서 DB insert로직
				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("VSTEL", myTable.getString("VSTEL"));
				map.put("MATNR", myTable.getString("MATNR"));
				map.put("LFIMG", myTable.getString("LFIMG"));
				map.put("MEINS", myTable.getString("MEINS"));
				map.put("VHILM", myTable.getString("VHILM"));
				/*map.put("SNDOK", "S");
				map.put("MESSAGE", "");*/
				
				/*String result = ExceptionCode.CODE_00;
				try{
					result = serverService.setCompany( map, result );	
					logger.info("{}", "------ 100% ------");
					
				}catch(Exception e){
					
					logger.error("{}, message:{}, map:{}", "ERROR", e.getMessage(), map.toString());
					result = ExceptionCode.CODE_90;
					
				}*/
				
				
				
				
				
				try {
					DB db = new DB();
					//logger.info("map:"+map);
					logger.info("trying to db connect");
					if(db.sdh_crate_pack_master_if(map)){
						logger.info("db return true");
						myTable.setValue("SNDOK","S");
						logger.info("myTable.setValue(\"SNDOK\",\"S\");");
						logger.info("myTable.toString():"+myTable.toString());
					}else{
						logger.info("db return false");
						myTable.setValue("SNDOK","E");
						logger.info("myTable.setValue(\"SNDOK\",\"E\");");
						myTable.setValue("MESSAGE","DB ERROR");
						logger.info("myTable.setValue(\"MESSAGE\",\"DB ERROR\");");
					}
				} catch (ClassNotFoundException e) {
					myTable.setValue("SNDOK","E");
					e.printStackTrace();
					logger.info("DB_SDH_CRATE_PACK_MASTER_IF insert failed");
				}
				
				
			}
			
			//JCoParameterList output = function.getExportParameterList();
			/*output.setValue("S","SNDOK");
			output.setValue("success","MESSAGE");*/
		}
	}
	
	static class CLS_SDH_EIS_MATERIAL_CODE implements JCoServerFunctionHandler {
		
		public void handleRequest(JCoServerContext serverCtx, JCoFunction function) {
			JCoTable myTable = function.getTableParameterList().getTable("T_DATA");
			logger.info("myTable.getNumRows():" + myTable.getNumRows());
			
			logger.info("myTable.getRecordMetaData():" + myTable.getRecordMetaData());
			
			
			for (int i = 0; i < myTable.getNumRows(); i++, myTable.nextRow())
			{ 
				logger.info("------------------------------------");
				logger.info("getRow:"+myTable.getRow());
				myTable.setRow(i);
				logger.info("isEmpty:"+myTable.isEmpty());
				logger.info("getFieldCount:"+myTable.getFieldCount());
				
				String ZLEV1 = myTable.getString("ZLEV1");
				String ZLEV2 = myTable.getString("ZLEV2");
				String ZLEV3 = myTable.getString("ZLEV3");
				String MATNR = myTable.getString("MATNR");
				String ZUSER = myTable.getString("ZUSER");
				String DATUM = myTable.getString("DATUM");
				String UZEIT = myTable.getString("UZEIT");
				String LVORM = myTable.getString("LVORM");
				//String SNDOK = myTable.getString("SNDOK");
				//String MESSAGE = myTable.getString("MESSAGE");
				
				
				
				logger.info("EIS 계층구조 1레벨 코드(ZLEV1):"+ZLEV1);//ZLEV1	v	CHAR	8		EIS 계층구조 1레벨 코드
				logger.info("EIS 계층구조 2레벨 코드(ZLEV2):"+ZLEV2);//ZLEV2	v	CHAR	8		EIS 계층구조 2레벨 코드
				logger.info("EIS 계층구조 3레벨 코드(ZLEV3):"+ZLEV3);//ZLEV3	v	CHAR	8		EIS 계층구조 3레벨 코드
				logger.info("자재 번호(MATNR):"+MATNR);//MATNR	v	CHAR	18		자재 번호
				logger.info("사용자이름(ZUSER):"+ZUSER);//ZUSER		CHAR	12		사용자이름
				logger.info("일자(DATUM):"+DATUM);//DATUM		CHAR	8		일자
				logger.info("시간(UZEIT):"+UZEIT);//UZEIT		CHAR	6		시간
				logger.info("삭제표시(LVORM):"+LVORM);//LVORM		CHAR	1		삭제표시
				//logger.info("자재 번호(SNDOK):"+SNDOK);//SNDOK		CHAR	1		성공 = 'S'  , 실패 = 'E' 
				//logger.info("자재 번호(MESSAGE):"+MESSAGE);//MESSAGE 		CHAR	200		ERROR MESSAGE
				
				
				
				//여기서 DB insert로직
				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("ZLEV1", myTable.getString("ZLEV1"));
				map.put("ZLEV2", myTable.getString("ZLEV2"));
				map.put("ZLEV3", myTable.getString("ZLEV3"));
				map.put("MATNR", myTable.getString("MATNR"));
				map.put("ZUSER", myTable.getString("ZUSER"));
				map.put("DATUM", myTable.getString("DATUM"));
				map.put("UZEIT", myTable.getString("UZEIT"));
				map.put("LVORM", myTable.getString("LVORM"));
				map.put("SNDOK", "S");
				map.put("MESSAGE", "");
				
				try {
					logger.info("trying to db connect");
					DB db = new DB();
					//logger.info("map:"+map);
					if(db.sdh_eis_material_code(map)){
						logger.info("db return true");
						myTable.setValue("SNDOK","S");
						logger.info("myTable.setValue(\"SNDOK\",\"S\");");
						logger.info("myTable.toString:"+myTable.toString());

					}else{
						logger.info("db return false");
						myTable.setValue("SNDOK","E");
						logger.info("myTable.setValue(\"SNDOK\",\"E\");");
						myTable.setValue("MESSAGE","Error occurred!");
						logger.info("myTable.setValue(\"MESSAGE\",\"DB ERROR\")");
					}
				} catch (ClassNotFoundException e) {
					myTable.setValue("SNDOK","E");
					e.printStackTrace();
					logger.info("DB_SDH_EIS_MATERIAL_CODE insert failed");
				}
			}
		}
	}
	
	
	static class SetTraceHandler implements JCoServerFunctionHandler {
		
		public void handleRequest(JCoServerContext serverCtx, JCoFunction function) {
			JCoTable myTable = function.getTableParameterList().getTable("T_DATA");
			logger.info("myTable.getNumRows():" + myTable.getNumRows());
			
			logger.info("myTable.getRecordMetaData():" + myTable.getRecordMetaData());
			
			/*//JCoTable fields = function.getTableParameterList().getChar("MATNR");
			JCoFieldIterator iterator = fields.getFieldIterator();
			while (iterator.hasNextField()) {
				JCoField field = iterator.nextField();
				logger.info("FieldValue: "+field.getString() +field.getName());
			}*/
			
			for (int i = 0; i < myTable.getNumRows(); i++, myTable.nextRow())
			{ 
				logger.info("------------------------------------");
				logger.info("getRow:"+myTable.getRow());
				myTable.setRow(i);
				logger.info("isEmpty:"+myTable.isEmpty());
				logger.info("getFieldCount:"+myTable.getFieldCount());
				
				/*logger.info("출하 지점/입고 지점(VSTEL):"+myTable.getString("VSTEL"));
				logger.info("자재 번호(MATNR):"+myTable.getString("MATNR"));
				logger.info("실제수량납품 (판매단위)(LFIMG):"+myTable.getString("LFIMG"));
				logger.info("기본 단위(MEINS):"+myTable.getString("MEINS"));
				logger.info("포장재(VHILM):"+myTable.getString("VHILM"));
				*/
				String VSTER = myTable.getString("VSTER");
				String MATNR = myTable.getString("MATNR");
				String LFIMG = myTable.getString("LFIMG");
				String MEINS = myTable.getString("MEINS");
				String VHILM = myTable.getString("VHILM");
				
				logger.info("출하 지점/입고 지점(VSTEL):"+VSTER);
				logger.info("자재 번호(MATNR):"+MATNR);
				logger.info("실제수량납품 (판매단위)(LFIMG):"+LFIMG);
				logger.info("기본 단위(MEINS):"+MEINS);
				logger.info("포장재(VHILM):"+VHILM);
				
				//여기서 DB insert로직
				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("VSTER", myTable.getString("VSTER"));
				map.put("MATNR", myTable.getString("MATNR"));
				map.put("LFIMG", myTable.getString("LFIMG"));
				map.put("MEINS", myTable.getString("MEINS"));
				map.put("VHILM", myTable.getString("VHILM"));
				
				/*String result = ExceptionCode.CODE_00;
				try{
					result = serverService.setCompany( map, result );	
					logger.info("{}", "------ 100% ------");
					
				}catch(Exception e){
					
					logger.error("{}, message:{}, map:{}", "ERROR", e.getMessage(), map.toString());
					result = ExceptionCode.CODE_90;
					
				}*/
				
				myTable.setValue("SNDOK","S");
				logger.info("myTable.setValue SNDOK S");
				logger.info("myTable.toString:"+myTable.toString());
			}
		}
	}
	
	static public void step7DataRepository() {
		logger.info("..step7DataRepository");
		JCoCustomRepository cR = JCo.createCustomRepository("MyCustomRepository");
		JCoListMetaData impList1 = JCo.createListMetaData("TABLES");
		JCoListMetaData impList2 = JCo.createListMetaData("TABLES");
		JCoListMetaData impList3 = JCo.createListMetaData("TABLES");
		JCoListMetaData impList4 = JCo.createListMetaData("TABLES");
		JCoListMetaData impList5 = JCo.createListMetaData("TABLES");
		JCoListMetaData impList6 = JCo.createListMetaData("TABLES");
		JCoListMetaData impList7 = JCo.createListMetaData("TABLES");
		
		JCoRecordMetaData ZLEC3001 = JCo.createRecordMetaData("ZSDT1008", 1); // ZLES3001,ZSDT1008,
		
		/*
		ZLEC3001.add("VSTEL", JCoMetaData.TYPE_CHAR, 4, 0, 0, 0);
		ZLEC3001.add("MATNR", JCoMetaData.TYPE_CHAR, 18, 4, 0, 0);
		ZLEC3001.add("LFIMG", JCoMetaData.TYPE_CHAR, 13, 22, 0, 0);
		ZLEC3001.add("MEINS", JCoMetaData.TYPE_CHAR, 3, 35, 0, 0);
		ZLEC3001.add("VHILM", JCoMetaData.TYPE_CHAR, 18, 38, 0, 0);
		ZLEC3001.add("SNDOK", JCoMetaData.TYPE_CHAR, 1, 56, 0, 0);
		ZLEC3001.add("MESSAGE", JCoMetaData.TYPE_CHAR, 200, 57, 0, 0);
		ZLEC3001.setRecordLength(257, 514);
		*/
		ZLEC3001.add("VSTEL" ,  JCoMetaData.TYPE_CHAR , 4 , 0 , 8 , 0 );
		ZLEC3001.add("MATNR" ,  JCoMetaData.TYPE_CHAR , 18 , 4 , 36 , 8 );
		ZLEC3001.add("LFIMG" ,  JCoMetaData.TYPE_CHAR , 13 , 22 , 26 , 44 );
		ZLEC3001.add("MEINS" ,  JCoMetaData.TYPE_CHAR , 3 , 35 , 6 , 70 );
		ZLEC3001.add("VHILM" ,  JCoMetaData.TYPE_CHAR , 18 , 38 , 36 , 76 );
		ZLEC3001.add("SNDOK" ,  JCoMetaData.TYPE_CHAR , 1 , 56 , 2 , 112 );
		ZLEC3001.add("MESSAGE" ,  JCoMetaData.TYPE_CHAR , 200 , 57 , 400 , 114 );
		ZLEC3001.setRecordLength(257, 514);
		ZLEC3001.lock();
		
		impList1.add("T_DATA", JCoMetaData.TYPE_TABLE, ZLEC3001, 0);
		impList1.lock();
		
		
		JCoRecordMetaData ZLEC3002 = JCo.createRecordMetaData("ZWMT0001", 1); // ZLES3001,ZSDT1008,
		/*
		ZLEC3002.add("MATNR", JCoMetaData.TYPE_CHAR, 18, 0, 0, 0);
		ZLEC3002.add("WERKS", JCoMetaData.TYPE_CHAR, 4, 18, 0, 0);
		ZLEC3002.add("MEINS", JCoMetaData.TYPE_CHAR, 3, 22, 0, 0);
		ZLEC3002.add("ZBOX", JCoMetaData.TYPE_CHAR, 3, 25, 0, 0);
		ZLEC3002.add("ZPAL", JCoMetaData.TYPE_CHAR, 3, 28, 0, 0);
		ZLEC3002.add("SNDOK", JCoMetaData.TYPE_CHAR, 1, 31, 0, 0);
		ZLEC3002.add("MESSAGE", JCoMetaData.TYPE_CHAR, 200, 32, 0, 0);
		ZLEC3002.setRecordLength(232, 464);
		*/
		ZLEC3002.add("MATNR" ,  JCoMetaData.TYPE_CHAR , 18 , 0 , 36 , 0 );
		ZLEC3002.add("WERKS" ,  JCoMetaData.TYPE_CHAR , 4 , 18 , 8 , 36 );
		ZLEC3002.add("MEINS" ,  JCoMetaData.TYPE_CHAR , 3 , 22 , 6 , 44 );
		ZLEC3002.add("ZBOX" ,  JCoMetaData.TYPE_CHAR , 3 , 25 , 6 , 50 );
		ZLEC3002.add("ZPAL" ,  JCoMetaData.TYPE_CHAR , 3 , 28 , 6 , 56 );
		ZLEC3002.add("SNDOK" ,  JCoMetaData.TYPE_CHAR , 1 , 31 , 2 , 62 );
		ZLEC3002.add("MESSAGE" ,  JCoMetaData.TYPE_CHAR , 200 , 32 , 400 , 64 );
		ZLEC3002.setRecordLength(232, 464);
		ZLEC3002.lock();
		
		impList2.add("T_DATA", JCoMetaData.TYPE_TABLE, ZLEC3002, 0);
		impList2.lock();
		
		JCoRecordMetaData ZLEC3003 = JCo.createRecordMetaData("ZMM1T0320", 1); // ZLES3001,ZSDT1008,
		/*
		ZLEC3003.add("ZLEV1", JCoMetaData.TYPE_CHAR, 8, 0, 0, 0);
		ZLEC3003.add("ZLEV2", JCoMetaData.TYPE_CHAR, 8, 8, 0, 0);
		ZLEC3003.add("ZLEV3", JCoMetaData.TYPE_CHAR, 8, 16, 0, 0);
		ZLEC3003.add("MATNR", JCoMetaData.TYPE_CHAR, 18, 24, 0, 0);
		ZLEC3003.add("ZUSER", JCoMetaData.TYPE_CHAR, 12, 42, 0, 0);
		ZLEC3003.add("DATUM", JCoMetaData.TYPE_CHAR, 8, 54, 0, 0);
		ZLEC3003.add("UZEIT", JCoMetaData.TYPE_CHAR, 6, 62, 0, 0);
		ZLEC3003.add("LVORM", JCoMetaData.TYPE_CHAR, 1, 68, 0, 0);
		ZLEC3003.add("SNDOK", JCoMetaData.TYPE_CHAR, 1, 69, 0, 0);
		ZLEC3003.add("MESSAGE", JCoMetaData.TYPE_CHAR, 200, 70, 0, 0);
		ZLEC3003.setRecordLength(270, 540);
		*/
		ZLEC3003.add("ZLEV1" ,  JCoMetaData.TYPE_CHAR , 8 , 0 , 16 , 0 );
		ZLEC3003.add("ZLEV2" ,  JCoMetaData.TYPE_CHAR , 8 , 8 , 16 , 16 );
		ZLEC3003.add("ZLEV3" ,  JCoMetaData.TYPE_CHAR , 8 , 16 , 16 , 32 );
		ZLEC3003.add("MATNR" ,  JCoMetaData.TYPE_CHAR , 18 , 24 , 36 , 48 );
		ZLEC3003.add("ZUSER" ,  JCoMetaData.TYPE_CHAR , 12 , 42 , 24 , 84 );
		ZLEC3003.add("DATUM" ,  JCoMetaData.TYPE_CHAR , 8 , 54 , 16 , 108 );
		ZLEC3003.add("UZEIT" ,  JCoMetaData.TYPE_CHAR , 6 , 62 , 12 , 124 );
		ZLEC3003.add("LVORM" ,  JCoMetaData.TYPE_CHAR , 1 , 68 , 2 , 136 );
		ZLEC3003.add("SNDOK" ,  JCoMetaData.TYPE_CHAR , 1 , 69 , 2 , 138 );
		ZLEC3003.add("MESSAGE" ,  JCoMetaData.TYPE_CHAR , 200 , 70 , 400 , 140 );
		ZLEC3003.setRecordLength(270, 540);
		ZLEC3003.lock();
		
		impList3.add("T_DATA", JCoMetaData.TYPE_TABLE, ZLEC3003, 0);
		impList3.lock();
		
		JCoRecordMetaData ZLEC3004 = JCo.createRecordMetaData("ZMM1T0310", 1); 
		/*
		ZLEC3004.add("ZLEVEL", JCoMetaData.TYPE_CHAR, 2, 0, 0, 0);
		ZLEC3004.add("ZCODE", JCoMetaData.TYPE_CHAR, 8, 2, 0, 0);
		ZLEC3004.add("ZCODE_NAME", JCoMetaData.TYPE_CHAR, 40, 10, 0, 0);
		ZLEC3004.add("ZUSER", JCoMetaData.TYPE_CHAR, 12, 50, 0, 0);
		ZLEC3004.add("DATUM", JCoMetaData.TYPE_CHAR, 8, 62, 0, 0);
		ZLEC3004.add("UZEIT", JCoMetaData.TYPE_CHAR, 6, 70, 0, 0);
		ZLEC3004.add("LVORM", JCoMetaData.TYPE_CHAR, 1, 76, 0, 0);
		ZLEC3004.add("SNDOK", JCoMetaData.TYPE_CHAR, 1, 77, 0, 0);
		ZLEC3004.add("MESSAGE", JCoMetaData.TYPE_CHAR, 200, 78, 0, 0);
		ZLEC3004.setRecordLength(278, 556);
		*/
		ZLEC3004.add("ZLEVEL" ,  JCoMetaData.TYPE_CHAR , 2 , 0 , 4 , 0 );
		ZLEC3004.add("ZCODE" ,  JCoMetaData.TYPE_CHAR , 8 , 2 , 16 , 4 );
		ZLEC3004.add("ZCODE_NAME" ,  JCoMetaData.TYPE_CHAR , 40 , 10 , 80 , 20 );
		ZLEC3004.add("ZUSER" ,  JCoMetaData.TYPE_CHAR , 12 , 50 , 24 , 100 );
		ZLEC3004.add("DATUM" ,  JCoMetaData.TYPE_CHAR , 8 , 62 , 16 , 124 );
		ZLEC3004.add("UZEIT" ,  JCoMetaData.TYPE_CHAR , 6 , 70 , 12 , 140 );
		ZLEC3004.add("LVORM" ,  JCoMetaData.TYPE_CHAR , 1 , 76 , 2 , 152 );
		ZLEC3004.add("SNDOK" ,  JCoMetaData.TYPE_CHAR , 1 , 77 , 2 , 154 );
		ZLEC3004.add("MESSAGE" ,  JCoMetaData.TYPE_CHAR , 200 , 78 , 400 , 156 );
		ZLEC3004.setRecordLength(278, 556);
		ZLEC3004.lock();
		
		impList4.add("T_DATA", JCoMetaData.TYPE_TABLE, ZLEC3004, 0);
		impList4.lock();
		
		JCoRecordMetaData ZLEC3005 = JCo.createRecordMetaData("ZUSDT01", 1); 
		/*
		ZLEC3005.add("WERKS", JCoMetaData.TYPE_CHAR, 4, 0, 0, 0);
		ZLEC3005.add("LGORT", JCoMetaData.TYPE_CHAR, 4, 4, 0, 0);
		ZLEC3005.add("MATNR", JCoMetaData.TYPE_CHAR, 18, 8, 0, 0);
		ZLEC3005.add("MAKTX", JCoMetaData.TYPE_CHAR, 40, 26, 0, 0);
		ZLEC3005.add("ZSORT", JCoMetaData.TYPE_CHAR, 4, 66, 0, 0);
		ZLEC3005.add("LVORM", JCoMetaData.TYPE_CHAR, 1, 70, 0, 0);
		ZLEC3005.add("SNDOK", JCoMetaData.TYPE_CHAR, 1, 71, 0, 0);
		ZLEC3005.add("MESSAGE", JCoMetaData.TYPE_CHAR, 200, 72, 0, 0);
		ZLEC3005.setRecordLength(272, 544);
		*/
		ZLEC3005.add("WERKS" ,  JCoMetaData.TYPE_CHAR , 4 , 0 , 8 , 0 );
		ZLEC3005.add("LGORT" ,  JCoMetaData.TYPE_CHAR , 4 , 4 , 8 , 8 );
		ZLEC3005.add("MATNR" ,  JCoMetaData.TYPE_CHAR , 18 , 8 , 36 , 16 );
		ZLEC3005.add("MAKTX" ,  JCoMetaData.TYPE_CHAR , 40 , 26 , 80 , 52 );
		ZLEC3005.add("ZSORT" ,  JCoMetaData.TYPE_CHAR , 4 , 66 , 8 , 132 );
		ZLEC3005.add("LVORM" ,  JCoMetaData.TYPE_CHAR , 1 , 70 , 2 , 140 );
		ZLEC3005.add("SNDOK" ,  JCoMetaData.TYPE_CHAR , 1 , 71 , 2 , 142 );
		ZLEC3005.add("MESSAGE" ,  JCoMetaData.TYPE_CHAR , 200 , 72 , 400 , 144 );
		ZLEC3005.setRecordLength(272, 544);
		ZLEC3005.lock();
		
		impList5.add("T_DATA", JCoMetaData.TYPE_TABLE, ZLEC3005, 0);
		impList5.lock();
		
		JCoRecordMetaData ZLEC3006 = JCo.createRecordMetaData("MARM", 1);
		/*
		ZLEC3006.add("MATNR", JCoMetaData.TYPE_CHAR, 18, 0, 0, 0);
		ZLEC3006.add("MEINH", JCoMetaData.TYPE_CHAR, 3, 18, 0, 0);
		ZLEC3006.add("UMREZ", JCoMetaData.TYPE_CHAR, 5, 21, 0, 0);
		ZLEC3006.add("UMREN", JCoMetaData.TYPE_CHAR, 5, 26, 0, 0);
		ZLEC3006.add("EAN11", JCoMetaData.TYPE_CHAR, 18, 31, 0, 0);
		ZLEC3006.add("LVORM", JCoMetaData.TYPE_CHAR, 1, 49, 0, 0);
		ZLEC3006.add("SNDOK", JCoMetaData.TYPE_CHAR, 1, 50, 0, 0);
		ZLEC3006.add("MESSAGE", JCoMetaData.TYPE_CHAR, 200, 51, 0, 0);
		ZLEC3006.setRecordLength(251, 502);
		*/
		ZLEC3006.add("MATNR" ,  JCoMetaData.TYPE_CHAR , 18 , 0 , 36 , 0 );
		ZLEC3006.add("MEINH" ,  JCoMetaData.TYPE_CHAR , 3 , 18 , 6 , 36 );
		ZLEC3006.add("UMREZ" ,  JCoMetaData.TYPE_CHAR , 5 , 21 , 10 , 42 );
		ZLEC3006.add("UMREN" ,  JCoMetaData.TYPE_CHAR , 5 , 26 , 10 , 52 );
		ZLEC3006.add("EAN11" ,  JCoMetaData.TYPE_CHAR , 18 , 31 , 36 , 62 );
		ZLEC3006.add("LVORM" ,  JCoMetaData.TYPE_CHAR , 1 , 49 , 2 , 98 );
		ZLEC3006.add("SNDOK" ,  JCoMetaData.TYPE_CHAR , 1 , 50 , 2 , 100 );
		ZLEC3006.add("MESSAGE" ,  JCoMetaData.TYPE_CHAR , 200 , 51 , 400 , 102 );
		ZLEC3006.setRecordLength(251, 502);
		ZLEC3006.lock();
		
		impList6.add("T_DATA", JCoMetaData.TYPE_TABLE, ZLEC3006, 0);
		impList6.lock();
		
		JCoListMetaData importList = JCo.createListMetaData("IMPORTS");
		JCoListMetaData exportList = JCo.createListMetaData("EXPORTS");
		exportList.add("TKNUM_CNT_R", JCoMetaData.TYPE_CHAR, 6, 12, 0, null, null, JCoListMetaData.EXPORT_PARAMETER, null, null);
		exportList.add("VBELN_CNT_R", JCoMetaData.TYPE_CHAR, 6, 12, 0, null, null, JCoListMetaData.EXPORT_PARAMETER, null, null);
		//exportList.add("VBELN_CNT_R", JCoMetaData.TYPE_CHAR, 6, 12, 0, null, null, JCoListMetaData.EXPORT_PARAMETER, null, null);
		exportList.lock();
		importList.add("TKNUM_CNT", JCoMetaData.TYPE_CHAR, 6, 12, 0, null, null, JCoListMetaData.IMPORT_PARAMETER, null, null);
		importList.add("VBELN_CNT", JCoMetaData.TYPE_CHAR, 6, 12, 0, null, null, JCoListMetaData.IMPORT_PARAMETER, null, null);
		importList.lock();

		JCoRecordMetaData ZLEU3010 = JCo.createRecordMetaData("ZLES3010", 1); // t_3010,ZSDT1008,
		JCoRecordMetaData ZLEU3011 = JCo.createRecordMetaData("ZLES3011", 1); // t_3010,ZSDT1008,
		/*
		ZLEU3010.add("SEQNO", JCoMetaData.TYPE_CHAR, 12, 0, 0, 0);
		ZLEU3010.add("TKNUM", JCoMetaData.TYPE_CHAR, 10, 12, 0, 0);
		ZLEU3010.add("SHTYP", JCoMetaData.TYPE_CHAR, 4, 22, 0, 0);
		ZLEU3010.add("TPLST", JCoMetaData.TYPE_CHAR, 4, 26, 0, 0);
		ZLEU3010.add("BEZEI1", JCoMetaData.TYPE_CHAR, 20, 30, 0, 0);
		ZLEU3010.add("DTDIS", JCoMetaData.TYPE_CHAR, 8, 50, 0, 0);
		ZLEU3010.add("STDIS", JCoMetaData.TYPE_CHAR, 1, 58, 0, 0);
		ZLEU3010.add("ROUTE", JCoMetaData.TYPE_CHAR, 6, 59, 0, 0);
		ZLEU3010.add("BEZEI2", JCoMetaData.TYPE_CHAR, 40, 65, 0, 0);
		ZLEU3010.add("TDLNR", JCoMetaData.TYPE_CHAR, 10, 105, 0, 0);		
		ZLEU3010.add("NAME1", JCoMetaData.TYPE_CHAR, 35, 115, 0, 0);
		ZLEU3010.add("TELF1", JCoMetaData.TYPE_CHAR, 16, 150, 0, 0);
		ZLEU3010.add("TELF2", JCoMetaData.TYPE_CHAR, 16, 166, 0, 0);
		ZLEU3010.add("DPABF", JCoMetaData.TYPE_CHAR, 8, 182, 0, 0);
		ZLEU3010.add("UPDKZ", JCoMetaData.TYPE_CHAR, 1, 190, 0, 0);
		ZLEU3010.add("SNDOK", JCoMetaData.TYPE_CHAR, 1, 191, 0, 0);
		ZLEU3010.add("MESSAGE", JCoMetaData.TYPE_CHAR, 200, 192, 0, 0);
		ZLEU3010.setRecordLength(257, 514);
		*/

		ZLEU3010.add("SEQNO" ,  JCoMetaData.TYPE_CHAR , 12 , 0 , 24 , 0 );
		ZLEU3010.add("TKNUM" ,  JCoMetaData.TYPE_CHAR , 10 , 12 , 20 , 24 );
		ZLEU3010.add("SHTYP" ,  JCoMetaData.TYPE_CHAR , 4 , 22 , 8 , 44 );
		ZLEU3010.add("TPLST" ,  JCoMetaData.TYPE_CHAR , 4 , 26 , 8 , 52 );
		ZLEU3010.add("BEZEI1" ,  JCoMetaData.TYPE_CHAR , 20 , 30 , 40 , 60 );
		ZLEU3010.add("DTDIS" ,  JCoMetaData.TYPE_CHAR , 8 , 50 , 16 , 100 );
		ZLEU3010.add("STDIS" ,  JCoMetaData.TYPE_CHAR , 1 , 58 , 2 , 116 );
		ZLEU3010.add("ROUTE" ,  JCoMetaData.TYPE_CHAR , 6 , 59 , 12 , 118 );
		ZLEU3010.add("BEZEI2" ,  JCoMetaData.TYPE_CHAR , 40 , 65 , 80 , 130 );
		ZLEU3010.add("TDLNR" ,  JCoMetaData.TYPE_CHAR , 10 , 105 , 20 , 210 );
		ZLEU3010.add("NAME1" ,  JCoMetaData.TYPE_CHAR , 35 , 115 , 70 , 230 );
		ZLEU3010.add("TELF1" ,  JCoMetaData.TYPE_CHAR , 16 , 150 , 32 , 300 );
		ZLEU3010.add("TELF2" ,  JCoMetaData.TYPE_CHAR , 16 , 166 , 32 , 332 );
		ZLEU3010.add("DPABF" ,  JCoMetaData.TYPE_CHAR , 8 , 182 , 16 , 364 );
		ZLEU3010.add("UPDKZ" ,  JCoMetaData.TYPE_CHAR , 1 , 190 , 2 , 380 );
		ZLEU3010.add("SNDOK" ,  JCoMetaData.TYPE_CHAR , 1 , 191 , 2 , 382 );
		ZLEU3010.add("MESSAGE" ,  JCoMetaData.TYPE_CHAR , 200 , 192 , 400 , 384 );
		ZLEU3010.setRecordLength(392, 784);
		ZLEU3010.lock();
		
		impList7.add("T_3010", JCoMetaData.TYPE_TABLE, ZLEU3010, 0);
		/*
		ZLEU3011.add("SEQNO", JCoMetaData.TYPE_CHAR, 12, 0, 0, 0);
		ZLEU3011.add("TKNUM", JCoMetaData.TYPE_CHAR, 10, 12, 0, 0);
		ZLEU3011.add("TPNUM", JCoMetaData.TYPE_CHAR, 4, 22, 0, 0);
		ZLEU3011.add("VBELN", JCoMetaData.TYPE_CHAR, 10, 26, 0, 0);
		ZLEU3011.add("POSNR", JCoMetaData.TYPE_CHAR, 6, 36, 0, 0);
		ZLEU3011.add("MATNR", JCoMetaData.TYPE_CHAR, 18, 42, 0, 0);
		ZLEU3011.add("ARKTX", JCoMetaData.TYPE_CHAR, 40, 60, 0, 0);
		ZLEU3011.add("WERKS", JCoMetaData.TYPE_CHAR, 4, 100, 0, 0);
		ZLEU3011.add("LGORT", JCoMetaData.TYPE_CHAR, 4, 104, 0, 0);
		ZLEU3011.add("LFIMG1", JCoMetaData.TYPE_CHAR, 13, 108, 0, 0);
		
		ZLEU3011.add("MEINS1", JCoMetaData.TYPE_CHAR, 3, 121, 0, 0);
		ZLEU3011.add("NTGEW1", JCoMetaData.TYPE_CHAR, 13, 124, 0, 0);
		ZLEU3011.add("BRGEW1", JCoMetaData.TYPE_CHAR, 13, 137, 0, 0);
		ZLEU3011.add("GEWEI1", JCoMetaData.TYPE_CHAR, 3, 150, 0, 0);
		ZLEU3011.add("VOLUM1", JCoMetaData.TYPE_CHAR, 13, 153, 0, 0);
		ZLEU3011.add("VOLEH1", JCoMetaData.TYPE_CHAR, 3, 166, 0, 0);
		ZLEU3011.add("MEINH", JCoMetaData.TYPE_CHAR, 3, 169, 0, 0);
		ZLEU3011.add("UMREZ", JCoMetaData.TYPE_CHAR, 5, 172, 0, 0);
		ZLEU3011.add("UMREN", JCoMetaData.TYPE_CHAR, 5, 177, 0, 0);
		ZLEU3011.add("BOX_LFIMG", JCoMetaData.TYPE_CHAR, 13, 182, 0, 0);
		
		ZLEU3011.add("BOX_EA_LFIMG", JCoMetaData.TYPE_CHAR, 13, 195, 0, 0);
		ZLEU3011.add("LFART", JCoMetaData.TYPE_CHAR, 4, 208, 0, 0);
		ZLEU3011.add("VSTEL", JCoMetaData.TYPE_CHAR, 4, 212, 0, 0);
		ZLEU3011.add("VTEXT", JCoMetaData.TYPE_CHAR, 30, 216, 0, 0);
		ZLEU3011.add("VKORG", JCoMetaData.TYPE_CHAR, 4, 246, 0, 0);
		ZLEU3011.add("WADAT", JCoMetaData.TYPE_CHAR, 8, 250, 0, 0);
		ZLEU3011.add("ZTIME", JCoMetaData.TYPE_CHAR, 6, 258, 0, 0);
		ZLEU3011.add("LFDAT", JCoMetaData.TYPE_CHAR, 8, 264, 0, 0);
		ZLEU3011.add("ROUTE", JCoMetaData.TYPE_CHAR, 6, 272, 0, 0);
		ZLEU3011.add("BEZEI1", JCoMetaData.TYPE_CHAR, 40, 278, 0, 0);
		
		ZLEU3011.add("KUNNR", JCoMetaData.TYPE_CHAR, 10, 318, 0, 0);
		ZLEU3011.add("NAME1", JCoMetaData.TYPE_CHAR, 35, 328, 0, 0);
		ZLEU3011.add("VKBUR", JCoMetaData.TYPE_CHAR, 4, 363, 0, 0);
		ZLEU3011.add("BEZEI2", JCoMetaData.TYPE_CHAR, 20, 367, 0, 0);
		ZLEU3011.add("J_1KFREPRE", JCoMetaData.TYPE_CHAR, 10, 387, 0, 0);
		ZLEU3011.add("TELF1", JCoMetaData.TYPE_CHAR, 16, 397, 0, 0);
		ZLEU3011.add("TELF2", JCoMetaData.TYPE_CHAR, 16, 413, 0, 0);
		ZLEU3011.add("ORT01", JCoMetaData.TYPE_CHAR, 35, 429, 0, 0);
		ZLEU3011.add("STRAS", JCoMetaData.TYPE_CHAR, 35, 464, 0, 0);
		ZLEU3011.add("KUNAG", JCoMetaData.TYPE_CHAR, 10, 499, 0, 0);
		
		ZLEU3011.add("NAME2", JCoMetaData.TYPE_CHAR, 35, 509, 0, 0);
		ZLEU3011.add("BTGEW2", JCoMetaData.TYPE_CHAR, 15, 544, 0, 0);
		ZLEU3011.add("GEWEI2", JCoMetaData.TYPE_CHAR, 3, 559, 0, 0);
		ZLEU3011.add("NTGEW2", JCoMetaData.TYPE_CHAR, 15, 562, 0, 0);
		ZLEU3011.add("VOLUM2", JCoMetaData.TYPE_CHAR, 15, 577, 0, 0);
		ZLEU3011.add("VOLEH2", JCoMetaData.TYPE_CHAR, 3, 592, 0, 0);
		ZLEU3011.add("UPDKZ", JCoMetaData.TYPE_CHAR, 1, 595, 0, 0);
		ZLEU3011.add("SNDOK", JCoMetaData.TYPE_CHAR, 1, 596, 0, 0);
		ZLEU3011.add("MESSAGE", JCoMetaData.TYPE_CHAR, 200, 597, 0, 0);
		ZLEU3011.setRecordLength(797, 1594);
		*/
		ZLEU3011.add("SEQNO" ,  JCoMetaData.TYPE_CHAR , 12 , 0 , 24 , 0 );
		ZLEU3011.add("TKNUM" ,  JCoMetaData.TYPE_CHAR , 10 , 12 , 20 , 24 );
		ZLEU3011.add("TPNUM" ,  JCoMetaData.TYPE_CHAR , 4 , 22 , 8 , 44 );
		ZLEU3011.add("VBELN" ,  JCoMetaData.TYPE_CHAR , 10 , 26 , 20 , 52 );
		ZLEU3011.add("POSNR" ,  JCoMetaData.TYPE_CHAR , 6 , 36 , 12 , 72 );
		ZLEU3011.add("MATNR" ,  JCoMetaData.TYPE_CHAR , 18 , 42 , 36 , 84 );
		ZLEU3011.add("ARKTX" ,  JCoMetaData.TYPE_CHAR , 40 , 60 , 80 , 120 );
		ZLEU3011.add("WERKS" ,  JCoMetaData.TYPE_CHAR , 4 , 100 , 8 , 200 );
		ZLEU3011.add("LGORT" ,  JCoMetaData.TYPE_CHAR , 4 , 104 , 8 , 208 );
		ZLEU3011.add("LFIMG1" ,  JCoMetaData.TYPE_CHAR , 13 , 108 , 26 , 216 );
		ZLEU3011.add("MEINS1" ,  JCoMetaData.TYPE_CHAR , 3 , 121 , 6 , 242 );
		ZLEU3011.add("NTGEW1" ,  JCoMetaData.TYPE_CHAR , 13 , 124 , 26 , 248 );
		ZLEU3011.add("BRGEW1" ,  JCoMetaData.TYPE_CHAR , 13 , 137 , 26 , 274 );
		ZLEU3011.add("GEWEI1" ,  JCoMetaData.TYPE_CHAR , 3 , 150 , 6 , 300 );
		ZLEU3011.add("VOLUM1" ,  JCoMetaData.TYPE_CHAR , 13 , 153 , 26 , 306 );
		ZLEU3011.add("VOLEH1" ,  JCoMetaData.TYPE_CHAR , 3 , 166 , 6 , 332 );
		ZLEU3011.add("MEINH" ,  JCoMetaData.TYPE_CHAR , 3 , 169 , 6 , 338 );
		ZLEU3011.add("UMREZ" ,  JCoMetaData.TYPE_CHAR , 5 , 172 , 10 , 344 );
		ZLEU3011.add("UMREN" ,  JCoMetaData.TYPE_CHAR , 5 , 177 , 10 , 354 );
		ZLEU3011.add("BOX_LFIMG" ,  JCoMetaData.TYPE_CHAR , 13 , 182 , 26 , 364 );
		ZLEU3011.add("BOX_EA_LFIMG" ,  JCoMetaData.TYPE_CHAR , 13 , 195 , 26 , 390 );
		ZLEU3011.add("LFART" ,  JCoMetaData.TYPE_CHAR , 4 , 208 , 8 , 416 );
		ZLEU3011.add("VSTEL" ,  JCoMetaData.TYPE_CHAR , 4 , 212 , 8 , 424 );
		ZLEU3011.add("VTEXT" ,  JCoMetaData.TYPE_CHAR , 30 , 216 , 60 , 432 );
		ZLEU3011.add("VKORG" ,  JCoMetaData.TYPE_CHAR , 4 , 246 , 8 , 492 );
		ZLEU3011.add("WADAT" ,  JCoMetaData.TYPE_CHAR , 8 , 250 , 16 , 500 );
		ZLEU3011.add("ZTIME" ,  JCoMetaData.TYPE_CHAR , 6 , 258 , 12 , 516 );
		ZLEU3011.add("LFDAT" ,  JCoMetaData.TYPE_CHAR , 8 , 264 , 16 , 528 );
		ZLEU3011.add("ROUTE" ,  JCoMetaData.TYPE_CHAR , 6 , 272 , 12 , 544 );
		ZLEU3011.add("BEZEI1" ,  JCoMetaData.TYPE_CHAR , 40 , 278 , 80 , 556 );
		ZLEU3011.add("KUNNR" ,  JCoMetaData.TYPE_CHAR , 10 , 318 , 20 , 636 );
		ZLEU3011.add("NAME1" ,  JCoMetaData.TYPE_CHAR , 35 , 328 , 70 , 656 );
		ZLEU3011.add("VKBUR" ,  JCoMetaData.TYPE_CHAR , 4 , 363 , 8 , 726 );
		ZLEU3011.add("BEZEI2" ,  JCoMetaData.TYPE_CHAR , 20 , 367 , 40 , 734 );
		ZLEU3011.add("J_1KFREPRE" ,  JCoMetaData.TYPE_CHAR , 10 , 387 , 20 , 774 );
		ZLEU3011.add("TELF1" ,  JCoMetaData.TYPE_CHAR , 16 , 397 , 32 , 794 );
		ZLEU3011.add("TELF2" ,  JCoMetaData.TYPE_CHAR , 16 , 413 , 32 , 826 );
		ZLEU3011.add("ORT01" ,  JCoMetaData.TYPE_CHAR , 35 , 429 , 70 , 858 );
		ZLEU3011.add("STRAS" ,  JCoMetaData.TYPE_CHAR , 35 , 464 , 70 , 928 );
		ZLEU3011.add("KUNAG" ,  JCoMetaData.TYPE_CHAR , 10 , 499 , 20 , 998 );
		ZLEU3011.add("NAME2" ,  JCoMetaData.TYPE_CHAR , 35 , 509 , 70 , 1018 );
		ZLEU3011.add("BTGEW2" ,  JCoMetaData.TYPE_CHAR , 15 , 544 , 30 , 1088 );
		ZLEU3011.add("GEWEI2" ,  JCoMetaData.TYPE_CHAR , 3 , 559 , 6 , 1118 );
		ZLEU3011.add("NTGEW2" ,  JCoMetaData.TYPE_CHAR , 15 , 562 , 30 , 1124 );
		ZLEU3011.add("VOLUM2" ,  JCoMetaData.TYPE_CHAR , 15 , 577 , 30 , 1154 );
		ZLEU3011.add("VOLEH2" ,  JCoMetaData.TYPE_CHAR , 3 , 592 , 6 , 1184 );
		ZLEU3011.add("UPDKZ" ,  JCoMetaData.TYPE_CHAR , 1 , 595 , 2 , 1190 );
		ZLEU3011.add("SNDOK" ,  JCoMetaData.TYPE_CHAR , 1 , 596 , 2 , 1192 );
		ZLEU3011.add("MESSAGE" ,  JCoMetaData.TYPE_CHAR , 200 , 597 , 400 , 1194 );
		ZLEU3011.setRecordLength(797, 1594);
		
		impList7.add("T_3011", JCoMetaData.TYPE_TABLE, ZLEU3011, 0);
		impList7.lock();
			
		JCoFunctionTemplate fT1 = JCo.createFunctionTemplate("SDH_CRATE_PACK_MASTER_IF", null, null, null, impList1, null);
		JCoFunctionTemplate fT2 = JCo.createFunctionTemplate("SDH_MATERIAL_MAINT_IF_ST", null, null, null, impList2, null);
		JCoFunctionTemplate fT3 = JCo.createFunctionTemplate("SDH_EIS_MATERIAL_CODE", null, null, null, impList3, null);
		JCoFunctionTemplate fT4 = JCo.createFunctionTemplate("SDH_EIS_LEVEL_CODE", null, null, null, impList4, null);
		JCoFunctionTemplate fT5 = JCo.createFunctionTemplate("SDH_SORT_MASTER_TABLE", null, null, null, impList5, null);
		JCoFunctionTemplate fT6 = JCo.createFunctionTemplate("SDH_INTAKE_AMOUNT_PACK", null, null, null, impList6, null);
		JCoFunctionTemplate fT7 = JCo.createFunctionTemplate("SDH_SEND_SHIP_DELI_INF", importList, exportList, null, impList7, null);
		
		cR.addFunctionTemplateToCache(fT1);
		cR.addFunctionTemplateToCache(fT2);
		cR.addFunctionTemplateToCache(fT3);
		cR.addFunctionTemplateToCache(fT4);
		cR.addFunctionTemplateToCache(fT5);
		cR.addFunctionTemplateToCache(fT6);
		cR.addFunctionTemplateToCache(fT7);
		
		JCoServer server;
		try {
			server = JCoServerFactory.getServer(SERVER_NAME1);
		} catch (JCoException ex) {
			throw new RuntimeException("Unable to create the server " + SERVER_NAME1 + " because of " + ex.getMessage(),
					ex);
		}

		String repDest = server.getRepositoryDestination();
;
		if (repDest != null) {
			try {
				cR.setDestination(JCoDestinationManager.getDestination(repDest));
			} catch (JCoException e) {
				e.printStackTrace();
				logger.info(">>> repository contains static function definition only");
			}
		}
		server.setRepository(cR);

		JCoServerFunctionHandler sdhCrateHandler1 = new CLS_SDH_CRATE_PACK_MASTER_IF();
		JCoServerFunctionHandler sdhCrateHandler2 = new CLS_SDH_MATERIAL_MAINT_IF_ST();
		JCoServerFunctionHandler sdhCrateHandler3 = new CLS_SDH_EIS_MATERIAL_CODE();
		JCoServerFunctionHandler sdhCrateHandler4 = new CLS_SDH_EIS_LEVEL_CODE();
		JCoServerFunctionHandler sdhCrateHandler5 = new CLS_SDH_SORT_MASTER_TABLE();
		JCoServerFunctionHandler sdhCrateHandler6 = new CLS_SDH_INTAKE_AMOUNT_PACK();
		JCoServerFunctionHandler sdhCrateHandler7 = new CLS_SDH_SEND_SHIP_DELI_INF();
		DefaultServerHandlerFactory.FunctionHandlerFactory factory = new DefaultServerHandlerFactory.FunctionHandlerFactory();
		factory.registerHandler(fT1.getName(), sdhCrateHandler1);
		factory.registerHandler(fT2.getName(), sdhCrateHandler2);
		factory.registerHandler(fT3.getName(), sdhCrateHandler3);
		factory.registerHandler(fT4.getName(), sdhCrateHandler4);
		factory.registerHandler(fT5.getName(), sdhCrateHandler5);
		factory.registerHandler(fT6.getName(), sdhCrateHandler6);
		factory.registerHandler(fT7.getName(), sdhCrateHandler7);
		
		
		server.setCallHandlerFactory(factory);

		server.start();
		logger.info("The program can be stoped using <ctrl>+<c>");
	}
	
	
	static class CLS_SDH_SEND_SHIP_DELI_INF implements JCoServerFunctionHandler {
		
		public void handleRequest(JCoServerContext serverCtx, JCoFunction function) {
						
			logger.info("function.name():" + function.getName());
			logger.info("serverCtx.getConnectionAttributes().toString():" + serverCtx.getConnectionAttributes().toString());
			
			String TKNUM_CNT = function.getImportParameterList().getString("TKNUM_CNT");
            String VBELN_CNT = function.getImportParameterList().getString("VBELN_CNT");
            logger.info("---IMPORT DATA---");
            logger.info("TKNUM_CNT:"+TKNUM_CNT);
            logger.info("VBELN_CNT:"+VBELN_CNT);
            
            String WERKS_CNT = null;
            String DPABF_CNT = null;
            int TKNUM_CNT_R = 0;
            int VBELN_CNT_R = 0;
            
            String SEQ_EXEC =null;
            
            //SEQ_EXEC 구해서 insert할때마다 같이 호출
            try {
            	SEQ_EXEC = DB.sdh_seq_exec();
				if(SEQ_EXEC == null){
					
				}else{
					logger.info("SEQ_EXEC:"+SEQ_EXEC);
				}
				
			} catch (ClassNotFoundException e) {
				
				e.printStackTrace();
				logger.info("DB.sdh_seq_exec() failed");
			}
            
            
			JCoTable myTable = function.getTableParameterList().getTable("T_3010");
			logger.info("myTable.getNumRows():" + myTable.getNumRows());
			logger.info("myTable.getRecordMetaData():" + myTable.getRecordMetaData());
			ArrayList<String> dataList = new ArrayList<String>();
			for (int i = 0; i < myTable.getNumRows(); i++, myTable.nextRow())
			{ 
				logger.info("------------------------------------");
				logger.info("getRow:"+myTable.getRow());
				myTable.setRow(i);
				logger.info("isEmpty:"+myTable.isEmpty());
				logger.info("getFieldCount:"+myTable.getFieldCount());
				
				String SEQNO = myTable.getString("SEQNO");//	V	CHAR	12		Sequence No.
				String TKNUM = myTable.getString("TKNUM");//	V	CHAR	10		선적문서번호
				String SHTYP = myTable.getString("SHTYP");//		CHAR	4		선적 유형
				String TPLST = myTable.getString("TPLST");//		CHAR	4		운송계획지점
				String BEZEI1 = myTable.getString("BEZEI1");//		CHAR	20		운송계획지점 내역
				String DTDIS = myTable.getString("DTDIS");//		CHAR	8		선적계획종료일
				String STDIS = myTable.getString("STDIS");//		CHAR	1		운송 계획 상태
				String ROUTE = myTable.getString("ROUTE");//		CHAR	6		운송경로
				String BEZEI2 = myTable.getString("BEZEI2");//		CHAR	40		운송경로명
				String TDLNR = myTable.getString("TDLNR");//		CHAR	10		운송차량
				String NAME1 = myTable.getString("NAME1");//		CHAR	35		운송차량명
				String TELF1 = myTable.getString("TELF1");//		CHAR	16		첫 번째 전화번호(운송차량)
				String TELF2 = myTable.getString("TELF2");//		CHAR	16		두 번째 전화번호(운송차량)
				String DPABF = myTable.getString("DPABF");//		CHAR	8		선적완료계획일
				String UPDKZ = myTable.getString("UPDKZ");//		CHAR	1		삭제 Flag (선적단위 갱신지시자)
				String SNDOK = myTable.getString("SNDOK");//		CHAR	1		성공 = 'S'  , 실패 = 'E' 
				String MESSAGE = myTable.getString("MESSAGE");//		CHAR	200		ERROR MESSAGE 
				
				
				logger.info("Sequence No.(SEQNO):"+SEQNO);//	V	CHAR	12		Sequence No.
				logger.info("선적문서번호(TKNUM):"+TKNUM);//	V	CHAR	10		선적문서번호
				logger.info("선적 유형 SHTYP:"+SHTYP);//		CHAR	4		선적 유형
				logger.info("운송계획지점 TPLST:"+TPLST);//		CHAR	4		운송계획지점
				logger.info("운송계획지점 내역 BEZEI1:"+BEZEI1);//		CHAR	20		운송계획지점 내역
				logger.info("선적계획종료일 DTDIS:"+DTDIS);//		CHAR	8		선적계획종료일
				logger.info("운송 계획 상태 STDIS:"+STDIS);//		CHAR	1		운송 계획 상태
				logger.info("운송경로 ROUTE:"+ROUTE);//		CHAR	6		운송경로
				logger.info("운송경로명 BEZEI2:"+BEZEI2);//		CHAR	40		운송경로명
				logger.info("운송차량 TDLNR:"+TDLNR);//		CHAR	10		운송차량
				logger.info("운송차량명 NAME1:"+NAME1);//		CHAR	35		운송차량명
				logger.info("첫 번째 전화번호 TELF1:"+TELF1);//		CHAR	16		첫 번째 전화번호(운송차량)
				logger.info("두 번째 전화번호 TELF2:"+TELF2);//		CHAR	16		두 번째 전화번호(운송차량)
				logger.info("선적완료계획일 DPABF:"+DPABF);//		CHAR	8		선적완료계획일
				logger.info("삭제 Flag UPDKZ:"+UPDKZ);//		CHAR	1		삭제 Flag (선적단위 갱신지시자)
				logger.info("SNDOK:"+SNDOK);//		CHAR	1		성공 = 'S'  , 실패 = 'E' 
				logger.info("MESSAGE:"+MESSAGE);//		CHAR	200		ERROR MESSAGE 
				
				//여기서 DB insert로직
				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("SEQNO", myTable.getString("SEQNO"));
				map.put("TKNUM", myTable.getString("TKNUM"));
				map.put("SHTYP", myTable.getString("SHTYP"));
				map.put("TPLST", myTable.getString("TPLST"));
				map.put("BEZEI1", myTable.getString("BEZEI1"));
				map.put("DTDIS", myTable.getString("DTDIS"));
				map.put("STDIS", myTable.getString("STDIS"));
				map.put("ROUTE", myTable.getString("ROUTE"));
				map.put("BEZEI2", myTable.getString("BEZEI2"));
				map.put("TDLNR", myTable.getString("TDLNR"));
				map.put("NAME1", myTable.getString("NAME1"));
				map.put("TELF1", myTable.getString("TELF1"));
				map.put("TELF2", myTable.getString("TELF2"));
				map.put("DPABF", myTable.getString("DPABF"));
				map.put("UPDKZ", myTable.getString("UPDKZ"));
				map.put("SEQ_EXEC", SEQ_EXEC);
				
				WERKS_CNT = TPLST;
				DPABF_CNT = DPABF;
				dataList.add(TPLST);
				try {
					logger.info("trying to db connect");
					if(DB.sdh_send_ship_deli_inf_ship(map)){
						logger.info("db return true");
						myTable.setValue("SNDOK","S");
						logger.info("myTable.setValue(\"SNDOK\",\"S\");");
						logger.info("myTable..toString:"+myTable.toString());
					}else{
						logger.info("db return false");
						myTable.setValue("SNDOK","E");
						logger.info("myTable.setValue(\"SNDOK\",\"E\");");
						myTable.setValue("MESSAGE","DB ERROR");
						logger.info("myTable.setValue(\"MESSAGE\",\"DB ERROR\");");
					}
					TKNUM_CNT_R++;
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					myTable.setValue("SNDOK","E");
					e.printStackTrace();
					logger.info("DB_SDH_SEND_SHIP_DELI_INF_SHIP insert failed");
				} catch (Exception e) {
					e.printStackTrace();
					logger.info("xxx");
				}
			}
			
			//리포트 프로그램을 호출하기 위함 - 플랜트 정보 받아오기
			
			ArrayList<String> plantList = new ArrayList<String>();
	        for (int i = 0; i < dataList.size(); i++) {
	            if (!plantList.contains(dataList.get(i))) {
	            	plantList.add(dataList.get(i));
	            }
	        }
	        
	        logger.info("plantList:"+plantList);
	        System.out.println("plantList:"+plantList);
			
			
			
			
			JCoTable myTable2 = function.getTableParameterList().getTable("T_3011");
			logger.info("myTable2.getNumRows():" + myTable2.getNumRows());
			logger.info("myTable2.getRecordMetaData():" + myTable2.getRecordMetaData());
			for (int i = 0; i < myTable2.getNumRows(); i++, myTable2.nextRow())
			{
				logger.info("------------------------------------");
				logger.info("getRow2:"+myTable2.getRow());
				myTable2.setRow(i);
				logger.info("isEmpty2:"+myTable2.isEmpty());
				logger.info("getFieldCount2:"+myTable2.getFieldCount());

				String SEQNO = myTable2.getString("SEQNO");//	V	CHAR	12		Sequence No.
				String TKNUM = myTable2.getString("TKNUM");//	V	CHAR	10		선적문서번호
				String TPNUM = myTable2.getString("TPNUM");//		CHAR	4		선적 유형
				String VBELN = myTable2.getString("VBELN");//		CHAR	4		운송계획지점
				String POSNR = myTable2.getString("POSNR");//		CHAR	20		운송계획지점 내역
				String MATNR = myTable2.getString("MATNR");//		CHAR	8		선적계획종료일
				String ARKTX = myTable2.getString("ARKTX");//		CHAR	1		운송 계획 상태
				String WERKS = myTable2.getString("WERKS");//		CHAR	6		운송경로
				String LGORT = myTable2.getString("LGORT");//		CHAR	40		운송경로명
				String LFIMG1 = myTable2.getString("LFIMG1");//
				logger.info("Sequence No.(SEQNO):"+SEQNO);//SEQNO	V	CHAR	10		Sequence No.
				logger.info("선적문서번호 (TKNUM):"+TKNUM);//TKNUM	V	CHAR	10		선적문서번호
				logger.info("선적품목 (TPNUM):"+TPNUM);//TPNUM	V	CHAR	4		선적품목
				logger.info("납품번호 (VBELN):"+VBELN);//VBELN	V	CHAR	10		납품번호
				logger.info("납품품목 (POSNR):"+POSNR);//POSNR	V	CHAR	6		납품품목
				logger.info("자재번호 (MATNR):"+MATNR);//MATNR		CHAR	18		자재번호
				logger.info("LIPS-ARKTX(ARKTX):"+ARKTX);//ARKTX		CHAR	40		LIPS-ARKTX
				logger.info("플랜트 (WERKS):"+WERKS);//WERKS		CHAR	4		플랜트
				logger.info("저장위치 (LGORT):"+LGORT);//LGORT		CHAR	4		저장위치
				logger.info("실제수량납품 (LFIMG1):"+LFIMG1);//LFIMG1		CHAR	13		실제수량납품 (판매단위)
				String MEINS1 = myTable2.getString("MEINS1");//MEINS1		CHAR	3		기본 단위
				String NTGEW1 = myTable2.getString("NTGEW1");//NTGEW1		CHAR	13		순 중량
				String BRGEW1 = myTable2.getString("BRGEW1");//BRGEW1		CHAR	13		총중량
				String GEWEI1 = myTable2.getString("GEWEI1");//GEWEI1		CHAR	3		중량 단위
				String VOLUM1 = myTable2.getString("VOLUM1");//VOLUM1		CHAR	13		볼륨
				String VOLEH1 = myTable2.getString("VOLEH1");//VOLEH1		CHAR	3		부피 단위
				String MEINH = myTable2.getString("MEINH");//MEINH		CHAR	3		환산 단위
				String UMREZ = myTable2.getString("UMREZ");//UMREZ		CHAR	5		기본 단위로 환산하는데 사용되는 분자
				String UMREN = myTable2.getString("UMREN");//UMREN		CHAR	5		기본 단위로 환산하는데 사용하는 분모
				String BOX_LFIMG = myTable2.getString("BOX_LFIMG");//BOX_LFIMG		CHAR	13		BOX 환산logger.info("실제수량납품 (MEINS1):"+MEINS1);//MEINS1		CHAR	3		기본 단위
				logger.info("기본 단위(MEINS1):"+MEINS1);//MEINS1		CHAR	3		기본 단위
				logger.info("순 중량(NTGEW1):"+NTGEW1);//NTGEW1		CHAR	13		순 중량
				logger.info("총중량(BRGEW1):"+BRGEW1);//BRGEW1		CHAR	13		총중량
				logger.info("중량 단위(GEWEI1):"+GEWEI1);//GEWEI1		CHAR	3		중량 단위
				logger.info("볼륨(VOLUM1):"+VOLUM1);//VOLUM1		CHAR	13		볼륨
				logger.info("부피 단위(VOLEH1):"+VOLEH1);//VOLEH1		CHAR	3		부피 단위
				logger.info("환산 단위(MEINH):"+MEINH);//MEINH		CHAR	3		환산 단위
				logger.info("기본 단위로 환산하는데 사용되는 분자(UMREZ):"+UMREZ);//UMREZ		CHAR	5		기본 단위로 환산하는데 사용되는 분자

				
				


				
				logger.info("기본 단위로 환산하는데 사용하는 분모(UMREN):"+UMREN);//UMREN		CHAR	5		기본 단위로 환산하는데 사용하는 분모
				logger.info("BOX 환산(BOX_LFIMG):"+BOX_LFIMG);//BOX_LFIMG		CHAR	13		BOX 환산
				

				
				String BOX_EA_LFIMG = myTable2.getString("BOX_EA_LFIMG");//BOX_EA_LFIMG		CHAR	13		BOX 환산후 낱개
				String LFART = myTable2.getString("LFART");//LFART		CHAR	4		납품유형
				String VSTEL = myTable2.getString("VSTEL");//VSTEL		CHAR	4		출하지점
				String VTEXT = myTable2.getString("VTEXT");//VTEXT		CHAR	30		출하지점 내역
				String VKORG = myTable2.getString("VKORG");//VKORG		CHAR	4		판매조직
				String WADAT = myTable2.getString("WADAT");//WADAT		CHAR	8		계획 자재 이동일(납품요청일)
				String ZTIME = myTable2.getString("ZTIME");//ZTIME		CHAR	6		납품처 도착 예정시간
				String LFDAT = myTable2.getString("LFDAT");//LFDAT		CHAR	8		납품일
				String ROUTE = myTable2.getString("ROUTE");//ROUTE		CHAR	6		운송경로
				String BEZEI1 = myTable2.getString("BEZEI1");//BEZEI1		CHAR	40		운송경로명
								
				logger.info("BOX 환산후 낱개(BOX_EA_LFIMG):"+BOX_EA_LFIMG);//BOX_EA_LFIMG		CHAR	4		납품유형
				logger.info("납품유형(LFART):"+LFART);//LFART		CHAR	4		납품유형
				logger.info("출하지점(VSTEL):"+VSTEL);//VSTEL		CHAR	4		출하지점
				logger.info("출하지점 내역(VTEXT):"+VTEXT);//VTEXT		CHAR	30		출하지점 내역
				logger.info("판매조직(VKORG):"+VKORG);//VKORG		CHAR	4		판매조직
				logger.info("계획 자재 이동일(납품요청일)(WADAT):"+WADAT);//WADAT		CHAR	8		계획 자재 이동일(납품요청일)
				logger.info("납품처 도착 예정시간(ZTIME):"+ZTIME);//ZTIME		CHAR	6		납품처 도착 예정시간
				logger.info("납품일(LFDAT):"+LFDAT);//LFDAT		CHAR	8		납품일
				logger.info("운송경로(ROUTE):"+ROUTE);//ROUTE		CHAR	6		운송경로
				logger.info("운송경로명(BEZEI1):"+BEZEI1);//BEZEI1		CHAR	40		운송경로명
				String KUNNR = myTable2.getString("KUNNR");//KUNNR		CHAR	4		납품유형
				String NAME1 = myTable2.getString("NAME1");//VSTEL		CHAR	4		출하지점
				String VKBUR = myTable2.getString("VKBUR");//VTEXT		CHAR	30		출하지점 내역
				String BEZEI2 = myTable2.getString("BEZEI2");//VKORG		CHAR	4		판매조직
				String J_1KFREPRE = myTable2.getString("J_1KFREPRE");//WADAT		CHAR	8		계획 자재 이동일(납품요청일)
				String TELF1 = myTable2.getString("TELF1");//ZTIME		CHAR	6		납품처 도착 예정시간
				String TELF2 = myTable2.getString("TELF2");//LFDAT		CHAR	8		납품일
				String ORT01 = myTable2.getString("ORT01");//ROUTE		CHAR	6		운송경로
				String STRAS = myTable2.getString("STRAS");//BEZEI1		CHAR	40		운송경로명
				String KUNAG = myTable2.getString("KUNAG");//BEZEI1		CHAR	40		운송경로명

				logger.info("납품처(KUNNR):"+KUNNR);//KUNNR		CHAR	10		납품처
				logger.info("납품처명(NAME1):"+NAME1);//NAME1		CHAR	35		납품처명
				logger.info("사업장(VKBUR):"+VKBUR);//VKBUR		CHAR	4		사업장
				logger.info("사업장명(BEZEI2):"+BEZEI2);//BEZEI2		CHAR	20		사업장명
				logger.info("대리점 대표자(J_1KFREPRE):"+J_1KFREPRE);//J_1KFREPRE		CHAR	10		대리점 대표자
				logger.info("전화(TELF1):"+TELF1);//TELF1		CHAR	16		전화
				logger.info("핸드폰(TELF2):"+TELF2);//TELF2		CHAR	16		핸드폰
				logger.info("주소1(ORT01):"+ORT01);//ORT01		CHAR	35		주소1
				logger.info("주소2(STRAS):"+STRAS);//STRAS		CHAR	35		주소2
				logger.info("판매처 (KUNAG):"+KUNAG);//KUNAG		CHAR	10		판매처
				String NAME2 = myTable2.getString("NAME2");//KUNNR		CHAR	4		납품유형
				String BTGEW2 = myTable2.getString("BTGEW2");//VSTEL		CHAR	4		출하지점
				String GEWEI2 = myTable2.getString("GEWEI2");//VTEXT		CHAR	30		출하지점 내역
				String NTGEW2 = myTable2.getString("NTGEW2");//VKORG		CHAR	4		판매조직
				String VOLUM2 = myTable2.getString("VOLUM2");//WADAT		CHAR	8		계획 자재 이동일(납품요청일)
				String VOLEH2 = myTable2.getString("VOLEH2");//ZTIME		CHAR	6		납품처 도착 예정시간
				String UPDKZ = myTable2.getString("UPDKZ");//LFDAT		CHAR	8		납품일
				//String SNDOK = myTable2.getString("SNDOK");//ROUTE		CHAR	6		운송경로
				//String MESSAGE = myTable2.getString("MESSAGE");//BEZEI1		CHAR	40		운송경로명
				
				logger.info("판매처명(NAME2):"+NAME2);//NAME2		CHAR	35		판매처명
				logger.info("총 중량(BTGEW2):"+BTGEW2);//BTGEW2		CHAR	15		총 중량
				logger.info("중량 단위(GEWEI2):"+GEWEI2);//GEWEI2		CHAR	3		중량 단위
				logger.info("순 중량(NTGEW2):"+NTGEW2);//NTGEW2		CHAR	15		순 중량
				logger.info("볼륨(부피)(VOLUM2):"+VOLUM2);//VOLUM2		CHAR	15		볼륨(부피)
				logger.info("부피 단위(VOLEH2):"+VOLEH2);//VOLEH2		CHAR	3		부피 단위
				logger.info("삭제 Flag (납품단위 갱신지시자)(UPDKZ):"+UPDKZ);//UPDKZ		CHAR	1		삭제 Flag (납품단위 갱신지시자)
				//logger.info("실제수량납품(SNDOK):"+SNDOK);//SNDOK		CHAR	1		성공 = 'S'  , 실패 = 'E'
				//logger.info("실제수량납품(MESSAGE):"+MESSAGE);//MESSAGE		CHAR	200		ERROR MESSAGE

				
				//myTable2.setValue("MESSAGE","S");
				HashMap<String, Object> map = new HashMap<String, Object>();

				map.put("SEQNO", myTable2.getString("SEQNO"));
				map.put("TKNUM", myTable2.getString("TKNUM"));
				map.put("TPNUM", myTable2.getString("TPNUM"));
				map.put("VBELN", myTable2.getString("VBELN"));
				map.put("POSNR", myTable2.getString("POSNR"));
				map.put("MATNR", myTable2.getString("MATNR"));
				map.put("ARKTX", myTable2.getString("ARKTX"));
				map.put("WERKS", myTable2.getString("WERKS"));
				map.put("LGORT", myTable2.getString("LGORT"));
				map.put("LFIMG1", myTable2.getString("LFIMG1"));
				/*MEINS1
				NTGEW1
				BRGEW1
				GEWEI1
				VOLUM1
				VOLEH1
				MEINH
				UMREZ
				UMREN
				BOX_LFIMG*/
				map.put("MEINS1", myTable2.getString("MEINS1"));
				map.put("NTGEW1", myTable2.getString("NTGEW1"));
				map.put("BRGEW1", myTable2.getString("BRGEW1"));
				map.put("GEWEI1", myTable2.getString("GEWEI1"));
				map.put("VOLUM1", myTable2.getString("VOLUM1"));
				map.put("VOLEH1", myTable2.getString("VOLEH1"));
				map.put("MEINH", myTable2.getString("MEINH"));
				map.put("UMREZ", myTable2.getString("UMREZ"));
				map.put("UMREN", myTable2.getString("UMREN"));
				map.put("BOX_LFIMG", myTable2.getString("BOX_LFIMG"));

				map.put("BOX_EA_LFIMG", myTable2.getString("BOX_EA_LFIMG"));
				map.put("LFART", myTable2.getString("LFART"));
				map.put("VSTEL", myTable2.getString("VSTEL"));
				map.put("VTEXT", myTable2.getString("VTEXT"));
				map.put("VKORG", myTable2.getString("VKORG"));
				map.put("WADAT", myTable2.getString("WADAT"));
				map.put("ZTIME", myTable2.getString("ZTIME"));
				map.put("LFDAT", myTable2.getString("LFDAT"));
				map.put("ROUTE", myTable2.getString("ROUTE"));
				map.put("BEZEI1", myTable2.getString("BEZEI1"));
				
				map.put("KUNNR", myTable2.getString("KUNNR"));
				map.put("NAME1", myTable2.getString("NAME1"));
				map.put("VKBUR", myTable2.getString("VKBUR"));
				map.put("BEZEI2", myTable2.getString("BEZEI2"));
				map.put("J_1KFREPRE", myTable2.getString("J_1KFREPRE"));
				map.put("TELF1", myTable2.getString("TELF1"));
				map.put("TELF2", myTable2.getString("TELF2"));
				map.put("ORT01", myTable2.getString("ORT01"));
				map.put("STRAS", myTable2.getString("STRAS"));
				map.put("KUNAG", myTable2.getString("KUNAG"));
				
				map.put("NAME2", myTable2.getString("NAME2"));
				map.put("BTGEW2", myTable2.getString("BTGEW2"));
				map.put("GEWEI2", myTable2.getString("GEWEI2"));
				map.put("NTGEW2", myTable2.getString("NTGEW2"));
				map.put("VOLUM2", myTable2.getString("VOLUM2"));
				map.put("VOLEH2", myTable2.getString("VOLEH2"));
				map.put("UPDKZ", myTable2.getString("UPDKZ"));

				map.put("SEQ_EXEC", SEQ_EXEC);
				
				try {
					if(DB.sdh_send_ship_deli_inf_deli(map)){
						myTable2.setValue("SNDOK","S");
					}else{
						myTable2.setValue("SNDOK","E");
						myTable2.setValue("MESSAGE","DB ERROR");
					}
					VBELN_CNT_R++;
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					myTable2.setValue("SNDOK","E");
					logger.info("DB_SDH_SEND_SHIP_DELI_INF_DELI insert failed");
					//e.printStackTrace();
				}
			}
			
			HashMap<String, Object> countmap = new HashMap<String, Object>();
			
			try {
				
				countmap.put("DPABF", DPABF_CNT);
				countmap.put("WERKS", WERKS_CNT);
				countmap.put("TKNUM_CNT", TKNUM_CNT);
				countmap.put("VBELN_CNT", VBELN_CNT);
				countmap.put("TKNUM_CNT_R", TKNUM_CNT_R);
				countmap.put("VBELN_CNT_R", VBELN_CNT_R);
				logger.info("count map:"+countmap);
				DB aa = new DB();
				aa.DB_SAP_SHIP_COUNT(countmap);
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				logger.info("DB_SDH_SEND_SHIP_DELI_INF_DELI insert failed");
				//e.printStackTrace();
			}
			
			//****강제로 변ㅅ에 등록해 본다 **/
			logger.info("*******TKNUM_CNT_R: "+ TKNUM_CNT_R);
			//TKNUM_CNT_R = 200;
			logger.info("*******TKNUM_CNT_R: "+ TKNUM_CNT_R);
			logger.info("*******VBELN_CNT_R: "+ VBELN_CNT_R);
			function.getExportParameterList().setValue("TKNUM_CNT_R", TKNUM_CNT_R);
			function.getExportParameterList().setValue("VBELN_CNT_R", VBELN_CNT_R);
			
			//프로시저 호출 먼저
			
			try {
				DB aa = new DB();
				aa.DB_SP_DELIVERYINFO();
				logger.info("call sp_deliveryInfo Success");
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				logger.info(" call sp_deliveryInfo failed");
				e.printStackTrace();
			}
			
			REPORT_PROGRAM rp = new REPORT_PROGRAM();
			logger.info("--call report program--");
			try {
				rp.reportgenerate(plantList, SEQ_EXEC);
			} catch (IOException | InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
	}

	
	
	static public void step3DataRepository() {
		JCoCustomRepository cR = JCo.createCustomRepository("MyCustomRepository");
		JCoListMetaData impList = JCo.createListMetaData("TABLES");
		JCoRecordMetaData ZLEC3003 = JCo.createRecordMetaData("ZMM1T0320", 1); // ZLES3001,ZSDT1008,

		ZLEC3003.add("ZLEV1", JCoMetaData.TYPE_CHAR, 8, 0, 0, 0);
		ZLEC3003.add("ZLEV2", JCoMetaData.TYPE_CHAR, 8, 8, 0, 0);
		ZLEC3003.add("ZLEV3", JCoMetaData.TYPE_CHAR, 8, 16, 0, 0);
		ZLEC3003.add("MATNR", JCoMetaData.TYPE_CHAR, 18, 24, 0, 0);
		ZLEC3003.add("ZUSER", JCoMetaData.TYPE_CHAR, 12, 42, 0, 0);
		ZLEC3003.add("DATUM", JCoMetaData.TYPE_CHAR, 8, 54, 0, 0);
		ZLEC3003.add("UZEIT", JCoMetaData.TYPE_CHAR, 6, 62, 0, 0);
		ZLEC3003.add("LVORM", JCoMetaData.TYPE_CHAR, 1, 68, 0, 0);
		ZLEC3003.add("SNDOK", JCoMetaData.TYPE_CHAR, 1, 69, 0, 0);
		ZLEC3003.add("MESSAGE", JCoMetaData.TYPE_CHAR, 200, 70, 0, 0);
		ZLEC3003.setRecordLength(270, 540);
		ZLEC3003.lock();
		
		impList.add("T_DATA", JCoMetaData.TYPE_TABLE, ZLEC3003, 0);
		impList.lock();
		JCoFunctionTemplate fT = JCo.createFunctionTemplate("SDH_EIS_MATERIAL_CODE", null, null, null, impList, null);
		cR.addFunctionTemplateToCache(fT);
		
		
		JCoServer server;
		try {
			server = JCoServerFactory.getServer(SERVER_NAME1);
		} catch (JCoException ex) {
			throw new RuntimeException("Unable to create the server " + SERVER_NAME1 + " because of " + ex.getMessage(),
					ex);
		}

		String repDest = server.getRepositoryDestination();
		if (repDest != null) {
			try {
				cR.setDestination(JCoDestinationManager.getDestination(repDest));
			} catch (JCoException e) {
				e.printStackTrace();
				logger.info(">>> repository contains static function definition only");
			}
		}
		server.setRepository(cR);

		JCoServerFunctionHandler sdhCrateHandler = new CLS_SDH_EIS_MATERIAL_CODE();
		DefaultServerHandlerFactory.FunctionHandlerFactory factory = new DefaultServerHandlerFactory.FunctionHandlerFactory();
		factory.registerHandler(fT.getName(), sdhCrateHandler);
		server.setCallHandlerFactory(factory);
		server.start();
		logger.info("The program can be stoped using <ctrl>+<c>");
	}
	
	
	static public void step6DataRepository() {
		JCoCustomRepository cR = JCo.createCustomRepository("MyCustomRepository");
		JCoListMetaData impList = JCo.createListMetaData("TABLES");
		JCoRecordMetaData ZLEC3006 = JCo.createRecordMetaData("MARM", 1); 
	
		
		
		/*MATNR	v	CHAR	4
MEINH	v	CHAR	4
UMREZ		CHAR	18
UMREN		CHAR	40
EAN11		CHAR	4
LVORM		CHAR	1
SNDOK		CHAR	1
MESSAGE 		CHAR	200

		*/
		/*ZLEC3006.add("MATNR", JCoMetaData.TYPE_CHAR, 4, 0, 0, 0);
		ZLEC3006.add("MEINH", JCoMetaData.TYPE_CHAR, 4, 4, 0, 0);
		ZLEC3006.add("UMREZ", JCoMetaData.TYPE_CHAR, 18, 8, 0, 0);
		ZLEC3006.add("UMREN", JCoMetaData.TYPE_CHAR, 40, 26, 0, 0);
		ZLEC3006.add("EAN11", JCoMetaData.TYPE_CHAR, 4, 66, 0, 0);
		ZLEC3006.add("LVORM", JCoMetaData.TYPE_CHAR, 1, 70, 0, 0);
		ZLEC3006.add("SNDOK", JCoMetaData.TYPE_CHAR, 1, 71, 0, 0);
		ZLEC3006.add("MESSAGE", JCoMetaData.TYPE_CHAR, 200, 72, 0, 0);
		ZLEC3006.setRecordLength(272, 544);
		ZLEC3006.lock();*/
		ZLEC3006.add("MATNR", JCoMetaData.TYPE_CHAR, 18, 0, 0, 0);
		ZLEC3006.add("MEINH", JCoMetaData.TYPE_CHAR, 3, 18, 0, 0);
		ZLEC3006.add("UMREZ", JCoMetaData.TYPE_CHAR, 5, 21, 0, 0);
		ZLEC3006.add("UMREN", JCoMetaData.TYPE_CHAR, 5, 26, 0, 0);
		ZLEC3006.add("EAN11", JCoMetaData.TYPE_CHAR, 18, 31, 0, 0);
		ZLEC3006.add("LVORM", JCoMetaData.TYPE_CHAR, 1, 49, 0, 0);
		ZLEC3006.add("SNDOK", JCoMetaData.TYPE_CHAR, 1, 50, 0, 0);
		ZLEC3006.add("MESSAGE", JCoMetaData.TYPE_CHAR, 200, 51, 0, 0);
		ZLEC3006.setRecordLength(251, 502);
		ZLEC3006.lock();
		
		impList.add("T_DATA", JCoMetaData.TYPE_TABLE, ZLEC3006, 0);
		impList.lock();
		JCoFunctionTemplate fT = JCo.createFunctionTemplate("SDH_INTAKE_AMOUNT_PACK", null, null, null, impList, null);
		cR.addFunctionTemplateToCache(fT);
		
		
		JCoServer server;
		try {
			server = JCoServerFactory.getServer(SERVER_NAME1);
		} catch (JCoException ex) {
			throw new RuntimeException("Unable to create the server " + SERVER_NAME1 + " because of " + ex.getMessage(),
					ex);
		}

		String repDest = server.getRepositoryDestination();
		if (repDest != null) {
			try {
				cR.setDestination(JCoDestinationManager.getDestination(repDest));
			} catch (JCoException e) {
				e.printStackTrace();
				logger.info(">>> repository contains static function definition only");
			}
		}
		server.setRepository(cR);

		JCoServerFunctionHandler sdhCrateHandler = new CLS_SDH_INTAKE_AMOUNT_PACK();
		DefaultServerHandlerFactory.FunctionHandlerFactory factory = new DefaultServerHandlerFactory.FunctionHandlerFactory();
		factory.registerHandler(fT.getName(), sdhCrateHandler);
		server.setCallHandlerFactory(factory);
		server.start();
		logger.info("The program can be stoped using <ctrl>+<c>");
	}
	
	static public void step5DataRepository() {
		JCoCustomRepository cR = JCo.createCustomRepository("MyCustomRepository");
		JCoListMetaData impList = JCo.createListMetaData("TABLES");
		JCoRecordMetaData ZLEC3005 = JCo.createRecordMetaData("ZUSDT01", 1); 
	
		
		
		/*WERKS	v	CHAR	4
LGORT	v	CHAR	4
MATNR	v	CHAR	18
MAKTX		CHAR	40
ZSORT		CHAR	4
LVORM		CHAR	1
SNDOK		CHAR	1
MESSAGE 		CHAR	200

		*/
		ZLEC3005.add("WERKS", JCoMetaData.TYPE_CHAR, 4, 0, 0, 0);
		ZLEC3005.add("LGORT", JCoMetaData.TYPE_CHAR, 4, 4, 0, 0);
		ZLEC3005.add("MATNR", JCoMetaData.TYPE_CHAR, 18, 8, 0, 0);
		ZLEC3005.add("MAKTX", JCoMetaData.TYPE_CHAR, 40, 26, 0, 0);
		ZLEC3005.add("ZSORT", JCoMetaData.TYPE_CHAR, 4, 66, 0, 0);
		ZLEC3005.add("LVORM", JCoMetaData.TYPE_CHAR, 1, 70, 0, 0);
		ZLEC3005.add("SNDOK", JCoMetaData.TYPE_CHAR, 1, 71, 0, 0);
		ZLEC3005.add("MESSAGE", JCoMetaData.TYPE_CHAR, 200, 72, 0, 0);
		ZLEC3005.setRecordLength(272, 544);
		ZLEC3005.lock();
		
		impList.add("T_DATA", JCoMetaData.TYPE_TABLE, ZLEC3005, 0);
		impList.lock();
		JCoFunctionTemplate fT = JCo.createFunctionTemplate("SDH_SORT_MASTER_TABLE", null, null, null, impList, null);
		cR.addFunctionTemplateToCache(fT);
		
		
		JCoServer server;
		try {
			server = JCoServerFactory.getServer(SERVER_NAME1);
		} catch (JCoException ex) {
			throw new RuntimeException("Unable to create the server " + SERVER_NAME1 + " because of " + ex.getMessage(),
					ex);
		}

		String repDest = server.getRepositoryDestination();
		if (repDest != null) {
			try {
				cR.setDestination(JCoDestinationManager.getDestination(repDest));
			} catch (JCoException e) {
				e.printStackTrace();
				logger.info(">>> repository contains static function definition only");
			}
		}
		server.setRepository(cR);

		JCoServerFunctionHandler sdhCrateHandler = new CLS_SDH_SORT_MASTER_TABLE();
		DefaultServerHandlerFactory.FunctionHandlerFactory factory = new DefaultServerHandlerFactory.FunctionHandlerFactory();
		factory.registerHandler(fT.getName(), sdhCrateHandler);
		server.setCallHandlerFactory(factory);
		server.start();
		logger.info("The program can be stoped using <ctrl>+<c>");
	}
	
	static class CLS_SDH_SORT_MASTER_TABLE implements JCoServerFunctionHandler {
		
		public void handleRequest(JCoServerContext serverCtx, JCoFunction function) {
			
			//List<HashMap<String, Object>> list = new ArrayList<>();

			logger.info("here!!");
			logger.info("function.name():" + function.getName());
			
			JCoTable myTable = function.getTableParameterList().getTable("T_DATA");
			logger.info("myTable.getNumRows():" + myTable.getNumRows());
			logger.info("myTable.getRecordMetaData():" + myTable.getRecordMetaData());
			
			for (int i = 0; i < myTable.getNumRows(); i++, myTable.nextRow())
			{ 
				logger.info("------------------------------------");
				logger.info("getRow:"+myTable.getRow());
				myTable.setRow(i);
				logger.info("isEmpty:"+myTable.isEmpty());
				logger.info("getFieldCount:"+myTable.getFieldCount());
			
				String WERKS = myTable.getString("WERKS");
				String LGORT = myTable.getString("LGORT");
				String MATNR = myTable.getString("MATNR");
				String MAKTX = myTable.getString("MAKTX");
				String ZSORT = myTable.getString("ZSORT");
				String LVORM = myTable.getString("LVORM");
				//String SNDOK = myTable.getString("SNDOK");
				//String MESSAGE = myTable.getString("MESSAGE");
				
				logger.info("플랜트(WERKS):"+WERKS);//WERKS	v	CHAR	4		플랜트
				logger.info("저장 위치(LGORT):"+LGORT);//LGORT	v	CHAR	4		저장 위치
				logger.info("자재 번호(MATNR):"+MATNR);//MATNR	v	CHAR	18		자재 번호
				logger.info("자재내역(MAKTX):"+MAKTX);//MAKTX		CHAR	40		자재내역
				logger.info("Sort 순서(ZSORT):"+ZSORT);//ZSORT		CHAR	4		Sort 순서
				logger.info("삭제 지시자(LVORM):"+LVORM);//LVORM		CHAR	1		삭제 지시자
				//logger.info("성공(SNDOK):"+SNDOK);//SNDOK		CHAR	1		성공 = 'S'  , 실패 = 'E' 
				//logger.info("ERROR MESSAGE (MESSAGE):"+MESSAGE);//MESSAGE 		CHAR	200		ERROR MESSAGE 

				
				
				//여기서 DB insert로직
				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("WERKS", WERKS);
				map.put("LGORT", LGORT);
				map.put("MATNR", MATNR);
				map.put("MAKTX", MAKTX);
				map.put("ZSORT", ZSORT);
				map.put("LVORM", LVORM);
				map.put("SNDOK", "S");
				map.put("MESSAGE", "");
				
				
				try {
					logger.info("trying to db connect");
					if(DB.sdh_sort_master_table(map)){
						logger.info("db return true");
						myTable.setValue("SNDOK","S");
						logger.info("myTable.setValue(\"SNDOK\",\"S\");");
						logger.info("myTable.toString:"+myTable.toString());
					}else{
						logger.info("db return false");
						myTable.setValue("SNDOK","E");
						logger.info("myTable.setValue(\"SNDOK\",\"E\");");
						myTable.setValue("MESSAGE","DB ERROR");
						logger.info("myTable.setValue(\"MESSAGE\",\"DB ERROR\");");
					}
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					myTable.setValue("SNDOK","E");
					e.printStackTrace();
					logger.info("CLS_SDH_SORT_MASTER_TABLE DB insert failed");
				}
				
				
			}
		}
	}
	static public void step4DataRepository() {
		JCoCustomRepository cR = JCo.createCustomRepository("MyCustomRepository");
		JCoListMetaData impList = JCo.createListMetaData("TABLES");
		JCoRecordMetaData ZLEC3004 = JCo.createRecordMetaData("ZMM1T0310", 1); 
		/*ZLEVEL	v	CHAR	2
ZCODE	v	CHAR	8
ZCODE_NAME		CHAR	40
ZUSER		CHAR	12
DATUM		CHAR	8
UZEIT		CHAR	6
LVORM		CHAR	1
SNDOK		CHAR	1
MESSAGE 		CHAR	200
		*/
		ZLEC3004.add("ZLEVEL", JCoMetaData.TYPE_CHAR, 2, 0, 0, 0);
		ZLEC3004.add("ZCODE", JCoMetaData.TYPE_CHAR, 8, 2, 0, 0);
		ZLEC3004.add("ZCODE_NAME", JCoMetaData.TYPE_CHAR, 40, 10, 0, 0);
		ZLEC3004.add("ZUSER", JCoMetaData.TYPE_CHAR, 12, 50, 0, 0);
		ZLEC3004.add("DATUM", JCoMetaData.TYPE_CHAR, 8, 62, 0, 0);
		ZLEC3004.add("UZEIT", JCoMetaData.TYPE_CHAR, 6, 70, 0, 0);
		ZLEC3004.add("LVORM", JCoMetaData.TYPE_CHAR, 1, 76, 0, 0);
		ZLEC3004.add("SNDOK", JCoMetaData.TYPE_CHAR, 1, 77, 0, 0);
		ZLEC3004.add("MESSAGE", JCoMetaData.TYPE_CHAR, 200, 78, 0, 0);
		ZLEC3004.setRecordLength(278, 556);
		ZLEC3004.lock();
		
		impList.add("T_DATA", JCoMetaData.TYPE_TABLE, ZLEC3004, 0);
		impList.lock();
		JCoFunctionTemplate fT = JCo.createFunctionTemplate("SDH_EIS_LEVEL_CODE", null, null, null, impList, null);
		cR.addFunctionTemplateToCache(fT);
		
		
		JCoServer server;
		try {
			server = JCoServerFactory.getServer(SERVER_NAME1);
		} catch (JCoException ex) {
			throw new RuntimeException("Unable to create the server " + SERVER_NAME1 + " because of " + ex.getMessage(),
					ex);
		}

		String repDest = server.getRepositoryDestination();
		if (repDest != null) {
			try {
				cR.setDestination(JCoDestinationManager.getDestination(repDest));
			} catch (JCoException e) {
				e.printStackTrace();
				logger.info(">>> repository contains static function definition only");
			}
		}
		server.setRepository(cR);

		JCoServerFunctionHandler sdhCrateHandler = new CLS_SDH_EIS_LEVEL_CODE();
		DefaultServerHandlerFactory.FunctionHandlerFactory factory = new DefaultServerHandlerFactory.FunctionHandlerFactory();
		factory.registerHandler(fT.getName(), sdhCrateHandler);
		server.setCallHandlerFactory(factory);
		server.start();
		logger.info("The program can be stoped using <ctrl>+<c>");
	}
	static public void step2DataRepository() {

		JCoCustomRepository cR = JCo.createCustomRepository("MyCustomRepository");
		JCoListMetaData impList = JCo.createListMetaData("TABLES");
		JCoRecordMetaData ZLEC3001 = JCo.createRecordMetaData("ZWMT0001", 1); // ZLES3001,ZSDT1008,
		
		/*MATNR	v	CHAR	18
		WERKS	v	CHAR	4
		MEINS		CHAR	3
		ZBOX		CHAR	3
		ZPAL		CHAR	3
		SNDOK		CHAR	1
		MESSAGE 		CHAR	200
*/
		ZLEC3001.add("MATNR", JCoMetaData.TYPE_CHAR, 18, 0, 0, 0);
		ZLEC3001.add("WERKS", JCoMetaData.TYPE_CHAR, 4, 18, 0, 0);
		ZLEC3001.add("MEINS", JCoMetaData.TYPE_CHAR, 3, 22, 0, 0);
		ZLEC3001.add("ZBOX", JCoMetaData.TYPE_CHAR, 3, 25, 0, 0);
		ZLEC3001.add("ZPAL", JCoMetaData.TYPE_CHAR, 3, 28, 0, 0);
		ZLEC3001.add("SNDOK", JCoMetaData.TYPE_CHAR, 1, 31, 0, 0);
		ZLEC3001.add("MESSAGE", JCoMetaData.TYPE_CHAR, 200, 32, 0, 0);
		ZLEC3001.setRecordLength(232, 464);
		ZLEC3001.lock();
		
		impList.add("T_DATA", JCoMetaData.TYPE_TABLE, ZLEC3001, 0);
		impList.lock();
		JCoFunctionTemplate fT = JCo.createFunctionTemplate("SDH_MATERIAL_MAINT_IF_ST", null, null, null, impList, null);
		cR.addFunctionTemplateToCache(fT);
		
		
		JCoServer server;
		try {
			server = JCoServerFactory.getServer(SERVER_NAME1);
		} catch (JCoException ex) {
			throw new RuntimeException("Unable to create the server " + SERVER_NAME1 + " because of " + ex.getMessage(),
					ex);
		}

		String repDest = server.getRepositoryDestination();
		if (repDest != null) {
			try {
				cR.setDestination(JCoDestinationManager.getDestination(repDest));
			} catch (JCoException e) {
				e.printStackTrace();
				logger.info(">>> repository contains static function definition only");
			}
		}
		server.setRepository(cR);

		JCoServerFunctionHandler sdhCrateHandler = new CLS_SDH_MATERIAL_MAINT_IF_ST();
		DefaultServerHandlerFactory.FunctionHandlerFactory factory = new DefaultServerHandlerFactory.FunctionHandlerFactory();
		factory.registerHandler(fT.getName(), sdhCrateHandler);
		server.setCallHandlerFactory(factory);
		server.start();
		logger.info("The program can be stoped using <ctrl>+<c>");
	}
	
	
	
	static class CLS_SDH_EIS_LEVEL_CODE implements JCoServerFunctionHandler {
		
		public void handleRequest(JCoServerContext serverCtx, JCoFunction function) {
			
			//List<HashMap<String, Object>> list = new ArrayList<>();

			logger.info("here!!");
			logger.info("function.name():" + function.getName());
			
			JCoTable myTable = function.getTableParameterList().getTable("T_DATA");
			logger.info("myTable.getNumRows():" + myTable.getNumRows());
			logger.info("myTable.getRecordMetaData():" + myTable.getRecordMetaData());
			
			for (int i = 0; i < myTable.getNumRows(); i++, myTable.nextRow())
			{ 
				logger.info("------------------------------------");
				logger.info("getRow:"+myTable.getRow());
				myTable.setRow(i);
				logger.info("isEmpty:"+myTable.isEmpty());
				logger.info("getFieldCount:"+myTable.getFieldCount());
			
				String ZLEVEL = myTable.getString("ZLEVEL");
				String ZCODE = myTable.getString("ZCODE");
				String ZCODE_NAME = myTable.getString("ZCODE_NAME");
				String ZUSER = myTable.getString("ZUSER");
				String DATUM = myTable.getString("DATUM");
				String UZEIT = myTable.getString("UZEIT");
				String LVORM = myTable.getString("LVORM");
				String SNDOK = myTable.getString("SNDOK");
				String MESSAGE = myTable.getString("MESSAGE");
				
				logger.info("EIS 계층구조 기본레벨(ZLEVEL):"+ZLEVEL);//ZLEVEL	v	CHAR	2		EIS 계층구조 기본레벨
				logger.info("EIS 계층구조 코드(ZCODE):"+ZCODE);//ZCODE	v	CHAR	8		EIS 계층구조 코드
				logger.info("EIS 계층구조 코드명(ZCODE_NAME):"+ZCODE_NAME);//ZCODE_NAME		CHAR	40		EIS 계층구조 코드명
				logger.info("사용자이름(ZUSER):"+ZUSER);//ZUSER		CHAR	12		사용자이름
				logger.info("일자(DATUM):"+DATUM);//DATUM		CHAR	8		일자
				logger.info("시간(UZEIT):"+UZEIT);//UZEIT		CHAR	6		시간
				logger.info("삭제표시(LVORM):"+LVORM);//LVORM		CHAR	1		삭제표시
				logger.info("성공(SNDOK):"+SNDOK);//SNDOK		CHAR	1		성공 = 'S'  , 실패 = 'E' 
				logger.info("ERROR MESSAGE (MESSAGE):"+MESSAGE);//MESSAGE 		CHAR	200		ERROR MESSAGE 

				
				
				//여기서 DB insert로직
				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("ZLEVEL", ZLEVEL);
				map.put("ZCODE", ZCODE);
				map.put("ZCODE_NAME", ZCODE_NAME);
				map.put("ZUSER", ZUSER);
				map.put("DATUM", DATUM);
				map.put("UZEIT", UZEIT);
				map.put("LVORM", LVORM);
				map.put("SNDOK", "S");
				map.put("MESSAGE", "");
				
				
				//logger.info("map:"+map);
				
				try {
					logger.info("trying to db connect");
					if(DB.sdh_eis_level_code(map)){
						logger.info("db return true");
						myTable.setValue("SNDOK","S");
						logger.info("myTable.setValue(\"SNDOK\",\"S\");");
						logger.info("myTable.toString():"+myTable.toString());
					}else{
						logger.info("db return false");
						myTable.setValue("SNDOK","E");
						logger.info("myTable.setValue(\"SNDOK\",\"E\");");
						myTable.setValue("MESSAGE","DB ERROR");
						logger.info("myTable.setValue(\"MESSAGE\",\"DB ERROR\");");
					}
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					myTable.setValue("SNDOK","E");
					e.printStackTrace();
					logger.info("CLS_SDH_EIS_LEVEL_CODE DB insert failed");
				}
				
				
			}
		}
	}
	
	static class CLS_SDH_INTAKE_AMOUNT_PACK implements JCoServerFunctionHandler {
		
		public void handleRequest(JCoServerContext serverCtx, JCoFunction function) {
			
			//List<HashMap<String, Object>> list = new ArrayList<>();

			logger.info("here!!");
			logger.info("function.name():" + function.getName());
			
			JCoTable myTable = function.getTableParameterList().getTable("T_DATA");
			logger.info("myTable.getNumRows():" + myTable.getNumRows());
			logger.info("myTable.getRecordMetaData():" + myTable.getRecordMetaData());
			
			for (int i = 0; i < myTable.getNumRows(); i++, myTable.nextRow())
			{ 
				logger.info("------------------------------------");
				logger.info("getRow:"+myTable.getRow());
				myTable.setRow(i);
				logger.info("isEmpty:"+myTable.isEmpty());
				logger.info("getFieldCount:"+myTable.getFieldCount());
			
				String MATNR = myTable.getString("MATNR");
				String MEINH = myTable.getString("MEINH");
				String UMREZ = myTable.getString("UMREZ");
				String UMREN = myTable.getString("UMREN");
				String EAN11 = myTable.getString("EAN11");
				String LVORM = myTable.getString("LVORM");
				String SNDOK = myTable.getString("SNDOK");
				String MESSAGE = myTable.getString("MESSAGE");
				
				
				logger.info("자재 번호(MATNR):"+MATNR);//MATNR	v	CHAR	4		자재 번호
				logger.info("재고유지단위의 대체단위(MEINH):"+MEINH);//MEINH	v	CHAR	4		재고유지단위의 대체단위
				logger.info("기본 단위로 환산하는데 사용되는 분자(UMREZ):"+UMREZ);//UMREZ		CHAR	18		기본 단위로 환산하는데 사용되는 분자
				logger.info("기본 단위로 환산하는데 사용하는 분모(UMREN):"+UMREN);//UMREN		CHAR	40		기본 단위로 환산하는데 사용하는 분모
				logger.info("국제물품번호 (EAN/UPC)(EAN11):"+EAN11);//EAN11		CHAR	4		국제물품번호 (EAN/UPC)
				logger.info("삭제표시(LVORM):"+LVORM);//LVORM		CHAR	1		삭제표시
				logger.info("성공(SNDOK):"+SNDOK);//SNDOK		CHAR	1		성공 = 'S'  , 실패 = 'E' 
				logger.info("ERROR MESSAGE (MESSAGE):"+MESSAGE);//MESSAGE 		CHAR	200		ERROR MESSAGE 

				
				
				//여기서 DB insert로직
				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("MATNR", MATNR);
				map.put("MEINH", MEINH);
				map.put("UMREZ", UMREZ);
				map.put("UMREN", UMREN);
				map.put("EAN11", EAN11);
				map.put("LVORM", LVORM);
				map.put("SNDOK", "S");
				map.put("MESSAGE", "");
				
								
				try {
					logger.info("trying to db connect");
					if(DB.sdh_intake_amount_pack(map)){
						logger.info("db return true");
						myTable.setValue("SNDOK","S");
						logger.info("myTable.setValue(\"SNDOK\",\"S\");");
						logger.info("myTable.toString():"+myTable.toString());
					}else{
						logger.info("db return false");
						myTable.setValue("SNDOK","E");
						logger.info("myTable.setValue(\"SNDOK\",\"E\");");
						myTable.setValue("MESSAGE","DB ERROR");
						logger.info("myTable.setValue(\"MESSAGE\",\"DB ERROR\");");
					}
				} catch (ClassNotFoundException e) {
					myTable.setValue("SNDOK","E");
					e.printStackTrace();
					logger.info("CLS_SDH_INTAKE_AMOUNT_PACK DB insert failed");
				}
				
				
			}
		}
	}
	
	static class CLS_SDH_MATERIAL_MAINT_IF_ST implements JCoServerFunctionHandler {
		
		public void handleRequest(JCoServerContext serverCtx, JCoFunction function) {
			
			//List<HashMap<String, Object>> list = new ArrayList<>();

			logger.info("here!!");
			logger.info("function.name():" + function.getName());
			
			JCoTable myTable = function.getTableParameterList().getTable("T_DATA");
			logger.info("myTable.getNumRows():" + myTable.getNumRows());
			logger.info("myTable.getRecordMetaData():" + myTable.getRecordMetaData());
			
			
			for (int i = 0; i < myTable.getNumRows(); i++, myTable.nextRow())
			{ 
				logger.info("------------------------------------");
				logger.info("getRow:"+myTable.getRow());
				myTable.setRow(i);
				logger.info("isEmpty:"+myTable.isEmpty());
				logger.info("getFieldCount:"+myTable.getFieldCount());
				

				String MATNR = myTable.getString("MATNR");
				String WERKS = myTable.getString("WERKS");
				String MEINS = myTable.getString("MEINS");
				String ZBOX = myTable.getString("ZBOX");
				String ZPAL = myTable.getString("ZPAL");
				
				logger.info("자재 번호(MATNR):"+MATNR);//MATNR	v	CHAR	18		자재 번호
				logger.info("플랜트(WERKS):"+WERKS);//WERKS	v	CHAR	4		플랜트
				logger.info("기본 단위(MEINS):"+MEINS);//MEINS		CHAR	3		기본 단위
				logger.info("박스단위(ZBOX):"+ZBOX);//ZBOX		CHAR	3		박스단위
				logger.info("팔렛단위(ZPAL):"+ZPAL);//ZPAL		CHAR	3		팔렛단위

				
				
				//여기서 DB insert로직
				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("MATNR", myTable.getString("MATNR"));
				map.put("WERKS", myTable.getString("WERKS"));
				map.put("MEINS", myTable.getString("MEINS"));
				map.put("ZBOX", myTable.getString("ZBOX"));
				map.put("ZPAL", myTable.getString("ZPAL"));
				
				try {
					logger.info("trying to db connect");
					if(DB.sdh_material_maint_if_st(map)){
						logger.info("db return true");

						myTable.setValue("SNDOK","S");
						logger.info("myTable.setValue(\"SNDOK\",\"S\");");
						logger.info("myTable.toString():"+myTable.toString());
					}else{
						logger.info("db return false");
						myTable.setValue("SNDOK","E");
						logger.info("myTable.setValue(\"SNDOK\",\"E\");");
						myTable.setValue("MESSAGE","DB ERROR");
						logger.info("myTable.setValue(\"MESSAGE\",\"DB ERROR\");");
					}
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					logger.info("SNDOK E");
					myTable.setValue("SNDOK","E");
					e.printStackTrace();
					logger.info("SDH_MATERIAL_MAINT_IF_ST DB insert failed");
				}
				
			}
			/*REPORT_PROGRAM rp = new REPORT_PROGRAM();
			try {
				rp.reportgenerate();
			} catch (IOException | InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
		}
	}
	
	static class REPORT_PROGRAM {
		
		public void reportgenerate(ArrayList<String> plantList, String SEQ_EXEC) throws IOException, InterruptedException {
			
			String paramStr = "";
			for(int i=0;i<plantList.size();i++){
				paramStr += plantList.get(i).toString();
				if(i<plantList.size()-1){
					paramStr += ",";
				}
			}
			logger.info("plantList:"+paramStr);
			logger.info("SEQ_EXEC:"+SEQ_EXEC);
			
			ProcessBuilder builder = new ProcessBuilder("cmd.exe", "/c", "start", reportFilePath, paramStr, SEQ_EXEC);			   
	        String dirPath = reportFilePath.substring(0,reportFilePath.lastIndexOf("/"));	        
			builder.directory(new File(dirPath));
			
			try {
				builder.start();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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
			reportFilePath = properties.getProperty("reportFilePath");

			System.out.println(properties.getProperty("username")); 
			System.out.println(properties.getProperty("password")); 
			System.out.println(properties.getProperty("url")); 
			
			DB.connectionUrl = driver + url + ";database="+database;
			DB.dbUserId = username;
			DB.dbPassword = password;
		} 
		catch (IOException e) { 
			e.printStackTrace(); 
		}
		
	}
	public static void main(String[] a) {
		logger.info("step by step server");
		System.out.println(System.getProperty("java.library.path"));
		StepByStepServer sb = new StepByStepServer();
		sb.GetProperties();
		
		//1) 제품별 크레이트포장 마스터 IF SDH_CRATE_PACK_MASTER_IF
		//step1DataRepository();
		//2) 자재 운반 단위 유지 보수 IF 구조 SDH_MATERIAL_MAINT_IF_ST
		//step2DataRepository();
		//3) EIS계층구조코드별 자재코드관리 테이블 SDH_EIS_MATERIAL_CODE
		//step3DataRepository();
		//4) EIS 계층구조 레벨별 코드명 SDH_EIS_LEVEL_CODE
		//step4DataRepository();
		//5) 저장위치 별 Sort Master 테이블 SDH_SORT_MASTER_TABLE
		//step5DataRepository();
		//6) 자재별 포장단위 기준 내입량 SDH_INTAKE_AMOUNT_PACK
		//step6DataRepository();
		//7) 선적/납품 정보 송신 SDH_SEND_SHIP_DELI_INF
		step7DataRepository();		
	}
}
