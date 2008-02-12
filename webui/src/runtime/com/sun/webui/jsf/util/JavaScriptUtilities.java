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
package com.sun.webui.jsf.util;

import com.sun.faces.extensions.avatar.components.ScriptsComponent;
import com.sun.webui.theme.Theme;
import com.sun.webui.theme.ThemeContext;
import com.sun.webui.jsf.theme.JSFThemeContext;
import com.sun.webui.jsf.theme.ThemeJavascript;

import java.io.IOException;
import java.util.Map;
import java.util.StringTokenizer;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

/**
 * This class provides common methods for rendering JavaScript includes, default
 * properties, etc.
 */
public class JavaScriptUtilities {
    // The key used to enable JavaScript debugging.
    private static final String DEBUG_KEY = "com_sun_webui_jsf_util_debug";
 
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Global methods
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    /**
     * Test flag to enable JavaScript debugging.
     * 
     * @return true if enabled.
     */
    public static boolean isDebug() {
        // Debugging is typically enabled via the head or themeLinks tags.
        // However, "debug" may also be appended URLs as a query param. This
        // allows quick testing when the JSP page cannot be edited directly.
        return (getRequestMap().containsKey(DEBUG_KEY) 
            || getRequestParameterMap().containsKey("debug"));
    }

    /**
     * Set flag to enable JavaScript debugging.
     *
     * @param enable Enable JavaScript debugging.
     */
    public static void setDebug(boolean enable) {
        getRequestMap().put(DEBUG_KEY, (enable) ? Boolean.TRUE : null);
    }

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // JavaScript methods
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    /**
     * Returns JavaScript to obtain the DOM node associated with the given
     * component.
     * 
     * When complex components are rendered, a DOM object corresponding to the
     * component is created. To manipulate the component on the client side, we
     * will invoke functions on the DOM object.
     * 
     * Providing a component, with a client id of "form1:btn1", will return
     * "document.getElementById('form1:btn1')". This JavaScript obtains the
     * DOM object associated the HTML element. Thus, we can disable a button 
     * using "document.getElementById('form1:btn1').disable(true);"
     *
     * @param context The current FacesContext.
     * @param component The current component being rendered.
     */
    public static String getDomNode(FacesContext context,
            UIComponent component) {
        StringBuffer buff = new StringBuffer(128);
        buff.append("document.getElementById('")
            .append(component.getClientId(context))
            .append("')");
        return buff.toString();
    }

    /**
     * Returns JavaScript used to require a Dojo module. For example, a value of
     * For example, a value of "widget.*" will return
     * "webui.suntheme.dojo.require('webui.suntheme.widget.*')" for a theme, 
     * named "suntheme".
     *
     * @param name The JavaScript object name to append.
     */
    public static String getModule(String name) {
        StringBuffer buff = new StringBuffer(128);
        buff.append(getModuleName("dojo"))
            .append(".require('")
            .append(getModuleName(name))
            .append("');");
        return buff.toString();
    }

    /**
     * Returns a string comprised of a theme prifix and the given module name.
     * For example, a value of "widget.button" will return 
     * "webui.suntheme.widget.button" for a theme, named "suntheme".
     *
     * @param name The module to append to the theme prefix.
     */
    public static String getModuleName(String name) {
        StringBuffer buff = new StringBuffer(128);
        buff.append(getTheme().getJSString(ThemeJavascript.MODULE))
            .append(".")
            .append(name);
        return buff.toString();
    }

    /**
     * Returns JavaScript to obtain the widget associated with the 
     * component. Providing a component, with a client id of "form1:btn1",
     * will return "webui.@THEME@.dojo.widget.byId('form1:btn1');".
     *       
     * @param context The current FacesContext.
     * @param component The current component being rendered.
     */ 
    public static String getWidget(FacesContext context,
            UIComponent component) {
        StringBuffer buff = new StringBuffer(128);
        buff.append(getModuleName("dojo"))
            .append(".widget.byId('")
            .append(component.getClientId(context))
            .append("')");
        return buff.toString();        
    }

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Rendering methods
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    /**
     * Render bootstrap.
     *
     * @param component UIComponent to be rendered.
     * @param writer ResponseWriter to which the component should be rendered.
     * @param webuiAll Flag indicating to include all webui functionality.
     * @param webuiJsfx Flag indicating to include default Ajax functionality.
     * @param jsfx Flag indicating to include JSF Extensions resources.
     *
     * @exception IOException if an input/output error occurs.
     */
    public static void renderBootstrap(UIComponent component,
            ResponseWriter writer, boolean webuiAll, boolean webuiJsfx, 
            boolean jsfx) throws IOException, JSONException {
        // Render config.
        renderJavaScript(component, writer, getBootstrapConfig(webuiAll,
            webuiJsfx, jsfx));

        // Render webui include.
        renderWebuiInclude(component, writer, webuiAll, webuiJsfx);

        // Render JSF Extensions include.
        if (jsfx && webuiJsfx) {
            renderPrototypeInclude(component, writer);
            renderJsfxInclude(component, writer);
        }

        // Render global include.
        renderGlobalInclude(component, writer);
    }

    /**
     * Render JavaScript in the page, including enclosing script tags.
     *
     * @param component UIComponent to be rendered.
     * @param writer ResponseWriter to which the component should be rendered.
     * @param js The JavaScript string to render.
     *
     * @exception IOException if an input/output error occurs.
     */
    public static void renderJavaScript(UIComponent component,
            ResponseWriter writer, String js) throws IOException {
        renderJavaScript(component, writer, js, false);
    }

    /**
     * Render JavaScript in the page, including enclosing script tags.
     *
     * @param component UIComponent to be rendered.
     * @param writer ResponseWriter to which the component should be rendered.
     * @param js The JavaScript string to render.
     * @param defer Wait until the page has loaded before executing code.
     *
     * @exception IOException if an input/output error occurs.
     */
    public static void renderJavaScript(UIComponent component,
            ResponseWriter writer, String js, boolean defer) throws IOException {
        if (js == null) {
            return;
        }
        renderJavaScriptBegin(component, writer, defer);
        writer.write(js);
        renderJavaScriptEnd(component, writer, defer);
    }

    /**
     * Render JavaScript in the page, including enclosing script tags.
     *
     * @param component UIComponent to be rendered.
     * @param writer ResponseWriter to which the component should be rendered.
     * @param defer Wait until the page has loaded before executing code.
     *
     * @exception IOException if an input/output error occurs.
     */
    public static void renderJavaScriptBegin(UIComponent component,
            ResponseWriter writer, boolean defer) throws IOException {
        if (isDebug()) {
            writer.write("\n");
        }
        writer.startElement("script", component);
        writer.writeAttribute("type", "text/javascript", null);

        if (defer) {
            // The webui.@THEME@.dojo.addOnLoad function starts scripts after 
            // the DOM has loaded but before all of the page elements have 
            // loaded, which means your script doesn't have to wait for images 
            // and other large resources before it manipulates page structure.
            writer.write(getModuleName("dojo"));
            writer.write(".addOnLoad(function() {");
        }
    }

    /**
     * Render JavaScript in the page, including enclosing script tags.
     *
     * @param component UIComponent to be rendered.
     * @param writer ResponseWriter to which the component should be rendered.
     * @param defer Wait until the page has loaded before executing code.
     *
     * @exception IOException if an input/output error occurs.
     */
    public static void renderJavaScriptEnd(UIComponent component,
            ResponseWriter writer, boolean defer) throws IOException {
        if (defer) {
            writer.write("});");
        }
        writer.endElement("script");
    }

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Private config methods
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    /**
     * Get properties used to configure Ajax.
     * 
     * @param jsfx Flag indicating to include JSF Extensions resources.
     */
    private static JSONObject getAjaxConfig(boolean jsfx)
            throws JSONException {
        JSONObject json = new JSONObject();
        json.put("module", getModuleName("widget.jsfx"))
            .put("jsfx", jsfx);
        return json;
    }

    /**
     * Helper method to render config.
     * 
     * @param webuiAll Flag indicating to include all webui functionality.
     * @param webuiJsfx Flag indicating to include default Ajax functionality.
     * @param jsfx Flag indicating to include JSF Extensions resources.
     */
    private static String getBootstrapConfig(boolean webuiAll, 
            boolean webuiJsfx, boolean jsfx) throws JSONException {
        Theme theme = getTheme();
        JSONObject webui = new JSONObject();

        // Append JavaScript.
        //
        // var webui = {
        //        suntheme: {
        //            config: {
        //                ...
        //            }
        //         }
        //     };
        //
        StringTokenizer st = new StringTokenizer(getModuleName("config"), ".");
        StringBuffer buff = new StringBuffer(256);
        if (st.hasMoreTokens()) {
            buff.append("var ")
                .append(st.nextToken()) // var webui = {
                .append("=");
            
            if (st.hasMoreTokens()) {
                JSONObject suntheme = new JSONObject();
                webui.put(st.nextToken(), suntheme); // suntheme: {
                
                if (st.hasMoreTokens()) {
                    JSONObject config = new JSONObject();
                    suntheme.put(st.nextToken(), config); // config: {

                    // Add config properties.
                    config.put("ajax", getAjaxConfig(jsfx))
                        .put("djConfig", getDojoConfig())
                        .put("module", theme.getJSString(ThemeJavascript.MODULE))
                        .put("modulePath", theme.getPathToJSFile((isDebug())
                            ? ThemeJavascript.MODULE_PATH_UNCOMPRESSED
                            : ThemeJavascript.MODULE_PATH))
                        .put("isDebug", isDebug())
                        .put("theme", getThemeConfig(FacesContext.getCurrentInstance()))
                        .put("webuiAll", webuiAll)
                        .put("webuiJsfx", webuiJsfx);
                }
            }
            buff.append(JSONUtilities.getString(webui))
                .append(";");
        }
        return buff.toString();
    }

    /**
     * Get properties used to configure Dojo.
     */
    private static JSONObject getDojoConfig() throws JSONException {
        JSONObject json = new JSONObject();
        json.put("isDebug", isDebug())
            .put("baseUrl", getTheme().getPathToJSFile((isDebug())
                ? ThemeJavascript.DOJO_MODULE_PATH_UNCOMPRESSED
                : ThemeJavascript.DOJO_MODULE_PATH));
        return json;
    }

    /**
     * Return JSONObject containing the following properties in order
     * to initialize the JavaScript theme.
     * <ul>
     * <li>prefix - the application contex plust the theme servlet prefix</li>
     * <li>module - the value of ThemeJavascript.THEME_MODULE</li>
     * <li>modulePath - the value of ThemeJavascript.THEME_MODULE_PATH or
     * ThemeJavascript.THEME_MODULE_PATH_UNCOMPRESSED if in debug mode.</li>
     * <li>bundle - the value of ThemeJavascript.THEME_BUNDLE</li>
     * <li>custom - a JSONArray of the application's theme javascript
     * bundles. @see com.sun.webui.theme.ThemeContext#THEME_RESOURCES </li>
     * </ul>
     */
    private static JSONObject getThemeConfig(FacesContext context) 
            throws JSONException {

	// This is the namespace for the js theme.
	// It is webui.@THEME@.theme. It is the "module" parameter for
	// webui.@THEME@.dojo.requireLocalization and 
        // webui.@THEME@.dojo.i18n.getLocalization
	//
	String themeModule = getTheme().getJSString(ThemeJavascript.THEME_MODULE);

	// The theme module path prefix.
	//
	String themeModulePath = isDebug()
            ? getTheme().getJSString(ThemeJavascript.THEME_MODULE_PATH_UNCOMPRESSED)
	    : getTheme().getJSString(ThemeJavascript.THEME_MODULE_PATH);

	// The "bundle" parameter for 
	// webui.@THEME@.dojo.requireLocalization and 
        // webui.@THEME@.dojo.i18n.getLocalization.
	// It is the base name for the theme properties js file in the 
	// nls directories, @THEME@.js
	//
	String themeBundle = getTheme().getJSString(ThemeJavascript.THEME_BUNDLE);

	// While "toString" is not supposed to be guaranteed, the javadoc
	// says it returns the complete lang, country and variant
	// separated by "underbars".
	//
	// It's not clear if we want to be explicit in terms of 
	// loading an explicit locale. It may be sufficient to 
	// just allow dojo to load its notion of the "current"
	// locale.
	//
	String themeLocale = context.getViewRoot().getLocale().toString().toLowerCase().replaceAll("_", "-");

	// Get the ThemeContext for the application's theme resources
	// and the appcontext and the theme servlet context combined
	// in the "getResourcePath" call.
	//
	ThemeContext themeContext = JSFThemeContext.getInstance(context);

	// Fool getResourcePath into returning the prefix.
	// by passing "", since we don't have path, we just want the
	// prefix. This will have a trailing "/", so get rid of it.
	//
	String themePrefix = themeContext.getResourcePath("");
	int lastSlash = themePrefix.lastIndexOf("/");
	if (lastSlash > 0) {
	    themePrefix = themePrefix.substring(0, lastSlash);
	}

	// Get the application's custom theme package(s).
	//
	JSONArray customThemes = null;
	String customThemeResources[] = themeContext.getThemeResources();
	if (customThemeResources != null) {
	    // Format this as a javascript array
	    //
	    customThemes = new JSONArray();
	    for (int i = 0; i < customThemeResources.length; ++i) {
		customThemes.put(i, customThemeResources[i]);
	    }
	}

        JSONObject json = new JSONObject();
        json.put("bundle", themeBundle)
            .put("custom", customThemes)
            .put("locale", themeLocale)
            .put("module", themeModule)
            .put("modulePath", themeModulePath)
            .put("prefix", themePrefix);

        return json;
    }

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Private helper methods
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    // Helper method to get Theme objects.
    private static Theme getTheme() {
        return ThemeUtilities.getTheme(FacesContext.getCurrentInstance());
    }

    // Helper method to get request map.
    private static Map getRequestMap() {
        return FacesContext.getCurrentInstance().getExternalContext().
            getRequestMap();
    }

    // Helper method to get request parameter map.
    private static Map getRequestParameterMap() {
        return FacesContext.getCurrentInstance().getExternalContext().
            getRequestParameterMap();
    }

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // JavaScript include methods
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    /**
     * Helper method to render JavaScript include.
     *
     * @param component UIComponent to be rendered.
     * @param writer ResponseWriter to which the component should be rendered.
     */
    private static void renderGlobalInclude(UIComponent component,
            ResponseWriter writer) throws IOException {
        String javascripts[] = getTheme().getGlobalJSFiles();
        if (javascripts == null) {
            return;
        }
        for (int i = 0; i < javascripts.length; i++) {
            Object file = javascripts[i];
            if (file == null) {
                continue;
            }
            if (isDebug()) {
                writer.write("\n");
            }
            writer.startElement("script", component);
            writer.writeAttribute("type", "text/javascript", null);
            writer.writeURIAttribute("src", file.toString(), null);
            writer.endElement("script");
        }
    }

    /**
     * Render given JavaScript file in page, including script tags.
     *
     * Note: JavaScript includes must be output in page prior to instantiating
     * widgets. This can be done via the head, themeLinks, or portalTheme tags,
     * but not via any other component renderer. Thus, this method is declared
     * private to ovoid misuse.
     * 
     * If JavaScript includes are output by renderers, timing issues can occur 
     * when client-side widgets and server-side components are rendered in the 
     * same page. For example, button HTML may be rendered as a JSON property
     * (the child of a widget), which also contains a JavaScript include. In 
     * this scenario, ther buttons in the page may not initialize correctly
     * because the widget has not added the JavaScript include, yet. See CR 6517246.
     *
     * @param component The current component being rendered.
     * @param writer The current ResponseWriter.
     * @param file The JavaScript file to include.
     */
    private static void renderJavaScriptInclude(UIComponent component,
            ResponseWriter writer, String file) throws  IOException {
        if (file == null) {
	    return;
	}
	String jsFile = getTheme().getPathToJSFile(file);
	if (jsFile == null) {
	    return;
	}
        if (isDebug()) {
            writer.write("\n");
        }
        writer.startElement("script", component);
        writer.writeAttribute("type", "text/javascript", null);
        writer.writeURIAttribute("src", jsFile, null);
        writer.endElement("script");
    }

    /**
     * Helper method to render JavaScript include.
     *
     * @param component UIComponent to be rendered.
     * @param writer ResponseWriter to which the component should be rendered.
     *
     * @exception IOException if an input/output error occurs.
     */
    private static void renderJsfxInclude(UIComponent component,
            ResponseWriter writer) throws IOException {
        // Note: JavaScript shall be included client-side, but still need to
        // register with the scripts tag.
        Map requestMap = getRequestMap();
        if (!requestMap.containsKey(ScriptsComponent.AJAX_JS_LINKED)) {
//            renderJavaScriptInclude(component, writer, (isDebug())
//                ? ThemeJavascript.JSFX_UNCOMPRESSED
//                : ThemeJavascript.JSFX);
            requestMap.put(ScriptsComponent.AJAX_JS_LINKED, Boolean.TRUE);
        }
    }

    /**
     * Helper method to render JavaScript include.
     *
     * @param component UIComponent to be rendered.
     * @param writer ResponseWriter to which the component should be rendered.
     *
     * @exception IOException if an input/output error occurs.
     */
    private static void renderPrototypeInclude(UIComponent component,
            ResponseWriter writer) throws IOException {
        // Note: JavaScript shall be included client-side, but still need to
        // register with the scripts tag.
        Map map = getRequestMap();
        if (!map.containsKey(ScriptsComponent.PROTOTYPE_JS_LINKED)) {
//            renderJavaScriptInclude(component, writer, (isDebug())
//                ? ThemeJavascript.PROTOTYPE_UNCOMPRESSED
//                : ThemeJavascript.PROTOTYPE);
            map.put(ScriptsComponent.PROTOTYPE_JS_LINKED, Boolean.TRUE);
        }
    }

    /**
     * Helper method to render JavaScript include.
     *
     * @param component UIComponent to be rendered.
     * @param writer ResponseWriter to which the component should be rendered.
     * @param webuiAll Flag indicating to include all webui functionality.
     * @param webuiJsfx Flag indicating to include default Ajax functionality.
     * 
     * @exception IOException if an input/output error occurs.
     */
    private static void renderWebuiInclude(UIComponent component,
        ResponseWriter writer, boolean webuiAll, boolean webuiJsfx)
            throws IOException {
        String webui = null;
        if (webuiAll) {
            if (webuiJsfx) {
                webui = (isDebug())
                    ? ThemeJavascript.WEBUI_JSFX_ALL_UNCOMPRESSED
                    : ThemeJavascript.WEBUI_JSFX_ALL;
            } else {
                webui = (isDebug())
                    ? ThemeJavascript.WEBUI_ALL_UNCOMPRESSED
                    : ThemeJavascript.WEBUI_ALL;
            }
        } else {
            if (webuiJsfx) {
                webui = (isDebug())
                    ? ThemeJavascript.WEBUI_JSFX_UNCOMPRESSED
                    : ThemeJavascript.WEBUI_JSFX;
            } else {
                webui = (isDebug())
                    ? ThemeJavascript.WEBUI_UNCOMPRESSED
                    : ThemeJavascript.WEBUI;
            }
        }
        renderJavaScriptInclude(component, writer, webui);
    }
}
