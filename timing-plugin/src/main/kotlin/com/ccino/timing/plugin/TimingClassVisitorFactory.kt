package com.ccino.timing.plugin

import com.android.build.api.instrumentation.AsmClassVisitorFactory
import com.android.build.api.instrumentation.ClassContext
import com.android.build.api.instrumentation.ClassData
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.Opcodes

/**
 * ASM ClassVisitor 工厂
 * 用于创建字节码转换的 ClassVisitor
 */
abstract class TimingClassVisitorFactory : AsmClassVisitorFactory<TimingParams> {
    
    override fun createClassVisitor(
        classContext: ClassContext,
        nextClassVisitor: ClassVisitor
    ): ClassVisitor {
        val params = parameters.get()
        
        // 如果插件被禁用，直接返回原始 visitor
        if (!params.enabled.get()) {
            return nextClassVisitor
        }
        
        return TimingClassVisitor(
            nextClassVisitor,
            params.annotationDescriptor.get(),
            params.logTag.orNull,
            params.minDuration.get()
        )
    }
    
    override fun isInstrumentable(classData: ClassData): Boolean {
        val params = parameters.get()
        val className = classData.className
        
        // 解析白名单和黑名单
        val includeList = params.includePackages.orNull
            ?.split(",")
            ?.filter { it.isNotEmpty() }
            ?: emptyList()
        
        val excludeList = params.excludePackages.orNull
            ?.split(",")
            ?.filter { it.isNotEmpty() }
            ?: emptyList()
        
        // 1. 如果有白名单，只处理白名单中的类
        if (includeList.isNotEmpty()) {
            val included = includeList.any { className.startsWith(it) }
            if (!included) {
                return false
            }
        }
        
        // 2. 排除黑名单中的类
        if (excludeList.any { className.startsWith(it) }) {
            return false
        }
        
        return true
    }
}

/**
 * 自定义 ClassVisitor
 * 遍历类中的所有方法，找到带指定注解的方法进行处理
 */
class TimingClassVisitor(
    nextClassVisitor: ClassVisitor,
    private val annotationDescriptor: String,
    private val customLogTag: String?,
    private val minDuration: Long
) : ClassVisitor(Opcodes.ASM9, nextClassVisitor) {
    
    private var className: String = ""
    
    override fun visit(
        version: Int,
        access: Int,
        name: String,
        signature: String?,
        superName: String?,
        interfaces: Array<out String>?
    ) {
        this.className = name
        super.visit(version, access, name, signature, superName, interfaces)
    }
    
    override fun visitMethod(
        access: Int,
        name: String,
        descriptor: String,
        signature: String?,
        exceptions: Array<out String>?
    ): org.objectweb.asm.MethodVisitor {
        val mv = super.visitMethod(access, name, descriptor, signature, exceptions)
        
        // 返回自定义的 MethodVisitor 来处理方法
        return TimingMethodVisitor(
            mv,
            access,
            name,
            descriptor,
            className,
            annotationDescriptor,
            customLogTag,
            minDuration
        )
    }
}
