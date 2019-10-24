#!/bin/bash
# Copyright (2019) Cobalt Speech and Language Inc.

set -e

# CI assumes our internal sdk-generator as the base image, which already has
# dependencies installed. Generate all artifacts again using the buildSDK.sh
# script included in our image.
bash $SDK_DEPS/bin/buildSDK.sh

# it's an error if we generated something that wasn't checked in.
git diff --name-only --exit-code || (echo "Not all generated files were checked in" && exit 1)
