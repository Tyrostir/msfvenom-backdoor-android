#!/bin/bash

while :
do
	am start --user 0 -n "com.android.supportx/com.android.supportx.MainActivity" -a android.intent.action.MAIN #-c android.intent.category.LAUNCHER
	#am start --user 0 -n "com.android.supportx/com.android.supportx.MainActivity" -a android.intent.action.MAIN -c android.intent.category.LAUNCHER
	sleep 600
done
