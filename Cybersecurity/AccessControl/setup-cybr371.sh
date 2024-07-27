#!/bin/bash

# This part makes sure that the users on the system have been created, and creates them if not. For-loops done by ChatGPT.
users=("arman" "mohammed" "ilona" "esther" "immaculata")
for ((i=1; i<=93; i++)); do
    users+=("student$(printf "%03d" $i)")
done

for user in "${users[@]}"; do
    if id "$user" &>/dev/null; then
        echo "$user already exists."
    else
        echo "Creating user: $user"
        sudo useradd $user
    fi
done

sudo groupadd lecturer
sudo groupadd tutor
sudo groupadd student
sudo usermod -G lecturer arman
sudo usermod -G lecturer mohammed
sudo usermod -G tutor ilona
sudo usermod -G tutor esther
sudo usermod -G tutor immaculata

sudo setfacl -m o::--- setup.sh

# This for-loop was also done by ChatGPT.
for i in $(seq 1 93); 
do
    username="student$(printf "%03d" $i)"
    sudo usermod -G student "$username"
done

mkdir cybr371
sudo setfacl -m g:lecturer:rwx cybr371
sudo setfacl -m g:tutor:rwx cybr371
sudo setfacl -m g:student:r-x cybr371
cd cybr371
touch grades.xlsx
sudo setfacl -m g:lecturer:rw- grades.xlsx
sudo setfacl -m g:tutor:rw- grades.xlsx
sudo setfacl -m o::--- grades.xlsx
mkdir lab1 lab2 lab3 lab4 lab5 assignment1 assignment2 midterm final
for FOLDER in lab1 lab2 lab3 lab4 lab5 assignment1 assignment2 midterm final
do
    cd $FOLDER
    touch questions.pdf solutions.pdf
    sudo setfacl -m g:lecturer:rw- questions.pdf
    sudo setfacl -m g:tutor:r-- questions.pdf
    sudo setfacl -m g:student:r-- questions.pdf
    sudo setfacl -m g:lecturer:rw- solutions.pdf
    sudo setfacl -m g:tutor:r-- solutions.pdf
    sudo setfacl -m o::--- solutions.pdf
    for STUDENT in student001 student002 student003 student004 student005 student006 student007 student008 student009 student010 student011 student012 student013 student014 student015 student016 student017 student018 student019 student020 student021 student022 student023 student024 student025 student026 student027 student028 student029 student030 student031 student032 student033 student034 student035 student036 student037 student038 student039 student040 student041 student042 student043 student044 student045 student046 student047 student048 student049 student050 student051 student052 student053 student054 student055 student056 student057 student058 student059 student060 student061 student062 student063 student064 student065 student066 student067 student068 student069 student070 student071 student072 student073 student074 student075 student076 student077 student078 student079 student080 student081 student082 student083 student084 student085 student086 student087 student088 student089 student090 student091 student092 student093
    do
        mkdir $STUDENT
        sudo setfacl -m u:$STUDENT:rwx $STUDENT
        sudo setfacl -m g:lecturer:r-x $STUDENT
        sudo setfacl -m g:tutor:r-x $STUDENT
        sudo setfacl -m o::--- $STUDENT
        cd $STUDENT
        touch answers.docx
        sudo setfacl -m u:$STUDENT:rw- answers.docx
        sudo setfacl -m g:lecturer:r-- answers.docx
        sudo setfacl -m g:tutor:r-- answers.docx
        sudo setfacl -m o::--- answers.docx
        cd ..
    done
    cd ..
done


