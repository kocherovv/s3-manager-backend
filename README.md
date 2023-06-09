# study-project-restapi-spring
It's a simple Springboot REST api. Its use AWS SDK for connecting with S3.
I use 3 entities: User, File and Revision. I impl entity listener pattern with Spring.
This project has not yet been written and does not use class packages.

# Descripsion 
Users can register, login and read/download the files.
Moderators, can upload, update and delete the files, read users. 
Admin can do all that he want, update/delete users, change roles and other. 

Hibernate used for work with postgres. 
Postgres used for stored audit data, user data and metainformation about files.<br>
I used classic Spring Security Auth via session and OAuth2 with Google at the same time now. <br>
BUT it's for fun, it's actually REST, so I'm planning to implement JWT authentication with oauth2, but it still doesn't finished.
