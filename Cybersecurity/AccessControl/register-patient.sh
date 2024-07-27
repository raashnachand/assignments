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
