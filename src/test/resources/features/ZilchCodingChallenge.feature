Feature: Zilch Coding Challenge

  @WebTest
  Scenario: Navigate to the ToolsQA Demo Book Store
    Given I start up my browser
    When I navigate to the ToolsQA Demo Book Store
    Then I confirm that the search bar is present

  @WebTest
  Scenario: Navigate to the ToolsQA Demo Web Form and fill it in
    Given I start up my browser
    When I navigate to the ToolsQA Demo Web Form
    And I fill in my details correctly
    Then I confirm that the details have been submitted

  @ApiTest
  Scenario: Use the ToolsQA Demo Book Store API to request the status of a book
    Given I want to know if a book exists in the book store
    When I request the book via the ISBN "9781449325862"
    Then the book will be "Git Pocket Guide"