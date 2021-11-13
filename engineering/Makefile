
# this is related to the applicationId in the application/defaultConfig
PACKAGE_NAME="inc.combustion.engineering"

lint:
	./gradlew lint --info

run-unit-tests:
	./gradlew test

run-instrumented-tests: clear-app-data disable-animations
	./gradlew connectedAndroidTest

run-all-tests: run-unit-tests build-install-app run-instrumented-tests

assemble:
	./gradlew assemble 

assemble-release-app:
	./gradlew assembleRelease

assemble-debug-app:
	./gradlew assembleDebug

install-debug-app:
	./gradlew installDebug
  
build-and-install-debug-app: assemble-debug-app install-debug-app

uninstall-app:
	adb uninstall ${PACKAGE_NAME}

clear-app-data:
	adb shell pm clear ${PACKAGE_NAME}
  
clean:
	./gradlew clean

list-devices:
	adb devices
