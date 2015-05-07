* * * * * * * * * * * * * * * * * * *
* MoTrails DB Schema                *
* Created by Richard Hall 4/21/2015 *
* Senior Project                    *
* * * * * * * * * * * * * * * * * * *

CREATE TABLE Users (
	UserID serial primary key,
	Username varchar(40),
	Password varchar(80),
	Name varchar(24),
	Email varchar(80),
	Location varchar(220)
);

CREATE TABLE Trails (
	TrailID serial primary key,
	UserID integer references Users(UserID) on delete cascade,
	TrailPath path
);

CREATE TABLE Waypoints (
	WaypointID serial primary key,
	TrailID integer references Trails(TrailID) on delete cascade,
	Title varchar(120),
	Description text,
	Picture varchar(120),
	Coordinates point
);