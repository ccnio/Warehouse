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
        // 过滤掉不需要处理的类
        return when {
            classData.className.startsWith("android.") -> false
            classData.className.startsWith("androidx.") -> false
            classData.className.startsWith("kotlin.") -> false
            classData.className.startsWith("java.") -> false
            classData.className.startsWith("javax.") -> false
            else -> true
        }
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
