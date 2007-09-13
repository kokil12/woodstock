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

dojo.provide("webui.@THEME@.widget.widgetBase");

dojo.require("dojo.widget.*");
dojo.require("webui.@THEME@.*");
dojo.require("webui.@THEME@.theme.*");
dojo.require("webui.@THEME@.widget.*");

/**
 * The widgetBase object is used for base functionality in all widgets. It 
 * inherits from the dojo.widget.HtmlWidget module which, in turn, inherits 
 * from dojo.widget.DomWidget and dojo.widget.Widget. The dojo.widget.Widget
 * module is responsible for calling the buildRendering(), initialize(), 
 * postInitialize(), and postCreate() functions in that order.
 *
 * The dojo.widget.DomWidget module is responsible for calling fillInTemplate() 
 * from within buildRendering(). The fillInTemplate() function is used to fill 
 * in template properties during initialization. Anything to be set only once 
 * should be added here (e.g., setting public functions on a DOM node). Public
 * functions such as getProps(), setProps(), and refresh() are set on the DOM 
 * via the "superclass" function of widgetBase.
 *
 * The initialization() function is responsible for initializing widget object. 
 * For example, we use this function to initialize CSS selectors use by the 
 * button, dropDown, and listbox widgets.
 *
 * The postInitialization() function is responsible for invoking the private
 * _setProps() function. The _setProps() function is used to set widget 
 * properties that can be updated by a web app developer. Properties should be
 * set if and only if a key-value pair has been given. The function is also used
 * during initialization since it does not extend the widget with known 
 * properties.
 *
 * The private _setProps() function is responsible for invoking setCommonProps()
 * and setEventProps(). These properties may or may not be set on the outermost
 * DOM node; however, core (i.e., id, class, style, etc.) properties are. Core 
 * properties are set on the DOM via the "superclass" function of widgetBase 
 * which invokes the setCoreProps() function.
 *
 * The getClassName() function is responsible for obtaining the selector that
 * will be set on the outermost DOM node. The private _setProps() function 
 * of widgetBase ensures that the getClassName() function is called prior to 
 * invoking setCoreProps(). In most cases, this function will be overridded in
 * order to apply widget specific selectors. However, selectors should be 
 * concatinated in order of precedence (e.g., the user's className property is 
 * always appended last).
 *
 * The public setProps() function is responsible for extending the widget object
 * with properties so they can be used during later updates. After extending the
 * widget, the private _setProps() function is called. In some cases, the public
 * setProps() function may be overridden. For example, the label clears the
 * contents property from the widget because that is something we do not want to
 * extend.
 *
 * The postCreate() function is typically used after the widget has been 
 * rendered completely. For example, the progressBar calls a timeout used to
 * periodically publish progress events. At this point, newly created DOM nodes
 * still have not been added to the document.
 */
webui.@THEME@.widget.widgetBase = function() {
    // Register widget.
    dojo.widget.HtmlWidget.call(this);
}

/**
 * This function is used to include default Ajax functionality. Before the given
 * module is included in the page, a test is performed to ensure that the 
 * default Ajax implementation is being used.
 */
webui.@THEME@.widget.widgetBase.ajaxify = function() {
    // To do: Get module from the theme.
    if (webui.@THEME@.widget.jsfx) {
        this.widget.require("webui.@THEME@.widget.jsfx." + this.widgetType);
    }
}

/**
 * This function is used to render the widget from a template.
 *
 * @param props Key-Value pairs of properties.
 * @param frag HTML fragment.
 * @param parent The parent of this widget.
 */
webui.@THEME@.widget.widgetBase.buildRendering = function (props, frag, parent) {
    // Get default templates.
    if (this.templatePath == null && this.templateString == null) {
        this.templatePath = this.widget.getTemplatePath(this.widgetType);
        this.templateString = this.widget.getTemplateString(this.widgetType);
    }

    // The templatePath should have precedence. Therefore, in order for the 
    // templatePath to be used, templateString must be null.
    if (this.templatePath != null) {
        this.templateString = null;
    }

    // Template must be set prior to calling "superclass".
    return webui.@THEME@.widget.widgetBase.superclass.buildRendering.call(this, props, frag, parent);
}

/**
 * This closure is used to process widget events.
 */
webui.@THEME@.widget.widgetBase.event = {
    /**
     * This closure is used to process refresh events.
     */
    refresh: {
        /**
         * Event topics for custom AJAX implementations to listen for.
         */
        beginTopic: null,
        endTopic: null,
 
        /**
         * Process refresh event.
         *
         * @param execute The string containing a comma separated list of client ids 
         * against which the execute portion of the request processing lifecycle
         * must be run.
         */
        processEvent: function(execute) {
            // Include default AJAX implementation.
            this.ajaxify();

            // Publish an event for custom AJAX implementations to listen for.
            dojo.event.topic.publish(
                this.event.refresh.beginTopic, {
                    id: this.id,
                    execute: execute,
                    endTopic: this.event.refresh.endTopic
                });
            return true;
        }
    },

    /**
     * This closure is used to process state change events.
     */
    state: {
        /**
         * Event topics for custom AJAX implementations to listen for.
         */
        beginTopic: null,
        endTopic: null,

        /**
         * Process state change event.
         *
         * @param props Key-Value pairs of properties.
         */
        processEvent: function(props) {
            // Include default AJAX implementation.
            this.ajaxify();

            // Publish an event for custom AJAX implementations to listen for.
            dojo.event.topic.publish(
                this.event.state.beginTopic, {
                    id: this.id,
                    endTopic: this.event.state.endTopic,
                    props: props
                });
            return true;
        }
    },

    /**
     * This closure is used to process submit events.
     */
    submit: {
        /**
         * Event topics for custom AJAX implementations to listen for.
         */
        beginTopic: null,
        endTopic: null,
    
        /**
         * Process submit event.
         *
         * @param execute Comma separated string containing a list of client ids 
         * against which the execute portion of the request processing lifecycle
         * must be run.
         */
        processEvent: function(execute) {
            // Include default AJAX implementation.
            this.ajaxify();

            // Publish an event for custom AJAX implementations to listen for.
            dojo.event.topic.publish(
                this.event.submit.beginTopic, {
                    id: this.id,
                    execute: execute,
                    endTopic: this.event.submit.endTopic
                });
            return true;
        }
    }
}

/**
 * This function is used to fill in template properties.
 *
 * Note: This is called after the buildRendering() function. Anything to be set 
 * only once should be added here; otherwise, use the _setProps() function.
 *
 * @param props Key-Value pairs of properties.
 * @param frag HTML fragment.
 */
webui.@THEME@.widget.widgetBase.fillInTemplate = function(props, frag) {
    webui.@THEME@.widget.widgetBase.superclass.fillInTemplate.call(this, props, frag, parent);

    // Since the anchor id and name must be the same on IE, we cannot obtain the
    // widget using the DOM node ID via the public functions below. Therefore, 
    // we need to set the widget id via closure magic.
    var id = this.id;

    // Set public functions.
    this.domNode.getProps = function() { return dojo.widget.byId(id).getProps(); }
    this.domNode.refresh = function(execute) { return dojo.widget.byId(id).refresh(execute); }
    this.domNode.setProps = function(props, notify) { return dojo.widget.byId(id).setProps(props, notify); }

    // In order to register widgets properly, the DOM node id must be set prior 
    // to creating any widget children. Otherwise, widgets may not be destroyed.
    this.domNode.id = id;

    return true;
}

/**
 * This function is used to obtain the outermost HTML element class name.
 *
 * Note: Selectors should be concatinated in order of precedence (e.g., the 
 * user's className property is always appended last).
 */
webui.@THEME@.widget.widgetBase.getClassName = function() {
    return this.className;
}

/**
 * This function is used to get common properties from the widget. Please see
 * the setCommonProps() function for a list of supported properties.
 */
webui.@THEME@.widget.widgetBase.getCommonProps = function() {
    var props = {};

    // Set properties.
    if (this.accessKey) { props.accessKey = this.accessKey; }
    if (this.dir) { props.dir = this.dir; }
    if (this.lang) { props.lang = this.lang; }
    if (this.tabIndex) { props.tabIndex = this.tabIndex; }
    if (this.title) { props.title = this.title; }

    return props;
}

/**
 * This function is used to get core properties from the widget. Please see
 * the setCoreProps() function for a list of supported properties.
 */
webui.@THEME@.widget.widgetBase.getCoreProps = function() {
    var props = {};

    // Set properties.
    if (this.className) { props.className = this.className; }
    if (this.id) { props.id = this.id; }
    if (this.style) { props.style = this.style; }
    if (this.visible != null) { props.visible = this.visible; }

    return props;
}

/**
 * This function is used to get event properties from the widget. Please
 * see the setEventProps() function for a list of supported properties.
 */
webui.@THEME@.widget.widgetBase.getEventProps = function() {
    var props = {};

    // Set properties.
    if (this.onBlur) { props.onBlur = this.onBlur; }
    if (this.onChange) { props.onChange = this.onChange; }
    if (this.onClick) { props.onClick = this.onClick; }
    if (this.onDblClick) { props.onDblClick = this.onDblClick; }
    if (this.onFocus) { props.onFocus = this.onFocus; }
    if (this.onKeyDown) { props.onKeyDown = this.onKeyDown; }
    if (this.onKeyPress) { props.onKeyPress = this.onKeyPress; }
    if (this.onKeyUp) { props.onKeyUp = this.onKeyUp; }
    if (this.onMouseDown) { props.onMouseDown = this.onMouseDown; }
    if (this.onMouseOut) { props.onMouseOut = this.onMouseOut; }
    if (this.onMouseOver) { props.onMouseOver = this.onMouseOver; }
    if (this.onMouseUp) { props.onMouseUp = this.onMouseUp; }
    if (this.onMouseMove) { props.onMouseMove = this.onMouseMove; }
    if (this.onSelect) { props.onSelect = this.onSelect; }

    return props;
}

/**
 * This function is used to get widget properties. Please see the 
 * _setProps() function for a list of supported properties.
 */
webui.@THEME@.widget.widgetBase.getProps = function() {
    var props = {};

    // Set properties.
    Object.extend(props, this.getCommonProps());
    Object.extend(props, this.getCoreProps());
    Object.extend(props, this.getEventProps());

    return props;
}

/**
 * This function is used to initialize the widget.
 *
 * Note: This is called after the fillInTemplate() function.
 *
 * @param props Key-Value pairs of properties.
 * @param frag HTML fragment.
 * @param parent The parent of this widget.
 */
webui.@THEME@.widget.widgetBase.initialize = function (props, frag, parent) {
    return webui.@THEME@.widget.widgetBase.superclass.initialize.call(this, props, frag, parent);
}

/**
 * This function is used to test if widget has been initialized.
 *
 * Note: It is assumed that an HTML element is used as a place holder for the
 * document fragment.
 */
webui.@THEME@.widget.widgetBase.isInitialized = function() {
    // Testing if the outermost DOM node has been added to the document and
    // ensuring a Dojo attach point exists works fine for JSP. However, the 
    // following code always returns null for facelets.
    //
    // var domNode = document.getElementById(this.id);
    // if (domNode && domNode.getAttribute("dojoattachpoint")) {
    if (this.initialized == true) {
        return true;
    }
    return false;
}

/**
 * This function is used after the widget has been initialized.
 *
 * Note: This is called after the initialize() function.
 *
 * @param props Key-Value pairs of properties.
 * @param frag HTML fragment.
 * @param parent The parent of this widget.
 */
webui.@THEME@.widget.widgetBase.postInitialize = function (props, frag, parent) {
    // Set properties.
    this._setProps(props);

    // Call "superclass" after setting properties due to flashing. This is best
    // seen while invoking refresh() for checkbox/radiobutton group.
    return webui.@THEME@.widget.widgetBase.superclass.postInitialize.call(this, props, frag, parent);
}

/**
 * This function is used after the widget has been initialized and rendered.
 *
 * Note: This is called after the postInitialize() function.
 *
 * @param props Key-Value pairs of properties.
 * @param frag HTML fragment.
 * @param parent The parent of this widget.
 */
webui.@THEME@.widget.widgetBase.postCreate = function (props, frag, parent) {
    // All widget properties have been set.
    this.initialized = true;

    return webui.@THEME@.widget.widgetBase.superclass.postCreate.call(this, props, frag, parent);
}

/**
 * This function is used to set common properties for the given domNode
 * with the following Object literals.
 *
 * <ul>
 *  <li>accessKey</li>
 *  <li>dir</li>
 *  <li>lang</li>
 *  <li>tabIndex</li>
 *  <li>title</li>
 * </ul>
 *
 * @param domNode The DOM node to assign properties to.
 * @param props Key-Value pairs of properties.
 */
webui.@THEME@.widget.widgetBase.setCommonProps = function(domNode, props) {
    if (domNode == null || props == null) {
        return false;
    }
    if (props.accessKey) { 
        domNode.accessKey = props.accessKey;
    }
    if (props.dir) {
        domNode.dir = props.dir;
    }
    if (props.lang) {
        domNode.lang = props.lang;
    }
    if (props.tabIndex > -1 && props.tabIndex < 32767) {
        domNode.tabIndex = props.tabIndex;
    }
    if (props.title) {
        domNode.title = props.title;
    }
    return true;
}

/**
 * This function is used to set core properties for the given domNode
 * with the following Object literals. These properties are typically
 * set on the outermost element.
 *
 * <ul>
 *  <li>className</li>
 *  <li>id</li>
 *  <li>style</li>
 *  <li>visible</li>
 * </ul>
 *
 * Note: The className is typically provided by a web app developer. If 
 * the widget has a default className, it should be added to the DOM node
 * prior to calling this function. For example, the "myCSS" className would
 * be appended to the existing "Tblsun4" className (e.g., "Tbl_sun4 myCSS").
 *
 * @param domNode The DOM node to assign properties to.
 * @param props Key-Value pairs of properties.
 */
webui.@THEME@.widget.widgetBase.setCoreProps = function(domNode, props) {
    if (domNode == null || props == null) {
        return false;
    }
    if (props.className) {
        domNode.className = props.className;
    }
    if (props.id) { 
        domNode.id = props.id;
    }
    if (props.style) { 
        domNode.style.cssText = props.style;
    }
    if (props.visible != null) {
        webui.@THEME@.common.setVisibleElement(domNode, 
            new Boolean(props.visible).valueOf());
    }
    return true;
}

/**
 * This function is used to set event properties for the given domNode
 * with the following Object literals.
 *
 * <ul>
 *  <li>onBlur</li>
 *  <li>onChange</li>
 *  <li>onClick</li>
 *  <li>onDblClick</li>
 *  <li>onFocus</li>
 *  <li>onKeyDown</li>
 *  <li>onKeyPress</li>
 *  <li>onKeyUp</li>
 *  <li>onMouseDown</li>
 *  <li>onMouseOut</li>
 *  <li>onMouseOver</li>
 *  <li>onMouseUp</li>
 *  <li>onMouseMove</li>
 *  <li>onSelect</li>
 * </ul>
 *
 * @param domNode The DOM node to assign properties to.
 * @param props Key-Value pairs of properties.
 */
webui.@THEME@.widget.widgetBase.setEventProps = function(domNode, props) {
    if (domNode == null || props == null) {
        return false;
    }

    // Note: JSON strings are not recognized as JavaScript. In order for
    // events to work properly, an anonymous function must be created.
    if (props.onBlur) { 
        domNode.onblur = (typeof props.onBlur == 'string')
            ? new Function("event", props.onBlur)
            : props.onBlur;
    }
    if (props.onClick) {
        domNode.onclick = (typeof props.onClick == 'string')
            ? new Function("event", props.onClick)
            : props.onClick;
    }
    if (props.onChange) {
        domNode.onchange = (typeof props.onChange == 'string')
            ? new Function("event", props.onChange)
            : props.onChange;
    }
    if (props.onDblClick) {
        domNode.ondblclick = (typeof props.onDblClick == 'string')
            ? new Function("event", props.onDblClick)
            : props.onDblClick;
    }
    if (props.onFocus) {
        domNode.onfocus = (typeof props.onFocus == 'string')
            ? new Function("event", props.onFocus)
            : props.onFocus;
    }
    if (props.onKeyDown) {
        domNode.onkeydown = (typeof props.onKeyDown == 'string')
            ? new Function("event", props.onKeyDown)
            : props.onKeyDown;
    }
    if (props.onKeyPress) {
        domNode.onkeypress = (typeof props.onKeyPress == 'string')
            ? new Function("event", props.onKeyPress)
            : props.onKeyPress;
    }
    if (props.onKeyUp) {
        domNode.onkeyup = (typeof props.onKeyUp == 'string')
            ? new Function("event", props.onKeyUp)
            : props.onKeyUp;
    }
    if (props.onMouseDown) {
        domNode.onmousedown = (typeof props.onMouseDown == 'string')
            ? new Function("event", props.onMouseDown)
            : props.onMouseDown;
    }
    if (props.onMouseOut) {
        domNode.onmouseout = (typeof props.onMouseOut == 'string')
            ? new Function("event", props.onMouseOut)
            : props.onMouseOut;
    }
    if (props.onMouseOver) {
        domNode.onmouseover = (typeof props.onMouseOver == 'string')
            ? new Function("event", props.onMouseOver)
            : props.onMouseOver;
    }
    if (props.onMouseUp) {
        domNode.onmouseup = (typeof props.onMouseUp == 'string')
            ? new Function("event", props.onMouseUp)
            : props.onMouseUp;
    }
    if (props.onMouseMove) {
        domNode.onmousemove = (typeof props.onMouseMove == 'string')
            ? new Function("event", props.onMouseMove)
            : props.onMouseMove;
    }
    if (props.onSelect) {
        domNode.onselect = (typeof props.onSelect == 'string')
            ? new Function("event", props.onSelect)
            : props.onSelect;
    }
    return true;
}

/**
 * This function is used to set widget properties. Please see the 
 * _setProps() function for a list of supported properties.
 *
 * Note: This function updates the widget object for later updates. Further, the
 * widget shall be updated only for the given key-value pairs.
 *
 * Note: If the notify param is true, the widget's state change event shall be
 * published. This is typically used to keep client-side state in sync with the
 * server.
 *
 * @param props Key-Value pairs of properties.
 * @param notify Publish an event for custom AJAX implementations to listen for.
 */
webui.@THEME@.widget.widgetBase.setProps = function(props, notify) {
    if (props == null) {
        return false;
    }

    // Extend widget object for later updates.
    this.widget.extend(this, props);

    // Set properties.
    this._setProps(props);

    // Notify listeners state has changed.
    if (new Boolean(notify).valueOf() == true) {
        this.stateChanged(props);
    }
    return true;
}

/**
 * This function is used to set widget properties with the following 
 * Object literals.
 *
 * <ul>
 *  <li>className</li>
 *  <li>id</li>
 *  <li>style</li>
 *  <li>visible</li>
 * </ul>
 *
 * Note: This is considered a private API, do not use. This function should only
 * be invoked through postInitialize() and setProps(). Further, the widget shall
 * be updated only for the given key-value pairs.
 *
 * @param props Key-Value pairs of properties.
 */
webui.@THEME@.widget.widgetBase._setProps = function(props) {
    if (props == null) {
        return false;
    }

    // Set style class -- must be set before calling setCoreProps().
    props.className = this.getClassName();

    // Set more properties.
    return this.setCoreProps(this.domNode, props);
}

// Inherit base widget properties.
dojo.inherits(webui.@THEME@.widget.widgetBase, dojo.widget.HtmlWidget);

// Override base widget by assigning properties to class prototype.
dojo.lang.extend(webui.@THEME@.widget.widgetBase, {
    // Set private functions.
    ajaxify: webui.@THEME@.widget.widgetBase.ajaxify,
    buildRendering: webui.@THEME@.widget.widgetBase.buildRendering,
    fillInTemplate: webui.@THEME@.widget.widgetBase.fillInTemplate,
    getClassName: webui.@THEME@.widget.widgetBase.getClassName,
    getCommonProps: webui.@THEME@.widget.widgetBase.getCommonProps,
    getCoreProps: webui.@THEME@.widget.widgetBase.getCoreProps,
    getEventProps: webui.@THEME@.widget.widgetBase.getEventProps,
    getProps: webui.@THEME@.widget.widgetBase.getProps,
    initialize: webui.@THEME@.widget.widgetBase.initialize,
    isInitialized: webui.@THEME@.widget.widgetBase.isInitialized,
    postInitialize: webui.@THEME@.widget.widgetBase.postInitialize,
    postCreate: webui.@THEME@.widget.widgetBase.postCreate,
    refresh: webui.@THEME@.widget.widgetBase.event.refresh.processEvent,
    setCommonProps: webui.@THEME@.widget.widgetBase.setCommonProps,
    setCoreProps: webui.@THEME@.widget.widgetBase.setCoreProps,
    setEventProps: webui.@THEME@.widget.widgetBase.setEventProps,
    setProps: webui.@THEME@.widget.widgetBase.setProps,
    _setProps: webui.@THEME@.widget.widgetBase._setProps,
    stateChanged: webui.@THEME@.widget.widgetBase.event.state.processEvent,

    // Set defaults.
    event: webui.@THEME@.widget.widgetBase.event, // Common events.
    theme: webui.@THEME@.theme.common, // Common theme utils.
    widget: webui.@THEME@.widget.common // Common widget utils.
});
