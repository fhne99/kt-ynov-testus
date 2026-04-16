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