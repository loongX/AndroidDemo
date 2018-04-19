#!/bin/bash
#获取当前目录
SHELL_FOLDER=$(cd "$(dirname "$0")";pwd)
#需要拉取的文件夹地址
address=("./" 	
		#"./source/_posts/Java-Web"		
		 )
address_size=${#address[@]}
for ((i=0; i<address_size; i++)); do
    echo "########"
	echo "###### address:  ${address[i]}"
	echo "####"
	cd "${SHELL_FOLDER}"
	cd "${address[i]}"
	git pull
done

