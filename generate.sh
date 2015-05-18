#!/bin/bash

basename=$1
dir=$2
private=${basename}.priv.pem
public=${basename}.pub.pem
cert=${basename}.cert.pcks12

openssl genrsa 2048 > ${private}
yes 'dc' | openssl req -x509 -new -key ${private} -out ${public}
openssl pkcs12 -export -in ${public} -inkey ${private} -out ${cert}

echo "Output"
openssl pkcs12 -info -in ${cert}

mv ${private} ${public} ${cert} ${dir}
