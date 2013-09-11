package com.redcats.tst.dao.impl;

import com.redcats.tst.dao.ITestCaseStepExecutionDAO;
import com.redcats.tst.database.DatabaseSpring;
import com.redcats.tst.entity.TestCaseStepExecution;
import com.redcats.tst.factory.IFactoryTestCaseStepExecution;
import com.redcats.tst.log.MyLogger;
import com.redcats.tst.util.ParameterParserUtil;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Level;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * {Insert class description here}
 *
 * @author Tiago Bernardes
 * @version 1.0, 03/01/2013
 * @since 2.0.0
 */
@Repository
public class TestCaseStepExecutionDAO implements ITestCaseStepExecutionDAO {

    /**
     * Description of the variable here.
     */
    @Autowired
    private DatabaseSpring databaseSpring;
    @Autowired
    private IFactoryTestCaseStepExecution factoryTestCaseStepExecution;

    /**
     * Short one line description.
     * <p/>
     * Longer description. If there were any, it would be here. <p> And even
     * more explanations to follow in consecutive paragraphs separated by HTML
     * paragraph breaks.
     *
     * @param variable Description text text text.
     */
    @Override
    public void insertTestCaseStepExecution(TestCaseStepExecution testCaseStepExecution) {
        final String query = "INSERT INTO testcasestepexecution(id, test, testcase, step, batnumexe, returncode, start, END, fullstart, fullend) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try {
            PreparedStatement preStat = this.databaseSpring.connect().prepareStatement(query);
            preStat.setLong(1, testCaseStepExecution.getId());
            preStat.setString(2, testCaseStepExecution.getTest());
            preStat.setString(3, testCaseStepExecution.getTestCase());
            preStat.setInt(4, testCaseStepExecution.getStep());
            preStat.setString(5, testCaseStepExecution.getBatNumExe());
            preStat.setString(6, testCaseStepExecution.getReturnCode());
            if (testCaseStepExecution.getStart() != 0) {
                preStat.setTimestamp(7, new Timestamp(testCaseStepExecution.getStart()));
            } else {
                preStat.setString(7, "0000-00-00 00:00:00");
            }
            if (testCaseStepExecution.getEnd() != 0) {
                preStat.setTimestamp(8, new Timestamp(testCaseStepExecution.getEnd()));
            } else {
                preStat.setString(8, "0000-00-00 00:00:00");
            }
            preStat.setString(9, new SimpleDateFormat("yyyyMMddHHmmssSSS").format(testCaseStepExecution.getStart()));
            preStat.setString(10, new SimpleDateFormat("yyyyMMddHHmmssSSS").format(testCaseStepExecution.getEnd()));
            MyLogger.log(TestCaseStepExecutionDAO.class.getName(), Level.DEBUG, "Insert testcasestepexecution " + testCaseStepExecution.getId() + "-"
                    + testCaseStepExecution.getTest() + "-" + testCaseStepExecution.getTestCase() + "-" + testCaseStepExecution.getStep());
            try {
                preStat.executeUpdate();

            } catch (SQLException exception) {
                MyLogger.log(TestCaseStepExecutionDAO.class.getName(), Level.ERROR, exception.toString());
            } finally {
                preStat.close();
            }
        } catch (SQLException exception) {
            MyLogger.log(TestCaseStepExecutionDAO.class.getName(), Level.ERROR, exception.toString());
        } finally {
            this.databaseSpring.disconnect();
        }
    }

    @Override
    public void updateTestCaseStepExecution(TestCaseStepExecution testCaseStepExecution) {
        final String query = "UPDATE testcasestepexecution SET returncode = ?, start = ?, fullstart = ?, end = ?, fullend = ?, timeelapsed = ? WHERE id = ? AND step = ? AND test = ? AND testcase = ?";

        try {
            Timestamp timeStart = new Timestamp(testCaseStepExecution.getStart());
            Timestamp timeEnd = new Timestamp(testCaseStepExecution.getEnd());

            PreparedStatement preStat = this.databaseSpring.connect().prepareStatement(query);
            preStat.setString(1, ParameterParserUtil.parseStringParam(testCaseStepExecution.getReturnCode(), ""));
            preStat.setTimestamp(2, timeStart);
            preStat.setString(3, new SimpleDateFormat("yyyyMMddHHmmssSSS").format(timeStart));
            preStat.setTimestamp(4, timeEnd);
            preStat.setString(5, new SimpleDateFormat("yyyyMMddHHmmssSSS").format(timeEnd));
            preStat.setFloat(6, (timeEnd.getTime() - timeStart.getTime()) / (float) 1000);
            preStat.setLong(7, testCaseStepExecution.getId());
            preStat.setInt(8, testCaseStepExecution.getStep());
            preStat.setString(9, testCaseStepExecution.getTest());
            preStat.setString(10, testCaseStepExecution.getTestCase());
            MyLogger.log(TestCaseStepExecutionDAO.class.getName(), Level.DEBUG, "Update testcasestepexecution " + testCaseStepExecution.getId() + "-"
                    + testCaseStepExecution.getTest() + "-" + testCaseStepExecution.getTestCase() + "-" + testCaseStepExecution.getStep());
            try {
                preStat.executeUpdate();

            } catch (SQLException exception) {
                MyLogger.log(TestCaseStepExecutionDAO.class.getName(), Level.ERROR, exception.toString());
            } finally {
                preStat.close();
            }
        } catch (SQLException exception) {
            MyLogger.log(TestCaseStepExecutionDAO.class.getName(), Level.ERROR, exception.toString());
        } finally {
            this.databaseSpring.disconnect();
        }
    }
    
    
    @Override
    public List<TestCaseStepExecution> findTestCaseStepExecutionById(long id){
        List<TestCaseStepExecution> result = null;
        TestCaseStepExecution resultData;
        boolean throwEx = false;
        final String query = "SELECT * FROM TestCaseStepExecution WHERE id = ? order by start";

        try {
            PreparedStatement preStat = this.databaseSpring.connect().prepareStatement(query);
            preStat.setString(1, String.valueOf(id));

            try {
                ResultSet resultSet = preStat.executeQuery();
                result = new ArrayList<TestCaseStepExecution>();
                try {
                    while (resultSet.next()) {
                        String test = resultSet.getString("test");
                        String testcase = resultSet.getString("testcase");
                        int step = resultSet.getInt("step");
                        String batNumExe = resultSet.getString("batnumexe");
                        long start = resultSet.getLong("start");
                        long end = resultSet.getLong("end");
                        long fullstart = resultSet.getLong("fullstart");
                        long fullend = resultSet.getLong("Fullend");
                        long timeelapsed = resultSet.getLong("timeelapsed");
                        String returnCode = resultSet.getString("returncode");
                        resultData = factoryTestCaseStepExecution.create(id, test,testcase, step, batNumExe, start, end, fullstart, fullend, timeelapsed, returnCode, null, null, null);
                        result.add(resultData);
                    }
                } catch (SQLException exception) {
                    MyLogger.log(TestCaseExecutionDataDAO.class.getName(), Level.ERROR, exception.toString());
                } finally {
                    resultSet.close();
                }
            } catch (SQLException exception) {
                MyLogger.log(TestCaseExecutionDataDAO.class.getName(), Level.ERROR, exception.toString());
            } finally {
                preStat.close();
            }
        } catch (SQLException exception) {
            MyLogger.log(TestCaseExecutionDataDAO.class.getName(), Level.ERROR, exception.toString());
        } finally {
            this.databaseSpring.disconnect();
        }
        return result;
    }
}