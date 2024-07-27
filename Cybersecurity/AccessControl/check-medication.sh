#!/bin/bash

#scan first line of file, format, print
patientfile="patients/$1$2$3"
if [ ! -s $patientfile ]
then
    echo -e "Patient does not exist."
else
    first_line=$(head -n 1 ${patientfile})
    primary_doctor=
    secondary_doctor=
    fifth_field=$(echo "$first_line" | awk -F',' '{print $5}')
    sixth_field=$(echo "$first_line" | awk -F',' '{print $6}')

    #taking the fields, extracting their names from the comment of the user
    if [ $fifth_field = "~drloun" ]
    then
        primary_doctor=$(getent passwd | grep '^drloun:' | awk -F: '{print $5}')
    elif [ $fifth_field = "~drstethosc" ]
    then
        primary_doctor=$(getent passwd | grep '^drstethosc:' | awk -F: '{print $5}')
    fi

    if [ -n $sixth_field ] then
        if [ $sixth_field = "#drloun" ]
        then
            secondary_doctor=$(getent passwd | grep '^drloun:' | awk -F: '{print $5}')
        elif [ $sixth_field = "#drstethosc" ]
        then
            secondary_doctor=$(getent passwd | grep '^drstethosc:' | awk -F: '{print $5}')
        fi
    fi

    formatted_first_line=$(echo "$first_line" | awk -F',' -v p_d="${primary_doctor}" -v s_d="${secondary_doctor}" '{OFS="\t\t"; $1=$1; print $1 " " $2, p_d, s_d}')

    echo -e "Patient\t\t\tPrimary Doctor\t\tSecondary Doctor(s)\n$formatted_first_line\n\nDate of Visit\t\tAttended Doctor\t\tMedication\t\tDosage"

    #skip the first two lines, read medication, format and print
    tail -n +3 ${patientfile} | while IFS= read -r line; do
        second_field=$(echo "$line" | awk -F',' '{print $2}')
        attended_doctor=$(getent passwd | grep "^${second_field}:" | awk -F: '{print $5}')
        formatted_line=$(echo "$line" | awk -F',' -v a_d="$attended_doctor" '{OFS="\t\t"; $1=$1; print $1, a_d, $4, $5}')
        echo "$formatted_line"
    done
fi
