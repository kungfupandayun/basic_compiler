#!/bin/sh

PATH=./src/test/script/launchers:./src/main/bin:"$PATH"

# ./../../deca/syntax/valid/creat/*.deca ./../../deca/syntax/valid/provided/hello.deca ./../..
# ./../../deca/syntax/valid/Class/*.deca
for i in  ./../../deca/syntax/valid/creat/*.deca ./../../deca/syntax/valid/provided/*.deca ./../../deca/syntax/valid/Class/*.deca
do
echo "*************************************************************"
echo "$i"
# Remplacer <executable> par test_synt ou test_lex
# ou test_context ou decac
echo -e "\e[32m"
test_synt "$i"
echo -e "\e[39m"

echo -e "\e[32m"
 decac -p "$i"
echo -e "\e[39m"
done
