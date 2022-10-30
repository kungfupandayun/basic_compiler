#! /bin/sh

# Auteur : gl07
# Version initiale : 01/01/2020

# Encore un test simpliste. On compile un fichier (cond0.deca), on
# lance ima dessus, et on compare le résultat avec la valeur attendue.

# Ce genre d'approche est bien sûr généralisable, en conservant le
# résultat attendu dans un fichier pour chaque fichier source.
cd "$(dirname "$0")"/../../../.. || exit 1

PATH=./src/test/script/launchers:./src/main/bin:"$PATH"

# On ne teste qu'un fichier. Avec une boucle for appropriée, on
# pourrait faire bien mieux ...

test_codegen_valide(){

    	t=${1%*.*}
	rm -f ./"$t".ass 2>/dev/null
	decac ./"$t".deca || exit 1
	if [ ! -f ./"$t".ass ]; then
	    echo "Fichier $t.ass non généré."
	    exit 1
	else

	resultat=$(ima ./"$t".ass) || #exit 1
	echo "resultat=> $resultat"
	rm -f ./"$t".ass

	# On code en dur la valeur attendue.
	attendu1=ok
	attendu2=1
	#attendu3=erreur

	if [ "$resultat" = "$attendu1" ]
	then
	    echo "Succes attendu $attendu1"
	elif [ "$resultat = $attendu2" ]
	then
	    echo "Succes attendu $attendu2"
	#elif [ $resultat = $attendu3 ]
	#then
	#    echo "Succes attendu $attendu3"
	else
	    echo "Echèc inattendu de ima:"
	    echo "$resultat"
	    exit 1
	fi
fi


}

for cas_de_test in src/test/deca/codegen/valid/provided/*.deca  src/test/deca/codegen/valid/objet/*.deca src/test/deca/codegen/valid/sansObjet/*.deca
do
	echo "$cas_de_test"
    	test_codegen_valide "$cas_de_test"
done


echo "////////////////////////////////////////////////////////"

test_codegen_invalide(){
    	t=${1%*.*}
rm -f ./"$t".ass 2>/dev/null
decac ./"$t".deca

if [ ! -f ./"$t".ass ]; then
    echo "Fichier $t.ass non généré."

else


	resultat=$(ima ./"$t".ass)
	echo "resultat=> $resultat"
	rm -f ./"$t".ass

	# On code en dur la valeur attendue.
	attendu1=ok

	if [ "$resultat" = "$attendu1" ]
	then
	    echo "Succes attendu $attendu1"
	    exit 1
	else
	    echo "Echèc attendu de ima:"
	    echo "$resultat"
	fi
fi


}

for cas_de_test in src/test/deca/codegen/invalid/*.deca
do
	echo "$cas_de_test"
    test_codegen_invalide "$cas_de_test"
done
