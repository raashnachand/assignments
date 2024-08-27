#!/bin/sh
cd /home/ec2-user/
java MusicGuruServer 5000 &
java MusicGuruHealthCheck 5001 &
