package com.ware


import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project

class MPlugin implements Plugin<Project> {
    private final static TAG = "MPluginLog "

    @Override
    void apply(Project project) {
        println("************** M plugin *************")

        if (!project.plugins.hasPlugin('com.android.application')) {
            throw new GradleException('Matrix Plugin, Android Application plugin required')
        }

        project.afterEvaluate {
            def android = project.extensions.android
            android.applicationVariants.all { variant ->


                def list = new ArrayList<String>(2)
                println(TAG + "list size = ${list.size()} ${variant.getMappingFileProvider().get().files}")
                MTransform.inject(project, /*configuration.trace, */ variant.getVariantData().getScope())

//                if (configuration.removeUnusedResources.enable) {
//                    if (Util.isNullOrNil(configuration.removeUnusedResources.variant) || variant.name.equalsIgnoreCase(configuration.removeUnusedResources.variant)) {
//                        Log.i(TAG, "removeUnusedResources %s", configuration.removeUnusedResources)
//                        RemoveUnusedResourcesTask removeUnusedResourcesTask = project.tasks.create("remove" + variant.name.capitalize() + "UnusedResources", RemoveUnusedResourcesTask)
//                        removeUnusedResourcesTask.inputs.property(RemoveUnusedResourcesTask.BUILD_VARIANT, variant.name)
//                        project.tasks.add(removeUnusedResourcesTask)
//                        removeUnusedResourcesTask.dependsOn variant.packageApplication
//                        variant.assemble.dependsOn removeUnusedResourcesTask
//                    }
//                }

            }
        }
    }
}