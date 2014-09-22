#!/bin/bash

# A helper script to layout the necessary directories for a new project along the maven/gradle defaults.

fail()
{
	echo $@
	exit 1
}

while [ -n "$1" ]
do
	modulename="$1" ; shift
	[ -n "$modulename" ] || fail "Module name required."
	[ -d "$modulename" ] || mkdir "$modulename" || fail "Cannot create module directory '$modulename'."

	maindirs=("main" "test")
	subdirs=("java" "resources" "scala")

	for maindir in "${maindirs[@]}"
	do
		for subdir in "${subdirs[@]}"
		do
		   mkdir -p "$modulename/src/$maindir/$subdir"
		done
	done

	echo "Be sure to modify settings.gradle by adding '$modulename' to the include line!"
done



