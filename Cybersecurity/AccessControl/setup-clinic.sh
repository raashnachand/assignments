#!/bin/bash

sudo groupadd doctor
sudo groupadd nurse
sudo useradd -g doctor -c "Dr Lou Ngevity" drloun
sudo useradd -g doctor -c "Dr Stethos Cope" drstethosc
sudo useradd -g nurse -c "Dr Bea Sure" drbeas
sudo useradd -g nurse -c "Phil Paine" philp

mkdir WellingtonClinic
sudo setfacl -m u:administrator:rwx WellingtonClinic
sudo setfacl -m g:doctor:rwx WellingtonClinic
sudo setfacl -m g:nurse:rwx WellingtonClinic
sudo setfacl -m o::--- WellingtonClinic
cd WellingtonClinic
mkdir patients
sudo setfacl -m u:administrator:rwx patients
sudo setfacl -m g:doctor:rwx patients
sudo setfacl -m g:nurse:r-x patients
sudo setfacl -m o::--- patients
touch register-patient.sh
touch check-medication.sh
sudo setfacl -m u:administrator:rwx register-patient.sh
sudo setfacl -m u:administrator:rwx check-medication.sh
sudo setfacl -m g:doctor:--x register-patient.sh
sudo setfacl -m g:doctor:--x check-medication.sh
sudo setfacl -m g:nurse:--- register-patient.sh
sudo setfacl -m g:nurse:r-x check-medication.sh
sudo setfacl -m o::--- check-medication.sh
sudo setfacl -m o::--- register-patient.sh
cat <<EOF >register-patient.sh
    #!/bin/bash

    if groups | grep -q '\bnurse\b'; then
        echo "You cannot register a new patient."
    else
        echo "Enter the following information about the patient."
        echo -e "First name: "
        read FIRSTNAME
        echo -e "Last name: "
        read LASTNAME
        echo -e "Year of birth: "
        read BIRTHYEAR

        full_name="$FIRSTNAME $LASTNAME"
        can_register=true

        getent passwd | while IFS= read -r line; do
            name_from_passwd=$(echo "$line" | awk -F: '{print $5}')
            if [ "$name_from_passwd" = "$full_name" ]; then
                echo "You cannot register a staff member as a patient."
                can_register=false
                break
            fi
        done

        if [ "$can_register" = true ]; then
            cd patients
            patientfile="${FIRSTNAME}${LASTNAME}${BIRTHYEAR}"
            touch "$patientfile"
            contents="$FIRSTNAME,$LASTNAME,$BIRTHYEAR,$(date +%d/%m/%Y),\"~\"$(whoami)"
            echo -e "$contents" > "$patientfile"

            sudo setfacl -R -m u:administrator:rwx "$patientfile"
            sudo setfacl -R -m g:nurses:--- "$patientfile"
            sudo setfacl -R -m u:"$(whoami)":rw- "$patientfile"
            sudo setfacl -R -m o::--- "$patientfile"
        fi
    fi
EOF
cat <<EOF >check-medication.sh
    #!/bin/bash

    #scan first line of file, format, print
    patientfile="patients/$1$2$3"
    if [ ! -s $patientfile ]
    then
        echo -e "Patient does not exist."
    else
        first_line=$(head -n 1 ${patientfile})
        IFS=, read -ra fields <<< "$first_line"

        formatted_first_line=$(echo "$first_line" | awk -F',' '{OFS="\t\t"; $1=$1; print $1 " " $2, $5, $6}')

        echo -e "Patient\t\t\tPrimary Doctor\t\tSecondary Doctor(s)\n$formatted_first_line\n\nDate of Visit\t\tAttended Doctor\t\tMedication\t\tDosage"

        #skip the first two lines, read medication, format and print
        tail -n +3 ${patientfile} | while IFS= read -r line; do
            formatted_line=$(echo "$line" | awk -F',' '{OFS="\t\t"; $1=$1; print $1, $2, $4, $5}')
            echo "$formatted_line"
        done
    fi
EOF
