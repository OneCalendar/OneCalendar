#!/bin/bash

echo -en "\033[31m OneCalendar! To Meet Them All project's metrics \033[0m"
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
    find $1 -name $2 | xargs -d "\n" cat |  grep -v "type class" | grep "$3" | wc -l
}

occurenceFromFile() {
    find $1 -name $2 | xargs -d "\n" grep --color=auto -H $3
}

echo 

scalaCaseClass=$(occurenceCount app "*.scala" "case class")
scalaGlobalClass=$(occurenceCount app "*.scala" "class")
scalaClass=$(echo $scalaGlobalClass-$scalaCaseClass | bc -l)

echo "nombre de class scala dans app/ : $scalaClass"
echo "nombre de case class scala dans app/ : $scalaCaseClass"
echo "nombre de object scala dans app/ : $(occurenceCount app "*.scala" object)"

echo

echo "nombre de méthode scala dans app/ : $(occurenceCount app "*.scala" def)"

echo

echo "nombre de methode de test : $(occurenceCount test "*.scala" "test(")"
echo "nombre de méthode de test ignorées : $(occurenceCount test "*.scala" "ignore(")"

echo

echo "nombre de fois ou 'null' est utilisé dans le code : $(occurenceCount app "*.scala" null)"
occurenceFromFile app "*.scala" null

echo

echo "nombre de fois ou 'var' est utilisé dans le code : $(occurenceCount app "*.scala" var)"
occurenceFromFile app "*.scala" var
