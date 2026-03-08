.PHONY: build deploy build-native

build-native:
	cd .. && ./gradlew build -Dquarkus.native.enabled=true -Dquarkus.native.container-build=true

build: build-native
	@echo "Build artifact: ../build/function.zip"

deploy:
	sam deploy --guided

deploy-fast:
	sam deploy
