# study-project-restapi-spring
It's a simple Springboot REST api. Its use AWS SDK for connecting with S3. 
Users can register, login and read/download the files.
Moderators, can upload, update and delete the files, read users. 
Admin can do all that he want, update/delete users, change roles and other. 

Hibernate used for work with postgres. 
Postgres used for stored audit data, user data and metainformation about files.

I tried to use classic Spring Security Auth and OAuth2 with Google at the same time 
for users.

