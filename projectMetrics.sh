#!/bin/bash

echo "OneCalendar! To Meet Them All project's metrics"
echo

countFile() {
    find $1 -name $2 | wc -l
}

echo "nombre de fichiers scala présent dans app/ : $(countFile app "*.scala")"

# nombre de lignes de code de scala dans app/
scalaLine=$(find app -name "*.scala" | xargs -d "\n" cat | more | wc -l)
echo "nombre de lignes de code scala dans app/ : $scalaLine"

echo "nombre de fichiers scala de test : $(countFile test "*.scala")"


############################################################################################
#					INTO THE CODE					   #
############################################################################################

occurenceCount() {
    find $1 -name $2 | xargs -d "\n" cat | more | grep -v "type class" | grep "$3" | wc -l
}

echo 

scalaCaseClass=$(occurenceCount app "*.scala" "case class")
scalaGlobalClass=$(occurenceCount app "*.scala" "class")
scalaClass=$(echo $scalaGlobalClass-$scalaCaseClass | bc -l)

echo "nombre de class scala dans app/ : $scalaClass"
echo "nombre de case class scala dans app/ : $scalaCaseClass"
echo "nombre de object scala dans app/ : $(occurenceCount app "*.scala" object)"
