#! /bin/sh

# Auteur : gl07
# Version initiale : 01/01/2020

cd "$(dirname "$0")"/../../../.. || exit 1

PATH=./src/test/script/launchers:"$PATH"

# /!\ test valide lexicalement, mais invalide pour l'étape A.
# test_lex peut au choix afficher les messages sur la sortie standard
# (1) ou sortie d'erreur (2). On redirige la sortie d'erreur sur la
# sortie standard pour accepter les deux (2>&1)
#if test_lex src/test/deca/lexical/invalid/provided/simple_lex.deca 2>&1 \
#    | head -n 1 | grep -q 'simple_lex.deca:[0-9]'
#then
#    echo "Echec inattendu de test_lex"
#    exit 1
#else
#    echo "Sucess attendu"
#fi

# Ligne 10 codée en dur. Il faudrait stocker ça quelque part ...
#if test_lex src/test/deca/lexical/invalid/provided/chaine_incomplete.deca 2>&1 \
#    | grep -q -e 'chaine_incomplete.deca:10:'
#then
#    echo "Echec attendu pour test_lex"
#else
#    echo "Erreur non detectee par test_lex pour chaine_incomplete.deca"
#    exit 1
#fi


test_lex_invalide () {
    # $1 = premier argument.
    if test_lex "$1" 2>&1 | grep -q -e "$1:[0-9][0-9]*:"
    then
        echo "Echec attendu pour test_lex sur $1."
    else
        echo "Succes inattendu pour test_ sur $1."
        exit 1
    fi
}

for cas_de_test in src/test/deca/lexical/invalid/provided/chaine_incomplete.deca
do
    test_lex_invalide "$cas_de_test"
done

test_lex_valide () {
	if test_lex "$1" 2>&1 | grep -q -e ':[0-9][0-9]*:'
	then
	    echo "Echec inattendu pour test_lex $1."
	    exit 1
	else
	    echo "Succes attendu pour test_lex $1."
	fi
}

for cas_de_test in src/test/deca/lexical/valid/provided/*.deca
do
    test_lex_valide "$cas_de_test"
done
