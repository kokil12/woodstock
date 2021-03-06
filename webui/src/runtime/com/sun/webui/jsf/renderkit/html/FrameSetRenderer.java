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

 /*
  * $Id: FrameSetRenderer.java,v 1.2 2007-07-10 06:52:25 venkatesh045 Exp $
  */

package com.sun.webui.jsf.renderkit.html;

import com.sun.faces.annotation.Renderer;
import java.io.IOException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import com.sun.webui.jsf.component.FrameSet;
import com.sun.webui.jsf.util.RenderingUtilities;

/**
 * <p>Renderer for a {@link FrameSet} component.</p>
 */

@Renderer(@Renderer.Renders(componentFamily="com.sun.webui.jsf.FrameSet"))
public class FrameSetRenderer extends AbstractRenderer {
    
    
    // ======================================================== Static Variables

     /**
     * <p>The set of String pass-through attributes to be rendered.</p>
     */
    private static final String stringAttributes[] =
    { "rows", "cols", "borderColor"}; //NOI18N
    private static final String integerAttributes[] =
    { "border", "frameSpacing"}; //NOI18N

    
    // -------------------------------------------------------- Renderer Methods
    
    
    /**
     * <p>Render the appropriate element start for the outermost
     * element.</p>
     *
     * @param context <code>FacesContext</code> for the current request
     * @param component component to be rendered
     * @param writer <code>ResponseWriter</code> to which the element
     *  start should be rendered
     *
     * @exception IOException if an input/output error occurs
     */
    protected void renderStart(FacesContext context, UIComponent component,
            ResponseWriter writer) throws IOException {
        FrameSet frameset = (FrameSet) component;
        
        // I don't think this is the correct way to write the XML
        // header /avk
        
        if (!RenderingUtilities.isPortlet(context)) {
            writer.startElement("frameset", component);
       }
        
    }
    
    
    
    /**
     * <p>Render the appropriate element attributes, followed by the
     * nested <code>&lt;head&gt;</code> element, plus the beginning
     * of a nested <code>&lt;body&gt;</code> element.</p>
     *
     * @param context <code>FacesContext</code> for the current request
     * @param component component to be rendered
     * @param writer <code>ResponseWriter</code> to which the element
     *  start should be rendered
     *
     * @exception IOException if an input/output error occurs
     */
    protected void renderAttributes(FacesContext context, UIComponent component,
            ResponseWriter writer) throws IOException {
        
        FrameSet frameset = (FrameSet) component;
        
        // Render a nested "head" element
        if (!RenderingUtilities.isPortlet(context)) {
            //id
            String id=frameset.getClientId(context);
            if (id != null) {
                writer.writeAttribute("id", id, null); //NOI18N
            }
            //class
            String styleClass = frameset.getStyleClass();
            if (styleClass != null) {
                writer.writeAttribute("class", styleClass, null); //NOI18N
            }
            //style
            String style = frameset.getStyle();
            if (style != null) {
                writer.writeAttribute("style", style, null); //NOI18N
            }
            //tooltip
            String toolTip = frameset.getToolTip();
            if (toolTip != null) {
                writer.writeAttribute("title", toolTip, "toolTip"); //NOI18N
            }
            //write out the rest of the attributes
            addStringAttributes(context, component, writer, stringAttributes);
            addIntegerAttributes(context, component, writer, integerAttributes);
            if (frameset.isFrameBorder()) {
                writer.writeAttribute("frameBorder","1", "frameBorder");//NOI18N
            } else {
                writer.writeAttribute("frameBorder","0", "frameBorder");//NOI18N
            }
            writer.write("\n"); //NOI18N
         }
        
    }
    /**
     * <p>Render the appropriate element end.</p>
     *
     * @param context <code>FacesContext</code> for the current request
     * @param component component to be rendered
     * @param writer <code>ResponseWriter</code> to which the element
     *  start should be rendered
     *
     * @exception IOException if an input/output error occurs
     */
    protected void renderEnd(FacesContext context, UIComponent component,
            ResponseWriter writer) throws IOException {
        
        FrameSet frameset = (FrameSet) component;
        
        // End the outermost "html" element
        if (!RenderingUtilities.isPortlet(context)) {
            writer.endElement("frameset"); //NOI18N
            writer.write("\n"); //NOI18N
        }
                
    }
    
    
    // --------------------------------------------------------- Private Methods
}
