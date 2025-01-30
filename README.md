# Salesforce Seasonal Release Test Automation

## Table of contents
1. [Introduction](#markdown-header-introduction)
2. [How to Install](#markdown-header-how-to-install)
3. [How to Run](#markdown-header-how-to-run)
4. [Maintenance of Suite](#markdown-header-maintenance-of-suite)
5. [Glossary of Terms](#markdown-header-glossary-of-terms)
***
### Introduction
This readme guide is intended to outline exactly what is required in order to run and maintain the test automation suite for future usage as well as an installation guide, and a glossary of terms that you will need to know in order to create your own tests.
***
### How to Install
First, you must have IntelliJ or another similar IDE installed. (such as Eclipse, Jupyter, etc.)
You must also have Git installed. (either Git for Windows or the equivalent for Mac and Linux distributions.)
You will also need to have Java JDK 1.8 installed as well.
Lastly, you will need to have Apache Maven installed on your machine so that the packages used in the repository can be installed.

Once you have the pre-requisite installations, you will need to download this repository to your machine using the BitBucket link provided in the repository.
***
### How to Run
To run the suite, the first thing to do is to set up your credentials on your IDE. For this purpose, I am going to be using IntelliJ.

Firstly, you need to make sure both JAVA_HOME and MAVEN_HOME system variables are set; you need to do this with the help of someone from the ITSD team, who have the correct privileges to be able to do this.

Secondly, once the previous variables are set up, you will need to go through into your IDE and set up the runner parameters. 
To do this, you need to go to the "Run -> Edit Configurations" option in the menu. 
Once selected, you will need to add the main run configurations; which is a Maven template.

#### Configuration
Working Directory: ``H:/Coding/workspace/salesforce-seasonal-release-test-automation``

Command Line: ``test "-Dcucumber.options=--tags '@[YOUR-TAG-HERE]" -Denv=[YOUR-ENV-HERE] -Dopenreport=false -DforkCount=0 -DreuseForks=false``
***
### Maintenance of Suite
As the suite uses xpaths to validate elements within the Salesforce User Interface; it is highly reliant on the software remaining stable and not changing the location of the HTML elements on the page.
However, should an element not be found, the system will say what element xpath could not be found; thus it is then required that the data in the test is changed to match the correct xpath location.

I have attempted to make the xpath variables as easy to reconfigure as possible by placing all the xpaths into a page-object model that allows the user to know exactly what page the xpath is related to.

***
### Glossary of Terms

#### BrowserActions
BrowserActions are a list of actions that the selenium web-browser can utilise on the currently active web page

###### origin()
origin() means to reset the active element to the origin of the page, as defined in HTML as `<html>`.

###### focus()
focus() means to set the currently active element to a new element which must be passed into the focus command, either by using a Locator or a specific HTML tag.

###### touch()
touch() means to click on the currently active element in web mode or, using a touch-screen, tap on the element.

###### compose()
compose() means to type into the currently active element, which must be a field, some text which must be provided.

###### pause()
pause() means to prevent the runner from continuing for a brief period of time as defined by the mandatory time in seconds.

###### contains()
contains() means to check the currently active element for text containing the words or phrases provided.

###### scrollToElement()
scrollToElement() means to scroll to an element present in the page's HTML but not present on the screen.

###### changeTab()
changeTab() means to actively change the currently focussed tab that is being opened by the current browser. Must include tab number.

###### collect() 
collect() means to make a list of elements with a non-unique identifier, such as class name, css, HTML tag, etc.

###### select()
select() can only be used after using collect() and means to select an element from the previously collected list of elements.

###### ascend()
ascend() is used to navigate one layer up in the page's HTML code, to reach the parent tag of the current element.

###### descend()
descend() is either used to navigate one layer down in the page's HTML code, to reach the direct child tag of the current element, or if provided with a specific tag, to search all child nodes for said tag.

#### Locator
Locators are a way of finding a specific element in the page; usually locators can be broken down by relevance to the element type in a number of ways.

###### xpath()
xpath() is a type of locator that uses the exact path of the element in the page. This method is the most unreliable as any changes to the page cause the exact location of the element to change, breaking tests.

###### css()
css() is a type of locator that uses css element types to find an element in the page. This method is unreliable as multiple elements can have the same css.

###### className()
className() is a type of locator that uses class names to find an element in the page. This method is also unreliable as multiple elements may have the same class name.

###### linkText()
linkText() is a type of locator that uses the text of a link to find an element on the page. The element must be a type of hyperlink and is slightly more reliable as it is less likely that multiple links have the same link text.

###### name()
name() is a type of locator that uses the name of an element to find the element in question on the page. This method is more reliable than most, as it is highly unlikely that multiple elements will contain the same name.

###### id()
id() is a type of locator that uses the id of an element to find the element in the page. This is the most reliable method of finding an element as no two elements should ever have the same id.

#### HtmlTags
HtmlTags uses specific HTML tags, as defined by the locator type TagName to find elements by a specific HTML tag, which can be used to search for elements or collect elements.
***