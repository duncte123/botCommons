#!/bin/sh
git checkout beta
git pull
git merge develop
git push
git checkout develop
