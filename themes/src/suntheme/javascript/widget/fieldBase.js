// widget/fieldBase.js
//
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

/**
 * @name widget/fieldBase.js
 * @version @THEME_VERSION@
 * @overview This module contains classes and functions for the fieldBase object.
 */
dojo.provide("webui.@THEME@.widget.fieldBase");

dojo.require("webui.@THEME@.widget.submitBase");

/**
 * This function is used to construct a template based widget.
 *
 * @name webui.@THEME@.widget.fieldBase
 * @inherits webui.@THEME@.widget.submitBase
 * @constructor
 */
dojo.declare("webui.@THEME@.widget.fieldBase", webui.@THEME@.widget.submitBase, {
    // Set defaults.
    disabled: false,
    required: false,
    size: 20,
    valid: true
});

/**
 * Helper function to obtain HTML input element class names.
 */
webui.@THEME@.widget.fieldBase.prototype.getInputClassName = function() {   
    return null; // Overridden by subclass.
}

/**
 * Returns the HTML input element that makes up the text field.
 *
 * @return a reference to the HTML input element. 
 */
webui.@THEME@.widget.fieldBase.prototype.getInputElement = function() {
    return this.fieldNode;
}

/**
 * This function is used to get widget properties. Please see the 
 * setProps() function for a list of supported properties.
 */
webui.@THEME@.widget.fieldBase.prototype.getProps = function() {
    var props = this.inherited("getProps", arguments);
    
    // Set properties.
    if (this.alt) { props.alt = this.alt; }
    if (this.disabled != null) { props.disabled = this.disabled; }
    if (this.label) { props.label= this.label; }
    if (this.maxLength > 0) { props.maxLength = this.maxLength; }    
    if (this.notify) { props.notify = this.notify; }
    if (this.submitForm != null) { props.submitForm = this.submitForm; }
    if (this.text != null) { props.text = this.text; }
    if (this.title != null) { props.title = this.title; }
    if (this.type) { props.type= this.type; }
    if (this.readOnly != null) { props.readOnly = this.readOnly; }
    if (this.required != null) { props.required = this.required; }
    if (this.size > 0) { props.size = this.size; }
    if (this.style != null) { props.style = this.style; }
    if (this.valid != null) { props.valid = this.valid; }
    
    // After widget has been initialized, get user's input.
    if (this.isInitialized() == true && this.fieldNode.value != null) {
        props.value = this.fieldNode.value;
    } else if (this.value != null) {
        props.value = this.value;
    }
    return props;
}

/**
 * This function is used to fill in remaining template properties, after the
 * buildRendering() function has been processed.
 * <p>
 * Note: Unlike Dojo 0.4, the DOM nodes don't exist in the document, yet. 
 * </p>
 */
webui.@THEME@.widget.fieldBase.prototype.postCreate = function () {
    // Set ids.
    if (this.id) {
        this.fieldNode.id = this.id + "_field";
        this.fieldNode.name = this.id + "_field";
        this.labelContainer.id = this.id + "_label";
    }
    
    // Set public functions.
    this.domNode.getInputElement = function() { return dijit.byId(this.id).getInputElement(); }
    
    return this.inherited("postCreate", arguments);
}

/**
 * This function is used to set widget properties. Please see the setProps() 
 * function for a list of supported properties.
 * <p>
 * Note: This is considered a private API, do not use. This function should only
 * be invoked via setProps().
 * </p>
 *
 * @param {Object} props Key-Value pairs of properties.
 * @private
 */
webui.@THEME@.widget.fieldBase.prototype._setProps = function(props) {
    if (props == null) {
        return false;
    }
    
    // Set properties.
    if (props.submitForm == false || props.submitForm == true ) { 
        // connect the keyPress event
        dojo.connect(this.fieldNode, "onkeypress", this, "submitForm");
    }
    if (props.maxLength > 0) { this.fieldNode.maxLength = props.maxLength; }
    if (props.size > 0) { this.fieldNode.size = props.size;  }
    if (props.value != null) { this.fieldNode.value = props.value; }
    if (props.title != null) { this.fieldNode.title = props.title; }   
    if (props.disabled != null) { 
        this.fieldNode.disabled = new Boolean(props.disabled).valueOf();
    }
    if (props.readOnly != null) { 
        this.fieldNode.readOnly = new Boolean(props.readOnly).valueOf();
    }
    
    // Set label properties.
    if (props.label || (props.valid != null || props.required != null) && this.label) {
        // Ensure property exists so we can call setProps just once.
        if (props.label == null) {
            props.label = {}; // Avoid updating all props using "this" keyword.
        }
        
        // Set properties.
        props.label.id = this.label.id; // Required for updateFragment().
        props.label.required = this.required;
        props.label.valid = this.valid;
        
        // Update/add fragment.
        this.widget.updateFragment(this.labelContainer, props.label);
    }
    
    // Set HTML input element class name.
    this.fieldNode.className = this.getInputClassName();
    
    // Set more properties.
    this.setCommonProps(this.fieldNode, props);
    this.setEventProps(this.fieldNode, props);
    
    // Set remaining properties.
    return this.inherited("_setProps", arguments);
}

/**
 * Process keyPress events on the field, which enforces/disables 
 * submitForm behavior.
 *
 * @param {Event} event The JavaScript event
 */
webui.@THEME@.widget.fieldBase.prototype.submitForm = function(event) {
    if (event == null) {
        return false;
    }
    if (event.keyCode == event.KEY_ENTER) {               
        if (this.submitForm == false) {
            // Disable form submission.
            if (window.event) {
                event.cancelBubble = true;
                event.returnValue = false;
            } else{
                event.preventDefault();
                event.stopPropagation();
            }
            return false;
        } else {
            // Submit the form                    
            if (event.currentTarget.form) {
                event.currentTarget.form.submit();
            }
        }
    }
    return true;    
}
