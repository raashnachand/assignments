# MusicGuru

A TCP/IP server that a client connects to and requests for a song from a certain year. The server then returns a random song from the top 10 charts of that year.

This project is split into two parts, and each were assessed separately:

`AWSServer`: the actual writing of the code for the server and client, and the creation of an AWS server to host the server on.
`LoadBalanceAndContainers`: the creation of load balancers so that multiple clients can connect to the server using one IP address, and creating Docker images for horizontal scaling. This is split further into two parts: one for the load balancers and the other for the container creation.