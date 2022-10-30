#!/bin/sh


# Remplacer <executable> par test_synt ou test_lex
# ou test_context ou decac
echo ./../../deca/syntax/invalid/provided/chaine_incomplete.deca
echo -e "\e[91m"
test_lex ./../../deca/syntax/invalid/provided/chaine_incomplete.deca
echo -e "\e[39m"

echo "*************************************************************"

for i in ./../../deca/syntax/invalid/lexi_invalid/*.deca ./../../deca/syntax/invalid/creat/*.deca
do
echo "$i"
echo -e "\e[91m"
	test_lex "$i"
echo -e "\e[39m"

done
