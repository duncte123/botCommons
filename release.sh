#!/bin/sh
git checkout master
git pull
git merge develop
git push
git checkout develop
