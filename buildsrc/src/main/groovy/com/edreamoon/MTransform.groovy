package com.edreamoon

import com.android.build.api.transform.*
import org.gradle.api.Project


class MTransform extends Transform {
    private Project mProject

    MTransform(Project project) {
        mProject = project
    }

    @Override
    String getName() {
        return "MPlu"
    }

    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
        return null
    }

    @Override
    Set<? super QualifiedContent.Scope> getScopes() {
        return null
    }

    @Override
    boolean isIncremental() {
        return false
    }
}