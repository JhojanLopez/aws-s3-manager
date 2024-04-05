# S3 Manager
This project has made using spring boot v3 with [Spring cloud AWS](https://spring.io/projects/spring-cloud-aws) and 
[Spring cloud AWS S3](https://docs.awspring.io/spring-cloud-aws/docs/3.0.0-M1/reference/html/index.html).

## Use Cases

Some use cases that it has this project are:

- create, list and delete buckets
- create, download, delete and move objects between buckets
- generate pre signed link for to get objects

## Notes

- This project has two apis with differences implementations using S3Client and S3Template
- For the correct work of application we must put the AWS credentials in application.yml (access and secret key)
