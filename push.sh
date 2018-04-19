#!/bin/bash
#获取当前目录
SHELL_FOLDER=$(cd "$(dirname "$0")";pwd)
cd ${SHELL_FOLDER}
echo "脚本目录：${SHELL_FOLDER}"

#推送
sh O.sh
sh kkey.sh
 