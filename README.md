
## dotCMS Hubspot Plugin

This is a POC plugin that provides two components, a (servlet) filter and a viewtool.

The filter proxys access to the hubspot api endponts, which are not browser accessable by default.  In in providing this, it also obfuscates the hubspot api key from clients.  To access the apis, you use the normal hubspot api call as in the hubspot documentation, except that you make the call locally and append `/hubAPI` to the call.  The plugin will intercept the calls and proxy (GETs and POSTs) them to hubspots api 

So if you want to call : `https://developers.hubspot.com/docs/methods/contacts/create_contact`

instead you should call: `https://dotcmssite.com/hubAPI/docs/methods/contacts/create_contact`

The second component it provides is a viewtool that will sniff the hubspot tracking cookie and return all know data regarding the visitor.  This data is returned as a JSON object - which has all of hubspots current information.  
There are two methods, `$hubspot.getContact()` which will get the visitors information and cache it in the visitors session and `$hubspot.refreshContact()` which will obviously refresh the visitor's information from hubspot.


Here is an example response from hubspot
https://gist.github.com/wezell/2cb1f972d233e3b6ef8fd3bf8be436bc

To get the current contact info in serverside velocity you would:

```
$hubspot.getContact().properties.firstname
$hubspot.getContact().properties.lastName
$hubspot.getContact().properties.hubspotscore
```

## Installation

Before installing the plugin, you need to change a single line of code and set your hubspot api key

https://github.com/dotCMS/com.dotcms.hubspot/blob/master/src/main/java/com/dotcms/osgi/api/HubspotAPI.java#L25

## Todo
* oauth authentication
* more defined value pojos to help make sense of the json responses
* hubspot form integration - will allow instant mapping of the hubspotutk cookie (this could be done now via /hubAPI if the form is created in hubspot manually)
* ?
