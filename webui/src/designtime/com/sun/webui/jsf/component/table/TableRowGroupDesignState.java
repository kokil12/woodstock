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
 * TableRowGroupDesignState.java
 * Created on April 29, 2005, 12:40 PM
 * Version 1.0
 */

package com.sun.webui.jsf.component.table;

import com.sun.data.provider.FieldKey;
import com.sun.data.provider.TableDataProvider;
import com.sun.data.provider.impl.ObjectArrayDataProvider;
import com.sun.data.provider.impl.ObjectListDataProvider;
import com.sun.rave.designtime.DesignBean;
import com.sun.rave.designtime.ext.DesignBeanExt;
import com.sun.rave.designtime.DesignContext;
import com.sun.rave.designtime.DesignProject;
import com.sun.rave.designtime.DesignProperty;
import com.sun.rave.designtime.Position;
import com.sun.rave.designtime.faces.FacesDesignContext;
import com.sun.rave.designtime.faces.FacesDesignProject;
import com.sun.webui.jsf.component.Checkbox;
import com.sun.webui.jsf.component.TableColumn;
import com.sun.webui.jsf.component.TableRowGroupDesignInfo;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Vector;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

/**
 * This class defines the design time state of the Table Group Component
 *
 * @author Winston Prakash
 */
public class TableRowGroupDesignState {
    
    private ResourceBundle bundle =
            ResourceBundle.getBundle(TableRowGroupDesignState.class.getPackage().getName() + ".Bundle");
    
    private static final String MODEL_INSTANCE_NAME_SUFFIX =  "DefaultModel"; //NOI18N
    
    private static final String SOURCE_DATA_PROPERTY = "sourceData";
    private static final String SOURCE_VARIABLE_PROPERTY = "sourceVar";
    private static final String ROWS_PROPERTY = "rows";
    private static final String EMPTY_DATA_MSG_PROPERTY = "emptyDataMsg";
    private static final String PAGINATED_PROPERTY = "paginated";
    
    private DesignBean tableRowGroupBean = null;
    
    FacesDesignContext fcontext = null;
    
    public static final String sourceVarNameBase = "currentRow"; //NOI18N
    private String sourceVarName = sourceVarNameBase;
    
    private Map selectedColumnsDesignStates = new HashMap();
    
    private Vector selectedColumnNames = new Vector();
    private Vector availableColumnNames = new Vector();
    
    private TableDataProvider tableDataProvider;
    private DesignBean dataProviderBean = null;
    
    private int paginationRows = 10;
    private String emptyDataMsg = null;
    
    private boolean childBeansDeleted = false;
    private boolean rowGroupPaginated = false;
    
    private boolean dataProviderReset = false;
    
    private static int varCount = 0;
    
    private List sourceVariableList = new ArrayList();
    
    /** Creates a new instance of TableDesignState */
    public TableRowGroupDesignState(DesignBean tblGrpBean) {
        tableRowGroupBean = tblGrpBean;
        fcontext = (FacesDesignContext) tableRowGroupBean.getDesignContext();
    }
    
    /**
     * Set the selected column names
     */
    public void setSelectedColumnNames(Vector selectedColNames){
        selectedColumnNames =  selectedColNames;
    }
    
    /**
     * Get the selected column names
     */
    public Vector getSelectedColumnNames(){
        return selectedColumnNames;
    }
    
    /**
     * Set the available column names
     */
    public void setAvailableColumnNames(Vector availColNames){
        availableColumnNames =  availColNames;
    }
    
    /**
     * Get the available column names
     */
    public Vector getAvailableColumnNames(){
        return availableColumnNames;
    }
    
    /**
     * Set the Table column design states
     */
    public void setColumnDesignStates(Map colDesignStates){
        selectedColumnsDesignStates =  colDesignStates;
    }
    
    /**
     * Get the Table column design states
     */
    public Map getColumnDesignStates(){
        return selectedColumnsDesignStates;
    }
    
    /**
     * Get the associated Data Model Bean
     */
    public DesignBean getDataProviderBean(){
        return dataProviderBean;
    }
    
    /**
     * Set the Data model DesignBeean to this design state
     */
    public void setDataProviderBean(DesignBean modelBean){
        setDataProviderBean(modelBean, true);
    }
    
    /**
     * Set the Data model DesignBeean to this design state
     * Force the selected columns names with all columns from the Data model
     */
    public void setDataProviderBean(DesignBean modelBean, boolean resetColumns){
        if(modelBean != dataProviderBean){
            if(modelBean.getInstance()  instanceof TableDataProvider){
                tableDataProvider =  (TableDataProvider) modelBean.getInstance();
            }else if(List.class.isAssignableFrom(modelBean.getBeanInfo().getBeanDescriptor().getBeanClass())){
                List listObject = (List)modelBean.getInstance();
                if(listObject == null){
                    listObject = new ArrayList();
                }
                tableDataProvider =  new ObjectListDataProvider(listObject);
                if(modelBean instanceof DesignBeanExt){
                    try {
                        java.lang.reflect.Type[] parameterTypes = ((com.sun.rave.designtime.ext.DesignBeanExt) modelBean).getTypeParameters();
                        if (parameterTypes != null && (parameterTypes.length > 0)) {
                            ((com.sun.data.provider.impl.ObjectListDataProvider) tableDataProvider).setObjectType((java.lang.Class) parameterTypes[0]);
                        }
                    } catch (ClassNotFoundException exc) {
                        exc.printStackTrace();
                    }
                }
            }else if(modelBean.getInstance()  instanceof Object[]){
                tableDataProvider = new ObjectArrayDataProvider((Object[])modelBean.getInstance());
            }else{
                throw new IllegalArgumentException(dataProviderBean.getInstanceName() + bundle.getString("NOT_DATA_PROVIDER"));
            }
            
            FieldKey[] columns = tableDataProvider.getFieldKeys();
            if ((columns == null) || (columns.length == 0)) {
                return;
            }
            
            // OK new Table Data Provider is added. Remove all old columns
            DesignBean[] children = tableRowGroupBean.getChildBeans();
            for(int i=0; i< children.length; i++){
                fcontext.deleteBean(children[i]);
            }
            childBeansDeleted = true;
        }else{
            childBeansDeleted = false;
        }
        
        dataProviderBean = modelBean;
        
        if(resetColumns){
            resetTableColumns();
        }
    }
    
    public void loadState() {
        
        // Load the model bean.
        loadModelBean();
        
        if(!dataProviderReset){
            Map dpFields = new HashMap();
            try{
                FieldKey[] columns = tableDataProvider.getFieldKeys();
                if((columns != null) && (columns.length > 0)){
                    for (int i=0; i< columns.length; i++){
                        //Skip FieldKey of type "Class" - 6309491
                        if(tableDataProvider.getType(columns[i]).toString().indexOf("java.lang.Class") == -1){
                            dpFields.put(columns[i].getDisplayName(), columns[i]);
                        }
                    }
                }
            }catch(Exception exc){
                exc.printStackTrace();
            }
            loadSourceVariable();
            // Load the child state from the TableColumn
            int childCount = tableRowGroupBean.getChildBeanCount();
            for(int i=0; i< childCount; i++){
                DesignBean tblColumndBean = tableRowGroupBean.getChildBean(i);
                if (tblColumndBean.getInstance() instanceof TableColumn){
                    TableColumnDesignState tblColDesignState = new TableColumnDesignState(tblColumndBean);
                    tblColDesignState.loadState();
                    // If the Column has been removed from the CachedRowsetDataProvider change designstates name to
                    // TableColumnBean instance name and the data binding too and set the child type as Static Text
                    // Reset only if the Table Column is bound to this Table Data Provider
                    /*if(!dpFields.contains(tblColDesignState.getName()) &&
                            (tblColDesignState.getValueExpression().indexOf(sourceVarName) != -1)){
                        tblColDesignState.setName(tblColumndBean.getInstanceName());
                        tblColDesignState.setHeader(tblColumndBean.getInstanceName());
                        tblColDesignState.setType(StaticText.class);
                        tblColDesignState.setValueExpression(ResourceBundle.getBundle("com/sun/webui/jsf/component/table/Bundle").getString("STATIC_TEXT_LBL"));
                    }*/
                    if(dpFields.keySet().contains(tblColDesignState.getName())){
                        FieldKey column =  (FieldKey) dpFields.get(tblColDesignState.getName());
                        tblColDesignState.setColumnType(tableDataProvider.getType(column));
                    }
                    selectedColumnsDesignStates.put(tblColDesignState.getName(), tblColDesignState);
                    selectedColumnNames.add(tblColDesignState.getName());
                }
            }
        }
        
        paginationRows = getIntegerPropertyValue(ROWS_PROPERTY);
        if(paginationRows == 0) paginationRows = 10;
        emptyDataMsg = getStringPropertyValue(EMPTY_DATA_MSG_PROPERTY);
    }
    
    /**
     * Load the source variable. Even the Row Group gets new Data Provider
     * the old source variable name should be preserved
     */
    public void loadSourceVariable(){
        sourceVarName = getStringPropertyValue(SOURCE_VARIABLE_PROPERTY);
    }
    
    // For performance improvement. No need to get all the contexts in the project
    // Bug Fix: 6422729
    private DesignContext[] getDesignContexts(DesignBean designBean){
        DesignProject designProject = designBean.getDesignContext().getProject();
        DesignContext[] contexts;
        if (designProject instanceof FacesDesignProject) {
            contexts = ((FacesDesignProject)designProject).findDesignContexts(new String[] {
                "request",
                "session",
                "application"
            });
        } else {
            contexts = new DesignContext[0];
        }
        DesignContext[] designContexts = new DesignContext[contexts.length + 1];
        designContexts[0] = designBean.getDesignContext();
        System.arraycopy(contexts, 0, designContexts, 1, contexts.length);
        return designContexts;
    }
    
    /**
     * Load the model bean from the TableRowGroup bean from the source data tag.
     * If not found create or get the default model bean from the context
     */
    private void loadModelBean(){
        String sourceDataStr = getPropertyValueSource(SOURCE_DATA_PROPERTY);
        if(sourceDataStr != null) {
            //DesignContext[] contexts = fcontext.getProject().getDesignContexts();
            DesignContext[] contexts = getDesignContexts(tableRowGroupBean);
            for (int i = 0; i < contexts.length; i++) {
                DesignBean[] dpModelBeans = contexts[i].getBeansOfType(TableDataProvider.class);
                for(int j=0; j< dpModelBeans.length; j++){
                    String modelBindingExpr = ((FacesDesignContext)contexts[i]).getBindingExpr(dpModelBeans[j]);
                    if(sourceDataStr.startsWith(modelBindingExpr)){
                        dataProviderBean = dpModelBeans[j];
                        tableDataProvider =  (TableDataProvider) dataProviderBean.getInstance();
                        break;
                    }
                }
                
                DesignBean[] listModelBeans = contexts[i].getBeansOfType(List.class);
                for(int j=0; j< listModelBeans.length; j++){
                    String modelBindingExpr = ((FacesDesignContext)contexts[i]).getBindingExpr(listModelBeans[j]);
                    if(sourceDataStr.startsWith(modelBindingExpr)){
                        dataProviderBean = listModelBeans[j];
                        List listObject = (List)dataProviderBean.getInstance();
                        if(listObject == null){
                            listObject = new ArrayList();
                        }
                        tableDataProvider =  new ObjectListDataProvider(listObject);
                        if(dataProviderBean instanceof DesignBeanExt){
                            try {
                                java.lang.reflect.Type[] parameterTypes = ((com.sun.rave.designtime.ext.DesignBeanExt) dataProviderBean).getTypeParameters();
                                if (parameterTypes != null && (parameterTypes.length > 0)) {
                                    ((com.sun.data.provider.impl.ObjectListDataProvider) tableDataProvider).setObjectType((java.lang.Class) parameterTypes[0]);
                                }
                            } catch (ClassNotFoundException exc) {
                                exc.printStackTrace();
                            }
                        }
                        
                        break;
                    }
                }
                
                DesignBean[] arrayModelBeans = contexts[i].getBeansOfType(Object[].class);
                for(int j=0; j< arrayModelBeans.length; j++){
                    String modelBindingExpr = ((FacesDesignContext)contexts[i]).getBindingExpr(arrayModelBeans[j]);
                    if(sourceDataStr.startsWith(modelBindingExpr)){
                        dataProviderBean = arrayModelBeans[j];
                        tableDataProvider =  new ObjectArrayDataProvider((Object[])dataProviderBean.getInstance());
                        break;
                    }
                }
            }
        }
        // XXX - What should we do if the user deleteds the source data?
        if(dataProviderBean == null){
            dataProviderBean = TableDesignHelper.createDefaultDataProvider(tableRowGroupBean.getBeanParent());
            tableDataProvider =  (TableDataProvider) dataProviderBean.getInstance();
            resetTableColumns();
            dataProviderReset = true;
        }
    }
    
    private void resetTableColumns(){
        // Set the selected column names from the default data model
        FieldKey[] columns = tableDataProvider.getFieldKeys();
        if((columns != null) && (columns.length > 0)){
            for (int i=0; i< columns.length; i++){
                if (tableDataProvider.getType(columns[i]).toString().indexOf("java.lang.Class") != -1) {
                    continue;
                }
                selectedColumnNames.add(columns[i].getDisplayName());
                TableColumnDesignState tblColDesignState = new TableColumnDesignState(columns[i].getDisplayName());
                tblColDesignState.setColumnType(tableDataProvider.getType(columns[i]));
                if (tableDataProvider.getType(columns[i]).isAssignableFrom(Boolean.class)){
                    tblColDesignState.setChildType(Checkbox.class);
                }
                selectedColumnsDesignStates.put(tblColDesignState.getName(), tblColDesignState);
            }
        }
    }
    
    /**
     * Clear all the property values set to this state
     */
    public void clearProperties(){
        paginationRows = 5;
    }
    
    /**
     * Get the boolean value of the property
     */
    public boolean getBooleanPropertyValue(String propertyname){
        boolean value = false;
        Object propValue = getPropertyValue(propertyname);
        if(propValue != null){
            value = ((Boolean) propValue).booleanValue();
        }
        return value;
    }
    
    /**
     * Get the boolean value of the property
     */
    private boolean getBooleanPropertyValue(String propertyname, DesignBean bean) {
        boolean value = false;
        Object propValue = getPropertyValue(propertyname, bean);
        if(propValue != null){
            value = ((Boolean) propValue).booleanValue();
        }
        return value;
    }
    
    /**
     * Get String property value
     */
    private String getStringPropertyValue(String propertyname){
        Object value = getPropertyValue(propertyname);
        if(value != null){
            return value.toString();
        }else{
            return null;
        }
    }
    
    /**
     * Get int property value
     */
    private int getIntegerPropertyValue(String propertyname){
        Object value = getPropertyValue(propertyname);
        if(value != null){
            return ((Integer)value).intValue();
        }else{
            return -1;
        }
    }
    
    /**
     * Load the property value from the bean to this state
     */
    private Object getPropertyValue(String propertyname, DesignBean bean){
        Object propertyValue = null;
        DesignProperty designProperty = bean.getProperty(propertyname);
        if(designProperty != null){
            if(designProperty.getValue() != null){
                propertyValue = designProperty.getValue();
            }
        }
        return propertyValue;
    }
    
    private Object getPropertyValue(String propertyname) {
        return getPropertyValue(propertyname, tableRowGroupBean);
    }
    
    /**
     * Get the property value source from the bean to this state
     */
    private String getPropertyValueSource(String propertyname){
        String propertyValue = null;
        DesignProperty designProperty = tableRowGroupBean.getProperty(propertyname);
        if(designProperty != null){
            propertyValue = designProperty.getValueSource();
        }
        return propertyValue;
    }
    
    /**
     * Set the value to the bean property as stored in this state
     */
    private void setPropertyValue(String propertyname, Object value){
        if(value != null){
            DesignProperty designProperty = tableRowGroupBean.getProperty(propertyname);
            if(designProperty != null){
                Object origValue = getPropertyValue(propertyname);
                if(value != origValue){
                    if((value instanceof String) && value.toString().equals("")){
                        designProperty.unset();
                    }else{
                        designProperty.setValue(value);
                    }
                }
            }
        }
    }
    
    private void setPropertyValue(String propertyname, Object value, DesignBean bean){
        if(value != null){
            DesignProperty designProperty = bean.getProperty(propertyname);
            if(designProperty != null){
                Object origValue = getPropertyValue(propertyname, bean);
                if(value != origValue){
                    if((value instanceof String) && value.toString().equals("")){
                        designProperty.unset();
                    }else{
                        designProperty.setValue(value);
                    }
                }
            }
        }
    }
    
    
    /**
     * Set a boolean value to the property
     */
    private void setBooleanPropertyValue(String propertyname, boolean value){
        DesignProperty designProperty = tableRowGroupBean.getProperty(propertyname);
        if(designProperty != null){
            boolean origValue = getBooleanPropertyValue(propertyname);
            if(origValue != value){
                if(value){
                    designProperty.setValue(new Boolean(true));
                }else{
                    designProperty.unset();
                }
            }
        }
    }
    
    /**
     * Set the empty data  message
     */
    public void setEmptyDataMsg(String msg){
        emptyDataMsg = msg;
    }
    
    /**
     * Get the empty data  message
     */
    public String getEmptyDataMsg(){
        return emptyDataMsg;
    }
    
    /**
     * Set the paginated property to the table row group
     */
    public void setPaginated(boolean paginated){
        rowGroupPaginated = paginated;
    }
    
    /**
     * Get the paginated property to the table row group
     */
    public boolean getPaginated(){
        return rowGroupPaginated;
    }
    
    /**
     * Set the no of rows used to paginate the table row group
     */
    public void setRows(int rows){
        paginationRows = rows;
    }
    
    /**
     * Get the no of rows used to paginate the table row group
     */
    public int getRows(){
        return paginationRows;
    }
    
    /*public int getColumnWidth(int colNo){
        String colName = (String) selectedColumnNames.get(colNo);
        TableColumnDesignState columnsDesignState = (TableColumnDesignState) selectedColumnsDesignStates.get(colName);
        return columnsDesignState.getWidth();
    }
     
    public void setColumnWidth(int colNo, int colWidth){
        setColumnWidth(colNo, colWidth, false);
    }
     
    public void setColumnWidth(int colNo, int colWidth, boolean immediat){
        String colName = (String) selectedColumnNames.get(colNo);
        TableColumnDesignState columnsDesignState = (TableColumnDesignState) selectedColumnsDesignStates.get(colName);
        columnsDesignState.setWidth(colWidth, immediat);
    }*/
    
    /**
     * Save the design state of the TableRowGroup component
     */
    public void saveState() {
        
        if (selectedColumnNames.size() < 1){
            return;
        }
   
        String dataProviderBeanExpr = ((FacesDesignContext) dataProviderBean.getDesignContext()).getBindingExpr(dataProviderBean);
        TableRowGroupDesignInfo rgdi = (TableRowGroupDesignInfo) tableRowGroupBean.getDesignInfo();
        rgdi.setColumnsAlreadyReconstructed(true);
        setPropertyValue(SOURCE_DATA_PROPERTY, dataProviderBeanExpr);
        setPropertyValue(EMPTY_DATA_MSG_PROPERTY, emptyDataMsg);
        setBooleanPropertyValue(PAGINATED_PROPERTY, rowGroupPaginated);

        if (sourceVarName == null) {
            sourceVarName = sourceVarNameBase;
        }

        setPropertyValue(SOURCE_VARIABLE_PROPERTY, sourceVarName);

        try {
            setPropertyValue(ROWS_PROPERTY, new Integer(paginationRows));
        } catch (Exception exc) {
            exc.printStackTrace();
        }
   
        
        if(dataProviderReset){
            // OK new Table Data Provider is reset. Remove all old columns
            DesignBean[] children = tableRowGroupBean.getChildBeans();
            for(int i=0; i< children.length; i++){
                fcontext.deleteBean(children[i]);
            }
            childBeansDeleted = true;
        }
        
        // Persist the design states of the selected Table Column.
        // Create the table column bean if not created already.
        for (int i=0; i< selectedColumnNames.size(); i++){
            // Create the Table Columns, set it to the TableColumnDesignState and save its state
            TableColumnDesignState tblColDesignState = (TableColumnDesignState) selectedColumnsDesignStates.get(selectedColumnNames.get(i));
            if((tblColDesignState.getTableColumnBean() == null) || childBeansDeleted){
                DesignBean tableColumnBean = fcontext.createBean(TableColumn.class.getName(), tableRowGroupBean, null);
                tblColDesignState.setTableColumnBean(tableColumnBean);
            }
            tblColDesignState.setSourceVariable(sourceVarName);
            tblColDesignState.saveState();
            fcontext.moveBean(tblColDesignState.getTableColumnBean(), tableRowGroupBean, new Position(i));
        }
        
        // Remove only the tabel column design bean. Do not yet
        // remove the Table Column design states. User must not
        // have closed the customizer only pressed apply button.
        for (int i=0; i< availableColumnNames.size(); i++){
            TableColumnDesignState tblColDesignState = (TableColumnDesignState) selectedColumnsDesignStates.get(availableColumnNames.get(i));
            if(tblColDesignState.getTableColumnBean() != null){
                fcontext.deleteBean(tblColDesignState.getTableColumnBean());
                tblColDesignState.setTableColumnBean(null);
            }
        }
        
        //create or delete the "selection column" if appropriate
        String tableRowGroupBeanName = tableRowGroupBean.getInstanceName();
        String selectionColumnName = tableRowGroupBeanName + TableRowGroupDesignInfo.SELECTION_COLUMN_SUFFIX;
        //look for the "selection column" design bean
        DesignBean selectionColumnBean = TableDesignHelper.findChildBeanByName(tableRowGroupBean, selectionColumnName);
        DesignBean tableBean = tableRowGroupBean.getBeanParent();
        //get the selectMultipleButton and deselectMultipleButton properties of the table bean
        boolean useSelectMultipleButton = getBooleanPropertyValue("selectMultipleButton", tableBean);
        boolean useDeselectMultipleButton = getBooleanPropertyValue("deselectMultipleButton", tableBean);
        if (useSelectMultipleButton || useDeselectMultipleButton) { //if either is true
            if (selectionColumnBean == null) {  //if the selection column does not exist
                //create the selection column
                selectionColumnBean = fcontext.createBean(TableColumn.class.getName(), tableRowGroupBean, new Position(0));
                selectionColumnBean.setInstanceName(selectionColumnName);
                //create a checkbox as a child of the selection column
                DesignBean selectionChildBean = fcontext.createBean(Checkbox.class.getName(), selectionColumnBean, null);
                String selectionChildName = tableRowGroupBeanName + TableRowGroupDesignInfo.SELECTION_CHILD_SUFFIX;
                selectionChildBean.setInstanceName(selectionChildName);
                //set selectionChildName as the value of the selectionColumnBean's selectId property
                setPropertyValue("selectId", selectionChildName, selectionColumnBean);
                UIComponent tableComponent = (UIComponent)tableBean.getInstance();
                FacesContext facesContext = fcontext.getFacesContext();
                String tableClientId = tableComponent.getClientId(facesContext);
                String javascript = "setTimeout(function(){document.getElementById('" + tableClientId + "').initAllRows()}, 0);"; //NOI18N
                //set the javascript as the value of the selectionColumnBean's onClick property
                setPropertyValue("onClick", javascript, selectionColumnBean);
            }
        }
        else if (selectionColumnBean != null) { //if neither useSelectMultipleButton or useDeselectMultipleButton is true, and the selection column exists
            //delete the selection column
            fcontext.deleteBean(selectionColumnBean);
        }
        
        if(childBeansDeleted) TableDesignHelper.adjustTableWidth(tableRowGroupBean);
        
        childBeansDeleted = false;
        
        if(!TableDesignHelper.isDefaultDataProvider(tableRowGroupBean.getBeanParent(), dataProviderBean)){
            TableDesignHelper.deleteDefaultDataProvider(tableRowGroupBean.getBeanParent());
        }
    }
}
