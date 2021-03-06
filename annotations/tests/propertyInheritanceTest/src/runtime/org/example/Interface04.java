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

package org.example;

import com.sun.faces.annotation.Component;
import com.sun.faces.annotation.Property;
import org.example.base.SuperBean01;

public interface Interface04 {
    
    @Property
    public String getOne();

    public void setOne(String one);
    
    @Property(displayName="The Second", isAttribute=false)
    public String getTwo();

    public void setTwo(String two);
    
    @Property(displayName="Bad Value")
    public String getThree();

    public void setThree(String three);
    
}
