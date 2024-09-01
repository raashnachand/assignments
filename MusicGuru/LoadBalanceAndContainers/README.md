# LoadBalanceAndContainers

The creation of load balancers so that multiple clients can connect to the server using one IP address, and creating Docker for horizontal scaling. This is split further into two parts: Part A for the load balancers and Part B for the container creation.

## Part A

Created:
- A HealthCheck server for AWS to check the health of each instance created by the load balancer.
- A Virtual Private Cloud (VPC) to span availability zones.
- Security groups.
- Launch template for AWS to create instances from as needed.
- Load balancers.
- Target groups to redirect traffic to different load balancers.
- Autoscalers to create and destroy instances according to demand.

`Project 3 Part A Load Balancing.pdf` details the instructions as to how to create the above, and also contains a list of questions to answer.

`chandraas_answers_partA.pdf` contains the proof of following the instructions and the answers to the aforementioned questions.

## Part B

- Created a `Dockerfile` for Docker to create the image from.
- Pushed the Docker image to a container registry - you can see it [here](https://hub.docker.com/u/raashnachand/).
- Used Fargate clusters as an alternative to load balancing.

As above, `Project 3 Part b.pdf` details the instructions as to how to create the above, and also contains a list of questions to answer.

`chandraas_answers_partB.pdf` contains the proof of following the instructions and the answers to the aforementioned questions.