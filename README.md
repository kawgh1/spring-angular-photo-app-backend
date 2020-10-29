### Springboot Angular Photo Sharing Web App - Backend

This project was based on a course by "Get Arrays - Software Development Teaching Platform"
https://www.udemy.com/course/building-a-restful-api-application-using-spring-and-angular/

This was a great project. A lot of troubleshooting was required throughout the project as well as the
deployment. I did all the Heroku deployment including rewriting the backend Java code to connect to Amazon S3 storage 
to host user uploaded images (which Heroku does not support on their servers). 
I added some mobile formatting, UX improvements and other code fixes.

#### Things I learned about:

- Spring Security
- Amazon S3 storage and hosting
- Java Mail API for sending pre-formed automated e-mails based on a user action (ex. new user registration)
- JWT (JSON Web Token) Authentication and Authorization with Java, SpringBoot + Angular
- HttpInterceptors
- Web Caching
- Google Maps API
- Angular Resolvers (https://codeburst.io/understanding-resolvers-in-angular-736e9db71267)

To view the live site deployment, please visit: https://spring-angular-photoshar-front.herokuapp.com

To view the Angular frontend code please visit: https://github.com/kawgh1/spring-angular-photo-app-frontend

