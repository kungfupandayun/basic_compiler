#!/bin/sh
#cd "$(dirname "$0")"/../../../.. || exit 1

PATH=./src/test/script/launchers:./src/main/bin:"$PATH"

# On ne teste qu'un fichier. Avec une boucle for appropriée, on
# pourrait faire bien mieux ...

test_codegen_invalide(){
	t=${1%*.*}
	echo "$t"

	rm -f ./"$t".ass 2>/dev/null
	decac ./"$t".deca

	if [ ! -f ./"$t".ass ]; then
	    echo "Fichier $t.ass non généré."
	else
		ima ./"$t".ass
		rm -f ./"$t".ass
	fi

}

for cas_de_test in ./../../deca/codegen/invalid/*.deca  ./../../deca/codegen/invalid/Objet/*.deca
do
    echo "$cas_de_test"
    test_codegen_invalide "$cas_de_test"
done
