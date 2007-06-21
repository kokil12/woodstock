//<!--
// The contents of this file are subject to the terms
// of the Common Development and Distribution License
// (the License).  You may not use this file except in
// compliance with the License.
// 
// You can obtain a copy of the license at
// https://woodstock.dev.java.net/public/CDDLv1.0.html.
// See the License for the specific language governing
// permissions and limitations under the License.
// 
// When distributing Covered Code, include this CDDL
// Header Notice in each file and include the License file
// at https://woodstock.dev.java.net/public/CDDLv1.0.html.
// If applicable, add the following below the CDDL Header,
// with the fields enclosed by brackets [] replaced by
// you own identifying information:
// "Portions Copyrighted [year] [name of copyright owner]"
// 
// Copyright 2007 Sun Microsystems, Inc. All rights reserved.
//
// This Javascript file should be included in any page that uses the associated
// component, where JSF Extensions is used as the underlying transfer protocol.
//

dojo.provide("webui.@THEME@.widget.jsfx.accordionTab");

dojo.require("webui.@THEME@.widget.jsfx.*");
dojo.require("webui.@THEME@.widget.accordionTab");

/**
 * This function is used to obtain data asynchronously.
 */
webui.@THEME@.widget.jsfx.accordionTab = {
    /**
     * This function is used to process menu actions associated with a given 
     * tabContent.
     * <ul>
     *  <li>old tab id</li>
     *  <li>new tab id</li>
     * </ul>
     *
     * @param props Key-Value pairs of properties.
     */
    processTabActionEvent: function(props) {
        if (props == null) {
            return false;
        }

        // Dynamic Faces requires a DOM node as the source property.
        var domNode = document.getElementById(props.id);

        // Generate AJAX request using the JSF Extensions library.
        new DynaFaces.fireAjaxTransaction(
            (domNode) ? domNode : document.forms[0], {
            execute: props.id, // Need to decode hidden field.
            render: props.id,
            replaceElement: webui.@THEME@.widget.jsfx.accordionTab.tabActionCallback,
            xjson: {
                id : props.id,
                action: props.actionName,
                event: "tabAction"
            }
        });

        return true;
    },

    /**
     * This function is used to load a tabContent asynchronously.
     *
     * <ul>
     *  <li>id</li>
     * </ul>
     *
     * @param props Key-Value pairs of properties.
     */
    processLoadContentEvent: function(props) {
        if (props == null) {
            return false;
        }

        // Dynamic Faces requires a DOM node as the source property.
        var domNode = document.getElementById(props.id);

        // Generate AJAX request using the JSF Extensions library.
        new DynaFaces.fireAjaxTransaction(
            (domNode) ? domNode : document.forms[0], {
            execute: props.id, // Need to decode hidden field.
            render: props.id,
            replaceElement: webui.@THEME@.widget.jsfx.progressBar.loadContentCallback,
            xjson: {
                id: props.id,
                event: "loadContent"
            }
        });

        return true;
    },

    /**
     * This function is used to update tab with the loaded content.
     *
     * @param id The client id.
     * @param content The content returned by the AJAX response.
     * @param closure The closure argument provided to DynaFaces.fireAjaxTransaction.
     * @param xjson The zjson argument provided to DynaFaces.fireAjaxTransaction.
     */
    loadContentCallback: function(id, content, closure, xjson) {
        if (id == null || content == null) {
            return false;
        }

        // Parse JSON text.
        var json = JSON.parse(content);

        // Set progress.
        var widget = dojo.widget.byId(id);
        widget.setProps(json);

        // Publish an event for custom AJAX implementations to listen for.
        dojo.event.topic.publish(webui.@THEME@.widget.accordionTab.loadContent.endEventTopic, json);
        return true;
    },

    /**
     * This function is used to handle menu items changes in a tabContent widget.
     *
     * @param id The client id.
     * @param content The content returned by the AJAX response.
     * @param closure The closure argument provided to DynaFaces.fireAjaxTransaction.
     * @param xjson The xjson argument provided to DynaFaces.fireAjaxTransaction.
     */
    tabActionCallback: function(id, content, closure, xjson) {
        if (id == null || content == null) {
            return false;
        }

        // Parse JSON text.
        var json = JSON.parse(content);

        // Add rows.
        var widget = dojo.widget.byId(id);
        widget.setProps(json);

        // Publish an event for custom AJAX implementations to listen for.
        dojo.event.topic.publish(webui.@THEME@.widget.accordionTab.tabAction.endEventTopic, json);
        return true;
    }
}

// Listen for Dojo Widget event signalling the tabContent's need to refresh, process
// menu actions or load tab contents.
dojo.event.topic.subscribe(webui.@THEME@.widget.accordionTab.loadContent.beginEventTopic,
    webui.@THEME@.widget.jsfx.accordionTab, "processLoadContentEvent");
dojo.event.topic.subscribe(webui.@THEME@.widget.accordionTab.refresh.beginEventTopic,
    webui.@THEME@.widget.jsfx.common, "processRefreshEvent");
dojo.event.topic.subscribe(webui.@THEME@.widget.accordionTab.tabAction.beginEventTopic,
    webui.@THEME@.widget.jsfx.accordionTab, "processTabActionEvent");
    
//-->