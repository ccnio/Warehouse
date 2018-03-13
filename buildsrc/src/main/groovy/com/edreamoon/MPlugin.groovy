package com.edreamoon

import org.gradle.api.Plugin
import org.gradle.api.Project

class MPlugin implements Plugin<Project> {
@Override
void apply(Project project) {
    println("************** M plugin *************")
//    project.android.productFlavors.create("name2")
//    project.android.registerTransform(new MTransform(project))
}
}