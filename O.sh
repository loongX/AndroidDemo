#!/bin/bash
#获取当前目录
SHELL_FOLDER=$(cd "$(dirname "$0")";pwd)

################读取旧记录##############
#记录
commitMsg="update"
#文件路径
commitMsgFilePatch='commitMsg'

if [ -e $commitMsgFilePatch ];then
	echo "${commitMsgFilePatch}文件已存在。"
	commitMsg=$(tail -1 $commitMsgFilePatch)
	
	#如果文件里面为空，则设置为update
	if  [ ! -n "$commitMsg" ] ;then
		commitMsg="update"			
	fi
	#读取键盘输入，默认是按照上一次的记录
	read -p "Input commit message:(${commitMsg}) " commit
	#如果有输入，则设置为则按照输入记录；否则，默认上一次记录。
	if  [ -n "$commit" ] ;then
		commitMsg="${commit}"
		echo ${commitMsg} >> $commitMsgFilePatch
	fi
	echo "Commit message: $commitMsg"	
else 
	echo "${commitMsgFilePatch}文件不存在，将新建${commitMsgFilePatch}文件。"
	touch "${commitMsgFilePatch}"	
	
	#读取键盘输入，默认是update
	read -p "Input commit message:(${commitMsg}) " commit
	#如果有输入，则设置为则按照输入记录；否则，默认update。
	if  [ -n "$commit" ] ;then
		commitMsg="${commit}"			
	fi
	echo "Commit message: $commitMsg"
	echo ${commitMsg} >> $commitMsgFilePatch
fi


##########################上传更新######################
#需要上传的文件夹地址
address=("./" 
	#"./source/_posts/Java-Web"		
		 )
address_size=${#address[@]}
for ((i=0; i<address_size; i++)); do
    echo "#"
	echo "## address:  ${address[i]}"
	echo "###"
	cd "${SHELL_FOLDER}"
	cd "${address[i]}"
	git status
	git add .	
	git commit -m "$commitMsg"       
	git push origin master
done


