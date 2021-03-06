/*
 * Cerberus  Copyright (C) 2013  vertigo17
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This file is part of Cerberus.
 *
 * Cerberus is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Cerberus is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Cerberus.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.cerberus.service;

import java.util.List;

import org.cerberus.entity.User;
import org.cerberus.exception.CerberusException;

/**
 * @author vertigo
 */
public interface IUserService {

    /**
     * @param login
     * @return the user that match the login
     * @throws CerberusException
     */
    User findUserByKey(String login) throws CerberusException;

    /**
     * @return a list of all the users
     * @throws CerberusException
     */
    List<User> findallUser() throws CerberusException;

    /**
     * @param user
     * @return
     * @throws CerberusException
     */
    void insertUser(User user) throws CerberusException;

    /**
     * @param user
     * @return
     * @throws CerberusException
     */
    void deleteUser(User user) throws CerberusException;

    /**
     * @param user
     * @throws CerberusException
     */
    void updateUser(User user) throws CerberusException;

    /**
     * @param user
     * @param currentPassword
     * @param newPassword
     * @param confirmPassword
     * @return
     * @throws CerberusException
     */
    User updateUserPassword(User user, String currentPassword, String newPassword, String confirmPassword) throws CerberusException;

    /**
     * @param user
     * @param password
     * @return
     */
    boolean verifyPassword(User user, String password);

    /**
     *
     * @param user
     * @param User
     * @return true if user exist. false if not.
     */
    boolean isUserExist(String user);
    
        /**
     *
     * @param start first row of the resultSet
     * @param amount number of row of the resultSet
     * @param column order the resultSet by this column
     * @param dir Asc or desc, information for the order by command
     * @param searchTerm search term on all the column of the resultSet
     * @param individualSearch search term on a dedicated column of the
     * resultSet
     * @return
     */
    List<User> findUserListByCriteria(int start, int amount, String column, String dir, String searchTerm, String individualSearch);
    
    /**
     * 
     * @param searchTerm words to be searched in every column (Exemple : article)
     * @param inds part of the script to add to where clause (Exemple : `type` = 'Article')
     * @return The number of records for these criterias
     */
    Integer getNumberOfUserPerCrtiteria(String searchTerm, String inds);
    
    /**
     * @param login
     * @return the user that match the login
     * @throws CerberusException
     */
    User findUserByKeyWithDependencies(String login) throws CerberusException;

    List<User> findAllUserBySystem(String system);
}
