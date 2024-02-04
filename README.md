
## todo:
- join verify and save in userSavePort

# Testing scenario
## Team
- Create 2x team, fail validation, close window
- Update team, fail validation, close window
- Add team and make sure it shows up in panel user
- Deactivate user barcode, it should show up on team view. Reactivate barcode
## User
1. Create user, fail validation, close window
- with image and without
2. Update user, fail validation, close window
- with image and without
## Barcode
1. Generate barcodes
## User, Team, Barcode
1. Add user to team
2. Add user multiple times in team and fail
3. Add user into 2 teams
## User Panel
1. Add 150 users
2. assign them to teams
3. Test pagination


Potential issue:
1. Too large images
max_allowed_packet variable on the MySQL server. This variable controls the maximum size of a single packet that the server can handle. You can modify it in the MySQL configuration file (my.cnf or my.ini) or dynamically using SQL:
SET GLOBAL max_allowed_packet = 524288000; -- Adjust the value as needed
2. If there are a lot of users joined in multiple teams, its somehow truncating number of users that shows up on panel user.
This was fixed with DISTINCT is UserLoadPort, but keep in mind

CREATE USER 'gambaru'@'localhost' IDENTIFIED BY 'password';
GRANT ALL PRIVILEGES ON *.* TO 'gambaru'@'localhost' WITH GRANT OPTION;
CREATE DATABASE gambaru;

# INSTALL
/etc/mysql/my.ini
C:\wamp64\bin\mysql\mysql{version}\my.ini

[mysqld]
max_allowed_packet=32M

(7,610,467 > 4,194,304). You can change this value on the server by setting the 'max_allowed_packet'