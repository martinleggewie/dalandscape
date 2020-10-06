

CREATE TABLE daboundaries AS SELECT * FROM CSVREAD('classpath:data/showcase/showcase_boundaries.csv', null, 'fieldSeparator=;');
CREATE TABLE dasystems AS SELECT * FROM CSVREAD('classpath:data/showcase/showcase_systems.csv', null, 'fieldSeparator=;');
CREATE TABLE dasystemcomponents AS SELECT * FROM CSVREAD('classpath:data/showcase/showcase_systemcomponents.csv', null, 'fieldSeparator=;');
CREATE TABLE dateams AS SELECT * FROM CSVREAD('classpath:data/showcase/showcase_teams.csv', null, 'fieldSeparator=;');
CREATE TABLE dateammembers AS SELECT * FROM CSVREAD('classpath:data/showcase/showcase_teammembers.csv', null, 'fieldSeparator=;');
CREATE TABLE dausecases AS SELECT * FROM CSVREAD('classpath:data/showcase/showcase_usecases.csv', null, 'fieldSeparator=;');
CREATE TABLE dausecasegroups AS SELECT * FROM CSVREAD('classpath:data/showcase/showcase_usecasegroups.csv', null, 'fieldSeparator=;');
CREATE TABLE dausers AS SELECT * FROM CSVREAD('classpath:data/showcase/showcase_users.csv', null, 'fieldSeparator=;');

CREATE TABLE dascenes AS SELECT * FROM CSVREAD('classpath:data/showcase/showcase_scenes.csv', null, 'fieldSeparator=;');
CREATE TABLE dastoryboards AS SELECT * FROM CSVREAD('classpath:data/showcase/showcase_storyboards.csv', null, 'fieldSeparator=;');
CREATE TABLE dastoryboardscontainscenes AS SELECT * FROM CSVREAD('classpath:data/showcase/showcase_storyboardsContainScenes.csv', null, 'fieldSeparator=;');

CREATE TABLE datransitiondefinitions AS SELECT * FROM CSVREAD('classpath:data/showcase/showcase_transitiondefinitions.csv', null, 'fieldSeparator=;');
