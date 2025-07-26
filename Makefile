# -----
#  Makefile for kosmos
#

#
# adjust if these are not on your path
#
CLJ := clj
CLOJURE := clojure

#
# Targets
#
.PHONY: clean dev attach test tag version

clean:
	@rm -rf .cpcache/ target/ pom* .lein*

deps:
	@${CLOJURE} -X:deps prep

#
# put your local overrides for this project under an alias in
#   ~/.clojure/deps.edn called overrides
#
dev:	deps
	@${CLOJURE} -M:dev:overrides

test:	deps
	@${CLOJURE} -M:test

attach: PORT=$(shell cat .nrepl-port)
attach:
	@${CLOJURE} -Mattach --attach localhost:${PORT}

version:
	@next-version --file .version

tag: VERSION:=$(shell cat .version)
tag:
	git tag -a ${VERSION} -m "release/tag version v${VERSION}"

build: clean deps
	@${CLOJURE} -T:build:deploy build/jar

deploy: build
	@${CLOJURE} -T:build:deploy build/deploy
