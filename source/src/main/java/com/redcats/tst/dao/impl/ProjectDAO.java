package com.redcats.tst.dao.impl;

import com.redcats.tst.dao.IProjectDAO;
import com.redcats.tst.database.DatabaseSpring;
import com.redcats.tst.entity.Project;
import com.redcats.tst.factory.IFactoryProject;
import com.redcats.tst.log.MyLogger;
import org.apache.log4j.Level;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author bcivel
 */
@Repository
public class ProjectDAO implements IProjectDAO {

    @Autowired
    private DatabaseSpring databaseSpring;
    @Autowired
    private IFactoryProject factoryProject;

    @Override
    public List<Project> findAllProject() {
        List<Project> result = null;
        String idProject;
        String vcCode;
        String description;
        final String query = "SELECT * FROM project ORDER BY idproject";

        try {
            PreparedStatement preStat = this.databaseSpring.connect().prepareStatement(query);
            try {
                ResultSet resultSet = preStat.executeQuery();
                result = new ArrayList<Project>();
                try {
                    while (resultSet.next()) {
                        idProject = resultSet.getString("idproject") == null ? "" : resultSet.getString("idproject");
                        vcCode = resultSet.getString("VCCode") == null ? "" : resultSet.getString("VCCode");
                        description = resultSet.getString("Description") == null ? "" : resultSet.getString("Description");
                        String active = resultSet.getString("active") == null ? "" : resultSet.getString("active");
                        String dateCreation = resultSet.getString("dateCreation") == null ? "" : resultSet.getString("dateCreation");
                        result.add(factoryProject.create(idProject, vcCode, description, active, dateCreation));
                    }
                } catch (SQLException exception) {
                    MyLogger.log(ProjectDAO.class.getName(), Level.ERROR, exception.toString());
                } finally {
                    resultSet.close();
                }
            } catch (SQLException exception) {
                MyLogger.log(ProjectDAO.class.getName(), Level.ERROR, exception.toString());
            } finally {
                preStat.close();
            }
        } catch (SQLException exception) {
            MyLogger.log(ProjectDAO.class.getName(), Level.ERROR, exception.toString());
        } finally {
            this.databaseSpring.disconnect();
        }

        return result;
    }


}