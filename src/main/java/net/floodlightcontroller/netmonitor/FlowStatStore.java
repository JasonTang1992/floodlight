import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

public class FlowStatStore {
	private final static String Name = "This is a testcase for JDBC";
	private final static String Database = "localhost/TrafficMeasurement";
	private final static String TableFlowEntry = "flowentry";
	private final static String TableFlowRate = "FlowRate";
	private final static String TableFlowStatistic = "flowstatistic";
	private final static String User = "root";
	private final static String Password = "pl,okm123";

	/**
	 * @param DatapathID
	 * @param TableID
	 * @param Match
	 * @param Priority
	 * @return 0 means all right;
	 */
	public int EventPacketIn(String DatapathID, String TableID, String Match, String Priority) {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		String sqlcmd = "INSERT INTO " + TableFlowEntry + " (`DatapathID`,`TableID`,`Match`,`Priority`)" + " VALUES (\'"
				+ DatapathID + "\',\'" + TableID + "\',\'" + Match + "\',\'" + Priority + "\');";
		try {
			conn = DriverManager.getConnection("jdbc:mysql://" + Database + "?user=" + User + "&password=" + Password);
			// Do something with the Connection

			stmt = conn.createStatement();
			System.out.println(sqlcmd);
			stmt.execute(sqlcmd);

		} catch (SQLException ex) {
			// handle any errors
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
		} finally {
			// it is a good idea to release
			// resources in a finally{} block
			// in reverse-order of their creation
			// if they are no-longer needed

			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException sqlEx) {
				} // ignore

				rs = null;
			}

			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException sqlEx) {
				} // ignore

				stmt = null;
			}
		}
		return 0;
	}

	/**
	 * @param wildcard
	 * @param bytecount
	 * @return
	 */
	public int EventFlowReply(String Wildcard, String Timestamp, String ByteCount) {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		String sqlcmd = "INSERT INTO " + TableFlowStatistic + " (`Wildcard`,`Timestamp`,`ByteCount`)" + " VALUES ("
				+ Wildcard + "," + Timestamp + "," + ByteCount + ");";
		try {
			conn = DriverManager.getConnection("jdbc:mysql://" + Database + "?user=" + User + "&password=" + Password);
			// Do something with the Connection

			stmt = conn.createStatement();
			System.out.println(sqlcmd);
			stmt.execute(sqlcmd);

		} catch (SQLException ex) {
			// handle any errors
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
		} finally {
			// it is a good idea to release
			// resources in a finally{} block
			// in reverse-order of their creation
			// if they are no-longer needed

			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException sqlEx) {
				} // ignore

				rs = null;
			}

			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException sqlEx) {
				} // ignore

				stmt = null;
			}
		}
		return 0;
	}

	/**
	 * @param Wildcard
	 * @param Timestamp
	 * @param Rate
	 * @return
	 */
	public int pushFlowRate(String Wildcard, String Timestamp, String Rate) {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		String sqlcmd = "INSERT INTO " + TableFlowRate + " (`Wildcard`,`Timestamp`,`Rate`)" + " VALUES (" + Wildcard
				+ "," + Timestamp + "," + Rate + ");";
		try {
			conn = DriverManager.getConnection("jdbc:mysql://" + Database + "?user=" + User + "&password=" + Password);
			// Do something with the Connection

			stmt = conn.createStatement();
			System.out.println(sqlcmd);
			stmt.execute(sqlcmd);

		} catch (SQLException ex) {
			// handle any errors
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
		} finally {
			// it is a good idea to release
			// resources in a finally{} block
			// in reverse-order of their creation
			// if they are no-longer needed

			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException sqlEx) {
				} // ignore

				rs = null;
			}

			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException sqlEx) {
				} // ignore

				stmt = null;
			}
		}
		return 0;
	}

	/**
	 * @param DatapathID
	 * @param TableID
	 * @param Match
	 * @param Priority
	 * @return
	 */
	public long getWildcard(String DatapathID, String TableID, String Match, String Priority) {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		String sqlcmd = "SELECT Wildcard FROM " + TableFlowEntry + " WHERE `DatapathID`=" + "\'" + DatapathID + "\'"
				+ " AND " + "`TableID`=" + "\'" + TableID + "\'" + " AND " + "`Match`=" + "\'" + Match + "\'" + " AND "
				+ "`Priority`=" + "\'" + Priority + "\';";
		try {
			conn = DriverManager.getConnection("jdbc:mysql://" + Database + "?user=" + User + "&password=" + Password);
			// Do something with the Connection

			stmt = conn.createStatement();
			System.out.println(sqlcmd);
			rs = stmt.executeQuery(sqlcmd);
			if (rs.first()) {
				return rs.getLong(1);
			} else {
				System.out.println("getWildcard:SQL Return Error");
			}

		} catch (SQLException ex) {
			// handle any errors
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
		} finally {
			// it is a good idea to release
			// resources in a finally{} block
			// in reverse-order of their creation
			// if they are no-longer needed

			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException sqlEx) {
				} // ignore

				rs = null;
			}

			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException sqlEx) {
				} // ignore

				stmt = null;
			}
		}

		return -1;
	}

	/**
	 * @param wildcard
	 * @return
	 */
	public Map<Long, Long> getAllByteCounts(long wildcard) {
		Map<Long, Long> res = new HashMap<Long, Long>();
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		String sqlcmd = "SELECT TimeStamp,ByteCount FROM " + TableFlowStatistic + " WHERE `wildcard`="
				+ String.valueOf(wildcard) + " ORDER BY TimeStamp DESC" + ";";
		try {
			conn = DriverManager.getConnection("jdbc:mysql://" + Database + "?user=" + User + "&password=" + Password);
			// Do something with the Connection

			stmt = conn.createStatement();
			System.out.println(sqlcmd);
			rs = stmt.executeQuery(sqlcmd);
			if (rs.next()) {
				do {
					res.put(rs.getLong(1), rs.getLong(2));
				} while (rs.next());
			} else {
				System.out.println("getWildcard:SQL Return Error");
			}

		} catch (SQLException ex) {
			// handle any errors
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
		} finally {
			// it is a good idea to release
			// resources in a finally{} block
			// in reverse-order of their creation
			// if they are no-longer needed

			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException sqlEx) {
				} // ignore

				rs = null;
			}

			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException sqlEx) {
				} // ignore

				stmt = null;
			}
		}

		return res;
	}

	/**
	 * @param wildcard
	 * @param num
	 * @return
	 */
	public Map<Long, Long> getByteCounts(long wildcard, long num) {
		Map<Long, Long> res = new HashMap<Long, Long>();
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		String sqlcmd = "SELECT TimeStamp,ByteCount FROM " + TableFlowStatistic + " WHERE `wildcard`="
				+ String.valueOf(wildcard) + " ORDER BY TimeStamp DESC" + " LIMIT " + String.valueOf(num) + " " + ";";
		try {
			conn = DriverManager.getConnection("jdbc:mysql://" + Database + "?user=" + User + "&password=" + Password);
			// Do something with the Connection

			stmt = conn.createStatement();
			System.out.println(sqlcmd);
			rs = stmt.executeQuery(sqlcmd);
			if (rs.next()) {
				do {
					res.put(rs.getLong(1), rs.getLong(2));
				} while (rs.next());
			} else {
				System.out.println("getWildcard:SQL Return Error");
			}

		} catch (SQLException ex) {
			// handle any errors
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
		} finally {
			// it is a good idea to release
			// resources in a finally{} block
			// in reverse-order of their creation
			// if they are no-longer needed

			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException sqlEx) {
				} // ignore

				rs = null;
			}

			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException sqlEx) {
				} // ignore

				stmt = null;
			}
		}

		return res;
	}

	/**
	 * @param wildcard
	 * @return
	 */
	public Map<Long, Long> getAllFlowRates(long wildcard) {
		Map<Long, Long> res = new HashMap<Long, Long>();
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		String sqlcmd = "SELECT TimeStamp,Rate FROM " + TableFlowRate + " WHERE `wildcard`=" + String.valueOf(wildcard)
				+ " ORDER BY TimeStamp DESC" + ";";
		try {
			conn = DriverManager.getConnection("jdbc:mysql://" + Database + "?user=" + User + "&password=" + Password);
			// Do something with the Connection

			stmt = conn.createStatement();
			System.out.println(sqlcmd);
			rs = stmt.executeQuery(sqlcmd);
			if (rs.next()) {
				do {
					res.put(rs.getLong(1), rs.getLong(2));
				} while (rs.next());
			} else {
				System.out.println("getWildcard:SQL Return Error");
			}

		} catch (SQLException ex) {
			// handle any errors
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
		} finally {
			// it is a good idea to release
			// resources in a finally{} block
			// in reverse-order of their creation
			// if they are no-longer needed

			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException sqlEx) {
				} // ignore

				rs = null;
			}

			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException sqlEx) {
				} // ignore

				stmt = null;
			}
		}

		return res;
	}

	/**
	 * @param wildcard
	 * @param num
	 * @return
	 */
	public Map<Long, Long> getFlowRates(long wildcard, long num) {
		Map<Long, Long> res = new HashMap<Long, Long>();
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		String sqlcmd = "SELECT TimeStamp,Rate FROM " + TableFlowRate + " WHERE `wildcard`=" + String.valueOf(wildcard)
				+ " ORDER BY TimeStamp DESC" + " LIMIT " + String.valueOf(num) + " " + ";";
		try {
			conn = DriverManager.getConnection("jdbc:mysql://" + Database + "?user=" + User + "&password=" + Password);
			// Do something with the Connection

			stmt = conn.createStatement();
			System.out.println(sqlcmd);
			rs = stmt.executeQuery(sqlcmd);
			if (rs.next()) {
				do {
					res.put(rs.getLong(1), rs.getLong(2));
				} while (rs.next());
			} else {
				System.out.println("getWildcard:SQL Return Error");
			}

		} catch (SQLException ex) {
			// handle any errors
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
		} finally {
			// it is a good idea to release
			// resources in a finally{} block
			// in reverse-order of their creation
			// if they are no-longer needed

			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException sqlEx) {
				} // ignore

				rs = null;
			}

			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException sqlEx) {
				} // ignore

				stmt = null;
			}
		}

		return res;
	}


}
