#!/usr/bin/env python
from setuptools import setup

setup(
    name='cobalt-cubic',
    python_requires='>=3.5.0',
    version='1.5.0',
    description='This client library is designed to support the Cobalt API for speech recognition with Cubic',
    author='Cobalt Speech and Language Inc.',
    maintainer_email='tech@cobaltspeech.com',
    url='https://cobaltspeech.github.io/sdk-cubic',
    packages=["cubic"],
    install_requires=[
        'googleapis-common-protos==1.52.0',
        'grpcio-tools==1.35.0'
    ]
)
