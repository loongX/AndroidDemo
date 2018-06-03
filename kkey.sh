#!/bin/bash

#################卡住，如有键盘输入则结束对话#######################
anykey()
{
       SAVEDSTTY=`stty -g`
       stty -echo
       stty raw
       dd if=/dev/tty bs=1 count=1 2> /dev/null
       stty -raw
       stty echo
       stty $SAVEDSTTY
}
echo -ne "\nThe end of the displayed message,press any key to exit!"
char=`anykey`