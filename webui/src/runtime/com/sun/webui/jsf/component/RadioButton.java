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

import com.sun.faces.annotation.Component;
import com.sun.faces.annotation.Property;
import com.sun.webui.jsf.util.ComponentUtilities;
import com.sun.webui.jsf.util.ConversionUtilities;

import java.util.Map;

import javax.el.ValueExpression;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter; // for javadoc
import javax.faces.convert.ConverterException;
import javax.faces.render.Renderer;

/**
 * <p>A component that represents a radio button.</p>
 * <p>
 * The <code>RadioButton</code> can be used as a single radio button
 * or one radio button among a group of radio button. A group
 * of radio button represents a multiple selection list which can have any
 * number of radio button selected, or none selected. A radio button can
 * represent a <code>Boolean</code> value, a <code>String</code> value,
 * or a developer defined <code>Object</code> value.
 * </p>
 * <h3>Detecting a selected radio button</h3>
 * <p>
 * The <code>RadioButton</code> uses both the <code>selected</code>
 * and <code>selectedValue</code> properties to pass information about
 * the radio button's selection status. The <code>selected</code>
 * property is used to indicate that the radio button is selected.
 * The <code>selectedValue</code> property is used to pass a data value,
 * a string by default, for the radio button. A radio button is considered to be
 * selected when the value of the <code>selected</code> property is equal
 * to the value of the <code>selectedValue</code> property. A radio button can
 * be initally selected by assigning the same value
 * to the <code>selectedValue</code> and the <code> selected</code>
 * properties. <code>isChecked</code> is called to determine
 * if this <code>RadioButton</code> is selected.
 * </p>
 * <p>If the <code>selectedValue</code> property is not specified or its
 * value is <code>null</code> then the radio button behaves like a
 * boolean control. If the radio button is selected, the value of the
 * <code>selected</code> property is a true <code>Boolean</code>
 * instance. If the radio button is not selected, the value of the
 * <code>selected</code> property will be a false <code>Boolean</code>
 * instance.
 * </p>
 * <p> <em>Note that a value binding expression that evaluates to a
 * primitive boolean value can be assigned to the <code>selected</code>
 * property. Proper type coercion from <code>Boolean</code> to
 * <code>boolean</code> occurs.</em>
 * </p>
 * <p>
 * When a radio button is part of a group, the selected radio
 * button is maintained as a request attribute in the
 * <code>RequestMap</code>. The name of the attribute is
 * the value of the radio button's <code>name</code> property.
 * The request attribute value is the
 * value of the <code>selectedValue</code> property of the
 * selected radio button.
 * The <code>selected</code> property
 * of the selected radio button within the group, will also contain the
 * value of the <code>selectedValue</code> property of the
 * respective selected radio button. If no radio buttons are selected,
 * no request attribute is created, however at least one radio button
 * must be selected.
 * </p>
 * <p><em>
 * Note that the <code>RadioButton</code> does not enforce the
 * requirement that at least one radio button must be selected.
 * The application should ensure that this requirement is met.
 * </em></p>
 * <h3>Using a <code>radio button</code> tag as a boolean control</h3>
 * <p>
 * If the <code>selectedValue</code> property is not specified or its
 * value is <code>null</code> then the radio button behaves like a
 * boolean control.
 * </p>
 * <p>
 * To use the <code>RadioButton</code> as a boolean control, do not
 * specify a value for the <code>selectedValue</code> property. The
 * radio button is selected if the <code>selected</code> property is not
 * null and has the value of a Boolean instance with a <code>true</code>
 * value. If the radio button is not selected, then the value of the
 * <code>selected</code> property is a false <code>Boolean</code> instance.
 * </p>
 * <p><em>Note that using a boolean radio button in a group and
 * referencing the request property for the selected radio button is not
 * useful, since the value of the request property will be an
 * indistinguishable <code>true</code> value.</em>
 * </p>
 * <h3>Using a <code>RadioButton</code> to represent a developer defined
 * value</h3>
 * <p> The <code>selectedValue</code> property can be assigned a
 * developer defined object value to represent the value of a selected
 * radio button. If the radio button is selected, the value of the
 * <code>selected</code> property is assigned the value of the
 * <code>selectedValue</code> property.
 * </p>
 * <p>
 * If the value of the <code>selectedValue</code> property is a
 * developer defined object, a <code>Converter</code> must be registered
 * to convert to and from a <code>String</code> value.<br>
 * In addition the object must support an
 * <code>equals</code> method that returns <code>true</code> when the
 * value of the <code>selectedValue</code> property is compared to
 * the <code>selected</code> property value in order to detect a
 * selected radio button.
 * </p>
 * <h3>Using a <code>RadioButton</code> as one control in a group</h3>
 * <p>
 * The <code>name</code> property determines whether a
 * radio button is part of a group. A radio button is treated as part of a group
 * of radio buttons if the <code>name</code> property of the radio button is
 * assigned a value equal to the <code>name</code> property of the other
 * radio buttons in the group. In other words, all radio button of a
 * group have the same <code>name</code> property value. The group behaves
 * like a single selection list, where only one radio button
 * can be selected. The value of the name property must
 * be unique within the scope of the Form parent containing the
 * radio buttons.
 * </p>
 * <h3>Facets</h3>
 * <p>
 * The following facets are supported:
 * </p>
 * <ul>
 *   <li><em>image</em> If the image facet exists, it replaces the
 *	{@link com.sun.webui.jsf.component.ImageComponent} subcompoent
 *	normally created for the image associated with the radio button,
 *	if the <code>imageURL</code> property is not null.</li>
 *   <li><em>label</em> If the label facet exists, it replaces the
 *	{@link com.sun.webui.jsf.component.Label} subcomponent normally
 *	created for the label associated with the radio button, if the
 *	label property is not null.</li>
 * </ul>
 * <p>
 * Add an image or label facet to the <code>RadioButton</code> if more
 * control over the properties of the subcomponents is needed.
 * </p>
 * <p>
 * <em>Note that if a facet is exists, <code>RadioButton</code> properties
 * that would normally be assigned to the created subcomponent, will
 * not be assigned to the facet</em>
 * </p>
 * <p>
 * <em>Note that unexpected layout of the <code>RadioButton</code> may occur
 * if the component specified by the facet is not a
 * {@link com.sun.webui.jsf.component.ImageComponent} for the image facet or
 * {@link com.sun.webui.jsf.component.Label} for the label facet.</em>
 * </p>
 * <h3>ImageComponent and Label subcomponents</h3>
 * <p>
 * An image and a label may be associated with the
 * <code>RadioButton</code>.<br/>
 * If the <code>imageURL</code> property is not null and an image facet
 * does not exist then a {@link com.sun.webui.jsf.component.ImageComponent}
 * component is created.<br/>
 * If the <code>label</code> property is not null and a label facet does not
 * exist then a {@link com.sun.webui.jsf.component.Label} component is
 * created.
 * </p>
 * <p>
 * The following <code>RadioButton</code> properties are assigned to
 * the subcomponents only if a facet does not exist.<br/>
 * For the {@link com.sun.webui.jsf.component.ImageComponent} subcomponent
 * <ul>this.getId() + "_image" is assigned to the <code>id</code> property.</li>
 * <li>this.getImageURL() is assigned to the <code>url</code> property.</li>
 * <li>this.getToolTip() is assigned to the <code>toolTip</code> property.</li>
 * <li>this.getToolTip() is assigned to the <code>alt</code> property.</li>
 * <li>this.isVisible() is assigned to the <code>visible</code> property.</li>
 * <li>this.isRendered() is assigned to the <code>renderer</code> property.</li>
 * </ul>
 * </p>
 * <p>
 * For the {@link com.sun.webui.jsf.component.Label} subcomponent
 * <ul>
 * <li>this.getId() + "_label" is assigned to the <code>id</code>
 * property.</li>
 * <li>this.getClientId() is assigned to the <code>for</code>
 * property.</li>
 * <li>this.getLabel() is assigned to the <code>text</code> property.</li>
 * <li>this.getLabelLevel is assigned to the <code>labelLevel</code> property.</li>
 * <li>this.getToolTip is assigned to the <code>toolTip</code> property.</li>
 * <li>this.isVisible is assigned to the <code>visible</code> property.</li>
 * <li>this.isRendered is assigned to the <code>renderer</code> property.</li>
 * </ul>
 * </p>
 * <em>Note that if a value binding exists for one of the
 * <code>RadioButton</code> properties mentioned above, the value binding is
 * set on the subcomponent for that property.</em>
 * </p>
 */
@Component(type="com.sun.webui.jsf.RadioButton", family="com.sun.webui.jsf.RadioButton", displayName="Radio Button", tagName="radioButton",
tagRendererType="com.sun.webui.jsf.widget.RadioButton",
        helpKey="projrave_ui_elements_palette_wdstk-jsf1.2_radiobutton",
        propertiesHelpKey="projrave_ui_elements_palette_wdstk-jsf1.2_propsheets_radiobutton_props")
        public class RadioButton extends RbCbSelector {
    public static final String RB_ID = "_rb"; //NOI18N
    /**
     * Default constructor.
     */
    public RadioButton() {
        super();
        setMultiple(false);
        setRendererType("com.sun.webui.jsf.widget.RadioButton");
    }
    
    /**
     * <p>Return the family for this component.</p>
     */
    public String getFamily() {
        return "com.sun.webui.jsf.RadioButton";
    }
    
    public String getRendererType() {
        // Ensure we have a valid Ajax request.
        if (ComponentUtilities.isAjaxRequest(getFacesContext(), this)) {
            return "com.sun.webui.jsf.ajax.RadioButton";
        }
        return super.getRendererType();
    }
    
    /**
     * Return the value of the <code>selectedValue</code> property
     * of the selected radio button in the group of radio buttons
     * identified by the <code>name</code> parameter.
     * A <code>RadioButton</code> is one of a group of radio buttons
     * if more than on radio button has the same value for the
     * <code>name</code> property.<br/>
     * When one of the radio buttons among that group is selected,
     * the value of its <code>selectedValue</code> property
     * is maintained in a request attribute identified by the value
     * of its <code>name</code> property.
     *
     * @param name the value a RadioButton name property.
     */
    public static Object getSelected(String name) {
        
        Map rm = FacesContext.getCurrentInstance().getExternalContext().
                getRequestMap();
        
        if (name != null) {
            return rm.get(name);
        } else {
            return null;
        }
    }
    
    /**
     * Alternative HTML template to be used by this component.
     */
    @Property(name="htmlTemplate", isHidden=true, isAttribute=true, displayName="HTML Template", category="Appearance")
    private String htmlTemplate = null;
    
    /**
     * Get alternative HTML template to be used by this component.
     */
    
    public String getHtmlTemplate() {
        if (this.htmlTemplate != null) {
            return this.htmlTemplate;
        }
        ValueExpression _vb = getValueExpression("htmlTemplate");
        if (_vb != null) {
            return (String) _vb.getValue(getFacesContext().getELContext());
        }
        return null;
    }
    
    /**
     * Set alternative HTML template to be used by this component.
     */
    
    public void setHtmlTemplate(String htmlTemplate) {
        this.htmlTemplate = htmlTemplate;
    }
    
    /**
     * Returns the absolute ID of an HTML element suitable for use as
     * the value of an HTML LABEL element's <code>for</code> attribute.
     * If the <code>ComplexComponent</code> has sub-compoents, and one of
     * the sub-components is the target of a label, if that sub-component
     * is a <code>ComplexComponent</code>, then
     * <code>getLabeledElementId</code> must called on the sub-component and
     * the value returned. The value returned by this
     * method call may or may not resolve to a component instance.
     * <p>
     * If <code>isReadOnly</code> returns true, <code>null</code> is
     * returned.
     * </p>
     *
     * @param context The FacesContext used for the request
     * @return An abolute id suitable for the value of an HTML LABEL element's
     * <code>for</code> attribute.
     */
    public String getLabeledElementId(FacesContext context) {
        String clntId = this.getClientId(context);
        return clntId.concat(this.RB_ID);
    }
    
    /**
     * <p>Update the request parameter that holds the value of the
     * <code>selectedValue</code> property of the selected radio button.
     * </p>
     * If the <code>name</code> property has been set
     * a request attribute is created.
     * The value of the <code>name</code> property will
     * be used for the request attribute name and the value of the request
     * attribute will be the value of the
     * <code>selectedValue</code> property.
     * <p>
     * The request attribute described above is available during
     * a <code>ValueChangeEvent</code>.
     * </p>
     *
     * @param context The context of this request.
     */
    public void validate(FacesContext context) {
        
        super.validate(context);
        
        if (!(isValid() && isChecked())) {
            return;
        }
        
        String groupName = getName();
        if (groupName == null) {
            return;
        }
        
        addToRequestMap(context, groupName);
    }
    
    protected void addToRequestMap(FacesContext context, String groupName) {
        
        Map requestMap = context.getExternalContext().getRequestMap();
        requestMap.put(groupName, getValue());
    }
    
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Tag attribute methods
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    
    // Hide items
    @Property(name="items", isHidden=true, isAttribute=false)
    public Object getItems() {
        return super.getItems();
    }
    
    // Hide required
    @Property(name="required", isHidden=true, isAttribute=false)
    public boolean isRequired() {
        return super.isRequired();
    }
    
    // Hide hidden
    @Property(isHidden=true, isAttribute=false)
    public Object getValue() {
        return super.getValue();
    }
    
    // Hide onSelect
    @Property(isHidden=true, isAttribute=false)
    public String getOnSelect() {
        return super.getOnSelect();
    }
    
    /**
     * <p>Sets the style level for the generated label, provided the
     * label attribute has been set. Valid values are 1 (largest), 2 and
     * 3 (smallest). The default value is 3.</p>
     */
    @Property(name="labelLevel", displayName="Label Level", category="Appearance")
    private int labelLevel = Integer.MIN_VALUE;
    private boolean labelLevel_set = false;
    
    /**
     * <p>Sets the style level for the generated label, provided the
     * label attribute has been set. Valid values are 1 (largest), 2 and
     * 3 (smallest). The default value is 3.</p>
     */
    public int getLabelLevel() {
        if (this.labelLevel_set) {
            return this.labelLevel;
        }
        ValueExpression _vb = getValueExpression("labelLevel");
        if (_vb != null) {
            Object _result = _vb.getValue(getFacesContext().getELContext());
            if (_result == null) {
                return Integer.MIN_VALUE;
            } else {
                return ((Integer) _result).intValue();
            }
        }
        return 3;
    }
    
    /**
     * <p>Sets the style level for the generated label, provided the
     * label attribute has been set. Valid values are 1 (largest), 2 and
     * 3 (smallest). The default value is 3.</p>
     * @see #getLabelLevel()
     */
    public void setLabelLevel(int labelLevel) {
        this.labelLevel = labelLevel;
        this.labelLevel_set = true;
    }
    
    /**
     * <p>Restore the state of this component.</p>
     */
    public void restoreState(FacesContext _context,Object _state) {
        Object _values[] = (Object[]) _state;
        super.restoreState(_context, _values[0]);
        this.labelLevel = ((Integer) _values[1]).intValue();
        this.labelLevel_set = ((Boolean) _values[2]).booleanValue();
        this.htmlTemplate = (String) _values[3];
    }
    
    /**
     * <p>Save the state of this component.</p>
     */
    public Object saveState(FacesContext _context) {
        Object _values[] = new Object[4];
        _values[0] = super.saveState(_context);
        _values[1] = new Integer(this.labelLevel);
        _values[2] = this.labelLevel_set ? Boolean.TRUE : Boolean.FALSE;
        _values[3] = this.htmlTemplate;
        return _values;
    }
}
