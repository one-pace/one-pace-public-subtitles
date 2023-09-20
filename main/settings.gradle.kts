
// required for FTP task with SSL session reuse enabled
// must be set here, before Gradle instantiates SSL-related functionality
// by cloning the git repository
System.setProperty("jdk.tls.useExtendedMasterSecret", "false")

val subktRepo: String by settings

sourceControl {
    gitRepository(uri(subktRepo)) {
        producesModule("myaa:subkt")
    }
}

pluginManagement {
    val subktVersion: String by settings

    resolutionStrategy {
        eachPlugin {
            if (requested.id.id == "myaa.subkt") {
                useModule("myaa:subkt:$subktVersion")
            }
        }
    }
}

buildscript {
    repositories {
        mavenCentral()
        jcenter()
    }
}
