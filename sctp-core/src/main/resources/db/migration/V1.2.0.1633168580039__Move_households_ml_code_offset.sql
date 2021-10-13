--
-- Move auto_increment value offset to 300,000. In case legacy system recruits
-- more households this will allow the system to fill into the gap between current offset to the following.
--

alter table households auto_increment = 300000;