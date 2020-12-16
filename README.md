# Monster Trading Card Game

## About

This HTTP/REST-based server is built to be a platform for trading and battling with
and against each other in a magical card-game world.
* a user is a registered player with credentials (unique username, password).
* a user can manage his cards.
* a card consists of: a name and multiple attributes (damage, element type).
* a card is either a spell card or a monster card.
* a user has multiple cards in his stack.
* a stack is the collection of all his current cards (hint: cards can be removed
by trading).
* a user can buy cards by acquiring packages.
* a package consists of 5 cards and can be acquired from the server by paying 5
virtual coins.
* every user has 20 coins to buy (4) packages.
* the best 4 cards are selected by the user to be used in the deck.
* the deck is used in the battles against other players.
* a battle is a request to the server to compete against another user with your
currently defined deck (see detail description below).

## Getting Started

Gradle is used to build the Project.
Use `java -jar mtcg.jar` to run the HTTP Server.

## Artifacts

See Github Packages or Releases.

## Maintainers

* Stefan Tirea BIF3C
