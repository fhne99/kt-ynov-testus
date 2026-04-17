Feature: Book management

  Scenario: the user creates a book and retrieves it
    Given the user creates the book with title "Clean Code" and author "Robert Martin"
    When the user gets all books
    Then the list should contain the following books
      | title      | author        |
      | Clean Code | Robert Martin |

  Scenario: the user creates two books and retrieves both
    Given the user creates the book with title "Clean Code" and author "Robert Martin"
    And the user creates the book with title "Kotlin in Action" and author "Jemerov"
    When the user gets all books
    Then the list should contain the following books
      | title            | author        |
      | Clean Code       | Robert Martin |
      | Kotlin in Action | Jemerov       |

  Scenario: the user reserves a book
    Given the user creates the book with title "Clean Code" and author "Robert Martin"
    When the user reserves the book with title "Clean Code"
    Then the book with title "Clean Code" should be reserved

  Scenario: the user cannot reserve an already reserved book
    Given the user creates the book with title "Clean Code" and author "Robert Martin"
    And the user has already reserved the book with title "Clean Code"
    When the user tries to reserve the book with title "Clean Code"
    Then the response status should be 400