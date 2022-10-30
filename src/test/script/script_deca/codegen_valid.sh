#!/bin/sh
#cd "$(dirname "$0")"/../../../.. || exit 1

PATH=./src/test/script/launchers:./src/main/bin:"$PATH"

# On ne teste qu'un fichier. Avec une boucle for appropriée, on
# pourrait faire bien mieux ...

test_codegen_valide(){
	t=${1%*.*}

	rm -f ./"$t".ass 2>/dev/null
	decac ./"$t".deca

	if [ ! -f ./"$t".ass ]; then
	    echo "Fichier $t.ass non généré."
	else

	ima ./"$t".ass
	rm -f ./"$t".ass

	fi
}


for cas_de_test in ./../../deca/codegen/valid/creat/*.deca  ./../../deca/codegen/valid/provided/*.deca  ./../../deca/codegen/valid/objet/*.deca

do
    echo "$cas_de_test"
    echo -e "\e[32m"
    test_codegen_valide "$cas_de_test"
    echo -e "\e[39m"
done
