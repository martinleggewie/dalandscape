# BankDa - A showcase to demonstrate the features of dalandscape

To get other people interested into what we can accomplish with the tooling in dalandscape, we define a showcase in which the various features become apparent one by one.

As at least some members of the development team work for a bank, and because first other people who get to see dalandscape's features also work in the financial domain, we use a the fictious bank and its system landscape as the frame.

## Setting the scene

Let's introduce **BankDa**, a fictious financial institute which has been on the market for quite some time. (Originally, we wanted to use the name "DaBank", but apparently there is a real company with this name on the market - see https://www.dabank.co.uk)

During its existence, BankDa has already been through quite some changes, and due to the fast evolving nature of the financial business, it will also undergo some more changes in the future.
Luckily, BankDa has built its processes on digital technologies right from the beginning, and this resulted in a stable, but flexible IT system landscape.
But even with the best digital technologies as a foundation, BankDa frequently needs to update and change the system landscape because otherwise it could not compete with its competitors in the long run.

As such changes cannot be done in a big bang while the company still needs to have a running business, the IT teams always need to apply changes step-by-step.
Also, system landscapes tend to get bigger and more complicated over time.
Because of this tendency, the responsible IT managers always require that these changes are documented in a sustainable way.
They literally forbid their teams to define important changes only in non-formalized slide decks which were sent via mail.
Instead, they insist that the important aspects of the system landscape and the changes performed on it are modeled, following the rules of a defined model.

Even more important:
A system landscape as a whole is only a tool which enables the business to accomplish the important goal - earn money.
Therefore, the IT managers also require that business people can get an idea of which systems are involved for the various use cases.

## The initial system landscape

TODO: Find a proper way of showing the needed entities and connections for each of the defined steps.

---

## Braindump


### Step 1 - Initial system landscape

The very first and very small system landscape.
This is not much more than an MVP.

* Actors
  * Customer
* Systems and their contained system components
  * Web frontend to run in a browser
    * Enrollment UI
    * Authentication UI
    * Bank account overview UI
    * Open current account UI
    * Send money UI
  * Backend for business logic
    * Enrollment and authentication module
    * Bank account module
  * Core banking system to connect to the rest of the banking world
    * State central bank connector
    * Current account module
    * Savings account module
  * Database
    * Customer data schema
    * Bank account data schema
  * State central bank
    * Bank API
* Use cases
  * Enroll as a customer (set username and password)
  * Authenticate customer (aka login)
  * Open a current account
  * Get list of bank accounts
  * Send money to other current accounts or to external accounts
* Teams
  * Customer data squad
  * Bank account squad


### Step 2 - Add savings account to the portfolio

BankDa management decides to add savings accounts to the portfolio.

* Add new use cases, assign it to all relevant system components
  * Open savings account
  * Transfer money between savings accounts and current accounts
* Add new UI to the webfrontend
* Add new savings module to the backend
* Connect the new savings module to the core banking savings account module
* Assign the new UI to the Bank account squad


### Step 3 - Add stock broking to the portfolio

To increase profit even more, BankDa management decides to add stock broking to the portfolio.
Unfortunately, the existing core banking system cannot deal with this, and it would be much too expensive to extend it.

Therefore BankDa needs to integrate a new software solution called BrokerDa from the vendor with the same name into the system landscape.
BrokerDa comes as a separate system incl. UI which can be operated next to the existing one.
BrokerDa needs a connection to the BrokerFederation system, similar to the connection between core banking system and state central bank.
BrokerDa also needs to store data.
For the data storage, it is not needed to have a separate database, but instead the schema containing all database objects can be placed on the already existing database.

In the pilot phase BankDa accepts that the customers would need to interact with two UIs, a new onw for opening and using stock deposits, and the existing one for opening and using current and savings accounts.

What BankDa cannot accept is that the customer would need to do a separate customer enrollment.
Therefore, BrokerDa can integrate to BankDa's existing identity provider by implementing a corresponding connector which becomes part of BrokerDa, and which will connect to the customer data module in the banking backend.

* Add new use cases, assign it to all relevant system components
  * Open stock deposit
  * Get list of stocks in stock deposit
  * Trade stocks
  * Search for available stocks
* Add new systems and their contained system components
  * BrokerDa web frontend
    * Stock deposit overview UI
    * Trade stock UI
  * BrokerDa backend for business logic
    * Stock deposit module
    * BrokerFederation connector module
  * BrokerFederation
    * Stock Broker API
* Add system components to exising system
  * Database
    * Stock deposit data schema
* Add a new team    
  * Stock deposit team, responsible for the new BrokerDa systems and system components.


### Step 4 - Split the customer domain and the account domain


### Step 5 - Integrate stock broking UI in the banking UI


### Step 6 - Introduce a mobile app which can deal with current accounts