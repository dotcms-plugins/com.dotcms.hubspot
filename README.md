
## dotCMS Hubspot POC Plugin

This is a POC plugin that provides 4 components, 
- A Hubspot api interceptor that allows hubspot APIs to be called through dotCMS
- A Velocity viewtool that maps a site visitor to a hubspot contact, `$hubspot.contact` 
- a workflow actionlet that pushes a Content entry to Hubspot as a Contact  
- a Rule Condition that allows you to perform actions on a visitor based on their lead score in hubspot

---
## API Interceptor
The interceptor proxys access to the hubspot api endponts, which are not browser accessable by default.  In in providing this, it also obfuscates the hubspot api key from clients.  To access the apis, you use the normal hubspot api call as in the hubspot documentation, except that you make the call locally and append `/hubapi` to the call.  The plugin will intercept the calls and proxy (GETs and POSTs) them to hubspots api.  N

So if you want to call : `https://developers.hubspot.com/docs/methods/contacts/create_contact`

instead you should call: `https://dotcmssite.com/hubapi/docs/methods/contacts/create_contact`

To see the api working and to see the data hubspot has on you, call:

`https://dotcmssite.com/hubapi/me`

Here is an example response from hubspot
https://gist.github.com/wezell/2cb1f972d233e3b6ef8fd3bf8be436bc

**Note** this component exposes the Hubspot API based the api key used in the plugin.  This should either be locked down or should modified for use in production environs. 

---
## Hubspot Viewtool 
This component provides a viewtool that will sniff the hubspot tracking cookie and return all know data regarding the visitor.  This data is returned as a JSON object - which has all of hubspots current information.  
There are two methods, `$hubspot.getContact()` which will get the visitors information and cache it in the visitors session and `$hubspot.refreshContact()` which will obviously refresh the visitor's information from hubspot.


To get your current contact info in serverside velocity you would:

```
$hubspot.getContact().properties.firstname.value
$hubspot.getContact().properties.lastname.value
$hubspot.getContact().properties.hubspotscore.value
$hubspot.getContact().properties.email.value
```
---
## Workflow Actionlet Content -> Hubspot Contact

This actionlet can be used to within a workflow to push a piece of content - say a visitor entered form to hubspot as a new contact.  To use it, you will need 2 things:

1. Hubspot Portal Id : 
How to get the Portal Id: https://knowledge.hubspot.com/articles/kcs_article/account/where-can-i-find-my-hubspot-portal-id
2. Hubspot Form Id : 
How to get the Form Id: https://knowledge.hubspot.com/articles/kcs_article/forms/how-do-i-find-the-form-guid

We suggest using a kitchen sink form approach create one large hubspot form that has every parameter you might want to collect and using that as a basis for all your form postings.

** Hubspot Fields Mapping: ** 
On Hubspot each field of the contact form have an id, in order to map each of the fields of the *Content Type* using this Actionlet with those Hubspot fields we use the **Hubspot Fields Mapping** parameter, with that parameter we map the dotCMS field varname with the hotspot Contact Form field id.

**Example:**
dotCMSFieldVarname1:hotspotFieldId1, dotCMSFieldVarname2:hotspotFieldId2, dotCMSFieldVarname3:hotspotFieldId3

**NOTE:** Only mapped fields are going to be sent to the Hubspot API

## Screenshots
![actionlet](https://cloud.githubusercontent.com/assets/923947/17302614/3d0dddfe-57da-11e6-9715-16ffdf9d0fa9.png)

---

## Hubspot Score Rule Condition
This adds a new Rule condition that allows an adminstrator to use a visitors lead score to present specific actions within dotCMS

---

## Installation

Before installing the plugin, you need to change a single line of code and set your hubspot api key

https://github.com/dotCMS-plugins/com.dotcms.hubspot/blob/master/src/main/java/com/dotcms/osgi/api/HubspotAPI.java#L25

Then build it
`./gradlew jar`

Then upload the files into your dotCMS installation:

---
## Javascript to POST data (a contact) into HubSpot

This is an example of a javascript function that takes a json object and posts it into Hubspot.


```js
<script>

/*
Takes the Contact Us form and sends it to hubspot instead of dotCMS
*/

function postToHubspot() {

	errorFieldName='';
	var isValid = checkTabFields("Contact Us"); 
	if(!isValid){	
		if(errorFieldName != null && errorFieldName != ''){
			document.getElementById(errorFieldName).focus();	
		}
		return false;	
	}


	var formObj = dojo.formToJson("submitContentForm");
	
	var jsonObject =  (formObj.constructor === "test".constructor) ? JSON.parse(formObj) : formObj;

	var valids=["email","firstname","lastname", "website","phone","address","city","state","zip"]
	
	
	
	var validSet = new Set(valids);
	var properties = [];
	for (var prop in jsonObject) {
	  var lprop=prop.toLowerCase();
	  var val = jsonObject[prop];
		  if(validSet.has(lprop)){
			var x={"property":lprop, "value":val}


			properties.push(x)
		  }
	  
	  }
	var newProps = {"properties":properties};
	var jsonStr=JSON.stringify(newProps);
	console.log("newProps",newProps);
	console.log("jsonStr",jsonStr);

	$.ajax({
		url: '/hubAPI/contacts/v1/contact/',
		type: 'POST',
		cache: false,
		data: jsonStr,
		dataType: 'json',

		success: function (data, status, xhr) {
			console.log("Success: ");
			console.log("data", data);
			window.location="/contact-us/thank-you";

		},
		error: function (data, status, xhr) {
			console.log("fail: ");
			console.log("data", data);
			console.log("status", status);
			alert("form failed:" + data);
		}
	});
	
	
}



function overrideSubmit(){

	var submitButton = dojo.byId("submitButton");

	dojo.empty(submitButton);
	dojo.style(submitButton, "text-align", "center");
	

	// Create a button programmatically:
	var myButton = new dijit.form.Button({
		label: "Save",
		onClick: function(){postToHubspot()}
	});
	
	myButton.placeAt(submitButton);
   
}
dojo.addOnLoad(overrideSubmit);

</script>

```






## Todo
* oauth authentication
* more defined value pojos to help make sense of the json responses
* hubspot form integration - will allow instant mapping of the hubspotutk cookie (this could be done now via /hubAPI if the form is created in hubspot manually)
* ?

## DISCLAIMER
Plugins are code outside of the dotCMS core code and unless explicitly stated otherwise, are not covered or warrantied  under dotCMS support engagements. However, support and customization for plugins is available through our Enterprise Services department. Contact us for more information.


