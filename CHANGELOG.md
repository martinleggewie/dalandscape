# CHANGELOG

## 1.0.0

* Supports the following domain model:

  * Entities:
    * Landscape
    * Person
    * System
    * SystemComponents
    * UseCase
    * UseCaseGroup
    * Team
  * Connections:
    * Landscape consistsOf System
    * Landscape consistsOf Team
    * Landscape consistsOf UseCaseGroup
    * System contains SystemComponent
    * SystemComponent dependsOn SystemComponent
    * SystemComponent implements UseCase
    * Team consistsOf Person
    * Team responsibleFor System
    * Team responsibleFor SystemComponent
    * UseCaseGroup contains UseCase
    
* Creates the following types of diagrams:

  * Landscape: For each defined Landscape, the diagram shows Systems and the SystemComponents they contain, together with the dependencies between SystemComponents
  * SystemsInfo: The diagram consists of three parts:
    * Systems and the SystemComponents they contain
    * Systems and the UseCases they implicitly implement (because of the relation path System --> SystemComponent --> UseCase)
    * Systems and the UseCaseGroups they implicitely implement (because of the relation path System --> SystemComponent --> UseCase <-- UseCaseGroup)
  * TeamsInfo: The diagram consists of five parts
    * Teams and the UseCases they are implicitly responsible for (because of the relation path Team --> SystemComponent --> UseCase)
    * Teams and the UseCaseGroups they are implicitly responsible for (because of the relation path Team --> SystemComponent --> UseCase <-- UseCaseGroup)
    * Teams and the Persons they consist of
    * Teams and the Systems they are responsible for
    * Teams and the SystemComponents they are responsible for  

* Data definition done via a bunch of CSV files which will be imported as SQL into an in-memory H2 database.


## 1.1.0

* Extended the version 1.0.0 domain model to support modeling the migration of Systems, SystemComponents, and some Connections by the following:

  * Entities:
    * Storyboard and Scene
    * TransitionBook, TransitionSet, EntityTransition, ConnectionTransition
  * Connections:
    * Systems connect to Systems (to be able to support the situation in which a SystemComponent belongs to more than one System)

* Based on the model of Storybooks and Scenes, the application can now calculate all the in-between transitions automatically.

* Added two new types of diagrams:

  * Storyboard: For each defined Storyboard, the corresponding diagram shows all the Scenes which belong to the Storyboard.
  * TransitionBook: For each defined Storyboard, the corresponding diagram shows all the Scenes plus the calculated in-between TransitionSet
  
* Added this CHANGELOG.md file :-)