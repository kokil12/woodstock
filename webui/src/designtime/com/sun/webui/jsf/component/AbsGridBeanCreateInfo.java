/*
 * The contents of this file are subject to the terms
 * of the Common Development and Distribution License
 * (the License).  You may not use this file except in
 * compliance with the License.
 * 
 * You can obtain a copy of the license at
 * https://woodstock.dev.java.net/public/CDDLv1.0.html.
 * See the License for the specific language governing
 * permissions and limitations under the License.
 * 
 * When distributing Covered Code, include this CDDL
 * Header Notice in each file and include the License file
 * at https://woodstock.dev.java.net/public/CDDLv1.0.html.
 * If applicable, add the following below the CDDL Header,
 * with the fields enclosed by brackets [] replaced by
 * you own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 * 
 * Copyright 2007 Sun Microsystems, Inc. All rights reserved.
 */
package com.sun.webui.jsf.component;

import java.awt.Image;
import java.beans.BeanDescriptor;
import java.beans.EventSetDescriptor;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;

import javax.faces.event.ActionEvent;

import com.sun.rave.faces.event.Action;
import com.sun.rave.designtime.*;
import com.sun.rave.designtime.Constants;
import com.sun.webui.jsf.component.util.DesignMessageUtil;


/**
 * BeanCreateInfo which creates a Group Panel with Grid Positioning
 *
 * @author Tor Norbye
 */
public class AbsGridBeanCreateInfo implements BeanCreateInfo {
    public AbsGridBeanCreateInfo() {
    }

    public String getBeanClassName() {
        return "com.sun.webui.jsf.component.PanelGroup";
    }

    public Result beanCreatedSetup(DesignBean bean) {
        DesignContext context = bean.getDesignContext();

        // Force to block (div)
        DesignProperty property = bean.getProperty("block");

        if (property != null) {
            property.setValue(Boolean.TRUE);
        }

        // Style
        property = bean.getProperty("style");

        if (property != null) {
            String s =
                "-rave-layout: grid; position: relative; background-color: white; border: solid 1px gray; width: 200px; height: 200px;";
            String style = (String)property.getValue();

            if ((style != null) && (style.length() > 0)) {
                s = s + style;
            }

            property.setValue(s);
        }

        return Result.SUCCESS;
    }

    public String getDisplayName() {
        return DesignMessageUtil.getMessage(AbsGridBeanCreateInfo.class, "absGrid.name");
    }

    public String getDescription() {
        return DesignMessageUtil.getMessage(AbsGridBeanCreateInfo.class, "absGrid.tip");
    }

    public Image getLargeIcon() {
        return null;
    }

    public Image getSmallIcon() {
        String iconFileName_C16 = "AbsGrid_C16";

        String name;
        name = iconFileName_C16;

        if (name == null) {
            return null;
        }

        Image image = TabGridBeanCreateInfo.loadImage(name + ".png");

        if (image == null) {
            image = TabGridBeanCreateInfo.loadImage(name + ".gif");
        }

        return image;

        //return null;
    }

    public String getHelpKey() {
        return null;
    }
}
