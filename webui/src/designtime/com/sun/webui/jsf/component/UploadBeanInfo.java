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

import java.beans.BeanDescriptor;

import com.sun.rave.designtime.Constants;
import com.sun.webui.jsf.component.util.DesignUtil;
import java.beans.EventSetDescriptor;

/**
 * BeanInfo for the {@link com.sun.webui.jsf.component.Upload}component.
 */
public class UploadBeanInfo extends UploadBeanInfoBase {
    
    public UploadBeanInfo() {
        DesignUtil.hideProperties(this, new String[]{"converter", "maxLength"});
    }
    
    public BeanDescriptor getBeanDescriptor() {
        BeanDescriptor beanDescriptor = super.getBeanDescriptor();
        // Do not allow component to be resized
        // This is set here rather than in XML metadata to get IDE support, but
        // maybe this isn't a good enough reason.
        beanDescriptor.setValue(Constants.BeanDescriptor.RESIZE_CONSTRAINTS,
                new Integer(Constants.ResizeConstraints.NONE));
        beanDescriptor.setValue(
            Constants.BeanDescriptor.INLINE_EDITABLE_PROPERTIES,
            new String[] { "label://label" }); // NOI18N
        return beanDescriptor;
    }
    
}
