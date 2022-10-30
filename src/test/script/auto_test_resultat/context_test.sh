#! /bin/sh

# Auteur : gl07
# Version initiale : 01/01/2020

# Test minimaliste de la vérification contextuelle.
# Le principe et les limitations sont les mêmes que pour basic-synt.sh


cd "$(dirname "$0")"/../../../.. || exit 1

PATH=./src/test/script/launchers:"$PATH"

# exemple de définition d'une fonction
test_context_invalide () {
    # $1 = premier argument.
    if test_context "$1" 2>&1 | grep -q -e "$1:[0-9][0-9]*:"
    then
        echo "Echec attendu pour test_contex sur $1."
    else
        echo "Succes inattendu pour test_contex sur $1."
        exit 1
    fi
}

for cas_de_test in src/test/deca/context/invalid/provided/*.deca src/test/deca/context/invalid/sansObjet/*.deca src/test/deca/context/invalid/class/*.deca
do
    test_context_invalide "$cas_de_test"
done

test_context_valide () {
	if test_context "$1" 2>&1 | grep -q -e ':[0-9][0-9]*:'
	then
	    echo "Echec inattendu pour test_context $1."
	    exit 1
	else
	    echo "Succes attendu pour test_context $1."
	fi
}

for cas_de_test in src/test/deca/context/valid/provided/*.deca src/test/deca/context/valid/sansObjet/*.deca src/test/deca/context/valid/class/*.deca
do
    test_context_valide "$cas_de_test"
done
