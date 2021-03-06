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
package org.cerberus.serviceEngine.impl;

import java.util.Date;
import org.apache.log4j.Level;
import org.cerberus.entity.MessageEvent;
import org.cerberus.entity.MessageEventEnum;
import org.cerberus.entity.MessageGeneral;
import org.cerberus.entity.SoapLibrary;
import org.cerberus.entity.TestCaseExecution;
import org.cerberus.entity.TestCaseExecutionData;
import org.cerberus.entity.TestCaseStepActionExecution;
import org.cerberus.exception.CerberusEventException;
import org.cerberus.exception.CerberusException;
import org.cerberus.log.MyLogger;
import org.cerberus.service.ISoapLibraryService;
import org.cerberus.serviceEngine.IActionService;
import org.cerberus.serviceEngine.IPropertyService;
import org.cerberus.serviceEngine.IRecorderService;
import org.cerberus.serviceEngine.ISoapService;
import org.cerberus.serviceEngine.IWebDriverService;
import org.cerberus.serviceEngine.IXmlUnitService;
import org.cerberus.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author bcivel
 */
@Service
public class ActionService implements IActionService {

    @Autowired
    private IPropertyService propertyService;
    @Autowired
    private IWebDriverService webdriverService;
    @Autowired
    private ISoapService soapService;
    @Autowired
    private ISoapLibraryService soapLibraryService;
    @Autowired
    private IRecorderService recorderService;
    @Autowired
    private IXmlUnitService xmlUnitService;

    @Override
    public TestCaseStepActionExecution doAction(TestCaseStepActionExecution testCaseStepActionExecution) {
        MessageEvent res;

        /**
         * Decode the object field before doing the action.
         */
        if (testCaseStepActionExecution.getObject().contains("%")) {
            boolean isCalledFromCalculateProperty = false;
            if (testCaseStepActionExecution.getAction().equals("calculateProperty")) {
                isCalledFromCalculateProperty = true;
            }
            try {
                testCaseStepActionExecution.setObject(propertyService.getValue(testCaseStepActionExecution.getObject(), testCaseStepActionExecution, isCalledFromCalculateProperty));
            } catch (CerberusEventException cex) {
                testCaseStepActionExecution.setActionResultMessage(cex.getMessageError());
                testCaseStepActionExecution.setExecutionResultMessage(new MessageGeneral(cex.getMessageError().getMessage()));
                return testCaseStepActionExecution;
            }
        }

        /**
         * Timestamp starts after the decode. TODO protect when property is
         * null.
         */
        testCaseStepActionExecution.setStart(new Date().getTime());

        String object = testCaseStepActionExecution.getObject();
        String property = testCaseStepActionExecution.getProperty();
        String propertyName = testCaseStepActionExecution.getPropertyName();
        MyLogger.log(RunTestCaseService.class.getName(), Level.DEBUG, "Doing Action : " + testCaseStepActionExecution.getAction() + " with object : " + object + " and property : " + property);

        TestCaseExecution tCExecution = testCaseStepActionExecution.getTestCaseStepExecution().gettCExecution();
        //TODO On JDK 7 implement switch with string
        if (testCaseStepActionExecution.getAction().equals("click")) {
            res = this.doActionClick(tCExecution, object, property);

        } else if (testCaseStepActionExecution.getAction().equals("clickAndWait")) {
            res = this.doActionClickWait(tCExecution, object, property);

        } else if (testCaseStepActionExecution.getAction().equals("doubleClick")) {
            res = this.doActionDoubleClick(tCExecution, object, property);

        } else if (testCaseStepActionExecution.getAction().equals("enter")) {
            res = this.doActionKeyPress(tCExecution, object, "RETURN");

        } else if (testCaseStepActionExecution.getAction().equals("keypress")) {
            res = this.doActionKeyPress(tCExecution, object, property);

        } else if (testCaseStepActionExecution.getAction().equals("mouseOver")) {
            res = this.doActionMouseOver(tCExecution, object, property);

        } else if (testCaseStepActionExecution.getAction().equals("mouseOverAndWait")) {
            res = this.doActionMouseOverAndWait(tCExecution, object, property);

        } else if (testCaseStepActionExecution.getAction().equals("openUrlWithBase")) {
            res = this.doActionOpenURLWithBase(tCExecution, object, property);

        } else if (testCaseStepActionExecution.getAction().equals("openUrl")) {
            res = this.doActionOpenURL(tCExecution, object, property);

        } else if (testCaseStepActionExecution.getAction().equals("openUrlLogin")) {
            testCaseStepActionExecution.setObject(testCaseStepActionExecution.getTestCaseStepExecution().gettCExecution().getCountryEnvironmentApplication().getUrlLogin());
            res = this.doActionUrlLogin(tCExecution);

        } else if (testCaseStepActionExecution.getAction().equals("select")) {
            res = this.doActionSelect(tCExecution, object, property);

        } else if (testCaseStepActionExecution.getAction().equals("selectAndWait")) {
            res = this.doActionSelect(tCExecution, object, property);
            this.doActionWait(tCExecution, StringUtil.NULL, StringUtil.NULL);

        } else if (testCaseStepActionExecution.getAction().equals("focusToIframe")) {
            res = this.doActionFocusToIframe(tCExecution, object, property);

        } else if (testCaseStepActionExecution.getAction().equals("focusDefaultIframe")) {
            res = this.doActionFocusDefaultIframe(tCExecution);

        } else if (testCaseStepActionExecution.getAction().equals("type")) {
            res = this.doActionType(tCExecution, object, property, propertyName);

        } else if (testCaseStepActionExecution.getAction().equals("wait")) {
            res = this.doActionWait(tCExecution, object, property);

        } else if (testCaseStepActionExecution.getAction().equals("mouseDown")) {
            res = this.doActionMouseDown(tCExecution, object, property);

        } else if (testCaseStepActionExecution.getAction().equals("mouseUp")) {
            res = this.doActionMouseUp(tCExecution, object, property);

        } else if (testCaseStepActionExecution.getAction().equals("switchToWindow")) {
            res = this.doActionSwitchToWindow(tCExecution, object, property);

        } else if (testCaseStepActionExecution.getAction().equals("manageDialog")) {
            res = this.doActionManageDialog(tCExecution, object, property);

        } else if (testCaseStepActionExecution.getAction().equals("callSoapWithBase")) {
            res = this.doActionMakeSoapCall(testCaseStepActionExecution, object, true);

        } else if (testCaseStepActionExecution.getAction().equals("callSoap")) {
            res = this.doActionMakeSoapCall(testCaseStepActionExecution, object, false);

        } else if (testCaseStepActionExecution.getAction().equals("mouseDownMouseUp")) {
            res = this.doActionMouseDownMouseUp(tCExecution, object, property);

        } else if (testCaseStepActionExecution.getAction().equals("calculateProperty")) {
            res = new MessageEvent(MessageEventEnum.ACTION_SUCCESS_PROPERTYCALCULATED);
            res.setDescription(res.getDescription().replaceAll("%PROP%", propertyName));
        } else if (testCaseStepActionExecution.getAction().equals("takeScreenshot")) {
            res = this.doActionTakeScreenshot(testCaseStepActionExecution);
            //res = new MessageEvent(MessageEventEnum.ACTION_SUCCESS_TAKESCREENSHOT);
        } else if (testCaseStepActionExecution.getAction().equals("getPageSource")) {
            res = this.doActionGetPageSource(testCaseStepActionExecution);
        } else if (testCaseStepActionExecution.getAction().equals("removeDifference")) {
            res = this.doActionRemoveDifference(testCaseStepActionExecution, object, property);
        } else {
            res = new MessageEvent(MessageEventEnum.ACTION_FAILED_UNKNOWNACTION);
            res.setDescription(res.getDescription().replaceAll("%ACTION%", testCaseStepActionExecution.getAction()));
        }

        MyLogger.log(RunTestCaseService.class.getName(), Level.DEBUG, "Result of the action : " + res.getCodeString() + " " + res.getDescription());
        testCaseStepActionExecution.setActionResultMessage(res);

        /**
         * Determine here the impact of the Action on the full test return code
         * from the ResultMessage of the Action.
         */
        testCaseStepActionExecution.setExecutionResultMessage(new MessageGeneral(res.getMessage()));
        /**
         * Determine here if we stop the test from the ResultMessage of the
         * Action.
         */
        testCaseStepActionExecution.setStopExecution(res.isStopTest());

        testCaseStepActionExecution.setEnd(new Date().getTime());
        return testCaseStepActionExecution;
    }

    private MessageEvent doActionClick(TestCaseExecution tCExecution, String string1, String string2) {
        MessageEvent message;
        if (tCExecution.getApplication().getType().equalsIgnoreCase("GUI")) {
            return webdriverService.doSeleniumActionClick(tCExecution.getSession(), string1, string2, true, true);
        } else if ( tCExecution.getApplication().getType().equalsIgnoreCase("APK")){
            return webdriverService.doSeleniumActionClick(tCExecution.getSession(), string1, string2, true, false);
        }
        message = new MessageEvent(MessageEventEnum.ACTION_NOTEXECUTED_NOTSUPPORTED_FOR_APPLICATION);
        message.setDescription(message.getDescription().replaceAll("%ACTION%", "Click"));
        message.setDescription(message.getDescription().replaceAll("%APPLICATIONTYPE%", string1));
        return message;
    }

    private MessageEvent doActionMouseDown(TestCaseExecution tCExecution, String string1, String string2) {
        MessageEvent message;
        if (tCExecution.getApplication().getType().equalsIgnoreCase("GUI")) {
            return webdriverService.doSeleniumActionMouseDown(tCExecution.getSession(), string1, string2);
        }
        message = new MessageEvent(MessageEventEnum.ACTION_NOTEXECUTED_NOTSUPPORTED_FOR_APPLICATION);
        message.setDescription(message.getDescription().replaceAll("%ACTION%", "MouseDown"));
        message.setDescription(message.getDescription().replaceAll("%APPLICATIONTYPE%", string1));
        return message;
    }

    private MessageEvent doActionMouseUp(TestCaseExecution tCExecution, String string1, String string2) {
        MessageEvent message;
        if (tCExecution.getApplication().getType().equalsIgnoreCase("GUI")) {
            return webdriverService.doSeleniumActionMouseUp(tCExecution.getSession(), string1, string2);
        }
        message = new MessageEvent(MessageEventEnum.ACTION_NOTEXECUTED_NOTSUPPORTED_FOR_APPLICATION);
        message.setDescription(message.getDescription().replaceAll("%ACTION%", "MouseUp"));
        message.setDescription(message.getDescription().replaceAll("%APPLICATIONTYPE%", string1));
        return message;
    }

    private MessageEvent doActionSwitchToWindow(TestCaseExecution tCExecution, String string1, String string2) {
        MessageEvent message;
        if (tCExecution.getApplication().getType().equalsIgnoreCase("GUI")) {
            return webdriverService.doSeleniumActionSwitchToWindow(tCExecution.getSession(), string1, string2);
        }
        message = new MessageEvent(MessageEventEnum.ACTION_NOTEXECUTED_NOTSUPPORTED_FOR_APPLICATION);
        message.setDescription(message.getDescription().replaceAll("%ACTION%", "SwitchToWindow"));
        message.setDescription(message.getDescription().replaceAll("%APPLICATIONTYPE%", string1));
        return message;
    }

    private MessageEvent doActionManageDialog(TestCaseExecution tCExecution, String string1, String string2) {
        MessageEvent message;
        if (tCExecution.getApplication().getType().equalsIgnoreCase("GUI")) {
            return webdriverService.doSeleniumActionManageDialog(tCExecution.getSession(), string1, string2);
        }
        message = new MessageEvent(MessageEventEnum.ACTION_NOTEXECUTED_NOTSUPPORTED_FOR_APPLICATION);
        message.setDescription(message.getDescription().replaceAll("%ACTION%", "ManageDialog"));
        message.setDescription(message.getDescription().replaceAll("%APPLICATIONTYPE%", string1));
        return message;
    }

    private MessageEvent doActionClickWait(TestCaseExecution tCExecution, String string1, String string2) {
        MessageEvent message;
        if (tCExecution.getApplication().getType().equalsIgnoreCase("GUI")) {
            return webdriverService.doSeleniumActionClickWait(tCExecution.getSession(), string1, string2);
        }
        message = new MessageEvent(MessageEventEnum.ACTION_NOTEXECUTED_NOTSUPPORTED_FOR_APPLICATION);
        message.setDescription(message.getDescription().replaceAll("%ACTION%", "ClickAndWait"));
        message.setDescription(message.getDescription().replaceAll("%APPLICATIONTYPE%", string1));
        return message;
    }

    private MessageEvent doActionDoubleClick(TestCaseExecution tCExecution, String string1, String string2) {
        MessageEvent message;
        if (tCExecution.getApplication().getType().equalsIgnoreCase("GUI")
                || tCExecution.getApplication().getType().equalsIgnoreCase("APK")) {
            return webdriverService.doSeleniumActionDoubleClick(tCExecution.getSession(), string1, string2);
        }
        message = new MessageEvent(MessageEventEnum.ACTION_NOTEXECUTED_NOTSUPPORTED_FOR_APPLICATION);
        message.setDescription(message.getDescription().replaceAll("%ACTION%", "ClickAndWait"));
        message.setDescription(message.getDescription().replaceAll("%APPLICATIONTYPE%", string1));
        return message;
    }

    private MessageEvent doActionType(TestCaseExecution tCExecution, String html, String property, String propertyName) {
        MessageEvent message;
        if (tCExecution.getApplication().getType().equalsIgnoreCase("GUI")
                || tCExecution.getApplication().getType().equalsIgnoreCase("APK")) {
            return webdriverService.doSeleniumActionType(tCExecution.getSession(), html, property, propertyName);
        }
        message = new MessageEvent(MessageEventEnum.ACTION_NOTEXECUTED_NOTSUPPORTED_FOR_APPLICATION);
        message.setDescription(message.getDescription().replaceAll("%ACTION%", "ClickAndWait"));
        message.setDescription(message.getDescription().replaceAll("%APPLICATIONTYPE%", tCExecution.getApplication().getType()));
        return message;
    }

    private MessageEvent doActionMouseOver(TestCaseExecution tCExecution, String html, String property) {
        MessageEvent message;
        if (tCExecution.getApplication().getType().equalsIgnoreCase("GUI")) {
            return webdriverService.doSeleniumActionMouseOver(tCExecution.getSession(), html, property);
        }
        message = new MessageEvent(MessageEventEnum.ACTION_NOTEXECUTED_NOTSUPPORTED_FOR_APPLICATION);
        message.setDescription(message.getDescription().replaceAll("%ACTION%", "ClickAndWait"));
        message.setDescription(message.getDescription().replaceAll("%APPLICATIONTYPE%", tCExecution.getApplication().getType()));
        return message;
    }

    private MessageEvent doActionMouseOverAndWait(TestCaseExecution tCExecution, String actionObject, String actionProperty) {
        MessageEvent message;
        if (tCExecution.getApplication().getType().equalsIgnoreCase("GUI")) {
            return webdriverService.doSeleniumActionMouseOverAndWait(tCExecution.getSession(), actionObject, actionProperty);
        }
        message = new MessageEvent(MessageEventEnum.ACTION_NOTEXECUTED_NOTSUPPORTED_FOR_APPLICATION);
        message.setDescription(message.getDescription().replaceAll("%ACTION%", "ClickAndWait"));
        message.setDescription(message.getDescription().replaceAll("%APPLICATIONTYPE%", tCExecution.getApplication().getType()));
        return message;
    }

    private MessageEvent doActionWait(TestCaseExecution tCExecution, String object, String property) {
        MessageEvent message;
        if (tCExecution.getApplication().getType().equalsIgnoreCase("GUI")
                || tCExecution.getApplication().getType().equalsIgnoreCase("APK")) {
            return webdriverService.doSeleniumActionWait(tCExecution.getSession(), object, property);
        }
        if (tCExecution.getApplication().getType().equalsIgnoreCase("CMP")) {
            try {
                if (!StringUtil.isNull(object) && StringUtil.isNumeric(object)) {
                    Thread.sleep(Integer.parseInt(object));
                    message = new MessageEvent(MessageEventEnum.ACTION_SUCCESS_WAIT_TIME);
                    message.setDescription(message.getDescription().replaceAll("%TIME%", object));
                    return message;
                } else if (!StringUtil.isNull(property) && StringUtil.isNumeric(property)) {
                    Thread.sleep(Integer.parseInt(property));
                    message = new MessageEvent(MessageEventEnum.ACTION_SUCCESS_WAIT_TIME);
                    message.setDescription(message.getDescription().replaceAll("%TIME%", property));
                    return message;
                } else if (StringUtil.isNull(object) && StringUtil.isNull(property)){
                    Thread.sleep(1000*tCExecution.getSession().getDefaultWait());
                    message = new MessageEvent(MessageEventEnum.ACTION_SUCCESS_WAIT_TIME);
                    message.setDescription(message.getDescription().replaceAll("%TIME%", String.valueOf(1000*tCExecution.getSession().getDefaultWait())));
                    return message;
                } else {
                message = new MessageEvent(MessageEventEnum.ACTION_FAILED_WAIT_INVALID_FORMAT);
                return message;
                }
            } catch (InterruptedException exception) {
                MyLogger.log(ActionService.class.getName(), Level.INFO, exception.toString());
                message = new MessageEvent(MessageEventEnum.ACTION_FAILED_WAIT);
                message.setDescription(message.getDescription().replaceAll("%TIME%", property));
                return message;
            }
        }
        message = new MessageEvent(MessageEventEnum.ACTION_NOTEXECUTED_NOTSUPPORTED_FOR_APPLICATION);
        message.setDescription(message.getDescription().replaceAll("%ACTION%", "ClickAndWait"));
        message.setDescription(message.getDescription().replaceAll("%APPLICATIONTYPE%", tCExecution.getApplication().getType()));
        return message;
    }

    private MessageEvent doActionKeyPress(TestCaseExecution tCExecution, String html, String property) {
        MessageEvent message;
        if (tCExecution.getApplication().getType().equalsIgnoreCase("GUI")) {
            return webdriverService.doSeleniumActionKeyPress(tCExecution.getSession(), html, property);
        }
        message = new MessageEvent(MessageEventEnum.ACTION_NOTEXECUTED_NOTSUPPORTED_FOR_APPLICATION);
        message.setDescription(message.getDescription().replaceAll("%ACTION%", "KeyPress"));
        message.setDescription(message.getDescription().replaceAll("%APPLICATIONTYPE%", tCExecution.getApplication().getType()));
        return message;
    }

    private MessageEvent doActionOpenURLWithBase(TestCaseExecution tCExecution, String value, String property) {
        MessageEvent message;
        if (tCExecution.getApplication().getType().equalsIgnoreCase("GUI")) {
            return webdriverService.doSeleniumActionOpenURL(tCExecution.getSession(), tCExecution.getUrl(), value, property, true);
        }
        message = new MessageEvent(MessageEventEnum.ACTION_NOTEXECUTED_NOTSUPPORTED_FOR_APPLICATION);
        message.setDescription(message.getDescription().replaceAll("%ACTION%", "OpenURLWithBase"));
        message.setDescription(message.getDescription().replaceAll("%APPLICATIONTYPE%", tCExecution.getApplication().getType()));
        return message;
    }

    private MessageEvent doActionOpenURL(TestCaseExecution tCExecution, String value, String property) {
        MessageEvent message;
        if (tCExecution.getApplication().getType().equalsIgnoreCase("GUI")) {
            return webdriverService.doSeleniumActionOpenURL(tCExecution.getSession(), tCExecution.getUrl(), value, property, false);
        }
        message = new MessageEvent(MessageEventEnum.ACTION_NOTEXECUTED_NOTSUPPORTED_FOR_APPLICATION);
        message.setDescription(message.getDescription().replaceAll("%ACTION%", "OpenURL"));
        message.setDescription(message.getDescription().replaceAll("%APPLICATIONTYPE%", tCExecution.getApplication().getType()));
        return message;
    }

    private MessageEvent doActionSelect(TestCaseExecution tCExecution, String html, String property) {
        MessageEvent message;
        if (tCExecution.getApplication().getType().equalsIgnoreCase("GUI")
                || tCExecution.getApplication().getType().equalsIgnoreCase("APK")) {
            return webdriverService.doSeleniumActionSelect(tCExecution.getSession(), html, property);
        }
        message = new MessageEvent(MessageEventEnum.ACTION_NOTEXECUTED_NOTSUPPORTED_FOR_APPLICATION);
        message.setDescription(message.getDescription().replaceAll("%ACTION%", "Select"));
        message.setDescription(message.getDescription().replaceAll("%APPLICATIONTYPE%", tCExecution.getApplication().getType()));
        return message;
    }

    private MessageEvent doActionUrlLogin(TestCaseExecution tCExecution) {
        MessageEvent message;
        if (tCExecution.getApplication().getType().equalsIgnoreCase("GUI")) {
            return webdriverService.doSeleniumActionUrlLogin(tCExecution.getSession(), tCExecution.getUrl(), tCExecution.getCountryEnvironmentApplication().getUrlLogin());
        }
        message = new MessageEvent(MessageEventEnum.ACTION_NOTEXECUTED_NOTSUPPORTED_FOR_APPLICATION);
        message.setDescription(message.getDescription().replaceAll("%ACTION%", "UrlLogin"));
        message.setDescription(message.getDescription().replaceAll("%APPLICATIONTYPE%", tCExecution.getApplication().getType()));
        return message;
    }

    private MessageEvent doActionFocusToIframe(TestCaseExecution tCExecution, String object, String property) {
        MessageEvent message;
        if (tCExecution.getApplication().getType().equalsIgnoreCase("GUI")) {
            return webdriverService.doSeleniumActionFocusToIframe(tCExecution.getSession(), object, property);
        }
        message = new MessageEvent(MessageEventEnum.ACTION_NOTEXECUTED_NOTSUPPORTED_FOR_APPLICATION);
        message.setDescription(message.getDescription().replaceAll("%ACTION%", "FocusToIframe"));
        message.setDescription(message.getDescription().replaceAll("%APPLICATIONTYPE%", tCExecution.getApplication().getType()));
        return message;
    }

    private MessageEvent doActionFocusDefaultIframe(TestCaseExecution tCExecution) {
        MessageEvent message;
        if (tCExecution.getApplication().getType().equalsIgnoreCase("GUI")) {
            return webdriverService.doSeleniumActionFocusDefaultIframe(tCExecution.getSession());
        }
        message = new MessageEvent(MessageEventEnum.ACTION_NOTEXECUTED_NOTSUPPORTED_FOR_APPLICATION);
        message.setDescription(message.getDescription().replaceAll("%ACTION%", "FocusDefaultIframe"));
        message.setDescription(message.getDescription().replaceAll("%APPLICATIONTYPE%", tCExecution.getApplication().getType()));
        return message;

    }

    private MessageEvent doActionMakeSoapCall(TestCaseStepActionExecution testCaseStepActionExecution, String object, boolean withBase) {
        MessageEvent message;
        TestCaseExecution tCExecution = testCaseStepActionExecution.getTestCaseStepExecution().gettCExecution();
        //if (tCExecution.getApplication().getType().equalsIgnoreCase("WS")) {
        try {
            SoapLibrary soapLibrary = soapLibraryService.findSoapLibraryByKey(object);
            String servicePath;
            if (withBase) {
                servicePath = tCExecution.getCountryEnvironmentApplication().getIp();
            } else {
                servicePath = soapLibrary.getServicePath();
            }
            /**
             * Decode Enveloppe replacing properties encaplsulated with %
             */
            String decodedEnveloppe = soapLibrary.getEnvelope();
            if (soapLibrary.getEnvelope().contains("%")) {
                try {
                    decodedEnveloppe = propertyService.getValue(soapLibrary.getEnvelope(), testCaseStepActionExecution, false);
                } catch (CerberusEventException cee) {
                    message = new MessageEvent(MessageEventEnum.ACTION_FAILED_CALLSOAP);
                    message.setDescription(message.getDescription().replaceAll("%SOAPNAME%", object));
                    message.setDescription(message.getDescription().replaceAll("%DESCRIPTION%", cee.getMessageError().getDescription()));
                    return message;
                }
            }
            return soapService.callSOAPAndStoreResponseInMemory(tCExecution.getExecutionUUID(), decodedEnveloppe, servicePath, soapLibrary.getMethod());
        } catch (CerberusException ex) {
            message = new MessageEvent(MessageEventEnum.ACTION_FAILED_CALLSOAP);
            message.setDescription(message.getDescription().replaceAll("%SOAPNAME%", object));
            message.setDescription(message.getDescription().replaceAll("%DESCRIPTION%", ex.getMessageError().getDescription()));
            return message;
        }
        //}
    }

    private MessageEvent doActionMouseDownMouseUp(TestCaseExecution tCExecution, String object, String property) {
        MessageEvent message;
        if (tCExecution.getApplication().getType().equalsIgnoreCase("GUI")) {
            return webdriverService.doSeleniumActionMouseDownMouseUp(tCExecution.getSession(), object, property);
        }
        message = new MessageEvent(MessageEventEnum.ACTION_NOTEXECUTED_NOTSUPPORTED_FOR_APPLICATION);
        message.setDescription(message.getDescription().replaceAll("%ACTION%", "Click"));
        message.setDescription(message.getDescription().replaceAll("%APPLICATIONTYPE%", tCExecution.getApplication().getType()));
        return message;
    }

    private MessageEvent doActionTakeScreenshot(TestCaseStepActionExecution testCaseStepActionExecution) {
        MessageEvent message;
        if (testCaseStepActionExecution.getTestCaseStepExecution().gettCExecution().getApplication().getType().equalsIgnoreCase("GUI")
                || testCaseStepActionExecution.getTestCaseStepExecution().gettCExecution().getApplication().getType().equalsIgnoreCase("APK")) {
            String screenshotPath = recorderService.recordScreenshotAndGetName(testCaseStepActionExecution.getTestCaseStepExecution().gettCExecution(),
                    testCaseStepActionExecution, 0);
            testCaseStepActionExecution.setScreenshotFilename(screenshotPath);
            message = new MessageEvent(MessageEventEnum.ACTION_SUCCESS_TAKESCREENSHOT);
            return message;
        }
        message = new MessageEvent(MessageEventEnum.ACTION_NOTEXECUTED_NOTSUPPORTED_FOR_APPLICATION);
        message.setDescription(message.getDescription().replaceAll("%ACTION%", "TakeScreenShot"));
        message.setDescription(message.getDescription().replaceAll("%APPLICATIONTYPE%", testCaseStepActionExecution.getTestCaseStepExecution().gettCExecution().getApplication().getType()));
        return message;
    }

    private MessageEvent doActionGetPageSource(TestCaseStepActionExecution testCaseStepActionExecution) {
        MessageEvent message;
        if (testCaseStepActionExecution.getTestCaseStepExecution().gettCExecution().getApplication().getType().equalsIgnoreCase("GUI")
                || testCaseStepActionExecution.getTestCaseStepExecution().gettCExecution().getApplication().getType().equalsIgnoreCase("APK")) {
            String screenshotPath = recorderService.recordPageSourceAndGetName(testCaseStepActionExecution.getTestCaseStepExecution().gettCExecution(),
                    testCaseStepActionExecution, 0);
            testCaseStepActionExecution.setScreenshotFilename(screenshotPath);
            message = new MessageEvent(MessageEventEnum.ACTION_SUCCESS_GETPAGESOURCE);
            return message;
        }
        message = new MessageEvent(MessageEventEnum.ACTION_NOTEXECUTED_NOTSUPPORTED_FOR_APPLICATION);
        message.setDescription(message.getDescription().replaceAll("%ACTION%", "Click"));
        message.setDescription(message.getDescription().replaceAll("%APPLICATIONTYPE%", testCaseStepActionExecution.getTestCaseStepExecution().gettCExecution().getApplication().getType()));
        return message;
    }

    private MessageEvent doActionRemoveDifference(TestCaseStepActionExecution testCaseStepActionExecution, String object, String property) {
        // Filters differences from the given object pattern
        String filteredDifferences = xmlUnitService.removeDifference(object, property);

        // If filtered differences are null then service has returned with errors
        if (filteredDifferences == null) {
            MessageEvent message = new MessageEvent(MessageEventEnum.ACTION_FAILED_REMOVEDIFFERENCE);
            message.setDescription(message.getDescription().replaceAll("%DIFFERENCE%", object));
            message.setDescription(message.getDescription().replaceAll("%DIFFERENCES%", property));
            return message;
        }

        // Sets the property value to the new filtered one
        for (TestCaseExecutionData data : testCaseStepActionExecution.getTestCaseExecutionDataList()) {
            if (data.getProperty().equals(testCaseStepActionExecution.getPropertyName())) {
                data.setValue(filteredDifferences);
                break;
            }
        }

        // Sends success
        MessageEvent message = new MessageEvent(MessageEventEnum.ACTION_SUCCESS_REMOVEDIFFERENCE);
        message.setDescription(message.getDescription().replaceAll("%DIFFERENCE%", object));
        message.setDescription(message.getDescription().replaceAll("%DIFFERENCES%", property));
        return message;
    }

}
