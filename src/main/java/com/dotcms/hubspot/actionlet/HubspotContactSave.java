package com.dotcms.hubspot.actionlet;

import com.dotcms.repackage.org.apache.commons.httpclient.HttpClient;
import com.dotcms.repackage.org.apache.commons.httpclient.HttpStatus;
import com.dotcms.repackage.org.apache.commons.httpclient.methods.PostMethod;
import com.dotmarketing.portlets.contentlet.model.Contentlet;
import com.dotmarketing.portlets.workflows.actionlet.WorkFlowActionlet;
import com.dotmarketing.portlets.workflows.model.WorkflowActionClassParameter;
import com.dotmarketing.portlets.workflows.model.WorkflowActionFailureException;
import com.dotmarketing.portlets.workflows.model.WorkflowActionletParameter;
import com.dotmarketing.portlets.workflows.model.WorkflowProcessor;
import com.dotmarketing.util.Logger;
import com.dotmarketing.util.UtilMethods;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Jonathan Gamba
 *         7/29/16
 */
public class HubspotContactSave extends WorkFlowActionlet {

    private static final long serialVersionUID = 1L;

    //https://developers.hubspot.com/docs/methods/forms/submit_form
    //https://forms.hubspot.com/uploads/form/v2/:portal_id/:form_guid
    private final String POST_URL = "https://forms.hubspot.com/uploads/form/v2";
    private final String PORTAL_ID = "";

    private final String PARAMETER_FIELDS_MAPPING = "fieldsMapping";
    private final String PARAMETER_PORTAL_ID = "portalId";
    private final String PARAMETER_FORM_ID = "formId";

    private final String SPLIT_STRING = ",";
    private final String SPLIT_MAPPING_STRING = ":";

    /**
     * Returns the list of parameters required in order to set up this Actionlet inside a Workflow Action
     *
     * @return List of attributes required for the set up of this Actionlet
     */
    @Override
    public List<WorkflowActionletParameter> getParameters() {
        List<WorkflowActionletParameter> params = new ArrayList<>();
        params.add(new WorkflowActionletParameter(PARAMETER_FIELDS_MAPPING, "Hubspot Fields Mapping", "", true));
        params.add(new WorkflowActionletParameter(PARAMETER_PORTAL_ID, "HubSpot Portal Id", PORTAL_ID, true));
        params.add(new WorkflowActionletParameter(PARAMETER_FORM_ID, "HubSpot Form Id", "", false));
        return params;
    }

    @Override
    public String getName() {
        return "Content -> Hubspot Contact";
    }

    @Override
    public String getHowTo() {
        return "This actionlet will post content from a Content entry to a Hubspot Contact form.  formId will be read from the contentlet.formId field if it exists, otherwise it will take what is entered below.";
    }

    /**
     * This method reads the parameters set on this Actionlet (<strong>Hubspot Portal Id</strong>, <strong>Hubspot Form Id</strong>
     * and the <strong>Hubspot Fields Mapping</strong>) and executes a post call to the Hubspot API with the field values
     * of the processed Contentlet in order to create a Hubspot contact Form entry.
     * <br>
     * <br>
     * On Hubspot each field of the contact form have an id, in order to map each of the fields of the
     * Content Type using this Actionlet with those Hubspot fields we use the <strong>Hubspot Fields Mapping</strong>
     * parameter, with that parameter we map the dotCMS field varname with the hotspot Contact Form field id.
     * <br>
     * <strong>Example:</strong> dotCMSFieldVarname1:hotspotFieldId1, dotCMSFieldVarname2:hotspotFieldId2, dotCMSFieldVarname3:hotspotFieldId3
     * <br>
     * <strong>NOTE:</strong> Only mapped fields are going to be sent to the Hubspot API
     *
     * @param processor
     * @param parameters
     * @throws WorkflowActionFailureException
     */
    @Override
    public void executeAction(WorkflowProcessor processor, Map<String, WorkflowActionClassParameter> parameters) throws WorkflowActionFailureException {

        //Get the content we want to process
        Contentlet contentletToProcess = processor.getContentlet();

        //Create the client and post method in order to submit the content to the hubspot API
        HttpClient client = new HttpClient();
        /*
         https://developers.hubspot.com/docs/methods/forms/submit_form
         https://forms.hubspot.com/uploads/form/v2/:portal_id/:form_guid
         */
        
        
        final String formId = (contentletToProcess.get("formId")!=null) 
            ? contentletToProcess.getStringProperty("formId")
            : parameters.get(PARAMETER_FORM_ID).getValue();

        
        
        
        PostMethod postMethod = new PostMethod(POST_URL +
                "/" + parameters.get(PARAMETER_PORTAL_ID).getValue() +
                "/" + formId);

        //Prepare the post parameters using the mapping between the contentlet and the Hubspot fields
        String fieldsMapping = parameters.get(PARAMETER_FIELDS_MAPPING).getValue().trim();//dotCMSField1:prop1, dotCMSField2:prop2
        String[] fieldsMappingArray = fieldsMapping.split(SPLIT_STRING);
        for ( String mapping : fieldsMappingArray ) {
            mapping = mapping.trim();
            if ( UtilMethods.isSet(mapping) ) {

                String fields[] = mapping.split(SPLIT_MAPPING_STRING);
                String dotCMSVarName = fields[0].trim();
                String hubspotProperty = fields[1].trim();

                //Getting the dotCMS field value
                String fieldValue = contentletToProcess.getStringProperty(dotCMSVarName);
                if ( UtilMethods.isSet(fieldValue) ) {
                    //Add the form parameter for the post call
                    postMethod.addParameter(hubspotProperty, fieldValue);
                }
            }
        }

        try {
            //And finally submit the contact info
            client.executeMethod(postMethod);

            int responseStatusCode = postMethod.getStatusCode();
            if ( responseStatusCode == HttpStatus.SC_OK || responseStatusCode == HttpStatus.SC_NO_CONTENT ) {//Everything ok...
                Logger.info(this.getClass(), "Successfully submit to Hubspot Contact Form");
            } else {
                Logger.error(this.getClass(), "Error submitting to Hubspot Contact Form. Response code [" + responseStatusCode + "].");
            }
        } catch (IOException e) {
            Logger.error(this.getClass(), e.getMessage(), e);
            throw new WorkflowActionFailureException(e.getMessage(), e);
        } finally {
            postMethod.releaseConnection();
        }
    }

}