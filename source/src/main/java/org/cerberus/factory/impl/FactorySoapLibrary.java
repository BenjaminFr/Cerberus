/* DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
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
package org.cerberus.factory.impl;

import org.cerberus.entity.SoapLibrary;
import org.cerberus.factory.IFactorySoapLibrary;
import org.springframework.stereotype.Service;

/**
 *
 * @author cte
 */
@Service
public class FactorySoapLibrary implements IFactorySoapLibrary {

    @Override
    public SoapLibrary create(String type, String name, String envelope, String description, String servicePath, String parsingAnswer, String method) {
        SoapLibrary s = new SoapLibrary();
        s.setName(name);
        s.setEnvelope(envelope);
        s.setType(type);
        s.setDescription(description);
        s.setServicePath(servicePath);
        s.setParsingAnswer(parsingAnswer);
        s.setMethod(method);
        return s;
    }
}
