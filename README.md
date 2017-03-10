## Introduction
This is an Atlassian Confluence plugin to easily import documents from Google Drive into Confluence. Import all your existing team's documentation from Google Drive seamlessly and start enjoying Confluence.

## Before you start
To start using this plugin you only need to create some Confluence Space (other than 'demonstration space' provided by default). All imported pages will be added into that space.

## How it works
Find the corresponding item in the menu

![alt-text](http://imgur.com/f8lcM2N.png)

You'll get to the page with single button for authorization and import.
![alt-text](http://imgur.com/h0iRvzM.png)

Next, you'll be able to choose a Google Document using Google Picker UI
![alt-text](http://imgur.com/Ng0FhLu.png)

Few seconds later, voi la!
![alt-text](http://imgur.com/vjBin5b.png)

## Development notes
It bears noting, that prior to deploying this plugin to the real users, it's mandatory to fill missing Google App credentials at src/main/resources/page.vm.
These are developer's credentials, so this paragraph does not concern end users of the plugin at all.
So, anyone who wish to use this code as a base implementation of their plugin and then deploy it it the public, should:

1. Create an application with Googpe App Console
2. Enable Drive API for it
3. Create OAuth credentials for it
4. Enable Google Picker API
5. Create API key for it. As your API key would be publicly accessible in the JS code, it's mandatory to limit it's uses only for certain domains (may be *.atlassian.com in our case)

for 1-3 look https://developers.google.com/drive/v3/web/about-auth#OAuth2Authorizing 

for 4-5 refer to https://developers.google.com/picker/docs/#appreg

## Known issues
Things that are acknowledged and planned to be worked on
* Let users configure Confluence Space and root page to import Google Docs to
* Let users import multiple documents at a time
* Retrieve API key from the servlet not to expose it to public
* Change ugly native alets about page creating with fancy Confluecne-style notifications
* Add more MIME types (SpreadSheets, Drawings, etc.)
