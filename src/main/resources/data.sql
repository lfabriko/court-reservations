insert into court_surface(id, cost_in_minutes) values(1, 100);
insert into court_surface(id, cost_in_minutes) values(2, 200);

insert into court(id, description, court_surface_id) values(1, 'north court', 1);
insert into court(id, description, court_surface_id) values(2, 'south court', 1);
insert into court(id, description, court_surface_id) values(3, 'east court', 2);
insert into court(id, description, court_surface_id) values(4, 'west court', 2);
