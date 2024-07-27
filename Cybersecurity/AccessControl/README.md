# Access Control

An assignment for CYBR371 demonstrating access control procedures in Linux. The scripts work on an Ubuntu VM.

- `CYBR371_Assignment_1_24T1.pdf`: The assignment description.
- `chandraas_assignment1.pdf`: My report for the assignment.
- `setup-cybr371.sh` and `setup-clinic.sh`: a bash script that creates users and groups, assigns users to groups, defines user home directories, creates subdirectories and files, and sets up permissions for the directories and files. For `setup-cybr371.sh`, the users are lecturers, tutors, and students, and the directories consist of files relating to assignments, labs and tests. For `setup-clinic.sh`, the users are doctors and nurses, and the directories are a `patients` folder consisting of patient files.
- `register-patient.sh`: a bash script that allows doctors to create a new patient in the system and add their details to the `patients` folder.
- `check-medication.sh`: a bash script that allows nurses to enter a patient's first name, surname, and year of birth to retrieve the patient's details and check what medication they are prescribed.
- `patients`: a directory containing example files that `register-patient.sh` and `setup-clinc.sh` work on.